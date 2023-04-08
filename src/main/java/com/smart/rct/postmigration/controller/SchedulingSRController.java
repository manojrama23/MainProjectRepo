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
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.dto.SchedulingSRDto;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.postmigration.service.SchedulingSRService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class SchedulingSRController {
	final static Logger logger = LoggerFactory.getLogger(SchedulingSRController.class);

	@Autowired
	SchedulingSRDto schedulingDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	CustomerService customerService;

	@Autowired
	SchedulingSRService schedulingService;

	/**
	 * This method will save Scheduling Details to DB
	 * 
	 * @param schedulingDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = Constants.SAVE_SCHEDULING_DETAILS)
	public JSONObject saveSchedulingDetails(@RequestBody JSONObject schedulingDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		try {
			sessionId = schedulingDetails.get("sessionId").toString();
			serviceToken = schedulingDetails.get("serviceToken").toString();
			customerId = Integer.valueOf(schedulingDetails.get("customerId").toString());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				SchedulingVerizonModel schedulingVerizonModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) schedulingDetails.get("schedulingDetails")),
						SchedulingVerizonModel.class);
				SchedulingVerizonEntity schedulingVerizonEntity = schedulingDto
						.getVerizonSchedulingEntity(schedulingVerizonModel);
				if (schedulingVerizonEntity != null) {
					if (schedulingService.saveVerizonSchedulingDetails(schedulingVerizonEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingVerizonModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
									Constants.ACTION_SAVE, "Verizon Scheduling  Details Saved Successfully", sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
									Constants.ACTION_UPDATE, "Verizon Scheduling  Details Updated Successfully",
									sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				SchedulingSprintModel schedulingSprintModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) schedulingDetails.get("schedulingDetails")),
						SchedulingSprintModel.class);
				SchedulingSprintEntity schedulingSprintEntity = schedulingDto
						.getSprintSchedulingEntity(schedulingSprintModel);
				if (schedulingSprintEntity != null) {
					if (schedulingService.saveSprintSchedulingDetails(schedulingSprintEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						if (schedulingSprintModel.getId() != null) {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
									Constants.ACTION_SAVE, "Sprint Scheduling  Details Saved Successfully", sessionId);
						} else {
							commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
									Constants.ACTION_UPDATE, "Sprint Scheduling  Details Updated Successfully",
									sessionId);
						}
					} else {
						resultMap.put("status", Constants.FAIL);
					}
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in SchedulingSRController.saveSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
	}

	/**
	 * This api deletes the Scheduling details
	 * 
	 * @param deleteScheduledDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_SCHEDULING_DETAILS)
	public JSONObject deleteSchedulingDetails(@RequestBody JSONObject deleteScheduledDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String id = null;
		Integer customerId = null;
		try {
			sessionId = deleteScheduledDetails.get("sessionId").toString();
			serviceToken = deleteScheduledDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			id = deleteScheduledDetails.get("id").toString();
			customerId = Integer.valueOf(deleteScheduledDetails.get("customerId").toString());
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteVerizonDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
							Constants.ACTION_DELETE, "verizon Scheduling  Details Deleted Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (CommonUtil.isValidObject(id) && schedulingService.deleteSprintDetails(Integer.parseInt(id))) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
							Constants.ACTION_DELETE, "Sprint Scheduling  Details Deleted Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in SchedulingSRController.deleteSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api get the Scheduling Details
	 * 
	 * @param schedulingDetails
	 * @return JSONObject
	 */

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_SCHEDULING_DETAILS)
	public JSONObject getSchedulingDetails(@RequestBody JSONObject schedulingDetails) {
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
			sessionId = schedulingDetails.get("sessionId").toString();
			serviceToken = schedulingDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = schedulingDetails.get("searchStatus").toString();
			customerId = Integer.valueOf(schedulingDetails.get("customerId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) schedulingDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					schedulingVerizonModel = new Gson().fromJson(
							schedulingDetails.toJSONString((Map) schedulingDetails.get("searchDetails")),
							SchedulingVerizonModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService
						.getVerizonSchedulingDetails(schedulingVerizonModel, page, count, customerId);
				schedulingVerizonEntity = (List<SchedulingVerizonEntity>) schedulingDetailsObj
						.get("schedulingVerizonEntityList");
				if (CommonUtil.isValidObject(schedulingVerizonEntity)) {
					schedulingVerizonModelList = new ArrayList<SchedulingVerizonModel>();
					for (SchedulingVerizonEntity schedulingEntity : schedulingVerizonEntity) {
						schedulingVerizonModelList.add(schedulingDto.getSchedulingVerizonModel(schedulingEntity));
					}
				}
				resultMap.put("comboBoxListDetails", schedulingDetailsObj.get("comboBoxListDetails"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("enodebId", schedulingDetailsObj.get("enodebId"));
				resultMap.put("enodebName", schedulingDetailsObj.get("enodebName"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("schedulingVerizonModelList", schedulingVerizonModelList);
				resultMap.put("status", Constants.SUCCESS);
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (Constants.SEARCH.equals(searchStatus)) {
					schedulingSprintModel = new Gson().fromJson(
							schedulingDetails.toJSONString((Map) schedulingDetails.get("searchDetails")),
							SchedulingSprintModel.class);
				}
				Map<String, Object> schedulingDetailsObj = schedulingService
						.getSprintSchedulingDetails(schedulingSprintModel, page, count, customerId);
				schedulingSprintEntity = (List<SchedulingSprintEntity>) schedulingDetailsObj
						.get("schedulingSprintEntityList");
				if (CommonUtil.isValidObject(schedulingSprintEntity)) {
					schedulingSprintModelList = new ArrayList<SchedulingSprintModel>();
					for (SchedulingSprintEntity schedulingEntity : schedulingSprintEntity) {
						schedulingSprintModelList.add(schedulingDto.getSchedulingSprintModel(schedulingEntity));
					}
				}
				resultMap.put("marketDetailsList", schedulingDetailsObj.get("marketDetailsList"));
				resultMap.put("regionDetailsList", schedulingDetailsObj.get("regionDetailsList"));
				resultMap.put("feregionDetailsList", schedulingDetailsObj.get("feregionDetailsList"));
				resultMap.put("fenightDetailsList", schedulingDetailsObj.get("fenightDetailsList"));
				resultMap.put("fedayDetailsList", schedulingDetailsObj.get("fedayDetailsList"));
				resultMap.put("pageCount", schedulingDetailsObj.get("paginationcount"));
				resultMap.put("username", schedulingDetailsObj.get("username"));
				resultMap.put("market", schedulingDetailsObj.get("market"));
				resultMap.put("region", schedulingDetailsObj.get("region"));
				resultMap.put("schedulingSprintEntity", schedulingSprintModelList);
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
	@RequestMapping(value = Constants.EXPORT_SCHEDULING_DETAILS, method = RequestMethod.POST)
	public void exportSchedulingDetails(@RequestBody JSONObject schedulingExportDetails, HttpServletResponse response)
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
			sessionId = schedulingExportDetails.get("sessionId").toString();
			serviceToken = schedulingExportDetails.get("serviceToken").toString();
			response.setHeader("sessionId", sessionId);
			response.setHeader("serviceToken", serviceToken);

			if (StringUtils.isNotEmpty(schedulingExportDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(schedulingExportDetails.get("customerId").toString());
			}
			if (customerId == Constants.VZN_CUSTOMER_ID) {
				if (schedulingExportDetails.get("searchStatus")!=null && !"".equals(schedulingExportDetails.get("searchStatus"))) {
					searchStatus = schedulingExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingVerizonModel = new Gson().fromJson(
								schedulingExportDetails.toJSONString((Map) schedulingExportDetails.get("searchDetails")),
								SchedulingVerizonModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingVerizonModel = new SchedulingVerizonModel();
					}
				}else {
					schedulingVerizonModel = new SchedulingVerizonModel();
				}
				
				//if (schedulingService.getSchedulingDetailsToCreateExcel(new SchedulingVerizonModel())) {
				if (schedulingService.getSchedulingDetailsToCreateExcel(schedulingVerizonModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.SCHEDULING_DETAILS))
							.append(Constants.SCHEDULING_VERIZON_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
							Constants.ACTION_EXPORT, "Verizon Scheduling  Details Exported Successfully", sessionId);
				}
			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				if (schedulingExportDetails.get("searchStatus")!=null && !"".equals(schedulingExportDetails.get("searchStatus"))) {
					searchStatus = schedulingExportDetails.get("searchStatus").toString();
					if("search".equals(searchStatus)) {
						schedulingSprintModel = new Gson().fromJson(
								schedulingExportDetails.toJSONString((Map) schedulingExportDetails.get("searchDetails")),
								SchedulingSprintModel.class);
					}else if("load".equals(searchStatus)) {
						schedulingSprintModel = new SchedulingSprintModel();
					}
				}else {
					schedulingSprintModel = new SchedulingSprintModel();
				}
				
				//if (schedulingService.getSchedulingSprintToCreateExcel(new SchedulingSprintModel())) {
				if (schedulingService.getSchedulingSprintToCreateExcel(schedulingSprintModel)) {
					filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
							.append(LoadPropertyFiles.getInstance().getProperty(Constants.SCHEDULING_DETAILS))
							.append(Constants.SCHEDULING_SPRINT_XLSX);
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
					commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
							Constants.ACTION_EXPORT, "Sprint Scheduling  Details Exported Successfully", sessionId);
				}
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;
			}
		} catch (Exception e) {
			logger.error("Exception in SchedulingSRController.exportSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
			return;
		}
		return;
	}

	/**
	 * This api imports the Scheduling Details into DB
	 * 
	 * @param importSchedulingDetails
	 * @return void
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.IMPORT_SCHEDULING_DETAILS, method = RequestMethod.POST)
	public JSONObject importSchedulingDetails(@RequestParam("schedulingFile") MultipartFile file, String sessionId,
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
				resultMap = schedulingService.importVerizonSchedulingDetails(file, sessionId);
				commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
						Constants.ACTION_IMPORT, "Verizon Scheduling  Details Imported Successfully", sessionId);

			}
			if (customerId == Constants.SPT_CUSTOMER_ID) {
				resultMap = schedulingService.importSprintSchedulingDetails(file, sessionId);
				commonUtil.saveAudit(Constants.EVENT_S_AND_R, Constants.EVENT_S_AND_R_SCHEDULING,
						Constants.ACTION_IMPORT, "Sprint Scheduling  Details Deleted Successfully", sessionId);
			}
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			logger.error("Exception in SchedulingSRController.importSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.GET_CUSTOMER_ID_LIST)
	public JSONObject getCustomerIdList(@RequestBody JSONObject customerIdDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = customerIdDetails.get("sessionId").toString();
			serviceToken = customerIdDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(user.getCustomerId());
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("allProgramList", customerService.getAllProgramList(customerDetailsModel));

			Map<String, Object> obj = schedulingService.getCustomerIdList();	
			resultMap.put("CustomerList", obj);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			logger.error(
					"Exception in SchedulingSRController.getCustomerIdList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
		return resultMap;
	}

}