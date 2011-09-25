package net.retakethe.policyauction.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class Problem {
	
	@Persist
	@Property
	private String titleMessage;

	@Persist
	@Property
	private String errorMessage;
	
	public void setup(final String titleMessage, final String errorMessage){
		this.titleMessage = titleMessage;
		this.errorMessage = errorMessage;
	}

}
