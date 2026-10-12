# Service auth provider 


[![Build Status](https://travis-ci.org/hmcts/service-auth-provider-app.svg?branch=master)](https://travis-ci.org/hmcts/service-auth-provider-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0cb10a161dc24d0092470cda7c304c87)](https://app.codacy.com/app/HMCTS/service-auth-provider-app)
[![codecov](https://codecov.io/gh/hmcts/service-auth-provider-app/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/service-auth-provider-app)

This microservice is used to authenticate services across HMCTS.

## Getting Started

### Prerequisites
- [JDK 8](https://java.com)
 
### Building
To build the project execute the following command:
```bash
$ ./gradlew build
```

### Configuration

In order to setup Service Auth Provider to work with a client service, you need to do the following:

* In the Azure Key Vault named `s2s-{environment}` add the service's secret used for generating OTPs (one-time passwords).
This has to be done in each environment the service is going to be deployed to. Service Auth Provider will use that secret
for validating OTPs. It has to be a BASE32-encoded sequence of ten random bytes (16 characters after encoding). By convention,
the Azure Key Vault secret's name should follow this format: `microservicekey-{service-name}`. [Here's](#generating-secret) how to generate it.
* To make it work on AKS , Add the client service name (as in HTTP requests ) and Azure Key Vault secret created in the previous steps to [values.yaml](charts/rpe-service-auth-provider/values.yaml). 
A service **TEST_SERVICE** with secret key **microservicekey-test-service**  needs to be configured as below :

 ```
java:
  keyVaults:
    "s2s":
      secrets:
        - name: microservicekey-test-service
          alias: microserviceKeys.test_service
```
 Note: **test_service** is lower cased in alias mapping, though its not mandatory. 
* Bump the helm chart minor version in [Chart.yaml](charts/rpe-service-auth-provider/Chart.yaml) 

#### <a name="generating-secret"></a>Generating the microservice secret

Here's a sample Java snippet to generate a microservice secret:

```
byte[] bytes = new byte[10];
SecureRandom.getInstanceStrong().nextBytes(bytes);
String secret = new Base32().encodeAsString(bytes);
```

Sample Python code to generate that secret:

```
import os
import base64
base64.b32encode(os.urandom(10))
```
#### Writing a secret into all the needed vaults
Use `./bin/set-secret-in-all-vaults <microservice-name>` with the hyphenated secret-name suffix
(for example, `deed-poll-api`). It checks sandbox, demo, aat, prod, perftest and ithc before writing,
preserves existing enabled secrets, and creates only missing secrets. Each new secret contains an
independently generated BASE32 encoding of ten cryptographically random bytes. It then verifies all
six vaults; paste the actual output into the PR template.

The script requires Python 3, `azure-cli`, an authenticated session (`az login`), and the appropriate
vault permissions. The documented AAD group is `dcd_reform_dev_logs`; verify access for every target
vault before provisioning. The preflight lists active secret metadata in all vaults and deleted-secret
metadata where a name is missing. Access failures, disabled secrets, malformed responses and soft-deleted
names stop the run with a non-zero exit code. A disabled or deleted secret needs a separate decision;
the script does not enable, recover, purge or rotate it.

To check without creating anything, run:

```bash
./bin/check-all-vaults-for-s2s-secret <microservice-name>
```

Both commands read metadata only for existing secrets. New values are passed to Azure through a
private temporary file, removed after the write; they are not printed or put in command-line arguments.
The checker verifies presence and enabled status, not secret contents, expiry, or successful token leasing.

Run provisioning for a given service sequentially: the script checks again immediately before each
write, but Azure's [`secret set`](https://learn.microsoft.com/en-us/cli/azure/keyvault/secret#az-keyvault-secret-set)
is not an atomic create-only operation. Another operator creating the
same secret between that check and the write could still be overwritten. Coordinate provisioning
rather than running concurrent copies. If a write fails or its result is uncertain, the script stops
without retrying it. Inspect metadata before rerunning; completed creations are preserved on a rerun.
There is no rollback of secrets already created in earlier vaults.

Run the script tests locally (a fake Azure CLI is used; no credentials or network needed):

```bash
python3 -m unittest discover -s bin/tests -v
```

#### Getting the change to production
Create a pull request after you've set the secret in all vaults, once your build is green you can request a review by posting on the #platops-code-review Slack channel.
If the build is green, and the PR template was filled out correctly showing that the secret has been entered in all vaults then the change will be merged and a build automatically triggered. Once the build is finished and passed it will be automatically deployed to the AAT and production environments. If you need it in demo you can merge the code to demo and `git push`.

#### Reading the secret in client service's infrastructure code

Once the service's secret is stored in Azure Key Vault, it can be retrieved
from the S2S key vault with Terraform and written into your own vault.

```hcl
data "azurerm_key_vault" "key_vault" {
  name                = "${var.product}-${var.env}" # update these values if required
  resource_group_name = "${var.product}-${var.env}" # update these values if required
}

data "azurerm_key_vault" "s2s_vault" {
  name                = "s2s-${var.env}"
  resource_group_name = "rpe-service-auth-provider-${var.env}"
}

data "azurerm_key_vault_secret" "key_from_vault" {
  name         = "microservicekey-ccd-data" # update key name e.g. microservicekey-your-name
  key_vault_id = data.azurerm_key_vault.s2s_vault.id
}

resource "azurerm_key_vault_secret" "s2s" {
  name         = "s2s-secret"
  value        = data.azurerm_key_vault_secret.key_from_vault.value
  key_vault_id = data.azurerm_key_vault.key_vault.id
}
```

### Running
To run the app execute:
```bash
$ ./gradlew bootRun
```
You can also run the app on docker.  
To build:
```bash
$ docker-compose build
```
And to run:
```bash
$ docker-compose up
```
Dockerized app comes with preconfigured sample service. See [docker-compose.yml](docker-compose.yml) for details.

## Documentation
API documentation is provided with Swagger.  
Json spec is available under standard `/v3/api-docs` route.

Flow diagram can be found [here](docs/design.md)

## Developing

### Unit tests
To run all unit tests execute the following command:
```bash
$ ./gradlew test
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
