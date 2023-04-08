package com.smart.rct.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class AuditConstants {
	public static final String PROGRAMNAME_4G_USM_LIVE = "4G-USM-LIVE";
	public static final String PROGRAMNAME_4G_FSU = "4G-FSU";
	public static final String PROGRAMNAME_5G_CBAND = "5G-CBAND";
	public static final String PROGRAMNAME_5G_DSS = "5G-DSS";
	
	public static final String BANDNAME_AWS = "AWS";
	public static final String BANDNAME_PCS = "PCS";
	public static final String BANDNAME_700 = "700";
	public static final String BANDNAME_850 = "850";
	public static final String BANDNAME_CBRS = "CBRS";
	public static final String BANDNAME_LAA = "LAA";
	
	public static final String BANDWIDTH_5MHZ = "5MHz";
	public static final String BANDWIDTH_10MHZ = "10MHz";
	public static final String BANDWIDTH_15MHZ = "15MHz";
	public static final String BANDWIDTH_20MHZ = "20MHz";
	
	public static final String OCNS_TEST_WAIT = "OCNS_TEST_WAIT(SECONDS)";
	public static final String OCNS_TEST_RSSIDIFF = "OCNS_TEST_RSSIDIFF(dB)";
	public static final String OCNS_TEST_PATHDIFF = "OCNS_TEST_PATHDIFF(dB)";
	
	public static final String RSSI_TEST_RSSIDIFF = "RSSI_TEST_RSSIDIFF(dB)";
	public static final String RSSI_TEST_PATHDIFF = "RSSI_TEST_PATHDIFF(dB)";
	public static final String RSSI_TEST_WAIT = "RSSI_TEST_WAIT(SECONDS)";
	public static final String RSSI_TEST_LENGTH = "RSSI_TEST_LENGTH(INT)";
	
	public static final String FSU_RELEASE_VERSION = "FSU_RELEASE_VERSION";
	
	public static final String CBAND_TWAMP_WAIT = "CBAND_TWAMP_WAIT(SECONDS)";
	
	public static final String FSU_USERNAME = "FSU_UserName";
	public static final String FSU_PASSWORD = "FSU_Password";
	public static final String FSU_SSH_LOGIN_TIMEOUT = "FSU_SSH_LOGIN_TIMEOUT(SECONDS)";
	public static final String FSU_EXPECT_1 = "FSU_EXPECT_1";
	public static final String FSU_EXPECT_2 = "FSU_EXPECT_2";
	public static final String FSU_EXPECT_3 = "FSU_EXPECT_3";
	public static final String FSU_EXPECT_4 = "FSU_EXPECT_4";
	public static final String FSU_EXPECT_5 = "FSU_EXPECT_5";
	public static final String FSU_EXIT_COMMAND1 = "FSU_EXIT_COMMAND1";
	public static final String FSU_EXIT_COMMAND2 = "FSU_EXIT_COMMAND2";
	public static final String FSU_EXPECT_DELAY = "FSU_EXPECT_DELAY(SECONDS)";
	
	public static final String FSU_TYPE_10 = "FSU_TYPE_10";
	public static final String FSU_TYPE_20 = "FSU_TYPE_20";
	
	public static final String USECASE_5G_CBAND = "USECASE_5G_CBAND";
	public static final String USECASE_5G_DSS = "USECASE_5G_DSS";
	
	//CBand mmu audit
	public static final String CBand_USERNAME = "CBand_UserName";
	public static final String CBand_PASSWORD = "CBand_Password";
	public static final String CBand_SSH_LOGIN_TIMEOUT = "CBand_SSH_LOGIN_TIMEOUT(SECONDS)";
	public static final String CBand_EXIT_COMMAND1 = "CBand_EXIT_COMMAND1";
	public static final String CBand_EXIT_COMMAND2 = "CBand_EXIT_COMMAND2";
	public static final String CBand_EXPECT_DELAY = "CBand_EXPECT_DELAY(SECONDS)";
	public static final String CBand_EXPECT_1 = "CBand_EXPECT_1";
	public static final String CBand_EXPECT_2 = "CBand_EXPECT_2";
	public static final String CBand_EXPECT_3 = "CBand_EXPECT_3";
	public static final String CBand_EXPECT_4 = "CBand_EXPECT_4";
	public static final String CBand_EXPECT_5 = "CBand_EXPECT_5";
	public static final String CBand_PATH = "CBand_PATH";
	
	public static final String TXPOWERUPPERLIMIT = "DSS_TXPowerUL";
	public static final String TXPOWELOWERLIMIT = "DSS_TXPowerLL";
	public static final String RXPOWERUPPERLIMIT = "DSS_RXPowerUL";
	public static final String RXPOWELOWERLIMIT = "DSS_RXPowerLL";
	
	//RSSI audit
	public static final String RSSI_USERNAME = "RSSI_UserName";
	public static final String RSSI_PASSWORD = "RSSI_Password";
	public static final String RSSI_SSH_LOGIN_TIMEOUT = "RSSI_SSH_LOGIN_TIMEOUT(SECONDS)";
	public static final String RSSI_EXIT_COMMAND1 = "RSSI_EXIT_COMMAND1";
	public static final String RSSI_EXIT_COMMAND2 = "RSSI_EXIT_COMMAND2";
	public static final String RSSI_EXPECT_DELAY = "RSSI_EXPECT_DELAY(SECONDS)";
	public static final String RSSI_EXPECT_1 = "RSSI_EXPECT_1";
	public static final String RSSI_EXPECT_2 = "RSSI_EXPECT_2";
	public static final String RSSI_PATH = "RSSI_PATH";
	public static final String RSSI_FILE_NAME = "RSSI_FILE_NAME";
	public static final String RSSI_OUTPUT = "RSSI_OUTPUT";
	
	public static final String CBAND_MH1_IP_PATH = "CBAND_MH1_IP_PATH";
	public static final String CBAND_MH1_IP_TIMEOUT = "CBAND_MH1_IP_TIMEOUT";
	public static final String CBAND_MH1_IP_EXPECT1 = "CBAND_MH1_IP_EXPECT1 ";
	public static final String CBAND_MH1_IP_EXPECT2 = "CBAND_MH1_IP_EXPECT2";
	public static final String CBAND_MH1_IP_EXPECT3 = "CBAND_MH1_IP_EXPECT3";
	public static final String CBAND_MH1_IP_EXPECT_DELAY = "CBAND_MH1_IP_EXPECT_DELAY";
	public static final String CBAND_MH1_IP_EXIT = "CBAND_MH1_IP_EXIT";
	
	public static final String CBAND_MMU_REPORTING_PATH = "CBAND_MMU_REPORTING_PATH";
	public static final String CBAND_MMU_REPORTING_TIMEOUT = "CBAND_MMU_REPORTING_TIMEOUT";
	public static final String CBAND_MMU_REPORTING_DELAY = "CBAND_MMU_REPORTING_DELAY";
	public static final String CBAND_MMU_REPORTING_CDPATH = "CBAND_MMU_REPORTING_CDPATH";
	
	public static final String CBAND_MMU_SERIAL_NUMBER_API = "CBAND_MMU_SERIAL_NUMBER_API";
	public static final String USM_SERIAL_NUMBER_API = "USM_SERIAL_NUMBER_API";
	
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX1_HEADERS =  Arrays.asList("cell-identity", "spectrum-sharing", "slot-level-operation-mode", "cell-num", "user-label", "dl-antenna-count", "ul-antenna-count", "number-of-rx-paths-per-ru", "cell-path-type", "administrative-state", "operational-state", "activation-state", "power");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX1_INDEXES =  Arrays.asList("cell-num", "cell-identity");

	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX2_HEADERS =  Arrays.asList("unit-type", "unit-id", "port-id", "tx-power", "rx-power", "connected-du-cpri-port-id", "connected-enb-digital-unit-board-id", "connected-enb-digital-unit-port-id", "du-cpri-port-mode", "pri-port-mode", "hardware-name", "mplan-ipv6", "enb-ne-id", "pri-port-mode", "connected-digital-unit-board-id", "radio-unit-port-id", "software-vendor-name", "firmware-name", "package-version", "patch-version", "software-name", "software-version", "cpri-speed-running", "tx-wavelength");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX2_INDEXES =  Arrays.asList("unit-type", "unit-id", "connected-digital-unit-board-id");

	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX3_HEADERS =  Arrays.asList("ne-id", "ne_type", "sw-version","flavor-id", "ip-address", "f1-app-state");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX3_INDEXES =  Arrays.asList("ne-id", "flavor-id");

	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX4_HEADERS =  Arrays.asList("ne_id", "alarm-unit-type", "alarm-type");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX4_INDEXES =  Arrays.asList("ne_id", "alarm-unit-type");
	
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX5_HEADERS =  Arrays.asList("fsu-id", "support-cell-number", "connected-pod-type", "connected-pod-id","connected-pod-port-id", "connected-pod-id	vlan-id");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX5_INDEXES =  Arrays.asList("fsu-id", "support-cell-number");

	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX6_HEADERS =  Arrays.asList("pod-id", "dss", "ip", "pod-type", "snc-state", "gateway", "mtu", "sctp-state");
	public static final List<String> AUDIT_CRITICAL_PARAMS_INDEX6_INDEXES=  Arrays.asList("pod-id","pod-type");
	
	public static final List<String> AUDIT_CRITICAL_PARAMS_SFP_HEADERS =  Arrays.asList("unit-type", "unit-id", "port-id", "tx-power", "rx-power", "connected-du-cpri-port-id", "connected-enb-digital-unit-board-id", "connected-enb-digital-unit-port-id", "du-cpri-port-mode", "pri-port-mode", "hardware-name", "mplan-ipv6", "enb-ne-id", "pri-port-mode", "connected-digital-unit-board-id", "radio-unit-port-id", "vendor-name", "firmware-name", "package-version", "patch-version", "software-name", "software-version", "cpri-speed-running", "tx-wavelength");
	public static final List<String> AUDIT_CRITICAL_PARAMS_SFP_INDEXES=  Arrays.asList("unit-type", "unit-id", "port-id");

}
