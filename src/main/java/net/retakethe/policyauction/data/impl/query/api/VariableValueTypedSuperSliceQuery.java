package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.SuperSliceQuery
 */
public interface VariableValueTypedSuperSliceQuery<K, T extends Timestamp, SN, N>
        extends Query<VariableValueTypedSuperSlice<T, SN, N>> {

}
