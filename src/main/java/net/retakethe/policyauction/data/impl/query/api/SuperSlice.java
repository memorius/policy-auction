package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.SuperSlice
 */
public interface SuperSlice<T extends Timestamp, SN, N> {

    List<SupercolumnResult<T, SN, N>> getSuperColumns();

    SupercolumnResult<T, SN, N> getSupercolumn(NamedSupercolumn<?, T, SN, N> supercolumn);

    SupercolumnResult<T, SN, N> getSupercolumn(SupercolumnRange<?, T, SN, N> supercolumn, SN supercolumnName);
}
