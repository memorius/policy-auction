package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import net.retakethe.policyauction.data.impl.query.api.OrderedRows;
import net.retakethe.policyauction.data.impl.query.api.Row;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

public class OrderedRowsImpl<K, T extends Timestamp, N>
        extends RowsImpl<K, T, N> implements OrderedRows<K, T, N> {

    private final me.prettyprint.hector.api.beans.OrderedRows<K, N, Object> wrappedRows;

    public OrderedRowsImpl(me.prettyprint.hector.api.beans.OrderedRows<K, N, Object> wrappedRows) {
        super(wrappedRows);
        this.wrappedRows = wrappedRows;
    }

    @Override
    public List<Row<K, T, N>> getList() {
        List<me.prettyprint.hector.api.beans.Row<K, N, Object>> wrappedList = wrappedRows.getList();
        return Functional.map(wrappedList,
                new Functional.Converter<me.prettyprint.hector.api.beans.Row<K, N, Object>, Row<K, T, N>>() {
                    @Override
                    public Row<K, T, N> convert(me.prettyprint.hector.api.beans.Row<K, N, Object> wrappedRow) {
                        return wrapRow(wrappedRow);
                    }
                });
    }

    @Override
    public Row<K, T, N> peekLast() {
        me.prettyprint.hector.api.beans.Row<K, N, Object> wrappedRow = wrappedRows.peekLast();
        return wrapRow(wrappedRow);
    }
}
