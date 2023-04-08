package com.smart.rct.migration.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.query.criteria.internal.predicate.IsEmptyPredicate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.common.repositoryImpl.LsmRepositoryImpl;
import com.smart.rct.common.service.LsmService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.dto.UploadFileDto;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.model.UploadFileModel;
import com.smart.rct.migration.repository.CmdRuleBuilderRepository;
import com.smart.rct.migration.service.CmdRuleBuilderService;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PasswordCrypt;

@RestController
public class UploadFileController {
	final static Logger logger = LoggerFactory.getLogger(UploadFileController.class);

	@Autowired
	public UploadFileService uploadFileService;

	@Autowired
	public LsmRepositoryImpl lsmRepositoryImpl;
	@Autowired
	LsmService lsmService;
	@Autowired
	UploadFileDto uploadFileDto;
	@Autowired
	UseCaseBuilderService useCaseBuilderService;
	
	@Autowired
	CmdRuleBuilderRepository cmdRuleBuilderRepository;

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	/**
	 * This method will upload the script
	 * 
	 * @param UPLOADFile,uploadFile
	 * @return JSONObject
	 */

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/saveUploadScript")
	@ResponseBody
	public JSONObject saveUploadScript(@RequestPart(required = true, value = "UPLOAD") MultipartFile UPLOADFile,
			@RequestParam("sessionId") String sessionId, @RequestParam("allowDuplicate") String allowDuplicate,
			@RequestParam("serviceToken") String serviceToken, @RequestParam("customerName") String customerName,
			@RequestParam("fileName") String fileName,@RequestParam("uploadedBy") String uploadedBy, 
			@RequestParam("lsmName") String lsmName, 
			@RequestParam("lsmVersion") String lsmVersion,@RequestParam("remarks") String remarks, 
			@RequestParam("customerId") String customerId,@RequestParam("migrationType") String migrationType,
			@RequestParam("programName") String programName,@RequestParam("programId") String programId
			,@RequestParam("state") String state,@RequestParam("subType") String subType,
			@RequestParam("scriptType") String scriptType,@RequestParam("connectionLocation") String connectionLocation,
			@RequestParam("connectionLocationUserName") String connectionLocationUserName,@RequestParam("connectionLocationPwd") String connectionLocationPwd,
			@RequestParam("connectionTerminal") String connectionTerminal,@RequestParam("connectionTerminalUserName") String connectionTerminalUserName,
			@RequestParam("connectionTerminalPwd") String connectionTerminalPwd,@RequestParam("prompt") String prompt,@RequestParam("arguments") String arguments,
			@RequestParam("sudoPassword") String sudoPassword) {
		JSONObject result = null;
		JSONObject expiryDetails = null;
		NetworkConfigEntity neEntity = null;
		NeVersionEntity neVersionEntity = null;
		JSONObject mapObject = new JSONObject();
		StringBuilder uploadPath = new StringBuilder();
		StringBuilder uploadDBPath = new StringBuilder();
		try {
			boolean isAllowDuplicate = Boolean.valueOf(allowDuplicate);
			int custId = Integer.valueOf(customerId.toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			if(prompt!=null && !prompt.trim().isEmpty()) {
				// Do nothing
			}else {
				mapObject.put("reason", "Prompt is empty");
				return mapObject;
			}
			
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
			}else if (subType.equalsIgnoreCase("PREAUDIT")) {
				subType = "PREAUDIT";
			}else if (subType.equalsIgnoreCase("NESTATUS")) {
				subType = "NESTATUS";
			}
			
			UploadFileEntity uploadScriptDuplicateEntity = uploadFileService.getUploadScriptDuplicate(fileName,migrationType,programName,subType);
			if(uploadScriptDuplicateEntity!=null) {
				mapObject.put("reason", "Upload script already exists");
				return mapObject;
			}
			
			String sFileName = UPLOADFile.getOriginalFilename();
			
			if(!sFileName.equalsIgnoreCase("ConstantFile.sh")) {
				if(uploadFileService.isFileEmpty(UPLOADFile)) {
					mapObject.put("reason", "Upload Script Empty");
					return mapObject;
				}
			}
			
			uploadPath = uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"));
			
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(Integer.parseInt(programId));
			
			if(lsmVersion!=null && !lsmVersion.trim().isEmpty() && lsmName!=null && !lsmName.trim().isEmpty()) {
				neEntity = uploadFileService.getNeEntity(lsmVersion, lsmName,Integer.parseInt(programId));
			}
			
			if(lsmName!=null && !lsmName.trim().isEmpty()) {
				
				//uploadPath.append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator).append(neEntity.getNeVersionEntity().getId()).append(File.separator)
				//.append(neEntity.getId()).append(File.separator);
				
				uploadPath.append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITH_VERSION_NE.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType)
						.replace("versionId", neEntity.getNeVersionEntity().getId().toString()).replace("neId", neEntity.getId().toString()));
				
				//uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator).append(neEntity.getNeVersionEntity().getId()).append(File.separator)
				//.append(neEntity.getId()).append(File.separator);
				
				uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITH_VERSION_NE.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType)
						.replace("versionId", neEntity.getNeVersionEntity().getId().toString()).replace("neId", neEntity.getId().toString()));
				
			}else if(lsmVersion!=null && !lsmVersion.trim().isEmpty()){
				
				neVersionEntity = uploadFileService.getNeVersionEntity(lsmVersion,programId);
				
				//uploadPath.append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator).append(neVersionEntity.getId()).append(File.separator);
				
				uploadPath.append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITH_VERSION.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType)
						.replace("versionId", neVersionEntity.getId().toString()));
				
				//uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator).append(neVersionEntity.getId()).append(File.separator);
				
				uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITH_VERSION.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType)
						.replace("versionId", neVersionEntity.getId().toString()));
				
				
			}else {
				
				//uploadPath.append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator);
				uploadPath.append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType));
				
				
				//uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(File.separator).append(programId).append(File.separator).append(migrationType).append(File.separator)
				//.append(subType).append(File.separator);
				
				uploadDBPath.append(File.separator).append(Constants.CUSTOMER).append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", programId).replace("migrationType", migrationType).replace("subType", subType));
			}
			
			String tempPath = uploadPath.toString();
			File folder = new File(uploadPath.toString());

			if (!folder.exists()) {
				FileUtil.createDirectory(uploadPath.toString());
			}

			if (StringUtils.isNotEmpty(tempPath.toString())) {
				File file = new File(tempPath.toString() + UPLOADFile.getOriginalFilename());
				if (file.exists()) {
					if (!isAllowDuplicate) {
						result = CommonUtil.buildResponseJson(Constants.CONFIRM,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPLOAD_SCRIPT_DUPLICATE),
								sessionId, serviceToken);
						return result;
					}

				}
			}

			if (uploadFileService.uploadFile(UPLOADFile, uploadPath.toString())) {
				
				String command = tempPath.toString()+UPLOADFile.getOriginalFilename();
				if(UPLOADFile.getOriginalFilename().contains(".")) {
					String[] extention = UPLOADFile.getOriginalFilename().split("\\.");
					if(extention[1].equalsIgnoreCase("sh") || extention[1].equalsIgnoreCase("py")) {
						boolean output = uploadFileService.executeCommand(command);
						/*if(output) {
							uploadFileService.removeFile(command);
							mapObject.put("reason", "Uploaded Script Content Error");
							return mapObject;
						}*/
					}
				}else {
					mapObject.put("reason", "Upload file with proper extension");
					return mapObject;
				}

				UploadFileEntity uploadFileEntity = new UploadFileEntity();
				uploadFileEntity.setFileName(UPLOADFile.getOriginalFilename());
				uploadFileEntity.setFilePath(uploadDBPath.toString());
				uploadFileEntity.setNeListEntity(neEntity);
				uploadFileEntity.setUploadedBy(uploadedBy);
				uploadFileEntity.setRemarks(remarks);
				uploadFileEntity.setUseCount(0);
				uploadFileEntity.setCustomerId(custId);
				uploadFileEntity.setCreationDate(new Date());
				uploadFileEntity.setProgram(programName);
				uploadFileEntity.setMigrationType(migrationType);
				uploadFileEntity.setState(state);
				uploadFileEntity.setSubType(subType);
				uploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				uploadFileEntity.setNeVersion(neVersionEntity);
				uploadFileEntity.setScriptType(scriptType);
				uploadFileEntity.setConnectionLocation(connectionLocation);
				uploadFileEntity.setConnectionLocationUserName(connectionLocationUserName);
				uploadFileEntity.setPrompt(prompt);
				uploadFileEntity.setArguments(arguments);
				
				if(connectionLocationPwd!=null && !connectionLocationPwd.trim().isEmpty()) {
					uploadFileEntity.setConnectionLocationPwd(PasswordCrypt.encrypt(connectionLocationPwd));
				}else {
					uploadFileEntity.setConnectionLocationPwd("");
				}
				
				uploadFileEntity.setConnectionTerminal(connectionTerminal);
				uploadFileEntity.setConnectionTerminalUserName(connectionTerminalUserName);
				if(connectionTerminalPwd!=null && !connectionTerminalPwd.trim().isEmpty()) {
					uploadFileEntity.setConnectionTerminalPwd(PasswordCrypt.encrypt(connectionTerminalPwd));
				}else {
					uploadFileEntity.setConnectionTerminalPwd("");
				}
				
				
				if(sudoPassword!=null && !sudoPassword.trim().isEmpty()) {
					uploadFileEntity.setSudoPassword(PasswordCrypt.encrypt(sudoPassword));
				}else {
					uploadFileEntity.setSudoPassword("");
				}

				if (!isAllowDuplicate) {
					uploadFileService.createUploadScript(uploadFileEntity);
				} else {
					UploadFileEntity uploadScriptEntity = uploadFileService.getUploadScriptByPath(uploadPath,fileName);
					uploadScriptEntity.setRemarks(remarks);
					uploadFileService.updateUploadScript(uploadScriptEntity);
				}
				mapObject.put("status", Constants.SUCCESS);
				commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_SCRIPT_STORE, Constants.ACTION_UPLOAD, "Script File Uploaded Successfully", sessionId);
			}

		} catch (Exception e) {
			logger.info("Exception in load Upload Script : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to Upload Script");
		}
		return mapObject;
	}

	private Map<String, String> loadArgumentDetails() {
		Map<String, String> argumentDetails = new HashMap<String, String>();
		try {
			argumentDetails.put(Constants.NE_ID, Constants.NE_ID_DESC);
			argumentDetails.put(Constants.NE_NAME, Constants.NE_NAME_DESC);
			argumentDetails.put(Constants.CDU_IP, Constants.CDU_IP_DESC);
			argumentDetails.put(Constants.CASCADE_ID, Constants.CASCADE_ID_DESC);
			argumentDetails.put(Constants.AD_ID, Constants.AD_ID_DESC);
			argumentDetails.put(Constants.LSM_USERNAME, Constants.LSM_USERNAME_DESC);
			argumentDetails.put(Constants.LSM_PWD, Constants.LSM_PWD_DESC);
			argumentDetails.put(Constants.MARKET, Constants.MARKET_DESC);
			argumentDetails.put(Constants.RS_IP, Constants.RS_IP_DESC);
			argumentDetails.put(Constants.MSMA_IP, Constants.MSMA_IP_DESC);
			argumentDetails.put(Constants.JUMP_BOX_IP, Constants.JUMP_BOX_IP_DESC);
			argumentDetails.put(Constants.JUMP_SANE_IP, Constants.JUMP_SANE_IP_DESC);
			argumentDetails.put(Constants.UNQ_ID, Constants.UNQ_ID_DESC);
			argumentDetails.put(Constants.VLSM_RS_IP, Constants.VLSM_RS_IP_DESC);
			argumentDetails.put(Constants.LSM_IP, Constants.LSM_IP_DESC);
			argumentDetails.put(Constants.PUT_SERVER_IP, Constants.PUT_SERVER_IP_DESC);
			argumentDetails.put(Constants.VLSM_IP, Constants.VLSM_IP_DESC);
			argumentDetails.put(Constants.IS_LAB, Constants.IS_LAB_DESC);
			argumentDetails.put(Constants.MCMA_IP, Constants.MCMA_IP_DESC);
			argumentDetails.put(Constants.DIR, Constants.DIR_DESC);
			argumentDetails.put(Constants.OPS_ATP_INPUT_FILE, Constants.OPS_ATP_INPUT_FILE_DESC);
			argumentDetails.put(Constants.BANDS, Constants.BANDS_DESC);
			argumentDetails.put(Constants.CREDENTIALS, Constants.CREDENTIALS_DESC);
			argumentDetails.put(Constants.SANE_OPTIONS, Constants.SANE_OPTIONS_DESC);
			argumentDetails.put(Constants.HOP_STRING, Constants.HOP_STRING_DESC);
			argumentDetails.put(Constants.EXCEL_FILE, Constants.EXCEL_FILE_DESC);
			
		} catch (Exception e) {
			logger.info("Exception in loadArgumentDetails : " + ExceptionUtils.getFullStackTrace(e));
		}
		return argumentDetails;
	}
	/**
	 * This api returns upload script details
	 * 
	 * @param page, count
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getUploadScript")
	public JSONObject getUploadScript(@RequestBody JSONObject getUploadFile) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		int programId = 0;
		try {
			sessionId = getUploadFile.get("sessionId").toString();
			serviceToken = getUploadFile.get("serviceToken").toString();
			programId = Integer.valueOf(getUploadFile.get("programId").toString());
//			String customerName = (String) getUploadFile.get("customerName");
			Integer custId = (Integer) getUploadFile.get("customerId");
			
			String migrationType = getUploadFile.get("migrationType").toString();
			String migrationSubType = getUploadFile.get("subType").toString();
			
			/*if(migrationType.equalsIgnoreCase("premigration")) {
				migrationType = "PreMigration";
			}else if(migrationType.equalsIgnoreCase("migration")) {
				migrationType = "Migration";
			}else if(migrationType.equalsIgnoreCase("postmigration")) {
				migrationType = "PostMigration";
			}
			
			if (migrationSubType.equalsIgnoreCase("precheck")) {
				migrationSubType = "PreCheck";
			} else if (migrationSubType.equalsIgnoreCase("commission")) {
				migrationSubType = "Commission";
			} else if (migrationSubType.equalsIgnoreCase("postcheck")) {
				migrationSubType = "PostCheck";
			} else if (migrationSubType.equalsIgnoreCase("AUDIT")) {
				migrationSubType = "Audit";
			}else if (migrationSubType.equalsIgnoreCase("RANATP")) {
				migrationSubType = "RanATP";
			}*/
			
