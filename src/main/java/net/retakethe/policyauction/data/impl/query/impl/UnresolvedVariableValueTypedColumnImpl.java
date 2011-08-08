package net.retakethe.policyauction.data.impl.query.impl;

import java.nio.ByteBuffer;

import net.retakethe.policyauction.data.impl.query.api.UnresolvedVariableValueTypedColumn;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;

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
        return valueSerializer.fromByteBuffer(getValueBytes());
    }

    @Override
    public ByteBuffer getValueBytes() {
        return wrappedColumn.getValueBytes();
    }

    @Override
    public ByteBuffer getNameBytes() {
        return wrappedColumn.getNameBytes();
    }

    @Override
    public long getClock() {
        return wrappedColumn.getClock();
    }

    @Override
    public int getTtl() {
        return wrappedColumn.getTtl();
    }

    @Override
    public Serializer<N> getNameSerializer() {
        return wrappedColumn.getNameSerializer();
    }
}
