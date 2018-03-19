package uk.gov.hmcts.auth.provider.service.token;

import com.google.common.base.Supplier;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Suppliers.memoizeWithExpiration;

public class CachedServiceTokenGenerator implements ServiceTokenGenerator {
    private final Supplier<String> cachedSupplier;

    public CachedServiceTokenGenerator(ServiceTokenGenerator delegate, int ttlInSeconds) {
        this(delegate, ttlInSeconds, TimeUnit.SECONDS);
    }

    public CachedServiceTokenGenerator(ServiceTokenGenerator delegate, int ttl, TimeUnit timeUnit) {
        cachedSupplier = memoizeWithExpiration(delegate::generate, ttl, timeUnit);
    }

    @Override
    public String generate() throws ServiceTokenGenerationException {
        return cachedSupplier.get();
    }
}
