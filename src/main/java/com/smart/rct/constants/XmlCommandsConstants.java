package com.smart.rct.constants;

import java.util.concurrent.ConcurrentHashMap;

public class XmlCommandsConstants {

	public static final String EARFCN_CELL_FDD_TDD_EARFCN_UL = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-frequency-relation eutran-fa-information eutran-fa-prior-info-func earfcn-ul | display xml";
	public static final String EARFCN_CELL_FDD_TDD_PRIORITY = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-frequency-relation eutran-fa-information eutran-fa-prior-info-func priority | display xml";

	public static final String ENB_FUNCTION_EARFCN_EUTRAN_CELL_CONF_IDLE = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-cell-conf-idle | display xml";

	public static final String ENB_FUNCTION_EARFCN_CELL_DL_TOTAL_POWER = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-cell-info cell-dl-total-power | display xml";

	public static final String ENB_FUNCTION_EARFCN_CELL_PLMN_INFO = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cell-plmn-info | display xml";

	public static final String ENB_FUNCTION_TRAFFIC_MANAGE_FUN_CELL_CONTROL = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cell-son cell-load-balancing traffic-manage-func-cell-control | display xml";

	public static final String ENB_FUNCTION_PRACH_CONFIG_LOGIC = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd scheduling-config physical-resource-config prach-config-logic | display xml";
	
	public static final String ENB_FUNCTION_CALL_TRACE_CSL_INFO = "show managed-element enb-function call-trace csl-info | display xml";
	
	public static final String ENB_FUNCTION_CELL_CALL_COUNT = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cell-call-count-status | display xml";
	
	public static final String ENB_FUNCTION_RADIO_PATH_CONTROL = "show managed-element hardware-management radio-unit radio-unit-info path-control | display xml";
	
	public static final String EARFCN_CELL_FDD_TDD_EARFCN_DL = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-frequency-relation eutran-fa-information eutran-fa-prior-info-func earfcn-dl | display xml";
	
	public static final String RADIOUNIT_CARRIER_CONTROLINFO = "show managed-element hardware-management radio-unit radio-unit-info carrier-control-info | display xml";
	
	public static final String RADIOUNIT_CPRIPORT = "show managed-element hardware-management radio-unit radio-unit-info external-port cpri-port | display xml";
	
	public static final String RADIOUNIT_ANTENALINE_DEVICE_INFO = "show managed-element hardware-management radio-unit radio-unit-info antenna-unit antenna-line-device antenna-line-device-info | display xml";
	
	
	public static final String ENB_FUNCTION_IMS_EMERGENCY = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd eutran-cell-info ims-emergency-support | display xml";
	
	public static final String DIGITALUNIT_CPRIPORT_ENTRIES = "show managed-element hardware-management digital-unit digital-unit-entries external-port cpri-port cpri-port-entries | display xml";
	
	public static final String IPSYS_IPV6_STATICROUTE = "show managed-element ip-system ip-route ipv6-route ipv6-static-route | display xml";
	public static final String ENB_FUNCTION_OPERATIONAL_STATE = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd operational-state | display xml";
	
