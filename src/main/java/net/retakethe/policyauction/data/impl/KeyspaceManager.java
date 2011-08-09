package net.retakethe.policyauction.data.impl;

import me.prettyprint.hector.api.Keyspace;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;

public interface KeyspaceManager {
    Keyspace getKeyspace(SchemaKeyspace schemaKS);
}
