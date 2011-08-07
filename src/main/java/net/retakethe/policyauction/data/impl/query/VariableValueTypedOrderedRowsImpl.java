package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import net.retakethe.policyauction.util.Functional;

public class VariableValueTypedOrderedRowsImpl<K, N>
        extends VariableValueTypedRowsImpl<K, N> implements VariableValueTypedOrderedRows<K, N> {

    private final OrderedRows<K, N, Object> wrappedRows;

    public VariableValueTypedOrderedRowsImpl(OrderedRows<K, N, Object> wrappedRows) {
        super(wrappedRows);
        this.wrappedRows = wrappedRows;
    }

    @Override
    public List<VariableValueTypedRow<K, N>> getList() {
        List<Row<K, N, Object>> wrappedList = wrappedRows.getList();
        return Functional.map(wrappedList, new Functional.Converter<Row<K, N, Object>, VariableValueTypedRow<K, N>>() {
            @Override
            public VariableValueTypedRow<K, N> convert(Row<K, N, Object> wrappedRow) {
                return wrapRow(wrappedRow);
            }
        });
    }

    @Override
    public VariableValueTypedRow<K, N> peekLast() {
        Row<K, N, Object> wrappedRow = wrappedRows.peekLast();
        return wrapRow(wrappedRow);
    }
}
