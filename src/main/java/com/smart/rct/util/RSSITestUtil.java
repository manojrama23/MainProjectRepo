package com.smart.rct.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;

@Component
public class RSSITestUtil {

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
	
	
	public String getRSSIAudit(UploadFileEntity scriptEntity, String dataOutput, String neId,
			String dbcollectionFileName, NetworkConfigEntity networkConfigEntity, boolean useCurrPassword,
			String sanePassword, NetworkConfigEntity neEntity, String sProgramId, String migrationType,
			String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit,
			Map<String, Boolean> createHtmlMapboo, StringBuilder required5gOutput, AtomicBoolean isMultipleDUo) {
		
		// TODO Auto-generated method stub

		StringBuilder shelloutput = new StringBuilder();
		
		try {
			
			String testScriptName = XmlCommandsConstants.RSSI_USE_CASE;	
			String[] radioUnit = getRadioUnit(neId, ciqFileName, sProgramId);
/*			int size=radioUnit.length;
			String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName, newRadios,size);*/
			
			String testshellop = gettestOutput(scriptEntity, dataOutput, neId, dbcollectionFileName,
					networkConfigEntity, useCurrPassword, sanePassword, neEntity, sProgramId, migrationType,
					originalUseCaseName, ciqFileName, userName, outputFileNameAudit, testScriptName,isMultipleDUo); //, newRadios,size);
			
			//test
			//testshellop = returnshellop(testScriptName + ".xml");			
			
			if(testshellop.contains("DUO ERROR")) {
				shelloutput.append("\n" + testshellop);
				String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
						testScriptName + ".xml");
				required5gOutput.append(dataOutput1);
				required5gOutput.append("/n");
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
			}
			else if(testshellop.contains("RSSI IMBALANCE") || testshellop.contains("rssi")) {
				shelloutput.append("\n" + testshellop);
				
				String dataOutput1 = getRequiredOutPut5GAudit(testshellop,
						testScriptName + ".xml");
				required5gOutput.append(dataOutput1);
				required5gOutput.append("/n");
				
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), true);
			
			} else {
				createHtmlMapboo.put(FilenameUtils.removeExtension(scriptEntity.getFileName()), false);
			}
			
			} catch(Exception e) {
			logger.error("Exception RSSITestUtil in getRSSIAudit() " + ExceptionUtils.getFullStackTrace(e));
		}		
		return shelloutput.toString();

	}
	
		public String gettestOutput(UploadFileEntity scriptEntity,String dataOutput, String neId, String dbcollectionFileName, NetworkConfigEntity networkConfigEntity2, 
			boolean useCurrPassword, String sanePassword, NetworkConfigEntity networkConfigEntity, String sProgramId, String migrationType, 
			String originalUseCaseName, String ciqFileName, String userName, String outputFileNameAudit, String testScriptName, AtomicBoolean isMultipleDUo) {//, List <String> newRadios, int size) {
		
				String rssiOutput = "";

				try {
				
					JSONObject duoData = isDuoApplicable(scriptEntity, networkConfigEntity, useCurrPassword, sanePassword);
					if(duoData.containsKey("status") && duoData.get("status").equals(Constants.SUCCESS)) {
						String rssiUserName = "";
						String rssiPassword = "";
						String timeout = "";
						String expect1 = "";
						String expect2 = "";
						String expect3 = "";
						String expect4 = "";
						String command3 = "";
						String command4 = "";
						String expectDelay = "";
						String path = "";
						String fileName = "";
						
						List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_USERNAME);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							rssiUserName = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_PASSWORD);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							rssiPassword = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_SSH_LOGIN_TIMEOUT);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							timeout = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_EXPECT_1);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							expect1 = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_EXPECT_2);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							expect2 = auditConstantsList.get(0).getParameterValue();
						}
