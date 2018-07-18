package uk.gov.hmcts.auth.provider.service.api.auth;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation("Sign in, lease auth token")
    @ApiResponses({
        @ApiResponse(code = 200, message = "JWT token"),
        @ApiResponse(code = 401, message = "Unauthorised. Returns error message.")
    })
    @ResponseBody
    public ResponseEntity<String> lease(
        @Valid @RequestBody SignIn signIn
    ) {
        String token = authService.lease(signIn.microservice, signIn.oneTimePassword);
        return ok(token);
    }

    @GetMapping("/details")
    @ApiOperation("Validate service's token")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Token valid. Returns service name"),
        @ApiResponse(code = 401, message = "Token invalid.")
    })
    @ResponseBody
    public ResponseEntity<String> authCheck(@RequestHeader(name = "Authorization") @NotEmpty String bearerToken) {
        String jwt = TokenExtractor.fromBearerToken(bearerToken);
        String serviceName = authService.verify(jwt);

        return ok(serviceName);
    }
}
