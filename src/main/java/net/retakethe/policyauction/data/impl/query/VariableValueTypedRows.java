package net.retakethe.policyauction.data.impl.query;



/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.Rows
 */
public interface VariableValueTypedRows<K, N> extends Iterable<VariableValueTypedRow<K, N>> {

    VariableValueTypedRow<K, N> getByKey(K key);

    int getCount();
}
