package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;

public interface NamedSubcolumn<N, V> {

    N getName();

    Serializer<V> getValueSerializer();
}
