output "vaultUri" {
  value = "${local.vault_uri}"
}

output "vaultName" {
  value = "${local.vault_name}"
}

output "microserviceName" {
  value = "${var.component}"
}

output "test_service_name" {
  value = "send_letter_tests"
}
