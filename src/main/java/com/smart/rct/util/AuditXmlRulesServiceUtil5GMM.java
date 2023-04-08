package com.smart.rct.util;

import java.io.StringReader;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit4GfsuHardwareDetailsEntity;
import com.smart.rct.postmigration.entity.Audit5GMMHardwareDetailsEntity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.entity.AuditDetailConstantsEntity;
import com.smart.rct.postmigration.repository.Audit5GMMHardwareDetailsRepository;
import com.smart.rct.postmigration.repository.AuditDetailConstantsRepository;
import com.smart.rct.postmigration.repository.AuditFSUHardwareDetailsRepository;
import com.smart.rct.postmigration.repositoryImpl.Audit4gfsuardwareDetailsRepositoryImpl;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.service.NeMappingService;

@Component
public class AuditXmlRulesServiceUtil5GMM {
	final static Logger logger = LoggerFactory.getLogger(AuditXmlRulesServiceUtil5GMM.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	NeMappingService neMappingService;

	@Autowired
	Audit5GCBandSummaryService audit5GCBandSummaryService;
	
	@Autowired
	Audit5GMMHardwareDetailsRepository audit5GMMHardwareDetailsRepository;
	
	@Autowired
	Audit4GSummaryService audit4GSummaryService;
	
	@Autowired
	AuditFSUHardwareDetailsRepository auditFSUHardwareDetailsRepository;
	
	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;
	
	@Autowired
	Audit4GFsuSummaryService audit4GFsuSummaryService;
	
	
	@Autowired
	AuditDetailConstantsRepository auditDetailConstantsRepository;
	
	@Autowired
	Audit5GDSSSummaryService audit5GDSSSummaryService;

	public String getXmlElementData(Element element, String elementName) {
		String outPut = null;
		if (element.getElementsByTagName(elementName) != null
				&& element.getElementsByTagName(elementName).getLength() > 0) {
			outPut = element.getElementsByTagName(elementName).item(0).getTextContent();

		} else {
			outPut = "-";
		}
		return outPut;
	}

	public StringBuilder get5GMMAuditSfpHtml(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("processor-unit-id");
			headerList1.add("port-id");
			headerList1.add("unit-id");
			headerList1.add("hardware-name");
			headerList1.add("serial-number");
			headerList1.add("vendor-name");
			headerList1.add("wave-length");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("optic-module-inventory");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild6, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
		    if(neVersion.toUpperCase().contains("22.A")) {     
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "AU_22A_SFP_Inventory" + "</b></td></tr>\n");
		    }else{
		    	htmlContent.append(
						"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
		    }
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String hw = tdData.get("hardware-name");
				boolean hardwerePresent = true;
				List<Audit5GMMHardwareDetailsEntity> auditConstantsList1 = audit5GMMHardwareDetailsRepository
						.getAuditHardwareDetailsEntityList(hw, "AU");
				if (ObjectUtils.isEmpty(auditConstantsList1)) {
					hardwerePresent = false;
				}
				

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("hardware-name")) {
						if (hardwerePresent) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					  } else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "AU_SFP_Inventory" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getAudit5GMMA1Checks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			// For a1-report-config entries
			String entry = "a1-report-config";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("gnodeb-id");
			headerList1.add("cell-identity");
			headerList1.add("index");
			headerList1.add("a1-threshold-rsrp");
			headerList1.add("a1-time-to-trigger");
			headerList1.add("a1-purpose");

			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			headerList4.add("gnodeb-id");
			headerList4.add("cell-identity");
			headerList4.add("nr-ul-coverage-method");

			NodeList nodeList = document.getElementsByTagName("gnb-cu-cp-function-entries");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData4 = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("gutran-cu-cell-entries");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("report-config-entries");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("a1-report-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											objtableData.put("gnodeb-id", getXmlElementData(element, "gnodeb-id"));
											objtableData.put("cell-identity",
													getXmlElementData(elementchild, "cell-identity"));
											objtableData.put("index", getXmlElementData(elementchild1, "index"));
											objtableData.put("a1-threshold-rsrp",
													getXmlElementData(elementchild2, "a1-threshold-rsrp"));
											objtableData.put("a1-time-to-trigger",
													getXmlElementData(elementchild2, "a1-time-to-trigger"));
											objtableData.put("a1-purpose",
													getXmlElementData(elementchild2, "a1-purpose"));

											tabelData1.add(objtableData);
										}
									}
								}
							}

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							objtableData.put("gnodeb-id", getXmlElementData(element, "gnodeb-id"));
							objtableData.put("cell-identity", getXmlElementData(elementchild, "cell-identity"));
							objtableData.put("nr-ul-coverage-method",
									getXmlElementData(elementchild, "nr-ul-coverage-method"));
							tabelData4.add(objtableData);
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			        if(neVersion.toUpperCase().contains("22.A")) {     
					htmlContent.append(
							"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "ACPF_22A_A1_checks" + "</b></td></tr>\n");
				    }else{
				    	htmlContent.append(
								"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
				    }
			
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + entry + "</b></td></tr>\n");
			String gnbID = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails) && listOfCiqDetails.get(0).getCiqMap().containsKey("GNODEBID")) {
				gnbID = listOfCiqDetails.get(0).getCiqMap().get("GNODEBID").getHeaderValue().replaceAll("^0+(?!$)", "");
			}
			Set<String> gnbIdSet = new HashSet<>();
			if (!gnbID.isEmpty()) {
				gnbIdSet.add(gnbID);
			}
			trimTabledataString1(tabelData1, gnbIdSet, "gnodeb-id");
			Set<Integer> cellIdset = getcellIds(enbId, dbcollectionFileName, "5GNRCIQAU", "eNBId");
			trimTabledata(tabelData1, cellIdset, "cell-identity");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("a1-threshold-rsrp")) {
						if (!value.equals("63")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("a1-time-to-trigger")) {
						if (!value.equals("ms128")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("a1-purpose")) {
						if (!value.equals("en-dc-in-nr-ul-coverage-ueulsplit-sup-purpose")
								&& tdData.get("index").equals("3")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("en-dc-in-nr-ul-coverage-ueulsplit-notsup-purpose")
								&& tdData.get("index").equals("5")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			entry = "nr-ul-coverage-method";
			trimTabledataString1(tabelData4, gnbIdSet, "gnodeb-id");
			trimTabledata(tabelData4, cellIdset, "cell-identity");
			HashMap<String, List<String>> validationMap = new HashMap<>();
			validationMap.put("nr-ul-coverage-method", Arrays.asList("A1-A2"));
			htmlContent.append(createHtmltableWithValidation(headerList4, entry, tabelData4, validationMap));

			htmlContent.append("</table>\n");
			
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData4, auditIssueAll);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "ACPF_A1_checks"  + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder createHtmltable(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

				tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");

			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	private void trimTabledata(List<LinkedHashMap<String, String>> tabelData, Set<Integer> cellidset, String key) {
		try {
			List<LinkedHashMap<String, String>> deleteData = new ArrayList<>();
			for (LinkedHashMap<String, String> tdData : tabelData) {
				if (tdData.containsKey(key) && !ObjectUtils.isEmpty(cellidset)) {
					if (!cellidset.contains(NumberUtils.toInt(tdData.get(key)))) {
						deleteData.add(tdData);
					}
				}
			}

			for (LinkedHashMap<String, String> tdData : deleteData) {
				tabelData.remove(tdData);
			}
		} catch (Exception e) {
			logger.error("AuditXmlRuleServiceUtil trimTabledata() " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	public Set<Integer> getcellIds(String enbId, String dbcollectionFileName, String sheetname, String idname) {
		Set<Integer> cellIdSet = new HashSet<>();
		try {
			String[] cellKeys = { "CC0 Cell Identity", "CC1 Cell Identity", "CC2 Cell Identity", "CC3 Cell Identity",
					"CC4 Cell Identity", "CC5 Cell Identity", "CC6 Cell Identity", "CC7 Cell Identity" };
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					sheetname, idname);
			if (!ObjectUtils.isEmpty(listOfCiqDetails)) {
				for (CIQDetailsModel ciqDetailsModel : listOfCiqDetails) {
					LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
					for (String cellKey : cellKeys) {
						if (objMapDetails.containsKey(cellKey)) {
							String cellId = objMapDetails.get(cellKey).getHeaderValue();
							if (NumberUtils.isNumber(cellId)) {
								cellIdSet.add(NumberUtils.toInt(cellId));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getcellIds() " + ExceptionUtils.getFullStackTrace(e));
		}

		return cellIdSet;
	}

	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where(idname).is(enbId).and("sheetAliasName").is(sheetname));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	public StringBuilder getAudit5GMMA2Checks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("gnodeb-id");
			headerList2.add("cell-identity");
			headerList2.add("index");
			headerList2.add("a2-threshold-rsrp");
			headerList2.add("a2-time-to-trigger");
			headerList2.add("a2-purpose");
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}
			NodeList nodeList = document.getElementsByTagName("gnb-cu-cp-function-entries");
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("gutran-cu-cell-entries");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("report-config-entries");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("a2-report-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											objtableData.put("gnodeb-id", getXmlElementData(element, "gnodeb-id"));
											objtableData.put("cell-identity",
													getXmlElementData(elementchild, "cell-identity"));
											objtableData.put("index", getXmlElementData(elementchild1, "index"));
											objtableData.put("a2-threshold-rsrp",
													getXmlElementData(elementchild2, "a2-threshold-rsrp"));
											objtableData.put("a2-time-to-trigger",
													getXmlElementData(elementchild2, "a2-time-to-trigger"));
											objtableData.put("a2-purpose",
													getXmlElementData(elementchild2, "a2-purpose"));

											tabelData2.add(objtableData);
										}
									}
								}
							}

						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			 if(neVersion.toUpperCase().contains("22.A")) {     
					htmlContent.append(
							"<tr><td colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>" + "ACPF_22A_A2_checks" + "</b></td></tr>\n");
				    }else{
				    	htmlContent.append(
								"<tr><td colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
				    }
			String gnbID = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails) && listOfCiqDetails.get(0).getCiqMap().containsKey("GNODEBID")) {
				gnbID = listOfCiqDetails.get(0).getCiqMap().get("GNODEBID").getHeaderValue().replaceAll("^0+(?!$)", "");
			}
			Set<String> gnbIdSet = new HashSet<>();
			if (!gnbID.isEmpty()) {
				gnbIdSet.add(gnbID);
			}
			trimTabledataString1(tabelData2, gnbIdSet, "gnodeb-id");

			Set<Integer> cellIdset = getcellIds(enbId, dbcollectionFileName, "5GNRCIQAU", "eNBId");

			String entry = "a2-report-config";
			htmlContent.append(
					"<tr><td colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>" + entry + "</b></td></tr>\n");
			trimTabledata(tabelData2, cellIdset, "cell-identity");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList2) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();

			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssue2 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData2) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("a2-threshold-rsrp")) {
						if (!value.equals("48") && tdData.get("index").equals("1")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("60") && tdData.get("index").equals("4")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("60") && tdData.get("index").equals("6")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("a2-time-to-trigger")) {
						if (!value.equals("ms40") && tdData.get("index").equals("1")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("ms128") && tdData.get("index").equals("4")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("ms128") && tdData.get("index").equals("6")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("a2-purpose")) {
						if (!value.equals("en-dc-out-of-nr-ul-coverage-ueulsplit-sup-purpose")
								&& tdData.get("index").equals("4")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("en-dc-out-of-nr-ul-coverage-ueulsplit-notsup-purpose")
								&& tdData.get("index").equals("6")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			if (auditIssue1 != null)
				auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);

			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "ACPF_A2_checks" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getAudit5GMMA3Checks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			headerList3.add("gnodeb-id");
			headerList3.add("cell-identity");
			headerList3.add("index");
			headerList3.add("a3-time-to-trigger");
			headerList3.add("a3-purpose");
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			NodeList nodeList = document.getElementsByTagName("gnb-cu-cp-function-entries");

			List<LinkedHashMap<String, String>> tabelData3 = new ArrayList<>();
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("gutran-cu-cell-entries");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("report-config-entries");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("a3-report-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											objtableData.put("gnodeb-id", getXmlElementData(element, "gnodeb-id"));
											objtableData.put("cell-identity",
													getXmlElementData(elementchild, "cell-identity"));
											objtableData.put("index", getXmlElementData(elementchild1, "index"));
											objtableData.put("a3-time-to-trigger",
													getXmlElementData(elementchild2, "a3-time-to-trigger"));
											objtableData.put("a3-purpose",
													getXmlElementData(elementchild2, "a3-purpose"));

											tabelData3.add(objtableData);
										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			 if(neVersion.toUpperCase().contains("22.A")) {     
					htmlContent.append(
							"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + "ACPF_22A_A3_checks" + "</b></td></tr>\n");
				    }else{
				    	htmlContent.append(
								"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
				    }

			String gnbID = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails) && listOfCiqDetails.get(0).getCiqMap().containsKey("GNODEBID")) {
				gnbID = listOfCiqDetails.get(0).getCiqMap().get("GNODEBID").getHeaderValue().replaceAll("^0+(?!$)", "");
			}
			Set<String> gnbIdSet = new HashSet<>();
			if (!gnbID.isEmpty()) {
				gnbIdSet.add(gnbID);
			}
			trimTabledataString1(tabelData3, gnbIdSet, "gnodeb-id");

			Set<Integer> cellIdset = getcellIds(enbId, dbcollectionFileName, "5GNRCIQAU", "eNBId");

			String entry = "a3-report-config";
			trimTabledata(tabelData3, cellIdset, "cell-identity");
			htmlContent.append(
					"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + entry + "</b></td></tr>\n");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList3) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData3) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("a3-time-to-trigger")) {
						if (!value.equals("ms128") && tdData.get("index").equals("2")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData3, auditIssueAll);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "ACPF_A3_checks" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getAudit5GMME1F1X2Checks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			String entry1 = "end-point-x2-entries";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("gnodeb-id");
			headerList1.add("x2-neighbor-index");
			headerList1.add("remote-ip-address");
			headerList1.add("secondary-remote-ip-address");
			headerList1.add("enb-id");
			headerList1.add("sctp-state");
			headerList1.add("x2-ap-state");
			headerList1.add("mcc");
			headerList1.add("mnc");

			String entry2 = "end-point-f1c-entries";
			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("gnodeb-id");
			headerList2.add("end-point-f1-index");
			headerList2.add("gnb-du-id");
			headerList2.add("remote-ip-address");
			headerList2.add("secondary-remote-ip-address");
			headerList2.add("sctp-state");
			headerList2.add("f1-ap-state");

			String entry3 = "end-point-e1-entries";
			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			headerList3.add("gnodeb-id");
			headerList3.add("end-point-e1-index");
			headerList3.add("cu-up-id");
			headerList3.add("remote-ip-address");
			headerList3.add("sctp-state");
			headerList3.add("e1-ap-state");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData3 = new ArrayList<>();
			StringBuilder auditIssueAll = new StringBuilder();

			// gnb-cu-cp-function
			NodeList nodeList = document.getElementsByTagName("gnb-cu-cp-function");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// gnb-cu-cp-function-entries
					NodeList childNodeList = element.getElementsByTagName("gnb-cu-cp-function-entries");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// end-point-x2
							NodeList childNodeList1 = elementchild.getElementsByTagName("end-point-x2");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// end-point-x2-entries
									NodeList childNodeList2 = elementchild1
											.getElementsByTagName("end-point-x2-entries");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList1.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData1.add(objtableData);
										}
									}
								}
							}

							// end-point-f1c
							childNodeList1 = elementchild.getElementsByTagName("end-point-f1c");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// end-point-f1c-entries
									NodeList childNodeList2 = elementchild1
											.getElementsByTagName("end-point-f1c-entries");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList2.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData2.add(objtableData);
										}
									}
								}
							}

							// end-point-e1
							childNodeList1 = elementchild.getElementsByTagName("end-point-e1");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// end-point-e1-entries
									NodeList childNodeList2 = elementchild1
											.getElementsByTagName("end-point-e1-entries");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList3.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData3.add(objtableData);
										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			 if(neVersion.toUpperCase().contains("22.A")) {     
					htmlContent.append(
							"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "ACPF_X2E1F1_22A_Checks" + "</b></td></tr>\n");
				    }else{
				    	htmlContent.append(
								"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
				    }
		
			String gnbID = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails) && listOfCiqDetails.get(0).getCiqMap().containsKey("GNODEBID")) {
				gnbID = listOfCiqDetails.get(0).getCiqMap().get("GNODEBID").getHeaderValue().replaceAll("^0+(?!$)", "");
			}
			Set<String> gnbIdSet = new HashSet<>();
			if (!gnbID.isEmpty()) {
				gnbIdSet.add(gnbID);
			}
			trimTabledataString1(tabelData1, gnbIdSet, "gnodeb-id");

			HashMap<String, List<String>> validationMap = new HashMap<>();
			validationMap.put("sctp-state", Arrays.asList("enabled"));
			validationMap.put("x2-ap-state", Arrays.asList("enabled"));
			htmlContent.append(createHtmltableendc(headerList1, entry1, tabelData1, validationMap));

			try {
				String siteName = null;
				List<NeMappingEntity> data1 = null;
				Set<String> gnodebIdList = new HashSet<>();
				List<NeMappingEntity> data = neMappingService.getSiteName(enbId);
				if (data != null && !data.isEmpty()) {
					siteName = data.get(0).getSiteName();
					data1 = neMappingService.getGnodebs(siteName);
					if (data1 != null) {
						for (NeMappingEntity neEntity : data1) {
							gnodebIdList.add(neEntity.getEnbId().replaceAll("^0+(?!$)", ""));
						}
					}
				}
				trimTabledataString(tabelData2, gnodebIdList, "gnb-du-id");
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}

			validationMap = new HashMap<>();
			validationMap.put("sctp-state", Arrays.asList("enabled"));
			validationMap.put("f1-ap-state", Arrays.asList("enabled"));
			htmlContent.append(createHtmltableendc(headerList2, entry2, tabelData2, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("sctp-state", Arrays.asList("enabled"));
			validationMap.put("e1-ap-state", Arrays.asList("enabled"));
			htmlContent.append(createHtmltableendc(headerList3, entry3, tabelData3, validationMap));

			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());


			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData3, auditIssueAll);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "ACPF_E1_F1_X2_checks" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	private void trimTabledataString(List<LinkedHashMap<String, String>> tabelData, Set<String> cellidset, String key) {
		try {
			List<LinkedHashMap<String, String>> deleteData = new ArrayList<>();
			for (LinkedHashMap<String, String> tdData : tabelData) {
				if (tdData.containsKey(key) && !ObjectUtils.isEmpty(cellidset)) {
					if (!cellidset.contains(tdData.get(key).replaceAll("^0+(?!$)", ""))) {
						deleteData.add(tdData);
					}
				}
			}

			for (LinkedHashMap<String, String> tdData : deleteData) {
				tabelData.remove(tdData);
			}
		} catch (Exception e) {
			logger.error("AuditXmlRuleServiceUtil trimTabledata() " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	StringBuilder createHtmltableendc(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData, HashMap<String, List<String>> validationMap) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
				String key = resultTableData.getKey();
				String value = resultTableData.getValue();
				if (validationMap.containsKey(key)) {
					if (validationMap.get(key).contains(value)) {
						tableData.append("<td align=center>" + value + "</td>\n");
					} else {
						tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
					}
				} else {
					tableData.append("<td align=center>" + value + "</td>\n");
				}
			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	public StringBuilder getAudit5GMMHTMlParamChecks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}
			

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			String entry1 = "drx-profile-du";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cell-identity");
			headerList1.add("drx-cycle");
			headerList1.add("drx-on-duration-timer-msec-normal");
			headerList1.add("drx-inactivity-timer-normal");
			headerList1.add("drx-unsynch-counter-th");

			String entry4 = "beam-management";
			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			headerList4.add("cell-identity");
			headerList4.add("dl-mu-mimo-switch");
			headerList4.add("num-trs-restriction");
			headerList4.add("qcl-config-periodic-csi-rs");
			headerList4.add("num-trs-restriction-sdx50");
			String entry5 = "cell-cac-info";
			LinkedHashSet<String> headerList5 = new LinkedHashSet<>();
			headerList5.add("cell-identity");
			headerList5.add("nsa-call-threshold");
			String entry6 = "coloc-covered-cell-num";
			LinkedHashSet<String> headerList6 = new LinkedHashSet<>();
			headerList6.add("cell-identity");
			headerList6.add("coloc-covered-cell-num");
			String entry7 = "csl-tce-ems-server";
			LinkedHashSet<String> headerList7 = new LinkedHashSet<>();
			headerList7.add("csl-tce-ems-server-port");
			headerList7.add("csl-tce-ems-option");
			String entry8 = "csl-tce-server";
			LinkedHashSet<String> headerList8 = new LinkedHashSet<>();
			headerList8.add("csl-tce-server-ip-address");
			headerList8.add("csl-tce-server-port");
			headerList8.add("csl-tce-option");
			String entry9 = "drb-rlc-info-func";
			LinkedHashSet<String> headerList9 = new LinkedHashSet<>();
			headerList9.add("config-type");
			headerList9.add("qci");
			headerList9.add("gnb-timer-poll-retransmit");
			headerList9.add("ue-t-reassembly");
			headerList9.add("ue-timer-status-prohibit");
			String entry10 = "dl-mimo-configuration";
			LinkedHashSet<String> headerList10 = new LinkedHashSet<>();

			headerList10.add("pmi-cycling-switch");
			String entry11 = "ul-power-control-common-config";
			LinkedHashSet<String> headerList11 = new LinkedHashSet<>();
			headerList11.add("cell-identity");
			headerList11.add("p0-nominal-with-grant");
			headerList11.add("p0-nominal-pucch");
			String entry12 = "tssi-info";
			LinkedHashSet<String> headerList12 = new LinkedHashSet<>();
			headerList12.add("modem-id");
			headerList12.add("cell-num");
			headerList12.add("path");
			headerList12.add("tssi");
			String entry13 = "ul-mimo-configuration";
			LinkedHashSet<String> headerList13 = new LinkedHashSet<>();
			headerList13.add("cell-identity");
			headerList13.add("ul-su-mimo-switch");
			headerList13.add("ul-mu-mimo-switch");
			headerList13.add("dynamic-srs-port-adaptation");
			String entry14 = "logical-channel-config";
			LinkedHashSet<String> headerList14 = new LinkedHashSet<>();
			headerList14.add("qci");
			headerList14.add("status");
			headerList14.add("prioritised-bitrate");
			headerList14.add("bucket-size-duration");
			headerList14.add("logical-channel-group");
			headerList14.add("logical-channel-sr-mask");
			headerList14.add("logical-channel-sr-delay-timer-applied");

			String entry15 = "nr-carrier-aggregation";
			LinkedHashSet<String> headerList15 = new LinkedHashSet<>();
			headerList15.add("cell-identity");
			headerList15.add("ca-available-type");
			headerList15.add("p-cell-only-flag");
			headerList15.add("max-dl-ca-cc-num");
			headerList15.add("max-ul-ca-cc-num");
			String entry16 = "pdsch-config";
			LinkedHashSet<String> headerList16 = new LinkedHashSet<>();
			headerList16.add("cell-identity");
			headerList16.add("dmrs-pdsch-fdm");
			headerList16.add("pdcch-pdsch-fdm");
			String entry17 = "prach-config";
			LinkedHashSet<String> headerList17 = new LinkedHashSet<>();
			headerList17.add("cell-identity");
			headerList17.add("preamble-receiver-target-power");
			headerList17.add("rsrp-threshold");
			headerList17.add("preamble-trans-max");
			headerList17.add("msg1-frequency-start");
			headerList17.add("prach-configuration-index");
			headerList17.add("ssb-per-rach-occasion-choice");
			headerList17.add("cb-preambles-per-ssb");
			headerList17.add("auto-configure-prach-rb-offset-on-off");
			headerList17.add("prach-receiver-mode-enable");

			String entry19 = "ssb-configuration";
			LinkedHashSet<String> headerList19 = new LinkedHashSet<>();
			headerList19.add("cell-identity");
			headerList19.add("ssb-subcarrier-spacing");
			headerList19.add("ssb-periodicity");
			headerList19.add("nr-of-tx-ssb");
			headerList19.add("rmsi-coreset-index");
			headerList19.add("ssb-position");
			headerList19.add("ssb-freq-offset");

			headerList19.add("ssb-freq-config-mode");
			String entry20 = "managed-element";
			LinkedHashSet<String> headerList20 = new LinkedHashSet<>();
			headerList20.add("administrative-state");
			headerList20.add("operational-state");
			headerList20.add("sw-version");
			headerList20.add("user-label");
			headerList20.add("operational-mode");

			String entry21 = "gutran-du-cell-entries";
			LinkedHashSet<String> headerList21 = new LinkedHashSet<>();
			headerList21.add("cell-identity");
			headerList21.add("administrative-state");
			headerList21.add("operational-state");
			String entry22 = "active-alarm-entries";
			LinkedHashSet<String> headerList22 = new LinkedHashSet<>();
			headerList22.add("alarm-unit-type");
			headerList22.add("alarm-unit-id");
			headerList22.add("alarm-type");
			headerList22.add("location");
			headerList22.add("raised-time");

			headerList22.add("probable-cause");
			headerList22.add("specific-problem");
			headerList22.add("severity");

			String entry23 = "pusch-power-control-config";
			LinkedHashSet<String> headerList23 = new LinkedHashSet<>();
			headerList23.add("cell-identity");
			headerList23.add("max-target-sinr-64qam");
			headerList23.add("rerror-tpc-up-cmd");
			String entry24 = "ul-ca-scheduler-config";
			LinkedHashSet<String> headerList24 = new LinkedHashSet<>();
			headerList24.add("cell-identity");
			headerList24.add("ul-total-power-limit-offset");
			String entry25 = "ip-interface";
			LinkedHashSet<String> headerList25 = new LinkedHashSet<>();
			headerList25.add("interface-name");
			headerList25.add("ip");
			headerList25.add("management");
			headerList25.add("control");
			headerList25.add("bearer");
			headerList25.add("ieee1588");
			String entry26 = "radio-unit-info";
			LinkedHashSet<String> headerList26 = new LinkedHashSet<>();
			headerList26.add("electrical-tilt");
			headerList26.add("operational-mode");

			String entry27 = "digital-unit-entries";
			LinkedHashSet<String> headerList27 = new LinkedHashSet<>();
			headerList27.add("unit-id");
			headerList27.add("port-id");
			headerList27.add("mtu");
			headerList27.add("tx-wavelength");
			headerList27.add("tx-power");
			headerList27.add("rx-power");

			String entry28 = "ul-mini-slot-config";
			LinkedHashSet<String> headerList28 = new LinkedHashSet<>();
			headerList28.add("cell-identity");
			headerList28.add("msg3-mini-slot-on-off");
			headerList28.add("ul-mini-slot-on-off");
			headerList28.add("ap-csi-rep-mini-slot-on-off");
			headerList28.add("normal-pusch-mini-slot-on-off");

			String entry29 = "pusch-waveform-config-idle";
			LinkedHashSet<String> headerList29 = new LinkedHashSet<>();
			headerList29.add("cell-identity");
			headerList29.add("pusch-waveform-adaptation-mode");

			String entry30 = "ul-scheduling-common-config";
			LinkedHashSet<String> headerList30 = new LinkedHashSet<>();
			headerList30.add("cell-identity");
			headerList30.add("retx-bsr-timer");

			String entry31 = "tdd-config-idle";
			LinkedHashSet<String> headerList31 = new LinkedHashSet<>();
			headerList31.add("cell-identity");
			headerList31.add("tdd-index");
			headerList31.add("nr-of-downlink-symbols-1");

			String entry32 = "multicarrier-scenario-functionality-config";
			LinkedHashSet<String> headerList32 = new LinkedHashSet<>();
			headerList32.add("fr2-ue-overheating-mitigation-support");

			String entry33 = "prach-coverage-extension";
			LinkedHashSet<String> headerList33 = new LinkedHashSet<>();
			headerList33.add("maximum-supportable-coverage-fr2");

			String entry34 = "pucch-au-du-config-idle";
			LinkedHashSet<String> headerList34 = new LinkedHashSet<>();
			headerList34.add("a6-pucch-csi-coverage-enh");
			String entry35 = "ul-su-mimo-config";
			LinkedHashSet<String> headerList35 = new LinkedHashSet<>();
			headerList35.add("cell-identity");
			headerList35.add("rank1-best-tpmi-selection-for-codebook-non-coherent-2t");
			headerList35.add("ul-su-mimo-phr-rb-threshold-rank2-in");

			String entry36 = "ul-preschedule";
			LinkedHashSet<String> headerList36 = new LinkedHashSet<>();
			headerList36.add("cell-identity");
			headerList36.add("latency-group-enable");
			headerList36.add("latency-group-zero-qci");
			headerList36.add("latency-group-zero-active-duration");
			headerList36.add("latency-group-zero-grant-size");

			String entry37 = "qci-scheduling-config";
			LinkedHashSet<String> headerList37 = new LinkedHashSet<>();
			headerList37.add("qci");
			headerList37.add("priority-level-for-qci");

			String entry38 = "ul-pusch-config";
			LinkedHashSet<String> headerList38 = new LinkedHashSet<>();
			headerList38.add("cell-identity");
			headerList38.add("pusch-allocationlist-mode");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData4 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData5 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData6 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData7 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData8 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData9 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData10 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData11 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData12 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData13 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData14 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData15 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData16 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData17 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData19 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData20 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData21 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData22 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData23 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData24 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData25 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData26 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData27 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData28 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData29 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData30 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData31 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData32 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData33 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData34 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData35 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData36 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData37 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData38 = new ArrayList<>();
			StringBuilder auditIssueAll = new StringBuilder();
			// gnb-du-function
			NodeList nodeList = document.getElementsByTagName("gnb-du-function");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// gutran-du-cell
					NodeList childNodeList = element.getElementsByTagName("gutran-du-cell");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// gutran-du-cell-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("gutran-du-cell-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// drx-config-du-cell
									NodeList childNodeList2 = elementchild1.getElementsByTagName("drx-config-du-cell");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// drx-profile-du
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("drx-profile-du");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList1.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData1.add(objtableData);
												}
											}
										}
									}

									// beam-management
									childNodeList2 = elementchild1.getElementsByTagName("beam-management");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList4.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData4.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("ul-preschedule");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList36.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData36.add(objtableData);
										}
									}

									// ul-scheduling-common-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-scheduling-common-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList30.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData30.add(objtableData);
										}
									}

									// tdd-config-list
									childNodeList2 = elementchild1.getElementsByTagName("tdd-config-list");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList31.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData31.add(objtableData);
										}
									}

									// ul-mini-slot-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-mini-slot-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList28.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData28.add(objtableData);
										}
									}

									// cell-cac-info
									childNodeList2 = elementchild1.getElementsByTagName("cell-cac-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList5.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData5.add(objtableData);
										}
									}

									// nr-carrier-aggregation
									childNodeList2 = elementchild1.getElementsByTagName("nr-carrier-aggregation");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											for (int z = 0; z < elementchild2
													.getElementsByTagName("coloc-covered-cell-num").getLength(); z++) {
												LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
												objtableData.put("cell-identity",
														getXmlElementData(elementchild1, "cell-identity"));
												objtableData.put("coloc-covered-cell-num",
														elementchild2.getElementsByTagName("coloc-covered-cell-num")
																.item(z).getTextContent());
												tabelData6.add(objtableData);
											}

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList15.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData15.add(objtableData);
										}
									}

									// ul-power-control-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-power-control-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// ul-power-control-common-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ul-power-control-common-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList11.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData11.add(objtableData);
												}
											}

											// pusch-power-control-config
											childNodeList3 = elementchild2
													.getElementsByTagName("pusch-power-control-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList23.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData23.add(objtableData);
												}
											}
										}
									}

									// ul-ca-scheduler-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-ca-scheduler-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// ul-ca-power-sharing-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ul-ca-power-sharing-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList24.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData24.add(objtableData);
												}
											}
										}
									}

									// ul-mimo-configuration
									childNodeList2 = elementchild1.getElementsByTagName("ul-mimo-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList13.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData13.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("ul-mimo-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// pdsch-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ul-su-mimo-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList35.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData35.add(objtableData);
												}
											}
										}
									}

									// dl-physical-resource-config
									childNodeList2 = elementchild1.getElementsByTagName("dl-physical-resource-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// pdsch-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("pdsch-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList16.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData16.add(objtableData);
												}
											}
										}
									}

									// ul-physical-resource-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-physical-resource-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// pdsch-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("prach-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList17.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData17.add(objtableData);
												}
											}

											// srs-resource-config
											childNodeList3 = elementchild2
													.getElementsByTagName("pusch-waveform-config-idle");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList29.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData29.add(objtableData);
												}
											}
											childNodeList3 = elementchild2.getElementsByTagName("ul-pusch-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList38.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData38.add(objtableData);
												}
											}

										}
									}

									// ssb-configuration
									childNodeList2 = elementchild1.getElementsByTagName("ssb-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList19.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData19.add(objtableData);
										}
									}

									// gutran-du-cell-entries
									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									for (String headername : headerList21) {
										objtableData.put(headername, getXmlElementData(elementchild1, headername));
									}
									tabelData21.add(objtableData);
								}
							}
						}
					}

					// rlc-functions
					childNodeList = element.getElementsByTagName("rlc-functions");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// endc-bc-information-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("drb-rlc-info-func");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList9.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData9.add(objtableData);
								}
							}
						}
					}

					// dl-mimo-configuration
					childNodeList = element.getElementsByTagName("dl-mimo-configuration");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList10.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData10.add(objtableData);
						}
					}

					// multicarrier-scenario-functionality-config
					childNodeList = element.getElementsByTagName("multicarrier-scenario-functionality-config");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList32.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData32.add(objtableData);
						}
					}
					childNodeList = element.getElementsByTagName("prach-coverage-extension");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList33.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData33.add(objtableData);
						}
					}
					childNodeList = element.getElementsByTagName("pucch-au-du-config-idle");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList34.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData34.add(objtableData);
						}
					}
					// gutran-du-qci
					childNodeList = element.getElementsByTagName("gutran-du-qci");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// logical-channel-config
							NodeList childNodeList1 = elementchild.getElementsByTagName("logical-channel-config");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									Iterator<String> itr = headerList14.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild, header));
									header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild1, header));
									}
									tabelData14.add(objtableData);
								}
							}
							childNodeList1 = elementchild.getElementsByTagName("qci-scheduling-config");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									Iterator<String> itr = headerList37.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild1, header));
									}
									tabelData37.add(objtableData);
								}
							}
						}
					}

				}
			}

			// common-management
			nodeList = document.getElementsByTagName("common-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// csl-configuration
					NodeList childNodeList = element.getElementsByTagName("csl-configuration");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// csl-tce-ems-server
							NodeList childNodeList1 = elementchild.getElementsByTagName("csl-tce-ems-server");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList7.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData7.add(objtableData);
								}
							}

							// csl-tce-server
							childNodeList1 = elementchild.getElementsByTagName("csl-tce-server");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList8.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData8.add(objtableData);
								}
							}
						}
					}
				}
			}

			// hardware-management
			nodeList = document.getElementsByTagName("hardware-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// digital-unit
					NodeList childNodeList = element.getElementsByTagName("digital-unit");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// tssi
							NodeList childNodeList1 = elementchild.getElementsByTagName("tssi");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// tssi-info
									NodeList childNodeList2 = elementchild1.getElementsByTagName("tssi-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											headerList12.forEach(header -> {
												objtableData.put(header, getXmlElementData(elementchild2, header));
											});
											tabelData12.add(objtableData);

										}
									}
								}
							}

							// digital-unit-entries
							childNodeList1 = elementchild.getElementsByTagName("digital-unit-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;
									// external-port
									NodeList childNodeList2 = elementchild1.getElementsByTagName("external-port");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											// ethernet-port
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ethernet-port");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;
													// ethernet-port-entries
													NodeList childNodeList4 = elementchild3
															.getElementsByTagName("ethernet-port-entries");

													for (int n = 0; n < childNodeList4.getLength(); n++) {
														Node ChildNode4 = childNodeList4.item(n);

														if (Node.ELEMENT_NODE == ChildNode4.getNodeType()) {
															Element elementchild4 = (Element) ChildNode4;
															LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
															Iterator<String> itr = headerList27.iterator();
															String header = itr.next();
															objtableData.put(header, getXmlElementData(elementchild1, header));
															header = itr.next();
															objtableData.put(header, getXmlElementData(elementchild4, header));

															while (itr.hasNext()) {
																header = itr.next();
																objtableData.put(header, getXmlElementData(elementchild4, header));
															}
															tabelData27.add(objtableData);
															/*LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
															headerList27.forEach(header -> {
																objtableData.put(header,
																		getXmlElementData(elementchild4, header));
															});
															tabelData27.add(objtableData);*/

														}
													}
												}
											}
										}
									}
								}
							}
						}
					}

					// radio-unit
					childNodeList = element.getElementsByTagName("radio-unit");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// radio-unit-info
							NodeList childNodeList1 = elementchild.getElementsByTagName("radio-unit-info");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList26.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData26.add(objtableData);
								}
							}
						}
					}
				}
			}

			// fault-management
			nodeList = document.getElementsByTagName("fault-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// active-alarm
					NodeList childNodeList = element.getElementsByTagName("active-alarm");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// active-alarm-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("active-alarm-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;
									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList22.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData22.add(objtableData);
								}
							}
						}
					}
				}
			}

			nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {
					Element element = (Element) inChildNode;
					LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
					headerList20.forEach(header -> {
						objtableData.put(header, getXmlElementData(element, header));
					});
					tabelData20.add(objtableData);
				}
			}

			// ip-interface
			nodeList = document.getElementsByTagName("ip-system");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// cpu
					NodeList childNodeList = element.getElementsByTagName("cpu");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// ip-interface
							NodeList childNodeList1 = elementchild.getElementsByTagName("ip-interface");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// external-interfaces
									NodeList childNodeList2 = elementchild1.getElementsByTagName("external-interfaces");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// ipv6-address
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ipv6-address");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList25.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild2, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData25.add(objtableData);
												}
											}

										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			if(neVersion.toUpperCase().contains("22.A")) {
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "AU_22A_Param_Check" + "</b></td></tr>\n");
			}else {
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			}
			HashMap<String, List<String>> validationMap = new HashMap<>();
			
			
			htmlContent.append(
					"<tr><td colspan=" + headerList20.size() + " bgcolor=#EEEEEE><b>" + entry20 + "</b></td></tr>\n");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList20) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData20) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					if ("administrative-state".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("unlocked")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("operational-state".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("enabled")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("user-label".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().toUpperCase().contains("GROW")) {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");

						} else {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("operational-mode".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("normal-mode")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			validationMap = new HashMap<>();
			validationMap.put("administrative-state", Arrays.asList("unlocked"));
			validationMap.put("operational-state", Arrays.asList("enabled"));
			htmlContent.append(createHtmltableWithValidation(headerList21, entry21, tabelData21, validationMap));

			htmlContent.append(createHtmltableAU20AParamCheck(headerList25, entry25, tabelData25));
			validationMap = new HashMap<>();
			validationMap.put("operational-mode", Arrays.asList("normal-mode"));
			htmlContent.append(createHtmltableWithValidation(headerList26, entry26, tabelData26, validationMap));

			htmlContent.append(
					"<tr><td colspan=" + headerList27.size() + " bgcolor=#EEEEEE><b>" + entry27 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList27) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData27) {
				HashMap<String, String> map;
				String portId = tdData.get("port-id");
				String unitId = tdData.get("unit-id");
				String hw = "";
				String txPowerUl = "0";
				String txPowerLl = "0";
				String rxPowerUl = "0";
				String rxPowerLl = "0";
				String txPowerMax = "0";
				String txPowerMin = "0";

				map = gethadwareAndVender(fullOutputLog, XmlCommandsConstants.AU_21D_SFP_INVENTORY,portId,unitId);
				if (!map.isEmpty()) {
					hw = map.get("hardware-name").trim();
				}

				List<Audit5GMMHardwareDetailsEntity> auditConstantsList1 = audit5GMMHardwareDetailsRepository
						.getAuditHardwareDetailsEntityList(hw, "AU");
				
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerUl = auditConstantsList1.get(0).getFailRxPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerLl = auditConstantsList1.get(0).getFailRxPowerLL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					rxPowerUl = auditConstantsList1.get(0).getWarningRxPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					rxPowerLl = auditConstantsList1.get(0).getWarningRxPowerLL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerMax = auditConstantsList1.get(0).gettXPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerMin = auditConstantsList1.get(0).gettXPowerLL();
				}

				Double txPowerUlD = 0.0;
				Double txPowerLlD = 0.0;
				Double rxPowerUlD = 0.0;
				Double rxPowerLlD = 0.0;
				Double txPowerMaxLimit = 0.0;
				Double txPowerMinLimit = 0.0;
				
				if (NumberUtils.isNumber(txPowerUl)) {
					txPowerUlD = Double.parseDouble(txPowerUl);
				}
				if (NumberUtils.isNumber(txPowerLl)) {
					txPowerLlD = Double.parseDouble(txPowerLl);
				}
				if (NumberUtils.isNumber(rxPowerUl)) {
					rxPowerUlD = Double.parseDouble(rxPowerUl);
				}
				if (NumberUtils.isNumber(rxPowerLl)) {
					rxPowerLlD = Double.parseDouble(rxPowerLl);
				}
				if (NumberUtils.isNumber(txPowerMax)) {
					txPowerMaxLimit = Double.parseDouble(txPowerMax);
				}
				if (NumberUtils.isNumber(txPowerMin)) {
					txPowerMinLimit = Double.parseDouble(txPowerMin);
				}

				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("rx-power")) {

						if (NumberUtils.isNumber(value)) {
							Double rxPower = Double.parseDouble(value);

							if (rxPower >= txPowerLlD && rxPower <= txPowerUlD) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else if (rxPower >= rxPowerLlD && rxPower <= rxPowerUlD) {
								tableData.append("<td align=center bgcolor ='#FFA500'>" + value + "</td>\n");

							}  else {
								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							}
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("tx-power")) {

						if (NumberUtils.isNumber(value)) {
							Double txPower = Double.parseDouble(value);

							if (txPower >= txPowerMinLimit && txPower <= txPowerMaxLimit) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else {

								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							}
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center >" + value + "</td>\n");

					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			validationMap = new HashMap<>();
			validationMap.put("drx-cycle", Arrays.asList("drx-long-cycle-normal-ms160"));
			validationMap.put("drx-on-duration-timer-msec-normal", Arrays.asList("drx-on-duration-ms10"));
			validationMap.put("drx-inactivity-timer-normal", Arrays.asList("drx-inactivity-ms100"));
			validationMap.put("drx-unsynch-counter-th", Arrays.asList("5"));
			htmlContent.append(createHtmltableWithValidation(headerList1, entry1, tabelData1, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("dl-mu-mimo-switch", Arrays.asList("off"));
			validationMap.put("num-trs-restriction", Arrays.asList("disable"));
			validationMap.put("num-trs-restriction-sdx50", Arrays.asList("disable"));
			validationMap.put("qcl-config-periodic-csi-rs", Arrays.asList("off"));
			htmlContent.append(createHtmltableWithValidation(headerList4, entry4, tabelData4, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("nsa-call-threshold", Arrays.asList("100.0", "100"));
			htmlContent.append(createHtmltableWithValidation(headerList5, entry5, tabelData5, validationMap));

			htmlContent.append(createHtmltableAU20AParamCheck(headerList6, entry6, tabelData6));

			validationMap = new HashMap<>();
			validationMap.put("csl-tce-ems-option", Arrays.asList("abnormal-call-only"));
			htmlContent.append(createHtmltableWithValidation(headerList7, entry7, tabelData7, validationMap));

			validationMap = new HashMap<>();
			if(!neVersion.toUpperCase().contains("22.A")) {
			validationMap.put("csl-tce-server-port", Arrays.asList("50021"));
			}
			validationMap.put("csl-tce-option", Arrays.asList("normal-and-abnormal-and-intra-ho-call"));
			htmlContent.append(createHtmltableWithValidation(headerList8, entry8, tabelData8, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("qci", Arrays.asList("7", "132", "8", "9"));
			validationMap.put("gnb-timer-poll-retransmit", Arrays.asList("t-poll-retransmit-ms30"));
			validationMap.put("ue-t-reassembly", Arrays.asList("ms15"));
			validationMap.put("ue-timer-status-prohibit", Arrays.asList("ms15"));
			htmlContent.append(createHtmltableWithValidation(headerList9, entry9, tabelData9, validationMap));

			validationMap = new HashMap<>();

			validationMap.put("pmi-cycling-switch", Arrays.asList("on"));
			htmlContent.append(createHtmltableWithValidation(headerList10, entry10, tabelData10, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("p0-nominal-with-grant", Arrays.asList("-76"));
			validationMap.put("p0-nominal-pucch", Arrays.asList("-80"));
			htmlContent.append(createHtmltableWithValidation(headerList11, entry11, tabelData11, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("max-target-sinr-64qam", Arrays.asList("28"));
			validationMap.put("rerror-tpc-up-cmd", Arrays.asList("0"));
			htmlContent.append(createHtmltableWithValidation(headerList23, entry23, tabelData23, validationMap));
            if(!neVersion.contains("22.A")) {
			validationMap = new HashMap<>();
			htmlContent.append(createHtmltableWithValidation(headerList24, entry24, tabelData24, validationMap));
            }
			// tssi
			validationMap = new HashMap<>();
			validationMap.put("tssi", Arrays.asList("0", "-"));
			htmlContent.append(createHtmltablewithInverseValidation(headerList12, entry12, tabelData12, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("ul-su-mimo-switch", Arrays.asList("on"));
			validationMap.put("dynamic-srs-port-adaptation", Arrays.asList("off"));
			htmlContent.append(createHtmltableWithValidation(headerList13, entry13, tabelData13, validationMap));

			

			htmlContent.append(
					"<tr><td colspan=" + headerList14.size() + " bgcolor=#EEEEEE><b>" + entry14 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList14) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData14) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					if ("qci".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("7") || resultTableData.getValue().equals("8")
								|| resultTableData.getValue().equals("9") || resultTableData.getValue().equals("132")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("prioritised-bitrate".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("prioritised-bit-rate-infinity")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("bucket-size-duration".equalsIgnoreCase(resultTableData.getKey())
							&& tdData.get("qci").equals("132")) {
						if (resultTableData.getValue().equals("bucket-size-duration-ms50")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("logical-channel-group".equalsIgnoreCase(resultTableData.getKey())
							&& tdData.get("qci").equals("132")) {
						if (resultTableData.getValue().equals("3")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("logical-channel-sr-mask".equalsIgnoreCase(resultTableData.getKey())
							&& tdData.get("qci").equals("132")) {
						if (resultTableData.getValue().equals("0")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("logical-channel-sr-delay-timer-applied".equalsIgnoreCase(resultTableData.getKey())
							&& tdData.get("qci").equals("132")) {
						if (resultTableData.getValue().equals("0")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			validationMap = new HashMap<>();
			validationMap.put("max-dl-ca-cc-num", Arrays.asList("8"));
			validationMap.put("max-ul-ca-cc-num", Arrays.asList("4"));
			htmlContent.append(createHtmltableWithValidation(headerList15, entry15, tabelData15, validationMap));

			validationMap = new HashMap<>();
			if(neVersion.toUpperCase().contains("22.A")) {
				htmlContent.append(
						"<tr><td colspan=" + headerList16.size() + " bgcolor=#EEEEEE><b>" + entry16 + "</b></td></tr>\n");
				tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList16) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}

				tableHeader = tableHeader + "</tr>\n";
				tableData = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tabelData16) {
					tableData.append("<tr>\n");

					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
						if ("dmrs-pdsch-fdm".equalsIgnoreCase(resultTableData.getKey()) || "pdcch-pdsch-fdm".equalsIgnoreCase(resultTableData.getKey())) {
							if (resultTableData.getValue().equalsIgnoreCase("true")) {
								tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
							} else {
								tableData.append(
										"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
							}
						}  else {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						}

					}
					tableData.append("</tr>\n");

				}
				htmlContent.append(tableHeader);
				htmlContent.append(tableData);
			}else {
				validationMap.put("dmrs-pdsch-fdm", Arrays.asList("false"));
				validationMap.put("pdcch-pdsch-fdm", Arrays.asList("false"));
				htmlContent.append(createHtmltableWithValidation(headerList16, entry16, tabelData16, validationMap));
			}
			
		
			
			
			validationMap = new HashMap<>();
			validationMap.put("preamble-receiver-target-power", Arrays.asList("-69"));
			validationMap.put("rsrp-threshold", Arrays.asList("48"));
			validationMap.put("preamble-trans-max", Arrays.asList("preamble-trans-max-n8"));

			validationMap.put("prach-configuration-index", Arrays.asList("194"));
			validationMap.put("ssb-per-rach-occasion-choice", Arrays.asList("ssb-per-ro-two-choice"));
			validationMap.put("cb-preambles-per-ssb", Arrays.asList("28"));
			validationMap.put("prach-receiver-mode-enable", Arrays.asList("true"));
			htmlContent.append(createHtmltableWithValidation(headerList17, entry17, tabelData17, validationMap));

			htmlContent.append(
					"<tr><td colspan=" + headerList19.size() + " bgcolor=#EEEEEE><b>" + entry19 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList19) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData19) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					if ("ssb-freq-config-mode".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("gscn-based-manual")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("rmsi-coreset-index".equalsIgnoreCase(resultTableData.getKey())) {
						if (tdData.get("ssb-freq-offset").equals("-1") && resultTableData.getValue().equals("1")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else if ((tdData.get("ssb-freq-offset").equals("0")
								|| tdData.get("ssb-freq-offset").equals("1"))
								&& resultTableData.getValue().equals("0")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("nr-of-tx-ssb".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("48")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("ssb-position".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue()
								.equals("1111111111110000000011111111111111111111111100000000111111111111")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			// htmlContent.append(createHtmltableAU20AParamCheck(headerList22, entry22,
			// tabelData22));

			htmlContent.append(
					"<tr><td colspan=" + headerList22.size() + " bgcolor=#EEEEEE><b>" + entry22 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList22) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData22) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

					tableData.append("<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			validationMap = new HashMap<>();
			validationMap.put("msg3-mini-slot-on-off", Arrays.asList("1"));
			validationMap.put("ul-mini-slot-on-off", Arrays.asList("1"));
			validationMap.put("ap-csi-rep-mini-slot-on-off", Arrays.asList("1"));
			validationMap.put("normal-pusch-mini-slot-on-off", Arrays.asList("1"));

			htmlContent.append(createHtmltableWithValidation(headerList28, entry28, tabelData28, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("pusch-waveform-adaptation-mode",
					Arrays.asList("enable-pusch-waveform-adaptation-with-msg3-cp-ofdm"));
			htmlContent.append(createHtmltableWithValidation(headerList29, entry29, tabelData29, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("retx-bsr-timer", Arrays.asList("retx-bsr-timer-sf40"));
			htmlContent.append(createHtmltableWithValidation(headerList30, entry30, tabelData30, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("nr-of-downlink-symbols-1", Arrays.asList("9"));
			htmlContent.append(createHtmltableWithValidation(headerList31, entry31, tabelData31, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("fr2-ue-overheating-mitigation-support", Arrays.asList("on"));
			htmlContent.append(createHtmltableWithValidation(headerList32, entry32, tabelData32, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("maximum-supportable-coverage-fr2", Arrays.asList("max-1250m-coverage"));
			htmlContent.append(createHtmltableWithValidation(headerList33, entry33, tabelData33, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("a6-pucch-csi-coverage-enh", Arrays.asList("a6-pucch-csi-coverage-enh-enable"));
			htmlContent.append(createHtmltableWithValidation(headerList34, entry34, tabelData34, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("rank1-best-tpmi-selection-for-codebook-non-coherent-2t", Arrays.asList("off"));
			validationMap.put("ul-su-mimo-phr-rb-threshold-rank2-in", Arrays.asList("1"));
			htmlContent.append(createHtmltableWithValidation(headerList35, entry35, tabelData35, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("latency-group-enable", Arrays.asList("1"));
			validationMap.put("latency-group-zero-qci", Arrays.asList("133"));
			validationMap.put("latency-group-zero-active-duration", Arrays.asList("330"));
			validationMap.put("latency-group-zero-grant-size", Arrays.asList("64"));
			htmlContent.append(createHtmltableWithValidation(headerList36, entry36, tabelData36, validationMap));

			htmlContent.append(
					"<tr><td colspan=" + headerList37.size() + " bgcolor=#EEEEEE><b>" + entry37 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList37) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData37) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("priority-level-for-qci")) {
						if (!value.equals("80") && tdData.get("qci").equals("132")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			validationMap = new HashMap<>();
			validationMap.put("pusch-allocationlist-mode", Arrays.asList("pusch-allocationlist-mode-minislot-on"));

			htmlContent.append(createHtmltableWithValidation(headerList38, entry38, tabelData38, validationMap));
			htmlContent.append("</table>\n");
			
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData4, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData5, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData6, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData7, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData8, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData9, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData10, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData11, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData12, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData13, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData14, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData15, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData16, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData17, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData19, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData20, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData21, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData22, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData23, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData24, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData25, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData26, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData27, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData28, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData29, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData30, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData31, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData32, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData33, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData34, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData35, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData36, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData37, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData38, auditIssueAll);
			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "AU_Param_Check" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	private HashMap<String, String> gethadwareAndVender(String fullOutputLog, String command, String portId, String unitId) {
		HashMap<String, String> map = new HashMap<>();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("processor-unit-id");
			headerList1.add("port-id");
			headerList1.add("unit-id");
			headerList1.add("hardware-name");
			headerList1.add("serial-number");
			headerList1.add("vendor-name");
			headerList1.add("wave-length");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("optic-module-inventory");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild6, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.get("port-id").equals(portId) && tdData.get("processor-unit-id").equals(unitId)) {
					map.put("hardware-name", tdData.get("hardware-name"));

				}
			}
			/*for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (!ObjectUtils.isEmpty(tdData.get("hardware-name"))) {
					map.put("hardware-name", tdData.get("hardware-name"));

				}
			}*/

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return map;
	}

	StringBuilder createHtmltablewithInverseValidation(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData, HashMap<String, List<String>> validationMap) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
				String key = resultTableData.getKey();
				String value = resultTableData.getValue();
				if (validationMap.containsKey(key)) {
					if (validationMap.get(key).contains(value)) {
						tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				} else {
					tableData.append("<td align=center>" + value + "</td>\n");
				}
			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	StringBuilder createHtmltableAU20AParamCheck(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

				tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");

			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	StringBuilder createHtmltableWithValidation(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData, HashMap<String, List<String>> validationMap) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
				String key = resultTableData.getKey();
				String value = resultTableData.getValue();
				if (validationMap.containsKey(key)) {
					if (validationMap.get(key).contains(value)) {
						tableData.append("<td align=center>" + value + "</td>\n");
					} else {
						tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
					}
				} else {
					tableData.append("<td align=center>" + value + "</td>\n");
				}
			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	public StringBuilder getAudit5GMMSonANRChecks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("gnodeb-id");
			headerList2.add("cell-identity");
			headerList2.add("anr-switch");
			headerList2.add("underused-ncr-delete-switch");
			headerList2.add("low-performance-ncr-delete-switch");
			headerList2.add("ncr-rank-based-delete-switch");
			headerList2.add("ems-based-anr-switch");
			headerList2.add("initial-suppression-time");

			NodeList nodeList = document.getElementsByTagName("gnb-cu-cp-function-entries");
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			StringBuilder auditIssueAll = new StringBuilder();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("gutran-cu-cell-entries");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("cell-son");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("anr-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											objtableData.put("gnodeb-id", getXmlElementData(element, "gnodeb-id"));
											objtableData.put("cell-identity",
													getXmlElementData(elementchild, "cell-identity"));
											objtableData.put("anr-switch",
													getXmlElementData(elementchild1, "anr-switch"));
											objtableData.put("underused-ncr-delete-switch",
													getXmlElementData(elementchild2, "underused-ncr-delete-switch"));
											objtableData.put("low-performance-ncr-delete-switch", getXmlElementData(
													elementchild2, "low-performance-ncr-delete-switch"));
											objtableData.put("ncr-rank-based-delete-switch",
													getXmlElementData(elementchild2, "ncr-rank-based-delete-switch"));
											objtableData.put("ems-based-anr-switch",
													getXmlElementData(elementchild2, "ems-based-anr-switch"));
											objtableData.put("initial-suppression-time",
													getXmlElementData(elementchild2, "initial-suppression-time"));

											tabelData2.add(objtableData);
										}
									}
								}
							}

						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");

			String gnbID = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails) && listOfCiqDetails.get(0).getCiqMap().containsKey("GNODEBID")) {
				gnbID = listOfCiqDetails.get(0).getCiqMap().get("GNODEBID").getHeaderValue().replaceAll("^0+(?!$)", "");
			}
			Set<String> gnbIdSet = new HashSet<>();
			if (!gnbID.isEmpty()) {
				gnbIdSet.add(gnbID);
			}
			trimTabledataString1(tabelData2, gnbIdSet, "gnodeb-id");

			Set<Integer> cellIdset = getcellIds(enbId, dbcollectionFileName, "5GNRCIQAU", "eNBId");
			trimTabledata(tabelData2, cellIdset, "cell-identity");

			HashMap<String, List<String>> validationMap = new HashMap<>();
			validationMap.put("anr-switch", Arrays.asList("on"));
			validationMap.put("underused-ncr-delete-switch", Arrays.asList("off"));
			validationMap.put("low-performance-ncr-delete-switch", Arrays.asList("on"));
			validationMap.put("ncr-rank-based-delete-switch", Arrays.asList("on"));
			validationMap.put("ems-based-anr-switch", Arrays.asList("on"));
			validationMap.put("initial-suppression-time", Arrays.asList("10"));
			htmlContent.append(createHtmltableWithValidation(headerList2, command, tabelData2, validationMap));

			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	private void trimTabledataString1(List<LinkedHashMap<String, String>> tabelData1, Set<String> gnodebIdset,
			String key) {
		try {
			List<LinkedHashMap<String, String>> deleteData = new ArrayList<>();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.containsKey(key) && !ObjectUtils.isEmpty(gnodebIdset)) {
					if (!gnodebIdset.contains(tdData.get(key).replaceAll("^0+(?!$)", ""))) {
						deleteData.add(tdData);
					}
				}
			}

			for (LinkedHashMap<String, String> tdData : deleteData) {
				tabelData1.remove(tdData);
			}
		} catch (Exception e) {
			logger.error("AuditXmlRuleServiceUtil trimTabledata() " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	public StringBuilder getAuditHTML5GMMSFPInventory(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("unit-id");
			headerList1.add("hardware-name");
			headerList1.add("serial-number");
			headerList1.add("vendor-name");
			headerList1.add("wave-length");
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("optic-module-inventory");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild6, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			if(neVersion.toUpperCase().contains("22.A")) {     
				htmlContent.append(
						"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "IAU_22A_SFP_Inventory" + "</b></td></tr>\n");
			    }else{
			    	htmlContent.append(
							"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
			    }
			

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String hw = tdData.get("hardware-name");
				boolean hardwerePresent = true;
				List<Audit5GMMHardwareDetailsEntity> auditConstantsList1 = audit5GMMHardwareDetailsRepository
						.getAuditHardwareDetailsEntityList(hw, "IAU");
				
				if (ObjectUtils.isEmpty(auditConstantsList1)) {
					hardwerePresent = false;
				}

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("hardware-name")) {
						if (hardwerePresent) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					  } else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");

			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, null);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "IAU_SFP_Inventory" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getAuditHTML5GMMPROCESSORENTRIES(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			String entry1 = "processor-inventory-entries";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("hardware-name");
			headerList1.add("serial-number");
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			// gnb-cu-cp-function
			NodeList nodeList = document.getElementsByTagName("processor-inventory-entries");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
					headerList1.forEach(header -> {
						objtableData.put(header, getXmlElementData(element, header));
					});
					tabelData1.add(objtableData);
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			if(neVersion.toUpperCase().contains("22.A")) {     
				htmlContent.append(
						"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "IAU_22A_Processor_Entries" + "</b></td></tr>\n");
			    }else{
			    	htmlContent.append(
							"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");	
			    }
			

			htmlContent.append(createHtmltableAU20AParamCheck(headerList1, entry1, tabelData1));

			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, null);


		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "IAU_Processor_Entries" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getAudit5GMMIAUParamChecks(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity, NetworkConfigEntity networkConfigEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			String neVersion = "";
			if (neVersion.isEmpty()) {
				neVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();
			}

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			String entry1 = "drx-profile-du";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cell-identity");
			headerList1.add("drx-cycle");
			headerList1.add("drx-on-duration-timer-msec-normal");
			headerList1.add("drx-inactivity-timer-normal");
			headerList1.add("drx-unsynch-counter-th");
			
			String entry5 = "cell-cac-info";
			LinkedHashSet<String> headerList5 = new LinkedHashSet<>();
			headerList5.add("cell-identity");
			headerList5.add("nsa-call-threshold");
			String entry6 = "coloc-covered-cell-num";
			LinkedHashSet<String> headerList6 = new LinkedHashSet<>();
			headerList6.add("cell-identity");
			headerList6.add("coloc-covered-cell-num");
			String entry7 = "csl-tce-ems-server";
			LinkedHashSet<String> headerList7 = new LinkedHashSet<>();
			headerList7.add("csl-tce-ems-server-port");
			headerList7.add("csl-tce-ems-option");
			String entry8 = "csl-tce-server";
			LinkedHashSet<String> headerList8 = new LinkedHashSet<>();
			headerList8.add("csl-tce-server-ip-address");
			headerList8.add("csl-tce-server-port");
			headerList8.add("csl-tce-option");
			String entry9 = "drb-rlc-info-func";
			LinkedHashSet<String> headerList9 = new LinkedHashSet<>();
			headerList9.add("config-type");
			headerList9.add("qci");
			headerList9.add("gnb-timer-poll-retransmit");
			headerList9.add("ue-t-reassembly");
			headerList9.add("ue-timer-status-prohibit");
			String entry10 = "dl-mimo-configuration";
			LinkedHashSet<String> headerList10 = new LinkedHashSet<>();
			// headerList10.add("dl-prb-bundling-size-static");
			headerList10.add("pmi-cycling-switch");
			String entry11 = "ul-power-control-common-config";
			LinkedHashSet<String> headerList11 = new LinkedHashSet<>();
			headerList11.add("cell-identity");
			headerList11.add("p0-nominal-with-grant");
			headerList11.add("p0-nominal-pucch");
			String entry12 = "tssi-info";
			LinkedHashSet<String> headerList12 = new LinkedHashSet<>();
			headerList12.add("modem-id");
			headerList12.add("cell-num");
			headerList12.add("path");
			headerList12.add("lowTssi");
			headerList12.add("highTssi");
			headerList12.add("status");
			String entry13 = "ul-mimo-configuration";
			LinkedHashSet<String> headerList13 = new LinkedHashSet<>();
			headerList13.add("cell-identity");
			headerList13.add("ul-su-mimo-switch");
			headerList13.add("ul-mu-mimo-switch");
			headerList13.add("dynamic-srs-port-adaptation");
			String entry14 = "logical-channel-config";
			LinkedHashSet<String> headerList14 = new LinkedHashSet<>();
			headerList14.add("qci");
			headerList14.add("prioritised-bitrate");
			String entry15 = "nr-carrier-aggregation";
			LinkedHashSet<String> headerList15 = new LinkedHashSet<>();
			headerList15.add("cell-identity");
			headerList15.add("ca-available-type");
			headerList15.add("p-cell-only-flag");
			headerList15.add("max-dl-ca-cc-num");
			headerList15.add("max-ul-ca-cc-num");
			String entry16 = "pdsch-config";
			LinkedHashSet<String> headerList16 = new LinkedHashSet<>();
			headerList16.add("cell-identity");
			headerList16.add("dmrs-pdsch-fdm");
			String entry17 = "prach-config";
			LinkedHashSet<String> headerList17 = new LinkedHashSet<>();
			headerList17.add("cell-identity");
			headerList17.add("preamble-receiver-target-power");
			headerList17.add("rsrp-threshold");
			headerList17.add("preamble-trans-max");
			headerList17.add("prach-configuration-index");
			headerList17.add("ssb-per-rach-occasion-choice");
			headerList17.add("cb-preambles-per-ssb");
			headerList17.add("auto-configure-prach-rb-offset-on-off");
			String entry18 = "srs-resource-config";
			LinkedHashSet<String> headerList18 = new LinkedHashSet<>();
			headerList18.add("cell-identity");
			headerList18.add("b-srs-periodic");
			String entry19 = "ssb-configuration";
			LinkedHashSet<String> headerList19 = new LinkedHashSet<>();
			headerList19.add("cell-identity");
			headerList19.add("ssb-subcarrier-spacing");
			headerList19.add("ssb-periodicity");
			headerList19.add("ssb-freq-offset");
			headerList19.add("ssb-freq-config-mode");
			String entry22 = "active-alarm-entries";
			LinkedHashSet<String> headerList22 = new LinkedHashSet<>();
			headerList22.add("alarm-unit-type");
			headerList22.add("alarm-unit-id");
			headerList22.add("alarm-type");
			headerList22.add("location");
			headerList22.add("raised-time");
			headerList22.add("alarm-group");
			headerList22.add("probable-cause");
			headerList22.add("specific-problem");
			headerList22.add("severity");
			headerList22.add("alarm-code");
			headerList22.add("sequence-number");
			headerList22.add("time-info");
			String entry23 = "ul-ca-scheduler-config";
			LinkedHashSet<String> headerList23 = new LinkedHashSet<>();
			headerList23.add("cell-identity");
			headerList23.add("ul-ca-power-sharing-config");
			headerList23.add("ul-total-power-limit-offset");
			headerList23.add("ul-ca-power-sharing-config");
			String entry24 = "pusch-power-control-config";
			LinkedHashSet<String> headerList24 = new LinkedHashSet<>();
			headerList24.add("cell-identity");
			headerList24.add("max-target-sinr-64qam");
			headerList24.add("rerror-tpc-up-cmd");
			String entry25 = "beam-management";
			LinkedHashSet<String> headerList25 = new LinkedHashSet<>();
			headerList25.add("cell-identity");
			headerList25.add("beambook-type");
			headerList25.add("num-trs-restriction");
			headerList25.add("qcl-config-periodic-csi-rs");
			headerList25.add("num-trs-restriction-sdx50");
			String entry26 = "ul-su-mimo-config";
			LinkedHashSet<String> headerList26 = new LinkedHashSet<>();
			headerList26.add("cell-identity");
			headerList26.add("ul-su-mimo-phr-rb-threshold-rank2-in");
			String entry27 = "ul-scheduling-config";
			LinkedHashSet<String> headerList27 = new LinkedHashSet<>();
			headerList27.add("cell-identity");
			headerList27.add("retx-bsr-timer");
			String entry28 = "multicarrier-scenario-functionality-config";
			LinkedHashSet<String> headerList28 = new LinkedHashSet<>();
			headerList28.add("fr2-ue-overheating-mitigation-support");

			String entry29 = "dl-codebook-configuration";
			LinkedHashSet<String> headerList29 = new LinkedHashSet<>();
			headerList29.add("dl-prb-bundling-size-static");

			String entry30 = "managed-element";
			LinkedHashSet<String> headerList30 = new LinkedHashSet<>();
			headerList30.add("operational-mode");
			headerList30.add("user-label");
			
			String entry31 = "digital-unit-entries";
			LinkedHashSet<String> headerList31 = new LinkedHashSet<>();
			headerList31.add("unit-type");
			headerList31.add("unit-id");
			headerList31.add("port-id");
			headerList31.add("mtu");
			headerList31.add("tx-wavelength");
			headerList31.add("tx-power");
			headerList31.add("rx-power");
			
			String entry32 = "radio-unit-info";
			LinkedHashSet<String> headerList32 = new LinkedHashSet<>();
			headerList32.add("operational-mode");
			

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData5 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData6 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData7 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData8 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData9 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData10 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData11 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData12 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData13 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData14 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData15 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData16 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData17 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData18 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData19 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData22 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData23 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData24 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData25 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData26 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData27 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData28 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData29 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData30 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData31 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData32 = new ArrayList<>();
			StringBuilder auditIssueAll = new StringBuilder();
			// gnb-du-function
			NodeList nodeList = document.getElementsByTagName("gnb-du-function");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// gutran-du-cell
					NodeList childNodeList = element.getElementsByTagName("gutran-du-cell");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// gutran-du-cell-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("gutran-du-cell-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// drx-config-du-cell
									NodeList childNodeList2 = elementchild1.getElementsByTagName("drx-config-du-cell");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// drx-profile-du
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("drx-profile-du");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList1.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData1.add(objtableData);
												}
											}
										}
									}

									// cell-cac-info
									childNodeList2 = elementchild1.getElementsByTagName("cell-cac-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList5.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData5.add(objtableData);
										}
									}

									// nr-carrier-aggregation
									childNodeList2 = elementchild1.getElementsByTagName("nr-carrier-aggregation");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											for (int z = 0; z < elementchild2
													.getElementsByTagName("coloc-covered-cell-num").getLength(); z++) {
												LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
												objtableData.put("cell-identity",
														getXmlElementData(elementchild1, "cell-identity"));
												objtableData.put("coloc-covered-cell-num",
														elementchild2.getElementsByTagName("coloc-covered-cell-num")
																.item(z).getTextContent());
												tabelData6.add(objtableData);
											}

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList15.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData15.add(objtableData);
										}
									}

									// ul-power-control-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-power-control-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// ul-power-control-common-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("ul-power-control-common-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList11.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData11.add(objtableData);
												}
											}
										}
									}

									// ul-mimo-configuration
									childNodeList2 = elementchild1.getElementsByTagName("ul-mimo-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList13.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData13.add(objtableData);
										}
									}

									childNodeList2 = elementchild1.getElementsByTagName("ul-ca-scheduler-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList23.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData23.add(objtableData);
										}
									}

									childNodeList2 = elementchild1.getElementsByTagName("pusch-power-control-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList24.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData24.add(objtableData);
										}
									}

									childNodeList2 = elementchild1.getElementsByTagName("beam-management");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList25.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData25.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("ul-su-mimo-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList26.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData26.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("ul-scheduling-common-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList27.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData27.add(objtableData);
										}
									}
									// dl-physical-resource-config
									childNodeList2 = elementchild1.getElementsByTagName("dl-physical-resource-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// pdsch-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("pdsch-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList16.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData16.add(objtableData);
												}
											}
										}
									}

									// ul-physical-resource-config
									childNodeList2 = elementchild1.getElementsByTagName("ul-physical-resource-config");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											// pdsch-config
											NodeList childNodeList3 = elementchild2
													.getElementsByTagName("prach-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList17.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData17.add(objtableData);
												}
											}

											// srs-resource-config
											childNodeList3 = elementchild2.getElementsByTagName("srs-resource-config");
											for (int m = 0; m < childNodeList3.getLength(); m++) {
												Node ChildNode3 = childNodeList3.item(m);

												if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {
													Element elementchild3 = (Element) ChildNode3;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
													Iterator<String> itr = headerList18.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild1, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild3, header));
													}
													tabelData18.add(objtableData);
												}
											}
										}
									}

									// ssb-configuration
									childNodeList2 = elementchild1.getElementsByTagName("ssb-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList19.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData19.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("dl-codebook-configuration");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList29.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild2, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData29.add(objtableData);
										}
									}
								}
							}
						}
					}
					// rlc-functions
					childNodeList = element.getElementsByTagName("rlc-functions");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// endc-bc-information-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("drb-rlc-info-func");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList9.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData9.add(objtableData);
								}
							}
						}
					}

					// dl-mimo-configuration
					childNodeList = element.getElementsByTagName("dl-mimo-configuration");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList10.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData10.add(objtableData);
						}
					}

					childNodeList = element.getElementsByTagName("multicarrier-scenario-functionality-config");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							headerList28.forEach(header -> {
								objtableData.put(header, getXmlElementData(elementchild, header));
							});
							tabelData28.add(objtableData);
						}
					}

					// gutran-du-qci
					childNodeList = element.getElementsByTagName("gutran-du-qci");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// logical-channel-config
							NodeList childNodeList1 = elementchild.getElementsByTagName("logical-channel-config");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									Iterator<String> itr = headerList14.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild1, header));
									}
									tabelData14.add(objtableData);
								}
							}
						}
					}
				}
			}

			// common-management
			nodeList = document.getElementsByTagName("common-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// csl-configuration
					NodeList childNodeList = element.getElementsByTagName("csl-configuration");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// csl-tce-ems-server
							NodeList childNodeList1 = elementchild.getElementsByTagName("csl-tce-ems-server");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList7.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData7.add(objtableData);
								}
							}

							// csl-tce-server
							childNodeList1 = elementchild.getElementsByTagName("csl-tce-server");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList8.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData8.add(objtableData);
								}
							}
						}
					}
				}
			}

			// hardware-management
			nodeList = document.getElementsByTagName("hardware-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// digital-unit
					NodeList childNodeList = element.getElementsByTagName("digital-unit");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// tssi
							NodeList childNodeList1 = elementchild.getElementsByTagName("tssi");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// tssi-info
									NodeList childNodeList2 = elementchild1.getElementsByTagName("tssi-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											headerList12.forEach(header -> {
												objtableData.put(header, getXmlElementData(elementchild2, header));
											});
											tabelData12.add(objtableData);

										}
									}
								}
							}
							childNodeList1 = elementchild.getElementsByTagName("digital-unit-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// tssi-info
									NodeList childNodeList2 = elementchild1.getElementsByTagName("ethernet-port-entries");
									for (int l = 0; l < childNodeList2.getLength(); l++) {
										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList31.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));
											header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData31.add(objtableData);

										}
									}
								}
							}
						}
					}
					childNodeList = element.getElementsByTagName("radio-unit");
					for (int k = 0; k < childNodeList.getLength(); k++) {

						Node ChildNode1 = childNodeList.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild = (Element) ChildNode1;

							// tssi-info
							NodeList childNodeList1 = elementchild.getElementsByTagName("radio-unit-info");
							for (int l = 0; l < childNodeList1.getLength(); l++) {
								Node ChildNode2 = childNodeList1.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild1 = (Element) ChildNode2;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									Iterator<String> itr = headerList32.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild1, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild1, header));
									}
									tabelData32.add(objtableData);

								}
							}
						}
					}
				}
			}
			nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {
					Element element = (Element) inChildNode;
					LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
					Iterator<String> itr = headerList30.iterator();
					String header ;
					while (itr.hasNext()) {
						header = itr.next();
						if ((header.equalsIgnoreCase("operational-mode"))
								&& element.getElementsByTagName(header) != null
								&& element.getElementsByTagName(header).getLength() > 0) {
							for (int z = 0; z < element.getElementsByTagName(header).getLength(); z++) {
								if (element.getElementsByTagName(header).item(z).getParentNode().getNodeName()
										.equalsIgnoreCase("managed-element")) {
									objtableData.put(header,
											element.getElementsByTagName(header).item(z).getTextContent());
								}
							}
						} else {
							objtableData.put(header, getXmlElementData(element, header));
						}
					}
					tabelData30.add(objtableData);
				}
			}

			// fault-management
			nodeList = document.getElementsByTagName("fault-management");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// active-alarm
					NodeList childNodeList = element.getElementsByTagName("active-alarm");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// active-alarm-entries
							NodeList childNodeList1 = elementchild.getElementsByTagName("active-alarm-entries");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;
									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									headerList22.forEach(header -> {
										objtableData.put(header, getXmlElementData(elementchild1, header));
									});
									tabelData22.add(objtableData);
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			if(neVersion.contains("22.A")) {
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "IAU_22A_Param_Check" + "</b></td></tr>\n");
			}else {
				htmlContent.append(
						"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			}
			String msgstartfreq = "";
			String iaumounting = "";
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"5GNRCIQAU", "eNBId");
			if (!ObjectUtils.isEmpty(listOfCiqDetails)
					&& listOfCiqDetails.get(0).getCiqMap().containsKey("msg1_frequency_start")) {
				msgstartfreq = listOfCiqDetails.get(0).getCiqMap().get("msg1_frequency_start").getHeaderValue();
			}
			if (!ObjectUtils.isEmpty(listOfCiqDetails)
					&& listOfCiqDetails.get(0).getCiqMap().containsKey("IAU_Mount_Type")) {
				iaumounting = listOfCiqDetails.get(0).getCiqMap().get("IAU_Mount_Type").getHeaderValue();
			}

			HashMap<String, List<String>> validationMap = new HashMap<>();
			/*
			 * validationMap.put("operational-mode", Arrays.asList("normal-mode"));
			 * htmlContent.append(createHtmltableWithValidation(headerList30, entry30,
			 * tabelData30,validationMap));
			 */

			htmlContent.append(
					"<tr><td colspan=" + headerList30.size() + " bgcolor=#EEEEEE><b>" + entry30 + "</b></td></tr>\n");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList30) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData30) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					if ("operational-mode".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().equals("normal-mode")) {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						} else {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
						}
					} else if ("user-label".equalsIgnoreCase(resultTableData.getKey())) {
						if (resultTableData.getValue().toUpperCase().contains("GROW")) {
							tableData.append(
									"<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");

						} else {
							tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			
			validationMap = new HashMap<>();
			validationMap.put("operational-mode", Arrays.asList("normal-mode"));
			htmlContent.append(createHtmltableWithValidation(headerList32, entry32, tabelData32, validationMap));
			
			validationMap = new HashMap<>();
			validationMap.put("nsa-call-threshold", Arrays.asList("100.0", "100"));
			htmlContent.append(createHtmltableWithValidation(headerList5, entry5, tabelData5, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("ca-available-type", Arrays.asList("ca-on"));
			validationMap.put("p-cell-only-flag", Arrays.asList("0", "false"));
			validationMap.put("max-dl-ca-cc-num", Arrays.asList("4"));
			// validationMap.put("max-ul-ca-cc-num", Arrays.asList("2"));
			htmlContent.append(createHtmltableWithValidation(headerList15, entry15, tabelData15, validationMap));

			validationMap = new HashMap<>();
			htmlContent.append(createHtmltableWithValidation(headerList6, entry6, tabelData6, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("p0-nominal-with-grant", Arrays.asList("-76"));
			validationMap.put("p0-nominal-pucch", Arrays.asList("-80"));
			htmlContent.append(createHtmltableWithValidation(headerList11, entry11, tabelData11, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("ul-su-mimo-switch", Arrays.asList("on"));
			validationMap.put("ul-mu-mimo-switch", Arrays.asList("off"));
			validationMap.put("dynamic-srs-port-adaptation", Arrays.asList("off"));
			htmlContent.append(createHtmltableWithValidation(headerList13, entry13, tabelData13, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("dmrs-pdsch-fdm", Arrays.asList("1", "true"));
			htmlContent.append(createHtmltableWithValidation(headerList16, entry16, tabelData16, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("preamble-receiver-target-power", Arrays.asList("-69"));
			validationMap.put("prach-configuration-index", Arrays.asList("189"));
			validationMap.put("ssb-per-rach-occasion-choice", Arrays.asList("ssb-per-ro-one-choice"));
			validationMap.put("cb-preambles-per-ssb", Arrays.asList("56"));
			validationMap.put("rsrp-threshold", Arrays.asList("48"));
			validationMap.put("preamble-trans-max", Arrays.asList("preamble-trans-max-n8"));
			validationMap.put("auto-configure-prach-rb-offset-on-off",
					Arrays.asList("auto-prach-rb-offset-on-multiple-regions"));
			htmlContent.append(createHtmltableWithValidation(headerList17, entry17, tabelData17, validationMap));

			validationMap = new HashMap<>();
			// validationMap.put("b-srs-periodic", Arrays.asList("1"));
			htmlContent.append(createHtmltableWithValidation(headerList18, entry18, tabelData18, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("ssb-subcarrier-spacing", Arrays.asList("subcarrier-spacing-120khz"));
			validationMap.put("ssb-periodicity", Arrays.asList("ssb-periodicity-20ms"));
			validationMap.put("ssb-freq-config-mode", Arrays.asList("non-gscn-center"));
			validationMap.put("ssb-freq-offset", Arrays.asList("0"));
			htmlContent.append(createHtmltableWithValidation(headerList19, entry19, tabelData19, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("drx-cycle", Arrays.asList("drx-long-cycle-normal-ms160"));
			validationMap.put("drx-on-duration-timer-msec-normal", Arrays.asList("drx-on-duration-ms10"));
			validationMap.put("drx-inactivity-timer-normal", Arrays.asList("drx-inactivity-ms100"));			
			validationMap.put("drx-unsynch-counter-th", Arrays.asList("5"));
			
			htmlContent.append(createHtmltableWithValidation(headerList1, entry1, tabelData1, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("qci", Arrays.asList("8", "9", "7", "132"));
			validationMap.put("prioritised-bitrate", Arrays.asList("prioritised-bit-rate-infinity"));
			htmlContent.append(createHtmltableWithValidation(headerList14, entry14, tabelData14, validationMap));

			validationMap = new HashMap<>();
			// validationMap.put("dl-prb-bundling-size-static",
			// Arrays.asList("prb-bundling-size-n2"));
			validationMap.put("pmi-cycling-switch", Arrays.asList("on"));
			htmlContent.append(createHtmltableWithValidation(headerList10, entry10, tabelData10, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("config-type", Arrays.asList("A6G"));
			validationMap.put("qci", Arrays.asList("132", "8", "9", "7"));
			validationMap.put("gnb-timer-poll-retransmit", Arrays.asList("t-poll-retransmit-ms30"));
			validationMap.put("ue-t-reassembly", Arrays.asList("ms15"));
			validationMap.put("ue-timer-status-prohibit", Arrays.asList("ms15"));
			htmlContent.append(createHtmltableWithValidation(headerList9, entry9, tabelData9, validationMap));

			validationMap = new HashMap<>();
			htmlContent.append(createHtmltablewithInverseValidation(headerList12, entry12, tabelData12, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("csl-tce-ems-option", Arrays.asList("abnormal-call-only"));
			htmlContent.append(createHtmltableWithValidation(headerList7, entry7, tabelData7, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("csl-tce-server-port", Arrays.asList("50021"));
			validationMap.put("csl-tce-option", Arrays.asList("normal-and-abnormal-and-intra-ho-call"));
			htmlContent.append(createHtmltableWithValidation(headerList8, entry8, tabelData8, validationMap));

			/*
			 * validationMap = new HashMap<>(); validationMap.put("ssb-freq-config-mode",
			 * Arrays.asList("center")); validationMap.put("ssb-freq-config-mode",
			 * Arrays.asList("gscn-based"));
			 * htmlContent.append(createHtmltableWithValidation(headerList19, entry19,
			 * tabelData19,validationMap));
			 */
			htmlContent.append(
					"<tr><td colspan=" + headerList22.size() + " bgcolor=#EEEEEE><b>" + entry22 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList22) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData22) {
				tableData.append("<tr>\n");
				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					tableData.append("<td align=center bgcolor ='#fa8c8c'>" + resultTableData.getValue() + "</td>\n");
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			/*validationMap = new HashMap<>();
			validationMap.put("alarm-type", Arrays.asList("service-off"));
			htmlContent.append(createHtmltablewithInverseValidation(headerList22, entry22, tabelData22, validationMap));*/

			validationMap = new HashMap<>();
			validationMap.put("ul-total-power-limit-offset", Arrays.asList("0"));
			htmlContent.append(createHtmltableWithValidation(headerList23, entry23, tabelData23, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("max-target-sinr-64qam", Arrays.asList("28"));
			validationMap.put("rerror-tpc-up-cmd", Arrays.asList("0"));
			htmlContent.append(createHtmltableWithValidation(headerList24, entry24, tabelData24, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("num-trs-restriction", Arrays.asList("disable"));
			validationMap.put("num-trs-restriction-sdx50", Arrays.asList("disable"));
			validationMap.put("qcl-config-periodic-csi-rs", Arrays.asList("off"));
			validationMap.put("beambook-type", Arrays.asList(iaumounting));
			htmlContent.append(createHtmltableWithValidation(headerList25, entry25, tabelData25, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("ul-su-mimo-phr-rb-threshold-rank2-in", Arrays.asList("1"));
			htmlContent.append(createHtmltableWithValidation(headerList26, entry26, tabelData26, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("retx-bsr-timer", Arrays.asList("retx-bsr-timer-sf40"));
			htmlContent.append(createHtmltableWithValidation(headerList27, entry27, tabelData27, validationMap));
			validationMap = new HashMap<>();
			validationMap.put("fr2-ue-overheating-mitigation-support", Arrays.asList("on"));
			htmlContent.append(createHtmltableWithValidation(headerList28, entry28, tabelData28, validationMap));

			validationMap = new HashMap<>();
			validationMap.put("dl-prb-bundling-size-static", Arrays.asList("prb-bundling-size-n2"));
			htmlContent.append(createHtmltableWithValidation(headerList29, entry29, tabelData29, validationMap));
			
			htmlContent.append(
					"<tr><td colspan=" + headerList31.size() + " bgcolor=#EEEEEE><b>" + entry31 + "</b></td></tr>\n");
			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList31) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData31) {
				HashMap<String, String> map;
				String portId = tdData.get("port-id");
				String unitId = tdData.get("unit-id");
				String hw = "";
				String txPowerUl = "0";
				String txPowerLl = "0";
				String rxPowerUl = "0";
				String rxPowerLl = "0";
				String txPowerMax = "0";
				String txPowerMin = "0";

				map = gethadwareAndVender(fullOutputLog, XmlCommandsConstants.IAU_21D_SFP_INVENTORY, portId, unitId);
				if (!map.isEmpty()) {
					hw = map.get("hardware-name").trim();
				}

				List<Audit5GMMHardwareDetailsEntity> auditConstantsList1 = audit5GMMHardwareDetailsRepository
						.getAuditHardwareDetailsEntityList(hw, "IAU");
				
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerUl = auditConstantsList1.get(0).getFailRxPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerLl = auditConstantsList1.get(0).getFailRxPowerLL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					rxPowerUl = auditConstantsList1.get(0).getWarningRxPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					rxPowerLl = auditConstantsList1.get(0).getWarningRxPowerLL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerMax = auditConstantsList1.get(0).gettXPowerUL();
				}
				if (!ObjectUtils.isEmpty(auditConstantsList1)) {
					txPowerMin = auditConstantsList1.get(0).gettXPowerLL();
				}

				Double txPowerUlD = 0.0;
				Double txPowerLlD = 0.0;
				Double rxPowerUlD = 0.0;
				Double rxPowerLlD = 0.0;
				Double txPowerMaxLimit = 0.0;
				Double txPowerMinLimit = 0.0;
				
				if (NumberUtils.isNumber(txPowerUl)) {
					txPowerUlD = Double.parseDouble(txPowerUl);
				}
				if (NumberUtils.isNumber(txPowerLl)) {
					txPowerLlD = Double.parseDouble(txPowerLl);
				}
				if (NumberUtils.isNumber(rxPowerUl)) {
					rxPowerUlD = Double.parseDouble(rxPowerUl);
				}
				if (NumberUtils.isNumber(rxPowerLl)) {
					rxPowerLlD = Double.parseDouble(rxPowerLl);
				}
				if (NumberUtils.isNumber(txPowerMax)) {
					txPowerMaxLimit = Double.parseDouble(txPowerMax);
				}
				if (NumberUtils.isNumber(txPowerMin)) {
					txPowerMinLimit = Double.parseDouble(txPowerMin);
				}

				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("rx-power")) {

						if (NumberUtils.isNumber(value)) {
							Double rxPower = Double.parseDouble(value);

							if (rxPower >= txPowerLlD && rxPower <= txPowerUlD) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else if (rxPower >= rxPowerLlD && rxPower <= rxPowerUlD) {
								tableData.append("<td align=center bgcolor ='#FFA500'>" + value + "</td>\n");

							}  else {
								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							}
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("tx-power")) {

						if (NumberUtils.isNumber(value)) {
							Double txPower = Double.parseDouble(value);

							if (txPower >= txPowerMinLimit && txPower <= txPowerMaxLimit) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else {

								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							}
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center >" + value + "</td>\n");

					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			

			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData5, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData6, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData7, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData8, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData9, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData10, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData11, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData12, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData13, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData14, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData15, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData16, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData17, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData18, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData19, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData22, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData23, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData24, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData25, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData26, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData27, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData28, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData29, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData30, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData31, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData32, auditIssueAll);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "IAU_Param_Check" + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getAuditHTML5GMMAuDscp(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			// For a1-report-config entries
			String entry1 = "bearer-traffic";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("qci");
			headerList1.add("dscp");

			String entry4 = "dscp-to-cos-table";
			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			headerList4.add("dscp");
			headerList4.add("cos");

			NodeList nodeList = document.getElementsByTagName("managed-element");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData4 = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("qos");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("diffserv");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("bearer-traffic");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											objtableData.put("qci", getXmlElementData(elementchild2, "qci"));
											objtableData.put("dscp", getXmlElementData(elementchild2, "dscp"));

											tabelData1.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("dscp-to-cos-table");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											objtableData.put("dscp", getXmlElementData(elementchild2, "dscp"));
											objtableData.put("cos", getXmlElementData(elementchild2, "cos"));

											tabelData4.add(objtableData);
										}
									}
								}
							}

						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + entry1 + "</b></td></tr>\n");

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("qci")) {
						if (value.equals("128") || value.equals("129") || value.equals("130") || value.equals("131")
								|| value.equals("132") || value.equals("133") || value.equals("7") || value.equals("8")
								|| value.equals("9")) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("dscp")) {
						if (value.equals("0") || value.equals("8") || value.equals("16") || value.equals("46")) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			HashMap<String, List<String>> validationMap = new HashMap<>();
			validationMap.put("dscp", Arrays.asList("10", "16", "24", "32", "46", "8","0"));
			validationMap.put("cos", Arrays.asList("1", "2", "3", "4", "5","0"));
			htmlContent.append(createHtmltableWithValidation(headerList4, entry4, tabelData4, validationMap));

			htmlContent.append("</table>\n");
			
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, null);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getAuditHTML5GMMIAUDscp(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			// For a1-report-config entries
			String entry1 = "bearer-traffic";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("qci");
			headerList1.add("dscp");

			String entry4 = "dscp-to-cos-table";
			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			headerList4.add("dscp");
			headerList4.add("cos");

			NodeList nodeList = document.getElementsByTagName("managed-element");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData4 = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("qos");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("diffserv");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("bearer-traffic");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											objtableData.put("qci", getXmlElementData(elementchild2, "qci"));
											objtableData.put("dscp", getXmlElementData(elementchild2, "dscp"));

											tabelData1.add(objtableData);
										}
									}
									childNodeList2 = elementchild1.getElementsByTagName("dscp-to-cos-table");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											objtableData.put("dscp", getXmlElementData(elementchild2, "dscp"));
											objtableData.put("cos", getXmlElementData(elementchild2, "cos"));

											tabelData4.add(objtableData);
										}
									}
								}
							}

						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + entry1 + "</b></td></tr>\n");

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("qci")) {
						if (((value.equals("128") || value.equals("129") || value.equals("130") || value.equals("131"))
								&& (tdData.get("dscp").equals("46")))
								|| ((value.equals("7") || value.equals("133")) && (tdData.get("dscp").equals("16")))
								|| ((value.equals("132") || value.equals("8")) && (tdData.get("dscp").equals("0")))
								|| (value.equals("9") && (tdData.get("dscp").equals("8")))) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("dscp")) {
						if (((tdData.get("qci").equals("128") || tdData.get("qci").equals("129")
								|| tdData.get("qci").equals("130") || tdData.get("qci").equals("131"))
								&& (value.equals("46")))
								|| ((tdData.get("qci").equals("7") || tdData.get("qci").equals("133"))
										&& (value.equals("16")))
								|| ((tdData.get("qci").equals("132") || tdData.get("qci").equals("8"))
										&& (value.equals("0")))
								|| (tdData.get("qci").equals("9") && (value.equals("8")))) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			/*HashMap<String, List<String>> validationMap = new HashMap<>();
			validationMap.put("dscp", Arrays.asList("10", "16", "24", "32", "46", "8","0"));
			validationMap.put("cos", Arrays.asList("1", "2", "3", "4", "5","0"));
			htmlContent.append(createHtmltableWithValidation(headerList4, entry4, tabelData4, validationMap));*/
			htmlContent.append(
					"<tr><td colspan=" + headerList4.size() + " bgcolor=#EEEEEE><b>" + entry4 + "</b></td></tr>\n");

			 tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList4) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			 tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData4) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("dscp")) {
						if (  (value.equals("0") && (tdData.get("cos").equals("0")))
								|| ((value.equals("10") || value.equals("8")) && (tdData.get("cos").equals("1"))) 
								|| (value.equals("16") && (tdData.get("cos").equals("2")))
								|| (value.equals("24") && (tdData.get("cos").equals("3")))  
								|| (value.equals("32") && (tdData.get("cos").equals("4")))
								|| (value.equals("46") && (tdData.get("cos").equals("5"))) ) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("cos")) {
						if (  (tdData.get("dscp").equals("0") && (value.equals("0")))
								|| ((tdData.get("dscp").equals("10") || tdData.get("dscp").equals("8")) && (value.equals("1"))) 
								|| (tdData.get("dscp").equals("16") && (value.equals("2")))
								|| (tdData.get("dscp").equals("24") && (value.equals("3")))  
								|| (tdData.get("dscp").equals("32") && (value.equals("4")))
								|| (tdData.get("dscp").equals("46") && (value.equals("5"))) ) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, null);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData4, null);


		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getCBandMMUReportingAudit(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();

		try {
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			date = date.replaceAll("-", "");
			String outputLog1 = StringUtils.substringAfter(fullOutputLog, command);
			outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);
			String after1 = "fh0_" + date + ".csv";
			String after2 = "fh1_" + date + ".csv";
			String after3 = "fh2_" + date + ".csv";
			String outputLogTable1 = StringUtils.substringAfter(outputLog1, after1);
			String outputLogTable2 = StringUtils.substringAfter(outputLog1, after2);
			String outputLogTable3 = StringUtils.substringAfter(outputLog1, after3);
			outputLogTable1 = StringUtils.substringBefore(outputLogTable1, after2);
			outputLogTable2 = StringUtils.substringBefore(outputLogTable2, after3);
			//outputLogTable3 = StringUtils.substringBefore(outputLogTable3, "[");
			String[] op1 = outputLogTable1.trim().split(",");
			String[] op2 = outputLogTable2.trim().split(",");
			String[] op3 = outputLogTable3.trim().split(",");
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("Test-Case");
			headerList1.add("Result");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData3 = new ArrayList<>();
			List<String> al = new ArrayList<>();
			al.add("VDU_ID");
			al.add("MMU");
			al.add("RADIO_TYPE");
			al.add("MMU_AUDIT_RESULT");
			al.add("COMMENT");
			al.add("MMU 48VVol Voltage Range Check");
			al.add("MMU FW Version Check");
			al.add("MMU L0 Link Test");
			al.add("MMU L0/L1 Swap Check");
			al.add("MMU L1 Inter MMU Cable Swap Check");
			al.add("MMU L1 Link Test");
			al.add("MMU SFP0 RX Optical Signal Check");
			al.add("MMU SFP0 TX Optical Signal Check");
			al.add("MMU SFP0 TX WaveLength Check");
			al.add("MMU SFP0 Vendor Name Check");
			al.add("MMU SFP1 RX Optical Signal Check");
			al.add("MMU SFP1 TX Optical Signal Check");
			al.add("MMU SFP1 TX WaveLength Check");
			al.add("MMU SFP1 Vendor Name Check");
			al.add("MMU Serial Number Fetch");
			al.add("VDU ens1f3 SFP Alarm Check");
			al.add("VDU ens1f3 SFP RX Power Check");
			al.add("VDU ens1f3 SFP TX Power Check");
			al.add("VDU ens1f3 SFP Vender Rev Check");
			al.add("VDU ens1f3 SFP Vendor Name Check");
			al.add("VDU ens1f3 SFP Vendor PN Number Fetch");
			al.add("VDU ens1f3 SFP Wavelength Check");
			al.add("VDU ens2f3 SFP Alarm Check");
			al.add("VDU ens2f3 SFP RX Power Check");
			al.add("VDU ens2f3 SFP TX Power Check");
			al.add("VDU ens2f3 SFP Vender Rev Check");
			al.add("VDU ens2f3 SFP Vendor Name Check");
			al.add("VDU ens2f3 SFP Vendor PN Number Fetch");
			al.add("VDU ens2f3 SFP Wavelength Check");
			ArrayList<String> Al = new ArrayList<>();
			for (int j = 0; j < op1.length; j++) {
				Al.add(op1[j]);
			}
			if (Al.size() < al.size()) {
				while (Al.size() != al.size()) {
					Al.add("-");
				}
			}
			ArrayList<String> Am = new ArrayList<>();
			for (int j = 0; j < op2.length; j++) {
				Am.add(op2[j]);
			}
			if (Am.size() < al.size()) {
				while (Am.size() != al.size()) {
					Am.add("-");
				}
			}
			ArrayList<String> An = new ArrayList<>();
			for (int j = 0; j < op3.length; j++) {
				An.add(op3[j]);
			}
			if (An.size() < al.size()) {
				while (An.size() != al.size()) {
					An.add("-");
				}
			}
			for (int m = 0; m < al.size(); m++) {
				LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
				Iterator<String> itr = headerList1.iterator();
				String header = itr.next();
				objtableData.put(header, al.get(m));
				header = itr.next();
				objtableData.put(header, Al.get(m));

				tabelData1.add(objtableData);
			}
			for (int m = 0; m < al.size(); m++) {
				LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
				Iterator<String> itr = headerList1.iterator();
				String header = itr.next();
				objtableData.put(header, al.get(m));
				header = itr.next();
				objtableData.put(header, Am.get(m));

				tabelData2.add(objtableData);
			}
			for (int m = 0; m < al.size(); m++) {
				LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
				Iterator<String> itr = headerList1.iterator();
				String header = itr.next();
				objtableData.put(header, al.get(m));
				header = itr.next();
				objtableData.put(header, An.get(m));

				tabelData3.add(objtableData);
			}

			System.out.println(op1.length);
			System.out.println(op2[0]);
			ArrayList<String> ping = new ArrayList<>();
			if(fullOutputLog.contains(XmlCommandsConstants.CBAND_VDU_AUDIT)) {
	         	ping = getMMUpingStatus(fullOutputLog, XmlCommandsConstants.CBAND_VDU_AUDIT);
			}
			if (op1.length == 1 && op2.length == 1 && op3.length == 1) {
				if (!op1[0].isEmpty() && !op2[0].isEmpty() && !op3[0].isEmpty()
						&& op1[0].contains("No such file or directory") && op2[0].contains("No such file or directory")
						&& op3[0].contains("No such file or directory")) {
					htmlContent.setLength(0);
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "MMU AUDIT Report" + "</b></td></tr>\n");
					htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>No such file or directory</td></tr>\n");
					htmlContent.append("</table>");
					if (!ping.contains("ping1") && !ping.contains("ping2") && !ping.contains("ping3")) {
						audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(203, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), "MMU AUDIT Report" + " : " + "NO DATA");
					}
					return htmlContent;
				}
			}
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + "MMU AUDIT Report"
					+ "</b></td></tr>\n");

			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssue2 = new StringBuilder();
			StringBuilder auditIssue3 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			
			List<CIQDetailsModel> listOfCiqDetailsday2 = getCiqDetailsForRuleValidationsheet(enbId,
					dbcollectionFileName, "Day2", "eNBId");

			Set<String> oruList = new HashSet<>();
			if (!ObjectUtils.isEmpty(listOfCiqDetailsday2)) {
				for (CIQDetailsModel ciqData : listOfCiqDetailsday2) {
					if (ciqData.getCiqMap().containsKey("oruId")) {
						oruList.add(ciqData.getCiqMap().get("oruId").getHeaderValue().trim());
					}
				}
			}

			StringBuilder tableData;
			String tableHeader = "";
			if (op1.length > 5) {
				htmlContent.append("<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>"
						+ "MMU - 0 Test Report" + "</b></td></tr>\n");
				tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList1) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}

				tableHeader = tableHeader + "</tr>\n";
				tableData = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tabelData1) {
					tableData.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
						String key = resultTableData.getKey();
						String value = resultTableData.getValue();
						if(key.equals("Result") && tdData.get("Test-Case").equals("VDU ens2f3 SFP Wavelength Check")) {
							
							value= StringUtils.substringBefore(value, "[");
						}

						if (value.equals("FAIL")) {
							auditIssue1.append(tdData.get("Test-Case") + " Result : " + tdData.get("Result") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}

					}
					tableData.append("</tr>\n");

				}
				htmlContent.append(tableHeader);
				htmlContent.append(tableData);
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditIssueAll.append(auditIssue2);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
			} else if (oruList.contains("0")) {
				htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "MMU - 0 Test Report" + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
				if(!ping.contains("ping1")) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(204, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "MMU - 0 Test Report" + " : " + "NO DATA");
				}
			}
			if (op2.length > 5) {
				htmlContent.append("<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>"
						+ "MMU - 1 Test Report" + "</b></td></tr>\n");
				tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList1) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}

				tableHeader = tableHeader + "</tr>\n";
				tableData = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tabelData2) {
					tableData.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

						String key = resultTableData.getKey();
						String value = resultTableData.getValue();
						if(key.equals("Result") && tdData.get("Test-Case").equals("VDU ens2f3 SFP Wavelength Check")) {
							
							value= StringUtils.substringBefore(value, "[");
						}

						if (value.equals("FAIL")) {
							auditIssue2.append(tdData.get("Test-Case") + " Result : " + tdData.get("Result") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}

					}
					tableData.append("</tr>\n");

				}
				htmlContent.append(tableHeader);
				htmlContent.append(tableData);
			} else if (oruList.contains("1")) {
				htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "MMU - 1 Test Report" + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
				if(!ping.contains("ping2")) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(205, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "MMU - 1 Test Report" + " : " + "NO DATA");
				}
			}
			if (op3.length > 5) {
				htmlContent.append("<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>"
						+ "MMU - 2 Test Report" + "</b></td></tr>\n");
				tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList1) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}

				tableHeader = tableHeader + "</tr>\n";
				tableData = new StringBuilder();
				for (LinkedHashMap<String, String> tdData : tabelData3) {
					tableData.append("<tr>\n");
					for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

						String key = resultTableData.getKey();
						String value = resultTableData.getValue();
						if(key.equals("Result") && tdData.get("Test-Case").equals("VDU ens2f3 SFP Wavelength Check")) {							
							value= StringUtils.substringBefore(value, "[");
						}

						if (value.equals("FAIL")) {
							auditIssue3.append(tdData.get("Test-Case") + " Result : " + tdData.get("Result") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}

					}
					tableData.append("</tr>\n");

				}
				htmlContent.append(tableHeader);
				htmlContent.append(tableData);
			} else if (oruList.contains("2")) {
				htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + "MMU - 2 Test Report" + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
				if(!ping.contains("ping3")) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(206, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "MMU - 2 Test Report" + " : " + "NO DATA");
				}
			}
			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditIssueAll.append(auditIssue2);
			auditIssueAll.append(auditIssue3);
			
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData3, auditIssueAll);
			if (auditIssue1.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(93, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
			}
			if (auditIssue2.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(94, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue2.toString());
			}
			if (auditIssue3.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(95, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue3.toString());
			}

		} catch (Exception e) {

			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	private ArrayList<String> getMMUpingStatus(String fullOutputLog, String command) {
		ArrayList<String> map = new ArrayList<>();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("o-ran-ru-id");
			headerList1.add("mplane-ipv6");

			LinkedHashSet<String> headerList2= new LinkedHashSet<>();
			headerList2.add("alarm-unit-type");
			headerList2.add("alarm-type");
			headerList2.add("probable-cause");
			headerList2.add("specific-problem");
			headerList2.add("severity");
			headerList2.add("location");

			List<LinkedHashMap<String, String>> tabelData15 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData13 = new ArrayList<>();
			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList childNodeList = element.getElementsByTagName("hardware-management");

					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;
							NodeList childNodeList1 = elementchild.getElementsByTagName("o-ran-radio-unit-info");

							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									NodeList childNodeList2 = elementchild1.getElementsByTagName("mplane-info");
									for (int l = 0; l < childNodeList2.getLength(); l++) {

										Node ChildNode2 = childNodeList2.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;
											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											objtableData.put("o-ran-ru-id",
													getXmlElementData(elementchild1, "o-ran-ru-id"));
											objtableData.put("mplane-ipv6",
													getXmlElementData(elementchild2, "mplane-ipv6"));

											tabelData15.add(objtableData);
										}
									}

								}
							}

						}
					}
					NodeList nodelist1 = element.getElementsByTagName("active-alarm-entries");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList2.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild6, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData13.add(objtableData);
						}
					}
				}
			}
			boolean a1 = false;
			boolean a2 = false;
			boolean a3 = false;
			boolean a4 = false;
			boolean a5 = false;
			boolean a6 = false;
			Map<String, String> ipList2 = new HashMap<>();
			List<String> ipList = new ArrayList<>();
			for (LinkedHashMap<String, String> tdData : tabelData15) {
				if (compareIPAddr(tdData.get("mplane-ipv6").trim(), tdData.get("mplane-ipv6").trim())) {
					ipList2.put(tdData.get("o-ran-ru-id"), tdData.get("mplane-ipv6"));
					ipList.add(tdData.get("mplane-ipv6"));
				}
			}

			for (LinkedHashMap<String, String> tdData : tabelData13) {

				if (tdData.get("alarm-type").equals("port-down")) {
					if (tdData.get("location").contains("FRONTHAUL_PORT[0]") && ipList2.containsKey("0")) {
						a4 = true;
					} else if (tdData.get("location").contains("FRONTHAUL_PORT[1]") && ipList2.containsKey("1")) {
						a5 = true;
					} else if (tdData.get("location").contains("FRONTHAUL_PORT[2]") && ipList2.containsKey("2")) {
						a6 = true;
					}
				}

			}
			a1 = true;
			a2 = true;
			a3 = true;

			for (LinkedHashMap<String, String> tdData : tabelData15) {
				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("mplane-ipv6")) {
						if (ipList.contains(value)) {
							if (tdData.get("o-ran-ru-id").trim().equals("0")) {
								a1 = false;
							} else if (tdData.get("o-ran-ru-id").trim().equals("1")) {
								a2 = false;
							} else if (tdData.get("o-ran-ru-id").trim().equals("2")) {
								a3 = false;
							}
						} 
					}
				}

			}
			if(tabelData15.isEmpty()) {
				a1 = true;
				a2 = true;
				a3 = true;
			}
			if (a1 == true || a4 == true) {
				map.add("ping1");
			}
			if (a2 == true || a5 == true) {
				map.add("ping2");
			}
			if (a3 == true || a6 == true) {
				map.add("ping3");
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return map;
	}
	private boolean compareIPAddr(String ip1, String ip2) {
		boolean result = false;
		try {
			InetAddress ipAddr1 = InetAddress.getByName(ip1);
			InetAddress ipAddr2 = InetAddress.getByName(ip2);
			if(ipAddr1.equals(ipAddr2)) {
				result = true;
			}
		} catch(Exception e) {
			result = false;
		}
		return result;
	}
	
	public StringBuilder getFSUThoughtputAuditHtmlContent(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cell-number");
			headerList1.add("cell-user-label");
			headerList1.add("rlc-dl-throughput");
			headerList1.add("rlc-ul-throughput");
			headerList1.add("mac-dl-throughput");
			headerList1.add("mac-ul-throughput");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("cell-throughput-data");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild6, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			HashMap<String, List<String>> validationMap = new HashMap<>();
			htmlContent.append(createHtmltableDSS(headerList1, command, tabelData1, validationMap));
			htmlContent.append("</table>\n");

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	StringBuilder createHtmltableDSS(LinkedHashSet<String> headerList, String command,
			List<LinkedHashMap<String, String>> tabelData, HashMap<String, List<String>> validationMap) {
		StringBuilder htmlContent = new StringBuilder();

		htmlContent
				.append("<tr><td colspan=" + headerList.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");

		String tableHeader = "";
		tableHeader = tableHeader + "<tr>\n";
		for (String headerName : headerList) {
			tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
		}

		tableHeader = tableHeader + "</tr>\n";
		StringBuilder tableData = new StringBuilder();
		for (LinkedHashMap<String, String> tdData : tabelData) {
			tableData.append("<tr>\n");

			for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
				String key = resultTableData.getKey();
				String value = resultTableData.getValue();
				if (validationMap.containsKey(key)) {
					if (validationMap.get(key).contains(value)) {
						tableData.append("<td align=center>" + value + "</td>\n");
					} else {
						tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
					}
				} else {
					tableData.append("<td align=center>" + value + "</td>\n");
				}
			}
			tableData.append("</tr>\n");

		}
		htmlContent.append(tableHeader);
		htmlContent.append(tableData);

		return htmlContent;
	}

	public StringBuilder getCBandMMUSerialNumberAudit(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("o-ran-ru-id");
			headerList1.add("name");
			headerList1.add("class");
			headerList1.add("serial-num");
			headerList1.add("hardware-rev");
			headerList1.add("mfg-name");
			headerList1.add("mfg-date");
			headerList1.add("product-code");
			headerList1.add("dying-gasp-support");
			headerList1.add("dynamic-power-amplifier-control-enabled");

			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("P_MMU_SERIAL_NMBR_ALPHA_BAR");
			headerList2.add("P_MMU_SERIAL_NMBR_BETA_BAR");
			headerList2.add("P_MMU_SERIAL_NMBR_GAMMA_BAR");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();

			// gnb-cu-cp-function
			NodeList nodeList = document.getElementsByTagName("o-ran-radio-unit-info");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					// digital-unit
					NodeList childNodeList = element.getElementsByTagName("o-ran-hardware");
					for (int j = 0; j < childNodeList.getLength(); j++) {

						Node ChildNode = childNodeList.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList1 = elementchild.getElementsByTagName("component");
							for (int k = 0; k < childNodeList1.getLength(); k++) {

								Node ChildNode1 = childNodeList1.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
									Iterator<String> itr = headerList1.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(element, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild1, header));
									}
									tabelData1.add(objtableData);
								}
							}
						}
					}

				}
			}
			boolean ovOpPresent = true;
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssue4 = new StringBuilder();
			StringBuilder auditIssue5 = new StringBuilder();
			StringBuilder auditIssue6 = new StringBuilder();
			StringBuilder auditIssue7 = new StringBuilder();
			StringBuilder auditIssue8 = new StringBuilder();
			StringBuilder auditIssue9 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			
			HashMap<String, String> ovMap = new HashMap<>();
			String outputLog1 = StringUtils.substringAfter(fullOutputLog, XmlCommandsConstants.CBAND_VDU_OVOUTPUT);
			outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);

			List<HashMap<String, Object>> trakerList = null;
			System.out.println(outputLog1);

			try {
				trakerList = new ObjectMapper().readValue(outputLog1,
						new TypeReference<List<HashMap<String, Object>>>() {
						});
			} catch (Exception e) {
				ovOpPresent = false;

				e.printStackTrace();
			}
			System.out.println("trakerList.size()"+trakerList.size());
			if (ovOpPresent) {
				String alfa = "";
				String beta = "";
				String gamma = "";
				LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
				for (HashMap<String, Object> entryData : trakerList) {
					if(trakerList.size()!=1) {
					if (entryData.containsKey("P_5G_CBAND_SCOPE")
							&& StringUtils.isNotEmpty(entryData.get("P_5G_CBAND_SCOPE").toString())
							&& "1".equalsIgnoreCase(entryData.get("P_5G_CBAND_SCOPE").toString())) {
						if (entryData.containsKey("P_MMU_SERIAL_NMBR_ALPHA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR").toString())) {
							alfa = entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR").toString();
						}
						if (entryData.containsKey("P_MMU_SERIAL_NMBR_BETA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR").toString())) {
							beta = entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR").toString();
						}
						if (entryData.containsKey("P_MMU_SERIAL_NMBR_GAMMA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR").toString())) {
							gamma = entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR").toString();
						}

					}
					}else {

						if (entryData.containsKey("P_MMU_SERIAL_NMBR_ALPHA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR").toString())) {
							alfa = entryData.get("P_MMU_SERIAL_NMBR_ALPHA_BAR").toString();
						}
						if (entryData.containsKey("P_MMU_SERIAL_NMBR_BETA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR").toString())) {
							beta = entryData.get("P_MMU_SERIAL_NMBR_BETA_BAR").toString();
						}
						if (entryData.containsKey("P_MMU_SERIAL_NMBR_GAMMA_BAR")
								&& entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR") != null
								&& StringUtils.isNotEmpty(entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR").toString())) {
							gamma = entryData.get("P_MMU_SERIAL_NMBR_GAMMA_BAR").toString();
						}

					
					}
					
				}
				if (!alfa.isEmpty() || !beta.isEmpty() || !gamma.isEmpty()) {
					objtableData.put("P_MMU_SERIAL_NMBR_ALPHA_BAR", alfa);
					objtableData.put("P_MMU_SERIAL_NMBR_BETA_BAR", beta);
					objtableData.put("P_MMU_SERIAL_NMBR_GAMMA_BAR", gamma);
					tabelData2.add(objtableData);
					ovMap.put(alfa, "0");
					ovMap.put(beta, "1");
					ovMap.put(gamma, "2");
					HashMap<String, List<String>> validationMap = new HashMap<>();
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append(createHtmltableWithValidation(headerList2,
							XmlCommandsConstants.CBAND_VDU_OVOUTPUT, tabelData2, validationMap));
					htmlContent.append("</table>");

					
				}else {
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append(
							"<tr><td  bgcolor=#EEEEEE><b>" + XmlCommandsConstants.CBAND_VDU_OVOUTPUT + "</b></td></tr>\n");
					htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>" +  "NO DATA OV" + "</td></tr>\n");
					htmlContent.append("</table>");
					ovOpPresent = false;
					if (!outputLog1.contains("Fuze ID is Empty")) {
						audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(207, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""),
								XmlCommandsConstants.CBAND_VDU_OVOUTPUT + " : NO DATA OV");
					}

				}

			} else {
				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append(
						"<tr><td  bgcolor=#EEEEEE><b>" + XmlCommandsConstants.CBAND_VDU_OVOUTPUT + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>" + "NO DATA OV" + "</td></tr>\n");
				htmlContent.append("</table>");
				ovOpPresent = false;
				if (!outputLog1.contains("Fuze ID is Empty")) {
					audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(207, runTestEntity.getId(),
							enbId.replaceAll("^0+(?!$)", ""),
							XmlCommandsConstants.CBAND_VDU_OVOUTPUT + " : NO DATA OV");
				}

			}
			 
			if (tabelData1.size() == 0) {
				String errorMessage = "";
				if (outputLog.contains("rpc-error")) {
					try {
						NodeList nodeList2 = document.getElementsByTagName("rpc-error");
						for (int i = 0; i < nodeList2.getLength(); i++) {
							Node inChildNode = nodeList2.item(i);
							if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

								Element element = (Element) inChildNode;
								errorMessage = getXmlElementData(element, "error-message");
							}
						}
					} catch (Exception e) {

						errorMessage = "Script not Executed";
						e.printStackTrace();
					}
					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
					htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
					htmlContent.append("</table>");
					if (errorMessage.equals("Script not Executed")) {
						audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(102, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), command + " : " + errorMessage);
					} else if (errorMessage.toUpperCase().contains("TARGET NE IS NOT FOUND")
							|| errorMessage.toUpperCase().contains("TARGET NE IS NOT INITIALIZED")) {
						audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(106, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), command + " : " + errorMessage);
					} else {
						audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(110, runTestEntity.getId(),
								enbId.replaceAll("^0+(?!$)", ""), command + " : " + errorMessage);
					}

				} else {

					htmlContent.append(
							"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
					htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
					htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
					htmlContent.append("</table>");
					audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(201, runTestEntity.getId(),
							enbId.replaceAll("^0+(?!$)", ""), command + " : " + "NO DATA USM");
				}
				return htmlContent;
			}
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}
			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String oruid = "";
				String serialNumber = "";

				if (ovMap.containsKey(tdData.get("serial-num").trim())) {
					serialNumber = tdData.get("serial-num").trim();
					oruid = ovMap.get(tdData.get("serial-num").trim());
				}

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("serial-num")) {
						if (value.trim().equalsIgnoreCase(serialNumber)) {
							if (oruid.equalsIgnoreCase(tdData.get("o-ran-ru-id").trim())) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else {
								if (tdData.get("o-ran-ru-id").trim().equals("0") && oruid.equals("1")) {
									auditIssue4.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								} else if (tdData.get("o-ran-ru-id").trim().equals("0") && oruid.equals("2")) {
									auditIssue5.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								} else if (tdData.get("o-ran-ru-id").trim().equals("1") && oruid.equals("0")) {
									auditIssue6.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								} else if (tdData.get("o-ran-ru-id").trim().equals("1") && oruid.equals("2")) {
									auditIssue7.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								} else if (tdData.get("o-ran-ru-id").trim().equals("2") && oruid.equals("0")) {
									auditIssue8.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								} else if (tdData.get("o-ran-ru-id").trim().equals("2") && oruid.equals("1")) {
									auditIssue9.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
											+ tdData.get("serial-num") + "\n");
								}
								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							}

						} else if (ovOpPresent) {
							auditIssue1.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " serial-num : "
									+ tdData.get("serial-num") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			if (auditIssue1.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(58, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
			}

			if (auditIssue4.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(61, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue4.toString());
			}
			if (auditIssue5.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(62, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue5.toString());
			}
			if (auditIssue6.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(63, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue6.toString());
			}
			if (auditIssue7.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(64, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue7.toString());
			}
			if (auditIssue8.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(65, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue8.toString());
			}
			if (auditIssue9.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(66, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue9.toString());
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getUsmPTPAuditHTML(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			String entry1 = "IEEE1588-Configuration";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("interface-name");
			headerList1.add("ip");
			headerList1.add("prefix-length");
			headerList1.add("ip-get-type");
			headerList1.add("ip-oper");
			headerList1.add("prefix-length-oper");
			headerList1.add("address-state");
			headerList1.add("management");
			headerList1.add("signal-s1");
			headerList1.add("signal-x2");
			headerList1.add("bearer-s1");
			headerList1.add("bearer-x2");
			headerList1.add("ieee1588");

			String entry2 = "Clock-Source-Info-Verification";
			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("clock-source-id");
			headerList2.add("clock-source");
			headerList2.add("quality-level");
			headerList2.add("lock-out-enable");
			headerList2.add("priority-level");
			headerList2.add("holdoff-time");
			headerList2.add("restore-time");
			headerList2.add("active-clock");

			String entry3 = "GM-IP-Verification";
			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			headerList3.add("ip-version");
			headerList3.add("first-ipv4-address");
			headerList3.add("second-ipv4-address");
			headerList3.add("first-ipv6-address");
			headerList3.add("second-ipv6-address");
			headerList3.add("current-grand-master-ip-address");

			String entry4 = "PTP-Info-Verification";
			LinkedHashSet<String> headerList4 = new LinkedHashSet<>();
			headerList4.add("ptp-hybrid-mode");
			headerList4.add("ptp-profile");
			headerList4.add("ptp-domain-telecom");
			headerList4.add("ptp-domain-g8265-1");
			headerList4.add("ptp-domain-g8275-1");
			headerList4.add("ptp-domain-g8275-2");
			headerList4.add("secondary-domain");
			headerList4.add("delay-asymmetry");
			headerList4.add("ptp-frequency-holdover-exceed-threshold");
			headerList4.add("ptp-phase-holdover-exceed-threshold");
			headerList4.add("ptp-phase-holdover-exceed-fdd-threshold");
			headerList4.add("ptp-holdover-advance-notification");
			headerList4.add("acceptable-clock-class");
			headerList4.add("slave-ip-address");
			headerList4.add("available-state");
			headerList4.add("locking-state");

			String entry5 = "System_Location_Verification";
			LinkedHashSet<String> headerList5 = new LinkedHashSet<>();
			headerList5.add("latitude");
			headerList5.add("longitude");
			headerList5.add("height");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData3 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData4 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData5 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("hardware-management");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("digital-unit");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									NodeList childNodeList3 = elementchild2.getElementsByTagName("clock-unit");
									for (int m = 0; m < childNodeList3.getLength(); m++) {

										Node ChildNode3 = childNodeList3.item(m);
										if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {

											Element elementchild3 = (Element) ChildNode3;

											NodeList childNodeList4 = elementchild3.getElementsByTagName("ptp-info");
											for (int n = 0; n < childNodeList4.getLength(); n++) {

												Node ChildNode4 = childNodeList4.item(n);
												if (Node.ELEMENT_NODE == ChildNode4.getNodeType()) {

													Element elementchild4 = (Element) ChildNode4;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

													Iterator<String> itr = headerList4.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild4, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild4, header));
													}

													tabelData4.add(objtableData);
												}
											}
										}
									}
									childNodeList3 = elementchild2.getElementsByTagName("clock-unit");
									for (int m = 0; m < childNodeList3.getLength(); m++) {

										Node ChildNode3 = childNodeList3.item(m);
										if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {

											Element elementchild3 = (Element) ChildNode3;

											NodeList childNodeList4 = elementchild3.getElementsByTagName("ptp-info");
											for (int n = 0; n < childNodeList4.getLength(); n++) {

												Node ChildNode4 = childNodeList4.item(n);
												if (Node.ELEMENT_NODE == ChildNode4.getNodeType()) {

													Element elementchild4 = (Element) ChildNode4;

													NodeList childNodeList5 = elementchild4
															.getElementsByTagName("grand-master-info");
													for (int o = 0; o < childNodeList5.getLength(); o++) {

														Node ChildNode5 = childNodeList5.item(o);
														if (Node.ELEMENT_NODE == ChildNode5.getNodeType()) {

															Element elementchild5 = (Element) ChildNode5;

															LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

															Iterator<String> itr = headerList3.iterator();
															String header = itr.next();
															objtableData.put(header,
																	getXmlElementData(elementchild5, header));

															while (itr.hasNext()) {
																header = itr.next();
																objtableData.put(header,
																		getXmlElementData(elementchild5, header));
															}

															tabelData3.add(objtableData);
														}
													}
												}
											}
										}
									}
									childNodeList3 = elementchild2.getElementsByTagName("clock-unit");
									for (int m = 0; m < childNodeList3.getLength(); m++) {

										Node ChildNode3 = childNodeList3.item(m);
										if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {

											Element elementchild3 = (Element) ChildNode3;

											NodeList childNodeList4 = elementchild3
													.getElementsByTagName("clock-source-info");
											for (int n = 0; n < childNodeList4.getLength(); n++) {

												Node ChildNode4 = childNodeList4.item(n);
												if (Node.ELEMENT_NODE == ChildNode4.getNodeType()) {

													Element elementchild4 = (Element) ChildNode4;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

													Iterator<String> itr = headerList2.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild4, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild4, header));
													}

													tabelData2.add(objtableData);
												}
											}
										}
									}
								}
							}

						}
					}

					childNodeList1 = element.getElementsByTagName("common-management");
					for (int j = 0; j < childNodeList1.getLength(); j++) {

						Node ChildNode = childNodeList1.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList2 = elementchild.getElementsByTagName("system-location");
							for (int k = 0; k < childNodeList2.getLength(); k++) {

								Node ChildNode1 = childNodeList2.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// ucr
									NodeList childNodeList3 = elementchild1.getElementsByTagName("location-info");
									for (int l = 0; l < childNodeList3.getLength(); l++) {
										Node ChildNode2 = childNodeList3.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList5.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild2, header));
											header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild2, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData5.add(objtableData);
										}
									}
								}
							}
						}
					}
					childNodeList1 = element.getElementsByTagName("ip-system");
					for (int j = 0; j < childNodeList1.getLength(); j++) {

						Node ChildNode = childNodeList1.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList2 = elementchild.getElementsByTagName("external-interfaces");
							for (int k = 0; k < childNodeList2.getLength(); k++) {

								Node ChildNode1 = childNodeList2.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// ucr
									NodeList childNodeList3 = elementchild1.getElementsByTagName("ipv6-address");
									for (int l = 0; l < childNodeList3.getLength(); l++) {
										Node ChildNode2 = childNodeList3.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList1.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));
											header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild2, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData1.add(objtableData);
										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + entry1 + "</b></td></tr>\n");
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"CIQUpstateNY", "eNBId");
			String primaryClockSource = "";
			String secondaryClockSource = "";
			String scenario1Type = "";
			String PTP_Height = "";
			String Lat = "";
			String Long = "";
			String ip = "";
			if (!ObjectUtils.isEmpty(listOfCiqDetails)) {
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("primaryClockSource")) {
					primaryClockSource = listOfCiqDetails.get(0).getCiqMap().get("primaryClockSource").getHeaderValue()
							.replaceAll("^0+(?!$)", "");
				}
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("secondaryClockSource")) {
					secondaryClockSource = listOfCiqDetails.get(0).getCiqMap().get("secondaryClockSource")
							.getHeaderValue().replaceAll("^0+(?!$)", "");
				}
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("PTPHeight")) {
					PTP_Height = listOfCiqDetails.get(0).getCiqMap().get("PTPHeight").getHeaderValue()
							.replaceAll("^0+(?!$)", "");
				}
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("Lat")) {
					Lat = listOfCiqDetails.get(0).getCiqMap().get("Lat").getHeaderValue().replaceAll("^0+(?!$)", "");
				}
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("Long")) {
					Long = listOfCiqDetails.get(0).getCiqMap().get("Long").getHeaderValue().replaceAll("^0+(?!$)", "");
				}
				if (listOfCiqDetails.get(0).getCiqMap().containsKey("grandMasterIP")) {
					ip = listOfCiqDetails.get(0).getCiqMap().get("grandMasterIP").getHeaderValue()
							.replaceAll("^0+(?!$)", "");
				}
				
			}
			if (primaryClockSource.equalsIgnoreCase("gps-type")
					&& secondaryClockSource.equalsIgnoreCase("ieee1588-phasetype")) {
				scenario1Type = "Scenario1";
			} else if (primaryClockSource.equalsIgnoreCase("ieee1588-phasetype")
					&& secondaryClockSource.equalsIgnoreCase("gps-type")) {
				scenario1Type = "Scenario3";
			} else if (primaryClockSource.equalsIgnoreCase("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {
				scenario1Type = "Scenario2";
			}

			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssue2 = new StringBuilder();
			StringBuilder auditIssue3 = new StringBuilder();
			StringBuilder auditIssue4 = new StringBuilder();
			StringBuilder auditIssue5 = new StringBuilder();
			StringBuilder auditIssue6 = new StringBuilder();
			StringBuilder auditIssue7 = new StringBuilder();
			StringBuilder auditIssue8 = new StringBuilder();
			StringBuilder auditIssue9 = new StringBuilder();
			StringBuilder auditIssue10 = new StringBuilder();
			StringBuilder auditIssue11 = new StringBuilder();
			StringBuilder auditIssue12 = new StringBuilder();
			StringBuilder auditIssue13 = new StringBuilder();
			StringBuilder auditIssue14 = new StringBuilder();
			StringBuilder auditIssue15 = new StringBuilder();
			StringBuilder auditIssue16 = new StringBuilder();
			StringBuilder auditIssue17 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			
			if(scenario1Type.equals("Scenario1") || scenario1Type.equals("Scenario3")) {
				if(!(tabelData2.size()==2)) {
					auditIssue17.append("There should be two clock sources with clock-source-id: 0 and 1 are configured under clock-source-info"+"/n");	
				}
			}else if(scenario1Type.equals("Scenario2")) {
				if(!(tabelData2.size()==1)) {
					auditIssue17.append("There should be one clock sources with clock-source-id:  1 is configured under clock-source-info"+"/n");	
				}
			}

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("ieee1588") && (scenario1Type.equals("Scenario1")
								|| scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
						if (!value.equalsIgnoreCase("true") && tdData.get("interface-name").contains("ge_0_0_1.30")) {
							auditIssue1.append("interface-name : " + tdData.get("interface-name") + " ieee1588 : "
									+ tdData.get("ieee1588") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("false")
								&& tdData.get("interface-name").contains("ge_0_0_1.40")) {
							auditIssue1.append("interface-name : " + tdData.get("interface-name") + " ieee1588 : "
									+ tdData.get("ieee1588") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append(
					"<tr><td colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>" + entry2 + "</b></td></tr>\n");

			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList2) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData2) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("clock-source")) {
						if (!value.equalsIgnoreCase("gps-type") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue2.append("clock-source-id : " + tdData.get("clock-source-id") + " clock-source : "
									+ tdData.get("clock-source") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("ieee1588-phasetype")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario1")) {
							auditIssue2.append("clock-source-id : " + tdData.get("clock-source-id") + " clock-source : "
									+ tdData.get("clock-source") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}  else if (!value.equalsIgnoreCase("ieee1588-phasetype")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario2")) {
							auditIssue2.append("clock-source-id : " + tdData.get("clock-source-id") + " clock-source : "
									+ tdData.get("clock-source") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("gps-type") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario3")) {
							auditIssue2.append("clock-source-id : " + tdData.get("clock-source-id") + " lock-source : "
									+ tdData.get("clock-source") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("ieee1588-phasetype")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario3")) {
							auditIssue2.append("clock-source-id : " + tdData.get("clock-source-id") + " clock-source : "
									+ tdData.get("clock-source") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("quality-level")) {
						if (!value.equalsIgnoreCase("ssu-a") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue3.append("clock-source-id : " + tdData.get("clock-source-id") + " quality-level : "
									+ tdData.get("quality-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("ssu-a") && tdData.get("clock-source-id").contains("1")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue3.append("clock-source-id : " + tdData.get("clock-source-id") + " quality-level : "
									+ tdData.get("quality-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}else if (!value.equalsIgnoreCase("prc")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario2")) {
							auditIssue3.append("clock-source-id : " + tdData.get("clock-source-id") + " quality-level : "
									+ tdData.get("quality-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("ssu-a") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario3")) {
							auditIssue3.append("clock-source-id : " + tdData.get("clock-source-id") + " quality-level : "
									+ tdData.get("quality-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("prc")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario3")) {
							auditIssue3.append("clock-source-id : " + tdData.get("clock-source-id") + " quality-level : "
									+ tdData.get("quality-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("lock-out-enable") && (scenario1Type.equals("Scenario1")
							|| scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
						if (!value.equalsIgnoreCase("off")) {
							auditIssue4.append("clock-source-id : " + tdData.get("clock-source-id") + " lock-out-enable : "
									+ tdData.get("lock-out-enable") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}  else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("priority-level")) {
						if (!value.equalsIgnoreCase("1") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue5.append("clock-source-id : " + tdData.get("clock-source-id") + " priority-level : "
									+ tdData.get("priority-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("2") && tdData.get("clock-source-id").contains("1")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue5.append("clock-source-id : " + tdData.get("clock-source-id") + " priority-level : "
									+ tdData.get("priority-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}  else if (!value.equalsIgnoreCase("2")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario2")) {
							auditIssue5.append("clock-source-id : " + tdData.get("clock-source-id") + " priority-level : "
									+ tdData.get("priority-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("1") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario3")) {
							auditIssue5.append("clock-source-id : " + tdData.get("clock-source-id") + " priority-level : "
									+ tdData.get("priority-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("2")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario3")) {
							auditIssue5.append("clock-source-id : " + tdData.get("clock-source-id") + " priority-level : "
									+ tdData.get("priority-level") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("holdoff-time") && (scenario1Type.equals("Scenario1")
							|| scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
						if (!value.equalsIgnoreCase("p300") ) {
							auditIssue6.append("clock-source-id : " + tdData.get("clock-source-id") + " holdoff-time : "
									+ tdData.get("holdoff-time") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("restore-time") && (scenario1Type.equals("Scenario1")
							|| scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
						if (!value.equalsIgnoreCase("5") ) {
							auditIssue7.append("clock-source-id : " + tdData.get("clock-source-id") + " restore-time : "
									+ tdData.get("restore-time") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}  else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("active-clock")) {
						if (!value.equalsIgnoreCase("active") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue8.append("clock-source-id : " + tdData.get("clock-source-id") + " active-clock : "
									+ tdData.get("active-clock") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("standby") && tdData.get("clock-source-id").contains("1")
								&& scenario1Type.equals("Scenario1")) {
							auditIssue8.append("clock-source-id : " + tdData.get("clock-source-id") + " active-clock : "
									+ tdData.get("active-clock") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}  else if (!value.equalsIgnoreCase("active")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario2")) {
							auditIssue8.append("clock-source-id : " + tdData.get("clock-source-id") + " active-clock : "
									+ tdData.get("active-clock") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("standby") && tdData.get("clock-source-id").contains("0")
								&& scenario1Type.equals("Scenario3")) {
							auditIssue8.append("clock-source-id : " + tdData.get("clock-source-id") + " active-clock : "
									+ tdData.get("active-clock") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equalsIgnoreCase("active")
								&& tdData.get("clock-source-id").contains("1") && scenario1Type.equals("Scenario3")) {
							auditIssue8.append("clock-source-id : " + tdData.get("clock-source-id") + " active-clock : "
									+ tdData.get("active-clock") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append(
					"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + entry3 + "</b></td></tr>\n");

			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList3) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData3) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();

					if (key.equals("first-ipv6-address") && (scenario1Type.equals("Scenario1"))) {
						if (compareIPAddr(ip, value)) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							auditIssue9.append("first-ipv6-address: " + tdData.get("first-ipv6-address") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("current-grand-master-ip-address") && (scenario1Type.equals("Scenario1"))) {
						if (compareIPAddr(ip, value)) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							auditIssue9.append("current-grand-master-ip-address: "
									+ tdData.get("current-grand-master-ip-address") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");

					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append(
					"<tr><td colspan=" + headerList4.size() + " bgcolor=#EEEEEE><b>" + entry4 + "</b></td></tr>\n");

			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList4) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData4) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();

					if (key.equals("ptp-profile")) {
						if (!value.equals("telecom-2008") && (scenario1Type.equals("Scenario1"))) {
							auditIssue10.append("ptp-profile: " + tdData.get("ptp-profile") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("itu-g8275-1")
								&& (scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
							auditIssue10.append("ptp-profile: " + tdData.get("ptp-profile") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("ptp-domain-telecom")) {
						if (!value.equals("0") && (scenario1Type.equals("Scenario1"))) {
							auditIssue11.append("ptp-domain-telecom: " + tdData.get("ptp-domain-telecom") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("ptp-domain-g8275-1")) {
						if (!value.equals("24")
								&& (scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
							auditIssue12.append("ptp-domain-g8275-1: " + tdData.get("ptp-domain-g8275-1") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("available-state")) {
						if (!value.equals("running") && (scenario1Type.equals("Scenario1")
								|| scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
							auditIssue13.append("available-state: " + tdData.get("available-state") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("locking-state")) {
						if (!value.equals("frequency-locking") && (scenario1Type.equals("Scenario1"))) {
							auditIssue4.append("locking-state: " + tdData.get("locking-state") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else if (!value.equals("phase-locking")
								&& (scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
							auditIssue14.append("locking-state: " + tdData.get("locking-state") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else if (key.equals("ptp-hybrid-mode")) {
						if (!value.equals("disable")
								&& (scenario1Type.equals("Scenario2") || scenario1Type.equals("Scenario3"))) {
							auditIssue15.append("ptp-hybrid-mode: " + tdData.get("ptp-hybrid-mode") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}

			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append(
					"<tr><td colspan=" + headerList5.size() + " bgcolor=#EEEEEE><b>" + entry5 + "</b></td></tr>\n");

			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList5) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData5) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();

					if (key.equals("latitude")
							&& (scenario1Type.equals("Scenario1") || scenario1Type.equals("Scenario2"))) {
						if (value.contains(Lat)) {
							tableData.append("<td align=center>" + value + "</td>\n");

						} else {
							auditIssue16.append("latitude: " + tdData.get("latitude") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						}
					} else if (key.equals("longitude")
							&& (scenario1Type.equals("Scenario1") || scenario1Type.equals("Scenario2"))) {
						if (value.contains(Long)) {
							tableData.append("<td align=center>" + value + "</td>\n");

						} else {
							auditIssue16.append("longitude: " + tdData.get("longitude") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						}
					} else if (key.equals("height")
							&& (scenario1Type.equals("Scenario1") || scenario1Type.equals("Scenario2"))) {
						if (value.contains(PTP_Height)) {
							tableData.append("<td align=center>" + value + "</td>\n");

						} else {
							auditIssue16.append("height: " + tdData.get("height") + "\n");
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");

						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");

					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append("</table>\n");
			
			
			auditIssueAll.append(auditIssue1);
			auditIssueAll.append(auditIssue2);
			auditIssueAll.append(auditIssue3);
			auditIssueAll.append(auditIssue4);
			auditIssueAll.append(auditIssue5);
			auditIssueAll.append(auditIssue6);
			auditIssueAll.append(auditIssue7);
			auditIssueAll.append(auditIssue8);
			auditIssueAll.append(auditIssue9);
			auditIssueAll.append(auditIssue10);
			auditIssueAll.append(auditIssue11);
			auditIssueAll.append(auditIssue12);
			auditIssueAll.append(auditIssue13);
			auditIssueAll.append(auditIssue14);
			auditIssueAll.append(auditIssue15);
			auditIssueAll.append(auditIssue16);
			auditIssueAll.append(auditIssue17);
			
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData3, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData4, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData5, auditIssueAll);
			
			if (auditIssue1.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(85, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(85, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(85, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue2.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(86, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue2.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(86, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(86, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue3.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(87, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue3.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(87, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(87, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue4.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(88, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue4.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(88, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(88, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue5.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(89, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue5.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(89, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(89, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue6.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(90, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue6.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(90, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(90, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue7.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(91, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue7.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(91, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(91, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue8.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(92, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue8.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(92, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(92, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue9.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(93, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue9.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(93, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(93, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue10.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(111, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue10.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(111, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(111, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue11.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(94, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue11.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(94, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(94, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue12.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(95, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue12.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(95, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(95, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue13.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(96, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue13.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(96, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(96, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue14.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(97, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue14.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(97, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(97, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue15.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(98, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue15.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(98, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(98, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue16.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(99, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue16.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(99, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(99, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			if (auditIssue17.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(100, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue17.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(100, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(100, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getUSMMMUSerialNumberAudit(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			String outputLog1 = StringUtils.substringAfter(fullOutputLog, XmlCommandsConstants.AUDIT_4G_OVOUTPUT);
			outputLog1 = StringUtils.substringBefore(outputLog1, XmlCommandsConstants.ENDTEXT5G);
			List<HashMap<String, Object>> trakerList = null;
			boolean ovOpPresent = true;
			try {
				trakerList = new ObjectMapper().readValue(outputLog1,
						new TypeReference<List<HashMap<String, Object>>>() {
						});
			} catch (Exception e) {
				ovOpPresent = false;
				audit4GSummaryService.createAudit4GSummaryEntity(113, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), XmlCommandsConstants.AUDIT_4G_OVOUTPUT + " : NO DATA OV");
				e.printStackTrace();
			}

			String entry1 = "radio-unit-info";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("connected-digital-unit-board-type");
			headerList1.add("connected-digital-unit-board-id");
			headerList1.add("connected-digital-unit-port-id");
			headerList1.add("cascade-radio-unit-id");
			headerList1.add("unit-type");
			headerList1.add("board-type");
			headerList1.add("Radio");
			headerList1.add("radio-unit-serial-number");
			headerList1.add("carrier-index");
			headerList1.add("cell-number");
			headerList1.add("Sector");

			String entry2 = "eutran-cell-fdd-tdd";
			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("cell-num");
			headerList2.add("cell-band-carrier");

			String entry3 = "Ov-Response-Table";

			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			headerList3.add("key");
			headerList3.add("value");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			HashSet<LinkedHashMap<String, Object>> tabelData3 = new HashSet<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("hardware-management");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("radio-unit");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									NodeList childNodeList3 = elementchild2.getElementsByTagName("radio-unit-info");
									for (int m = 0; m < childNodeList3.getLength(); m++) {

										Node ChildNode3 = childNodeList3.item(m);
										if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {

											Element elementchild3 = (Element) ChildNode3;

											NodeList childNodeList4 = elementchild3
													.getElementsByTagName("carrier-control-info");
											for (int n = 0; n < childNodeList4.getLength(); n++) {

												Node ChildNode4 = childNodeList4.item(n);
												if (Node.ELEMENT_NODE == ChildNode4.getNodeType()) {

													Element elementchild4 = (Element) ChildNode4;

													LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

													Iterator<String> itr = headerList1.iterator();
													String header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));
													header = itr.next();
													objtableData.put(header, getXmlElementData(elementchild3, header));

													while (itr.hasNext()) {
														header = itr.next();
														objtableData.put(header,
																getXmlElementData(elementchild4, header));
													}

													tabelData1.add(objtableData);
												}
											}
										}
									}

								}
							}

						}
					}

					childNodeList1 = element.getElementsByTagName("enb-function");
					for (int j = 0; j < childNodeList1.getLength(); j++) {

						Node ChildNode = childNodeList1.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList2 = elementchild.getElementsByTagName("eutran-generic-cell");
							for (int k = 0; k < childNodeList2.getLength(); k++) {

								Node ChildNode1 = childNodeList2.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// ucr
									NodeList childNodeList3 = elementchild1.getElementsByTagName("eutran-cell-fdd-tdd");
									for (int l = 0; l < childNodeList3.getLength(); l++) {
										Node ChildNode2 = childNodeList3.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList2.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild2, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData2.add(objtableData);
										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + entry1 + "</b></td></tr>\n");

			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.get("board-type").equals("rfv01u-d20")) {
					tdData.put("Radio", "Legacy LL radio");
				} else if (tdData.get("board-type").equals("rfv01u-d10")) {
					tdData.put("Radio", "Legacy HH Radio");
				} else if (tdData.get("board-type").equals("rt4401-480")) {
					tdData.put("Radio", "CBRS Radio");
				} else if (tdData.get("board-type").equals("rt2201-460")) {
					tdData.put("Radio", "LAA Radio");
				} else if (tdData.get("board-type").equals("rf4440d-130")) {
					tdData.put("Radio", "ORAN LL");
				} else if (tdData.get("board-type").equals("rf4439d-250")) {
					tdData.put("Radio", "ORAN HH");
				}
			}

			ArrayList<String> ts3500 = new ArrayList<String>();
			ArrayList<String> ts5000 = new ArrayList<String>();

			for (LinkedHashMap<String, String> tdData : tabelData2) {
				if (tdData.get("cell-num").length() == 3) {
					if (tdData.get("cell-band-carrier").contains("3500")) {
						if (!ts3500.contains(tdData.get("cell-num").substring(0, 2))) {
							ts3500.add(tdData.get("cell-num").substring(0, 2));
						}
					} else if (tdData.get("cell-band-carrier").contains("5000")) {
						if (!ts5000.contains(tdData.get("cell-num").substring(0, 2))) {
							ts5000.add(tdData.get("cell-num").substring(0, 2));
						}
					}
				}
			}
			Collections.sort(ts3500);
			Collections.sort(ts5000);

			HashMap<String, String> ovMap = new HashMap<>();
			for (int i = 0; i < ts3500.size(); i++) {
				String Sector1 = "-";
				if (i == 0) {
					Sector1 = "Alpha";
					ovMap.put(ts3500.get(i), Sector1);
				} else if (i == 1) {
					Sector1 = "Beta";
					ovMap.put(ts3500.get(i), Sector1);
				} else if (i == 2) {
					Sector1 = "Gamma";
					ovMap.put(ts3500.get(i), Sector1);
				} else if (i == 3) {
					Sector1 = "Delta";
					ovMap.put(ts3500.get(i), Sector1);
				} else if (i == 4) {
					Sector1 = "Epsilon";
					ovMap.put(ts3500.get(i), Sector1);
				} else if (i == 5) {
					Sector1 = "Theta";
					ovMap.put(ts3500.get(i), Sector1);
				}
			}
			for (int i = 0; i < ts5000.size(); i++) {
				String Sector1 = "-";
				if (i == 0) {
					Sector1 = "Alpha";
					ovMap.put(ts5000.get(i), Sector1);
				} else if (i == 1) {
					Sector1 = "Beta";
					ovMap.put(ts5000.get(i), Sector1);
				} else if (i == 2) {
					Sector1 = "Gamma";
					ovMap.put(ts5000.get(i), Sector1);
				} else if (i == 3) {
					Sector1 = "Delta";
					ovMap.put(ts5000.get(i), Sector1);
				} else if (i == 4) {
					Sector1 = "Epsilon";
					ovMap.put(ts5000.get(i), Sector1);
				} else if (i == 5) {
					Sector1 = "Theta";
					ovMap.put(ts5000.get(i), Sector1);
				}
			}
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.get("cell-number").length() == 1) {
					if (tdData.get("cell-number").equals("1")) {
						tdData.put("Sector", "Alpha");
					} else if (tdData.get("cell-number").equals("2")) {
						tdData.put("Sector", "Beta");
					} else if (tdData.get("cell-number").equals("3")) {
						tdData.put("Sector", "Gamma");
					} else if (tdData.get("cell-number").equals("4")) {
						tdData.put("Sector", "Delta");
					} else if (tdData.get("cell-number").equals("5")) {
						tdData.put("Sector", "Epsilon");
					} else if (tdData.get("cell-number").equals("6")) {
						tdData.put("Sector", "Theta");
					}
				} else if (tdData.get("cell-number").length() == 2) {
					if (tdData.get("cell-number").substring(0, 1).equals("1")) {
						tdData.put("Sector", "Alpha");
					} else if (tdData.get("cell-number").substring(0, 1).equals("2")) {
						tdData.put("Sector", "Beta");
					} else if (tdData.get("cell-number").substring(0, 1).equals("3")) {
						tdData.put("Sector", "Gamma");
					} else if (tdData.get("cell-number").substring(0, 1).equals("4")) {
						tdData.put("Sector", "Delta");
					} else if (tdData.get("cell-number").substring(0, 1).equals("5")) {
						tdData.put("Sector", "Epsilon");
					} else if (tdData.get("cell-number").substring(0, 1).equals("6")) {
						tdData.put("Sector", "Theta");
					}
				} else if (tdData.get("cell-number").length() == 3) {
					String s = tdData.get("cell-number").substring(0, 2);
					if (ovMap.containsKey(s)) {
						tdData.put("Sector", ovMap.get(s));
					}
				}
			}
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}
			String cbrsAlfaOv = "";
			String cbrsbetaOv = "";
			String cbrsgamaOv = "";
			String laaAlfa = "";
			String laabeta = "";
			String laagamma = "";
			String rrhHiAlfa = "";
			String rrhHiBeta = "";
			String rrhHiGamma = "";
			String rrhLoAlfa = "";
			String rrhLoBeta = "";
			String rrhLoGamma = "";
			if (ovOpPresent) {
				for (HashMap<String, Object> entryData : trakerList) {
					if (entryData.containsKey("P_4G_LTE_FINAL")
							&& StringUtils.isNotEmpty(entryData.get("P_4G_LTE_FINAL").toString())
							&& "1".equalsIgnoreCase(entryData.get("P_4G_LTE_FINAL").toString())) {
						if (entryData.containsKey("P_RRH_HI_SERIAL_NUMBER_ALPHA")
								&& entryData.get("P_RRH_HI_SERIAL_NUMBER_ALPHA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_HI_SERIAL_NUMBER_ALPHA").toString())) {
							rrhHiAlfa = entryData.get("P_RRH_HI_SERIAL_NUMBER_ALPHA").toString();
						}
						if (entryData.containsKey("P_RRH_HI_SERIAL_NUMBER_BETA_BA")
								&& entryData.get("P_RRH_HI_SERIAL_NUMBER_BETA_BA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_HI_SERIAL_NUMBER_BETA_BA").toString())) {
							rrhHiBeta = entryData.get("P_RRH_HI_SERIAL_NUMBER_BETA_BA").toString();
						}
						if (entryData.containsKey("P_RRH_HI_SERIAL_NUMBER_GAMMA")
								&& entryData.get("P_RRH_HI_SERIAL_NUMBER_GAMMA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_HI_SERIAL_NUMBER_GAMMA").toString())) {
							rrhHiGamma = entryData.get("P_RRH_HI_SERIAL_NUMBER_GAMMA").toString();
						}
						if (entryData.containsKey("P_RRH_LO_SERIAL_NUMBER_ALPHA")
								&& entryData.get("P_RRH_LO_SERIAL_NUMBER_ALPHA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_LO_SERIAL_NUMBER_ALPHA").toString())) {
							rrhLoAlfa = entryData.get("P_RRH_LO_SERIAL_NUMBER_ALPHA").toString();
						}
						if (entryData.containsKey("P_RRH_LO_SERIAL_NUMBER_BETA")
								&& entryData.get("P_RRH_LO_SERIAL_NUMBER_BETA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_LO_SERIAL_NUMBER_BETA").toString())) {
							rrhLoBeta = entryData.get("P_RRH_LO_SERIAL_NUMBER_BETA").toString();
						}
						if (entryData.containsKey("P_RRH_LO_SERIAL_NUMBER_GAMMA")
								&& entryData.get("P_RRH_LO_SERIAL_NUMBER_GAMMA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_RRH_LO_SERIAL_NUMBER_GAMMA").toString())) {
							rrhLoGamma = entryData.get("P_RRH_LO_SERIAL_NUMBER_GAMMA").toString();
						}

						if (entryData.containsKey("P_CBRS_RRH_SERIAL_NUMBER_ALPHA")
								&& entryData.get("P_CBRS_RRH_SERIAL_NUMBER_ALPHA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_CBRS_RRH_SERIAL_NUMBER_ALPHA").toString())) {
							cbrsAlfaOv = entryData.get("P_CBRS_RRH_SERIAL_NUMBER_ALPHA").toString();
						}
						if (entryData.containsKey("P_CBRS_RRH_SERIAL_NUMBER_BETA_")
								&& entryData.get("P_CBRS_RRH_SERIAL_NUMBER_BETA_") != null
								&& StringUtils.isNotEmpty(entryData.get("P_CBRS_RRH_SERIAL_NUMBER_BETA_").toString())) {
							cbrsbetaOv = entryData.get("P_CBRS_RRH_SERIAL_NUMBER_BETA_").toString();
						}
						if (entryData.containsKey("P_CBRS_RRH_SERIAL_NUMBER_GAMMA")
								&& entryData.get("P_CBRS_RRH_SERIAL_NUMBER_GAMMA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_CBRS_RRH_SERIAL_NUMBER_GAMMA").toString())) {
							cbrsgamaOv = entryData.get("P_CBRS_RRH_SERIAL_NUMBER_GAMMA").toString();
						}
						if (entryData.containsKey("P_LAA_RRH_SERIAL_NUMBER_ALPHA")
								&& entryData.get("P_LAA_RRH_SERIAL_NUMBER_ALPHA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_LAA_RRH_SERIAL_NUMBER_ALPHA").toString())) {
							laaAlfa = entryData.get("P_LAA_RRH_SERIAL_NUMBER_ALPHA").toString();
						}
						if (entryData.containsKey("P_LAA_RRH_SERIAL_NUMBER_BETA")
								&& entryData.get("P_LAA_RRH_SERIAL_NUMBER_BETA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_LAA_RRH_SERIAL_NUMBER_BETA").toString())) {
							laabeta = entryData.get("P_LAA_RRH_SERIAL_NUMBER_BETA").toString();
						}
						if (entryData.containsKey("P_LAA_RRH_SERIAL_NUMBER_GAMMA")
								&& entryData.get("P_LAA_RRH_SERIAL_NUMBER_GAMMA") != null
								&& StringUtils.isNotEmpty(entryData.get("P_LAA_RRH_SERIAL_NUMBER_GAMMA").toString())) {
							laagamma = entryData.get("P_LAA_RRH_SERIAL_NUMBER_GAMMA").toString();
						}
					}
				}
			}
			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("radio-unit-serial-number") && ovOpPresent) {
						if (!value.equals(rrhLoAlfa)
								&& (tdData.get("Radio").equals("Legacy LL radio")
										|| tdData.get("Radio").equals("ORAN LL"))
								&& tdData.get("Sector").equals("Alpha")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(rrhLoBeta)
								&& (tdData.get("Radio").equals("Legacy LL radio")
										|| tdData.get("Radio").equals("ORAN LL"))
								&& tdData.get("Sector").equals("Beta")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(rrhLoGamma)
								&& (tdData.get("Radio").equals("Legacy LL radio")
										|| tdData.get("Radio").equals("ORAN LL"))
								&& tdData.get("Sector").equals("Gamma")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(rrhHiAlfa)
								&& (tdData.get("Radio").equals("Legacy HH Radio")
										|| tdData.get("Radio").equals("ORAN HH"))
								&& tdData.get("Sector").equals("Alpha")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(rrhHiBeta)
								&& (tdData.get("Radio").equals("Legacy HH Radio")
										|| tdData.get("Radio").equals("ORAN HH"))
								&& tdData.get("Sector").equals("Beta")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(rrhHiGamma)
								&& (tdData.get("Radio").equals("Legacy HH Radio")
										|| tdData.get("Radio").equals("ORAN HH"))
								&& tdData.get("Sector").equals("Gamma")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(cbrsAlfaOv) && tdData.get("Radio").equals("CBRS Radio")
								&& tdData.get("Sector").equals("Alpha")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(cbrsbetaOv) && tdData.get("Radio").equals("CBRS Radio")
								&& tdData.get("Sector").equals("Beta")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(cbrsgamaOv) && tdData.get("Radio").equals("CBRS Radio")
								&& tdData.get("Sector").equals("Gamma")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(laaAlfa) && tdData.get("Radio").equals("LAA Radio")
								&& tdData.get("Sector").equals("Alpha")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(laabeta) && tdData.get("Radio").equals("LAA Radio")
								&& tdData.get("Sector").equals("Beta")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else if (!value.equals(laagamma) && tdData.get("Radio").equals("LAA Radio")
								&& tdData.get("Sector").equals("Gamma")) {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("connected-digital-unit-board-id: "
									+ tdData.get("connected-digital-unit-board-id")
									+ " connected-digital-unit-port-id: " + tdData.get("connected-digital-unit-port-id")
									+ " Radio: " + tdData.get("Radio") + " cell-number : " + tdData.get("cell-number")
									+ " Sector : " + tdData.get("Sector") + " radio-unit-serial-number: "
									+ tdData.get("radio-unit-serial-number") + "\n");
						} else {
							tableData.append("<td align=center>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}

			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			htmlContent.append(
					"<tr><td colspan=" + headerList2.size() + " bgcolor=#EEEEEE><b>" + entry2 + "</b></td></tr>\n");

			tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList2) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData2) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String value = resultTableData.getValue();
					tableData.append("<td align=center>" + value + "</td>\n");

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);

			if (ovOpPresent) {
				for (HashMap<String, Object> tdData : trakerList) {
					for (Entry<String, Object> resultTableData : tdData.entrySet()) {
						String key = resultTableData.getKey();
						Object value = resultTableData.getValue();
						LinkedHashMap<String, Object> objtableData = new LinkedHashMap<>();
						Iterator<String> itr = headerList3.iterator();
						String header = itr.next();
						objtableData.put(header, key);
						header = itr.next();
						objtableData.put(header, value);
						if (tdData.containsKey("P_4G_LTE_FINAL")
								&& StringUtils.isNotEmpty(tdData.get("P_4G_LTE_FINAL").toString())
								&& "1".equalsIgnoreCase(tdData.get("P_4G_LTE_FINAL").toString())) {
							if (key.equals("P_CBRS_RRH_SERIAL_NUMBER_ALPHA")
									|| key.equals("P_CBRS_RRH_SERIAL_NUMBER_BETA_")
									|| key.equals("P_CBRS_RRH_SERIAL_NUMBER_GAMMA")
									|| key.equals("P_LAA_RRH_SERIAL_NUMBER_ALPHA")
									|| key.equals("P_LAA_RRH_SERIAL_NUMBER_GAMMA")
									|| key.equals("P_LAA_RRH_SERIAL_NUMBER_BETA")
									|| key.equals("P_RRH_LO_SERIAL_NUMBER_ALPHA")
									|| key.equals("P_RRH_LO_SERIAL_NUMBER_BETA")
									|| key.equals("P_RRH_LO_SERIAL_NUMBER_GAMMA")
									|| key.equals("P_RRH_HI_SERIAL_NUMBER_ALPHA")
									|| key.equals("P_RRH_HI_SERIAL_NUMBER_BETA_BA")
									|| key.equals("P_RRH_HI_SERIAL_NUMBER_GAMMA")) {

								tabelData3.add(objtableData);
							}
						}
					}
				}
				tableHeader = "";
				tableHeader = tableHeader + "<tr>\n";
				for (String headerName : headerList3) {
					tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
				}

				tableHeader = tableHeader + "</tr>\n";
				htmlContent.append(
						"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + entry3 + "</b></td></tr>\n");
				tableData = new StringBuilder();
				for (LinkedHashMap<String, Object> tdData : tabelData3) {
					tableData.append("<tr>\n");

					for (Entry<String, Object> resultTableData : tdData.entrySet()) {
						tableData.append("<td align=center>" + resultTableData.getValue() + "</td>\n");

					}
					tableData.append("</tr>\n");

				}
				htmlContent.append(tableHeader);
				htmlContent.append(tableData);
			} else {
				htmlContent.append(
						"<tr><td colspan=" + headerList3.size() + " bgcolor=#EEEEEE><b>" + entry3 + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			}
			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());

			auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData2, auditIssueAll);
			//auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData3);
		
			if (auditIssue1.length() != 0) {
				audit4GSummaryService.createAudit4GSummaryEntity(114, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
				audit4GSummaryService.createAudit4GPassFailEntity(114, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			} else {
				audit4GSummaryService.createAudit4GPassFailEntity(114, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getCbandAudit(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			if (command.equals(XmlCommandsConstants.CBAND_VDU_E_TILT)) {
				htmlContent.append(get5GCbandEtilt(fullOutputLog, command, enbId, dbcollectionFileName, runTestEntity));
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return htmlContent;
	}

	private Object get5GCbandEtilt(String fullOutputLog, String command, String enbId, String dbcollectionFileName,
			RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("o-ran-ru-id");
			headerList1.add("antenna-id");
			headerList1.add("user-label");
			headerList1.add("config-tilt");
			headerList1.add("config-install-date");
			headerList1.add("config-installer-id");
			headerList1.add("config-base-station-id");
			headerList1.add("config-sector-id");
			headerList1.add("config-antenna-bearing");
			headerList1.add("config-installed-tilt");
			HashMap<String, String> productCode = getproductCodeOru(fullOutputLog,
					XmlCommandsConstants.CBAND_VDU_POSTAUDIT);
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("o-ran-radio-unit-info");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("ret-info");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild1, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			if (tabelData1.size() == 0) {
				htmlContent.setLength(0);
				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
				htmlContent.append("</table>");
				if (productCode.containsValue("RT8808-77A")) {
					audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(85, runTestEntity.getId(),
							enbId.replaceAll("^0+(?!$)", ""), command + " : No Data From USM");
				}
				return htmlContent;
			}
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			List<CIQDetailsModel> listOfCiqDetailsday2 = getCiqDetailsForRuleValidationsheet(enbId,
					dbcollectionFileName, "Day2", "eNBId");
			StringBuilder auditIssue1 = new StringBuilder();
			StringBuilder auditIssueAll = new StringBuilder();

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String etilt = "";
				if (!ObjectUtils.isEmpty(listOfCiqDetailsday2)) {
					for (CIQDetailsModel ciqData : listOfCiqDetailsday2) {
						if (ciqData.getCiqMap().containsKey("oruId") && ciqData.getCiqMap().get("oruId")
								.getHeaderValue().trim().equals(tdData.get("o-ran-ru-id").trim())) {
							if (ciqData.getCiqMap().containsKey("ElectricalTilt")) {
								etilt = ciqData.getCiqMap().get("ElectricalTilt").getHeaderValue().trim();
							}
						}
					}
				}

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {

					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("config-tilt") && productCode.containsKey(tdData.get("o-ran-ru-id").trim())
							&& productCode.get(tdData.get("o-ran-ru-id").trim()).equals("RT8808-77A")) {
						if (etilt.equals(value.substring(0, value.length() - 1))) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
							auditIssue1.append("o-ran-ru-id : " + tdData.get("o-ran-ru-id") + " config-tilt : "
									+ tdData.get("config-tilt") + "\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
			AuditCriticalParamsSummaryEntity auditCriticalParamsEntity = auditCriticalParamsService
					.createAuditCriticalParamsSummaryEntity(enbId, runTestEntity.getId());
			auditIssueAll.append(auditIssue1);
			auditCriticalParamsService.storeAuditCriticalParams(auditCriticalParamsEntity, tabelData1, auditIssueAll);
			if (auditIssue1.length() != 0) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(85, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	private HashMap<String, String> getproductCodeOru(String fullOutputLog, String command) {
		HashMap<String, String> map = new HashMap<>();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("o-ran-ru-id");
			headerList1.add("active");
			headerList1.add("running");
			headerList1.add("product-code");
			

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("o-ran-radio-unit-info");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("software-slot");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild1, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.get("active").trim().equals("true") && tdData.get("running").trim().equals("true")) {
					map.put(tdData.get("o-ran-ru-id"), tdData.get("product-code"));

				}
			}
			

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return map;
	}

/*	public void checkEmptyTableCband(List<LinkedHashMap<String, String>> tabelData, String entry,
			RunTestEntity runTestEntity, String enbId) {
		try {
			if (tabelData.isEmpty()) {
				audit5GCBandSummaryService.createAudit5GCBandSummaryEntity(201, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), entry + " : No Data From USM");
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}*/

	public StringBuilder get4GFsuAudit(String fullOutputLog, String command, String enbId, String dbcollectionFileName,
			RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			if (command.equals(XmlCommandsConstants.AUDIT4G_FSU_SFP_INVENTORY)) {
				htmlContent.append(get4GFSuHtmalContent(fullOutputLog, command, enbId, dbcollectionFileName, runTestEntity));
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return htmlContent;
	}

	private Object get4GFSuHtmalContent(String fullOutputLog, String command, String enbId, String dbcollectionFileName,
			RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("processor-unit-type");
			headerList1.add("processor-unit-id");
			headerList1.add("port-type");
			headerList1.add("port-id");
			headerList1.add("unit-id");
			headerList1.add("vendor-family-type");
			headerList1.add("hardware-name");
			headerList1.add("vendor-name");
			headerList1.add("manufactured-date");
			headerList1.add("install-date");
			headerList1.add("service-date");
			headerList1.add("position");
			headerList1.add("manufactured-data");
			headerList1.add("wave-length");
			headerList1.add("service-data");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ChildNode1 = nodeList.item(i);
				if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

					Element elementchild1 = (Element) ChildNode1;

					NodeList nodelist1 = elementchild1.getElementsByTagName("optic-module-inventory");
					for (int k = 0; k < nodelist1.getLength(); k++) {
						Node ChildNode = nodelist1.item(k);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {
							Element elementchild6 = (Element) ChildNode;
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							Iterator<String> itr = headerList1.iterator();
							String header = itr.next();
							objtableData.put(header, getXmlElementData(elementchild1, header));
							while (itr.hasNext()) {
								header = itr.next();
								objtableData.put(header, getXmlElementData(elementchild6, header));
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			if (tabelData1.size() == 0) {
				htmlContent.setLength(0);
				htmlContent.append(
						"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
				htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
				htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
				htmlContent.append("</table>");
				return htmlContent;
			}
			StringBuilder auditIssue1 = new StringBuilder();
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String hw = tdData.get("hardware-name");
				String venderName = "";
				String wavelength = "";
				boolean entryPresent = false;
				boolean matchingType = false;
				boolean hardwerePresent = true;
				List<Audit4GfsuHardwareDetailsEntity> auditConstantsList1 = auditFSUHardwareDetailsRepository
						.getAuditHardwareDetailsEntityList(hw, "DU");

				if (ObjectUtils.isEmpty(auditConstantsList1)) {
					hardwerePresent = false;
				} else {
					venderName = auditConstantsList1.get(0).getVendorName();
					wavelength = auditConstantsList1.get(0).getWaveLength();
				}
				if (tdData.get("port-type").equals("cpri-type") || tdData.get("port-type").equals("du-cpri-type")) {
					matchingType = true;
				}

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("hardware-name") && matchingType) {
						if (hardwerePresent) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							if (!entryPresent) {
								auditIssue1.append("processor-unit-id :" + tdData.get("processor-unit-id")
										+ " port-id :" + tdData.get("port-id") + " port-type :"
										+ tdData.get("port-type") + " hardware-name : " + tdData.get("hardware-name")
										+ " vendor-name : " + tdData.get("vendor-name") + " wave-length : "
										+ tdData.get("wave-length") + "\n");
							}
							entryPresent = true;
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("vendor-name") && matchingType) {
						if (value.contains(venderName)) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							if (!entryPresent) {
								auditIssue1.append("processor-unit-id :" + tdData.get("processor-unit-id")
										+ " port-id :" + tdData.get("port-id") + " port-type :"
										+ tdData.get("port-type") + " hardware-name : " + tdData.get("hardware-name")
										+ " vendor-name : " + tdData.get("vendor-name") + " wave-length : "
										+ tdData.get("wave-length") + "\n");
							}
							entryPresent = true;
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else if (key.equals("wave-length") && matchingType) {
						if (value.contains(wavelength)) {
							tableData.append("<td align=center>" + value + "</td>\n");
						} else {
							if (!entryPresent) {
								auditIssue1.append("processor-unit-id :" + tdData.get("processor-unit-id")
										+ " port-id :" + tdData.get("port-id") + " port-type :"
										+ tdData.get("port-type") + " hardware-name : " + tdData.get("hardware-name")
										+ " vendor-name : " + tdData.get("vendor-name") + " wave-length : "
										+ tdData.get("wave-length") + "\n");
							}
							entryPresent = true;
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}
					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
			if (auditIssue1.length() != 0) {
				audit4GFsuSummaryService.createAudit4GFsuSummaryEntity(27, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
				audit4GFsuSummaryService.createAudit4GFsuPassFailSummaryEntity(27, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "fail");
			}else {
				audit4GFsuSummaryService.createAudit4GFsuPassFailSummaryEntity(27, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), "pass");
			}
			

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	public StringBuilder getDssPowerAudit(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cell-identity");
			headerList1.add("cell-num");
			headerList1.add("power");
			headerList1.add("dl-antenna-count");
			headerList1.add("nr-physical-cell-id");
			headerList1.add("nr-bandwidth-dl");
			headerList1.add("nr-frequency-band");
			headerList1.add("o-ran-ru-id");
			headerList1.add("product-code");

			LinkedHashSet<String> headerList2 = new LinkedHashSet<>();
			headerList2.add("o-ran-ru-id");
			headerList2.add("nr-support-cell-number");

			LinkedHashSet<String> headerList3 = new LinkedHashSet<>();
			headerList3.add("nr-support-cell-number");
			headerList3.add("product-code");

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData2 = new ArrayList<>();
			List<LinkedHashMap<String, String>> tabelData3 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("gnb-du-function");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("gutran-du-cell");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									NodeList childNodeList3 = elementchild2
											.getElementsByTagName("gutran-du-cell-entries");
									for (int m = 0; m < childNodeList3.getLength(); m++) {

										Node ChildNode3 = childNodeList3.item(m);
										if (Node.ELEMENT_NODE == ChildNode3.getNodeType()) {

											Element elementchild3 = (Element) ChildNode3;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

											Iterator<String> itr = headerList1.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild3, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild3, header));
											}

											tabelData1.add(objtableData);
										}
									}
								}
							}

						}
					}

					childNodeList1 = element.getElementsByTagName("hardware-management");
					for (int j = 0; j < childNodeList1.getLength(); j++) {

						Node ChildNode = childNodeList1.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList2 = elementchild.getElementsByTagName("o-ran-radio-unit-info");
							for (int k = 0; k < childNodeList2.getLength(); k++) {

								Node ChildNode1 = childNodeList2.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// ucr
									NodeList childNodeList3 = elementchild1
											.getElementsByTagName("oru-fsu-cuplane-info");
									for (int l = 0; l < childNodeList3.getLength(); l++) {
										Node ChildNode2 = childNodeList3.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList2.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData2.add(objtableData);
										}
									}
								}
							}
						}
					}
					childNodeList1 = element.getElementsByTagName("hardware-management");
					for (int j = 0; j < childNodeList1.getLength(); j++) {

						Node ChildNode = childNodeList1.item(j);
						if (Node.ELEMENT_NODE == ChildNode.getNodeType()) {

							Element elementchild = (Element) ChildNode;

							// clock-unit
							NodeList childNodeList2 = elementchild.getElementsByTagName("o-ran-radio-unit-info");
							for (int k = 0; k < childNodeList2.getLength(); k++) {

								Node ChildNode1 = childNodeList2.item(k);
								if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

									Element elementchild1 = (Element) ChildNode1;

									// ucr
									NodeList childNodeList3 = elementchild1.getElementsByTagName("system");
									for (int l = 0; l < childNodeList3.getLength(); l++) {
										Node ChildNode2 = childNodeList3.item(l);
										if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

											Element elementchild2 = (Element) ChildNode2;

											LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
											Iterator<String> itr = headerList3.iterator();
											String header = itr.next();
											objtableData.put(header, getXmlElementData(elementchild1, header));

											while (itr.hasNext()) {
												header = itr.next();
												objtableData.put(header, getXmlElementData(elementchild2, header));
											}
											tabelData3.add(objtableData);
										}
									}
								}
							}
						}
					}
				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			List<CIQDetailsModel> listOfCiqDetails = getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					"vDUGrowSiteLevel(Day1)CQ", "eNBId");
			String enb4G = "";
			StringBuilder auditIssue1 = new StringBuilder();
			if (!ObjectUtils.isEmpty(listOfCiqDetails)) {

				if (listOfCiqDetails.get(0).getCiqMap().containsKey("4GeNB")) {
					enb4G = listOfCiqDetails.get(0).getCiqMap().get("4GeNB").getHeaderValue().trim()
							.replaceAll("^0+(?!$)", "");
				}

			}
			List<CIQDetailsModel> listOfCiqDetailsDay2 = getCiqDetailsForRuleValidationsheet(enb4G,
					dbcollectionFileName, "vDUDay_2", "eNBId");

			for (LinkedHashMap<String, String> tdData : tabelData1) {
				for (LinkedHashMap<String, String> tdData2 : tabelData2) {
					if (tdData2.get("nr-support-cell-number").equals(tdData.get("cell-num"))) {
						tdData.put("o-ran-ru-id", tdData2.get("o-ran-ru-id"));
					}
				}
				for (LinkedHashMap<String, String> tdData3 : tabelData3) {
					if (tdData3.get("nr-support-cell-number").equals(tdData.get("cell-num"))) {
						tdData.put("product-code", tdData3.get("product-code"));
					}
				}
			}

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");
				String bandWidth = "";
				if (tdData.get("nr-bandwidth-dl").contains("nr-bandwidth-5")) {
					bandWidth = "5";
				} else if (tdData.get("nr-bandwidth-dl").contains("nr-bandwidth-10")) {
					bandWidth = "10";
				} else if (tdData.get("nr-bandwidth-dl").contains("nr-bandwidth-15")) {
					bandWidth = "15";
				} else if (tdData.get("nr-bandwidth-dl").contains("nr-bandwidth-20")) {
					bandWidth = "20";
				}
				String productCode = tdData.get("product-code");
				String band = tdData.get("nr-frequency-band");
				String diversity = "";
				if (tdData.get("dl-antenna-count").contains("dl-antenna-count-4tx")) {
					diversity = "4";
				} else if (tdData.get("dl-antenna-count").contains("dl-antenna-count-2tx")) {
					diversity = "2";
				}
				String power = "";

				List<AuditDetailConstantsEntity> auditConstantsList = auditDetailConstantsRepository
						.getAuditConstantsEntityList(bandWidth, productCode, band, diversity);
				Double dbPower = 0.0;
				if (!ObjectUtils.isEmpty(auditConstantsList)) {
					power = auditConstantsList.get(0).getPower();
					dbPower = Double.parseDouble(power);
				}
				String frSolution = "";
				if (!ObjectUtils.isEmpty(listOfCiqDetailsDay2)) {
					for (CIQDetailsModel ciqData : listOfCiqDetailsDay2) {
						if (ciqData.getCiqMap().containsKey("cell-identity") && ciqData.getCiqMap().get("cell-identity")
								.getHeaderValue().trim().equals(tdData.get("cell-identity").trim())) {

							if (ciqData.getCiqMap().containsKey("FR1_SolutionCell")) {
								frSolution = ciqData.getCiqMap().get("FR1_SolutionCell").getHeaderValue().trim();
							}
						}
					}
				}

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();

					if (key.equals("power") && frSolution.toLowerCase().contains("cleannr") 
							&& !(tdData.get("cell-identity").trim().equals("RF4440d-13A")||tdData.get("cell-identity").trim().equals("RF4439d-25A")) ) {
						if (NumberUtils.isNumber(value)) {
							Double nwPower = Double.parseDouble(value);
							if (nwPower <= dbPower) {
								tableData.append("<td align=center>" + value + "</td>\n");
							} else {
								tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
								auditIssue1.append("cell-identity :" + tdData.get("cell-identity") + " cell-num :"
										+ tdData.get("cell-num") + " power :" + tdData.get("power") + " o-ran-ru-id : "
										+ tdData.get("o-ran-ru-id") + " product-code : " + tdData.get("product-code")
										+ "\n");
							}
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}

					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}
				}
				tableData.append("</tr>\n");

			}

			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
			if (auditIssue1.length() != 0) {
				audit5GDSSSummaryService.createAudit5GDSSSummaryEntity(95, runTestEntity.getId(),
						enbId.replaceAll("^0+(?!$)", ""), auditIssue1.toString());
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;

	}

	public StringBuilder getUsmHarwaareTable(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			htmlContent
					.append(get4GFcchardwareTable(fullOutputLog, command, enbId, dbcollectionFileName, runTestEntity));
			if(fullOutputLog.contains(XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO)) {
			htmlContent.append(get4GFccSecondTable(fullOutputLog, XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO, enbId,
					dbcollectionFileName, runTestEntity));
			}
			if(fullOutputLog.contains(XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO+"50")) {
			htmlContent.append(get4GFccFirstTable(fullOutputLog, XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO, enbId,
					dbcollectionFileName, runTestEntity));
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return htmlContent;

	}

	private StringBuilder get4GFcchardwareTable(String fullOutputLog, String command, String enbId,
			String dbcollectionFileName, RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();

			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			// gnb-cu-cp-function
			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList nodeList1 = element.getChildNodes();

					for (int j = 0; j < nodeList1.getLength(); j++) {
						Node nodeList2 = nodeList1.item(j);
						if (Node.ELEMENT_NODE == nodeList2.getNodeType()) {

							Element element1 = (Element) nodeList2;
							NodeList nodeList3 = element1.getChildNodes();
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							for (int k = 0; k < nodeList3.getLength(); k++) {

								Node n1 = nodeList3.item(k);
								if (Node.ELEMENT_NODE == n1.getNodeType()) {
									headerList1.add(n1.getNodeName().trim());
									objtableData.put(n1.getNodeName().trim(), n1.getTextContent());
								}
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command  + "</b></td></tr>\n");
			

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String value = resultTableData.getValue();
					tableData.append("<td align=center>" + value + "</td>\n");
					
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			htmlContent.setLength(0);
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td  bgcolor=#EEEEEE><b>" + command + "</b></td></tr>\n");
			htmlContent.append("<tr>\n<td align=center bgcolor=FFFF00>NO DATA</td></tr>\n");
			htmlContent.append("</table>");
		}
		return htmlContent;
	}

	private StringBuilder get4GFccSecondTable(String fullOutputLog, String command, String enbId, String dbcollectionFileName,
			RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command );
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cbsd-index");
			headerList1.add("cbsd-id");
			headerList1.add("cbsd-state");
			headerList1.add("user-id");
			headerList1.add("fcc-id");
			headerList1.add("connected-ru");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("cbrs-function");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("cbsd-info");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

									Iterator<String> itr = headerList1.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild2, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild2, header));
									}

									tabelData1.add(objtableData);
								}
							}
						}
					}

				}
			}
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append("<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command + "-BEFORE"
					+ "</b></td></tr>\n");			
			List<String> listOfRuwithwithdate = getCBRSTablewithdate(fullOutputLog, XmlCommandsConstants.AUDIT_4G_FCC_RETRIEVE_RADIO_UNIT_INVENTORY, enbId,
					dbcollectionFileName);
			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					String s2=tdData.get("connected-ru");
					s2=StringUtils.substringAfter(s2, "ru-");
					if (key.equals("fcc-id") ) {
						if (value.equals("A3LRT4401-48A1") && listOfRuwithwithdate.contains(s2)) {
							tableData.append("<td align=center>" + value + "</td>\n");							
						}else if(value.equals("A3LRT4401-48A") && !listOfRuwithwithdate.contains(s2)){
							tableData.append("<td align=center>" + value + "</td>\n");
							
						} else {
							tableData.append("<td align=center bgcolor ='#fa8c8c'>" + value + "</td>\n");
						}

					} else {
						tableData.append("<td align=center>" + value + "</td>\n");
					}

				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return htmlContent;
	}
	private  List<String> getCBRSTablewithdate(String fullOutputLog, String command, String neId,
			String dbcollectionFileName) {
		List<String> listOfRus=new ArrayList<>();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList nodeList1 = element.getChildNodes();

					for (int j = 0; j < nodeList1.getLength(); j++) {
						Node nodeList2 = nodeList1.item(j);
						if (Node.ELEMENT_NODE == nodeList2.getNodeType()) {

							Element element1 = (Element) nodeList2;
							NodeList nodeList3 = element1.getChildNodes();
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							for (int k = 0; k < nodeList3.getLength(); k++) {

								Node n1 = nodeList3.item(k);
								if (Node.ELEMENT_NODE == n1.getNodeType()) {
									headerList1.add(n1.getNodeName().trim());
									objtableData.put(n1.getNodeName().trim(), n1.getTextContent());
								}
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			for (LinkedHashMap<String, String> tdData : tabelData1) {

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					boolean hardwareDate = false;
					try {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date sCreationDate = dateFormat.parse(tdData.get("manufactured-date"));
						Date sCreationDate2 = dateFormat.parse("2022-05-03");
						hardwareDate = sCreationDate.after(sCreationDate2);
					} catch (Exception e) {
						hardwareDate=false;
					}
					if (key.equals("hardware-name") && hardwareDate) {
						if (value.equals("RT4401-48A")) {
							String s= tdData.get("unit-id");
							s=StringUtils.substringBetween(s, "[", "]");
							s=s.replaceAll("_", "-");
							listOfRus.add(s);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return listOfRus;
	}
	private StringBuilder get4GFccFirstTable(String fullOutputLog, String command, String enbId, String dbcollectionFileName,
			RunTestEntity runTestEntity) {
		StringBuilder htmlContent = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command+ "50");
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("cbsd-index");
			headerList1.add("cbsd-id");
			headerList1.add("cbsd-state");
			headerList1.add("user-id");
			headerList1.add("fcc-id");
			headerList1.add("connected-ru");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("cbrs-function");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("cbsd-info");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

									Iterator<String> itr = headerList1.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild2, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild2, header));
									}

									tabelData1.add(objtableData);
								}
							}
						}
					}

				}
			}
			htmlContent.append(
					"<br><br><table cellspacing=0 cellpadding=5 border=1 bordercolor=#000000 style=\"min-width: 100%;table-layout: fixed;white-space: pre;\">\n");
			htmlContent.append(
					"<tr><td colspan=" + headerList1.size() + " bgcolor=#EEEEEE><b>" + command+"-AFTER"  + "</b></td></tr>\n");
			

			String tableHeader = "";
			tableHeader = tableHeader + "<tr>\n";
			for (String headerName : headerList1) {
				tableHeader = tableHeader + "<th align=center>" + headerName + "</th>\n";
			}

			tableHeader = tableHeader + "</tr>\n";
			StringBuilder tableData = new StringBuilder();
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				tableData.append("<tr>\n");

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String value = resultTableData.getValue();
					tableData.append("<td align=center>" + value + "</td>\n");
					
				}
				tableData.append("</tr>\n");

			}
			htmlContent.append(tableHeader);
			htmlContent.append(tableData);
			htmlContent.append("</table>\n");
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return htmlContent;
	}
}
