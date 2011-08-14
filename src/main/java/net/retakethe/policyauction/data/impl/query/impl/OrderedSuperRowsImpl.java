package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import net.retakethe.policyauction.data.impl.query.api.OrderedSuperRows;
import net.retakethe.policyauction.data.impl.query.api.SuperRow;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

public class OrderedSuperRowsImpl<K, T extends Timestamp, SN, N>
        extends SuperRowsImpl<K, T, SN, N>
        implements OrderedSuperRows<K, T, SN, N> {

    private final me.prettyprint.hector.api.beans.OrderedSuperRows<K, SN, N, Object> wrappedSuperRows;

    public OrderedSuperRowsImpl(
            me.prettyprint.hector.api.beans.OrderedSuperRows<K, SN, N, Object> wrappedSuperRows) {
        super(wrappedSuperRows);
        this.wrappedSuperRows = wrappedSuperRows;
    }

    @Override
    public List<SuperRow<K, T, SN, N>> getList() {
        return Functional.map(wrappedSuperRows.getList(),
                new Functional.Converter<me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object>,
                                         SuperRow<K, T, SN, N>>() {
                    @Override
                    public SuperRow<K, T, SN, N> convert(
                            me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object> wrappedRow) {
                        return wrapRow(wrappedRow);
                    }
                });
    }
}
