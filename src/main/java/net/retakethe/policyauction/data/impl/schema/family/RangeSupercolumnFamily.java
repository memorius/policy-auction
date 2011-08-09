package net.retakethe.policyauction.data.impl.schema.family;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

/**
 * Used for supercolumn families which contain a single supercolumn range in each row.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <SN> the supercolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the subcolumn name type, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class RangeSupercolumnFamily<K, SN, N> extends SupercolumnFamily<K, SN, N> {

    private SupercolumnRange<K, SN, N> supercolumnRange;

    public RangeSupercolumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType,
            Type<SN> supercolumnNameType, Type<N> subcolumnNameType) {
        super(keyspace, name, keyType, supercolumnNameType, subcolumnNameType);
    }

    protected void setSupercolumnRange(SupercolumnRange<K, SN, N> supercolumnRange) {
        if (this.supercolumnRange != null) {
            throw new IllegalStateException("supercolumnRange already set");
        }
        this.supercolumnRange = supercolumnRange;
    }

    public VariableValueTypedSuperSliceQuery<K, SN, N> createSuperSliceQuery(KeyspaceManager keyspaceManager, K key,
            SN start, SN finish, boolean reversed, int count) {
        return QueryFactory.createVariableValueTypedSuperSliceQuery(keyspaceManager, this, key, supercolumnRange,
                start, finish, reversed, count);
    }

    public VariableValueTypedSupercolumnQuery<SN, N> createSupercolumnQuery(KeyspaceManager keyspaceManager, K key,
            SN supercolumnName) {
        return QueryFactory.createSupercolumnQuery(keyspaceManager, this, key, supercolumnRange, supercolumnName);
    }
}