	public static final String ENB_FUNCTION_TERMPOINTMME = "show managed-element enb-function term-point-to-mme | display xml";
	public static final String REQUEST_THROUGHPUT = "request retrieve-cell-throughput | display xml";
	
	
	public static final String UPSTATE_NY_MME_IPS = "2001:4888:2011:5001:01b1:028a:0000:0000,2001:4888:2011:5001:1b1:28a:0:0,2001:4888:2011:5001:1b1:28a::,2001:4888:2011:6001:01b4:028a:0000:0000,2001:4888:2011:6001:1b4:28a:0:0,2001:4888:2011:6001:1b4:28a::,2001:4888:2011:5001:01b1:028a:0000:0001,2001:4888:2011:5001:1b1:28a:0:1,2001:4888:2011:5001:1b1:28a:0:1,2001:4888:2011:6001:01b4:028a:0000:0001,2001:4888:2011:6001:1b4:28a:0:1,2001:4888:2011:6001:1b4:28a::1,2001:4888:2011:6001:01b4:028a:0000:0001,2001:4888:2011:6001:1b4:28a:0:1,2001:4888:2011:6001:1b4:28a::1,2001:4888:2011:6001:1b4:28a:0:1,2001:4888:2011:6001:1b4:28a::1,2001:4888:2011:6001:01b4:028a:0000:0001,2001:4888:2011:5092:01b1:028a:0000:0002,2001:4888:2011:5092:1b1:28a:0:2,2001:4888:2011:5092:1b1:28a::2,2001:4888:2011:5092:1b1:28a:0:22,2001:4888:2011:5092:1b1:28a::22,2001:4888:2011:5092:01b1:028a:0000:0022,2001:4888:2011:6092:1b4:28a:0:2,2001:4888:2011:6092:1b4:28a::2,2001:4888:2011:6092:01b4:028a:0000:0002,2001:4888:2011:6092:01b4:028a:0000:0022,2001:4888:2011:6092:1b4:28a:0:22,2001:4888:2011:6092:1b4:28a::22\n" + 
			"\n" + 
			"";
	public static final String NEWENGLAND_MME_IPS="2001:4888:2010:11:106:28A:0:0,2001:4888:2010:5011:123:28A:0:0,2001:4888:2010:5011:123:28A:0:1,2001:4888:2010:11:106:28A:0:1,2001:4888:2010:11:106:28A:0:2,2001:4888:2010:5011:123:28A:0:2,2001:4888:2010:92:106:28a:0:2,2001:4888:2010:92:106:28a:0:22,2001:4888:2010:92:106:28a:0:32,2001:4888:2010:92:106:28a:0:42,2001:4888:2010:5092:123:28a:0:2,2001:4888:2010:5092:123:28a:0:22,2001:4888:2010:5092:123:28a:0:32,2001:4888:2010:5092:123:28a:0:42"; 
	
	
	public static final ConcurrentHashMap<String, String> SCRIFT_FAIL_DETAILS = new ConcurrentHashMap<>();
	public static final String RADIOUNIT_ANTENALINE_DEVICE_INFO_RETINFO = "show managed-element hardware-management radio-unit radio-unit-info antenna-unit antenna-line-device antenna-line-device-info ret ret-info | display xml";
	public static final String EUTRAN_CBRS_INFO = "show managed-element enb-function eutran-generic-cell eutran-cell-fdd-tdd cbrs-info | display xml";

	
	public static final String ENDTEXT5G="outputends";
	public static final String ENDTEXT5GTWAMP="5gTwampEnds";
	public static final String ENDTEXT5GENB="enbEnds";
	
	public static final String AUPF_GETCSL_SERVER_SETTINGS="AUPF_Get_CSL_Serv_Settings";
	public static final String ACPF_GETCSL_SERVER_SETTINGS="ACPF_Get_CSL_Serv_Settings";
	public static final String AU_ADDTNL_PARAM_CHECKS="AU_20A_Addtnl_Param_Check_Sacremento";
	public static final String ACPFA1A2A3_INCL_NR_UL_CHECKS="ACPF_A1_A2_A3_Incl_NR_UL_checks";
	public static final String ACPFE1F1X2CHECKS="ACPF_E1_F1_X2_check";
	public static final String AU_20A_PARAMCHECK="AU_20A_Param_Check";
	public static final String ENB_20A0SWVERSION="eNB_ENDCAudit20A0SWVer";
	public static final String ENB_20B0SWVERSION="eNB_ENDCAudit20B0SWVer";
	public static final String ACPF_20AP3_CHECK = "ACPF_20AP3Checks";
	public static final String ACPF_20C_AUDIT = "ACPF_20C_Audit";
	public static final String AU_20C_PARAMCHECK="AU_20C_Param_Check";
	public static final String AU_20C_PROCESSOR_ENTRIES="AU_20C_Processor_Entries";
	public static final String ACPFE1F1X2_20C="ACPF_E1_F1_X2_20C_check";
	public static final String ACPFA1A2A3_20C_CHECKS="ACPF_A1_A2_A3_20C_checks";
	public static final String ENB_20C0SWVERSION="eNB_ENDCAudit20C0SWVer";
	public static final String IAU_PARAMCHECK="IAU_Param_Check";
	public static final String IAU_TWAMP_F1C_LINK="IAU_F1C_LINK_TWAMP";
	public static final String IAU_DIAGNOSTIC = "IAU_DIAGNOSTIC_TWAMP";
	public static final String ACPFA2_CHECKS="ACPF_A2_checks";
	public static final String ACPFA3_CHECKS="ACPF_A3_checks";
	public static final String AU_21B_PARAMCHECK="AU_21B_Param_Check";
	public static final String AU_TWAMP_F1C_LINK="AU_TWAMP_F1C_LINK";
	public static final String AU_DIAGNOSTIC = "AU_DIAGNOSTIC";
	//21D 
	public static final String AU_21D_SFP_INVENTORY="AU_21D_SFP_Inventory";
	public static final String ACPF_A1_21D_CHECKS="ACPF_A1_21D_checks";
	public static final String ACPF_A2_21D_CHECKS = "ACPF_A2_21D_checks";
	public static final String ACPF_A3_21D_CHECKS = "ACPF_A3_21D_checks";
	public static final String ACPF_E1_F1_X2_21D_CHECKS="ACPF_E1_F1_X2_21D_checks";
	public static final String AU_21D_PARAM_CHECK="AU_21D_Param_Check";
	public static final String ACPF_SON_ANR_CHECKS="ACPF_SON_ANR_checks";
	public static final String IAU_21D_PARAM_CHECK="IAU_21D_Param_Check";
	public static final String IAU_21D_PROCESSOR_ENTRIES="IAU_21D_Processor_Entries";
	public static final String IAU_21D_SFP_INVENTORY="IAU_21D_SFP_Inventory";
	public static final String AU_DSCP_PARAM_CHECK="AU_DSCP_Param_Check";
	public static final String IAU_DSCP_PARAM_CHECK="IAU_DSCP_Param_Check";
	//DSS Post-Audit 
	
