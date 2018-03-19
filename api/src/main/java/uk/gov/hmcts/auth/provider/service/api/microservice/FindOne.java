package uk.gov.hmcts.auth.provider.service.api.microservice;

public interface FindOne<T> {
    T findOne(String id);
}
