package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FilenameUtils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class CbandMMUReportingToolUtil {

	static final Logger logger = LoggerFactory.getLogger(Audit5GCbandUtil.class);

	@Autowired
	DuoGeneralConfigService duoGeneralConfigService;

	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;

	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	FileUploadRepository fileUploadRepository;

	public String getmmuReportingToolOutput(UploadFileEntity scriptEntity, String dataOutput, String neId,
			String dbcollectionFileName, NetworkConfigEntity networkConfigEntity, boolean useCurrPassword,
			String sanePassword, NetworkConfigEntity neEntity, String sProgramId, String migrationType,
			String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5gOutput, AtomicBoolean isMultipleDUo) {
		StringBuilder shelloutput = new StringBuilder();

		try {

			String testScriptName = XmlCommandsConstants.CBAND_VDU_MMUREPORTINGTOOL;
			String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,isMultipleDUo); 

			//testshellop = returnshellop(testScriptName + ".xml");
			
				shelloutput.append("\n" + testshellop);
				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5gOutput.append(dataOutput1);
				required5gOutput.append("/n");
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
			

		} catch (Exception e) {
			logger.error("Exception RSSITestUtil in getRSSIAudit() " + ExceptionUtils.getFullStackTrace(e));
		}
		return shelloutput.toString();

	}

	@SuppressWarnings("unchecked")
	public String gettestOutput(UploadFileEntity scriptEntity, String dataOutput, String neId,
			String dbcollectionFileName, NetworkConfigEntity networkConfigEntity2, boolean useCurrPassword,
			String sanePassword, NetworkConfigEntity networkConfigEntity, String sProgramId, String migrationType,
			String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			String testScriptName, AtomicBoolean isMultipleDUo) {

		String rssiOutput = "";

		try {

			JSONObject duoData = isDuoApplicable(scriptEntity, networkConfigEntity, useCurrPassword, sanePassword);
			if (duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				String dBpath = "";
				String timeout = "";
				String expectDelay = "";
				String cdPath = "";
				
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				String date = simpleDateFormat.format(new Date());
				date=date.replaceAll("-", "");

				List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
						AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.CBAND_MMU_REPORTING_PATH);
				if (!ObjectUtils.isEmpty(auditConstantsList)) {
					dBpath = auditConstantsList.get(0).getParameterValue();
				}

				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
						AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.CBAND_MMU_REPORTING_TIMEOUT);

				if (!ObjectUtils.isEmpty(auditConstantsList)) {
					timeout = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
						AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.CBAND_MMU_REPORTING_DELAY);

				if (!ObjectUtils.isEmpty(auditConstantsList)) {
					expectDelay = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
						AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.CBAND_MMU_REPORTING_CDPATH);

				if (!ObjectUtils.isEmpty(auditConstantsList)) {
					cdPath = auditConstantsList.get(0).getParameterValue();
				}
				duoData.put("timeout", timeout);
				duoData.put("expectDelay", expectDelay);
				duoData.put("dBpath", dBpath);
				duoData.put("neID", neId);
				duoData.put("date", date);
				duoData.put("cdPath", cdPath);
				if(isMultipleDUo.get()) {
					rssiOutput = duoSessionNeId(userName, outputFileNameAudit, scriptEntity.getFileName(), duoData,neId,sProgramId);
				
				}else {
					rssiOutput = duoSession(userName, outputFileNameAudit, scriptEntity.getFileName(), duoData);	
				}
			}

		} catch (Exception e) {
			logger.error("Exception mh1TestUtil in gettestOutput() " + ExceptionUtils.getFullStackTrace(e));
		}

		return rssiOutput;

	}

	private String duoSessionNeId(String user, String outputFileName, String scriptName, JSONObject duoData, String neId2, String sProgramId) {
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
			if (duoData.containsKey("password")) {
				useCurrPassword = false;
				password = duoData.get("password").toString();
			}

			path = duoData.get("dBpath").toString();
			userPrompt = duoData.get("userPrompt").toString();
			superUserPrompt = duoData.get("superUserPrompt").toString();
			UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
			String exeFileString = "\nExecuting " + scriptName + "\n";
			output.append(exeFileString);
			OutputStream os = new FileOutputStream(outputFileName, true);
			os.write(exeFileString.getBytes());
			String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId2;
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

					String dBpath = duoData.get("dBpath").toString();
					long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
					String neId = duoData.get("neID").toString();
					long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
					String date = duoData.get("date").toString();
					String cdPath = duoData.get("cdPath").toString();
					result = ses.getCbandMMuAuditReportingOutput(dBpath, timeout, expectDelay, neId,date,cdPath);

					ses.setIsSessionInUse(false);
				} else {
					ses.disconnectSession();
					result = connectDuoServerNeId(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt,
							superUserPrompt, duoData,neId2);
				}
			} else {
				result = connectDuoServerNeId(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt,
						superUserPrompt, duoData,neId2);
			}
			String resultString = result.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
					.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
					.replaceAll("[*\\[]K", "");
			os.write(resultString.getBytes());
			output.append(resultString);

		} catch (Exception e) {
			logger.error("Exception RSSITestUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}
		return output.toString();

	}

	private String connectDuoServerNeId(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword,
			boolean useCurrPassword, String path, String userPrompt, String superUserPrompt, JSONObject duoData, String neId2) {
		String result = "";
		String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId2;
		try {
			for (Entry<String, Boolean> entry : GlobalStatusMap.socketSessionInCreation.entrySet()) {
				String key = entry.getKey().toString();
				Boolean value=entry.getValue();
				System.out.println("IN_Creation"+"-----"+key+"----"+value);
				
				if (key.contains(userDetailsEntity.getVpnUserName()) && !key.contains(userName_NeId)) {
					while (GlobalStatusMap.socketSessionInCreation.get(key)) {
						Thread.sleep(5000);
					}
					
				}
			}
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

						String dBpath = duoData.get("dBpath").toString();
						long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
						String neId = duoData.get("neID").toString();
						long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
						String date = duoData.get("date").toString();
						String cdPath = duoData.get("cdPath").toString();
						result = sockets.getCbandMMuAuditReportingOutput(dBpath, timeout, expectDelay, neId, date,cdPath);
						result = sessionOutput + "\n" + result;
						GlobalStatusMap.socketSessionUser.put(userName_NeId, sockets);
					}
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
			logger.error("Exception mmuReprtingTooll in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
		}
		return result;
	}

	private String getRequiredOutPut5GAudit(String shelloutput, String fileName) {

		StringBuilder objBuilder = new StringBuilder();

		try {
			String fileNameWoExten = FilenameUtils.removeExtension(fileName);
			objBuilder.append("\n");
			objBuilder.append(fileNameWoExten);
			objBuilder.append("\n");

			objBuilder.append(shelloutput);
			objBuilder.append("\n");

		} catch (Exception e) {
			logger.error("Exception mh1TestUtil in getRequiredOutPut5GAudit() " + ExceptionUtils.getFullStackTrace(e));
		}

		objBuilder.append(XmlCommandsConstants.ENDTEXT5G);
		objBuilder.append("\n");
		return objBuilder.toString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject isDuoApplicable(UploadFileEntity scriptEntity, NetworkConfigEntity networkConfigEntity,
			boolean useCurrPassword, String sanePassword) {
		JSONObject result = new JSONObject();
		result.put("status", Constants.FAIL);
		try {
			List<ProgramTemplateModel> duoGeneralConfigList = duoGeneralConfigService.getDuoGeneralConfigList();
			if (duoGeneralConfigList != null && !duoGeneralConfigList.isEmpty()
					&& duoGeneralConfigList.get(0).getValue().equalsIgnoreCase("ON")) {
				List<NetworkConfigDetailsEntity> neDetailsList = networkConfigEntity.getNeDetails();
				if (neDetailsList != null && neDetailsList.size() == 1) {
					NetworkConfigDetailsEntity neDetails = neDetailsList.get(0);
					if ("SANE".equalsIgnoreCase(neDetails.getServerTypeEntity().getServerType())) {
						if (neDetails.getPath() == null || neDetails.getPath().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason",
									"DUO ERROR: " + "\n" + "Path is not specified for Network Config Entity");
							return result;
						}
						if (neDetails.getSuperUserPrompt() == null || neDetails.getSuperUserPrompt().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason", "DUO ERROR: " + "\n"
									+ "SuperUserPrompt is not specified for Network Config Entity");
							return result;
						}
						if (neDetails.getUserPrompt() == null || neDetails.getUserPrompt().isEmpty()) {
							result.put("status", Constants.FAIL);
							result.put("reason",
									"DUO ERROR: " + "\n" + "UserPrompt is not specified for Network Config Entity");
							return result;
						}
						result.put("serverIp", neDetails.getServerIp());
						result.put("path", neDetails.getPath());
						result.put("superUserPrompt", neDetails.getSuperUserPrompt());
						result.put("userPrompt", neDetails.getUserPrompt());
						if (useCurrPassword == false) {
							result.put("password", sanePassword);
						}
						result.put("status", Constants.SUCCESS);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception RSSITestUtil in isDuoApplicable() " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
		}
		return result;

	}

	public String duoSession(String user, String outputFileName, String scriptName, JSONObject duoData)
			throws Exception {
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
			if (duoData.containsKey("password")) {
				useCurrPassword = false;
				password = duoData.get("password").toString();
			}

			path = duoData.get("dBpath").toString();
			userPrompt = duoData.get("userPrompt").toString();
			superUserPrompt = duoData.get("superUserPrompt").toString();
			UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
			String exeFileString = "\nExecuting " + scriptName + "\n";
			output.append(exeFileString);
			OutputStream os = new FileOutputStream(outputFileName, true);
			os.write(exeFileString.getBytes());

			if (!GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())) {
				GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), false);
			}
			while (GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName())) {
				Thread.sleep(5000);
			}
			if (GlobalStatusMap.socketSessionUser.containsKey(userDetailsEntity.getVpnUserName())) {
				SocketSession ses = GlobalStatusMap.socketSessionUser.get(userDetailsEntity.getVpnUserName());
				if (ses.isConnectedSession()) {
					while (ses.getIsSessionInUse()) {
						Thread.sleep(5000);
					}
					ses.setIsSessionInUse(true);

					String dBpath = duoData.get("dBpath").toString();
					long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
					String neId = duoData.get("neID").toString();
					long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
					String date = duoData.get("date").toString();
					String cdPath = duoData.get("cdPath").toString();
					result = ses.getCbandMMuAuditReportingOutput(dBpath, timeout, expectDelay, neId,date,cdPath);

					ses.setIsSessionInUse(false);
				} else {
					ses.disconnectSession();
					result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt,
							superUserPrompt, duoData);
				}
			} else {
				result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt,
						superUserPrompt, duoData);
			}
			String resultString = result.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
					.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
					.replaceAll("[*\\[]K", "");
			os.write(resultString.getBytes());
			output.append(resultString);

		} catch (Exception e) {
			logger.error("Exception RSSITestUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}
		return output.toString();

	}

	public String connectDuoServer(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword,
			boolean useCurrPassword, String path, String userPrompt, String superUserPrompt, JSONObject duoData) {
		String result = "";
		try {

			if (GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())) {
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), true);
			} else {
				GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), true);
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

						String dBpath = duoData.get("dBpath").toString();
						long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
						String neId = duoData.get("neID").toString();
						long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
						String date = duoData.get("date").toString();
						String cdPath = duoData.get("cdPath").toString();
						result = sockets.getCbandMMuAuditReportingOutput(dBpath, timeout, expectDelay, neId, date,cdPath);
						result = sessionOutput + "\n" + result;
						GlobalStatusMap.socketSessionUser.put(userDetailsEntity.getVpnUserName(), sockets);
					}
				}
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
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
				GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
			}

		} catch (Exception e) {
			logger.error("Exception mmuReprtingTooll in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
		}
		return result;
	}

	//@SuppressWarnings("finally")
	/*private String returnshellop(String name) {
		String fullOutputLog = "";
		try {
			fullOutputLog = new String(Files.readAllBytes(Paths.get("/home/user/Desktop/Responses/" + name)));
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			return fullOutputLog;
		}
	}*/

}
