package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;


/**
 * Subcolumn insertions/mutations/deletions using our Schema classes.
 * <p>
 * To use, create via a {@link Mutator}, queue any number of mutations as below,
 * then call {@link Mutator#execute()}.
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
 * @author Nick Clarke
 */
public interface SubcolumnMutator<K, T extends Timestamp, SN, N> {
}
