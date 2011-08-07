package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 * Base class for schema definitions of cassandra Column Families and Super Column Families.
 * <p>
 * (Can't use an actual java enum due to use of generic type parameters - enums don't support this.)
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 *
 * TODO: define which keyspace CFs are in? - MAIN or LOGGING, get from keyspaceManager.
 */
public abstract class BaseColumnFamily<K> {

    private final String name;
    private final Class<K> keyType;
    private final Serializer<K> keySerializer;

    protected BaseColumnFamily(String name, Class<K> keyType, Serializer<K> keySerializer) {
        this.keyType = keyType;
        this.keySerializer = keySerializer;
        this.name = name;
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

    public void addRowDeletion(Mutator<K> mutator, K key) {
        mutator.addDeletion(key, getName());
    }
}
