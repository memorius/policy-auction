package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.MutatorWrapper;

/**
 * Cassandra subcolumns with fixed names, of supercolumns where there isn't a fixed name.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class SupercolumnRangeNamedSubcolumn<K, SN, N, V> extends SupercolumnRangeSubcolumn<K, SN, N, V> {
 
    private final N name;

    public SupercolumnRangeNamedSubcolumn(N name,
            SupercolumnRange<K, SN, N> supercolumn,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(supercolumn, valueType, valueSerializer);
        this.name = name;
    }

    public N getName() {
        return name;
    }

    public void addSubcolumnDeletion(MutatorWrapper<K> mutator, K key, SN supercolumnName) {
        mutator.addSubcolumnDeletion(key, this, supercolumnName, name);
    }

}
