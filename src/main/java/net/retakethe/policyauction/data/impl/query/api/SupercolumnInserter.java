package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSuperNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSuperSubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeSubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;


/**
 * Supercolumn/subcolumn insertions/mutations/deletions using our Schema classes.
 * <p>
 * To use, create via a {@link MutatorWrapper}, queue any number of mutations as below,
 * then call {@link MutatorWrapper#execute()}.
 * <p>
 * To create a supercolumn inserter:
 * {@link SupercolumnRange#createSupercolumnInserter(MutatorWrapper, Object, Object)}
 * {@link NamedSupercolumn#createSupercolumnInserter(MutatorWrapper, Object)}
 * {@link RangeSupercolumnFamily#createSupercolumnInserter(MutatorWrapper, Object, Object)}
 * {@link SingleRowRangeSupercolumnFamily#createSupercolumnInserter(MutatorWrapper, Object)}
 * <p>
 * Subcolumn insertion: using the inserter created above:
 * {@link NamedSuperNamedSubcolumn#addSubcolumnInsertion(SupercolumnInserter, Object)}
 * {@link NamedSuperSubcolumnRange#addSubcolumnInsertion(SupercolumnInserter, Object, Object)}
 * {@link SuperRangeNamedSubcolumn#addSubcolumnInsertion(SupercolumnInserter, Object)}
 * {@link SuperRangeSubcolumnRange#addSubcolumnInsertion(SupercolumnInserter, Object, Object)}
 *
 * @author Nick Clarke
 */
public interface SupercolumnInserter<K, SN, N> {
}
