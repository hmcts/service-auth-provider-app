package uk.gov.hmcts.auth.provider.service.api.auth;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/testing-support")
@ConditionalOnProperty("auth.provider.service.testing-support.enabled")
public class AuthIntegrationTestSupportController {

    private final JwtTool jwtTool;

    @Autowired
    public AuthIntegrationTestSupportController(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    @RequestMapping(value = "/lease", method = POST)
    @ResponseBody
    public ResponseEntity<String> lease(@RequestParam @NotEmpty String microservice) {
        return ok(jwtTool.issueTokenForSubject(microservice));
    }
}
