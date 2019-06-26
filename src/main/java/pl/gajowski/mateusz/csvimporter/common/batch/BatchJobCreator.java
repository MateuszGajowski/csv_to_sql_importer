package pl.gajowski.mateusz.csvimporter.common.batch;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.gajowski.mateusz.csvimporter.common.batch.configuration.FileBatchImporterProperties;
import pl.gajowski.mateusz.csvimporter.common.batch.loggers.JobExecutionTimeLogger;

import java.util.function.Consumer;

@AllArgsConstructor
public class BatchJobCreator<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobCreator.class);

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
    private final Partitioner partitioner;

    @NonNull
    private final FileBatchImporterProperties properties;
    @NonNull
    private final String name;

    private final Consumer<SimpleStepBuilder<I, O>> stepEnricher;
    private final Consumer<FlowJobBuilder> jobEnricher;


    public static <I, O> BatchJobCreatorBuilder<I, O> builder() {
        return new BatchJobCreatorBuilder<I, O>();
    }


    public Job createJob()  {
        LOGGER.info("Creating job={}, with properties={}", name, properties);

        final FlowJobBuilder builder = jobBuilderFactory.get(name + "_job")
                .incrementer(new RunIdIncrementer())
                .flow(partitionStep())
                .end();

        if (jobEnricher != null) {
            jobEnricher.accept(builder);
        }

        return builder.build();
    }

    private Step partitionStep() {
        return stepBuilderFactory.get(name + "_main_step")
                .partitioner(name + "_partitioned", partitioner)
                .step(mainStep())
                .gridSize(properties.getGridSize())
                .taskExecutor(taskExecutor())
                .build();
    }

    private Step mainStep() {
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


    public static class BatchJobCreatorBuilder<I, O> {
        private @NonNull JobBuilderFactory jobBuilderFactory;
        private @NonNull StepBuilderFactory stepBuilderFactory;
        private @NonNull ItemReader<I> itemReader;
        private @NonNull ItemProcessor<I, O> itemProcessor;
        private @NonNull ItemWriter<O> itemWriter;
        private @NonNull FileBatchImporterProperties properties;
        private @NonNull String name;
        private @NonNull Partitioner partitioner;
        private Consumer<SimpleStepBuilder<I, O>> stepEnricher;
        private Consumer<FlowJobBuilder> jobEnricher;

        BatchJobCreatorBuilder() {
        }

        public BatchJobCreatorBuilder<I, O> jobBuilderFactory(@NonNull JobBuilderFactory jobBuilderFactory) {
            this.jobBuilderFactory = jobBuilderFactory;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> stepBuilderFactory(@NonNull StepBuilderFactory stepBuilderFactory) {
            this.stepBuilderFactory = stepBuilderFactory;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> itemReader(@NonNull ItemReader<I> itemReader) {
            this.itemReader = itemReader;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> itemProcessor(@NonNull ItemProcessor<I, O> itemProcessor) {
            this.itemProcessor = itemProcessor;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> itemWriter(@NonNull ItemWriter<O> itemWriter) {
            this.itemWriter = itemWriter;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> properties(@NonNull FileBatchImporterProperties properties) {
            this.properties = properties;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> stepEnricher(Consumer<SimpleStepBuilder<I, O>> stepEnricher) {
            this.stepEnricher = stepEnricher;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> jobEnricher(Consumer<FlowJobBuilder> jobEnricher) {
            this.jobEnricher = jobEnricher;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> partitioner(Partitioner partitioner) {
            this.partitioner = partitioner;
            return this;
        }

        public BatchJobCreatorBuilder<I, O> multiResourcePartitioner() {
            this.partitioner = multiResourcePartiioner();
            return this;
        }

        public BatchJobCreator<I, O> build() {
            return new BatchJobCreator<>(jobBuilderFactory, stepBuilderFactory, itemReader, itemProcessor, itemWriter, partitioner, properties, name, stepEnricher, jobEnricher);
        }

        public String toString() {
            return "BatchJobCreator.BatchJobCreatorBuilder(jobBuilderFactory=" + this.jobBuilderFactory + ", stepBuilderFactory=" + this.stepBuilderFactory + ", itemReader=" + this.itemReader + ", itemProcessor=" + this.itemProcessor + ", itemWriter=" + this.itemWriter + ", properties=" + this.properties + ", name=" + this.name + ", stepEnricher=" + this.stepEnricher + ")";
        }

        @SneakyThrows
        private Partitioner multiResourcePartiioner() {
            final MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            partitioner.setResources(resolver.getResources(properties.getFilesLocation()));
            return partitioner;
        }
    }
}
