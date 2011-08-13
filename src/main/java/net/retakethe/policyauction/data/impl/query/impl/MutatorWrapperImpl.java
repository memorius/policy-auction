package net.retakethe.policyauction.data.impl.query.impl;

import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;
import net.retakethe.policyauction.data.impl.schema.value.ValueImpl;

/**
 * @author Nick Clarke
 */
public class MutatorWrapperImpl<K, T extends Timestamp> implements MutatorWrapperInternal<K, T> {

    private final SchemaKeyspace keyspace;
    private final Serializer<K> keySerializer;
    private final Mutator<K> wrappedMutator;
    private final List<SubcolumnMutatorImpl<K, T, ?, ?>> supercolumnMutators;

    public MutatorWrapperImpl(SchemaKeyspace keyspace, Serializer<K> keySerializer, KeyspaceManager keyspaceManager) {
        this.keyspace = keyspace;
        this.keySerializer = keySerializer;
        this.wrappedMutator = HFactory.createMutator(keyspaceManager.getKeyspace(keyspace), keySerializer);
        this.supercolumnMutators = new LinkedList<SubcolumnMutatorImpl<K, T, ?, ?>>();
    }

    @Override
    public <N, V> void addColumnInsertion(K key, Column<K, T, N, V> column, N name, Value<T, V> value) {
        ColumnFamily<K, T, N> cf = column.getColumnFamily();
        validateCF(cf);
        HColumn<N, V> hColumn;
        long timestamp = value.getTimestamp().getCassandraValue();
        Integer ttl = ((ValueImpl<T, V>) value).getTimeToLiveSeconds();
        if (ttl == null) {
            hColumn = HFactory.createColumn(name, value.getValue(), timestamp,
                    cf.getColumnNameSerializer(), column.getValueSerializer());
        } else {
            hColumn = HFactory.createColumn(name, value.getValue(), timestamp, ttl,
                    cf.getColumnNameSerializer(), column.getValueSerializer());
        }
        wrappedMutator.addInsertion(key, cf.getName(), hColumn);
    }

    @Override
    public <N, V> void addColumnDeletion(K key, Column<K, T, N, V> column, N name, T timestamp) {
        ColumnFamily<K, T, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addDeletion(key, cf.getName(), name, cf.getColumnNameSerializer(),
                timestamp.getCassandraValue());
    }

    @Override
    public <SN> void addSupercolumnDeletion(K key, Supercolumn<K, T, SN, ?> supercolumn,
            SN supercolumnName) {
        SupercolumnFamily<K, T, SN, ?> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSuperDelete(key, scf.getName(), supercolumnName, scf.getSupercolumnNameSerializer());
    }

    @Override
    public void addRowDeletion(BaseColumnFamily<K, T> cf, K key, T timestamp) {
        validateCF(cf);
        wrappedMutator.addDeletion(key, cf.getName(), timestamp.getCassandraValue());
    }

    @Override
    public MutationResult execute() {
        for (SubcolumnMutatorImpl<K, T, ?, ?> mutator : supercolumnMutators) {
            mutator.apply();
        }
        return wrappedMutator.execute();
    }

    @Override
    public <SN, N> SubcolumnMutator<K, T, SN, N> createSubcolumnMutator(
            K key, Supercolumn<K, T, SN, N> supercolumn, SN supercolumnName) {
        SupercolumnFamily<K, T, SN, N> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        SubcolumnMutatorImpl<K, T, SN, N> mutator =
            new SubcolumnMutatorImpl<K, T, SN, N>(wrappedMutator, key, supercolumn, supercolumnName);
        supercolumnMutators.add(mutator);
        return mutator;
    }

    private void validateCF(BaseColumnFamily<K, T> cf) {
        if (cf.getKeyspace() != keyspace) {
            throw new IllegalArgumentException("Column Family " + cf.getName() + " has the wrong keyspace "
                    + " to be used with this Mutator. Got " + cf.getKeyspace() + ", expected " + keyspace);
        }
        if (cf.getKeySerializer() != keySerializer) {
            throw new IllegalArgumentException("Column Family " + cf.getName() + " has the wrong key serializer"
                    + " to be used with this Mutator");
        }
    }
}
