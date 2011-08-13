package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

public class VariableValueTypedOrderedRowsImpl<K, T extends Timestamp, N>
        extends VariableValueTypedRowsImpl<K, T, N> implements VariableValueTypedOrderedRows<K, T, N> {

    private final OrderedRows<K, N, Object> wrappedRows;

    public VariableValueTypedOrderedRowsImpl(OrderedRows<K, N, Object> wrappedRows) {
        super(wrappedRows);
        this.wrappedRows = wrappedRows;
    }

    @Override
    public List<VariableValueTypedRow<K, T, N>> getList() {
        List<Row<K, N, Object>> wrappedList = wrappedRows.getList();
        return Functional.map(wrappedList, new Functional.Converter<Row<K, N, Object>, VariableValueTypedRow<K, T, N>>() {
            @Override
            public VariableValueTypedRow<K, T, N> convert(Row<K, N, Object> wrappedRow) {
                return wrapRow(wrappedRow);
            }
        });
    }

    @Override
    public VariableValueTypedRow<K, T, N> peekLast() {
        Row<K, N, Object> wrappedRow = wrappedRows.peekLast();
        return wrapRow(wrappedRow);
    }
}
