package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.util.Functional;

public final class QueryUtils {
    private QueryUtils() {}

    /**
     * Get names for a list of columns and validate that they belong to the same column family.
     *
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     * @throws IllegalArgumentException if any columns don't belong to this {@link ColumnFamily}.
     */
    protected static <K, N> N[] getColumnNamesUnresolved(final ColumnFamily<K, N> cf,
            List<NamedColumn<K, N, ?>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<NamedColumn<K, N, ?>, N>() {
                @Override
                public N convert(NamedColumn<K, N, ?> column) {
                    if (column.getColumnFamily() != cf) {
                        throw new IllegalArgumentException("NamedColumn '" + column.getName() + "' is from column family '"
                                + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
                    }
                    return column.getName();
                }
            });

        return toArray(columnNames);
    }

    private static <N> N[] toArray(List<N> columnNames) {
        @SuppressWarnings("unchecked") // Generic array creation
        N[] columnNamesArray = columnNames.toArray((N[]) new Object[columnNames.size()]);

        return columnNamesArray;
    }
}