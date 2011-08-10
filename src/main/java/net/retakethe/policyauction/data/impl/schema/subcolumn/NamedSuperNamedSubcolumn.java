package net.retakethe.policyauction.data.impl.schema.subcolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.query.impl.SupercolumnInserterInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;

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
public class NamedSuperNamedSubcolumn<K, SN, N, V> extends NamedSuperSubcolumn<K, SN, N, V>
        implements NamedSubcolumn<K, SN, N, V> {
 
    private final N name;

    public NamedSuperNamedSubcolumn(N name, NamedSupercolumn<K, SN, N> supercolumn, Type<V> valueType) {
        super(supercolumn, valueType);
        this.name = name;
    }

    @Override
    public N getName() {
        return name;
    }

    public void addSubcolumnInsertion(SupercolumnInserter<K, SN, N> inserter, V value) {
        ((SupercolumnInserterInternal<K, SN, N>) inserter).addSubcolumnInsertion(this, value);
    }

    public void addSubcolumnDeletion(MutatorWrapper<K> m, K key) {
        ((MutatorWrapperInternal<K>) m).addSubcolumnDeletion(key, getSupercolumn().getName(), this);
    }
}
