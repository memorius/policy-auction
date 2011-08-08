package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;

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
public abstract class NamedSupercolumnSubcolumn<K, SN, N, V> extends Subcolumn<K, SN, N, V>{

    private final NamedSupercolumn<K, SN, N> supercolumn;

    protected NamedSupercolumnSubcolumn(NamedSupercolumn<K, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
        this.supercolumn = supercolumn;
    }

    @Override
    public NamedSupercolumn<K, SN, N> getSupercolumn() {
        return supercolumn;
    }
}
