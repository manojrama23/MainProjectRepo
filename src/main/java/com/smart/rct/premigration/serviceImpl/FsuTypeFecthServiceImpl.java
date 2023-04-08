package com.smart.rct.premigration.serviceImpl;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.service.FsuTypeFecthService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.PasswordCrypt;

@Service
public class FsuTypeFecthServiceImpl implements FsuTypeFecthService{

	static final Logger logger = LoggerFactory.getLogger(FsuTypeFecthServiceImpl.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	DuoGeneralConfigService duoGeneralConfigService;
	
	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
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
	
	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getFSUType(NeMappingEntity neMappingEntity, String user, String fsuIp) {
		JSONObject result = new JSONObject();
		result.put("status", Constants.FAIL);
		result.put("reason", "Unable to Fecth FSU Type from USM");
		try {
			NetworkConfigEntity networkConfigEntity = neMappingEntity.getNetworkConfigEntity();
			JSONObject duoData = isDuoApplicable(networkConfigEntity, true, "");
			if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
				String fsuUserName = "";
				String fsuPassword = "";
				String timeout = "";
				String expect1 = "";
				String expect2 = "";
				String expect3 = "";
				String expect4 = "";
				String fsuType10 = "";
				String fsuType20 = "";
				String command3 = "";
				String command4 = "";
				String expectDelay = "";
				String expect5 ="";
				List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_USERNAME);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					fsuUserName = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_PASSWORD);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					fsuPassword = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_SSH_LOGIN_TIMEOUT);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					timeout = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_1);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expect1 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_2);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expect2 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_3);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expect3 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_4);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expect4 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_TYPE_20);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					fsuType20 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_TYPE_10);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					fsuType10 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_5);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expect5 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXPECT_DELAY);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					expectDelay = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXIT_COMMAND1);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					command3 = auditConstantsList.get(0).getParameterValue();
				}
				auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU,
						AuditConstants.FSU_EXIT_COMMAND2);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					command4 = auditConstantsList.get(0).getParameterValue();
				}
				duoData.put("fsuUserName", fsuUserName);
				duoData.put("fsuPassword", fsuPassword);
				duoData.put("timeout", timeout);
				duoData.put("expect1", expect1);
				duoData.put("expect2", expect2);
				duoData.put("expect3", expect3);
				duoData.put("expect4", expect4);
				duoData.put("vsmUser", networkConfigEntity.getNeUserName());
				duoData.put("vsmpassword", networkConfigEntity.getNePassword());
				duoData.put("vsmIp", networkConfigEntity.getNeIp());
				duoData.put("fsuIp", fsuIp);
				duoData.put("expect5", expect5);
				duoData.put("expectDelay", expectDelay);
				duoData.put("command3", command3);
				duoData.put("command4", command4);
				if(!(fsuUserName.isEmpty() || fsuPassword.isEmpty() || timeout.isEmpty() || expect1.isEmpty() || expect2.isEmpty() 
						|| expect3.isEmpty() || expect4.isEmpty() || fsuType20.isEmpty() || fsuType10.isEmpty()
						|| expect5.isEmpty() || expectDelay.isEmpty() || command3.isEmpty() || command4.isEmpty())) {
					String fsuType = duoSession(user, duoData);
					System.out.println("FSU Type : " + fsuType);
					if(fsuType.toLowerCase().contains(fsuType20)) {
						result.put("status", Constants.SUCCESS);
						result.put("fsuType", Constants.FSU_TYPE_20);
					} else if(fsuType.toLowerCase().contains(fsuType10)) {
						result.put("status", Constants.SUCCESS);
						result.put("fsuType", Constants.FSU_TYPE_10);
					}
				}
				
			}
		} catch (Exception e) {
			logger.error("Exception in getFSUType() FsuTypeFecthServiceImpl : " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	
	public String duoSession(String user, JSONObject duoData) throws Exception {

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
					//FSU Type Code 
					String vsmUser = duoData.get("vsmUser").toString();
					String vsmpassword = duoData.get("vsmpassword").toString();
					String vsmIp = duoData.get("vsmIp").toString();
					String fsuUserName = duoData.get("fsuUserName").toString();
					String fsuPassword = duoData.get("fsuPassword").toString();
					String fsuIp = duoData.get("fsuIp").toString();
					long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
					String expect1 = duoData.get("expect1").toString();
					String expect2 = duoData.get("expect2").toString();
					String expect3 = duoData.get("expect3").toString();
					String expect4 = duoData.get("expect4").toString();
					String expect5 = duoData.get("expect5").toString();
					String command3 = duoData.get("command3").toString();
					String command4 = duoData.get("command4").toString();
					long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
					result = ses.getFsuLog(vsmUser, vsmpassword, fsuUserName, fsuPassword, vsmIp, fsuIp, expect1, expect2, expect3, expect4, timeout,
							expectDelay, expect5, command3, command4);
					
					ses.setIsSessionInUse(false);
				} else {
					ses.disconnectSession();
					result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, duoData);
				}
			} else {
				result = connectDuoServer(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, duoData);
			}
			String resultString = result.replaceAll("\\[\\dm", "").replaceAll("\\[[\\d;]*m", "")
					.replaceAll("\\u0007", "").replaceAll("\\u001B", "").replaceAll("\\]\\d;", "")
					.replaceAll("[*\\[]K", "");
			output.append(resultString);
		} catch(Exception e) {
			logger.error("Exception RunTestServiceImpl in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}		
		return output.toString();
		
	}
	
	public String connectDuoServer(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword, boolean useCurrPassword, String path,
			String userPrompt, String superUserPrompt, JSONObject duoData) {
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
					//FSU Type Code
					//String resultOutput = sockets.runCommand(command);
					String vsmUser = duoData.get("vsmUser").toString();
					String vsmpassword = duoData.get("vsmpassword").toString();
					String vsmIp = duoData.get("vsmIp").toString();
					String fsuUserName = duoData.get("fsuUserName").toString();
					String fsuPassword = duoData.get("fsuPassword").toString();
					String fsuIp = duoData.get("fsuIp").toString();
					long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
					String expect1 = duoData.get("expect1").toString();
					String expect2 = duoData.get("expect2").toString();
					String expect3 = duoData.get("expect3").toString();
					String expect4 = duoData.get("expect4").toString();
					String expect5 = duoData.get("expect5").toString();
					String command3 = duoData.get("command3").toString();
					String command4 = duoData.get("command4").toString();
					long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
					result = sockets.getFsuLog(vsmUser, vsmpassword, fsuUserName, fsuPassword, vsmIp, fsuIp, expect1, expect2, expect3, expect4, timeout,
							expectDelay, expect5, command3, command4);
					
					
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
}
