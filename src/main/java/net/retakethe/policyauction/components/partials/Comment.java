package net.retakethe.policyauction.components.partials;

import java.util.List;

import net.retakethe.policyauction.entities.CommentItem;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;

public class Comment {
	
	@Parameter(allowNull = false, required = true, defaultPrefix = BindingConstants.PROP)
	private List<CommentItem> comments;

}
