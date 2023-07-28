terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.67.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.40.0"
    }
  }
}
