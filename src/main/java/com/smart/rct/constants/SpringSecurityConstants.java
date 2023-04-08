package com.smart.rct.constants;

public class SpringSecurityConstants {
	// Roles
	public static final String ADMIN = "Admin";
	public static final String USER = "User";

	// Role Groups
	public static final String[] ACCESS_ALL = { "Admin", "User" };

	// Queries
	public static final String USERS_BY_USERNAME_QUERY = "SELECT USER_NAME, PASSWORD, STATUS FROM USER_DETAILS WHERE USER_NAME = ?";
	public static final String AUTHORITIES_BY_USERNAME_QUERY = "SELECT USER_NAME, ROLE FROM USER_DETAILS u, USER_ROLES r WHERE u.ROLE_ID = r.ID AND u.USER_NAME = ?";
}
