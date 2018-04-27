package uk.gov.hmcts.auth.provider.service.api.auth.jwt;

public interface JwtTool {
    String issueTokenForSubject(String subject);
    String verifyAndExtractSubject(String jwt);
}
