package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;

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
}
