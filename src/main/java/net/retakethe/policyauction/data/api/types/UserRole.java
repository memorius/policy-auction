package net.retakethe.policyauction.data.api.types;

public enum UserRole {
	LOGIN,
	
	POLICY_CREATE,
	POLICY_EDIT,
	POLICY_DELETE,
	POLICY_MARK,
	POLICY_RETIRE,
	
	TAG_CREATE,
	TAG_EDIT,
	TAG_DELETE,
	
	COMMENT_CREATE_THREAD,
	COMMENT_CREATE_REPLY,
	COMMENT_MODERATE_EDIT,
	COMMENT_MODERATE_DELETE,
	
	USERS_EDIT,
	USERS_LOCK,
	
	ITEMS_REPORT,
	
	// The default user role (when not set at annotation)
	NONE,
};