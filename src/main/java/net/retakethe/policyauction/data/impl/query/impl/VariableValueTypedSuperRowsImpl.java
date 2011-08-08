package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Iterator;

import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRows;

public class VariableValueTypedSuperRowsImpl<K, SN, N> implements VariableValueTypedSuperRows<K, SN, N> {

    private final class WrappingIterator implements Iterator<VariableValueTypedSuperRow<K, SN, N>> {
        private final Iterator<SuperRow<K, SN, N, Object>> wrappedIterator;

        public WrappingIterator(Iterator<SuperRow<K, SN, N, Object>> wrappedIterator) {
            this.wrappedIterator = wrappedIterator;
        }

        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        @Override
        public VariableValueTypedSuperRow<K, SN, N> next() {
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
    public Iterator<VariableValueTypedSuperRow<K, SN, N>> iterator() {
        return new WrappingIterator(wrappedRows.iterator());
    }

    @Override
    public VariableValueTypedSuperRow<K, SN, N> getByKey(K key) {
        return wrapRow(wrappedRows.getByKey(key));
    }

    @Override
    public int getCount() {
        return wrappedRows.getCount();
    }

    protected VariableValueTypedSuperRow<K, SN, N> wrapRow(SuperRow<K, SN, N, Object> wrappedRow) {
        if (wrappedRow == null) {
            return null;
        }
        return new VariableValueTypedSuperRowImpl<K, SN, N>(wrappedRow);
    }
}
