package com.smart.rct.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.models.CIQDetailsModel;

@Component
public class RSSIAuditXmlRulesServiceUtil {
	
	final static Logger logger = LoggerFactory.getLogger(AuditXmlRulesServiceUtil.class);
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	Audit4GSummaryService audit4GSummaryService;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;
	
	// written by Shivam
	public StringBuilder getpreexiting(String fullOutputLog, String command) {
		StringBuilder troubleShootingHavingPreexiting = new StringBuilder();
		try {
			String outputLog1 = StringUtils.substringAfter(fullOutputLog, command);
			outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);
			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			List<LinkedHashMap<String, String>> tableData2 = new ArrayList<>();
			headerList2.add("RRH");
			headerList2.add("FA_ID");
			headerList2.add("EARFCN");
			headerList2.add("FINAL_RSSI_SANITY_RESULT");
			headerList2.add("FINAL_PIM_SANITY_RESULT");
			headerList2.add("PATH");
			headerList2.add("RSSI_SANITY_RSSI");
			headerList2.add("PIM_SANITY_RSSI");
			headerList2.add("RSSI_DELTA");
			headerList2.add("PIM_DELTA");
			headerList2.add("RSSI_PATH_TO_PATH_COMPARISON");
			headerList2.add("PIM_PATH_TO_PATH_COMPARISON");
			headerList2.add("CHECK_PATH_STATUS");
			headerList2.add("TROUBLESHOOTING_STEPS");

			String outputLog12 = "";
			if (outputLog1.contains("TROUBLESHOOTING_STEPS,COMMENTS")) {
				outputLog12 = StringUtils.substringAfter(outputLog1, "TROUBLESHOOTING_STEPS,COMMENTS");
			} else {
				outputLog12 = StringUtils.substringAfter(outputLog1, "TROUBLESHOOTING_STEPS");						
			}
			if (outputLog1.contains("ENB_ID,RRH,EARFCN,PATH,VSWR,VSWR_STATUS")) {
				outputLog12 = StringUtils.substringBefore(outputLog12,
						"ENB_ID,RRH,EARFCN,PATH,VSWR,VSWR_STATUS");						
			} else {
				outputLog12 = StringUtils.substringBefore(outputLog12,
						"################");
			}

			String[] splitS = outputLog12.split("\n");
			for (int z = 0; z < splitS.length; z++) {
				String temp[] = splitS[z].split(",");
				logger.info("splits " + splitS[z]);
				ArrayList<String> Al = new ArrayList<String>();
				for (int j = 0; j < temp.length; j++) {
					Al.add(temp[j]);
				}
				if (Al.size() < headerList2.size()) {
					while (Al.size() != headerList2.size()) {
						Al.add("-");
					}
				}

				if (temp.length > 1 && !Al.isEmpty()) {

					LinkedHashMap<String, String> objtableData1 = new LinkedHashMap<>();
					Iterator<String> itr = headerList2.iterator();
					String header = itr.next();
					objtableData1.put(header, Al.get(0));
					header = itr.next();
					objtableData1.put(header, Al.get(1));
					header = itr.next();
					objtableData1.put(header, Al.get(2));
					header = itr.next();
					objtableData1.put(header, Al.get(3));
					header = itr.next();

					header = itr.next();
					objtableData1.put(header, Al.get(5));
					header = itr.next();
					objtableData1.put(header, Al.get(6));
					header = itr.next();

					header = itr.next();
					objtableData1.put(header, Al.get(8));
					header = itr.next();

					header = itr.next();
					objtableData1.put(header, Al.get(10));
					header = itr.next();

					header = itr.next();
					objtableData1.put(header, Al.get(12));
					header = itr.next();
					objtableData1.put(header, Al.get(13));
					tableData2.add(objtableData1);

				}
			}
			
			boolean issuenotpresent=true;
			for (LinkedHashMap<String, String> tdData : tableData2) {
				if (tdData.get("TROUBLESHOOTING_STEPS").contains("PRE_EXISTING")) {
					String s1 = tdData.get("TROUBLESHOOTING_STEPS");
					if(s1.contains("Refer")) {
					s1=StringUtils.substringBefore(s1, "Refer");
					}
					s1=s1+"No Troubleshooting is required";
					tdData.put("TROUBLESHOOTING_STEPS", s1);
					troubleShootingHavingPreexiting.append("RRH: " + tdData.get("RRH") + "," + " FA_ID: "
							+ tdData.get("FA_ID") + "," 
							+ " RSSI_SANITY_RSSI: " + tdData.get("RSSI_SANITY_RSSI") + ","
							+ " FINAL_RSSI_SANITY_RESULT: " + tdData.get("FINAL_RSSI_SANITY_RESULT") + ","
							+ " TROUBLESHOOTING_STEPS: "
							+ tdData.get("TROUBLESHOOTING_STEPS") + "\n");
					issuenotpresent = false;
				}
			}
			System.out.println(	"troubleShootingHavingPreexiting   :"+troubleShootingHavingPreexiting);
			if(issuenotpresent) {
				troubleShootingHavingPreexiting.setLength(0);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return troubleShootingHavingPreexiting;

	}
	
	public StringBuilder getRSSIImbalance(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {

		StringBuilder htmlContent = new StringBuilder();
		StringBuilder auditIssue1 = new StringBuilder();
		StringBuilder auditIssue2 = new StringBuilder();
		StringBuilder auditIssue3 = new StringBuilder();
		StringBuilder auditIssue4 = new StringBuilder();
		StringBuilder auditIssue5 = new StringBuilder();
		StringBuilder auditIssueAll = new StringBuilder();
		

		String ways = "";
		String fileName = "";

		List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository
				.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE, AuditConstants.RSSI_OUTPUT);

		if (!ObjectUtils.isEmpty(auditConstantsList)) {
			ways = auditConstantsList.get(0).getParameterValue().trim();
		}

		auditConstantsList = auditConstantsRepository
				.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE, AuditConstants.RSSI_FILE_NAME);

		if (!ObjectUtils.isEmpty(auditConstantsList)) {
			fileName = auditConstantsList.get(0).getParameterValue().trim();
		}

		try {
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			LinkedHashSet<String> headerList5 = new LinkedHashSet<>();
			//headerList5.add("ENB_ID");
			headerList5.add("RRH");
			headerList5.add("EARFCN");
			headerList5.add("PATH");
			headerList5.add("VSWR");
			headerList5.add("VSWR_STATUS");
			LinkedHashSet<String> headerList6 = new LinkedHashSet<>();
			//headerList6.add("ENB_ID");
			headerList6.add("RRH");
			headerList6.add("PATH");
			headerList6.add("EARFCN");
			headerList6.add("TX_POWER");
			headerList6.add("DL_MAX_POWER");
			headerList6.add("TX_POWER_STATUS");

			if (ways.equalsIgnoreCase("way1")) {
				headerList1.add("STATUS");
				headerList1.add("MCM_IP");
				headerList1.add("ENB_ID");
				headerList1.add("STAGE");
				headerList1.add("RRH");
				headerList1.add("FA_ID");
				headerList1.add("PATH");
				headerList1.add("RSSI_SANITY_RSSI_VALUES");
				headerList1.add("PIM_SANITY_RSSI_VALUES");
				// headerList1.add("NOMINAL_RSSI");
				// headerList1.add("TROUBLESHOOTING_STEPS");

				headerList2.add("RRH");
				headerList2.add("FA_ID");
				headerList2.add("RSSI_SANITY");
				headerList2.add("PIM_SANITY");
				headerList2.add("TROUBLESHOOTING_STEPS");

				headerList3.add("STATUS");
				headerList3.add("MCM_IP");
				headerList3.add("ENB_ID");
				headerList3.add("CELL/INV_ID/RRH_ID");
				headerList3.add("RRH_IP/ADMIN_STATUS");
				headerList3.add("BOARD_TYPE/CARR_TYPE");

				headerList4.add("RSSI TOOL DETAILED LSMR SERVER LOG PATH");

				String outputLog1 = StringUtils.substringAfter(fullOutputLog, command);
				outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);

				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append("<tr><td align=center colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>"
						+ "RSSI AUDIT ISSUE LIST" + "</b></td></tr>\n");

				// String op =
				// outputLog1.substring(outputLog1.indexOf("TEST"),outputLog1.indexOf("STATUS")).trim();
				//
				// htmlContent.append("<tr><td align=center colspan=" + headerList1.size() + op
				// + "</td></tr>\n");

				List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
				List<LinkedHashMap<String, String>> tableData2 = new ArrayList<>();
				List<LinkedHashMap<String, String>> tableData3 = new ArrayList<>();
				List<LinkedHashMap<String, String>> tableData4 = new ArrayList<>();

				String outputLog12 = outputLog1.substring(outputLog1.indexOf("TROUBLESHOOTING_STEPS") + 21).trim();
				String outputLog11 = StringUtils.substringBetween(outputLog1, "PIM_SANITY_RSSI_VALUES", "RRH,FA_ID")
						.trim();
				String outputLog13 = outputLog1.substring(outputLog1.indexOf("BOARD_TYPE/CARR_TYPE"),
						outputLog1.indexOf("STATUS,MCM_IP,ENB_ID,STAGE")).trim();
				String outputLog14 = StringUtils
						.substringBetween(outputLog1, "RSSI IMBALANCE ISSUE LIST END ################", "[HNRTNYCRLSM-")
						.trim();

				String tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList1) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}
				tableHeader = tableHeader + "</tr>\n";
				htmlContent.append(tableHeader);

