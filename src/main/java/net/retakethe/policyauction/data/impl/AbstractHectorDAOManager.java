package net.retakethe.policyauction.data.impl;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;

/**
 * @author Nick Clarke
 *
 */
public class AbstractHectorDAOManager {

    public static final class NoSuchColumnException extends Exception {
        private static final long serialVersionUID = 0L;

        public NoSuchColumnException(String columnName) {
            super("Column '" + columnName + "' not found");
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

    protected String getNonNullStringColumn(ColumnSlice<String, String> cs, String columnName) 
            throws NoSuchColumnException {
        String value = getStringColumnOrNull(cs, columnName);
        if (value == null) {
            throw new NoSuchColumnException(columnName);
        }
        return value;
    }

}
