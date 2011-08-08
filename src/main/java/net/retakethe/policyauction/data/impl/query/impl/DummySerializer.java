package net.retakethe.policyauction.data.impl.query.impl;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;

/**
 * A serializer implementation for use where Hector requires us to provide one but it will never be used,
 * or we don't know the correct type to use.
 *
 * @param <T> the type to (not) be serialized
 * @author Nick Clarke
 */
public class DummySerializer<T> extends AbstractSerializer<T> {

    @SuppressWarnings("rawtypes")
    private static final DummySerializer instance = new DummySerializer();

    @SuppressWarnings("unchecked")
    public static <T> DummySerializer<T> get() {
      return instance;
    }

    /**
     * @see #get()
     */
    private DummySerializer() {}

    @Override
    public ByteBuffer toByteBuffer(T obj) {
        throw new UnsupportedOperationException("DummySerializer.toByteBuffer");
    }

    @Override
    public T fromByteBuffer(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("DummySerializer.fromByteBuffer");
    }
}
