package net.retakethe.policyauction.data.impl.query;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;

/**
 * A serializer implementation for use where Hector requires us to provide one but it will never be used,
 * or we don't know the correct type to use.
 *
 * @param <V> the type to (not) be serialized
 * @author Nick Clarke
 */
public class DummySerializer<V> extends AbstractSerializer<V> {

    @Override
    public ByteBuffer toByteBuffer(V obj) {
        throw new UnsupportedOperationException("DummySerializer.toByteBuffer");
    }

    @Override
    public V fromByteBuffer(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("DummySerializer.fromByteBuffer");
    }
}
