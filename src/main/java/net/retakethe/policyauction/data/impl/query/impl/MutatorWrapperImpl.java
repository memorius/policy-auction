package net.retakethe.policyauction.data.impl.query.impl;

import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * @author Nick Clarke
 */
public class MutatorWrapperImpl<K> implements MutatorWrapper<K> {

    private final SchemaKeyspace keyspace;
    private final Serializer<K> keySerializer;
    private final Mutator<K> wrappedMutator;
    private final List<SupercolumnInserterImpl<K, ?, ?>> supercolumnInserters;

    public MutatorWrapperImpl(SchemaKeyspace keyspace, Serializer<K> keySerializer, KeyspaceManager keyspaceManager) {
        this.keyspace = keyspace;
        this.keySerializer = keySerializer;
        this.wrappedMutator = HFactory.createMutator(keyspaceManager.getKeyspace(keyspace), keySerializer);
        this.supercolumnInserters = new LinkedList<SupercolumnInserterImpl<K, ?, ?>>();
    }

    @Override
    public <N, V> void addColumnInsertion(K key, NamedColumn<K, N, V> column, V value) {
        ColumnFamily<K, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addInsertion(key, cf.getName(),
                HFactory.createColumn(column.getName(), value,
                        cf.getColumnNameSerializer(), column.getValueSerializer()));
    }

    @Override
    public <N, V> void addColumnInsertion(K key, ColumnRange<K, N, V> column, N name, V value) {
        ColumnFamily<K, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addInsertion(key, cf.getName(),
                HFactory.createColumn(name, value, cf.getColumnNameSerializer(), column.getValueSerializer()));
    }

    @Override
    public <N, V> void addColumnDeletion(K key, NamedColumn<K, N, V> column) {
        ColumnFamily<K, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addDeletion(key, cf.getName(), column.getName(), cf.getColumnNameSerializer());
    }

    @Override
    public <N, V> void addColumnDeletion(K key, ColumnRange<K, N, V> column, N name) {
        ColumnFamily<K, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addDeletion(key, cf.getName(), name, cf.getColumnNameSerializer());
    }

    @Override
    public <SN> void addSupercolumnDeletion(K key, SupercolumnRange<K, SN, ?> supercolumn,
            SN supercolumnName) {
        SupercolumnFamily<K, SN, ?> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSuperDelete(key, scf.getName(), supercolumnName, scf.getSupercolumnNameSerializer());
    }

    @Override
    public <SN> void addSupercolumnDeletion(K key, NamedSupercolumn<K, SN, ?> supercolumn) {
        SupercolumnFamily<K, SN, ?> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSuperDelete(key, scf.getName(), supercolumn.getName(), scf.getSupercolumnNameSerializer());
    }

    @Override
    public <SN, N> void addSubcolumnDeletion(K key, SN supercolumnName, SubcolumnRange<K, SN, N, ?> subcolumn,
            N subcolumnName) {
        SupercolumnFamily<K, SN, N> scf = subcolumn.getSupercolumn().getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSubDelete(key, scf.getName(), supercolumnName, subcolumnName,
                scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer());
    }

    @Override
    public <SN, N> void addSubcolumnDeletion(K key, SN supercolumnName, NamedSubcolumn<K, SN, N, ?> subcolumn) {
        SupercolumnFamily<K, SN, N> scf = subcolumn.getSupercolumn().getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSubDelete(key, scf.getName(), supercolumnName, subcolumn.getName(),
                scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer());
    }
    
    @Override
    public void addRowDeletion(BaseColumnFamily<K> cf, K key) {
        validateCF(cf);
        wrappedMutator.addDeletion(key, cf.getName());
    }

    @Override
    public MutationResult execute() {
        for (SupercolumnInserterImpl<K, ?, ?> inserter : supercolumnInserters) {
            inserter.apply(wrappedMutator);
        }
        return wrappedMutator.execute();
    }

    private void validateCF(BaseColumnFamily<K> cf) {
        if (cf.getKeyspace() != keyspace) {
            throw new IllegalArgumentException("Column Family " + cf.getName() + " has the wrong keyspace "
                    + " to be used with this Mutator. Got " + cf.getKeyspace() + ", expected " + keyspace);
        }
        if (cf.getKeySerializer() != keySerializer) {
            throw new IllegalArgumentException("Column Family " + cf.getName() + " has the wrong key serializer"
                    + " to be used with this Mutator");
        }
    }

    @Override
    public <SN, N> SupercolumnInserter<K, SN, N> createSupercolumnInserter(
            K key, SupercolumnRange<K, SN, N> supercolumn, SN supercolumnName) {
        SupercolumnFamily<K, SN, N> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        SupercolumnInserterImpl<K, SN, N> inserter =
            new SupercolumnInserterImpl<K, SN, N>(key, supercolumn, supercolumnName);
        supercolumnInserters.add(inserter);
        return inserter;
    }

    @Override
    public <SN, N> SupercolumnInserter<K, SN, N> createSupercolumnInserter(
            K key, NamedSupercolumn<K, SN, N> supercolumn) {
        SupercolumnFamily<K, SN, N> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        SupercolumnInserterImpl<K, SN, N> inserter =
                new SupercolumnInserterImpl<K, SN, N>(key, supercolumn, supercolumn.getName());
        supercolumnInserters.add(inserter);
        return inserter;
    }
}
