terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.72.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.42.0"
    }
  }
}
