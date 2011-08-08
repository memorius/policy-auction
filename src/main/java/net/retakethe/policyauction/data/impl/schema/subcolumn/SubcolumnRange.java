package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;

public interface SubcolumnRange<K, SN, N, V> {

    Serializer<V> getValueSerializer();
}
