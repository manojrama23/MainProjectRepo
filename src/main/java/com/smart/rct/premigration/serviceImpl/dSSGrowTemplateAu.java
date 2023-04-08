package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
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
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.util.CommonUtil;

@Component
public class dSSGrowTemplateAu {
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
			if (CommonUtil.isValidObject(filetype) && filetype.equalsIgnoreCase("vDUCellGrow")) {
			String eNB4G = "";
			String NEID = "";
			String gNBID="";
			List<CIQDetailsModel> listCIQDetailsModelDay1 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
			HashMap<String, String> day1DataMap = new HashMap<>();
			String[] dataListDay1 = {"NEID", "vDU_Version","4GeNB" ,"vDU_Release", "NEName", "gNBID", "gNBIDLength", "gNBDUID", "gNBDUName", "EndpointCUIPaddress",
					"Network", "f1uAddress", "f1cGateway","flavor_id","FR1_Solution"};
			
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
			List<CIQDetailsModel> listFSUCIQ = fileUploadRepository.getEnbTableSheetDetailsss(ciqFileName, "FSUCIQ", eNB4G, dbcollectionFileName);
			List<CIQDetailsModel> listCIQDetailsModelDay2 = fileUploadRepository.getEnbTableSheetDetailsss(ciqFileName, "vDUDay_2", eNB4G, dbcollectionFileName);
			List<CIQDetailsModel> list_DSS_MOP_Parameters1 = fileUploadRepository.getEnbTableDetailsRanConfig2(ciqFileName, gNBID, enbName, dbcollectionFileName, "DSS_MOP_Parameters-1", "");
			List<CIQDetailsModel> list_DSS_MOP_Parameters = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, eNB4G, enbName, dbcollectionFileName, "DSS_MOP_Parameters-4", "");

			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			growFileName = "vDUCellGrow" + enbName + dateString + ".csv";
			HashMap<String, String> DSS4DataMap = new HashMap<>();
			HashMap<String, String> FSUDataHashMap = new HashMap<>();
			HashMap<String, String> dayDPDataMap = new HashMap<>();
			HashMap<String, String> dayDSSPDataMap = new HashMap<>();
			HashMap<String, String> day0DataMap = new HashMap<>();
			HashMap<String, String> day2DataHashMap = new HashMap<>();
			List<HashMap<String, String>> day2DataMap = new ArrayList<>();
			List<HashMap<String, String>> FSUDataMap = new ArrayList<>();
			List<HashMap<String, String>> DSS4HashDataMap = new ArrayList<>();
			Set<String> oranIdset = new HashSet<>();
			String[] DSS4Parameters = {"4GeNB","cell-num","target-cell-identity"};
			String[] FSUParameters = {"RU_PortID","4G_enB","Connected_DU_PortID","LCC_Card_Port","LCC_Card_Port2"};
			String[] DSSParameters = {"4GeNB","remote-ip-address"};
			String[] DSSParameters1 = {"4GeNB","remote-ip-address"};
			String[] dataListDay0 = {"cidr","gw","gw1","gw2","4GeNB","cidr1","cidr2","addr0","addr","addr4"};
			//String[] dataListDay1 = {"NEID", "vDU_Version","4GeNB" ,"vDU_Release", "NEName", "gNBID", "gNBIDLength", "gNBDUID", "gNBDUName", "EndpointCUIPaddress",
				//	"Network1", "f1uAddress", "f1cGateway"};
			String[] dataListDay2 = {"NEID", "sector-id","CarrierID","nr-arfcn-dl", "POD_PORT_ID", "RU_PORT_ID","vru-id", "vlanId", "oruId", "nr-arfcn-ul", "NRBandwidth", "nr_PCI",
					"dlAntennaCount", "ulAntennaCount", "numberRxPathsRU", "Cell_Path_Type", "cell-num",  "nrDLBandwidth","nr-physical-cell-id",
					"nrfrequency",  "Tracking_Area_Code_Usage", "Tracking_Area_Code", "connected-pod-id", "prachzczc", "prachrsi", "dl-antenna-count",
					"ul-antenna-count", "number-of-rx-paths-per-ru", "Dynamic_Spectrum_Sharing_Mode", "connected-pod-type", "power", "dynamicSpectrumSharingMode", "slotLevelOperationMode", "dssTargetLTECellNum", "endpointDSSIndex",
					"support-cell-number","4GeNB", "NR-Cell-Power","vlan-id", "FSU-ip ","connected-pod-port-id", "connected-fsu-port-id", "fsu-id","unit-type","sharing-enabled","Latitude","Height","Longitude"};
			
			
			
			
			
