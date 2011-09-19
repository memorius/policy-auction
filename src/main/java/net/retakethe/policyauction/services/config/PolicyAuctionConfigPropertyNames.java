package net.retakethe.policyauction.services.config;

/**
 * Property names used for configuration.
 *
 * @author Nick Clarke
 */
public final class PolicyAuctionConfigPropertyNames {
    private PolicyAuctionConfigPropertyNames() {}

    /**
     * Value as obtained from aws account page
     */
    public static final String EMAIL_SENDER_AWS_ACCESS_KEY
            = "net.retakethe.policyauction.services.impl.EmailSenderImpl.aws-accessKey";

    /**
     * Value as obtained from aws account page
     */
    public static final String EMAIL_SENDER_AWS_SECRET_KEY
            = "net.retakethe.policyauction.services.impl.EmailSenderImpl.aws-secretKey";

    /**
     * Comma-separated <code>host:port,host:port</code> or just <code>host,host</code>
     */
    public static final String DAO_MANAGER_CASSANDRA_HOSTS
            = "net.retakethe.policyauction.data.impl.manager.DAOManagerImpl.cassandra-hosts";

    /**
     * As in Cassandra passwd.properties
     */
    public static final String DAO_MANAGER_CASSANDRA_USERNAME
            = "net.retakethe.policyauction.data.impl.manager.DAOManagerImpl.cassandra-username";

    /**
     * As in Cassandra passwd.properties
     */
    public static final String DAO_MANAGER_CASSANDRA_PASSWORD
            = "net.retakethe.policyauction.data.impl.manager.DAOManagerImpl.cassandra-password";
}
