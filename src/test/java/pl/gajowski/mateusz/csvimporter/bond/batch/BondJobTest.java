package pl.gajowski.mateusz.csvimporter.bond.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.gajowski.mateusz.csvimporter.bond.BaseBatchTestConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.batch.configuration.BondBatchImporterJobConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.batch.processor.BondProcessorConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.batch.reader.BondReaderConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.batch.reader.BondReaderTest;
import pl.gajowski.mateusz.csvimporter.bond.batch.writter.BondWriterConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BaseBatchTestConfiguration.class,
        BondWriterConfiguration.class,
        BondReaderConfiguration.class,
        BondProcessorConfiguration.class,
        BondBatchImporterJobConfiguration.class})
@SpringBatchTest
public class BondJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StepExecution getStepExecution() {
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.getExecutionContext().putString("fileName",
                BondReaderTest.class.getClassLoader().getResource("input/TRIM Bond ALL - Stressed.csv").toExternalForm());
        return execution;
    }

    @Test
    public void givenJobConfiguration_processWholeFile() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Long itemsCount = jdbcTemplate.queryForObject("select count(*) from JTD_BOND_ALL", Long.class);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(itemsCount).isEqualTo(4080);
    }
}
