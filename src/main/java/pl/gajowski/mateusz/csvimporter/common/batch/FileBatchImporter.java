package pl.gajowski.mateusz.csvimporter.common.batch;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.gajowski.mateusz.csvimporter.bond.batch.configuration.FileBatchImporterProperties;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Builder
public class FileBatchImporter<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBatchImporter.class);

    @NonNull
    private final JobBuilderFactory jobBuilderFactory;
    @NonNull
    private final StepBuilderFactory stepBuilderFactory;
    @NonNull
    private final ItemReader<I> itemReader;
    @NonNull
    private final ItemProcessor<I, O> itemProcessor;
    @NonNull
    private final ItemWriter<O> itemWriter;

    @NonNull
    private final FileBatchImporterProperties properties;
    @NonNull
    private final String name;

    private final Consumer<SimpleStepBuilder<I, O>> stepEnricher;


    public Job createJob() throws Exception {
        LOGGER.info("Creating job={}, with properties={}", name, properties);

        return jobBuilderFactory.get(name + "_job")
                .incrementer(new RunIdIncrementer())
                .flow(partitionStep())
                .end()
                .build();
    }

    private Step partitionStep() throws Exception {
        return stepBuilderFactory.get(name + "_main_step")
                .partitioner(fileToWriterStep())
                .partitioner(name + "_partitioned", partitioner())
                .gridSize(properties.getGridSize())
                .taskExecutor(taskExecutor())
                .build();
    }

    private Step fileToWriterStep() {
        SimpleStepBuilder<I, O> step = stepBuilderFactory.get(name + "_processing_step")
                .<I, O>chunk(properties.getChunk())
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter);

        if (stepEnricher != null) {
            stepEnricher.accept(step);
        }

        return step.build();
    }

    private Partitioner partitioner() throws Exception {
        final MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        partitioner.setResources(resolver.getResources(properties.getFilesLocation()));
        return partitioner;
    }

    private ThreadPoolTaskExecutor taskExecutor() {
        final FileBatchImporterProperties.ThreadPoolConfig properties
                = this.properties.getThreadPoolConfig();

        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(properties.getMaxPoolSize());
        taskExecutor.setCorePoolSize(properties.getCorePoolSize());
        taskExecutor.setQueueCapacity(properties.getQueueCapacity());
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
