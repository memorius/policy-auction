/*
 * sample code from 
 * https://forums.aws.amazon.com/message.jspa?messageID=219406
 * 
 * as of 20110909, SES is still in the sandbox environment,
 * michael@birks.co.nz and michael@michaelbirks.com are both verified emails.
 */

package net.retakethe.policyauction.services;

import java.util.LinkedList;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
 
public class SendEmail
{
	public static final String ACCESS_KEY = "_INSERT_ACCESS_KEY_";
	public static final String SECRET_KEY = "_INSERT_SECRET_KEY_";

 
	public static void main(String args[]) {
		String sender = "michael@birks.co.nz"; // should be verified email
 
		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add("michael@michaelbirks.com"); // again a verified email, if you are in sandbox
 
		SendMail(sender, recipients, "Straight from AWS SES", "Hey, did you know that this message was sent via Simple Email Service programmatically using AWS Java SDK.");
	}
 
	public static void SendMail(String sender, LinkedList<String> recipients, String subject, String body) {
		Destination destination = new Destination(recipients);
 
		Content subjectContent = new Content(subject);
		Content bodyContent = new Content(body);
		Body msgBody = new Body(bodyContent);
		Message msg = new Message(subjectContent, msgBody);
 
		SendEmailRequest request = new SendEmailRequest(sender, destination, msg);
 
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient(credentials);
		SendEmailResult result = sesClient.sendEmail(request);
		
		System.out.println(result);
	}
}
