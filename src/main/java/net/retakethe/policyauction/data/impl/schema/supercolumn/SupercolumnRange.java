package net.retakethe.policyauction.data.impl.schema.supercolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;

/**
 * Cassandra supercolumn ranges where there isn't a single supercolumn name.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class SupercolumnRange<K, SN, N> extends Supercolumn<K, SN, N> {

    protected SupercolumnRange(SupercolumnFamily<K, SN, N> supercolumnFamily) {
        super(supercolumnFamily);
    }

    public SupercolumnInserter<K, SN, N> createSupercolumnInserter(MutatorWrapper<K> mutator, K key,
            SN supercolumnName) {
        return mutator.createSupercolumnInserter(key, this, supercolumnName);
    }

    public void addSupercolumnDeletion(MutatorWrapper<K> mutator, K key, SN supercolumnName) {
        mutator.addSupercolumnDeletion(key, this, supercolumnName);
    }
}
