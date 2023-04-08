package com.smart.rct.migration.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.service.LsmService;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.model.SearchModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.migration.repository.UploadFileRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.repositoryImpl.UseCaseBuilderRepositoryImpl;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PaginationModel;

@RestController
public class UseCaseBuilderController {

	@Autowired
	UseCaseBuilderService useCaseBuilderService;

	@Autowired
	UseCaseBuilderRepositoryImpl useCaseBuilderRepositoryImpl;

	@Autowired
	LsmService lsmService;

	@Autowired
	UploadFileService uploadFileService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;

	@Autowired
	UploadFileRepository uploadFileRepository;

	private static final Logger logger = LoggerFactory.getLogger(FileRuleBuilderController.class);

	@RequestMapping(value = "/createUseCaseBuilder", method = RequestMethod.POST)
	public JSONObject createUseCaseBuilder(@RequestBody JSONObject useCaseDetail) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		int customerId = 0;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		NeVersionEntity neVersionEntity = null;
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		try {
			sessionId = useCaseDetail.get("sessionId").toString();
			serviceToken = useCaseDetail.get("serviceToken").toString();
			customerId = (int) useCaseDetail.get("customerId");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = useCaseDetail.get("migrationType").toString();
			programId = (int) useCaseDetail.get("programId");
			subType = useCaseDetail.get("subType").toString();

			if (migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			} else if (migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			} else if (migrationType.equalsIgnoreCase("postmigration")) {
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
			} else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			} else if (subType.equalsIgnoreCase("PREAUDIT")) {
				subType = "PREAUDIT";
			}else if (subType.equalsIgnoreCase("neststus")) {
				subType = "NESTATUS";
			}

			UseCaseBuilderModel useCaseBuilderModel = new Gson().fromJson(
					useCaseDetail.toJSONString((Map) useCaseDetail.get("useCaseDetails")), UseCaseBuilderModel.class);

			if (useCaseBuilderModel.getLsmVersion() != null && !useCaseBuilderModel.getLsmVersion().trim().isEmpty()) {
				neVersionEntity = uploadFileService.getNeVersionEntity(useCaseBuilderModel.getLsmVersion(),
						useCaseDetail.get("programId").toString());
			}

			useCaseBuilderModel.setCustomerId(customerId);
			useCaseBuilderModel.setNeVersion(neVersionEntity);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			// check duplicate rule name creation
			boolean ruleNameExistence = useCaseBuilderService.duplicateUseCaseName(useCaseBuilderModel.getUseCaseName(),
					customerId, migrationType, programId, user.getRole(), subType);

			UseCaseBuilderEntity executionSequenceExistence = useCaseBuilderService
					.duplicateExecutionSequence(useCaseBuilderModel.getExecutionSequence(), programId, user.getRole());

			if (ruleNameExistence) {
				return CommonUtil.buildResponseJson(Constants.FAIL, "UseCase Name already exists !", sessionId,
						serviceToken);
			}

			if (executionSequenceExistence != null) {
				return CommonUtil.buildResponseJson(Constants.FAIL,
						"Execution Sequence already exist for the program !", sessionId, serviceToken);
			}

