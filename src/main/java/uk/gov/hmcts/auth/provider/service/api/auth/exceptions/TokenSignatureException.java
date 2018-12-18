package uk.gov.hmcts.auth.provider.service.api.auth.exceptions;

public class TokenSignatureException extends RuntimeException {
    public TokenSignatureException( Throwable e ){
        super(e);
    }
}
