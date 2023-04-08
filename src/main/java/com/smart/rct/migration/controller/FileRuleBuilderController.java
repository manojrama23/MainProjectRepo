package com.smart.rct.migration.controller;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.dto.FileRuleBuilderDto;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.migration.service.CmdRuleBuilderService;
import com.smart.rct.migration.service.FileRuleBuilderService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@RestController
public class FileRuleBuilderController {

	@Autowired
	FileRuleBuilderService fileRuleBuilderService;

	@Autowired
	FileRuleBuilderDto FileRuleBuilderDto;
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	CmdRuleBuilderService cmdRuleBuilderService;

	private static final Logger logger = LoggerFactory.getLogger(FileRuleBuilderController.class);

	/**
	 * This method will create file rule builder - Save new file rule builder to DB
	 * 
	 * @param saveFileRuleBuilder
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/createFileRuleBuilder")
	public JSONObject createFileRuleBuilder(@RequestBody JSONObject saveFileRuleBuilder) {

		String sessionId = null;
		String serviceToken = null;
		int customerId = 0;
		JSONObject expiryDetails = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = saveFileRuleBuilder.get("sessionId").toString();
			serviceToken = saveFileRuleBuilder.get("serviceToken").toString();
			customerId = Integer.valueOf(saveFileRuleBuilder.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = saveFileRuleBuilder.get("migrationType").toString();
			programId = (int) saveFileRuleBuilder.get("programId");
			subType = saveFileRuleBuilder.get("subType").toString();
			FileRuleBuilderEntity fileRuleEntity = new Gson().fromJson(
					saveFileRuleBuilder.toJSONString((Map) saveFileRuleBuilder.get("fileRuleDetail")),
					FileRuleBuilderEntity.class);
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderService.getCustomerDetailsEntity(programId);
			fileRuleEntity.setCustomerId(customerId);
			fileRuleEntity.setCreationDate(new Date());
			fileRuleEntity.setUseCount(0);
			fileRuleEntity.setMigrationType(migrationType);
			fileRuleEntity.setCustomerDetailsEntity(customerDetailsEntity);
			fileRuleEntity.setSubType(subType);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			fileRuleEntity.setCreatedBy(user.getUserName());
			// check duplicate rule name creation
			boolean ruleNameExistence = fileRuleBuilderService.duplicateFileName(fileRuleEntity.getRuleName(),
					customerId, migrationType, programId, user.getRole(),subType);
			if (ruleNameExistence) {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Rule name  already exists !");
				return mapObject;
			}
			if (fileRuleEntity != null) {
				// creating new User
				if (fileRuleBuilderService.createFileRuleBuilder(fileRuleEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_FILE_RULE_BUILDER, Constants.ACTION_SAVE, "File Rule Builder Created Successfully", sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in create File Rule Builder : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to Create File Rule Builder");
		}
		return mapObject;
	}

	/**
	 * This method will search file rule builder - search file rule builder in DB
	 * 
	 * @param searchFileRuleBuilder
	 * @return JSONObject
	 */

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getFileRuleBuilder")
	public JSONObject getFileRuleBuilder(@RequestBody JSONObject getFileRuleBuilder) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;		
		String searchStatus = null;
		int customerId = 0;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = getFileRuleBuilder.get("sessionId").toString();
			serviceToken = getFileRuleBuilder.get("serviceToken").toString();
			searchStatus = getFileRuleBuilder.get("searchStatus").toString();
			customerId = Integer.valueOf(getFileRuleBuilder.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			Map<String, Object> paginationData = (Map<String, Object>) getFileRuleBuilder.get("pagination");
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
			migrationType = getFileRuleBuilder.get("migrationType").toString();
			programId = (int) getFileRuleBuilder.get("programId");
			subType = getFileRuleBuilder.get("subType").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if ("search".equals(searchStatus)) {
				FileRuleBuilderModel fileRuleBuilderModel = new Gson().fromJson(
						getFileRuleBuilder.toJSONString((Map) getFileRuleBuilder.get("searchCriteria")),
						FileRuleBuilderModel.class);
				/*
				 * Map<String, String> searchCriteriaData = (Map<String,
				 * String>) getFileRuleBuilder.get("searchCriteria"); searchBy =
				 * searchCriteriaData.get("searchBy").toString();
				 * searchParameter =
				 * searchCriteriaData.get("searchParameter").toString().trim();
				 */

				// if (StringUtils.isNotEmpty(searchBy) &&
				// StringUtils.isNotEmpty(searchParameter)) {
				Map<String, Object> searchList = fileRuleBuilderService.searchFileRuleBuilder(fileRuleBuilderModel,
						customerId, page, count, migrationType, programId, subType, user);
				mapObject.put("fileRuleBuilderData", searchList.get("fileRuleDetails"));
				mapObject.put("pageCount", searchList.get("totalCount"));
				mapObject.put("status", Constants.SUCCESS);
				// }
			} else if ("load".equals(searchStatus)) {
				Map<String, Object> searchList = fileRuleBuilderService.searchFileRuleBuilder(customerId, page, count,
						migrationType, programId, subType, user);
				mapObject.put("fileRuleBuilderData", searchList.get("fileRuleDetails"));
				mapObject.put("pageCount", searchList.get("totalCount"));
				mapObject.put("status", Constants.SUCCESS);
			}
		} catch (Exception e) {
			logger.info("Exception in load File Rule Builder : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to load File Rule Builder");
		}
		return mapObject;

	}

	/**
	 * This method will update FileRuleBuilder details to DB
	 * 
	 * @param updateFileRuleBuilder
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/updateFileRuleBuilder")
	public JSONObject updateFileRuleBuilder(@RequestBody JSONObject updateFileRuleBuilder) {
		String sessionId = null;
		String serviceToken = null;
		int customerId = 0;
		JSONObject expiryDetails = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateFileRuleBuilder.get("sessionId").toString();
			serviceToken = updateFileRuleBuilder.get("serviceToken").toString();
			customerId = Integer.valueOf(updateFileRuleBuilder.get("customerId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = updateFileRuleBuilder.get("migrationType").toString();
			programId = (int) updateFileRuleBuilder.get("programId");
			subType = updateFileRuleBuilder.get("subType").toString();
			FileRuleBuilderEntity fileRuleEntity = new Gson().fromJson(
					updateFileRuleBuilder.toJSONString((Map) updateFileRuleBuilder.get("fileRuleDetail")),
					FileRuleBuilderEntity.class);
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderService.getCustomerDetailsEntity(programId);
			fileRuleEntity.setCustomerId(customerId);
			fileRuleEntity.setCreationDate(new Date());
			fileRuleEntity.setMigrationType(migrationType);			
			fileRuleEntity.setCustomerDetailsEntity(customerDetailsEntity);
			fileRuleEntity.setSubType(subType);
			fileRuleEntity.setCreatedBy(UserSessionPool.getInstance().getSessionUser(sessionId).getUserName());
			// check duplicate rule name creation
//			String ruleName = fileRuleBuilderService.duplicateFileName(fileRuleEntity.getRuleName(),
//					fileRuleEntity.getCustomerId());
//			if (fileRuleEntity.getRuleName().equals(ruleName)) {
//				mapObject.put("status", Constants.FAIL);
//				mapObject.put("reason", "Rule name  already exists !");
//				return mapObject;
//			}
			if (fileRuleEntity != null) {
				// updating user details
				if (fileRuleBuilderService.updateFileRuleBuilder(fileRuleEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_FILE_RULE_BUILDER, Constants.ACTION_UPDATE, "File Rule Builder Updated Successfully", sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {

			logger.info("Exception in updateFileRuleBuilder : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to update File Rule Builder");
		}
		return mapObject;
	}

	/**
	 * This method will delete FileRuleBuilder details from DB
	 * 
	 * @param deleteFileRuleBuilder
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/deleteFileRuleBuilder")
	public JSONObject deleteFileRuleBuilder(@RequestBody JSONObject deleteFileRuleBuilder) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String id = null;
		try {
			sessionId = deleteFileRuleBuilder.get("sessionId").toString();
			serviceToken = deleteFileRuleBuilder.get("serviceToken").toString();
			id = deleteFileRuleBuilder.get("id").toString();
			if (StringUtils.isNotEmpty(id)) {
				if (fileRuleBuilderService.deleteFileRule(Integer.valueOf(id))) {
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("status", Constants.SUCCESS);
				} else {
					resultMap.put("reason",GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_DELETE_FILERULE));
					resultMap.put("status", Constants.FAIL);
				}
			}
//			return CommonUtil.buildResponseJson(Constants.FAIL, "File Rule Builder deletion failed", sessionId,serviceToken);
			commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_FILE_RULE_BUILDER, Constants.ACTION_DELETE, "File Rule Builder Deleted Successfully", sessionId);
		} catch (Exception e) {
			logger.info("Exception in delete file rule builder in FileRuleBuilderController "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

}
