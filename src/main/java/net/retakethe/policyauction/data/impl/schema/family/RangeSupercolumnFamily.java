package net.retakethe.policyauction.data.impl.schema.family;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultigetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;

/**
 * Used for supercolumn families which contain a single supercolumn range in each row.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the subcolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <R> the supercolumn range type; this allows access to the subcolumn definitions.
 *
 * @author Nick Clarke
 */
public class RangeSupercolumnFamily<K, T extends Timestamp, SN, N, R extends SupercolumnRange<K, T, SN, N>>
        extends SupercolumnFamily<K, T, SN, N> {

    private R supercolumnRange;

    public RangeSupercolumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType,
            TimestampFactory<T> timestampFactory,
            Type<SN> supercolumnNameType, Type<N> subcolumnNameType) {
        super(keyspace, name, keyType, timestampFactory, supercolumnNameType, subcolumnNameType);
    }

    /**
     * Initialization call to set the supercolumn range definition.
     * Can't be set in constructor due to cyclic dependency.
     *
     * @throws IllegalStateException if called more than once
     */
    protected void setSupercolumnRange(R supercolumnRange) {
        if (this.supercolumnRange != null) {
            throw new IllegalStateException("supercolumnRange already set");
        }
        this.supercolumnRange = supercolumnRange;
    }

    public R getSupercolumnRange() {
        return supercolumnRange;
    }

    public SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(MutatorWrapper<K, T> m, K key,
            SN supercolumnName) {
        return ((MutatorWrapperInternal<K, T>) m).createSubcolumnMutator(key, supercolumnRange, supercolumnName);
    }

    public void addSupercolumnDeletion(MutatorWrapper<K, T> m, K key, SN supercolumnName) {
        ((MutatorWrapperInternal<K, T>) m).addSupercolumnDeletion(key, supercolumnRange, supercolumnName);
    }

    public VariableValueTypedSupercolumnQuery<T, SN, N> createSupercolumnQuery(KeyspaceManager keyspaceManager, K key,
            SN supercolumnName) {
        return createSupercolumnQuery(keyspaceManager, key, supercolumnRange, supercolumnName);
    }

    public VariableValueTypedSuperSliceQuery<K, T, SN, N> createSuperSliceQuery(KeyspaceManager keyspaceManager, K key,
            SN start, SN finish, boolean reversed, int count) {
        return createSuperSliceQuery(keyspaceManager, key, supercolumnRange, start, finish, reversed, count);
    }

    public VariableValueTypedMultigetSuperSliceQuery<K, T, SN, N> createMultigetSuperSliceQuery(
            KeyspaceManager keyspaceManager,
            SN start, SN finish, boolean reversed, int count) {
        return createMultigetSuperSliceQuery(keyspaceManager, supercolumnRange, start, finish, reversed, count);
    }

    public VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N> createRangeSuperSlicesQuery(
            KeyspaceManager keyspaceManager,
            SN start, SN finish, boolean reversed, int count) {
        return createRangeSuperSlicesQuery(keyspaceManager, supercolumnRange, start, finish, reversed, count);
    }
}
