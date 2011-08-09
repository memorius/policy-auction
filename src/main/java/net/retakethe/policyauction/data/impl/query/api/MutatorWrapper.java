package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.mutation.MutationResult;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * Column/supercolumn/subcolumn insertions/mutations/deletions using our Schema classes.
 *
 * @see me.prettyprint.hector.api.mutation.Mutator
 * @author Nick Clarke
 */
public interface MutatorWrapper<K> {

    <N, V> void addColumnDeletion(K key, NamedColumn<K, N, V> column);

    <N, V> void addColumnDeletion(K key, ColumnRange<K, N, V> columnRange, N name);

    <N, V> void addColumnInsertion(K key, NamedColumn<K, N, V> column, V value);

    <N, V> void addColumnInsertion(K key, ColumnRange<K, N, V> column, N name, V value);

    <SN, N> SupercolumnInserter<K, SN, N> createSupercolumnInserter(K key, SupercolumnRange<K, SN, N> supercolumn,
            SN supercolumnName);

    <SN, N> SupercolumnInserter<K, SN, N> createSupercolumnInserter(K key, NamedSupercolumn<K, SN, N> supercolumn);

    <SN> void addSupercolumnDeletion(K key, SupercolumnRange<K, SN, ?> supercolumn, SN supercolumnName);

    <SN> void addSupercolumnDeletion(K key, NamedSupercolumn<K, SN, ?> supercolumn);

    <SN, N> void addSubcolumnDeletion(K key, SN supercolumnName, NamedSubcolumn<K, SN, N, ?> subcolumn);

    <SN, N> void addSubcolumnDeletion(K key, SN supercolumnName, SubcolumnRange<K, SN, N, ?> subcolumn, N subcolumnName); 

    void addRowDeletion(BaseColumnFamily<K> cf, K key);

    /**
     * Batch executes all mutations scheduled to this Mutator instance by addInsertion, addDeletion etc.
     * May throw a HectorException which is a RuntimeException.
     * @return A MutationResult holds the status.
     */
    MutationResult execute();
}