//						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
//								AuditConstants.RSSI_EXPECT_3);
//						
//						if(!ObjectUtils.isEmpty(auditConstantsList)) {
//							expect3 = auditConstantsList.get(0).getParameterValue();
//						}
//						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
//								AuditConstants.RSSI_EXPECT_4);
//						
//						if(!ObjectUtils.isEmpty(auditConstantsList)) {
//							expect4 = auditConstantsList.get(0).getParameterValue();
//						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_EXPECT_DELAY);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							expectDelay = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_EXIT_COMMAND1);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							command3 = auditConstantsList.get(0).getParameterValue();
						}
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_USM_LIVE,
								AuditConstants.RSSI_EXIT_COMMAND2);
						
						if(!ObjectUtils.isEmpty(auditConstantsList)) {
							command4 = auditConstantsList.get(0).getParameterValue();
						}
						
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
	                            AuditConstants.PROGRAMNAME_4G_USM_LIVE, AuditConstants.RSSI_PATH);
						
						if (!ObjectUtils.isEmpty(auditConstantsList)) {
		                    path = auditConstantsList.get(0).getParameterValue().trim();
		                }
						 
						auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(
		                            AuditConstants.PROGRAMNAME_4G_USM_LIVE, AuditConstants.RSSI_FILE_NAME);
							
						if (!ObjectUtils.isEmpty(auditConstantsList)) {
			                 fileName = auditConstantsList.get(0).getParameterValue().trim();
			            }
										
						duoData.put("rssiUserName", rssiUserName);
						duoData.put("rssiPassword", rssiPassword);
						duoData.put("timeout", timeout);
						duoData.put("expect1", expect1);
						duoData.put("expect2", expect2);
						duoData.put("expect3", expect3);
						duoData.put("expect4", expect4);
						duoData.put("vsmUser", networkConfigEntity.getNeUserName());
						duoData.put("vsmpassword", networkConfigEntity.getNePassword());
						duoData.put("vsmIp", networkConfigEntity.getNeIp());
						duoData.put("rssiIp", networkConfigEntity.getNeRsIp());
//						duoData.put("expect5", networkConfigEntity2.getNeSuperUserPrompt());
						duoData.put("expectDelay", expectDelay);
						duoData.put("command3", command3);
						duoData.put("command4", command4);
						duoData.put("neID",neId);
						//rssi
						//duoData.put("radioUnits", newRadios);
						duoData.put("path", path);
						duoData.put("filename", fileName);
						
