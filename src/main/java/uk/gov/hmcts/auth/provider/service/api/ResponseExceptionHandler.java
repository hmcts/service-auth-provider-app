package uk.gov.hmcts.auth.provider.service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidAuthHeaderException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidOneTimePasswordException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenExpiredException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenSignatureException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.UnmappedTokenException;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;
import uk.gov.hmcts.auth.provider.service.api.microservice.UnknownMicroserviceException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnknownMicroserviceException.class)
    protected ResponseEntity<ErrorDto> handleUnknownMicroserviceException() {
        return unauthorized("Unknown microservice");
    }

    @ExceptionHandler(InvalidOneTimePasswordException.class)
    protected ResponseEntity<ErrorDto> handleInvalidOneTimePasswordException() {
        return unauthorized("Invalid one-time password");
    }

    @ExceptionHandler(TokenSignatureException.class)
    protected ResponseEntity<ErrorDto> handleTokenSignatureException() {
        return unauthorized("Invalid token signature");
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity<ErrorDto> handleTokenExpiredException() {
        return unauthorized("Token expired");
    }

    @ExceptionHandler(UnmappedTokenException.class)
    protected ResponseEntity<ErrorDto> handleUnmappedTokenException() {
        return unauthorized("Error verifying token");
    }

    @ExceptionHandler(InvalidAuthHeaderException.class)
    protected ResponseEntity<ErrorDto> handleInvalidAuthHeaderException() {
        return unauthorized("Invalid authorization header");
    }

    private ResponseEntity<ErrorDto> unauthorized(String message) {
        return status(UNAUTHORIZED).body(new ErrorDto(message));
    }
}
