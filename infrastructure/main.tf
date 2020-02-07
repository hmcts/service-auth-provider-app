provider "azurerm" {
  version = "1.32.1"
}

locals {
  # environment whose vault should be used by preview
  vault_env = "${var.env}"

  vault_uri         = "${module.key-vault.key_vault_uri}"
  vault_name         = "${module.key-vault.key_vault_name}"

  # name of the service -> name of the vault secret holding the key
  microservice_key_names = {
    "CCD_ADMIN"                   = "microservicekey-ccd-admin"
    "CCD_CASE_DOCUMENT_AM_API"    = "microservicekey-ccd-case-document-am-api"
    "CCD_DATA"                    = "microservicekey-ccd-data"
    "CCD_DEFINITION"              = "microservicekey-ccd-definition"
    "CCD_DEFINITION_DESIGNER"     = "microservicekey-ccd-definition-designer"
    "CCD_GW"                      = "microservicekey-ccd-gw"
    "CCD_PS"                      = "microservicekey-ccd-ps"
    "CMC"                         = "microservicekey-cmc"
    "CMC_LEGAL_FRONTEND"          = "microservicekey-cmcLegalFrontend"
    "CMC_CLAIM_STORE"             = "microservicekey-cmcClaimStore"
    "CMC_CLAIM_EXTERNAL_API"      = "microservicekey-cmc-claim-external-api"
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
    "SSCS_BULKSCAN"               = "microservicekey-sscs-bulkscan"
    "SSCS_TRIBUNALS_CASE"         = "microservicekey-sscs-tribunals-case"
    "PROBATE_FRONTEND"            = "microservicekey-probate-frontend"
    "PROBATE_BACKEND"             = "microservicekey-probate-backend"
    "SEND_LETTER_CONSUMER"        = "microservicekey-send-letter-consumer"
    "SEND_LETTER_TESTS"           = "microservicekey-send-letter-tests"
    "EM_GW"                       = "microservicekey-em-gw"
    "FINREM_DOCUMENT_GENERATOR"   = "microservicekey-finrem-document-generator"
    "FINREM_NOTIFICATION_SERVICE" = "microservicekey-finrem-notification-service"
    "FINREM_CASE_ORCHESTRATION"   = "microservicekey-finrem-case-orchestration"
    "FINREM_PAYMENT_SERVICE"      = "microservicekey-finrem-payment-service"
    "FINREM_CCD_DATA_MIGRATOR"    = "microservicekey-finrem-ccd-data-migrator"
    "JUI_WEBAPP"                  = "microservicekey-jui-webapp"
    "PUI_WEBAPP"                  = "microservicekey-pui-webapp"
    "COH_COR"                     = "microservicekey-coh-cor"
    "BULK_SCAN_PROCESSOR"         = "microservicekey-bulk-scan-processor"
    "BULK_SCAN_PROCESSOR_TESTS"   = "microservicekey-bulk-scan-processor-tests"
    "BULK_SCAN_ORCHESTRATOR"      = "microservicekey-bulk-scan-orchestrator"
    "BULK_SCAN_PAYMENT_PROCESSOR" = "microservicekey-bulk-scan-payment-processor"
    "BULK_SCAN_CCD_SAMPLE_APP"    = "microservicekey-bulk-scan-ccd-sample-app"
    "BULK_SCAN_SAMPLE_APP_TESTS"  = "microservicekey-bulk-scan-sample-app-tests"
    "BAR_API"                     = "microservicekey-bar-api"
    "API_GW"                      = "microservicekey-api-gw"
    "EM_ANNOTATION_APP"           = "microservicekey-em-annotation-app"
    "EM_NPA_APP"                  = "microservicekey-em-npa-app"
    "CET"                         = "microservicekey-cet"
    "CET_FRONTEND"                = "microservicekey-cet-frontend"
    "FPL_CASE_SERVICE"            = "microservicekey-fpl-case-service"
    "IAC"                         = "microservicekey-iac"
    "EM_STITCHING_API"            = "microservicekey-em-stitching-api"
    "EM_CCD_ORCHESTRATOR"         = "microservicekey-em-ccd-orchestrator"
    "CCPAY_BUBBLE"                = "microservicekey-ccpay-bubble"
    "DG_TEMPLATE_MANAGEMENT_API"  = "microservicekey-dg-template-management"
    "DG_DOCASSEMBLY_API"          = "microservicekey-dg-docassembly-api"
    "RD_PROFESSIONAL_API"         = "microservicekey-rd-professional-api"
    "RD_USER_PROFILE_API"         = "microservicekey-rd-user-profile-api"
    "RD_JUDICIAL_API"             = "microservicekey-rd-judicial-api"
    "ETHOS_REPL_SERVICE"          = "microservicekey-ethos-repl-service"
    "EMPLOYMENT_TRIBUNALS"        = "microservicekey-employment-tribunals"
    "AM_ACCESSMGMT_API"           = "microservicekey-am-accessmgmt-api"
    "XUI_WEBAPP"                  = "microservicekey-xui-webapp"
    "CTSC_WORK_ALLOCATION"        = "microservicekey-ctsc-work-allocation"
  }

  microservice_secret_names = "${values(local.microservice_key_names)}"

  microservice_key_settings = "${zipmap(
                                    formatlist("MICROSERVICEKEYS_%s", keys(local.microservice_key_names)),
                                    data.azurerm_key_vault_secret.microservice_keys.*.value
                                )}"

  core_app_settings = {
    JWT_KEY                 = "${data.azurerm_key_vault_secret.jwt_key.value}"
    TESTING_SUPPORT_ENABLED = "${var.testing_support}"
  }

  sku_size = "${var.env == "prod" || var.env == "sprod" || var.env == "aat" ? "I2" : "I1"}"
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

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = "${var.location}"

  tags = "${var.common_tags}"
}

resource "azurerm_application_insights" "appinsights" {
  name                = "${var.product}-${var.component}-appinsights-${var.env}"
  location            = "${var.appinsights_location}"
  resource_group_name = "${azurerm_resource_group.rg.name}"
  application_type    = "Web"

  tags = "${var.common_tags}"
}

module "key-vault" {
  source              = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product             = "s2s"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${azurerm_resource_group.rg.name}"

  # dcd_reform_dev_logs group object ID
  product_group_object_id = "70de400b-4f47-4f25-a4f0-45e1ee4e4ae3"
  common_tags             = "${var.common_tags}"

  managed_identity_object_id = "${var.managed_identity_object_id}"
}
