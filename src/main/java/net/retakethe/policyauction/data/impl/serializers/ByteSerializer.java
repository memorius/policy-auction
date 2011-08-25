package net.retakethe.policyauction.data.impl.serializers;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.hector.api.ddl.ComparatorType;

/**
 * @author Nick Clarke
 */
public class ByteSerializer extends AbstractSerializer<Byte> {

    private static final ByteSerializer INSTANCE = new ByteSerializer();

    public static ByteSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private ByteSerializer() {}

    @Override
    public ByteBuffer toByteBuffer(Byte obj) {
        if (obj == null) {
            return null;
        }
        ByteBuffer b = ByteBuffer.allocate(1);
        b.put(obj);
        b.rewind();
        return b;
    }

    @Override
    public Byte fromByteBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        byte in = byteBuffer.get();
        return in;
    }

    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.BYTESTYPE;
    }
}
