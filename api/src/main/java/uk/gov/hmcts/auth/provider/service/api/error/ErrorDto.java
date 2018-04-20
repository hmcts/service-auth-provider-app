package uk.gov.hmcts.auth.provider.service.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDto {

    public final String message;

    public ErrorDto(@JsonProperty("message") String message) {
        this.message = message;
    }
}
