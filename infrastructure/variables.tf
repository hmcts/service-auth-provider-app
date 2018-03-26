variable "product" {
  type    = "string"
  default = "rpe"
}

variable "microservice" {
  type    = "string"
  default = "service-auth-provider"
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

variable "subscription" {
  type = "string"
}

variable "testing_support" {
  type = "string"
}
