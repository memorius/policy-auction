package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.Subcolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;
import net.retakethe.policyauction.data.impl.schema.value.ValueImpl;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class SubcolumnMutatorImpl<K, T extends Timestamp, SN, N> implements SubcolumnMutatorInternal<K, T, SN, N> {

    private final Mutator<K> wrappedMutator;
    private final K key;
    private final Supercolumn<K, T, SN, N> supercolumn;
    private final SN supercolumnName;
    private final List<HColumn<N, ?>> subcolumns;

    public SubcolumnMutatorImpl(Mutator<K> wrappedMutator, K key,
            Supercolumn<K, T, SN, N> supercolumn, SN supercolumnName) {
        this.wrappedMutator = wrappedMutator;
        this.key = key;
        this.supercolumn = supercolumn;
        this.supercolumnName = supercolumnName;
        this.subcolumns = new LinkedList<HColumn<N, ?>>();
    }

    @Override
    public <V> void addSubcolumnInsertion(Subcolumn<K, T, SN, N, V> subcolumn, N subcolumnName, Value<T, V> value) {
        validateSubcolumn(subcolumn);
        HColumn<N, V> hColumn;
        long timestamp = value.getTimestamp().getCassandraValue();
        Integer ttl = ((ValueImpl<T, V>) value).getTimeToLiveSeconds();
        if (ttl == null) {
            hColumn = HFactory.createColumn(subcolumnName, value.getValue(), timestamp,
                    supercolumn.getSupercolumnFamily().getSubcolumnNameSerializer(), subcolumn.getValueSerializer());
        } else {
            hColumn = HFactory.createColumn(subcolumnName, value.getValue(), timestamp, ttl,
                    supercolumn.getSupercolumnFamily().getSubcolumnNameSerializer(), subcolumn.getValueSerializer());
        }
        subcolumns.add(hColumn);
    }

    @Override
    public void addSubcolumnDeletion(Subcolumn<K, T, SN, N, ?> subcolumn, N subcolumnName, T timestamp) {
        validateSubcolumn(subcolumn);
        SupercolumnFamily<K, T, SN, N> scf = supercolumn.getSupercolumnFamily();
        wrappedMutator.addSubDelete(key, scf.getName(), supercolumnName, subcolumnName,
                scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                timestamp.getCassandraValue());
    }

    protected void apply() {
        SupercolumnFamily<K, T, SN, N> scf = supercolumn.getSupercolumnFamily();

        // Must copy, HSuperColumnImpl stores a reference
        List<HColumn<N, Object>> copy = new ArrayList<HColumn<N, Object>>();
        for (HColumn<N, ?> subcolumn : subcolumns) {
            @SuppressWarnings("unchecked")
            HColumn<N, Object> castSubcolumn = (HColumn<N, Object>) subcolumn;
            copy.add(castSubcolumn);
        }
        // We applied the changes to the underlying mutator. Any further changes will start a new set.
        // (This doesn't affect the semantics as far as I can see - our batching just groups changes per supercolumn
        //  in the wire representation.)
        subcolumns.clear();

        // Note the ValueSerializer passed to createSuperColumn is not actually used when sending, only when receiving.
        // We pass variable-value-typed HColumns as the subcolumns; their individual serializers will be used.
        wrappedMutator.addInsertion(key, scf.getName(),
                HFactory.createSuperColumn(supercolumnName,
                        copy,
                        scf.getSupercolumnNameSerializer(),
                        scf.getSubcolumnNameSerializer(), DummySerializer.get()));
    }

    private void validateSubcolumn(Subcolumn<K, T, SN, N, ?> subcolumn) {
        if (subcolumn.getSupercolumn() != supercolumn) {
            throw new IllegalArgumentException("The supplied subcolumn does not belong to this mutator's supercolumn");
        }
    }
}
