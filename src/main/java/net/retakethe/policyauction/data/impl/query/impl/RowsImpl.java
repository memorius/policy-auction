package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Iterator;

import net.retakethe.policyauction.data.impl.query.api.Row;
import net.retakethe.policyauction.data.impl.query.api.Rows;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class RowsImpl<K, T extends Timestamp, N> implements Rows<K, T, N> {

    private final class WrappingIterator implements Iterator<Row<K, T, N>> {
        private final Iterator<me.prettyprint.hector.api.beans.Row<K, N, Object>> wrappedIterator;

        public WrappingIterator(Iterator<me.prettyprint.hector.api.beans.Row<K, N, Object>> wrappedIterator) {
            this.wrappedIterator = wrappedIterator;
        }

        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        @Override
        public Row<K, T, N> next() {
            return wrapRow(wrappedIterator.next());
        }

        @Override
        public void remove() {
            wrappedIterator.remove();
        }
    }

    private final me.prettyprint.hector.api.beans.Rows<K, N, Object> wrappedRows;

    public RowsImpl(me.prettyprint.hector.api.beans.Rows<K, N, Object> wrappedRows) {
        this.wrappedRows = wrappedRows;
    }

    @Override
    public Iterator<Row<K, T, N>> iterator() {
        return new WrappingIterator(wrappedRows.iterator());
    }

    @Override
    public Row<K, T, N> getByKey(K key) {
        return wrapRow(wrappedRows.getByKey(key));
    }

    @Override
    public int getCount() {
        return wrappedRows.getCount();
    }

    protected Row<K, T, N> wrapRow(me.prettyprint.hector.api.beans.Row<K, N, Object> wrappedRow) {
        if (wrappedRow == null) {
            return null;
        }
        return new RowImpl<K, T, N>(wrappedRow);
    }
}
