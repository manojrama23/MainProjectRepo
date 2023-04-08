package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import com.smart.rct.common.entity.CustomerDetailsEntity;
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
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class Audit5GDSSUtil {


	static final Logger logger = LoggerFactory.getLogger(Audit5GDSSUtil.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	DuoGeneralConfigService duoGeneralConfigService;
	
	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	
	@Autowired
	AuditXmlRulesServiceUtil5GDSS auditXmlRulesServiceUtil5GDSS;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	NeMappingService neMappingService;
	
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
				//	twampshellop = duoSession(userName, outputFileNameAudit, testScriptName + ".xml", duoData, tempCommand);
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
			if ("DSS".equalsIgnoreCase(endName)) {
				String dbcollectionFileName = CommonUtil.createMongoDbFileName(sProgramId, ciqFileName);
				List<CIQDetailsModel> listCIQDetailsModel = getCiqDetailsForRuleValidationsheet(neId, dbcollectionFileName, 
						"vDUGrowSiteLevel(Day1)CQ", "eNBId");
				String gnbId = "";
				if (!ObjectUtils.isEmpty(listCIQDetailsModel))

				{
					CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);

					LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();


					if (testScriptName.contains("ACPF")) {
						if(objMapDetails.containsKey("ACPF_ID")) {
							gnbId = objMapDetails.get("ACPF_ID").getHeaderValue();
							gnbId = gnbId.trim().replaceAll("^0+(?!$)", "");
							if(gnbId.length() > 2 && NumberUtils.isNumber(gnbId)) {
								endName = "ACPF";
							}
						} 
						NeMappingModel neMappingModel = new NeMappingModel();
						CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
						programDetailsEntity.setId(programId);
						neMappingModel.setProgramDetailsEntity(programDetailsEntity);
						neMappingModel
								.setEnbId(objMapDetails.get("ACPF_ID").getHeaderValue().replaceAll("^0+(?!$)", ""));
						List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
						if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0)
								&& (CommonUtil.isValidObject(neMappingEntities.get(0))
										&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
										&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
							mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
						}
					} else if (testScriptName.contains("AUPF")) {
						if(objMapDetails.containsKey("AUPF_ID")) {
							gnbId = objMapDetails.get("AUPF_ID").getHeaderValue();
							gnbId = gnbId.trim().replaceAll("^0+(?!$)", "");
							if(gnbId.length() > 2 && NumberUtils.isNumber(gnbId)) {
								endName = "AUPF";
							} 
						}
						
						NeMappingModel neMappingModel = new NeMappingModel();
						CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
						programDetailsEntity.setId(programId);
						neMappingModel.setProgramDetailsEntity(programDetailsEntity);
						neMappingModel
								.setEnbId(objMapDetails.get("AUPF_ID").getHeaderValue().replaceAll("^0+(?!$)", ""));
						List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
						if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0)
								&& (CommonUtil.isValidObject(neMappingEntities.get(0))
										&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
										&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
							mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
						}
					} else if (testScriptName.contains("vDU")) {
						if(objMapDetails.containsKey("NEID")) {
							endName = "ADPF";
							gnbId = objMapDetails.get("NEID").getHeaderValue().replaceAll("^0+(?!$)", "");
						}
						
					} else if (testScriptName.contains("eNB")) {
						if(objMapDetails.containsKey("4GeNB")) {
							endName = "eNB";
							gnbId = objMapDetails.get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", "");
						}
						
					} else if (testScriptName.contains("FSU")) {
						if(objMapDetails.containsKey("NEID")) {								
							List<CIQDetailsModel> listCIQsheetDetailsModel = fileUploadRepository
									.getEnbTableSheetDetailss(ciqFileName, "DSS_MOP_Parameters-1",
											objMapDetails.get("NEID").getHeaderValue(), dbcollectionFileName);
							if (!ObjectUtils.isEmpty(listCIQsheetDetailsModel) && listCIQsheetDetailsModel.get(0).getCiqMap().containsKey("FSUID")) {
								endName = "FSU";
								gnbId = listCIQsheetDetailsModel.get(0).getCiqMap().get("FSUID").getHeaderValue()
										.replaceAll("^0+(?!$)", "");
							}
						}
						
					}
					command = CommonUtil.getCurlCommandAuditCbandTw(scriptAbsFileName, mcmip, gnbId, endName, testScriptName, replaceData);		
				}	
				
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
	
	public String getCbandTwampOutput(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String,Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo) {
		StringBuilder shelloutput = new StringBuilder();
		try {
			String testScriptName = "";
			long waitTime = 60000;
			List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.CBAND_TWAMP_WAIT);
			
			if(!ObjectUtils.isEmpty(auditConstantsList) && NumberUtils.isNumber(auditConstantsList.get(0).getParameterValue())) {
				waitTime = NumberUtils.toLong(auditConstantsList.get(0).getParameterValue()) * 1000;
			}
			
			testScriptName = XmlCommandsConstants.DSS_F1U_SOURCEIP;
			List<String> replaceData = new ArrayList<>();
			String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
			//testshellop = returnshellop(testScriptName + ".xml");
			
			shelloutput.append("\n" + testshellop);
			
			String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
					testScriptName + ".xml");
			required5GOutput.append(dataOutput1);
			required5GOutput.append("/n");
			
			if(testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
				
				String sourceIp = auditXmlRulesServiceUtil5GDSS.getCbandSourceIp(required5GOutput.toString(), testScriptName, neId, dbcollectionFileName, networkConfigEntity);
				
				System.out.println(sourceIp);
				
				if(!compareIPAddr(sourceIp, sourceIp)) {
					return shelloutput.toString();
				}
				
				testScriptName = XmlCommandsConstants.DSS_F1U_DESTINATIONIP;
				testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
						networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
						originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
				//testshellop = returnshellop(testScriptName + ".xml");
				
				shelloutput.append("\n" + testshellop);
				
				dataOutput1 = getRequiredOutPut5GAudit(testshellop,
						testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				if(testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
					
					LinkedHashMap<String, String> destinationIpList = auditXmlRulesServiceUtil5GDSS.getCbandDestinationIp(required5GOutput.toString(), testScriptName, neId, dbcollectionFileName, networkConfigEntity);
								
					System.out.println(destinationIpList);
					
					for(Map.Entry<String, String> destinationIp : destinationIpList.entrySet()) {
						String key = destinationIp.getKey();
						String value = destinationIp.getValue();
						
						if(!compareIPAddr(value, value)) {
							continue;
						}
						testScriptName = XmlCommandsConstants.DSS_F1U_TESTTWPING;
						replaceData = new ArrayList<>();
						replaceData.add(sourceIp);
						replaceData.add(value);
						testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
								networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
								originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
						//testshellop = returnshellop(testScriptName + ".xml");
						if(testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
							shelloutput.append("\n" + testshellop);
							
							dataOutput1 = getRequiredOutPut5GAudit(testshellop,
									testScriptName + ".xml");
							required5GOutput.append(dataOutput1);
							required5GOutput.append("/n");
							
							Thread.sleep(waitTime);
							String testId = auditXmlRulesServiceUtil5GDSS.gettestIdtwamp(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity);
							
							if(!NumberUtils.isNumber(testId)) {
								continue;
							}
							
							testScriptName = XmlCommandsConstants.DSS_F1U_TESTTWPING_DIAGNOSIS;
							replaceData = new ArrayList<>();
							replaceData.add(testId);
							testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
									networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
									originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, replaceData,isMultipleDUo);
							//testshellop = returnshellop(testScriptName + ".xml");
							if(testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
								shelloutput.append("\n" + testshellop);
								
								dataOutput1 = getRequiredOutPut5GAudit(testshellop,
										key + ".xml");
								required5GOutput.append(dataOutput1);
								required5GOutput.append("/n");
								
								createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
							}
						}
						
					}
				} else {
					createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
				}
				
			} else {
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
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
	
	private boolean compareIPAddr(String ip1, String ip2) {
		boolean result = false;
		try {
			InetAddress ipAddr1 = InetAddress.getByName(ip1);
			InetAddress ipAddr2 = InetAddress.getByName(ip2);
			if(ipAddr1.equals(ipAddr2)) {
				result = true;
			}
		} catch(Exception e) {
			result = false;
		}
		return result;
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
