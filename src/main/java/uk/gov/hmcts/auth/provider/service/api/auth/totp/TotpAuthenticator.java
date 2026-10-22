package uk.gov.hmcts.auth.provider.service.api.auth.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.HmacHashFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

@Component
public class TotpAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(TotpAuthenticator.class);
    private final Map<HmacHashFunction, GoogleAuthenticator> authenticators = new EnumMap<>(HmacHashFunction.class);
    private final Clock clock;

    public TotpAuthenticator() {
        this(Clock.systemUTC());
    }

    TotpAuthenticator(Clock clock) {
        this.clock = clock;
        for (HmacHashFunction algorithm : HmacHashFunction.values()) {
            authenticators.put(algorithm, new GoogleAuthenticator(
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                    .setHmacHashFunction(algorithm).build()
            ));
        }
    }

    public String issueOneTimePassword(String base32Key) {
        return issueOneTimePassword(base32Key, HmacHashFunction.HmacSHA1);
    }

    public String issueOneTimePassword(String base32Key, HmacHashFunction algorithm) {
        return String.format(Locale.ROOT, "%06d", authenticators.get(algorithm).getTotpPassword(base32Key, clock.millis()));
    }

    public boolean isOneTimePasswordValid(String base32Key, String token) {
        return isOneTimePasswordValid(base32Key, token, HmacHashFunction.HmacSHA1);
    }

    public boolean isOneTimePasswordValid(String base32Key, String token, HmacHashFunction algorithm) {
        try {
            return authenticators.get(algorithm).authorize(base32Key, Integer.parseInt(token), clock.millis());
        } catch (NumberFormatException exc) {
            logger.warn("Could not convert token string into integer");
            return false;
        }
    }
}
