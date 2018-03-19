package uk.gov.hmcts.auth.provider.service.token;

public interface ServiceTokenGenerator {
    String generate() throws ServiceTokenGenerationException;
}
