package com.smart.rct.common.controller;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.rct.common.dto.CustomerDetailsDto;
import com.smart.rct.common.dto.NeVersionDto;
import com.smart.rct.common.dto.NetworkTypeDetailsDto;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.SnrGeneralEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.CustomerModel;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.OvScheduledTaskRepository;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.common.service.GenerateSystemReportService;
import com.smart.rct.common.service.NeVersionService;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.configuration.DailyOvScheduleConfig;
//import com.smart.rct.configuration.TaskSchedulerConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class GeneralConfigController {

	final static Logger logger = LoggerFactory.getLogger(GeneralConfigController.class);

	@Autowired
	NetworkTypeDetailsDto networkTypeDetailsDto;

	@Autowired
	CustomerDetailsDto customerDetailsDto;

	@Autowired
	NetworkTypeDetailsService networkTypeDetailsService;

	@Autowired
	CustomerService customerService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	NeVersionService neVersionService;

	@Autowired
	NeVersionDto neVersionDto;
	
	@Autowired
	GenerateSystemReportService genSysReport;
	
	@Autowired
	DuoGeneralConfigService duoGeneralConfigService;
	
	@Autowired
	FetchProcessRepository fetchProcessRepository;
	
	@Autowired
	OvScheduledTaskRepository ovScheduledTaskRepository;
	
	@Autowired
	FetchProcessService fetchProcessService;
	
	@Autowired
	DailyOvScheduleConfig dailyOvScheduleConfig;

	
	
	/**
	 * This method will get the Session Timeout from application.properties file
	 * 
	 * @param params
	 * @return JSONObject
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = Constants.CONFIGURATION_DETAILS, method = RequestMethod.POST)

	public JSONObject getConfigDetails(@RequestBody JSONObject params) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String fetchDays = null;

		List<CustomerDetailsEntity> programList = null;
		List<CustomerEntity> custList = null;
		try {
			sessionId = params.get("sessionId").toString();
			serviceToken = params.get("serviceToken").toString();
			String configType = params.get("generalConfigType").toString();
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			LoadPropertyFiles.getInstance().init();
			
			List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
			List<ProgramTemplateModel> configDetailModelListData = new ArrayList<ProgramTemplateModel>();
			if(StringUtils.isNotEmpty(configType) && Constants.CONFIG_TYPE_GENERAL.equalsIgnoreCase(configType)){
				
				ProgramTemplateModel configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("Session Time Out (in minutes)");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("sessionTimeOut"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);
				
				/*configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("Number Of Active Sessions Per User");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("sessionsPerUser"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);
				
				configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("Total Active Sessions");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("totalActiveSessions"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);*/
				
				configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("Tool Deployment");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("deploymentType"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);
				
				configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("History (in days)");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("actionPerformed"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);
				
				configDetailModel = new ProgramTemplateModel();
				configDetailModel.setLabel("CIQ TYPE");
				configDetailModel.setValue(LoadPropertyFiles.getInstance().getProperty("ciqType"));
				configDetailModel.setType("GENERAL");
				configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
				configDetailModelList.add(configDetailModel);
				
				configDetailModelList = customerService.getSnrConfigList(configDetailModelList);
				
				configDetailModelList=customerService.getOvTemplateDetails(configDetailModelList,configType);
				
				List<ProgramTemplateModel> duoGeneralConfigList = duoGeneralConfigService.getDuoGeneralConfigList();
				if(duoGeneralConfigList==null || duoGeneralConfigList.isEmpty()) {
					DuoGeneralConfigEntity duoGeneralConfigEntity = new DuoGeneralConfigEntity();
					duoGeneralConfigEntity.setLabel("DUO AUTHENTICATION");
					duoGeneralConfigEntity.setValue("OFF");
					duoGeneralConfigEntity.setConfigType(Constants.CONFIG_TYPE_GENERAL);
					duoGeneralConfigService.saveDuoGeneralConfigEntity(duoGeneralConfigEntity);
					duoGeneralConfigList = duoGeneralConfigService.getDuoGeneralConfigList();
				}
				if(duoGeneralConfigList!=null && !duoGeneralConfigList.isEmpty()) {
					configDetailModel = duoGeneralConfigList.get(0);
					configDetailModel.setId(null);
					configDetailModelList.add(configDetailModel);
				}
			}
			
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(user.getCustomerId());
				customerDetailsModel.setCustomerEntity(customerEntity);
				programList = customerService.getCustomerDetailsList(customerDetailsModel);
			} else {
				programList = customerService.getProgramDetailsList(user);
			}
			if (user.getRoleId() < 3) {
				custList = customerService.getCustomerList(false, false);
			} else {
				CustomerEntity customerEntity = customerService.getCustomerById(user.getCustomerId());
				List<CustomerEntity> customerEntities = new ArrayList<>();
				customerEntities.add(customerEntity);
				custList = customerEntities;
			}
			
			configDetailModelList = customerService.getProgTemplateDetails(configDetailModelList, programList,configType);
			if(StringUtils.isNotEmpty(configType) && Constants.CONFIG_TYPE_S_R.equalsIgnoreCase(configType)){
				configDetailModelList = customerService.getComboBoxList(configDetailModelList, custList);
				
				configDetailModelListData=customerService.getOvTemplateDetails(configDetailModelList,"general");
				for (ProgramTemplateModel template : configDetailModelList) {
					if (template.getLabel().equals("OV No. Of fetch Days"))
						fetchDays = template.getValue();
				}
			}
			if(StringUtils.isNotEmpty(configType)){
				configDetailModelList = configDetailModelList.stream().filter(x->x.getConfigType().equalsIgnoreCase(configType)).collect(Collectors.toList());
			}
			
			resultMap.put("ConfigDetails", configDetailModelList);
			resultMap.put("fetchDays", fetchDays);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			logger.error(
					"Exception GeneralConfigController.getConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_READ_CONFIGURATION_DETAILS));
			return resultMap;
		}

		return resultMap;
	}

	/**
	 * This Controller will set Session Timeout to application.properties file
	 * 
	 * @param configParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = Constants.EDIT_CONFIGURATION_DETAILS, method = RequestMethod.POST)
	public JSONObject saveConfigDetails(@RequestBody String configParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		JsonObject jsonData = null;
		String sessionId = null;
		String serviceToken = null;
		JsonObject configDetails = null;
		try {
			JSONObject object = (JSONObject) new JSONParser().parse(configParams);
			jsonData = CommonUtil.parseRequestDataToJson(configParams);
			sessionId = jsonData.get("sessionId").getAsString();
			serviceToken = jsonData.get("serviceToken").getAsString();

			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String type = jsonData.get("type").getAsString();
			configDetails = jsonData.get("configDetails").getAsJsonObject();
			if (Constants.SESSION_DETAILS.equals(type)) {
				configDetails = jsonData.get("configDetails").getAsJsonObject();
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("sessionTimeOut",
						configDetails.get("sessiontimeout").getAsString());
				// Set the SESSION_TIMEOUT for the current session
				try {
					int intVal = Integer.parseInt(configDetails.get("sessiontimeout").getAsString());
					if (intVal > 0) {
						GlobalInitializerListener.MAX_INACTIVE_SESSION_TIMEOUT = intVal * 60000;
					}
				} catch (Exception e) {
					logger.error(
							"Unable to set the timeout for the current session" + ExceptionUtils.getFullStackTrace(e));
				}
			}else if(Constants.SESSION_PER_USER.equals(type)) {
				configDetails = jsonData.get("configDetails").getAsJsonObject();
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("sessionsPerUser",
						configDetails.get("dynamicParam").getAsString());
			}else if(Constants.SESSION_TOTAL_ACTIVE.equals(type)) {
				configDetails = jsonData.get("configDetails").getAsJsonObject();
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("totalActiveSessions",
						configDetails.get("dynamicParam").getAsString());
			}else if (Constants.DEPLOYMENT_DETAILS.equals(type)) {
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("deploymentType",
						configDetails.get("deploymenttype").getAsString());
			} else if (Constants.ACTION_PERFORMED.equals(type)) {
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("actionPerformed",
						configDetails.get("dynamicParam").getAsString());
			}else if (Constants.SCHEDULED_TIME.equals(type)) {
				List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
				configDetailModelList = customerService.getSnrConfigList(configDetailModelList);
				for(ProgramTemplateModel template : configDetailModelList) {
					if(template.getLabel().equals("SCHEDULE TIME")) {
						SnrGeneralEntity snrGeneralEntity = new SnrGeneralEntity();
						snrGeneralEntity.setId(template.getId());
						snrGeneralEntity.setValue(configDetails.get("value").getAsString());
						snrGeneralEntity.setLabel("SCHEDULE TIME");
						snrGeneralEntity.setConfigType(Constants.CONFIG_TYPE_GENERAL);
						customerService.saveSnrTemplate(snrGeneralEntity);
					}
				}
			}
			else if (Constants.SCHEDULED_FREQ.equals(type)) {
				List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
				configDetailModelList = customerService.getSnrConfigList(configDetailModelList);
				for(ProgramTemplateModel template : configDetailModelList) {
					if(template.getLabel().equals("SCHEDULE FREQUENCY")) {
						SnrGeneralEntity snrGeneralEntity = new SnrGeneralEntity();
						snrGeneralEntity.setId(template.getId());
						snrGeneralEntity.setValue(configDetails.get("value").getAsString());
						snrGeneralEntity.setLabel("SCHEDULE FREQUENCY");
						snrGeneralEntity.setConfigType(Constants.CONFIG_TYPE_GENERAL);
						customerService.saveSnrTemplate(snrGeneralEntity);
					}
				}
			}
			else if (Constants.SCHEDULED_ENABLE.equals(type)) {
				List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
				configDetailModelList = customerService.getSnrConfigList(configDetailModelList);
				for(ProgramTemplateModel template : configDetailModelList) {
					if(template.getLabel().equals("SCHEDULE ENABLE")) {
						SnrGeneralEntity snrGeneralEntity = new SnrGeneralEntity();
						snrGeneralEntity.setId(template.getId());
						snrGeneralEntity.setValue(configDetails.get("value").getAsString());
						snrGeneralEntity.setLabel("SCHEDULE ENABLE");
						snrGeneralEntity.setConfigType(Constants.CONFIG_TYPE_GENERAL);
						customerService.saveSnrTemplate(snrGeneralEntity);
					}
				}
			}else if ("ciqType".equals(type)) {
				LoadPropertyFiles.getInstance().init();
				LoadPropertyFiles.getInstance().setConfigProperties("ciqType",
						configDetails.get("dynamicParam").getAsString());
			}
			else if ("GENERAL OV".equals(type)) {
				OvGeneralEntity objOvGeneralEntity = new Gson().fromJson(
						object.toJSONString((Map) object.get("configDetails")),
						OvGeneralEntity.class);
				customerService.saveOvTemplate(objOvGeneralEntity);

			} else if (Constants.DUO_AUTHENTICATION_TYPE.equals(type)) {
				List<ProgramTemplateModel> duoGeneralConfigList = duoGeneralConfigService.getDuoGeneralConfigList();
				if(duoGeneralConfigList!=null && !duoGeneralConfigList.isEmpty()) {
					ProgramTemplateModel configDetailModel = duoGeneralConfigList.get(0);
					DuoGeneralConfigEntity duoGeneralConfigEntity = new DuoGeneralConfigEntity();
					duoGeneralConfigEntity.setId(configDetailModel.getId());
					duoGeneralConfigEntity.setValue(configDetails.get("value").getAsString());
					duoGeneralConfigEntity.setLabel("DUO AUTHENTICATION");
					duoGeneralConfigEntity.setConfigType(Constants.CONFIG_TYPE_GENERAL);
					duoGeneralConfigService.saveDuoGeneralConfigEntity(duoGeneralConfigEntity);
				}
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
					Constants.ACTION_UPDATE, "Configuration Details Updated Successfully For Type: " + type, sessionId);
			return resultMap;
		} catch (Exception e) {
			logger.error(
					"Exception in GeneralConfigController.saveConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EDIT_CONFIGURATION_DETAILS));
			return resultMap;
		}
	}

	/**
	 * This method will Save Network type to DB
	 * 
	 * @param networkTypeDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_NETWORK_TYPE, method = RequestMethod.POST)
	public JSONObject addNetworkType(@RequestBody JSONObject networkTypeDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = networkTypeDetails.get("sessionId").toString();
			serviceToken = networkTypeDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			NetworkTypeDetailsModel nwTypeModel = new Gson().fromJson(
					JSONObject.toJSONString((Map) networkTypeDetails.get("nwTypeDetails")),
					NetworkTypeDetailsModel.class);
			NetworkTypeDetailsEntity networkTypeDetailsEntity = networkTypeDetailsDto.getNwTypeDetails(nwTypeModel,
					sessionId);

			if (networkTypeDetailsEntity != null) {
				NetworkTypeDetailsEntity entity = networkTypeDetailsService
						.getNetworkTypeByName(networkTypeDetailsEntity.getNetworkType());
				if (CommonUtil.isValidObject(entity) && entity.getId() > 0
						&& entity.getId() != networkTypeDetailsEntity.getId()) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWORK_TYPE_EXIST));
					return resultMap;
				}
				if (networkTypeDetailsService.saveNetworkTypeDetails(networkTypeDetailsEntity)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWORK_TYPE_SAVED_SUCCESSFULLY));
					if (nwTypeModel.getId() != null) {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG, Constants.ACTION_UPDATE,
								"Network Type Updated Successfully For Type: " + nwTypeModel.getNetworkType(),
								sessionId);
					} else {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG, Constants.ACTION_ADD,
								"Network Type Added Successfully For Type: " + nwTypeModel.getNetworkType(), sessionId);
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SAVE_NETWORK_TYPE));
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error(
					"Exception in GeneralConfigController.addNetworkType(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SAVE_NETWORK_TYPE));
			return resultMap;
		}
	}

	/**
	 * This api will Get the Network Type Detials
	 * 
	 * @param getNetworkTypeDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_NETWOK_TYPE_DETAILS, method = RequestMethod.POST)
	public JSONObject getNetworkTypeDetails(@RequestBody JSONObject getNetworkTypeDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = getNetworkTypeDetails.get("sessionId").toString();
			serviceToken = getNetworkTypeDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			List<NetworkTypeDetailsEntity> nwTypeEntityList = networkTypeDetailsService.getNwTypeDetails(false);
			resultMap.put("networkTypeDetails", nwTypeEntityList);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.getNetworkTypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_NETWORK_TYPE_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This api will delete the NetworkTypeDetails
	 * 
	 * @param deleteNwTypeDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_NETWORK_TYPE, method = RequestMethod.POST)
	public JSONObject deleteNetwokTypeDetials(@RequestBody JSONObject deleteNwTypeDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String nwTypeId = null;
		try {
			sessionId = deleteNwTypeDetails.get("sessionId").toString();
			serviceToken = deleteNwTypeDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			nwTypeId = deleteNwTypeDetails.get("nwTypeId").toString();
			if (StringUtils.isNotEmpty(nwTypeId)) {
				NetworkTypeDetailsEntity networkTypeDetailsEntity = networkTypeDetailsService
						.getNetworkTypeById((Integer.valueOf(nwTypeId)));
				if (CommonUtil.isValidObject(networkTypeDetailsEntity)
						&& networkTypeDetailsService.deleteNetworkTypeDetials((Integer.valueOf(nwTypeId)))) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWOTK_TYPE_DELETED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
							Constants.ACTION_DELETE,
							"Network Type Deleted Successfully: " + networkTypeDetailsEntity.getNetworkType(),
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NETWOTK_TYPE));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.deleteNetwokTypeDetials(): "
					+ ExceptionUtils.getFullStackTrace(e));
			if (e instanceof RctException) {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWORK_TYPE_DETAILS_ASSOSIATED));
			} else {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NETWOTK_TYPE));
			}
		}
		return resultMap;
	}

	/**
	 * This api will get the programList
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_PROGRAM_LIST, method = RequestMethod.POST)
	public JSONObject getProgramList(@RequestBody JSONObject customerDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;

		try {
			sessionId = customerDetails.get("sessionId").toString();
			serviceToken = customerDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(user.getCustomerId());
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("programNamesList", customerService.getCustomerDetailsList(customerDetailsModel));
			} else {
				resultMap.put("programNamesList", customerService.getProgramDetailsList(user));
			}
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error(
					"Exception in GeneralConfigController.getProgramList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_PROGRAMS_LIST));

		}
		return resultMap;
	}

	/**
	 * This api will get the CustomerList
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CUSTOMER_LIST, method = RequestMethod.POST)
	public JSONObject getCustomerList(@RequestBody JSONObject customerDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;

		try {
			sessionId = customerDetails.get("sessionId").toString();
			serviceToken = customerDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(user.getCustomerId());
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("programNamesList", customerService.getCustomerDetailsList(customerDetailsModel));
			} else {
				resultMap.put("programNamesList", customerService.getProgramDetailsList(user));
			}

			if (user.getRoleId() < 3) {
				List<CustomerEntity> customerEntities = customerService.getCustomerList(false, true);
				resultMap.put("customerList", customerEntities);
				resultMap.put("allProgramList", customerService.getAllProgramList(null));
			} else {
				CustomerEntity customerEntity = customerService.getCustomerById(user.getCustomerId());
				List<CustomerEntity> customerEntities = new ArrayList<>();
				customerEntities.add(customerEntity);
				resultMap.put("customerList", customerEntities);
				
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("allProgramList", customerService.getAllProgramList(customerDetailsModel));
			}

			List<NetworkTypeDetailsEntity> networkTypeDetailsEntities = networkTypeDetailsService
					.getNwTypeDetails(false);
			resultMap.put("netWorkDetails", networkTypeDetailsEntities);

			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error(
					"Exception in GeneralConfigController.getCustomerList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CUSTOMER_LIST));

		}
		return resultMap;
	}

	/**
	 * This api adds customer and its details
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.ADD_CUSTOMER, method = RequestMethod.POST)
	public JSONObject addCustomer(@RequestPart(required = true, value = "icon") MultipartFile iconFile,
			@RequestParam("sessionId") String sessionId, @RequestParam("serviceToken") String serviceToken,
			@RequestParam("customerDetails") String customerDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			JSONObject customer = CommonUtil.parseDataToJSON(customerDetails);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			CustomerModel customerModel = new Gson().fromJson(customer.toJSONString((Map) customer),
					CustomerModel.class);
			CustomerEntity customerEntity = customerDetailsDto.getCustomerEntity(customerModel, sessionId);
			if (CommonUtil.isValidObject(customerEntity)) {
				CustomerEntity entity = customerService.getCustomerByName(customerEntity.getCustomerName());
				if (CommonUtil.isValidObject(entity) && entity.getId() > 0) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_NAME_EXIST));
					return resultMap;
				}
				StringBuilder iconSavePath = new StringBuilder();
				iconSavePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.CUSTOMER_ICON_SAVE_PATH);

				File iconDir = new File(iconSavePath.toString());
				if (!iconDir.exists()) {
					FileUtil.createDirectory(iconSavePath.toString());
				}
				FileUtil.transferMultipartFile(iconFile, iconSavePath.toString());
				File oldIcon = new File(iconSavePath.toString() + "/" + iconFile.getOriginalFilename());
				iconSavePath.append(customerEntity.getCustomerName().toLowerCase() + "_ " + timeStamp + "_icon.png");
				File newIcon = new File(iconSavePath.toString());
				oldIcon.renameTo(newIcon);
				StringBuilder iconPath = new StringBuilder();
				iconPath.append(Constants.CUSTOMER_ICON_GET_PATH);
				iconPath.append(customerEntity.getCustomerName().toLowerCase() + "_ " + timeStamp + "_icon.png");
				customerEntity.setIconPath(iconPath.toString());
				customerEntity = customerService.saveCustomer(customerEntity);
				if (customerEntity != null) {
					customerEntity = customerService.saveCustomer(customerEntity);
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_SAVED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
							Constants.ACTION_SAVE,
							"Customer" + " " + customerModel.getCustomerName() + " " + " Created Successfully",
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_CUSTOMER_DETAILS));
				}
			}
			return resultMap;
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.addCustomer(): " + ExceptionUtils.getFullStackTrace(e));
			if (e instanceof RctException) {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			} else {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_CUSTOMER_DETAILS));
			}
			return resultMap;
		}

	}

	/**
	 * This api adds customer and its details
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPDATE_CUSTOMER_ICON, method = RequestMethod.POST)
	public JSONObject updateCustomerIcon(@RequestPart(required = true, value = "icon") MultipartFile iconFile,
			@RequestParam("sessionId") String sessionId, @RequestParam("serviceToken") String serviceToken,
			@RequestParam("customerId") String customerId, @RequestParam("customerName") String customerName) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (CommonUtil.isValidObject(customerName) && CommonUtil.isValidObject(customerId)) {

				StringBuilder iconSavePath = new StringBuilder();
				iconSavePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.CUSTOMER_ICON_SAVE_PATH);

				File iconDir = new File(iconSavePath.toString());
				if (!iconDir.exists()) {
					FileUtil.createDirectory(iconSavePath.toString());
				}
				FileUtil.transferMultipartFile(iconFile, iconSavePath.toString());
				File oldIcon = new File(iconSavePath.toString() + "/" + iconFile.getOriginalFilename());
				iconSavePath.append(customerName.toLowerCase() + "_ " + timeStamp + "_icon.png");
				File newIcon = new File(iconSavePath.toString());
				oldIcon.renameTo(newIcon);

				CustomerEntity customerEntity = customerService.getCustomerById(Integer.parseInt(customerId));
				StringBuilder iconPath = new StringBuilder();
				iconPath.append(Constants.CUSTOMER_ICON_GET_PATH);
				iconPath.append(customerEntity.getCustomerName().toLowerCase() + "_ " + timeStamp + "_icon.png");
				customerEntity.setIconPath(iconPath.toString());

				String iconFolders[] = customerEntity.getIconPath().split("/");

				File prevIcon = new File(iconSavePath + iconFolders[iconFolders.length - 1]);
				if (prevIcon.exists()) {
					prevIcon.delete();
				}
				customerEntity = customerService.saveCustomer(customerEntity);

				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_ICON_SAVED_SUCCESSFULLY));
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
						Constants.ACTION_UPDATE, "Customer Logo Updated Successfully For: " + customerName, sessionId);
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CUSTOMER_ICON));
			}
			return resultMap;
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.updateCustomerIcon(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CUSTOMER_ICON));
			return resultMap;
		}

	}

	/**
	 * This api saves customer and its details
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_CUSTOMER, method = RequestMethod.POST)
	public JSONObject saveCustomer(@RequestBody JSONObject customerDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = customerDetails.get("sessionId").toString();
			serviceToken = customerDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			CustomerModel customerModel = new Gson().fromJson(customerDetails.toJSONString((Map) customerDetails.get("customerDetails")), CustomerModel.class);
			CustomerEntity customerEntity = customerDetailsDto.getCustomerEntity(customerModel, sessionId);

			if (CommonUtil.isValidObject(customerEntity)) {
				CustomerEntity prevCustomerEntity = customerService.getCustomerById(customerEntity.getId());
				customerEntity.setIconPath(prevCustomerEntity.getIconPath());
				customerEntity = customerService.saveCustomer(customerEntity);
				if (customerEntity != null) {
					customerEntity = customerService.saveCustomer(customerEntity);
					if(Constants.INACTIVE.equalsIgnoreCase(customerEntity.getStatus())){
						customerService.inActivateCustomer(Integer.valueOf(customerEntity.getId()));
					}
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_DETAILS_SAVED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
							Constants.ACTION_UPDATE,
							"Customer Details Updated Successfully For: " + customerEntity.getCustomerName(),
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SAVE_CUSTOMER_DETAILS));
				}
			}
			return resultMap;
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.saveCustomer: " + ExceptionUtils.getFullStackTrace(e));
			if (e instanceof RctException) {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			} else {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SAVE_CUSTOMER_DETAILS));
			}
			return resultMap;
		}

	}

	/**
	 * This api deletes the customer and its details
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_CUSTOMER, method = RequestMethod.POST)
	public JSONObject deleteCustomer(@RequestBody JSONObject customerDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String customerId = null;
		try {
			sessionId = customerDetails.get("sessionId").toString();
			serviceToken = customerDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			customerId = customerDetails.get("id").toString();
			if (CommonUtil.isValidObject(customerId)) {
				CustomerEntity customerEntity = customerService.getCustomerById(Integer.parseInt(customerId));
				if (CommonUtil.isValidObject(customerEntity)) {
					if (CommonUtil.isValidObject(customerEntity.getCustomerDetails()) && customerEntity.getCustomerDetails().size() > 0) {
						customerEntity.getCustomerDetails().forEach((u) -> {
							try {
								customerService.deleteCustomerDetails(u.getId());
							} catch (RctException e) {
								logger.error("Exception in GeneralConfigController.deleteCustomer(): " + ExceptionUtils.getFullStackTrace(e));
								e.printStackTrace();
							}
						});
					}
					if (customerService.deleteCustomer(Integer.valueOf(customerId))) {
						StringBuilder iconDetelePath = new StringBuilder();
						iconDetelePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						.append(Constants.CUSTOMER).append(Constants.CUSTOMER_ICON_SAVE_PATH)
						.append(customerEntity.getIconPath().replaceAll("/customer/", ""));
						
						File icon = new File(iconDetelePath.toString());
						if (icon.exists()) {
							icon.delete();
						}
						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_CUSTOMER_SUCCESSFULLY));
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG, Constants.ACTION_DELETE,
								"Customer Deleted Successfully: " + customerEntity.getCustomerName(), sessionId);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CUSTOMER));
					}
				}
			} else {
				resultMap.put("status", Constants.FAIL);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error(
					"Exception in GeneralConfigController.deleteCustomer(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CUSTOMER));
		}
		return resultMap;
	}

	/**
	 * This api deletes the customer and its details
	 * 
	 * @param customerDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_CUSTOMER_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteCustomerDetails(@RequestBody JSONObject customerDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String customerDetailId = null;
		List<ProgramGenerateFileEntity> generateFileEntities = null;
		List<ProgramTemplateEntity> programTemplateEntities = null;
		ProgramTemplateModel programTemplateModel = null;
		CustomerDetailsEntity programDetailsEntity = null;
		String programName = "";
		try {
			sessionId = customerDetails.get("sessionId").toString();
			serviceToken = customerDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			customerDetailId = customerDetails.get("id").toString();

			/*programTemplateModel = new ProgramTemplateModel();
			programDetailsEntity = new CustomerDetailsEntity();

			programDetailsEntity.setId(Integer.parseInt(customerDetailId));
			programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			List<ProgramTemplateEntity> entities = customerService.getProgTemplateDetails(programTemplateModel);
			if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
				programName = entities.get(0).getProgramDetailsEntity().getProgramName();
				boolean dataExist = false;
				for (ProgramTemplateEntity programTemplateEntity : entities) {
					if (CommonUtil.isValidObject(programTemplateEntity.getValue())
							&& programTemplateEntity.getValue().length() > 0) {
						dataExist = true;
						break;
					}
				}
				if (!dataExist) {
					if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
						programTemplateEntities = new ArrayList<ProgramTemplateEntity>();
						for (ProgramTemplateEntity programTemplateEntity : entities) {
							customerService.deleteProgramTemplate(programTemplateEntity.getId());
							programTemplateEntity.setId(null);
							programTemplateEntities.add(programTemplateEntity);
						}
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_DETAILS_ASSOSIATED));
					return resultMap;
				}
			}
			List<ProgramGenerateFileEntity> programGenerateFileDetails = customerService
					.getProgramGenerateFileDetails(Integer.parseInt(customerDetailId));
			if (CommonUtil.isValidObject(programGenerateFileDetails) && programGenerateFileDetails.size() > 0) {
				generateFileEntities = new ArrayList<ProgramGenerateFileEntity>();
				for (ProgramGenerateFileEntity generateFileEntity : programGenerateFileDetails) {
					customerService.deleteProgramGenerateFileEntity(generateFileEntity.getId());
					generateFileEntity.setId(null);
					generateFileEntities.add(generateFileEntity);
				}
			}*/
			if (CommonUtil.isValidObject(customerDetailId) && customerService.deleteCustomerDetails(Integer.parseInt(customerDetailId))) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_CUSTOMER_DETAILS_SUCCESSFULLY));
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG, Constants.ACTION_DELETE, "Customer Details Deleted Successfully: " + programName, sessionId);
			}/* else {
				resultMap.put("status", Constants.FAIL);
				if (!customerService.duplicateProgramGenerateFileDetails(Integer.parseInt(customerDetailId))) {
					if (CommonUtil.isValidObject(generateFileEntities) && generateFileEntities.size() > 0) {
						for (ProgramGenerateFileEntity generateFileEntity : generateFileEntities) {
							customerService.saveProgramGenerateFileEntity(generateFileEntity);
						}
					}
				}
				if (CommonUtil.isValidObject(programTemplateEntities) && programTemplateEntities.size() > 0) {
					for (ProgramTemplateEntity programTemplateEntity : programTemplateEntities) {
						programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
						programTemplateModel.setLabel(programTemplateEntity.getLabel());
						if (!customerService.duplicateProgaramTemplate(programTemplateModel)) {
							customerService.saveProgramTemplate(programTemplateEntity);
						}
					}
				}
			}*/
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			/*if (!customerService.duplicateProgramGenerateFileDetails(Integer.parseInt(customerDetailId))) {
				if (CommonUtil.isValidObject(generateFileEntities) && generateFileEntities.size() > 0) {
					for (ProgramGenerateFileEntity generateFileEntity : generateFileEntities) {
						customerService.saveProgramGenerateFileEntity(generateFileEntity);
					}
				}
			}
			if (CommonUtil.isValidObject(programTemplateEntities) && programTemplateEntities.size() > 0) {
				for (ProgramTemplateEntity programTemplateEntity : programTemplateEntities) {
					programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
					programTemplateModel.setLabel(programTemplateEntity.getLabel());
					if (!customerService.duplicateProgaramTemplate(programTemplateModel)) {
						customerService.saveProgramTemplate(programTemplateEntity);
					}
				}
			}*/
			logger.error("Exception in GeneralConfigController.deleteCustomerDetails(): "+ ExceptionUtils.getFullStackTrace(e));
			if (e instanceof RctException) {
				resultMap.put("reason", e.getMessage());
			} else {
				resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_DETAILS_ASSOSIATED));
			}
		}
		return resultMap;
	}

	/**
	 * This method will update the program Template
	 * 
	 * @param programDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.PROGRAM_TEMPLATE_UPDATE, method = RequestMethod.POST)
	public JSONObject programTemplateUpdate(@RequestBody JSONObject programDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean status = false;
		List<CustomerDetailsEntity> programList = null;
		try {
			sessionId = programDetails.get("sessionId").toString();
			serviceToken = programDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			ProgramTemplateEntity objProgramTemplateEntity = new Gson().fromJson(
					programDetails.toJSONString((Map) programDetails.get("programTemplateDetails")),
					ProgramTemplateEntity.class);

			if (objProgramTemplateEntity != null) {
				if (objProgramTemplateEntity.getConfigType() != null
						&& objProgramTemplateEntity.getConfigType().equals("s&r")) {
					List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
					Map<String, String> configDetailList = new HashMap<>();

					Map<String, Object> obj = (Map<String, Object>) programDetails.get("programTemplateDetails");
					String type = obj.get("inputType").toString();
					Map<String, Object> obj1 = (Map<String, Object>) obj.get("programDetailsEntity");
					String program = (String) obj1.get("programName");
					if (type.equals("dropdown")) {
						User user = UserSessionPool.getInstance().getSessionUser(sessionId);
						if (user.getRoleId() <= 3) {
							CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
							CustomerEntity customerEntity = new CustomerEntity();
							customerEntity.setId(user.getCustomerId());
							customerDetailsModel.setCustomerEntity(customerEntity);
							programList = customerService.getCustomerDetailsList(customerDetailsModel);
						} else {
							programList = customerService.getProgramDetailsList(user);
						}
						configDetailModelList = customerService.getProgTemplateDetails(configDetailModelList,
								programList, "s&r");
						for (ProgramTemplateModel temp : configDetailModelList) {
							if (temp.getProgramDetailsEntity().getProgramName().equals(program))
								configDetailList.put(temp.getLabel(), temp.getValue());
						}
						if (obj.get("label").equals("PREMIGRATION_SCHEDULE")) {
//							if (obj.get("value").equals("D-0")) {
//								resultMap.put("status", Constants.FAIL);
//								resultMap.put("reason", "Please Schedule Dates Less than D-0");
//								return resultMap;
//							}
						} else if (obj.get("label").equals("NE_GROW_SCHEDULE")) {
							if (configDetailList.get("PREMIGRATION_SCHEDULE") != null && !configDetailList.get("PREMIGRATION_SCHEDULE").equals("OFF") ) {
								/*
									ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
	
									if (!ObjectUtils.isEmpty(objProgramTemplateEntity)
											&& StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {
										String scheduledDateTime = objProgramTemplateEntity.getValue();										
										List<String> scheduleDateTimeFormat = Arrays.stream(scheduledDateTime.split("\\|")).collect(Collectors.toList());
										String time = scheduleDateTimeFormat.get(scheduleDateTimeFormat.size()-1);
										//scheduleDateTimeFormat.remove(scheduleDateTimeFormat.size()-1);
										List<String> dateTimeValues = scheduleDateTimeFormat.stream().map(date -> (date +" "+time )).collect(Collectors.toList());
										taskSchedulerConfig.setCrontabTiming(dateTimeValues.get(0));
										
										
								}*/
