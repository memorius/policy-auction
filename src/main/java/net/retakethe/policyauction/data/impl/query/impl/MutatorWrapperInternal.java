package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Internal interface used by column / query classes.
 *
 * @author Nick Clarke
 */
public interface MutatorWrapperInternal<K, T extends Timestamp> extends MutatorWrapper<K, T> {

    <N, V> void addColumnDeletion(K key, NamedColumn<K, T, N, V> column);

    <N, V> void addColumnDeletion(K key, ColumnRange<K, T, N, V> columnRange, N name);

    <N, V> void addColumnInsertion(K key, NamedColumn<K, T, N, V> column, V value);

    <N, V> void addColumnInsertion(K key, ColumnRange<K, T, N, V> column, N name, V value);

    <SN, N> SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(K key, SupercolumnRange<K, T, SN, N> supercolumn,
            SN supercolumnName);

    <SN, N> SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(K key, NamedSupercolumn<K, T, SN, N> supercolumn);

    <SN> void addSupercolumnDeletion(K key, SupercolumnRange<K, T, SN, ?> supercolumn, SN supercolumnName);

    <SN> void addSupercolumnDeletion(K key, NamedSupercolumn<K, T, SN, ?> supercolumn);

    void addRowDeletion(BaseColumnFamily<K, T> cf, K key);
}
