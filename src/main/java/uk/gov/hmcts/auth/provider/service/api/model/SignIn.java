package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import jakarta.validation.constraints.NotEmpty;

public class SignIn implements Serializable {

    private static final long serialVersionUID = -8745737989098046658L;

    @Schema(description = "Name of the microservice")
    @NotEmpty
    public final String microservice;

    @Schema(description = "Google Authenticator OTP")
    @NotEmpty
    public final String oneTimePassword;

    public SignIn(
        @JsonProperty("microservice") String microservice,
        @JsonProperty("oneTimePassword") String oneTimePassword
    ) {
        this.microservice = microservice;
        this.oneTimePassword = oneTimePassword;
    }
}
