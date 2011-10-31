package net.retakethe.policyauction.entities;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Property;

public class ArticleItem {
	
	@Property
	private String id;
	
	@Property
	private String title;
	
	@Property
	private String summary;
	
	@Property
	private String content;
	
	@Property
	private Date added;
	
	@Property
	private Date closes;
	
	@Property
	private Integer ayes;
	
	@Property
	private Integer noes;
	
	@Property
	private List<CommentItem> comments;

	@Property
	private List<Tag> tags;
}
