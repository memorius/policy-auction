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

import org.apache.tapestry5.ioc.annotations.Inject;

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
    private final String accessKey;
    private final String secretKey;

    /**
     * Default constructor used by {@link AppModule#bind(org.apache.tapestry5.ioc.ServiceBinder)}
     *
     * @throws InitializationException
     */
    @Inject // This is the one to call from AppModule to register this as a service
    public EmailSenderImpl() {
        // TODO: get these from config properties at startup. See comments in AppModule.bind(ServiceBinder).
        accessKey = "_INSERT_ACCESS_KEY_";
        secretKey = "_INSERT_SECRET_KEY_";
    }

    /**
     * For standalone testing
     */
	public static void main(String args[]) {
		String sender = "michael@birks.co.nz"; // should be verified email

		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add("michael@michaelbirks.com"); // again a verified email, if you are in sandbox

		new EmailSenderImpl().sendMail(sender, recipients,
		        "Straight from AWS SES",
		        "Hey, did you know that this message was sent via Simple Email Service programmatically using AWS Java SDK.");
	}

	@Override
    public void sendMail(String sender, LinkedList<String> recipients, String subject, String body) {
		Destination destination = new Destination(recipients);
 
		Content subjectContent = new Content(subject);
		Content bodyContent = new Content(body);
		Body msgBody = new Body(bodyContent);
		Message msg = new Message(subjectContent, msgBody);
 
		SendEmailRequest request = new SendEmailRequest(sender, destination, msg);
 
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient(credentials);
		SendEmailResult result;
        try {
            result = sesClient.sendEmail(request);
        } catch (MessageRejectedException e) {
            // TODO: handle sensibly
            // What does this mean? sendEmail method documents it but doesn't say what it means.
            throw new RuntimeException(e);
        } catch (AmazonServiceException e) {
            // TODO: handle sensibly
            // Error response from service: server error, bad request data, etc
            throw new RuntimeException(e);
        } catch (AmazonClientException e) {
            // TODO: handle sensibly
            // Can't connect to service: network connection problem etc
            throw new RuntimeException(e);
        }

		System.out.println(result);
	}
}