	//in use
	public static final String DSS_VDU_PREAUDIT = "DSS_vDU_PreAudit";
	public static final String DSS_F1U_SOURCEIP = "DSS_vDU_F1U_SourceIp";
	public static final String DSS_F1U_DESTINATIONIP = "DSS_AUPF_F1U_DestinationIp";
	public static final String DSS_F1U_TESTTWPING = "DSS_vDU_F1U_TestTwPing";
	public static final String DSS_F1U_TESTTWPING_DIAGNOSIS = "DSS_vDU_F1U_Diagnosis";
	public static final String DSS_ENB_PREAUDIT = "DSS_eNB_PreAudit";
	public static final String DSS_ACPF_E1F1X2 = "DSS_ACPF_E1F1X2";
	public static final String DSS_ACPF_POSTAUDIT = "DSS_ACPF_PostAudit";
	public static final String DSS_AUPF_POSTAUDIT = "DSS_AUPF_PostAudit";
	public static final String DSS_ENB_POSTAUDIT = "DSS_eNB_PostAudit";
	public static final String DSS_FSU_POSTAUDIT = "DSS_FSU_PostAudit";
	public static final String DSS_VDU_POSTAUDIT = "DSS_vDU_PostAudit";
	public static final String DSS_ENB_SOFTWAREVERSION = "DSS_eNB_SoftwareVersion";
	public static final String DSS_FSU_SOFTWAREVERSION = "DSS_FSU_SoftwareVersion";
	public static final String DSS_VDU_STATICROUTE = "DSS_vDU_staticroute";
	public static final String DSS_VDU_GUTRANDUCELL = "DSS_vDU_gutranducell";
	public static final String DSS_VDU_NRFREQUENCY = "DSS_vDU_nrfrequency";
	public static final String DSS_VDU_PRACHCONFIG = "DSS_vDU_prachconfig";
	public static final String DSS_VDU_PODSTATUS = "DSS_vDU_podstatus";
	public static final String DSS_VDU_PTPSYNC = "DSS_vDU_ptpsync";
	public static final String DSS_VDU_ENDPOINTDSS = "DSS_vDU_endpointdss";
	public static final String DSS_VDU_VIRTUALPORTCHECK = "DSS_vDU_virtualportcheck";
	public static final String DSS_VDU_EXTERNALINTERFACE = "DSS_vDU_externalinterface";
	public static final String DSS_VDU_FLAVORANDSV= "DSS_vDU_flavorandsf";
	public static final String DSS_VDU_MMUSERIALNUMBER= "DSS_vDU_MmuSerialNum";
	public static final String DSS_VDU_FSUMPLANEIP= "DSS_vDU_fsu_mplaneip";
	public static final String DSS_FSU_FSUTYPE = "DSS_FSU_FsuType";
	public static final String DSS_FSU_VRUENTRIES = "DSS_vDU_VruEntries";
	public static final String DSS_VDU_TXRXPOWER = "DSS_vDU_TxRxPower";
	public static final String DSS_FSU_PREAUDIT = "DSS_FSU_PreAudit";
	public static final String DSS_ACPF_PREAUDIT = "DSS_ACPF_PreAudit";
	public static final String DSS_ENB_ENDC_AUDIT = "DSS_eNB_ENDCAudit";
	public static final String DSS_VDU_MH1IPFETCH = "DSS_VDU_MH1IPFETCH";
	public static final String DSS_VDU_ORUTABLE = "DSS_vDU_OruTable";
	public static final String DSS_VDU_ORUPLUMBING = "DSS_vDU_PlumbingOru";
	public static final String DSS_VDU_ORUPOSTTABLE = "DSS_vDU_OruPostTable";
	public static final String DSS_VDU_POWERAUDIT = "DSS_vDU_PowerAudit";
	
	
	//NEUP USECASE
	public static final String DSS_VDU_NEUP_USECASE = "DSS_vDU_NEUP_USECASE";
	public static final String CBAND_VDU_NEUP_USECASE = "CBand_vDU_NEUP_USECASE";
	public static final String AUDIT_4G_NEUP_USECASE = "4GUSM_NEUP_USECASE";
	public static final String AU_NEUP_USECASE = "AU_NEUP_USECASE";
	public static final String AUDIT4G_NEUP_USECASE = "4GFSU_NEUP_USECASE";
	public static final String AUDIT_4G_FIRMWARECHECK = "4GUSM_FIRMWARECHECK";
	public static final String DSS_VDU_FIRMWARECHECK = "DSS_vDU_CheckAvailableFirmware";
	public static final String CBAND_VDU_FIRMWARECHECK  = "CBand_vDU_FIRMWARECHECK";
	public static final String AU_FIRMWARECHECK = "AU_FIRMWARECHECK";
	public static final String AUDIT4G_FIRMWARECHECK = "4GFSU_FIRMWARECHECK";
	public static final String IAU_NEUP_USECASE = "IAU_NEUP_Check";
	public static final String IAU_FIRMWARECHECK = "IAU_CheckAvailableFirmware";
	
	
	
