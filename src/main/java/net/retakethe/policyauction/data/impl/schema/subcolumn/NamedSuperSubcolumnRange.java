package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.impl.SubcolumnMutatorInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

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
public class NamedSuperSubcolumnRange<K, T extends Timestamp, SN, N, V> extends NamedSuperSubcolumn<K, T, SN, N, V>
        implements SubcolumnRange<K, T, SN, N, V> {

    public NamedSuperSubcolumnRange(NamedSupercolumn<K, T, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
    }

    @Override
    public void addSubcolumnInsertion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName, V value) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnInsertion(this, subcolumnName, value);
    }

    @Override
    public void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnDeletion(this, subcolumnName);
    }
}
