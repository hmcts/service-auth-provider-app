package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

public class SignIn implements Serializable {

    private static final long serialVersionUID = -8745737989098046658L;

    @ApiModelProperty("Name of the microservice")
    @NotEmpty
    @JsonProperty("microservice")
    public final String microservice;

    @ApiModelProperty("Google Authenticator OTP")
    @NotEmpty
    @JsonProperty("one_time_password")
    public final String oneTimePassword;

    public SignIn(
        @JsonProperty("microservice") String microservice,
        @JsonProperty("one_time_password") String oneTimePassword
    ) {
        this.microservice = microservice;
        this.oneTimePassword = oneTimePassword;
    }
}
