package uk.gov.hmcts.auth.provider.service.api.microservice;

public class Microservice {
    public final String id;
    public final String key;

    public Microservice(String id, String key) {
        this.id = id;
        this.key = key;
    }
}
