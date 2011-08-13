package net.retakethe.policyauction.data.impl.schema.supercolumn;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Cassandra supercolumn ranges where there isn't a single supercolumn name.
 *
 * @param <K> the key type of the supercolumn family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the subcolumn name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class SupercolumnRange<K, T extends Timestamp, SN, N> extends Supercolumn<K, T, SN, N> {

    protected SupercolumnRange(SupercolumnFamily<K, T, SN, N> supercolumnFamily) {
        super(supercolumnFamily);
    }

    public SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(MutatorWrapper<K, T> m, K key,
            SN supercolumnName) {
        return ((MutatorWrapperInternal<K, T>) m).createSubcolumnMutator(key, this, supercolumnName);
    }

    public void addSupercolumnDeletion(MutatorWrapper<K, T> m, K key, SN supercolumnName) {
        ((MutatorWrapperInternal<K, T>) m).addSupercolumnDeletion(key, this, supercolumnName);
    }
}
