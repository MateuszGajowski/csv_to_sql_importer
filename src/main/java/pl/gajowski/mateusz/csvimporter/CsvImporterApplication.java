package pl.gajowski.mateusz.csvimporter;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@SpringBootApplication
@Configuration
public class CsvImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvImporterApplication.class, args);
	}

	@Bean
	public LoggingMeterRegistry loggingMeterRegistry() {
		LoggingRegistryConfig config = new LoggingRegistryConfig() {
			@Override
			public String get(String s) {
				return null;
			}

			@Override
			public Duration step() {
				return Duration.ofSeconds(15);
			}
		};
		return LoggingMeterRegistry.builder(config).build();
	}
}
