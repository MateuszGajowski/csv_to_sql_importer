package pl.gajowski.mateusz.csvimporter.bond.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.common.batch.processor.TableNameResolver;

@Configuration
public class BondProcessorConfiguration {
    @Bean
    public ItemProcessor<Bond, BondEntity> bondItemProcessor() {
        return new BondItemProcessor(bondTableNameResolver());
    }

    @Bean
    public TableNameResolver bondTableNameResolver() {
        return new BondTableNameResolver();
    }
}
