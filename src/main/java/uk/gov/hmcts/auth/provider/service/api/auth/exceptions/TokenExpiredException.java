package uk.gov.hmcts.auth.provider.service.api.auth.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException( Throwable e ){
        super(e);
    }
}
