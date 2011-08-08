package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.SuperSlice
 */
public interface VariableValueTypedSuperSlice<SN, N> {

    List<VariableValueTypedSupercolumn<SN, N>> getSuperColumns();

    VariableValueTypedSupercolumn<SN, N> getSupercolumn(NamedSupercolumn<?, SN, N> supercolumn);

    VariableValueTypedSupercolumn<SN, N> getSupercolumn(SupercolumnRange<?, SN, N> supercolumn, SN supercolumnName);
}