			String network = "";
			if(!ObjectUtils.isEmpty(list_DSS_MOP_Parameters )) {
				for(String key : DSS4Parameters) {
					if(list_DSS_MOP_Parameters .get(0).getCiqMap().containsKey(key)) {
						DSS4DataMap.put(key, list_DSS_MOP_Parameters .get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						DSS4DataMap.put(key, "");
					}
				}
			} else {
				for(String key : DSS4Parameters) {
					DSS4DataMap.put(key, "");
				}
			}

			if(!ObjectUtils.isEmpty(listFSUCIQ )) {
				for(String key : FSUParameters) {
					if(listFSUCIQ .get(0).getCiqMap().containsKey(key)) {
						FSUDataHashMap.put(key, listFSUCIQ .get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						FSUDataHashMap.put(key, "");
					}
				}
			} else {
				for(String key : FSUParameters) {
					FSUDataHashMap.put(key, "");
				}
			}
			
			if(!ObjectUtils.isEmpty(list_DSS_MOP_Parameters )) {
				for(String key : DSSParameters) {
					if(list_DSS_MOP_Parameters .get(0).getCiqMap().containsKey(key)) {
						dayDPDataMap.put(key, list_DSS_MOP_Parameters .get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						dayDPDataMap.put(key, "");
					}
				}
			} else {
				for(String key : DSSParameters1) {
					dayDPDataMap.put(key, "");
				}
			}
			
			
			
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
			}*/
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay2)) {
				for(String key : dataListDay2) {
					if(listCIQDetailsModelDay2.get(0).getCiqMap().containsKey(key)) {
						day2DataHashMap.put(key, listCIQDetailsModelDay2.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day2DataHashMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay2) {
					day2DataHashMap.put(key, "");
				}
			}
			
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay2)) {
				for(CIQDetailsModel ciqDetails : listCIQDetailsModelDay2) {
					HashMap<String, String> tempMap = new HashMap<>();
					for(String key : dataListDay2) {
						if(ciqDetails.getCiqMap().containsKey(key)) {
							tempMap.put(key, ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						} else {
							tempMap.put(key, "");
						}
						/*if(key.equals("oruId")) {
							oranIdset.add(ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						}*/
					}
					day2DataMap.add(tempMap);
				}
			}
			
			if(!ObjectUtils.isEmpty(list_DSS_MOP_Parameters)) {
				for(CIQDetailsModel ciqDetails : list_DSS_MOP_Parameters) {
					HashMap<String, String> tempMap = new HashMap<>();
					for(String key : DSS4Parameters) {
						if(ciqDetails.getCiqMap().containsKey(key)) {
							tempMap.put(key, ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						} else {
							tempMap.put(key, "");
						}
						/*if(key.equals("oruId")) {
							oranIdset.add(ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						}*/
					}
					DSS4HashDataMap.add(tempMap);
				}
			}
			
			if(!ObjectUtils.isEmpty(listFSUCIQ)) {
				for(CIQDetailsModel ciqDetails : listFSUCIQ) {
					HashMap<String, String> tempMap = new HashMap<>();
					for(String key : FSUParameters) {
						if(ciqDetails.getCiqMap().containsKey(key)) {
							tempMap.put(key, ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						} else {
							tempMap.put(key, "");
						}
					}
					FSUDataMap.add(tempMap);
				}
			}
			
			if(enbId.length() == 11) {
				network = enbId.substring(0, 3);
			} else if(enbId.length() == 10) {
				network = "0" + enbId.substring(0, 2);
			}
	   String  NeVersion=StringUtils.substringBefore(day1DataMap.get("vDU_Version"), "-");
	   
			if(NeVersion.equals("21.C.0"))
			{
			sb.append("\"@CELL_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
					+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"DPP ID\",\"PRACH RSI\",\"PRACH ZCZC\","
					+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
					+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"Power\",\"Dynamic Spectrum Sharing Mode\","
					+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"MV IO Site Migration Indicator\"\n");
			String cellnum="";
			String TrackAreaCodeUsage="";
			for(HashMap<String, String> data : day2DataMap) {
				
				if(data.get("4GeNB").equals(DSS4DataMap.get("4GeNB"))) {
					  int H= Integer.parseInt(data.get("CarrierID"));
					  int G=Integer.parseInt(data.get("sector-id"));  
					  int a=16;
				      int target=16*G + H +1;
				      for(HashMap<String, String> dataD : DSS4HashDataMap) {
				    	  int Targetresult= Integer.parseInt(dataD.get("target-cell-identity"));
				    	  if(Targetresult==target) {
				    		  cellnum =dataD.get("cell-num");
				      }
				      
				      
					     
				  }
				}
				if (data.get("Tracking_Area_Code_Usage").equals("not used")) {
					TrackAreaCodeUsage="not-use";
					
				}
				String NR_DL_Arfcn=bandSelection(data.get("nr-arfcn-dl"));
				String CellPath=cellPath(NR_DL_Arfcn,data.get("dl-antenna-count"), data.get("ul-antenna-count") );
				sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"" + data.get("cell-num") + "\"," 
						+ "\"" + data.get("nr-physical-cell-id") + "\"," + "\"" + data.get("nr-arfcn-dl") + "\"," + "\"" + data.get("nr-arfcn-ul") + "\"," 
						+ "\"" + data.get("NRBandwidth") + "\"," + "\"" + data.get("NRBandwidth") + "\"," + "\""+ data.get("nrfrequency") +"\"," 
						+ "\"" + TrackAreaCodeUsage+ "\"," + "\"" + data.get("Tracking_Area_Code") + "\"," + "\"" + data.get("connected-pod-id") + "\"," 
						+ "\"" + data.get("prachrsi") + "\"," + "\"" + data.get("prachzczc") + "\",\"16\"," 
						+ "\"ssb-per-ro-one-choice\"," + "\"unrestricted-set\"," + "\"auto-prach-rb-offset-off\"," 
						+ "\"tdd-configuration-8\"," + "\"" + data.get("dl-antenna-count") + "\"," + "\"" + data.get("ul-antenna-count") + "\","
						+ "\"" + data.get("number-of-rx-paths-per-ru") + "\"," + "\"" + CellPath + "\",\"1\",\"45\"," 
						+ "\"" + data.get("Dynamic_Spectrum_Sharing_Mode").toLowerCase() + "\"," + "\"slot-level-dss-mode\"," + "\"" + cellnum + "\"," 
						+ "\"0\"," + "\"config-option-0\"" + "\n");
			}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			sb.append("\"ADD\","+"\"" + day2DataHashMap.get("fsu-id")+"\","+"\""+ day2DataHashMap.get("FSU-ip ")+"\"\n");
			String ad="";
			for(HashMap<String, String> data : day2DataMap) {
				 ad += "/" + data.get("support-cell-number");
				
				 
			}
			 String suppotcell= ad.substring(1,ad.length());
			 
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"ADD\","+"\""+ day2DataHashMap.get("fsu-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ day2DataHashMap.get("connected-pod-port-id")+"\","+"\""+ day2DataHashMap.get("connected-fsu-port-id")+"\","+"\""+ day2DataHashMap.get("vlan-id")+"\","+"\""+ suppotcell+"\"\n");
			
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
			for(HashMap<String, String> data : day2DataMap) {
			sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+ data.get("unit-type").toLowerCase()+"\",\"-\","+"\"" + data.get("support-cell-number")+"\","
					+ ""+"\""+ data.get("sharing-enabled").toLowerCase()+"\",\"N 000:00:00.000\",\"E 000:00:00.000\",\"0000.00m\"\n");
			}
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			String RU_port = "";
			for(HashMap<String, String> data : day2DataMap) {
				for(HashMap<String, String> data1 : FSUDataMap) {
					System.out.println(data1.get("Connected_DU_PortID"));
					if(data.get("connected-fsu-port-id").equals(data1.get("Connected_DU_PortID"))) {
						System.out.println("MAtched");
						if(!data1.get("LCC_Card_Port").replaceAll("^0+(?!$)", "").isEmpty()) {
							System.out.println(data1.get("LCC_Card_Port"));
							 RU_port="0";
						}
						else if(!data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty()) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "1";
						}
						else if(!data1.get("LCC_Card_Port2").isEmpty() && !data1.get("LCC_Card_Port2").isEmpty() ) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "";
						}
						sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+ RU_port +"\","+"\"" + data.get("support-cell-number")+"\"\n");
						RU_port="";
					}
				}
				System.out.println("hi");
			//sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+FSUDataHashMap.get("RU_PortID")+"\","+"\"" + data.get("support-cell-number")+"\"\n");
			}
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
			sb.append("\"NONE\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"NONE\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
		
			
			
			sb.append("\"@SON_INFORMATION\"\n");
			sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
			sb.append("\"off\",\"off\",\"off\",\"off\"");
			}
			else if (NeVersion.contains("21.D")) {
				sb.append("\"@CELL_INFORMATION\"\n");
				sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
						+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"EMF Control Enable\",\"DPP ID\","
						+ "\"PRACH RSI\",\"PRACH ZCZC\","
						+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
						+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"NR MV IO Site Migration Indicator\""
						+ ",\"Power\",\"Dynamic Spectrum Sharing Mode\","
						+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"Max EIRP Selection Mode\"\n");
			String cellnum="";
			String TrackAreaCodeUsage="";
			for(HashMap<String, String> data : day2DataMap) {
					
					if(data.get("4GeNB").equals(DSS4DataMap.get("4GeNB"))) {
						  int H= Integer.parseInt(data.get("CarrierID"));
						  int G=Integer.parseInt(data.get("sector-id"));  
						  int a=16;
					      int target=16*G + H +1;
					      for(HashMap<String, String> dataD : DSS4HashDataMap) {
					    	  int Targetresult= Integer.parseInt(dataD.get("target-cell-identity"));
					    	  if(Targetresult==target) {
					    		  cellnum =dataD.get("cell-num");
					      }    
					  }
					}
					if (data.get("Tracking_Area_Code_Usage").equals("not used")) {
						TrackAreaCodeUsage="not-use";
						
					}
			    String NR_DL_Arfcn=bandSelection(data.get("nr-arfcn-dl"));
			    String CellPath=cellPath(NR_DL_Arfcn,data.get("dl-antenna-count"), data.get("ul-antenna-count") );
			    sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"" + data.get("cell-num") + "\"," 
							+ "\"" + data.get("nr-physical-cell-id") + "\"," + "\"" + data.get("nr-arfcn-dl") + "\"," + "\"" + data.get("nr-arfcn-ul") + "\"," 
							+ "\"" + data.get("NRBandwidth") + "\"," + "\"" + data.get("NRBandwidth") + "\"," + "\""+ data.get("nrfrequency") +"\"," 
							+ "\"" + TrackAreaCodeUsage + "\"," + "\"" + data.get("Tracking_Area_Code") + "\"," + "\"disable\"," +  "\"" + data.get("connected-pod-id") + "\"," 
							+ "\"" + data.get("prachrsi") + "\"," + "\"" + data.get("prachzczc") + "\",\"16\"," 
							+ "\"ssb-per-ro-one-choice\"," + "\"unrestricted-set\"," + "\"auto-prach-rb-offset-off\"," 
							+ "\"tdd-configuration-8\"," + "\"" + data.get("dl-antenna-count") + "\"," + "\"" + data.get("ul-antenna-count") + "\","
							+ "\"" + data.get("number-of-rx-paths-per-ru") + "\"," + "\"" + CellPath + "\",\"1\"," + "\"config-option-0\"," + "\"45\"," 
							+ "\"" + data.get("Dynamic_Spectrum_Sharing_Mode").toLowerCase() + "\"," + "\"slot-level-dss-mode\"," + "\"" + cellnum + "\"," 
							+ "\"0\","  + "\"sas-response-based\"" + "\n");
				}
			
			sb.append("\"@CELL_EMF_INFORMATION\"\n");
			//pending
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"EMF Grid Index\",\"EMF Power Density\",\"EMF Compliance Boundary\"\n");
			for(HashMap<String, String> data : day2DataMap) {		
				sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"0\",\"40\",\"20\"\n");
			}
			
			sb.append("\"@NR_CBRS_CHANNEL_PREFERENCE_INFORMATION\"\n");
			
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Preferred NR Arfcn\",\"Preferred Lowest Channel\",\"Preferred Highest Channel\",\"Preference\"\n");
			//for(HashMap<String, String> data : day2DataMap) {				
			sb.append("\"NONE\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			//}
			
			
			sb.append("\"@NR_CBRS_CHANNEL_BLOCK_LISTED_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Block Listed Channel\"\n");
			//for(HashMap<String, String> data : day2DataMap) {	
			sb.append("\"NONE\",\"\",\"\",\"\"\n");
			//}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			sb.append("\"ADD\","+"\"" + day2DataHashMap.get("fsu-id")+"\","+"\""+ day2DataHashMap.get("FSU-ip ")+"\"\n");
			
//			String ad="";
//			for(HashMap<String, String> data : day2DataMap) {
//				 ad += "/" + data.get("support-cell-number");
//				 
//			}
//			 String suppotcell= ad.substring(1,ad.length());
//			
//			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
//			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
//			sb.append("\"ADD\","+"\""+ day2DataHashMap.get("fsu-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ day2DataHashMap.get("connected-pod-port-id")+"\","+"\""+ day2DataHashMap.get("connected-fsu-port-id")+"\","+"\""+ day2DataHashMap.get("vlan-id")+"\","+"\""+ suppotcell+"\"\n");
			//pending
			String ad="";
			String adm = "";
			for(HashMap<String, String> data : day2DataMap) {
				int scn = Integer.parseInt(data.get("support-cell-number"));
				if(scn < 9) {
					ad += "/" + data.get("support-cell-number");
				} else if(scn > 8 && scn < 18) {
					adm += "/" + data.get("support-cell-number");
				}	 
			}
			 String suppotcell= ad.substring(1,ad.length());
			 String supportcellNew = "";
			 if(!adm.isEmpty() && adm != null) {
				 supportcellNew = adm.substring(1,adm.length());
			 }
			
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			
			if(!ad.isEmpty()) {
				sb.append("\"ADD\","+"\""+ day2DataHashMap.get("fsu-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "0"+"\","+"\""+ "0" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\","+"\""+ suppotcell+"\"\n");
			}
			if(!adm.isEmpty()) {
				sb.append("\"ADD\","+"\""+ day2DataHashMap.get("fsu-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "1" +"\","+"\""+ "1" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\","+"\""+ supportcellNew+"\"\n");
			
			}
			
			
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Uncertainty Semi Major\","
					+ "\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\",\"Latitude\",\"Longitude\",\"Height\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+ data.get("unit-type").toLowerCase()+"\",\"-\","+"\"" + data.get("support-cell-number")+"\","
						+ ""+"\""+ data.get("sharing-enabled").toLowerCase()+"\"," + "\""+"8" +"\"," + "\""+"8" +"\"," + "\""+"0" +"\"," + "\""+"0" +"\"," + "\""+"100"
						+"\",\"N 000:00:00.000\",\"E 000:00:00.000\",\"0000.00m\"\n");
			}
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			 String RU_port = "";
			for(HashMap<String, String> data : day2DataMap) {
				for(HashMap<String, String> data1 : FSUDataMap) {
					System.out.println(data1.get("Connected_DU_PortID"));
					if(data.get("connected-fsu-port-id").equals(data1.get("Connected_DU_PortID"))) {
						System.out.println("MAtched");
						if(!(data1.get("LCC_Card_Port").replaceAll("^0+(?!$)", "").isEmpty())) {
							System.out.println(data1.get("LCC_Card_Port"));
							 RU_port="0";
						}
						else if(!(data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty())) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "1";
						}
						else if(!data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty() && !data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty() ) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "";
						}
						sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+ RU_port +"\","+"\"" + data.get("support-cell-number")+"\"\n");
						RU_port="";
					}
				}
			}
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Uncertainty Semi Major\","
					+ "\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\",\"Latitude\",\"Longitude\",\"Height\","
					+ "\"Azimuth\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\"\n");
		    sb.append("\"NONE\"," + "\"\"," + "\"oru\"," + "\"\"," + "\"\"," 
						+ "\"\"," + "\"\"," + "\"8\"," + "\""+"8" +"\"," + "\""+"0" +"\"," + "\""+"0" +"\"," + "\""+"100" 
						+"\",\"N 000:00:00.000\",\"E 000:00:00.000\",\"0000.00m\",\"-1\",\"true\",\"130\",\"0\",\"10\""+"\n");
		    
		    sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"NONE\"," + "\"\"," + "\"\",\"\"," + "\"\"," + "\"\","  + "\"\"," + "\"\"" + "\n");

			sb.append("\"@SON_INFORMATION\"\n");
			sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
			sb.append("\"off\",\"off\",\"off\",\"off\"");
				
			}else if (NeVersion.contains("22.A")) {
				sb.append("\"@CELL_INFORMATION\"\n");
				sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
						+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"EMF Control Enable\",\"DPP ID\","
						+ "\"PRACH RSI\",\"PRACH ZCZC\","
						+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
						+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"NR MV IO Site Migration Indicator\""
						+ ",\"Power\",\"Dynamic Spectrum Sharing Mode\","
						+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"Max EIRP Selection Mode\",\"HST Flag\"\n");
			String cellnum="";
			String TrackAreaCodeUsage="";
			for(HashMap<String, String> data : day2DataMap) {
					
					if(data.get("4GeNB").equals(DSS4DataMap.get("4GeNB"))) {
						  int H= Integer.parseInt(data.get("CarrierID"));
						  int G=Integer.parseInt(data.get("sector-id"));  
						  int a=16;
					      int target=16*G + H +1;
					      for(HashMap<String, String> dataD : DSS4HashDataMap) {
					    	  int Targetresult= Integer.parseInt(dataD.get("target-cell-identity"));
					    	  if(Targetresult==target) {
					    		  cellnum =dataD.get("cell-num");
					      }    
					  }
					}
					String DynamicSpectrum= data.get("Dynamic_Spectrum_Sharing_Mode").toLowerCase();
					if(day1DataMap.containsKey("FR1_Solution")&&!day1DataMap.get("FR1_Solution").isEmpty()) {
						System.out.println("DynamicSpectrum :"+day1DataMap.get("FR1_Solution"));
						if(day1DataMap.get("FR1_Solution").equalsIgnoreCase("CleanNR")) {
							cellnum="0";
							DynamicSpectrum="false";
						}
					}
					if (data.get("Tracking_Area_Code_Usage").equals("not used")) {
						TrackAreaCodeUsage="not-use";
						
					}
			    String NR_DL_Arfcn=bandSelection(data.get("nr-arfcn-dl"));
			    String CellPath=cellPath(NR_DL_Arfcn,data.get("dl-antenna-count"), data.get("ul-antenna-count") );
			    sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"" + data.get("cell-num") + "\"," 
							+ "\"" + data.get("nr-physical-cell-id") + "\"," + "\"" + data.get("nr-arfcn-dl") + "\"," + "\"" + data.get("nr-arfcn-ul") + "\"," 
							+ "\"" + data.get("NRBandwidth") + "\"," + "\"" + data.get("NRBandwidth") + "\"," + "\""+ data.get("nrfrequency") +"\"," 
							+ "\"" + TrackAreaCodeUsage + "\"," + "\"" + data.get("Tracking_Area_Code") + "\"," + "\"disable\"," +  "\"" + data.get("connected-pod-id") + "\"," 
							+ "\"" + data.get("prachrsi") + "\"," + "\"" + data.get("prachzczc") + "\",\"16\"," 
							+ "\"ssb-per-ro-one-choice\"," + "\"unrestricted-set\"," + "\"manual-config-low\"," 
							+ "\"tdd-configuration-8\"," + "\"" + data.get("dl-antenna-count") + "\"," + "\"" + data.get("ul-antenna-count") + "\","
							+ "\"" + data.get("number-of-rx-paths-per-ru") + "\"," + "\"" + CellPath + "\",\"1\"," + "\"config-option-0\"," + "\"45\"," 
							+ "\"" + DynamicSpectrum + "\"," + "\"slot-level-dss-mode\"," + "\"" + cellnum + "\"," 
							+ "\"0\","  + "\"sas-response-based\"," + "\"ul-hst-cell-enable-off\"\n");
				}
			
			sb.append("\"@CELL_EMF_INFORMATION\"\n");
			//pending
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"EMF Grid Index\",\"EMF Power Density\",\"EMF Compliance Boundary\"\n");
			sb.append("\"NONE\",\"\",\"\",\"\",\"\",\"\"\n");
			/*for(HashMap<String, String> data : day2DataMap) {		
				sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"0\",\"40\",\"20\"\n");
			}*/
			
			sb.append("\"@NR_CBRS_CHANNEL_PREFERENCE_INFORMATION\"\n");
			
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Preferred NR Arfcn\",\"Preferred Lowest Channel\",\"Preferred Highest Channel\",\"Preference\"\n");
			//for(HashMap<String, String> data : day2DataMap) {				
			sb.append("\"NONE\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			//}
			
			
			sb.append("\"@NR_CBRS_CHANNEL_BLOCK_LISTED_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Block Listed Channel\"\n");
			//for(HashMap<String, String> data : day2DataMap) {	
			sb.append("\"NONE\",\"\",\"\",\"\"\n");
			//}
			
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"Sub Type\",\"FHM Mode\",\"RF Operation Mode\",\"FSU Mode\",\"FSU NE ID\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Uncertainty Semi Major\","
					+ "\"Uncertainty Semi Minor\",\"Orientation Of Major Axis\",\"Uncertainty Altitude\",\"Confidence\",\"Latitude\",\"Longitude\",\"Height\","
					+ "\"Azimuth\",\"X Pole Antenna\",\"Antenna Gain dBi\",\"Cable Loss\",\"Accuracy Margin dB\"\n");
			String fsuID="";
			for(HashMap<String, String> data : day2DataMap) {
				fsuID=data.get("fsu-id");
			
				 sb.append("\"ADD\"," + "\""+ data.get("vru-id")+"\"," + "\"oru\","+"\"" +"0" +"\","+ "\"\"," + "\"\"," +"\"true\","+"\""+ fsuID+ "\","
							+ "\"none\"," + "\""+data.get("support-cell-number")+"\"," + "\"8\"," + "\""+"8" +"\"," + "\""+"0" +"\"," + "\""+"0" +"\"," + "\""+"100" 
										+"\",\"N 000:00:00.000\",\"E 000:00:00.000\",\"0000.00m\",\"-1\",\"true\",\"130\",\"0\",\"10\""+"\n");
			}
			
			
