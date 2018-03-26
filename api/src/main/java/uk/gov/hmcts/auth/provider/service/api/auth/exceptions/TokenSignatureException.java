package uk.gov.hmcts.auth.provider.service.api.auth.exceptions;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

/**
 * SonarQube reports as error. Max allowed - 5 parents
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class TokenSignatureException extends UnknownErrorCodeException {

    public TokenSignatureException(Throwable cause) {
        super(AlertLevel.P4, cause);
    }
}
