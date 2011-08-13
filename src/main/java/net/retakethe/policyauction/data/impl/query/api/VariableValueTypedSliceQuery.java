package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.SliceQuery
 */
public interface VariableValueTypedSliceQuery<K, T extends Timestamp, N>
        extends Query<VariableValueTypedColumnSlice<T, N>> {

}
