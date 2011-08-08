package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedColumnImpl<N, V> extends UnresolvedVariableValueTypedColumnImpl<N>
        implements VariableValueTypedColumn<N, V> {

    private final Serializer<V> valueSerializer;

    public VariableValueTypedColumnImpl(HColumn<N, Object> wrappedColumn, Serializer<V> valueSerializer) {
        super(wrappedColumn);
        this.valueSerializer = valueSerializer;
    }

    @Override
    public V getValue() {
        return getValue(valueSerializer);
    }

    @Override
    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }
}