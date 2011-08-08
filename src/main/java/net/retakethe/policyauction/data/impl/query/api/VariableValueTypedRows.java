package net.retakethe.policyauction.data.impl.query.api;

/**
 * @see me.prettyprint.hector.api.beans.Rows
 * @author Nick Clarke
 */
public interface VariableValueTypedRows<K, N> extends Iterable<VariableValueTypedRow<K, N>> {

    VariableValueTypedRow<K, N> getByKey(K key);

    int getCount();
}
