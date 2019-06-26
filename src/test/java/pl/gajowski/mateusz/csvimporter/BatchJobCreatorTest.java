package pl.gajowski.mateusz.csvimporter;

import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.gajowski.mateusz.csvimporter.bond.BaseBatchTestConfiguration;
import pl.gajowski.mateusz.csvimporter.common.batch.BatchJobCreator;
import pl.gajowski.mateusz.csvimporter.common.batch.configuration.FileBatchImporterProperties;
import pl.gajowski.mateusz.csvimporter.common.batch.loggers.ItemProcessLogger;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBatchTest
@ContextConfiguration(classes = {BaseBatchTestConfiguration.class, BatchJobCreatorTest.JobConfiguration.class})
public class BatchJobCreatorTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@Autowired
	private ItemReader<String> stepScopedItemReader;

	@Autowired
	private ItemReader<String> jobScopedItemReader;

	@Autowired
	private JobConfiguration.WriterResultHolder writerResultHolder;

	@Before
	public void setUp() {
	}

	public StepExecution getStepExecution() {
		StepExecution execution = MetaDataInstanceFactory.createStepExecution();
		execution.getExecutionContext().putString("input.data", "foo,bar");
		return execution;
	}

	public JobExecution getJobExecution() {
		JobExecution execution = MetaDataInstanceFactory.createJobExecution();
		execution.getExecutionContext().putString("input.data", "foo,bar");
		return execution;
	}

	@Test
	public void givenSimpleItemReader_readsItem() throws Exception {
		Assert.assertEquals("foo", this.stepScopedItemReader.read());
		Assert.assertEquals("bar", this.stepScopedItemReader.read());
		Assert.assertNull(this.stepScopedItemReader.read());
	}

	@Test
	public void givenBatchJob_processWholeJob() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		assertThat(writerResultHolder).isNotNull();
		assertThat(writerResultHolder.getValues()).hasSize(2);
		assertThat(writerResultHolder.getValues()).containsExactly("AA", "BB");
	}


	@Configuration
	@EnableBatchProcessing
	public static class JobConfiguration {
		@Bean
		public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
			return BatchJobCreator.<String, String>builder()
					.jobBuilderFactory(jobBuilderFactory)
					.stepBuilderFactory(stepBuilderFactory)
					.itemReader(new ListItemReader<>(Arrays.asList("aa", "bb")))
					.itemProcessor(String::toUpperCase)
					.itemWriter(items -> writerResultHolder().values.addAll(items))
					.properties(new FileBatchImporterProperties())
					.name("bond")
					.stepEnricher(it -> it.listener(new ItemProcessLogger<>()))
					.partitioner(new SinglePartitioner())
					.build()
					.createJob();
		}

		@Bean
		@StepScope
		public ItemReader<String> stepScopedItemReader(@Value("#{stepExecutionContext['input.data']}") String data) {
			return new ListItemReader<>(Arrays.asList(data.split(",")));
		}

		@Bean
		@JobScope
		public ItemReader<String> jobScopedItemReader(@Value("#{jobExecutionContext['input.data']}") String data) {
			return new ListItemReader<>(Arrays.asList(data.split(",")));
		}

		@Bean
		public WriterResultHolder writerResultHolder() {
			return new WriterResultHolder();
		}

		@Data
		public static class WriterResultHolder {
			private List<String> values = new ArrayList<>();
		}

		public static class SinglePartitioner implements Partitioner {

			@Override
			public Map<String, ExecutionContext> partition(int gridSize) {
				Map<String, ExecutionContext> map = new HashMap<>();
				map.put("key", new ExecutionContext());
				return map;
			}
		}

	}


}
