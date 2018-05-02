package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class SignInWithoutOtp implements Serializable {

    private static final long serialVersionUID = 7053616822958826331L;

    @NotEmpty
    @ApiModelProperty("Name of the service")
    public final String microservice;

    public SignInWithoutOtp(
        @JsonProperty("microservice") String microservice
    ) {
        this.microservice = microservice;
    }
}
