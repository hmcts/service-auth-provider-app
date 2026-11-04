package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.warrenstrange.googleauth.HmacHashFunction;

public class Microservice {
    public final String id;
    public final String key;

    public final HmacHashFunction otpAlgorithm;

    public Microservice(String id, String key) {
        this(id, key, HmacHashFunction.HmacSHA1);
    }

    public Microservice(String id, String key, HmacHashFunction otpAlgorithm) {
        this.id = id;
        this.key = key;
        this.otpAlgorithm = java.util.Objects.requireNonNull(otpAlgorithm);
    }
}
