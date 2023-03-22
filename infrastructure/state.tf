terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 2.25"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.36.0"
    }
  }
}
