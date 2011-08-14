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
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;

/**
 * Column/supercolumn/subcolumn insertions/mutations/deletions using our Schema classes.
 * <p>
 * To use, queue any number of mutations as below, then call {@link #execute()}.
 * <p>
 * To obtain a mutator: {@link BaseColumnFamily#createMutator(KeyspaceManager)}.
 * <p>
 * Row deletion:
 * {@link BaseColumnFamily#addRowDeletion(Mutator, Object)}.
 * {@link BaseColumnFamily#addRowDeletion(Mutator, Object, Timestamp)}.
 * <p>
 * Column deletion:
 * {@link NamedColumn#addColumnDeletion(Mutator, Object)},
 * {@link NamedColumn#addColumnDeletion(Mutator, Object, Timestamp)},
 * {@link ColumnRange#addColumnDeletion(Mutator, Object, Object)}.
 * {@link ColumnRange#addColumnDeletion(Mutator, Object, Object, Timestamp)}.
 * {@link RangeColumnFamily#addColumnDeletion(Mutator, Object, Object)}.
 * {@link RangeColumnFamily#addColumnDeletion(Mutator, Object, Object, Timestamp)}.
 * {@link SingleRowRangeColumnFamily#addColumnDeletion(Mutator, Object)}.
 * {@link SingleRowRangeColumnFamily#addColumnDeletion(Mutator, Object, Timestamp)}.
 * <p>
 * Column insertion:
 * {@link NamedColumn#addColumnInsertion(Mutator, Object, Value)},
 * {@link ColumnRange#addColumnInsertion(Mutator, Object, Object, Value)}.
 * {@link RangeColumnFamily#addColumnInsertion(Mutator, Object, Object, Value)}.
 * {@link SingleRowRangeColumnFamily#addColumnInsertion(Mutator, Object, Value)}.
 * <p>
 * Supercolumn deletion:
 * {@link NamedSupercolumn#addSupercolumnDeletion(Mutator, Object)}
 * {@link SupercolumnRange#addSupercolumnDeletion(Mutator, Object, Object)}.
 * {@link RangeSupercolumnFamily#addSupercolumnDeletion(Mutator, Object, Object)}.
 * {@link SingleRowRangeSupercolumnFamily#addSupercolumnDeletion(Mutator, Object)}.
 * <p>
 * To create a subcolumn mutator:
 * {@link SupercolumnRange#createSubcolumnMutator(Mutator, Object, Object)}
 * {@link NamedSupercolumn#createSubcolumnMutator(Mutator, Object)}
 * {@link RangeSupercolumnFamily#createSubcolumnMutator(Mutator, Object, Object)}
 * {@link SingleRowRangeSupercolumnFamily#createSubcolumnMutator(Mutator, Object)}
 * <p>
 * Subcolumn insertion: using the subcolumn mutator created above:
 * {@link NamedSubcolumn#addSubcolumnInsertion(SubcolumnMutator, Value)}
 * {@link SubcolumnRange#addSubcolumnInsertion(SubcolumnMutator, Object, Value)}
 * <p>
 * Subcolumn deletion: using the subcolumn mutator created above:
 * {@link NamedSubcolumn#addSubcolumnDeletion(SubcolumnMutator)}
 * {@link NamedSubcolumn#addSubcolumnDeletion(SubcolumnMutator, Timestamp)}
 * {@link SubcolumnRange#addSubcolumnDeletion(SubcolumnMutator, Object)}
 * {@link SubcolumnRange#addSubcolumnDeletion(SubcolumnMutator, Object, Timestamp)}
 *
 * @see me.prettyprint.hector.api.mutation.Mutator
 * @author Nick Clarke
 */
public interface Mutator<K, T extends Timestamp> {

    /**
     * Batch executes all mutations scheduled to this Mutator instance by addInsertion, addDeletion etc.
     * May throw a HectorException which is a RuntimeException.
     * @return A MutationResult holds the status.
     */
    MutationResult execute();
}
