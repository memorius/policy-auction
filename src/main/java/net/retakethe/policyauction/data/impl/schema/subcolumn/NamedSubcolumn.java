package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface NamedSubcolumn<K, T extends Timestamp, SN, N, V> {

    Supercolumn<K, T, SN, N> getSupercolumn();

    Serializer<V> getValueSerializer();

    void addSubcolumnInsertion(SubcolumnMutator<K, T, SN, N> m, V value);

    void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m);

    N getName();
}
