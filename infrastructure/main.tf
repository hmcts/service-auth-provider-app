provider "azurerm" {
  features {}
}

provider "azurerm" {
  alias                           = "cnp_dev"
  subscription_id                 = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
  resource_provider_registrations = "none"
  features {}
}

locals {
  vault_uri  = module.key-vault.key_vault_uri
  vault_name = module.key-vault.key_vault_name
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = var.location

  tags = var.common_tags
}

module "application_insights" {
  source = "git@github.com:hmcts/terraform-module-application-insights?ref=4.x"

  env      = var.env
  product  = var.product
  name     = "${var.product}-${var.component}-appinsights"
  location = var.appinsights_location

  resource_group_name = azurerm_resource_group.rg.name
  alert_limit_reached = true

  common_tags = var.common_tags
}

moved {
  from = azurerm_application_insights.appinsights
  to   = module.application_insights.azurerm_application_insights.this
}


data "azurerm_user_assigned_identity" "rpe-shared-identity" {
  name                = "rpe-shared-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}

data "azurerm_user_assigned_identity" "jenkins-preview" {
  provider = azurerm.cnp_dev
  count    = var.env == "aat" ? 1 : 0

  # Temporary exception for DTSPO-30107: Civil preview deploys currently read
  # AAT team secrets because the Jenkins library maps preview vaults to AAT.
  # Remove once preview secret loading no longer requires AAT vault access.
  name                = "jenkins-preview-mi"
  resource_group_name = "managed-identities-preview-rg"
}

module "key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product             = "s2s"
  env                 = var.env
  tenant_id           = var.tenant_id
  object_id           = var.jenkins_AAD_objectId
  jenkins_object_id   = data.azurerm_user_assigned_identity.jenkins.principal_id
  resource_group_name = azurerm_resource_group.rg.name

  # dcd_reform_dev_logs group object ID
  product_group_object_id = "70de400b-4f47-4f25-a4f0-45e1ee4e4ae3"
  common_tags             = var.common_tags

  managed_identity_object_ids = concat(
    [
      data.azurerm_user_assigned_identity.rpe-shared-identity.principal_id,
    ],
    var.env == "aat" ? [
      data.azurerm_user_assigned_identity.jenkins-preview[0].principal_id,
    ] : []
  )
}

data "azurerm_user_assigned_identity" "jenkins" {
  name                = "jenkins-${var.env == "sandbox" ? "sbox" : var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}

resource "azurerm_key_vault_secret" "connection-string" {
  name         = "app-insights-connection-string"
  value        = module.application_insights.connection_string
  key_vault_id = module.key-vault.key_vault_id
}
