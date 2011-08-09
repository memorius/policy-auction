package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;

public interface SubcolumnRange<K, SN, N, V> {

    Supercolumn<K, SN, N> getSupercolumn();

    Serializer<V> getValueSerializer();
}
