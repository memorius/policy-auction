package net.retakethe.policyauction.data.impl.schema.subcolumn;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;

public interface SubcolumnRange<K, T extends Timestamp, SN, N, V> {

    Supercolumn<K, T, SN, N> getSupercolumn();

    Serializer<V> getValueSerializer();

    void addSubcolumnInsertion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName, Value<T, V> value);

    /**
     * Delete column, using current timestamp
     */
    void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName);

    /**
     * Delete column, using specified timestamp
     */
    void addSubcolumnDeletion(SubcolumnMutator<K, T, SN, N> m, N subcolumnName, T timestamp);
}
