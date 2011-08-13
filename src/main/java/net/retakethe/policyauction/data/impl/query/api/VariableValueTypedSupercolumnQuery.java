package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface VariableValueTypedSupercolumnQuery<T extends Timestamp, SN, N>
        extends Query<VariableValueTypedSupercolumn<T, SN, N>> {

}
