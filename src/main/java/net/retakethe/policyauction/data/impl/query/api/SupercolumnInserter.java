package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.Subcolumn;

public interface SupercolumnInserter<K, SN, N> {
    <V> void addSubcolumnInsertion(Subcolumn<K, SN, N, V> subcolumn, N subcolumnName, V value);
}