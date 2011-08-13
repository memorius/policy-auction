package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Iterator;

import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRows;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class VariableValueTypedSuperRowsImpl<K, T extends Timestamp, SN, N>
        implements VariableValueTypedSuperRows<K, T, SN, N> {

    private final class WrappingIterator implements Iterator<VariableValueTypedSuperRow<K, T, SN, N>> {
        private final Iterator<SuperRow<K, SN, N, Object>> wrappedIterator;

        public WrappingIterator(Iterator<SuperRow<K, SN, N, Object>> wrappedIterator) {
            this.wrappedIterator = wrappedIterator;
        }

        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        @Override
        public VariableValueTypedSuperRow<K, T, SN, N> next() {
            return wrapRow(wrappedIterator.next());
        }

        @Override
        public void remove() {
            wrappedIterator.remove();
        }
    }

    private final SuperRows<K, SN, N, Object> wrappedRows;

    public VariableValueTypedSuperRowsImpl(SuperRows<K, SN, N, Object> wrappedRows) {
        this.wrappedRows = wrappedRows;
    }

    @Override
    public Iterator<VariableValueTypedSuperRow<K, T, SN, N>> iterator() {
        return new WrappingIterator(wrappedRows.iterator());
    }

    @Override
    public VariableValueTypedSuperRow<K, T, SN, N> getByKey(K key) {
        return wrapRow(wrappedRows.getByKey(key));
    }

    @Override
    public int getCount() {
        return wrappedRows.getCount();
    }

    protected VariableValueTypedSuperRow<K, T, SN, N> wrapRow(SuperRow<K, SN, N, Object> wrappedRow) {
        if (wrappedRow == null) {
            return null;
        }
        return new VariableValueTypedSuperRowImpl<K, T, SN, N>(wrappedRow);
    }
}
