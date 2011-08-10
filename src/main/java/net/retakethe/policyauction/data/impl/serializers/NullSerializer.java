package net.retakethe.policyauction.data.impl.serializers;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;

/**
 * Used for null values, e.g. columns where only the name is meaningful.
 * Values are ignored when storing, an empty byte array is stored instead. Queries always return null values.
 *
 * @author Nick Clarke
 */
public class NullSerializer extends AbstractSerializer<Object> {

    private static final NullSerializer instance = new NullSerializer();
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static NullSerializer get() {
        return instance;
    }

    /**
     * @see #get()
     */
    private NullSerializer() {}

    @Override
    public ByteBuffer toByteBuffer(Object obj) {
        return ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
    }

    @Override
    public Object fromByteBuffer(ByteBuffer byteBuffer) {
        return null;
    }
}
