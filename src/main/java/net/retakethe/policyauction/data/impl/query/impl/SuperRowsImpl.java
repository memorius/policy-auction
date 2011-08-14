package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Iterator;

import net.retakethe.policyauction.data.impl.query.api.SuperRow;
import net.retakethe.policyauction.data.impl.query.api.SuperRows;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class SuperRowsImpl<K, T extends Timestamp, SN, N>
        implements SuperRows<K, T, SN, N> {

    private final class WrappingIterator implements Iterator<SuperRow<K, T, SN, N>> {
        private final Iterator<me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object>> wrappedIterator;

        public WrappingIterator(Iterator<me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object>> wrappedIterator) {
            this.wrappedIterator = wrappedIterator;
        }

        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        @Override
        public SuperRow<K, T, SN, N> next() {
            return wrapRow(wrappedIterator.next());
        }

        @Override
        public void remove() {
            wrappedIterator.remove();
        }
    }

    private final me.prettyprint.hector.api.beans.SuperRows<K, SN, N, Object> wrappedRows;

    public SuperRowsImpl(me.prettyprint.hector.api.beans.SuperRows<K, SN, N, Object> wrappedRows) {
        this.wrappedRows = wrappedRows;
    }

    @Override
    public Iterator<SuperRow<K, T, SN, N>> iterator() {
        return new WrappingIterator(wrappedRows.iterator());
    }

    @Override
    public SuperRow<K, T, SN, N> getByKey(K key) {
        return wrapRow(wrappedRows.getByKey(key));
    }

    @Override
    public int getCount() {
        return wrappedRows.getCount();
    }

    protected SuperRow<K, T, SN, N> wrapRow(me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object> wrappedRow) {
        if (wrappedRow == null) {
            return null;
        }
        return new SuperRowImpl<K, T, SN, N>(wrappedRow);
    }
}