	public static final String AUDIT_4G_REQUEST = "4GAudit_Request";
	public static final String AUDIT_4G_NTP_STATUS_REQUEST = "4GAudit_Ntp_Status_Request";
	public static final String AUDIT_4G_PACKAGE_INVENTORY = "4GAudit_Package_Inventory";
	public static final String AUDIT_4G_UCR_INVENTORY = "4GAudit_Ucr_Inventory";
	public static final String AUDIT_4G_PROCESSOR_FIRMWARE_INVENTORY = "4GAudit_Processor_Firmware_Inventory";
	public static final String AUDIT_4G_RADIO_UNIT_INVENTORY_MMU = "4GAudit_Radio_Unit_Inventory_Mmu";
	public static final String AUDIT_4G_RADIO_UNIT_INVENTORY_RRH = "4GAudit_Radio_Unit_Inventory_Rrh";
	public static final String AUDIT_4G_TTLNA_INFO = "4GAudit_Ttlna_Info";
	public static final String AUDIT_4G_CELL_THROUGHPUT = "4GAudit_Cell_Throughput";
	public static final String AUDIT_4G_SFP_INVENTORY = "4GAudit_Sfp_Inventory";
	public static final String AUDIT_4G_RADIO_UNIT_SFP_INVENTORY = "4GAudit_Radio_Unit_Sfp_Inventory";
	public static final String AUDIT_4G_TRUECALL_TRACEJOB = "4GAudit_TrueCall_TraceJob";
	public static final String AUDIT_4G_ENB_STATE_CHECK = "4GAudit_Enb_State_Check";
	public static final String AUDIT_4G_OPTICDISTANCECHECK = "4GAudit_OpticDistanceCheck";
	public static final String AUDIT_4G_TERMPOINTENB = "4GAudit_TermPoint_Enb";
	public static final String AUDIT_4G_ACTIVE_SOFTWARE = "4GAudit_Active_Software";
	public static final String AUDIT_4G_OCNS_TEST_CELL_NUM = "4GAudit_Ocns_Test_CellNum";
	public static final String AUDIT_4G_OCNS_TEST_TERMINATE = "4GAudit_Ocns_Test_Terminate";
	public static final String AUDIT_4G_CHECK_RSSI = "4GAudit_Check_Path_Rssi";
	public static final String AUDIT_4G_CHECK_TXPOWER = "4GAudit_Check_TxPower";
	public static final String AUDIT_4G_SDLC_STATE = "4GAudit_Sdlc_State";
	public static final String AUDIT_4G_ACTIVE_RET_ALARM ="4GAudit_ActiveRetAlarm";
	public static final String AUDIT_4G_CBRSMODEANDUSERID ="4GAudit_CbrsModeAndUserId";
	public static final String AUDIT_4G_CBRSCALLSIGNANDFCCID ="4GAudit_CallsignAndFccId";
	public static final String AUDIT_4G_ENB_BANDWIDTH ="4GAudit_eNB_BandwidthCheck";
	public static final String AUDIT_4G_ENB_CBSDSTATE ="4GAudit_eNB_CbsdState";
	public static final String AUDIT_4G_ENB_CBSDANDGRANTSTATE ="4GAudit_eNB_CbsdAndGrantState";
	public static final String AUDIT_4G_ENB_PCIALLOCSTATUS ="4GAudit_eNB_PciAllocStatus";
	public static final String AUDIT_4G_ENB_PREFERREDEARFCN ="4GAudit_eNB_PreferredEARFCN";
	public static final String AUDIT_4G_RSSIIMBALANCE ="4GAudit_RssiImbalance";
	public static final String AUDIT_4G_CARRIER_RSSI = "4GAudit_Carrier_Rssi";
	public static final String AUDIT_4G_DU_OPTIC_LEVEL = "4GAudit_DU_Optic_Level";
	public static final String AUDIT_4G_RU_OPTIC_LEVEL = "4GAudit_RU_Optic_Level";
	public static final String AUDIT_4G_CBRSCHECKS = "4GAudit_cbrs_checks";
	public static final String AUDIT_4G_FW_AUTO_FUSING = "4GAudit_FW_Auto_Fusing";
	public static final String AUDIT_4G_RADIO_FIRMWARE_VERIFICATION = "4GAudit_Radio_Firmware_Verification";
	public static final String AUDIT_4G_GROWPREFIXCHECK = "4GAudit_Grow_Prefix_Check";
	public static final String AUDIT_4G_RET_CHECKS= "4GAudit_Ret_Audit1";
	public static final String AUDIT_4G_RET_AUDIT = "4GAudit_Ret_Audit2";
	public static final String AUDIT_4G_PTP_CHECKS = "4GAudit_PTP_Checks";
	public static final String AUDIT_4G_SERIAL_NUMBER = "4GAudit_Serial_Number_Checks";
	public static final String AUDIT_4G_OVOUTPUT = "4GAudit_SerialNumMmu_Ov";
	public static final String AUDIT_4G_FCC_RETRIEVE_RADIO_UNIT_INVENTORY = "4GAudit_fcc_Retrieve_Radio_Unit_Inventory";
	public static final String AUDIT_4G_FCC_CBSD_INFO = "4GAudit_fcc_cbsd_info";
	public static final String AUDIT_4G_CONFIGURE_FCC_ID = "4GAudit_configure_fcc_id";
	public static final String AUDIT_4G_CBRS_RETRIEVE_RADIO_UNIT_INVENTORY = "4GAudit_CBRS_Retrieve_Radio_Unit_Inventory";
	
	
	public static final String CBAND_VDU_AUDIT = "CBand_vDU_Audit";
	public static final String CBAND_F1U_SOURCEIP = "CBand_vDU_F1U_SourceIp";
	public static final String CBAND_F1U_DESTINATIONIP = "CBand_vDU_F1U_DestinationIp";
	public static final String CBAND_F1U_TESTTWPING = "CBand_vDU_F1U_TestTwPing";
	public static final String CBAND_F1U_TESTTWPING_DIAGNOSIS = "CBand_vDU_F1U_Diagnosis";
	public static final String CBAND_ACPF_ALARMENTRIES = "CBand_ACPF_AlarmEntries";
	public static final String CBAND_ACPF_E1F1X2 = "CBand_ACPF_E1F1X2";
	public static final String CBAND_AUPF_ALARMENTRIES = "CBand_AUPF_AlarmEntries";
	public static final String CBAND_ENB_ALARMENTRIES = "CBand_eNB_AlarmEntries";
	public static final String CBAND_VDU_PACKAGEINVENTORY = "CBand_vDU_PackageInventory";
	public static final String CBAND_VDU_POSTAUDIT = "CBand_vDU_PostAudit";
	public static final String CBAND_ENB_PACKAGEINVENTORY = "CBand_eNB_PackageInventory";
	public static final String CBAND_VDU_CELLSTATUS = "CBand_vDU_cellStatus";
	public static final String CBAND_VDU_MMUSERIALNUM = "CBand_vDU_MmuSerialNum";
	public static final String CBAND_VDU_VDUSTATUS = "CBand_vDU_vDUStatus";
	public static final String CBAND_VDU_SFPMMUCHECK = "CBand_vDU_SFPMMUCheck";
	public static final String CBAND_VDU_SOFTWARESLOT = "CBand_vDU_softwareslot";
	public static final String CBAND_VDU_E_TILT = "CBand_vDU_e_tilt";
	
