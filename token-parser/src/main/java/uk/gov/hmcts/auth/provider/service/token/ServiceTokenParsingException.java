package uk.gov.hmcts.auth.provider.service.token;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

/**
 * SonarQube reports as error. Max allowed - 5 parents
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ServiceTokenParsingException extends UnknownErrorCodeException {
    public ServiceTokenParsingException() {
        this(null);
    }

    public ServiceTokenParsingException(Throwable cause) {
        super(AlertLevel.P3, cause);
    }
}
