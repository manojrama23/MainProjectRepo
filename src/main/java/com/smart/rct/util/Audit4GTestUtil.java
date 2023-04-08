package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class Audit4GTestUtil {

	static final Logger logger = LoggerFactory.getLogger(Audit4GTestUtil.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	DuoGeneralConfigService duoGeneralConfigService;
	
	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	
	@Autowired
	AuditXmlRulesServiceUtil auditXmlRulesServiceUtil;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	private String duoSessionNeID(String user, String outputFileName, String scriptName, JSONObject duoData, String command, String neId, String sProgramId) {
		String result = "";
		StringBuffer output = new StringBuffer();
		boolean useCurrPassword = true;
		String serverIp = null;
		String password = null;
		String path = null;
		String userPrompt = null;
		String superUserPrompt = null;

		try {
			serverIp = duoData.get("serverIp").toString();
			if (duoData.containsKey("password")) {
				useCurrPassword = false;
				password = duoData.get("password").toString();
			}
			path = duoData.get("path").toString();
			userPrompt = duoData.get("userPrompt").toString();
			superUserPrompt = duoData.get("superUserPrompt").toString();
			UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
			String exeFileString = "\nExecuting " + scriptName + "\n";
			output.append(exeFileString);
			OutputStream os = new FileOutputStream(outputFileName, true);
			os.write(exeFileString.getBytes());
			String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId;
			if (!GlobalStatusMap.socketSessionInCreation.containsKey(userName_NeId)) {
				GlobalStatusMap.socketSessionInCreation.put(userName_NeId, false);
			}
			while (GlobalStatusMap.socketSessionInCreation.get(userName_NeId)) {
				Thread.sleep(5000);
			}
			if (GlobalStatusMap.socketSessionUser.containsKey(userName_NeId)) {
				SocketSession ses = GlobalStatusMap.socketSessionUser.get(userName_NeId);
				if (ses.isConnectedSession()) {

					while (ses.getIsSessionInUse()) {
						Thread.sleep(5000);
					}
					ses.setIsSessionInUse(true);
					result = ses.runCommand(command);

					ses.setIsSessionInUse(false);
				} else {
					ses.disconnectSession();
					result = connectDuoServerNeId(userDetailsEntity, serverIp, password, useCurrPassword, path,
							userPrompt, superUserPrompt, command, neId, sProgramId);
				}
			} else {
				result = connectDuoServerNeId(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt,
						superUserPrompt, command, neId, sProgramId);
			}
			String resultString = result.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
					.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
					.replaceAll("[*\\[]K", "").replaceAll("[*\\[]A", "").replaceAll("[*\\[]C1", "")
					.replaceAll("[*\\[]C", "");
			os.write(resultString.getBytes());
			output.append(resultString);
		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}
		return output.toString();

	}
	private String connectDuoServerNeId(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword,
			boolean useCurrPassword, String path, String userPrompt, String superUserPrompt, String command,
			String neId, String sProgramId) {
		String result = "";
		String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId;
		try {
			Thread.sleep(10000);
			if (GlobalStatusMap.socketSessionInCreation.containsKey(userName_NeId)) {
				GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, true);
			} else {
				GlobalStatusMap.socketSessionInCreation.put(userName_NeId, true);
			}
			// System.out.println("Enter Ip : ");
			// String ip = "10.20.120.82";
			String user = userDetailsEntity.getVpnUserName();
			String password = null;
			if (useCurrPassword) {
				password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
			} else {
				password = sanepassword;
			}
			// System.out.println("USER : " + user);
			// System.out.println("PASSWORD : " + password);
			// System.out.println("IP : " + serverIp);
			SocketSession sockets = new SocketSession(user, password, serverIp);
			JSONObject sessionResult = sockets.connectSession(path, userPrompt, superUserPrompt);
			if (sessionResult.get("status").toString().equals(Constants.SUCCESS)) {
				if (sockets.isConnectedSession()) {
					String sessionOutput = "";
					if (sessionResult.containsKey("expectOutput")) {
						sessionOutput = sessionResult.get("expectOutput").toString();
					}
					String resultOutput = sockets.runCommand(command);
					result = sessionOutput + "\n" + resultOutput;
					// System.out.println("----------------Output--------------");
					// System.out.println(result);
					// System.out.println("####################################");
					GlobalStatusMap.socketSessionUser.put(userName_NeId, sockets);
				}
				GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
			} else {
				if (sessionResult.get("reason").toString().equals(Constants.DUO_AUTHENTICATION_FAILURE)) {
					result = "DUO ERROR: " + "\n" + "Permission denied, please try again \n spawn id exp";
				} else if (sessionResult.get("reason").toString().equals(Constants.DUO_UNKNOWN_HOST)) {
					result = "DUO ERROR: " + "\n" + "Unknown Host Ip, Please Check Sane Server IP";
				} else {
					result = "DUO ERROR: " + "\n" + sessionResult.get("reason").toString();
				}
				if (sessionResult.containsKey("expectOutput")) {
					result = sessionResult.get("expectOutput").toString() + "\n" + result;
				}
				GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
			}
		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
		}
		return result;
	}
	
	public String gettestOutput(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			String testScriptName, List<String> replaceData, AtomicBoolean isMultipleDUo) {
		String twampshellop = "";
		try {			
			//Implementing wait for TWAMP
			//Thread.sleep(Constants.TWAMP_TIMEOUT);
			
			JSONObject duoData = isDuoApplicable(scriptEntity, networkConfigEntity, useCurrPassword, sanePassword);
			if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				JSONObject resultCurlCommand = getCurlCommandAudit4GTest(neId, sProgramId, migrationType, scriptEntity, neEntity,
						originalUseCaseName, ciqFileName, testScriptName, replaceData);
				if(resultCurlCommand.containsKey("status") && resultCurlCommand.get("status").equals(Constants.SUCCESS)) {
					String tempCommand = resultCurlCommand.get("command").toString();
					System.out.println("--------------Command --------------");
					System.out.println(tempCommand);
					System.out.println("--------------Command --------------");
					if(isMultipleDUo.get()) {
						twampshellop = duoSessionNeID(userName, outputFileNameAudit, testScriptName + ".xml", duoData,
								tempCommand,neId,sProgramId);
					}else {
						twampshellop = duoSession(userName, outputFileNameAudit, testScriptName + ".xml", duoData, tempCommand);
					}
				} else if(resultCurlCommand.containsKey("reason")) {
					twampshellop = resultCurlCommand.get("reason").toString();
				}
			}
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in gettwampoutput() " + ExceptionUtils.getFullStackTrace(e));
		}
		return twampshellop;
	}
	
	public String gettestOutputCarrier(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			String testScriptName, AtomicBoolean isMultipleDUo) {
		String twampshellop = "";
		try {			
			//Implementing wait for TWAMP
			//Thread.sleep(Constants.TWAMP_TIMEOUT);
			
			JSONObject duoData = isDuoApplicable(scriptEntity, networkConfigEntity, useCurrPassword, sanePassword);
			if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				JSONObject resultCurlCommand = getCurlCommandAudit4GTestCarrier(neId, sProgramId, migrationType, scriptEntity, neEntity,
						originalUseCaseName, ciqFileName, testScriptName);
				if(resultCurlCommand.containsKey("status") && resultCurlCommand.get("status").equals(Constants.SUCCESS)) {
					String tempCommand = resultCurlCommand.get("command").toString();
					System.out.println("--------------Command --------------");
					System.out.println(tempCommand);
					System.out.println("--------------Command --------------");
					if(isMultipleDUo.get()) {
						twampshellop = duoSessionNeID(userName, outputFileNameAudit, testScriptName + ".xml", duoData,
								tempCommand,neId,sProgramId);
					}else {
						twampshellop = duoSession(userName, outputFileNameAudit, testScriptName + ".xml", duoData, tempCommand);
					}
					
				} else if(resultCurlCommand.containsKey("reason")) {
					twampshellop = resultCurlCommand.get("reason").toString();
				}
			}
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in gettwampoutput() " + ExceptionUtils.getFullStackTrace(e));
		}
		return twampshellop;
	}
	@SuppressWarnings("unchecked")
	public JSONObject isDuoApplicable(UploadFileEntity scriptEntity, NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword) {
		JSONObject result = new JSONObject();
		result.put("status", Constants.FAIL);
		try {
			List<ProgramTemplateModel> duoGeneralConfigList = duoGeneralConfigService.getDuoGeneralConfigList();
			if(duoGeneralConfigList!=null && !duoGeneralConfigList.isEmpty() && duoGeneralConfigList.get(0).getValue().equalsIgnoreCase("ON")) {
				List<NetworkConfigDetailsEntity> neDetailsList = networkConfigEntity.getNeDetails();
				if(neDetailsList!=null && neDetailsList.size()==1) {
					NetworkConfigDetailsEntity neDetails = neDetailsList.get(0);
					if("SANE".equalsIgnoreCase(neDetails.getServerTypeEntity().getServerType())) {
						if(neDetails.getPath() == null || neDetails.getPath().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason", "DUO ERROR: " + "\n" + "Path is not specified for Network Config Entity");
							return result;
						}
						if(neDetails.getSuperUserPrompt() == null || neDetails.getSuperUserPrompt().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason", "DUO ERROR: " + "\n" + "SuperUserPrompt is not specified for Network Config Entity");
							return result;
						}
						if(neDetails.getUserPrompt() == null || neDetails.getUserPrompt().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason", "DUO ERROR: " + "\n" + "UserPrompt is not specified for Network Config Entity");
							return result;
						}
						result.put("serverIp", neDetails.getServerIp());
						result.put("path", neDetails.getPath());
						result.put("superUserPrompt", neDetails.getSuperUserPrompt());
						result.put("userPrompt", neDetails.getUserPrompt());
						if(useCurrPassword == false) {
							result.put("password", sanePassword);
						}
						result.put("status", Constants.SUCCESS);
					}
				}
			}			
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in isDuoApplicable() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
		}
		return result;
		
	}

	public JSONObject getCurlCommandAudit4GTest(String neId, String sProgramId,
			String migrationType, UploadFileEntity scriptEntity, 
			NetworkConfigEntity neEntity, String useCaseName, String ciqFileName, String testScriptName, List<String> replaceData) {
		JSONObject result = new JSONObject();
		String command = "";
		String endName = "";
		String mcmip = "";
		try {
			int programId = Integer.parseInt(sProgramId);
			String scriptAbsFileName = "";
			
			if (useCaseName.contains(Constants.COMMISION_USECASE) || useCaseName.contains(Constants.RF_USECASE)) {
				scriptAbsFileName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.CUSTOMER + "/"
						+ Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", String.valueOf(programId))
								.replace("migrationType", migrationType).replace("subType", scriptEntity.getSubType())
						+ useCaseName.trim().replaceAll(" ", "_") + "/" + testScriptName + ".xml";
			} else {
				scriptAbsFileName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
						+ scriptEntity.getFilePath() + "/" + useCaseName.trim().replaceAll(" ", "_") + "/"
						+ testScriptName + ".xml";
			}
			if (CommonUtil.isValidObject(neEntity) && CommonUtil.isValidObject(neEntity.getNeTypeEntity())
					&& neEntity.getNeTypeEntity().getId() == Constants.NW_CONFIG_VLSM_ID
					&& CommonUtil.isValidObject(neEntity.getNeDetails())) {
				for (NetworkConfigDetailsEntity detailsEntity : neEntity.getNeDetails()) {
					if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
						if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_VLSM_MCMA_ID
								&& CommonUtil.isValidObject(detailsEntity.getServerIp())
								&& detailsEntity.getServerIp().length() > 0) {
							mcmip = detailsEntity.getServerIp();
							break;
						}
					}
				}
			}
			if (CommonUtil.isValidObject(neEntity) && CommonUtil.isValidObject(neEntity.getNeTypeEntity())
					&& neEntity.getNeTypeEntity().getId() == Constants.NW_CONFIG_USM_ID) {
				mcmip = neEntity.getNeIp();
			}
			if (StringUtils.isNotEmpty(scriptEntity.getFileName())) {
				endName = scriptEntity.getFileName().substring(0,
						scriptEntity.getFileName().indexOf('_'));
			}
			if ("4GAudit".equalsIgnoreCase(endName)) {
				//String dbcollectionFileName = CommonUtil.createMongoDbFileName(sProgramId, ciqFileName);
				//List<CIQDetailsModel> listCIQDetailsModel = getCiqDetailsForRuleValidationsheet(neId, dbcollectionFileName, 
				//		"CIQUpstateNY", "eNBId");
				String gnbId = "";
				endName = "eNB";
				gnbId = neId.replaceAll("^0+(?!$)", "");
				command = CommonUtil.getCurlCommandAudit4GTest(scriptAbsFileName, mcmip, gnbId, endName, testScriptName, replaceData);				
				
				result.put("command", command);
				result.put("status", Constants.SUCCESS);

			}
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
		}
		return result;
	}public JSONObject getCurlCommandAudit4GTestCarrier(String neId, String sProgramId,
			String migrationType, UploadFileEntity scriptEntity, 
			NetworkConfigEntity neEntity, String useCaseName, String ciqFileName, String testScriptName) {
		JSONObject result = new JSONObject();
		String command = "";
		String endName = "";
		String mcmip = "";
		try {
			int programId = Integer.parseInt(sProgramId);
			String scriptAbsFileName = "";
			
			if (useCaseName.contains(Constants.COMMISION_USECASE) || useCaseName.contains(Constants.RF_USECASE)) {
				scriptAbsFileName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.CUSTOMER + "/"
						+ Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", String.valueOf(programId))
								.replace("migrationType", migrationType).replace("subType", scriptEntity.getSubType())
						+ useCaseName.trim().replaceAll(" ", "_") + "/" + testScriptName + ".xml";
			} else {
				scriptAbsFileName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
						+ scriptEntity.getFilePath() + "/" + useCaseName.trim().replaceAll(" ", "_") + "/"
						+ testScriptName + ".xml";
			}
			if (CommonUtil.isValidObject(neEntity) && CommonUtil.isValidObject(neEntity.getNeTypeEntity())
					&& neEntity.getNeTypeEntity().getId() == Constants.NW_CONFIG_VLSM_ID
					&& CommonUtil.isValidObject(neEntity.getNeDetails())) {
				for (NetworkConfigDetailsEntity detailsEntity : neEntity.getNeDetails()) {
					if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
						if (detailsEntity.getServerTypeEntity().getId() == Constants.NW_CONFIG_VLSM_MCMA_ID
								&& CommonUtil.isValidObject(detailsEntity.getServerIp())
								&& detailsEntity.getServerIp().length() > 0) {
							mcmip = detailsEntity.getServerIp();
							break;
						}
					}
				}
			}
			if (CommonUtil.isValidObject(neEntity) && CommonUtil.isValidObject(neEntity.getNeTypeEntity())
					&& neEntity.getNeTypeEntity().getId() == Constants.NW_CONFIG_USM_ID) {
				mcmip = neEntity.getNeIp();
			}
			if (StringUtils.isNotEmpty(scriptEntity.getFileName())) {
				endName = scriptEntity.getFileName().substring(0,
						scriptEntity.getFileName().indexOf('_'));
			}
			if ("4GAudit".equalsIgnoreCase(endName)) {
				//String dbcollectionFileName = CommonUtil.createMongoDbFileName(sProgramId, ciqFileName);
				//List<CIQDetailsModel> listCIQDetailsModel = getCiqDetailsForRuleValidationsheet(neId, dbcollectionFileName, 
				//		"CIQUpstateNY", "eNBId");
				String gnbId = "";
				endName = "eNB";
				gnbId = neId.replaceAll("^0+(?!$)", "");
				command = CommonUtil.getCurlCommandAudit(scriptAbsFileName, mcmip, gnbId, endName);				
				
				result.put("command", command);
				result.put("status", Constants.SUCCESS);

			}
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
		}
		return result;
	}
	
	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where(idname).is(enbId).and("sheetAliasName").is(sheetname));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}
	
	public String duoSession(String user, String outputFileName, String scriptName, JSONObject duoData, String command) throws Exception{
		String result = "";
		StringBuilder output = new StringBuilder();
		boolean useCurrPassword = true;
		String serverIp = null;
		String password = null;
		String path = null;
		String userPrompt = null;
		String superUserPrompt = null;
		
		try {
			serverIp = duoData.get("serverIp").toString();
			if(duoData.containsKey("password")) {
				useCurrPassword = false;
				password = duoData.get("password").toString();
			}
			path = duoData.get("path").toString();
			userPrompt = duoData.get("userPrompt").toString();
			superUserPrompt = duoData.get("superUserPrompt").toString();
			UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
			String exeFileString = "\nExecuting " + scriptName + "\n";
			output.append(exeFileString);
			OutputStream os = new FileOutputStream(outputFileName, true);
			os.write(exeFileString.getBytes());
			//Thread.sleep(1000);
			
			//System.out.println("------------------Session IN Creation : " + GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName()));
			if(!GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())) {
				GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), false);
			}
			while(GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName())) {
				Thread.sleep(5000);
			}
			if(GlobalStatusMap.socketSessionUser.containsKey(userDetailsEntity.getVpnUserName())) {
				SocketSession ses = GlobalStatusMap.socketSessionUser.get(userDetailsEntity.getVpnUserName());
				if(ses.isConnectedSession()) {
					//System.out.println("------------------Session IN USE : " +  ses.getIsSessionInUse());
					while(ses.getIsSessionInUse()) {
						Thread.sleep(5000);
					}
					ses.setIsSessionInUse(true);
					result = ses.runCommand(command);
					//System.out.println("----------------Output--------------");	
					//System.out.println(result);
					//System.out.println("####################################");
					ses.setIsSessionInUse(false);
				} else {
					ses.disconnectSession();
					result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, command);
				}
			} else {
				result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, command);
			}
			String resultString = result.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
					.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
					.replaceAll("[*\\[]K", "");
			os.write(resultString.getBytes());
			output.append(resultString);
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}		
		return output.toString();
		
	}
	
	public String connectDuoServer(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword, boolean useCurrPassword, String path,
			String userPrompt, String superUserPrompt, String command) {
		String result = "";
		try {
			
			if(GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())){
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), true);
			} else {
				GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), true);
			}
			//System.out.println("Enter Ip : ");
			//String ip = "10.20.120.82";
			String user = userDetailsEntity.getVpnUserName();
			String password = null;
			if(useCurrPassword) {
				password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
			} else {
				password = sanepassword;
			}
			//System.out.println("USER : " + user);
			//System.out.println("PASSWORD : " + password);
			//System.out.println("IP : " + serverIp);
			SocketSession sockets = new SocketSession(user, password, serverIp);
			JSONObject sessionResult = sockets.connectSession(path, userPrompt, superUserPrompt);
			if(sessionResult.get("status").toString().equals(Constants.SUCCESS)) {
				if(sockets.isConnectedSession()) {
					String sessionOutput = "";
					if(sessionResult.containsKey("expectOutput")) {
						sessionOutput = sessionResult.get("expectOutput").toString();
					}
					String resultOutput = sockets.runCommand(command);
					result = sessionOutput + "\n" + resultOutput;
					//System.out.println("----------------Output--------------");	
					//System.out.println(result);
					//System.out.println("####################################");
					GlobalStatusMap.socketSessionUser.put(userDetailsEntity.getVpnUserName(), sockets);
				}				
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
			} else {
				if(sessionResult.get("reason").toString().equals(Constants.DUO_AUTHENTICATION_FAILURE)) {
					result = "DUO ERROR: " + "\n" + "Permission denied, please try again \n spawn id exp";
				} else if(sessionResult.get("reason").toString().equals(Constants.DUO_UNKNOWN_HOST)) {
					result = "DUO ERROR: " + "\n" + "Unknown Host Ip, Please Check Sane Server IP";
				} else {
					result = "DUO ERROR: " + "\n" + sessionResult.get("reason").toString();
				}
				if(sessionResult.containsKey("expectOutput")) {
					result = sessionResult.get("expectOutput").toString() + "\n" + result;
				}
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
			}
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
		}
		return result;
	}
	
	public String get4GTestOutput(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String,Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo) {
		StringBuilder shelloutput = new StringBuilder();
		try {
			String testScriptName = "";
			long waitTime = 60000;
			List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE, AuditConstants.OCNS_TEST_WAIT);
			
			if(!ObjectUtils.isEmpty(auditConstantsList) && NumberUtils.isNumber(auditConstantsList.get(0).getParameterValue())) {
				waitTime = NumberUtils.toLong(auditConstantsList.get(0).getParameterValue()) * 1000;
			}
			testScriptName = XmlCommandsConstants.AUDIT_4G_RSSIIMBALANCE;
			String testshellop1 = gettestOutputCarrier(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,isMultipleDUo);
			//testshellop1 = returnshellop(testScriptName + ".xml");
			shelloutput.append("\n" + testshellop1);
			
			String dataOutput2 = getRequiredOutPut5GAudit(testshellop1,
					testScriptName + ".xml");
			required5GOutput.append(dataOutput2);
			required5GOutput.append("/n");
			List<CIQDetailsModel> listCiqDetails = getCiqDetailsForRuleValidationsheet(neId, dbcollectionFileName, "CIQUpstateNY", "eNBId");
			
			if(!ObjectUtils.isEmpty(listCiqDetails)) {
				for(CIQDetailsModel ciqDetails : listCiqDetails) {
					String cellnum = "";
					String lcccard = "";
					String crpiportno = "";
					if(ciqDetails.getCiqMap().containsKey("Cell_ID") && ciqDetails.getCiqMap().containsKey("lCCCard")
							&& ciqDetails.getCiqMap().containsKey("CRPIPortID")) {
						cellnum = ciqDetails.getCiqMap().get("Cell_ID").getHeaderValue().trim();
						lcccard = ciqDetails.getCiqMap().get("lCCCard").getHeaderValue().trim();
						crpiportno = ciqDetails.getCiqMap().get("CRPIPortID").getHeaderValue().trim();
					}
					if(NumberUtils.isNumber(cellnum) && NumberUtils.isNumber(lcccard) && NumberUtils.isNumber(crpiportno)) {
						
						testScriptName = XmlCommandsConstants.AUDIT_4G_OCNS_TEST_CELL_NUM;
						List<String> replaceData = new ArrayList<>();
						replaceData.add(cellnum);
						String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
								networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
								originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
					//	testshellop = returnshellop(testScriptName + cellnum+ ".xml");
						if(testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
							
							shelloutput.append("\n" + testshellop);
							
							String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + ".xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							
							String inVocationId = auditXmlRulesServiceUtil.getInvocationId(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity).trim();
							
							if(!NumberUtils.isNumber(inVocationId)) {
								createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
								return shelloutput.toString();
							}
							
							Thread.sleep(waitTime);
							
							testScriptName = XmlCommandsConstants.AUDIT_4G_CHECK_RSSI;
							replaceData = new ArrayList<>();
							replaceData.add(lcccard);
							replaceData.add(crpiportno);
							
							testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
									networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
									originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
							//testshellop = returnshellop(testScriptName +  ".xml");
							shelloutput.append("\n" + testshellop);
							dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + cellnum + "cellnum.xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							
							testScriptName = XmlCommandsConstants.AUDIT_4G_CHECK_TXPOWER;
							replaceData = new ArrayList<>();
							replaceData.add(lcccard);
							replaceData.add(crpiportno);
							
							testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
									networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
									originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
							//testshellop = returnshellop(testScriptName +  ".xml");
							shelloutput.append("\n" + testshellop);
							dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + cellnum + "cellnum.xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							
							testScriptName = XmlCommandsConstants.AUDIT_4G_OCNS_TEST_TERMINATE;
							replaceData = new ArrayList<>();
							replaceData.add(inVocationId);
							
							testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
									networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
									originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
							//testshellop = returnshellop(testScriptName +  ".xml");
							shelloutput.append("\n" + testshellop);
							dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + cellnum + "cellnum.xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							
							if(testshellop.contains("<rpc-reply") /*&& !(testshellop.contains("<rpc-error"))*/ && testshellop.contains("<ok/>")) {
								createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
							} else {
								createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
							}
							
							
						} else {
							shelloutput.append("\n" + testshellop);
							
							String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + ".xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							//createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
						}
					}
				}
			}
			
		} catch(Exception e) {
			logger.error("Exception Audit4GTestUtil in get4GTestOutput() " + ExceptionUtils.getFullStackTrace(e));
		}		
		return shelloutput.toString();
	}
	
	private String getRequiredOutPut5GAudit(String shelloutput, String fileName) {

		StringBuilder objBuilder = new StringBuilder();

		try {
			String fileNameWoExten = FilenameUtils.removeExtension(fileName);
			objBuilder.append("\n");
			objBuilder.append(fileNameWoExten);
			objBuilder.append("\n");

			String endName = "";
			if (StringUtils.isNotEmpty(shelloutput) && shelloutput.contains("<rpc-reply")
					&& shelloutput.contains("</rpc-reply>")) {
				shelloutput = shelloutput.substring(shelloutput.indexOf("<rpc-reply"),
						shelloutput.indexOf("</rpc-reply>"));
				objBuilder.append(shelloutput);
				objBuilder.append("\n");
				objBuilder.append("</rpc-reply>");
				objBuilder.append("\n");
			}

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getRequiredOutPut5GAudit() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		// TODO Auto-generated method stub

		objBuilder.append(XmlCommandsConstants.ENDTEXT5G);
		objBuilder.append("\n");
		return objBuilder.toString();
	}
	
	
	/*private String returnshellop(String name)
	{
		String fullOutputLog = "";
		try {
			fullOutputLog = new String(Files.readAllBytes(Paths.get("/home/user/Desktop/Responses/"+name)));
		} catch (IOException e) {
			System.out.println(e);
		}
		return fullOutputLog;
	}*/
}