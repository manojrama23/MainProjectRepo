package com.smart.rct.migration.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.model.XmlRuleBuilderModel;
import com.smart.rct.migration.model.XmlSerachModel;
import com.smart.rct.migration.service.XmlRuleBuilderService;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.PaginationModel;

@RestController
public class XmlRuleBuilderController {

	@Autowired
	XmlRuleBuilderService xmlRuleBuilderService;

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	FileUploadRepository fileUploadRepository;

	private static final Logger logger = LoggerFactory.getLogger(XmlRuleBuilderController.class);

	@RequestMapping(value = "/createXmlFileRule", method = RequestMethod.POST)
	public JSONObject createXmlFileRule(@RequestBody JSONObject xmlRuleDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		int customerId = 0;

		try {
			sessionId = xmlRuleDetails.get("sessionId").toString();
			serviceToken = xmlRuleDetails.get("serviceToken").toString();
			customerId = Integer.valueOf(xmlRuleDetails.get("customerId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			migrationType = xmlRuleDetails.get("migrationType").toString();
			programId = (int) xmlRuleDetails.get("programId");
			subType = xmlRuleDetails.get("subType").toString();
			
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

			XmlRuleBuilderModel xmlRuleBuilderModel = new Gson().fromJson(
					xmlRuleDetails.toJSONString((Map) xmlRuleDetails.get("xmlRuleDetail")), XmlRuleBuilderModel.class);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			boolean ruleNameExistence = xmlRuleBuilderService.duplicateFileName(xmlRuleBuilderModel.getRuleName(),
					customerId, migrationType, programId, user.getRole(),subType);
			if (ruleNameExistence) {
				return CommonUtil.buildResponseJson(Constants.FAIL, "Xml Rule already exists !", sessionId,
						serviceToken);
			}
			
			if (xmlRuleBuilderModel != null) {
				if (xmlRuleBuilderService.createXmlRuleBuilder(xmlRuleBuilderModel, migrationType, programId, subType,
						sessionId, customerId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,Constants.ACTION_SAVE, "XmlRuleBuilder Created Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "XmlRuleBuilder created successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "XmlRuleBuilder creation failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "For creating XmlRuleBuilder data is not proper",
					sessionId, serviceToken);
		} catch (Exception ex) {
			logger.error(" createXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(ex));
			return CommonUtil.buildResponseJson(Constants.FAIL, ex.getMessage(), sessionId, serviceToken);
		}
	}

	@RequestMapping(value = "/loadXmlFileRule", method = RequestMethod.POST)
	public JSONObject loadXmlFileRule(@RequestBody JSONObject xmlRuleDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String searchStatus = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		int customerId = 0;
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			sessionId = xmlRuleDetails.get("sessionId").toString();
			serviceToken = xmlRuleDetails.get("serviceToken").toString();
			searchStatus = xmlRuleDetails.get("searchStatus").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			customerId = (int) xmlRuleDetails.get("customerId");
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = xmlRuleDetails.get("migrationType").toString();
			programId = (int) xmlRuleDetails.get("programId");
			subType = xmlRuleDetails.get("subType").toString();
			
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

			String pagination = xmlRuleDetails.get("pagination").toString();
			int rowCount = 0;
			//int pageCount = 0;
			PaginationModel paginationModel = new Gson().fromJson(pagination, PaginationModel.class);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);

			if ("search".equalsIgnoreCase(searchStatus)) {
				XmlSerachModel searchModel = new Gson().fromJson(
						xmlRuleDetails.toJSONString((Map) xmlRuleDetails.get("searchCriteria")), XmlSerachModel.class);

				Map<String,Object> xmlRuleBuilderModelList = xmlRuleBuilderService
						.loadXmlRuleBuilderSearchDetails(paginationModel, searchModel, programId, migrationType,
								subType, user, customerId);

				//rowCount = xmlRuleBuilderModelList.size();
				//pageCount = CommonUtil.getPageCount(rowCount, Integer.parseInt(paginationModel.getCount()));

				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("xmlRuleDetail", xmlRuleBuilderModelList.get("xmlRuleBuilderEntityList"));
				resultMap.put("pageCount", xmlRuleBuilderModelList.get("totalCount"));
				//return new JSONObject(resultMap);
				
			} else {
				Map<String,Object> xmlRuleBuilderModelList = xmlRuleBuilderService.loadXmlRuleBuilderDetails(
						paginationModel, programId, migrationType, subType, user, customerId);

				//rowCount = xmlRuleBuilderModelList.size();
				//pageCount = CommonUtil.getPageCount(rowCount, Integer.parseInt(paginationModel.getCount()));
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("xmlRuleDetail", xmlRuleBuilderModelList.get("xmlRuleBuilderEntityList"));
				resultMap.put("pageCount", xmlRuleBuilderModelList.get("totalCount"));
				//return new JSONObject(resultMap);
			}
			
			ProgramTemplateEntity programTemplateEntity = fileUploadRepository.getProgramTemplate(programId, Constants.PROMPT_TEMPLATE);
			
			
			if(programTemplateEntity!=null) {
				if(programTemplateEntity.getValue()!=null && !programTemplateEntity.getValue().trim().isEmpty()) {
					JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
					resultMap.put("prompt", objData);
				}else {
					resultMap.put("prompt", null);
				}
				
			}else {
				resultMap.put("prompt", null);
			}

		}catch(ParseException e2) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap
					.get(FaultCodes.WRONG_PROMPT_TEMPLATE_JSON));
		} catch (Exception e) {
			logger.error(" loadXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
		
		return new JSONObject(resultMap);
	}

	@RequestMapping(value = "/updateXmlFileRule", method = RequestMethod.POST)
	public JSONObject updateXmlFileRule(@RequestBody JSONObject xmlRuleDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		try {

			sessionId = xmlRuleDetails.get("sessionId").toString();
			serviceToken = xmlRuleDetails.get("serviceToken").toString();			
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			migrationType = xmlRuleDetails.get("migrationType").toString();
			programId = (int) xmlRuleDetails.get("programId");
			subType = xmlRuleDetails.get("subType").toString();
			
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

			XmlRuleBuilderModel xmlRuleBuilderModel = new Gson().fromJson(
					xmlRuleDetails.toJSONString((Map) xmlRuleDetails.get("xmlRuleDetail")), XmlRuleBuilderModel.class);

			if (xmlRuleBuilderModel != null) {
				if (xmlRuleBuilderService.updateXmlRuleBuilder(xmlRuleBuilderModel, migrationType, programId, subType,
						sessionId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,
							Constants.ACTION_UPDATE, "XmlRuleBuilder Updated Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "XmlRuleBuilder updated successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "XmlRuleBuilder updation failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "XmlRuleBuilder updation failed", sessionId,
					serviceToken);
		} catch (Exception e) {
			logger.error(" updateXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
	}

	@RequestMapping(value = "/deleteXmlFileRule", method = RequestMethod.POST)
	public JSONObject deleteXmlFileRule(@RequestBody JSONObject xmlRuleDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		int xmlRuleBuilderId = 0;
		try {
			sessionId = xmlRuleDetails.get("sessionId").toString();
			serviceToken = xmlRuleDetails.get("serviceToken").toString();
			xmlRuleBuilderId = Integer.valueOf((String) xmlRuleDetails.get("id"));
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (xmlRuleBuilderId != 0) {
				if (xmlRuleBuilderService.deleteXmlRuleBuilder(xmlRuleBuilderId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_COMMAND_RULE_BUILDER,
							Constants.ACTION_DELETE, "XmlRuleBuilder Deleted Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "XmlRuleBuilder deleted successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "XmlRuleBuilder deletion failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "XmlRuleBuilder deletion failed", sessionId,
					serviceToken);
		} catch (Exception e) {
			logger.error(" deleteXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
	}
}