			List<UseCaseScriptsModel> scriptList = useCaseBuilderModel.getScriptList();
			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
				UseCaseBuilderParamEntity scriptExeSeq = useCaseBuilderService
						.duplicateScriptExecutionSequence(useCaseScriptsModel.getScriptSequence(), programId);
				if (scriptExeSeq != null) {
					return CommonUtil.buildResponseJson(Constants.FAIL, "Script Execution Sequence("
							+ scriptExeSeq.getExecutionSequence() + ") already exist for the program !", sessionId,
							serviceToken);
				}

			}

			ArrayList scriptNameList = new ArrayList();
			int iIncrement = 0;
			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
				if (iIncrement == 0) {
					scriptNameList.add(useCaseScriptsModel.getScript().get("scriptName"));
					iIncrement = 1;
				} else {
					if (scriptNameList.contains(useCaseScriptsModel.getScript().get("scriptName"))) {
						return CommonUtil.buildResponseJson(Constants.FAIL, "Scripts already exist", sessionId,
								serviceToken);
					} else {
						scriptNameList.add(useCaseScriptsModel.getScript().get("scriptName"));
					}
				}
			}

			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {

				if (useCaseScriptsModel.getScriptSequence() != null
						&& !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					// do nothing
				} else {
					CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
							.getCheckListDetails(programId);
					if (checkListScriptDetEntity == null) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								"Check List script is empty for the program", sessionId, serviceToken);
					}
					CheckListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
							useCaseScriptsModel.getScript().get("scriptName"), "",
							checkListScriptDetEntity.getCheckListFileName());
					if (CheckListScriptDetails == null || CheckListScriptDetails.isEmpty()) {
						return CommonUtil.buildResponseJson(Constants.FAIL, "Script is not available in the check list",
								sessionId, serviceToken);
					}

					if (CheckListScriptDetails.size() > 1) {
						/*
						 * return CommonUtil.buildResponseJson(Constants.FAIL,
						 * "Check List script having more than one record", sessionId, serviceToken);
						 */
					}
				}
			}

			if (useCaseBuilderModel != null) {
				if (useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, customerId, migrationType,
						programId, subType, sessionId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_USE_CASE_BUILDER,
							Constants.ACTION_SAVE, "Use Case Created Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "UseCaseBuilder created successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "UseCaseBuilder creation failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "For creating UseCaseBuilder data is not proper",
					sessionId, serviceToken);
		} catch (Exception ex) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(ex));
			return CommonUtil.buildResponseJson(Constants.FAIL, ex.getMessage(), sessionId, serviceToken);
		}
	}

	@RequestMapping(value = "/loadUseCaseBuilder", method = RequestMethod.POST)
	public JSONObject loadUseCaseBuilder(@RequestBody JSONObject useCaseBuilderDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String searchStatus = null;
		int customerId = 0;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			sessionId = useCaseBuilderDetails.get("sessionId").toString();
			serviceToken = useCaseBuilderDetails.get("serviceToken").toString();
			searchStatus = useCaseBuilderDetails.get("searchStatus").toString();
			customerId = (int) useCaseBuilderDetails.get("customerId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			migrationType = useCaseBuilderDetails.get("migrationType").toString();
			programId = (int) useCaseBuilderDetails.get("programId");
			subType = useCaseBuilderDetails.get("subType").toString();
			String pagination = useCaseBuilderDetails.get("pagination").toString();

			/*
			 * if(migrationType.equalsIgnoreCase("premigration")) { migrationType =
			 * "PreMigration"; }else if(migrationType.equalsIgnoreCase("migration")) {
			 * migrationType = "Migration"; }else
			 * if(migrationType.equalsIgnoreCase("postmigration")) { migrationType =
			 * "PostMigration"; }
			 * 
			 * if (subType.equalsIgnoreCase("precheck")) { subType = "PreCheck"; } else if
			 * (subType.equalsIgnoreCase("commission")) { subType = "Commission"; } else if
			 * (subType.equalsIgnoreCase("postcheck")) { subType = "PostCheck"; } else if
			 * (subType.equalsIgnoreCase("AUDIT")) { subType = "Audit"; }else if
			 * (subType.equalsIgnoreCase("RANATP")) { subType = "RanATP"; }
			 */

			int rowCount = 0;
			int pageCount = 0;
			PaginationModel paginationModel = new Gson().fromJson(pagination, PaginationModel.class);

			Map<String, Map<String, List<Map<String, String>>>> neList = useCaseBuilderService
					.getSmScriptList(programId, migrationType, subType);
			//List<Map<String, String>> scriptList = useCaseBuilderService.getScriptList(programId, migrationType);
			List<Map<String, String>> scriptInfoWithoutVersionName = useCaseBuilderService
					.scriptInfoWithoutVersionName(programId, migrationType, subType);
			Map<String, List<Map<String, String>>> scriptListWithoutSM = useCaseBuilderService
					.getScriptListWithoutSM(programId, migrationType, subType);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if ("search".equalsIgnoreCase(searchStatus)) {
				SearchModel searchModel = new Gson().fromJson(
						useCaseBuilderDetails.toJSONString((Map) useCaseBuilderDetails.get("searchCriteria")),
						SearchModel.class);

				Map<String, Object> useCaseBuilderModelList = useCaseBuilderService.loadUseCaseBuilderSearchDetails(
						paginationModel, searchModel, customerId, migrationType, programId, subType, user);
				rowCount = useCaseBuilderModelList.size();
				pageCount = CommonUtil.getPageCount(rowCount, Integer.parseInt(paginationModel.getCount()));
				List<Map<String, String>> commandRuleList = useCaseBuilderService.getCommandRuleList(programId,
						migrationType, subType);
				List<Map<String, String>> fileRuleList = useCaseBuilderService.getFileRuleList(programId, migrationType,
						subType);
				List<Map<String, String>> xmlRuleList = useCaseBuilderService.getXmlRuleList(programId, migrationType,
						subType);
				List<Map<String, String>> shellRuleList = useCaseBuilderService.getShellRuleList(programId,
						migrationType, subType);
				resultMap.put("nwTypeInfo", neList);
				// resultMap.put("scriptInfo", scriptList);
				resultMap.put("scriptInfo", scriptInfoWithoutVersionName);
				resultMap.put("scriptInfoWithoutSM", scriptListWithoutSM);
				// resultMap.put("scriptInfoWithoutVersionName", scriptInfoWithoutVersionName);
				resultMap.put("cmdRules", commandRuleList);
				resultMap.put("fileRules", fileRuleList);
				resultMap.put("xmlRules", xmlRuleList);
				resultMap.put("shellRules", shellRuleList);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("useCaseDetails", useCaseBuilderModelList.get("cmdRuleBuilderData"));
				resultMap.put("pageCount", useCaseBuilderModelList.get("totalCount"));
				return new JSONObject(resultMap);
			} else {

				List<UseCaseBuilderModel> useCaseBuilderModelList = useCaseBuilderService.loadUseCaseBuilderDetails(
						paginationModel, customerId, migrationType, programId, subType, user);
				int page = Integer.parseInt(paginationModel.getPage());
				int count = Integer.parseInt(paginationModel.getCount());
				Object pageCounts = useCaseBuilderService.getPageCount(page, count, customerId, migrationType,
						programId, subType, user);
				// rowCount = useCaseBuilderModelList.size();
				// pageCount = CommonUtil.getPageCount(rowCount,
				// Integer.parseInt(paginationModel.getCount()));
				List<Map<String, String>> commandRuleList = useCaseBuilderService.getCommandRuleList(programId,
						migrationType, subType);
				List<Map<String, String>> fileRuleList = useCaseBuilderService.getFileRuleList(programId, migrationType,
						subType);
				List<Map<String, String>> xmlRuleList = useCaseBuilderService.getXmlRuleList(programId, migrationType,
						subType);
				List<Map<String, String>> shellRuleList = useCaseBuilderService.getShellRuleList(programId,
						migrationType, subType);
				resultMap.put("nwTypeInfo", neList);
				// resultMap.put("scriptInfo", scriptList);
				resultMap.put("scriptInfo", scriptInfoWithoutVersionName);
				resultMap.put("scriptInfoWithoutSM", scriptListWithoutSM);
				// resultMap.put("scriptInfoWithoutVersionName", scriptInfoWithoutVersionName);
				resultMap.put("cmdRules", commandRuleList);
				resultMap.put("fileRules", fileRuleList);
				resultMap.put("shellRules", shellRuleList);
				resultMap.put("xmlRules", xmlRuleList);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("useCaseDetails", useCaseBuilderModelList);
				resultMap.put("pageCount", pageCounts);
				return new JSONObject(resultMap);
			}

		} catch (Exception e) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
	}

	@RequestMapping(value = "/updateUseCaseBuilder", method = RequestMethod.POST)
	public JSONObject updateUseCaseBuilder(@RequestBody JSONObject useCaseDetail) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		Integer customerId = 0;
		String migrationType = null;
		int programId = 0;
		String subType = null;
		NeVersionEntity neVersionEntity = null;
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		try {

			sessionId = useCaseDetail.get("sessionId").toString();
			serviceToken = useCaseDetail.get("serviceToken").toString();
			customerId = (int) useCaseDetail.get("customerId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			migrationType = useCaseDetail.get("migrationType").toString();
			programId = (int) useCaseDetail.get("programId");
			subType = useCaseDetail.get("subType").toString();

			if (migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			} else if (migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			} else if (migrationType.equalsIgnoreCase("postmigration")) {
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
			} else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			}else if (subType.equalsIgnoreCase("PREAUDIT")) {
				subType = "PREAUDIT";
			}else if (subType.equalsIgnoreCase("NESTATUS")) {
				subType = "NESTATUS";
			}

			UseCaseBuilderModel useCaseBuilderModel = new Gson().fromJson(
					useCaseDetail.toJSONString((Map) useCaseDetail.get("useCaseDetails")), UseCaseBuilderModel.class);

			if (useCaseBuilderModel.getLsmVersion() != null && !useCaseBuilderModel.getLsmVersion().trim().isEmpty()) {
				neVersionEntity = uploadFileService.getNeVersionEntity(useCaseBuilderModel.getLsmVersion(),
						useCaseDetail.get("programId").toString());

			}

			useCaseBuilderModel.setNeVersion(neVersionEntity);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			UseCaseBuilderEntity executionSequenceExistence = useCaseBuilderService
					.duplicateExecutionSequence(useCaseBuilderModel.getExecutionSequence(), programId, user.getRole());

			if (executionSequenceExistence != null) {

				if (executionSequenceExistence.getId() == Integer.parseInt(useCaseBuilderModel.getId())) {
					// do nothing
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL,
							"Execution Sequence already exist for the program !", sessionId, serviceToken);
				}

			}

			List<UseCaseScriptsModel> scriptList = useCaseBuilderModel.getScriptList();

			if (scriptList.isEmpty()) {
				return CommonUtil.buildResponseJson(Constants.FAIL, "Atleast one script should be mandatory", sessionId,
						serviceToken);
			}

			ArrayList scriptNameList = new ArrayList();
			int iIncrement = 0;
			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
				if (iIncrement == 0) {
					scriptNameList.add(useCaseScriptsModel.getScript().get("scriptName"));
					iIncrement = 1;
				} else {
					if (scriptNameList.contains(useCaseScriptsModel.getScript().get("scriptName"))) {
						return CommonUtil.buildResponseJson(Constants.FAIL, "Scripts already exist", sessionId,
								serviceToken);
					} else {
						scriptNameList.add(useCaseScriptsModel.getScript().get("scriptName"));
					}
				}
			}

			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {

				if (useCaseScriptsModel.getScriptSequence() != null
						&& !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					// do nothing
				} else {
					CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
							.getCheckListDetails(programId);
					if (checkListScriptDetEntity == null) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								"Check List script is empty for the program", sessionId, serviceToken);
					}
					CheckListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
							useCaseScriptsModel.getScript().get("scriptName"), "",
							checkListScriptDetEntity.getCheckListFileName());
					if (CheckListScriptDetails == null || CheckListScriptDetails.isEmpty()) {
						return CommonUtil.buildResponseJson(Constants.FAIL, "Script is not available in the check list",
								sessionId, serviceToken);
					}

					if (CheckListScriptDetails.size() > 1) {
						/*
						 * return CommonUtil.buildResponseJson(Constants.FAIL,
						 * "Check List script having more than one record", sessionId, serviceToken);
						 */
					}
				}
			}

			if (useCaseBuilderModel != null) {
				if (useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, customerId, migrationType,
						programId, subType, sessionId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_USE_CASE_BUILDER,
							Constants.ACTION_UPDATE, "Use Case Builder Updated Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "UseCaseBuilder updated successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "UseCaseBuilder updation failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "UseCaseBuilder updation failed", sessionId,
					serviceToken);
		} catch (Exception e) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
	}

	@RequestMapping(value = "/deleteUseCaseBuilder", method = RequestMethod.POST)
	public JSONObject deleteUseCaseBuilder(@RequestBody JSONObject useCaseDetail) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		int useCaseBuilderId = 0;
		try {
			sessionId = useCaseDetail.get("sessionId").toString();
			serviceToken = useCaseDetail.get("serviceToken").toString();
			useCaseBuilderId = Integer.valueOf((String) useCaseDetail.get("id"));
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepository
					.getUseCaseBuilderEntity(useCaseBuilderId);
			if (useCaseBuilderEntity.getUseCount() > 0) {
				return CommonUtil.buildResponseJson(Constants.FAIL,
						"Use Case is already ran for Migration/Post Migration", sessionId, serviceToken);
			}
			if (useCaseBuilderId != 0) {
				if (useCaseBuilderService.deleteUseCaseBuilder(useCaseBuilderId)) {
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_USE_CASE_BUILDER,
							Constants.ACTION_DELETE, "Use Case Builder Deleted Successfully", sessionId);
					return CommonUtil.buildResponseJson(Constants.SUCCESS, "UseCaseBuilder deleted successfully",
							sessionId, serviceToken);
				} else {
					return CommonUtil.buildResponseJson(Constants.FAIL, "UseCaseBuilder deletion failed", sessionId,
							serviceToken);
				}
			}
			return CommonUtil.buildResponseJson(Constants.FAIL, "UseCaseBuilder deletion failed", sessionId,
					serviceToken);
		} catch (Exception e) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
			return CommonUtil.buildResponseJson(Constants.FAIL, e.getMessage(), sessionId, serviceToken);
		}
	}

	/**
	 * This method will return the Script content from Script file
	 * 
	 * @param viewScript
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/viewUseCaseScript")
	public JSONObject viewUseCaseScript(@RequestBody JSONObject viewScript) {
		String sessionId = null;
		String serviceToken = null;
		String scriptId = null;
		String useCaseId = null;
		String scriptContent = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		String migrationType = null;
		String subType = null;
		String programId = null;
		try {
			sessionId = viewScript.get("sessionId").toString();
			serviceToken = viewScript.get("serviceToken").toString();
			scriptId = viewScript.get("scriptId").toString();
			useCaseId = viewScript.get("useCaseId").toString();
			migrationType = viewScript.get("migrationType").toString();
			subType = viewScript.get("subType").toString();
			programId = viewScript.get("programId").toString();

			if (migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			} else if (migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			} else if (migrationType.equalsIgnoreCase("postmigration")) {
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
			} else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			}else if (subType.equalsIgnoreCase("PREAUDIT")) {
				subType = "PREAUDIT";
			}else if (subType.equalsIgnoreCase("NESTATUS")) {
				subType = "NESTATUS";
			}

			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			UploadFileEntity uploadFileEntity = uploadFileRepository.getUploadFileEntity(Integer.parseInt(scriptId));
			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepository
					.getUseCaseBuilderEntity(Integer.parseInt(useCaseId));

			String filePath = "";
			if (!"PostMigration".equals(migrationType) && (useCaseBuilderEntity.getUseCaseName().contains(Constants.COMMISION_USECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains(Constants.RF_USECASE))) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.CUSTOMER + "/"
						+ Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", programId)
								.replace("migrationType", migrationType).replace("subType", subType)
						+ useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			} else {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + uploadFileEntity.getFilePath()
						+ useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			}

			scriptContent = uploadFileService.readContentFromFile(filePath, uploadFileEntity.getFileName());
			mapObject.put("scriptFileContent", scriptContent);
			mapObject.put("status", Constants.SUCCESS);

		} catch (Exception e) {

			logger.info("Exception in viewScript() : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to show the content of the script");
		}
		return mapObject;
	}

	/**
	 * This method will updating Script content into Script file
	 * 
	 * @param saveViewScript
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/saveUseCaseViewScript")
	public JSONObject saveUseCaseViewScript(@RequestBody JSONObject saveViewScript) {
		String sessionId = null;
		String serviceToken = null;
		String scriptId = null;
		String useCaseId = null;
		String scriptFileContent = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		String migrationType = null;
		String subType = null;
		String programId = null;
		try {
			sessionId = saveViewScript.get("sessionId").toString();
			serviceToken = saveViewScript.get("serviceToken").toString();
			scriptId = saveViewScript.get("scriptId").toString();
			useCaseId = saveViewScript.get("useCaseId").toString();
			scriptFileContent = saveViewScript.get("scriptFileContent").toString();
			migrationType = saveViewScript.get("migrationType").toString();
			subType = saveViewScript.get("subType").toString();
			programId = saveViewScript.get("programId").toString();

			if (migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			} else if (migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			} else if (migrationType.equalsIgnoreCase("postmigration")) {
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
			} else if (subType.equalsIgnoreCase("RANATP")) {
				subType = "RanATP";
			} else if (subType.equalsIgnoreCase("")) {
				subType = "NEGrow";
			}else if (subType.equalsIgnoreCase("PREAUDIT")) {
				subType = "PREAUDIT";
			}else if (subType.equalsIgnoreCase("NESTATUS")) {
				subType = "NESTATUS";
			}

			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			UploadFileEntity uploadFileEntity = uploadFileRepository.getUploadFileEntity(Integer.parseInt(scriptId));
			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepository
					.getUseCaseBuilderEntity(Integer.parseInt(useCaseId));

			String filePath = "";
			if (!"PostMigration".equals(migrationType) && (useCaseBuilderEntity.getUseCaseName().contains(Constants.COMMISION_USECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains(Constants.RF_USECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains(Constants.GROWCELLUSECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains(Constants.GROWENBUSECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains(Constants.PNPUSECASE)
					|| useCaseBuilderEntity.getUseCaseName().contains("AU")
					|| useCaseBuilderEntity.getUseCaseName().contains("AUCaCell")
					|| useCaseBuilderEntity.getUseCaseName().contains("pnp"))) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.CUSTOMER + "/"
						+ Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", programId)
								.replace("migrationType", migrationType).replace("subType", subType)
						+ useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			} else {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + uploadFileEntity.getFilePath()
						+ useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			}

			// updating content of the script
			if (uploadFileService.saveViewScript(filePath, uploadFileEntity.getFileName(), scriptFileContent)) {
				mapObject.put("status", Constants.SUCCESS);
			} else {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Failed to save script content");
			}
		} catch (Exception e) {

			logger.info("Exception in saveViewScript() : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("status", Constants.FAIL);
			mapObject.put("reason", "Failed to save Upload Script content");
		}
		return mapObject;
	}

}
