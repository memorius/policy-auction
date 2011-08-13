package net.retakethe.policyauction.data.impl.serializers;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.ddl.ComparatorType;

/**
 * @author Nick Clarke
 */
public abstract class AbstractStringSerializer<T> extends AbstractSerializer<T> {

    protected AbstractStringSerializer() {}

    protected abstract String toString(T obj);

    protected abstract T fromString(String obj);

    @Override
    public final ByteBuffer toByteBuffer(T obj) {
        if (obj == null) {
            return null;
        }
        return StringSerializer.get().toByteBuffer(toString(obj));
    }

    @Override
    public final T fromByteBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        return fromString(StringSerializer.get().fromByteBuffer(byteBuffer));
    }

    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.UTF8TYPE;
    }
}
