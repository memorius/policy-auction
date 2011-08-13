package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.impl.SubcolumnMutatorInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Cassandra subcolumns with fixed names, of supercolumns with fixed names.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the subcolumn value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class NamedSuperNamedSubcolumn<K, T extends Timestamp, SN, N, V> extends NamedSuperSubcolumn<K, T, SN, N, V>
        implements NamedSubcolumn<K, T, SN, N, V> {
 
    private final N name;

    public NamedSuperNamedSubcolumn(N name, NamedSupercolumn<K, T, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
        this.name = name;
    }

    @Override
    public N getName() {
        return name;
    }

    @Override
    public void addSubcolumnInsertion(SubcolumnMutator<K, T, SN, N> m, V value) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnInsertion(this, getName(), value);
    }

    @Override
    public void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m) {
        ((SubcolumnMutatorInternal<K, T, SN, N>) m).addSubcolumnDeletion(this, getName());
    }
}
