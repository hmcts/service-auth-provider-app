provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

locals {
  vault_env = "${var.env == "preview" ? "aat" : var.env}"
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

data "azurerm_key_vault_secret" "bulk-scan-orchestrator" {
  name      = "microservicekey-bulk-scan-orchestrator"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

data "vault_generic_secret" "barApi" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/bar-api"
}

# region: for functional/smoke tests
# todo: create a separate test service just for this app
data "vault_generic_secret" "test_s2s_secret" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/microservice-keys/send-letter-tests"
}

resource "azurerm_key_vault_secret" "test-s2s-name" {
  name      = "test-service-name"
  value     = "send_letter_tests"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "test-s2s-secret" {
  name      = "test-service-secret"
  value     = "${data.vault_generic_secret.test_s2s_secret.data["value"]}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}
# endregion

module "s2s-api" {
  source       = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product      = "${var.product}-${var.component}"
  location     = "${var.location}"
  env          = "${var.env}"
  ilbIp        = "${var.ilbIp}"
  subscription = "${var.subscription}"
  capacity     = "${var.capacity}"
  common_tags  = "${var.common_tags}"

  app_settings = {
    JWT_KEY                                      = "${data.vault_generic_secret.jwtKey.data["value"]}"
    MICROSERVICEKEYS_CCD_ADMIN                  = "${data.vault_generic_secret.ccdAdmin.data["value"]}"
    MICROSERVICEKEYS_CCD_DATA                   = "${data.vault_generic_secret.ccdData.data["value"]}"
    MICROSERVICEKEYS_CCD_DEFINITION             = "${data.vault_generic_secret.ccdDefinition.data["value"]}"
    MICROSERVICEKEYS_CCD_GW                     = "${data.vault_generic_secret.ccdGw.data["value"]}"
    MICROSERVICEKEYS_CMC                        = "${data.vault_generic_secret.cmc.data["value"]}"
    MICROSERVICEKEYS_CMC_LEGAL_FRONTEND         = "${data.vault_generic_secret.cmcLegalFrontend.data["value"]}"
    MICROSERVICEKEYS_DIVORCE                    = "${data.vault_generic_secret.divorce.data["value"]}"
    MICROSERVICEKEYS_DIVORCE_CCD_SUBMISSION     = "${data.vault_generic_secret.divorceCcdSubmission.data["value"]}"
    MICROSERVICEKEYS_DIVORCE_FRONTEND           = "${data.vault_generic_secret.divorceFrontend.data["value"]}"
    MICROSERVICEKEYS_DIVORCE_CCD_VALIDATION     = "${data.vault_generic_secret.divorceCcdValidation.data["value"]}"
    MICROSERVICEKEYS_DIVORCE_DOCUMENT_UPLOAD    = "${data.vault_generic_secret.divorceDocumentUpload.data["value"]}"
    MICROSERVICEKEYS_DIVORCE_DOCUMENT_GENERATOR = "${data.vault_generic_secret.divorceDocumentGenerator.data["value"]}"
    MICROSERVICEKEYS_DRAFT_STORE_TESTS          = "${data.vault_generic_secret.draftStoreTests.data["value"]}"
    MICROSERVICEKEYS_JOBSCHEDULER               = "${data.vault_generic_secret.jobscheduler.data["value"]}"
    MICROSERVICEKEYS_SSCS                       = "${data.vault_generic_secret.sscs.data["value"]}"
    MICROSERVICEKEYS_SSCS_TRIBUNALS_CASE        = "${data.vault_generic_secret.sscsTribunalsCase.data["value"]}"
    MICROSERVICEKEYS_PROBATE_FRONTEND           = "${data.vault_generic_secret.probateFrontend.data["value"]}"
    MICROSERVICEKEYS_PROBATE_BACKEND            = "${data.vault_generic_secret.probateBackend.data["value"]}"
    MICROSERVICEKEYS_SEND_LETTER_CONSUMER       = "${data.vault_generic_secret.sendLetterConsumer.data["value"]}"
    MICROSERVICEKEYS_SEND_LETTER_TESTS          = "${data.vault_generic_secret.sendLetterTests.data["value"]}"
    MICROSERVICEKEYS_REFERENCE                  = "${data.vault_generic_secret.reference.data["value"]}"
    MICROSERVICEKEYS_EM_GW                      = "${data.vault_generic_secret.emGw.data["value"]}"
    MICROSERVICEKEYS_CMC_CLAIM_STORE            = "${data.vault_generic_secret.cmcClaimStore.data["value"]}"
    MICROSERVICEKEYS_CCD_PS                     = "${data.vault_generic_secret.ccdPs.data["value"]}"
    MICROSERVICEKEYS_FINREM                     = "${data.vault_generic_secret.finRem.data["value"]}"
    MICROSERVICEKEYS_FINREM_DRAFT_STORE         = "${data.vault_generic_secret.finRemDraftStore.data["value"]}"
    MICROSERVICEKEYS_JUI_WEBAPP                 = "${data.vault_generic_secret.juiWebapp.data["value"]}"
    MICROSERVICEKEYS_PUI_WEBAPP                 = "${data.vault_generic_secret.puiWebapp.data["value"]}"
    MICROSERVICEKEYS_COH_COR                    = "${data.vault_generic_secret.cohcor.data["value"]}"
    MICROSERVICEKEYS_BULK_SCAN_PROCESSOR        = "${data.vault_generic_secret.bulkScanProcessor.data["value"]}"
    MICROSERVICEKEYS_BULK_SCAN_PROCESSOR_TESTS  = "${data.vault_generic_secret.bulkScanProcessorTests.data["value"]}"
    MICROSERVICEKEYS_BULK_SCAN_ORCHESTRATOR     = "${data.azurerm_key_vault_secret.bulk-scan-orchestrator.value}"
    MICROSERVICEKEYS_BAR_API                    = "${data.vault_generic_secret.barApi.data["value"]}"
    TESTING_SUPPORT_ENABLED                      = "${var.testing_support}"
  }
}

module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  product             = "s2s"
  env                 = "${local.vault_env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.s2s-api.resource_group_name}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
}
