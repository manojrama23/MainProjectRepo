package com.smart.rct.constants;

public interface FaultCodes {
	public static final String SESSION_TIME_OUT = "1000";
	public static final String UNAUTHORIZED_ACCESS = "1011";
	public static final String CIQ_EXCEL_DUPLICATE = "2024";
	public static final String UPLOAD_SCRIPT_DUPLICATE = "9057";
	public static final String FAILED_TO_RETRIEVE_USER_LIST = "2000";
	public static final String USER_CREATION_FAILED = "2001";
	public static final String USER_UPDATION_FAILED = "2002";
	public static final String USER_DELETION_FAILED = "2003";
	public static final String USER_EXISTS = "2006";
	public static final String ROLE_EXISTS = "2007";
	// public static final String LOCATION_EXISTS = "2008";
	
	// public static final String INVALID_USER = "2011";
	public static final String UNABLE_TO_MAIL_PASSWORD = "2012";
	
	public static final String UNABLE_TO_DELETE_USER = "2015";
	public static final String EMAIL_ALREADY_USED = "2017";
	public static final String ADMIN_CANNOT_BE_DELETED = "2021";
	public static final String ADMIN_ROLE_CANNOT_BE_CHANGED = "2022";
	public static final String ADMIN_STATUS_CANNOT_BE_CHANGED = "2023";
	
	public static final String UPLOADED_FILE_EMPTY = "3001";
	public static final String FAILED_TO_UPLOAD_FILE = "3002";
	public static final String FAILED_TO_UPLOAD_FILE_WITH_OUT_ENODE_B = "1012";
	
	public static final String FAILED_TO_UPLOAD_NO_CONTENT = "1013";
	public static final String FAILED_TO_UPDATE_SCHEDULING_TEMPLATE = "1014";
	//Login Details
	public static final String USER_BLOCKED = "2030";
	public static final String INVALID_USER_OR_PASSWORD = "2031";
	public static final String USER_LOGIN_FAILED = "2032";
	public static final String NOT_A_REGISTERED_EMAIL = "2033";
	public static final String UNABLE_TO_READ_PROPERTIES = "2034";
	public static final String UNABLE_TO_AUTHENTICATE = "2035";
	public static final String UNABLE_TO_MESSAGE = "2036";
	public static final String EMAIL_SESSION_TIME_OUT = "2037";
	public static final String PASSWORD_RESET_FAILED = "2038";
	public static final String UNABLE_TO_CHANGE_PASSWORD = "2039";
	public static final String INVALID_PASSWORD = "2040";
	public static final String USER_LOGOUT_FAILED = "2041";
	public static final String USER_LOGIN_BLOCKED = "2042";

	//User Details
	public static final String CREATION_FAILED = "3001";
	public static final String USERNAME_EXIST = "3002";
	public static final String CREATED_SUCCESSFULLY = "3003";
	public static final String UPDATION_FAILED = "3004";
	public static final String UPDATED_SUCCESSFULLY = "3005";
	public static final String GET_LIST_FAILED = "3006";
	public static final String DELETION_FAILED = "3007";
	public static final String DELETED_SUCCESSFULLY = "3008";
	public static final String FAILED_DBCOUNT = "3009";
	
	//Role Details
	public static final String ROLE_EXIST = "4001";
	public static final String CREATE_ROLE_FAILED = "4002";
	public static final String CREATED_ROLE_SUCCESSFULLY = "4003";
	public static final String ROLE_UPDATE_FAILED = "4004";
	public static final String ROLE_UPDATED_SUCCESSFULLY = "4005";
	public static final String ROLE_DELETION_FAILED = "4006";
	public static final String ROLE_DELETED_SUCCESSFULLY = "4007";
	public static final String GET_ROLE_FAILED = "4008";
	
	//Audit Details
	public static final String FAILED_TO_SAVE_AUDIT_DETAILS = "6001";
	public static final String FAILED_TO_UPDATE_AUDIT_DETAILS = "6002";
	public static final String FAILED_TO_DELETE_AUDIT_DETAILS = "6003";
	public static final String FAILED_TO_GET_AUDIT_DETAILS = "6004";
	public static final String FAILED_TO_FILTER_AUDIT_DETAILS = "6005";
	public static final String AUDIT_DETAILS_SAVED_SUCCESSFULLY = "6006";
	public static final String AUDIT_DETAILS_UPDATED_SUCCESSFULLY = "6007";
	public static final String AUDIT_DETAILS_DELETED_SUCCESSFULLY = "6008";

	//Misc Configuration
	public static final String FAILED_TO_READ_CONFIGURATION_DETAILS = "7001";
	public static final String FAILED_TO_EDIT_CONFIGURATION_DETAILS = "7002";
	
