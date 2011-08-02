package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;


/**
 * Subcolumn insertions/mutations/deletions using our Schema classes.
 * <p>
 * To use, create via a {@link MutatorWrapper}, queue any number of mutations as below,
 * then call {@link MutatorWrapper#execute()}.
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
 * @author Nick Clarke
 */
public interface SubcolumnMutator<K, SN, N> {
}