package com.smart.rct.postmigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.dto.ReportsDto;
import com.smart.rct.postmigration.dto.SchedulingSRDto;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.postmigration.service.ReportsService;
import com.smart.rct.postmigration.service.SchedulingSRService;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class ReportsController {

	final static Logger logger = LoggerFactory.getLogger(OverallReportsController.class);

	@Autowired
	SchedulingSRDto schedulingDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	SchedulingSRService schedulingService;
	
	@Autowired
	ReportsService reportsService;

	@Autowired
	ReportsDto reportsDto;

	/**
	 * This api will getOverallReportsDetails
	 * 
	 * @param overallReportsDetails
	 * @return JSONObject
	 */

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = "/getReports")
	public JSONObject getReportsDetails(@RequestBody JSONObject overallReportsDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		Integer customerId = null;
		String pgname = null;
		String searchStatus = null;
		ReportsModel reportsModel=null;
		List<JSONObject> list = new ArrayList<>();
		List<String> filter = null;
		try {
			sessionId = overallReportsDetails.get("sessionId").toString();
			serviceToken = overallReportsDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = overallReportsDetails.get("searchStatus").toString();
			customerId = Integer.valueOf(overallReportsDetails.get("customerId").toString());
			pgname= (String) overallReportsDetails.get("programName");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) overallReportsDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					reportsModel = new Gson().fromJson(
							overallReportsDetails.toJSONString((Map) overallReportsDetails.get("searchDetails")),
							ReportsModel.class);
				}
				Map<String, Object> detailsObj = reportsService.getReportsDetails(customerId, page, count,pgname,reportsModel);
				List<ReportsEntity > entity =  (List<ReportsEntity>) detailsObj.get("runTestEntity");
				if (CommonUtil.isValidObject(entity)) {
					for (ReportsEntity reportsEntity : entity) {
						JSONObject map = reportsDto.getReportsDetailsModel(reportsEntity,pgname,filter);
						list.add(map);
					}
				}
				
				resultMap.put("overallVerizonModelList", list);
				resultMap.put("pageCount", detailsObj.get("paginationcount"));
				resultMap.put("Mm", Constants. REPORTS_COLUMNS_5G);
				resultMap.put("Dss", Constants. REPORTS_COLUMNS_DSS);
				resultMap.put("program_4g", Constants. REPORTS_COLUMNS_4G);
				resultMap.put("program_4g_fsu", Constants. REPORTS_COLUMNS_4G_FSU);
				resultMap.put("All",Constants. REPORTS_COLUMNS_ALL);
				resultMap.put("status", Constants.SUCCESS);
			}
			
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			//resultMap.put("reason","Failed");
			logger.error("Exception in OverallReportsController.getOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	/**
	 * This api deletes the Overall report details
	 * 
	 * @param deleteOverallReportsDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/delete")
	public JSONObject deleteOverallReportsDetails(@RequestBody JSONObject deleteOverallReportsDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String id = null;
		Integer customerId = null;
		try {
			sessionId = deleteOverallReportsDetails.get("sessionId").toString();
			serviceToken = deleteOverallReportsDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			id = deleteOverallReportsDetails.get("id").toString();
			customerId = Integer.valueOf(deleteOverallReportsDetails.get("customerId").toString());
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteVerizonDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
							Constants.ACTION_DELETE, "Verizon Overall Reports  Details Deleted Successfully",
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteSprintDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
							Constants.ACTION_DELETE, "Sprint Overall Reports  Details Deleted Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in OverallReportsController.deleteOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	/**
	 * This api will exportOverallDetails into excel
	 * 
	 * @param schedulingExportDetails
	 * @return void
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public void exportOverallDetails(@RequestBody JSONObject OverallExportDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String searchStatus =null;
		String pgname = null;
		ReportsModel reportsModel=null;
		List<String> filter = null;
		ReportsModel schedulingVerizonModel = null;
		SchedulingSprintModel schedulingSprintModel = null;
		try {
			sessionId = OverallExportDetails.get("sessionId").toString();
			serviceToken = OverallExportDetails.get("serviceToken").toString();
			response.setHeader("sessionId", sessionId);
			response.setHeader("serviceToken", serviceToken);
			filter = (List<String>) OverallExportDetails.get("filter");
			Map<String, Integer> paginationData = (Map<String, Integer>) OverallExportDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			pgname= (String) OverallExportDetails.get("programName");
			if (StringUtils.isNotEmpty(OverallExportDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(OverallExportDetails.get("customerId").toString());
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (OverallExportDetails.get("searchStatus")!=null && !"".equals(OverallExportDetails.get("searchStatus"))) {
					searchStatus = OverallExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						reportsModel = new Gson().fromJson(
								OverallExportDetails.toJSONString((Map) OverallExportDetails.get("searchDetails")),
								ReportsModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingVerizonModel = new ReportsModel();
					}
				}else {
					schedulingVerizonModel = new ReportsModel();
				}
				
				//if (schedulingService.getOverallDetailsToCreateExcel(new SchedulingVerizonModel())) {
				if (reportsService.getDetailsToCreateExcel(customerId,page,count,pgname,reportsModel,filter,"download")) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(Constants.OVERALL_DETAILS);
					String zipFilepath = filePath.toString() + ".zip";
					boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath, filePath.toString());
					if (!status) {
						logger.info("downloadLogFileWFM() file not found:" + zipFilepath);
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						out.println("{\"status\": \"File Not Found\"}");
						out.close();
						return;
					}
					downloadFile = new File(zipFilepath);
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
					if (CommonUtil.isValidObject(filePath) && CommonUtil.isValidObject(zipFilepath)) {
						FileUtil.deleteFileOrFolder(filePath.toString());
						FileUtil.deleteFileOrFolder(zipFilepath);
					}	
				}
			}
			 else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;
			}
		} catch (Exception e) {
			logger.error("Exception in OverallReportsController.exportOverallDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
			return;
		}
		return;
	}


}