//								if (obj.get("value").equals("D-0") && !obj.get("value").equals("OFF")) {
//									resultMap.put("status", Constants.FAIL);
//									resultMap.put("reason", "Please Schedule Dates Less than D-0!!");
//									return resultMap;
//								}

							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Please Schedule Pre-Migration!!");
								return resultMap;
							}

						} else if (obj.get("label").equals("MIGRATION_SCHEDULE")) {
							if (configDetailList.get("PREMIGRATION_SCHEDULE") != null && !configDetailList.get("PREMIGRATION_SCHEDULE").equals("OFF")) {
								if (!obj.get("value").equals("D-0") && !obj.get("value").equals("OFF")) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", "Please Schedule Migration as D-0!!");
									return resultMap;
								}
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Please Schedule Pre-Migration!!");
								return resultMap;
							}
						} else if (obj.get("label").equals("POST_MIGRATION_AUDIT_SCHEDULE")) {
						//	if (configDetailList.get("PREMIGRATION_SCHEDULE") != null && !configDetailList.get("PREMIGRATION_SCHEDULE").equals("OFF")) {
								if (!obj.get("value").equals("D-0") && !obj.get("value").equals("OFF")) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", "Please Schedule Post Migration Audit as D-0!!");
									return resultMap;
								}
//							} else {
//								resultMap.put("status", Constants.FAIL);
//								resultMap.put("reason", "Please Schedule Pre-Migration!!");
//								return resultMap;
//							}

						} else if (obj.get("label").equals("POST_MIGRATION_RANATP_SCHEDULE ")) {
							if (configDetailList.get("PREMIGRATION_SCHEDULE") != null && !configDetailList.get("PREMIGRATION_SCHEDULE").equals("OFF")) {
								if (!obj.get("value").equals("D-0") && !obj.get("value").equals("OFF")) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", "Please Schedule PostMigration RAN-ATP as D-0!!");
									return resultMap;
								}
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Please Schedule Pre-Migration!!");
								return resultMap;
							}
						}
					}

				}
				status = customerService.saveProgramTemplate(objProgramTemplateEntity);
				if (status) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
							Constants.ACTION_UPDATE, "Program Template Details Updated Successfully for Template: "
									+ objProgramTemplateEntity.getLabel(),
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_EDIT_CONFIGURATION_DETAILS));
				}

			}

		} catch (Exception e) {
			logger.error("Exception in GeneralConfigController.programTemplateUpdate(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EDIT_CONFIGURATION_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will Save Ne Version Details to DB
	 * 
	 * @param neVersionDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_NE_VERSION_DETAILS, method = RequestMethod.POST)
	public JSONObject saveNeVersion(@RequestBody JSONObject neVersionDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = neVersionDetails.get("sessionId").toString();
			serviceToken = neVersionDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			NeVersionModel neVersionModel = new Gson().fromJson(
					JSONObject.toJSONString((Map) neVersionDetails.get("neVersionDetails")), NeVersionModel.class);
			NeVersionEntity neVersionEntity = neVersionDto.getNeVersionEntity(neVersionModel, sessionId);
			boolean isDuplicate = neVersionService.duplicateNeVersion(neVersionModel);
			if (isDuplicate) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_VERSION_DETAILS_ALREADY_EXIST));
				return resultMap;
			}
			if (neVersionEntity != null) {
				if (neVersionService.createNeVersion(neVersionEntity)) {
					if (CommonUtil.isValidObject(neVersionEntity.getId()) && neVersionEntity.getId() > 0) {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_NE_VERSION_CONFIG, Constants.ACTION_UPDATE,
								"NE Version Details Updated Successfully For: " + neVersionModel.getNeVersion(),
								sessionId);
					} else {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_NE_VERSION_CONFIG, Constants.ACTION_SAVE,
								"NE Version " + neVersionModel.getNeVersion() + " Created Successfully", sessionId);
					}
					resultMap.put("status", Constants.SUCCESS);

				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error(
					"Exception in GeneralConfigController.saveNeVersion(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
	}

	/**
	 * This method will Get Ne Version Details to DB
	 * 
	 * @param neVersionDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_NE_VERSION_DETAILS, method = RequestMethod.POST)
	public JSONObject getNeVersionDetails(@RequestBody JSONObject neVersionDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		List<CustomerDetailsEntity> programList = null;
		try {
			sessionId = neVersionDetails.get("sessionId").toString();
			serviceToken = neVersionDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);

			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(user.getCustomerId());
				customerDetailsModel.setCustomerEntity(customerEntity);
				programList = customerService.getCustomerDetailsList(customerDetailsModel);
				resultMap.put("programNamesList", programList);
			} else {
				programList = customerService.getProgramDetailsList(user);
				resultMap.put("programNamesList", programList);
			}
			List<NeVersionEntity> neVersionEntities = neVersionService.getNeVersionList(null);
			if (neVersionEntities != null && neVersionEntities.size() > 0) {
				Set<Integer> programIdList = programList.stream().map(x -> x.getId()).collect(Collectors.toSet());
				neVersionEntities = neVersionEntities.stream()
						.filter(x -> (programIdList.contains(x.getProgramDetailsEntity().getId())))
						.collect(Collectors.toList());
			}
			resultMap.put("neVersionDetails", neVersionEntities);
			resultMap.put("status", Constants.SUCCESS);
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in GeneralConfigController.getNeVersionDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			return resultMap;
		}
	}

	/**
	 * This api deletes the NeVersion
	 * 
	 * @param neVersionDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_NE_VERSION_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteNeVersionDetails(@RequestBody JSONObject neVersionDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String neVersionDetailId = null;
		try {
			sessionId = neVersionDetails.get("sessionId").toString();
			serviceToken = neVersionDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			neVersionDetailId = neVersionDetails.get("id").toString();
			NeVersionEntity neVersionEntity = neVersionService.getNeVersionById(Integer.parseInt(neVersionDetailId));
			if (CommonUtil.isValidObject(neVersionEntity) && CommonUtil.isValidObject(neVersionDetailId)
					&& neVersionService.deleteNeVersionDetails(Integer.parseInt(neVersionDetailId))) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_NE_VERSION_DETAILS_SUCCESSFULLY));
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NE_VERSION_CONFIG,
						Constants.ACTION_DELETE,
						"NE Version Details Deleted Successfully: " + neVersionEntity.getNeVersion(), sessionId);
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_VERSION_DETAILS_ASSOSIATED));
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GeneralConfigController.deleteNeVersionDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			if (e instanceof RctException) {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_VERSION_DETAILS_ASSOSIATED));
			} else {
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NE_VERSION_DETAILS));
			}
		}
		return resultMap;
	}

	/**
	 * This method will update scheduling Details template to DB
	 * 
	 * @param schedulingTemplateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.SCHEDULING_TEMPLATE_UPDATE, method = RequestMethod.POST)
	public JSONObject updateSchedulingTemplate(@RequestBody JSONObject schedulingTemplateDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean status = false;
		try {
			sessionId = schedulingTemplateDetails.get("sessionId").toString();
			serviceToken = schedulingTemplateDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			SchedulingReportsTemplateEntity schedulingReportsTemplateEntity = new Gson().fromJson(
					schedulingTemplateDetails
							.toJSONString((Map) schedulingTemplateDetails.get("schedulingTemplateDetails")),
					SchedulingReportsTemplateEntity.class);
			if (schedulingReportsTemplateEntity != null) {
				status = customerService.saveSchedulingTemplate(schedulingReportsTemplateEntity);
				if (status) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
							Constants.ACTION_UPDATE, "Scheduling Template Details Updated Successfully For: "
									+ schedulingReportsTemplateEntity.getLabel(),
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in GeneralConfigController.updateSchedulingTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_SCHEDULING_TEMPLATE));
		}
		return resultMap;
	}

	@PostConstruct
	public void generateSystemReport() {
		try {
			genSysReport.generatesysReport();
		}catch (Exception e) {
			logger.error("Exception in GeneralConfigController.generateSystemReport(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		
	}
}

