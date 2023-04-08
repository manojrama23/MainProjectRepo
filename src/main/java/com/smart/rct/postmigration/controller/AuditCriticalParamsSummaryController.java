package com.smart.rct.postmigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.postmigration.dto.AuditAuditCriticalParamsSummaryDto;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.models.AuditCriticalParamsSummaryModel;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class AuditCriticalParamsSummaryController {
	final static Logger logger = LoggerFactory.getLogger(AuditCriticalParamsSummaryController.class);

	@Autowired
	AuditAuditCriticalParamsSummaryDto auditCriticalParamsSummaryDto;
	
	@Autowired
	AuditCriticalParamsService auditCriticalParamsSummaryService;
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	CustomerService customerService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.AUDIT_CRITICAL_PARAMS_SUMMARY_DETAILS, method = RequestMethod.POST)
	public JSONObject getAuditCriticalParamsSummaryReport(@RequestBody JSONObject auditCriticalSummaryReportDetails) {
		
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String fromDate = "";
		String toDate = "";
		String neId = null;
		String neName = null;
		String programName = "";
		String siteName = "";
		String searchStatus = "";
		int runTestId;
		
		List<AuditCriticalParamsSummaryModel> auditStatusEntities= null;
		try {
			sessionId = auditCriticalSummaryReportDetails.get("sessionId").toString();
			serviceToken = auditCriticalSummaryReportDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			
			Map<String, Integer> paginationData = (Map<String, Integer>) auditCriticalSummaryReportDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel = new Gson().fromJson(
					JSONObject.toJSONString((Map) auditCriticalSummaryReportDetails.get("searchCriteria")),
					AuditCriticalParamsSummaryModel.class);
			Map<String, Object> auditStatusList = auditCriticalParamsSummaryService
					.getAuditCriticalParamsSearchSummaryEntityList(auditCriticalParamsSummaryModel, page, count);
			resultMap.put("pageCount", auditStatusList.get("pageCount"));
			auditStatusEntities = (List<AuditCriticalParamsSummaryModel>) auditStatusList.get("auditStatusList");

			resultMap.put("auditStatusDetails", auditStatusEntities);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error( ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to Get Audit Critical Parameter Details");
		}
		return resultMap;
	}
	 @RequestMapping(value = Constants.EXPORT_AUDIT_CRITICAL_PARAMS_DETAILS, method = RequestMethod.POST)
		public void getAuditCriticalParamsBulkReport(@RequestBody JSONObject auditCriticalSummaryReportDetails, HttpServletResponse response)
				throws IOException {
			FileInputStream inputStream = null;
			OutputStream outStream = null;
			StringBuilder filePath = new StringBuilder();
			File downloadFile = null;
			String sessionId = null;
			String serviceToken = null;
			Integer customerId = null;
			List<CustomerDetailsEntity> programNamesList = null;
			AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel = null;
			String searchStatus = null;
			try {
				sessionId = auditCriticalSummaryReportDetails.get("sessionId").toString();
				serviceToken = auditCriticalSummaryReportDetails.get("serviceToken").toString();
				Map<String, Integer> paginationData = (Map<String, Integer>) auditCriticalSummaryReportDetails.get("pagination");
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				if (user.getRoleId() <= 3) {
					CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
					CustomerEntity customerEntity = new CustomerEntity();
					customerEntity.setId(customerId);
					customerDetailsModel.setCustomerEntity(customerEntity);
					programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
				} else {
					programNamesList = customerService.getProgramDetailsList(user);
				}
				List<String> programs = programNamesList.stream().map(x -> x.getProgramName()).collect(Collectors.toList());
				
				auditCriticalParamsSummaryModel = new Gson().fromJson(JSONObject.toJSONString((Map) auditCriticalSummaryReportDetails.get("searchDetails")), AuditCriticalParamsSummaryModel.class);

				if (auditCriticalParamsSummaryService.createAuditCriticalParamsBulkReportExcel(auditCriticalSummaryReportDetails)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.AUDIT_CRITICAL_PARAMS_DETAILS))
							.append(Constants.AUDIT_CRITICAL_PARAMS_SUMMARY_XLSX);
					downloadFile = new File(filePath.toString());
					if (!downloadFile.exists()) {
						logger.info("downloadFile() file not found:" + filePath.toString());
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
					response.setHeader("sessionId", auditCriticalSummaryReportDetails.get("sessionId").toString());
					response.setHeader("serviceToken", auditCriticalSummaryReportDetails.get("serviceToken").toString());
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
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_AUDIT_CRITICAL_PARAMS,
							Constants.ACTION_EXPORT, "Audit Critical Param Details File Exported Successfully", sessionId);
				} else {
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \""
							+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_AUDIT_CRITICAL_PARAMS_REPORT)
							+ "\"}");
					out.close();
					return;
				}
			} catch (Exception e) {
				logger.error("Exception in AuditCriticalParamsSummaryController.getAuditCriticalParamsBulkReport(): "
						+ ExceptionUtils.getFullStackTrace(e));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \""
						+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_AUDIT_CRITICAL_PARAMS_REPORT) + "\"}");
				out.close();
				return;
			}
			
			return;
	 }
}
