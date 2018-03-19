package uk.gov.hmcts.auth.provider.service.api.microservice;

import lombok.Data;

@Data
public class Microservice {
    private final String id;
    private final String key;
}
