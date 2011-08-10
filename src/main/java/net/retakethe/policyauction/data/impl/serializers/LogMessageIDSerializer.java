package net.retakethe.policyauction.data.impl.serializers;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.impl.types.LogMessageIDImpl;

/**
 * @author Nick Clarke
 */
public class LogMessageIDSerializer extends AbstractUUIDSerializer<LogMessageID> {

    private static final LogMessageIDSerializer INSTANCE = new LogMessageIDSerializer();

    public static LogMessageIDSerializer get() {
        return INSTANCE;
    }

    @Override
    protected UUID toUUID(LogMessageID obj) {
        return ((LogMessageIDImpl) obj).getUUID();
    }

    @Override
    protected LogMessageID fromUUID(UUID uuid) {
        return new LogMessageIDImpl(uuid);
    }
}
