package net.retakethe.policyauction.data.impl.schema;

import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedMultiGetSliceQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedSliceQuery;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class ColumnFamily<K> extends BaseColumnFamily<K> {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Row-exists flag column for distinguishing real rows from tombstone rows in range_slice queries.
     * <p>
     * We use an empty byte array as the column value because it has the smallest representation in Cassandra.
     */
    public static final class ExistsColumn<K> extends StringNamedColumn<K, byte[]> {
        public ExistsColumn(ColumnFamily<K> columnFamily) {
            super("_", columnFamily, byte[].class, BytesArraySerializer.get());
        }
    }

    /**
     * @see #addExistsMarker(Mutator, Object)
     */
    public final ExistsColumn<K> EXISTS;

    public ColumnFamily(String name, Class<K> keyType, Serializer<K> keySerializer) {
        super(name, keyType, keySerializer);
        EXISTS = new ExistsColumn<K>(this);
    }

    /**
     * @param columns columns for {@link SliceQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to this ColumnFamily.
     */
    public <N> VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns, K key) {
        return QueryFactory.createVariableValueTypedSliceQuery(ks, this, columns, key);
    }

    public <N> VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(Keyspace ks,
            ColumnRange<K, N, ?> columnRange, N start, N finish, boolean reversed, int count, K key) {
        return QueryFactory.createVariableValueTypedSliceQuery(ks, this,
                columnRange, start, finish, reversed, count, key);
    }

    /**
     * @param columns columns for {@link MultigetSliceQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to this ColumnFamily.
     */
    public <N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedMultiGetSliceQuery(ks, this, columns);
    }

    public <N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(Keyspace ks,
            ColumnRange<K, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedMultiGetSliceQuery(ks, this,
                columnRange, start, finish, reversed, count);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to this ColumnFamily.
     */
    public <N> VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedRangeSlicesQuery(ks, this, columns);
    }

    public <N> VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(Keyspace ks,
            ColumnRange<K, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedRangeSlicesQuery(ks, this, columnRange,
                start, finish, reversed, count);
    }
    
    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to this ColumnFamily.
     */
    public <N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, List<NamedColumn<K, N, V>> columns) {
        return QueryFactory.createRangeSlicesQuery(ks, this, columns);
    }
    
    public <N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, ColumnRange<K, N, V> columnRange,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(ks, this, columnRange, start, finish, reversed, count);
    }
    
    /**
     * Add 'row exists' column.
     * <p>
     * This dummy column exists in every column family and should always be set when creating rows,
     * or when updating rows that might have been deleted.
     * <p>
     * Its absence (value doesn't matter) is used in range-slice queries to filter out deleted tombstone rows
     * when there is no other suitable column to filter with - e.g. when querying with setReturnKeysOnly().
     * See HectorDAOTestBase#cleanColumnFamily for an example.
     * See here for more info on tombstones: {@link "http://wiki.apache.org/cassandra/FAQ#range_ghosts"}.
     */
    public void addExistsMarker(Mutator<K> mutator, K key) {
        EXISTS.addInsertion(mutator, key, EMPTY_BYTE_ARRAY);
    }
}
