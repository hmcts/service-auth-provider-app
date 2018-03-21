package uk.gov.hmcts.auth.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

public class StrangeTotpAuthenticator implements TotpAuthenticator {

    private final IGoogleAuthenticator googleAuthenticator;

    public StrangeTotpAuthenticator() {
        this(new GoogleAuthenticator());
    }

    public StrangeTotpAuthenticator(IGoogleAuthenticator googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    @Override
    public String issueOneTimePassword(String base32Key) {
        return String.valueOf(googleAuthenticator.getTotpPassword(base32Key));
    }

    @Override
    public boolean isOneTimePasswordValid(String base32Key, String token) {
        try {
            return googleAuthenticator.authorize(base32Key, Integer.parseInt(token));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
