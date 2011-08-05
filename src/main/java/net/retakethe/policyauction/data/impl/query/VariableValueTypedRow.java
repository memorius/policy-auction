package net.retakethe.policyauction.data.impl.query;


/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.Row
 */
public interface VariableValueTypedRow<K, N> {

    K getKey();

    VariableValueTypedColumnSlice<N> getColumnSlice();
}