	public static final String DSS_ENB_IMPACT_AUDIT = "DSS_eNB_Impact_Audit";
	public static final String DSS_ENB_POST_IMPACT_AUDIT = "DSS_eNB_Post_Impact_Audit";
	public static final String DSS_FSU_IMPACT_AUDIT = "DSS_FSU_Impact_Audit";
	public static final String DSS_FSU_POST_IMPACT_AUDIT = "DSS_FSU_Post_Impact_Audit";
	public static final String CBAND_ENB_IMPACT_AUDIT = "CBand_eNB_Impact_Audit";
	public static final String CBAND_ENB_POST_IMPACT_AUDIT = "CBand_eNB_Post_Impact_Audit";
	
	
	public static final String CBAND_VDU_MMUAUDIT = "CBand_vDU_mmuAudit";
	public static final String CBAND_VDU_MH1IPFETCH = "CBAND_VDU_MH1IPFETCH";
	public static final String CBAND_VDU_MMUREPORTINGTOOL = "CBand_vDU_MMU_Reporting_Tool";
	public static final String CBAND_VDU_SERIALNUMMMU = "CBand_vDU_SerialNumMmu";
	public static final String CBAND_VDU_OVOUTPUT = "CBand_SerialNumMmu_Ov";
	//rssi audit
	public static final String RSSI_USE_CASE = "RSSI_USE_CASE";
	
