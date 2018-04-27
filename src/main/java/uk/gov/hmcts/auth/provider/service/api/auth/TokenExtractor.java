package uk.gov.hmcts.auth.provider.service.api.auth;

import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidAuthHeaderException;

public class TokenExtractor {

    /**
     * Extracts token from bearer token.
     */
    public static String fromBearerToken(String header) {
        if (!header.startsWith("Bearer ")) {
            throw new InvalidAuthHeaderException();
        } else {
            return header.substring("Bearer ".length());
        }
    }
}
