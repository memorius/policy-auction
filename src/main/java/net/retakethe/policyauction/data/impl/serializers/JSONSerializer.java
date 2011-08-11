package net.retakethe.policyauction.data.impl.serializers;

import org.apache.tapestry5.json.JSONObject;

/**
 * @author Nick Clarke
 */
public class JSONSerializer extends AbstractStringSerializer<JSONObject> {

    private static final JSONSerializer INSTANCE = new JSONSerializer();

    public static JSONSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private JSONSerializer() {}

    @Override
    protected String toString(JSONObject obj) {
        return obj.toCompactString();
    }

    @Override
    protected JSONObject fromString(String obj) {
        return new JSONObject(obj);
    }
}
