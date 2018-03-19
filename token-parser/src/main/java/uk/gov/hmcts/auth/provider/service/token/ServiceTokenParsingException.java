package uk.gov.hmcts.auth.provider.service.token;

public class ServiceTokenParsingException extends RuntimeException {
    public ServiceTokenParsingException() {
    }

    public ServiceTokenParsingException(Throwable cause) {
        super(cause);
    }
}
