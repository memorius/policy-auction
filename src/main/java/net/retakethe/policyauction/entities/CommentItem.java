package net.retakethe.policyauction.entities;

import org.apache.tapestry5.annotations.Property;

public class CommentItem {
	
	@Property
	private String id;
	
	@Property
	private String comment;

	@Property
	private UserItem user;
}
