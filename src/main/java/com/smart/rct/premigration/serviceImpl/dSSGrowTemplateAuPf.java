package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.util.SystemOutLogger;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.constants.AuditConstants;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.util.CommonUtil;


@Component
public class dSSGrowTemplateAuPf {
	static final Logger logger = LoggerFactory.getLogger(CBandGrowTemplate.class);
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	@Autowired
	GenerateCsvService objGenerateCsvService;

	

	public JSONObject generateGrowTemplate(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder sb = new StringBuilder();
		StringBuilder fileBuilder = new StringBuilder();
		String growFileName = "";
		
	
		
	
		try {
			/*String DbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			List<CIQDetailsModel> listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId,
					DbcollectionFileName);
			String eNB4G = "";
			String NEID = "";
			String gNBID="";
			if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
				if (listCIQDetailsModel.get(0).getCiqMap().containsKey("4GeNB") && listCIQDetailsModel.get(0).getCiqMap().containsKey("NEID") && 
						listCIQDetailsModel.get(0).getCiqMap().containsKey("gNBID") ){
					eNB4G = listCIQDetailsModel.get(0).getCiqMap().get("4GeNB").getHeaderValue();
					NEID = listCIQDetailsModel.get(0).getCiqMap().get("NEID").getHeaderValue();
					gNBID = listCIQDetailsModel.get(0).getCiqMap().get("gNBID").getHeaderValue();
					eNB4G = eNB4G.replaceAll("^0+(?!$)", "");
					NEID = NEID.replaceAll("^0+(?!$)", "");
					gNBID = gNBID.replaceAll("^0+(?!$)", "");
				}
				
			}	*/
			if (CommonUtil.isValidObject(filetype) && filetype.equalsIgnoreCase("vDUGrow")) {
			String eNB4G = "";
			String NEID = "";
			String gNBID="";
			List<CIQDetailsModel> listCIQDetailsModelDay1 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
			HashMap<String, String> day1DataMap = new HashMap<>();
			String[] dataListDay1 = {"NEID", "vDU_Version","4GeNB" ,"vDU_Release", "NEName", "gNBID", "gNBIDLength", "gNBDUID", "gNBDUName", "EndpointCUIPaddress",
					"Network", "f1uAddress", "f1cGateway"};
			
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay1)) {
				for(String key : dataListDay1) {
					if(listCIQDetailsModelDay1.get(0).getCiqMap().containsKey(key)) {
						day1DataMap.put(key, listCIQDetailsModelDay1.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day1DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay1) {
					day1DataMap.put(key, "");
				}
			}
			NEID=day1DataMap.get("NEID");
			eNB4G=day1DataMap.get("4GeNB");
			gNBID=day1DataMap.get("gNBID");
			NEID = NEID.replaceAll("^0+(?!$)", "");
			eNB4G = eNB4G.replaceAll("^0+(?!$)", "");
			gNBID = gNBID.replaceAll("^0+(?!$)", "");

			List<CIQDetailsModel> listCIQDetailsModelDay0 = fileUploadRepository.getEnbTableDetailsRanConfigg(ciqFileName, NEID, enbName, dbcollectionFileName, "vDUHELM(Day0)Orchestrator", "");
			List<CIQDetailsModel> listCIQDetailsModelDay2 = fileUploadRepository.getEnbTableSheetDetailsss(ciqFileName, "vDUDay_2", eNB4G, dbcollectionFileName);
			List<CIQDetailsModel> list_DSS_MOP_Parameters1 = fileUploadRepository.getEnbTableDetailsRanConfig2(ciqFileName, gNBID, enbName, dbcollectionFileName, "DSS_MOP_Parameters-1", "");

			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			growFileName = "vDUGrow" + enbName + dateString + ".csv";
			
			HashMap<String, String> dayDSSPDataMap = new HashMap<>();
			HashMap<String, String> day0DataMap = new HashMap<>();
			//HashMap<String, String> day1DataMap = new HashMap<>();
			List<HashMap<String, String>> day2DataMap = new ArrayList<>();
			Set<String> oranIdset = new HashSet<>();
			String[] DSSParameters1 = {"4GeNB","remote-ip-address"};
			String[] dataListDay0 = {"cidr","gw","gw1","gw2","4GeNB","cidr1","cidr2","addr0","addr","addr4"};
			//String[] dataListDay1 = {"NEID", "vDU_Version","4GeNB" ,"vDU_Release", "NEName", "gNBID", "gNBIDLength", "gNBDUID", "gNBDUName", "EndpointCUIPaddress",
				//	"Network", "f1uAddress", "f1cGateway"};
			String[] dataListDay2 = {"NEID", "sector-id","CarrierID","nr-arfcn-dl", "POD_PORT_ID", "RU_PORT_ID","vru-id", "vlanId", "oruId", "nr-arfcn-ul", "nrfrequency", "nr_PCI",
					"dlAntennaCount", "ulAntennaCount", "numberRxPathsRU", "Cell_Path_Type", "cell-num",  "nrDLBandwidth","nr-physical-cell-id",
					"NR_Frequency_Band",  "Tracking_Area_Code_Usage", "Tracking_Area_Code", "connected-pod-id", "prachzczc", "prachrsi", "dl-antenna-count",
					"ul-antenna-count", "number-of-rx-paths-per-ru", "Dynamic_Spectrum_Sharing_Mode", "connected-pod-type", "power", "dynamicSpectrumSharingMode", "slotLevelOperationMode", "dssTargetLTECellNum", "endpointDSSIndex",
					"support-cell-number", "NR-Cell-Power","FSU-ip","vlan-id", "connected-pod-port-id", "connected-fsu-port-id", "fsu-id","unit-type","sharing-enabled","Latitude","Height","Longitude"};
			
			String network = "";
			if(!ObjectUtils.isEmpty(list_DSS_MOP_Parameters1 )) {
				for(String key : DSSParameters1) {
					if(list_DSS_MOP_Parameters1 .get(0).getCiqMap().containsKey(key)) {
						dayDSSPDataMap.put(key, list_DSS_MOP_Parameters1 .get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						dayDSSPDataMap.put(key, "");
					}
				}
			} else {
				for(String key : DSSParameters1) {
					dayDSSPDataMap.put(key, "");
				}
			}
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay0)) {
				for(String key : dataListDay0) {
					if(listCIQDetailsModelDay0.get(0).getCiqMap().containsKey(key)) {
						day0DataMap.put(key, listCIQDetailsModelDay0.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day0DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay0) {
					day0DataMap.put(key, "");
				}
			}
			/*if(!ObjectUtils.isEmpty(listCIQDetailsModelDay1)) {
				for(String key : dataListDay1) {
					if(listCIQDetailsModelDay1.get(0).getCiqMap().containsKey(key)) {
						day1DataMap.put(key, listCIQDetailsModelDay1.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day1DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay1) {
					day1DataMap.put(key, "");
				}
			}
			*/
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay2)) {
				for(CIQDetailsModel ciqDetails : listCIQDetailsModelDay2) {
					HashMap<String, String> tempMap = new HashMap<>();
					for(String key : dataListDay2) {
						if(ciqDetails.getCiqMap().containsKey(key)) {
							tempMap.put(key, ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						} else {
							tempMap.put(key, "");
						}
					}
					day2DataMap.add(tempMap);
				}
			}
			
			if(enbId.length() == 11) {
				network = enbId.substring(0, 3);
			} else if(enbId.length() == 10) {
				network = "0" + enbId.substring(0, 2);
			}
			String  NeVersion=StringUtils.substringBefore(day1DataMap.get("vDU_Version"), "-");
			if(NeVersion.contains("22.A")) {
				 
				String R_V=day1DataMap.get("vDU_Release");
				String Release_v=R_V.replaceAll("-","_");
				sb.append("\"@ADPF\"\n");
				sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
							+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"PRACH Coverage Extension B6G TDD\",\"CBRS Mode\",\"CBRS Measure Unit\"\n");
				 
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("vDU_Version"), "-") + "\"," 
							+ "\"" + Release_v + "\"," +"\"" +day1DataMap.get("Network") +"\"," + "\"" + day1DataMap.get("NEName") + "\"," 
							+ "\"" + "\"," + "\"" + day1DataMap.get("gNBID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gNBDUID") + "\"," 
							+ "\"" + day1DataMap.get("gNBDUName") + "\"," + "\"" + day1DataMap.get("EndpointCUIPaddress") + "\"," + "\"" + "0" + "\","  + "\"" + "normal-coverage"+ "\","
							+ "\"" +"cbrs-on"+ "\"," + "\"" + "10mhz" + "\"" + "\n");
				
				sb.append("\"@SERVER_INFORMATION\"\n");
				sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
				sb.append("\"\",\"\",\"\"\n");
				
				sb.append("\"@ENDPOINT_DSS_INFORMATION\"\n");
				sb.append("\"NE ID\",\"DSS Index\",\"Remote IP Address\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\",\"0\","+"\""+ dayDSSPDataMap.get("remote-ip-address") + "\"\n");
				
				
				sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
				
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			    sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
		        sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
				
//				sb.append("\"@EXTERNAL_IP_INFORMATION\"\n"); //pending
//				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"DSS\",\"Carrier Aggregation\",\"Mplane\"\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr") + "\"," + "\"64\",\"true\"," + "" + "\"false\",\"false\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," + "" + "\"false\",\"false\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," + "" +"\"false\",\"true\"" + "\n");

				sb.append("\"@EXTERNAL_IP_INFORMATION\"\n"); //pending
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"DSS\",\"Carrier Aggregation\",\"Mplane\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0\"," +  "\""+ day0DataMap.get("addr") + "\"," + "\"64\",\"true\"," +"\"true\","+ "" + "\"true\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," +"\"true\","+ "" + "\"false\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," +"\"false\","+ "" + "\"false\",\"true\"" + "\n");
				//sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ "" + "\"," + "\"\",\"\"," +"\"\","+ "" +"\"\",\"\"" + "\n");
				//sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ "" + "\"," + "\"\",\"\"," +"\"\","+ "" +"\"\",\"\"" + "\n");

				
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," +"\"false\","+ "" +"\"false\",\"true\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," +"\"false\","+ "" +"\"false\",\"false\"" + "\n");

				
				sb.append("\"@ROUTE_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\","+"\""+ day0DataMap.get("cidr") + "\"," + "\""+ day0DataMap.get("gw") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\","+"\""+ day0DataMap.get("cidr1") + "\"," + "\""+ day0DataMap.get("gw1") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\","+"\""+ day0DataMap.get("cidr2") + "\"," + "\""+ day0DataMap.get("gw2") + "\"" + "\n");
				
				sb.append("\"@CSL_TCE_INFORMATION\"\n");
				sb.append("\"NE ID\",\"CSL TCE Server IP Address\",\"CSL TCE Server Port\",\"CSL TCE Option\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\","+ "\"" + "\"," + "\""+ "\","+ "\"normal-and-abnormal-and-intra-ho-call" + "\"" + "\n");
				
			}
			else if(NeVersion.contains("21.D")) {
			 
				String R_V=day1DataMap.get("vDU_Release");
				String Release_v=R_V.replaceAll("-","_");
				sb.append("\"@ADPF\"\n");
				sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
							+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\",\"CBRS Mode\",\"CBRS Measure Unit\"\n");
				 
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("vDU_Version"), "-") + "\"," 
							+ "\"" + Release_v + "\"," +"\"" +day1DataMap.get("Network") +"\"," + "\"" + day1DataMap.get("NEName") + "\"," 
							+ "\"" + "\"," + "\"" + day1DataMap.get("gNBID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gNBDUID") + "\"," 
							+ "\"" + day1DataMap.get("gNBDUName") + "\"," + "\"" + day1DataMap.get("EndpointCUIPaddress") + "\"," + "\"" + "0" + "\"," 
							+ "\"" +"cbrs-on"+ "\"," + "\"" + "10mhz" + "\"" + "\n");
				
				sb.append("\"@SERVER_INFORMATION\"\n");
				sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
				sb.append("\"\",\"\",\"\"\n");
				
				sb.append("\"@ENDPOINT_DSS_INFORMATION\"\n");
				sb.append("\"NE ID\",\"DSS Index\",\"Remote IP Address\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\",\"0\","+"\""+ dayDSSPDataMap.get("remote-ip-address") + "\"\n");
				
				
				sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
				
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			    sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
		        sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
				
