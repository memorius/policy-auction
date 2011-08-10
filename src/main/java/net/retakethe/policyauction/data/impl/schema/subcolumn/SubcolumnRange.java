package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;

public interface SubcolumnRange<K, SN, N, V> {

    Supercolumn<K, SN, N> getSupercolumn();

    Serializer<V> getValueSerializer();

    void addSubcolumnInsertion(SubcolumnMutator<K, SN, N> m, N subcolumnName, V value);

    void addSubcolumnDeletion(SubcolumnMutator<K, SN, N> m, N subcolumnName);
}
