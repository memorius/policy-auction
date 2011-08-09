package net.retakethe.policyauction.data.impl.schema.family;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultiGetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.schema.Schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.types.Type;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the subcolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class SupercolumnFamily<K, SN, N> extends BaseColumnFamily<K> {

    private final Type<SN> supercolumnNameType;
    private final Type<N> subcolumnNameType;

    protected SupercolumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType,
            Type<SN> supercolumnNameType, Type<N> subcolumnNameType) {
        super(keyspace, name, keyType);
        this.supercolumnNameType = supercolumnNameType;
        this.subcolumnNameType = subcolumnNameType;
    }

    public Type<SN> getSupercolumnNameType() {
        return this.supercolumnNameType;
    }

    public Serializer<SN> getSupercolumnNameSerializer() {
        return this.supercolumnNameType.getSerializer();
    }

    public Type<N> getSubcolumnNameType() {
        return this.subcolumnNameType;
    }

    public Serializer<N> getSubcolumnNameSerializer() {
        return this.subcolumnNameType.getSerializer();
    }

    public VariableValueTypedSuperSliceQuery<K, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager,
            K key, List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return QueryFactory.createVariableValueTypedSuperSliceQuery(keyspaceManager, this, key, supercolumns);
    }

    public VariableValueTypedSuperSliceQuery<K, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager,
            K key, SupercolumnRange<K, SN, N> supercolumnRange,
            SN start, SN finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedSuperSliceQuery(keyspaceManager, this, key,
                supercolumnRange, start, finish, reversed, count);
    }

    public VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> createVariableValueTypedMultiGetSuperSliceQuery(
            KeyspaceManager keyspaceManager,
            List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return QueryFactory.createVariableValueTypedMultiGetSuperSliceQuery(keyspaceManager, this, supercolumns);
    }

    public VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> createVariableValueTypedMultiGetSuperSliceQuery(
            KeyspaceManager keyspaceManager,
            SupercolumnRange<K, SN, N> supercolumnRange,
            SN start, SN finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedMultiGetSuperSliceQuery(keyspaceManager, this,
                supercolumnRange, start, finish, reversed, count);
    }

    public VariableValueTypedRangeSuperSlicesQuery<K, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return QueryFactory.createVariableValueTypedRangeSuperSlicesQuery(keyspaceManager, this, supercolumns);
    }

    public VariableValueTypedRangeSuperSlicesQuery<K, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnRange<K, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedRangeSuperSlicesQuery(keyspaceManager, this,
                supercolumnRange, start, finish, reversed, count);
    }
}
