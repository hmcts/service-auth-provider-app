variable "product" {
  type    = "string"
}

variable "component" {
  type    = "string"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "server_port" {
  type    = "string"
  default = "8080"
}

variable "env" {
  type = "string"
}

variable "vault_section" {
  default     = "test"
}

variable "ilbIp" {}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  type        = "string"
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "subscription" {
  type = "string"
}

variable "testing_support" {
  default = "false"
  type = "string"
}
