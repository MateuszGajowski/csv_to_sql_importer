package pl.gajowski.mateusz.csvimporter.configuration;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfiguration {

    @Value("${metrics.console.step:15s}")
    private Duration metricsLogStep;

    @Bean
    @ConditionalOnProperty(value = "metrics.console.enabled", havingValue = "true")
    public LoggingMeterRegistry loggingMeterRegistry() {
        LoggingRegistryConfig config = new LoggingRegistryConfig() {
            @Override
            public String get(String s) {
                return null;
            }

            @Override
            public Duration step() {
                return metricsLogStep;
            }
        };
        return LoggingMeterRegistry.builder(config).build();
    }
}