	//Network Details
	public static final String NETWORK_TYPE_SAVED_SUCCESSFULLY = "7003";
	public static final String FAILED_TO_SAVE_NETWORK_TYPE = "7004";
	public static final String FAILED_TO_GET_NETWORK_TYPE_DETAILS = "7005";
	public static final String FAILED_TO_DELETE_NETWOTK_TYPE = "7006";
	public static final String NETWOTK_TYPE_DELETED_SUCCESSFULLY = "7007";
	public static final String NETWORK_TYPE_EXIST = "9025";
	
	//Customer Details
	public static final String FAILED_TO_GET_CUSTOMER_LIST = "7008";
	public static final String FAILED_TO_ADD_CUSTOMER_DETAILS = "7009";
	public static final String FAILED_TO_SAVE_CUSTOMER_DETAILS = "7010";
	public static final String FAILED_TO_DELETE_CUSTOMER = "7011";
	public static final String FAILED_TO_DELETE_CUSTOMER_DETAILS = "7012";
	public static final String CUSTOMER_SAVED_SUCCESSFULLY = "7013";
	public static final String CUSTOMER_DETAILS_SAVED_SUCCESSFULLY = "7014";
	public static final String DELETED_CUSTOMER_DETAILS_SUCCESSFULLY = "7015";
	public static final String DELETED_CUSTOMER_SUCCESSFULLY = "7016";
	public static final String CUSTOMER_ICON_SAVED_SUCCESSFULLY = "7017";
	public static final String FAILED_TO_UPDATE_CUSTOMER_ICON = "7018";
	public static final String CUSTOMER_NAME_EXIST = "9028";
	
	//LSM Details
	public static final String FAILED_TO_ADD_NWCONFIG_DETAILS = "8001";
	public static final String FAILED_TO_UPDATE_NWCONFIG_DETAILS = "8002";
	public static final String FAILED_TO_GET_NWCONFIG_DETAILS = "8003";
	public static final String FAILED_TO_DELETE_NWCONFIG_DETAILS = "8004";
	public static final String FAILED_TO_UPLOAD_NWCONFIG_DETAILS = "8005";
	public static final String NWCONFIG_DETAILS_UPDATED_SUCCESSFULLY = "8006";
	public static final String NWCONFIG_DETAILS_DELETED_SUCCESSFULLY = "8007";
	public static final String NWCONFIG_DETAILS_UPLOADED_SUCCESSFULLY = "8008";
	public static final String NWCONFIG_DETAILS_CREATED_SUCCESSFULLY = "8009";
	public static final String REQ_INFO_NOT_FOUND = "8010";
	public static final String NWCONFIG_DETAILS_ALREADY_EXIST = "9032";
	public static final String NWCONFIG_DETAILS_ZIP_NOT_AVAILABLE="8012";
	public static final String DELETED_NWCONFIG_DETAILS_SUCCESSFULLY = "9052";
	public static final String FAILED_TO_DELETE_NWCONFIG_SERVER_DETAILS = "9053";
	
	//CSV Details
	public static final String FAILED_TO_GENERATE_FILE = "9001";
	public static final String FAILED_TO_GET_CSV_AUDIT_DETAILS = "9002";
	public static final String GENERATED_CSV_SUCCESSFULLY = "9003";
	public static final String FAILED_TO_DELETE_CSV_DETAILS = "9095";
	public static final String CSV_DETAILS_DELETED_SUCCESSFULLY = "9096";
	
	
	//CIQ Details
	public static final String FAILED_TO_UPLOAD_CIQ_FILE = "9004";
	public static final String FAILED_TO_GET_CIQ_AUDIT_DETAILS = "9005";
	public static final String FAILED_TO_UPDATE_CIQ_AUDIT_DETAILS = "9006";
	public static final String FAILED_TO_DELETE_CIQ_DETAILS = "9007";
	public static final String FAILED_TO_GET_CIQ_DETAILS = "9008";
	public static final String FAILED_TO_RETRIVE_CIQ_DETAILS = "9009";
	public static final String FAILED_TO_UPDATE_CIQ_DETAILS = "9010";
	public static final String FAILED_TO_CREATE_CIQ_DETAILS = "9011";
	public static final String FAILED_TO_DELETE_CIQ_ROW_DETAILS = "9012";
	public static final String CIQ_FILE_UPLOADED_SUCCESSFULLY = "9013";
	public static final String CIQ_AUDIT_DETAILS_UPDATED_SUCCESSFULLY = "9014";
	public static final String DELETED_CIQ_DETAILS_SUCCESSFULLY = "9015";
	public static final String UPDATED_CIQ_DETAILS_SUCCESSFULLY = "9016";
	public static final String CREATED_CIQ_DETAILS_SUCCESSFULLY = "9017";
	public static final String DELETED_CIQ_ROW_DETAILS_SUCCESSFULLY = "9018";
	public static final String UPDATED_CIQ_ENB_DETAILS_SUCCESSFULLY = "9019";
	public static final String FAILED_TO_GET_CIQ_SHEET_DETAILS = "9090";
	public static final String CREATED_CHECKLIST_DETAILS_SUCCESSFULLY = "9091";
	public static final String FAILED_TO_CREATE_CHECKLIST_DETAILS = "9092";
	public static final String FAILED_TO_DELETE_CHECKLIST_ROW_DETAILS = "9093";
	public static final String PROVIDE_PROGRAM_TEMPLATE_DETAILS = "9094";
	public static final String PROVIDE_CHECKLIST_PROGRAM_TEMPLATE_DETAILS = "9098";
	
