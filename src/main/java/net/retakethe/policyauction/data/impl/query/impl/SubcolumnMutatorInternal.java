package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.subcolumn.Subcolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface SubcolumnMutatorInternal<K, T extends Timestamp, SN, N> extends SubcolumnMutator<K, T, SN, N> {

    <V> void addSubcolumnInsertion(Subcolumn<K, T, SN, N, V> subcolumn, N subcolumnName, V value);

    void addSubcolumnDeletion(Subcolumn<K, T, SN, N, ?> subcolumn, N subcolumnName);
}
