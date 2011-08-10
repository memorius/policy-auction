package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * Internal interface used by column / query classes.
 *
 * @author Nick Clarke
 */
public interface MutatorWrapperInternal<K> extends MutatorWrapper<K> {

    <N, V> void addColumnDeletion(K key, NamedColumn<K, N, V> column);

    <N, V> void addColumnDeletion(K key, ColumnRange<K, N, V> columnRange, N name);

    <N, V> void addColumnInsertion(K key, NamedColumn<K, N, V> column, V value);

    <N, V> void addColumnInsertion(K key, ColumnRange<K, N, V> column, N name, V value);

    <SN, N> SubcolumnMutator<K, SN, N> createSubcolumnMutator(K key, SupercolumnRange<K, SN, N> supercolumn,
            SN supercolumnName);

    <SN, N> SubcolumnMutator<K, SN, N> createSubcolumnMutator(K key, NamedSupercolumn<K, SN, N> supercolumn);

    <SN> void addSupercolumnDeletion(K key, SupercolumnRange<K, SN, ?> supercolumn, SN supercolumnName);

    <SN> void addSupercolumnDeletion(K key, NamedSupercolumn<K, SN, ?> supercolumn);

    void addRowDeletion(BaseColumnFamily<K> cf, K key);
}
