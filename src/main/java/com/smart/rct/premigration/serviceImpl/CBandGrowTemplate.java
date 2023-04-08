package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import com.smart.rct.util.CommonUtil;

import io.micrometer.core.instrument.util.DoubleFormat;

@Component
public class CBandGrowTemplate {
	static final Logger logger = LoggerFactory.getLogger(CBandGrowTemplate.class);
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	public JSONObject generateGrowTemplate(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder sb = new StringBuilder();
		StringBuilder fileBuilder = new StringBuilder();
		String vDUpnpGrowFileName = "";
		try {
			
			List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "Day0_1", "");
			List<CIQDetailsModel> listCIQDetailsModelDay2 = fileUploadRepository.getEnbTableSheetDetailss(ciqFileName, "Day2", enbId, dbcollectionFileName);
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			 if (CommonUtil.isValidObject(filetype) && filetype.equalsIgnoreCase("vDUpnp")) {
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			vDUpnpGrowFileName = "vDU_pnp_GrowTemplate_" + enbName + dateString + ".csv";
			
			HashMap<String, String> day1DataMap = new HashMap<>();
			List<HashMap<String, String>> day2DataMap = new ArrayList<>();
			Set<String> oranIdset = new HashSet<>();
			
			String[] dataListDay1 = {"NEID", "eNB_Version", "vDU_Release", "neName", "gnb_ID", "gNBIDLength", "gnbDuId", "gnbDuName", "cuIPaddress",
					"f1cAddress", "f1uAddress", "f1cGateway","network"};
			String[] dataListDay2 = {"NEID", "oruSupportCellNumber", "POD_PORT_ID", "RU_PORT_ID", "vlanId", "oruId", "nrDl_Arfcn", "nrUl_Arfcn", "nr_PCI",
					"dlAntennaCount", "ulAntennaCount", "numberRxPathsRU", "Cell_Path_Type", "cellNum", "sectorID", "carrierID", "nrDLBandwidth",
					"nrULBandwidth", "nrFrequencyBand", "trackingAreaCodeUsage", "trackingAreaCode", "dpp_ID", "prachRsi", "prachZczc", "prachConfigIndex",
					"prachSSBPerRO", "restrictedSet", "prachRBOffsetAutoConfig", "tddRatio", "power", "dynamicSpectrumSharingMode", "slotLevelOperationMode", "dssTargetLTECellNum", "endpointDSSIndex",
					"multivendorInterOpSiteDSS", "AntennaAzimuth", "latitude2", "longitude2", "height2"};
			
			String network = "";
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay01)) {
				for(String key : dataListDay1) {
					if(listCIQDetailsModelDay01.get(0).getCiqMap().containsKey(key)) {
						day1DataMap.put(key, listCIQDetailsModelDay01.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day1DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay1) {
					day1DataMap.put(key, "");
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
						if(key.equals("oruId")) {
							oranIdset.add(ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						}
					}
					day2DataMap.add(tempMap);
				}
			}
			/*
			if(enbId.length() == 11) {
				network = enbId.substring(0, 3);
			} else if(enbId.length() == 10) {
				network = "0" + enbId.substring(0, 2);
			}*/
			String NeVersion=StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-");
			sb.append("\"@ADPF\"\n");
			sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
					+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\","
					+ "\"NE Serial Number\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-") + "\"," 
					+ "\"" + day1DataMap.get("vDU_Release") + "\","  + "\"" + day1DataMap.get("network") + "\"," + "\"" + day1DataMap.get("neName") + "\"," 
					+ "\"" + "\"," + "\"" + day1DataMap.get("gnb_ID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gnbDuId") + "\"," 
					+ "\"" + day1DataMap.get("gnbDuName") + "\"," + "\"" + day1DataMap.get("cuIPaddress") + "\"," + "\"" + "0" + "\"," + "\"" + "\"" + "\n");
			
