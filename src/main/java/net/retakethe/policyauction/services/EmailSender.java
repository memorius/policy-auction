package net.retakethe.policyauction.services;

import java.util.List;


public interface EmailSender {

    public static class EmailNotSentException extends Exception {
        private static final long serialVersionUID = 0L;

        public EmailNotSentException(String message) {
            super(message);
        }

        public EmailNotSentException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Send an email message.
     *
     * @param recipients
     * @param subject
     * @param body
     *
     * @throws EmailNotSentException for all failures
     */
    void sendMail(List<String> recipients, String subject, String body)
            throws EmailNotSentException;
}
