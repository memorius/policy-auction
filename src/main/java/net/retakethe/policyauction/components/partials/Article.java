package net.retakethe.policyauction.components.partials;

import net.retakethe.policyauction.entities.ArticleItem;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;

public class Article {
	
	@Parameter(allowNull = false, required = true, defaultPrefix = BindingConstants.PROP)
	private ArticleItem item;
	
	@Parameter(allowNull = false, required = true, defaultPrefix = BindingConstants.PROP)
	private Boolean detailed;

}
