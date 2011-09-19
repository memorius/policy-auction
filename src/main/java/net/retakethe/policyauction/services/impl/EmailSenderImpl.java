/*
 * sample code from 
 * https://forums.aws.amazon.com/message.jspa?messageID=219406
 * 
 * as of 20110909, SES is still in the sandbox environment,
 * michael@birks.co.nz and michael@michaelbirks.com are both verified emails.
 */

package net.retakethe.policyauction.services.impl;

import java.util.LinkedList;

import net.retakethe.policyauction.data.impl.manager.InitializationException;
import net.retakethe.policyauction.services.AppModule;
import net.retakethe.policyauction.services.EmailSender;
import net.retakethe.policyauction.services.config.PolicyAuctionConfigPropertyNames;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
 
public class EmailSenderImpl implements EmailSender
{
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

    private final String accessKey;
    private final String secretKey;
    private final boolean configured;

    /**
     * Constructor used by {@link AppModule#bind(org.apache.tapestry5.ioc.ServiceBinder)}
     *
     * @throws InitializationException
     */
    @Inject // This is the one to call from AppModule to register this as a service
    public EmailSenderImpl(
            // These values come from web.xml (or tomcat context config) <context-param> settings.
            @Inject @Symbol(PolicyAuctionConfigPropertyNames.EMAIL_SENDER_AWS_ACCESS_KEY) final String accessKey,
            @Inject @Symbol(PolicyAuctionConfigPropertyNames.EMAIL_SENDER_AWS_SECRET_KEY) final String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;

        if ("DUMMY_PLACEHOLDER_VALUE".equals(this.accessKey)) {
            this.configured = false;
            logNotConfiguredWarning();
        } else if ("DUMMY_PLACEHOLDER_VALUE".equals(this.secretKey)) {
            this.configured = false;
            logNotConfiguredWarning();
        } else {
            this.configured = true;
        }
    }

    private void logNotConfiguredWarning() {
        logger.warn(getClass().getName() + " is not configured, no emails will be sent. "
                + "To fix this, set <context-params> in web.xml or tomcat context config.");
    }

	@Override
    public void sendMail(String sender, LinkedList<String> recipients, String subject, String body)
	        throws EmailNotSentException {
	    if (!configured) {
	        String message = "Not sending email because " + getClass().getName() + " is not configured";
	        logger.debug(message);
	        throw new EmailNotSentException(message);
	    }

		Destination destination = new Destination(recipients);
 
		Content subjectContent = new Content(subject);
		Content bodyContent = new Content(body);
		Body msgBody = new Body(bodyContent);
		Message msg = new Message(subjectContent, msgBody);
 
		SendEmailRequest request = new SendEmailRequest(sender, destination, msg);
 
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient(credentials);
		SendEmailResult result;
		// TODO: if there are problems they may be temporary; we could keep emails in Cassandra and retry later.
        try {
            result = sesClient.sendEmail(request);
            logger.debug("Sent email ok: " + result);
        } catch (MessageRejectedException e) {
            // What does this mean? sendEmail method documents it but doesn't say what it means.
            throw new EmailNotSentException(e);
        } catch (AmazonServiceException e) {
            // Error response from service: server error, bad request data, etc
            throw new EmailNotSentException(e);
        } catch (AmazonClientException e) {
            // Can't connect to service: network connection problem etc
            throw new EmailNotSentException(e);
        }
	}
}
