package net.retakethe.policyauction.data.impl.schema.family;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;

/**
 * Used for single-row lists such as those in "moderation" table.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the subcolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class SingleRowRangeSupercolumnFamily<K, T extends Timestamp, SN, N, R extends SupercolumnRange<K, T, SN, N>>
        extends RangeSupercolumnFamily<K, T, SN, N, R> {

    private final K key;

    public SingleRowRangeSupercolumnFamily(SchemaKeyspace keyspace, String name, K key, Type<K> keyType,
            TimestampFactory<T> timestampFactory,
            Type<SN> supercolumnNameType, Type<N> subcolumnNameType) {
        super(keyspace, name, keyType, timestampFactory, supercolumnNameType, subcolumnNameType);
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(MutatorWrapper<K, T> mutator, SN supercolumnName) {
        return createSubcolumnMutator(mutator, key, supercolumnName);
    }

    public void addSupercolumnDeletion(MutatorWrapper<K, T> mutator, SN supercolumnName) {
        addSupercolumnDeletion(mutator, key, supercolumnName);
    }

    public VariableValueTypedSuperSliceQuery<K, T, SN, N> createSuperSliceQuery(KeyspaceManager keyspaceManager,
            SN start, SN finish, boolean reversed, int count) {
        return createSuperSliceQuery(keyspaceManager, key, start, finish, reversed, count);
    }

    public VariableValueTypedSupercolumnQuery<T, SN, N> createSupercolumnQuery(KeyspaceManager keyspaceManager,
            SN supercolumnName) {
        return createSupercolumnQuery(keyspaceManager, key, supercolumnName);
    }
}
