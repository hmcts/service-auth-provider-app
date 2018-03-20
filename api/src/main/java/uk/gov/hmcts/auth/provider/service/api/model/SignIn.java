package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class SignIn implements Serializable {

    private static final long serialVersionUID = -8745737989098046658L;

    @NotEmpty
    public final String microservice;

    @NotEmpty
    public final String oneTimePassword;

    public SignIn(
        @JsonProperty("microservice") String microservice,
        @JsonProperty("one_time_password") String oneTimePassword
    ) {
        this.microservice = microservice;
        this.oneTimePassword = oneTimePassword;
    }
}
