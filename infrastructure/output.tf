output "vaultUri" {
  value = "${module.key-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${module.key-vault.key_vault_name}"
}

output "microserviceName" {
  value = "${var.component}"
}
