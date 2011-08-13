package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface SubcolumnMutatorInternal<K, T extends Timestamp, SN, N> extends SubcolumnMutator<K, T, SN, N> {

    <V> void addSubcolumnInsertion(SubcolumnRange<K, T, SN, N, V> subcolumn, N subcolumnName, V value);

    <V> void addSubcolumnInsertion(NamedSubcolumn<K, T, SN, N, V> subcolumn, V value);

    void addSubcolumnDeletion(SubcolumnRange<K, T, SN, N, ?> subcolumn, N subcolumnName);

    void addSubcolumnDeletion(NamedSubcolumn<K, T, SN, N, ?> subcolumn);
}
