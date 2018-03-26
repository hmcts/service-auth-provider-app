package uk.gov.hmcts.auth.provider.service.api.microservice;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

/**
 * SonarQube reports as error. Max allowed - 5 parents
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class UnknownMicroserviceException extends UnknownErrorCodeException {

    UnknownMicroserviceException() {
        super(AlertLevel.P4, (Throwable) null);
    }
}