//						rssiOutput = duoSession(userName, outputFileNameAudit,
//								scriptEntity.getFileName(), duoData,size);
						if(isMultipleDUo.get()) {
						rssiOutput = duoSessionNEID(userName, outputFileNameAudit,
								scriptEntity.getFileName(), duoData,neId,sProgramId);
						}else {
							rssiOutput = duoSession(userName, outputFileNameAudit,
									scriptEntity.getFileName(), duoData);
						}
					}
						
				} catch(Exception e) {
						logger.error("Exception RSSITestUtil in gettestOutput() " + ExceptionUtils.getFullStackTrace(e));
					}
					
					return rssiOutput;
						
			}
					
			private String duoSessionNEID(String user, String outputFileName, String scriptName, JSONObject duoData, String neId2, String sProgramId) {
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
					//path = duoData.get("path").toString();
					
                    //String path = "";
                   
					//path = "2,1,31,1,3,1,1";
					path = duoData.get("path").toString();
					userPrompt = duoData.get("userPrompt").toString();
					superUserPrompt = duoData.get("superUserPrompt").toString();
					UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
					String exeFileString = "\nExecuting " + scriptName + "\n";
					output.append(exeFileString);
					OutputStream os = new FileOutputStream(outputFileName, true);
					os.write(exeFileString.getBytes());
					// Thread.sleep(1000);

					// System.out.println("------------------Session IN Creation : " +
					// GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName()));
					String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId2;
					if (!GlobalStatusMap.socketSessionInCreation.containsKey(userName_NeId)) {
						GlobalStatusMap.socketSessionInCreation.put(userName_NeId, false);
					}
					while (GlobalStatusMap.socketSessionInCreation.get(userName_NeId)) {
						Thread.sleep(5000);
					}
					if (GlobalStatusMap.socketSessionUser.containsKey(userName_NeId)) {
						SocketSession ses = GlobalStatusMap.socketSessionUser.get(userName_NeId);
						if(ses.isConnectedSession()) {
							while(ses.getIsSessionInUse()) {
								Thread.sleep(5000);
							}
							ses.setIsSessionInUse(true);
							
							String vsmUser = duoData.get("vsmUser").toString();
							String vsmpassword = duoData.get("vsmpassword").toString();
							String vsmIp = duoData.get("vsmIp").toString();
							String rssiUserName = duoData.get("rssiUserName").toString();
							String rssiPassword = duoData.get("rssiPassword").toString();
							String rssiIp = duoData.get("rssiIp").toString();
							long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
							String expect1 = duoData.get("expect1").toString();
							String expect2 = duoData.get("expect2").toString();
							String expect3 = duoData.get("expect3").toString();
							String expect4 = duoData.get("expect4").toString();
							String command3 = duoData.get("command3").toString();
							String command4 = duoData.get("command4").toString();
							String neId = duoData.get("neID").toString();
							String fileName = duoData.get("filename").toString();
							//rssi
							//List <String> newRadios = (List<String>) duoData.get("radioUnits");
							
							long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
							
//							result = ses.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4, timeout, 
//									expectDelay, command3, command4, neId, newRadios,size, fileName);
							
							result = ses.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4, timeout, 
									expectDelay, command3, command4, neId, fileName);
							
							
							ses.setIsSessionInUse(false);
						} else {
							ses.disconnectSession();
							result = connectDuoServerNEID(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, duoData,neId2);
						}
					} else {
						result = connectDuoServerNEID(userDetailsEntity, serverIp, password, useCurrPassword, path, userPrompt, superUserPrompt, duoData,neId2);
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

			private String connectDuoServerNEID(UserDetailsEntity userDetailsEntity, String serverIp, String sanepassword, boolean useCurrPassword, String path,
					String userPrompt, String superUserPrompt, JSONObject duoData, String neId2) {
				String result = "";
				String userName_NeId = userDetailsEntity.getVpnUserName() + "_" + neId2;
				try {
					
					if(GlobalStatusMap.socketSessionInCreation.containsKey(userName_NeId)){
						GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, true);
					} else {
						GlobalStatusMap.socketSessionInCreation.put(userName_NeId, true);
					}
					
					String user = userDetailsEntity.getVpnUserName();
					String password = null;
					
					if(useCurrPassword) {
						password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
					} else {
						password = sanepassword;
					}
					
					SocketSession sockets = new SocketSession(user, password, serverIp);
					JSONObject sessionResult = sockets.connectSessionRSSI(path, userPrompt, superUserPrompt);
					
					if(sessionResult.get("status").toString().equals(Constants.SUCCESS)) {
						if(sockets.isConnectedSession()) {
							String sessionOutput = "";
							if(sessionResult.containsKey("expectOutput")) {
								sessionOutput = sessionResult.get("expectOutput").toString();
								
								String vsmUser = duoData.get("vsmUser").toString();
								String vsmpassword = duoData.get("vsmpassword").toString();
								String vsmIp = duoData.get("vsmIp").toString();
								String rssiUserName = duoData.get("rssiUserName").toString();
								String rssiPassword = duoData.get("rssiPassword").toString();
								String rssiIp = duoData.get("rssiIp").toString();
								long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
								long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
								String expect1 = duoData.get("expect1").toString();
								String expect2 = duoData.get("expect2").toString();
								String expect3 = duoData.get("expect3").toString();
								String expect4 = duoData.get("expect4").toString();
								String command3 = duoData.get("command3").toString();
								String command4 = duoData.get("command4").toString();
								String neId = duoData.get("neID").toString();
								String fileName = duoData.get("filename").toString();
								
								result = sockets.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4,
										timeout, expectDelay, command3, command4, neId, fileName);
								
								result = sessionOutput + "\n" + result;
								GlobalStatusMap.socketSessionUser.put(userName_NeId, sockets);			
							}
						}				
						GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
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
						GlobalStatusMap.socketSessionInCreation.replace(userName_NeId, false);
					}
					
				} catch(Exception e) {
					logger.error("Exception RSSITestUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
					GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
				}
				return result;
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
			
			private String getRequiredOutPut5GAudit(String shelloutput, String fileName) {

				StringBuilder objBuilder = new StringBuilder();

				try {
					String fileNameWoExten = FilenameUtils.removeExtension(fileName);
					objBuilder.append("\n");
					objBuilder.append(fileNameWoExten);
					objBuilder.append("\n");

					String endName = "";
					if (StringUtils.isNotEmpty(shelloutput) && (shelloutput.contains("RSSI IMBALANCE") || (shelloutput.contains("DUO ERROR")))) {
							objBuilder.append(shelloutput);
							objBuilder.append("\n");
					}

				} catch (Exception e) {
					logger.error("Exception RSSITestUtil in getRequiredOutPut5GAudit() "
							+ ExceptionUtils.getFullStackTrace(e));
				}
				
				objBuilder.append(XmlCommandsConstants.ENDTEXT5G); 
				objBuilder.append("\n");
				return objBuilder.toString();
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
					logger.error("Exception RSSITestUtil in isDuoApplicable() " + ExceptionUtils.getFullStackTrace(e));
					result.put("status", Constants.FAIL);
				}
				return result;
				
			}
			
			public String duoSession(String user, String outputFileName, String scriptName, JSONObject duoData) throws Exception{
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
					//path = duoData.get("path").toString();
					
                    //String path = "";
                   
					//path = "2,1,31,1,3,1,1";
					path = duoData.get("path").toString();
					userPrompt = duoData.get("userPrompt").toString();
					superUserPrompt = duoData.get("superUserPrompt").toString();
					UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user);
					String exeFileString = "\nExecuting " + scriptName + "\n";
					output.append(exeFileString);
					OutputStream os = new FileOutputStream(outputFileName, true);
					os.write(exeFileString.getBytes());
					// Thread.sleep(1000);

					// System.out.println("------------------Session IN Creation : " +
					// GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName()));
					if (!GlobalStatusMap.socketSessionInCreation.containsKey(userDetailsEntity.getVpnUserName())) {
						GlobalStatusMap.socketSessionInCreation.put(userDetailsEntity.getVpnUserName(), false);
					}
					while (GlobalStatusMap.socketSessionInCreation.get(userDetailsEntity.getVpnUserName())) {
						Thread.sleep(5000);
					}
					if (GlobalStatusMap.socketSessionUser.containsKey(userDetailsEntity.getVpnUserName())) {
						SocketSession ses = GlobalStatusMap.socketSessionUser.get(userDetailsEntity.getVpnUserName());
						if(ses.isConnectedSession()) {
							while(ses.getIsSessionInUse()) {
								Thread.sleep(5000);
							}
							ses.setIsSessionInUse(true);
							
							String vsmUser = duoData.get("vsmUser").toString();
							String vsmpassword = duoData.get("vsmpassword").toString();
							String vsmIp = duoData.get("vsmIp").toString();
							String rssiUserName = duoData.get("rssiUserName").toString();
							String rssiPassword = duoData.get("rssiPassword").toString();
							String rssiIp = duoData.get("rssiIp").toString();
							long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
							String expect1 = duoData.get("expect1").toString();
							String expect2 = duoData.get("expect2").toString();
							String expect3 = duoData.get("expect3").toString();
							String expect4 = duoData.get("expect4").toString();
							String command3 = duoData.get("command3").toString();
							String command4 = duoData.get("command4").toString();
							String neId = duoData.get("neID").toString();
							String fileName = duoData.get("filename").toString();
							//rssi
							//List <String> newRadios = (List<String>) duoData.get("radioUnits");
							
							long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
							
//							result = ses.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4, timeout, 
//									expectDelay, command3, command4, neId, newRadios,size, fileName);
							
							result = ses.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4, timeout, 
									expectDelay, command3, command4, neId, fileName);
							
							
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
					os.write(resultString.getBytes());
					output.append(resultString);
						
				} catch (Exception e) {
					logger.error("Exception RSSITestUtil in duoSession() " + ExceptionUtils.getFullStackTrace(e));
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
					
					String user = userDetailsEntity.getVpnUserName();
					String password = null;
					
					if(useCurrPassword) {
						password = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
					} else {
						password = sanepassword;
					}
					
					SocketSession sockets = new SocketSession(user, password, serverIp);
					JSONObject sessionResult = sockets.connectSessionRSSI(path, userPrompt, superUserPrompt);
					
					if(sessionResult.get("status").toString().equals(Constants.SUCCESS)) {
						if(sockets.isConnectedSession()) {
							String sessionOutput = "";
							if(sessionResult.containsKey("expectOutput")) {
								sessionOutput = sessionResult.get("expectOutput").toString();
								
								String vsmUser = duoData.get("vsmUser").toString();
								String vsmpassword = duoData.get("vsmpassword").toString();
								String vsmIp = duoData.get("vsmIp").toString();
								String rssiUserName = duoData.get("rssiUserName").toString();
								String rssiPassword = duoData.get("rssiPassword").toString();
								String rssiIp = duoData.get("rssiIp").toString();
								long timeout = NumberUtils.toLong(duoData.get("timeout").toString().trim());
								long expectDelay = NumberUtils.toLong(duoData.get("expectDelay").toString().trim());
								String expect1 = duoData.get("expect1").toString();
								String expect2 = duoData.get("expect2").toString();
								String expect3 = duoData.get("expect3").toString();
								String expect4 = duoData.get("expect4").toString();
								String command3 = duoData.get("command3").toString();
								String command4 = duoData.get("command4").toString();
								String neId = duoData.get("neID").toString();
								String fileName = duoData.get("filename").toString();
								//rssi
								/*List <String> newRadios = (List<String>) duoData.get("radioUnits");
								
								result = sockets.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4,
										timeout, expectDelay, command3, command4, neId, newRadios,size, fileName);*/
								
								result = sockets.getRSSITest(vsmUser, vsmpassword, rssiUserName, rssiPassword, vsmIp, rssiIp, expect1, expect2, expect3, expect4,
										timeout, expectDelay, command3, command4, neId, fileName);
								
								//String resultOutput = sockets.runCommand(command);
								result = sessionOutput + "\n" + result;
								GlobalStatusMap.socketSessionUser.put(userDetailsEntity.getVpnUserName(), sockets);			
							}
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
					logger.error("Exception RSSITestUtil in connectDuoServer() " + ExceptionUtils.getFullStackTrace(e));
					GlobalStatusMap.socketSessionInCreation.replace(userDetailsEntity.getVpnUserName(), false);
				}
				return result;
			}

	public String[] getRadioUnit(String enbId, String ciqFileName, String programId) {

		String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, ciqFileName);
		List<CIQDetailsModel> resultList = getCIQDetailsModelList(enbId, dbcollectionFileName);

		String sheetName = "";
		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);

		if (mkt.equals(Constants.VZ_GROW_UNY)) {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		} else if (mkt.equals(Constants.VZ_GROW_NE)) {
			sheetName = Constants.VZ_GROW_CIQNewEngland;
		} else {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
			mkt = Constants.VZ_GROW_UNY;
		}

		List<CIQDetailsModel> listOfCiqDetails = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
				dbcollectionFileName, sheetName, "eNBId");

		String lcc = "";
		String cpr = "";
		String r;
		Set<String> ra = new TreeSet<String>();
		ArrayList<String> al = new ArrayList<String>();
		if (!ObjectUtils.isEmpty(listOfCiqDetails)) {

			for (CIQDetailsModel ciqData : listOfCiqDetails) {

				// lccnum = tdData.get("connected-digital-unit-board-id");
				if (ciqData.getCiqMap().containsKey("CRPIPortID")) {
					cpr = ciqData.getCiqMap().get("CRPIPortID").getHeaderValue().trim();
				}
				if (ciqData.getCiqMap().containsKey("lCCCard")) {
					lcc = ciqData.getCiqMap().get("lCCCard").getHeaderValue().trim();
				}

				r = "RRH_" + lcc.toString() + "_" + cpr.toString() + "_0";
				ra.add(r);
			}
			// treeset to arraylist
			al.addAll(ra);

		}
		// arraylist to string array
		String[] radioU = new String[al.size()];

		for (int i = 0; i < al.size(); i++) {
			radioU[i] = al.get(i);
		}
		// radioU = ra.toArray(new String[ra.size()]);

		return radioU;

	}
			public List<CIQDetailsModel> getCIQDetailsModelList(String enbId, String dbcollectionFileName) {
				List<CIQDetailsModel> resultList = null;
				Query query = new Query();
				query.addCriteria(Criteria.where("eNBId").is(enbId));
				try {
					resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

				} catch (Exception e) {
					logger.error(
							"Exception getCIQDetailsModelList() in RSSITestUtil :" + ExceptionUtils.getFullStackTrace(e));

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
