package net.retakethe.policyauction.data.impl.serializers;

import java.nio.ByteBuffer;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

/**
 * @author Nick Clarke
 */
public abstract class AbstractUUIDSerializer<T> extends AbstractSerializer<T> {

    protected AbstractUUIDSerializer() {}

    protected abstract UUID toUUID(T obj);

    protected abstract T fromUUID(UUID obj);

    @Override
    public ByteBuffer toByteBuffer(T obj) {
        if (obj == null) {
            return null;
        }
        return UUIDSerializer.get().toByteBuffer(toUUID(obj));
    }

    @Override
    public T fromByteBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        return fromUUID(UUIDSerializer.get().fromByteBuffer(byteBuffer));
    }
}
