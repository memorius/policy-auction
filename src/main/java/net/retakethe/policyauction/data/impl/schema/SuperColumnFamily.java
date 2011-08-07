package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class SuperColumnFamily<K> extends BaseColumnFamily<K> {

    public SuperColumnFamily(String name, Class<K> keyType, Serializer<K> keySerializer) {
        super(name, keyType, keySerializer);
    }

}
