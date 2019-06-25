package pl.gajowski.mateusz.csvimporter.bond.model.entity;

import java.util.Arrays;

public enum BondTable {
    JTD_BOND_ALL("JTD_BOND", "ALL"),
    JTD_BOND_FX("JTD_BOND", "FX"),
    JTD_BOND_IR("JTD_BOND", "IR");

    private String prefix;
    private String type;

    BondTable(String prefix, String type) {
        this.prefix = prefix;
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getType() {
        return type;
    }

    public String getTableName() {
        return prefix + "_" + type;
    }

    public static BondTable findByType(String type) {
        return Arrays.stream(values())
                .filter(it -> type.equals(it.getType()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Could not find table with type " + type)
                );
    }
}
