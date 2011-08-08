package net.retakethe.policyauction.data.impl.query.api;

/**
 * @see me.prettyprint.hector.api.beans.SuperRow
 * @author Nick Clarke
 */
public interface VariableValueTypedSuperRow<K, SN, N> {

    K getKey();

    VariableValueTypedSuperSlice<SN, N> getSuperSlice();
}