//			
			String ad="";
			String adm = "";
			for(HashMap<String, String> data : day2DataMap) {
				int scn = Integer.parseInt(data.get("support-cell-number"));
				if(scn < 9) {
					ad += "/" + data.get("support-cell-number");
				} else if(scn > 8 && scn < 18) {
					adm += "/" + data.get("support-cell-number");
				}	 
			}
			 String suppotcell= ad.substring(1,ad.length());
			 String supportcellNew = "";
			 if(!adm.isEmpty() && adm != null) {
				 supportcellNew = adm.substring(1,adm.length());
			 }
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Connected FSU Port ID\",\"Support Cell Number\"\n");
			for(HashMap<String, String> data : day2DataMap) {
			if(day1DataMap.get("flavor_id").equalsIgnoreCase("CascadeLake")){
			if(!ad.isEmpty()) {
				sb.append("\"ADD\","+"\""+ data.get("vru-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "0"+"\","+"\""+ "" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\",\"0\","+"\""+ data.get("support-cell-number")+"\"\n");
			}
			if(!adm.isEmpty()) {
				sb.append("\"ADD\","+"\""+ data.get("vru-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "1" +"\","+"\""+ "" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\",\"1\","+"\""+ data.get("support-cell-number")+"\"\n");
			
			}
			}
			else {
				if(!ad.isEmpty()) {
					sb.append("\"ADD\","+"\""+ data.get("vru-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "0"+"\","+"\""+ "" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\",\"1\","+"\""+  data.get("support-cell-number")+"\"\n");
				}
				if(!adm.isEmpty()) {
					sb.append("\"ADD\","+"\""+ data.get("vru-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ "2" +"\","+"\""+ "" +"\","+"\""+ day2DataHashMap.get("vlan-id")+"\",\"0\","+"\""+  data.get("support-cell-number")+"\"\n");
				
				}
			}
			
			}
			
			
			
		    
		    sb.append("\"@ORU_FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected fsu RU CPRI Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			 String RU_port = "";
			for(HashMap<String, String> data : day2DataMap) {
				for(HashMap<String, String> data1 : FSUDataMap) {
					System.out.println(data1.get("Connected_DU_PortID"));
					if(data.get("connected-fsu-port-id").equals(data1.get("Connected_DU_PortID"))) {
						System.out.println("MAtched");
						if(!(data1.get("LCC_Card_Port").replaceAll("^0+(?!$)", "").isEmpty())) {
							System.out.println(data1.get("LCC_Card_Port"));
							 RU_port="0";
						}
						else if(!(data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty())) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "1";
						}
						else if(!data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty() && !data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty() ) {
							System.out.println(data1.get("LCC_Card_Port2"));
							  RU_port = "";
						}
						sb.append("\"ADD\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+ RU_port +"\","+"\"" + data.get("support-cell-number")+"\"\n");
						RU_port="";
					}
				}
			}
		   
			String f1ugw=day2DataHashMap.get("FSU-ip ");
			String a=f1ugw.substring(0,f1ugw.length()-1);
            String b=f1ugw.substring(f1ugw.length()-1,f1ugw.length());
            int c=0;
            if(NumberUtils.isNumber(b)) {
             c=Integer.parseInt(b);
             c++;
            }
            String d=String.valueOf(c);
            String e=a+d;
			sb.append("\"@ORU_FSU_IP_RANGE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU NE ID\",\"First MPlane IP\"\n");
			sb.append("\"ADD\","+"\"" + day2DataHashMap.get("fsu-id")+"\","+"\""+e+"\"\n");

			sb.append("\"@SON_INFORMATION\"\n");
			sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
			sb.append("\"off\",\"off\",\"off\",\"off\"");
				
			}

		else {	
			sb.append("\"@CELL_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
					+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"DPP ID\",\"PRACH RSI\",\"PRACH ZCZC\","
					+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
					+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Power\",\"Dynamic Spectrum Sharing Mode\","
					+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"MV IO Site Migration Indicator\"\n");
			String cellnum="";
			String TrackAreaCodeUsage="";
			for(HashMap<String, String> data : day2DataMap) {
				
				if(data.get("4GeNB").equals(DSS4DataMap.get("4GeNB"))) {
					  int H= Integer.parseInt(data.get("CarrierID"));
					  int G=Integer.parseInt(data.get("sector-id"));  
					  int a=16;
				      int target=16*G + H +1;
				      for(HashMap<String, String> dataD : DSS4HashDataMap) {
				    	  int Targetresult= Integer.parseInt(dataD.get("target-cell-identity"));
				    	  if(Targetresult==target) {
				    		  cellnum =dataD.get("cell-num");
				      }
				      
				      
					     
				  }
				}
				if (data.get("Tracking_Area_Code_Usage").equals("not used")) {
					TrackAreaCodeUsage="not-use";
					
				}
				String NR_DL_Arfcn=bandSelection(data.get("nr-arfcn-dl"));
				String CellPath=cellPath(NR_DL_Arfcn,data.get("dl-antenna-count"), data.get("ul-antenna-count") );
				sb.append("\"ADD\"," + "\"" + data.get("sector-id") + "\"," + "\"" + data.get("CarrierID") + "\"," + "\"" + data.get("cell-num") + "\"," 
						+ "\"" + data.get("nr-physical-cell-id") + "\"," + "\"" + data.get("nr-arfcn-dl") + "\"," + "\"" + data.get("nr-arfcn-ul") + "\"," 
						+ "\"" + data.get("NRBandwidth") + "\"," + "\"" + data.get("NRBandwidth") + "\"," + "\""+ data.get("nrfrequency") +"\"," 
						+ "\"" + TrackAreaCodeUsage + "\"," + "\"" + data.get("Tracking_Area_Code") + "\"," + "\"" + data.get("connected-pod-id") + "\"," 
						+ "\"" + data.get("prachrsi") + "\"," + "\"" + data.get("prachzczc") + "\",\"16\"," 
						+ "\"ssb-per-ro-one-choice\"," + "\"unrestricted-set\"," + "\"auto-prach-rb-offset-off\"," 
						+ "\"tdd-configuration-8\"," + "\"" + data.get("dl-antenna-count") + "\"," + "\"" + data.get("ul-antenna-count") + "\","
						+ "\"" + data.get("number-of-rx-paths-per-ru") + "\"," + "\"" + CellPath + "\",\"45\"," 
						+ "\"" + data.get("Dynamic_Spectrum_Sharing_Mode").toLowerCase() + "\"," + "\"slot-level-dss-mode\"," + "\"" + cellnum + "\"," 
						+ "\"0\"," + "\"config-option-0\"" + "\n");
			}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			 sb.append("\"ADD\","+"\"" + day2DataHashMap.get("fsu-id")+"\","+"\""+ day2DataHashMap.get("FSU-ip ")+"\"\n");
			String ad="";
			for(HashMap<String, String> data : day2DataMap) {
				 ad += "/" + data.get("support-cell-number");
				 
			}
			 String suppotcell= ad.substring(1,ad.length());
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			 sb.append("\"ADD\","+"\""+ day2DataHashMap.get("fsu-id")+"\","+"\"" + day2DataHashMap.get("connected-pod-type")+"\",\"0\","+"\""+ day2DataHashMap.get("connected-pod-port-id")+"\","+"\""+ day2DataHashMap.get("connected-fsu-port-id")+"\","+"\""+ day2DataHashMap.get("vlan-id")+"\","+"\""+ suppotcell+"\"\n");
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
			for(HashMap<String, String> data : day2DataMap) {
			sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+ data.get("unit-type").toLowerCase()+"\",\"-\","+"\"" + data.get("support-cell-number")+"\","
					+ ""+"\""+ data.get("sharing-enabled").toLowerCase()+"\",\"N 000:00:00.000\",\"E 000:00:00.000\",\"0000.00m\"\n");
			}
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			 String RU_port = "";
				for(HashMap<String, String> data : day2DataMap) {
					for(HashMap<String, String> data1 : FSUDataMap) {
						System.out.println(data1.get("Connected_DU_PortID"));
						if(data.get("connected-fsu-port-id").equals(data1.get("Connected_DU_PortID"))) {
							System.out.println("MAtched");
							if(!data1.get("LCC_Card_Port").replaceAll("^0+(?!$)", "").isEmpty()) {
								System.out.println(data1.get("LCC_Card_Port"));
								 RU_port="0";
							}
							else if(!data1.get("LCC_Card_Port2").replaceAll("^0+(?!$)", "").isEmpty()) {
								System.out.println(data1.get("LCC_Card_Port2"));
								  RU_port = "1";
							}
							else if(!data1.get("LCC_Card_Port2").isEmpty() && !data1.get("LCC_Card_Port2").isEmpty() ) {
								System.out.println(data1.get("LCC_Card_Port2"));
								  RU_port = "";
							}
							sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+ RU_port +"\","+"\"" + data.get("support-cell-number")+"\"\n");
							RU_port="";
						}
					}
					System.out.println("hi");
				//sb.append("\"ADD\","+"\""+ data.get("fsu-id")+ "\","+"\""+data.get("vru-id")+"\","+"\""+data.get("connected-fsu-port-id")+"\","+"\""+FSUDataHashMap.get("RU_PortID")+"\","+"\"" + data.get("support-cell-number")+"\"\n");
				}
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
		    sb.append("\"NONE\",\"\",\"\",\"\"," + "\"\"," + "\"\"," + "\"\"," + "\"\"," 
						+ "\"\"," + "\"\"" + "\n");
		
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"NONE\"," + "\"\"," + "\"\",\"\"," + "\"\"," + "\"\","  + "\"\"," + "\"\"" + "\n");
		
			
			
			sb.append("\"@SON_INFORMATION\"\n");
			sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
			sb.append("\"off\",\"off\",\"off\",\"off\"");
		}}
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