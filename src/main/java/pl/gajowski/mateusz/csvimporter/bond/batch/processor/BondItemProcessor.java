package pl.gajowski.mateusz.csvimporter.bond.batch.processor;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;
import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondEntity;
import pl.gajowski.mateusz.csvimporter.common.batch.processor.TableNameResolver;

@RequiredArgsConstructor
public class BondItemProcessor implements ItemProcessor<Bond, BondEntity> {

    final TableNameResolver tableNameResolver;

    @Override
    public BondEntity process(Bond item) {
        if (item.getZeroSimulation()) {
            return null;
        }

        final BondEntity entity = new BondEntity();
        entity.setScenarioDate(item.getScenarioDate());
        entity.setMvUnitNotional(item.getMvUnitNotional());
        entity.setIsin(item.getIsin());
        entity.setCountryCode(StringUtils.left(item.getIsin(), 2));
        entity.setTableName(tableNameResolver.resolve(item.getFileName()));

        return entity;
    }
}
