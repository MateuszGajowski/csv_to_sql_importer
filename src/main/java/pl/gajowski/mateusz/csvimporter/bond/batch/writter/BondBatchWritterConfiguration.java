package pl.gajowski.mateusz.csvimporter.bond.batch.writter;

import com.google.common.collect.ImmutableMap;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.gajowski.mateusz.csvimporter.common.batch.processor.TableNameResolver;
import pl.gajowski.mateusz.csvimporter.bond.batch.processor.BondTableNameResolver;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondTable;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.common.batch.writter.TableNameClassifier;

import javax.sql.DataSource;

@Configuration
public class BondBatchWritterConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    @StepScope
    @SuppressWarnings("unchecked")
    public ItemWriter<BondEntity> bondItemWriter(@Value("#{stepExecutionContext['fileName']}") String sourceFile) {
        final BackToBackPatternClassifier classifier = new BackToBackPatternClassifier();
        classifier.setRouterDelegate(new TableNameClassifier());

        classifier.setMatcherMap(
                ImmutableMap.<String, ItemWriter<? extends BondEntity>>builder()
                        .put(BondTable.JTD_BOND_ALL.getTableName(), allItemWriter())
                        .put(BondTable.JTD_BOND_FX.getTableName(), fxItemWriter())
                        .put(BondTable.JTD_BOND_IR.getTableName(), irItemWriter())
                        .build()
        );

        final ClassifierCompositeItemWriter<BondEntity> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(classifier);

        return writer;
    }

    @Bean
    @Qualifier("bondAllItemWriter")
    public ItemWriter<BondEntity> allItemWriter() {
        return createItemWriter(BondTable.JTD_BOND_ALL);
    }

    @Bean
    @Qualifier("bondFxItemWriter")
    public ItemWriter<BondEntity> fxItemWriter() {
        return createItemWriter(BondTable.JTD_BOND_FX);
    }

    @Bean
    @Qualifier("bondIrItemWriter")
    public ItemWriter<BondEntity> irItemWriter() {
        return createItemWriter(BondTable.JTD_BOND_IR);
    }

    public ItemWriter<BondEntity> createItemWriter(BondTable bondTable) {
        return new JdbcBatchItemWriterBuilder<BondEntity>()
                .sql("INSERT INTO " + bondTable.getTableName()
                        + " (SCENARIO_DATE, MV_UNIT, ISIN, COUNTRY_CODE) VALUES (:scenarioDate, :mvUnitNotional, :isin, :countryCode)")
                .beanMapped()
                .dataSource(dataSource)
                .build();
    }
}
