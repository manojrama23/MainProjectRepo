package com.smart.rct.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.OvScheduledModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.FetchScheduledService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.configuration.DailyOvScheduleConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.controller.WorkFlowManagementController;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class OvScheduledTaskController {

	final static Logger logger = LoggerFactory.getLogger(OvScheduledTaskController.class);
	
	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	
	@Autowired
	FetchScheduledService fetchScheduledService;
	
	@Autowired
	FetchProcessService fetchProcessService;
	
	@Autowired
	DailyOvScheduleConfig dailyOvScheduleConfig;
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	FetchProcessRepository fetchProcessRepository;

	@Autowired
	WorkFlowManagementController workFlowManagementController;
	/**
	 * This method will give the Audit Trail details
	 * 
	 * @param auditListDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/getOvStatusScheduledDetails", method = RequestMethod.POST)
	public JSONObject getOvScheduleddetails(@RequestBody JSONObject auditListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String searchStatus = null;
		String programName = null;
		String fetchDays = null;
		List<OvScheduledModel> ovStatusEntities= null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			searchStatus = auditListDetails.get("searchStatus").toString();
			programName = auditListDetails.get("programName").toString();
			List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if(Constants.LOAD.equals(searchStatus)){
				Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");
				int count = paginationData.get("count");
				int page = paginationData.get("page");	
				Map<String, Object> ovStatusList = ovScheduledTaskService.getOvStatusScheduledDetails(page, count,programName);
				resultMap.put("pageCount", ovStatusList.get("pageCount"));
				ovStatusEntities = (List<OvScheduledModel> ) ovStatusList.get("ovStatusList");
			}if(Constants.SEARCH.equals(searchStatus)){
				Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");
				int count = paginationData.get("count");
				int page = paginationData.get("page");
				OvScheduledModel ovScheduledModel = new Gson().fromJson(JSONObject.toJSONString((Map) auditListDetails.get("searchCriteria")),OvScheduledModel.class);
				Map<String, Object> ovStatusList = ovScheduledTaskService.getOvStatusScheduledSearchDetails(ovScheduledModel, page, count, programName);
				resultMap.put("pageCount", ovStatusList.get("pageCount"));
				ovStatusEntities = (List<OvScheduledModel> ) ovStatusList.get("ovStatusList");
			}
		/*	if(CommonUtil.isValidObject(auditTrailEntities)){
				auditTrailModels = new ArrayList<AuditTrailModel>();
				for(AuditTrailEntity auditTrailEntity: auditTrailEntities){
					auditTrailModels.add(auditTrailDto.getAuditTrailDetailsModel(auditTrailEntity));
				}
			}*/
			configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
			for (ProgramTemplateModel template : configDetailModelList) {
				if (template.getLabel().equals(Constants.FETCH_DAYS))
					fetchDays = template.getValue();
			}
			
			resultMap.put("ovScheduledDetails", ovStatusEntities);
			resultMap.put("fetchDays", fetchDays);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error( ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to Get Ov Scheduled Details");
		}
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })

	@RequestMapping(value = "/forceFetchTest", method = RequestMethod.POST)
	public JSONObject forceFetchTest(@RequestBody JSONObject auditListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;  
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			String programName = auditListDetails.get("programName").toString();
			JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Force Fetch",
					"Uploaded through Force Fetch Functionality", programName,"");
			if (statusOfOv.containsKey("statusCode")
					&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
				resultMap.put("status", Constants.SUCCESS);
			} else if (statusOfOv.containsKey("statusCode")
					&& "401".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Username OR Password is Wrong!");

			} else if (statusOfOv.containsKey("fetchIntraction")
					&& Constants.OV_INTRACTION_OFF.equalsIgnoreCase(statusOfOv.get("fetchIntraction").toString())) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Interaction Off Pls Check General Configurations");

			} else if (statusOfOv.containsKey("fetchIntraction")
					&& Constants.OV_INTRACTION_OFF.equalsIgnoreCase(statusOfOv.get("fetchIntraction").toString())) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Failed to Fetch");
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Failed to Fetch");
			}

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to Get Ov Scheduled Details");
		}
		return resultMap;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/forceFetch", method = RequestMethod.POST)
	public JSONObject forceFetch(@RequestBody JSONObject fetchCiqDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String ovAutomation = null;
		String ovOverallInteraction = null;
		try {
			sessionId = fetchCiqDetails.get("sessionId").toString();
			serviceToken = fetchCiqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			ObjectMapper mapper = new ObjectMapper();
			FetchDetailsModel fetchDetailsModel = mapper.readValue(fetchCiqDetails.toJSONString(),
					new TypeReference<FetchDetailsModel>() {
					});
			List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
			configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
			for (ProgramTemplateModel template : configDetailModelList) {
				if (template.getLabel().equals("OV AUTOMATION"))
					ovAutomation = template.getValue();
				if (template.getLabel().equals("OV OVERALL INTERACTION"))
					ovOverallInteraction = template.getValue();
			}
			if (!ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList())) {
				List<OvScheduledEntity> listSchedule = ovScheduledTaskService
						.getForceFecthOvDetails(fetchDetailsModel.getRfScriptList());
				if (!ObjectUtils.isEmpty(listSchedule)) {
					List<String> dbList = listSchedule.stream().map(entity -> entity.getNeId())
							.collect(Collectors.toList());

					List<String> notMappedOvData = fetchDetailsModel.getRfScriptList().stream()
							.filter(data -> !(dbList.contains(data))).collect(Collectors.toList());

					if (!ObjectUtils.isEmpty(notMappedOvData)) {
						String ovNotMapData = String.join(",", notMappedOvData);
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "NE Id " + ovNotMapData + " is not present in ov hence not uploaded");
						return resultMap;
					}
					
					List<OvScheduledEntity> trackerId = listSchedule.stream()
							.filter(data -> Objects.isNull(data.getTrackerId())).collect(Collectors.toList());
					
					if (!ObjectUtils.isEmpty(trackerId)) {
						List<String> dbList1 = trackerId.stream().map(entity -> entity.getNeId())
								.collect(Collectors.toList());
						String ovNotMapData = String.join(",", dbList1);
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "NE Id " + ovNotMapData + " is not associated with tracker id in ov hence not uploaded");
						return resultMap;
					}
					//if interaction ON nd Automation OFF
					if (ovAutomation != null && ovAutomation.equals("OFF") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
					JSONObject niIdDetails = ovScheduledTaskService.forceFetchDetails(fetchDetailsModel,
							"OV- Force Fetch", "Uploaded through Force Fetch Functionality", listSchedule);
					if (Constants.SUCCESS.equalsIgnoreCase(niIdDetails.get("status").toString())) {
						// premigration scheduled api
						CustomerDetailsEntity programmeEntity=new CustomerDetailsEntity();
						programmeEntity.setId(fetchDetailsModel.getProgramId());
						dailyOvScheduleConfig.OvScheduledTasksExcution(dbList,programmeEntity,"OV- Force Fetch");
						resultMap.put("status", Constants.SUCCESS);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", niIdDetails.get("reason").toString());
					}
					}else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "OV Interaction and OV Automaion should be configured properly");
					}
						

				}else {

					if (!ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList())) {
						String ovNotMapData = String.join(",", fetchDetailsModel.getRfScriptList());
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "NE Id " + ovNotMapData + " is not present in ov hence not uploaded");
						return resultMap;
					}
				}

			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to Get Ov Scheduled Details");
		}
		return resultMap;
	}
	

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/scheduleCheck", method = RequestMethod.POST)
	public JSONObject scheduleCheck(@RequestBody JSONObject auditListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String ovAutomation = null;
		String ovOverallInteraction = null;
		String programName =null;
		String fetchDate = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			if(auditListDetails.containsKey("programName"))
				programName = auditListDetails.get("programName").toString();
			if(auditListDetails.containsKey("fetchDate"))
			   fetchDate = auditListDetails.get("fetchDate").toString();
			if(programName ==null || fetchDate == null ) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Failed to Execute Please Select ProgramName and Fetch Date");
				
				return resultMap;
			}
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(programName);
			List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
			configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
			for (ProgramTemplateModel template : configDetailModelList) {
				if (template.getLabel().equals("OV AUTOMATION"))
					ovAutomation = template.getValue();
				if (template.getLabel().equals("OV OVERALL INTERACTION"))
					ovOverallInteraction = template.getValue();
			}
			if (ovAutomation != null && ovAutomation.equals("OFF") && ovOverallInteraction!=null && ovOverallInteraction.equals("ON")) {
			JSONObject statusOfOv = fetchProcessService.getOvFetchDetails("OV- Force Fetch",
					"Uploaded through Force Fetch Functionality", programName,fetchDate);
			if (statusOfOv.containsKey("statusCode")
					&& "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
				dailyOvScheduleConfig.OvScheduledTasksExcution(null,programmeEntity,"OV- Force Fetch");
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			}else if (statusOfOv.containsKey("statusCode")
					&& "401".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Username OR Password is Wrong!");

			} else if (statusOfOv.containsKey("fetchIntraction")
					&& Constants.OV_INTRACTION_OFF.equalsIgnoreCase(statusOfOv.get("fetchIntraction").toString())) {
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Fetch Interation is Off Pls Check General Configurations");

			}else if(statusOfOv.containsKey("reason")){
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", statusOfOv.get("reason"));
			}else {
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Failed to Execute");
			}
			}else
			{
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Interaction and OV Automaion need to configured properly");
			}
			
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error( ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to  Scheduled Details");
		}
		return resultMap;
	}
	
    @RequestMapping(value = Constants.EXPORT_OV_DETAILS, method = RequestMethod.POST)
	public void exportOvDetails(@RequestBody JSONObject networkconfigDownloadDetails,HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		List<CustomerDetailsEntity> programNamesList = null;
		OvScheduledModel ovScheduledModel = null;
		String searchStatus = null;
		try {
			sessionId = networkconfigDownloadDetails.get("sessionId").toString();
			serviceToken = networkconfigDownloadDetails.get("serviceToken").toString();
			Map<String, Integer> paginationData = (Map<String, Integer>) networkconfigDownloadDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			if (StringUtils.isNotEmpty(networkconfigDownloadDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(networkconfigDownloadDetails.get("customerId").toString());
			}
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
			if (networkconfigDownloadDetails.get("searchStatus")!=null && !"".equals(networkconfigDownloadDetails.get("searchStatus"))) {
				searchStatus = networkconfigDownloadDetails.get("searchStatus").toString();
				if("search".equals(searchStatus)) {
					 ovScheduledModel = new Gson().fromJson(JSONObject.toJSONString((Map) networkconfigDownloadDetails.get("searchDetails")),OvScheduledModel.class);
				}else if("load".equals(searchStatus)){
					ovScheduledModel = new OvScheduledModel();
				}
			}else {
				ovScheduledModel = new OvScheduledModel();
			}
			
			//if (networkConfigService.getNetWorkDetailsForCreateExcel(new NetworkConfigModel(), programs, false)) {
			if (ovScheduledTaskService.getOvDetailsForCreateExcel(ovScheduledModel, programs, false,page,count)) {
				filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
						.append(Constants.NETWORKCONFIG_XLSX);
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
				response.setHeader("sessionId", networkconfigDownloadDetails.get("sessionId").toString());
				response.setHeader("serviceToken", networkconfigDownloadDetails.get("serviceToken").toString());
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
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG,
						Constants.ACTION_EXPORT, "Network Config Details File Exported Successfully", sessionId);
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \""
						+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_NETWORK_CONFIG)
						+ "\"}");
				out.close();
				return;
			}
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.exportNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \""
					+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_NETWORK_CONFIG) + "\"}");
			out.close();
			return;
		}
		return;
	}
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_OV_DETAILS)
	public JSONObject deleteOvDetails(@RequestBody JSONObject deleteOvDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer ovId = null;
		JSONObject runTestParams = new JSONObject();

		try {
			sessionId = deleteOvDetails.get("sessionId").toString();
			serviceToken = deleteOvDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			ovId = (Integer) deleteOvDetails.get("id");

			OvScheduledEntity OvScheduledEntity = ovScheduledTaskService.getOvDetail(ovId);
			
			boolean status = ovScheduledTaskService.deleteOvDetails(ovId);
			if (status) {
				if (!status) {
					resultMap.put("reason","Failed to delete Ov Details");
					resultMap.put("status", Constants.FAIL);
					return resultMap;
				} else {
//					if(OvScheduledEntity.getWorkFlowManagementEntity()!=null && OvScheduledEntity.getWorkFlowManagementEntity().getId()!=null ) {
//					runTestParams.put("sessionId", sessionId);
//					runTestParams.put("serviceToken", serviceToken);
//					runTestParams.put("programId", OvScheduledEntity.getCustomerDetailsEntity().getId());
//					runTestParams.put("id", OvScheduledEntity.getWorkFlowManagementEntity().getId());
//					workFlowManagementController.deleteWfmRunTestData(runTestParams);
//					}
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason", "Deleted Successfully");
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in deleteOvDetails() OvScheduledTaskController : "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason","Failed to delete Ov Details");
		}
		return resultMap;
	}
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/updateOvDetails")
	public JSONObject editOvDetails(@RequestBody JSONObject editOvDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String enbId = null;
		String trackerId = null;
		JSONObject runTestParams = new JSONObject();
		String programName = null;
		try {
			sessionId = editOvDetails.get("sessionId").toString();
			serviceToken = editOvDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			trackerId = editOvDetails.get("trackerId").toString();
			enbId = editOvDetails.get("neId").toString();
			programName = editOvDetails.get("programName").toString();
			
			OvScheduledEntity OvScheduledEntity = ovScheduledTaskService.getOvScheduledServiceDetails(trackerId, enbId);

			if (editOvDetails.containsKey("ciqGenerationDate") && editOvDetails.get("ciqGenerationDate") != null) {
				
				if(!editOvDetails.get("ciqGenerationDate").toString().contains(" "))
				{
					String str = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM);
					OvScheduledEntity.setCiqGenerationDate(editOvDetails.get("ciqGenerationDate").toString()+" "+str.split(" ")[1]);
				}else
					OvScheduledEntity.setCiqGenerationDate(editOvDetails.get("ciqGenerationDate").toString());
				OvScheduledEntity = fetchProcessService.statusUpdateApi(OvScheduledEntity, "CIQ","edit","");

			}
			if (editOvDetails.containsKey("envGenerationDate") && editOvDetails.get("envGenerationDate") != null) {
				if(!editOvDetails.get("envGenerationDate").toString().contains(" "))
				{
					String str = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM);
					OvScheduledEntity.setEnvGenerationDate(editOvDetails.get("envGenerationDate").toString()+" "+str.split(" ")[1]);
					logger.error("Ov scheduled task contoller ");
				}else
					OvScheduledEntity.setEnvGenerationDate(editOvDetails.get("envGenerationDate").toString());
				OvScheduledEntity = fetchProcessService.statusUpdateApi(OvScheduledEntity, "ENV","edit","");
				logger.error("Ov scheduled task contoller with edit");
			}
			if (editOvDetails.containsKey("preMigGrowGenerationDate")
					&& editOvDetails.get("preMigGrowGenerationDate") != null) {
				if(!editOvDetails.get("preMigGrowGenerationDate").toString().contains(" "))
				{
					String str = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM);
					OvScheduledEntity.setPreMigGrowGenerationDate(editOvDetails.get("preMigGrowGenerationDate").toString()+" "+str.split(" ")[1]);
				}else
					OvScheduledEntity.setPreMigGrowGenerationDate(editOvDetails.get("preMigGrowGenerationDate").toString());
				OvScheduledEntity = fetchProcessService.statusUpdateApi(OvScheduledEntity, "GROW","edit","");
			}

			//ovScheduledTaskService.mergeOvScheduledDetails(OvScheduledEntity);

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", "Updated Data Successfully");
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info(
					"Exception in editOvDetails() OvScheduledTaskController : " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", "Failed to delete Ov Details");
		}
		return resultMap;
	}

}