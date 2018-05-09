provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "jwtKey" {
  path = "secret/${var.vault_section}/ccidam/service-auth-provider/api/jwt-key"
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

  app_settings = {
    AUTH_PROVIDER_SERVICE_SERVER_JWT_KEY                                      = "${data.vault_generic_secret.jwtKey.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CCD_DATA                   = "${data.vault_generic_secret.ccdData.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CCD_DEFINITION             = "${data.vault_generic_secret.ccdDefinition.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CCD_GW                     = "${data.vault_generic_secret.ccdGw.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CMC                        = "${data.vault_generic_secret.cmc.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CMC_LEGAL_FRONTEND         = "${data.vault_generic_secret.cmcLegalFrontend.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE                    = "${data.vault_generic_secret.divorce.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE_CCD_SUBMISSION     = "${data.vault_generic_secret.divorceCcdSubmission.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE_FRONTEND           = "${data.vault_generic_secret.divorceFrontend.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE_CCD_VALIDATION     = "${data.vault_generic_secret.divorceCcdValidation.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE_DOCUMENT_UPLOAD    = "${data.vault_generic_secret.divorceDocumentUpload.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_DIVORCE_DOCUMENT_GENERATOR = "${data.vault_generic_secret.divorceDocumentGenerator.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_JOBSCHEDULER               = "${data.vault_generic_secret.jobscheduler.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_SSCS                       = "${data.vault_generic_secret.sscs.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_SSCS_TRIBUNALS_CASE        = "${data.vault_generic_secret.sscsTribunalsCase.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_PROBATE_FRONTEND           = "${data.vault_generic_secret.probateFrontend.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_PROBATE_BACKEND            = "${data.vault_generic_secret.probateBackend.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_SEND_LETTER_CONSUMER       = "${data.vault_generic_secret.sendLetterConsumer.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_SEND_LETTER_TESTS          = "${data.vault_generic_secret.sendLetterTests.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_REFERENCE                  = "${data.vault_generic_secret.reference.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_EM_GW                      = "${data.vault_generic_secret.emGw.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CMC_CLAIM_STORE            = "${data.vault_generic_secret.cmcClaimStore.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_FINREM                     = "${data.vault_generic_secret.finRem.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_FINREM_DRAFT_STORE         = "${data.vault_generic_secret.finRemDraftStore.data["value"]}"
    AUTH_PROVIDER_SERVICE_SERVER_MICROSERVICE_KEYS_CCD_PS                     = "${data.vault_generic_secret.ccdPs.data["value"]}"
    AUTH_PROVIDER_SERVICE_TESTING_SUPPORT_ENABLED                             = "${var.testing_support}"
  }
}

module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.s2s-api.resource_group_name}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
}
