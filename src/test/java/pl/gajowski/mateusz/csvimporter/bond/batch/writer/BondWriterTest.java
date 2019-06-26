package pl.gajowski.mateusz.csvimporter.bond.batch.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import pl.gajowski.mateusz.csvimporter.bond.BaseBatchTestConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.batch.writter.BondWriterConfiguration;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondTable;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BaseBatchTestConfiguration.class,
        BondWriterConfiguration.class})
@TestExecutionListeners(
        listeners = {StepScopeTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class BondWriterTest {
    @Autowired
    private ItemWriter<BondEntity> bondItemWriter;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void givenBondEntity_writesEntityToDb() {
        BondEntity bondEntity = new BondEntity();
        bondEntity.setTableName(BondTable.JTD_BOND_ALL.getTableName());
        bondEntity.setIsin("PL1234");
        bondEntity.setCountryCode("PL");
        bondEntity.setMvUnit(new BigDecimal(0.1232));

        assertThatCode(() -> bondItemWriter.write(Collections.singletonList(bondEntity)))
                .doesNotThrowAnyException();

        BondEntity savedEntity =
                jdbcTemplate.queryForObject("select * from JTD_BOND_ALL", new BeanPropertyRowMapper<>(BondEntity.class));

        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity).isEqualToIgnoringGivenFields(bondEntity, "tableName");
    }

}