	//NE Mapping
	
	public static final String NE_MAPPING_DETAILS_UPDATED_SUCCESSFULLY = "9097";
	public static final String FAILED_TO_UPDATE_NE_MAPPING_DETAILS = "9099";
	public static final String FAILED_TO_GET_NE_MAPPING_DETAILS = "9115";
	public static final String FAILED_TO_SAVE_NE_MAPPING_DETAILS = "9116";
	
	//EnB Details
	public static final String FAILED_TO_GET_ENB_DETAILS = "9020";
	public static final String FAILED_TO_GET_ENB_INFO = "9021";
	public static final String FAILED_TO_GET_ENB_TABLE_INFO = "9022";
	public static final String FAILED_TO_GET_ENB_DETAILS_FILENAME = "9023";
	public static final String FAILED_TO_UPDATE_CIQ_ENB_DETAILS = "9024";
	public static final String FAILED_TO_GET_NE_COMMISSION_DETAILS = "9033";
	public static final String FAILED_TO_GENERATE_ENB_FILES= "9034";
	public static final String FAILED_TO_GENERATE_CSV_FILES= "9039";

	
	public static final String FAILED_TO_GET_PRE_GROW_DETAILS = "9035";
	public static final String FAILED_PRE_GROW = "9029";
	public static final String AUTH_FAIL = "9030";
	public static final String FILE_NOT_FOUND = "9031";
	public static final String FAILED_TO_GET_ENB_MAP_DETAIL = "9032";

	//Scheduling
	public static final String SCHEDULING_DETAILS_CREATED_SUCCESSFULLY = "9050";
	public static final String FAILED_TO_ADD_SCHEDULING_DETAILS = "9051";
	public static final String NE_VERSION_DETAILS_ALREADY_EXIST = "9054";
	public static final String DELETED_NE_VERSION_DETAILS_SUCCESSFULLY = "9055";
	public static final String FAILED_TO_DELETE_NE_VERSION_DETAILS = "9056";
	
	public static final String SCRIPT_FILE_DUPLICATE = "9057";
	public static final String CHECKLIST_FILE_DUPLICATE = "9058";
	public static final String FAILED_TO_UNZIP_SCRIPT_FILE = "9059";
	
	public static final String NETWORK_TYPE_DETAILS_ASSOSIATED = "9060";
	public static final String CUSTOMER_DETAILS_ASSOSIATED = "9061";
	public static final String PROGRAM_NAME_ALREADY_USED = "9062";
	public static final String CIQ_AUDIT_DETAILS_NOT_FOUND = "9063";
	public static final String CIQ_FILES_FETCHED_SUCCESSFULLY = "9064";
	
	
	//ChekList
	public static final String FAILED_TO_UPLOAD_CHECKILST_FILE = "9070";
	public static final String FAILED_TO_FETCH_CIQ_FILE = "9071";
	public static final String FETCH_CIQ_NOT_FOUND = "9072";
	public static final String FETCH_SCRIPT_NOT_FOUND = "9073";
	public static final String FETCH_CHECK_LIST_NOT_FOUND = "9074";
	public static final String CIQ_FILE_FETCHED_SUCCESSFULLY = "9075";
	public static final String FETCH_CIQ_TEMPLATE_NOT_FOUND = "9076";
	public static final String GENERATE_ACTIVE_FILES_NOT_FOUND = "9077";	
	
	public static final String GENERATED_ENV_SUCCESSFULLY = "9078";
	public static final String GENERATED_COMM_SCRIPT_SUCCESSFULLY = "9079";
	public static final String FAILED_TO_GET_PROGRAMS_LIST = "9080";
	public static final String GENERATED_AUDIT_DETAILS_UPDATED_SUCCESSFULLY = "9081";
	public static final String FAILED_TO_UPDATE_GENERATED_AUDIT_DETAILS = "9082";
	
