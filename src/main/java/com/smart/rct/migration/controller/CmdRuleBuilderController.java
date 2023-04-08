package com.smart.rct.migration.controller;

import java.rmi.UnexpectedException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleBuilderModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.service.CmdRuleBuilderService;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@RestController
public class CmdRuleBuilderController {

	final static Logger logger = LoggerFactory.getLogger(CmdRuleBuilderController.class);

	@Autowired
	CmdRuleBuilderService cmdRuleBuilderService;

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	FileUploadRepository fileUploadRepository;

	/**
	 * This method will create command rule builder - Save new command rule builder
	 * to DB
	 * 
	 * @param createCmdRuleBuilderDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/createCmdRuleBuilder")
	public JSONObject createCmdRuleBuilder(@RequestBody JSONObject createCmdRuleBuilderDetails) {
		String sessionId = null;
		String serviceToken = null;
		int customerId = 0;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		String migrationType = null;
		int programId = 0;
		String subType = null;
		try {
			sessionId = createCmdRuleBuilderDetails.get("sessionId").toString();
			serviceToken = createCmdRuleBuilderDetails.get("serviceToken").toString();
			customerId = Integer.valueOf(createCmdRuleBuilderDetails.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = createCmdRuleBuilderDetails.get("migrationType").toString();
			programId = (int) createCmdRuleBuilderDetails.get("programId");
			subType = createCmdRuleBuilderDetails.get("subType").toString();
			
			if(migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			}else if(migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			}else if(migrationType.equalsIgnoreCase("postmigration")) {
				migrationType = "PostMigration";
			}
			
			if (subType.equalsIgnoreCase("precheck")) {
				subType = "PreCheck";
			} else if (subType.equalsIgnoreCase("commission")) {
				subType = "Commission";
			} else if (subType.equalsIgnoreCase("postcheck")) {
				subType = "PostCheck";
			} else if (subType.equalsIgnoreCase("AUDIT")) {
				subType = "Audit";
			}else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			}
			
			CmdRuleBuilderEntity cmdRuleBuilderEntity = new Gson().fromJson(
					createCmdRuleBuilderDetails.toJSONString((Map) createCmdRuleBuilderDetails.get("cmdRuleDetail")),
					CmdRuleBuilderEntity.class);
			//cmdRuleBuilderEntity.setCustomerId(customerId);
			cmdRuleBuilderEntity.setCreationDate(new Date());
			cmdRuleBuilderEntity.setUseCount(0);
			cmdRuleBuilderEntity.setMigrationType(migrationType);
			cmdRuleBuilderEntity.setSubType(subType);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			cmdRuleBuilderEntity.setCreatedBy(user.getUserName());
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderService.getCustomerDetailsEntity(programId);
			cmdRuleBuilderEntity.setCustomerDetailsEntity(customerDetailsEntity);
			// check duplicate rule name creation
			boolean ruleNameExistence = cmdRuleBuilderService.duplicateFileName(cmdRuleBuilderEntity.getRuleName()
					, migrationType, programId, user.getRole(),subType);
			if (ruleNameExistence) {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Rule name  already exists !");
				return mapObject;
			}

			if (cmdRuleBuilderEntity != null) {
				// creating new User
				if (cmdRuleBuilderService.createCmdRule(cmdRuleBuilderEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,
							Constants.ACTION_SAVE, "Command Rule Created Successfully", sessionId);
				} else {
					mapObject.put("message", "Command rule creation fail");
					mapObject.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			logger.info("Exception in create Command Rule Builder : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to create Command Rule Builder");
		}
		return mapObject;
	}

	/**
	 * This method will search command rule builder - search command rule builder in
	 * DB
	 * 
	 * @param searchCmdRuleBuilderDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getCmdRuleBuilder")
	public JSONObject getCmdRuleBuilder(@RequestBody JSONObject searchCmdRuleBuilderDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String searchBy = null;
		String searchParameter = null;
		String searchStatus = null;
		int customerId = 0;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = searchCmdRuleBuilderDetails.get("sessionId").toString();
			serviceToken = searchCmdRuleBuilderDetails.get("serviceToken").toString();
			searchStatus = (String) searchCmdRuleBuilderDetails.get("searchStatus");
			customerId = Integer.valueOf(searchCmdRuleBuilderDetails.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			Map<String, Object> paginationData = (Map<String, Object>) searchCmdRuleBuilderDetails.get("pagination");
			int count = 0;
			int page = (Integer) paginationData.get("page");
			if (paginationData.get("count") instanceof String) {
				count = Integer.parseInt((String) paginationData.get("count"));
			} else {
				count = (Integer) paginationData.get("count");
			}
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = searchCmdRuleBuilderDetails.get("migrationType").toString();
			programId = (int) searchCmdRuleBuilderDetails.get("programId");
			subType = searchCmdRuleBuilderDetails.get("subType").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			
			/*if(migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			}else if(migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			}else if(migrationType.equalsIgnoreCase("postmigration")) {
				migrationType = "PostMigration";
			}
			
			if (subType.equalsIgnoreCase("precheck")) {
				subType = "PreCheck";
			} else if (subType.equalsIgnoreCase("commission")) {
				subType = "Commission";
			} else if (subType.equalsIgnoreCase("postcheck")) {
				subType = "PostCheck";
			} else if (subType.equalsIgnoreCase("AUDIT")) {
				subType = "Audit";
			}else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			}*/
			
