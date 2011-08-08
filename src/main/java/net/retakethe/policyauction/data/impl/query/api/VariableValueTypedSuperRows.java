package net.retakethe.policyauction.data.impl.query.api;

/**
 * @see me.prettyprint.hector.api.beans.SuperRows
 * @author Nick Clarke
 */
public interface VariableValueTypedSuperRows<K, SN, N> extends Iterable<VariableValueTypedSuperRow<K, SN, N>> {

    VariableValueTypedSuperRow<K, SN, N> getByKey(K key);

    int getCount();
}
