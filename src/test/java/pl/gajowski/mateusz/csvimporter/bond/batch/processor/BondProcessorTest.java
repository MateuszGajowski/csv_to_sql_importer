package pl.gajowski.mateusz.csvimporter.bond.batch.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import pl.gajowski.mateusz.csvimporter.bond.BaseBatchTestConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondTable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BaseBatchTestConfiguration.class,
        BondProcessorConfiguration.class})
@TestExecutionListeners(
        listeners = {StepScopeTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class BondProcessorTest {
    @Autowired
    private ItemProcessor<Bond, BondEntity> bondItemProcessor;

    @Test
    public void givenCorrectBond_returnsBondEntity() throws Exception {
        Bond bond = new Bond();
        bond.setZeroSimulation(false);
        bond.setIsin("PL1234");
        bond.setMvUnitNotional(new BigDecimal(0.1232));
        bond.setFileName("TRIM Bond ALL - Stressed.csv");
        bond.setScenarioDate(LocalDate.now());

        BondEntity entity = bondItemProcessor.process(bond);

        assertThat(entity).isNotNull();
        assertThat(entity.getCountryCode()).isEqualTo("PL");
        assertThat(entity.getIsin()).isEqualTo(bond.getIsin());
        assertThat(entity.getScenarioDate()).isEqualTo(bond.getScenarioDate());
        assertThat(entity.getMvUnit()).isEqualTo(bond.getMvUnitNotional());
        assertThat(entity.getTableName()).isEqualTo(BondTable.JTD_BOND_ALL.getTableName());
    }

    @Test
    public void givenSimulationBond_returnsNullEntity() throws Exception {
        Bond bond = new Bond();
        bond.setIsin("PL1234");
        bond.setFileName("TRIM Bond ALL - Stressed.csv");
        bond.setScenarioDate(LocalDate.now());
        bond.setZeroSimulation(true);

        BondEntity entity = bondItemProcessor.process(bond);

        assertThat(entity).isNull();
    }
}
