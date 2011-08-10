package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.mutation.MutationResult;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * Column/supercolumn/subcolumn insertions/mutations/deletions using our Schema classes.
 * <p>
 * To use, queue any number of mutations as below, then call {@link #execute()}.
 * <p>
 * To obtain a mutator: {@link BaseColumnFamily#createMutator(KeyspaceManager)}.
 * <p>
 * Row deletion:
 * {@link BaseColumnFamily#addRowDeletion(MutatorWrapper, Object)}.
 * <p>
 * Column deletion:
 * {@link NamedColumn#addColumnDeletion(MutatorWrapper, Object)},
 * {@link ColumnRange#addColumnDeletion(MutatorWrapper, Object, Object)}.
 * {@link RangeColumnFamily#addColumnDeletion(MutatorWrapper, Object, Object)}.
 * {@link SingleRowRangeColumnFamily#addColumnDeletion(MutatorWrapper, Object)}.
 * <p>
 * Column insertion:
 * {@link NamedColumn#addColumnInsertion(MutatorWrapper, Object, Object)},
 * {@link ColumnRange#addColumnInsertion(MutatorWrapper, Object, Object, Object)}.
 * {@link RangeColumnFamily#addColumnInsertion(MutatorWrapper, Object, Object, Object)}.
 * {@link SingleRowRangeColumnFamily#addColumnInsertion(MutatorWrapper, Object, Object)}.
 * <p>
 * Supercolumn deletion:
 * {@link NamedSupercolumn#addSupercolumnDeletion(MutatorWrapper, Object)}
 * {@link SupercolumnRange#addSupercolumnDeletion(MutatorWrapper, Object, Object)}.
 * {@link RangeSupercolumnFamily#addSupercolumnDeletion(MutatorWrapper, Object, Object)}.
 * {@link SingleRowRangeSupercolumnFamily#addSupercolumnDeletion(MutatorWrapper, Object)}.
 * <p>
 * To create a subcolumn mutator:
 * {@link SupercolumnRange#createSubcolumnMutator(MutatorWrapper, Object, Object)}
 * {@link NamedSupercolumn#createSubcolumnMutator(MutatorWrapper, Object)}
 * {@link RangeSupercolumnFamily#createSubcolumnMutator(MutatorWrapper, Object, Object)}
 * {@link SingleRowRangeSupercolumnFamily#createSubcolumnMutator(MutatorWrapper, Object)}
 * <p>
 * Subcolumn insertion: using the subcolumn mutator created above:
 * {@link NamedSubcolumn#addSubcolumnInsertion(SubcolumnMutator, Object)}
 * {@link SubcolumnRange#addSubcolumnInsertion(SubcolumnMutator, Object, Object)}
 * <p>
 * Subcolumn deletion: using the subcolumn mutator created above:
 * {@link NamedSubcolumn#addSubcolumnDeletion(SubcolumnMutator)}
 * {@link SubcolumnRange#addSubcolumnDeletion(SubcolumnMutator, Object)}
 *
 * @see me.prettyprint.hector.api.mutation.Mutator
 * @author Nick Clarke
 */
public interface MutatorWrapper<K> {

    /**
     * Batch executes all mutations scheduled to this Mutator instance by addInsertion, addDeletion etc.
     * May throw a HectorException which is a RuntimeException.
     * @return A MutationResult holds the status.
     */
    MutationResult execute();
}
