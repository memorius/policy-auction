package net.retakethe.policyauction.data.impl.query;

import java.nio.ByteBuffer;

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
