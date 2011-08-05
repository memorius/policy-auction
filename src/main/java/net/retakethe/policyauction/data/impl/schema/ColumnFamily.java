package net.retakethe.policyauction.data.impl.schema;

import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.QueryFactory;

/**
 * Enum-like class for Cassandra Column Families.
 * <p>
 * (Can't use an actual java enum due to use of generic type parameters - enums don't support this.)
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 *
 * TODO: define which keyspace CFs are in? - MAIN or LOGGING, get from keyspaceManager.
 */
public class ColumnFamily<K> {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Row-exists flag column for distinguishing real rows from tombstone rows in range_slice queries.
     * <p>
     * We use an empty byte array as the column value because it has the smallest representation in Cassandra.
     */
    public static final class ExistsColumn<K> extends StringNamedColumn<K, byte[]> {
        public ExistsColumn(Class<K> keyType, ColumnFamily<K> columnFamily) {
            super("_", keyType, columnFamily, byte[].class, BytesArraySerializer.get());
        }
    }

    private final String name;
    private final Class<K> keyType;
    private final Serializer<K> keySerializer;

    /**
     * @see #addExistsMarker(Mutator, Object)
     */
    public final ExistsColumn<K> EXISTS;

    public ColumnFamily(String name, Class<K> keyType, Serializer<K> keySerializer) {
        this.keyType = keyType;
        this.keySerializer = keySerializer;
        this.name = name;
        EXISTS = new ExistsColumn<K>(keyType, this);
    }

    public String getName() {
        return name;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public Serializer<K> getKeySerializer() {
        return keySerializer;
    }

    public Mutator<K> createMutator(Keyspace ks) {
        return HFactory.createMutator(ks, getKeySerializer());
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, can be empty,
     *      must be columns belonging to this ColumnFamily.
     */
    public <N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, List<Column<K, N, V>> columns) {
        return QueryFactory.createRangeSlicesQuery(ks, this, columns);
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

    public void addRowDeletion(Mutator<K> mutator, K key) {
        mutator.addDeletion(key, name);
    }
}
