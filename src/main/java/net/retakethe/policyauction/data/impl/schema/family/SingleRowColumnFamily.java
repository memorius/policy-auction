package net.retakethe.policyauction.data.impl.schema.family;

/**
 * @author Nick Clarke
 *
 * @param <K> the column family key type
 */
public interface SingleRowColumnFamily<K> {
    K getKey();
}
