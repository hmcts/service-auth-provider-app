package uk.gov.hmcts.auth.provider.service.token;

public interface ServiceTokenParser {
    String parse(String jwt) throws ServiceTokenParsingException;
}
