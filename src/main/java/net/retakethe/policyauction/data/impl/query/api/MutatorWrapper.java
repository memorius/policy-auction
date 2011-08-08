package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.mutation.MutationResult;
import net.retakethe.policyauction.data.impl.schema.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.Column;
import net.retakethe.policyauction.data.impl.schema.Subcolumn;
import net.retakethe.policyauction.data.impl.schema.Supercolumn;

/**
 * Column/supercolumn/subcolumn insertions/mutations/deletions using our Schema classes.
 *
 * @see me.prettyprint.hector.api.mutation.Mutator
 * @author Nick Clarke
 */
public interface MutatorWrapper<K> {

    <N, V> void addColumnInsertion(K key, Column<K, N, V> column, N name, V value);

    <SN, N> SupercolumnInserter<K, SN, N> createSupercolumnInserter(K key, Supercolumn<K, SN, N> supercolumn,
            SN supercolumnName);

    <SN> void addSupercolumnDeletion(K key, Supercolumn<K, SN, ?> supercolumn, SN supercolumnName);

    <SN, N> void addSubcolumnDeletion(K key, Subcolumn<K, SN, N, ?> subcolumn, SN supercolumnName, N subcolumnName); 

    void addRowDeletion(BaseColumnFamily<K> cf, K key);

    /**
     * Batch executes all mutations scheduled to this Mutator instance by addInsertion, addDeletion etc.
     * May throw a HectorException which is a RuntimeException.
     * @return A MutationResult holds the status.
     */
    MutationResult execute();
}
