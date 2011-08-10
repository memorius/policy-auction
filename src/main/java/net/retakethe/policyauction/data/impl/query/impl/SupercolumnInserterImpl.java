package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class SupercolumnInserterImpl<K, SN, N> implements SupercolumnInserterInternal<K, SN, N> {

    private final K key;
    private final Supercolumn<K, SN, N> supercolumn;
    private final SN supercolumnName;
    private final List<HColumn<N, ?>> subcolumns;

    public SupercolumnInserterImpl(K key, Supercolumn<K, SN, N> supercolumn, SN supercolumnName) {
        this.key = key;
        this.supercolumn = supercolumn;
        this.supercolumnName = supercolumnName;
        this.subcolumns = new LinkedList<HColumn<N, ?>>();
    }

    @Override
    public <V> void addSubcolumnInsertion(SubcolumnRange<K, SN, N, V> subcolumn, N subcolumnName, V value) {
        validateSubcolumn(subcolumn);
        HColumn<N, ?> hColumn = HFactory.createColumn(subcolumnName, value,
                subcolumn.getSupercolumn().getSupercolumnFamily().getSubcolumnNameSerializer(),
                subcolumn.getValueSerializer());
        subcolumns.add(hColumn);
    }

    @Override
    public <V> void addSubcolumnInsertion(NamedSubcolumn<K, SN, N, V> subcolumn, V value) {
        validateSubcolumn(subcolumn);
        HColumn<N, ?> hColumn = HFactory.createColumn(subcolumn.getName(), value,
                subcolumn.getSupercolumn().getSupercolumnFamily().getSubcolumnNameSerializer(),
                subcolumn.getValueSerializer());
        subcolumns.add(hColumn);
    }

    protected void apply(Mutator<K> wrappedMutator) {
        SupercolumnFamily<K, SN, N> scf = supercolumn.getSupercolumnFamily();

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

    private void validateSubcolumn(SubcolumnRange<K, SN, N, ?> subcolumn) {
        if (subcolumn.getSupercolumn() != supercolumn) {
            throw new IllegalArgumentException("The supplied subcolumn does not belong to this mutator's supercolumn");
        }
    }

    private void validateSubcolumn(NamedSubcolumn<K, SN, N, ?> subcolumn) {
        if (subcolumn.getSupercolumn() != supercolumn) {
            throw new IllegalArgumentException("The supplied subcolumn does not belong to this mutator's supercolumn");
        }
    }
}
