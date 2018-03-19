package uk.gov.hmcts.auth.provider.service.token;

import org.junit.Test;
import org.mockito.Mockito;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CachedServiceTokenGeneratorTest {

    private final ServiceTokenGenerator delegate = Mockito.mock(ServiceTokenGenerator.class);

    @Test
    public void shouldReuserOldTokenIfTimeHasNotExpired() {
        CachedServiceTokenGenerator generator = new CachedServiceTokenGenerator(delegate, 900);
        generator.generate();
        generator.generate();
        generator.generate();
        verify(delegate, times(1)).generate();
    }

    @Test
    public void shouldMakeAnotherCallIfSpecifiedAmountHasPassed() throws InterruptedException {
        CachedServiceTokenGenerator generator = new CachedServiceTokenGenerator(delegate, 1, MILLISECONDS);
        generator.generate();
        verify(delegate, times(1)).generate();
        Thread.sleep(2);    // no easy way to fake the Clock for Suppliers.memoizeWithExpiration
        generator.generate();
        verify(delegate, times(2)).generate();
    }
}
