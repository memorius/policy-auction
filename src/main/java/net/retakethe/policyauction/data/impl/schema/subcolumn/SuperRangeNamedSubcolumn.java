package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.impl.SubcolumnMutatorInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

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
public class SuperRangeNamedSubcolumn<K, SN, N, V> extends SuperRangeSubcolumn<K, SN, N, V>
        implements NamedSubcolumn<K, SN, N, V> {
 
    private final N name;

    public SuperRangeNamedSubcolumn(N name, SupercolumnRange<K, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
        this.name = name;
    }

    @Override
    public N getName() {
        return name;
    }

    @Override
    public void addSubcolumnInsertion(SubcolumnMutator<K, SN, N> m, V value) {
        ((SubcolumnMutatorInternal<K, SN, N>) m).addSubcolumnInsertion(this, value);
    }

    @Override
    public void addSubcolumnDeletion(SubcolumnMutator<K, SN, N> m) {
        ((SubcolumnMutatorInternal<K, SN, N>) m).addSubcolumnDeletion(this);
    }
}
