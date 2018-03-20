package uk.gov.hmcts.auth.provider.service.api.auth;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/lease")
    @ResponseBody
    public ResponseEntity<?> lease(
        @RequestParam @NotEmpty String microservice,
        @RequestParam @NotEmpty String oneTimePassword
    ) {
        String token = authService.lease(microservice, oneTimePassword);
        return ok(token);
    }

    @PostMapping("/v1/lease")
    @ResponseBody
    public ResponseEntity<?> lease(
        @Valid @RequestBody SignIn signIn
    ) {
        String token = authService.lease(signIn.microservice, signIn.oneTimePassword);
        return ok(token);
    }

    @GetMapping("/details")
    @ResponseBody
    public ResponseEntity<?> authCheck(@RequestHeader(name = "Authorization") @NotEmpty String bearerToken) {
        String jwt = TokenExtractor.fromBearerToken(bearerToken);
        String serviceName = authService.verify(jwt);

        return ok(serviceName);
    }
}