			if ("search".equals(searchStatus)) {

				CmdRuleBuilderModel cmdRuleBuilderModel = new Gson().fromJson(searchCmdRuleBuilderDetails.toJSONString((Map) searchCmdRuleBuilderDetails.get("searchCriteria")), CmdRuleBuilderModel.class);
				
			//	Map<String, String> searchCriteriaData = (Map<String, String>) searchCmdRuleBuilderDetails
			//			.get("searchCriteria");
			//	searchBy = searchCriteriaData.get("searchBy").toString();
			//	searchParameter = searchCriteriaData.get("searchParameter").toString().trim();

			//	if (StringUtils.isNotEmpty(searchBy) && StringUtils.isNotEmpty(searchParameter)) {
					Map<String, Object> searchList = cmdRuleBuilderService.searchCmdRule(cmdRuleBuilderModel,
							customerId, page, count, migrationType, programId, subType, user);
					mapObject.put("cmdRuleBuilderData", searchList.get("cmdRuleBuilderData"));
					mapObject.put("pageCount", searchList.get("totalCount"));
					mapObject.put("status", Constants.SUCCESS);
			//	}
			} else if ("load".equals(searchStatus)) {
				Map<String, Object> searchList = cmdRuleBuilderService.searchCmdRule(customerId, page, count,
						migrationType, programId, subType, user);
				mapObject.put("cmdRuleBuilderData", searchList.get("cmdRuleBuilderData"));
				mapObject.put("pageCount", searchList.get("totalCount"));
				mapObject.put("status", Constants.SUCCESS);
			}
			
