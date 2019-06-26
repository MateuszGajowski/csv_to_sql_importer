package pl.gajowski.mateusz.csvimporter.bond.model.entity;

import lombok.Data;
import pl.gajowski.mateusz.csvimporter.common.entity.TableNameAwareEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

//narazie bez adnotacji
@Data
public class BondEntity implements TableNameAwareEntity {
    private LocalDate scenarioDate;
    private BigDecimal mvUnit;
    private String isin;
    private String countryCode;

    private String tableName;

    @Override
    public String getTableName() {
        return tableName;
    }
}
