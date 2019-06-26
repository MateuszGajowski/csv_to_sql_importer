package pl.gajowski.mateusz.csvimporter.bond;

import lombok.Data;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import pl.gajowski.mateusz.csvimporter.common.batch.configuration.FileBatchImporterProperties;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BaseBatchTestConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("/org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("/org/springframework/batch/core/schema-h2.sql")
                .addScript("drop-schema.sql")
                .addScript("schema.sql")
                .generateUniqueName(true)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }


    @Bean
    public WriterResultHolder writerResultHolder() {
        return new WriterResultHolder();
    }

    @Bean
    public FileBatchImporterProperties fileBatchImporterProperties() {
        FileBatchImporterProperties properties = new FileBatchImporterProperties();
        properties.setDelimeter(",");
        return properties;
    }

    @Data
    public static class WriterResultHolder {
        private List<String> values = new ArrayList<>();
    }
}
