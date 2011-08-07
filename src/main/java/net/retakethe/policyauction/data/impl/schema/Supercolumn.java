package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

/**
 * Base class for Cassandra named supercolumns and supercolumn ranges.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class Supercolumn<K, SN, N> {

    private final SupercolumnFamily<K, SN, N> supercolumnFamily;

    protected Supercolumn(SupercolumnFamily<K, SN, N> supercolumnFamily) {
        this.supercolumnFamily = supercolumnFamily;
    }

    public SupercolumnFamily<K, SN, N> getSupercolumnFamily() {
        return supercolumnFamily;
    }
}
