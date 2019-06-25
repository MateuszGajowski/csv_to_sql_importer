package pl.gajowski.mateusz.csvimporter.bond.batch.processor;

import pl.gajowski.mateusz.csvimporter.bond.model.entity.BondTable;
import pl.gajowski.mateusz.csvimporter.common.batch.processor.TableNameResolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BondTableNameResolver implements TableNameResolver {

    private static Pattern PATTERN = Pattern.compile("TRIM Bond ([A-Z]+) - Stressed\\.csv");

    @Override
    public String resolve(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not find valid bondTable for file " + fileName);
        }

        final String type = matcher.group(1);
        final BondTable bondTable = BondTable.findByType(type);

        return bondTable.getTableName();
    }
}
