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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
public class OverallReportsController {

	final static Logger logger = LoggerFactory.getLogger(OverallReportsController.class);

	@Autowired
	SchedulingSRDto schedulingDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	SchedulingSRService schedulingService;

	/**
	 * This method will save Overall Reports Details to DB
	 * 
	 * @param overallDetails
	 * @return resultMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = Constants.SAVE_OVERALL_REPORTS_DETAILS)
	public JSONObject saveOverallReportsDetails(@RequestBody JSONObject overallDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		try {
			sessionId = overallDetails.get("sessionId").toString();
			serviceToken = overallDetails.get("serviceToken").toString();
			customerId = Integer.valueOf(overallDetails.get("customerId").toString());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				SchedulingVerizonModel schedulingVerizonModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) overallDetails.get("overallDetails")),
						SchedulingVerizonModel.class);
				SchedulingVerizonEntity schedulingVerizonEntity = schedulingDto
						.getVerizonOverallReportsEntity(schedulingVerizonModel);
				if (schedulingVerizonEntity != null) {
					if (schedulingService.saveVerizonSchedulingDetails(schedulingVerizonEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingVerizonModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
									Constants.ACTION_SAVE, "Verizon Overall Reports  Details Saved Successfully",
									sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
									Constants.ACTION_UPDATE, "Verizon Overall Reports  Details Updated Successfully",
									sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				SchedulingSprintModel schedulingSprintModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) overallDetails.get("overallDetails")),
						SchedulingSprintModel.class);
				SchedulingSprintEntity schedulingSprintEntity = schedulingDto
						.getSprintOverallReportsEntity(schedulingSprintModel);
				if (schedulingSprintEntity != null) {
					if (schedulingService.saveSprintSchedulingDetails(schedulingSprintEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingSprintModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
									Constants.ACTION_SAVE, "Sprint Overall Reports  Details Saved Successfully",
									sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
									Constants.ACTION_UPDATE, "Sprint Overall Reports  Details Updated Successfully",
									sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error(
					"Exception in OverallReportsController.saveEodReports(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
	}

	/**
	 * This api deletes the Overall report details
	 * 
	 * @param deleteOverallReportsDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_OVERALL_REPORTS_DETAILS)
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
	 * This api will getOverallReportsDetails
	 * 
	 * @param overallReportsDetails
	 * @return JSONObject
	 */

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_OVERALL_REPORTS_DETAILS)
	public JSONObject getOverallReportsDetails(@RequestBody JSONObject overallReportsDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		Integer customerId = null;
		String searchStatus = null;
		SchedulingVerizonModel overallVerizonModel = null;
		SchedulingSprintModel overallSprintModel = null;
		List<SchedulingVerizonEntity> overallVerizonEntity = null;
		List<SchedulingSprintEntity> overallSprintEntity = null;
		List<SchedulingVerizonModel> overallVerizonModelList = null;
		List<SchedulingSprintModel> overallSprintModelList = null;
		try {
			sessionId = overallReportsDetails.get("sessionId").toString();
			serviceToken = overallReportsDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = overallReportsDetails.get("searchStatus").toString();
			customerId = Integer.valueOf(overallReportsDetails.get("customerId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) overallReportsDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					overallVerizonModel = new Gson().fromJson(
							overallReportsDetails.toJSONString((Map) overallReportsDetails.get("searchDetails")),
							SchedulingVerizonModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService
						.getVerizonOverallReportsDetails(overallVerizonModel, page, count, customerId);
				overallVerizonEntity = (List<SchedulingVerizonEntity>) schedulingDetailsObj
						.get("overallVerizonEntityList");
				if (CommonUtil.isValidObject(overallVerizonEntity)) {
					overallVerizonModelList = new ArrayList<SchedulingVerizonModel>();
					for (SchedulingVerizonEntity overallEntity : overallVerizonEntity) {
						overallVerizonModelList.add(schedulingDto.getOverallVerizonModel(overallEntity));
					}
				}
				resultMap.put("comboBoxListDetails", schedulingDetailsObj.get("comboBoxListDetails"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("enodebId", schedulingDetailsObj.get("enodebId"));
				resultMap.put("enodebName", schedulingDetailsObj.get("enodebName"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("overallVerizonModelList", overallVerizonModelList);
				resultMap.put("status", Constants.SUCCESS);
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					overallSprintModel = new Gson().fromJson(
							overallReportsDetails.toJSONString((Map) overallReportsDetails.get("searchDetails")),
							SchedulingSprintModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService
						.getSprintOverallReportsDetails(overallSprintModel, page, count, customerId);
				overallSprintEntity = (List<SchedulingSprintEntity>) schedulingDetailsObj
						.get("overallSprintEntityList");
				if (CommonUtil.isValidObject(overallSprintEntity)) {
					overallSprintModelList = new ArrayList<SchedulingSprintModel>();
					for (SchedulingSprintEntity overallEntity : overallSprintEntity) {
						overallSprintModelList.add(schedulingDto.getSchedulingSprintModel(overallEntity));
					}
				}
				resultMap.put("marketDetailsList", schedulingDetailsObj.get("marketDetailsList"));
				resultMap.put("regionDetailsList", schedulingDetailsObj.get("regionDetailsList"));
				resultMap.put("feregionDetailsList", schedulingDetailsObj.get("feregionDetailsList"));
				resultMap.put("fenightDetailsList", schedulingDetailsObj.get("fenightDetailsList"));
				resultMap.put("fedayDetailsList", schedulingDetailsObj.get("fedayDetailsList"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("region", schedulingDetailsObj.get("region"));
				resultMap.put("enodebId", schedulingDetailsObj.get("enodebId"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("overallSprintEntity", overallSprintModelList);
				resultMap.put("status", Constants.SUCCESS);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in OverallReportsController.getOverallReportsDetails(): "
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
	@RequestMapping(value = Constants.EXPORT_OVERALL_DETAILS, method = RequestMethod.POST)
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
		SchedulingVerizonModel schedulingVerizonModel = null;
		SchedulingSprintModel schedulingSprintModel = null;
		try {
			sessionId = OverallExportDetails.get("sessionId").toString();
			serviceToken = OverallExportDetails.get("serviceToken").toString();
			response.setHeader("sessionId", sessionId);
			response.setHeader("serviceToken", serviceToken);

			if (StringUtils.isNotEmpty(OverallExportDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(OverallExportDetails.get("customerId").toString());
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (OverallExportDetails.get("searchStatus")!=null && !"".equals(OverallExportDetails.get("searchStatus"))) {
					searchStatus = OverallExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingVerizonModel = new Gson().fromJson(
								OverallExportDetails.toJSONString((Map) OverallExportDetails.get("searchDetails")),
								SchedulingVerizonModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingVerizonModel = new SchedulingVerizonModel();
					}
				}else {
					schedulingVerizonModel = new SchedulingVerizonModel();
				}
				
				//if (schedulingService.getOverallDetailsToCreateExcel(new SchedulingVerizonModel())) {
				if (schedulingService.getOverallDetailsToCreateExcel(schedulingVerizonModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.OVERALL_DETAILS))
							.append(Constants.OVERALL_REPORT_VERIZON_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
							Constants.ACTION_EXPORT, "Verizon Overall Reports  Details Exported Successfully",
							sessionId);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (OverallExportDetails.get("searchStatus")!=null && !"".equals(OverallExportDetails.get("searchStatus"))) {
					searchStatus = OverallExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingSprintModel = new Gson().fromJson(
								OverallExportDetails.toJSONString((Map) OverallExportDetails.get("searchDetails")),
								SchedulingSprintModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingSprintModel = new SchedulingSprintModel();
					}
				}else {
					schedulingSprintModel = new SchedulingSprintModel();
				}
				
				//if (schedulingService.getOverallSprintToCreateExcel(new SchedulingSprintModel())) {
				if (schedulingService.getOverallSprintToCreateExcel(schedulingSprintModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.OVERALL_DETAILS))
							.append(Constants.OVERALL_REPORT_SPRINT_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL,
							Constants.ACTION_EXPORT, "Sprint Overall Reports  Details Exported Successfully",
							sessionId);
				}
			} else {
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

	/**
	 * This api imports the Overall Details into DB
	 * 
	 * @param importSchedulingDetails
	 * @return void
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.IMPORT_OVERALL_DETAILS, method = RequestMethod.POST)
	public JSONObject importOverallDetails(@RequestParam("overallReportsFile") MultipartFile file, String sessionId,
			String serviceToken, Integer customerId) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				resultMap = schedulingService.importVerizonOverallDetails(file, sessionId);
				commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL, Constants.ACTION_IMPORT,
						"Verizon Overall Reports  Details Imported Successfully", sessionId);
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				resultMap = schedulingService.importSprintOverallDetails(file, sessionId);
				commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_OVERALL, Constants.ACTION_EXPORT,
						"Sprint Overall Reports  Details Imported Successfully", sessionId);
			}
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			logger.error("Exception in OverallReportsController.importOverallDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
		return resultMap;
	}

}
