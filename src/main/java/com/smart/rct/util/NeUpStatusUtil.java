package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.DuoGeneralConfigService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.XmlCommandsConstants;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.postmigration.entity.AuditFirmwareDetailsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.postmigration.repository.AuditFirmwareDetailsRepository;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class NeUpStatusUtil {

	static final Logger logger = LoggerFactory.getLogger(NeUpStatusUtil.class);
	
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
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	NeMappingService neMappingService;
	
	@Autowired
	AuditFirmwareDetailsRepository auditFirmwareDetailsRepository;
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
			logger.error("Exception NeUpStatusUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception NeUpStatusUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
			GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
		}
		return result;
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
			logger.error("Exception NeUpStatusUtil in gettwampoutput() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception NeUpStatusUtil in isDuoApplicable() " + ExceptionUtils.getFullStackTrace(e));
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
			int programId = Integer.parseInt(sProgramId);
			String scriptAbsFileName = "";

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
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(sProgramId, ciqFileName);
			List<CIQDetailsModel> listCIQDetailsModel = getCIQDetailsModelList(neId, dbcollectionFileName);
			if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
				CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);

				LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
				for (CIQDetailsModel ciqDetails : listCIQDetailsModel) {
					if (ciqDetails.getSheetAliasName().equals("vDUGrowSiteLevel(Day1)CQ")) {
						ciqDetailsModel = ciqDetails;
					}
				}
				objMapDetails = ciqDetailsModel.getCiqMap();
				if (objMapDetails.containsKey("NEID")) {
					endName = "ADPF";
					gnbId = objMapDetails.get("NEID").getHeaderValue().replaceAll("^0+(?!$)", "");
				} else {
					result.put("status", Constants.FAIL);
					result.put("reason", "NE_ID not found");
					return result;
				}

			}
			command = CommonUtil.getCurlCommandAudit4GTestjson(mcmip, gnbId, endName, replaceData);

			result.put("command", command);
			result.put("status", Constants.SUCCESS);

		} catch (Exception e) {
			logger.error(
					"Exception NeUpStatusUtil in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
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
						+ useCaseName.trim().replaceAll(" ", "_") + "/" + scriptEntity.getFileName();
			} else {
				scriptAbsFileName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
						+ scriptEntity.getFilePath() + "/" + useCaseName.trim().replaceAll(" ", "_") + "/"
						+ scriptEntity.getFileName();
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
				endName = scriptEntity.getFileName().substring(0, scriptEntity.getFileName().indexOf('_'));
			}
			if ("ACPF".equalsIgnoreCase(endName) || "AUPF".equalsIgnoreCase(endName) || "AU".equalsIgnoreCase(endName)
					|| "DU".equalsIgnoreCase(endName) || "DSS".equalsIgnoreCase(endName)
					|| "4GAudit".equalsIgnoreCase(endName) || "IAU".equalsIgnoreCase(endName)
					|| "CBand".equalsIgnoreCase(endName) || "4GUSM".equalsIgnoreCase(endName) || "4GFSU".equalsIgnoreCase(endName)) {
				String dbcollectionFileName = CommonUtil.createMongoDbFileName(sProgramId, ciqFileName);
				List<CIQDetailsModel> listCIQDetailsModel = getCIQDetailsModelList(neId, dbcollectionFileName);
				String gnbId = "";
				if (!ObjectUtils.isEmpty(listCIQDetailsModel))

				{
					CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);

					LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();

					if ("ACPF".equalsIgnoreCase(endName) && !ObjectUtils.isEmpty(objMapDetails)
							&& objMapDetails.containsKey("NE_ID ACPF")) {
						String acpfName = "";
						if (objMapDetails.containsKey("NE_ID_ACPF Name")) {
							acpfName = objMapDetails.get("NE_ID_ACPF Name").getHeaderValue();
						}
						gnbId = objMapDetails.get("NE_ID ACPF").getHeaderValue();
						gnbId = gnbId.trim();
						gnbId = gnbId.replaceAll("^0+(?!$)", "");
						/*
						 * if(acpfName.contains("ACPFP")) { gnbId = gnbId.trim(); gnbId =
						 * gnbId.replaceAll("^0+(?!$)", ""); } else if (StringUtils.isNotEmpty(gnbId) &&
						 * gnbId.length() >= 4) { gnbId = gnbId.trim(); gnbId =
						 * gnbId.substring(gnbId.length() - 4); gnbId = gnbId.replaceAll("^0+(?!$)",
						 * "");
						 * 
						 * }
						 */

					} else if ("AUPF".equalsIgnoreCase(endName) && !ObjectUtils.isEmpty(objMapDetails)
							&& objMapDetails.containsKey("NE_ID_AUPF")) {
						String aupfName = "";
						if (objMapDetails.containsKey("NE_ID_AUPF Name")) {
							aupfName = objMapDetails.get("NE_ID_AUPF Name").getHeaderValue();
						}
						gnbId = objMapDetails.get("NE_ID_AUPF").getHeaderValue();
						gnbId = gnbId.trim();
						gnbId = gnbId.replaceAll("^0+(?!$)", "");

						/*
						 * if(aupfName.contains("AUPFP")) { gnbId = gnbId.trim(); gnbId =
						 * gnbId.replaceAll("^0+(?!$)", ""); } else if (StringUtils.isNotEmpty(gnbId) &&
						 * gnbId.length() >= 4) { gnbId = gnbId.trim(); gnbId =
						 * gnbId.substring(gnbId.length() - 4); gnbId = gnbId.replaceAll("^0+(?!$)",
						 * "");
						 * 
						 * }
						 */

					} else if (("AU".equalsIgnoreCase(endName) || "DU".equalsIgnoreCase(endName)
							|| "IAU".equalsIgnoreCase(endName)) && !ObjectUtils.isEmpty(objMapDetails)
							&& objMapDetails.containsKey("NE ID AU")) {

						endName = "DU";
						gnbId = objMapDetails.get("NE ID AU").getHeaderValue();
						if (StringUtils.isNotEmpty(gnbId)) {
							gnbId = gnbId.trim();
							gnbId = gnbId.replaceAll("^0+(?!$)", "");
						}

					} else if ("DSS".equalsIgnoreCase(endName) && !ObjectUtils.isEmpty(objMapDetails)) {
						for (CIQDetailsModel ciqDetails : listCIQDetailsModel) {
							if (ciqDetails.getSheetAliasName().equals("vDUGrowSiteLevel(Day1)CQ")) {
								ciqDetailsModel = ciqDetails;
							}
						}
						objMapDetails = ciqDetailsModel.getCiqMap();
						if (scriptEntity.getFileName().contains("ACPF")) {
							if (objMapDetails.containsKey("ACPF_ID")) {
								gnbId = objMapDetails.get("ACPF_ID").getHeaderValue();
								gnbId = gnbId.trim().replaceAll("^0+(?!$)", "");
								if (gnbId.length() > 2 && NumberUtils.isNumber(gnbId)) {
									endName = "ACPF";
								} else {
									result.put("status", Constants.FAIL);
									result.put("reason", "ACPF_ID data not correct");
									return result;
								}
							} else {
								result.put("status", Constants.FAIL);
								result.put("reason", "ACPF_ID not found");
								return result;
							}

						} else if (scriptEntity.getFileName().contains("AUPF")) {
							if (objMapDetails.containsKey("AUPF_ID")) {
								gnbId = objMapDetails.get("AUPF_ID").getHeaderValue();
								gnbId = gnbId.trim().replaceAll("^0+(?!$)", "");
								if (gnbId.length() > 2 && NumberUtils.isNumber(gnbId)) {
									endName = "AUPF";
								} else {
									result.put("status", Constants.FAIL);
									result.put("reason", "AUPF_ID data not correct");
									return result;
								}
							} else {
								result.put("status", Constants.FAIL);
								result.put("reason", "AUPF_ID not found");
								return result;
							}

						} else if (scriptEntity.getFileName().contains("vDU")) {
							if (objMapDetails.containsKey("NEID")) {
								endName = "ADPF";
								gnbId = objMapDetails.get("NEID").getHeaderValue().replaceAll("^0+(?!$)", "");
							} else {
								result.put("status", Constants.FAIL);
								result.put("reason", "NE_ID not found");
								return result;
							}

						} else if (scriptEntity.getFileName().contains("eNB")) {
							if (objMapDetails.containsKey("4GeNB")) {
								endName = "eNB";
								gnbId = objMapDetails.get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", "");

								NeMappingModel neMappingModel = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
								// programDetailsEntity.setProgramName("VZN-4G-USM-LIVE");
								programDetailsEntity.setId(34);
								neMappingModel.setProgramDetailsEntity(programDetailsEntity);
								neMappingModel.setEnbId(
										objMapDetails.get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
								if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0)
										&& (CommonUtil.isValidObject(neMappingEntities.get(0))
												&& CommonUtil
														.isValidObject(neMappingEntities.get(0).getSiteConfigType())
												&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
								}
							} else {
								result.put("status", Constants.FAIL);
								result.put("reason", "4GeNB not found");
								return result;
							}

						} else if (scriptEntity.getFileName().contains("FSU")) {
							if (objMapDetails.containsKey("NEID")) {
								List<CIQDetailsModel> listCIQsheetDetailsModel = fileUploadRepository
										.getEnbTableSheetDetailss(ciqFileName, "DSS_MOP_Parameters-1",
												objMapDetails.get("NEID").getHeaderValue(), dbcollectionFileName);
								if (!ObjectUtils.isEmpty(listCIQsheetDetailsModel)
										&& listCIQsheetDetailsModel.get(0).getCiqMap().containsKey("FSUID")) {
									endName = "FSU";
									gnbId = listCIQsheetDetailsModel.get(0).getCiqMap().get("FSUID").getHeaderValue()
											.replaceAll("^0+(?!$)", "");
									if (objMapDetails.containsKey("4GeNB")) {
										NeMappingModel neMappingModel = new NeMappingModel();
										CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
										// programDetailsEntity.setProgramName("VZN-4G-USM-LIVE");
										programDetailsEntity.setId(34);
										neMappingModel.setProgramDetailsEntity(programDetailsEntity);
										neMappingModel.setEnbId(
												objMapDetails.get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", ""));
										List<NeMappingEntity> neMappingEntities = neMappingService
												.getNeMapping(neMappingModel);
										if ((CommonUtil.isValidObject(neMappingEntities)
												&& neMappingEntities.size() > 0)
												&& (CommonUtil.isValidObject(neMappingEntities.get(0))
														&& CommonUtil.isValidObject(
																neMappingEntities.get(0).getSiteConfigType())
														&& (neMappingEntities.get(0).getSiteConfigType()
																.length() > 0))) {
											mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
										}
									}
								} else {
									result.put("status", Constants.FAIL);
									result.put("reason", "FSU_ID not found");
									return result;
								}
							} else {
								result.put("status", Constants.FAIL);
								result.put("reason", "FSU_ID not found");
								return result;
							}

						}

					} else if ("CBand".equalsIgnoreCase(endName) && !ObjectUtils.isEmpty(objMapDetails)) {
						if (scriptEntity.getFileName().contains("vDU") && objMapDetails.containsKey("NEID")) {
							endName = "ADPF";
							gnbId = objMapDetails.get("NEID").getHeaderValue().replaceAll("^0+(?!$)", "");
						} else if (scriptEntity.getFileName().contains("AUPF")
								&& objMapDetails.containsKey("AUPF_ID")) {
							endName = "AUPF";
							gnbId = objMapDetails.get("AUPF_ID").getHeaderValue().replaceAll("^0+(?!$)", "");
						} else if (scriptEntity.getFileName().contains("ACPF")
								&& objMapDetails.containsKey("ACPF_ID")) {
							endName = "ACPF";
							gnbId = objMapDetails.get("ACPF_ID").getHeaderValue().replaceAll("^0+(?!$)", "");
						} else if (scriptEntity.getFileName().contains("eNB") && objMapDetails.containsKey("ENB_ID")) {
							endName = "eNB";
							gnbId = objMapDetails.get("ENB_ID").getHeaderValue().replaceAll("^0+(?!$)", "");

							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(programId);
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel
									.setEnbId(objMapDetails.get("ENB_ID").getHeaderValue().replaceAll("^0+(?!$)", ""));
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0)
									&& (CommonUtil.isValidObject(neMappingEntities.get(0))
											&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
											&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
								mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
							}
						}
					} else if ("4GUSM".equalsIgnoreCase(endName)) {
						endName = "eNB";
						gnbId = neId.replaceAll("^0+(?!$)", "");
					} else if (("4GFSU".equalsIgnoreCase(endName)|| "4GFSU".equalsIgnoreCase(endName)) && !ObjectUtils.isEmpty(objMapDetails)) {
						if (scriptEntity.getFileName().contains("FSU") && objMapDetails.containsKey("NE_ID")) {
							endName = "FSU";
							gnbId = objMapDetails.get("NE_ID").getHeaderValue().replaceAll("^0+(?!$)", "");
						} else if (scriptEntity.getFileName().contains("eNB")
								&& objMapDetails.containsKey("eNB_Name")) {
							endName = "eNB";
							gnbId = objMapDetails.get("eNB_Name").getHeaderValue().replaceAll("^0+(?!$)", "");
						}
					}

				}
				if (scriptEntity.getFileName().contains(XmlCommandsConstants.AU_TWAMP_F1C_LINK)) {
					CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
					LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
					if (objMapDetails.containsKey("Remote_IP_Address")) {
						String remoteIp = objMapDetails.get("Remote_IP_Address").getHeaderValue().trim();
						command = CommonUtil.getCurlCommandAuditTwamp(scriptAbsFileName, mcmip, gnbId, endName,
								scriptEntity.getFileName(), remoteIp);
					}
				} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.IAU_TWAMP_F1C_LINK)) {
					CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
					LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
					if (objMapDetails.containsKey("Remote_IP_Address")) {
						String remoteIp = objMapDetails.get("Remote_IP_Address").getHeaderValue().trim();
						command = CommonUtil.getCurlCommandAuditTwampIAU(scriptAbsFileName, mcmip, gnbId, endName,
								scriptEntity.getFileName(), remoteIp);
					}
				} else {
					command = CommonUtil.getCurlCommandAudit(scriptAbsFileName, mcmip, gnbId, endName);
				}

				result.put("command", command);
				result.put("status", Constants.SUCCESS);

			}
		} catch (Exception e) {
			logger.error(
					"Exception NeUpStatusUtil in getCurlCommandAudit() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception NeUpStatusUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception NeUpStatusUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception NeUpStatusUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
		}		
		return output.toString();
		
	}
	
	public String getFirmWareCheck(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo,
			String outputFileNameResult) {
		StringBuilder shelloutput = new StringBuilder();
		try {
			String testScriptName = "";
			String testshellop;
			int pId = Integer.parseInt(sProgramId);
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
					Constants.NE_STATUS_CONFIG_TEMPLATE);
			String[] totalRun = null;
			int run = 5;
			String[] sleepTime = null;
			int waitTime = 1;
			if (CommonUtil.isValidObject(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
				try {
					JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
					if (objData.containsKey("TotalRunFirmware")) {
						totalRun = objData.get("TotalRunFirmware").toString().trim().split(",");
					}
					if (objData.containsKey("WaitTimeFirmware")) {
						sleepTime = objData.get("WaitTimeFirmware").toString().trim().split(",");
					}
					run = Integer.valueOf(totalRun[0]);
					waitTime = Integer.valueOf(sleepTime[0]);
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
			String abc = "NE FIRMWARE VALIDATION RESULT LOG";
			OutputStream os = new FileOutputStream(outputFileNameResult, true);
			os.write(abc.getBytes());
			if (scriptEntity.getFileName().contains(XmlCommandsConstants.AUDIT_4G_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.AUDIT_4G_FIRMWARECHECK;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.DSS_VDU_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.DSS_VDU_FIRMWARECHECK;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.CBAND_VDU_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.CBAND_VDU_FIRMWARECHECK;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.AU_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.AU_FIRMWARECHECK;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.AUDIT4G_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.AUDIT4G_FIRMWARECHECK;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.IAU_FIRMWARECHECK)) {
				testScriptName = XmlCommandsConstants.IAU_FIRMWARECHECK;
			}
			for (int i = 0; i < run; i++) {

				testshellop = gettestOutputNeName(scriptEntity, dataOutput, neId, dbcollectionFileName,
						networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
						originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, isMultipleDUo);
				//testshellop = returnshellop(testScriptName + ".xml");
				shelloutput.append("\n" + testshellop);
				String exeFileString = "";
				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				if (testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
					StringBuilder inVocationId = getInvocationId(dataOutput1, testScriptName, neId,
							dbcollectionFileName, networkConfigEntity);
					if (inVocationId.length() == 0) {
						createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
						exeFileString = "\n" + "CHECK :" + (i + 1) + " STATUS: [PASS] " + "\n";
						os.write(exeFileString.getBytes());
						break;
					} else {
						createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
						exeFileString = "\n" + "CHECK :" + (i + 1) + " STATUS: [FAIL] " + "\n"
								+ "MISSING FIRMWARE IN FIRMWARE UPGRADE" + "\n" + inVocationId.toString() + "\n";
						os.write(exeFileString.getBytes());
					}

				} else {
					createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
					exeFileString = "\n" + "CHECK :" + (i + 1) + " STATUS: [FAIL] "
							+ " NE FIRMWARE VALIDATION FAILED RESULT LOG" + "\n"
							+ "[NO DATA FROM USM/IMPROPER RESPONSE]" + "\n";
					os.write(exeFileString.getBytes());
				}
				TimeUnit.SECONDS.sleep(waitTime);
			}

		} catch (Exception e) {
			logger.error("Exception NeUpStatusUtil in getFirmWareCheck() " + ExceptionUtils.getFullStackTrace(e));
		}
		return shelloutput.toString();
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
	private StringBuilder getInvocationId(String fullOutputLog, String command, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity) {
		StringBuilder invocationId = new StringBuilder();
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";
			LinkedHashSet<String> headerList1 = new LinkedHashSet<>();
			List<LinkedHashMap<String, String>> tabelData1 = new ArrayList<>();
			Document document = builder.parse(new InputSource(new StringReader(outputLog)));

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
			String softwareVersion = networkConfigEntity.getNeVersionEntity().getNeVersion();

			for (LinkedHashMap<String, String> tdData : tabelData1) {
				String firmwareName = "";
				String patchVersion = "";
				String packageVersion = "";
				String locationType = "";

				boolean firmwareNamePresent = false;
				boolean patchVersionPresent = false;
				boolean packageVersionPresent = false;

				if (tdData.containsKey("patch-version")) {
					patchVersion = tdData.get("patch-version");
				}
				if (tdData.containsKey("package-version")) {
					packageVersion = tdData.get("package-version");
				}
				if (tdData.containsKey("location-type")) {
					locationType = tdData.get("location-type");
				}
				if(locationType.contains("memory-running")) {
				if (tdData.containsKey("firmware-name")) {
					firmwareName = tdData.get("firmware-name").trim();
					if (firmwareName.length() != 0) {
						softwareVersion = StringUtils.substringBefore(softwareVersion, "-").trim();
						List<AuditFirmwareDetailsEntity> auditFirmwareDetailsList = null;
						if (command.contains(XmlCommandsConstants.AUDIT_4G_FIRMWARECHECK)) {
							auditFirmwareDetailsList = auditFirmwareDetailsRepository
									.getAuditFirmwareDetailsEntityALL(firmwareName, "eNB", softwareVersion,packageVersion);
						} else if (command.contains(XmlCommandsConstants.AU_FIRMWARECHECK)) {

							auditFirmwareDetailsList = auditFirmwareDetailsRepository
									.getAuditFirmwareDetailsEntityALL(firmwareName, "AU", softwareVersion,packageVersion);
						} else if (command.contains(XmlCommandsConstants.IAU_FIRMWARECHECK)) {

							auditFirmwareDetailsList = auditFirmwareDetailsRepository
									.getAuditFirmwareDetailsEntityALL(firmwareName, "MM_IAU", softwareVersion,packageVersion);
						} else if (command.contains(XmlCommandsConstants.AUDIT4G_FIRMWARECHECK)) {
							auditFirmwareDetailsList = auditFirmwareDetailsRepository
									.getAuditFirmwareDetailsEntityALL(firmwareName, "FSU", softwareVersion,packageVersion);
						}
						if (!ObjectUtils.isEmpty(auditFirmwareDetailsList)) {
							firmwareNamePresent = true;
							if (tdData.containsKey("package-version") && auditFirmwareDetailsList.get(0)
									.getPackageVersion().trim().equalsIgnoreCase(packageVersion.trim())) {
								packageVersionPresent = true;
							}
							if (tdData.containsKey("patch-version") && NumberUtils.isNumber(patchVersion.trim())
									&& NumberUtils.isNumber(auditFirmwareDetailsList.get(0).getPatchVersion().trim())
									&& Float.valueOf(patchVersion.trim()).equals(
											Float.valueOf(auditFirmwareDetailsList.get(0).getPatchVersion().trim()))) {
								patchVersionPresent = true;
							}
						}
					}
				}

				if (!(packageVersionPresent && patchVersionPresent && firmwareNamePresent)) {
					invocationId.append("["+"UNIT-TYPE : "+tdData.get("unit-type")+" UNIT-ID : "+tdData.get("unit-id")+" SUBUNIT-ID : "+tdData.get("subunit-id")+" FIRMWARE-NAME : " + firmwareName + " PACKAGE-VERSION : " + packageVersion
							+ " PATCH-VERSION : " + patchVersion + "]" + "\n");
				}
			 }
			}

		} catch (Exception e) {
			logger.error("Exception NeUpStatusUtil in getInvocationId() " + ExceptionUtils.getFullStackTrace(e));

		}
		return invocationId;
	}
	public String getNEUpResponse(UploadFileEntity scriptEntity, String dataOutput, String neId, String dbcollectionFileName,
			NetworkConfigEntity networkConfigEntity, boolean useCurrPassword, String sanePassword, NetworkConfigEntity neEntity,
			String sProgramId, String migrationType, String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5GOutput, AtomicBoolean isMultipleDUo, String outputFileNameResult) {
		StringBuilder shelloutput = new StringBuilder();
		StringBuilder neOplog=new StringBuilder();
		try {
			String startStatus="NE UP STATUS CHECK RESULT LOG"+"\n";
			OutputStream os = new FileOutputStream(outputFileNameResult, true);
			os.write(startStatus.getBytes());
			String testScriptName = "";
			String testshellop;
			int pId = Integer.parseInt(sProgramId);
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
					Constants.NE_STATUS_CONFIG_TEMPLATE);
			String[] totalRun = null;
			int run = 5;
			String[] sleepTime=null;
			int waitTime=1;
			if (CommonUtil.isValidObject(programTemplateEntity)
					&& StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
				try {
					JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
					if (objData.containsKey("TotalRun")) {
						totalRun = objData.get("TotalRun").toString().trim().split(",");
					}
					if (objData.containsKey("WaitTime")) {
						sleepTime = objData.get("WaitTime").toString().trim().split(",");
					}
					run = Integer.valueOf(totalRun[0]);
					waitTime=Integer.valueOf(sleepTime[0]);
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
			if (scriptEntity.getFileName().contains(XmlCommandsConstants.DSS_VDU_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.DSS_VDU_NEUP_USECASE;
			} else if (scriptEntity.getFileName().contains(XmlCommandsConstants.CBAND_VDU_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.CBAND_VDU_NEUP_USECASE;
			}else if (scriptEntity.getFileName().contains(XmlCommandsConstants.AUDIT_4G_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.AUDIT_4G_NEUP_USECASE;
			}else if (scriptEntity.getFileName().contains(XmlCommandsConstants.AU_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.AU_NEUP_USECASE;
			}else if (scriptEntity.getFileName().contains(XmlCommandsConstants.AUDIT4G_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.AUDIT4G_NEUP_USECASE;
			}else if (scriptEntity.getFileName().contains(XmlCommandsConstants.IAU_NEUP_USECASE)) {
				testScriptName = XmlCommandsConstants.IAU_NEUP_USECASE;
			}
			for (int i = 0; i < run; i++) {
				
				testshellop = gettestOutputNeName(scriptEntity, dataOutput, neId, dbcollectionFileName,
						networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
						originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, isMultipleDUo);
				//testshellop = returnshellop(testScriptName + ".xml");
				shelloutput.append("\n" + testshellop);

				String dataOutput1 = getRequiredOutPut5GAudit(testshellop, testScriptName + ".xml");
				required5GOutput.append(dataOutput1);
				required5GOutput.append("/n");
				
				if (!testshellop.contains("<rpc-reply") && !(testshellop.contains("<rpc-error"))) {
					neOplog.append("CHECK :"+(i+1)+" STATUS: [FAIL] "+"\n"+"[REASON: NO DATA FROM USM/IMPROPER RESPONSE]"+"\n");
					createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
					
				}else if ((testshellop.contains("<rpc-reply")) && (testshellop.contains("<rpc-error"))) {
					String errorOp = geterrorop(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity);
					neOplog.append("CHECK :"+(i+1)+" STATUS: [FAIL] "+"\n"+"[REASON: "+errorOp+"]"+"\n");
					createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
					
				}else if ((testshellop.contains("<rpc-reply")) && !(testshellop.contains("<rpc-error"))) {
					String errorOp = getop(dataOutput1, testScriptName, neId, dbcollectionFileName, networkConfigEntity);
					neOplog.append("CHECK :"+(i+1)+" STATUS: [PASS] "+"\n"+"[NEID: "+errorOp+"]"+"\n");					
					createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
					break;
					
				}
				TimeUnit.SECONDS.sleep(waitTime);
			}
			String exeFileString =neOplog.toString()+"\n";
			
			
			os.write(exeFileString.getBytes());
		} catch (Exception e) {
			logger.error("Exception NeUpStatusUtil in getNEUpResponse() " + ExceptionUtils.getFullStackTrace(e));
		}
		return shelloutput.toString();
	}
	
	private String getop(String fullOutputLog, String command, String enbId, String dbcollectionFileName, NetworkConfigEntity networkConfigEntity) {
		String invocationId = "";
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			
			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {
					Element element = (Element) inChildNode;
					invocationId = getXmlElementData(element, "ne-id").trim();
				}
			}
			
			
		} catch (Exception e) {
			logger.error("Exception getop in NeUpStatusUtil() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return invocationId;
	}
	private String geterrorop(String fullOutputLog, String command, String enbId, String dbcollectionFileName, NetworkConfigEntity networkConfigEntity) {
		String invocationId = "";
		try {
			String outputLog = StringUtils.substringAfter(fullOutputLog, command);
			outputLog = StringUtils.substringBefore(outputLog, XmlCommandsConstants.ENDTEXT5G);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			outputLog = "<parent>" + outputLog + "</parent>";

			Document document = builder.parse(new InputSource(new StringReader(outputLog)));
			
			NodeList nodeList = document.getElementsByTagName("rpc-reply");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node inChildNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == inChildNode.getNodeType()) {
					Element element = (Element) inChildNode;
					invocationId = getXmlElementData(element, "error-message").trim();
				}
			}
			
			
		} catch (Exception e) {
			logger.error("Exception geterrorop in NeUpStatusUtil() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return invocationId;
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
			logger.error("Exception getRequiredOutPut5GAudit in NeUpStatusUtil() "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		objBuilder.append(XmlCommandsConstants.ENDTEXT5G);
		objBuilder.append("\n");
		return objBuilder.toString();
	}
	public List<CIQDetailsModel> getCIQDetailsModelList(String enbId, String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("eNBId").is(enbId));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(
					"Exception getCIQDetailsModelList() in NeUpStatusUtil :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
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