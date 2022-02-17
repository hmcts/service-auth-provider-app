package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

public class SignInWithoutOtp implements Serializable {

    @Serial
    private static final long serialVersionUID = 7053616822958826331L;

    @NotEmpty
    @Schema(description = "Name of the service")
    public final String microservice;

    public SignInWithoutOtp(
        @JsonProperty("microservice") String microservice
    ) {
        this.microservice = microservice;
    }
}
