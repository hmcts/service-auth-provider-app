provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

locals {
  is_preview          = "${var.env == "preview" || var.env == "spreview"}"

  # environment whose vault should be used by preview
  vault_env           = "${var.env == "preview" ? "aat" : var.env == "spreview" ? "saat" : var.env }"

  preview_vault_uri   = "https://s2s-${local.vault_env}.vault.azure.net/"
  vault_uri           = "${local.is_preview ? local.preview_vault_uri : module.key-vault.key_vault_uri}"

  preview_vault_name  = "s2s-${local.vault_env}"
  vault_name          = "${local.is_preview ? local.preview_vault_name : module.key-vault.key_vault_name}"
}

data "vault_generic_secret" "jwtKey" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/jwt-key"
}

data "vault_generic_secret" "ccdAdmin" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/ccd-admin"
}

data "vault_generic_secret" "ccdData" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/ccd-data"
}

data "vault_generic_secret" "ccdDefinition" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/ccd-definition"
}

data "vault_generic_secret" "ccdGw" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/ccd-gw"
}

data "vault_generic_secret" "ccdPs" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/ccd-ps"
}

data "vault_generic_secret" "cmc" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/cmc"
}

data "vault_generic_secret" "cmcLegalFrontend" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/cmcLegalFrontend"
}

data "vault_generic_secret" "cmcClaimStore" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/cmcClaimStore"
}

data "vault_generic_secret" "divorce" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorce"
}

data "vault_generic_secret" "divorceFrontend" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorce-frontend"
}

data "vault_generic_secret" "divorceCcdSubmission" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorceCcdSubmission"
}

data "vault_generic_secret" "divorceCcdValidation" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorceCcdValidation"
}

data "vault_generic_secret" "divorceDocumentUpload" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentUpload"
}

data "vault_generic_secret" "divorceDocumentGenerator" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentGenerator"
}

data "vault_generic_secret" "draftStoreTests" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/draftStoreTests"
}

data "vault_generic_secret" "jobscheduler" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/platformJobScheduler"
}

data "vault_generic_secret" "reference" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/reference"
}

data "vault_generic_secret" "sscs" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/sscs"
}

data "vault_generic_secret" "sscsTribunalsCase" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/sscs-tribunals-case"
}

data "vault_generic_secret" "probateFrontend" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/probate-frontend"
}

data "vault_generic_secret" "probateBackend" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/probate-backend"
}

data "vault_generic_secret" "sendLetterConsumer" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/send-letter-consumer"
}

data "vault_generic_secret" "sendLetterTests" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/send-letter-tests"
}

data "vault_generic_secret" "emGw" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/em-gw"
}

data "vault_generic_secret" "finRem" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/finrem"
}

data "vault_generic_secret" "finRemDraftStore" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/finrem-draft-store"
}

data "vault_generic_secret" "juiWebapp" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/jui-webapp"
}

data "vault_generic_secret" "puiWebapp" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/pui-webapp"
}

data "vault_generic_secret" "cohcor" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/coh-cor"
}

data "vault_generic_secret" "bulkScanProcessor" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/bulk-scan-processor"
}

data "vault_generic_secret" "bulkScanProcessorTests" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/bulk-scan-processor-tests"
}

data "vault_generic_secret" "barApi" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/bar-api"
}

data "azurerm_key_vault_secret" "microservice_keys" {
  name      = "${local.microservice_secret_names[count.index]}"
  vault_uri = "${local.vault_uri}"
  count     = "${length(local.microservice_key_names)}"
}

data "azurerm_key_vault_secret" "jwt_key" {
  name      = "jwt-key"
  vault_uri = "${local.vault_uri}"
}

locals {
  # name of the service -> name of the vault secret holding the key
  microservice_key_names = {
    "CCD_ADMIN"                   = "microservicekey-ccd-admin"
    "CCD_DATA"                    = "microservicekey-ccd-data"
    "CCD_DEFINITION"              = "microservicekey-ccd-definition"
    "CCD_GW"                      = "microservicekey-ccd-gw"
    "CCD_PS"                      = "microservicekey-ccd-ps"
    "CMC"                         = "microservicekey-cmc"
    "CMC_LEGAL_FRONTEND"          = "microservicekey-cmcLegalFrontend"
    "CMC_CLAIM_STORE"             = "microservicekey-cmcClaimStore"
    "DIVORCE"                     = "microservicekey-divorce"
    "DIVORCE_FRONTEND"            = "microservicekey-divorce-frontend"
    "DIVORCE_CCD_SUBMISSION"      = "microservicekey-divorceCcdSubmission"
    "DIVORCE_CCD_VALIDATION"      = "microservicekey-divorceCcdValidation"
    "DIVORCE_DOCUMENT_UPLOAD"     = "microservicekey-divorceDocumentUpload"
    "DIVORCE_DOCUMENT_GENERATOR"  = "microservicekey-divorceDocumentGenerator"
    "DRAFT_STORE_TESTS"           = "microservicekey-draftStoreTests"
    "JOBSCHEDULER"                = "microservicekey-platformJobScheduler"
    "REFERENCE"                   = "microservicekey-reference"
    "SSCS"                        = "microservicekey-sscs"
    "SSCS_TRIBUNALS_CASE"         = "microservicekey-sscs-tribunals-case"
    "PROBATE_FRONTEND"            = "microservicekey-probate-frontend"
    "PROBATE_BACKEND"             = "microservicekey-probate-backend"
    "SEND_LETTER_CONSUMER"        = "microservicekey-send-letter-consumer"
    "SEND_LETTER_TESTS"           = "microservicekey-send-letter-tests"
    "EM_GW"                       = "microservicekey-em-gw"
    "FINREM"                      = "microservicekey-finrem"
    "FINREM_DRAFT_STORE"          = "microservicekey-finrem-draft-store"
    "JUI_WEBAPP"                  = "microservicekey-jui-webapp"
    "PUI_WEBAPP"                  = "microservicekey-pui-webapp"
    "COH_COR"                     = "microservicekey-coh-cor"
    "BULK_SCAN_PROCESSOR"         = "microservicekey-bulk-scan-processor"
    "BULK_SCAN_PROCESSOR_TESTS"   = "microservicekey-bulk-scan-processor-tests"
    "BULK_SCAN_ORCHESTRATOR"      = "microservicekey-bulk-scan-orchestrator"
    "BAR_API"                     = "microservicekey-bar-api"
  }

  microservice_secret_names = "${values(local.microservice_key_names)}"

  microservice_key_settings = "${zipmap(
                                    formatlist("MICROSERVICEKEYS_%s", keys(local.microservice_key_names)),
                                    data.azurerm_key_vault_secret.microservice_keys.*.value
                                )}"

  core_app_settings = {
    JWT_KEY                                     = "${data.azurerm_key_vault_secret.jwt_key.value}"
    TESTING_SUPPORT_ENABLED                     = "${var.testing_support}"
  }
}

module "s2s-api" {
  source       = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product      = "${var.product}-${var.component}"
  location     = "${var.location}"
  env          = "${var.env}"
  ilbIp        = "${var.ilbIp}"
  subscription = "${var.subscription}"
  capacity     = "${var.capacity}"
  common_tags  = "${var.common_tags}"

  app_settings = "${merge(local.core_app_settings, local.microservice_key_settings)}"
}

module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=feature/add-count-input-variable"
  product             = "s2s"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.s2s-api.resource_group_name}"
  # dcd_reform_dev_logs group object ID
  product_group_object_id = "70de400b-4f47-4f25-a4f0-45e1ee4e4ae3"
  count               = "${local.is_preview ? 0 : 1}"
}
