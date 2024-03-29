package uk.gov.hmcts.auth.provider.service.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.provider.service.api.model.SignInWithoutOtp;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/testing-support")
@ConditionalOnProperty("testing-support.enabled")
public class AuthIntegrationTestSupportController {

    private final JwtTool jwtTool;

    @Autowired
    public AuthIntegrationTestSupportController(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    @Operation(summary = "Generate a JWT token for arbitrary microservice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT token"),
    })
    @RequestMapping(value = "/lease", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> lease(@RequestBody SignInWithoutOtp signIn) {
        return ok(jwtTool.issueTokenForSubject(signIn.microservice));
    }
}
