package com.smart.rct.util;

public class RegexConstant {

	public static final String IPV6_IPV4 = "IP";
	public static final String ALPHA = "ALPHA";
	public static final String ALPHA_NUMERIC = "ALPHA_NUMERIC";

	public static final String NUMERIC = "NUMERIC";
	
	public static final String OTHER = "OTHERS";

	public static String DEFAULT_REGEX = "[\\w-_\\.+;(\\)://$#\\s\\W]*";

	public static String ALPHA_REGEX = "[a-zA-Z\\s_]*";

	public static String NUMERIC_REGEX = "[0-9]*";
	public static String ALPHAB_NUMERIC_REGEX = "[a-zA-Z0-9\\s_]*";

}
