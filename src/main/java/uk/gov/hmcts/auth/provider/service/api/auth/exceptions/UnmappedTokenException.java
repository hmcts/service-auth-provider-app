package uk.gov.hmcts.auth.provider.service.api.auth.exceptions;

public class UnmappedTokenException extends RuntimeException {
    public UnmappedTokenException( Throwable e ){
        super(e);
    }
}