	public static final String NE_MAPPING_NOT_FOUND = "9083";
	public static final String FAILED_TO_EXPORT_NETWORK_CONFIG = "9084";
	public static final String DUPLICATE_STEPS_FOUND= "9085";
	public static final String PROGRAM_NAME_NOT_ASSOCIATED_WITH_USER = "9086";
	
	public static final String NE_VERSION_DETAILS_ASSOSIATED = "9087";
	public static final Object PACK_SITE_DATA_SUCCESSFULLY = "9088";
	public static final Object FAILED_TO_PACK_SITE_DATA = "9089";
	
	public static final Object SITE_DATA_DETAILS_UPDATED_SUCCESSFULLY = "9100";
	public static final Object FAILED_TO_UPDATE_SITE_DATA_DETAILS = "9101";
	public static final Object FAILED_TO_DELETE_SITE_DATA_DETAILS ="9102";
	public static final Object SITE_DATA_DETAILS_DELETED_SUCCESSFULLY = "9103";
	public static final Object FAILED_TO_GET_SITE_DATA_DETAILS = "9104";
	
	public static final Object FAILED_TO_SEND_MAIL ="9105";
	public static final Object MAIL_SENT_SUCCESSFULLY  ="9106";
	public static final Object MAIL_ATTACHMENT_NOT_FOUND ="9107";
	public static final String CHECKLIST_FILE_NOT_FOUND = "9108";
	public static final String FAILED_TO_UPLOAD_SCRIPT_FILE = "9109";
	public static final Object FAILED_TO_SEND_VBS_MAIL  ="9110";
	
	public static final String FAILED_TO_GENERATE_BASH_FILE_MISSING_MCMA_IP = "9111";
	public static final String FILES_GENERATE_SUCCESSFULLY= "9112";
	
	public static final String  FAILED_TO_GENERATE_BASH_FILE= "9113";
	public static final String  FAILED_TO_GENERATE_CLI_FILE= "9114";
	public static final String  FAILED_TO_GENERATE_CMD_SYS_FILE= "9115";
	public static final String  FAILED_TO_GENERATE_CONFD_CLI_FILE= "9116";
	public static final String  FAILED_TO_GENERATE_EXPECT_FILE= "9117";
	
	public static final String SCRIPT_NAME_ALREADY_USED = "9117";
	public static final String FAILED_TO_SAVE_CHECKLIST_SCRIPT_DETAILS = "9118";
	public static final String FAILED_TO_GET_CHECKLIST_SCRIPT_DETAILS = "9119";
	public static final String EXE_SEQ_ALREADY_EXIST = "9120";
	public static final String SCRIPT_NAME_ALREADY_EXIST = "9121";
	public static final Object SCRIPT_SEQ_DETAILS_SAVED_SUCCESSFULLY  ="9122";
	
	public static final String CHECKLIST_SCRIPT_DETAILS_EMPTY_PROGRAM = "9123";
	public static final String CHECKLIST_SCRIPT_DETAILS_EMPTY_FILE = "9124";
	public static final String CHECKLIST_SCRIPT_DETAILS_DUPLICATE = "9125";
	
	
	public static final String FAILED_TO_REPORT_DETAILS = "9126";
	public static final String REQ_RECORDS_NOT_FOUND = "9127";
	public static final String FAILED_TO_DELETE_COMMANDRULE = "9128";
	public static final String FAILED_TO_DELETE_FILERULE = "9129";
	public static final String DUPLICATE_SCRIPT_EXEC_SEQ = "9130";
	public static final String TEST_NAME_ALREADY_EXISTS = "9131";
	public static final String COULD_NOT_CONNECT = "9132";
	public static final String CANNOT_INITIATE_EXECUTION = "9133";
	public static final String UPLOAD_SCRIPT_EMPTY = "9134";
	public static final String TEST_NAME_PRESENT = "9135";
	public static final String RESULT = "9136";
	public static final String LOAD_FAILED = "9137";
	public static final String FAILED_TO_DELETE_UPLOADSCRIPT = "9138";
	public static final String WRONG_PROMPT_TEMPLATE_JSON = "9139";
	public static final String FILE_PROCESSES_SUCCESSFULL = "9140";
	public static final String LSMNAME_LSMVERSION_NETWORKTYPE_EXISTS = "9141";
	public static final String  SUCCESSFULLY_CREATED_FILE = "9142";
	public static final String FAILED_MAP_DETAILS = "9143";
	
	public static final String  FAILED_TO_GENERATE_BASH_FILE_MISSING_MSMA_IP="9144";
	public static final String  FAILED_TO_GENERATE_VBS_FILE= "9145";
	
	public static final String FILE_DETAILS_SAVED_SUCCESSFULLY = "9200";
	
	public static final String FAILED_TO_EXPORT_AUDIT_CRITICAL_PARAMS_REPORT = "9201";

}
