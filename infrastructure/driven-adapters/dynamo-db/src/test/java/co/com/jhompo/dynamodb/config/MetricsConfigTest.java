package co.com.jhompo.dynamodb.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(MetricsConfig.class)
class MetricsConfigTest {

    @Test
    void shouldCreateMeterRegistryBean(ApplicationContext context) {
        // Verificar que el bean existe
        MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);

        assertThat(meterRegistry).isNotNull();
        assertThat(meterRegistry).isInstanceOf(SimpleMeterRegistry.class);
    }

    @Test
    void shouldCreateSimpleMeterRegistryWhenNoOtherBeanExists() {
        MetricsConfig config = new MetricsConfig();
        MeterRegistry registry = config.meterRegistry();

        assertThat(registry).isNotNull();
        assertThat(registry).isInstanceOf(SimpleMeterRegistry.class);
    }


}
