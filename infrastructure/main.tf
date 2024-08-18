provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = var.location

  tags = var.common_tags
}


data "azurerm_key_vault" "key_vault" {
  name                = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
}

module "application_insights" {
  source = "git@github.com:hmcts/terraform-module-application-insights?ref=main"

  env      = var.env
  product  = var.product
  name     = "${var.product}-${var.component}-appinsights"
  location = var.appinsights_location

  resource_group_name = azurerm_resource_group.rg.name

  common_tags = var.common_tags
}

data "azurerm_user_assigned_identity" "rpe-shared-identity" {
  name                = "rpe-shared-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}
