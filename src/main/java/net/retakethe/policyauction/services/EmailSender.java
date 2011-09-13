package net.retakethe.policyauction.services;

import java.util.LinkedList;

public interface EmailSender {

    /**
     * Send an email message.
     *
     * @param sender
     * @param recipients
     * @param subject
     * @param body
     */
    void sendMail(String sender, LinkedList<String> recipients, String subject, String body);
}
