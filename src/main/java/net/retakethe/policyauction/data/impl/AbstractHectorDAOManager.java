package net.retakethe.policyauction.data.impl;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.types.HectorPolicyIDImpl;

/**
 * @author Nick Clarke
 *
 */
public class AbstractHectorDAOManager {

    protected static final Object DUMMY_VALUE = new Object();

    public static final class NoSuchColumnException extends Exception {
        private static final long serialVersionUID = 0L;

        public NoSuchColumnException(String columnName) {
            super("NamedColumn '" + columnName + "' not found");
        }
    }

    protected String getStringColumnOrNull(ColumnSlice<String, String> cs, String columnName) {
        if (cs == null) {
            return null;
        }
        HColumn<String, String> col = cs.getColumnByName(columnName);
        if (col == null) {
            return null;
        }
        String value = col.getValue();
        if (value == null) {
            return null;
        }
        return value;
    }

    protected <N, V> V getColumnOrNull(VariableValueTypedColumnSlice<N> cs, NamedColumn<?, N, V> column) {
        VariableValueTypedColumn<N, V> col = cs.getColumn(column);
        if (col == null) {
            return null;
        }
        return col.getValue();
    }

    protected <N, V> V getNonNullColumn(VariableValueTypedColumnSlice<N> cs, NamedColumn<?, N, V> column)
            throws NoSuchColumnException {
        VariableValueTypedColumn<N, V> col = cs.getColumn(column);
        if (col == null) {
            throw new NoSuchColumnException(column.getName().toString());
        }
        V value = col.getValue();
        if (value == null) {
            throw new NoSuchColumnException(column.getName().toString());
        }
        return value;
    }

    protected String getNonNullStringColumn(ColumnSlice<String, String> cs, String columnName) 
            throws NoSuchColumnException {
        String value = getStringColumnOrNull(cs, columnName);
        if (value == null) {
            throw new NoSuchColumnException(columnName);
        }
        return value;
    }

    protected <A, B extends A> B getImpl(A interfaceInstance, Class<B> implClass) {
        try {
            return implClass.cast(interfaceInstance);
        } catch (ClassCastException e) {
            throw new RuntimeException("The supplied " + interfaceInstance.getClass().getName()
                    + " is not an instance of the expected implementation class " + implClass.getName());
        }
    }

    protected HectorPolicyIDImpl getPolicyIDImpl(PolicyID policyID) {
        return getImpl(policyID, HectorPolicyIDImpl.class);
    }
}
