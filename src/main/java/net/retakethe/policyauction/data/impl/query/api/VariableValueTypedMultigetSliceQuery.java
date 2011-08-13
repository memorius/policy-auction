package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSliceQuery
 */
public interface VariableValueTypedMultigetSliceQuery<K, T extends Timestamp, N>
        extends Query<VariableValueTypedRows<K, T, N>> {

    VariableValueTypedMultigetSliceQuery<K, T, N> setKeys(K... keys);

    VariableValueTypedMultigetSliceQuery<K, T, N> setKeys(Iterable<K> keys);
}
