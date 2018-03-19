package uk.gov.hmcts.auth.provider.service.api.auth.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(Throwable cause) {
        super(cause);
    }
}
