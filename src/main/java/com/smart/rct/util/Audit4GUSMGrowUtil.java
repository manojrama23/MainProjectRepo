package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class Audit4GUSMGrowUtil {

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
	AuditXmlRulesServiceUtil5GDSS auditXmlRulesServiceUtil5GDSS;
	
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
	private String duoSessionGrowNeId(String user, String outputFileName, JSONObject duoData, String command, String neId, String sProgramId) {
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
			String exeFileString = "\nExecuting " + "GROW_PREFIX_REMOVAL_CURL" + "\n";
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
	
	public String gettestOutput(String dataOutput, String neId, String dbcollectionFileName, NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			List<String> replaceData, AtomicBoolean isMultipleDUo) {
		String twampshellop = "";
		try {			
			//Implementing wait for TWAMP
			//Thread.sleep(Constants.TWAMP_TIMEOUT);
			
			JSONObject duoData = isDuoApplicable( networkConfigEntity, useCurrPassword, sanePassword);
			if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				JSONObject resultCurlCommand = getCurlCommandAudit4GTest(neId, sProgramId, migrationType, neEntity,
						originalUseCaseName, ciqFileName, replaceData);
				if(resultCurlCommand.containsKey("status") && resultCurlCommand.get("status").equals(Constants.SUCCESS)) {
					String tempCommand = resultCurlCommand.get("command").toString();
					System.out.println("--------------Command --------------");
					System.out.println(tempCommand);
					System.out.println("--------------Command --------------");
					if (isMultipleDUo.get()) {
						twampshellop = duoSessionGrowNeId(userName, outputFileNameAudit, duoData, tempCommand, neId,
								sProgramId);
					} else {
						twampshellop = duoSessionGrow(userName, outputFileNameAudit, duoData, tempCommand);
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
	
	public String gettestOutputNeName(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			String testScriptName, AtomicBoolean isMultipleDUo) {
		String twampshellop = "";
		try {

			JSONObject duoData = isDuoApplicable( networkConfigEntity, useCurrPassword, sanePassword);
			if (duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				JSONObject resultCurlCommand = getCurlCommandAuditNEName(neId, sProgramId, migrationType,
						scriptEntity, neEntity, originalUseCaseName, ciqFileName, testScriptName);
				if (resultCurlCommand.containsKey("status")
						&& resultCurlCommand.get("status").equals(Constants.SUCCESS)) {
					String tempCommand = resultCurlCommand.get("command").toString();
					System.out.println("--------------Command --------------");
					System.out.println(tempCommand);
					System.out.println("--------------Command --------------");
					if(isMultipleDUo.get()) {
						twampshellop = duoSessionNeID(userName, outputFileNameAudit, testScriptName + ".xml", duoData,
								tempCommand,neId,sProgramId);
					}else {
					twampshellop = duoSession(userName, outputFileNameAudit, testScriptName + ".xml", duoData,
							tempCommand);
					}
				} else if (resultCurlCommand.containsKey("reason")) {
					twampshellop = resultCurlCommand.get("reason").toString();
				}
			}
		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in gettwampoutput() " + ExceptionUtils.getFullStackTrace(e));
		}
		return twampshellop;
	}
	@SuppressWarnings("unchecked")
	public JSONObject isDuoApplicable(NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword) {
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

	@SuppressWarnings("unchecked")
	public JSONObject getCurlCommandAudit4GTest(String neId, String sProgramId,
			String migrationType, NetworkConfigEntity neEntity, String useCaseName, String ciqFileName,
			List<String> replaceData) {
		JSONObject result = new JSONObject();
		String command = "";
		String endName = "";
		String mcmip = "";
		try {

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

			String gnbId = "";
			endName = "eNB";
			gnbId = neId.replaceAll("^0+(?!$)", "");
			command = CommonUtil.getCurlCommandAudit4GTestjson(mcmip, gnbId, endName, replaceData);

			result.put("command", command);
			result.put("status", Constants.SUCCESS);

		} catch (Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
		}
		return result;
	}@SuppressWarnings("unchecked")
	public JSONObject getCurlCommandAuditNEName(String neId, String sProgramId,
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
			
			
				String gnbId = "";
				endName = "eNB";
				gnbId = neId.replaceAll("^0+(?!$)", "");
				command = CommonUtil.getCurlCommandAudit(scriptAbsFileName, mcmip, gnbId, endName);				
				
				result.put("command", command);
				result.put("status", Constants.SUCCESS);

			
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
		}
		return result;
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
	public String duoSessionGrow(String user, String outputFileName, JSONObject duoData, String command) throws Exception{
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
			String exeFileString = "\nExecuting " + "GROW_PREFIX_REMOVAL_CURL" + "\n";
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
	
	
	
	public String getTestOutput4GUSMGROW(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo) {
		StringBuilder shelloutput = new StringBuilder();
		try {
			String testScriptName = "";

			testScriptName = XmlCommandsConstants.AUDIT_4G_GROWPREFIX;
			String testshellop = gettestOutputNeName(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,isMultipleDUo);
			//testshellop = returnshellop(testScriptName + ".xml");
			if (testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {

				shelloutput.append("\n" + testshellop);

				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
				String inVocationId = auditXmlRulesServiceUtil5GDSS
						.getInvocationId(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity)
						.trim();

				if (inVocationId.isEmpty() || !inVocationId.contains("GROW")) {

					return shelloutput.toString();
				}
                 if(!inVocationId.isEmpty()) {
				String nEGrow = inVocationId.replace("GROW_", "");

				ArrayList<String> replaceData = new ArrayList<>();
				replaceData.add(nEGrow);

				testshellop = gettestOutput(dataOutput, neId, dbcollectionFileName, networkConfigEntity,
						useCurrPassword, sanePassword, neEntity, sProgramId, migrationType, originalUseCaseName,
						ciqFileName, userName, outputFileNameAudit, replaceData,isMultipleDUo);
				testScriptName = XmlCommandsConstants.AUDIT4G_GROW_PREFIX_REMOVAL;
				//testshellop = returnshellop(testScriptName + ".xml");
				shelloutput.append("\n" + testshellop);
				dataOutput1 = getRequiredOutPut5GAuditGrowPrifix(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
                 }
			} else {
				shelloutput.append("\n" + testshellop);

				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
			}

		} catch (Exception e) {
			logger.error("Exception Audit4GTestUtil in get4GTestOutput() " + ExceptionUtils.getFullStackTrace(e));
		}
		return shelloutput.toString();
	}
	
	private String getRequiredOutPut5GAuditGrowPrifix(String shelloutput, String fileName) {
		StringBuilder objBuilder = new StringBuilder();
		try {
			String fileNameWoExten = FilenameUtils.removeExtension(fileName);
			objBuilder.append("\n");
			objBuilder.append(fileNameWoExten);
			objBuilder.append("\n");

			if (StringUtils.isNotEmpty(shelloutput)) {
				objBuilder.append(shelloutput);
				objBuilder.append("\n");
			}

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getRequiredOutPut5GAudit() "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		objBuilder.append(XmlCommandsConstants.ENDTEXT5G);
		objBuilder.append("\n");
		return objBuilder.toString();
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