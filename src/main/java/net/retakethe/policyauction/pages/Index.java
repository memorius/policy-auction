package net.retakethe.policyauction.pages;

import java.util.Date;

import net.retakethe.policyauction.annotations.PublicPage;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.services.SendEmail;

import org.apache.tapestry5.annotations.SessionState;

/**
 * Start page of application policy-auction.
 */
@PublicPage
public class Index {
	@SessionState(create=false)
	private User currentUser;
	
	private boolean currentUserExists;
	
	
	public Date getCurrentTime() 
	{ 
		return new Date(); 
	}
	
	public String getUsername() {
		if (currentUserExists) {
			return currentUser.getUsername();
		} else {
			return "";
		}
	}
	
	/**
	 * test text insert from Michael
	 * @return String
	 * @deprecated
	 */
	@Deprecated
	public String getTestInsert() 
	{ 
		return new String("Text insert from Michael"); 
	}
	
	/**
	 * basic function to call the SendEmail class
	 * The idea is that if I can call it here to send a simple "Straight from AWS SES"
	 * email, that will prove functionality, and then we can extend the basic class to
	 * send whatever content to whoever.
	 * 
	 * This _seems_ to pick up the SendEMail class, but I haven't been able to
	 * track down what's happening there.
	 * - I cannot find the output from System.out.println(result) being called in SendEmail.SendMail()
	 * - I've attempted to convert the methods in SendEMail from 'void' output to String, and call the
	 *   toString() on the SendEmailResult object and passing it up the chain to echo in the browser
	 *   with no luck.
	 *   
	 * It could be as simple as me not calling it properly and so there being no results to pass around.
	 * 
	 * For what it's worth, the perl test files work fine in sending email.
	 */
	public String getEmailTest()
	{
		new SendEmail();
		return new String("Has an Email been sent?");
	}
}
