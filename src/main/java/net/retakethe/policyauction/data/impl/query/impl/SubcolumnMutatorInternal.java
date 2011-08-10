package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;

public interface SubcolumnMutatorInternal<K, SN, N> extends SubcolumnMutator<K, SN, N> {

    <V> void addSubcolumnInsertion(SubcolumnRange<K, SN, N, V> subcolumn, N subcolumnName, V value);

    <V> void addSubcolumnInsertion(NamedSubcolumn<K, SN, N, V> subcolumn, V value);

    void addSubcolumnDeletion(SubcolumnRange<K, SN, N, ?> subcolumn, N subcolumnName);

    void addSubcolumnDeletion(NamedSubcolumn<K, SN, N, ?> subcolumn);
}
