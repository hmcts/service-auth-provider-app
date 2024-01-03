provider "azurerm" {
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
  source = "git@github.com:hmcts/terraform-module-application-insights?ref=main"

  env     = var.env
  product = var.product
  name    = "${var.product}-${var.component}-appinsights"

  resource_group_name = azurerm_resource_group.rg.name

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

module "key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product             = "s2s"
  env                 = var.env
  tenant_id           = var.tenant_id
  object_id           = var.jenkins_AAD_objectId
  resource_group_name = azurerm_resource_group.rg.name

  # dcd_reform_dev_logs group object ID
  product_group_object_id = "70de400b-4f47-4f25-a4f0-45e1ee4e4ae3"
  common_tags             = var.common_tags

  managed_identity_object_ids = [data.azurerm_user_assigned_identity.rpe-shared-identity.principal_id]
}

resource "azurerm_key_vault_secret" "connection-string" {
  name         = "app-insights-connection-string"
  value        = module.application_insights.appinsights.connection_string
  key_vault_id = module.key-vault.key_vault_id
}