				String[] splitS = outputLog11.split("\n");

				for (int z = 0; z < splitS.length; z++) {

					String temp[] = splitS[z].split(",");
					ArrayList<String> Al = new ArrayList<String>();
					for (int j = 0; j < temp.length; j++) {
						Al.add(temp[j]);
					}
					System.out.println("length " + temp.length);
					while (Al.size() != headerList1.size()) {
						Al.add("-");
					}

					if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList1.size()) {
						LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
						Iterator<String> itr = headerList1.iterator();
						String header = itr.next();
						objtableData.put(header, Al.get(0));
						header = itr.next();
						objtableData.put(header, Al.get(1));
						header = itr.next();
						objtableData.put(header, Al.get(2));
						header = itr.next();
						objtableData.put(header, Al.get(3));
						header = itr.next();
						objtableData.put(header, Al.get(4));
						header = itr.next();
						objtableData.put(header, Al.get(5));
						header = itr.next();
						objtableData.put(header, Al.get(6));
						header = itr.next();
						objtableData.put(header, Al.get(7));
						header = itr.next();
						objtableData.put(header, Al.get(8));

						tabelData1.add(objtableData);
					}
				}
				splitS = outputLog12.split("\n");
				for (int z = 0; z < splitS.length - 6; z++) {
					String temp[] = splitS[z].split(",");

					System.out.println("length " + temp.length);
					if (temp.length > 1 && temp != null && temp.length == headerList2.size()) {

						LinkedHashMap<String, String> objtableData1 = new LinkedHashMap<>();
						Iterator<String> itr = headerList2.iterator();
						String header = itr.next();
						objtableData1.put(header, temp[0]);
						header = itr.next();
						objtableData1.put(header, temp[1]);
						header = itr.next();
						objtableData1.put(header, temp[2]);
						header = itr.next();
						objtableData1.put(header, temp[3]);
						header = itr.next();
						objtableData1.put(header, temp[4]);
						tableData2.add(objtableData1);
					}
				}
				splitS = outputLog13.split("\n");
				for (int z = 0; z < splitS.length; z++) {
					String temp[] = splitS[z].split(",");

					System.out.println("length " + temp.length);
					if (temp.length > 1 && temp != null && temp.length == headerList3.size()) {

						LinkedHashMap<String, String> objtableData2 = new LinkedHashMap<>();
						Iterator<String> itr = headerList3.iterator();
						String header = itr.next();
						objtableData2.put(header, temp[0]);
						header = itr.next();
						objtableData2.put(header, temp[1]);
						header = itr.next();
						objtableData2.put(header, temp[2]);
						header = itr.next();
						objtableData2.put(header, temp[3]);
						header = itr.next();
						objtableData2.put(header, temp[4]);
						header = itr.next();
						objtableData2.put(header, temp[5]);

						tableData3.add(objtableData2);
					}

				}

				splitS = outputLog14.split("\n");
				if (splitS.length > 1 && splitS != null) {
					for (int z = 0; z < splitS.length; z++) {
						LinkedHashMap<String, String> objtableData3 = new LinkedHashMap<>();
						Iterator<String> itr = headerList3.iterator();
						String header = itr.next();
						objtableData3.put(header, splitS[z]);

						tableData4.add(objtableData3);
					}
				}

				StringBuilder tableData = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tabelData1) {
					tableData.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
						String key = resultTableData.getKey();
						String value = resultTableData.getValue();
						tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
						if (value.contains("DETECTED") || value.contains("_FAILED")) {

							auditIssue1.append(key + "  :" + value + "\n");

						}

					}
					tableData.append("</tr>\n");
				}

				htmlContent.append(tableData);
				htmlContent.append("</table>\n");

				if (auditIssue1.length() != 0) {
					audit4GSummaryService.createAudit4GSummaryEntity(58, runTestEntity.getId(),
							enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
				}

				// another table
				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append("<tr><td align=center colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>"
						+ "RSSI AUDIT SUMMARY" + "</b></td></tr>\n");

				String tableHeader2 = "";
				tableHeader2 = tableHeader2 + "<tr>\n";
				for (String headerName : headerList2) {
					tableHeader2 = tableHeader2 + "<th align=center>" + headerName + "</th>\n";
				}
				tableHeader2 = tableHeader2 + "</tr>\n";

				htmlContent.append(tableHeader2);

				StringBuilder tableDataN = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tableData2) {
					tableDataN.append("<tr>\n");
					boolean present = false;
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					
						String value = resultTableData.getValue();
						if (value.contains("=")) {
							value = value.substring(value.indexOf("=") + 1);
						}

						if (value.contains("Fail") || value.contains("FAIL")) {

							tableDataN.append("<td align=center bgcolor=#fa8c8c >" + value + "</td>\n");
							if (present = false) {
								auditIssue2.append("RRH: " + tdData.get("RRH") + " FA_ID: " + tdData.get("FA_ID")
										+ " RSSI_SANITY: " + tdData.get("RSSI_SANITY") + " PIM_SANITY: "
										+ tdData.get("PIM_SANITY") + " TROUBLESHOOTING_STEPS: "
										+ tdData.get("TROUBLESHOOTING_STEPS") + "\n");
							}
							present = true;
						} else {
							tableDataN.append("<td align=center >" + value + "</td>\n");
						}
					}
					tableDataN.append("</tr>\n");
				}

				htmlContent.append(tableDataN);
				htmlContent.append("</table>\n");

				if (auditIssue2.length() != 0) {
					audit4GSummaryService.createAudit4GSummaryEntity(59, runTestEntity.getId(),
							enbId.replaceAll("^0+(?!$)", ""), auditIssue2.toString());
				}

				// third table
				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append("<tr><td align=center colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>"
						+ "RSSI TOOL ABORT/ISSUE LOG" + "</b></td></tr>\n");

				String tableHeader3 = "";
				tableHeader3 = tableHeader3 + "<tr>\n";
				for (String headerName : headerList3) {
					tableHeader3 = tableHeader3 + "<th align=center>" + headerName + "</th>\n";
				}
				tableHeader3 = tableHeader3 + "</tr>\n";

				htmlContent.append(tableHeader3);

				StringBuilder tableDataX = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tableData3) {
					tableDataX.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
						
						String value = resultTableData.getValue();
						if (value.contains("=")) {
							value = value.substring(value.indexOf("=") + 1);
						}

						tableDataX.append("<td align=center <br> " + value + "</td>\n");

					}
					tableDataX.append("</tr>\n");
				}

				htmlContent.append(tableDataX);
				htmlContent.append("</table>\n");

				// fourth table

				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

				String tableHeader4 = "";
				tableHeader4 = tableHeader4 + "<tr>\n";
				for (String headerName : headerList4) {
					tableHeader4 = tableHeader4 + "<th align=center bgcolor=#EEEEEE>" + headerName + "</th>\n";
				}
				tableHeader4 = tableHeader4 + "</tr>\n";

				htmlContent.append(tableHeader4);

				StringBuilder tableDataL = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tableData4) {
					tableDataL.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
						String value = resultTableData.getValue();

						tableDataL.append("<td align=left <br> " + value + "</td>\n");

					}
					tableDataL.append("</tr>\n");
				}

				htmlContent.append(tableDataL);
				htmlContent.append("</table>\n");
			} else if (ways.equalsIgnoreCase("way2")) {

				headerList1.add("INITIAL_STATUS");
				headerList1.add("MCM_IP");
				headerList1.add("ENB_ID");
				headerList1.add("STAGE");
				headerList1.add("RRH");
				headerList1.add("FA_ID");
				headerList1.add("EARFCN");
				headerList1.add("PATH");
				headerList1.add("RSSI_SANITY_RSSI_VALUES");
				headerList1.add("RSSI_SANITY_DIFF");
				headerList1.add("PIM_SANITY_RSSI_VALUES");
				headerList1.add("PIM_SANITY_DIFF");
				headerList1.add("STAGE1_RSSI");
				headerList1.add("STAGE2_RSSI");
				headerList1.add("DIFF");

				headerList2.add("RRH");
				headerList2.add("FA_ID");
				headerList2.add("EARFCN");
				headerList2.add("FINAL_RSSI_SANITY_RESULT");
				headerList2.add("FINAL_PIM_SANITY_RESULT");
				headerList2.add("PATH");
				headerList2.add("RSSI_SANITY_RSSI");
				headerList2.add("PIM_SANITY_RSSI");
				headerList2.add("RSSI_DELTA");
				headerList2.add("PIM_DELTA");
				headerList2.add("RSSI_PATH_TO_PATH_COMPARISON");
				headerList2.add("PIM_PATH_TO_PATH_COMPARISON");
				headerList2.add("CHECK_PATH_STATUS");
				headerList2.add("TROUBLESHOOTING_STEPS");

				LinkedHashSet<String> headerList2Temp = new LinkedHashSet<>();
				headerList2Temp.add("RRH");
				headerList2Temp.add("FA_ID");
				headerList2Temp.add("EARFCN");
				headerList2Temp.add("FINAL_RSSI_SANITY_RESULT");
				headerList2Temp.add("FINAL_PIM_SANITY_RESULT");
				headerList2Temp.add("PATH");
				headerList2Temp.add("RSSI_SANITY_RSSI");
				headerList2Temp.add("RSSI_DELTA");
				headerList2Temp.add("RSSI_PATH_TO_PATH_COMPARISON");
				headerList2Temp.add("CHECK_PATH_STATUS");
				headerList2Temp.add("TROUBLESHOOTING_STEPS");

				LinkedHashSet<String> headerList3Temp = new LinkedHashSet<>();
				headerList3Temp.add("RRH");
				headerList3Temp.add("FA_ID");
				headerList3Temp.add("EARFCN");
				headerList3Temp.add("FINAL_RSSI_SANITY_RESULT");
				headerList3Temp.add("PATH");
				headerList3Temp.add("RSSI_SANITY_RSSI");
				headerList3Temp.add("RSSI_DELTA");
				headerList3Temp.add("RSSI_PATH_TO_PATH_COMPARISON");
				headerList3Temp.add("CHECK_PATH_STATUS");
				headerList3Temp.add("TROUBLESHOOTING_STEPS");

				headerList3.add("STATUS");
				headerList3.add("MCM_IP");
				headerList3.add("ENB_ID");
				headerList3.add("CELL/INV_ID/RRH_ID");
				headerList3.add("RRH_IP/ADMIN_STATUS");
				headerList3.add("BOARD_TYPE/CARR_TYPE");

				headerList4.add("RSSI TOOL DETAILED LSMR SERVER LOG PATH");

				String outputLog1 = StringUtils.substringAfter(fullOutputLog, command);
				outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);

				/*String check = "STATUS,MCM_IP,ENB_ID,CELL/INV_ID/RRH_ID,RRH_IP/ADMIN_STATUS,BOARD_TYPE/CARR_TYPE ";
				int count = StringUtils.countMatches(fullOutputLog, check);*/

				if (!outputLog1.isEmpty()) {

					//List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
					List<LinkedHashMap<String, String>> tableData2 = new ArrayList<>();
					List<LinkedHashMap<String, String>> tableData3 = new ArrayList<>();
					//List<LinkedHashMap<String, String>> tableData4 = new ArrayList<>();
					List<LinkedHashMap<String, String>> tableData5 = new ArrayList<>();
					List<LinkedHashMap<String, String>> tableData6 = new ArrayList<>();
					String outputLog12 = "";
					/*String outputLog11 = "";
					String outputLog22 = "";
					String outputLog33 = "";
					String reRun = "RERUN";
					String sss = "";*/
			/*		Boolean reRunCheck = false;
					count = StringUtils.countMatches(fullOutputLog, reRun);
					if (count == 0) {
						outputLog11 = StringUtils.substringBetween(outputLog1, "STAGE2_RSSI,DIFF", "RRH,FA_ID").trim();
					} else if (count > 0) {
						outputLog33 = StringUtils.substringBetween(outputLog1, "STAGE2_RSSI,DIFF",
								"RRH,FA_ID,EARFCN,FINAL_RSSI_SANITY_RESULT").trim();
						outputLog11 = StringUtils.substringBetween(outputLog1, "STAGE2_RSSI,DIFF", "RERUN1_STATUS")
								.trim();
						sss = "STATUS,MCM_IP,ENB_ID,CELL/INV_ID/RRH_ID,RRH_IP/ADMIN_STATUS,BOARD_TYPE/CARR_TYPE";
						if (outputLog11.contains(sss)) {
							outputLog11 = outputLog11.replace(sss, "");
						}
						if (outputLog33.contains(sss)) {
							outputLog33 = outputLog33.replace(
									"STATUS,MCM_IP,ENB_ID,CELL/INV_ID/RRH_ID,RRH_IP/ADMIN_STATUS,BOARD_TYPE/CARR_TYPE",
									"");
						}
						reRunCheck = true;
					}*/
					

					String outputLog13 = outputLog1.substring(outputLog1.indexOf("BOARD_TYPE/CARR_TYPE"),
							outputLog1.indexOf("INITIAL_STATUS,MCM_IP,ENB_ID,STAGE")).trim();

					/*String outputLog14 = StringUtils.substringAfter(outputLog1,
							"RSSI IMBALANCE ISSUE LIST END ################");
					outputLog14 = StringUtils.substringBefore(outputLog14, "[");*/
					if (outputLog1.contains("TROUBLESHOOTING_STEPS,COMMENTS")) {
						outputLog12 = StringUtils.substringAfter(outputLog1, "TROUBLESHOOTING_STEPS,COMMENTS");
					} else {
						outputLog12 = StringUtils.substringAfter(outputLog1, "TROUBLESHOOTING_STEPS");						
					}
					if (outputLog1.contains("ENB_ID,RRH,EARFCN,PATH,VSWR,VSWR_STATUS")) {
						outputLog12 = StringUtils.substringBefore(outputLog12,
								"ENB_ID,RRH,EARFCN,PATH,VSWR,VSWR_STATUS");						
					} else {
						outputLog12 = StringUtils.substringBefore(outputLog12,
								"################");
					}

					String outputLogVSWR = StringUtils.substringAfter(outputLog1, "VSWR_STATUS");
					outputLogVSWR = StringUtils.substringBefore(outputLogVSWR,
							"ENB_ID,RRH,PATH,EARFCN,TX_POWER,DL_MAX_POWER,TX_POWER_STATUS");

					String outputLogTXPower = StringUtils.substringAfter(outputLog1,
							"ENB_ID,RRH,PATH,EARFCN,TX_POWER,DL_MAX_POWER,TX_POWER_STATUS");
					outputLogTXPower = StringUtils.substringBefore(outputLogTXPower,
							"################ RSSI IMBALANCE ISSUE LIST END ################");

					String[] splitoutputLogVSWR = outputLogVSWR.split("\n");
					for (int z = 0; z < splitoutputLogVSWR.length; z++) {
						String temp[] = splitoutputLogVSWR[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						if (Al.size() < headerList5.size()) {
							while (Al.size() != headerList5.size()) {
								Al.add("-");
							}
						}

						if (temp.length > 1 && !Al.isEmpty()) {

							LinkedHashMap<String, String> objtableData1 = new LinkedHashMap<>();
							Iterator<String> itr = headerList5.iterator();
							String header = itr.next();
							objtableData1.put(header, Al.get(1));
							header = itr.next();
							objtableData1.put(header, Al.get(2));
							header = itr.next();
							objtableData1.put(header, Al.get(3));
							header = itr.next();
							objtableData1.put(header, Al.get(4));
							header = itr.next();
							objtableData1.put(header, Al.get(5));

							tableData5.add(objtableData1);

						}
					}
					String[] splitoutputLogTXPower = outputLogTXPower.split("\n");
					for (int z = 0; z < splitoutputLogTXPower.length; z++) {
						String temp[] = splitoutputLogTXPower[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						if (Al.size() < headerList6.size()) {
							while (Al.size() != headerList6.size()) {
								Al.add("-");
							}
						}

						if (temp.length > 1 && !Al.isEmpty()) {

							LinkedHashMap<String, String> objtableData1 = new LinkedHashMap<>();
							Iterator<String> itr = headerList6.iterator();
							String header = itr.next();
							objtableData1.put(header, Al.get(1));
							header = itr.next();
							objtableData1.put(header, Al.get(2));
							header = itr.next();
							objtableData1.put(header, Al.get(3));
							header = itr.next();
							objtableData1.put(header, Al.get(4));
							header = itr.next();
							objtableData1.put(header, Al.get(5));
							header = itr.next();
							objtableData1.put(header, Al.get(6));
							tableData6.add(objtableData1);

						}
					}

					String[] splitS = outputLog12.split("\n");
					for (int z = 0; z < splitS.length; z++) {
						String temp[] = splitS[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						if (Al.size() < headerList2.size()) {
							while (Al.size() != headerList2.size()) {
								Al.add("-");
							}
						}

						if (temp.length > 1 && !Al.isEmpty()) {

							LinkedHashMap<String, String> objtableData1 = new LinkedHashMap<>();
							Iterator<String> itr = headerList2.iterator();
							String header = itr.next();
							objtableData1.put(header, Al.get(0));
							header = itr.next();
							objtableData1.put(header, Al.get(1));
							header = itr.next();
							objtableData1.put(header, Al.get(2));
							header = itr.next();
							objtableData1.put(header, Al.get(3));
							header = itr.next();
							objtableData1.put(header, Al.get(4));
							header = itr.next();
							objtableData1.put(header, Al.get(5));
							header = itr.next();
							objtableData1.put(header, Al.get(6));
							header = itr.next();
							header = itr.next();
							objtableData1.put(header, Al.get(8));
							header = itr.next();
							header = itr.next();
							objtableData1.put(header, Al.get(10));
							header = itr.next();
							header = itr.next();
							objtableData1.put(header, Al.get(12));
							header = itr.next();
							objtableData1.put(header, Al.get(13));
							tableData2.add(objtableData1);

						}
					}
					splitS = outputLog13.split("\n");
					for (int z = 0; z < splitS.length; z++) {
						String temp[] = splitS[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						if (Al.size() < headerList3.size()) {
							while (Al.size() != headerList3.size()) {
								Al.add("-");
							}
						}

						if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList3.size()) {

							LinkedHashMap<String, String> objtableData2 = new LinkedHashMap<>();
							Iterator<String> itr = headerList3.iterator();
							String header = itr.next();
							objtableData2.put(header, Al.get(0));
							header = itr.next();
							objtableData2.put(header, Al.get(1));
							header = itr.next();
							objtableData2.put(header, Al.get(2));
							header = itr.next();
							objtableData2.put(header, Al.get(3));
							header = itr.next();
							objtableData2.put(header, Al.get(4));
							header = itr.next();
							objtableData2.put(header, Al.get(5));

							tableData3.add(objtableData2);
						}
					}
					if (auditIssue1.length() != 0) {
						audit4GSummaryService.createAudit4GSummaryEntity(58, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
					}
					for (LinkedHashMap<String, String> tdData : tableData2) {
						if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("PASS")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("PASS")) {
							tdData.put("TROUBLESHOOTING_STEPS", "N/A");
						} else if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("PASS")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("FAIL")) {
							tdData.put("TROUBLESHOOTING_STEPS", "N/A");
						} else if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("FAIL")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("PASS")
								&& !tdData.get("TROUBLESHOOTING_STEPS").contains("PRE_EXISTING")) {
							tdData.put("TROUBLESHOOTING_STEPS",
									"Refer to Troubleshooting step #1 from RSSI Troubleshooting Guide");
						} else if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("FAIL")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("FAIL")
								&& !tdData.get("TROUBLESHOOTING_STEPS").contains("PRE_EXISTING")) {
							tdData.put("TROUBLESHOOTING_STEPS",
									"Refer to Troubleshooting step #1 from Troubleshooting Guide");
						} else if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("FAIL")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("PASS")
								&& tdData.get("TROUBLESHOOTING_STEPS").contains("PRE_EXISTING")) {
							String s1 = tdData.get("TROUBLESHOOTING_STEPS");
							if (s1.contains("Refer")) {
								s1 = StringUtils.substringBefore(s1, "Refer");
							}
							s1 = s1 + "No Troubleshooting is required";
							tdData.put("TROUBLESHOOTING_STEPS", s1);
						} else if (tdData.get("FINAL_RSSI_SANITY_RESULT").contains("FAIL")
								&& tdData.get("FINAL_PIM_SANITY_RESULT").contains("FAIL")
								&& tdData.get("TROUBLESHOOTING_STEPS").contains("PRE_EXISTING")) {
							String s1 = tdData.get("TROUBLESHOOTING_STEPS");
							if (s1.contains("Refer")) {
								s1 = StringUtils.substringBefore(s1, "Refer");
							}
							s1 = s1 + "No Troubleshooting is required";
							tdData.put("TROUBLESHOOTING_STEPS", s1);
						}
					}
					for (LinkedHashMap<String, String> tdData2 : tableData2) {
						tdData2.remove("FINAL_PIM_SANITY_RESULT");
					}
					// another table
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td align=center colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>"
							+ "Final RSSI Result and Troubleshooting" + "</b></td></tr>\n");

					String tableHeader2 = "";
					tableHeader2 = tableHeader2 + "<tr>\n";
					for (String headerName : headerList3Temp) {
						tableHeader2 = tableHeader2 + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader2 = tableHeader2 + "</tr>\n";

					htmlContent.append(tableHeader2);

					if (tableData2.isEmpty()) {
						StringBuilder auditIssue47 = new StringBuilder();

						auditIssue47.append("NO DATA : RSSI Tool did not return any data");
						htmlContent.append("<tr>\n<td align=center colspan=" + headerList2.size()
								+ " bgcolor=fa8c8c>Re-Run the tool (NO DATA : RSSI Tool did not return any data)</td></tr>\n");
						if (auditIssue47.length() != 0) {
							audit4GSummaryService.createAudit4GSummaryEntity(80, runTestEntity.getId(),
									enbId.replaceAll("^0+(?!$)", ""), auditIssue47.toString());
						}
					}

					StringBuilder tableDataN = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData2) {
						tableDataN.append("<tr>\n");
						boolean present = false;
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String value = resultTableData.getValue();
							if (tdData.get("EARFCN").isEmpty()) {
								tdData.put("TROUBLESHOOTING_STEPS", "Rerun the tool");
							}
							Boolean flag = false;
							String red = tdData.get("TROUBLESHOOTING_STEPS");
							if (!red.isEmpty()) {
								if (red.contains("N/A")) {
									flag = true;
								}
							}
							if (!flag) {
								tableDataN.append("<td align=center bgcolor=#fa8c8c  >" + value + "</td>\n");

								if (!present) {
									auditIssue2.append("RRH: " + tdData.get("RRH") + " FA_ID: " + tdData.get("FA_ID")
											+ " RSSI_SANITY_RSSI: " + tdData.get("RSSI_SANITY_RSSI")
											+ " FINAL_RSSI_SANITY_RESULT: " + tdData.get("FINAL_RSSI_SANITY_RESULT")
											+ " TROUBLESHOOTING_STEPS: " + tdData.get("TROUBLESHOOTING_STEPS") + "\n");
									present = true;

								}

							} else {

								tableDataN.append("<td align=center >" + value + "</td>\n");
							}

						}
						tableDataN.append("</tr>\n");
					}

					htmlContent.append(tableDataN);
					htmlContent.append("</table>\n");
					
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td align=center colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>"
							+ "RSSI TOOL ABORT/ISSUE LOG" + "</b></td></tr>\n");

					String tableHeader3 = "";
					tableHeader3 = tableHeader3 + "<tr>\n";
					for (String headerName : headerList3) {
						tableHeader3 = tableHeader3 + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader3 = tableHeader3 + "</tr>\n";

					htmlContent.append(tableHeader3);

					StringBuilder tableDataX = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData3) {
						tableDataX.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String value = resultTableData.getValue();
							tableDataX.append("<td align=center <br> " + value + "</td>\n");

						}
						tableDataX.append("</tr>\n");
					}

					htmlContent.append(tableDataX);
					htmlContent.append("</table>\n");
					
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

					htmlContent.append("<tr><td align=center colspan=" + headerList5.size() + " bgcolor=#EEEEEE><b>"
							+ "Final VSWR Result and  Status" + "</b></td></tr>\n");

					String tableHeader = "";
					tableHeader = tableHeader + "<tr>\n";
					for (String headerName : headerList5) {
						tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader = tableHeader + "</tr>\n";
					htmlContent.append(tableHeader);

					StringBuilder tableData = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData5) {
						tableData.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String key = resultTableData.getKey();
							String value = resultTableData.getValue();
							if (key.equals("VSWR_STATUS")) {
								if (!value.trim().contains("PASS")) {
									tableData.append("<td align=center word-wrap:break-word bgcolor=#fa8c8c> " + value
											+ "</td>\n");
									auditIssue4.append("RRH: " + tdData.get("RRH") + " EARFCN: " + tdData.get("EARFCN")
									+ " PATH: " + tdData.get("PATH")
									+ " VSWR: " + tdData.get("VSWR")
									+ " VSWR_STATUS: " + tdData.get("VSWR_STATUS") + "\n");
								} else {
									tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
									
								}

							} else {
								tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
							}
						}
						tableData.append("</tr>\n");
					}

					htmlContent.append(tableData);
					htmlContent.append("</table>\n");

					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

					htmlContent.append("<tr><td align=center colspan=" + headerList6.size() + " bgcolor=#EEEEEE><b>"
							+ "Final TX POWER Result and  Status" + "</b></td></tr>\n");

					tableHeader = "";
					tableHeader = tableHeader + "<tr>\n";
					for (String headerName : headerList6) {
						tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader = tableHeader + "</tr>\n";
					htmlContent.append(tableHeader);

					tableData = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData6) {
						tableData.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String key = resultTableData.getKey();
							String value = resultTableData.getValue();
							if (key.equals("TX_POWER_STATUS")) {
								if (!value.trim().contains("PASS")) {
									auditIssue5.append("RRH: " + tdData.get("RRH") + " EARFCN: " + tdData.get("EARFCN")
									+ " TX_POWER: " + tdData.get("TX_POWER")
									+ " DL_MAX_POWER: " + tdData.get("DL_MAX_POWER")
									+ " TX_POWER_STATUS: " + tdData.get("TX_POWER_STATUS") + "\n");
									tableData.append("<td align=center word-wrap:break-word bgcolor=#fa8c8c> " + value
											+ "</td>\n");
									
								} else {
									tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
								}

							} else {
								tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
							}
						}
						tableData.append("</tr>\n");
					}

					htmlContent.append(tableData);
					htmlContent.append("</table>\n");
			
					AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
							.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

					auditIssueAll.append(auditIssue1);
					auditIssueAll.append(auditIssue2);
					auditIssueAll.append(auditIssue3);
					auditIssueAll.append(auditIssue4);
					auditIssueAll.append(auditIssue5);
					auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tableData5, auditIssueAll);
					
			        if (auditIssue2.length() != 0) {
						audit4GSummaryService.createAudit4GSummaryEntity(59, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), auditIssue2.toString());
						audit4GSummaryService.createAudit4GPassFailEntity(59, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "fail");
					} else {
						audit4GSummaryService.createAudit4GPassFailEntity(59, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "pass");
					}
					if (auditIssue4.length() != 0) {
						audit4GSummaryService.createAudit4GSummaryEntity(69, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), auditIssue4.toString());
						audit4GSummaryService.createAudit4GPassFailEntity(69, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "fail");
					} else {
						audit4GSummaryService.createAudit4GPassFailEntity(69, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "pass");
					}
					if (auditIssue5.length() != 0) {
						audit4GSummaryService.createAudit4GSummaryEntity(29, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), auditIssue5.toString());
						audit4GSummaryService.createAudit4GPassFailEntity(29, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "fail");
					} else {
						audit4GSummaryService.createAudit4GPassFailEntity(29, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "pass");
					}
					
				/*	splitS = outputLog11.split("\n");

					for (int z = 0; z < splitS.length; z++) {

						String temp[] = splitS[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						System.out.println("length " + temp.length);
						while (Al.size() != headerList1.size()) {
							Al.add("-");
						}

						if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList1.size()) {
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, Al.get(0));
							header = itr.next();
							objtableData.put(header, Al.get(1));
							header = itr.next();
							objtableData.put(header, Al.get(2));
							header = itr.next();
							objtableData.put(header, Al.get(3));
							header = itr.next();
							objtableData.put(header, Al.get(4));
							header = itr.next();
							objtableData.put(header, Al.get(5));
							header = itr.next();
							objtableData.put(header, Al.get(6));
							header = itr.next();
							objtableData.put(header, Al.get(7));
							header = itr.next();
							objtableData.put(header, Al.get(8));
							header = itr.next();
							objtableData.put(header, Al.get(9));
							header = itr.next();
							objtableData.put(header, Al.get(10));
							header = itr.next();
							objtableData.put(header, Al.get(11));
							header = itr.next();
							objtableData.put(header, Al.get(12));
							header = itr.next();
							objtableData.put(header, Al.get(13));
							header = itr.next();
							objtableData.put(header, Al.get(14));

							tabelData1.add(objtableData);
						}
					}

					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

					htmlContent.append("<tr><td align=center colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>"
							+ "RSSI AUDIT ISSUE LIST" + "</b></td></tr>\n");

					tableHeader = "";
					tableHeader = tableHeader + "<tr>\n";
					for (String headerName : headerList1) {
						tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader = tableHeader + "</tr>\n";
					htmlContent.append(tableHeader);

					tableData = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tabelData1) {
						tableData.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String key = resultTableData.getKey();
							String value = resultTableData.getValue();
							tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
							if (value.contains("DETECTED") || value.contains("_FAILED") || value.contains("FAIL")) {

								auditIssue1.append(key + "  :" + value + "\n");

							}
						}
						tableData.append("</tr>\n");
					}

					htmlContent.append(tableData);
					htmlContent.append("</table>\n");

					if (reRunCheck) {
						for (int h = 1; h <= count; h++) {
							String head = headerList1.stream().findFirst().get();
							ArrayList<String> test = new ArrayList<String>(headerList1);
							int d = test.indexOf(head);
							String g = "RERUN" + h + "_STATUS";
							test.add(d, g);
							test.remove(head);
							headerList1.removeAll(headerList1);
							headerList1.addAll(test);

							if (h == 1) {
								String stri = "RERUN1_STATUS,MCM_IP,ENB_ID,STAGE,RRH,FA_ID,EARFCN,PATH,RSSI_SANITY_RSSI_VALUES,RSSI_SANITY_DIFF,PIM_SANITY_RSSI_VALUES,PIM_SANITY_DIFF,STAGE1_RSSI,STAGE2_RSSI,DIFF";
								htmlContent.append(
										"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
								htmlContent.append("<tr><td align=center colspan=" + headerList1.size()
										+ " bgcolor=#EEEEEE><b>" + "RERUN 1" + "</b></td></tr>\n");
								tableHeader = "";
								for (String headerName : headerList1) {
									tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
								}
								tabelData1.clear();
								tableHeader = tableHeader + "</tr>\n";
								htmlContent.append(tableHeader);
								outputLog22 = StringUtils.substringBetween(outputLog1, stri, "RERUN2").trim();
								if (outputLog22.contains(sss)) {
									outputLog22 = outputLog22.replace(sss, "");
								}
								String[] splitM = outputLog22.split("\n");

								for (int z = 0; z < splitM.length; z++) {

									String temp[] = splitM[z].split(",");
									ArrayList<String> Al = new ArrayList<String>();
									for (int j = 0; j < temp.length; j++) {
										Al.add(temp[j]);
									}
									
									while (Al.size() != headerList1.size()) {
										Al.add("-");
									}

									if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList1.size()) {
										LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
										Iterator<String> itr = headerList1.iterator();
										String header = itr.next();
										objtableData.put(header, Al.get(0));
										header = itr.next();
										objtableData.put(header, Al.get(1));
										header = itr.next();
										objtableData.put(header, Al.get(2));
										header = itr.next();
										objtableData.put(header, Al.get(3));
										header = itr.next();
										objtableData.put(header, Al.get(4));
										header = itr.next();
										objtableData.put(header, Al.get(5));
										header = itr.next();
										objtableData.put(header, Al.get(6));
										header = itr.next();
										objtableData.put(header, Al.get(7));
										header = itr.next();
										objtableData.put(header, Al.get(8));
										header = itr.next();
										objtableData.put(header, Al.get(9));
										header = itr.next();
										objtableData.put(header, Al.get(10));
										header = itr.next();
										objtableData.put(header, Al.get(11));
										header = itr.next();
										objtableData.put(header, Al.get(12));
										header = itr.next();
										objtableData.put(header, Al.get(13));
										header = itr.next();
										objtableData.put(header, Al.get(14));

										tabelData1.add(objtableData);
									}
								}
								tableData.setLength(0);
								for (LinkedHashMap<String, String> tdData : tabelData1) {
									tableData.append("<tr>\n");
									for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
										String key = resultTableData.getKey();
										String value = resultTableData.getValue();
										tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
										if (value.contains("DETECTED") || value.contains("_FAILED")
												|| value.contains("FAIL")) {

											auditIssue1.append(key + "  :" + value + "\n");

										}
									}
									tableData.append("</tr>\n");
								}

								htmlContent.append(tableData);
								htmlContent.append("</table>\n");

							} else if (h == 2) {

								String stri = "RERUN2_STATUS,MCM_IP,ENB_ID,STAGE,RRH,FA_ID,EARFCN,PATH,RSSI_SANITY_RSSI_VALUES,RSSI_SANITY_DIFF,PIM_SANITY_RSSI_VALUES,PIM_SANITY_DIFF,STAGE1_RSSI,STAGE2_RSSI,DIFF";
								htmlContent.append(
										"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
								htmlContent.append("<tr><td align=center colspan=" + headerList1.size()
										+ " bgcolor=#EEEEEE><b>" + "RERUN 2" + "</b></td></tr>\n");
								tableHeader = "";
								tabelData1.clear();
								for (String headerName : headerList1) {
									tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
								}
								tableHeader = tableHeader + "</tr>\n";
								htmlContent.append(tableHeader);
								outputLog22 = StringUtils
										.substringBetween(outputLog1, stri, "RRH,FA_ID,EARFCN,FINAL_RSSI_SANITY_RESULT")
										.trim();
								if (outputLog22.contains(sss)) {
									outputLog22 = outputLog22.replace(sss, "");
								}
								String[] splitM = outputLog22.split("\n");

								for (int z = 0; z < splitM.length; z++) {

									String temp[] = splitM[z].split(",");
									ArrayList<String> Al = new ArrayList<String>();
									for (int j = 0; j < temp.length; j++) {
										Al.add(temp[j]);
									}
									System.out.println("length " + temp.length);
									while (Al.size() != headerList1.size()) {
										Al.add("-");
									}

									if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList1.size()) {
										LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
										Iterator<String> itr = headerList1.iterator();
										String header = itr.next();
										objtableData.put(header, Al.get(0));
										header = itr.next();
										objtableData.put(header, Al.get(1));
										header = itr.next();
										objtableData.put(header, Al.get(2));
										header = itr.next();
										objtableData.put(header, Al.get(3));
										header = itr.next();
										objtableData.put(header, Al.get(4));
										header = itr.next();
										objtableData.put(header, Al.get(5));
										header = itr.next();
										objtableData.put(header, Al.get(6));
										header = itr.next();
										objtableData.put(header, Al.get(7));
										header = itr.next();
										objtableData.put(header, Al.get(8));
										header = itr.next();
										objtableData.put(header, Al.get(9));
										header = itr.next();
										objtableData.put(header, Al.get(10));
										header = itr.next();
										objtableData.put(header, Al.get(11));
										header = itr.next();
										objtableData.put(header, Al.get(12));
										header = itr.next();
										objtableData.put(header, Al.get(13));
										header = itr.next();
										objtableData.put(header, Al.get(14));

										tabelData1.add(objtableData);
									}
								}
								tableData.setLength(0);
								for (LinkedHashMap<String, String> tdData : tabelData1) {
									tableData.append("<tr>\n");
									for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
										String key = resultTableData.getKey();
										String value = resultTableData.getValue();
										tableData.append("<td align=center word-wrap:break-word> " + value + "</td>\n");
										if (value.contains("DETECTED") || value.contains("_FAILED")
												|| value.contains("FAIL")) {

											auditIssue1.append(key + "  :" + value + "\n");

										}
									}
									tableData.append("</tr>\n");
								}

								htmlContent.append(tableData);
								htmlContent.append("</table>\n");
							}
						}
					}

					splitS = outputLog13.split("\n");
					for (int z = 0; z < splitS.length; z++) {
						String temp[] = splitS[z].split(",");
						ArrayList<String> Al = new ArrayList<String>();
						for (int j = 0; j < temp.length; j++) {
							Al.add(temp[j]);
						}
						System.out.println("length " + temp.length);
						if (Al.size() < headerList3.size()) {
							while (Al.size() != headerList3.size()) {
								Al.add("-");
							}
						}

						if (temp.length > 1 && !Al.isEmpty() && Al.size() == headerList3.size()) {

							LinkedHashMap<String, String> objtableData2 = new LinkedHashMap<>();
							Iterator<String> itr = headerList3.iterator();
							String header = itr.next();
							objtableData2.put(header, Al.get(0));
							header = itr.next();
							objtableData2.put(header, Al.get(1));
							header = itr.next();
							objtableData2.put(header, Al.get(2));
							header = itr.next();
							objtableData2.put(header, Al.get(3));
							header = itr.next();
							objtableData2.put(header, Al.get(4));
							header = itr.next();
							objtableData2.put(header, Al.get(5));

							tableData3.add(objtableData2);
						}
					}

					splitS = outputLog14.split("\n");
					if (splitS.length > 1 && splitS != null) {
						for (int z = 0; z < splitS.length; z++) {
							if (!splitS[z].equals("")) {

								LinkedHashMap<String, String> objtableData3 = new LinkedHashMap<>();
								Iterator<String> itr = headerList3.iterator();
								String header = itr.next();
								objtableData3.put(header, splitS[z]);
								tableData4.add(objtableData3);
							}
						}
					}

					// third table
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td align=center colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>"
							+ "RSSI TOOL ABORT/ISSUE LOG" + "</b></td></tr>\n");

					String tableHeader3 = "";
					tableHeader3 = tableHeader3 + "<tr>\n";
					for (String headerName : headerList3) {
						tableHeader3 = tableHeader3 + "<th align=center>" + headerName + "</th>\n";
					}
					tableHeader3 = tableHeader3 + "</tr>\n";

					htmlContent.append(tableHeader3);

					StringBuilder tableDataX = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData3) {
						tableDataX.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String value = resultTableData.getValue();
							tableDataX.append("<td align=center <br> " + value + "</td>\n");

						}
						tableDataX.append("</tr>\n");
					}

					htmlContent.append(tableDataX);
					htmlContent.append("</table>\n");

					// fourth table

					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

					String tableHeader4 = "";
					tableHeader4 = tableHeader4 + "<tr>\n";
					for (String headerName : headerList4) {
						tableHeader4 = tableHeader4 + "<th align=center bgcolor=#EEEEEE>" + headerName + "</th>\n";
					}
					tableHeader4 = tableHeader4 + "</tr>\n";

					htmlContent.append(tableHeader4);

					StringBuilder tableDataL = new StringBuilder();
					for (LinkedHashMap<String, String> tdData : tableData4) {
						tableDataL.append("<tr>\n");
						for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
							String value = resultTableData.getValue();

							tableDataL.append("<td align=left <br> " + value + "</td>\n");

						}
						tableDataL.append("</tr>\n");
					}

					htmlContent.append(tableDataL);
					htmlContent.append("</table>\n");*/
				} else {

					auditIssue3.append("No Output");

					if (auditIssue3.length() != 0) {
						audit4GSummaryService.createAudit4GSummaryEntity(79, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), auditIssue3.toString());
					}

					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td align=center colspan=" + headerList1.size() + " bgcolor=#FFFF00><b>"
							+ "Empty Response" + "</b></td></tr>\n");
					htmlContent.append("</table>\n");
				}
			}

		} catch (Exception e) {

			StringBuilder auditIssue7 = new StringBuilder();

			auditIssue7.append("NO DATA : RSSI Tool did not return any data");

			if (auditIssue7.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(80, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue7.toString());
			}

			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append(
					"<tr>\n<td align=center bgcolor=FFFF00>NO DATA : RSSI Tool did not return any data</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}	
}
