package com.smart.rct.postmigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.postmigration.dto.Audit4GFsuSummaryDto;
import com.smart.rct.postmigration.dto.Audit4GSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GCBandSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GDSSSummaryDto;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSuccessSummaryModel;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.Audit5GDSSPassFailModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.premigration.controller.UploadCIQController;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class Audit4GSummaryController {
	final static Logger logger = LoggerFactory.getLogger(Audit4GSummaryController.class);
	
	@Autowired
	Audit4GSummaryService audit4GSummaryService;
	
	@Autowired
	Audit4GFsuSummaryService audit4GFsuSummaryService;
	
	@Autowired
	Audit5GCBandSummaryService audit5GCBandSummaryService;
	
	@Autowired
	Audit5GDSSSummaryService audit5GDSSSummaryService;
	
	@Autowired
	Audit4GSummaryDto audit4GSummaryDto;
	
	@Autowired
	Audit5GCBandSummaryDto audit5GCBandSummaryDto;
	
	@Autowired
	Audit5GDSSSummaryDto audit5GDSSSummaryDto;
	@Autowired
	Audit4GFsuSummaryDto audit4GFsuSummaryDto;
		
	@Autowired
	RunTestService runTestService;
	
	@Autowired
	UploadCIQController uploadCIQController;
	
	@Autowired
	WorkFlowManagementService workFlowManagementService;

	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getAudit4GSummaryReport")
	public JSONObject getAudit4GSummaryReport(@RequestBody JSONObject audit4GSummaryReportDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String neName = null;
		String neId = null;
		String programName = "";
		try {
			sessionId = audit4GSummaryReportDetails.get("sessionId").toString();
			serviceToken = audit4GSummaryReportDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			programName = audit4GSummaryReportDetails.get("programName").toString();
			neName = audit4GSummaryReportDetails.get("neName").toString();
			resultMap.put("neName", neName);
			int runTestId = Integer.parseInt(audit4GSummaryReportDetails.get("runTestId").toString());
			
			List<Audit4GSummaryModel> audit4GSummaryModelList = new ArrayList<>();
			List<Audit4GSuccessSummaryModel> audit4GSuccessSummaryModelList = new ArrayList<>();
			if(programName.contains("4G-USM-LIVE")) {
				List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByRunTestId(runTestId);
				
				if(audit4GSummaryEntityList!=null && !audit4GSummaryEntityList.isEmpty()) {
					neId = audit4GSummaryEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				audit4GSummaryModelList = audit4GSummaryDto.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
			} else if (programName.contains("5G-CBAND")) {
				List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = audit5GCBandSummaryService.getAudit5GCBandSummaryEntityListByRunTestId(runTestId);
				
				if(audit5GCBandSummaryEntityList!=null && !audit5GCBandSummaryEntityList.isEmpty()) {
					neId = audit5GCBandSummaryEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				audit4GSummaryModelList = audit5GCBandSummaryDto.getAudit5GCBandSummaryReportModelList(audit5GCBandSummaryEntityList);
			} else if (programName.contains("5G-DSS")) {
				List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSSummaryEntityListByRunTestId(runTestId);
				
				if(audit5GDSSSummaryEntityList!=null && !audit5GDSSSummaryEntityList.isEmpty()) {
					neId = audit5GDSSSummaryEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				audit4GSummaryModelList = audit5GDSSSummaryDto.getAudit5GDSSSummaryReportModelList(audit5GDSSSummaryEntityList);
			}else if(programName.contains("4G-FSU")) {
				List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuSummaryEntityListByRunTestId(runTestId);
			//	List<Audit4GFsuSuccessSummaryEntity> audit4GFsuSuccessSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuSuccessSummaryEntityListByRunTestId(runTestId);
		
				if(audit4GFsuSummaryEntityList!=null && !audit4GFsuSummaryEntityList.isEmpty()) {
					neId = audit4GFsuSummaryEntityList.get(0).getNeId();
				} 
			
				 
				resultMap.put("neId", neId);
				audit4GSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuSummaryReportModelList(audit4GFsuSummaryEntityList);
			//	audit4GSuccessSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuPassSummaryReportModelList(audit4GFsuSuccessSummaryEntityList);
			} 
			
			
			resultMap.put("postAuditIssues", audit4GSummaryModelList);
			//resultMap.put("postAuditSuccess", audit4GSuccessSummaryModelList);
			resultMap.put("status", Constants.SUCCESS);
			
 		} catch(Exception e) {
			logger.error("Exception Audit4GSummaryController in getAudit4GSummaryReport() " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", "Failed to Fetch Summary Report");
		}
		return resultMap;
	}
	
 	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getAudit4GPassFailSummaryReport")
	public JSONObject getAudit4GPassFailEachId(@RequestBody JSONObject audit4GSummaryDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String neName = null;
		String neId = null;
		String programName = "";
		String userName = "";
		
		try {
			sessionId = audit4GSummaryDetails.get("sessionId").toString();
			serviceToken = audit4GSummaryDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			
			  if (expiryDetails != null) { 
				  return expiryDetails; 
				  }
			 
			programName = audit4GSummaryDetails.get("programName").toString();
			userName = audit4GSummaryDetails.get("userName").toString();
			neName = audit4GSummaryDetails.get("neName").toString();
			resultMap.put("neName", neName);
			int runId = Integer.parseInt(audit4GSummaryDetails.get("runTestId").toString());
			
			
			Set<String> headerNames=new HashSet<>();
			List<AuditPassFailSummaryModel> auditPassFailSummaryModelList = new ArrayList<>();
			
			if (programName.contains("4G-FSU")) {
				List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = getSingleRunTestData(runId);

				if(audit4GFsuPassFailSummaryEntityList!=null && !audit4GFsuPassFailSummaryEntityList.isEmpty()) {
					neId = audit4GFsuPassFailSummaryEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				headerNames = audit4GFsuPassFailSummaryEntityList.stream()
						.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getTestName()).collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuPassFailSummaryReportModelList(audit4GFsuPassFailSummaryEntityList, programName,userName);
			} else if(programName.contains("4G-USM-LIVE")) {
				List<Audit4GPassFailEntity> audit4GPassFailSummaryEntityList = get4GSingleRunTestData(runId);
				
				if(audit4GPassFailSummaryEntityList!=null && !audit4GPassFailSummaryEntityList.isEmpty()) {
					neId = audit4GPassFailSummaryEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				headerNames = audit4GPassFailSummaryEntityList.stream()
						.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getTestName()).collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit4GSummaryDto.getAudit4GPassFailReportModelList(audit4GPassFailSummaryEntityList, programName,userName);

			} else if (programName.contains("5G-DSS")) {
				List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailModelList = get5GDSSSingleRunTestData(runId);
				
				if(audit5GDSSPassFailModelList!=null && !audit5GDSSPassFailModelList.isEmpty()) {
					neId = audit5GDSSPassFailModelList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				headerNames = audit5GDSSPassFailModelList.stream()
						.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getTestName()).collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit5GDSSSummaryDto.getAudit5GDSSPassFailReportModelList(audit5GDSSPassFailModelList, programName,userName);

			} else if (programName.contains("5G-CBAND")) {
				List<Audit5GCBandPassFailEntity> audit5GCBandPassFailModelList = get5GCBandSingleRunTestData(runId);
				
				if(audit5GCBandPassFailModelList!=null && !audit5GCBandPassFailModelList.isEmpty()) {
					neId = audit5GCBandPassFailModelList.get(0).getNeId();
				}
				resultMap.put("neId", neId);
				headerNames = audit5GCBandPassFailModelList.stream()
						.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getTestName()).collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit5GCBandSummaryDto.getAudit5GCBandPassFailReportModelList(audit5GCBandPassFailModelList, programName,userName);

			}
			
			resultMap.put("passfailStatus", auditPassFailSummaryModelList);
			resultMap.put("neheaders", headerNames);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			logger.error("Exception Audit4GSummaryController in getAudit4GSummaryReport() " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", "Failed to Fetch Summary Report");
		}
	
		
		return resultMap;
	}



	private List<Audit5GCBandPassFailEntity> get5GCBandSingleRunTestData(int runId) {
		List<Audit5GCBandPassFailEntity> audit5GCBandPassFailSummaryEntityList = audit5GCBandSummaryService.getAudit5GCBandPassFailsEntityEachRunId(runId);

		return audit5GCBandPassFailSummaryEntityList;
	}

	private List<Audit5GDSSPassFailSummaryEntity> get5GDSSSingleRunTestData(int runId) {
		List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSPassFailsEntityEachRunId(runId);
	
		return audit5GDSSPassFailSummaryEntityList;
	}

	private List<Audit4GFsuPassFailSummaryEntity> getSingleRunTestData(int runId) {
		List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuPassFailSummaryEntityEachRunId(runId);
		return audit4GFsuPassFailSummaryEntityList;
	} 

	private List<Audit4GPassFailEntity> get4GSingleRunTestData(int runId) {
		List<Audit4GPassFailEntity> audit4GsPassFailEntityList = audit4GSummaryService.getAudit4GPassFailEntityEachRunId(runId);
		return audit4GsPassFailEntityList;
	} 
	
	
	@RequestMapping(value = "/downloadAudit4GSummaryReport", method = RequestMethod.POST)
	public void downloadAudit4GSummaryReport(@RequestBody JSONObject audit4GSummaryReportDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String neId = null;
		String neName = null;
		String programName = "";
		try {
			neId = audit4GSummaryReportDetails.get("neId").toString();
			neName = audit4GSummaryReportDetails.get("neName").toString();
			filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH)).append(Constants.AUDIT_4G_SUMMARY_REPORT_FILEPATH)
				.append(neId);
			File fileDir = new File(filePath.toString());
			if(!fileDir.exists()) {
				fileDir.mkdir();
			}
			programName = audit4GSummaryReportDetails.get("programName").toString();
			String fileName = "";
			boolean status = false;
			if(programName.contains("4G-USM-LIVE")) {
				status = audit4GSummaryService.createAudit4GSummaryReportExcel(audit4GSummaryReportDetails, filePath.toString(), neName);
				fileName = "AUDIT_4G_SUMMARY_REPORT_";
			} else if (programName.contains("5G-CBAND")) {
				status = audit5GCBandSummaryService.createAudit5GCBandSummaryReportExcel(audit4GSummaryReportDetails, filePath.toString(), neName);
				fileName ="AUDIT_5GCBand_SUMMARY_REPORT_";
			} else if (programName.contains("5G-DSS")) {
				status = audit5GDSSSummaryService.createAudit5GDSSSummaryReportExcel(audit4GSummaryReportDetails, filePath.toString(), neName);
				fileName ="AUDIT_5GDSS_SUMMARY_REPORT_";
			}else if (programName.contains("4G-FSU")) {
				status = audit4GFsuSummaryService.createAudit4GFsuSummaryReportExcel(audit4GSummaryReportDetails, filePath.toString(), neName);
				fileName ="AUDIT_4G_FSU_SUMMARY_REPORT_";
			}
			if(status) {
				String xlsxFilePath = filePath.toString() + Constants.SEPARATOR + fileName + neName + ".xlsx";
				downloadFile = new File(xlsxFilePath);
				if(!downloadFile.exists()) {
					logger.info("downloadFile() file not found:" + xlsxFilePath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"Error While Downloading\"}");
					out.close();
					return;
				}
				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				if (CommonUtil.isValidObject(filePath)) {
					FileUtil.deleteFileOrFolder(filePath.toString());
				}
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;
			
			}
			
		} catch (Exception e) {
			logger.error("Exception in Audit4GSummaryController.downloadAudit4GSummaryReport(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
		}
	}
	

	@RequestMapping(value = "/downloadAudit4GPassFailSummaryReport", method = RequestMethod.POST)
	public void downloadAudit4GPassFailSummaryReport(@RequestBody JSONObject audit4GSummaryReportDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String neId = null;
		String neName = null;
		String programName = "";
		String runTestId = "";
		String user = "";
		int programId = Integer.parseInt(audit4GSummaryReportDetails.get("programId").toString());
		
		try {

			programName = audit4GSummaryReportDetails.get("programName").toString();
			
			user = audit4GSummaryReportDetails.get("userName").toString();
			String fileName = "";
			boolean status = false;
			
			 runTestId = audit4GSummaryReportDetails.get("runTestId").toString();
			System.out.println(runTestId);
			
			List<AuditPassFailSummaryModel> objRunTestModelList = new ArrayList<>();
						
			StringBuilder FileWritePath = new StringBuilder();
			
			  FileWritePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			  ) .append(Constants.CUSTOMER)
			  .append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR).
			  append("PostMigration/").append("SingleExcelReport"); 
			  File fileExists = new File(FileWritePath.toString()); 
			  if (fileExists.exists()) {
			  FileUtil.deleteFileOrFolder(FileWritePath.toString()); 
			  } 
			  if(!fileExists.exists()) { 
				  FileUtil.createDirectory(FileWritePath.toString());
			  }
		
			  
			if(programName.contains("4G-USM-LIVE")) {
				List<Audit4GPassFailEntity> singleRunTestData = get4GSingleRunTestData(Integer.parseInt(runTestId));
				objRunTestModelList = audit4GSummaryDto.getAudit4GPassFailReportModelList(singleRunTestData, programName, user);
				
				Set<String> headerNames = singleRunTestData.stream()
						.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getTestName()).collect(Collectors.toSet());
				JSONObject jsonObject1 = new JSONObject();
				
				jsonObject1.put("passfailStatus", objRunTestModelList);
				jsonObject1.put("neheaders", headerNames);
			
						
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1, FileWritePath.toString());
				fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
				
				}else if (programName.contains("4G-FSU")) {
				List<Audit4GFsuPassFailSummaryEntity> singleRunTestData = getSingleRunTestData(Integer.parseInt(runTestId));
				objRunTestModelList = audit4GFsuSummaryDto.getAudit4GFsuPassFailSummaryReportModelList(singleRunTestData, programName,user);
				
				Set<String> headerNames = singleRunTestData.stream()
						.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getTestName()).collect(Collectors.toSet());
				JSONObject jsonObject1 = new JSONObject();
				
				jsonObject1.put("passfailStatus",objRunTestModelList);
				jsonObject1.put("neheaders", headerNames);
				
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1, FileWritePath.toString());
				 fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
			} else if (programName.contains("5G-CBAND")) { 
				List<Audit5GCBandPassFailEntity> singleRunTestData = get5GCBandSingleRunTestData(Integer.parseInt(runTestId));
				objRunTestModelList = audit5GCBandSummaryDto.getAudit5GCBandPassFailReportModelList(singleRunTestData, programName,user);
				
				Set<String> headerNames = singleRunTestData.stream()
						.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getTestName()).collect(Collectors.toSet());
				JSONObject jsonObject1 = new JSONObject();
				
				jsonObject1.put("passfailStatus", objRunTestModelList);
				jsonObject1.put("neheaders", headerNames);
				
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1, FileWritePath.toString());
				 fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
			} else if (programName.contains("5G-DSS")) {
				List<Audit5GDSSPassFailSummaryEntity> singleRunTestData = get5GDSSSingleRunTestData(Integer.parseInt(runTestId));
				objRunTestModelList = audit5GDSSSummaryDto.getAudit5GDSSPassFailReportModelList(singleRunTestData, programName,user);
				
				Set<String> headerNames = singleRunTestData.stream()
						.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getTestName()).collect(Collectors.toSet());
				JSONObject jsonObject1 = new JSONObject();
				
				jsonObject1.put("passfailStatus", objRunTestModelList);
				jsonObject1.put("neheaders", headerNames);
				
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1, FileWritePath.toString());
				 fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
			}
				
			
			
			if(status) {
				String xlsxFilePath = FileWritePath.toString() + Constants.SEPARATOR + fileName ;
				downloadFile = new File(xlsxFilePath);
				if(!downloadFile.exists()) {
					logger.info("downloadFile() file not found:" + xlsxFilePath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"Error While Downloading\"}");
					out.close();
					return;
				}
				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				if (CommonUtil.isValidObject(FileWritePath)) {
					FileUtil.deleteFileOrFolder(FileWritePath.toString());
				}
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;
			
			}
			
		} catch (Exception e) {
			logger.error("Exception in Audit4GSummaryController.downloadAudit4GSummaryReport(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
		}
	}
	
	
	
	
	private void makeUnzip(String sourcezip, String destinationFilePath) throws IOException {

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(sourcezip));
		ZipEntry zipEntry = zis.getNextEntry();
		File destFile = new File(destinationFilePath);
		while (zipEntry != null) {
		     File newFile = newFile(destFile, zipEntry);
		     if (zipEntry.isDirectory()) {
		         if (!newFile.isDirectory() && !newFile.mkdirs()) {
		             throw new IOException("Failed to create directory " + newFile);
		         }
		     } else {
		         File parent = newFile.getParentFile();
		         if (!parent.isDirectory() && !parent.mkdirs()) {
		             throw new IOException("Failed to create directory " + parent);
		         }
		         
		         FileOutputStream fos = new FileOutputStream(newFile);
		         int len;
		         while ((len = zis.read(buffer)) > 0) {
		             fos.write(buffer, 0, len);
		         }
		         fos.close();
		     }
		 zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	public void getFiles(String migfilepath, String programId, String zipFileFolderpath, String folderName) throws IOException {

		// code for migration file copy
		if (migfilepath != null && !migfilepath.isEmpty()) {
			String filenames[] = migfilepath.split(",");
			String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			for (String name : filenames) {
				File zipFileDir = new File(zipFileFolderpath + Constants.SEPARATOR + folderName);
				if (!zipFileDir.exists()) {
					FileUtils.forceMkdir(zipFileDir);
				}
				if (CommonUtil.isValidObject(name) && name.trim().length() > 0) {
					File file = new File(filePath + name);
					FileUtils.copyFileToDirectory(file, zipFileDir);
				}
			}
		}
	}
	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}
}
