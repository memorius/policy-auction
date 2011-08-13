package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Base class for Cassandra named subcolumns and subcolumn ranges of named supercolumns.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class NamedSuperSubcolumn<K, T extends Timestamp, SN, N, V> extends Subcolumn<K, T, SN, N, V>{

    private final NamedSupercolumn<K, T, SN, N> supercolumn;

    protected NamedSuperSubcolumn(NamedSupercolumn<K, T, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
        this.supercolumn = supercolumn;
    }

    @Override
    public NamedSupercolumn<K, T, SN, N> getSupercolumn() {
        return supercolumn;
    }
}
