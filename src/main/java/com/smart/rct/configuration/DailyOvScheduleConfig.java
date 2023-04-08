package com.smart.rct.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
//import org.assertj.core.util.Arrays;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.controller.RunTestController;
import com.smart.rct.migration.controller.WorkFlowManagementController;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.usermanagement.controller.LoginActionController;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repository.UserDetailsRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.PasswordCrypt;

@Component
public class DailyOvScheduleConfig {

	@Autowired
	OvScheduledTaskService ovScheduledTaskService;

	@Autowired
	WorkFlowManagementController workFlowManagementController;

	@Autowired
	LoginActionController loginActionController;

	@Autowired
	RunTestController runTestController;

	@Autowired
	WorkFlowManagementService workFlowManagementService;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	FetchProcessService fetchProcessService;

	@Autowired
	CustomerService customerService;

	@Autowired
	Environment env;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	FetchProcessRepository fetchProcessRepository;

	final static Logger logger = LoggerFactory.getLogger(DailyOvScheduleConfig.class);

	public void OvScheduledTasksExcution(List<String> forceFetchIds,CustomerDetailsEntity programmeEntity,String type) {

		try {
			OvInteractionModel ovInteractionModel = null;
			OvAutomationModel ovAutomationModel = null;
			Map<String, List<OvScheduledEntity>> scheduledDetailsMap = ovScheduledTaskService.getOvScheduledDetails(forceFetchIds,programmeEntity);
			ovInteractionModel = customerService.getOvInteractionTemplate();
			ovAutomationModel = customerService.getOvAutomationTemplate();
				ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
				programTemplateModel.setProgramDetailsEntity(programmeEntity);
				programTemplateModel.setConfigType("s&r");
				List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
						.getScheduledDaysDetails(programTemplateModel);
			
			String neGrowAutomation = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.NE_GROW_AUTOMATION.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().get();
			
			if (!ObjectUtils.isEmpty(scheduledDetailsMap)) {
				if (scheduledDetailsMap.containsKey(Constants.PREMIGRATION_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PREMIGRATION_SCHEDULE))) {
					List<OvScheduledEntity> preMigOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PREMIGRATION_SCHEDULE);
						if(ovAutomationModel!=null && ovAutomationModel.getPreMigration().equals("ON")&& type.equals("OV- Auto Fetch") 
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigEnvExport(preMigOvScheduledEntity);
					
				}

				if (scheduledDetailsMap.containsKey(Constants.NE_GROW_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.NE_GROW_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.NE_GROW_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&&type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigEnvExportNegrow(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				////
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_NEGROW_MIG_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_NEGROW_MIG_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_NEGROW_MIG_POSTMIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&& ovAutomationModel.getPreMigration().equals("ON")
								&& ovAutomationModel.getMigration().equals("ON") && 
								ovAutomationModel.getPostAudit().equals("ON") && type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigNeGrowMigPost(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_NEGROW_MIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_NEGROW_MIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_NEGROW_MIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&& ovAutomationModel.getPreMigration().equals("ON") &&
								ovAutomationModel.getMigration().equals("ON") && type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigNegrowMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_MIG_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_MIG_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_MIG_POSTMIG_SCHEDULE);
						if(ovAutomationModel.getPreMigration().equals("ON")&& ovAutomationModel.getMigration().equals("ON")&&
								ovAutomationModel.getPostAudit().equals("ON")&&type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigMigPostMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_NEGROW_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_NEGROW_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_NEGROW_POSTMIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON") && ovAutomationModel.getPreMigration().equals("ON")
								&& ovAutomationModel.getPostAudit().equals("ON")&&type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigNegrowPostMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_MIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_MIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_MIG_SCHEDULE);
						if(ovAutomationModel.getMigration().equals("ON")&& ovAutomationModel.getPreMigration().equals("ON")
								&&type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingMigration(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_POSTMIG_SCHEDULE);
						if(ovAutomationModel.getPostAudit().equals("ON")&& ovAutomationModel.getPreMigration().equals("ON")
								&& type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPostMigAudit(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				if (scheduledDetailsMap.containsKey(Constants.NEGROW_MIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.NEGROW_MIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.NEGROW_MIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&& ovAutomationModel.getMigration().equals("ON")
								&&type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch") )
							schedulingPreMigNegrowMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				if (scheduledDetailsMap.containsKey(Constants.MIG_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.MIG_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.MIG_POSTMIG_SCHEDULE);
						if(ovAutomationModel.getMigration().equals("ON")&& ovAutomationModel.getPostAudit().equals("ON")
								&& type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigMigPostMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				if (scheduledDetailsMap.containsKey(Constants.NEGROW_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.NEGROW_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.NEGROW_POSTMIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&& ovAutomationModel.getPostAudit().equals("ON") &&
								type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch") )
							schedulingPreMigNegrowPostMig(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				if (scheduledDetailsMap.containsKey(Constants.NEGROW_MIG_POSTMIG_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.NEGROW_MIG_POSTMIG_SCHEDULE))) {
					List<OvScheduledEntity> neGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.NEGROW_MIG_POSTMIG_SCHEDULE);
						if(neGrowAutomation!=null && neGrowAutomation.equals("ON")&& ovAutomationModel !=null && ovAutomationModel.getMigration().equals("ON")
								&& ovAutomationModel.getPostAudit().equals("ON") && type.equals("OV- Auto Fetch")
								|| type.equals("OV- Force Fetch"))
							schedulingPreMigNeGrowMigPost(neGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(neGrowOvScheduledEntity);
				}
				
////
				if (scheduledDetailsMap.containsKey(Constants.MIGRATION_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.MIGRATION_SCHEDULE))) {
					List<OvScheduledEntity> migOvScheduledEntity = scheduledDetailsMap
							.get(Constants.MIGRATION_SCHEDULE);
						if(ovAutomationModel!=null && ovAutomationModel.getMigration().equals("ON")&& type.equals("OV- Auto Fetch")
								||type.equals("OV- Force Fetch"))
							schedulingMigration(migOvScheduledEntity);
						else
							schedulingPreMigEnvExport(migOvScheduledEntity);
					}
				}

				if (scheduledDetailsMap.containsKey(Constants.POST_MIGRATION_AUDIT_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.POST_MIGRATION_AUDIT_SCHEDULE))) {
					List<OvScheduledEntity> postAuditOvScheduledEntity = scheduledDetailsMap
							.get(Constants.POST_MIGRATION_AUDIT_SCHEDULE);
						if(ovAutomationModel!=null && ovAutomationModel.getPostAudit().equals("ON")&&type.equals("OV- Auto Fetch")
								||type.equals("OV- Force Fetch"))
							schedulingPostMigAudit(postAuditOvScheduledEntity);
						else 
							schedulingPreMigEnvExport(postAuditOvScheduledEntity);
					}
				

				if (scheduledDetailsMap.containsKey(Constants.POST_MIGRATION_RANATP_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.POST_MIGRATION_RANATP_SCHEDULE))) {
					List<OvScheduledEntity> postRanOvScheduledEntity = scheduledDetailsMap
							.get(Constants.POST_MIGRATION_RANATP_SCHEDULE);
					if(ovAutomationModel!=null && ovAutomationModel.getRanAtp().equals("ON")&&type.equals("OV- Auto Fetch")
								||type.equals("OV- Force Fetch"))
							schedulingPostMigRanAtp(postRanOvScheduledEntity);
						else
							schedulingPreMigEnvExport(postRanOvScheduledEntity);
					}
				

				if (scheduledDetailsMap.containsKey(Constants.PRE_MIG_NEGROW_SCHEDULE)
						&& !ObjectUtils.isEmpty(scheduledDetailsMap.get(Constants.PRE_MIG_NEGROW_SCHEDULE))) {
					List<OvScheduledEntity> preMigNeGrowOvScheduledEntity = scheduledDetailsMap
							.get(Constants.PRE_MIG_NEGROW_SCHEDULE);
						if (ovAutomationModel != null && neGrowAutomation!=null && neGrowAutomation.equals("ON")
								&& ovAutomationModel.getPreMigration().equals("ON")&&type.equals("OV- Auto Fetch")||type.equals("OV- Force Fetch")
								&& neGrowAutomation!=null && neGrowAutomation.equals("ON"))
							schedulingPreMigEnvExportNegrow(preMigNeGrowOvScheduledEntity);
						else
							schedulingPreMigEnvExport(preMigNeGrowOvScheduledEntity);
				}
			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	
	@SuppressWarnings("unchecked")
	public void schedulingPreMigMigPostMig(List<OvScheduledEntity> preMigOvScheduledEntity) {

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject postMigJsonObject = new JSONObject();
				JSONObject migJsonObject = new JSONObject();
				JSONObject neGrowJsonObject = new JSONObject();
				
				JSONObject runTestFormDetails = new JSONObject();
				JSONObject migrunTestFormDetails = new JSONObject();
				JSONObject postrunTestFormDetails = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();

				JSONObject neGrowStatusDetails = new JSONObject();
				
				JSONObject useCasesDetails = new JSONObject();
				JSONObject useCaseJSONObject = new JSONObject();
				List<String>postMigCases = new LinkedList<String>();
				List<String> negrowscript = new ArrayList<>();
				List<String> negrowuseCase = new ArrayList<>();
				
				List<String> migscript = new ArrayList<>();
				List<String> miguseCase = new ArrayList<>();
				
				List<Map> useCaseLst = null;
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject useCaseDetails = new JSONObject();
				JSONObject scriptDetails = new JSONObject();
				List<JSONObject> useCase = new ArrayList<>();
				List<JSONObject> script = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				List<String> enbIds = new ArrayList<>();
				String enbName = null;

				try {

					ovScheduledEntity.setMigrationStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					ovScheduledEntity.setPostmigrationAuditStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName", listCIQDetailsModel.get(0).getSiteName());
					
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", preMigJsonObject);

					
					//Mig
					migJsonObject.put("sessionId", loginUser.getTokenKey());
					migJsonObject.put("serviceToken", loginUser.getServiceToken());
					migJsonObject.put("userName", user.getUserName());
					migJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					migJsonObject.put("customerId", user.getCustomerId());
					migJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					migJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					migJsonObject.put("requestType", "RUN_TEST");
					migJsonObject.put("migrationType", "migration");
					migJsonObject.put("migrationSubType", "precheck");
					
					migrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					migrunTestFormDetails.put("checklistFileName", "");
					migrunTestFormDetails.put("checklistFilePath", "");
					migrunTestFormDetails.put("neDetails", enbList);

					migrunTestFormDetails.put("password", "");
					migrunTestFormDetails.put("lsmId", "");
					migrunTestFormDetails.put("lsmName", "");
					migrunTestFormDetails.put("lsmVersion", "");
					migrunTestFormDetails.put("currentPassword", true);
					migrunTestFormDetails.put("rfScriptFlag", true);
					migrunTestFormDetails.put("type", "OV");
					migrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String migrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.MIGRATION_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] migCases = migrationUsecases.split(",");
						for(int i=0;i<migCases.length;i++) {
						miguseCase.add(migCases[i]);
						}
					}
					/*
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							miguseCase.add("ENDC_X2_UseCase");
							miguseCase.add("Anchor_CSL_UseCase");
							miguseCase.add("CSL_Usecase");
							miguseCase.add("AU_Commision_Usecase");
							miguseCase.add("ACPF_A1A2_Config_Usecase");
							miguseCase.add("NBR_RF_Scripts_Usecase");
							miguseCase.add("GP_Script_Usecase");
							break;
						case "VZN-5G-DSS":
							miguseCase.add("Pre-Check_RF_Scripts_Usecase");
							miguseCase.add("Rollback_RF_Scripts_Usecase");
							miguseCase.add("Cutover_RF_Scripts_Usecase");
							break;
						default:
							miguseCase.add("CommissionScriptUsecase");
							miguseCase.add("RFUsecase");
						}
					}
					*/
					migrunTestFormDetails.put("useCase", miguseCase);
					migrunTestFormDetails.put("scripts", migscript);
					migJsonObject.put("runTestFormDetails", migrunTestFormDetails);
					
					
					//To get Dynamic UseCases Post
					postMigJsonObject.put("sessionId", loginUser.getTokenKey());
					postMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					postMigJsonObject.put("userName", user.getUserName());
					postMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					postMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					postMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					postMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					postMigJsonObject.put("requestType", "RUN_TEST");
					postMigJsonObject.put("migrationType", "postmigration");
					postMigJsonObject.put("migrationSubType", "AUDIT");
					
					postrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					postrunTestFormDetails.put("checklistFileName", "");
					postrunTestFormDetails.put("checklistFilePath", "");
					postrunTestFormDetails.put("neDetails", enbList);

					postrunTestFormDetails.put("password", "");
					postrunTestFormDetails.put("lsmId", "");
					postrunTestFormDetails.put("lsmName", "");
					postrunTestFormDetails.put("lsmVersion", "");
					postrunTestFormDetails.put("currentPassword", true);
					postrunTestFormDetails.put("rfScriptFlag", true);
					postrunTestFormDetails.put("type", "OV");
					postrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					//add post mig usecases
if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String postMigrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.POST_MIG_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						 String[] postMigCasesCopy = postMigrationUsecases.split(",");
						 for (String string : postMigCasesCopy) {
							 
						useCasesDetails = runTestController.getMigUseCases(postMigJsonObject); 
						 useCase.add(useCasesDetails);
						 }}

					/*
					useCaseJSONObject.put("sessionId", loginUser.getTokenKey());
					useCaseJSONObject.put("serviceToken", loginUser.getServiceToken());
					useCaseJSONObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					useCaseJSONObject.put("migrationType", "postmigration");
					useCaseJSONObject.put("migrationSubType", "AUDIT");
					useCaseJSONObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					useCaseJSONObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					useCaseJSONObject.put("programId",
							Integer.parseInt(ovScheduledEntity.getCustomerDetailsEntity().getId().toString()));
					useCaseJSONObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					enbIds.add(ovScheduledEntity.getNeId().toString());
					useCaseJSONObject.put("enbID", enbIds);

					useCasesDetails = runTestController.getMigUse.chnptCases(useCaseJSONObject);

					useCaseLst = (List<Map>) useCasesDetails.get("useCaseList");
					for (int i = 0; i < useCaseLst.size(); i++) {
						useCaseDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
						useCaseDetails.put("useCaseId", useCaseLst.get(i).get("useCaseId"));
						useCaseDetails.put("executionSequence", useCaseLst.get(i).get("executionSequence"));
						useCaseDetails.put("ucSleepInterval", useCaseLst.get(i).get("ucSleepInterval"));

						useCase.add(useCaseDetails);
						List<Map> scriptLst = (List<Map>) useCaseLst.get(i).get("scripts");
						for (int j = 0; j < scriptLst.size(); j++) {
							scriptDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
							scriptDetails.put("scriptName", scriptLst.get(j).get("scriptName"));
							scriptDetails.put("scriptId", scriptLst.get(j).get("scriptId"));
							scriptDetails.put("scriptExeSequence", scriptLst.get(j).get("scriptExeSequence"));
							scriptDetails.put("scriptSleepInterval", scriptLst.get(j).get("scriptSleepInterval"));
							scriptDetails.put("useGeneratedScript", scriptLst.get(j).get("useGeneratedScript"));
							script.add(scriptDetails);
						}
					*/
					
					postrunTestFormDetails.put("useCase", useCase);
					postrunTestFormDetails.put("scripts", script);
					postMigJsonObject.put("runTestFormDetails", postrunTestFormDetails);

					
					finalJsonObject.put("negrow", null);
					finalJsonObject.put("migration", migJsonObject);
					finalJsonObject.put("postmigration", postMigJsonObject);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");
//					finalJsonObject.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						finalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					neGrowStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.error("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	
	}
	@SuppressWarnings("unchecked")
	public void schedulingPreMigNegrowPostMig(List<OvScheduledEntity> preMigOvScheduledEntity) {
		

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject postMigJsonObject = new JSONObject();
				JSONObject migJsonObject = new JSONObject();
				JSONObject neGrowJsonObject = new JSONObject();
				
				JSONObject runTestFormDetails = new JSONObject();
				JSONObject migrunTestFormDetails = new JSONObject();
				JSONObject postrunTestFormDetails = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();

				JSONObject neGrowStatusDetails = new JSONObject();
				
				JSONObject useCasesDetails = new JSONObject();
				JSONObject useCaseJSONObject = new JSONObject();
				
				List<String> negrowscript = new ArrayList<>();
				List<String> negrowuseCase = new ArrayList<>();
				
				List<String> migscript = new ArrayList<>();
				List<String> miguseCase = new ArrayList<>();
				
				List<Map> useCaseLst = null;
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject useCaseDetails = new JSONObject();
				JSONObject scriptDetails = new JSONObject();
				List<JSONObject> useCase = new ArrayList<>();
				List<JSONObject> script = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				List<String> enbIds = new ArrayList<>();
				String enbName = null;

				try {

					ovScheduledEntity.setPostmigrationAuditStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName",listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", preMigJsonObject);

					
					//NE Grow 
					///
					neGrowJsonObject.put("sessionId", loginUser.getTokenKey());
					neGrowJsonObject.put("serviceToken", loginUser.getServiceToken());
					neGrowJsonObject.put("userName", user.getUserName());
					neGrowJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					neGrowJsonObject.put("customerId", user.getCustomerId());
					neGrowJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					neGrowJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					neGrowJsonObject.put("requestType", "RUN_TEST");
					neGrowJsonObject.put("migrationType", "premigration");
					neGrowJsonObject.put("migrationSubType", "NEGrow");
					
					
					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					runTestFormDetails.put("checklistFileName", "");
					runTestFormDetails.put("checklistFilePath", "");
					runTestFormDetails.put("neDetails", enbList);
					// runTestFormDetails.put("id",Integer.parseInt(preMigDetails.get("wfmid").toString()));
					runTestFormDetails.put("password", "");
					runTestFormDetails.put("lsmId", "");
					runTestFormDetails.put("lsmName", "");
					runTestFormDetails.put("lsmVersion", "");
					runTestFormDetails.put("currentPassword", true);
					runTestFormDetails.put("rfScriptFlag", true);
					runTestFormDetails.put("type", "OV");
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String neGrowUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.NE_GROW_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] neGrowCases=neGrowUsecases.split(",");
						for(int i=0;i<neGrowCases.length;i++) {
							negrowuseCase.add(neGrowCases[i]);
						}
					}
					/*
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							negrowuseCase.add("pnp");
							//useCase.add("AUCaCell");
							//useCase.add("AU");
							break;
						case "VZN-4G-USM-LIVE":
							negrowuseCase.add("GrowEnb");
							break;
						default:
							negrowuseCase.add("GrowEnb");
						}
					}
					*/
					runTestFormDetails.put("useCase", negrowuseCase);
					runTestFormDetails.put("scripts", negrowscript);
					neGrowJsonObject.put("runTestFormDetails", runTestFormDetails);
					
					
					//To get Dynamic UseCases Post
					postMigJsonObject.put("sessionId", loginUser.getTokenKey());
					postMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					postMigJsonObject.put("userName", user.getUserName());
					postMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					postMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					postMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					postMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					postMigJsonObject.put("requestType", "RUN_TEST");
					postMigJsonObject.put("migrationType", "postmigration");
					postMigJsonObject.put("migrationSubType", "AUDIT");
					
					postrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					postrunTestFormDetails.put("checklistFileName", "");
					postrunTestFormDetails.put("checklistFilePath", "");
					postrunTestFormDetails.put("neDetails", enbList);

					postrunTestFormDetails.put("password", "");
					postrunTestFormDetails.put("lsmId", "");
					postrunTestFormDetails.put("lsmName", "");
					postrunTestFormDetails.put("lsmVersion", "");
					postrunTestFormDetails.put("currentPassword", true);
					postrunTestFormDetails.put("rfScriptFlag", true);
					postrunTestFormDetails.put("type", "OV");
					postrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					//add post usecases
if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String postMigrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.POST_MIG_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						 String[] postMigCasesCopy = postMigrationUsecases.split(",");
						 for (String string : postMigCasesCopy) {
							 
							 postMigJsonObject.put("useCaseName", string);						 
						useCasesDetails = runTestController.getMigUseCases(postMigJsonObject); 
						 useCase.add(useCasesDetails);
						 }}
					/*
					useCaseJSONObject.put("sessionId", loginUser.getTokenKey());
					useCaseJSONObject.put("serviceToken", loginUser.getServiceToken());
					useCaseJSONObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					useCaseJSONObject.put("migrationType", "postmigration");
					useCaseJSONObject.put("migrationSubType", "AUDIT");
					useCaseJSONObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					useCaseJSONObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					useCaseJSONObject.put("programId",
							Integer.parseInt(ovScheduledEntity.getCustomerDetailsEntity().getId().toString()));
					useCaseJSONObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					enbIds.add(ovScheduledEntity.getNeId().toString());
					useCaseJSONObject.put("enbID", enbIds);

					useCasesDetails = runTestController.getMigUseCases(useCaseJSONObject);

					useCaseLst = (List<Map>) useCasesDetails.get("useCaseList");
					for (int i = 0; i < useCaseLst.size(); i++) {
						useCaseDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
						useCaseDetails.put("useCaseId", useCaseLst.get(i).get("useCaseId"));
						useCaseDetails.put("executionSequence", useCaseLst.get(i).get("executionSequence"));
						useCaseDetails.put("ucSleepInterval", useCaseLst.get(i).get("ucSleepInterval"));

						useCase.add(useCaseDetails);
						List<Map> scriptLst = (List<Map>) useCaseLst.get(i).get("scripts");
						for (int j = 0; j < scriptLst.size(); j++) {
							scriptDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
							scriptDetails.put("scriptName", scriptLst.get(j).get("scriptName"));
							scriptDetails.put("scriptId", scriptLst.get(j).get("scriptId"));
							scriptDetails.put("scriptExeSequence", scriptLst.get(j).get("scriptExeSequence"));
							scriptDetails.put("scriptSleepInterval", scriptLst.get(j).get("scriptSleepInterval"));
							scriptDetails.put("useGeneratedScript", scriptLst.get(j).get("useGeneratedScript"));
							script.add(scriptDetails);
						}

					}*/
					postrunTestFormDetails.put("useCase", useCase);
					postrunTestFormDetails.put("scripts", script);
					postMigJsonObject.put("runTestFormDetails", postrunTestFormDetails);

					
					finalJsonObject.put("negrow", neGrowJsonObject);
					finalJsonObject.put("migration", null);
					finalJsonObject.put("postmigration", postMigJsonObject);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");
//					finalJsonObject.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						finalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					neGrowStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.error("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	
		
	}
	@SuppressWarnings("unchecked")
	public void schedulingPreMigNegrowMig(List<OvScheduledEntity> preMigOvScheduledEntity) {

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject migJsonObject = new JSONObject();
				JSONObject neGrowJsonObject = new JSONObject();
				
				JSONObject runTestFormDetails = new JSONObject();
				JSONObject migrunTestFormDetails = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();

				JSONObject neGrowStatusDetails = new JSONObject();
				
				JSONObject useCasesDetails = new JSONObject();
				JSONObject useCaseJSONObject = new JSONObject();
				
				List<String> negrowscript = new ArrayList<>();
				List<String> negrowuseCase = new ArrayList<>();
				
				List<String> migscript = new ArrayList<>();
				List<String> miguseCase = new ArrayList<>();
				
				List<Map> useCaseLst = null;
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject useCaseDetails = new JSONObject();
				JSONObject scriptDetails = new JSONObject();
				List<JSONObject> useCase = new ArrayList<>();
				List<JSONObject> script = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				List<String> enbIds = new ArrayList<>();
				String enbName = null;

				try {

					ovScheduledEntity.setMigrationStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName",listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", preMigJsonObject);

					
					//NE Grow 
					///
					neGrowJsonObject.put("sessionId", loginUser.getTokenKey());
					neGrowJsonObject.put("serviceToken", loginUser.getServiceToken());
					neGrowJsonObject.put("userName", user.getUserName());
					neGrowJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					neGrowJsonObject.put("customerId", user.getCustomerId());
					neGrowJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					neGrowJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					neGrowJsonObject.put("requestType", "RUN_TEST");
					neGrowJsonObject.put("migrationType", "premigration");
					neGrowJsonObject.put("migrationSubType", "NEGrow");
					
					
					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					runTestFormDetails.put("checklistFileName", "");
					runTestFormDetails.put("checklistFilePath", "");
					runTestFormDetails.put("neDetails", enbList);
					// runTestFormDetails.put("id",Integer.parseInt(preMigDetails.get("wfmid").toString()));
					runTestFormDetails.put("password", "");
					runTestFormDetails.put("lsmId", "");
					runTestFormDetails.put("lsmName", "");
					runTestFormDetails.put("lsmVersion", "");
					runTestFormDetails.put("currentPassword", true);
					runTestFormDetails.put("rfScriptFlag", true);
					runTestFormDetails.put("type", "OV");
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String neGrowUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.NE_GROW_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] neGrowCases=neGrowUsecases.split(",");
						for(int i=0;i<neGrowCases.length;i++) {
							negrowuseCase.add(neGrowCases[i]);
						}
					}
					/*
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							negrowuseCase.add("pnp");
							//useCase.add("AUCaCell");
							//useCase.add("AU");
							break;
						case "VZN-4G-USM-LIVE":
							negrowuseCase.add("GrowEnb");//negrow
							break;
						default:
							negrowuseCase.add("GrowEnb");
						}
					}
					*/
					runTestFormDetails.put("useCase", negrowuseCase);
					runTestFormDetails.put("scripts", negrowscript);
					neGrowJsonObject.put("runTestFormDetails", runTestFormDetails);
					
					
					//Mig
					migJsonObject.put("sessionId", loginUser.getTokenKey());
					migJsonObject.put("serviceToken", loginUser.getServiceToken());
					migJsonObject.put("userName", user.getUserName());
					migJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					migJsonObject.put("customerId", user.getCustomerId());
					migJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					migJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					migJsonObject.put("requestType", "RUN_TEST");
					migJsonObject.put("migrationType", "migration");
					migJsonObject.put("migrationSubType", "precheck");
					
					migrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					migrunTestFormDetails.put("checklistFileName", "");
					migrunTestFormDetails.put("checklistFilePath", "");
					migrunTestFormDetails.put("neDetails", enbList);

					migrunTestFormDetails.put("password", "");
					migrunTestFormDetails.put("lsmId", "");
					migrunTestFormDetails.put("lsmName", "");
					migrunTestFormDetails.put("lsmVersion", "");
					migrunTestFormDetails.put("currentPassword", true);
					migrunTestFormDetails.put("rfScriptFlag", true);
					migrunTestFormDetails.put("type", "OV");
					migrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String migrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.MIGRATION_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] migCases = migrationUsecases.split(",");
						for(int i=0;i<migCases.length;i++) {
						miguseCase.add(migCases[i]);
						}
					}
					/*
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							miguseCase.add("ENDC_X2_UseCase");
							miguseCase.add("Anchor_CSL_UseCase");
							miguseCase.add("CSL_Usecase");
							miguseCase.add("AU_Commision_Usecase");
							miguseCase.add("ACPF_A1A2_Config_Usecase");
							miguseCase.add("NBR_RF_Scripts_Usecase");
							miguseCase.add("GP_Script_Usecase");
							break;
						case "VZN-5G-DSS":
							miguseCase.add("Pre-Check_RF_Scripts_Usecase");
							miguseCase.add("Rollback_RF_Scripts_Usecase");//migration
							miguseCase.add("Cutover_RF_Scripts_Usecase");
							break;
						default:
							miguseCase.add("CommissionScriptUsecase");
							miguseCase.add("RFUsecase");
						}
					}*/
					migrunTestFormDetails.put("useCase", miguseCase);
					migrunTestFormDetails.put("scripts", migscript);
					migJsonObject.put("runTestFormDetails", migrunTestFormDetails);

					
					finalJsonObject.put("negrow", neGrowJsonObject);
					finalJsonObject.put("migration", migJsonObject);
					finalJsonObject.put("postmigration", null);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");
//					finalJsonObject.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						finalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					neGrowStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.error("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	
		
	}
	//to do
	@SuppressWarnings("unchecked")
	public void schedulingPreMigNeGrowMigPost(List<OvScheduledEntity> preMigOvScheduledEntity) {
		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject postMigJsonObject = new JSONObject();
				JSONObject migJsonObject = new JSONObject();
				JSONObject neGrowJsonObject = new JSONObject();
				
				JSONObject runTestFormDetails = new JSONObject();
				JSONObject migrunTestFormDetails = new JSONObject();
				JSONObject postrunTestFormDetails = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();

				JSONObject neGrowStatusDetails = new JSONObject();
				
				JSONObject useCasesDetails = new JSONObject();
				JSONObject useCaseJSONObject = new JSONObject();
				
				List<String> negrowscript = new ArrayList<>();
				List<String> negrowuseCase = new ArrayList<>();
				
				List<String> migscript = new ArrayList<>();
				List<String> miguseCase = new ArrayList<>();
				
				List<Map> useCaseLst = null;
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject useCaseDetails = new JSONObject();
				JSONObject scriptDetails = new JSONObject();
				List<JSONObject> useCase = new ArrayList<>();
				List<JSONObject> script = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				List<String> enbIds = new ArrayList<>();
				String enbName = null;

				try {

					ovScheduledEntity.setMigrationStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					ovScheduledEntity.setPostmigrationAuditStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
					neDetails.put("siteName", listCIQDetailsModel.get(0).getSiteName());

					
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", preMigJsonObject);

					
					//NE Grow 
					///
					neGrowJsonObject.put("sessionId", loginUser.getTokenKey());
					neGrowJsonObject.put("serviceToken", loginUser.getServiceToken());
					neGrowJsonObject.put("userName", user.getUserName());
					neGrowJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					neGrowJsonObject.put("customerId", user.getCustomerId());
					neGrowJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					neGrowJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					neGrowJsonObject.put("requestType", "RUN_TEST");
					neGrowJsonObject.put("migrationType", "premigration");
					neGrowJsonObject.put("migrationSubType", "NEGrow");
					
					
					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					runTestFormDetails.put("checklistFileName", "");
					runTestFormDetails.put("checklistFilePath", "");
					runTestFormDetails.put("neDetails", enbList);
					// runTestFormDetails.put("id",Integer.parseInt(preMigDetails.get("wfmid").toString()));
					runTestFormDetails.put("password", "");
					runTestFormDetails.put("lsmId", "");
					runTestFormDetails.put("lsmName", "");
					runTestFormDetails.put("lsmVersion", "");
					runTestFormDetails.put("currentPassword", true);
					runTestFormDetails.put("rfScriptFlag", true);
					runTestFormDetails.put("type", "OV");
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String neGrowUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.NE_GROW_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] neGrowCases=neGrowUsecases.split(",");
						for(int i=0;i<neGrowCases.length;i++) {
							negrowuseCase.add(neGrowCases[i]);
						}
					}
					/*
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							negrowuseCase.add("pnp");
							//useCase.add("AUCaCell");
							//useCase.add("AU");
							break;
						case "VZN-4G-USM-LIVE":
							negrowuseCase.add("GrowEnb");//negrow
							break;
						default:
							negrowuseCase.add("GrowEnb");
						}
					}
					*/
					runTestFormDetails.put("useCase", negrowuseCase);
					runTestFormDetails.put("scripts", negrowscript);
					neGrowJsonObject.put("runTestFormDetails", runTestFormDetails);
					
					
					//Mig
					migJsonObject.put("sessionId", loginUser.getTokenKey());
					migJsonObject.put("serviceToken", loginUser.getServiceToken());
					migJsonObject.put("userName", user.getUserName());
					migJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					migJsonObject.put("customerId", user.getCustomerId());
					migJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					migJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					migJsonObject.put("requestType", "RUN_TEST");
					migJsonObject.put("migrationType", "migration");
					migJsonObject.put("migrationSubType", "precheck");
					
					migrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					migrunTestFormDetails.put("checklistFileName", "");
					migrunTestFormDetails.put("checklistFilePath", "");
					migrunTestFormDetails.put("neDetails", enbList);

					migrunTestFormDetails.put("password", "");
					migrunTestFormDetails.put("lsmId", "");
					migrunTestFormDetails.put("lsmName", "");
					migrunTestFormDetails.put("lsmVersion", "");
					migrunTestFormDetails.put("currentPassword", true);
					migrunTestFormDetails.put("rfScriptFlag", true);
					migrunTestFormDetails.put("type", "OV");
					migrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String migrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.MIGRATION_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] migCases = migrationUsecases.split(",");
						for(int i=0;i<migCases.length;i++) {
						miguseCase.add(migCases[i]);
						}
					}
					/*
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							miguseCase.add("ENDC_X2_UseCase");
							miguseCase.add("Anchor_CSL_UseCase");
							miguseCase.add("CSL_Usecase");
							miguseCase.add("AU_Commision_Usecase");
							miguseCase.add("ACPF_A1A2_Config_Usecase");
							miguseCase.add("NBR_RF_Scripts_Usecase");
							miguseCase.add("GP_Script_Usecase");
							break;
						case "VZN-5G-DSS":
							miguseCase.add("Pre-Check_RF_Scripts_Usecase");//migration
							miguseCase.add("Rollback_RF_Scripts_Usecase");
							miguseCase.add("Cutover_RF_Scripts_Usecase");
							break;
						default:
							miguseCase.add("CommissionScriptUsecase");
							miguseCase.add("RFUsecase");
						}
					}
					*/
					migrunTestFormDetails.put("useCase", miguseCase);
					migrunTestFormDetails.put("scripts", migscript);
					migJsonObject.put("runTestFormDetails", migrunTestFormDetails);
					
					
					//To get Dynamic UseCases Post
					postMigJsonObject.put("sessionId", loginUser.getTokenKey());
					postMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					postMigJsonObject.put("userName", user.getUserName());
					postMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					postMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					postMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					postMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					postMigJsonObject.put("requestType", "RUN_TEST");
					postMigJsonObject.put("migrationType", "postmigration");
					postMigJsonObject.put("migrationSubType", "AUDIT");
					
					postrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					postrunTestFormDetails.put("checklistFileName", "");
					postrunTestFormDetails.put("checklistFilePath", "");
					postrunTestFormDetails.put("neDetails", enbList);

					postrunTestFormDetails.put("password", "");
					postrunTestFormDetails.put("lsmId", "");
					postrunTestFormDetails.put("lsmName", "");
					postrunTestFormDetails.put("lsmVersion", "");
					postrunTestFormDetails.put("currentPassword", true);
					postrunTestFormDetails.put("rfScriptFlag", true);
					postrunTestFormDetails.put("type", "OV");
					postrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					//add post mig usecases
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String postMigrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.POST_MIG_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						 String[] postMigCasesCopy = postMigrationUsecases.split(",");
						 for (String string : postMigCasesCopy) {
							 
							 postMigJsonObject.put("useCaseName", string);
						 
						 
						useCasesDetails = runTestController.getMigUseCases(postMigJsonObject); 
						
						 useCase.add(useCasesDetails);
						 }
					}
					//add post mig usecases
					/*
					useCaseJSONObject.put("sessionId", loginUser.getTokenKey());
					useCaseJSONObject.put("serviceToken", loginUser.getServiceToken());
					useCaseJSONObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					useCaseJSONObject.put("migrationType", "postmigration");
					useCaseJSONObject.put("migrationSubType", "AUDIT");
					useCaseJSONObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					useCaseJSONObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					useCaseJSONObject.put("programId",
							Integer.parseInt(ovScheduledEntity.getCustomerDetailsEntity().getId().toString()));
					useCaseJSONObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					enbIds.add(ovScheduledEntity.getNeId().toString());
					useCaseJSONObject.put("enbID", enbIds);

					useCasesDetails = runTestController.getMigUseCases(useCaseJSONObject);

					useCaseLst = (List<Map>) useCasesDetails.get("useCaseList");
					for (int i = 0; i < useCaseLst.size(); i++) {
						useCaseDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
						useCaseDetails.put("useCaseId", useCaseLst.get(i).get("useCaseId"));
						useCaseDetails.put("executionSequence", useCaseLst.get(i).get("executionSequence"));
						useCaseDetails.put("ucSleepInterval", useCaseLst.get(i).get("ucSleepInterval"));

						useCase.add(useCaseDetails);
						List<Map> scriptLst = (List<Map>) useCaseLst.get(i).get("scripts");
						for (int j = 0; j < scriptLst.size(); j++) {
							scriptDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
							scriptDetails.put("scriptName", scriptLst.get(j).get("scriptName"));
							scriptDetails.put("scriptId", scriptLst.get(j).get("scriptId"));
							scriptDetails.put("scriptExeSequence", scriptLst.get(j).get("scriptExeSequence"));
							scriptDetails.put("scriptSleepInterval", scriptLst.get(j).get("scriptSleepInterval"));
							scriptDetails.put("useGeneratedScript", scriptLst.get(j).get("useGeneratedScript"));
							script.add(scriptDetails);
						}

					}
					*/
					postrunTestFormDetails.put("useCase", useCase);
					postrunTestFormDetails.put("scripts", script);
					postMigJsonObject.put("runTestFormDetails", postrunTestFormDetails);

					
					finalJsonObject.put("negrow", neGrowJsonObject);
					finalJsonObject.put("migration", migJsonObject);
					finalJsonObject.put("postmigration", postMigJsonObject);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");
