package net.retakethe.policyauction.data.impl;

import me.prettyprint.hector.api.Keyspace;
import net.retakethe.policyauction.data.impl.schema.Schema;

public interface KeyspaceManager {
    Keyspace getKeyspace(Schema.SchemaKeyspace schemaKS);
}
