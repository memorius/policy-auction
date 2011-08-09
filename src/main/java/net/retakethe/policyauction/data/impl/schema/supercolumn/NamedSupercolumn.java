package net.retakethe.policyauction.data.impl.schema.supercolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;

/**
 * Cassandra supercolumns with fixed names.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class NamedSupercolumn<K, SN, N> extends Supercolumn<K, SN, N> {

    private final SN name;

    protected NamedSupercolumn(SN name, SupercolumnFamily<K, SN, N> supercolumnFamily) {
        super(supercolumnFamily);
        this.name = name;
    }

    public SN getName() {
        return name;
    }

    public SupercolumnInserter<K, SN, N> createSupercolumnInserter(MutatorWrapper<K> mutator, K key) {
        return mutator.createSupercolumnInserter(key, this);
    }

    public void addSupercolumnDeletion(MutatorWrapper<K> mutator, K key) {
        mutator.addSupercolumnDeletion(key, this);
    }
}
