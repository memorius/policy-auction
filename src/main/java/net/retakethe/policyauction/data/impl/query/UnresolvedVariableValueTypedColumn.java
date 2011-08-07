package net.retakethe.policyauction.data.impl.query;

import java.nio.ByteBuffer;

import me.prettyprint.hector.api.Serializer;

/**
 * NamedColumn where the value type is not specified hence value is not accessible.
 *
 * @param <N> column name type
 * @author Nick Clarke
 */
public interface UnresolvedVariableValueTypedColumn<N> {

    N getName();

    <V> V getValue(Serializer<V> valueSerializer);

    /**
     * (Advanced) Returns the underlying ByteBuffer for the value via ByteBuffer.duplicate().
     */
    ByteBuffer getValueBytes();

    /**
     * (Advanced) Returns the underlying ByteBuffer for the name via ByteBuffer.duplicate().
     */
    ByteBuffer getNameBytes();

    long getClock();

    int getTtl();

    Serializer<N> getNameSerializer();
}
