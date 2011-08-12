package net.retakethe.policyauction.util;

/**
 * @author Nick Clarke
 */
public final class AssertArgument {
    private AssertArgument() {}

    /**
     * Check object is non-null else throw IllegalArgumentException(argName + " must not be null")
     *
     * @param object the argument to be checked for non-nullness
     * @param argName to show in the IllegalArgumentException message if object is null
     */
    public static void notNull(Object object, String argName) {
        if (object == null) {
            throw new IllegalArgumentException(argName + " must not be null");
        }
    }

    /**
     * Check condition is true else throw IllegalArgumentException(message + ": got '" + value + "'")
     *
     * @param condition the condition to check
     * @param message to throw in the IllegalArgumentException
     * @param value to be added to IllegalArgumentException message if condition fails
     */
    public static void isTrue(boolean condition, String message, Object value) {
        if (!condition) {
            throw new IllegalArgumentException(message + ": got '" + value + "'");
        }
    }
}
