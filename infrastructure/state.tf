terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.104.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.49.1"
    }
  }
}