			sb.append("\"@SERVER_INFORMATION\"\n");
			sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"1500\"" + "\n");
			}
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"3\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"4\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"5\",\"unlocked\",\"9000\"" + "\n");
			}
			
			sb.append("\"@VLAN_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"VLAN ID\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@EXTERNAL_IP_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"Carrier Aggregation\",\"Mplane\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1cAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1uAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			
			String ip = "";
			List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "dpp_ip", "mh1");
			if(!ObjectUtils.isEmpty(auditConstantsList)) {
				ip = auditConstantsList.get(0).getParameterValue().trim();
			}
			
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			if(oranIdset.contains("0")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh0");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh1");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh2");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh2\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			
			sb.append("\"@ROUTE_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			
			if (NeVersion.equals("21.C.0")||NeVersion.equals("21.D.0")) {
				
			sb.append("\"@CELL_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
					+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"DPP ID\",\"PRACH RSI\",\"PRACH ZCZC\","
					+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
					+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"Power\",\"Dynamic Spectrum Sharing Mode\","
					+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"MV IO Site Migration Indicator\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
						+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
						+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
						+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
						+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
						+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
						+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
						+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\",\"1\"," + "\"" + data.get("power") + "\"," 
						+ "\"" + data.get("dynamicSpectrumSharingMode").toLowerCase() + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
						+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
			}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String serialNumber = "";
				if(data.get("oruId").equals("0")) {
					serialNumber = "fh0";
				} else if(data.get("oruId").equals("1")) {
					serialNumber = "fh1";					
				}else if(data.get("oruId").equals("2")) {
					serialNumber = "fh2";					
				}
				String i= data.get("height2");
				DecimalFormat deci= new DecimalFormat("0.00");
				Double he =Double.parseDouble(i)/1.0;
				Double he1 = Double.parseDouble(deci.format(he)) + 0.00;
				String h=String.format("%.2f",he1)+"m";
				sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
						+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude2") + "\"," + "\"" + data.get("longitude2") + "\"," 
						+ "\"" + h + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
			}
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String[] podportid = data.get("POD_PORT_ID").split(",");
				String[] ruportid = data.get("RU_PORT_ID").split(",");
				if(podportid.length == ruportid.length) {
					for(int i=0; i<podportid.length; i++) {
						sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
								+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
					}
				}				
			}
			
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
				for(HashMap<String, String> data : day2DataMap) {
					sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
							+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
							+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
							+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
							+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
							+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
							+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
							+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\"," + "\"" + data.get("power") + "\"," 
							+ "\"" + data.get("dynamicSpectrumSharingMode").toLowerCase() + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
							+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
				}
				
				sb.append("\"@FSU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
				sb.append("\"\",\"\",\"\"\n");
				
				sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@ORU_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String serialNumber = "";
					if(data.get("oruId").equals("0")) {
						serialNumber = "fh0";
					} else if(data.get("oruId").equals("1")) {
						serialNumber = "fh1";					
					}else if(data.get("oruId").equals("2")) {
						serialNumber = "fh2";					
					}
					String i= data.get("height2");
					DecimalFormat deci= new DecimalFormat("0.00");
					Double he =Double.parseDouble(i)/1.0;
					Double he1 = Double.parseDouble(deci.format(he)) + 0.00;
					String h=String.format("%.2f",he1)+"m";
					sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
							+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude2") + "\"," + "\"" + data.get("longitude2") + "\"," 
							+ "\"" + h + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
				}
				
				sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String[] podportid = data.get("POD_PORT_ID").split(",");
					String[] ruportid = data.get("RU_PORT_ID").split(",");
					if(podportid.length == ruportid.length) {
						for(int i=0; i<podportid.length; i++) {
							sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
									+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
						}
					}				
				}
				
				sb.append("\"@SON_INFORMATION\"\n");
				sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
				sb.append("\"off\",\"off\",\"off\",\"off\"");
			}}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + vDUpnpGrowFileName);
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
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGenerationFSU() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", vDUpnpGrowFileName);

		}
		return fileGenerateResult;
	}
	public JSONObject generateGrowTemplateForCell(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder sb = new StringBuilder();
		StringBuilder fileBuilder = new StringBuilder();
		String vDUcellGrowFileName = "";
		try {
			
			List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "Day0_1", "");
			List<CIQDetailsModel> listCIQDetailsModelDay2 = fileUploadRepository.getEnbTableSheetDetailss(ciqFileName, "Day2", enbId, dbcollectionFileName);
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			 if (CommonUtil.isValidObject(filetype) && filetype.equalsIgnoreCase("vDUcell")) {
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			vDUcellGrowFileName = "vDU_Cell_GrowTemplate_" + enbName + dateString + ".csv";
			
			HashMap<String, String> day1DataMap = new HashMap<>();
			List<HashMap<String, String>> day2DataMap = new ArrayList<>();
			Set<String> oranIdset = new HashSet<>();
			
			String[] dataListDay1 = {"NEID", "eNB_Version", "vDU_Release", "neName", "gnb_ID", "gNBIDLength", "gnbDuId", "gnbDuName", "cuIPaddress",
					"f1cAddress", "f1uAddress", "f1cGateway"};
			String[] dataListDay2 = {"NEID", "oruSupportCellNumber", "POD_PORT_ID", "RU_PORT_ID", "vlanId", "oruId", "nrDl_Arfcn", "nrUl_Arfcn", "nr_PCI",
					"dlAntennaCount", "ulAntennaCount", "numberRxPathsRU", "Cell_Path_Type", "cellNum", "sectorID", "carrierID", "nrDLBandwidth",
					"nrULBandwidth", "nrFrequencyBand", "trackingAreaCodeUsage", "trackingAreaCode", "dpp_ID", "prachRsi", "prachZczc", "prachConfigIndex",
					"prachSSBPerRO", "restrictedSet", "prachRBOffsetAutoConfig", "tddRatio", "power", "dynamicSpectrumSharingMode", "slotLevelOperationMode", "dssTargetLTECellNum", "endpointDSSIndex",
					"multivendorInterOpSiteDSS", "AntennaAzimuth", "latitude2", "longitude2", "height2"};
			
			String network = "";
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay01)) {
				for(String key : dataListDay1) {
					if(listCIQDetailsModelDay01.get(0).getCiqMap().containsKey(key)) {
						day1DataMap.put(key, listCIQDetailsModelDay01.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day1DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay1) {
					day1DataMap.put(key, "");
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
						if(key.equals("oruId")) {
							oranIdset.add(ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
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
			String NeVersion=StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-");
			/*sb.append("\"@ADPF\"\n");
			sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
					+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\","
					+ "\"NE Serial Number\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-") + "\"," 
					+ "\"" + day1DataMap.get("vDU_Release") + "\","  + "\"" + network + "\"," + "\"" + day1DataMap.get("neName") + "\"," 
					+ "\"" + "\"," + "\"" + day1DataMap.get("gnb_ID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gnbDuId") + "\"," 
					+ "\"" + day1DataMap.get("gnbDuName") + "\"," + "\"" + day1DataMap.get("cuIPaddress") + "\"," + "\"" + "0" + "\"," + "\"" + "\"" + "\n");
			
			sb.append("\"@SERVER_INFORMATION\"\n");
			sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"1500\"" + "\n");
			}
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"3\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"4\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"5\",\"unlocked\",\"9000\"" + "\n");
			}
			
			sb.append("\"@VLAN_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"VLAN ID\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@EXTERNAL_IP_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"Carrier Aggregation\",\"Mplane\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1cAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1uAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			
			String ip = "";
			List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "dpp_ip", "mh1");
			if(!ObjectUtils.isEmpty(auditConstantsList)) {
				ip = auditConstantsList.get(0).getParameterValue().trim();
			}
			
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			if(oranIdset.contains("0")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh0");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh1");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh2");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh2\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			}
			
			sb.append("\"@ROUTE_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			*/
			if (NeVersion.equals("21.C.0")||NeVersion.equals("21.D.0")) {
				
			sb.append("\"@CELL_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
					+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"DPP ID\",\"PRACH RSI\",\"PRACH ZCZC\","
					+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
					+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"Power\",\"Dynamic Spectrum Sharing Mode\","
					+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"MV IO Site Migration Indicator\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
						+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
						+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
						+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
						+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
						+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
						+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
						+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\",\"1\"," + "\"" + data.get("power") + "\"," 
						+ "\"" + data.get("dynamicSpectrumSharingMode").toLowerCase() + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
						+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
			}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String serialNumber = "";
				if(data.get("oruId").equals("0")) {
					serialNumber = "fh0";
				} else if(data.get("oruId").equals("1")) {
					serialNumber = "fh1";					
				}else if(data.get("oruId").equals("2")) {
					serialNumber = "fh2";					
				}
				String i= data.get("height2");
				DecimalFormat deci= new DecimalFormat("0.00");
				Double he =Double.parseDouble(i)/1.0;
				Double he1 = Double.parseDouble(deci.format(he)) + 0.00;
				String h=String.format("%.2f",he1)+"m";
				sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
						+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude2") + "\"," + "\"" + data.get("longitude2") + "\"," 
						+ "\"" + h + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
				//String h=String.format("%.2f",he1)+"m";System.out.println(he1);
			}
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String[] podportid = data.get("POD_PORT_ID").split(",");
				String[] ruportid = data.get("RU_PORT_ID").split(",");
				if(podportid.length == ruportid.length) {
					for(int i=0; i<podportid.length; i++) {
						sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
								+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
					}
				}				
			}
			
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
				for(HashMap<String, String> data : day2DataMap) {
					sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
							+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
							+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
							+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
							+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
							+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
							+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
							+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\"," + "\"" + data.get("power") + "\"," 
							+ "\"" + data.get("dynamicSpectrumSharingMode").toLowerCase() + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
							+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
				}
				
				sb.append("\"@FSU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
				sb.append("\"\",\"\",\"\"\n");
				
				sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@ORU_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String serialNumber = "";
					if(data.get("oruId").equals("0")) {
						serialNumber = "fh0";
					} else if(data.get("oruId").equals("1")) {
						serialNumber = "fh1";					
					}else if(data.get("oruId").equals("2")) {
						serialNumber = "fh2";					
					}
					String i= data.get("height2");
					DecimalFormat deci= new DecimalFormat("0.00");
					Double he =Double.parseDouble(i)/1.0;
					Double he1 = Double.parseDouble(deci.format(he)) + 0.00;
					String h=String.format("%.2f",he1)+"m";
					sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
							+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude2") + "\"," + "\"" + data.get("longitude2") + "\"," 
							+ "\"" + h + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
					System.out.println(h);
				}
				
				sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String[] podportid = data.get("POD_PORT_ID").split(",");
					String[] ruportid = data.get("RU_PORT_ID").split(",");
					if(podportid.length == ruportid.length) {
						for(int i=0; i<podportid.length; i++) {
							sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
									+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
						}
					}				
				}
				
				sb.append("\"@SON_INFORMATION\"\n");
				sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
				sb.append("\"off\",\"off\",\"off\",\"off\"");
			}}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + vDUcellGrowFileName);
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
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGenerationFSU() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", vDUcellGrowFileName);

		}
		return fileGenerateResult;
	}
	public JSONObject generateGrowTemplateForADPF(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder sb = new StringBuilder();
		StringBuilder fileBuilder = new StringBuilder();
		String vDUGrowFileName = "";
		try {
			
			List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "Day0_1", "");
			List<CIQDetailsModel> listCIQDetailsModelDay2 = fileUploadRepository.getEnbTableSheetDetailss(ciqFileName, "Day2", enbId, dbcollectionFileName);
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			 if (CommonUtil.isValidObject(filetype) && filetype.equalsIgnoreCase("vDUgrow")) {
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			vDUGrowFileName = "vDU_Grow_GrowTemplate_" + enbName + dateString + ".csv";
			
			HashMap<String, String> day1DataMap = new HashMap<>();
			List<HashMap<String, String>> day2DataMap = new ArrayList<>();
			Set<String> oranIdset = new HashSet<>();
			
			String[] dataListDay1 = {"NEID", "eNB_Version", "vDU_Release", "neName", "gnb_ID", "gNBIDLength", "gnbDuId", "gnbDuName", "cuIPaddress",
					"f1cAddress", "f1uAddress", "f1cGateway","network"};
			String[] dataListDay2 = {"NEID", "oruSupportCellNumber", "POD_PORT_ID", "RU_PORT_ID", "vlanId", "oruId", "nrDl_Arfcn", "nrUl_Arfcn", "nr_PCI",
					"dlAntennaCount", "ulAntennaCount", "numberRxPathsRU", "Cell_Path_Type", "cellNum", "sectorID", "carrierID", "nrDLBandwidth",
					"nrULBandwidth", "nrFrequencyBand", "trackingAreaCodeUsage", "trackingAreaCode", "dpp_ID", "prachRsi", "prachZczc", "prachConfigIndex",
					"prachSSBPerRO", "restrictedSet", "prachRBOffsetAutoConfig", "tddRatio", "power", "dynamicSpectrumSharingMode", "slotLevelOperationMode", "dssTargetLTECellNum", "endpointDSSIndex",
					"multivendorInterOpSiteDSS", "AntennaAzimuth", "latitude2", "longitude2", "height2"};
			
			String network = "";
			if(!ObjectUtils.isEmpty(listCIQDetailsModelDay01)) {
				for(String key : dataListDay1) {
					if(listCIQDetailsModelDay01.get(0).getCiqMap().containsKey(key)) {
						day1DataMap.put(key, listCIQDetailsModelDay01.get(0).getCiqMap().get(key).getHeaderValue().trim());
					} else {
						day1DataMap.put(key, "");
					}
				}
			} else {
				for(String key : dataListDay1) {
					day1DataMap.put(key, "");
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
						if(key.equals("oruId")) {
							oranIdset.add(ciqDetails.getCiqMap().get(key).getHeaderValue().trim());
						}
					}
					day2DataMap.add(tempMap);
				}
			}
			
			/*if(enbId.length() == 11) {
				network = enbId.substring(0, 3);
			} else if(enbId.length() == 10) {
				network = "0" + enbId.substring(0, 2);
			}*/
			String NeVersion=StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-");
			sb.append("\"@ADPF\"\n");
			sb.append("\"NE ID\",\"NE Type\",\"NE Version\",\"Release Version\",\"Network\",\"NE Name\",\"GPL Version\","
					+ "\"gNB ID\",\"gNB ID Length\",\"gNB DU ID\",\"gNB DU Name\",\"Endpoint CU IP address\",\"Local Time Offset\","
					+ "\"NE Serial Number\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"gnb_du_cnf\"," + "\"" + StringUtils.substringBefore(day1DataMap.get("eNB_Version"), "-") + "\"," 
					+ "\"" + day1DataMap.get("vDU_Release") + "\","  + "\"" + day1DataMap.get("network")+ "\"," + "\"" + day1DataMap.get("neName") + "\"," 
					+ "\"" + "\"," + "\"" + day1DataMap.get("gnb_ID") + "\"," + "\"" + day1DataMap.get("gNBIDLength") + "\"," +  "\"" + day1DataMap.get("gnbDuId") + "\"," 
					+ "\"" + day1DataMap.get("gnbDuName") + "\"," + "\"" + day1DataMap.get("cuIPaddress") + "\"," + "\"" + "0" + "\"," + "\"" + "\"" + "\n");
			
			sb.append("\"@SERVER_INFORMATION\"\n");
			sb.append("\"NE ID\",\"CFM\",\"PSM\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@VIRTUAL_PORT_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Port Type\",\"Port ID\",\"Administrative State\",\"MTU\"\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"1500\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"1500\"" + "\n");
			}
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"midhaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"midhaul\",\"0\",\"unlocked\",\"1500\"" + "\n");
			if(oranIdset.contains("0")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"0\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"3\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"1\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"4\",\"unlocked\",\"9000\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"2\",\"unlocked\",\"9000\"" + "\n");
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"fronthaul\",\"5\",\"unlocked\",\"9000\"" + "\n");
			}
			
			sb.append("\"@VLAN_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"VLAN ID\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@EXTERNAL_IP_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Interface Name\",\"IP Address\",\"IP Prefix Length\",\"F1\",\"Carrier Aggregation\",\"Mplane\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1cAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh0\"," +  "\""+ day1DataMap.get("f1uAddress") + "\"," + "\"64\",\"true\",\"false\",\"false\"" + "\n");
			
			String ip = "";
			List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "dpp_ip", "mh1");
			if(!ObjectUtils.isEmpty(auditConstantsList)) {
				ip = auditConstantsList.get(0).getParameterValue().trim();
			}
			
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"mh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"true\",\"false\"" + "\n");
			if(oranIdset.contains("0")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh0");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh0\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			if(oranIdset.contains("1")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh1");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh1\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			if(oranIdset.contains("2")) {
				ip = "";
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, "rmp_ip", "fh2");
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					ip = auditConstantsList.get(0).getParameterValue().trim();
				}
				sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"rmp\",\"0\",\"fh2\"," +  "\""+ ip + "\"," + "\"64\",\"false\",\"false\",\"true\"" + "\n");
			}
			
			sb.append("\"@ROUTE_INFORMATION\"\n");
			sb.append("\"NE ID\",\"POD Type\",\"POD ID\",\"Prefix\",\"Gateway\"\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dip\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			sb.append("\""+ day1DataMap.get("NEID") + "\"," + "\"dpp\",\"0\",\"0:0:0:0:0:0:0:0/0\"," + "\""+ day1DataMap.get("f1cGateway") + "\"" + "\n");
			
			/*if (NeVersion.equals("21.C.0")) {
				
			sb.append("\"@CELL_INFORMATION\"\n");
			sb.append("\"State\",\"Sector ID\",\"Carrier ID\",\"Cell Num\",\"NR Physical Cell ID\",\"NR DL Arfcn\",\"NR UL Arfcn\",\"NR DL Bandwidth\","
					+ "\"NR UL Bandwidth\",\"NR Frequency Band\",\"Tracking Area Code Usage\",\"Tracking Area Code\",\"DPP ID\",\"PRACH RSI\",\"PRACH ZCZC\","
					+ "\"PRACH Configuration Index\",\"PRACH SSB per RO\",\"Restricted Set\",\"PRACH RB Offset Auto Configuration\",\"TDD Ratio Configuration\","
					+ "\"DL Antenna Count\",\"UL Antenna Count\",\"Number of Rx Paths per RU\",\"Cell Path Type\",\"Number of Tx SSB\",\"Power\",\"Dynamic Spectrum Sharing Mode\","
					+ "\"Slot Level Operation Mode\",\"DSS Target LTE Cell Num\",\"End Point DSS Index\",\"MV IO Site Migration Indicator\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
						+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
						+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
						+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
						+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
						+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
						+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
						+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\",\"1\"," + "\"" + data.get("power") + "\"," 
						+ "\"" + data.get("dynamicSpectrumSharingMode") + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
						+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
			}
			
			sb.append("\"@FSU_MPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
			sb.append("\"\",\"\",\"\"\n");
			
			sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
			sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
			
			sb.append("\"@ORU_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number or MPlane Interface Name\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String serialNumber = "";
				if(data.get("oruId").equals("0")) {
					serialNumber = "fh0";
				} else if(data.get("oruId").equals("1")) {
					serialNumber = "fh1";					
				}else if(data.get("oruId").equals("2")) {
					serialNumber = "fh2";					
				}
				sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
						+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude1") + "\"," + "\"" + data.get("longitude1") + "\"," 
						+ "\"" + data.get("height1") + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
			}
			
			sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
			sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
			for(HashMap<String, String> data : day2DataMap) {
				String[] podportid = data.get("POD_PORT_ID").split(",");
				String[] ruportid = data.get("RU_PORT_ID").split(",");
				if(podportid.length == ruportid.length) {
					for(int i=0; i<podportid.length; i++) {
						sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
								+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
					}
				}				
			}
			
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
				for(HashMap<String, String> data : day2DataMap) {
					sb.append("\"ADD\"," + "\"" + data.get("sectorID") + "\"," + "\"" + data.get("carrierID") + "\"," + "\"" + data.get("cellNum") + "\"," 
							+ "\"" + data.get("nr_PCI") + "\"," + "\"" + data.get("nrDl_Arfcn") + "\"," + "\"" + data.get("nrUl_Arfcn") + "\"," 
							+ "\"" + data.get("nrDLBandwidth") + "\"," + "\"" + data.get("nrULBandwidth") + "\"," + "\"" + data.get("nrFrequencyBand") + "\"," 
							+ "\"" + data.get("trackingAreaCodeUsage") + "\"," + "\"" + data.get("trackingAreaCode") + "\"," + "\"" + data.get("dpp_ID") + "\"," 
							+ "\"" + data.get("prachRsi") + "\"," + "\"" + data.get("prachZczc") + "\"," + "\"" + data.get("prachConfigIndex") + "\"," 
							+ "\"" + data.get("prachSSBPerRO") + "\"," + "\"" + data.get("restrictedSet") + "\"," + "\"" + data.get("prachRBOffsetAutoConfig") + "\"," 
							+ "\"" + data.get("tddRatio") + "\"," + "\"" + data.get("dlAntennaCount") + "\"," + "\"" + data.get("ulAntennaCount") + "\","
							+ "\"" + data.get("numberRxPathsRU") + "\"," + "\"" + data.get("Cell_Path_Type") + "\"," + "\"" + data.get("power") + "\"," 
							+ "\"" + data.get("dynamicSpectrumSharingMode") + "\"," + "\"" + data.get("slotLevelOperationMode") + "\"," + "\"" + data.get("dssTargetLTECellNum") + "\"," 
							+ "\"" + data.get("endpointDSSIndex") + "\"," + "\"" + data.get("multivendorInterOpSiteDSS") + "\"" + "\n");
				}
				
				sb.append("\"@FSU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"FSU MPlane Interface IP\"\n");
				sb.append("\"\",\"\",\"\"\n");
				
				sb.append("\"@FSU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected FSU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Unit Type\",\"Serial Number\",\"Support Cell Number\",\"Sharing Enabled\",\"Latitude\",\"Longitude\",\"Height\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@VRU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"FSU ID\",\"VRU ID\",\"Connected FSU Port ID\",\"Connected RU Port ID\",\"Support Cell Number\"\n");
				sb.append("\"\",\"\",\"\",\"\",\"\",\"\"\n");
				
				sb.append("\"@ORU_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Unit Type\",\"FHM Mode\",\"RF Operation Mode\",\"Serial Number\",\"Support Cell Number\",\"Latitude\",\"Longitude\",\"Height\",\"Azimuth\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String serialNumber = "";
					if(data.get("oruId").equals("0")) {
						serialNumber = "fh0";
					} else if(data.get("oruId").equals("1")) {
						serialNumber = "fh1";					
					}else if(data.get("oruId").equals("2")) {
						serialNumber = "fh2";					
					}
					sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"oru\",\"\",\"\"," + "\"" + serialNumber + "\"," 
							+ "\"" + data.get("oruSupportCellNumber") + "\"," + "\"" + data.get("latitude1") + "\"," + "\"" + data.get("longitude1") + "\"," 
							+ "\"" + data.get("height1") + "\"," + "\"" + data.get("AntennaAzimuth") + "\"" + "\n");
				}
				
				sb.append("\"@ORU_CUPLANE_INFORMATION\"\n");
				sb.append("\"State\",\"ORU ID\",\"Connected POD Type\",\"Connected POD ID\",\"Connected POD Port ID\",\"Connected RU Port ID\",\"Vlan ID\",\"Support Cell Number\"\n");
				for(HashMap<String, String> data : day2DataMap) {
					String[] podportid = data.get("POD_PORT_ID").split(",");
					String[] ruportid = data.get("RU_PORT_ID").split(",");
					if(podportid.length == ruportid.length) {
						for(int i=0; i<podportid.length; i++) {
							sb.append("\"ADD\"," + "\"" + data.get("oruId") + "\"," + "\"dpp\",\"0\"," + "\"" + podportid[i].trim() + "\"," 
									+ "\"" + ruportid[i].trim() + "\","  + "\"" + data.get("vlanId") + "\"," + "\"" + data.get("oruSupportCellNumber") + "\"" + "\n");
						}
					}				
				}
				
				sb.append("\"@SON_INFORMATION\"\n");
				sb.append("\"Initial PCI\",\"Initial RSI\",\"Initial NCRT\",\"Initial LTE NCRT\"\n");
				sb.append("\"off\",\"off\",\"off\",\"off\"");
			}*/}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + vDUGrowFileName);
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
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGenerationFSU() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", vDUGrowFileName);

		}
		return fileGenerateResult;
	}
	
}
