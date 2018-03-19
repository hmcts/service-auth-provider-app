package uk.gov.hmcts.auth.provider.service.token;

public class ServiceTokenGenerationException extends RuntimeException {
    public ServiceTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
