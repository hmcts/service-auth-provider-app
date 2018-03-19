package uk.gov.hmcts.auth.provider.service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class TokenDetails {

    @NotEmpty
    public final String microservice;

    @NotEmpty
    public final String oneTimePassword;

    public TokenDetails(
        @JsonProperty("microservice") String microservice,
        @JsonProperty("oneTimePassword") String oneTimePassword
    ) {
        this.microservice = microservice;
        this.oneTimePassword = oneTimePassword;
    }
}