	public static final String AUDIT4G_FSU_BACKUPDB = "Audit4G_FSU_backup_db";
	public static final String AUDIT4G_FSU_POSTCHECKS = "Audit4G_FSU_PostChecks";
	public static final String AUDIT4G_ENB_SERIALNUMBER = "Audit4G_eNB_SerialNumber";
	public static final String AUDIT4G_ENB_TXRFPOWER = "Audit4G_eNB_TxRfPower";
	public static final String AUDIT4G_ENB_RETRIEVE_CELL_THROUGHPUT = "Audit4G_eNB_retrieve_cell_throughput";
	public static final String AUDIT4G_FSU_SFP_INVENTORY = "Audit4G_FSU_Sfp_Inventory";
	
	//GrowPrefixRemoval
	public static final String AUDIT4G_GROW_PREFIX_REMOVAL = "GROW_PREFIX_REMOVAL_CURL";
	public static final String AUDIT4G_FSU_GROWPREFIX = "Audit4G_FSU_GrowPrefix";
	public static final String AU_GROWPREFIX = "AU_GrowPrefix";
	public static final String DSS_VDU_GROWPREFIX = "DSS_vDU_GrowPrefix";
	public static final String CBAND_VDU_GROWPREFIX = "CBand_vDU_GrowPrefix";
	public static final String AUDIT_4G_GROWPREFIX = "4GAudit_GrowPrefix";
	public static final String IAU_5GMMGROWPREFIX = "IAU_5GMMGrowPrefix";
}