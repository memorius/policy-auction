package net.retakethe.policyauction.data.impl.query;

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
        return valueSerializer.fromByteBuffer(getValueBytes());
    }

    @Override
    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }
}
