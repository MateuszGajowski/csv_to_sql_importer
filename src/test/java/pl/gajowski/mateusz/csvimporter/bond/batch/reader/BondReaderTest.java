package pl.gajowski.mateusz.csvimporter.bond.batch.reader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import pl.gajowski.mateusz.csvimporter.bond.BaseBatchTestConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BaseBatchTestConfiguration.class,
        BondReaderConfiguration.class})
@TestExecutionListeners(
        listeners = {StepScopeTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class BondReaderTest {
    @Autowired
    private FlatFileItemReader<Bond> bondReader;

    public StepExecution getStepExecution() {
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.getExecutionContext().putString("fileName",
                BondReaderTest.class.getClassLoader().getResource("input/TRIM Bond ALL - Stressed.csv").toExternalForm());
        return execution;
    }

    @Test
    public void givenFile_readsBond() throws Exception {
        bondReader.open(new ExecutionContext());
        Bond bond = bondReader.read();
        bondReader.close();

        assertThat(bond).isNotNull();
        assertThat(bond).hasFieldOrPropertyWithValue("isin", "NO0010821598");
        assertThat(bond.getScenarioDate()).isNull();
        assertThat(bond.getZeroSimulation()).isTrue();
    }

}
