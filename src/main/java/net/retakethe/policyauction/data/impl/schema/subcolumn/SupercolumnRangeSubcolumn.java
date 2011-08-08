package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

import me.prettyprint.hector.api.Serializer;

/**
 * Base class for Cassandra named subcolumns and subcolumn ranges of supercolumn ranges.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class SupercolumnRangeSubcolumn<K, SN, N, V> extends Subcolumn<K, SN, N, V> {

    private final SupercolumnRange<K, SN, N> supercolumn;

    protected SupercolumnRangeSubcolumn(SupercolumnRange<K, SN, N> supercolumn,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(supercolumn, valueType, valueSerializer);
        this.supercolumn = supercolumn;
    }

    @Override
    public SupercolumnRange<K, SN, N> getSupercolumn() {
        return supercolumn;
    }
}