//			custId = 2;
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			Map<String, String> argumentDetails = loadArgumentDetails();
			mapObject.put("argumentDetails", argumentDetails);
			Map<String, Object> paginationData = (Map<String, Object>) getUploadFile.get("pagination");
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
			// reading nwType,lsmVersion,LsmName
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			List<NetworkTypeDetailsModel> nwTypeDetailsList = lsmService.getNetWorksBasedOnCustomer(custId);
			// Map<String, List<String>> nwTypeList =
			// uploadFileService.getNetworkType(custName);
			//Map<String, List<String>> lsmList = uploadFileService.getLsmDetails(nwTypeDetailsList);
			//Map<String, List<String>> lsmList = uploadFileService.getNeDetails(nwTypeDetailsList,Integer.parseInt(getUploadFile.get("programId").toString()));
			Map<String, Map<String, List<Map<String, String>>>> lsmList = useCaseBuilderService
					.getSmScriptList(Integer.parseInt(getUploadFile.get("programId").toString()), migrationType,migrationSubType);
			
			ProgramTemplateEntity programTemplateEntity = fileUploadRepository.getProgramTemplate(programId, Constants.SCRIPT_STORE_TEMPLATE);
			
			
			if(programTemplateEntity!=null) {
				if(programTemplateEntity.getValue()!=null && !programTemplateEntity.getValue().trim().isEmpty()) {
					JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
					JSONArray scripts = (JSONArray) objData.get("scripts");
					JSONObject connLocation = (JSONObject) objData.get("connLocation");
					JSONObject SM = (JSONObject) connLocation.get("SM");
					JSONObject NE = (JSONObject) connLocation.get("NE");
					JSONArray terminals = (JSONArray) SM.get("terminals");
					JSONArray terminalsObj = new JSONArray();
					String terminalName = "";
					for (int i = 0; i < terminals.size(); i++) {
						objData = (JSONObject) terminals.get(i);
						terminalName = (String) objData.get("terminalName");
						if (!terminalName.equalsIgnoreCase("bsm") && !terminalName.equalsIgnoreCase("csr")) {
							terminalsObj.add(terminals.get(i));
						}
					}
					
					JSONObject connectionScriptObj = new JSONObject();
					JSONObject connLocationObj =  new JSONObject();
					JSONObject SMObj =  new JSONObject();
					SMObj.put("terminals", terminalsObj);
					connLocationObj.put("SM", SMObj);
					connLocationObj.put("NE", NE);
					connectionScriptObj.put("connLocation", connLocationObj);
					connectionScriptObj.put("scripts", scripts);
					
					
					
					mapObject.put("ConnectionScriptType", connectionScriptObj);
				}else {
					mapObject.put("ConnectionScriptType", null);
				}
				
			}else {
				mapObject.put("ConnectionScriptType", null);
			}
			
			
			//mapObject.put("scripts", objData.get("scripts").toString());
			//mapObject.put("utility", objData.get("utility").toString());
			//mapObject.put("ConnectionScriptType", objData);
			// mapObject.put("nwType", nwTypeList.get("nwType"));
			mapObject.put("lsmInfo", lsmList);

			// reading upload script list
			Map<String, Object> uploadList = uploadFileService.getUploadScriptDetails(custId, page, count,migrationType,(int) getUploadFile.get("programId"),migrationSubType,user);

			mapObject.put("uploadScriptTableDetails", uploadList.get("uploadScriptTableDetails"));
			mapObject.put("pageCount", uploadList.get("count"));
			mapObject.put("status", Constants.SUCCESS);

		}catch(ParseException e2) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("reason", "Script store template JSON is wrong");
		} catch (Exception e) {
			logger.info("Exception in load Upload Script : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to Upload Script");
		}
		return mapObject;
	}

	/**
	 * This method will update UploadScript details to DB
	 * 
	 * @param updateUploadScript
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/updateUploadScript")
	public JSONObject updateUploadScript(@RequestBody JSONObject updateUploadScript) {
		String sessionId = null;
		String serviceToken = null;
		int customerId = 0;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		int programId = 0;
		try {
			sessionId = updateUploadScript.get("sessionId").toString();
			serviceToken = updateUploadScript.get("serviceToken").toString();
			customerId = Integer.valueOf(updateUploadScript.get("customerId").toString());
			programId = Integer.valueOf(updateUploadScript.get("programId").toString());
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			UploadFileModel uploadFileModel = new Gson().fromJson(
					updateUploadScript.toJSONString((Map) updateUploadScript.get("uploadScriptFormData")),
					UploadFileModel.class);
			uploadFileModel.setCustomerId(customerId);
			uploadFileModel.setProgram(updateUploadScript.get("programName").toString());
			uploadFileModel.setSubType(updateUploadScript.get("subType").toString());
			if(updateUploadScript.get("subType").toString().equalsIgnoreCase("preaudit")) {
				uploadFileModel.setMigrationType("PreMigration");
			}
			if(updateUploadScript.get("subType").toString().equalsIgnoreCase("nestatus")) {
				uploadFileModel.setMigrationType("PreMigration");
			}
			//uploadFileModel.setState(updateUploadScript.get("state").toString());
			
			NetworkConfigEntity neEntity = uploadFileService.getNeEntity(uploadFileModel.getLsmVersion(), uploadFileModel.getLsmName(),programId);
			uploadFileModel.setNeList(neEntity);
			
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);
			uploadFileModel.setCustomerDetailsEntity(customerDetailsEntity);
			
			NeVersionEntity neVersionEntity = uploadFileService.getNeVersionEntity(uploadFileModel.getLsmVersion(),updateUploadScript.get("programId").toString());
			uploadFileModel.setNeVersion(neVersionEntity);
			
			UploadFileEntity uploadFileEntity = uploadFileDto.getFileRuleBuilderEntity(uploadFileModel);
			uploadFileEntity.setCreationDate(new Date());
			if (uploadFileEntity != null) {
				// updating user details
				if (uploadFileService.updateUploadScript(uploadFileEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_SCRIPT_STORE, Constants.ACTION_UPDATE, "Script File Updated Successfully", sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
				}
			}
		} catch (Exception e) {

			logger.info("Exception in Upload Script : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to update Upload Script");
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
	@PostMapping(value = "/saveViewScript")
	public JSONObject saveViewScript(@RequestBody JSONObject saveViewScript) {
		String sessionId = null;
		String serviceToken = null;
		String fileName = null;
		String filePath = null;
		String scriptFileContent = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = saveViewScript.get("sessionId").toString();
			serviceToken = saveViewScript.get("serviceToken").toString();
			fileName = saveViewScript.get("fileName").toString();
			filePath = saveViewScript.get("filePath").toString();
			scriptFileContent = saveViewScript.get("scriptFileContent").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") +filePath;
			// updating content of the script
			if (uploadFileService.saveViewScript(filePath, fileName, scriptFileContent)) {
				mapObject.put("status", Constants.SUCCESS);
			} else {
				mapObject.put("status", Constants.FAIL);
			}
		} catch (Exception e) {

			logger.info("Exception in saveViewScript() : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to save Upload Script content");
		}
		return mapObject;
	}

	/**
	 * This method will return the Script content from Script file
	 * 
	 * @param viewScript
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/viewScript")
	public JSONObject viewScript(@RequestBody JSONObject viewScript) {
		String sessionId = null;
		String serviceToken = null;
		String fileName = null;
		String filePath = null;
		String scriptContent = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = viewScript.get("sessionId").toString();
			serviceToken = viewScript.get("serviceToken").toString();
			fileName = viewScript.get("fileName").toString();
			filePath = viewScript.get("filePath").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") +filePath;
			scriptContent = uploadFileService.readContentFromFile(filePath, fileName);
			mapObject.put("scriptFileContent", scriptContent);
			mapObject.put("status", Constants.SUCCESS);

		} catch (Exception e) {

			logger.info("Exception in viewScript() : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to show the content of the script");
		}
		return mapObject;
	}

	/**
	 * This method will delete UploadScript details from DB
	 * 
	 * @param deleteUploadData
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/deleteUploadScript")
	public JSONObject deleteUploadData(@RequestBody JSONObject deleteUploadData) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		int id = 0;
		try {
			sessionId = deleteUploadData.get("sessionId").toString();
			serviceToken = deleteUploadData.get("serviceToken").toString();
			id = (int) deleteUploadData.get("id");
			if (id != 0) {
				String filePathFromDB = uploadFileService.getFilePath(id);
				String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePathFromDB;
				if (uploadFileService.deleteUploadScript(id)) {
					uploadFileService.deleteDirectory(filePath);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("status", Constants.SUCCESS);
				} else {
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_DELETE_UPLOADSCRIPT));
					resultMap.put("status", Constants.FAIL);
				}
			}
			
			commonUtil.saveAudit(Constants.EVENT_RULES, Constants.EVENT_RULES_SCRIPT_STORE, Constants.ACTION_DELETE, "Script File Deleted Successfully", sessionId);
		} catch (Exception e) {
			logger.info(
					"Exception in delete upload script in UploadFileController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}
	
	/**
	 * This method will search upload script 
	 * 
	 * @param searchUploadScriptDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/searchUploadScript")
	public JSONObject searchUploadScript(@RequestBody JSONObject searchUploadScriptDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String fileName = null;
		String uploadedBy = null;
		String startDate = null;
		String endDate = null;
		String searchStatus = null;
		int customerId = 0;
		String migrationType = null;
		String programName = null;
		String subType = null;
		JSONObject mapObject = new JSONObject();
		String state = null;
		int programId;
		try {
			sessionId = searchUploadScriptDetails.get("sessionId").toString();
			serviceToken = searchUploadScriptDetails.get("serviceToken").toString();
			searchStatus = (String) searchUploadScriptDetails.get("searchStatus");
			customerId = Integer.valueOf(searchUploadScriptDetails.get("customerId").toString());
			migrationType = searchUploadScriptDetails.get("migrationType").toString();
			programName = searchUploadScriptDetails.get("programName").toString();
			subType = searchUploadScriptDetails.get("subType").toString();
			programId = Integer.parseInt(searchUploadScriptDetails.get("programId").toString());
			
			Map<String, Object> searchCriteria = (Map<String, Object>) searchUploadScriptDetails.get("searchCriteria");
			
			if(searchCriteria.get("fileName")!=null && !searchCriteria.get("fileName").toString().trim().isEmpty()) {
				fileName = searchCriteria.get("fileName").toString();
			}
			
			if(searchCriteria.get("UploadedBy")!=null && !searchCriteria.get("UploadedBy").toString().trim().isEmpty()) {
				uploadedBy = searchCriteria.get("UploadedBy").toString();
			}
			
			if(searchCriteria.get("fromDate")!=null &&  !searchCriteria.get("fromDate").toString().trim().isEmpty() ) {
				startDate = searchCriteria.get("fromDate").toString();
			}
			
			if(searchCriteria.get("toDate")!=null &&  !searchCriteria.get("toDate").toString().trim().isEmpty() ) {
				endDate = searchCriteria.get("toDate").toString();
			}
			
			if(searchCriteria.get("searchState")!=null && !searchCriteria.get("searchState").toString().trim().isEmpty()) {
				state = searchCriteria.get("searchState").toString();
			}
			
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			
			
			ProgramTemplateEntity programTemplateEntity = fileUploadRepository.getProgramTemplate(programId, Constants.SCRIPT_STORE_TEMPLATE);
			
			
			if(programTemplateEntity!=null) {
				if(programTemplateEntity.getValue()!=null && !programTemplateEntity.getValue().trim().isEmpty()) {
					JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
					JSONArray scripts = (JSONArray) objData.get("scripts");
					JSONObject connLocation = (JSONObject) objData.get("connLocation");
					JSONObject SM = (JSONObject) connLocation.get("SM");
					JSONObject NE = (JSONObject) connLocation.get("NE");
					JSONArray terminals = (JSONArray) SM.get("terminals");
					JSONArray terminalsObj = new JSONArray();
					String terminalName = "";
					for (int i = 0; i < terminals.size(); i++) {
						objData = (JSONObject) terminals.get(i);
						terminalName = (String) objData.get("terminalName");
						if (!terminalName.equalsIgnoreCase("bsm") && !terminalName.equalsIgnoreCase("csr")) {
							terminalsObj.add(terminals.get(i));
						}
					}
					
					JSONObject connectionScriptObj = new JSONObject();
					JSONObject connLocationObj =  new JSONObject();
					JSONObject SMObj =  new JSONObject();
					SMObj.put("terminals", terminalsObj);
					connLocationObj.put("SM", SMObj);
					connLocationObj.put("NE", NE);
					connectionScriptObj.put("connLocation", connLocationObj);
					connectionScriptObj.put("scripts", scripts);
					
					
					
					mapObject.put("ConnectionScriptType", connectionScriptObj);
				}else {
					mapObject.put("ConnectionScriptType", null);
				}
				
			}else {
				mapObject.put("ConnectionScriptType", null);
			}

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			Map<String, Object> paginationData = (Map<String, Object>) searchUploadScriptDetails.get("pagination");
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
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if ("search".equals(searchStatus)) {
				
				
				Map<String, Object> searchList = uploadFileService.searchUploadScript(fileName, uploadedBy,startDate,endDate,
						customerId, page, count,migrationType,programName,subType,user,state);
				mapObject.put("uploadScriptTableDetails", searchList.get("uploadScriptData"));
				mapObject.put("pageCount", searchList.get("totalCount"));
				mapObject.put("status", Constants.SUCCESS);
			} else if ("load".equals(searchStatus)) {
				Map<String, Object> searchList = uploadFileService.searchUploadScript(fileName, uploadedBy,startDate,endDate,
						customerId, page, count,migrationType,programName,subType,user,state);
				mapObject.put("uploadScriptTableDetails", searchList.get("uploadScriptData"));
				mapObject.put("pageCount", searchList.get("totalCount"));
				mapObject.put("status", Constants.SUCCESS);
			}
		} catch (Exception e) {
			logger.info("Exception in searchUploadScript : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", "Failed to load Upload script details");
		}
		return mapObject;
	}
}
