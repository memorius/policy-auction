package net.retakethe.policyauction.data.impl.query.api;

import java.util.Collection;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSuperSliceQuery
 */
public interface VariableValueTypedMultigetSuperSliceQuery<K, T extends Timestamp, SN, N>
        extends Query<VariableValueTypedSuperRows<K, T, SN, N>> {

    VariableValueTypedMultigetSuperSliceQuery<K, T, SN, N> setKeys(K... keys);

    VariableValueTypedMultigetSuperSliceQuery<K, T, SN, N> setKeys(Collection<K> keys);
}
