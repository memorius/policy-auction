package net.retakethe.policyauction.data.impl.manager;

public class InitializationException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public InitializationException() {
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
