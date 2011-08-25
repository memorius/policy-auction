package net.retakethe.policyauction.data.api.exceptions;

/**
 * Runtime exception thrown if attempting to allocate votes or create policies,
 * when there aren't enough unallocated votes to do so.
 *
 * @author Nick Clarke
 */
public class InsufficientVotesException extends IllegalArgumentException {
    private static final long serialVersionUID = 0L;

    public InsufficientVotesException() {
    }

    public InsufficientVotesException(String s) {
        super(s);
    }

    public InsufficientVotesException(Throwable cause) {
        super(cause);
    }

    public InsufficientVotesException(String message, Throwable cause) {
        super(message, cause);
    }
}
