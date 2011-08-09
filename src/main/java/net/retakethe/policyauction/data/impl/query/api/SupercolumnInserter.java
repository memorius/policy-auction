package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;

public interface SupercolumnInserter<K, SN, N> {

    <V> void addSubcolumnInsertion(SubcolumnRange<K, SN, N, V> subcolumn, N subcolumnName, V value);

    <V> void addSubcolumnInsertion(NamedSubcolumn<K, SN, N, V> subcolumn, V value);
}
