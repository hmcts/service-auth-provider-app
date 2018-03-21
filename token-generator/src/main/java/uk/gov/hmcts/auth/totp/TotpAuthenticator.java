package uk.gov.hmcts.auth.totp;

public interface TotpAuthenticator {
    String issueOneTimePassword(String base32Key);

    boolean isOneTimePasswordValid(String base32Key, String token);
}
