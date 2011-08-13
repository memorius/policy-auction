package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Iterator;

import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRows;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedRowsImpl<K, T extends Timestamp, N> implements VariableValueTypedRows<K, T, N> {

    private final class WrappingIterator implements Iterator<VariableValueTypedRow<K, T, N>> {
        private final Iterator<Row<K, N, Object>> wrappedIterator;

        public WrappingIterator(Iterator<Row<K, N, Object>> wrappedIterator) {
            this.wrappedIterator = wrappedIterator;
        }

        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        @Override
        public VariableValueTypedRow<K, T, N> next() {
            return wrapRow(wrappedIterator.next());
        }

        @Override
        public void remove() {
            wrappedIterator.remove();
        }
    }

    private final Rows<K, N, Object> wrappedRows;

    public VariableValueTypedRowsImpl(Rows<K, N, Object> wrappedRows) {
        this.wrappedRows = wrappedRows;
    }

    @Override
    public Iterator<VariableValueTypedRow<K, T, N>> iterator() {
        return new WrappingIterator(wrappedRows.iterator());
    }

    @Override
    public VariableValueTypedRow<K, T, N> getByKey(K key) {
        return wrapRow(wrappedRows.getByKey(key));
    }

    @Override
    public int getCount() {
        return wrappedRows.getCount();
    }

    protected VariableValueTypedRow<K, T, N> wrapRow(Row<K, N, Object> wrappedRow) {
        if (wrappedRow == null) {
            return null;
        }
        return new VariableValueTypedRowImpl<K, T, N>(wrappedRow);
    }
}
