package uk.gov.hmcts.auth.provider.service.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/lease")
    @Operation(summary = "Sign in, lease auth token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT token"),
        @ApiResponse(responseCode = "401", description = "Unauthorised. Returns error message.")
    })
    @ResponseBody
    public ResponseEntity<String> lease(
        @Valid @RequestBody SignIn signIn
    ) {
        String token = authService.lease(signIn.microservice, signIn.oneTimePassword);
        return ok(token);
    }

    @GetMapping("/details")
    @Operation(summary = "Validate service's token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token valid. Returns service name"),
        @ApiResponse(responseCode = "401", description = "Token invalid.")
    })
    @ResponseBody
    public ResponseEntity<String> authCheck(@RequestHeader(name = "Authorization") @NotEmpty String bearerToken) {
        String jwt = TokenExtractor.fromBearerToken(bearerToken);
        String serviceName = authService.verify(jwt);

        return ok(serviceName);
    }
}