//					finalJsonObject.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						finalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					neGrowStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.info("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	}
//to do
	@SuppressWarnings("unchecked")
	public void schedulingMigration(List<OvScheduledEntity> preMigOvScheduledEntity) {

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject migJsonObject = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();
				JSONObject migrunTestFormDetails = new JSONObject();
				JSONObject migStatusDetails = new JSONObject();
				List<String> script = new ArrayList<>();
				List<String> useCase = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, Integer> pagination = new HashMap<>();
				Map<String, String> neDetails = new HashMap<>();
				String enbName = null;
				try {

					ovScheduledEntity.setMigrationStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName",listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", preMigJsonObject);

					migJsonObject.put("sessionId", loginUser.getTokenKey());
					migJsonObject.put("serviceToken", loginUser.getServiceToken());
					migJsonObject.put("userName", user.getUserName());
					migJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					migJsonObject.put("customerId", user.getCustomerId());
					migJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					migJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					migJsonObject.put("requestType", "RUN_TEST");
					migJsonObject.put("migrationType", "migration");
					migJsonObject.put("migrationSubType", "precheck");

					migrunTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					migrunTestFormDetails.put("checklistFileName", "");
					migrunTestFormDetails.put("checklistFilePath", "");
					migrunTestFormDetails.put("neDetails", enbList);

					migrunTestFormDetails.put("password", "");
					migrunTestFormDetails.put("lsmId", "");
					migrunTestFormDetails.put("lsmName", "");
					migrunTestFormDetails.put("lsmVersion", "");
					migrunTestFormDetails.put("currentPassword", true);
					migrunTestFormDetails.put("rfScriptFlag", true);
					migrunTestFormDetails.put("type", "OV");
					migrunTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					//migration
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String migrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.MIGRATION_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] migCases=migrationUsecases.split(",");
						for(int i=0;i<migCases.length;i++) {
						useCase.add(migCases[i]);
						}
					}
				/*	
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							useCase.add("ENDC_X2_UseCase");
							useCase.add("Anchor_CSL_UseCase");
							useCase.add("CSL_Usecase");
							useCase.add("AU_Commision_Usecase");
							useCase.add("ACPF_A1A2_Config_Usecase");
							useCase.add("NBR_RF_Scripts_Usecase");
							useCase.add("GP_Script_Usecase");
							break;
						case "VZN-5G-DSS":
							useCase.add("Pre-Check_RF_Scripts_Usecase");
							useCase.add("Rollback_RF_Scripts_Usecase");
							useCase.add("Cutover_RF_Scripts_Usecase");
							break;
						default:
							useCase.add("CommissionScriptUsecase");
							useCase.add("RFUsecase");
						}
					}*/
					migrunTestFormDetails.put("useCase", useCase);
					migrunTestFormDetails.put("scripts", script);
					migJsonObject.put("runTestFormDetails", migrunTestFormDetails);
					
					finalJsonObject.put("negrow", null);
					finalJsonObject.put("migration", migJsonObject);
					finalJsonObject.put("postmigration", null);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");

					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId", user.getCustomerId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						migrunTestFormDetails.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					migStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.info("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void schedulingPostMigAudit(List<OvScheduledEntity> preMigOvScheduledEntity) {
		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject postMigJsonObject = new JSONObject();
				JSONObject finalJsonObject = new JSONObject();
				JSONObject runTestFormDetails = new JSONObject();
				JSONObject neGrowStatusDetails = new JSONObject();
				JSONObject useCasesDetails = new JSONObject();
				JSONObject useCaseJSONObject = new JSONObject();
				List<Map> useCaseLst = null;
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject useCaseDetails = new JSONObject();
				JSONObject scriptDetails = new JSONObject();
				List<JSONObject> useCase = new ArrayList<>();
				List<JSONObject> script = new ArrayList<>();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				List<String> enbIds = new ArrayList<>();
				String enbName = null;

				try {

//					WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
//							.getWorkFlowManagementEntity(Integer
//									.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));

					ovScheduledEntity.setPostmigrationAuditStartDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName",listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					finalJsonObject.put("premigration", null);

					postMigJsonObject.put("sessionId", loginUser.getTokenKey());
					postMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					postMigJsonObject.put("userName", user.getUserName());
					postMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					postMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					postMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					postMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					postMigJsonObject.put("requestType", "RUN_TEST");
					postMigJsonObject.put("migrationType", "postmigration");
					postMigJsonObject.put("migrationSubType", "AUDIT");

					//runTestFormDetails.put("testname", workFlowManagementEntity.getTestName());
					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					runTestFormDetails.put("checklistFileName", "");
					runTestFormDetails.put("checklistFilePath", "");
					runTestFormDetails.put("neDetails", enbList);
//					runTestFormDetails.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					runTestFormDetails.put("password", "");
					runTestFormDetails.put("lsmId", "");
					runTestFormDetails.put("lsmName", "");
					runTestFormDetails.put("lsmVersion", "");
					runTestFormDetails.put("currentPassword", true);
					runTestFormDetails.put("rfScriptFlag", true);
					runTestFormDetails.put("type", "OV");
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
					
					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String postMigrationUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.POST_MIG_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						 String[] postMigCasesCopy = postMigrationUsecases.split(",");
						 for (String string : postMigCasesCopy) {
							 
							 postMigJsonObject.put("useCaseName", string);	
						useCasesDetails = runTestController.getMigUseCases(postMigJsonObject); 
						 useCase.add(useCasesDetails);
						 }
					}
					
					/*
					//To get Dynamic UseCases
					
					useCaseJSONObject.put("sessionId", loginUser.getTokenKey());
					useCaseJSONObject.put("serviceToken", loginUser.getServiceToken());
					useCaseJSONObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					useCaseJSONObject.put("migrationType", "postmigration");
					useCaseJSONObject.put("migrationSubType", "AUDIT");
					useCaseJSONObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					useCaseJSONObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					useCaseJSONObject.put("programId",
							Integer.parseInt(ovScheduledEntity.getCustomerDetailsEntity().getId().toString()));
					useCaseJSONObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					enbIds.add(ovScheduledEntity.getNeId().toString());
					useCaseJSONObject.put("enbID", enbIds);

					useCasesDetails = runTestController.getMigUseCases(useCaseJSONObject);

					useCaseLst = (List<Map>) useCasesDetails.get("useCaseList");
					for (int i = 0; i < useCaseLst.size(); i++) {
						useCaseDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
						useCaseDetails.put("useCaseId", useCaseLst.get(i).get("useCaseId"));
						useCaseDetails.put("executionSequence", useCaseLst.get(i).get("executionSequence"));
						useCaseDetails.put("ucSleepInterval", useCaseLst.get(i).get("ucSleepInterval"));

						useCase.add(useCaseDetails);
						List<Map> scriptLst = (List<Map>) useCaseLst.get(i).get("scripts");
						for (int j = 0; j < scriptLst.size(); j++) {
							scriptDetails.put("useCaseName", useCaseLst.get(i).get("useCaseName"));
							scriptDetails.put("scriptName", scriptLst.get(j).get("scriptName"));
							scriptDetails.put("scriptId", scriptLst.get(j).get("scriptId"));
							scriptDetails.put("scriptExeSequence", scriptLst.get(j).get("scriptExeSequence"));
							scriptDetails.put("scriptSleepInterval", scriptLst.get(j).get("scriptSleepInterval"));
							scriptDetails.put("useGeneratedScript", scriptLst.get(j).get("useGeneratedScript"));
							script.add(scriptDetails);
						}

					}
					*/
					runTestFormDetails.put("useCase", useCase);
					runTestFormDetails.put("scripts", script);
					postMigJsonObject.put("runTestFormDetails", runTestFormDetails);

					
					finalJsonObject.put("negrow", null);
					finalJsonObject.put("migration", null);
					finalJsonObject.put("postmigration", postMigJsonObject);
					finalJsonObject.put("sessionId", loginUser.getTokenKey());
					finalJsonObject.put("serviceToken", loginUser.getServiceToken());
					finalJsonObject.put("state", "normal");
//					finalJsonObject.put("id",
//							Integer.parseInt(ovScheduledEntity.getWorkFlowManagementEntity().getId().toString()));
					finalJsonObject.put("userName", user.getUserName());
					finalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					finalJsonObject.put("neDetails", enbList);
					finalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					finalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					finalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					finalJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					finalJsonObject.put("type", "OV");
					finalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						finalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
					neGrowStatusDetails = workFlowManagementController.workFlowTest(finalJsonObject);

				} catch (Exception e) {
					logger.info("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	}

	public void schedulingPostMigRanAtp(List<OvScheduledEntity> preMigOvScheduledEntity) {

		try {

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	@SuppressWarnings("unchecked")
	public void schedulingPreMigEnvExportNegrow(List<OvScheduledEntity> preMigOvScheduledEntity) {

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {

				JSONObject preMigJsonObject = new JSONObject();
				JSONObject preMigFinalJsonObject = new JSONObject();
				List<Map<String, String>> enbList = new ArrayList<>();
				JSONObject neGrowJsonObject = new JSONObject();
				JSONObject runTestFormDetails = new JSONObject();
				Map<String, String> neDetails = new HashMap<>();
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject preMigrationStatusDetails = new JSONObject();
				List<String> script = new ArrayList<>();
				List<String> useCase = new ArrayList<>();
				String enbName = null;
				try {

					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					if(listCIQDetailsModel.isEmpty())
						continue;
					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					if(ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName",listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					//dummy IP
					if(preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}	
					preMigFinalJsonObject.put("premigration", preMigJsonObject);

					neGrowJsonObject.put("sessionId", loginUser.getTokenKey());
					neGrowJsonObject.put("serviceToken", loginUser.getServiceToken());
					neGrowJsonObject.put("userName", user.getUserName());
					neGrowJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					neGrowJsonObject.put("customerId", user.getCustomerId());
					neGrowJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					neGrowJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					neGrowJsonObject.put("requestType", "RUN_TEST");
					neGrowJsonObject.put("migrationType", "premigration");
					neGrowJsonObject.put("migrationSubType", "NEGrow");

					// runTestFormDetails.put("testname", workFlowManagementEntity.getTestName());
					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
					runTestFormDetails.put("checklistFileName", "");
					runTestFormDetails.put("checklistFilePath", "");
					runTestFormDetails.put("neDetails", enbList);
					// runTestFormDetails.put("id",Integer.parseInt(preMigDetails.get("wfmid").toString()));
					runTestFormDetails.put("password", "");
					runTestFormDetails.put("lsmId", "");
					runTestFormDetails.put("lsmName", "");
					runTestFormDetails.put("lsmVersion", "");
					runTestFormDetails.put("currentPassword", true);
					runTestFormDetails.put("rfScriptFlag", true);
					runTestFormDetails.put("type", "OV");
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());

					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
						
						CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
						ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
						programTemplateModel.setProgramDetailsEntity(programmeEntity);
						programTemplateModel.setConfigType("s&r");
						
						List<ProgramTemplateEntity> listProgramTemplateEntity1 = fetchProcessRepository
								.getScheduledDaysDetails(programTemplateModel);
						
						
						String neGrowUsecases = listProgramTemplateEntity1.stream()
								.filter(entity -> Constants.NE_GROW_USECASES.equalsIgnoreCase(entity.getLabel()))
								.map(entity -> entity.getValue()).findFirst().get();
						
						String[] neGrowCases=neGrowUsecases.split(",");
						for(int i=0;i<neGrowCases.length;i++) {
						useCase.add(neGrowCases[i]);
						}
					}
						/*
						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
						case "VZN-5G-MM":
							useCase.add("pnp");
							//useCase.add("AUCaCell");
							//useCase.add("AU");
							break;
						case "VZN-4G-USM-LIVE":
							useCase.add("GrowEnb");
							break;
						default:
							useCase.add("GrowEnb");
						}*/
					
					runTestFormDetails.put("useCase", useCase);
					runTestFormDetails.put("scripts", script);
					neGrowJsonObject.put("runTestFormDetails", runTestFormDetails);

					preMigFinalJsonObject.put("negrow", neGrowJsonObject);

					preMigFinalJsonObject.put("migration", null);
					preMigFinalJsonObject.put("postmigration", null);
					preMigFinalJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigFinalJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigFinalJsonObject.put("state", "normal");
					preMigFinalJsonObject.put("userName", user.getUserFullName());
					preMigFinalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigFinalJsonObject.put("neDetails", enbList);
					preMigFinalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigFinalJsonObject.put("customerId", user.getCustomerId());
					preMigFinalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					preMigFinalJsonObject.put("programName",
							ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigFinalJsonObject.put("type", "OV");
					preMigFinalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						preMigFinalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					preMigrationStatusDetails = workFlowManagementController.workFlowTest(preMigFinalJsonObject);
				} catch (Exception e) {
					logger.error(ovScheduledEntity.getNeId() + ":::********Exception in getPreMigrationData() "
							+ ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void schedulingPreMigEnvExport(List<OvScheduledEntity> preMigOvScheduledEntity) {

		JSONObject loginDetails = new JSONObject();
		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");

		loginDetails.put("username", userLogin.getUserName());
		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
		User loginUser = Ovlogin(loginDetails);
		if (!preMigOvScheduledEntity.isEmpty()) {
			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
				JSONObject preMigJsonObject = new JSONObject();
				JSONObject preMigFinalJsonObject = new JSONObject();
				List<Map<String, String>> enbList = new ArrayList<>();
				Map<String, String> neDetails = new HashMap<>();
				Map<String, Integer> pagination = new HashMap<>();
				JSONObject preMigrationStatusDetails = new JSONObject();
				String enbName = null;
				try {

					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigJsonObject.put("filedId", 265);

					neDetails.put("neId", ovScheduledEntity.getNeId());
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
							ovScheduledEntity.getCiqName());
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
					
					//
					if(null==listCIQDetailsModel.get(0)||StringUtils.isEmpty(listCIQDetailsModel.get(0).geteNBName())) {
						try {
							List<CIQDetailsModel> listCIQDetailsModel1 = fileUploadRepository
									.getCIQDetailsModelList(ovScheduledEntity.getNeId(), dbcollectionFileName);
							if(StringUtils.isEmpty(listCIQDetailsModel1.get(0).geteNBName())) {

								List<CIQDetailsModel> listOfCiqDetails2 = fileUploadRepository.getCiqDetailsForRuleValidationsheet(
										ovScheduledEntity.getNeId(), dbcollectionFileName, "CIQUpstateNY", "eNBId");
								if(StringUtils.isEmpty(listOfCiqDetails2.get(0).geteNBName())) {
									System.out.println("getCiqDetailsForRuleValidationsheet" + ovScheduledEntity.getNeId());
								}else {
									neDetails.put("neName", listOfCiqDetails2.get(0).geteNBName());
								}
							}else {
								neDetails.put("neName", listCIQDetailsModel1.get(0).geteNBName());
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}else {
						neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
					}
					//
					if (listCIQDetailsModel.isEmpty())
						continue;
				//	System.out.println("Entering schedulingPreMigEnvExport" + ovScheduledEntity.getNeId());

					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName().contains("5G-MM"))
						neDetails.put("siteName", listCIQDetailsModel.get(0).getSiteName());
					enbList.add(neDetails);
					preMigJsonObject.put("neDetails", enbList);
					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());

					preMigJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());

					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					pagination.put("count", 10);
					pagination.put("page", 1);
					preMigJsonObject.put("pagination", pagination);
					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigJsonObject.put("neVersion", "");
					preMigJsonObject.put("fileType", "ALL");
					preMigJsonObject.put("remarks", "");
					preMigJsonObject.put("type", "OV");
					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					preMigJsonObject.put("EnvExport", true);
					// dummy IP
					if (preMigJsonObject.get("programName").toString().contains("USM")) {
						preMigJsonObject.put("integrationType", ovScheduledEntity.getIntegrationType());
					} else {
						preMigJsonObject.put("integrationType", "NA");
					}
					preMigFinalJsonObject.put("premigration", preMigJsonObject);
					preMigFinalJsonObject.put("negrow", null);
					preMigFinalJsonObject.put("migration", null);
					preMigFinalJsonObject.put("postmigration", null);
					preMigFinalJsonObject.put("sessionId", loginUser.getTokenKey());
					preMigFinalJsonObject.put("serviceToken", loginUser.getServiceToken());
					preMigFinalJsonObject.put("state", "normal");
					preMigFinalJsonObject.put("userName", user.getUserFullName());
					preMigFinalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
					preMigFinalJsonObject.put("neDetails", enbList);
					preMigFinalJsonObject.put("customerName",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
					preMigFinalJsonObject.put("customerId",
							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
					preMigFinalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
					preMigFinalJsonObject.put("programName",
							ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
					preMigFinalJsonObject.put("type", "OV");
					preMigFinalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
					if (ovScheduledEntity.getWorkFlowManagementEntity() != null) {
						preMigFinalJsonObject.put("wfmid",
								ovScheduledEntity.getWorkFlowManagementEntity().getId().toString());
					}
					preMigrationStatusDetails = workFlowManagementController.workFlowTest(preMigFinalJsonObject);
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		}

	}

//	@SuppressWarnings("unchecked")
//	public void schedulingPreMigNeGrow(List<OvScheduledEntity> preMigOvScheduledEntity) {
//
//		JSONObject loginDetails = new JSONObject();
//		UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");
//
//		loginDetails.put("username", userLogin.getUserName());
//		loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
//		User loginUser = Ovlogin(loginDetails);
//		if (!preMigOvScheduledEntity.isEmpty()) {
//			for (OvScheduledEntity ovScheduledEntity : preMigOvScheduledEntity) {
//
//				JSONObject preMigJsonObject = new JSONObject();
//				JSONObject preMigFinalJsonObject = new JSONObject();
//				List<Map<String, String>> enbList = new ArrayList<>();
//				JSONObject neGrowJsonObject = new JSONObject();
//				JSONObject runTestFormDetails = new JSONObject();
//				Map<String, String> neDetails = new HashMap<>();
//				Map<String, Integer> pagination = new HashMap<>();
//				JSONObject preMigrationStatusDetails = new JSONObject();
//				List<String> script = new ArrayList<>();
//				List<String> useCase = new ArrayList<>();
//				String enbName = null;
//				try {
//
//					preMigJsonObject.put("sessionId", loginUser.getTokenKey());
//					preMigJsonObject.put("serviceToken", loginUser.getServiceToken());
//					preMigJsonObject.put("ciqFileName", ovScheduledEntity.getCiqName());
//					preMigJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
//					preMigJsonObject.put("filedId", 265);
//
//					neDetails.put("neId", ovScheduledEntity.getNeId());
//					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
//							String.valueOf(ovScheduledEntity.getCustomerDetailsEntity().getId()),
//							ovScheduledEntity.getCiqName());
//					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(
//							ovScheduledEntity.getCiqName(), ovScheduledEntity.getNeId(), enbName, dbcollectionFileName);
//					if(listCIQDetailsModel.isEmpty())
//						continue;
//					neDetails.put("neName", listCIQDetailsModel.get(0).geteNBName());
//					enbList.add(neDetails);
//					preMigJsonObject.put("neDetails", enbList);
//					User user = UserSessionPool.getInstance().getSessionUser(loginUser.getTokenKey());
//					preMigJsonObject.put("customerName",
//							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
//					preMigJsonObject.put("customerId",
//							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getId());
//
//					preMigJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
//					pagination.put("count", 10);
//					pagination.put("page", 1);
//					preMigJsonObject.put("pagination", pagination);
//					preMigJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
//					preMigJsonObject.put("neVersion", "");
//					preMigJsonObject.put("fileType", "ALL");
//					preMigJsonObject.put("remarks", "");
//					preMigJsonObject.put("type", "OV");
//					preMigJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
//					preMigFinalJsonObject.put("premigration", preMigJsonObject);
//
//					neGrowJsonObject.put("sessionId", loginUser.getTokenKey());
//					neGrowJsonObject.put("serviceToken", loginUser.getServiceToken());
//					neGrowJsonObject.put("userName", user.getUserName());
//					neGrowJsonObject.put("customerName",
//							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
//					neGrowJsonObject.put("customerId", user.getCustomerId());
//					neGrowJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
//					neGrowJsonObject.put("programName", ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
//					neGrowJsonObject.put("requestType", "RUN_TEST");
//					neGrowJsonObject.put("migrationType", "premigration");
//					neGrowJsonObject.put("migrationSubType", "NEGrow");
//
//					// runTestFormDetails.put("testname", workFlowManagementEntity.getTestName());
//					runTestFormDetails.put("ciqName", ovScheduledEntity.getCiqName());
//					runTestFormDetails.put("checklistFileName", "");
//					runTestFormDetails.put("checklistFilePath", "");
//					runTestFormDetails.put("neDetails", enbList);
//					// runTestFormDetails.put("id",Integer.parseInt(preMigDetails.get("wfmid").toString()));
//					runTestFormDetails.put("password", "");
//					runTestFormDetails.put("lsmId", "");
//					runTestFormDetails.put("lsmName", "");
//					runTestFormDetails.put("lsmVersion", "");
//					runTestFormDetails.put("currentPassword", true);
//					runTestFormDetails.put("rfScriptFlag", true);
//					runTestFormDetails.put("type", "OV");
//					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
//
//					if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) {
//						switch (ovScheduledEntity.getCustomerDetailsEntity().getProgramName()) {
//						case "VZN-5G-MM":
//							useCase.add("pnp");
//							useCase.add("AUCaCell");
//							useCase.add("AU");
//							break;
//						case "VZN-4G-USM-LIVE":
//							useCase.add("GrowEnb");
//							break;
//						default:
//							useCase.add("GrowEnb");
//						}
//					}
//					runTestFormDetails.put("useCase", useCase);
//					runTestFormDetails.put("scripts", script);
//					neGrowJsonObject.put("runTestFormDetails", runTestFormDetails);
//
//					preMigFinalJsonObject.put("negrow", neGrowJsonObject);
//
//					preMigFinalJsonObject.put("migration", null);
//					preMigFinalJsonObject.put("postmigration", null);
//					preMigFinalJsonObject.put("sessionId", loginUser.getTokenKey());
//					preMigFinalJsonObject.put("serviceToken", loginUser.getServiceToken());
//					preMigFinalJsonObject.put("state", "normal");
//					preMigFinalJsonObject.put("userName", user.getUserFullName());
//					preMigFinalJsonObject.put("ciqName", ovScheduledEntity.getCiqName());
//					preMigFinalJsonObject.put("neDetails", enbList);
//					preMigFinalJsonObject.put("customerName",
//							ovScheduledEntity.getCustomerDetailsEntity().getCustomerEntity().getCustomerName());
//					preMigFinalJsonObject.put("customerId", user.getCustomerId());
//					preMigFinalJsonObject.put("programId", ovScheduledEntity.getCustomerDetailsEntity().getId());
//					preMigFinalJsonObject.put("programName",
//							ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
//					preMigFinalJsonObject.put("type", "OV");
//					preMigFinalJsonObject.put("trackerId", ovScheduledEntity.getTrackerId());
//
//					preMigrationStatusDetails = workFlowManagementController.workFlowTest(preMigFinalJsonObject);
//				} catch (Exception e) {
//					logger.info("Exception in getPreMigrationData() " + ExceptionUtils.getFullStackTrace(e));
//				}
//			}
//		}
//	}

	@SuppressWarnings("unchecked")
	public User Ovlogin(JSONObject loginDetails) {
		JSONObject expiryDetails = null;
		User user = null;
		try {
			if (GlobalStatusMap.schedulingLoginUsersDetails.containsKey("scheduledUserDetails")
					&& GlobalStatusMap.schedulingLoginUsersDetails.get("scheduledUserDetails") != null) {
				user = GlobalStatusMap.schedulingLoginUsersDetails.get("scheduledUserDetails");
				expiryDetails = CommonUtil.getSessionExpirationDetails(user.getTokenKey());
				if (expiryDetails != null)
					user = getUserDetails(loginDetails);
				else
					user = GlobalStatusMap.schedulingLoginUsersDetails.get("scheduledUserDetails");

			} else {
				user = getUserDetails(loginDetails);
			}
		} catch (Exception e) {
			logger.info("Exception in login() " + ExceptionUtils.getFullStackTrace(e));
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public User getUserDetails(JSONObject userJsonData) {

		JSONObject objStatus = new JSONObject();
		JSONObject loginData = new JSONObject();
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpSession session = request.getSession();
		Integer token = (int) Math.floor(Math.random() * (50000 - 100000) + 100000);
		String serviceToken = token.toString();
		loginData.put("username", userJsonData.get("username"));
		loginData.put("password", PasswordCrypt.encryptPasswordUI(userJsonData.get("password").toString()));
		loginData.put("serviceToken", serviceToken);
		loginData.put("platform", "RMT");
		objStatus = loginActionController.loginAction(loginData, request, session);
		User user = (User) objStatus.get("userDetails");
		GlobalStatusMap.schedulingLoginUsersDetails.put("scheduledUserDetails", user);

		return user;
	}

}
