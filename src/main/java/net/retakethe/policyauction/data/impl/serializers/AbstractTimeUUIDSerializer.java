package net.retakethe.policyauction.data.impl.serializers;

import java.nio.ByteBuffer;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.hector.api.ddl.ComparatorType;

/**
 * @author Nick Clarke
 */
public abstract class AbstractTimeUUIDSerializer<T> extends AbstractSerializer<T> {

    protected AbstractTimeUUIDSerializer() {}

    protected abstract UUID toUUID(T obj);

    protected abstract T fromUUID(UUID obj);

    @Override
    public final ByteBuffer toByteBuffer(T obj) {
        if (obj == null) {
            return null;
        }
        return UUIDSerializer.get().toByteBuffer(toUUID(obj));
    }

    @Override
    public final T fromByteBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        return fromUUID(UUIDSerializer.get().fromByteBuffer(byteBuffer));
    }

    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.TIMEUUIDTYPE;
    }
}
