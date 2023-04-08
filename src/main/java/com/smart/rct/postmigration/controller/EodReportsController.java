package com.smart.rct.postmigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.dto.SchedulingSRDto;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.postmigration.service.SchedulingSRService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class EodReportsController {

	final static Logger logger = LoggerFactory.getLogger(EodReportsController.class);

	@Autowired
	SchedulingSRDto schedulingDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	SchedulingSRService schedulingService;

	/**
	 * This method will save Scheduling Details to DB
	 * 
	 * @param schedulingDetails
	 * @return resultMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = Constants.SAVE_EOD_REPORTS_DETAILS)
	public JSONObject saveEodReportsDetails(@RequestBody JSONObject eodReportsDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		try {
			sessionId = eodReportsDetails.get("sessionId").toString();
			serviceToken = eodReportsDetails.get("serviceToken").toString();
			customerId = Integer.valueOf(eodReportsDetails.get("customerId").toString());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				SchedulingVerizonModel schedulingVerizonModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) eodReportsDetails.get("eodReportsDetails")),
						SchedulingVerizonModel.class);
				SchedulingVerizonEntity schedulingVerizonEntity = schedulingDto
						.getVerizonEodEntity(schedulingVerizonModel);
				if (schedulingVerizonEntity != null) {
					if (schedulingService.saveVerizonSchedulingDetails(schedulingVerizonEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingVerizonModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION,
									Constants.EVENT_POST_MIGRATION_EOD_REPORTS, Constants.ACTION_SAVE,
									"Verizon EOD Reports Details Saved Successfully", sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION,
									Constants.EVENT_POST_MIGRATION_EOD_REPORTS, Constants.ACTION_UPDATE,
									"Verizon EOD Reports Details Updated Successfully", sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				SchedulingSprintModel schedulingSprintModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) eodReportsDetails.get("eodReportsDetails")),
						SchedulingSprintModel.class);
				SchedulingSprintEntity schedulingSprintEntity = schedulingDto.getSprintEodEntity(schedulingSprintModel);
				if (schedulingSprintEntity != null) {
					if (schedulingService.saveSprintSchedulingDetails(schedulingSprintEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingSprintModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION,
									Constants.EVENT_POST_MIGRATION_EOD_REPORTS, Constants.ACTION_SAVE,
									"Sprint EOD Reports Details Saved Successfully", sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION,
									Constants.EVENT_POST_MIGRATION_EOD_REPORTS, Constants.ACTION_UPDATE,
									"Sprint EOD Reports Details Updated Successfully", sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error(
					"Exception in SchedulingSRController.saveEodReports(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
	}

	/**
	 * This api deletes the Eod details
	 * 
	 * @param eodReportDetails
	 * @return resultMap
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_EOD_REPORTS_DETAILS)
	public JSONObject deleteEodReportsDetails(@RequestBody JSONObject deleteEodReports) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String id = null;
		Integer customerId = null;
		try {
			sessionId = deleteEodReports.get("sessionId").toString();
			serviceToken = deleteEodReports.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			id = deleteEodReports.get("id").toString();
			customerId = Integer.valueOf(deleteEodReports.get("customerId").toString());
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteVerizonDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_EOD_REPORTS,
							Constants.ACTION_DELETE, "Verizon EOD Reports Details Deleted Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteSprintDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_EOD_REPORTS,
							Constants.ACTION_DELETE, "Sprint EOD Reports Details Updated Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in SchedulingSRController.deleteEodReportDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api get the Scheduling Details
	 * 
	 * @param eodReportDetails
	 * @return resultMap
	 */

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_EOD_REPORTS_DETAILS)
	public JSONObject getEodReportsDetails(@RequestBody JSONObject eodReportsDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		Integer customerId = null;
		String searchStatus = null;
		SchedulingVerizonModel schedulingVerizonModel = null;
		SchedulingSprintModel schedulingSprintModel = null;
		List<SchedulingVerizonEntity> schedulingVerizonEntity = null;
		List<SchedulingSprintEntity> schedulingSprintEntity = null;
		List<SchedulingVerizonModel> schedulingVerizonModelList = null;
		List<SchedulingSprintModel> schedulingSprintModelList = null;
		try {
			sessionId = eodReportsDetails.get("sessionId").toString();
			serviceToken = eodReportsDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = eodReportsDetails.get("searchStatus").toString();
			customerId = Integer.valueOf(eodReportsDetails.get("customerId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) eodReportsDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					schedulingVerizonModel = new Gson().fromJson(
							eodReportsDetails.toJSONString((Map) eodReportsDetails.get("searchDetails")),
							SchedulingVerizonModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService
						.getVerizonEodDetails(schedulingVerizonModel, page, count, customerId);
				schedulingVerizonEntity = (List<SchedulingVerizonEntity>) schedulingDetailsObj
						.get("schedulingVerizonEntityList");
				if (CommonUtil.isValidObject(schedulingVerizonEntity)) {
					schedulingVerizonModelList = new ArrayList<SchedulingVerizonModel>();
					for (SchedulingVerizonEntity schedulingEntity : schedulingVerizonEntity) {
						schedulingVerizonModelList.add(schedulingDto.getSchedulingVerizonModel(schedulingEntity));
					}
				}
				resultMap.put("comboBoxListDetails", schedulingDetailsObj.get("comboBoxListDetails"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("enodebId", schedulingDetailsObj.get("enodebId"));
				resultMap.put("enodebName", schedulingDetailsObj.get("enodebName"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("eodVerizonModelList", schedulingVerizonModelList);
				resultMap.put("status", Constants.SUCCESS);
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					schedulingSprintModel = new Gson().fromJson(
							eodReportsDetails.toJSONString((Map) eodReportsDetails.get("searchDetails")),
							SchedulingSprintModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService.getSprintEodDetails(schedulingSprintModel,
						page, count, customerId);
				schedulingSprintEntity = (List<SchedulingSprintEntity>) schedulingDetailsObj
						.get("schedulingSprintEntityList");
				if (CommonUtil.isValidObject(schedulingSprintEntity)) {
					schedulingSprintModelList = new ArrayList<SchedulingSprintModel>();
					for (SchedulingSprintEntity schedulingEntity : schedulingSprintEntity) {
						schedulingSprintModelList.add(schedulingDto.getSchedulingSprintModel(schedulingEntity));
					}
				}
				resultMap.put("regionDetailsList", schedulingDetailsObj.get("regionDetailsList"));
				resultMap.put("marketDetailsList", schedulingDetailsObj.get("comboBoxListDetails"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("region", schedulingDetailsObj.get("region"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("eodSprintEntity", schedulingSprintModelList);
				resultMap.put("status", Constants.SUCCESS);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in SchedulingSRController.getSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api export the Scheduling Details into excel
	 * 
	 * @param schedulingExportDetails
	 * @return void
	 */
	@RequestMapping(value = Constants.EXPORT_EOD_REPORTS_DETAILS, method = RequestMethod.POST)
	public void exportEodReportsDetails(@RequestBody JSONObject eodExportDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		SchedulingSprintModel schedulingSprintModel = null;
		String searchStatus = null;
		SchedulingVerizonModel schedulingVerizonModel = null;
		try {
			sessionId = eodExportDetails.get("sessionId").toString();
			serviceToken = eodExportDetails.get("serviceToken").toString();
			response.setHeader("sessionId", sessionId);
			response.setHeader("serviceToken", serviceToken);

			if (StringUtils.isNotEmpty(eodExportDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(eodExportDetails.get("customerId").toString());
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (eodExportDetails.get("searchStatus")!=null && !"".equals(eodExportDetails.get("searchStatus"))) {
					searchStatus = eodExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingVerizonModel = new Gson().fromJson(
								eodExportDetails.toJSONString((Map) eodExportDetails.get("searchDetails")),
								SchedulingVerizonModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingVerizonModel = new SchedulingVerizonModel();
					}
				}else {
					schedulingVerizonModel = new SchedulingVerizonModel();
				}
				
				//if (schedulingService.getEodVerizonToCreateExcel(new SchedulingVerizonModel())) {
				if (schedulingService.getEodVerizonToCreateExcel(schedulingVerizonModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.EOD_REPORTS_DETAILS))
							.append(Constants.EOD_REPORTS_VERIZON_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_EOD_REPORTS,
							Constants.ACTION_EXPORT, "Verizon EOD Reports Details Exported Successfully", sessionId);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (eodExportDetails.get("searchStatus")!=null && !"".equals(eodExportDetails.get("searchStatus"))) {
					searchStatus = eodExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingSprintModel = new Gson().fromJson(
								eodExportDetails.toJSONString((Map) eodExportDetails.get("searchDetails")),
								SchedulingSprintModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingSprintModel = new SchedulingSprintModel();
					}
				}else {
					schedulingSprintModel = new SchedulingSprintModel();
				}
				
				//if (schedulingService.getEodSprintToCreateExcel(new SchedulingSprintModel())) {
				if (schedulingService.getEodSprintToCreateExcel(schedulingSprintModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.EOD_REPORTS_DETAILS))
							.append(Constants.EOD_REPORTS_SPRINT_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_EOD_REPORTS,
							Constants.ACTION_EXPORT, "Sprint EOD Reports Details Exported Successfully", sessionId);
				}
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;
			}
		} catch (Exception e) {
			logger.error("Exception in SchedulingSRControllerexportSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
			return;
		}
		return;
	}

}
