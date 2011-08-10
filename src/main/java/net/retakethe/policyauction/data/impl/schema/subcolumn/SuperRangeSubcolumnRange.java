package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.query.impl.SupercolumnInserterInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * Cassandra subcolumns where there isn't a fixed subcolumn name, of supercolumns where there isn't a fixed name.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class SuperRangeSubcolumnRange<K, SN, N, V> extends SuperRangeSubcolumn<K, SN, N, V>
        implements SubcolumnRange<K, SN, N, V> {

    public SuperRangeSubcolumnRange(SupercolumnRange<K, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
    }

    public void addSubcolumnInsertion(SupercolumnInserter<K, SN, N> inserter, N subcolumnName, V value) {
        ((SupercolumnInserterInternal<K, SN, N>) inserter).addSubcolumnInsertion(this, subcolumnName, value);
    }

    public void addSubcolumnDeletion(MutatorWrapper<K> m, K key, SN supercolumnName, N subcolumnName) {
        ((MutatorWrapperInternal<K>) m).addSubcolumnDeletion(key, supercolumnName, this, subcolumnName);
    }
}
