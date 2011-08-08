package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.SliceQuery
 */
public interface VariableValueTypedSliceQuery<K, N> extends Query<VariableValueTypedColumnSlice<N>> {

}
