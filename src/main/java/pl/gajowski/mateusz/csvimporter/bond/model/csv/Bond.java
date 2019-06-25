package pl.gajowski.mateusz.csvimporter.bond.model.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.gajowski.mateusz.csvimporter.common.batch.model.FileNameAwareItem;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bond implements FileNameAwareItem {
    private Boolean error;
    private String errorText;
    private String isin;
    private BigDecimal mvUnitNotional;
    private LocalDate scenarioDate;
    private String scenarioSpecId;
    private String tradehubMessageId;
    private String type;
    private Boolean zeroSimulation;
    private String fileName;

    public String getCountryCode() {
        return "EN";
    }
}
