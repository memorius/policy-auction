package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedVariableValueTypedColumn;

/**
 * @author Nick Clarke
 *
 */
public class UnresolvedVariableValueTypedColumnImpl<N> implements UnresolvedVariableValueTypedColumn<N> {

    private final HColumn<N, Object> wrappedColumn;

    public UnresolvedVariableValueTypedColumnImpl(HColumn<N, Object> wrappedColumn) {
        this.wrappedColumn = wrappedColumn;
    }

    protected HColumn<N, Object> getWrappedColumn() {
        return this.wrappedColumn;
    }

    @Override
    public N getName() {
        return wrappedColumn.getName();
    }
    
    @Override
    public <V> V getValue(Serializer<V> valueSerializer) {
        return valueSerializer.fromByteBuffer(wrappedColumn.getValueBytes());
    }

    @Override
    public long getClock() {
        return wrappedColumn.getClock();
    }
}
