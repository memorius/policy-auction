package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.impl.SubcolumnMutatorInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;

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
public class SuperRangeSubcolumnRange<K, T extends Timestamp, SN, N, V> extends SuperRangeSubcolumn<K, T, SN, N, V>
        implements SubcolumnRange<K, T, SN, N, V> {

    public SuperRangeSubcolumnRange(SupercolumnRange<K, T, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
    }

    @Override
    public void addSubcolumnInsertion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName, Value<T, V> value) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnInsertion(this, subcolumnName, value);
    }

    @Override
    public void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnDeletion(this, subcolumnName,
                getSupercolumn().getSupercolumnFamily().createCurrentTimestamp());
    }

    @Override
    public void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName, T timestamp) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnDeletion(this, subcolumnName, timestamp);
    }
}