			ProgramTemplateEntity programTemplateEntity = fileUploadRepository.getProgramTemplate(programId, Constants.PROMPT_TEMPLATE);
			
			
			if(programTemplateEntity!=null) {
				if(programTemplateEntity.getValue()!=null && !programTemplateEntity.getValue().trim().isEmpty()) {
					JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
					mapObject.put("prompt", objData);
				}else {
					mapObject.put("prompt", null);
				}
				
			}else {
				mapObject.put("prompt", null);
			}
			
		}catch(ParseException e2) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("reason", "Prompt template JSON is wrong");
		}
		
		catch (Exception e) {
			logger.info("Exception in load Command Rule Builder : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to load Command Rule Builder");
		}
		return mapObject;
	}

	/**
	 * This method will update CommandRuleBuilder details to DB
	 * 
	 * @param updateCmdRuleBuilder
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/updateCmdRuleBuilder")
	public JSONObject updateCmdRuleBuilder(@RequestBody JSONObject updateCmdRuleBuilder) {
		String sessionId = null;
		String serviceToken = null;
		int customerId = 0;
		JSONObject expiryDetails = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateCmdRuleBuilder.get("sessionId").toString();
			serviceToken = updateCmdRuleBuilder.get("serviceToken").toString();
			customerId = Integer.valueOf(updateCmdRuleBuilder.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = updateCmdRuleBuilder.get("migrationType").toString();
			programId = (int) updateCmdRuleBuilder.get("programId");
			subType = updateCmdRuleBuilder.get("subType").toString();
			
			
			if(migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			}else if(migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			}else if(migrationType.equalsIgnoreCase("postmigration")) {
				migrationType = "PostMigration";
			}
			
			if (subType.equalsIgnoreCase("precheck")) {
				subType = "PreCheck";
			} else if (subType.equalsIgnoreCase("commission")) {
				subType = "Commission";
			} else if (subType.equalsIgnoreCase("postcheck")) {
				subType = "PostCheck";
			} else if (subType.equalsIgnoreCase("AUDIT")) {
				subType = "Audit";
			}else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			}
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderService.getCustomerDetailsEntity(programId);
			CmdRuleBuilderEntity cmdRuleEntity = new Gson().fromJson(
					updateCmdRuleBuilder.toJSONString((Map) updateCmdRuleBuilder.get("cmdRuleDetail")),
					CmdRuleBuilderEntity.class);
			//cmdRuleEntity.setCustomerId(customerId);
			cmdRuleEntity.setCreationDate(new Date());
			cmdRuleEntity.setMigrationType(migrationType);
			cmdRuleEntity.setSubType(subType);
			cmdRuleEntity.setCustomerDetailsEntity(customerDetailsEntity);
			cmdRuleEntity.setCreatedBy(UserSessionPool.getInstance().getSessionUser(sessionId).getUserName());
		//	cmdRuleEntity.setMigrationType(migrationType);
			// check duplicate rule name creation
//			String ruleName = cmdRuleBuilderService.duplicateFileName(cmdRuleEntity.getRuleName(),
//					cmdRuleEntity.getCustomerId());
//			if (cmdRuleEntity.getRuleName().equals(ruleName)) {
//				mapObject.put("status", Constants.FAIL);
//				mapObject.put("reason", "Rule name  already exists !");
//				return mapObject;
//			}
			if (cmdRuleEntity != null) {
				// updating user details
				if (cmdRuleBuilderService.updateCmdRuleBuilder(cmdRuleEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,
							Constants.ACTION_UPDATE, "Command Rule Updated Successfully", sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {

			logger.info("Exception in updateUser : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to update Command Rule Builder");
		}
		return mapObject;
	}

	/**
	 * This method will delete CommandRuleBuilder details from DB
	 * 
	 * @param deleteCmdRuleBuilder
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/deleteCmdRuleBuilder")
	public JSONObject deleteCmdRuleBuilder(@RequestBody JSONObject deleteCmdRuleBuilder) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String id = null;
		try {
			sessionId = deleteCmdRuleBuilder.get("sessionId").toString();
			serviceToken = deleteCmdRuleBuilder.get("serviceToken").toString();
			/*
			 * id = (int) deleteCmdRuleBuilder.get("id");
			 * cmdRuleBuilderService.deleteCmdRule(id); resultMap.put("sessionId",
			 * sessionId); resultMap.put("serviceToken", serviceToken);
			 * resultMap.put("status", Constants.SUCCESS);
			 */

			id = deleteCmdRuleBuilder.get("id").toString();
			if (StringUtils.isNotEmpty(id)) {
				if (cmdRuleBuilderService.deleteCmdRule(Integer.valueOf(id))) {
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("status", Constants.SUCCESS);
				} else {
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_DELETE_COMMANDRULE));
					resultMap.put("status", Constants.FAIL);
				}
			}

			commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,
					Constants.ACTION_DELETE, "Command Rule Deleted Successfully", sessionId);
		} catch (Exception e) {
			logger.info("Exception in delete command rule builder in CmdRuleBuilderController "+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}
}