//				sb.append("\"@EXTERNAL_IP_INFORMATION\"\n"); //pending
//				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"DSS\",\"Carrier Aggregation\",\"Mplane\"\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr") + "\"," + "\"64\",\"true\"," + "" + "\"false\",\"false\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," + "" + "\"false\",\"false\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," + "" +"\"false\",\"true\"" + "\n");

				sb.append("\"@EXTERNAL_IP_INFORMATION\"\n"); //pending
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"DSS\",\"Carrier Aggregation\",\"Mplane\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0\"," +  "\""+ day0DataMap.get("addr") + "\"," + "\"64\",\"true\"," +"\"true\","+ "" + "\"true\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," +"\"true\","+ "" + "\"false\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," +"\"false\","+ "" + "\"false\",\"true\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ "" + "\"," + "\"\",\"\"," +"\"\","+ "" +"\"\",\"\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ "" + "\"," + "\"\",\"\"," +"\"\","+ "" +"\"\",\"\"" + "\n");

				
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\"," +"\"false\","+ "" +"\"false\",\"true\"" + "\n");
//				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\"," +"\"false\","+ "" +"\"false\",\"false\"" + "\n");

				
				sb.append("\"@ROUTE_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\","+"\""+ day0DataMap.get("cidr") + "\"," + "\""+ day0DataMap.get("gw") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\","+"\""+ day0DataMap.get("cidr1") + "\"," + "\""+ day0DataMap.get("gw1") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\","+"\""+ day0DataMap.get("cidr2") + "\"," + "\""+ day0DataMap.get("gw2") + "\"" + "\n");
				
				sb.append("\"@CSL_TCE_INFORMATION\"\n");
				sb.append("\"NE ID\",\"CSL TCE Server IP Address\",\"CSL TCE Server Port\",\"CSL TCE Option\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\","+ "\"" + "\"," + "\""+ "\","+ "\"normal-and-abnormal-and-intra-ho-call" + "\"" + "\n");
				
			} else {
				sb.append("\"@ADPF\"\n");
				sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
					+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\","
					+ "\"NE Serial Number\"\n");
				String R_V=day1DataMap.get("vDU_Release");
				 String Release_v=R_V.replaceAll("-","_");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("vDU_Version"), "-") + "\"," 
						+ "\"" + Release_v + "\","  + "\""+day1DataMap.get("Network") + "\"," + "\"" + day1DataMap.get("NEName") + "\"," 
					+ "\"" + "\"," + "\"" + day1DataMap.get("gNBID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gNBDUID") + "\"," 
					+ "\"" + day1DataMap.get("gNBDUName") + "\"," + "\"" + day1DataMap.get("EndpointCUIPaddress") + "\"," + "\"" + "0" + "\"," + "\"" + "\"" + "\n");
			
				sb.append("\"@SERVER_INFORMATION\"\n");
				sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
				sb.append("\"\",\"\",\"\"\n");
				//my changes
				sb.append("\"@ENDPOINT_DSS_INFORMATION\"\n");
			
				sb.append("\"NE ID\",\"DSS Index\",\"Remote IP Address\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\",\"0\","+"\""+ dayDSSPDataMap.get("remote-ip-address") + "\"\n");
			
			
				sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
			
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			    sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
			        sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1956\"" + "\n");
			
				sb.append("\"@EXTERNAL_IP_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"Carrier Aggregation\",\"Mplane\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0.1050\"," +  "\""+ day0DataMap.get("addr0") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ day0DataMap.get("addr4") + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			
				sb.append("\"@ROUTE_INFORMATION\"\n");
				sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\","+"\""+ day0DataMap.get("cidr") + "\"," + "\""+ day0DataMap.get("gw") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\","+"\""+ day0DataMap.get("cidr1") + "\"," + "\""+ day0DataMap.get("gw1") + "\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\","+"\""+ day0DataMap.get("cidr2") + "\"," + "\""+ day0DataMap.get("gw2") + "\"" + "\n");
			}
			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + growFileName);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			
	
				
	}
		
		catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGenerationFSU() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", growFileName);

		}
		return fileGenerateResult;
	}
	 public  String bandSelection(String val) {
		int num =Integer.parseInt(val);
		String fVal="";
		if (173800 <num && num<178800)
		{
			fVal= "B5";
		}
		else if (149200 <num && num<151200)
		{
			fVal ="B13";
		}
		else if (386000 <num && num<398000)
		{
			fVal ="B2";
		}
		else if (422000 <num && num<440000)
		{
			fVal= "B4";
		}
		return fVal;
		}
	 public String cellPath(String fVal,String uVal, String vVal ) {
		 String paTh="";
		 String[] uValArr=uVal.split("-");
		 String uValFin=uValArr[3];
		 uValFin=uValFin.substring(0,2);
		 String[] vValArr=vVal.split("-");
		 String vValFin=vValArr[3];
		 vValFin=vValFin.substring(0,2);
		 uValFin =uValFin+vValFin;
		
		 if(fVal=="B13"|| fVal=="B4" || fVal=="B66") {
			 
			 if(uValFin.equals("2t2r"))
			 {
				paTh ="select-ab" ;
			 }
			 else if(uValFin.equals("4t4r"))
			 {
				paTh ="select-abcd" ;
			 }
			 
			 
		 }
		 else if (fVal=="B5"||fVal=="B2" ) {
			 if(uValFin.equals("2t2r"))
			 {
				paTh ="select-ef" ;
			 }
			 else if(uValFin.equals("4t4r"))
			 {
				
				paTh ="select-efgh" ;
			 }
			 
		 
		
	 }
		 return paTh;
	 }

}