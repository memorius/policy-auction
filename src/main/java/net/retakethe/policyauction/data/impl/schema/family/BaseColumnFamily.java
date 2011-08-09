package net.retakethe.policyauction.data.impl.schema.family;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperImpl;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.types.Type;

/**
 * Base class for schema definitions of cassandra Column Families and Super Column Families.
 * <p>
 * (Can't use an actual java enum due to use of generic type parameters - enums don't support this.)
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class BaseColumnFamily<K> {

    private final SchemaKeyspace keyspace;
    private final String name;
    private final Type<K> keyType;

    protected BaseColumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType) {
        this.keyspace = keyspace;
        this.name = name;
        this.keyType = keyType;
    }

    public SchemaKeyspace getKeyspace() {
        return keyspace;
    }

    public String getName() {
        return name;
    }

    public Type<K> getKeyType() {
        return keyType;
    }

    public Serializer<K> getKeySerializer() {
        return keyType.getSerializer();
    }

    /**
     * Create a mutator for setting up batch mutations.
     * This can then accumulate multiple column/supercolumn/subcolumn inserts,
     * and/or row/column/supercolumn/subcolumn deletes
     * to be sent to Cassandra as a single unit by calling {@link MutatorWrapper#execute()}.
     * <p>
     * Note the mutations are not ordered or atomic across different column families or rows!
     * <p>
     * They are atomic within each row of the same column family however,
     * but intermediate states are not isolated from concurrent read transactions.
     * <p>
     * A single mutator may be reused across multiple column families sharing the same keytype <K>
     * and key serializer, which allows updates to be sent more efficiently.
     */
    public MutatorWrapper<K> createMutator(KeyspaceManager keyspaceManager) {
        return new MutatorWrapperImpl<K>(getKeyspace(), getKeySerializer(), keyspaceManager);
    }

    public void addRowDeletion(MutatorWrapper<K> mutator, K key) {
        mutator.addRowDeletion(this, key);
    }
}
