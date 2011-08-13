package net.retakethe.policyauction.data.impl.schema.supercolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Base class for Cassandra named supercolumns and supercolumn ranges.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class Supercolumn<K, T extends Timestamp, SN, N> {

    private final SupercolumnFamily<K, T, SN, N> supercolumnFamily;

    protected Supercolumn(SupercolumnFamily<K, T, SN, N> supercolumnFamily) {
        this.supercolumnFamily = supercolumnFamily;
    }

    public SupercolumnFamily<K, T, SN, N> getSupercolumnFamily() {
        return supercolumnFamily;
    }
}
