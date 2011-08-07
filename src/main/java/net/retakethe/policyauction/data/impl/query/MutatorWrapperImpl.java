package net.retakethe.policyauction.data.impl.query;

import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.Column;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.Schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Subcolumn;
import net.retakethe.policyauction.data.impl.schema.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.SupercolumnFamily;

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
    public <N, V> void addColumnInsertion(K key, Column<K, N, V> column, N name, V value) {
        ColumnFamily<K, N> cf = column.getColumnFamily();
        validateCF(cf);
        wrappedMutator.addInsertion(key, cf.getName(),
                HFactory.createColumn(name, value, cf.getColumnNameSerializer(), column.getValueSerializer()));
    }

    @Override
    public <SN> void addSupercolumnDeletion(K key, Supercolumn<K, SN, ?> supercolumn,
            SN supercolumnName) {
        SupercolumnFamily<K, SN, ?> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSuperDelete(key, scf.getName(), supercolumnName, scf.getSupercolumnNameSerializer());
    }

    @Override
    public <SN, N> void addSubcolumnDeletion(K key, Subcolumn<K, SN, N, ?> subcolumn, SN supercolumnName,
            N subcolumnName) {
        SupercolumnFamily<K, SN, N> scf = subcolumn.getSupercolumn().getSupercolumnFamily();
        validateCF(scf);
        wrappedMutator.addSubDelete(key, scf.getName(), supercolumnName, subcolumnName,
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
            K key, Supercolumn<K, SN, N> supercolumn, SN supercolumnName) {
        SupercolumnFamily<K, SN, N> scf = supercolumn.getSupercolumnFamily();
        validateCF(scf);
        SupercolumnInserterImpl<K, SN, N> inserter =
                new SupercolumnInserterImpl<K, SN, N>(key, supercolumn, supercolumnName);
        supercolumnInserters.add(inserter);
        return inserter;
    }
}
