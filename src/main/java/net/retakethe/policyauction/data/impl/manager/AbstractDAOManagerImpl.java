package net.retakethe.policyauction.data.impl.manager;

import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.types.PolicyIDImpl;

/**
 * @author Nick Clarke
 */
public abstract class AbstractDAOManagerImpl {

    protected static final Object DUMMY_VALUE = new Object();

    public static final class NoSuchColumnException extends Exception {
        private static final long serialVersionUID = 0L;

        public NoSuchColumnException(String columnName) {
            super("NamedColumn '" + columnName + "' not found");
        }
    }

    private final KeyspaceManager keyspaceManager;

    protected AbstractDAOManagerImpl(KeyspaceManager keyspaceManager) {
        if (keyspaceManager == null) {
            throw new IllegalArgumentException("keyspace must not be null");
        }
        this.keyspaceManager = keyspaceManager;
    }

    protected KeyspaceManager getKeyspaceManager() {
        return keyspaceManager;
    }

    protected <T extends Timestamp, N, V> V getColumnOrNull(ColumnSlice<T, N> cs,
            NamedColumn<?, T, N, V> column) {
        ColumnResult<T, N, V> col = cs.getColumn(column);
        if (col == null) {
            return null;
        }
        return col.getValue().getValue();
    }
    

    protected <T extends Timestamp, N, V> V getColumnOrDefault(ColumnSlice<T, N> cs,
            NamedColumn<?, T, N, V> column, V defaultValue) {
        ColumnResult<T, N, V> col = cs.getColumn(column);
        if (col == null) {
            return defaultValue;
        }
        V storedValue = col.getValue().getValue();
		return storedValue==null ? defaultValue: storedValue;
    }

    protected <T extends Timestamp, N, V> V getNonNullColumn(ColumnSlice<T, N> cs,
            NamedColumn<?, T, N, V> column)
            throws NoSuchColumnException {
        ColumnResult<T, N, V> col = cs.getColumn(column);
        if (col == null) {
            throw new NoSuchColumnException(column.getName().toString());
        }
        V value = col.getValue().getValue();
        if (value == null) {
            throw new NoSuchColumnException(column.getName().toString());
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

    protected PolicyIDImpl getPolicyIDImpl(PolicyID policyID) {
        return getImpl(policyID, PolicyIDImpl.class);
    }
}
