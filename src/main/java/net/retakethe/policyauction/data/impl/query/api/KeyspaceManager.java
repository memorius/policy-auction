package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.Keyspace;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;

/**
 * Holder for Hector keyspace access.
 *
 * @author Nick Clarke
 */
public interface KeyspaceManager {
    Keyspace getKeyspace(SchemaKeyspace schemaKS);
}
