terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.108.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.52.0"
    }
  }
}
