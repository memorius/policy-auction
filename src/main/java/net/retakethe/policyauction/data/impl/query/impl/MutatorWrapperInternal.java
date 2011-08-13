package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;

/**
 * Internal interface used by column / query classes.
 *
 * @author Nick Clarke
 */
public interface MutatorWrapperInternal<K, T extends Timestamp> extends MutatorWrapper<K, T> {

    <N, V> void addColumnDeletion(K key, Column<K, T, N, V> column, N name, T timestamp);

    <N, V> void addColumnInsertion(K key, Column<K, T, N, V> column, N name, Value<T, V> value);

    <SN, N> SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(K key, Supercolumn<K, T, SN, N> supercolumn,
            SN supercolumnName);

    <SN> void addSupercolumnDeletion(K key, Supercolumn<K, T, SN, ?> supercolumn, SN supercolumnName);

    void addRowDeletion(BaseColumnFamily<K, T> cf, K key, T timestamp);
}
