package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;

/**
 * Cassandra subcolumns where there isn't a fixed subcolumn name, of supercolumns with fixed names.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class NamedSupercolumnSubcolumnRange<K, SN, N, V> extends NamedSupercolumnSubcolumn<K, SN, N, V>
        implements SubcolumnRange<K, SN, N, V> {

    public NamedSupercolumnSubcolumnRange(
            NamedSupercolumn<K, SN, N> supercolumn,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(supercolumn, valueType, valueSerializer);
    }

    public void addSubcolumnDeletion(MutatorWrapper<K> mutator, K key, N subcolumnName) {
        mutator.addSubcolumnDeletion(key, this, getSupercolumn().getName(), subcolumnName);
    }
}
