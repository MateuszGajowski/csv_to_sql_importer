package pl.gajowski.mateusz.csvimporter.bond.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.common.batch.FileBatchImporter;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BondBatchImporterConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

    @Bean
    public Job importBondJob(ItemWriter<BondEntity> bondItemWriter,
                             ItemProcessor<Bond, BondEntity> bondItemProcessor,
                             ItemReader<Bond> bondReader) throws Exception {
        return FileBatchImporter.<Bond, BondEntity>builder()
                .jobBuilderFactory(jobBuilderFactory)
                .stepBuilderFactory(stepBuilderFactory)
                .itemReader(bondReader)
                .itemProcessor(bondItemProcessor)
                .itemWriter(bondItemWriter)
                .properties(bondConfigurationProperties())
                .name("bond")
                .build()
                .createJob();
    }

    @ConfigurationProperties(prefix = "batch.bond")
    @Bean
    public FileBatchImporterProperties bondConfigurationProperties() {
        return new FileBatchImporterProperties();
    }

}
