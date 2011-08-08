package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;

import me.prettyprint.hector.api.Serializer;

/**
 * Base class for Cassandra named subcolumns and subcolumn ranges.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class Subcolumn<K, SN, N, V> {

    private final Supercolumn<K, SN, N> supercolumn;
    private final Class<V> valueType;
    private final Serializer<V> valueSerializer;

    protected Subcolumn(Supercolumn<K, SN, N> supercolumn,
            Class<V> valueType, Serializer<V> valueSerializer) {
        this.supercolumn = supercolumn;
        this.valueType = valueType;
        this.valueSerializer = valueSerializer;
    }

    public Supercolumn<K, SN, N> getSupercolumn() {
        return supercolumn;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }
}
