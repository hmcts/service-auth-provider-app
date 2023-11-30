terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.82.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.46.0"
    }
  }
}
