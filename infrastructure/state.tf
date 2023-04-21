terraform {
  backend "azurerm" {}

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.53.0"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "2.37.2"
    }
  }
}
