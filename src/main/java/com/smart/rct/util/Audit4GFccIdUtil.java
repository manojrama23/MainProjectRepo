package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
public class Audit4GFccIdUtil {

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
			logger.error("Exception Audit4GFccIdUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}
		return output.toString();

	}
	private String connectDuoServerNeId(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword,
			boolean useCurrPassword, String path, String userPrompt, String superUserPrompt, String command,
			String neId, String sProgramId) {
		String result = "";
		String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId;
		try {
			if (GlobalStatusMap.socketSessionInCreation.containsKey(userName_NeId)) {
				GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, true);
			} else {
				GlobalStatusMap.socketSessionInCreation.put(userName_NeId, true);
			}
			String user = userDetailsEntity.getVpnUserName();
			String password = null;
			if (useCurrPassword) {
				password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
			} else {
				password = sanepassword;
			}
			
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
			logger.error("Exception Audit4GFccIdUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
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
			
			JSONObject duoData = isDuoApplicable(scriptEntity, networkConfigEntity, useCurrPassword, sanePassword);
			if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				JSONObject resultCurlCommand = getCurlCommandAudit4GTest(neId, sProgramId, migrationType, scriptEntity, neEntity,
						originalUseCaseName, ciqFileName, testScriptName, replaceData);
				if(resultCurlCommand.containsKey("status") && resultCurlCommand.get("status").equals(Constants.SUCCESS)) {
					String tempCommand = resultCurlCommand.get("command").toString();
					
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
			logger.error("Exception Audit4GFccIdUtil in gettestOutput() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception Audit4GFccIdUtil in isDuoApplicable() " + ExceptionUtils.getFullStackTrace(e));
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
				String gnbId = "";
				endName = "eNB";
				gnbId = neId.replaceAll("^0+(?!$)", "");
				command = CommonUtil.getCurlCommandAudit4GTest(scriptAbsFileName, mcmip, gnbId, endName, testScriptName, replaceData);				
				
				result.put("command", command);
				result.put("status", Constants.SUCCESS);

			}
		} catch(Exception e) {
			logger.error("Exception Audit4GFccIdUtil in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
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
			if(!GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())) {
				GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), false);
			}
			while(GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName())) {
				Thread.sleep(5000);
			}
			if(GlobalStatusMap.socketSessionUser.containsKey(userDetailsEntity.getVpnUserName())) {
				SocketSession ses = GlobalStatusMap.socketSessionUser.get(userDetailsEntity.getVpnUserName());
				if(ses.isConnectedSession()) {
					
					while(ses.getIsSessionInUse()) {
						Thread.sleep(5000);
					}
					ses.setIsSessionInUse(true);
					result = ses.runCommand(command);
					
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
			logger.error("Exception Audit4GFccIdUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
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
			String user = userDetailsEntity.getVpnUserName();
			String password = null;
			if(useCurrPassword) {
				password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
			} else {
				password = sanepassword;
			}			
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
			logger.error("Exception Audit4GFccIdUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
		}
		return result;
	}
	
	public String get4GFCCTestOutput(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo) {
		StringBuilder shelloutput = new StringBuilder();
		try {
			String testScriptName = "";
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			testScriptName = XmlCommandsConstants.AUDIT_4G_FCC_RETRIEVE_RADIO_UNIT_INVENTORY;
			List<String> replaceData = new ArrayList<>();

			String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,
					isMultipleDUo);
			//testshellop = returnshellop(testScriptName + ".xml");
			if (testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
				createHtmlMapboo.put(XmlCommandsConstants.AUDIT_4G_FCC_RETRIEVE_RADIO_UNIT_INVENTORY, true);
				shelloutput.append("\n" + testshellop);

				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");

				getCBRSTablewithdate(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity,
						tabelData1);

				if (tabelData1.isEmpty()) {
					createHtmlMapboo.put(XmlCommandsConstants.AUDIT_4G_FCC_RETRIEVE_RADIO_UNIT_INVENTORY, true);
					return shelloutput.toString();
				}
				testScriptName = XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO;
				testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName, networkConfigEntity,
						useCurrPassword, sanePassword, neEntity, sProgramId, migrationType, originalUseCaseName,
						ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData, isMultipleDUo);
				//testshellop = returnshellop(testScriptName + "1.xml");
				shelloutput.append("\n" + testshellop);
				dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");

				for (LinkedHashMap<String, String> tdData : tabelData1) {
					if(tdData.containsKey("manufactured-date") &&  !tdData.get("manufactured-date").equals("-")) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date sCreationDate = dateFormat.parse(tdData.get("manufactured-date"));
					Date sCreationDate2 = dateFormat.parse("2022-05-03");
					boolean hardwareDate = sCreationDate.after(sCreationDate2);
					replaceData = new ArrayList<>();
					String ruPort = tdData.get("unit-id");
					ruPort = StringUtils.substringBetween(ruPort, "[", "]");
					String cbsdId = getCbbsdIndex(required5GOutput.toString(),
							XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO, neId, dbcollectionFileName,
							networkConfigEntity, ruPort);
					if (!ObjectUtils.isEmpty(cbsdId) && hardwareDate) {
						testScriptName = XmlCommandsConstants.AUDIT_4G_CONFIGURE_FCC_ID;
						replaceData = new ArrayList<>();
						replaceData.add(cbsdId);
						replaceData.add("A3LRT4401-48A1");

						testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
								networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
								originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,
								replaceData, isMultipleDUo);
						//testshellop = returnshellop(testScriptName + ".xml");
						shelloutput.append("\n" + testshellop);
						dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
						required5GOutput.append(dataOutput1);
						required5GOutput.append("/n");
					}else if (!ObjectUtils.isEmpty(cbsdId) && !hardwareDate) {
						testScriptName = XmlCommandsConstants.AUDIT_4G_CONFIGURE_FCC_ID;
						replaceData = new ArrayList<>();
						replaceData.add(cbsdId);
						replaceData.add("A3LRT4401-48A");

						testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
								networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
								originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,
								replaceData, isMultipleDUo);
						//testshellop = returnshellop(testScriptName + ".xml");
						shelloutput.append("\n" + testshellop);
						dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
						required5GOutput.append(dataOutput1);
						required5GOutput.append("/n");	
					}
				}
			}
				testScriptName = XmlCommandsConstants.AUDIT_4G_FCC_CBSD_INFO;

				testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName, networkConfigEntity,
						useCurrPassword, sanePassword, neEntity, sProgramId, migrationType, originalUseCaseName,
						ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData, isMultipleDUo);
				//testshellop = returnshellop(testScriptName + "2.xml");
				shelloutput.append("\n" + testshellop);
				dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + "50" + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");

			}else {
				shelloutput.append("\n" + testshellop);
				
				String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
						testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
			}
		} catch (Exception e) {
			logger.error("Exception Audit4GFccIdUtil in get4GFCCTestOutput() " + ExceptionUtils.getFullStackTrace(e));
		}
		return shelloutput.toString();
	}
	
	private String getCbbsdIndex(String fullOutputLog, String command, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, String ruPort) {
		String cbsdId = "";
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			headerList1.add("connected-ru");
			headerList1.add("cbsd-index");
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("managed-element");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;

					NodeList childNodeList1 = element.getElementsByTagName("cbrs-function");
					for (int k = 0; k < childNodeList1.getLength(); k++) {

						Node ChildNode1 = childNodeList1.item(k);
						if (Node.ELEMENT_NODE == ChildNode1.getNodeType()) {

							Element elementchild1 = (Element) ChildNode1;

							NodeList childNodeList2 = elementchild1.getElementsByTagName("cbsd-info");
							for (int l = 0; l < childNodeList2.getLength(); l++) {

								Node ChildNode2 = childNodeList2.item(l);
								if (Node.ELEMENT_NODE == ChildNode2.getNodeType()) {

									Element elementchild2 = (Element) ChildNode2;

									LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();

									Iterator<String> itr = headerList1.iterator();
									String header = itr.next();
									objtableData.put(header, getXmlElementData(elementchild2, header));

									while (itr.hasNext()) {
										header = itr.next();
										objtableData.put(header, getXmlElementData(elementchild2, header));
									}

									tabelData1.add(objtableData);
								}
							}
						}
					}

				}
			}
			ruPort=ruPort.replaceAll("_", "-");
			for (LinkedHashMap<String, String> tdData : tabelData1) {
				if (tdData.get("connected-ru").contains(ruPort)) {
					cbsdId = tdData.get("cbsd-index");
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return cbsdId;
	}
	private  void getCBRSTablewithdate(String fullOutputLog, String command, String neId,
			String dbcollectionFileName, NetworkConfigEntity networkConfigEntity,
			List<LinkedHashMap<String, String>> deleteData) {
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();

			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {

					Element element = (Element) inChildNode;
					NodeList nodeList1 = element.getChildNodes();

					for (int j = 0; j < nodeList1.getLength(); j++) {
						Node nodeList2 = nodeList1.item(j);
						if (Node.ELEMENT_NODE == nodeList2.getNodeType()) {

							Element element1 = (Element) nodeList2;
							NodeList nodeList3 = element1.getChildNodes();
							LinkedHashMap<String, String> objtableData = new LinkedHashMap<>();
							for (int k = 0; k < nodeList3.getLength(); k++) {

								Node n1 = nodeList3.item(k);
								if (Node.ELEMENT_NODE == n1.getNodeType()) {
									headerList1.add(n1.getNodeName().trim());
									objtableData.put(n1.getNodeName().trim(), n1.getTextContent());
								}
							}
							tabelData1.add(objtableData);
						}
					}

				}
			}

			for (LinkedHashMap<String, String> tdData : tabelData1) {

				for (Map.Entry<String, String> resultTableData : tdData.entrySet()) {
					String key = resultTableData.getKey();
					String value = resultTableData.getValue();
					if (key.equals("hardware-name")) {						
						if (value.equals("RT4401-48A")) {
							deleteData.add(tdData);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	public String getXmlElementData(Element element, String elementName) {
		String outPut = null;
		if (element.getElementsByTagName(elementName) != null
				&& element.getElementsByTagName(elementName).getLength() > 0) {
			outPut = element.getElementsByTagName(elementName).item(0).getTextContent();

		} else {
			outPut = "-";
		}
		return outPut;
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
			logger.error("Exception Audit4GFccIdUtil in getRequiredOutPut5GAudit() "
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