package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.SuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedSuperRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRow;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

public class VariableValueTypedOrderedSuperRowsImpl<K, T extends Timestamp, SN, N>
        extends VariableValueTypedSuperRowsImpl<K, T, SN, N>
        implements VariableValueTypedOrderedSuperRows<K, T, SN, N> {

    private final OrderedSuperRows<K, SN, N, Object> wrappedSuperRows;

    public VariableValueTypedOrderedSuperRowsImpl(OrderedSuperRows<K, SN, N, Object> wrappedSuperRows) {
        super(wrappedSuperRows);
        this.wrappedSuperRows = wrappedSuperRows;
    }

    @Override
    public List<VariableValueTypedSuperRow<K, T, SN, N>> getList() {
        return Functional.map(wrappedSuperRows.getList(),
                new Functional.Converter<SuperRow<K, SN, N, Object>, VariableValueTypedSuperRow<K, T, SN, N>>() {
                    @Override
                    public VariableValueTypedSuperRow<K, T, SN, N> convert(SuperRow<K, SN, N, Object> wrappedRow) {
                        return wrapRow(wrappedRow);
                    }
                });
    }
}
