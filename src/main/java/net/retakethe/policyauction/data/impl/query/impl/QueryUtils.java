package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
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
    protected static <K, T extends Timestamp, N> N[] getColumnNamesUnresolved(final ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<NamedColumn<K, T, N, ?>, N>() {
                @Override
                public N convert(NamedColumn<K, T, N, ?> column) {
                    if (column.getColumnFamily() != cf) {
                        throw new IllegalArgumentException("NamedColumn '" + column.getName() + "' is from column family '"
                                + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
                    }
                    return column.getName();
                }
            });

        return toArray(columnNames);
    }

    protected static <K, T extends Timestamp, N> void checkColumnBelongsToColumnFamily(ColumnFamily<K, T, N> cf,
            Column<K, T, N, ?> column) {
        if (column.getColumnFamily() != cf) {
            throw new IllegalArgumentException("Column is from column family '"
                    + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
        }
    }

    /**
     * Get names for a list of supercolumns and validate that they belong to the same supercolumn family.
     *
     * @param scf the SupercolumnFamily owning the supercolumns
     * @param supercolumns supercolumns to retrieve, can be empty,
     *      must be supercolumns belonging to the specified SupercolumnFamily.
     * @throws IllegalArgumentException if any supercolumns don't belong to this {@link SupercolumnFamily}.
     */
    protected static <K, T extends Timestamp, SN, N> SN[] getSupercolumnNamesUnresolved(
            final SupercolumnFamily<K, T, SN, N> scf,
            List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        List<SN> supercolumnNames = Functional.map(supercolumns,
                new Functional.Converter<NamedSupercolumn<K, T, SN, N>, SN>() {
            @Override
            public SN convert(NamedSupercolumn<K, T, SN, N> supercolumn) {
                checkSupercolumnBelongsToFamily(scf, supercolumn);
                return supercolumn.getName();
            }

        });

        return toArray(supercolumnNames);
    }

    protected static <K, T extends Timestamp, SN, N> void checkSupercolumnBelongsToFamily(
            SupercolumnFamily<K, T, SN, N> scf,
            Supercolumn<K, T, SN, N> supercolumn) {
        if (supercolumn.getSupercolumnFamily() != scf) {
            throw new IllegalArgumentException("Supercolumn is from supercolumn family '"
                    + supercolumn.getSupercolumnFamily().getName()
                    + "', expected supercolumn family '" + scf.getName() + "'");
        }
    }

    private static <N> N[] toArray(List<N> columnNames) {
        @SuppressWarnings("unchecked") // Generic array creation
        N[] columnNamesArray = columnNames.toArray((N[]) new Object[columnNames.size()]);

        return columnNamesArray;
    }
}
