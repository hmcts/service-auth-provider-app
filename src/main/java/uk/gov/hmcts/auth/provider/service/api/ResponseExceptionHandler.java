package uk.gov.hmcts.auth.provider.service.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    protected ResponseEntity handleUnknownMicroserviceException(HttpServletRequest req) {
        return unauthorized("Unknown microservice", req);
    }

    @ExceptionHandler(InvalidOneTimePasswordException.class)
    protected ResponseEntity handleInvalidOneTimePasswordException(HttpServletRequest req) {
        return unauthorized("Invalid one-time password", req);
    }

    @ExceptionHandler(TokenSignatureException.class)
    protected ResponseEntity handleTokenSignatureException(HttpServletRequest req) {
        return unauthorized("Invalid token signature", req);
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity handleTokenExpiredException(HttpServletRequest req) {
        return unauthorized("Token expired", req);
    }

    @ExceptionHandler(UnmappedTokenException.class)
    protected ResponseEntity handleUnmappedTokenException(HttpServletRequest req) {
        return unauthorized("Error verifying token", req);
    }

    @ExceptionHandler(InvalidAuthHeaderException.class)
    protected ResponseEntity handleInvalidAuthHeaderException(HttpServletRequest req) {
        return unauthorized("Invalid authorization header", req);
    }

    private ResponseEntity unauthorized(String msg, HttpServletRequest req) {
        if (req.getHeader(HttpHeaders.ACCEPT).equals(MediaType.TEXT_PLAIN_VALUE)) {
            return status(UNAUTHORIZED).body(msg);
        } else {
            // use json by default
            return status(UNAUTHORIZED).body(new ErrorDto(msg));
        }
    }
}
