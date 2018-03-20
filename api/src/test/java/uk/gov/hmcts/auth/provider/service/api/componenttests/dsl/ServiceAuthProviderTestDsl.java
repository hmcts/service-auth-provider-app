package uk.gov.hmcts.auth.provider.service.api.componenttests.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceAuthProviderTestDsl {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private ResultActions resultActions;

    public ServiceAuthProviderTestDsl(MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    public ServiceAuthProviderGivenDsl given() {
        return new ServiceAuthProviderGivenDsl();
    }

    public class ServiceAuthProviderGivenDsl {
        public ServiceAuthProviderWhenDsl when() {
            return new ServiceAuthProviderWhenDsl();
        }
    }

    public class ServiceAuthProviderWhenDsl {

        public ServiceAuthProviderWhenDsl lease(String microservice, String password) throws Exception {
            resultActions = mvc.perform(MockMvcRequestBuilders
                .post("/lease")
                .content(createJson(microservice, password))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));
            return this;
        }

        public ServiceAuthProviderWhenDsl details(String token) throws Exception {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);

            resultActions = mvc.perform(MockMvcRequestBuilders
                .get("/details")
                .headers(httpHeaders)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));
            return this;
        }

        public ServiceAuthProviderWhenDsl and() {
            return this;
        }

        public ServiceAuthProviderThenDsl then() {
            return new ServiceAuthProviderThenDsl();
        }
    }

    public class ServiceAuthProviderThenDsl {
        public ServiceAuthProviderThenDsl unauthorized(Consumer<ErrorDto> consumer) throws Exception {
            resultActions.andExpect(status().isUnauthorized());
            consumer.accept(bodyAs(ErrorDto.class));
            return this;
        }

        public ServiceAuthProviderThenDsl forbidden(Consumer<ErrorDto> consumer) throws Exception {
            resultActions.andExpect(status().isForbidden());
            consumer.accept(bodyAs(ErrorDto.class));
            return this;
        }

        public ServiceAuthProviderThenDsl notFound(Consumer<ErrorDto> consumer) throws Exception {
            resultActions.andExpect(status().isNotFound());
            consumer.accept(bodyAs(ErrorDto.class));
            return this;
        }

        public ServiceAuthProviderThenDsl token(Consumer<String> consumer) throws Exception {
            resultActions.andExpect(status().isOk());
            consumer.accept(resultActions.andReturn().getResponse().getContentAsString());
            return this;
        }

        public ServiceAuthProviderThenDsl subject(Consumer<String> consumer) throws Exception {
            resultActions.andExpect(status().isOk());
            consumer.accept(resultActions.andReturn().getResponse().getContentAsString());
            return this;
        }

        public ServiceAuthProviderThenDsl and() {
            return this;
        }
    }

    private <T> List<T> bodyAsList(Class<T> valueType) throws java.io.IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsByteArray(), collectionType);
    }

    private <T> T bodyAs(Class<T> valueType) throws java.io.IOException {
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsByteArray(), valueType);
    }

    private String createJson(final String microservice, final String password) throws IOException {
        SignIn signIn = new SignIn(microservice, password);
        return new ObjectMapper().writeValueAsString(signIn);
    }
}
