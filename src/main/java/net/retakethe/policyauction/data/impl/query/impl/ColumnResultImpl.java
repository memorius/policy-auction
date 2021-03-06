package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;
import net.retakethe.policyauction.data.impl.schema.value.ValueImpl;

/**
 * @author Nick Clarke
 */
public class ColumnResultImpl<T extends Timestamp, N, V>
        extends UnresolvedColumnResultImpl<N>
        implements ColumnResult<T, N, V> {

    private final Serializer<V> valueSerializer;
    private final BaseColumnFamily<?, T> columnFamily;

    public ColumnResultImpl(HColumn<N, Object> wrappedColumn, BaseColumnFamily<?, T> columnFamily,
            Serializer<V> valueSerializer) {
        super(wrappedColumn);
        this.columnFamily = columnFamily;
        this.valueSerializer = valueSerializer;
    }

    @Override
    public Value<T, V> getValue() {
        HColumn<N, Object> wrappedColumn = getWrappedColumn();
        V value = valueSerializer.fromByteBuffer(wrappedColumn.getValueBytes());
        long timestamp = wrappedColumn.getClock();
        return new ValueImpl<T, V>(value, columnFamily.createTimestampFromCassandraTimestamp(timestamp));
    }
}
