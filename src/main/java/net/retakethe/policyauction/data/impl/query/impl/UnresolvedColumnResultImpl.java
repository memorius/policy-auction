package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedColumnResult;

/**
 * @author Nick Clarke
 */
public class UnresolvedColumnResultImpl<N> implements UnresolvedColumnResult<N> {

    private final HColumn<N, Object> wrappedColumn;

    public UnresolvedColumnResultImpl(HColumn<N, Object> wrappedColumn) {
        if (wrappedColumn == null) {
            throw new IllegalArgumentException("wrappedColumn must not be null");
        }
        this.wrappedColumn = wrappedColumn;
    }

    protected HColumn<N, Object> getWrappedColumn() {
        return this.wrappedColumn;
    }

    @Override
    public N getName() {
        return wrappedColumn.getName();
    }
}
