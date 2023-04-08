package com.smart.rct.premigration.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.io.FileUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
//import org.codehaus.plexus.util.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.common.models.ErrorDisplayModel;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.controller.RunTestController;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.repository.UploadFileRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.repositoryImpl.UseCaseBuilderRepositoryImpl;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.service.ReportsService;
import com.smart.rct.premigration.dto.GenerateInfoAuditDto;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.repository.GenerateRepository;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.FsuTypeFecthService;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.premigration.serviceImpl.GenerateCsvServiceImpl;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PasswordCrypt;

import antlr.Version;

//aaaaaa
@RestController
public class GenerateCsvController {
	final static Logger logger = LoggerFactory.getLogger(GenerateCsvController.class);

	@Autowired
	GenerateCsvService objGenerateCsvService;

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	UploadFileService uploadFileService;
	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;
	@Autowired
	UploadFileRepository uploadFileRepository;

	@Autowired
	GenerateInfoAuditDto GenerateInfoAuditDto;

	@Autowired
	CustomerService customerService;

	@Autowired
	FileUploadService fileUploadService;

	@Autowired
	NeMappingService neMappingService;

	@Autowired
	RunTestController runTestController;

	@Autowired
	UseCaseBuilderRepositoryImpl useCaseBuilderRepositoryImpl;

	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	GenerateRepository generateRepository;
	
	@Autowired
	ReportsService reportService;
	
	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	
	@Autowired
	FetchProcessRepository fetchProcessRepository;
	
	@Autowired
	FetchProcessService fetchProcessService;
	
	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	
	@Autowired
	FsuTypeFecthService fsuTypeFecthService;
	
	@Autowired
	RunTestRepository runTestRepository;
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.GENERATE_FILE)
	public JSONObject generateFile(@RequestBody JSONObject ciqDetails) {

		String sessionId = null;
		String serviceToken = null;
		String ciqFileName = null;
		String enbName = null;
		String enbId = null;
		Integer programId = null;
		JSONObject expiryDetails = null;
		String fileType = null;
		Boolean status;
		//String enbstr="";
		List<Map<String,String>> obj= new ArrayList<>();
		// String wFMStatus = "false";
		String remarks = null;
		String neVersion = null;
		Boolean generateAllSites = null;
		String fsuType = "";
		String ErrorType = "";
		String ErrorType1 = "";
		List<Map<String, String>> enbList = null;
		Set<String> list=new LinkedHashSet<>();
		OvScheduledEntity ovScheduledEntity = null;
		List<OvScheduledEntity> ovScheduledEntityList = null;
		JSONObject scheduleObject = new JSONObject();
		String str3 = "";
		List<String> str2 = new ArrayList<>();
		JSONObject mapObject = new JSONObject();
		JSONObject reportsObject = new JSONObject();
		LinkedHashMap<String, String> finalResultMap = new LinkedHashMap<String, String>();
		String programName = null;
		//carrier add
		Boolean supportCA;
		boolean cbrsPref = false;
		boolean ovUpdate = false;
		if(ciqDetails.containsKey("ovUpdate")) {
			ovUpdate = (boolean) ciqDetails.get("ovUpdate");
		logger.error("OVupdate Key is "+ovUpdate );
		//System.out.println("OVupdate Key is ",Type2);
		}
		//dummy IP
		String integrationType = "Legacy IP";
		
		//fetching dummy IP
		if(ciqDetails.containsKey("integrationType") && null!=ciqDetails.get("integrationType")) {
			integrationType = ciqDetails.get("integrationType").toString();
		} else {
			integrationType = "Legacy IP";//PseudoIP
		}
		
		if(null!=ciqDetails.get("supportCA")) {
			supportCA = ciqDetails.get("supportCA").toString().equals("true") ? true : false;
		} else {
			supportCA = false;
		}
		/*if (StringUtils.isNotEmpty(integrationType)) {
			if ("LegacyIP".equals(integrationType)) {
				integrationType = "Legacy IP";
			} else if ("PseudoIP".equals(integrationType)) {
				integrationType = "Pseudo IP";
			}
		}
*/
		try {
			if (StringUtils.isNotEmpty((String) ciqDetails.get("wfmStatus"))) {
				if ("true".equalsIgnoreCase(ciqDetails.get("wfmStatus").toString())) {
					String wFMStatus = ciqDetails.get("wfmStatus").toString();
					remarks = "Generated through WFM";
				}
			}if (ciqDetails.containsKey("type") && ciqDetails.get("type").equals("OV")) {
				remarks = "Generated through Automation";
				
				if(ciqDetails.get("programName").toString().equals("VZN-4G-USM-LIVE")) {
					ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
					CustomerDetailsEntity customerDetailsEntity = new CustomerDetailsEntity();
					
						programId = Integer.parseInt(ciqDetails.get("programId").toString());
						customerDetailsEntity.setId(programId);
						programTemplateModel.setProgramDetailsEntity(customerDetailsEntity);;

					programTemplateModel.setConfigType("s&r");
					List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
							.getScheduledDaysDetails(programTemplateModel);
				
				String supportCACases = listProgramTemplateEntity.stream()
						.filter(entity -> Constants.SUPPORT_CA.equalsIgnoreCase(entity.getLabel()))
						.map(entity -> entity.getValue()).findFirst().get();
				
					if(supportCACases != null) {
						supportCA = supportCACases.toString().equalsIgnoreCase("ON") ? true : false;
					} else {
						supportCA = false;
					}
				
					String integrationTypeOV = "";
					if(null != integrationTypeOV) {
						integrationType = integrationTypeOV.toString().toLowerCase();
						integrationType = integrationTypeOV.toString().contains("pseudo") ? "Pseudo IP": "Legacy IP";
					}
					}
			} else {
				remarks = ciqDetails.get("remarks").toString();
			}
			if(ciqDetails.containsKey("type") && ciqDetails.get("type").equals("OV")
					&& ciqDetails.get("programName").toString().contains("4G-FSU")) 
			{
				fsuType="FSU20";
			}

			// enbName = ciqDetails.get("enbName").toString();
			fileType = ciqDetails.get("fileType").toString();
			programId = Integer.parseInt(ciqDetails.get("programId").toString());
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			programId = Integer.parseInt(ciqDetails.get("programId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			 List<Map<String, String>> enbID = (List<Map<String, String>>) ciqDetails.get("neDetails");

			if (CommonUtil.isValidObject(fileType)
					&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT) &&  ciqDetails.get("programName").toString().contains("VZN-4G-USM-LIVE")&& (enbID.size()==1)) {
				StringBuilder scriptUploadPath = new StringBuilder();
				StringBuilder uploadPaths = new StringBuilder();
				uploadPaths.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
				// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
				.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
				.append(Constants.SEPARATOR);
				scriptUploadPath.append(uploadPaths)
				.append(Constants.PRE_MIGRATION_SCRIPT
						.replace("filename", StringUtils.substringBeforeLast(ciqDetails.get("ciqFileName").toString(), "."))
						.replaceAll(" ", "_"));
				scriptUploadPath.append(enbID.get(0).get("neId"));
				File fp= new File(scriptUploadPath.toString());
				/*if(!fp.exists() || !fp.isDirectory()) {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason", "Associated RF Scripts are not Uploaded");
					return mapObject;
				}*/
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			programName = ciqDetails.get("programName").toString();
			enbList = (List<Map<String, String>>) ciqDetails.get("neDetails");
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			
			ciqFileName = ciqDetails.get("ciqFileName").toString();

			programId = Integer.parseInt(ciqDetails.get("programId").toString());
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			String pgmName = ciqDetails.get("programName").toString();
//			List<String> validateResut = validateNeidsGenerate(programId, enbList, dbcollectionFileName);
			
			List<String> validateResut = new ArrayList<>();
			
			/*if(null!=validateResut&&validateResut.size()>0) {
				for (String string : validateResut) {
//					neIds.join(",", string);
				}
				String neIds = String.join(",", validateResut);
				neIds=neIds+" validation failed for details check validate";
				mapObject.put("validateNeIdReport", neIds);
			}*/
			for (Map<String, String> enb : enbList) {
				Map<String, JSONObject> reportsMap = new LinkedHashMap<>();
				Map<String, String> scheduleMap = new LinkedHashMap<>();
				Map<String,String> map = new LinkedHashMap<>();
				enbId = enb.get("neId");
				enbName = enb.get("neName");
				mapObject.put("validateNeIdReport", "");
				mapObject.put("errorMsg", "");
				//
				if (StringUtils.isNotEmpty(enbName) && StringUtils.isNotEmpty(enbId)) {
					try {
						Map<String, Object> neMap = fileUploadService.validationEnbDetails(dbcollectionFileName,
								Integer.valueOf(programId), enbName, enbId);
						if (neMap != null && neMap.size() > 0 && neMap.containsKey("validationDetails")) {
							List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) neMap
									.get("validationDetails");
							if (objErrorMap.size() > 0) {
								validateResut.add(enbName);
								System.out.println(enbName);
								System.out.println("enbName"+enbName);
								if (null != validateResut && validateResut.size() > 0) {
									String neIds = String.join(",", validateResut);
									neIds = "Validation failed for bellow NE's " + neIds;
									String	errorMsg = "Validation failed for  NE : " + enbName+"."+objErrorMap.get(0).getErrorMessage();
									mapObject.put("errorMsg", errorMsg);
									mapObject.put("validateNeIdReport", neIds);
								}
								continue;
							}
						}
					} catch (Exception e) {
						logger.error("Validation failed NE's " + enbId);
					}

				}
				
				
				//
				
				reportsObject.put(enbId, reportsMap);
				scheduleObject.put(enbId, scheduleMap);
			
				StringBuilder uploadPath = new StringBuilder();

				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId);

				if (StringUtils.isNotEmpty(ciqFileName)) {
					File fileExist = new File(uploadPath.toString());
					if (!fileExist.exists()) {
						FileUtil.createDirectory(uploadPath.toString());
					}
					boolean fileGenerateStatus = false;
					JSONObject fileGenerateResult = new JSONObject();
					JSONObject envfileGenerateResult = new JSONObject();
					JSONObject mergeFile = new JSONObject();
					List<JSONObject> fileGenerateResultList = new ArrayList<JSONObject>();

					NeMappingModel neMappingModel = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					
					programDetailsEntity.setId(programId);
					neMappingModel.setProgramDetailsEntity(programDetailsEntity);
					neMappingModel.setEnbId(enbId);
					List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
					if (!CommonUtil.isValidObject(neMappingEntities) || neMappingEntities.size() <= 0) {
						mapObject.put("status", Constants.FAIL);
						mapObject.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
						
						map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
						map.put("enbId",enbId);
						obj.add(map);
						continue;
					}
					 if(fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						// boolean status = false;
						StringBuilder FetchPathFsu = new StringBuilder();
						StringBuilder FetchPath = new StringBuilder();
						StringBuilder FileWritePath = new StringBuilder();
						StringBuilder uploadPaths = new StringBuilder();
						StringBuilder uploadPath1 = new StringBuilder();
						StringBuilder uploadPath2 = new StringBuilder();
						StringBuilder uploadPathENDC = new StringBuilder();
						StringBuilder scriptUploadPath = new StringBuilder();
						StringBuilder scriptUploadPathENDC = new StringBuilder();
						uploadPath1.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId);
						FileWritePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId);
						uploadPath.append(Constants.PRE_MIGRATION_OFFLINE
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId));
						File fileExists = new File(uploadPath.toString());
						if (fileExists.exists()) {
							FileUtil.deleteFileOrFolder(uploadPath.toString());
							//FileUtil.createDirectory(uploadPath.toString());
						}
						if (!fileExists.exists())
						{
							FileUtil.createDirectory(uploadPath.toString());
						}
						FetchPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")+ "SRCT_OfflineAutomation/OfflineAutomation");
						
						File SourceFile = new File(FetchPath.toString());
						File DSourceFile = new File(uploadPath.toString());
						try {
						FileUtils.copyDirectoryToDirectory(SourceFile,DSourceFile);
						}catch(Exception e) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", " SRCT_OfflineAutomation_Setup not found ");
							break;
						}
						if (ciqDetails.get("programName").equals("VZN-4G-FSU")) {
							// for fsu nemapping independent
							NeMappingEntity neMappingEntity = neMappingEntities.get(0);
							if (!CommonUtil.isValidObject(neMappingEntity)
									|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								
								map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							
							
							if(ciqDetails.containsKey("fsuType") && ciqDetails.get("fsuType")!=null) {
								fsuType = ciqDetails.get("fsuType").toString().trim();
							}
							List<CIQDetailsModel> listCIQDetailsModel = null;
							listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "FSUCIQ", "");
							if(!(fsuType.equals("FSU10") || fsuType.equals("FSU20"))) {
								
								String fsuIp = "";
								if(!ObjectUtils.isEmpty(listCIQDetailsModel) && listCIQDetailsModel.get(0).getCiqMap().containsKey("FSU_Mgmt_IPv6_Address")) {
									fsuIp = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue().trim();
								}
								if(fsuIp.isEmpty()) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", "Unable to Fetch FSU Mgmt IPv6 Address from CIQ");
									return mapObject;
								}
								//UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user.getUserName());
								JSONObject fsuTypeResult = fsuTypeFecthService.getFSUType(neMappingEntity, user.getUserName(), fsuIp);
								if(fsuTypeResult!=null && fsuTypeResult.containsKey("status") && fsuTypeResult.get("status").equals(Constants.SUCCESS)) {
									fsuType = fsuTypeResult.get("fsuType").toString();
								} else {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", "Unable to Fetch FSU Type from USM, Please Select FSU Type To Generate Templates");
									return mapObject;
								}
								//String ex = ses.getFsuLog("user", "root123", "user", "root123", "10.20.120.76", "10.9.68.109");
								//System.out.println(ex);
								
							}
							
							//fetching neVersion from ciq
						//	neVersion = listCIQDetailsModel.get(0).getCiqMap().get("NE_Version").getHeaderValue().trim();
						//	neVersion = neVersion.substring(0,neVersion.indexOf("-"));
							neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();

							
							if (fsuType.equals(Constants.FSU_TYPE_10)) {
								listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "FSU10", "");
							}
							if(ObjectUtils.isEmpty(listCIQDetailsModel)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "No Data for " + enbId + " in " + fsuType + " sheet");
								
								map.put("reason","No Data for " + enbId + " in " + fsuType + " sheet");
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							uploadPath2.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File ComfileExists = new File(uploadPath.toString());
							if (!ComfileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGenerationFSU(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGenerationNTPFSU(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
							
						}
							else {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								
								map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							mapObject.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.GENERATED_COMM_SCRIPT_SUCCESSFULLY));
							JSONObject neGrowResult = createExecutableFilesForNEGrowFSU(ciqFileName, programId, uploadPath2.toString(), neMappingEntity, enbId, enbName, sessionId, 
									"", programName, fileGenerateResultList);
							if(neGrowResult != null && neGrowResult.containsKey("status")
									&& neGrowResult.get("status").equals(Constants.FAIL)) {
								if(neGrowResult.containsKey("reason")) {
									mapObject.put("reason",neGrowResult.get("reason"));
									map.put("reason",neGrowResult.get("reason").toString());
								} else {
									System.out.println("---------------1-------------------");
									mapObject.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									map.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								}
								
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							reportsMap.put("Commission", fileGenerateResult);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_COMM_SCRIPT_SUCCESSFULLY));
							if (!(ciqDetails.get("programName").equals("VZN-5G-DSS") || programName.contains("5G-CBAND"))) {
								for (JSONObject object : fileGenerateResultList) {
									if (object.get("status") != null && (boolean) object.get("status")) {
										fileGenerateStatus = true;
									} else if (object.get("status") != null && !(boolean) object.get("status")) {
										fileGenerateStatus = false;
										break;
									}
								}

								if (fileGenerateStatus) {
									String siteName = "";
									String eNBName = (String) enb.get("neName");
									
									JSONObject doZipResultObject = doZipAndSaveFSU(sessionId, fileType, uploadPath.toString(),
											ciqFileName, enbName, remarks, programDetailsEntity, fileGenerateResultList,programName,siteName);
									/*
									 * fileGenerateResultList.clear(); uploadPath.setLength(0);
									 */
									if (doZipResultObject != null && doZipResultObject.containsKey("status")
											&& doZipResultObject.get("status").equals(Constants.FAIL)) {
										System.out.println("-------------2-----------");
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason",
												GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
										map.put("reason",
												GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}

								finalResultMap.put(enbId, "Success");
								ArrayList<String> zipFileName=new ArrayList<String>();
								ArrayList<String> fPath=new ArrayList<String>();
								fPath.add(doZipResultObject.get("fPath").toString());
								zipFileName.add(doZipResultObject.get("zipFileName").toString());
								String fileName =doZipResultObject.get("zipFileName").toString();
								String filepathCom =doZipResultObject.get("fPath").toString();
								mapObject.put("zipFileName",zipFileName);
								mapObject.put("fPath",fPath);
								mapObject.put("enb", enbId);
								FetchPathFsu.append(filepathCom).append(fileName);
								finalResultMap.put(enbId, "Success");
								mapObject.put("CombinedResult", finalResultMap);
								mapObject.put("status", Constants.SUCCESS);
								commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_GENERATE,
										Constants.ACTION_GENERATE,
										"File Generated Successfully For NE: " + enbId + ", Type: " + fileType, sessionId);
								} else {
									System.out.println("------------3---------------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									break;
								}
							}
						}
						fileGenerateResultList.clear();
						fileGenerateResult.clear();
						uploadPaths.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
						.append(Constants.SEPARATOR);
						scriptUploadPath.append(uploadPaths).append(Constants.PRE_MIGRATION_SCRIPT
						.replace("filename", StringUtils.substringBeforeLast(ciqDetails.get("ciqFileName").toString(), ".")).replaceAll(" ", "_"));
						
						scriptUploadPath.append(enbId + ".zip");
						scriptUploadPathENDC.append(uploadPaths)
						.append(Constants.PRE_MIGRATION_SCRIPT
								.replace("filename", StringUtils.substringBeforeLast(ciqDetails.get("ciqFileName").toString(), "."))
								.replaceAll(" ", "_"));
						scriptUploadPathENDC.append(enbId + "_ENDC.zip");
						uploadPathENDC.append(uploadPath1);
						File fpENDC= new File(scriptUploadPathENDC.toString());
						File fp= new File(scriptUploadPath.toString());
						File fsuCom= new File(FetchPathFsu.toString());
						if(fp.exists() || fp.isDirectory() ||fsuCom.exists()|| fsuCom.isDirectory() ) {
							uploadPath1.append(Constants.PRE_MIGRATION_OFFLINE
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId))
							.append("OfflineAutomation/").append("SRCT_xml/").append(enbId + ".zip");
							File SourceFile1 = new File(scriptUploadPath.toString());
							File DSourceFile1 = new File(uploadPath1.toString());
							File DSourceFile3 = new File(FetchPathFsu.toString());
							//FileUtils.copyDirectory(SourceFile1,DSourceFile1);
							if(programName.contains("VZN-4G-FSU")) {
								Files.copy(DSourceFile3.toPath(), DSourceFile1.toPath(),StandardCopyOption.REPLACE_EXISTING);
							}else {
							Files.copy(SourceFile1.toPath(), DSourceFile1.toPath(),StandardCopyOption.REPLACE_EXISTING);
							}
							status=true;
							fileGenerateResult.put("fileName", "OfflineAutomation");
							fileGenerateResult.put("status", status);
							fileGenerateResultList.add(fileGenerateResult);

						}else {
							fileGenerateResult.put("status", Constants.FAIL);
							fileGenerateResult.put("reason", "Script File are not uploaded");
							//fileGenerateResultList.add(fileGenerateResult);
							if(!programName.contains("VZN-4G-USM") &&!programName.contains("VZN-5G-MM")) { 
								mapObject.put("reason", "RF Script File are not uploaded");
								mapObject.put("status", Constants.FAIL);
								break;
							}
							if(programName.contains("VZN-4G-USM")) {
							ErrorType="RF_Script";
							}
						}
						
						if(programName.contains("VZN-4G-USM")) {
							if(fpENDC.exists()) {
							uploadPathENDC.append(Constants.PRE_MIGRATION_OFFLINE
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId))
							.append("OfflineAutomation/").append("SRCT_xml/").append(enbId + "_ENDC.zip");
							File SourceFile2 = new File(scriptUploadPathENDC.toString());
							File DSourceFile2 = new File(uploadPathENDC.toString());
							//FileUtils.copyDirectory(SourceFile1,DSourceFile1);
							Files.copy(SourceFile2.toPath(), DSourceFile2.toPath(),StandardCopyOption.REPLACE_EXISTING);
							fileGenerateResultList.removeAll(fileGenerateResultList);
							status=true;
							fileGenerateResult.put("fileName", "OfflineAutomation");
							fileGenerateResult.put("status", status);
							fileGenerateResultList.add(fileGenerateResult);
							}
							else {
								ErrorType="ENDC_Script";
								fileGenerateResultList.removeAll(fileGenerateResultList);
								status=true;
								fileGenerateResult.put("fileName", "OfflineAutomation");
								fileGenerateResult.put("status", status);
								fileGenerateResultList.add(fileGenerateResult);
							}
							
						}
						if (programName.contains("VZN-4G-USM")) {
							if(!fpENDC.exists()&& !fp.exists()) {
								ErrorType="";
								mapObject.put("reason", "RF and ENDC Script File are not uploaded");
								mapObject.put("status", Constants.FAIL);
								break;
							}
						}
						
						if(!fileGenerateResult.isEmpty() && fileGenerateResult.containsKey("status")
								&& fileGenerateResult.get("status").equals(true)) {
						//create json object and put the value
						String filepath=FileWritePath.append(Constants.PRE_MIGRATION_OFFLINE
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId))
						.append("OfflineAutomation/").append("SRCT_config/").append("config.json").toString();
					    String mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
						String IP = "[" + mcmip +"]" +":7443";
						JSONObject jsonObject =new JSONObject();
						jsonObject.put("program_Id",programId );
						jsonObject.put("program_Name",programName );
						jsonObject.put("ip_address",IP);
						
						try {
							FileWriter filewriter = new FileWriter(filepath);
							filewriter.write(jsonObject.toString());
							filewriter.flush();
							filewriter.close();
						}
						catch(Exception e){
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", "Fail to write the Config.json");
							}
						}
						
					}

					// For DSS Rf script generation
					if ((ciqDetails.get("programName").toString().equals("VZN-5G-DSS") ||
							programName.contains("5G-CBAND"))&& !fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						NeMappingEntity neMappingEntity = neMappingEntities.get(0);
						//reports.setMarket(listCIQDetailsModelll.get(0).getCiqMap().get("Market").getHeaderValue());							
						if (!CommonUtil.isValidObject(neMappingEntity)
								|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							
							map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();
						JSONObject result = null;
						if (programName.contains("5G-CBAND") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
							uploadPath.append(Constants.PRE_MIGRATION_TEMPLATE
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							fileGenerateResult = objGenerateCsvService.csvGenerationCBand(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUpnp", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);
							scheduleMap.put("vDUpnpGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUpnp", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationCBandCell(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUcell", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("vDUcellGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUcell", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationCBandADPF(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUgrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("vDUGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUgrow", fileGenerateResult);
								
							
						}
						else if (programName.contains("5G-DSS") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
							uploadPath.append(Constants.PRE_MIGRATION_TEMPLATE
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							fileGenerateResult = objGenerateCsvService.csvGenerationDSS(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"pnpGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("pnpGrow", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationDSSAu(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUCellGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUCellGrow", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationDSSAuPf(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUGrow", fileGenerateResult);
							
								
							
							
						}

						else if (programName.contains("5G-DSS") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.ALL_5g)) {
							uploadPath.append(Constants.PRE_MIGRATION_All
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							fileGenerateResult = objGenerateCsvService.csvGenerationDSS(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"pnpGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("pnpGrow", fileGenerateResult);
							//result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									//uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationDSSAu(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUCellGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUCellGrow", fileGenerateResult);
							//result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									//uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationDSSAuPf(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUGrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUGrow", fileGenerateResult);
							
							result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
									
								
							
						}

						
						else if (programName.contains("5G-CBAND") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.ALL_5g)) {
							uploadPath.append(Constants.PRE_MIGRATION_All
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							fileGenerateResult = objGenerateCsvService.csvGenerationCBand(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUpnp", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);
							scheduleMap.put("vDUpnpGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUpnp", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationCBandCell(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUcell", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("vDUcellGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUcell", fileGenerateResult);
							
							fileGenerateResult = objGenerateCsvService.csvGenerationCBandADPF(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"vDUgrow", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							scheduleMap.put("vDUGrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("vDUgrow", fileGenerateResult);
							
							result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
							/*fileGenerateResult = objGenerateCsvService.csvGenerationCBand(ciqFileName, enbId, enbName, dbcollectionFileName, programId,
									uploadPath.toString(), sessionId,"cell", neMappingEntity, remarks);
							fileGenerateResultList.add(fileGenerateResult);	
							result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
							//reportsMap.put("CBAND", result);
							scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("cell", fileGenerateResult);*/
								
						} else {
							result = createExecutableFiles5GDSS(ciqFileName, Integer.valueOf(programId),
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
						}						
						if(!(programName.contains("5G-CBAND")||programName.contains("5G-DSS") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV))) {
							reportsMap.put("DSS", result);
						}						

						if (result != null && result.containsKey("status")
								&& result.get("status").equals(Constants.SUCCESS)) {
							mapObject.put("status", Constants.SUCCESS);
							mapObject.put("reason", "Rf Scripts Generated Successfully");
							
							commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
									Constants.EVENT_PRE_MIGRATION_RAN_CONFIG, Constants.ACTION_GENERATE,
									"Files Generated Successfully For: " + enbId, sessionId);
						} else if (result != null && result.containsKey("status")
								&& result.get("status").equals(Constants.FAIL)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", result.get("reason"));
							
							
							
							
							map.put("reason",result.get("reason").toString());
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						} else if (!(programName.contains("5G-CBAND")  || programName.contains("5G-DSS") && CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV))){
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", "Rf Scripts Generation Failed");
							map.put("reason","Rf Scripts Generation Failed");
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}
						if(!ObjectUtils.isEmpty(fileGenerateResultList)) {
							JSONObject neGrowResult = createExecutableFilesForNEGrowCBAND(ciqFileName, programId, uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, 
									"", programName, fileGenerateResultList);
							
							if(neGrowResult != null && neGrowResult.containsKey("status")
									&& neGrowResult.get("status").equals(Constants.FAIL)) {
								if(neGrowResult.containsKey("reason")) {
									mapObject.put("reason",neGrowResult.get("reason"));
									map.put("reason",neGrowResult.get("reason").toString());
								} else {
									System.out.println("--------4-----------");
									mapObject.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									map.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								}
								
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							JSONObject doZipResultObject = doZipAndSaveAudit(sessionId, fileType, uploadPath.toString(),
									ciqFileName, enbName, remarks, programDetailsEntity, fileGenerateResultList,programName,"",integrationType);
							if (doZipResultObject != null && doZipResultObject.containsKey("status")
									&& doZipResultObject.get("status").equals(Constants.FAIL)) {
								System.out.println("----------5-----------------");
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							ArrayList<String> zipFileName=new ArrayList<String>();
							ArrayList<String> fPath=new ArrayList<String>();
							fPath.add(doZipResultObject.get("fPath").toString());
							zipFileName.add(doZipResultObject.get("zipFileName").toString());
							mapObject.put("zipFileName",zipFileName);
							mapObject.put("fPath",fPath);
							mapObject.put("enb", enbId);

							if(fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
								mapObject.put("status", Constants.SUCCESS);
								mapObject.put("reason", "Grow Template Generated Successfully");
							} else if (fileType.equalsIgnoreCase(Constants.ALL_5g)) {
								mapObject.put("status", Constants.SUCCESS);
								mapObject.put("reason", "RF Scripts and Grow Template Generated Successfully");
							}
						}
					}

					// for fsu indpendent nemapping
					if (!(ciqDetails.get("programName").equals("VZN-4G-FSU"))
							&& !(ciqDetails.get("programName").equals("VZN-5G-USM-LIVE"))
							&& !(ciqDetails.get("programName").equals("VZN-5G-SNAP-5G-NR"))
							&& !(ciqDetails.get("programName").equals("VZN-5G-MM"))
							&& !(ciqDetails.get("programName").equals("VZN-5G-DSS"))
							&& !(ciqDetails.get("programName").toString().contains("5G-CBAND"))
							&& !fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						NeMappingEntity neMappingEntity = neMappingEntities.get(0);

						if (!CommonUtil.isValidObject(neMappingEntity)
								|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							
							map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}

						if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
														JSONObject scriptSeqResult = getScriptSequenceDetails(ciqFileName, programId,
									neMappingEntity, enbId, enbName, sessionId, serviceToken);
							if (scriptSeqResult != null && scriptSeqResult.containsKey("status")
									&& scriptSeqResult.get("status").equals(Constants.FAIL)) {
								return scriptSeqResult;
							}
						}

						if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)) {
							uploadPath.append(Constants.PRE_MIGRATION_ENV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
						
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
								// fileGenerateStatus =
								// objGenerateCsvService.commissionScriptFileGeneration(ciqFileName, enbId,
								// enbName, dbcollectionFileName, programId, uploadPath.toString(),
								// sessionId,Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4, neMappingEntity,
								// remarks);
								fileGenerateResult.put("status", true);
								fileGenerateResultList.add(fileGenerateResult);
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								fileGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env",
										neMappingEntity, remarks, supportCA);
								scheduleMap.put("EnvFileName", fileGenerateResult.get("fileName").toString());
								scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								reportsMap.put("env", fileGenerateResult);
								fileGenerateResultList.add(fileGenerateResult);
								//dummy env file
								if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
									fileGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
											neMappingEntity, remarks, supportCA);
									fileGenerateResultList.add(fileGenerateResult);
									scheduleMap.put("DummyEnvFileName", fileGenerateResult.get("fileName").toString());
									reportsMap.put("envDummy", fileGenerateResult);
//									filemane = fileGenerateResult.get("fileName").toString();
//									logger.error("this is the dummy env file name " +filemane);
								}															
								//
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env",
										neMappingEntity, remarks, supportCA);
								fileGenerateResultList.add(fileGenerateResult);
								reportsMap.put("env", fileGenerateResult);
								if(LoadPropertyFiles.getInstance().getProperty("ciqType").equals("NEW")) {
								fileGenerateResult = objGenerateCsvService.cpriFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								}
								scheduleMap.put("EnvFileName", fileGenerateResult.get("fileName").toString());
								String filemane = fileGenerateResult.get("fileName").toString();
								logger.error("this is the env file name " +filemane);
								//dummy env file
								if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
									fileGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
											neMappingEntity, remarks, supportCA);
									fileGenerateResultList.add(fileGenerateResult);
									scheduleMap.put("DummyEnvFileName", fileGenerateResult.get("fileName").toString());
									filemane = fileGenerateResult.get("fileName").toString();
									reportsMap.put("envDummy", fileGenerateResult);
									logger.error("this is the dummy env file name " +filemane);
								}															
								//
								scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								

							}
							
							
							
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_ENV_SUCCESSFULLY));
						} else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
							uploadPath.append(Constants.PRE_MIGRATION_CSV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							//commented as requested by client
							/*JSONObject dspIDCheck = checkDuplicateDSP(Integer.toString(programId), ciqFileName,
									dbcollectionFileName, sessionId, serviceToken, enbId, enbName);
							if (dspIDCheck.containsKey("result") && dspIDCheck.get("result").equals("empty")) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "Grow Fail - CPRI Values are not proper");
								
								map.put("reason","Grow Fail - CPRI Values are not proper");
								map.put("enbId",enbId);
								obj.add(map);
								continue;
								
							}
							
							if(dspIDCheck.containsKey("status") && dspIDCheck.get("status").equals("FAILED") )
							{
								
									map.put("reason",dspIDCheck.get("reason").toString());
									map.put("enbId",enbId);
									obj.add(map);
									continue;
								
							}*/
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							boolean check = true;
							///boolean ptpCheck= true;
							StringBuffer ptpCheck = new StringBuffer();
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
								if (!ptpCheck.toString().isEmpty() && null!= ptpCheck.toString()) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", ptpCheck.toString()+" For Selected Enb is not present in the Ciq.");
									return mapObject;
								}
								fileGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"enb_cell",
										 neMappingEntity, remarks, supportCA, integrationType);
								fileGenerateResultList.add(fileGenerateResult);
								scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
								scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								reportsMap.put("enb", fileGenerateResult);
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
								if (!ptpCheck.toString().isEmpty()) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", ptpCheck.toString()+" For Selected Enb is not configured in the Ciq");
									return mapObject;
								}
								fileGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"enb",
										 neMappingEntity, remarks, supportCA, integrationType); //dummyIP
								fileGenerateResultList.add(fileGenerateResult);
								scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
								scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								reportsMap.put("enb", fileGenerateResult);
								if (!ciqDetails.get("programName").equals("VZN-4G-FSU")&& 
										(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.A.0") 
												|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")
												|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0")
												|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0")
												|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")
												|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0"))
										&& LoadPropertyFiles.getInstance().getProperty("ciqType").equals("NEW")) {
									//uncommented as requested by client for 21.A.0
									List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
											enbName, dbcollectionFileName);
									boolean check2 = objGenerateCsvService.checkEnbExistence(listCIQDetailsModel,Constants.VZ_GROW_CIQUpstateNY);
									if(!check2) {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", "Selected Enb is not present in the Ciq");
										return mapObject;
									}
									check= objGenerateCsvService.checkEmptyValues(listCIQDetailsModel,Constants.VZ_GROW_CIQUpstateNY);
									if(check) {
									fileGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
											"cell", neMappingEntity, remarks, supportCA, integrationType);
								String AdState=fileGenerateResult.get("AdNew").toString();
									if(fileGenerateResult.containsKey("stat") && fileGenerateResult.get("stat").equals("fail")) {
										cbrsPref = true;
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", "Failed to generate GrowCell");
										map.put("reason","Failed to generate GrowCell");
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}
									if(fileGenerateResult.containsKey("cbrsPref")) {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", "Failed to generate cell grow : CBRS preference information is not populated in CIQ");
										map.put("reason","Failed to generate cell grow : CBRS preference information is not populated in CIQ");
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}
									logger.error("before merge file111:********"+enbId);
									mergeFile = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName,
											programId);
									
									fileGenerateResultList.add(mergeFile);
									fileGenerateResultList.add(fileGenerateResult);
									
									 logger.error("file name send for negrow");
									JSONObject fileProcessResult1 = createExecutableFilesForNEGrow(ciqFileName,
											programId, uploadPath.toString(), neMappingEntity, enbId, enbName,
											sessionId, "single",ciqDetails.get("programName").toString(), fileGenerateResultList);
									reportsMap.put("cell", fileGenerateResult);
									reportsMap.put("merge", mergeFile);
									if(supportCA.equals(true) && AdState.equals("New")) { 
									JSONObject result = createExecutableFilesCA(ciqFileName, Integer.valueOf(programId),
											uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);

									if (result != null && result.containsKey("status")
											&& result.get("status").equals(Constants.SUCCESS)) {
										mapObject.put("status", Constants.SUCCESS);
										mapObject.put("reason", "CA Scripts Generated Successfully");
										
										commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
												Constants.EVENT_PRE_MIGRATION_RAN_CONFIG, Constants.ACTION_GENERATE,
												"Files Generated Successfully For: " + enbId, sessionId);
									}else if (result != null && result.containsKey("status")
											&& (result.get("status").equals(Constants.FAIL) && result.get("reason").equals(" No CA Scripts fileName between the range")) ){
										mapObject.put("status", Constants.SUCCESS);
										mapObject.put("reason1", "CA scripts not found");
										
										commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
												Constants.EVENT_PRE_MIGRATION_RAN_CONFIG, Constants.ACTION_GENERATE,
												"Files Generated Successfully For: " + enbId, sessionId);
									}
									
									else if (result != null && result.containsKey("status")
											&& result.get("status").equals(Constants.FAIL)) {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", result.get("reason"));
										
										
										
										
										map.put("reason",result.get("reason").toString());
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}
									}
									}
								}
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
								fileGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"enb_cell", neMappingEntity, remarks, supportCA, integrationType);
								fileGenerateResultList.add(fileGenerateResult);
								scheduleMap.put("growFileName", fileGenerateResult.get("fileName").toString());
								scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								reportsMap.put("enb", fileGenerateResult);
							} else {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
								
							}
							if(check) {
								if(mapObject.containsKey("reason1")) {
								String reason1=	mapObject.get("reason1").toString();
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY)+","+reason1);
								}else {
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY));
								if(cbrsPref) {
									mapObject.put("reason",
											"Failed to generate cell grow : CBRS preference information is not populated in CIQ");
								}									
								if("Legacy IP".equals(integrationType)) {
									mapObject.put("status", Constants.SUCCESS);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY)
											+"$Migration Strategy : Legacy IP");
								} else {
									if(GenerateCsvServiceImpl.getIpPresent()) {
										mapObject.put("status", Constants.SUCCESS);
										mapObject.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY)
												+"$Migration Strategy : Pseudo IP");
									} else {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", "File cannot be generated. Pseudo IP is not present in CIQ" );
									}
								}
								}
							}
							else
							{
								System.out.println("-------A---------");
								mapObject.put("reason",
										"Failed to Generate Cell Grow Template");
								return mapObject;
							}
						} else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
							uploadPath.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script))) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT1, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT2, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT3, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT5, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT6, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT7, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT8, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT9, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT10, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT11, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity,
										remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_MME, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_ENV, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_COMM, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								
									
								String mcmip = "";
								if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
										&& CommonUtil.isValidObject(
												neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
										&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
												.getId() == Constants.NW_CONFIG_VLSM_ID
										&& CommonUtil.isValidObject(
												neMappingEntity.getNetworkConfigEntity().getNeDetails())) {
									for (NetworkConfigDetailsEntity detailsEntity : neMappingEntity
											.getNetworkConfigEntity().getNeDetails()) {
										if (CommonUtil.isValidObject(detailsEntity.getServerTypeEntity())) {
											if (detailsEntity.getServerTypeEntity()
													.getId() == Constants.NW_CONFIG_VLSM_MCMA_ID
													&& CommonUtil.isValidObject(detailsEntity.getServerIp())
													&& detailsEntity.getServerIp().length() > 0) {
												mcmip = detailsEntity.getServerIp();
												break;
											}
										}
									}
								}

								if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
										&& CommonUtil.isValidObject(
												neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
										&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
												.getId() == Constants.NW_CONFIG_USM_ID) {
									mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
									if (!CommonUtil.isValidObject(mcmip) || !(mcmip.length() > 0)) {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE_MISSING_MSMA_IP));
										map.put("reason",GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE_MISSING_MSMA_IP));
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}
								}

								if (!CommonUtil.isValidObject(mcmip) || !(mcmip.length() > 0)) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE_MISSING_MCMA_IP));
									map.put("reason",GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE_MISSING_MCMA_IP));
									map.put("enbId",enbId);
									obj.add(map);
									continue;
								}
								
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_VBS, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								
							} else {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								
								map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							mapObject.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.GENERATED_COMM_SCRIPT_SUCCESSFULLY));
							if (CommonUtil.isValidObject(fileType)
									&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
								if (CommonUtil.isValidObject(neMappingEntity)
										&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
										&& (neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
												|| neMappingEntity.getSiteConfigType()
														.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {

									JSONObject fileProcessResult = createExecutableFiles(ciqFileName, programId,
											uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId);
									//fileGenerateResultList.add(fileProcessResult);
									String info=mapObject.get("reason").toString();
									String info2=fileProcessResult.get("reason").toString();
									String FinalInfo= info + "\n"+info2;
									mapObject.put("reason", FinalInfo );
									if (fileProcessResult != null && fileProcessResult.containsKey("status")
											&& fileProcessResult.get("status").equals(Constants.FAIL)) {
										mapObject.put("status", Constants.FAIL);
										mapObject.put("reason", fileProcessResult.get("reason"));
										map.put("reason", fileProcessResult.get("reason").toString());
										map.put("enbId",enbId);
										obj.add(map);
										continue;
									}
								}
							}
							for(JSONObject fileResult: fileGenerateResultList) {
								if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
									reportsMap.put("COMM", fileResult);
								} else {
									reportsMap.put("COMM", fileResult);
									break;
								}
							}
						}
					} else if (ciqDetails.get("programName").equals("VZN-4G-FSU")&& !fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						// for fsu nemapping independent
						NeMappingEntity neMappingEntity = neMappingEntities.get(0);
						if (!CommonUtil.isValidObject(neMappingEntity)
								|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							
							map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}
						
						
						if(ciqDetails.containsKey("fsuType") && ciqDetails.get("fsuType")!=null) {
							fsuType = ciqDetails.get("fsuType").toString().trim();
						}
						List<CIQDetailsModel> listCIQDetailsModel = null;
						listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "FSUCIQ", "");
						if(!(fsuType.equals("FSU10") || fsuType.equals("FSU20"))) {
							
							String fsuIp = "";
							if(!ObjectUtils.isEmpty(listCIQDetailsModel) && listCIQDetailsModel.get(0).getCiqMap().containsKey("FSU_Mgmt_IPv6_Address")) {
								fsuIp = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue().trim();
							}
							if(fsuIp.isEmpty()) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "Unable to Fetch FSU Mgmt IPv6 Address from CIQ");
								return mapObject;
							}
							//UserDetailsEntity userDetailsEntity = userActionRepositoryImpl.getUserDetailsBasedName(user.getUserName());
							JSONObject fsuTypeResult = fsuTypeFecthService.getFSUType(neMappingEntity, user.getUserName(), fsuIp);
							if(fsuTypeResult!=null && fsuTypeResult.containsKey("status") && fsuTypeResult.get("status").equals(Constants.SUCCESS)) {
								fsuType = fsuTypeResult.get("fsuType").toString();
							} else {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "Unable to Fetch FSU Type from USM, Please Select FSU Type To Generate Templates");
								return mapObject;
							}
							//String ex = ses.getFsuLog("user", "root123", "user", "root123", "10.20.120.76", "10.9.68.109");
							//System.out.println(ex);
							
						}
						
						//fetching neVersion from ciq
						/*neVersion = listCIQDetailsModel.get(0).getCiqMap().get("NE_Version").getHeaderValue().trim();
						neVersion = neVersion.substring(0,neVersion.indexOf("-"));*/
						neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();

						
						if (fsuType.equals(Constants.FSU_TYPE_10)) {
							listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "FSU10", "");
						}
						if(ObjectUtils.isEmpty(listCIQDetailsModel)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", "No Data for " + enbId + " in " + fsuType + " sheet");
							
							map.put("reason","No Data for " + enbId + " in " + fsuType + " sheet");
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}
						if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)) {
							uploadPath.append(Constants.PRE_MIGRATION_ENV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							//List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
							//		.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							if (fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)) {
									
								
								fileGenerateResult = objGenerateCsvService.envFileGenerationForFsu(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"ENV_FSU",
										"ENV", remarks,neVersion, fsuType, listCIQDetailsModel);
								if(fileGenerateResult.containsKey("status")&& fileGenerateResult.get("status").equals(true)) {
								scheduleMap.put("fsuEnvFileName", fileGenerateResult.get("fileName").toString());
								scheduleMap.put("fsuEnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
								//reportsMap.put("ENV_FSU", fileGenerateResult);
								String fileName = fileGenerateResult.get("fileName").toString();
								System.out.println(" the env file for fsu : " + fileName);
								reportsMap.put("ENV_FSU", fileGenerateResult);
								fileGenerateResultList.add(fileGenerateResult);
							}
								fileGenerateResult = objGenerateCsvService.cpriFileGenerationFSU(ciqFileName, enbId, enbName, dbcollectionFileName, 
										programId, uploadPath.toString(), sessionId, neMappingEntity, remarks, fsuType, listCIQDetailsModel);
								fileGenerateResultList.add(fileGenerateResult);
								
							
							}
							
							
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_ENV_SUCCESSFULLY));
						} else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
							uploadPath.append(Constants.PRE_MIGRATION_CSV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							//List<CIQDetailsModel> listCIQDetailsModelll = fileUploadRepository
							//		.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							if (fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
								fileGenerateResult = objGenerateCsvService.csvFileGenerationForFsu(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"CSV_FSU",
										"fsu", remarks, neVersion, fsuType,listCIQDetailsModel);
								
								//reportsMap.put("CSV_FSU", fileGenerateResult);
								fileGenerateResultList.add(fileGenerateResult);
								

							}
							JSONObject neGrowResult = createExecutableFilesForNEGrowFSU(ciqFileName, programId, uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, 
									"", programName, fileGenerateResultList);
							if(neGrowResult != null && neGrowResult.containsKey("status")
									&& neGrowResult.get("status").equals(Constants.FAIL)) {
								if(neGrowResult.containsKey("reason")) {
									mapObject.put("reason",neGrowResult.get("reason"));
									map.put("reason",neGrowResult.get("reason").toString());
								} else {
									System.out.println("-------6---------");
									mapObject.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									map.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								}
								
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							scheduleMap.put("fsugrowFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("fsugrowFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("CSV", fileGenerateResult);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY));

						}
					else if (fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
							uploadPath.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File ComfileExists = new File(uploadPath.toString());
							if (!ComfileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGenerationFSU(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
								fileGenerateResult = objGenerateCsvService.commissionScriptFileGenerationNTPFSU(ciqFileName,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
								fileGenerateResultList.add(fileGenerateResult);
							
						}
							else {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								
								map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							mapObject.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.GENERATED_COMM_SCRIPT_SUCCESSFULLY));
							JSONObject neGrowResult = createExecutableFilesForNEGrowFSU(ciqFileName, programId, uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, 
									"", programName, fileGenerateResultList);
							if(neGrowResult != null && neGrowResult.containsKey("status")
									&& neGrowResult.get("status").equals(Constants.FAIL)) {
								if(neGrowResult.containsKey("reason")) {
									mapObject.put("reason",neGrowResult.get("reason"));
									map.put("reason",neGrowResult.get("reason").toString());
								} else {
									System.out.println("-------------7--------------");
									mapObject.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									map.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								}
								
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							reportsMap.put("Commission", fileGenerateResult);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_COMM_SCRIPT_SUCCESSFULLY));
						}
					}
					// For 5G
					if ((ciqDetails.get("programName").equals("VZN-5G-USM-LIVE")
							|| (ciqDetails.get("programName").equals("VZN-5G-SNAP-5G-NR"))
							|| (ciqDetails.get("programName").equals("VZN-5G-MM")))&& !fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {

						NeMappingEntity neMappingEntity = neMappingEntities.get(0);
						if (!CommonUtil.isValidObject(neMappingEntity)
								|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							
							map.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
							map.put("enbId",enbId);
							obj.add(map);
							continue;
						}
						if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)) {
							String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
									.getNeVersion().toString();
							if (version.contains("19")) {
								version = "19A";
							}
							if (version.contains("20.A")) {
								version = "20A";
							}
							if (version.contains("20.B")) {
								version = "20B";
							}
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							
							if (version.contains("20.C")) {
								version = "20C";
							} else if (version.contains("21.A")) {
								version = "21A";
							} else if (version.contains("21.B")) {
								version = "21B";
							} else if (version.contains("21.C")) {
								version = "21C";
							}else if (version.contains("21.D")) {
								version = "21D";
							} else if (version.contains("22.A")) {
								version = "22A";
							}else if (version.contains("22.C")) {
								version = "22C";
							}
							uploadPath.append(Constants.PRE_MIGRATION_ENV_5G
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

							// uploadPath.append("PreMigration").append("/").append("Output").append("/").append(ciqFileName.toString()).append("/").append("AU").append("/").append("AU_ENV").append("/").append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().toString());
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}

							if (fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)) {
								if (version.equalsIgnoreCase("19A")) {
									fileGenerateResult = objGenerateCsvService.envFileGenerationFor5G(ciqFileName,
											version, enbId, enbName, dbcollectionFileName, programId,
											uploadPath.toString(), sessionId, remarks);
									fileGenerateResultList.add(fileGenerateResult);
								}
								//bbs
								//String vers = "20A";
								String vers =version;
								fileGenerateResult = objGenerateCsvService.envFileGenerationFor5G28ghz(ciqFileName,
										vers, enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, remarks,ciqDetails.get("programName").toString());
								fileGenerateResultList.add(fileGenerateResult);
									scheduleMap.put("EnvFileName", fileGenerateResult.get("fileName").toString());
									scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							

							}
							for(JSONObject fileResult: fileGenerateResultList) {
								if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
									reportsMap.put("ENV", fileResult);
								} else {
									reportsMap.put("ENV", fileResult);
									break;
								}
							}
							// mapObject.put("reason",
							// GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_ENV_SUCCESSFULLY));
							mapObject.put("reason", "GENERATED AU_ENV SUCCESSFULLY");

						}
						// else if(CommonUtil.isValidObject(fileType) &&
						// fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
						else if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("route")) {

							uploadPath.append(Constants.PRE_MIGRATION_route_5G
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// if(!CommonUtil.isValidObject(neMappingEntity)){
								fileGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
								fileGenerateResultList.add(fileGenerateResult);
								List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
										enbName, dbcollectionFileName);
								String marketType="";
								String retValue = "";
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("MarketType")) {
									marketType=listCIQDetailsModels.get(0).getCiqMap().get("MarketType").getHeaderValue();
								}
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("AU_RET")) {
									retValue = listCIQDetailsModels.get(0).getCiqMap().get("AU_RET").getHeaderValue();
								}
								if(!marketType.isEmpty() && marketType.equalsIgnoreCase("snap")) {
									fileGenerateResult = objGenerateCsvService.offsetFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									if(!retValue.isEmpty()&& !retValue.equals("TBD")) {
										fileGenerateResult = objGenerateCsvService.tilt(ciqFileName, enbId,
												enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
												neMappingEntity, remarks, " ", " ", "RanConfig");
											fileGenerateResultList.add(fileGenerateResult);
									}
								}
							}
							reportsMap.put("ROUTE", fileGenerateResult);
							// mapObject.put("reason",
							// GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_ENV_SUCCESSFULLY));
							mapObject.put("reason", "GENERATED ROUTE SUCCESSFULLY");

						}
						// condition for A1_A2
						// else if(CommonUtil.isValidObject(fileType) &&
						// fileType.equalsIgnoreCase("A1_A2")) {

						else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT)) {
							// if("".equals(""))
							// {
							// uploadPath.append(Constants.PRE_MIGRATION_route_5G
							// .replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(),
							// "."))
							// .replace("enbId",enbId)
							// .replaceAll(" ", "_"));
							// File fileExists = new File(uploadPath.toString());
							// if (!fileExists.exists()) {
							// FileUtil.createDirectory(uploadPath.toString());
							// }
							// if(CommonUtil.isValidObject(neMappingEntity) &&
							// CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) &&
							// (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
							// ||
							// neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
							// ||
							// neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))){
							// fileGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName,
							// enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
							// sessionId,"XML", neMappingEntity, remarks);
							// fileGenerateResultList.add(fileGenerateResult);
							// }
							//
							// }
							// if("".equals(""))
							// {
							List<String> st = new ArrayList<>();
							String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
									.getNeVersion().toString();
							if (version.contains("19")) {
								version = "19A";
							}
							if (version.contains("20.A")) {
								version = "20A";
							}
							if (version.contains("20.B")) {
								version = "20B";

							}
							if (version.contains("20.C")) {
								version = "20C";
							} else if (version.contains("21.A")) {
								version = "21A";
							}else if (version.contains("21.B")) {
								version = "21B";
							} else if (version.contains("21.C")) {
								version = "21C";
							}else if (version.contains("21.D")) {
								version = "21D";
							} else if (version.contains("22.A")) {
								version = "22A";
							}else if (version.contains("22.C")) {
								version = "22C";
							}
							uploadPath.setLength(0);
							uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
									.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);

							uploadPath.append(Constants.PRE_MIGRATION_Commission
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// if(!CommonUtil.isValidObject(neMappingEntity)){
								//bbs
								//String ver = "20A";
								String ver = version;
								if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("22C"))) {
									fileGenerateResult = objGenerateCsvService.a1a2ConfigFileGeneration(ciqFileName, ver,
											enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
											sessionId, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									reportsMap.put("A1A2Config", fileGenerateResult);
									fileGenerateResult = objGenerateCsvService.a1a2CreateFileGeneration(ciqFileName, ver,
											enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
											sessionId, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									reportsMap.put("A1A2Create", fileGenerateResult);
								}								
								fileGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
								fileGenerateResultList.add(fileGenerateResult);
								reportsMap.put("ROUTE", fileGenerateResult);
								
								/*if(!version.equals("21A")) {
									fileGenerateResult = objGenerateCsvService.gpScriptFileGeneration(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
											"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
									
									for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
											.size(); i++) {
										JSONObject temp = new JSONObject();
										temp.put("status", fileGenerateResult.get("status"));
										temp.put("fileName",
												((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
										fileGenerateResultList.add(temp);
									}
								}*/
								
								
								List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
										enbName, dbcollectionFileName);
								String marketType="";
								String retValue = "";
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("MarketType")) {
									marketType=listCIQDetailsModels.get(0).getCiqMap().get("MarketType").getHeaderValue();
								}
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("AU_RET")) {
									retValue = listCIQDetailsModels.get(0).getCiqMap().get("AU_RET").getHeaderValue();
								}
								if(!marketType.isEmpty() && marketType.equalsIgnoreCase("snap")) {
									fileGenerateResult = objGenerateCsvService.offsetFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									if(!retValue.isEmpty()&& !retValue.equals("TBD")) {
										fileGenerateResult = objGenerateCsvService.tilt(ciqFileName, enbId,
												enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
												neMappingEntity, remarks, " ", " ", "RanConfig");
											fileGenerateResultList.add(fileGenerateResult);
									}
								}
								//    commented for removing specific template generation as client asked
								//	fileGenerateResult = objGenerateCsvService.additionalTemplete(version, ciqFileName,
								//			enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
								//			sessionId, "XML", neMappingEntity, remarks, "", "", "RanConfig");
								//	fileGenerateResultList.add(fileGenerateResult);
								// achor extra
								String eNBName = (String) enb.get("neName");
								String eNBId = (String) enb.get("neId");
								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
										.getEnbTableDetails(ciqFileName, enbId, eNBName, dbcollectionFileName);

								String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
								neidau = neidau.replaceAll("^0+(?!$)", "");
								List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
										.getEnbTableDetails(ciqFileName, neidau, eNBName, dbcollectionFileName);

								Set<String> neList = new LinkedHashSet<>();
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue().isEmpty()
													|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
															.getHeaderValue().equals("NA"))
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue());
										}
									}

								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue() != null) {
												if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue().equals("NA")
														|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
																.getHeaderValue().isEmpty())
													break;
												else
													neList.add(listCIQDetailsModell.get(i).getCiqMap()
															.get("Samsung eNB_ID").getHeaderValue());
											}
										}
									}
								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
												.getHeaderValue() != null)
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
													.getHeaderValue());
									}
								}
								fileGenerateResult = objGenerateCsvService.cslTemplete(ciqFileName, enbId, enbName,
										dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, neList, " ", " ", "RanConfig");
								for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
									fileGenerateResultList.add(temp);
								}
								for(JSONObject fileResult: fileGenerateResultList) {
									if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
										reportsMap.put("ANCHOR", fileResult);
									} else {
										reportsMap.put("ANCHOR", fileResult);
										break;
									}
								}
								
								
								
							}

							mapObject.put("reason", "GENERATED COMMISSION SCRIPTS SUCCESSFULLY");
							JSONObject fileProcessResult = createExecutableFiles5G(ciqFileName, programId,
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "COMM", version);
							if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", fileProcessResult.get("reason"));
								map.put("reason", fileProcessResult.get("reason").toString());
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}

						} else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENDC)) {
							String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
									.getNeVersion().toString();
							// String ver = "20A";
							if (version.contains("20.A")) {
								version = "20A";

							}
							if (version.contains("20.B")) {
								version = "20B";

							}
							if (version.contains("20.C")) {
								version = "20C";
							} 
							if(version.contains("21.A")) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "ENDC is not generated for version 21.A");
								map.put("reason", "ENDC is not generated for version 21.A");
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							uploadPath.append(Constants.PRE_MIGRATION_ENDC
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// List<String> neID =new ArrayList<>();
								// neID=(List<String>) (ciqDetails.get("neIDs"));
								// String enbid=neID.get(0);

								String eNBName = (String) enb.get("neName");
								String eNBId = (String) enb.get("neId");

								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
										.getEnbTableDetails(ciqFileName, enbId, eNBName, dbcollectionFileName);

								String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
								String nrfreq = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND")
										.getHeaderValue();
								neidau = neidau.replaceAll("^0+(?!$)", "");
								List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
										.getEnbTableDetails(ciqFileName, neidau, eNBName, dbcollectionFileName);

								Set<String> neList = new LinkedHashSet<>();
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue().isEmpty()
													|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
															.getHeaderValue().equals("NA"))
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue());
										}
									}

								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue() != null) {
												if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue().equals("NA")
														|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
																.getHeaderValue().isEmpty())
													break;
												else
													neList.add(listCIQDetailsModell.get(i).getCiqMap()
															.get("Samsung eNB_ID").getHeaderValue());
											}
										}
									}
								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
												.getHeaderValue() != null)
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
													.getHeaderValue());
									}
								}
								// String folderName = objGenerateCsvService.folderName(eNBId);
								String folderName = objGenerateCsvService.folderName(enbId);
								for (String neid : neList) {
									fileGenerateResult = objGenerateCsvService.endcTemplate(nrfreq, folderName, neid,
											version, ciqFileName, enbId, enbName, dbcollectionFileName, programId,
											uploadPath.toString(), sessionId, "XML", neMappingEntity, remarks,
											"RanConfig");

								}
								if (fileGenerateResult.size() > 0) {
									for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
											.size(); i++) {
										JSONObject temp = new JSONObject();
										temp.put("status", fileGenerateResult.get("status"));
										temp.put("fileName",
												((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
										fileGenerateResultList.add(temp);
									}
								} else {
									throw new Exception("No Records found");
								}
							}
							for(JSONObject fileResult: fileGenerateResultList) {
								if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
									reportsMap.put("ENDC", fileResult);
								} else {
									reportsMap.put("ENDC", fileResult);
									break;
								}
							}

							mapObject.put("reason", "GENERATED ENDC TEMPLATES SUCCESSFULLY");
							JSONObject fileProcessResult = createExecutableFiles5G(ciqFileName, programId,
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "ENDC", version);
							if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", fileProcessResult.get("reason"));
								map.put("reason", fileProcessResult.get("reason").toString());
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
						}

						else if (CommonUtil.isValidObject(fileType)
								&& fileType.equalsIgnoreCase(Constants.FILE_TYPE_CSV)) {
							List<String> st = new ArrayList<>();
							String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
									.getNeVersion().toString();
							if (version.contains("20.A")) {
								version = "20A";

							}
							if (version.contains("20.B")) {
								version = "20B";

							}
							if (version.contains("20.C")) {
								version = "20C";
							}
							if (version.contains("21.A")) {
								version = "21A";
							} else if (version.contains("21.B")) {
								version = "21B";
							} else if (version.contains("21.C")) {
								version = "21C";
							}else if (version.contains("21.D")) {
								version = "21D";
							} else if (version.contains("22.A")) {
								version = "22A";
							}else if (version.contains("22.C")) {
								version = "22C";
							}
							//
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							uploadPath.append(Constants.PRE_MIGRATION_TEMPLATE
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
							System.out.println(uploadPath);
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// if(!CommonUtil.isValidObject(neMappingEntity)){

								String releaseVer = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();

								fileGenerateResult = objGenerateCsvService.generateTemplates(ciqFileName, version,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, remarks, releaseVer,neMappingEntity);
								
								for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
									if(temp.get("fileName").toString().contains("AU_CaCell")) {
										scheduleMap.put("cellGrowFileName", temp.get("fileName").toString());
										reportsMap.put("CELL",temp);
									} else if(temp.get("fileName").toString().contains("AU_")) {
										scheduleMap.put("AuGrowFileName", temp.get("fileName").toString());
										reportsMap.put("AU",temp);
									} else if(temp.get("fileName").toString().contains("ACPF_")) {
										scheduleMap.put("AcpfGrowFileName", temp.get("fileName").toString());
										reportsMap.put("ACPF",temp);
									} else if(temp.get("fileName").toString().contains("AUPF_")) {
										scheduleMap.put("AupfGrowFileName", temp.get("fileName").toString());
										reportsMap.put("AUPF",temp);
									} else if(temp.get("fileName").toString().contains("pnp_macro")) {
										scheduleMap.put("Pnp_macroGrowFileName", temp.get("fileName").toString());
										reportsMap.put("pnp_macro",temp);
									} 
									scheduleMap.put("growFilePath", uploadPath.toString().substring(27));
									fileGenerateResultList.add(temp);
								}
								createExecutableFilesForNEGrow(ciqFileName, programId, uploadPath.toString(),
										neMappingEntity, enbId, enbName, sessionId, "single",ciqDetails.get("programName").toString(),fileGenerateResultList);
								
							}
							//createExecutableFilesForNEGrow(ciqFileName, programId, null, neMappingEntity, enbId,
							//		enbName, sessionId, "ALL",ciqDetails.get("programName").toString());

							mapObject.put("reason", "GENERATED GROW TEMPLATES SUCCESSFULLY");
						} else if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase(Constants.ALL_5g)) {
							String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
									.getNeVersion().toString();
							if (version.contains("19")) {
								version = "19A";
							}
							if (version.contains("20.A")) {
								version = "20A";
							}
							if (version.contains("20.B")) {
								version = "20B";

							}
							if (version.contains("20.C")) {
								version = "20C";
							} else if (version.contains("21.A")) {
								version = "21A";
							} else if (version.contains("21.B")) {
								version = "21B";
							} else if (version.contains("21.C")) {
								version = "21C";
							}
							else if (version.contains("21.D")) {
								version = "21D";
							} else if (version.contains("22.A")) {
								version = "22A";
							}else if (version.contains("22.C")) {
								version = "22C";
							}
							uploadPath.append(Constants.PRE_MIGRATION_All
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}

							if (version.equalsIgnoreCase("19A")) {
								fileGenerateResult = objGenerateCsvService.envFileGenerationFor5G(ciqFileName, version,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, remarks);
								fileGenerateResultList.add(fileGenerateResult);
							}
							//bbs
							//String vers = "20A";
							String vers = version;
							fileGenerateResult = objGenerateCsvService.envFileGenerationFor5G28ghz(ciqFileName, vers,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									remarks,ciqDetails.get("programName").toString());
							fileGenerateResultList.add(fileGenerateResult);
							scheduleMap.put("EnvFileName", fileGenerateResult.get("fileName").toString());
							scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
						
							reportsMap.put("ENV", fileGenerateResult);

							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// if(!CommonUtil.isValidObject(neMappingEntity)){

								String releaseVer = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();
								fileGenerateResult = objGenerateCsvService.generateTemplates(ciqFileName, version,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
										sessionId, remarks, releaseVer, neMappingEntity);
								// createExecutableFilesForNEGrow(ciqFileName, programId, uploadPath.toString(),
								// neMappingEntity, enbId, enbName, sessionId);
								for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
									fileGenerateResultList.add(temp);
									if(temp.get("fileName").toString().contains("AU_CaCell")) {
										scheduleMap.put("cellGrowFileName", temp.get("fileName").toString());
										reportsMap.put("CELL",temp);
									} else if(temp.get("fileName").toString().contains("AU_")) {
										scheduleMap.put("AuGrowFileName", temp.get("fileName").toString());
										reportsMap.put("AU",temp);
									} else if(temp.get("fileName").toString().contains("ACPF_")) {
										scheduleMap.put("AcpfGrowFileName", temp.get("fileName").toString());
										reportsMap.put("ACPF",temp);
									} else if(temp.get("fileName").toString().contains("AUPF_")) {
										scheduleMap.put("AupfGrowFileName", temp.get("fileName").toString());
										reportsMap.put("AUPF",temp);
									} else if(temp.get("fileName").toString().contains("pnp_macro")) {
										scheduleMap.put("Pnp_macroGrowFileName", temp.get("fileName").toString());
										reportsMap.put("pnp_macro",temp);
									}
									scheduleMap.put("growFilePath", uploadPath.toString().substring(27));
								}
								
								
							}

							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								// if(!CommonUtil.isValidObject(neMappingEntity)){
								//bbs
								//String vers = "20A";
								String ver = version; //22A
								if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("21D")|| version.equals("22C") || version.equals("22A"))) {
									fileGenerateResult = objGenerateCsvService.a1a2ConfigFileGeneration(ciqFileName, ver,
											enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
											sessionId, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									reportsMap.put("A1A2Config", fileGenerateResult);
									
									fileGenerateResult = objGenerateCsvService.a1a2CreateFileGeneration(ciqFileName, ver,
											enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
											sessionId, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
									reportsMap.put("A1A2Create", fileGenerateResult);
								}
								

								fileGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
								fileGenerateResultList.add(fileGenerateResult);
								reportsMap.put("ROUTE", fileGenerateResult);
								
								/*if(!version.equals("21A")) {
									fileGenerateResult = objGenerateCsvService.gpScriptFileGeneration(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
											"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
									
									for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
											.size(); i++) {
										JSONObject temp = new JSONObject();
										temp.put("status", fileGenerateResult.get("status"));
										temp.put("fileName",
												((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
										fileGenerateResultList.add(temp);
									}
								}*/
								
								
								List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
										enbName, dbcollectionFileName);
								String marketType="";
								String retValue = "";
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("MarketType")) {
									marketType=listCIQDetailsModels.get(0).getCiqMap().get("MarketType").getHeaderValue();
								}
								if(listCIQDetailsModels.get(0).getCiqMap().containsKey("AU_RET")) {
									retValue = listCIQDetailsModels.get(0).getCiqMap().get("AU_RET").getHeaderValue();
								}
								if(!marketType.isEmpty() && marketType.equalsIgnoreCase("snap")) {
									fileGenerateResult = objGenerateCsvService.offsetFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, "", "", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
								if(!retValue.isEmpty()&& !retValue.equals("TBD")) {
									fileGenerateResult = objGenerateCsvService.tilt(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
											neMappingEntity, remarks, " ", " ", "RanConfig");
									fileGenerateResultList.add(fileGenerateResult);
								}
								}
//							    commented for removing specific template generation as client asked
								//fileGenerateResult = objGenerateCsvService.additionalTemplete(version, ciqFileName,
								//		enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(),
								//		sessionId, "XML", neMappingEntity, remarks, "", "", "RanConfig");
								//fileGenerateResultList.add(fileGenerateResult);
								//newobj.add(fileGenerateResult);

								// achor extra
								String eNBName = (String) enb.get("neName");
								String eNBId = (String) enb.get("neId");
								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
										.getEnbTableDetails(ciqFileName, enbId, eNBName, dbcollectionFileName);

								String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
								String nrfreq = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND")
										.getHeaderValue();
								neidau = neidau.replaceAll("^0+(?!$)", "");

								List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
										.getEnbTableDetails(ciqFileName, neidau, eNBName, dbcollectionFileName);


								Set<String> neList = new LinkedHashSet<>();
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue().isEmpty()
													|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
															.getHeaderValue().equals("NA"))
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue());
										}
									}

								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue() != null) {
												if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue().equals("NA")
														|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
																.getHeaderValue().isEmpty())
													break;
												else
													neList.add(listCIQDetailsModell.get(i).getCiqMap()
															.get("Samsung eNB_ID").getHeaderValue());
											}
										}
									}
								}
								if (neList.size() == 0) {
									for (int i = 0; i < listCIQDetailsModell.size(); i++) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
												.getHeaderValue() != null)
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID")
													.getHeaderValue());
									}
								}
								List<JSONObject> newobj= new ArrayList<JSONObject>();
								fileGenerateResult = objGenerateCsvService.cslTemplete(ciqFileName, enbId, enbName,
										dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, neList, " ", " ", "RanConfig");
								for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
									fileGenerateResultList.add(temp);
									newobj.add(temp);

								}
								for(JSONObject fileResult: newobj) {
									if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
										reportsMap.put("ANCHOR", fileResult);
									} else {
										reportsMap.put("ANCHOR", fileResult);
										break;
									}
								}
								
								// String folderName = objGenerateCsvService.folderName(eNBId);//22A
 								if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("21D") || version.equals("22A"))) {
									newobj= new ArrayList<JSONObject>();
									String folderName = objGenerateCsvService.folderName(enbId);
									for (String neid : neList) {
										fileGenerateResult = objGenerateCsvService.endcTemplate(nrfreq, folderName, neid,
												version, ciqFileName, enbId, enbName, dbcollectionFileName, programId,
												uploadPath.toString(), sessionId, "XML", neMappingEntity, remarks,
												"RanConfig");

									}
									if (neList.size() > 0) {
										for (int i = 0; i < ((ArrayList<String>) fileGenerateResult.get("fileName"))
												.size(); i++) {
											JSONObject temp = new JSONObject();
											temp.put("status", fileGenerateResult.get("status"));
											temp.put("fileName",
													((ArrayList<String>) fileGenerateResult.get("fileName")).get(i));
											fileGenerateResultList.add(temp);
											newobj.add(temp);
										}
									}
									for(JSONObject fileResult: newobj) {
										if(fileResult.containsKey("status") && fileResult.get("status").equals("true")) {
											reportsMap.put("ENDC", fileResult);
										} else {
											reportsMap.put("ENDC", fileResult);
											break;
										}
									}
								}
								
							}
							mapObject.put("reason", "GENERATED ALL TEMPLATES SUCCESSFULLY");
							JSONObject fileProcessResult = createExecutableFiles5G(ciqFileName, programId,
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "ALL", version);
							JSONObject fileProcessResult1 = createExecutableFilesForNEGrow(ciqFileName, programId,
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "ALL",ciqDetails.get("programName").toString(),fileGenerateResultList);

							if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", fileProcessResult.get("reason"));
								map.put("reason", fileProcessResult.get("reason").toString());
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
							if (fileProcessResult1 != null && fileProcessResult1.containsKey("status")
									&& fileProcessResult1.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", fileProcessResult1.get("reason"));
								map.put("reason", fileProcessResult1.get("reason").toString());
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}
						}
					}

					// end of 5G (site name)

					if (!(ciqDetails.get("programName").equals("VZN-5G-DSS") || programName.contains("5G-CBAND"))&& !fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						for (JSONObject object : fileGenerateResultList) {
							if (object.get("status") != null && (boolean) object.get("status")) {
								fileGenerateStatus = true;
							} else if (object.get("status") != null && !(boolean) object.get("status")) {
								fileGenerateStatus = false;
								break;
							}
						}

						if (fileGenerateStatus) {
							String siteName = "";
							String eNBName = (String) enb.get("neName");
							if(programName.contains("5G-MM")) {
								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
										.getEnbTableDetails(ciqFileName, enbId, eNBName, dbcollectionFileName);
								if (CommonUtil.isValidObject(listCIQDetailsModel) && !listCIQDetailsModel.isEmpty()) {
									if (listCIQDetailsModel.get(0).getCiqMap().containsKey("VZW Site Name")) {
										siteName = listCIQDetailsModel.get(0).getCiqMap().get("VZW Site Name").getHeaderValue();
									}
								}
							}
							
							JSONObject doZipResultObject = doZipAndSaveAudit(sessionId, fileType, uploadPath.toString(),
									ciqFileName, enbName, remarks, programDetailsEntity, fileGenerateResultList,programName,siteName,integrationType);
							if (fileType.equalsIgnoreCase(Constants.FILE_TYPE_ENV)&&ovUpdate&&!programName.contains("5G-DSS")&&!programName.contains("5G-CBAND"))
							{
								JSONObject ovUpdateJson=new JSONObject();
								
								ovUpdateJson.put("FileName",doZipResultObject.get("zipFileName").toString());
								ovUpdateJson.put("filePath",doZipResultObject.get("fPath").toString());
								ovUpdateJson.put("programName",programName);
								ovUpdateJson.put("programId",programId);
								ovUpdateJson.put("ciqFileName",ciqFileName);
								ovUpdateJson.put("enbName",enbName);
								ovUpdateJson.put("enbId",enbId);
								int premigration_Id=Integer.parseInt(doZipResultObject.get("premigration_Id").toString());
								GenerateInfoAuditEntity generateInfoAuditEntity = runTestRepository.getGenerateInfoAuditEntityEntity(premigration_Id);
								JSONObject ENVuplaodResult =objGenerateCsvService.envUploadToOVFromPremigration(ovUpdateJson, generateInfoAuditEntity)	;
							}
							
							/*
							 * fileGenerateResultList.clear(); uploadPath.setLength(0);
							 */
							if (doZipResultObject != null && doZipResultObject.containsKey("status")
									&& doZipResultObject.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								System.out.println("--------8----------");
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}

						finalResultMap.put(enbId, "Success");
						ArrayList<String> zipFileName=new ArrayList<String>();
						ArrayList<String> fPath=new ArrayList<String>();
						fPath.add(doZipResultObject.get("fPath").toString());
						zipFileName.add(doZipResultObject.get("zipFileName").toString());
						mapObject.put("zipFileName",zipFileName);
						mapObject.put("fPath",fPath);
						mapObject.put("enb", enbId);

						finalResultMap.put(enbId, "Success");
						mapObject.put("CombinedResult", finalResultMap);
						mapObject.put("status", Constants.SUCCESS);
						commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_GENERATE,
								Constants.ACTION_GENERATE,
								"File Generated Successfully For NE: " + enbId + ", Type: " + fileType, sessionId);
						} else {
							System.out.println("----------9-----------");
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
							if("Pseudo IP".equals(integrationType)) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", "File cannot be generated. Pseudo IP is not present in CIQ" );
							}
						}
					}else if(fileType.equalsIgnoreCase(Constants.FILE_TYPE_OFFLINE)) {
						for (JSONObject object : fileGenerateResultList) {
							if (object.get("status") != null && (boolean) object.get("status")) {
								fileGenerateStatus = true;
							} else if (object.get("status") != null && !(boolean) object.get("status")) {
								fileGenerateStatus = false;
								break;
							}
						}

						if (fileGenerateStatus) {
							String siteName = "";
							String eNBName = (String) enb.get("neName");
							if(programName.contains("5G-MM")) {
								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
										.getEnbTableDetails(ciqFileName, enbId, eNBName, dbcollectionFileName);
								if (CommonUtil.isValidObject(listCIQDetailsModel) && !listCIQDetailsModel.isEmpty()) {
									if (listCIQDetailsModel.get(0).getCiqMap().containsKey("VZW Site Name")) {
										siteName = listCIQDetailsModel.get(0).getCiqMap().get("VZW Site Name").getHeaderValue();
									}
								}
							}
							
							JSONObject doZipResultObject = doZipAndSaveAudit(sessionId, fileType, uploadPath.toString(),
									ciqFileName, enbName, remarks, programDetailsEntity, fileGenerateResultList,programName,siteName,integrationType);
							/*
							 * fileGenerateResultList.clear(); uploadPath.setLength(0);
							 */
							if (doZipResultObject != null && doZipResultObject.containsKey("status")
									&& doZipResultObject.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								System.out.println("--------10------------");
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								map.put("enbId",enbId);
								obj.add(map);
								continue;
							}

						finalResultMap.put(enbId, "Success");
						ArrayList<String> zipFileName=new ArrayList<String>();
						ArrayList<String> fPath=new ArrayList<String>();
						fPath.add(doZipResultObject.get("fPath").toString());
						zipFileName.add(doZipResultObject.get("zipFileName").toString());
						mapObject.put("zipFileName",zipFileName);
						mapObject.put("fPath",fPath);
						mapObject.put("enb", enbId);

						finalResultMap.put(enbId, "Success");
						mapObject.put("status", Constants.SUCCESS);
						mapObject.put("reason", "Offline Setup file Generated Successfully");
						if(programName.contains("VZN-5G-MM")) {
							ErrorType1="Generate";
						}
						commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_GENERATE,
								Constants.ACTION_GENERATE,
								"File Generated Successfully For NE: " + enbId + ", Type: " + fileType, sessionId);
						} else {
							System.out.println("-------11--------");
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
							if(programName.contains("VZN-5G-MM")) {
								ErrorType="5G-MM";
							}
							
						}

					}
					else {
						finalResultMap.put(enbId, "Success");
						mapObject.put("CombinedResult", finalResultMap);
					}
				}
				
				
			}
			
			if((null!=enbID&&enbID.size()>0&&validateResut.size()>0)&&validateResut.size()==enbID.size()) {
				
				//
				
				String neIds = String.join("$", validateResut);
				neIds="Validation failed for all selected NE's "+neIds;
				mapObject.put("validateNeIdReport", neIds);
		
				//
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "");
			}
			if(ErrorType !=null && ErrorType.equalsIgnoreCase("ENDC_script")) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "Offline Setup file Generated Successfully without ENDC Script");
			}
			if(ErrorType !=null && ErrorType.equalsIgnoreCase("RF_script")) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "Offline Setup file Generated Successfully without RF Script");
			}if(ErrorType !=null && ErrorType.equalsIgnoreCase("5G-MM")&& ErrorType1.equalsIgnoreCase("Generate")) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "Offline Setup file Generated Successfully only for RF script uploaded GNODEB_AU ID ");
			}if(ErrorType !=null && ErrorType.equalsIgnoreCase("5G-MM")&& !ErrorType1.equalsIgnoreCase("Generate")) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "RF Script File are not uploaded");
			}
			Set<String> reportskey = reportsObject.keySet();
			for(String key: reportskey) {
				System.out.println(reportsObject.get(key));
			}
			
			Set<String> schedulekey = scheduleObject.keySet();
			for(String key: schedulekey) {
				System.out.println(scheduleObject.get(key));
			}
			
			ovScheduling(ciqDetails,reportskey,scheduleObject,reportsObject,programName,ovScheduledEntity,ovScheduledEntityList);
			reportService.insertReportDetails(reportsObject, programId, programName, user.getUserName(), ciqFileName, fileType);
			logger.error("no issues...its calling");
			if(obj.size() != 0) {
			if(obj.size() == enbList.size()) {
				mapObject.put("status", Constants.FAIL);
			}	
			else
			{
				mapObject.put("status", Constants.SUCCESS);
			}
			
			for(int i=0;i < obj.size();i++) {
				list.add(obj.get(i).get("reason"));			
			}
			
			for(String val: list) {
				String str = "";
				String reason ="";	
				Set <String> list1=new LinkedHashSet<>();

				for(Map<String,String> entry : obj) {
					if(val.equals(entry.get("reason"))) {
						list1.add(entry.get("enbId"));
					}
				}
				str = String.join(",", list1);
				reason = str + "-" + val;	
				str2.add(reason);
			}
			
			str3 = String.join("\n", str2);
			mapObject.put("reason", str3);
			}
		}

		catch (Exception e) {

			mapObject.put("status", Constants.FAIL);
			logger.error(
					"Exception in csvFileGeneration() GenerateCsvController : " + ExceptionUtils.getFullStackTrace(e));
			if (e.getMessage()!=null && e.getMessage().equals("No Records found")) {
				mapObject.put("reason", "GNODEB:" + " " + enbId + "  " + "No entries found in 4GLTE_5GNR_Relation Sheet for related NE_ID_AU");
				mapObject.put("status", Constants.SUCCESS);
			} else {
				System.out.println("---------12--------");
				mapObject.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
			}
				
		}
		return mapObject;
	}

	private List<String> validateNeidsGenerate(Integer programId, List<Map<String, String>> enbList,
			String dbcollectionFileName) {
		List<String> bf = new ArrayList<String>();

		String enbName = "";
		String enbId = "";
		try {
			for (Map<String, String> enb : enbList) {
				enbId = enb.get("neId");
				enbName = enb.get("neName");
				if (StringUtils.isNotEmpty(enbName) && StringUtils.isNotEmpty(enbId)) {
					Map<String, Object> neMap = fileUploadService.validationEnbDetails(dbcollectionFileName,
							Integer.valueOf(programId), enbName, enbId);
					if (neMap != null && neMap.size() > 0 && neMap.containsKey("validationDetails")) {
						List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) neMap.get("validationDetails");
						if (objErrorMap.size() > 0) {
							bf.add(enbId);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("exception while validate:" + enbId);
		}
		return bf;
	}

	@SuppressWarnings("unchecked")
	private JSONObject doZipAndSaveAudit(String sessionId, String fileType, String filePath, String ciqFileName,
			String enbName, String remarks, CustomerDetailsEntity programDetailsEntity,
			List<JSONObject> fileGenerateResultList, String programName, String siteName,String integrationType) {
		JSONObject mapObject = new JSONObject();
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			
			
			String zipFileName= fileType + "_" + enbName.replaceAll(" ", "_") + "_" + timeStamp+".zip";;
			if("Pseudo IP".equals(integrationType))
			{
				if(StringUtils.isNotEmpty(integrationType)) {
					integrationType=integrationType.replaceAll(" ", "_");
				}
				System.out.println("integrationType"+integrationType);
				zipFileName= fileType + "_" + enbName.replaceAll(" ", "_") + "_" + timeStamp + "_"+integrationType+".zip";;
			}
			 
			mapObject.put("zipFileName", zipFileName);
			mapObject.put("fPath", filePath);
			JSONObject fileProcessResult = addToZipFile(filePath + zipFileName, filePath, fileGenerateResultList);
			if (fileProcessResult != null && fileProcessResult.containsKey("status")
					&& fileProcessResult.get("status").equals(Constants.FAIL)) {
				return fileProcessResult;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity();
			String fileSavePath = filePath.toString();
			fileSavePath = fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
			objInfo.setFilePath(fileSavePath);
			objInfo.setCiqFileName(ciqFileName);
			objInfo.setNeName(enbName);
			objInfo.setRemarks(remarks);
			objInfo.setFileName(zipFileName);
			objInfo.setFileType(fileType);
			objInfo.setGenerationDate(new Date());
			objInfo.setGeneratedBy(user.getUserName());
			objInfo.setProgramDetailsEntity(programDetailsEntity);
			objInfo.setIntegrationType(integrationType);
			if(programName.contains("5G-MM")) {
				objInfo.setSiteName(siteName);
			}
			JSONObject saveResultJson= objGenerateCsvService.saveCsvAudit(objInfo);
			String premigration_Id= saveResultJson.get("Premigration_ID").toString();
			if(programName.contains("4G"))
			{
			HashMap<String, ArrayList<String>> map= new HashMap<>();
			ArrayList<String> arr=new ArrayList<>();
			arr.add(filePath);
			arr.add(zipFileName);
			mapObject.put(fileType,arr);
			}
			mapObject.put("status", Constants.SUCCESS);
			mapObject.put("premigration_Id", premigration_Id);
			mapObject.put("statusZip",true );
			System.out.println("File statusZip");
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.error("Exception in GenerateCsvController.saveCsvAudit() " + ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}
////Dwnload for Fsu Offline commission setup////////////
	@SuppressWarnings("unchecked")
	private JSONObject doZipAndSaveFSU(String sessionId, String fileType, String filePath, String ciqFileName,
			String enbName, String remarks, CustomerDetailsEntity programDetailsEntity,
			List<JSONObject> fileGenerateResultList, String programName, String siteName) {
		JSONObject mapObject = new JSONObject();
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String zipFileName = fileType + "_" + enbName.replaceAll(" ", "_") + "_" + timeStamp + ".zip";
			mapObject.put("zipFileName", zipFileName);
			mapObject.put("fPath", filePath);
			JSONObject fileProcessResult = addToZipFile(filePath + zipFileName, filePath, fileGenerateResultList);
			if (fileProcessResult != null && fileProcessResult.containsKey("status")
					&& fileProcessResult.get("status").equals(Constants.FAIL)) {
				return fileProcessResult;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity();
			String fileSavePath = filePath.toString();
			fileSavePath = fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
			objInfo.setFilePath(fileSavePath);
			objInfo.setCiqFileName(ciqFileName);
			objInfo.setNeName(enbName);
			objInfo.setRemarks(remarks);
			objInfo.setFileName(zipFileName);
			objInfo.setFileType(fileType);
			objInfo.setGenerationDate(new Date());
			objInfo.setGeneratedBy(user.getUserName());
			objInfo.setProgramDetailsEntity(programDetailsEntity);
			if(programName.contains("5G-MM")) {
				objInfo.setSiteName(siteName);
			}
			if(programName.contains("4G"))
			{
			HashMap<String, ArrayList<String>> map= new HashMap<>();
			ArrayList<String> arr=new ArrayList<>();
			arr.add(filePath);
			arr.add(zipFileName);
			mapObject.put(fileType,arr);
			}
			mapObject.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.error("Exception in GenerateCsvController.saveCsvAudit() " + ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}

	/**
	 * This method will add files to zip
	 * 
	 * @param destinationZipFile,
	 *            filePath, fileGenerateResultList
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject addToZipFile(String destinationZipFile, String filePath, List<JSONObject> fileGenerateResultList)
			throws FileNotFoundException, IOException {
		JSONObject mapObject = new JSONObject();
		try {
			logger.info("GenerateCsvController.addToZipFile() zipFilePath: " + destinationZipFile);
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(destinationZipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (JSONObject object : fileGenerateResultList) {

				if (object.get("fileName") != null && StringUtils.isNotEmpty((String) object.get("fileName"))) {
					File file = new File(filePath + object.get("fileName"));
					if (file.isDirectory()) {
						zipDirectory(file, file.getName(), zos);
					} else {
						FileInputStream fis = new FileInputStream(file);
						zos.putNextEntry(new ZipEntry(file.getName()));
						int length;
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);

						}
						zos.closeEntry();
						fis.close();
					}
					zos.closeEntry();

				}
			}
			zos.close();
			mapObject.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("reason", "Failed To Add Files To Zip");
			logger.error("Exception in GenerateCsvController.addToZipFile() " + ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}
	// scriptsequence for 5g

	// @SuppressWarnings("unchecked")
	// private JSONObject getScriptSequenceDetails5G(String ciqFileName, Integer
	// programId, NeMappingEntity neMappingEntity,
	// String enbId, String enbName, String sessionId, String serviceToken) {
	// JSONObject resultMap = new JSONObject();
	// List<CheckListScriptDetEntity> CheckListScriptDetails = null;
	// try {
	// StringBuilder rfScriptsPath = new StringBuilder();
	// rfScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
	// .append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
	// .append(Constants.PRE_MIGRATION_SCRIPT
	// .replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(),
	// "."))
	// .replaceAll(" ", "_"))
	// .append(enbId).append(Constants.SEPARATOR);
	//
	// logger.info("GenerateCsvController.getScriptSequenceDetails() rfScriptsPath:
	// " + rfScriptsPath.toString());
	//
	// File rfScriptFolder = new File(rfScriptsPath.toString());
	// File[] rfFiles = rfScriptFolder.listFiles();
	// List<String> scriptFileNames = new ArrayList<String>();
	// String vznCommCliNameStartsWith = "BASH_COMM_"
	// .replaceAll(" ", "").replaceAll("\\+", "*") + "_";
	// scriptFileNames.add(vznCommCliNameStartsWith + "*COMM_*.*");
	//
	// if(commission_templates_path !=null)
	// {
	// for (File file : rfFiles) {
	// String fileExtension = FilenameUtils.getExtension(file.getPath());
	// if (fileExtension.equalsIgnoreCase("sh")) {
	// scriptFileNames.add(file.getName());
	// }
	// }
	// }
	//
	//
	// if (rfFiles != null) {
	// for (File file : rfFiles) {
	// String fileExtension = FilenameUtils.getExtension(file.getPath());
	// if (fileExtension.equalsIgnoreCase("sh")) {
	// scriptFileNames.add(file.getName());
	// }
	// }
	// }
	// CheckListScriptDetEntity checkListScriptDetEntity =
	// useCaseBuilderRepositoryImpl
	// .getCheckListDetails(programId);
	// if (checkListScriptDetEntity == null) {
	// return CommonUtil.buildResponseJson(Constants.FAIL,
	// GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_EMPTY_PROGRAM),
	// sessionId, serviceToken);
	// }
	//
	// if (scriptFileNames != null) {
	// for (String fileName : scriptFileNames) {
	// CheckListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
	// fileName, " ",
	// checkListScriptDetEntity.getCheckListFileName());
	// if (CheckListScriptDetails == null || CheckListScriptDetails.isEmpty()) {
	// // return CommonUtil.buildResponseJson(Constants.FAIL,
	// //
	// GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_EMPTY_FILE)
	// // + " " +fileName, sessionId, serviceToken);
	// }
	// if (CheckListScriptDetails.size() > 1) {
	// // return CommonUtil.buildResponseJson(Constants.FAIL,
	// //
	// GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_DUPLICATE)
	// // + " " +fileName, sessionId, serviceToken);
	// }
	// }
	// }
	// resultMap.put("status", Constants.SUCCESS);
	// } catch (Exception e) {
	// resultMap.put("status", Constants.FAIL);
	// logger.error("Exception in GenerateCsvController.getScriptSequenceDetails() "
	// + ExceptionUtils.getFullStackTrace(e));
	// e.printStackTrace();
	// }
	// return resultMap;
	// }

	private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			long bytesRead = 0;
			byte[] bytesIn = new byte[1024];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}
			zos.closeEntry();
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getScriptSequenceDetails(String ciqFileName, Integer programId, NeMappingEntity neMappingEntity,
			String enbId, String enbName, String sessionId, String serviceToken) {
		JSONObject resultMap = new JSONObject();
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		try {
			StringBuilder rfScriptsPath = new StringBuilder();
			rfScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(enbId).append(Constants.SEPARATOR);

			logger.info("GenerateCsvController.getScriptSequenceDetails() rfScriptsPath: " + rfScriptsPath.toString());

			File rfScriptFolder = new File(rfScriptsPath.toString());
			File[] rfFiles = rfScriptFolder.listFiles();
			List<String> scriptFileNames = new ArrayList<String>();

			String sprintCommCliNameStartsWith = "CLI_COMM_"
					+ neMappingEntity.getSiteConfigType().replaceAll(" ", "").replaceAll("\\+", "*") + "_";
			String sprintCommBashNameStartsWith = "BASH_COMM_"
					+ neMappingEntity.getSiteConfigType().replaceAll(" ", "").replaceAll("\\+", "*") + "_";
			String vznCommCliNameStartsWith = "BASH_COMM_"
					+ neMappingEntity.getSiteConfigType().replaceAll(" ", "").replaceAll("\\+", "*") + "_";

			if (CommonUtil.isValidObject(neMappingEntity)
					&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
					&& (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
							|| neMappingEntity.getSiteConfigType()
									.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script))) {
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_DEACTIVATE_TM9_*.sh");

				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_BSM_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_BSM_FILE_NAME + "*.*");

			} else if (CommonUtil.isValidObject(neMappingEntity)
					&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
					&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_DEACTIVATE_TM9_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_BACKUP_PLD_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_LOCK_CELL_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_BACKHAUL_PORT_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*ENV_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_DISABLE_SBIP_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_DLT_IPVLAN_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_CRTE_IPVLAN_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_CRTE_SBOAMIP_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_CRTE_DLT_ROUTE_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_CHG_ELINK_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_UNLOCK_CELL_*.*");

				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_BSM_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_BSM_FILE_NAME + "*.*");

			} else if (CommonUtil.isValidObject(neMappingEntity)
					&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
					&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_DEACTIVATE_TM9_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_MME_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_ENV_*.*");
				scriptFileNames.add(sprintCommCliNameStartsWith + "*COMM_SCRIPT_*.*");

				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_CSR_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.PRECHECK_BSM_FILE_NAME + "*.*");
				scriptFileNames.add(sprintCommBashNameStartsWith + "*" + Constants.POSTCHECK_BSM_FILE_NAME + "*.*");

			} else if (CommonUtil.isValidObject(neMappingEntity)
					&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
					&& (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
							|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
							|| neMappingEntity.getSiteConfigType()
									.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
				scriptFileNames.add(vznCommCliNameStartsWith + "*COMM_*.*");
			} else if (CommonUtil.isValidObject(neMappingEntity)
					&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
					&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
				scriptFileNames.add(vznCommCliNameStartsWith + "*COMM_*.*");
			}
			if (rfFiles != null) {
				for (File file : rfFiles) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("sh")) {
						scriptFileNames.add(file.getName());
					}
				}
			}
			CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
					.getCheckListDetails(programId);
			if (checkListScriptDetEntity == null) {
				return CommonUtil.buildResponseJson(Constants.FAIL,
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_EMPTY_PROGRAM),
						sessionId, serviceToken);
			}
			String configType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
					&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
					&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
							.getId() == Constants.NW_CONFIG_USM_ID) {
				configType = "";
			}

			if (scriptFileNames != null) {
				for (String fileName : scriptFileNames) {
					CheckListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId, fileName, configType,
							checkListScriptDetEntity.getCheckListFileName());
					if (CheckListScriptDetails == null || CheckListScriptDetails.isEmpty()) {
						// return CommonUtil.buildResponseJson(Constants.FAIL,
						// GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_EMPTY_FILE)
						// + " " +fileName, sessionId, serviceToken);
					}
					if (CheckListScriptDetails.size() > 1) {
						// return CommonUtil.buildResponseJson(Constants.FAIL,
						// GlobalInitializerListener.faultCodeMap.get(FaultCodes.CHECKLIST_SCRIPT_DETAILS_DUPLICATE)
						// + " " +fileName, sessionId, serviceToken);
					}
				}
			}
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in GenerateCsvController.getScriptSequenceDetails() "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}

	public static void writeXMLattribute(File xmlFilePath) {
		String oldString = "xmlns:mid";
		String newString = "xmlns:nc";
		String replaceTag = "mid:managed-element";
		String xmlVerTag = "<?xml version";
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		FileWriter writer = null;
		File xmlFile = xmlFilePath;
		try {
			br = new BufferedReader(new FileReader(xmlFile));
			String line = br.readLine();
			while (line != null) {
				if (!line.contains(xmlVerTag)) {
					if (line.contains("<nc:running> </nc:running>")) {
						sb.append(line.replace("<nc:running> </nc:running>", "<nc:running></nc:running>"));
						sb.append("\n");
					}
					// else if (!(line.contains(replaceTag)) && line.contains(oldString)) {
					// sb.append(line.replace(oldString, newString));
					// sb.append("\n");
					// }
					// else
					// if(line.contains("xmlns:gnbcp=\"urn:ietf:params:xml:ns:netconf:base:1.0\""))
					// {
					// sb.append(line.replace("xmlns:gnbcp", "xmlns:nc"));
					// sb.append("\n");
					// }
					else {
						sb.append(line + "\n");
					}
				}
				line = br.readLine();
			}
			writer = new FileWriter(xmlFile);
			writer.write(sb.toString());
			// System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// anupama
	// create executables for 5G
	@SuppressWarnings("unchecked")
	private JSONObject createExecutableFiles5G(String ciqFileName, Integer programId, String generatedDirpath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String fileType,
			String version) {
		JSONObject resultMap = new JSONObject();
		String mcmip = null;
		try {

			String connectionLocation = Constants.CONNECTION_LOCATION_SM;
			logger.info("createExecutableFiles() called programId: " + programId + ", ciqFileName: " + ciqFileName
					+ ", generatedDirpath: " + generatedDirpath + ", SiteConfigType: "
					+ neMappingEntity.getSiteConfigType() + ", enbId: " + enbId + ", enbName: " + enbName);
			StringBuilder rfScriptsPath = new StringBuilder();
			rfScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(enbId).append(Constants.SEPARATOR);
			File rfScriptFolder = new File(rfScriptsPath.toString());
			
			//For Multidirectory
			StringBuilder multidirRfscripts = new StringBuilder(rfScriptsPath);
			StringBuilder multidirRfscriptsprev = new StringBuilder(rfScriptsPath);
			boolean multidirbool = false;
			multidirRfscripts.append(enbId).append(Constants.SEPARATOR);
			File multidirrfScriptFolder = new File(multidirRfscripts.toString());
			while(multidirrfScriptFolder.exists()) {
				multidirbool = true;
				multidirRfscriptsprev.append(enbId).append(Constants.SEPARATOR);
				multidirRfscripts.append(enbId).append(Constants.SEPARATOR);
				multidirrfScriptFolder = new File(multidirRfscripts.toString());
			}
			if(multidirbool) {
				multidirrfScriptFolder = new File(multidirRfscriptsprev.toString());
				FileUtils.copyDirectory(multidirrfScriptFolder, rfScriptFolder);
				FileUtil.deleteFileOrFolder(rfScriptsPath.append(enbId).append(Constants.SEPARATOR).toString());
			}


			File[] rfFiles = rfScriptFolder.listFiles();

			// for removing rf scripts first line
			try {
				if (rfFiles != null) {
					for (File scripts : rfFiles) {
						if (scripts.toString().contains("xml")) {
							File temp = new File(scripts.toString());
							writeXMLattribute(temp);
						}
					}
				}
			} catch (Exception e) {
				resultMap.put("reason", "rf scripts are not uploaded");
				return resultMap;

			}

			File generatedCommScriptFolder = new File(generatedDirpath.toString());
			File[] generatedCommScriptFiles = generatedCommScriptFolder.listFiles();

			// for test folder to do internal handling of .sh files
			String testPath = null;
			/*
			 * testPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).
			 * append(Constants.CUSTOMER)
			 * .append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
			 * .append(Constants.PRE_MIGRATION_Commission_Test .replace("filename",
			 * StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
			 * .replace("version","20A") .replace("enbId", enbId) .replaceAll(" ",
			 * "_")).append(Constants.SEPARATOR);
			 */
			testPath = generatedDirpath + "Test/";
			File commissionScriptTestFolder = new File(testPath);
			File[] commissionScriptTestScriptFiles = commissionScriptTestFolder.listFiles();
			// String configType = neMappingEntity.getSiteConfigType();
			String configType = null;
			boolean addDefaultCurlRule = false;

			mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
			addDefaultCurlRule = true;
			configType = "";
			// }
			if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
				String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
				List<CIQDetailsModel> listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId,
						dbcollectionFileName);
				if (!fileType.contains("ENDC")) {
					JSONObject finalMap = createBashFromXml5G(rfScriptsPath.toString(), rfFiles, "RF", mcmip, enbId,
							enbName, neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);
					if(finalMap.containsKey("status") && finalMap.containsKey("reason") && finalMap.get("status").equals(Constants.FAIL)) {
						return finalMap;
					}
				}
				
				/*
				 * createBashFromXml5G(generatedDirpath.toString(), generatedCommScriptFiles,
				 * "COMM", mcmip, enbId, enbName, neMappingEntity.getSiteConfigType());
				 */
				//18-Aug-2021 - Commenting to stop usecase generation other than RF scripts.
				/*if (fileType.contains("COMM") || fileType.contains("ALL") || fileType.contains("RANCONFIG")) {
					createBashFromXml5G(testPath, commissionScriptTestScriptFiles, "COMM", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);

					createBashFromXml5G(testPath, commissionScriptTestScriptFiles, "CSL", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);
					createBashFromXml5G(testPath, commissionScriptTestScriptFiles, "ANC", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);
					createBashFromXml5G(testPath, commissionScriptTestScriptFiles, "DCM", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);
				}
				if (fileType.contains("ENDC") || fileType.contains("ALL") || fileType.contains("RANCONFIG")) {
					createBashFromXml5G(testPath, commissionScriptTestScriptFiles, "ENDC", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel);
				}*/
				// for differentiating 5g-5g and 4g-5g scripts
				// File rfScriptFolder1 = new File(rfScriptsPath.toString());
				// File[] rfFiles1 = rfScriptFolder.listFiles();
				// anupama
				JSONObject fileProcessResult = new JSONObject();
				// System.out.println(finalMap.get("timeStamp").toString());
				if (fileType.contains("COMM") || fileType.contains("ALL") || fileType.contains("RANCONFIG")) {
					fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"RFNBR", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					
					// fileProcessResult = runTestController.ConstantScript5G(programId,
					// ciqFileName, enbId, sessionId,
					// "CSL", connectionLocation, configType, addDefaultCurlRule);
					// if (fileProcessResult != null && fileProcessResult.containsKey("status")
					// && fileProcessResult.get("status").equals(Constants.FAIL)) {
					// resultMap.put("status", Constants.FAIL);
					// if (fileProcessResult.containsKey("reason")) {
					// resultMap.put("reason", fileProcessResult.get("reason"));
					// }
					// return resultMap;
					// }
					//18-Aug-2021 - Commenting to stop usecase generation other than RF scripts.
					/*fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"A1A2", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}

					fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"AUCOMM", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}*/
					/*fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"AUGPSCRIPT", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}*/
					//18-Aug-2021 - Commenting to stop usecase generation other than RF scripts.
					/*fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"CSL", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"ANC", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}*/
				}
				//18-Aug-2021 - Commenting to stop usecase generation other than RF scripts.
				/*if (fileType.contains("ENDC") || fileType.contains("ALL") || fileType.contains("RANCONFIG")) {
					fileProcessResult = runTestController.ConstantScript5G(programId, ciqFileName, enbId, sessionId,
							"ENDC", connectionLocation, configType, addDefaultCurlRule, null, version, fileType);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
				}*/

				else {
					logger.info("GenerateCsvController.createExecutableFiles5G() mcmip is not found");
				}
			}
		}

		catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createExecutableFiles5GDSS(String ciqFileName, Integer programId, String generatedDirpath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String fileType,
			String version, String programName) {
		JSONObject resultMap = new JSONObject();
		String mcmip = null;
		try {
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			List<CIQDetailsModel> listCIQDetailsModel = null;
			if(programName.contains("5G-DSS")) {
				listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
			} else {
				listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId,
						dbcollectionFileName);
			}
			
			String neid = "";
			if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
				if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NEID")) {
					neid = listCIQDetailsModel.get(0).getCiqMap().get("NEID").getHeaderValue();
					neid = neid.replaceAll("^0+(?!$)", "");
				}
			}
			String connectionLocation = Constants.CONNECTION_LOCATION_SM;
			logger.info("createExecutableFilesDSS() called programId: " + programId + ", ciqFileName: " + ciqFileName
					+ ", generatedDirpath: " + generatedDirpath + ", SiteConfigType: "
					+ neMappingEntity.getSiteConfigType() + ", enbId: " + enbId + ", enbName: " + enbName);

			StringBuilder rfScriptsPath = new StringBuilder();
			rfScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(neid).append(Constants.SEPARATOR);
			File rfScriptFolder = new File(rfScriptsPath.toString());

			StringBuilder rfScriptsPathdest = new StringBuilder();
			rfScriptsPathdest.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(enbId).append(Constants.SEPARATOR);
			File rfScriptFolderdest = new File(rfScriptsPathdest.toString());
			
			//For Multidirectory
			StringBuilder multidirRfscripts = new StringBuilder(rfScriptsPath);
			StringBuilder multidirRfscriptsprev = new StringBuilder(rfScriptsPath);
			boolean multidirbool = false;
			multidirRfscripts.append(neid).append(Constants.SEPARATOR);
			File multidirrfScriptFolder = new File(multidirRfscripts.toString());
			while(multidirrfScriptFolder.exists()) {
				multidirbool = true;
				multidirRfscriptsprev.append(neid).append(Constants.SEPARATOR);
				multidirRfscripts.append(neid).append(Constants.SEPARATOR);
				multidirrfScriptFolder = new File(multidirRfscripts.toString());
			}
			if(multidirbool) {
				multidirrfScriptFolder = new File(multidirRfscriptsprev.toString());
				FileUtils.copyDirectory(multidirrfScriptFolder, rfScriptFolder);
				FileUtil.deleteFileOrFolder(rfScriptsPath.append(neid).append(Constants.SEPARATOR).toString());
			}
			
			if (rfScriptFolder.exists()) {
				if (!programName.contains("5G-DSS")) {
				deleteDuplicateScriptsDSS(rfScriptFolder);
				}
				if(!neid.equals(enbId)) {
					if (rfScriptFolderdest.exists()) {
						FileUtil.deleteFileOrFolder(rfScriptFolderdest.getPath());
					}
					FileUtil.renameDir(rfScriptFolder.getPath(), rfScriptFolderdest.getPath());
				}				
			} else if (!rfScriptFolderdest.exists()) {
				logger.info("Exception in GenerateCsvController.createExecutableFiles5GDSS() ");
				resultMap.put("reason", "No RF scripts found for NE ID: " + neid);
				resultMap.put("status", Constants.FAIL);
				return resultMap;
			}

			File[] rfFiles = rfScriptFolderdest.listFiles();

			File generatedCommScriptFolder = new File(generatedDirpath.toString());
			File[] generatedCommScriptFiles = generatedCommScriptFolder.listFiles();

			// for test folder to do internal handling of .sh files
			String testPath = null;
			/*
			 * testPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).
			 * append(Constants.CUSTOMER)
			 * .append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
			 * .append(Constants.PRE_MIGRATION_Commission_Test .replace("filename",
			 * StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
			 * .replace("version","20A") .replace("enbId", enbId) .replaceAll(" ",
			 * "_")).append(Constants.SEPARATOR);
			 */
			testPath = generatedDirpath + "Test/";
			File commissionScriptTestFolder = new File(testPath);
			File[] commissionScriptTestScriptFiles = commissionScriptTestFolder.listFiles();
			// String configType = neMappingEntity.getSiteConfigType();
			String configType = null;
			boolean addDefaultCurlRule = false;

			mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
			addDefaultCurlRule = true;
			configType = "";
			// }
			if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
				if(programName.contains("5G-CBAND")) {
					List<CIQDetailsModel> listCIQsheetDetailsModel = null;
					resultMap = createBashFromXml5GCBand(rfScriptsPathdest.toString(), rfFiles, "RF", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel,
							listCIQsheetDetailsModel);
				} else {
					List<CIQDetailsModel> listCIQsheetDetailsModel = fileUploadRepository
							.getEnbTableSheetDetailss(ciqFileName, "DSS_MOP_Parameters-1", neid, dbcollectionFileName);
					resultMap = createBashFromXml5GDSS(rfScriptsPathdest.toString(), rfFiles, "RF", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel,
							listCIQsheetDetailsModel);
				}
				if(resultMap.containsKey("status") && resultMap.get("status").equals(Constants.FAIL)) {
					return resultMap;
				}
				
				JSONObject fileProcessResult = new JSONObject();
				// System.out.println(finalMap.get("timeStamp").toString());
				fileProcessResult = runTestController.ConstantScript5GDSS(programId, ciqFileName, enbId, sessionId,
						"Extended", connectionLocation, configType, addDefaultCurlRule, null, version);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						resultMap.put("reason", fileProcessResult.get("reason"));
					}
					return resultMap;
				}
				
				if(!programName.contains("5G-CBAND")) {
					fileProcessResult = runTestController.ConstantScript5GDSS(programId, ciqFileName, enbId, sessionId,
							"Rollback", connectionLocation, configType, addDefaultCurlRule, null, version);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
				}
				
				fileProcessResult = runTestController.ConstantScript5GDSS(programId, ciqFileName, enbId, sessionId,
						"Cutover", connectionLocation, configType, addDefaultCurlRule, null, version);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						resultMap.put("reason", fileProcessResult.get("reason"));
					}
					return resultMap;
				}
				
				fileProcessResult = runTestController.ConstantScript5GDSS(programId, ciqFileName, enbId, sessionId,
						"Pre-Check", connectionLocation, configType, addDefaultCurlRule, null, version);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						resultMap.put("reason", fileProcessResult.get("reason"));
					}
					return resultMap;
				}	
				

			} else {
				logger.info("GenerateCsvController.createExecutableFiles5G() mcmip is not found");
			}
		}

		catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createExecutableFiles(String ciqFileName, Integer programId, String generatedDirpath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId) {
		JSONObject resultMap = new JSONObject();
		try {

			String connectionLocation = Constants.CONNECTION_LOCATION_NE;
			logger.info("createExecutableFiles() called programId: " + programId + ", ciqFileName: " + ciqFileName
					+ ", generatedDirpath: " + generatedDirpath + ", SiteConfigType: "
					+ neMappingEntity.getSiteConfigType() + ", enbId: " + enbId + ", enbName: " + enbName);
			StringBuilder rfScriptsPath = new StringBuilder();
			StringBuilder EndcScriptsPath = new StringBuilder();
			rfScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(enbId).append(Constants.SEPARATOR);
			EndcScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
			.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
			.append(Constants.PRE_MIGRATION_SCRIPT
					.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
					.replaceAll(" ", "_"))
			.append(enbId +"_ENDC").append(Constants.SEPARATOR);
			File rfScriptFolders = new File(rfScriptsPath.toString());
			File EndcScriptFolders = new File(EndcScriptsPath.toString());
			//For Multidirectory
			StringBuilder multidirRfscripts = new StringBuilder(rfScriptsPath);
			StringBuilder multidirRfscriptsprev = new StringBuilder(rfScriptsPath);
			StringBuilder multidirEndcscripts = new StringBuilder(EndcScriptsPath);
			StringBuilder multidirEndcscriptsprev = new StringBuilder(EndcScriptsPath);
			boolean multidirbool = false;
			boolean multiEndcdirbool = false;
			multidirRfscripts.append(enbId).append(Constants.SEPARATOR);
			File multidirrfScriptFolder = new File(multidirRfscripts.toString());
			while(multidirrfScriptFolder.exists()) {
				multidirbool = true;
				multidirRfscriptsprev.append(enbId).append(Constants.SEPARATOR);
				multidirRfscripts.append(enbId).append(Constants.SEPARATOR);
				multidirrfScriptFolder = new File(multidirRfscripts.toString());
			}
			if(multidirbool) {
				multidirrfScriptFolder = new File(multidirRfscriptsprev.toString());
				FileUtils.copyDirectory(multidirrfScriptFolder, rfScriptFolders);
				FileUtil.deleteFileOrFolder(rfScriptsPath.append(enbId).append(Constants.SEPARATOR).toString());
			}
			
			File[] rfFiless = rfScriptFolders.listFiles();
			
			multidirEndcscripts.append(enbId +"_ENDC").append(Constants.SEPARATOR);
			File multidirEndcScriptFolder = new File(multidirEndcscripts.toString());
			while(multidirEndcScriptFolder.exists()) {
				multiEndcdirbool = true;
				multidirEndcscriptsprev.append(enbId +"_ENDC").append(Constants.SEPARATOR);
				multidirEndcscripts.append(enbId +"_ENDC").append(Constants.SEPARATOR);
				multidirEndcScriptFolder = new File(multidirEndcscripts.toString());
			}
			if(multiEndcdirbool) {
				multidirEndcScriptFolder = new File(multidirEndcscriptsprev.toString());
				FileUtils.copyDirectory(multidirEndcScriptFolder, EndcScriptFolders);
				FileUtil.deleteFileOrFolder(EndcScriptsPath.append(enbId +"_ENDc").append(Constants.SEPARATOR).toString());
			}
			
			File[] EndcFiless = EndcScriptFolders.listFiles();
			
			try {
				if (rfFiless != null) {
					for (File scripts : rfFiless) {
						if (scripts.toString().contains("xml")) {
							File temp = new File(scripts.toString());
							writeXMLattribute(temp);
						}
					}
				}
			} catch (Exception e) {
				resultMap.put("reason", "rf scripts are not uploaded");
				return resultMap;

			}
			try {
				if (EndcFiless != null) {
					for (File scripts : EndcFiless) {
						if (scripts.toString().contains("xml")) {
							File temp = new File(scripts.toString());
							writeXMLattribute(temp);
						}
					}
				}
				else {
					resultMap.put("reason", "RF Scripts Generated;ENDC RF Scripts Not Uploaded");
					
					
				}
			} catch (Exception e) {
				resultMap.put("reason", "ENDC RF Scripts Not Uploaded");
				return resultMap;

			}
			if(EndcFiless != null && rfFiless !=null) {
				resultMap.put("reason", "RF Scripts Generated; ENDC RF Scripts Generated;");
			}
			if(EndcFiless == null && rfFiless ==null) {
				resultMap.put("reason", "RF Scripts Not Uploaded; ENDC RF Scripts Not Uploaded;");
			}
			if(EndcFiless != null && rfFiless ==null) {
				resultMap.put("reason", "RF Scripts Not Uploaded; ENDC RF Scripts Generated;");
			}
			
			String csrSleep = "";
			String bsmSleep = "";

			List<GrowConstantsEntity> objListProgDetails = fileUploadRepository.getGrowConstantsDetails();

			List<GrowConstantsEntity> csrCmds = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_CMD)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toList());

			Map<String, String> csrSleepMap = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

			List<GrowConstantsEntity> bsmCmds = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_CMD)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toList());

			Map<String, String> bsmSleepMap = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

			csrSleep = csrSleepMap.get(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME);
			bsmSleep = bsmSleepMap.get(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME);

			String btsId = neMappingEntity.getBtsId();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {

				CommonUtil.createVbsFromCmds(
						generatedDirpath.toString() + Constants.SEPARATOR + Constants.PRECHECK_CSR_FILE_NAME + ".vbs",
						csrCmds, csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						generatedDirpath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_CSR_FILE_NAME + ".vbs",
						csrCmds, csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						generatedDirpath.toString() + Constants.SEPARATOR + Constants.PRECHECK_BSM_FILE_NAME + ".vbs",
						bsmCmds, bsmSleep, btsId);
				CommonUtil.createVbsFromCmds(
						generatedDirpath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_BSM_FILE_NAME + ".vbs",
						bsmCmds, bsmSleep, btsId);

			}

			File rfScriptFolder = new File(rfScriptsPath.toString());
			File[] rfFiles = rfScriptFolder.listFiles();
			

			File generatedCommScriptFolder = new File(generatedDirpath.toString());
			File[] generatedCommScriptFiles = generatedCommScriptFolder.listFiles();
			
			File EndcScriptFolder = new File(EndcScriptsPath.toString());
			File[] EndcFiles = EndcScriptFolder.listFiles();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {

				ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(programId,
						Constants.SCRIPT_STORE_TEMPLATE);

				if (programTemplateEntity != null && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {

					createCliFromBatch(rfScriptsPath.toString(), rfFiles, "RF",
							neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion(),
							neMappingEntity.getNetworkConfigEntity().getNeRelVersion(),
							neMappingEntity.getSiteConfigType(), programTemplateEntity, ciqFileName, enbId, enbName,
							sessionId);

					createCliFromBatch(generatedDirpath.toString(), generatedCommScriptFiles, "COMM",
							neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion(),
							neMappingEntity.getNetworkConfigEntity().getNeRelVersion(),
							neMappingEntity.getSiteConfigType(), programTemplateEntity, ciqFileName, enbId, enbName,
							sessionId);

					createBashFromVbs(rfScriptsPath.toString(), rfFiles, "RF", neMappingEntity.getSiteConfigType(),
							programTemplateEntity, ciqFileName, enbId, enbName, sessionId);

					createBashFromVbs(generatedDirpath.toString(), generatedCommScriptFiles, "COMM",
							neMappingEntity.getSiteConfigType(), programTemplateEntity, ciqFileName, enbId, enbName,
							sessionId);

					JSONObject fileProcessResult = new JSONObject();

					String configType = neMappingEntity.getSiteConfigType();
					boolean addDefaultCurlRule = false;
					if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
							&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
							&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
									.getId() == Constants.NW_CONFIG_USM_ID) {
						addDefaultCurlRule = true;
						configType = "";
					}

					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId, "RF",
							connectionLocation, configType, addDefaultCurlRule, "nothing");
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
							"COMM", connectionLocation, configType, addDefaultCurlRule, "nothing");
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}

				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROVIDE_PROGRAM_TEMPLATE_DETAILS)
									+ " For " + Constants.SCRIPT_STORE_TEMPLATE);
					return resultMap;
				}
			}

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO)) {
				connectionLocation = Constants.CONNECTION_LOCATION_SM;
				String mcmip = "";
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_VLSM_ID
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : neMappingEntity.getNetworkConfigEntity()
							.getNeDetails()) {
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
				String configType = neMappingEntity.getSiteConfigType();
				boolean addDefaultCurlRule = false;
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_USM_ID) {
					mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
					addDefaultCurlRule = true;
					configType = "";
				}
				if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
					if (rfFiles != null) {
					createBashFromXml(rfScriptsPath.toString(), rfFiles, programId,"RF", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType());
					}
					if (EndcFiless != null) {
					createBashFromXml(EndcScriptsPath.toString(), EndcFiles, programId,"ENDC", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType());
					}
					/*if (rfFiles != null) {
					createBashFromXml(generatedDirpath.toString(), generatedCommScriptFiles,programId, "COMM", mcmip, enbId,
							enbName, neMappingEntity.getSiteConfigType());
					}*/
					JSONObject fileProcessResult = new JSONObject();
					if (rfFiles != null) {
					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId, "RF",
							connectionLocation, configType, addDefaultCurlRule, "nothing");
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					}
					if (EndcFiless != null) {
					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId, "ENDC",
							connectionLocation, configType, addDefaultCurlRule, "nothing");
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					}
					/*if (rfFiles != null) {
					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
							"COMM", connectionLocation, configType, addDefaultCurlRule, "nothing");
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					}*/
				} else {
					logger.info("GenerateCsvController.createExecutableFiles() mcmip is not found");
				}
			}

		} catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	// Vijay's Code
	// Has to remove other's Code
	@SuppressWarnings("uncheckedh")
	private JSONObject createExecutableFilesForNEGrow(String ciqFileName, Integer programId, String csvPath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String place, String programName, List<JSONObject> fileGenerateResultList) {
		JSONObject resultMap = new JSONObject();
		try {

			String connectionLocation = Constants.CONNECTION_LOCATION_NE;
			logger.info("createExecutableFiles() called programId: " + programId + ", ciqFileName: " + ciqFileName
					+ ", generatedDirpath: " + csvPath + ", SiteConfigType: " + neMappingEntity.getSiteConfigType()
					+ ", enbId: " + enbId + ", enbName: " + enbName);
			StringBuilder csvScriptsPath = new StringBuilder();
			if (programName.contains("5G")) {
				if (csvPath.contains("20A")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("20C")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("21A")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("21B")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21B").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21B").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("21C")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				}else if (csvPath.contains("21D")) {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21D").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "21D").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("22A")) {//22A
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "22A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "22A").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else if (csvPath.contains("22C")) {//22A
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "22C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "22C").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				} else {
					if (!place.equalsIgnoreCase("ALL")) {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20B").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					} else {
						csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(
										programId)
								.append(Constants.SEPARATOR)
								.append(Constants.PRE_MIGRATION_TEMPLATE_ALL
										.replace("filename",
												StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
										.replace("enbId", enbId).replace("version", "20B").replaceAll(" ", "_"));
						csvPath = csvScriptsPath.toString();
					}

				}

			} else {
				csvScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
						.append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replaceAll(" ", "_"))
						.append(enbId).append(Constants.SEPARATOR);
			}

			String csrSleep = "";
			String bsmSleep = "";

			List<GrowConstantsEntity> objListProgDetails = fileUploadRepository.getGrowConstantsDetails();

			List<GrowConstantsEntity> csrCmds = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_CMD)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toList());

			Map<String, String> csrSleepMap = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

			List<GrowConstantsEntity> bsmCmds = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_CMD)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toList());

			Map<String, String> bsmSleepMap = objListProgDetails.stream()
					.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME)
							&& X.getProgramDetailsEntity().getId().equals(programId))
					.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

			csrSleep = csrSleepMap.get(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME);
			bsmSleep = bsmSleepMap.get(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME);

			String btsId = neMappingEntity.getBtsId();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {

				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);

			}

			File rfScriptFolder = new File(csvScriptsPath.toString());
			File[] rfFiles = rfScriptFolder.listFiles();

			File generatedCSVFolder = new File(csvPath.toString());
			File[] generatedCSVFiles = generatedCSVFolder.listFiles();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO)) {
				connectionLocation = Constants.CONNECTION_LOCATION_SM;
				String mcmip = "";
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_VLSM_ID
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : neMappingEntity.getNetworkConfigEntity()
							.getNeDetails()) {
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
				String configType = neMappingEntity.getSiteConfigType();
				boolean addDefaultCurlRule = false;
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_USM_ID) {
					mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
					addDefaultCurlRule = true;
					configType = "";
				}
				if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
					List<CIQDetailsModel> listCIQDetailsModel = null;
					if (programName.contains("5G")) {
						String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId),
								ciqFileName);
						listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId, dbcollectionFileName);
					}
					createBashFromCSV(csvPath, generatedCSVFiles, "CSV", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), listCIQDetailsModel,fileGenerateResultList);
					
					//degrow 
					createBashFromDegrow(csvPath, "DEGROW", mcmip, enbId, enbName,programName);
					int DeletionDays=0;
					OvAutomationModel ovAutomationModel = customerService.getOvAutomationTemplate();
					if (ovAutomationModel != null) { 
						
						DeletionDays=ovAutomationModel.getDeletionDays();
					}
					for(int i=0;i<=DeletionDays;i++) {
						String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
				        String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
				        String bashFileName = "NeCreationTimeBashFile"+i+"_"+ enbId+ dateString+".sh" ;
				        LocalDate todayDate = LocalDate.now();
				        LocalDate Date1 =todayDate.minusDays(i);
				       String  Date=Date1.toString();
					createBashForYangCommand(csvPath, generatedCSVFiles, "YANG", mcmip, enbId, enbName,programName,Date,bashFileName);
					}
					createBashForPackageInventory(csvPath, generatedCSVFiles, "YANG", mcmip, enbId, enbName,programName,"","");
					JSONObject fileProcessResult = new JSONObject();
					if (!csvPath.contains("AU")) {
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"GrowCell", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"GrowEnb", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"DeGrow", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"NECREATION", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"pnp", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
					} else {

						if (csvPath.contains("20B")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU20B", connectionLocation, configType, addDefaultCurlRule, csvPath);
						} else if (csvPath.contains("20C")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU20C", connectionLocation, configType, addDefaultCurlRule, csvPath);
						} else if (csvPath.contains("21A")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU21A", connectionLocation, configType, addDefaultCurlRule, csvPath);
						} else if (csvPath.contains("21B")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU21B", connectionLocation, configType, addDefaultCurlRule, csvPath);
						} else if (csvPath.contains("21C")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU21C", connectionLocation, configType, addDefaultCurlRule, csvPath);
						} else if (csvPath.contains("21D")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU21D", connectionLocation, configType, addDefaultCurlRule, csvPath);
						}//22A  
						else if (csvPath.contains("22A")) {
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
								sessionId, "AU22A", connectionLocation, configType, addDefaultCurlRule, csvPath);
						}else if (csvPath.contains("22C")) {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU22C", connectionLocation, configType, addDefaultCurlRule, csvPath);
							}
						
						else {
							fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId,
									sessionId, "AU20A", connectionLocation, configType, addDefaultCurlRule, csvPath);
						}
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"AUCaCell", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"DeGrow5G", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"NECREATION5G", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"pnp5G", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
					}
				} else {
					logger.info("GenerateCsvController.createExecutableFiles() mcmip is not found");
				}
			}

		} catch (

		Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	private JSONObject createBashForYangCommand(String csvPath, File[] generatedCSVFiles, String fileType, String mcmip,
			String enbId, String enbName, String programName,String Date ,String BashFileName ) {
		// TODO Auto-generated method stub
		
		 JSONObject resultMap = new JSONObject();
        
        String csvFilePath = csvPath;
        try {
        	String Type="";
        	if (programName.contains("5G-MM")) {
	        	Type="DU_";
	        }else if (programName.contains("4G-USM-LIVE"))
	        {
	        	Type="eNB_";
	        }else if(programName.contains("4G-FSU")) {
	        	Type="FSU_";
	        }
            if ("YANG" == fileType) {
                csvFilePath = csvPath + "NeCreationTimeCBashFile/";
                File dest = new File(csvFilePath);
 
                if (!dest.exists()) {
                    FileUtil.createDirectory(csvFilePath);
                }
               // resultMap = CommonUtil.createBashFromDegrow(csvPath , csvFilePath + bashFileName, mcmip, enbId, enbName);
                resultMap = CommonUtil.createBashFromXmlForYang(csvPath, csvFilePath + BashFileName,
						mcmip, enbId, Type,Date);
            }
        } catch (Exception e) {
            logger.error(
                    "Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
        }
		return resultMap;
	}
	
	private JSONObject createBashForPackageInventory(String csvPath, File[] generatedCSVFiles, String fileType, String mcmip,
			String enbId, String enbName, String programName,String Date ,String BashFileName ) {
		// TODO Auto-generated method stub
		
		 JSONObject resultMap = new JSONObject();
		 String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
	        String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
        String BashFileName1="PackageInventory_"+ enbId+ dateString+".sh";
        String csvFilePath = csvPath;
        try {
        	String Type="";
        	if (programName.contains("5G-MM")) {
	        	Type="DU_";
	        }else if (programName.contains("4G-USM-LIVE"))
	        {
	        	Type="eNB_";
	        }else if(programName.contains("4G-FSU")) {
	        	Type="FSU_";
	        }
            if ("YANG" == fileType) {
                csvFilePath = csvPath + "NeCreationTimeCBashFile/";
                File dest = new File(csvFilePath);
 
                if (!dest.exists()) {
                    FileUtil.createDirectory(csvFilePath);
                }
               // resultMap = CommonUtil.createBashFromDegrow(csvPath , csvFilePath + bashFileName, mcmip, enbId, enbName);
                resultMap = CommonUtil.createBashFromXmlForpackageinventory(csvPath, csvFilePath + BashFileName1,
						mcmip, enbId, Type,Date);
            }
        } catch (Exception e) {
            logger.error(
                    "Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
        }
		return resultMap;
	}

	private JSONObject createBashFromCSV(String filePath, File[] files, String fileType, String mcmip, String enbId,
			String endName, String siteConfigType, List<CIQDetailsModel> listCIQDetailsModel, List<JSONObject> fileGenerateResultList) {
		String pnpFileName = "";
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String bashFileName1 = " ";
		String bashFileName2 = " ";
		String bashFileName3 = " ";

		try {
			logger.info("createBashFromCSV() called filePath: " + filePath + ", fileType: " + fileType + ", mcmip: "
					+ mcmip);
			List<String> filenameList = new ArrayList<>();
			for (JSONObject object : fileGenerateResultList) {
				if (object.get("fileName") != null && StringUtils.isNotEmpty((String) object.get("fileName"))) {
					filenameList.add(object.get("fileName").toString());
				}
			}
			
			if (files != null) {
				for (File file : files) {
					if(!filenameList.contains(file.getName())) {
						continue;
					}
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_CaCell")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_CaCell")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_20B")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_20B")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_20A")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_20A")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					}else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_20C")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_20C")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_21A")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_21A")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					}else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_21B")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_21B")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_21C")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_21C")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					}else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_21D")) {
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_21D")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_22A")) { //22A
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_22A")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					} else if (fileExtension.equalsIgnoreCase("csv") && file.getName().toString().contains("AU_22C")) { //22A
						if (pnpFileName.isEmpty()) {
							pnpFileName = pnpFileName + file.getName().toString() + ",";
						} else if (!pnpFileName.isEmpty() && !pnpFileName.contains("AU_22C")) {
							pnpFileName = pnpFileName + file.getName().toString();
						}
					}

				}

			}

			if (files != null) {
				for (File file : files) {
					if(!filenameList.contains(file.getName())) {
						continue;
					}
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if ("CSV" == fileType) {
						dateString = "";
					}
					if (fileExtension.equalsIgnoreCase("csv")) {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						// bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
						// + StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".") + dateString
								+ ".sh";
						String csvFile = file.getName().toString();
						String csvFilePath = null;
						if (csvFile.contains("GROW_ENB")) {
							csvFilePath = filePath + "GrowEnbBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("GROW_CELL")) {
							csvFilePath = filePath + "GrowCellBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_CaCell")) {
							csvFilePath = filePath + "AUCaCellBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");

							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";

							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_20B")) {
							csvFilePath = filePath + "AU20BBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_20A")) {
							csvFilePath = filePath + "AU20ABashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_20C")) {
							csvFilePath = filePath + "AU20CBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_21A")) {
							csvFilePath = filePath + "AU21ABashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_21B")) {
							csvFilePath = filePath + "AU21BBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_21C")) {
							csvFilePath = filePath + "AU21CBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_21D")) {
							csvFilePath = filePath + "AU21DBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("AU_22A")) { //22A
							csvFilePath = filePath + "AU22ABashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						}else if (csvFile.contains("AU_22C")) { //22A
							csvFilePath = filePath + "AU22CBashFile/";
							/*
							 * bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_" +
							 * StringUtils.substringBeforeLast(file.getName(), ".");
							 */
							bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".");
							bashFileName1 = StringUtils.substringBeforeLast(bashFileName, "_");
							bashFileName2 = StringUtils.substringBeforeLast(bashFileName1, "_");
							bashFileName3 = StringUtils.substringBeforeLast(bashFileName2, "_");
							bashFileName = bashFileName3 + ".sh";
							File dest = new File(csvFilePath);
							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						} else if (csvFile.contains("pnp_macro")) {
							if (filePath.contains("20A")) {
								csvFilePath = filePath + "/pnp20ABashFile/";
							} else if (filePath.contains("20B")) {
								csvFilePath = filePath + "/pnp20BBashFile/";
							} else if (filePath.contains("20C")) {
								csvFilePath = filePath + "/pnp20CBashFile/";
							} else if (filePath.contains("21A")) {
								csvFilePath = filePath + "/pnp21ABashFile/";
							} else if (filePath.contains("21B")) {
								csvFilePath = filePath + "/pnp21BBashFile/";
							} else if (filePath.contains("21C")) {
								csvFilePath = filePath + "/pnp21CBashFile/";
							}else if (filePath.contains("21D")) {
								csvFilePath = filePath + "/pnp21DBashFile/";
							} else if (filePath.contains("22A")) { //22A
								csvFilePath = filePath + "/pnp22ABashFile/";
							}else if (filePath.contains("22C")) { //22A
								csvFilePath = filePath + "/pnp22CBashFile/";
							}
							else {
								csvFilePath = filePath + "/pnpBashFile/";
							}
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}
						}
						if (csvFile.contains("pnp_macro") && filePath.contains("AU")) {
							if (filePath.contains("20B")) {
								bashFileName = bashFileName.replace("pnp", "pnp20B");
							} else if (filePath.contains("20C")) {
								bashFileName = bashFileName.replace("pnp", "pnp20C");
							}
							else if (filePath.contains("21A")) {
								bashFileName = bashFileName.replace("pnp", "pnp21A");
							}else if (filePath.contains("21B")) {
								bashFileName = bashFileName.replace("pnp", "pnp21B");
							} else if (filePath.contains("21C")) {
								bashFileName = bashFileName.replace("pnp", "pnp21C");
							}else if (filePath.contains("21D")) {
								bashFileName = bashFileName.replace("pnp", "pnp21D");
							} else if (filePath.contains("22A")) { //22A
								bashFileName = bashFileName.replace("pnp", "pnp22A");
							}else if (filePath.contains("22C")) { //22A
								bashFileName = bashFileName.replace("pnp", "pnp22C");
							}
							else {
								bashFileName = bashFileName.replace("pnp", "pnp20A");
							}
							resultMap = CommonUtil.createBashFromCSV(filePath + pnpFileName, csvFilePath + bashFileName,
									mcmip, enbId, listCIQDetailsModel, endName);
						} else {
							resultMap = CommonUtil.createBashFromCSV(filePath + file.getName(),
									csvFilePath + bashFileName, mcmip, enbId, listCIQDetailsModel, endName);
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
//degrow
	 private JSONObject createBashFromDegrow(String filePath, String fileType, String mcmip, String enbId,
	            String enbName, String programName) {
	    //    String pnpFileName = "";
	        JSONObject resultMap = new JSONObject();
	        String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
	        String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
	        String bashFileName = "DeGrowBashFile_"+ enbId+ dateString+".sh" ;
	        String Type="";
	        if (programName.contains("5G-MM")) {
	        	Type="DU_";
	        }else if (programName.contains("4G-USM-LIVE"))
	        {
	        	Type="eNB_";
	        }else if(programName.contains("4G-FSU")) {
	        	Type="FSU_";
	        }
	        String csvFilePath = filePath;
	        try {
	            if ("DEGROW" == fileType) {
	                csvFilePath = filePath + "DeGrowBashFile/";
	                File dest = new File(csvFilePath);
	 
	                if (!dest.exists()) {
	                    FileUtil.createDirectory(csvFilePath);
	                }
	                resultMap = CommonUtil.createBashFromDegrow(filePath , csvFilePath + bashFileName, mcmip, enbId, enbName,Type);
	            }
	        } catch (Exception e) {
	            logger.error(
	                    "Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
	        }
	 
	        return resultMap;
	    }
	
	
	
	
	private JSONObject createBashFromXml(String filePath, File[] files, Integer pId, String fileType, String mcmip, String enbId,
			String endName, String siteConfigType) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String folderName = null;
		try {
			logger.info("createBashFromXml() called filePath: " + filePath + ", fileType: " + fileType + ", mcmip: "
					+ mcmip);
			String[] CASeq = null;
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
					Constants.SEQUENCE_NUMBER_TEMPLATE);
			if(CommonUtil.isValidObject(programTemplateEntity) && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
				try {
					JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
					if(objData.containsKey("CA")) {
						CASeq = objData.get("CA").toString().trim().split(",");
					}
					
				}catch(Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
			if(files.length != 0) {
				if(fileType == "ENDC") {
					FileUtil.deleteFileOrFolder(filePath + "ENDC/");
				}else
				{
				FileUtil.deleteFileOrFolder(filePath + "RF/");
				//FileUtil.deleteFileOrFolder(filePath + "Extended/");
			}
			}
			if (files != null) {
				for (File file : files) {
					siteConfigType = siteConfigType.replaceAll(" ", "");
					String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
					if (fileName.contains("COMM")) {
						String fileExtension = FilenameUtils.getExtension(file.getPath());
						if ("COMM" == fileType) {
							dateString = "";
						}
						if (fileExtension.equalsIgnoreCase("xml")) {
							siteConfigType = siteConfigType.replaceAll(" ", "");
							bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
									+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
							folderName = "RF/";
							resultMap = CommonUtil.createBashFromXml(filePath + file.getName(), filePath + folderName + bashFileName,
									mcmip, enbId);
						}
						
					}
					else if(fileType == "ENDC") {
						String fileExtension = FilenameUtils.getExtension(file.getPath());
						if (fileExtension.equalsIgnoreCase("xml")) {
							siteConfigType = siteConfigType.replaceAll(" ", "");
							bashFileName = "BASH_" + fileType +  "_"
									+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
							folderName = "ENDC/";
							resultMap = CommonUtil.createBashFromXml(filePath + file.getName(), filePath + folderName + bashFileName,
									mcmip, enbId);
						}
					}
					else {
					String[] filenameSplit = fileName.split("[\\-\\_]");
					int seqno = NumberUtils.toInt(filenameSplit[0]); 
					if (!inSeq(seqno, CASeq)) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if ("COMM" == fileType) {
						dateString = "";
					}
					if (fileExtension.equalsIgnoreCase("xml")) {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
								+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						folderName = "RF/";
						resultMap = CommonUtil.createBashFromXml(filePath + file.getName(), filePath + folderName + bashFileName,
								mcmip, enbId);
					}
				}
				}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in GenerateCsvController.createBashFromXml() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	private JSONObject createBashFromXml5G(String filePath, File[] files, String fileType, String mcmip, String enbId,
			String endName, String siteConfigType, Integer pId, String ciqName,
			List<CIQDetailsModel> listCIQDetailsModel) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String folderName = null;
		try {
			logger.info("createBashFromXml() called filePath: " + filePath + ", fileType: " + fileType + ", mcmip: "
					+ mcmip);
			if (files != null) {
				int commCount = 1;
				Set<String> seqNoSet =  new HashSet<>();
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if ("COMM" == fileType) {
						timeStamp = "";
					}
					if (fileExtension.equalsIgnoreCase("xml")) {
						// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
						siteConfigType = siteConfigType.replaceAll(" ", "");
						String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
						Pattern pat = Pattern.compile("(^\\d+)[\\-\\_]+(\\d+)[\\-\\_]+(eNB|ACPF|AUPF|AU|iAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
						Matcher mat = pat.matcher(fileName);
						if (mat.find() || fileName.contains("nr-intra-gnb-nbr")) {
							bashFileName  = fileName + "_" + "RFUsecase" + "_" + enbId
									+ "_" + dateString + ".sh";
							folderName = "NBR/";
							if(mat.find()) {
								String[] filenameSplit = fileName.split("[\\-\\_]");
								if(seqNoSet.contains(filenameSplit[0])) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", "Duplicate Sequence numbers in the RF script filename, Contact RF for fix (Sequence No. : " + filenameSplit[0] + " )");
									return resultMap;
								} else {
									seqNoSet.add(filenameSplit[0]);
								}
							}
						} else {
							if (fileName.contains("A1A2")) {
								bashFileName = "AU_" + fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "A1A2_UseCase/";
							}
							if (fileName.contains("ROUTE") || fileName.contains("offset") || fileName.contains("tilt")) {
								bashFileName = fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "AU_ROUTE_UseCase/";
							}
							if (fileName.contains("DCM")) {
								bashFileName = fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "AU_ROUTE_UseCase/";

							}
							if (fileName.contains("CSL")) {
								bashFileName = fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "CSL_UseCase/";
							}
							if (fileName.contains("ANC")) {
								bashFileName = fileName + dateString + ".sh";
								commCount++;
								folderName = "Anchor_CSL_UseCase/";
							}
							if (fileName.contains("ENDC")) {
								bashFileName = fileName + dateString + ".sh";
								commCount++;
								folderName = "ENDC_UseCase/";
							}
							if (fileName.contains("drb-rlc")) {
								bashFileName = fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "AU_ROUTE_UseCase/";
							}
							/*if(fileName.contains("AU_GPScript")) {
								bashFileName = fileName + "_" + enbId + dateString + ".sh";
								commCount++;
								folderName = "AU_GPScript_UseCase/";
							}*/

						}
						File bash = new File(filePath + folderName);
						bash.mkdir();

						resultMap = CommonUtil.createBashFromXml5G(filePath + file.getName(),
								filePath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, pId, ciqName,
								listCIQDetailsModel);
						resultMap.put("timeStamp", timeStamp);
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in GenerateCsvController.createBashFromXml5G() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	private JSONObject createBashFromXml5GDSS(String filePath, File[] files, String fileType, String mcmip,
			String enbId, String endName, String siteConfigType, Integer pId, String ciqName,
			List<CIQDetailsModel> listCIQDetailsModel, List<CIQDetailsModel> listCIQsheetDetailsModel) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String folderName = null;
		try {
			logger.info("createBashFromXml5GDSS() called filePath: " + filePath + ", fileType: " + fileType
					+ ", mcmip: " + mcmip);
			if (files != null) {
				//Configurable Sequence Number
				String[] precheckSeq = null;
				String[] cutOverSeq = null;
				String[] rollBackSeq = null;
				ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
						Constants.SEQUENCE_NUMBER_TEMPLATE);
				if(CommonUtil.isValidObject(programTemplateEntity) && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
					try {
						JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
						if(objData.containsKey("PreCheck")) {
							precheckSeq = objData.get("PreCheck").toString().trim().split(",");
						}
						if(objData.containsKey("CutOver")) {
							cutOverSeq = objData.get("CutOver").toString().trim().split(",");						
						}
						if(objData.containsKey("RollBack")) {
							rollBackSeq = objData.get("RollBack").toString().trim().split(",");						
						}
					}catch(Exception e) {
						logger.error(ExceptionUtils.getFullStackTrace(e));
					}
				}
				if(files.length != 0) {
					FileUtil.deleteFileOrFolder(filePath + "Pre-Check/");
					FileUtil.deleteFileOrFolder(filePath + "Cutover/");
					FileUtil.deleteFileOrFolder(filePath + "Rollback/");
					FileUtil.deleteFileOrFolder(filePath + "Extended/");
				}
			/*	for (File file1 : files) {
					String fileExtension1 = FilenameUtils.getExtension(file1.getPath());
					if (fileExtension1.equalsIgnoreCase("xml")) {
						// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
						siteConfigType = siteConfigType.replaceAll(" ", "");
						String fileName = StringUtils.substringBeforeLast(file1.getName(), ".");

						// New Fix
						Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|IAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
						Matcher matach = pat.matcher(fileName);
						if (!matach.find()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "RF Scripts File Name Invalid :"+"("+fileName+")");
							return resultMap;
							
						}
					}
				}*/
				Set<String> seqNoSet = new HashSet<>();
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("xml")) {
						// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
						siteConfigType = siteConfigType.replaceAll(" ", "");
						String fileName = StringUtils.substringBeforeLast(file.getName(), ".");
						
						// New Fix
						Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|FSU|IAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
						Matcher mat = pat.matcher(fileName);
						while (mat.find()) {
							String[] filenameSplit = fileName.split("[\\-\\_]");
							int seqno = NumberUtils.toInt(filenameSplit[0]); 
							if (inSeq(seqno, precheckSeq)) {
								bashFileName = fileName + "_Pre-Check_" + "RFUsecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Pre-Check/";
							} else if (inSeq(seqno, cutOverSeq)) {
								bashFileName = fileName + "_Cutover_" + "RFUsecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Cutover/";
							} else if (inSeq(seqno, rollBackSeq)) {
								bashFileName = fileName + "_Rollback_" + "RFUsecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Rollback/";
							} else {
								bashFileName = fileName + "_Extended_" + "Usecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Extended/";
							}

							File bash = new File(filePath + folderName);
							bash.mkdir();
							String tempmcip = mcmip;
							if((filenameSplit[2].contains("eNB")) && listCIQDetailsModel.get(0).getCiqMap().containsKey("4GeNB")) {
								NeMappingModel neMappingModel = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
								//programDetailsEntity.setProgramName("VZN-4G-USM-LIVE");
								programDetailsEntity.setId(34);
								neMappingModel.setProgramDetailsEntity(programDetailsEntity);
								neMappingModel.setEnbId(listCIQDetailsModel.get(0).getCiqMap().get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
								if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
										&& (CommonUtil.isValidObject(neMappingEntities.get(0))
												&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
												&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
								}
							}
							if((filenameSplit[2].contains("FSU")) && listCIQDetailsModel.get(0).getCiqMap().containsKey("4GeNB")) {
								NeMappingModel neMappingModel = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
								programDetailsEntity.setId(34);
								neMappingModel.setProgramDetailsEntity(programDetailsEntity);
								neMappingModel.setEnbId(listCIQDetailsModel.get(0).getCiqMap().get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
								if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
										&& (CommonUtil.isValidObject(neMappingEntities.get(0))
												&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
												&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
								}
								NeMappingModel neMappingModel2 = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity2 = new CustomerDetailsEntity();
								programDetailsEntity2.setId(pId);
								neMappingModel2.setProgramDetailsEntity(programDetailsEntity2);
								neMappingModel2.setEnbId(filenameSplit[1].replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities2 = neMappingService.getNeMapping(neMappingModel2);
								if ((CommonUtil.isValidObject(neMappingEntities2) && neMappingEntities2.size() > 0) 
										&& (CommonUtil.isValidObject(neMappingEntities2.get(0))
												&& CommonUtil.isValidObject(neMappingEntities2.get(0).getSiteConfigType())
												&& (neMappingEntities2.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities2.get(0).getNetworkConfigEntity().getNeIp();
								}
							}
							if(filenameSplit[2].contains("ACPF") || filenameSplit[2].contains("AUPF")) {
								NeMappingModel neMappingModel = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
								programDetailsEntity.setId(pId);
								neMappingModel.setProgramDetailsEntity(programDetailsEntity);
								neMappingModel.setEnbId(filenameSplit[1].replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
								if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
										&& (CommonUtil.isValidObject(neMappingEntities.get(0))
												&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
												&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
								}
							}
							if(seqNoSet.contains(filenameSplit[0])) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Duplicate Sequence numbers in the RF script filename, Contact RF for fix (Sequence No. : " + filenameSplit[0] + " )");
								return resultMap;
							} else {
								seqNoSet.add(filenameSplit[0]);
							}
							resultMap = CommonUtil.createBashFromXml5GDSS(filePath + file.getName(),
									filePath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, pId,
									ciqName, listCIQDetailsModel, listCIQsheetDetailsModel, filenameSplit[2],filenameSplit[1]);
							mcmip = tempmcip;
							if(resultMap.containsKey("status") && resultMap.get("status").equals(Constants.FAIL)) {
								return resultMap;
							}
							resultMap.put("timeStamp", timeStamp);
						}
					}
				}
				if(!resultMap.containsKey("status")) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Rf Scripts fileName is not following standard pattern");
					return resultMap;
				}
			}
		} catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createBashFromXml5GDSS() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
	private JSONObject createBashFromXml5GCBand(String filePath, File[] files, String fileType, String mcmip,
			String enbId, String endName, String siteConfigType, Integer pId, String ciqName,
			List<CIQDetailsModel> listCIQDetailsModel, List<CIQDetailsModel> listCIQsheetDetailsModel) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String folderName = null;
		try {
			logger.info("createBashFromXml5GCBand() called filePath: " + filePath + ", fileType: " + fileType
					+ ", mcmip: " + mcmip);
			if (files != null) {
				String[] precheckSeq = null;
				String[] cutOverSeq = null;
				ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
						Constants.SEQUENCE_NUMBER_TEMPLATE);
				if(CommonUtil.isValidObject(programTemplateEntity) && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
					try {
						JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
						if(objData.containsKey("PreCheck")) {
							precheckSeq = objData.get("PreCheck").toString().trim().split(",");
						}
						if(objData.containsKey("CutOver")) {
							cutOverSeq = objData.get("CutOver").toString().trim().split(",");						
						}
					}catch(Exception e) {
						logger.error(ExceptionUtils.getFullStackTrace(e));
					}
				}
				if(files.length != 0) {
					FileUtil.deleteFileOrFolder(filePath + "Pre-Check/");
					FileUtil.deleteFileOrFolder(filePath + "Extended/");
				}
				for (File file1 : files) {
					String fileExtension1 = FilenameUtils.getExtension(file1.getPath());
					if (fileExtension1.equalsIgnoreCase("xml")) {
						// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
						siteConfigType = siteConfigType.replaceAll(" ", "");
						String fileName = StringUtils.substringBeforeLast(file1.getName(), ".");

						// New Fix
						Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|IAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
						Matcher matach = pat.matcher(fileName);
						if (!matach.find()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "RF Scripts File Name Invalid :"+"("+fileName+")");
							return resultMap;
							
						}
					}
				}
				Set<String> seqNoSet = new HashSet<>();
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("xml")) {
						// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
						siteConfigType = siteConfigType.replaceAll(" ", "");
						String fileName = StringUtils.substringBeforeLast(file.getName(), ".");

						// New Fix
						Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|IAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
						Matcher mat = pat.matcher(fileName);
						while (mat.find()) {
							String[] filenameSplit = fileName.split("[\\-\\_]");
							int seqno = NumberUtils.toInt(filenameSplit[0]); 
							if (inSeq(seqno, precheckSeq)) {
								bashFileName = fileName + "_Pre-Check_" + "RFUsecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Pre-Check/";
							} else if (inSeq(seqno, cutOverSeq)) {
								bashFileName = fileName + "_Cutover_" + "RFUsecase" + "_" + enbId + "_" + dateString
										+ ".sh";
								folderName = "Cutover/";
							}  else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "RF Scripts File Name Sequence Invalid :"+"("+fileName+")");
								return resultMap;
							}

							File bash = new File(filePath + folderName);
							bash.mkdir();
							String tempmcmip = mcmip;
							if(filenameSplit[2].contains("eNB") || filenameSplit[2].contains("ACPF") || filenameSplit[2].contains("AUPF")) {
								NeMappingModel neMappingModel = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
								programDetailsEntity.setId(pId);
								neMappingModel.setProgramDetailsEntity(programDetailsEntity);
								neMappingModel.setEnbId(filenameSplit[1].replaceAll("^0+(?!$)", ""));
								List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
								if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
										&& (CommonUtil.isValidObject(neMappingEntities.get(0))
												&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
												&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
									mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
								}
							}
							if(seqNoSet.contains(filenameSplit[0])) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Duplicate Sequence numbers in the RF script filename, Contact RF for fix (Sequence No. : " + filenameSplit[0] + " )");
								return resultMap;
							} else {
								seqNoSet.add(filenameSplit[0]);
							}
							resultMap = CommonUtil.createBashFromXml5GCBAND(filePath + file.getName(),
									filePath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, pId,
									ciqName, listCIQDetailsModel, listCIQsheetDetailsModel, filenameSplit[2], filenameSplit[1]);
							mcmip = tempmcmip;
							if(resultMap.containsKey("status") && resultMap.get("status").equals(Constants.FAIL)) {
								return resultMap;
							}
							resultMap.put("timeStamp", timeStamp);
						}
					}
				}
				if(!resultMap.containsKey("status")) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Rf Scripts fileName is not following standard pattern");
					return resultMap;
				}
			}
		} catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createBashFromXml5GCBand() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	/////////////**********************CA fix****************************///////////////////////////////
	private JSONObject createBashFromXml4GCA(String filePath, File[] files, String fileType, String mcmip,
			String enbId, String endName, String siteConfigType, Integer pId, String ciqName,
			List<CIQDetailsModel> listCIQDetailsModel, List<CIQDetailsModel> listCIQsheetDetailsModel) {

	JSONObject resultMap = new JSONObject();
	String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
	String dateString = new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
	String bashFileName = " ";
	String folderName = null;
	try {
		logger.info("createBashFromXml4GCA() called filePath: " + filePath + ", fileType: " + fileType
				+ ", mcmip: " + mcmip);
		if (files != null) {
			String[] CASeq = null;
			String[] cutOverSeq = null;
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(pId,
					Constants.SEQUENCE_NUMBER_TEMPLATE);
			if(CommonUtil.isValidObject(programTemplateEntity) && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
				try {
					JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
					if(objData.containsKey("CA")) {
						CASeq = objData.get("CA").toString().trim().split(",");
					}
					
				}catch(Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
			if(files.length != 0) {
				FileUtil.deleteFileOrFolder(filePath + "CA/");
				//FileUtil.deleteFileOrFolder(filePath + "Extended/");
			}
			Set<String> seqNoSet = new HashSet<>();
			for (File file : files) {
				String fileExtension = FilenameUtils.getExtension(file.getPath());
				if (fileExtension.equalsIgnoreCase("xml")) {
					// 1_'RF Template Name'_5G5GRfUsecase_'GNODEB_AU ID'_MMDDYYY_HH_MM_SS.sh
					siteConfigType = siteConfigType.replaceAll(" ", "");
					String fileName = StringUtils.substringBeforeLast(file.getName(), ".");

					// New Fix
					Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|IAU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
					Matcher mat = pat.matcher(fileName);
					while (mat.find()) {
						String[] filenameSplit = fileName.split("[\\-\\_]");
						int seqno = NumberUtils.toInt(filenameSplit[0]); 
						if (inSeq(seqno, CASeq)) {
							bashFileName = "BASH_CA_"+ fileName + "_CA_" + "Usecase" + "_" + enbId + "_" + dateString
									+ ".sh";
							folderName = "CA/";
						} 
						else{
							continue;
						}
						File bash = new File(filePath + folderName);
						bash.mkdir();
						String tempmcmip = mcmip;
						if(filenameSplit[2].contains("eNB")) {
							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(pId);
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel.setEnbId(filenameSplit[1].replaceAll("^0+(?!$)", ""));
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
									&& (CommonUtil.isValidObject(neMappingEntities.get(0))
											&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
											&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
								mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
							}
						}
						if(seqNoSet.contains(filenameSplit[0])) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Duplicate Sequence numbers in the RF script filename, Contact RF for fix (Sequence No. : " + filenameSplit[0] + " )");
							return resultMap;
						} else {
							seqNoSet.add(filenameSplit[0]);
						}
						resultMap = CommonUtil.createBashFromXml5GCBAND(filePath + file.getName(),
								filePath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, pId,
								ciqName, listCIQDetailsModel, listCIQsheetDetailsModel, filenameSplit[2], filenameSplit[1]);
						mcmip = tempmcmip;
						if(resultMap.containsKey("status") && resultMap.get("status").equals(Constants.FAIL)) {
							return resultMap;
						}
						resultMap.put("timeStamp", timeStamp);
					}
				}
			}
			if(!resultMap.containsKey("status")) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", " No CA Scripts fileName between the range");
				return resultMap;
			}
		}
	} catch (Exception e) {
		logger.error("Exception in GenerateCsvController.createBashFromXml5GCBand() "
				+ ExceptionUtils.getFullStackTrace(e));
	}
	return resultMap;
}
/////////////**********************CA fix end****************************///////////////////////////////
	@SuppressWarnings("unchecked")
	private JSONObject createCliFromBatch(String filePath, File[] files, String fileType, String neVersion,
			String relVersion, String siteConfigType, ProgramTemplateEntity programTemplateEntity, String ciqFileName,
			String enbId, String endName, String sessionId) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String cliFileName = " ";
		try {
			logger.info("createCliFromBatch() called filePath: " + filePath + ", fileType: " + fileType
					+ ", neVersion: " + neVersion + ", relVersion: " + relVersion);
			if (files != null) {
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if ("COMM" == fileType) {
						dateString = "";
					}
					if (fileExtension.equalsIgnoreCase("txt")) {
						timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
								.format(new Timestamp(System.currentTimeMillis()));
						siteConfigType = siteConfigType.replaceAll(" ", "");
						cliFileName = "CLI_" + fileType + "_" + siteConfigType + "_"
								+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						resultMap = CommonUtil.createCliFromBatch(filePath + file.getName(), filePath + cliFileName,
								neVersion, relVersion, programTemplateEntity, ciqFileName, enbId, endName);
					}
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CLI_FILE));
			logger.error(
					"Exception in GenerateCsvController.createCliFromBatch() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createBashFromVbs(String filePath, File[] files, String fileType, String siteConfigType,
			ProgramTemplateEntity programTemplateEntity, String ciqFileName, String enbId, String endName,
			String sessionId) {
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		try {
			logger.info("createBashFromVbs() called filePath: " + filePath + ", fileType: " + fileType);
			if (files != null) {
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if ("COMM" == fileType) {
						dateString = "";
					}
					if (fileExtension.equalsIgnoreCase("vbs")) {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
								.format(new Timestamp(System.currentTimeMillis()));
						bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
								+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						if (file.getName().contains(Constants.PRECHECK_CSR_FILE_NAME)
								|| file.getName().contains(Constants.PRECHECK_BSM_FILE_NAME)
								|| file.getName().contains(Constants.POSTCHECK_CSR_FILE_NAME)
								|| file.getName().contains(Constants.POSTCHECK_BSM_FILE_NAME)) {
							resultMap = CommonUtil.createCliFromVbs(filePath + file.getName(), filePath + bashFileName,
									null, null, programTemplateEntity, ciqFileName, enbId, endName);
						} else {
							resultMap = CommonUtil.createBashFromVbs(filePath + file.getName(), filePath + bashFileName,
									programTemplateEntity, ciqFileName, enbId, endName);
						}
					}
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_BASH_FILE));
			logger.error(
					"Exception in GenerateCsvController.createBashFromVbs() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/*
	 * private JSONObject createCmdSysFromBatch(String filePath, File[] files,String
	 * fileType,ProgramTemplateEntity programTemplateEntity, String ciqFileName,
	 * String enbId, String endName){ JSONObject resultMap = new JSONObject();
	 * String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new
	 * Timestamp(System.currentTimeMillis())); String cliFileName = " "; try{
	 * logger.info("createCmdSysFromBatch() called filePath: "
	 * +filePath+", fileType: "+fileType); if (files != null) { for (File file :
	 * files) { String fileExtension = FilenameUtils.getExtension(file.getPath());
	 * if (fileExtension.equalsIgnoreCase("txt")) { timeStamp = new
	 * SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new
	 * Timestamp(System.currentTimeMillis())); cliFileName = "CMD_SYS_"+ fileType
	 * +"_" + StringUtils.substringBeforeLast(file.getName(), ".") + "_" + timeStamp
	 * + ".sh"; resultMap = CommonUtil.createCmdSysFromBatch(filePath +
	 * file.getName() , filePath + cliFileName, programTemplateEntity, ciqFileName,
	 * enbId, endName); } } } }catch (Exception e) {
	 * logger.error("Exception in GenerateCsvController.createCmdSysFromBatch() " +
	 * ExceptionUtils.getFullStackTrace(e)); } return resultMap; }
	 */

	/**
	 * This api will get All ENB IDS for respective GNB AU ID Details
	 * 
	 * @param generateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_NEID_ENDC)
	public JSONObject getNeIdDetails(@RequestBody JSONObject ciqDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String ciqName = null;
		Integer programId = null;
		Map<String, String> neId;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			ciqName = ciqDetails.get("ciqName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			programId = (Integer) ciqDetails.get("programId");
			Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			neId = (Map<String, String>) ciqDetails.get("neId");
			String eNBName = neId.get("eNBName");
			String eNBId = neId.get("eNBId");
			// String sheetName="4GLTE5GNRX2";
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqName);

			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqName, eNBId, eNBName,
					dbcollectionFileName);
			String gnbidd = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();
			if (ciqName.contains("Boston")) {
				int len = gnbidd.length();
				gnbidd = gnbidd.substring(1, len);
			}
			List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository.getEnbTableDetails(ciqName, gnbidd,
					eNBName, dbcollectionFileName);
			List<String> neList = new ArrayList<>();
			for (int i = 0; i < listCIQDetailsModell.size(); i++) {
				neList.add(listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue());
			}

			resultMap.put("neList", neList);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in Geting EnbId details" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultMap;

	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_GENERATED_FILE_DETAILS)
	public JSONObject deleteGeneratedFileDetails(@RequestBody JSONObject deleteGenerateFileDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		Integer fileId = null;
		String fileName = null;
		String filePath = null;

		try {
			sessionId = deleteGenerateFileDetails.get("sessionId").toString();
			serviceToken = deleteGenerateFileDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			programId = (Integer) deleteGenerateFileDetails.get("programId");
			fileId = Integer.parseInt(deleteGenerateFileDetails.get("id").toString());
			fileName = deleteGenerateFileDetails.get("fileName").toString();
			filePath = deleteGenerateFileDetails.get("filePath").toString();

			GenerateInfoAuditModel generateInfoAuditModel = new GenerateInfoAuditModel();
			generateInfoAuditModel.setId(fileId);
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(programId);
			generateInfoAuditModel.setProgramDetailsEntity(programDetailsEntity);
			generateInfoAuditModel.setFileName(fileName);

			boolean status = objGenerateCsvService.deleteGeneratedFileDetails(generateInfoAuditModel);			
			if (status) {
				status = deleteGeneratedFile(programId, filePath, fileName);
				if (!status) {
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CSV_DETAILS));
					resultMap.put("status", Constants.FAIL);
					return resultMap;
				} else {
					if (StringUtils.isNotEmpty(filePath) && filePath.contains("COMMISSIONING_SCRIPT")) {
						JSONObject fileProcessResult = new JSONObject();
						String[] folders = filePath.split("/");
						String neId = folders[folders.length - 2];
						fileProcessResult = objGenerateCsvService.deleteUploadScriptFileDetails(neId, filePath,
								sessionId);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;
						}
					}
				}
			}
			else if(status == false)
			{
				GenerateInfoAuditEntity entity =generateRepository.getGenerateInfoAuditById(fileId);
				String  remarks = entity.getRemarks();
				if(remarks.contains("WFM")) {
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken",serviceToken);
					resultMap.put("reason", "Cannot delete as its generated from WFM");
					return resultMap;
				}				
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.CSV_DETAILS_DELETED_SUCCESSFULLY));
			commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_GENERATE,
					Constants.ACTION_DELETE, "Generated File Details Deleted Successfully File Name: " + fileName,
					sessionId);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in deleteGeneratedFileDetails() GenerateCsvController : "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CSV_DETAILS));
		}
		return resultMap;
	}

	private boolean deleteGeneratedFile(Integer programId, String filePath, String fileName) {
		boolean status = false;
		try {
			if (CommonUtil.isValidObject(programId) && CommonUtil.isValidObject(fileName)) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath.toString();
				logger.info("deleteSiteDataSFile filePath: " + filePath + ", fileName: " + fileName);
				File file = new File(filePath + fileName);
				if (file.exists()) {
					file.delete();
				}
			}
			status = true;
		} catch (Exception e) {
			logger.info(
					"Exception in deleteGeneratedFile in GenerateCsvController " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.UPDATE_GENERATED_FILE_DETAILS, method = RequestMethod.POST)
	public JSONObject updateGeneratedFileDetails(@RequestBody JSONObject updateGeneratedFileDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateGeneratedFileDetails.get("sessionId").toString();
			serviceToken = updateGeneratedFileDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			GenerateInfoAuditEntity generateInfoAuditEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create().fromJson(
							updateGeneratedFileDetails
									.toJSONString((Map) updateGeneratedFileDetails.get("generateInfoAuditDetails")),
							GenerateInfoAuditEntity.class);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			generateInfoAuditEntity.setGeneratedBy(user.getUserName());
			generateInfoAuditEntity.setGenerationDate(new Date());
			if (generateInfoAuditEntity != null) {
				if (objGenerateCsvService.updateGeneratedFileDetails(generateInfoAuditEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.GENERATED_AUDIT_DETAILS_UPDATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_GENERATE,
							Constants.ACTION_UPDATE,
							"Generated File Details Updated Successfully For: " + generateInfoAuditEntity.getFileName(),
							sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_UPDATE_GENERATED_AUDIT_DETAILS));
				}
			}
		} catch (Exception e) {
			logger.info("Exception in updateGeneratedFileDetails in GenerateCsvController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_GENERATED_AUDIT_DETAILS));
			mapObject.put("status", Constants.FAIL);
		}
		return mapObject;
	}

	/**
	 * This method will Generate CSV File
	 * 
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.CSV_FILE_GENERATION)
	public JSONObject csvFileGeneration(@RequestBody JSONObject ciqDetails) {

		String sessionId = null;
		String serviceToken = null;
		String ciqFileName = null;
		Integer customerId = null;
		Integer networkTypeId = null;
		String lsmVersion = null;
		String networkType = null;
		String customerName = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			ciqFileName = ciqDetails.get("fileName").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			networkTypeId = Integer.valueOf(ciqDetails.get("networkTypeId").toString());
			lsmVersion = ciqDetails.get("lsmVersion").toString();
			networkType = ciqDetails.get("networkType").toString();
			if (StringUtils.isNotEmpty(ciqDetails.get("customerId").toString())
					&& StringUtils.isNotEmpty(ciqDetails.get("customerName").toString())) {
				customerId = Integer.valueOf(ciqDetails.get("customerId").toString());
				customerName = ciqDetails.get("customerName").toString();
			}
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			StringBuilder uploadPath = new StringBuilder();
			uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(customerName).append(File.separator).append(networkType)
					.append(File.separator).append(lsmVersion).append(Constants.PRE_MIGRATION_CSV);

			if (StringUtils.isNotEmpty(ciqFileName)) {
				File fileExist = new File(uploadPath.toString());
				if (!fileExist.exists()) {
					FileUtil.createDirectory(uploadPath.toString());
				}
				boolean status = objGenerateCsvService.csvFileGeneration(ciqFileName, uploadPath.toString(), customerId,
						networkTypeId, lsmVersion, sessionId);

				if (status) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_GENERATE, "Files Generated Successfully For: " + ciqFileName, sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
				}

			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info(
					"Exception in csvFileGeneration() GenerateCsvController : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
		}
		return mapObject;
	}

	/**
	 * This api will get CSV Audit Details
	 * 
	 * @param generateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_CSV_AUDIT_DETAILS)
	public JSONObject getCsvAuditDetails(@RequestBody JSONObject generateDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String searchStatus = null;
		Integer programId = null;
		String fileType;
		List<GenerateInfoAuditEntity> generateInfoAuditEntities = null;
		List<GenerateInfoAuditModel> csvInfoAuditModel = null;
		GenerateInfoAuditModel csvModel = null;
		boolean showUserData = false;
		try {
			sessionId = generateDetails.get("sessionId").toString();
			serviceToken = generateDetails.get("serviceToken").toString();
			searchStatus = generateDetails.get("searchStatus").toString();
			fileType = generateDetails.get("fileType").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			programId = (Integer) generateDetails.get("programId");
			if (generateDetails.containsKey("showUserData")) {
				showUserData = (boolean) generateDetails.get("showUserData");
			}
			Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			List<ProgramGenerateFileEntity> programGenerateFileDetails = customerService
					.getProgramGenerateFileDetails(programId);
			String pgmName = programGenerateFileDetails.get(0).getProgramDetailsEntity().getProgramName();

			resultMap.put("programGenerateFileDetails", programGenerateFileDetails);
			if (pgmName.equalsIgnoreCase("VZN-4G-FSU")) {
				resultMap.put("programType", "FSU");
			}

			resultMap.put("programGenerateFileDetails", programGenerateFileDetails);

			resultMap.put("searchStartDate", startDate);
			resultMap.put("searchEndDate", curdate);

			Map<String, Integer> paginationData = (Map<String, Integer>) generateDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");

			if (Constants.LOAD.equals(searchStatus)) {
				csvModel = new GenerateInfoAuditModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				csvModel.setProgramDetailsEntity(programDetailsEntity);
				csvModel.setSearchStartDate(startDate);
				csvModel.setSearchEndDate(curdate);
				csvModel.setFileType(fileType);
				if(showUserData) {
					User user = UserSessionPool.getInstance().getSessionUser(sessionId);
					if(user!=null) {
						csvModel.setGeneratedBy(user.getUserName());
					}
				}
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				csvModel = new Gson().fromJson(
						generateDetails.toJSONString((Map) generateDetails.get("searchCriteria")),
						GenerateInfoAuditModel.class);
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				csvModel.setProgramDetailsEntity(programDetailsEntity);
				csvModel.setFileType(fileType);
				if(showUserData) {
					User user = UserSessionPool.getInstance().getSessionUser(sessionId);
					if(user!=null) {
						csvModel.setGeneratedBy(user.getUserName());
					}
				}

			}
			Map<String, Object> csvList = objGenerateCsvService.getCsvAuditDetails(csvModel, page, count);
			resultMap.put("pageCount", csvList.get("paginationcount"));
			resultMap.put("ciqList", csvList.get("ciqName"));
			generateInfoAuditEntities = (List<GenerateInfoAuditEntity>) csvList.get("fileList");

			if (CommonUtil.isValidObject(generateInfoAuditEntities)) {
				csvInfoAuditModel = new ArrayList<GenerateInfoAuditModel>();
				for (GenerateInfoAuditEntity generateInfoAuditEntity : generateInfoAuditEntities) {
					csvInfoAuditModel.add(GenerateInfoAuditDto.getcsvAuditDetailsModel(generateInfoAuditEntity));
				}
			}
			resultMap.put("csvAuditTrailDetModels", csvInfoAuditModel);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error(
					"Exception in getCsvAuditDetails()   GenerateCsvController:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CSV_AUDIT_DETAILS));
		}
		return resultMap;
	}

	
	/**
	 * This api for transferCiqAuditFile
	 * 
	 * @param ciqFileDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.TRANSFER_CIQ_AUDIT_FILE)
	public JSONObject transferCiqAuditFile(@RequestBody JSONObject ciqFileDetails) {
		String sessionId = null;
		String serviceToken = null;
		int iLsmId;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = ciqFileDetails.get("sessionId").toString();
			serviceToken = ciqFileDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			iLsmId = Integer.parseInt(ciqFileDetails.get("lsmId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			ObjectMapper objMapper = new ObjectMapper();
			String data = CommonUtil.convertObjectToJson(ciqFileDetails);
			JsonObject objData = CommonUtil.parseRequestDataToJson(data);
			List<GenerateInfoAuditModel> objHbSenderConfigEntityList = objMapper.readValue(
					objData.get("csvAuditDetails").toString(), new TypeReference<List<GenerateInfoAuditModel>>() {
					});
			resultMap = objGenerateCsvService.transferCiqFile(objHbSenderConfigEntityList, iLsmId, resultMap);
		} catch (Exception e) {
			logger.error(
					"Exception in getCsvAuditDetails()   GenerateCsvController:" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will Generate CSV File
	 * 
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.CIQ_FILE_VALIDATION)
	public JSONObject ciqFileValidation(@RequestBody JSONObject ciqDetails) {
		String sessionId = null;
		String serviceToken = null;
		String ciqFileName = null;
		Integer customerId = null;
		Integer networkTypeId = null;
		String lsmVersion = null;
		String networkType = null;
		String customerName = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			ciqFileName = ciqDetails.get("fileName").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			networkTypeId = Integer.valueOf(ciqDetails.get("networkTypeId").toString());
			lsmVersion = ciqDetails.get("lsmVersion").toString();
			networkType = ciqDetails.get("networkType").toString();
			if (StringUtils.isNotEmpty(ciqDetails.get("customerId").toString())
					&& StringUtils.isNotEmpty(ciqDetails.get("customerName").toString())) {
				customerId = Integer.valueOf(ciqDetails.get("customerId").toString());
				customerName = ciqDetails.get("customerName").toString();
			}
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			StringBuilder uploadPath = new StringBuilder();
			uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(customerName).append(File.separator).append(networkType)
					.append(File.separator).append(lsmVersion).append(Constants.PRE_MIGRATION_CSV);
			if (StringUtils.isNotEmpty(ciqFileName)) {
				File fileExist = new File(uploadPath.toString());
				if (!fileExist.exists()) {
					FileUtil.createDirectory(uploadPath.toString());
				}
				boolean status = objGenerateCsvService.csvFileGeneration(ciqFileName, uploadPath.toString(), customerId,
						networkTypeId, lsmVersion, sessionId);
				if (status) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.GENERATED_CSV_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_GENERATE, "Files Generated Successfully For: " + ciqFileName, sessionId);
				} else {
					System.out.println("--------13--------------");
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
				}
			}

		} catch (Exception e) {
			System.out.println("-------14-----------");
			mapObject.put("status", Constants.FAIL);
			mapObject.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
			logger.info(
					"Exception in csvFileGeneration() GenerateCsvController : " + ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}

	/**
	 * This api for transferCiqAuditFile
	 * 
	 * @param ciqFileDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = "/transferCsvFileAndGrow")
	public JSONObject transferCsvFileAndGrow(@RequestBody JSONObject csvFileDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = csvFileDetails.get("sessionId").toString();
			serviceToken = csvFileDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			EnbPreGrowAuditModel objEnbPreGrowAuditModel = new Gson().fromJson(
					csvFileDetails.toJSONString((Map) csvFileDetails.get("csvAuditDetails")),
					EnbPreGrowAuditModel.class);
			resultMap = objGenerateCsvService.transferCiqFileAndGrow(objEnbPreGrowAuditModel, resultMap);
		} catch (Exception e) {
			logger.error("Exception in transferCsvFileAndGrow()   GenerateCsvController:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will give the Audit Trail details
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = "/getGenFileSearchDetails", method = RequestMethod.POST)
	public JSONObject getGenFileSearchDetails(@RequestBody JSONObject genDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String searchStatus = null;
		List<GenerateInfoAuditModel> genList = null;
		Integer customerId = null;
		GenerateInfoAuditModel objCsvInfoAuditModel = null;
		try {
			sessionId = genDetails.get("sessionId").toString();
			serviceToken = genDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = genDetails.get("searchStatus").toString();
			customerId = (Integer) genDetails.get("customerId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) genDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			if (Constants.LOAD.equals(searchStatus)) {
				Map<String, Object> listMap = objGenerateCsvService.generateFilesListSearch(objCsvInfoAuditModel,
						customerId, page, count);
				resultMap.put("pageCount", listMap.get("count"));
				genList = (List<GenerateInfoAuditModel>) listMap.get("list");
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				objCsvInfoAuditModel = new Gson().fromJson(
						genDetails.toJSONString((Map) genDetails.get("searchCriteria")), GenerateInfoAuditModel.class);
				Map<String, Object> listMap = objGenerateCsvService.generateFilesListSearch(objCsvInfoAuditModel,
						customerId, page, count);
				resultMap.put("pageCount", listMap.get("count"));
				genList = (List<GenerateInfoAuditModel>) listMap.get("list");
			}
			resultMap.put("generateDetails", genList);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in getGenFileSearchDetails()   GenerateCsvController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CSV_AUDIT_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This method will generateCiqBasedFiles
	 * 
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/generateCiqBasedFiles")
	public JSONObject generateCiqBasedFiles(@RequestBody JSONObject ciqDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String id = null;
		String fileName = null;
		String programId = null;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = ciqDetails.get("ciqName").toString();
			programId = ciqDetails.get("programId").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, fileName);
			// some where else we used
			List<Map<String, String>> objList = fileUploadService.getEnbDetails(id, fileName, dbcollectionFileName);
			if (objList != null && objList.size() > 0) {
				LinkedHashMap<String, String> objFailedData = new LinkedHashMap<>();
				JSONObject result = generateallEnodebFiles(objList, programId, fileName, dbcollectionFileName,
						sessionId, serviceToken, objFailedData);
				if (result != null && result.containsKey("status") && result.get("status").equals(Constants.SUCCESS)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILES_GENERATE_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_GENERATE, "Files Generated Successfully For: " + fileName, sessionId);
					return resultMap;
				} else if (result != null && result.containsKey("status")
						&& result.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result.get("reason"));
					return resultMap;
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result.get("reason"));
					return resultMap;
				}
			}
		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_ENB_FILES));
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private JSONObject generateallEnodebFiles(List<Map<String, String>> objList, String programStringId,
			String ciqFileName, String dbcollectionFileName, String sessionId, String serviceToken,
			LinkedHashMap<String, String> objFailedData) {
		boolean fileGenerateEnvStatus = false;
		boolean fileGenerateCsvStatus = false;
		boolean fileCommGenerateStatus = false;
		JSONObject mapObject = new JSONObject();
		String remarks = "Generated in Ran Config";
		//carrier add
		Boolean supportCA = false;
		//dummy IP
		String dummy_IP = null;
		try {
			String csvPath = null;
			StringBuilder uploadPath = new StringBuilder();
			int programId = Integer.valueOf(programStringId);
			File fileExist = new File(uploadPath.toString());
			if (!fileExist.exists()) {
				FileUtil.createDirectory(uploadPath.toString());
			}
			for (Map<String, String> objEnbMap : objList) {
				if (objEnbMap != null && objEnbMap.size() > 0) {
					if (objEnbMap.containsKey("eNBName") && objEnbMap.containsKey("eNBId")) {

						String enbId = objEnbMap.get("eNBId");
						String enbName = objEnbMap.get("eNBName");

						try {

							boolean envFileExist = false;
							boolean csvFileExist = false;
							boolean commFileExist = false;

							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(programId);
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel.setEnbId(enbId);
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							if (!CommonUtil.isValidObject(neMappingEntities) || neMappingEntities.size() <= 0) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								return mapObject;
							}

							NeMappingEntity neMappingEntity = neMappingEntities.get(0);

							if (!CommonUtil.isValidObject(neMappingEntity)
									|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
								return mapObject;
							}

							JSONObject scriptSeqResult = getScriptSequenceDetails(ciqFileName, programId,
									neMappingEntity, enbId, enbName, sessionId, serviceToken);
							if (scriptSeqResult != null && scriptSeqResult.containsKey("status")
									&& scriptSeqResult.get("status").equals(Constants.FAIL)) {
								return scriptSeqResult;
							}

							// ENV Generation
							JSONObject fileEnvGenerateResult = new JSONObject();
							List<JSONObject> fileEnvGenerateResultList = new ArrayList<JSONObject>();
							String envUploadPath = "";
							uploadPath.setLength(0);
							uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
									.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
							uploadPath.append(Constants.PRE_MIGRATION_ENV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							File fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}

							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
								// fileGenerateEnvStatus =
								// objGenerateCsvService.commissionScriptFileGeneration(ciqFileName, enbId,
								// enbName, dbcollectionFileName, programId, uploadPath.toString(),
								// sessionId,Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4, neMappingEntity,
								// remarks);
								fileEnvGenerateResult.put("status", true);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								fileEnvGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env",
										neMappingEntity, remarks, supportCA);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
								//dummy env file
								if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
									fileEnvGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
											neMappingEntity, remarks, supportCA);
									fileEnvGenerateResultList.add(fileEnvGenerateResult);
								}	
								envFileExist = true;
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileEnvGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env",
										neMappingEntity, remarks, supportCA);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
								//dummy env file
								if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
									fileEnvGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
											neMappingEntity, remarks, supportCA);
									fileEnvGenerateResultList.add(fileEnvGenerateResult);
								}															
								//
								envFileExist = true;
							} else {
								fileEnvGenerateResult.put("status", true);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
							}
							envUploadPath = uploadPath.toString();

							// CSV Generation
							JSONObject fileCsvGenerateResult = new JSONObject();
							List<JSONObject> fileCsvGenerateResultList = new ArrayList<JSONObject>();
							String csvUploadPath = "";
							uploadPath.setLength(0);
							uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
									.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
							uploadPath.append(Constants.PRE_MIGRATION_CSV
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							//fetching dummy IP
							if(objEnbMap.containsKey("integrationType")) {
								dummy_IP = objEnbMap.get("integrationType").toString();
							} else {
								dummy_IP = "Legacy IP";
							}
							StringBuffer ptpCheck = new StringBuffer();
							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
								if (!ptpCheck.toString().isEmpty()) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", ptpCheck.toString()+" For Selected Enb is not present in the Ciq.");
									return mapObject;
								}
								fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"enb_cell", neMappingEntity, remarks, supportCA, dummy_IP);
								fileCsvGenerateResultList.add(fileCsvGenerateResult);
								csvFileExist = true;
								csvPath = uploadPath.toString();
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
								if (ptpCheck.toString().isEmpty()) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason",ptpCheck.toString()+ " For Selected Enb is not present in the Ciq");
									return mapObject;
								}
								fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"enb", neMappingEntity, remarks, supportCA, dummy_IP);
								fileCsvGenerateResultList.add(fileCsvGenerateResult);
								fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"cell", neMappingEntity, remarks, supportCA, dummy_IP);
								fileCsvGenerateResultList.add(fileCsvGenerateResult);
								csvFileExist = true;
								csvPath = uploadPath.toString();
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
								fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"enb_cell", neMappingEntity, remarks, supportCA, dummy_IP);
								fileCsvGenerateResultList.add(fileCsvGenerateResult);
								csvFileExist = true;
								csvPath = uploadPath.toString();
							} else {
								fileCsvGenerateResult.put("status", true);
								fileCsvGenerateResultList.add(fileCsvGenerateResult);
							}
							csvUploadPath = uploadPath.toString();
							// COMM SCRIPT Generation
							JSONObject fileCommGenerateResult = new JSONObject();
							List<JSONObject> fileCommGenerateResultList = new ArrayList<JSONObject>();
							String commUploadPath = "";
							uploadPath.setLength(0);
							uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
									.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
							uploadPath.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
									.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
									.replace("enbId", enbId).replaceAll(" ", "_"));
							fileExists = new File(uploadPath.toString());
							if (!fileExists.exists()) {
								FileUtil.createDirectory(uploadPath.toString());
							}
							commUploadPath = uploadPath.toString();

							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script))) {
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId,
										Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId,
										Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT1,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT2,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT3,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT5,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT6,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT7,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT8,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT9,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT10,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT11,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId,
										Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_MME,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_ENV,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_COMM,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
							} else if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
											.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
								fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(
										ciqFileName, enbId, enbName, dbcollectionFileName, programId,
										uploadPath.toString(), sessionId, Constants.GROW_COMM_SCRIPT_FILE_TYPE_VBS,
										neMappingEntity, remarks);
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
							} else {
								fileCommGenerateResult.put("status", true);
								fileCommGenerateResultList.add(fileCommGenerateResult);
							}

							if (CommonUtil.isValidObject(neMappingEntity)
									&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
									&& (neMappingEntity.getSiteConfigType()
											.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
											|| neMappingEntity.getSiteConfigType()
													.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
								JSONObject fileProcessResult = createExecutableFiles(ciqFileName, programId,
										uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId);
								JSONObject fileProcessResult1 = createExecutableFilesForNEGrow(ciqFileName, programId,
										csvPath, neMappingEntity, enbId, enbName, sessionId, "single","", fileCsvGenerateResultList);

								if (fileProcessResult != null && fileProcessResult.containsKey("status")
										&& fileProcessResult.get("status").equals(Constants.FAIL)) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", fileProcessResult.get("reason"));
									return mapObject;
								}

							}

							logger.info("generateallEnodebFiles in GenerateCsvController fileEnvGenerateResultList : "
									+ fileEnvGenerateResultList.size() + ", fileCsvGenerateResultList: "
									+ fileCsvGenerateResultList.size() + ", fileCommGenerateResultList: "
									+ fileCommGenerateResultList.size());

							for (JSONObject object : fileEnvGenerateResultList) {
								if (object.get("status") != null && (boolean) object.get("status")) {
									fileGenerateEnvStatus = true;
								} else if (object.get("status") != null && !(boolean) object.get("status")) {
									fileGenerateEnvStatus = false;
									logger.info("generateallEnodebFiles in GenerateCsvController failed for file : "
											+ object.get("fileName"));
									break;
								}
							}

							for (JSONObject object : fileCsvGenerateResultList) {
								if (object.get("status") != null && (boolean) object.get("status")) {
									fileGenerateCsvStatus = true;
								} else if (object.get("status") != null && !(boolean) object.get("status")) {
									fileGenerateCsvStatus = false;
									logger.info("generateallEnodebFiles in GenerateCsvController failed for file : "
											+ object.get("fileName"));
									break;
								}
							}

							for (JSONObject object : fileCommGenerateResultList) {
								if (object.get("status") != null && (boolean) object.get("status")) {
									fileCommGenerateStatus = true;
								} else if (object.get("status") != null && !(boolean) object.get("status")) {
									fileCommGenerateStatus = false;
									logger.info("generateallEnodebFiles in GenerateCsvController failed for file : "
											+ object.get("fileName"));
									break;
								}
							}

							logger.info("generateallEnodebFiles in GenerateCsvController fileGenerateEnvStatus : "
									+ fileGenerateEnvStatus + ", fileGenerateCsvStatus: " + fileGenerateCsvStatus
									+ ", fileCommGenerateStatus: " + fileCommGenerateStatus);

							if (!(fileGenerateEnvStatus && fileGenerateCsvStatus && fileCommGenerateStatus)) {
								objFailedData.put(enbName, "dataFailed");
							}

							if (envFileExist) {
								mapObject = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_ENV,
										envUploadPath.toString(), ciqFileName, enbName, remarks, programDetailsEntity,
										fileEnvGenerateResultList,"","","");
								if (mapObject != null && mapObject.containsKey("status")
										&& mapObject.get("status").equals(Constants.FAIL)) {
									System.out.println("---------15-------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									logger.info(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE) + " " + Constants.FILE_TYPE_ENV);
									return mapObject;
								}
							} else {
								FileUtil.deleteFileOrFolder(envUploadPath.toString());
							}
							if (csvFileExist) {
								mapObject = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_CSV,
										csvUploadPath.toString(), ciqFileName, enbName, remarks, programDetailsEntity,
										fileCsvGenerateResultList,"","","");
								if (mapObject != null && mapObject.containsKey("status")
										&& mapObject.get("status").equals(Constants.FAIL)) {
									System.out.println("-------16-------------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									logger.info(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE) + " " + Constants.FILE_TYPE_CSV);
									return mapObject;
								}
							} else {
								FileUtil.deleteFileOrFolder(csvUploadPath.toString());
							}

							if (commFileExist) {
								mapObject = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_COMM_SCRIPT,
										commUploadPath.toString(), ciqFileName, enbName, remarks, programDetailsEntity,
										fileCommGenerateResultList,"","","");
								if (mapObject != null && mapObject.containsKey("status")
										&& mapObject.get("status").equals(Constants.FAIL)) {
									System.out.println("---------17-----------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									logger.info(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE) + " "
											+ Constants.FILE_TYPE_COMM_SCRIPT);
									return mapObject;
								}
							} else {
								FileUtil.deleteFileOrFolder(commUploadPath.toString());
							}

						} catch (Exception e) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.FAILED_TO_GENERATE_ENB_FILES));
						}

					}

				}

			}

			if (objFailedData.size() > 0 || (mapObject != null && mapObject.containsKey("status")
					&& mapObject.get("status").equals(Constants.FAIL))) {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_ENB_FILES));
			} else {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILES_GENERATE_SUCCESSFULLY));
			}

		} catch (Exception e) {
			logger.info("Exception in generateallEnodebFiles in GenerateCsvController "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}

	/**
	 * This method will generateCiqBasedFiles
	 * 
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/generateCiqNeBasedFiles")
	public JSONObject generateCiqNeBasedFiles(@RequestBody JSONObject ciqDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String fileName = null;
		String programId = null;
		String enbId = null;
		String enbName = null;
		String wFMStatus = "false";
		AtomicBoolean status = new AtomicBoolean(true);
		OvScheduledEntity ovScheduledEntity = null;
		List<OvScheduledEntity> ovScheduledEntityList = null;
		try {
			if (StringUtils.isNotEmpty((String) ciqDetails.get("wfmStatus"))) {
				wFMStatus = ciqDetails.get("wfmStatus").toString();
			}
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = ciqDetails.get("ciqName").toString();
			// fileName = ciqDetails.get("ciqFileName").toString();
			programId = ciqDetails.get("programId").toString();
			enbId = ciqDetails.get("enbId").toString();
			enbName = ciqDetails.get("enbName").toString();
			String programName = ciqDetails.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, fileName);
			// some where else we used
			LinkedHashMap<String, String> objFailedData = new LinkedHashMap<>();
			/*if (!programName.contains("5G")) {
				JSONObject dspIDCheck = checkDuplicateDSP(programId, fileName, dbcollectionFileName, sessionId,
						serviceToken, enbId, enbName);
				if (dspIDCheck.containsKey("result") && dspIDCheck.get("result").equals("empty")) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Grow Fail - CPRI Values are not proper");
					status.set(false);
				}
				
				if (dspIDCheck.containsKey("status") && dspIDCheck.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", dspIDCheck.get("reason"));
					status.set(false);
				}
			}*/

			JSONObject result = null;
			if (!(programName.contains("DSS") || programName.contains("5G-CBAND")) && status.get()) {
				result = generateSingelEnodebFiles(programName, programId, fileName, dbcollectionFileName, sessionId,
						serviceToken, enbId, enbName, objFailedData, wFMStatus,ciqDetails);
				if(result.containsKey("status") && result.get("status").equals("FAILED"))
				{
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result.get("reason"));
					status.set(false);
				}
			} else if(status.get()){
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId);
				NeMappingModel neMappingModel = new NeMappingModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(Integer.valueOf(programId));
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setEnbId(enbId);
				List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);

				if (!CommonUtil.isValidObject(neMappingEntities) || neMappingEntities.size() <= 0) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
					status.set(false);
				}
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);

				if (!CommonUtil.isValidObject(neMappingEntity)
						|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
					status.set(false);
				}
				if(status.get()) {
					result = createExecutableFiles5GDSS(fileName, Integer.valueOf(programId), uploadPath.toString(),
							neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);
				}				
				
				JSONObject reportsObject = new JSONObject();
				Map<String, JSONObject> reportsMap = new LinkedHashMap<>();
				if(status.get()) {
					JSONObject result1 = new JSONObject(result);
					reportsMap.put("DSS", result1);
				} else {
					result = new JSONObject();
				}
				reportsObject.put(enbId, reportsMap);
				result.put("reportsMap", reportsObject);				
				
			}
			if(programName.equals("VZN-4G-USM-LIVE")) {
				System.out.println("Entered into program 4G USM in generateCiqNeBasedFiles");
			StringBuilder scriptUploadPath = new StringBuilder();
			StringBuilder uploadPaths = new StringBuilder();
			uploadPaths.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
			// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
			.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
			.append(Constants.SEPARATOR);
			scriptUploadPath.append(uploadPaths)
			.append(Constants.PRE_MIGRATION_SCRIPT
					.replace("filename", StringUtils.substringBeforeLast(fileName.toString(), "."))
					.replaceAll(" ", "_"));
			scriptUploadPath.append(enbId);
			File fp= new File(scriptUploadPath.toString());
			/*if(!fp.exists() || !fp.isDirectory()) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Associated RF Scripts are not Uploaded");
				return resultMap;
			}*/
			}
			if(!result.containsKey("reportsMap")) {
				JSONObject reportsObject = new JSONObject();
				Map<String, JSONObject> reportsMap = new LinkedHashMap<>();
				reportsObject.put(enbId, reportsMap);
				result.put("reportsMap", reportsObject);
			}
			JSONObject reportsObject = (JSONObject)result.get("reportsMap");
			Set<String> reportskey = reportsObject.keySet();
			for(String key: reportskey) {
				System.out.println(reportsObject.get(key));
			}
			JSONObject scheduleObject = (JSONObject)result.get("scheduleMap");
			if(scheduleObject!=null) {
			Set<String> schedulekey = scheduleObject.keySet();
			for(String key: schedulekey) {
				System.out.println(scheduleObject.get(key));
			}
			ovScheduling(ciqDetails,reportskey,scheduleObject,reportsObject,programName,ovScheduledEntity,ovScheduledEntityList);
			}
			reportService.insertReportDetails(reportsObject, Integer.parseInt(programId), programName, user.getUserName(), fileName, "RANConfig");
			logger.error("no issues...its working");
			if(!status.get()) {
				return resultMap;
			}
			 if (result != null && result.containsKey("status") && result.get("status").equals(Constants.SUCCESS) 
						&& result.containsValue(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILES_GENERATE_SUCCESSFULLY))) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILES_GENERATE_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_GENERATE, "Files Generated Successfully For: " + enbId, sessionId);
					LinkedHashMap<String, String> CombinedResult = (LinkedHashMap<String, String>) result
							.get("CombinedResult");
					resultMap.put("CombinedResult", CombinedResult);
					resultMap.put("paths4G", result.get("paths4G"));
					System.out.println("File generate successfully, result success");
					return resultMap;
				}else if (result != null && result.containsKey("status") && result.get("status").equals(Constants.FAIL)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result.get("reason"));
					System.out.println("File generate fail");
					return resultMap;
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result.get("reason"));
					System.out.println("File generate fail, else wdout if");
					return resultMap;
				}

		} catch (Exception e) {
			logger.info("Exception in generateCiqNeBasedFiles in GenerateCsvController "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_ENB_FILES));
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	private boolean fileCompare(StringBuilder filepath1, StringBuilder filepath2) {
		boolean areEqual = true;
		try {
			BufferedReader reader1 = new BufferedReader(new FileReader(filepath1.toString()));
			BufferedReader reader2 = new BufferedReader(new FileReader(filepath2.toString()));
			String line1 = reader1.readLine();
			String line2 = reader2.readLine();
			int lineNum = 1;
			while (line1 != null || line2 != null) {
				if (line1 == null || line2 == null) {
					areEqual = false;
					break;
				} else if (!line1.equalsIgnoreCase(line2)) {
					areEqual = false;
					break;
				}
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				lineNum++;
			}
			reader1.close();
			reader2.close();
		} catch (Exception e) {
			logger.info("Exception in fileComapre() in GenerateCsvController "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return areEqual;
	}
	private boolean filecompare(String oldFileName,String oldFilePath,String newFilePath,String newFileName,String fileType) throws IOException {
		
		int count = 0;
		boolean status;
		StringBuilder filepath1 = new StringBuilder();
		StringBuilder filepath2 = new StringBuilder();
		if(fileType.equals("ENV") || fileType.equals(""))
		{
			 filepath1.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(oldFilePath).append(oldFileName);
			 filepath2.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(newFilePath).append(newFileName);
			 status = fileCompare(filepath1,filepath2);
		}
		else
		{
			String[] files1 = oldFileName.split(",");
			String[] files2 = newFileName.split(",");
			for(int i=0;i< files1.length;i++) {
				filepath1.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(oldFilePath).append(files1[i]);
				filepath2.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(newFilePath).append(files2[i]);
				status = fileCompare(filepath1,filepath2);
				if(status)
					count++;
			}
			if(count == files1.length)
				status = true;
			else
				status = false;
		}
		
		return status;
	}
	
	
	@SuppressWarnings("static-access")
	private void ovScheduling(JSONObject ciqDetails,Set<String> reportskey ,JSONObject scheduleObject,JSONObject reportsObject,
			String programName ,OvScheduledEntity ovScheduledEntity,List<OvScheduledEntity> ovScheduledEntityList){
		
		OvScheduledEntity ovScheduledEntityy = null;
		try {
			System.out.println("Inside ovScheduling");
			if (ciqDetails.containsKey("type") && ciqDetails.get("type").equals("OV")) {
				for (String key1 : reportskey) {
					Map<String, JSONObject> reportsMap = (Map<String, JSONObject>) reportsObject.get(key1);
					Map<String, String> envScheduleMap = (Map<String, String>) scheduleObject.get(key1);
					boolean fileStatus = false;

					ovScheduledEntity = ovScheduledTaskService
							.getOvScheduledServiceDetails(ciqDetails.get("trackerId").toString(), key1);
					if (ovScheduledEntity != null) {
						if (programName.contains("4G-USM-LIVE")) {
							System.out.println("ovScheduling program check check-in");
//							if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
//									&& reportsMap.get("ENV").get("status").equals(true) && reportsMap.containsKey("enb")
//									&& reportsMap.get("enb").containsKey("status")
//									&& reportsMap.get("enb").get("status").equals(true)
//									&& reportsMap.containsKey("COMM") && reportsMap.get("COMM").containsKey("status")
//									&& reportsMap.get("COMM").get("status").equals(true)) {

							//	ovScheduledEntity.setPreMigStatus("Completed");
							/*for env text file
							 * if (reportsMap.containsKey("env") && reportsMap.get("env").containsKey("status")
									&& reportsMap.get("env").get("status").equals(true)){
							 */
							if (reportsMap.containsKey("zipENV") && reportsMap.get("zipENV").containsKey("statusZip")
									&& reportsMap.get("zipENV").get("statusZip").equals(true)) {
								if (!envScheduleMap.isEmpty()) {
									logger.error("Generate csv controller checking for env");
									if (ovScheduledEntity.getEnvFileName() != null
											&& ovScheduledEntity.getEnvFilePath() != null) {
										fileStatus = filecompare(ovScheduledEntity.getEnvFileName(),
												ovScheduledEntity.getEnvFilePath(),
												envScheduleMap.get("EnvFilePath").toString(),
												envScheduleMap.get("EnvFileName").toString(),"env");
									}
									ovScheduledEntity.setEnvFilePath(envScheduleMap.get("EnvFilePath").toString());
									ovScheduledEntity.setEnvFileName(envScheduleMap.get("EnvFileName").toString());
									ovScheduledEntity.setEnvStatus("Completed");
									String fileNmae = envScheduleMap.get("EnvFileName").toString();
									System.out.println("In if zipENV");
									logger.error(" env file name is" + fileNmae);
									if (!fileStatus) {
										if (ciqDetails.containsKey("EnvExport")
												&& ciqDetails.get("EnvExport").equals(true)) {
											ovScheduledEntity = fetchProcessService
													.getOvEnvUploadDetails(ovScheduledEntity);
											ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
													"ENV","",programName);
											ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
													"CIQ","",programName);
													System.out.println("the env sttus json : " +ovScheduledEntity.getEnvStatusJson()+ " for id "+ovScheduledEntity.getNeId());
										System.out.println("the env status : "+ovScheduledEntity.getEnvStatus());
										System.out.println("the env status date : "+ovScheduledEntity.getEnvGenerationDate());
										}
									}
								}
							} else {
								System.out.println("In else zipENV");
								ovScheduledEntity.setEnvExportStatus("Failure");
								ovScheduledEntity.setEnvStatus("Failure");
								ovScheduledEntity.setEnvFilePath(null);
								ovScheduledEntity.setEnvFileName(null);
								ovScheduledEntity.setEnvGenerationDate(null);
								ovScheduledEntity.setEnvStatusJson(null);
								ovScheduledEntity.setEnvUploadJson(null);
							}
							
							if(LoadPropertyFiles.getInstance().getProperty("ciqType").equals("OLD")) {
								System.out.println("### ID - "+ovScheduledEntity.getNeId());
							if (reportsMap.containsKey("enb") && reportsMap.get("enb").containsKey("status")
									&& reportsMap.get("enb").get("status").equals(true)) {
								System.out.println("### Checking enb status in ov scheduling neID "+ovScheduledEntity.getNeId());
								if (!envScheduleMap.isEmpty()) {
									System.out.println("### envScheduleMap isn't empty for : "+ovScheduledEntity.getNeId());
									if (ovScheduledEntity.getGrowFileName() != null
											&& ovScheduledEntity.getGrowFilePath() != null) {
										System.out.println("### GrowFileName isn't empty for : "+ovScheduledEntity.getNeId());
										fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
												ovScheduledEntity.getGrowFilePath(),
												envScheduleMap.get("growFilePath").toString(),
												envScheduleMap.get("growFileName").toString(),"");
									}
									ovScheduledEntity.setGrowFileName(envScheduleMap.get("growFileName").toString());
									ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
									ovScheduledEntity.setPreMigGrowStatus("Completed");
									if (!fileStatus) {
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"GROW","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
												System.out.println("### the grow status : "+ovScheduledEntity.getPreMigGrowStatus().toString());
									System.out.println("### the grow status date : "+ovScheduledEntity.getPreMigGrowGenerationDate().toString());
									}
								} else {
									System.out.println("### Envschedule map is empty for ID "+ovScheduledEntity.getNeId());
								}

							} else {
								ovScheduledEntity.setPreMigGrowStatus("Failure");
								ovScheduledEntity.setPreMigGrowGenerationDate(null);
								ovScheduledEntity.setPreMigGrowJson(null);
								ovScheduledEntity.setGrowFileName(null);
								ovScheduledEntity.setGrowFilePath(null);
							}
						}else {
							if(reportsMap.containsKey("enb") && reportsMap.get("enb").containsKey("status")
									&& reportsMap.get("enb").get("status").equals(true) && reportsMap.containsKey("cell")
									&& reportsMap.get("cell").containsKey("status")
									&& reportsMap.get("cell").get("status").equals(true)){
								System.out.println("*** ID "+ovScheduledEntity.getNeId());
								String fileName = envScheduleMap.get("growFileName").toString()+","+ envScheduleMap.get("growCellFileName").toString();
								System.out.println("*** envScheduleMap "+envScheduleMap);
								if (!envScheduleMap.isEmpty()) {
									System.out.println("*** Checking enb status in ov scheduling neID "+ovScheduledEntity.getNeId());
									if (ovScheduledEntity.getGrowFileName() != null
											&& ovScheduledEntity.getGrowFilePath() != null) {
										System.out.println("*** getGrowFileName "+ovScheduledEntity.getGrowFileName());
										System.out.println("*** getGrowFilePath "+ovScheduledEntity.getGrowFilePath());
										fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
												ovScheduledEntity.getGrowFilePath(),
												envScheduleMap.get("growFilePath").toString(),
												fileName,"GROW");
									}
									ovScheduledEntity.setGrowFileName(fileName);
									ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
									ovScheduledEntity.setPreMigGrowStatus("Completed");
									if (!fileStatus) {
										System.out.println("*** fileStatus "+fileStatus);
										System.out.println("*** file status is ntrue for "+ovScheduledEntity.getNeId());
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"GROW","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
												System.out.println("*** the grow sttus json : " +ovScheduledEntity.getPreMigGrowJson().toString());
												System.out.println("*** the grow status : "+ovScheduledEntity.getPreMigGrowStatus().toString());
									System.out.println("*** the grow status date : "+ovScheduledEntity.getPreMigGrowGenerationDate().toString());
									}
								} else {
									System.out.println("*** env scheduled map is empty for id "+ovScheduledEntity.getNeId());
								}
								
							}else {
								ovScheduledEntity.setPreMigGrowStatus("Failure");
								ovScheduledEntity.setPreMigGrowGenerationDate(null);
								ovScheduledEntity.setPreMigGrowJson(null);
								ovScheduledEntity.setGrowFileName(null);
								ovScheduledEntity.setGrowFilePath(null);
							}
							}
						
//							} else {
//								ovScheduledEntity.setPreMigStatus("Failure");
//								ovScheduledEntity.setEnvStatus("Failure");
//								ovScheduledEntity.setPreMigGrowStatus("Failure");
//							}
						}
					if(programName.contains("5G-MM")) {
//						if (reportsMap.containsKey("ROUTE") && reportsMap.get("ROUTE").containsKey("status")
//								&& reportsMap.get("ROUTE").get("status").equals(true)  && reportsMap.containsKey("ANCHOR")
//								&& reportsMap.get("ANCHOR").containsKey("status")
//								&& reportsMap.get("ANCHOR").get("status").equals(true) && reportsMap.containsKey("CELL") && reportsMap.get("CELL").containsKey("status")
//								&& reportsMap.get("CELL").get("status").equals(true) && reportsMap.containsKey("AU")
//								&& reportsMap.get("AU").containsKey("status")
//								&& reportsMap.get("AU").get("status").equals(true)
//								&& reportsMap.containsKey("pnp_macro")
//								&& reportsMap.get("pnp_macro").containsKey("status")
//								&& reportsMap.get("pnp_macro").get("status").equals(true) && reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
//								&& reportsMap.get("ENV").get("status").equals(true)) {

							//ovScheduledEntity.setPreMigStatus("Completed");

							if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
							&& reportsMap.get("ENV").get("status").equals(true)) {
						if (!envScheduleMap.isEmpty()) {
							if (ovScheduledEntity.getEnvFileName() != null
									&& ovScheduledEntity.getEnvFilePath() != null) {
								fileStatus = filecompare(ovScheduledEntity.getEnvFileName(),
										ovScheduledEntity.getEnvFilePath(),
										envScheduleMap.get("EnvFilePath").toString(),
										envScheduleMap.get("EnvFileName").toString(),"env");
							}
							ovScheduledEntity.setEnvFilePath(envScheduleMap.get("EnvFilePath").toString());
							ovScheduledEntity.setEnvFileName(envScheduleMap.get("EnvFileName").toString());
							ovScheduledEntity.setEnvStatus("Completed");
							String fileNmae = envScheduleMap.get("EnvFileName").toString();
							logger.error(" env file nmae is" + fileNmae);
							if (!fileStatus) {
								if (ciqDetails.containsKey("EnvExport")
										&& ciqDetails.get("EnvExport").equals(true)) {
									ovScheduledEntity = fetchProcessService
											.getOvEnvUploadDetails(ovScheduledEntity);
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"ENV","",programName);
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"CIQ","",programName);
											System.out.println("the env sttus json : " +ovScheduledEntity.getEnvStatusJson().toString());
											//System.out.println("the grow sttus" +ovScheduledEntity.getPreMigGrowJson().toString());
											System.out.println("the env status : "+ovScheduledEntity.getEnvStatus().toString());
										System.out.println("the env status date : "+ovScheduledEntity.getEnvGenerationDate().toString());
								}
							}
						}
							if (!envScheduleMap.isEmpty()) {
								int count=0,update=0;
								boolean updateEnvStatus = false;
								if (ovScheduledEntity.getEnvFileName() != null
										&& ovScheduledEntity.getEnvFilePath() != null) {
									fileStatus = filecompare(ovScheduledEntity.getEnvFileName(),
											ovScheduledEntity.getEnvFilePath(),
											envScheduleMap.get("EnvFilePath").toString(),
											envScheduleMap.get("EnvFileName").toString(),"ENV");
								}
								ovScheduledEntity.setEnvFilePath(envScheduleMap.get("EnvFilePath").toString());
								ovScheduledEntity.setEnvFileName(envScheduleMap.get("EnvFileName").toString());
								ovScheduledEntity.setEnvStatus("Completed");
								ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);

								ovScheduledEntityList = ovScheduledTaskService
										.getOvScheduledServiceDetailsList(ciqDetails.get("trackerId").toString(),programName);
								
								for(OvScheduledEntity ovEntity : ovScheduledEntityList) {
										if(ovScheduledEntity.getId() != ovEntity.getId()) {
												if(ovEntity.getEnvStatus().equals("Completed") && ovEntity.getEnvExportStatus()!= null && !fileStatus )
													update++;
												if(ovEntity.getEnvStatus().equals("Completed") && Objects.isNull(ovEntity.getEnvExportStatus()))
													count++;
										}
								}  
								if(count==ovScheduledEntityList.size()-1)
									updateEnvStatus = true;
								if(update==ovScheduledEntityList.size()-1)
									updateEnvStatus = true;
								
//								if (updateEnvStatus) {
									if (ciqDetails.containsKey("EnvExport")
											&& ciqDetails.get("EnvExport").equals(true)) {
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"ENV","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
										
										if (count == ovScheduledEntityList.size()-1) {
											for (OvScheduledEntity ovEntity : ovScheduledEntityList) {
												ovScheduledEntityy = fetchProcessService
														.getOvEnvUploadDetails(ovEntity);
												if (ovScheduledEntityy.getId() != ovScheduledEntity.getId()) {
													ovScheduledEntityy.setEnvGenerationDate(
															ovScheduledEntity.getEnvGenerationDate());
													ovScheduledEntityy.setEnvStatusJson(ovScheduledEntity.getEnvStatusJson());
													ovScheduledEntityy.setCiqUpdateJson(ovScheduledEntity.getCiqUpdateJson());
													ovScheduledEntityy.setCiqGenerationDate(
															ovScheduledEntity.getCiqGenerationDate());
													ovScheduledEntityy = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntityy);
												} else {
													ovScheduledEntity.setEnvExportStatus(ovScheduledEntityy.getEnvExportStatus());
													ovScheduledEntity.setEnvUploadJson(ovScheduledEntityy.getEnvUploadJson());
													ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
												}
											}
										}else
											ovScheduledEntity = fetchProcessService
											.getOvEnvUploadDetails(ovScheduledEntity);
									}
//								}
							} 
					} 
						else {
							ovScheduledEntity.setEnvExportStatus("Failure");
							ovScheduledEntity.setEnvStatus("Failure");
							ovScheduledEntity.setEnvFilePath(null);
							ovScheduledEntity.setEnvFileName(null);
							ovScheduledEntity.setEnvGenerationDate(null);
							ovScheduledEntity.setEnvStatusJson(null);
							ovScheduledEntity.setEnvUploadJson(null);
						}

						if (reportsMap.containsKey("CELL") && reportsMap.get("CELL").containsKey("status")
								&& reportsMap.get("CELL").get("status").equals(true) && reportsMap.containsKey("AU")
								&& reportsMap.get("AU").containsKey("status")
								&& reportsMap.get("AU").get("status").equals(true) && reportsMap.containsKey("pnp_macro")
								&& reportsMap.get("pnp_macro").containsKey("status")
								&& reportsMap.get("pnp_macro").get("status").equals(true)) {
							//String fileName = envScheduleMap.get("growFileName").toString()+","+ envScheduleMap.get("growCellFileName").toString();
							String fileName = envScheduleMap.get("cellGrowFileName").toString()+","+ envScheduleMap.get("AuGrowFileName").toString()+
									","+envScheduleMap.get("Pnp_macroGrowFileName").toString();
							if (!envScheduleMap.isEmpty()) {
								if (ovScheduledEntity.getGrowFileName() != null
										&& ovScheduledEntity.getGrowFilePath() != null) {
									fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
											ovScheduledEntity.getGrowFilePath(),
											envScheduleMap.get("growFilePath").toString(),
											fileName,"GROW");
								}
								ovScheduledEntity.setGrowFileName(fileName);
								ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
								ovScheduledEntity.setPreMigGrowStatus("Completed");
								if (!fileStatus) {
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"GROW","",programName);
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"CIQ","",programName);
											System.out.println("the grow sttus json : " +ovScheduledEntity.getPreMigGrowJson().toString());
											System.out.println("the grow status : "+ovScheduledEntity.getPreMigGrowStatus().toString());
									System.out.println("the grow status date : "+ovScheduledEntity.getPreMigGrowGenerationDate().toString());
								}
							}
						} 

							if (!envScheduleMap.isEmpty()) {
								int count=0,update=0;
								boolean updateGrowStatus =  false;
								String fileName = envScheduleMap.get("cellGrowFileName").toString()+","+ envScheduleMap.get("AuGrowFileName").toString()+
										","+envScheduleMap.get("Pnp_macroGrowFileName").toString();
								if (ovScheduledEntity.getGrowFileName() != null
										&& ovScheduledEntity.getGrowFilePath() != null) {
									fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
											ovScheduledEntity.getGrowFilePath(),
											envScheduleMap.get("growFilePath").toString(),
											fileName,"GROW");
								}
								ovScheduledEntity.setGrowFileName(fileName);
								ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
								ovScheduledEntity.setPreMigGrowStatus("Completed");
								ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);

								ovScheduledEntityList = ovScheduledTaskService
										.getOvScheduledServiceDetailsList(ciqDetails.get("trackerId").toString(),programName);
  
								for(OvScheduledEntity ovEntity : ovScheduledEntityList) {
									if(ovScheduledEntity.getId() != ovEntity.getId()) {
											if(ovEntity.getPreMigGrowStatus().equals("Completed") && ovEntity.getPreMigGrowJson()!= null && !fileStatus )
												update++;
											if(ovEntity.getPreMigGrowStatus().equals("Completed") && Objects.isNull(ovEntity.getPreMigGrowJson()))
												count++;
									}
							}  
								if(count==ovScheduledEntityList.size()-1)
									updateGrowStatus =true;
								if(update==ovScheduledEntityList.size()-1)
									updateGrowStatus = true;
								
//								if (updateGrowStatus) {
									
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"GROW","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
										
										if (count == ovScheduledEntityList.size()-1) {
											for (OvScheduledEntity ovEntity : ovScheduledEntityList) {
												if (ovEntity.getId() != ovScheduledEntity.getId()) {
													ovEntity.setPreMigGrowGenerationDate(
															ovScheduledEntity.getPreMigGrowGenerationDate());
													ovEntity.setPreMigGrowJson(ovScheduledEntity.getPreMigGrowJson());
													ovEntity.setCiqUpdateJson(ovScheduledEntity.getCiqUpdateJson());
													ovEntity.setCiqGenerationDate(
															ovScheduledEntity.getCiqGenerationDate());
													ovEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovEntity);
												} 
											}
										}
//									}
								
							}

						
						
						
						else {
							ovScheduledEntity.setPreMigGrowStatus("Failure");
							ovScheduledEntity.setPreMigGrowGenerationDate(null);
							ovScheduledEntity.setPreMigGrowJson(null);
							ovScheduledEntity.setGrowFileName(null);
							ovScheduledEntity.setGrowFilePath(null);
						}

//						} else {
//							ovScheduledEntity.setPreMigStatus("Failure");
//							ovScheduledEntity.setPreMigGrowStatus("Failure");
//							ovScheduledEntity.setEnvStatus("Failure");
//						}
					
					}
					if(programName.contains("4G-FSU")) {
//						if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
//								&& reportsMap.get("ENV").get("status").equals(true) && reportsMap.containsKey("enb")
//								&& reportsMap.get("CSV").containsKey("status")
//								&& reportsMap.get("CSV").get("status").equals(true)) {

						//	ovScheduledEntity.setPreMigStatus("Completed");
						if (reportsMap.containsKey("ENV_FSU") && reportsMap.get("ENV_FSU").containsKey("status")
								&& reportsMap.get("ENV_FSU").get("status").equals(true)) {
							if (!envScheduleMap.isEmpty()) {
								if (ovScheduledEntity.getEnvFileName() != null
										&& ovScheduledEntity.getEnvFilePath() != null) {
									fileStatus = filecompare(ovScheduledEntity.getEnvFileName(),
											ovScheduledEntity.getEnvFilePath(),
											envScheduleMap.get("fsuEnvFilePath").toString(),
											envScheduleMap.get("fsuEnvFileName").toString(),"ENV");
								}
								ovScheduledEntity.setEnvFilePath(envScheduleMap.get("fsuEnvFilePath").toString());
								ovScheduledEntity.setEnvFileName(envScheduleMap.get("fsuEnvFileName").toString());
								ovScheduledEntity.setEnvStatus("Completed");
								String fileNmae = envScheduleMap.get("fsuEnvFileName").toString();
								logger.error(" env file nmae is" + fileNmae);
								if (!fileStatus) {
									if (ciqDetails.containsKey("EnvExport")
											&& ciqDetails.get("EnvExport").equals(true)) {
										ovScheduledEntity = fetchProcessService
												.getOvEnvUploadDetails(ovScheduledEntity);
										if (!programName.contains("FSU")) {
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"ENV","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
										}
											//	System.out.println("the env sttus json : " +ovScheduledEntity.getEnvStatusJson().toString());
										System.out.println("the env status"+ovScheduledEntity.getEnvStatus().toString());
										//System.out.println("the env status"+ovScheduledEntity.getEnvGenerationDate().toString());
										break;
									}
								}
							}	
						} 
						/*
						else {
							ovScheduledEntity.setEnvExportStatus("Failure");
							ovScheduledEntity.setEnvStatus("Failure");
							ovScheduledEntity.setEnvFilePath(null);
							ovScheduledEntity.setEnvFileName(null);
							ovScheduledEntity.setEnvGenerationDate(null);
							ovScheduledEntity.setEnvStatusJson(null);
							ovScheduledEntity.setEnvUploadJson(null);
						}*/

						if (reportsMap.containsKey("CSV") && reportsMap.get("CSV").containsKey("status")
								&& reportsMap.get("CSV").get("status").equals(true)) {
							
								String fileName = envScheduleMap.get("fsugrowFileName").toString();
								if (!envScheduleMap.isEmpty()) {
									if (ovScheduledEntity.getGrowFileName() != null
											&& ovScheduledEntity.getGrowFilePath() != null) {
										fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
												ovScheduledEntity.getGrowFilePath(),
												envScheduleMap.get("fsugrowFilePath").toString(),
												fileName,"GROW");
									}
									ovScheduledEntity.setGrowFileName(fileName);
									ovScheduledEntity.setGrowFilePath(envScheduleMap.get("fsugrowFilePath").toString());
									ovScheduledEntity.setPreMigGrowStatus("Completed");
									System.out.println("++++++call is coming for grow+++++");
									if (!fileStatus) {
										System.out.println("@@@@@@call is coming for grow inside[[[[");
										if (!programName.contains("FSU")) {
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"GROW","",programName);
										ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
												"CIQ","",programName);
										}
										//System.out.println("the grow sttus json : " +ovScheduledEntity.getPreMigGrowJson().toString());
										//System.out.println("the grow status : "+ovScheduledEntity.getPreMigGrowStatus().toString());
									//System.out.println("the grow status date : "+ovScheduledEntity.getPreMigGrowGenerationDate().toString());
									}
								}
							

						} else {
							ovScheduledEntity.setPreMigGrowStatus("Failure");
							ovScheduledEntity.setPreMigGrowGenerationDate(null);
							ovScheduledEntity.setPreMigGrowJson(null);
							ovScheduledEntity.setGrowFileName(null);
							ovScheduledEntity.setGrowFilePath(null);
						}

//						} else {
//							ovScheduledEntity.setPreMigStatus("Failure");
//							ovScheduledEntity.setPreMigGrowStatus("Failure");
//							ovScheduledEntity.setEnvStatus("Failure");
//						}
					}
					if( programName.contains("5G-CBAND")) {
						if(reportsMap.containsKey("vDUpnp")&& reportsMap.get("vDUpnp").containsKey("status")&& reportsMap.get("vDUpnp").get("status").equals(true)&&
								reportsMap.containsKey("vDUcell")&& reportsMap.get("vDUcell").containsKey("status")&& reportsMap.get("vDUcell").get("status").equals(true)&& 
								reportsMap.containsKey("vDUgrow")&& reportsMap.get("vDUgrow").containsKey("status")&& reportsMap.get("vDUgrow").get("status").equals(true) ){
							if (!envScheduleMap.isEmpty()) {
								String fileName = envScheduleMap.get("vDUpnpGrowFileName").toString()+","+ envScheduleMap.get("vDUcellGrowFileName").toString()+
										","+envScheduleMap.get("vDUGrowFileName").toString();
								if (ovScheduledEntity.getGrowFileName() != null
										&& ovScheduledEntity.getGrowFilePath() != null) {
									
									fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
											ovScheduledEntity.getGrowFilePath(),
											envScheduleMap.get("growFilePath").toString(),
											fileName,"GROW");
								
								
					}
								ovScheduledEntity.setGrowFileName(fileName);
								ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
								
								ovScheduledEntity.setPreMigGrowStatus("Completed");
								if (!fileStatus) {
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"GROW","",programName);
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"CIQ","",programName);
									ovScheduledEntity.setEnvExportStatus("-");
									ovScheduledEntity.setEnvStatus("-");
									ovScheduledEntity.setEnvFileName("-");
									ovScheduledEntity.setEnvGenerationDate("-");
									logger.error("output &&&&&&&&");
								}	
						
								}	
					}else {
						ovScheduledEntity.setPreMigStatus("Failure");
						ovScheduledEntity.setEnvExportStatus("-");
						ovScheduledEntity.setEnvStatus("-");
						ovScheduledEntity.setEnvFileName("-");
						ovScheduledEntity.setEnvGenerationDate("-");
						ovScheduledEntity.setPreMigGrowStatus("-");
						ovScheduledEntity.setPreMigGrowGenerationDate("-");
						ovScheduledEntity.setCiqGenerationDate(null);
						logger.error("#############################");
					}
						
					
					}
					if( programName.contains("5G-DSS")) {
						if(reportsMap.containsKey("pnpGrow")&& reportsMap.get("pnpGrow").containsKey("status")&& reportsMap.get("pnpGrow").get("status").equals(true)&&
							reportsMap.containsKey("vDUCellGrow")&& reportsMap.get("vDUCellGrow").containsKey("status")&& reportsMap.get("vDUCellGrow").get("status").equals(true)&& 
							reportsMap.containsKey("vDUGrow")&& reportsMap.get("vDUGrow").containsKey("status")&& reportsMap.get("vDUGrow").get("status").equals(true) ) {
							if (!envScheduleMap.isEmpty()) {
								if (ovScheduledEntity.getGrowFileName() != null
										&& ovScheduledEntity.getGrowFilePath() != null) {
									
									fileStatus = filecompare(ovScheduledEntity.getGrowFileName(),
											ovScheduledEntity.getGrowFilePath(),
											envScheduleMap.get("growFilePath").toString(),
											envScheduleMap.get("growFileName").toString(),"");
								}
								ovScheduledEntity.setGrowFileName(envScheduleMap.get("growFileName").toString());
								ovScheduledEntity.setGrowFilePath(envScheduleMap.get("growFilePath").toString());
								ovScheduledEntity.setPreMigGrowStatus("Completed");
								if (!fileStatus) {
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"GROW","",programName);
									ovScheduledEntity = fetchProcessService.statusUpdateApi(ovScheduledEntity,
											"CIQ","",programName);
									ovScheduledEntity.setEnvExportStatus("-");
									ovScheduledEntity.setEnvStatus("-");
									ovScheduledEntity.setEnvFileName("-");
									ovScheduledEntity.setEnvGenerationDate("-");
									logger.error("output &&&&&&&&");
								}	
						
								}	
					}else {
						ovScheduledEntity.setPreMigStatus("Failure");
						ovScheduledEntity.setEnvExportStatus("-");
						ovScheduledEntity.setEnvStatus("-");
						ovScheduledEntity.setEnvFileName("-");
						ovScheduledEntity.setEnvGenerationDate("-");
						ovScheduledEntity.setPreMigGrowStatus("-");
						ovScheduledEntity.setPreMigGrowGenerationDate("-");
						ovScheduledEntity.setCiqGenerationDate(null);
						logger.error("#############################");
					}
						
					
					}
					/*
					if(programName.contains("5G-DSS") ) {
	//ne grow
						
							if (reportsMap.containsKey("DSS") && reportsMap.get("DSS").containsKey("status")
									&& reportsMap.get("DSS").get("status").equals(Constants.SUCCESS) 
									|| reportsMap.containsKey("CBAND") && reportsMap.get("CBAND").containsKey("status")
									&& reportsMap.get("CBAND").get("status").equals(Constants.SUCCESS)) {
								ovScheduledEntity.setPreMigStatus("Completed");
								ovScheduledEntity.setEnvExportStatus("-");
								ovScheduledEntity.setEnvStatus("-");
								ovScheduledEntity.setEnvFileName("-");
								ovScheduledEntity.setEnvGenerationDate("-");
								ovScheduledEntity.setPreMigGrowStatus("-");
								ovScheduledEntity.setPreMigGrowGenerationDate("-");
								ovScheduledEntity
								.setCiqGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
								}else {
									ovScheduledEntity.setPreMigStatus("Failure");
									ovScheduledEntity.setEnvExportStatus("-");
									ovScheduledEntity.setEnvStatus("-");
									ovScheduledEntity.setEnvFileName("-");
									ovScheduledEntity.setEnvGenerationDate("-");
									ovScheduledEntity.setPreMigGrowStatus("-");
									ovScheduledEntity.setPreMigGrowGenerationDate("-");
									ovScheduledEntity
									.setCiqGenerationDate(null);
								}
							
						}*/

				}

					ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
				}
			
			}
		
		}
		catch (Exception e) {
			
			logger.info("Exception in ovScheduleing() in GenerateCsvController "
			  + ExceptionUtils.getFullStackTrace(e));

		}
	}
	
	
	private JSONObject checkDuplicateDSP(String programId, String fileName, String dbcollectionFileName,
			String sessionId, String serviceToken, String enbId, String enbName) {
		JSONObject resultMap = new JSONObject();
		ArrayList<String> alS = new ArrayList<>();
		ArrayList<String> arr1 = new ArrayList<>();
		ArrayList<String> arr2 = new ArrayList<>();
		ArrayList<String> arr3 = new ArrayList<>();
		String check;
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(fileName, enbId,
					enbName, dbcollectionFileName);
			String[] cell_index_dsp = objGenerateCsvService.dataCiqIndexs(listCIQDetailsModel,
					Constants.VZ_GROW_DSP_CELL_INDEX, Constants.VZ_GROW_CIQUpstateNY);
			String[] dsp_id = objGenerateCsvService.dataCiqIndexs(listCIQDetailsModel, Constants.VZ_GROW_DSP_ID,
					Constants.VZ_GROW_CIQUpstateNY);
			String[] cpriPort = objGenerateCsvService.dataCiqIndexs(listCIQDetailsModel,
					Constants.VZ_GROW_CPRI_Port_Assignment, Constants.VZ_GROW_CIQUpstateNY);
			String[] ruPort = objGenerateCsvService.dataCiqIndexs(listCIQDetailsModel, Constants.VZ_GROW_RU_PORT,
					Constants.VZ_GROW_CIQUpstateNY);
			String[] power = objGenerateCsvService.dataCiqIndexs(listCIQDetailsModel, Constants.VZ_GROW_Output_Power,
					Constants.VZ_GROW_CIQUpstateNY);

			for (int j = 0; j < cpriPort.length; j++) {
				if (cpriPort[j].isEmpty()) {
					resultMap.put("result", "empty");
				}
				if (ruPort[j].contains("(")) {
					String k = StringUtils.substringBefore(ruPort[j].trim(), "(");
					int s = Integer.parseInt(k.trim());
				} else if (!ruPort[j].isEmpty()) {
					int o = Integer.parseInt(ruPort[j]);
				}
				if (cpriPort[j].contains("LCC-1")) {
					check = cell_index_dsp[j].trim() + dsp_id[j].trim();
					if (arr1.contains(check)) {
						resultMap.put("Status", "Duplicate");
					} else if (!check.equals("")) {
						arr1.add(check);
					}

				} else if (cpriPort[j].contains("LCC-2")) {
					check = cell_index_dsp[j].trim() + dsp_id[j].trim();
					if (arr2.contains(check)) {
						resultMap.put("Status", "Duplicate");
					} else if (!check.equals("")) {
						arr2.add(check);
					}
				} else {
					check = cell_index_dsp[j].trim() + dsp_id[j].trim();
					if (arr3.contains(check)) {
						resultMap.put("Status", "Duplicate");
					} else if (!check.equals("")) {
						arr3.add(check);
					}
				}
			}
		} catch (Exception e) {
			/*
			 * logger.info("Exception in generateCiqNeBasedFiles in GenerateCsvController "
			 * + ExceptionUtils.getFullStackTrace(e));
			 */
			resultMap.put("reason", "RU PORT column datatype is not proper");
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private JSONObject generateSingelEnodebFiles(String programName, String programStringId, String ciqFileName,
			String dbcollectionFileName, String sessionId, String serviceToken, String enbId, String enbName,
			LinkedHashMap<String, String> objFailedData, String wfmStatus,JSONObject ciqDetails) {
		boolean fileGenerateEnvStatus = false;
		boolean fileGenerateCsvStatus = false;
		boolean fileCommGenerateStatus = false;

		boolean envFileExist = false;
		boolean csvFileExist = false;
		boolean commFileExist = false;
		boolean allFileExist = false;
		boolean endcFileExist = false;
		JSONObject mapObject = new JSONObject();
		Integer progrmId = null;
		OvScheduledEntity ovScheduledEntity = null;
		LinkedHashMap<String, String> finalResultMap = new LinkedHashMap<String, String>();
		JSONObject finalReportsMap = new JSONObject();
		JSONObject finalscheduleMap = new JSONObject();
		String remarks = "Generated in Ran Config";
		
		progrmId = Integer.parseInt(ciqDetails.get("programId").toString());
		//
		try {
			Map<String, Object> neMap = fileUploadService.validationEnbDetails(dbcollectionFileName,
					Integer.valueOf(progrmId), enbName, enbId);
			if (neMap != null && neMap.size() > 0 && neMap.containsKey("validationDetails")) {
				List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) neMap
						.get("validationDetails");
				if (objErrorMap.size() > 0) {
					System.out.println(enbName);
					System.out.println("enbName"+enbName);
					String	neIds = "Validation failed for  NE : " + enbName+"."+objErrorMap.get(0).getErrorMessage();
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							neIds);
					return mapObject;
				}
			}
		} catch (Exception e) {
			logger.error("Validation failed NE's " + enbId);
		}


		//
		//carrier add
		Boolean supportCA = (Boolean) ciqDetails.get("supportCA");
		if (wfmStatus.equalsIgnoreCase("true")) {
			remarks = "Generated through WFM";
		}
		//fetching dummy IP
		String dummy_IP = null;
		
		if (ciqDetails.containsKey("integrationType") && ciqDetails.get("integrationType") != null) {
			dummy_IP = ciqDetails.get("integrationType").toString();
		} else {
			dummy_IP = "Legacy IP";
		}
		/*if (StringUtils.isNotEmpty(dummy_IP)) {
			if ("LegacyIP".equals(dummy_IP)) {
				dummy_IP = "Legacy IP";
			} else if ("PseudoIP".equals(dummy_IP)) {
				dummy_IP = "Pseudo IP";
			}
		}*/

		if (ciqDetails.containsKey("type") && ciqDetails.get("type").equals("OV")) {
			remarks = "Generated through Automation";
			if(ciqDetails.get("programName").toString().equals("VZN-4G-USM-LIVE")) {
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			CustomerDetailsEntity customerDetailsEntity = new CustomerDetailsEntity();
			
				progrmId = Integer.parseInt(ciqDetails.get("programId").toString());
				customerDetailsEntity.setId(progrmId);
				programTemplateModel.setProgramDetailsEntity(customerDetailsEntity);

			programTemplateModel.setConfigType("s&r");
			List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
					.getScheduledDaysDetails(programTemplateModel);
		
		String supportCACases = listProgramTemplateEntity.stream()
				.filter(entity -> Constants.SUPPORT_CA.equalsIgnoreCase(entity.getLabel()))
				.map(entity -> entity.getValue()).findFirst().get();
		
			if(supportCACases != null) {
				supportCA = supportCACases.toString().equalsIgnoreCase("ON") ? true : false;
			} else {
				supportCA = false;
			}
			
			}
		}
		try {
			String csvPath = null;
			StringBuilder uploadPath = new StringBuilder();
			uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programStringId);
			int programId = Integer.valueOf(programStringId);
			File fileExist = new File(uploadPath.toString());
			if (!fileExist.exists()) {
				FileUtil.createDirectory(uploadPath.toString());

			}

			try {
				mapObject.put("reportsMap", finalReportsMap);
				Map<String, JSONObject> reportsMaptemp = new LinkedHashMap<>();
				finalReportsMap.put(enbId, reportsMaptemp);
				mapObject.put("scheduleMap", finalscheduleMap);
				Map<String, String> scheduledtemp = new LinkedHashMap<>();
				finalscheduleMap.put(enbId, scheduledtemp);
				NeMappingModel neMappingModel = new NeMappingModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setEnbId(enbId);
				List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);

				if (!CommonUtil.isValidObject(neMappingEntities) || neMappingEntities.size() <= 0) {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
					return mapObject;
				}
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);
				
				if (!CommonUtil.isValidObject(neMappingEntity)
						|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())) {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_NOT_FOUND));
					return mapObject;
				}
				if (!programName.contains("5G")) {
					JSONObject scriptSeqResult = getScriptSequenceDetails(ciqFileName, programId, neMappingEntity,
							enbId, enbName, sessionId, serviceToken);
					if (scriptSeqResult != null && scriptSeqResult.containsKey("status")
							&& scriptSeqResult.get("status").equals(Constants.FAIL)) {
						return scriptSeqResult;
					}
				}

				// generation for multiple gnbs based on sitename
				int gSize = 1;
				String siteName = null;
				List<NeMappingEntity> data1 = null;
				List<String> gnodebIdList = new ArrayList<>();
				String oldNeId = enbId;

				if (programName.contains("VZN-5G-MM")) {
					List<NeMappingEntity> data = neMappingService.getSiteName(enbId);
					siteName = data.get(0).getSiteName();
					data1 = neMappingService.getGnodebs(siteName);
					gSize = data1.size();
					for (int y = 0; y < data1.size(); y++) {
						NeMappingEntity obj = data1.get(y);
						String gnodeb = obj.getEnbId();
						gnodebIdList.add(gnodeb);
					}

				}

				for (int z = 0; z < gSize; z++) {
					if (programName.contains("VZN-5G-MM")) {

						enbId = gnodebIdList.get(z);

						enbName = enbName.replace(oldNeId, enbId);
						oldNeId = enbId;
					}
					Map<String, JSONObject> reportsMap = new LinkedHashMap<>();
					finalReportsMap.put(enbId, reportsMap);
					Map<String, String> scheduleMap = new LinkedHashMap<>();
					finalscheduleMap.put(enbId, scheduleMap);
					// written code for 5g templates api calling also by putting condition
					JSONObject fileEnvGenerateResult = new JSONObject();
					JSONObject fileCpriGenerateResult = new JSONObject();
					List<JSONObject> fileEnvGenerateResultList = new ArrayList<JSONObject>();
					String envUploadPath = "";
					if (!programName.contains("5G")) {
						// ENV Generation
						// JSONObject fileEnvGenerateResult = new JSONObject();
						// List<JSONObject> fileEnvGenerateResultList = new ArrayList<JSONObject>();
						// String envUploadPath = "";
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_ENV
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replaceAll(" ", "_"));
						logger.error(" Checking For ENV");
						File fileExists = new File(uploadPath.toString());
						if (!fileExists.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						
						//reports.setUserName(user.getUserName());
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
							// fileGenerateEnvStatus =
							// objGenerateCsvService.commissionScriptFileGeneration(ciqFileName, enbId,
							// enbName, dbcollectionFileName, programId, uploadPath.toString(),
							// sessionId,Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4, neMappingEntity,
							// remarks);
							fileEnvGenerateResult.put("status", true);
							fileEnvGenerateResultList.add(fileEnvGenerateResult);
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
							System.out.println("Function to call env single enodeb 1");
							fileEnvGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env", neMappingEntity,
									remarks, supportCA);
							fileEnvGenerateResultList.add(fileEnvGenerateResult);
							scheduleMap.put("EnvFileName", fileEnvGenerateResult.get("fileName").toString());
							scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
						
							reportsMap.put("env",fileEnvGenerateResult);
							//dummy env file
							if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
								fileEnvGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
										neMappingEntity, remarks, supportCA);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
								reportsMap.put("envDummy",fileEnvGenerateResult);
							}	
							envFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							System.out.println("Function to call env single enodeb  2");
							fileEnvGenerateResult = objGenerateCsvService.envFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId,"env", neMappingEntity,
									remarks, supportCA);
							fileEnvGenerateResultList.add(fileEnvGenerateResult);
							envFileExist = true;
							
							if(LoadPropertyFiles.getInstance().getProperty("ciqType").equals("NEW")) {
								fileCpriGenerateResult = objGenerateCsvService.cpriFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										neMappingEntity, remarks);
								fileEnvGenerateResultList.add(fileCpriGenerateResult);
								}
							scheduleMap.put("EnvFileName", fileEnvGenerateResult.get("fileName").toString());
							reportsMap.put("env",fileEnvGenerateResult);
							//dummy env file
							if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
								fileEnvGenerateResult = objGenerateCsvService.envFileGenerationDummyIP(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,"envDummy",
										neMappingEntity, remarks, supportCA);
								fileEnvGenerateResultList.add(fileEnvGenerateResult);
								scheduleMap.put("DummyEnvFileName", fileEnvGenerateResult.get("fileName").toString());
								String filemane = fileEnvGenerateResult.get("fileName").toString();
								logger.error("this is the dummy env file name " +filemane);
								reportsMap.put("envDummy",fileEnvGenerateResult);
							}															
							//
							scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							
							logger.error("env generation done. Moving on...");
						} 
						else {
							fileEnvGenerateResult.put("status", true);
							fileEnvGenerateResultList.add(fileEnvGenerateResult);
							fileEnvGenerateResultList.add(fileCpriGenerateResult);
							
						}
						envUploadPath = uploadPath.toString();
					} else {
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								.toString();

						if (version.contains("19")) {
							version = "19A";
						}
						if (version.contains("20.A")) {
							version = "20A";
						}
						if (version.contains("20.B")) {
							version = "20B";
						}
						if (version.contains("20.C")) {
							version = "20C";
						}
						if (version.contains("21.A")) {
							version = "21A";
						} else if(version.contains("21.B")) {
							version = "21B";
						} else if(version.contains("21.C")) {
							version = "21C";
						}else if(version.contains("21.D")) {
							version = "21D";
						}else if(version.contains("22.A")) {
							version = "22A";
						}else if(version.contains("22.C")) {
							version = "22C";
						}
						
						
						
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_ENV_5G
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
						File fileExists1 = new File(uploadPath.toString());
						if (!fileExists1.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						
						if (version.equalsIgnoreCase("19A")) {
							fileEnvGenerateResult = objGenerateCsvService.envFileGenerationFor5G(ciqFileName, version,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									remarks);
							fileEnvGenerateResultList.add(fileEnvGenerateResult);
							envFileExist = true;
						}
						// bbs
						// String vers = "20A";
						String vers = version;
						
						fileEnvGenerateResult = objGenerateCsvService.envFileGenerationFor5G28ghz(ciqFileName, vers,
								enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
								remarks, programName);
						fileEnvGenerateResultList.add(fileEnvGenerateResult);
						

						envFileExist = true;

						envUploadPath = uploadPath.toString();
					}
					
					scheduleMap.put("EnvFileName", fileEnvGenerateResult.get("fileName").toString());
					scheduleMap.put("EnvFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
				
					reportsMap.put("ENV",fileEnvGenerateResult);
					// CSV Generation
					boolean check=true;
					JSONObject fileCsvGenerateResult = new JSONObject();
					JSONObject mergeFile = new JSONObject();
					List<JSONObject> fileCsvGenerateResultList = new ArrayList<JSONObject>();
					String csvUploadPath = "";
					if (!programName.contains("5G")) {
						// JSONObject fileCsvGenerateResult = new JSONObject();
						// JSONObject mergeFile = new JSONObject();
						// List<JSONObject> fileCsvGenerateResultList = new ArrayList<JSONObject>();
						// String csvUploadPath = "";
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_CSV
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replaceAll(" ", "_"));
						File fileExists2 = new File(uploadPath.toString());
						if (!fileExists2.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						//String dummy_IP = null;	
						//fetching dummy IP
						if(ciqDetails.containsKey("integrationType")) {
							dummy_IP = ciqDetails.get("integrationType").toString();
						} else {
							dummy_IP = "Legacy IP";
						}
						StringBuffer ptpCheck = new StringBuffer();
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
							ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
							if (!ptpCheck.toString().isEmpty()) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", ptpCheck.toString()+" For Selected Enb is not present in the Ciq.");
								return mapObject;
							}
							fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "enb_cell",
									neMappingEntity, remarks, supportCA, dummy_IP);
							fileCsvGenerateResultList.add(fileCsvGenerateResult);
							scheduleMap.put("growFileName", fileCsvGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("enb", fileCsvGenerateResult);
							csvPath = uploadPath.toString();
							csvFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							ptpCheck=objGenerateCsvService.checkEmptyValuesForPTP(dbcollectionFileName, enbId);
							if (!ptpCheck.toString().isEmpty()) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",ptpCheck.toString()+ " For Selected Enb is not present in the Ciq");
								return mapObject;
							}
							fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "enb",
									neMappingEntity, remarks, supportCA, dummy_IP);
							fileCsvGenerateResultList.add(fileCsvGenerateResult);
							logger.error(GenerateCsvServiceImpl.getIpPresent()+"**GenerateCsvServiceImpl.ip_present-enb******"+enbId);
							if(!GenerateCsvServiceImpl.getIpPresent()) {
								 mapObject.put("status", Constants.FAIL);
	                             mapObject.put("reason", "File cannot be generated. Pseudo IP is not present in CIQ" );
	                             return mapObject;
                            }
							
							csvPath = uploadPath.toString();
							scheduleMap.put("growFileName", fileCsvGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("enb", fileCsvGenerateResult);
							csvFileExist = true;
							
							//commented as requested by client supriya
							if((neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.A.0") 
									|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")
									|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0")
									|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0")
									|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")
									|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0")) 
									&& LoadPropertyFiles.getInstance().getProperty("ciqType").equals("NEW")) {
								logger.error("csvFileGeneration-checkEnbExistence******"+enbId);
								List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
										enbName, dbcollectionFileName);
								boolean check2 = objGenerateCsvService.checkEnbExistence(listCIQDetailsModel,Constants.VZ_GROW_CIQUpstateNY);
								logger.error("csvFileGeneration-check2******"+check2);
								if(!check2) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", "Selected Enb is not present in the Ciq");
									return mapObject;
								}
								check= objGenerateCsvService.checkEmptyValues(listCIQDetailsModel,Constants.VZ_GROW_CIQUpstateNY);
								logger.error("csvFileGeneration-check******"+check);
								if(!check) {
									logger.error("**checkEmptyValues false******");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason",
											"Failed to Generate Cell Grow Template");
									return mapObject;
								}
								if(check) {
									logger.error("genearetcsvctrl-check********");
								fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "cell",
									neMappingEntity, remarks, supportCA, dummy_IP);
								scheduleMap.put("growCellFileName", fileCsvGenerateResult.get("fileName").toString());
							reportsMap.put("cell", fileCsvGenerateResult);
							fileCsvGenerateResultList.add(fileCsvGenerateResult);
							String AdState = "Not New";
							if(fileCsvGenerateResult.get("AdNew")!=null) {
								 AdState = fileCsvGenerateResult.get("AdNew").toString();
							}
							
							//commented as requested by client
							if(fileCsvGenerateResult.containsKey("stat") && fileCsvGenerateResult.get("stat").equals("fail")) {
								logger.error("Grow is failing");
								System.out.println("-------C---------");
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", "Failed to generate Cell Grow Template");
								return mapObject;
							}
							logger.error("call csvgeneration2222*****"+enbId);
							mergeFile = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName, programId);
							fileCsvGenerateResultList.add(mergeFile);
							scheduleMap.put("growMergeFileName", fileCsvGenerateResult.get("fileName").toString());
							reportsMap.put("merge", mergeFile);
							csvPath = uploadPath.toString();
							csvFileExist = true;
							if(supportCA.equals(true) && AdState.equals("New")) { 
								JSONObject result = createExecutableFilesCA(ciqFileName, Integer.valueOf(programId),
										uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RF", "20A", programName);

								if (result != null && result.containsKey("status")
										&& result.get("status").equals(Constants.SUCCESS)) {
									mapObject.put("status", Constants.SUCCESS);
									mapObject.put("reason", "CA Scripts Generated Successfully");
									
									commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
											Constants.EVENT_PRE_MIGRATION_RAN_CONFIG, Constants.ACTION_GENERATE,
											"Files Generated Successfully For: " + enbId, sessionId);
								}
								else if (result != null && result.containsKey("status")
										&& (result.get("status").equals(Constants.FAIL) && result.get("reason").equals(" No CA Scripts fileName between the range")) ){
									mapObject.put("status", Constants.SUCCESS);
									mapObject.put("reason1", "CA scripts not found");
									
									commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
											Constants.EVENT_PRE_MIGRATION_RAN_CONFIG, Constants.ACTION_GENERATE,
											"Files Generated Successfully For: " + enbId, sessionId);
								}
								
								else if (result != null && result.containsKey("status")
										&& result.get("status").equals(Constants.FAIL)) {
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", result.get("reason"));
									
									
									
									
									mapObject.put("reason",result.get("reason").toString());
									mapObject.put("enbId",enbId);
									return mapObject;
									//continue;
								}
								}
								}
							}
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
										.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
							logger.error("csvFileGeneration-NE_CONFIG_TYPE_New_Site******"+enbId);
							fileCsvGenerateResult = objGenerateCsvService.csvFileGeneration(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "enb_cell",
									neMappingEntity, remarks, supportCA, dummy_IP);
							fileCsvGenerateResultList.add(fileCsvGenerateResult);
							csvPath = uploadPath.toString();
							csvFileExist = true;
							scheduleMap.put("growFileName", fileCsvGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("enb", fileCsvGenerateResult);
						} else {
							fileCsvGenerateResult.put("status", true);
							fileCsvGenerateResultList.add(fileCsvGenerateResult);
							scheduleMap.put("growFileName", fileCsvGenerateResult.get("fileName").toString());
							scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							reportsMap.put("enb", fileCsvGenerateResult);
						}
						csvUploadPath = uploadPath.toString();
					} else {
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								.toString();
						if (version.contains("20.A")) {
							version = "20A";

						}
						if (version.contains("20.B")) {
							version = "20B";

						}
						if (version.contains("20.C")) {
							version = "20C";

						}
						if (version.contains("21.A")) {
							version = "21A";
						} else if(version.contains("21.B")) {
							version = "21B";
						} else if(version.contains("21.C")) {
							version = "21C";
						}else if(version.contains("21.D")) {
							version = "21D";
						} else if(version.contains("22.A")) {
							version = "22A";
						}else if(version.contains("22.C")) {
							version = "22C";
						} 
						//
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_TEMPLATE
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

						File fileExists3 = new File(uploadPath.toString());
						if (!fileExists3.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){

							String releaseVer = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();

							fileCsvGenerateResult = objGenerateCsvService.generateTemplates(ciqFileName, version, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, remarks,
									releaseVer, neMappingEntity);

							for (int i = 0; i < ((ArrayList<String>) fileCsvGenerateResult.get("fileName"))
									.size(); i++) {
								JSONObject temp = new JSONObject();
								temp.put("status", fileCsvGenerateResult.get("status"));
								temp.put("fileName",
										((ArrayList<String>) fileCsvGenerateResult.get("fileName")).get(i));
								fileCsvGenerateResultList.add(temp);
								csvFileExist = true;
								if(temp.get("fileName").toString().contains("AU_CaCell")) {
									scheduleMap.put("cellGrowFileName", temp.get("fileName").toString());
									reportsMap.put("CELL",temp);
								} else if(temp.get("fileName").toString().contains("AU_")) {
									scheduleMap.put("AuGrowFileName", temp.get("fileName").toString());
									reportsMap.put("AU",temp);
								} else if(temp.get("fileName").toString().contains("ACPF_")) {
									scheduleMap.put("AcpfGrowFileName", temp.get("fileName").toString());
									reportsMap.put("ACPF",temp);
								} else if(temp.get("fileName").toString().contains("AUPF_")) {
									scheduleMap.put("AupfGrowFileName", temp.get("fileName").toString());
									reportsMap.put("AUPF",temp);
								} else if(temp.get("fileName").toString().contains("pnp_macro")) {
									scheduleMap.put("Pnp_macroGrowFileName", temp.get("fileName").toString());
									reportsMap.put("pnp_macro",temp);
								}
								scheduleMap.put("growFilePath", uploadPath.toString().substring(uploadPath.toString().lastIndexOf("Customer")));
							}
							createExecutableFilesForNEGrow(ciqFileName, programId, uploadPath.toString(),
									neMappingEntity, enbId, enbName, sessionId, "single",programName, fileCsvGenerateResultList);

						}
						csvUploadPath = uploadPath.toString();

					}
					int flag=0;
					if(programName.equals("VZN-4G-USM-LIVE")) {
					StringBuilder scriptUploadPath = new StringBuilder();
					StringBuilder uploadPaths = new StringBuilder();
					uploadPaths.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR);
					scriptUploadPath.append(uploadPaths)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));
					scriptUploadPath.append(enbId);
					File fp= new File(scriptUploadPath.toString());
					if(!fp.exists() || !fp.isDirectory()) {
						//flag=1;
					}
					}
					// COMM SCRIPT Generation
					logger.error("Let's get started with COMM SCRIPT generation");
					JSONObject fileCommGenerateResult = new JSONObject();
					List<JSONObject> fileCommGenerateResultList = new ArrayList<JSONObject>();
					String commUploadPath = "";
					if (!programName.contains("5G")) {
if( flag==0){
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replaceAll(" ", "_"));
						File fileExists4 = new File(uploadPath.toString());
						if (!fileExists4.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						commUploadPath = uploadPath.toString();
											
						//reports.setUserName(user.getUserName());
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script))) {
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							commFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)) {
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT1, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT2, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT3, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT5, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT6, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT7, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT8, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT9, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT10, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT11, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							commFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType()) && neMappingEntity
										.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_MME, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_ENV, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_COMM, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							commFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							
							
								
							
							commFileExist = true;
						} else if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
							fileCommGenerateResult = objGenerateCsvService.commissionScriptFileGeneration(ciqFileName,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									Constants.GROW_COMM_SCRIPT_FILE_TYPE_VBS, neMappingEntity, remarks);
							fileCommGenerateResultList.add(fileCommGenerateResult);
							commFileExist = true;
						} else {
							fileCommGenerateResult.put("status", true);
							fileCommGenerateResultList.add(fileCommGenerateResult);
						}

						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							JSONObject fileProcessResult = createExecutableFiles(ciqFileName, programId,
									uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId);
							JSONObject fileProcessResult1 = createExecutableFilesForNEGrow(ciqFileName, programId,
									csvPath, neMappingEntity, enbId, enbName, sessionId, "single",programName, fileCsvGenerateResultList);

							if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason", fileProcessResult.get("reason"));
								return mapObject;
							}
						}
						reportsMap.put("COMM", fileCommGenerateResult);
}
					} else {
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								.toString();
						if (version.contains("19")) {
							version = "19A";
						}
						if (version.contains("20.A")) {
							version = "20A";
						}
						if (version.contains("20.B")) {
							version = "20B";
						}
						
						if (version.contains("20.C")) {
							version = "20C";
						}
						if(version.contains("21.A")) {
							version = "21A";
						} else if(version.contains("21.B")) {
							version = "21B";
						} else if(version.contains("21.C")) {
							version = "21C";
						}else if(version.contains("21.D")) {
							version = "21D";
						} else if(version.contains("22.A")) {
							version = "22A";
						}else if(version.contains("22.C")) {
							version = "22C";
						} 
						// for neglecting the commision for 21A
						
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);

						uploadPath.append(Constants.PRE_MIGRATION_Commission
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

						String dateString = "_"
								+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));

						StringBuilder tempFolder = new StringBuilder();
						//reports.setUserName(user.getUserName());
						
						// tempFolder.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);
						//
						// tempFolder.append(Constants.Temp_Folder
						// .replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(),
						// "."))
						// .replace("enbId", enbId).replace("version", version).replace("date",
						// dateString));
						//
						tempFolder.setLength(0);
						tempFolder.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);
						tempFolder.append(Constants.Temp_Folder
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

						File fileExists = new File(uploadPath.toString());
						if (!fileExists.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){
							//String ver = "20A";
							String ver =version;
							if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("22C"))) {
								fileCommGenerateResult = objGenerateCsvService.a1a2ConfigFileGeneration(ciqFileName, ver,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										remarks, tempFolder.toString(), dateString, "RanConfig");
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
								reportsMap.put("A1A2Config", fileCommGenerateResult);
								fileCommGenerateResult = objGenerateCsvService.a1a2CreateFileGeneration(ciqFileName, ver,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										remarks, tempFolder.toString(), dateString, "RanConfig");
								fileCommGenerateResultList.add(fileCommGenerateResult);
								commFileExist = true;
								reportsMap.put("A1A2Create", fileCommGenerateResult);
							}
							
							fileCommGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, tempFolder.toString(), dateString, "RanConfig");
							fileCommGenerateResultList.add(fileCommGenerateResult);
							reportsMap.put("ROUTE", fileCommGenerateResult);
							
							/*if(!version.equals("21A")) {
								fileCommGenerateResult = objGenerateCsvService.gpScriptFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
								
								for (int i = 0; i < ((ArrayList<String>) fileCommGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileCommGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileCommGenerateResult.get("fileName")).get(i));
									fileCommGenerateResultList.add(temp);
								}
							}*/							
							
							List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
									enbName, dbcollectionFileName);
							String marketType="";
							String retValue = "";
							if(listCIQDetailsModels.get(0).getCiqMap().containsKey("MarketType")) {
								marketType=listCIQDetailsModels.get(0).getCiqMap().get("MarketType").getHeaderValue();
							}
							if(listCIQDetailsModels.get(0).getCiqMap().containsKey("AU_RET")) {
								retValue = listCIQDetailsModels.get(0).getCiqMap().get("AU_RET").getHeaderValue();
							}
							if(!marketType.isEmpty() && marketType.equalsIgnoreCase("snap")) {
							fileCommGenerateResult = objGenerateCsvService.offsetFileGeneration(ciqFileName, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, tempFolder.toString(), dateString, "RanConfig");
							fileCommGenerateResultList.add(fileCommGenerateResult);
							if(!retValue.isEmpty()&& !retValue.equals("TBD")) {
								fileCommGenerateResult = objGenerateCsvService.tilt(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
										neMappingEntity, remarks, " ", " ", "RanConfig");
								fileCommGenerateResultList.add(fileCommGenerateResult);
							}
							}
//						    commented for removing specific template generation as client asked
							//fileCommGenerateResult = objGenerateCsvService.additionalTemplete(version, ciqFileName,
							//		enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
							//		"XML", neMappingEntity, remarks, tempFolder.toString(), dateString, "RanConfig");
							//fileCommGenerateResultList.add(fileCommGenerateResult);
							commFileExist = true;
							reportsMap.put("ADD_Temp", fileCommGenerateResult);
							// achor extra
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
							neidau = neidau.replaceAll("^0+(?!$)", "");
							List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
									.getEnbTableDetails(ciqFileName, neidau, enbName, dbcollectionFileName);

							Set<String> neList = new LinkedHashSet<>();
							for (int i = 0; i < listCIQDetailsModell.size(); i++) {
								if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
											.getHeaderValue() != null) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID").getHeaderValue()
												.isEmpty()
												|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue().equals("NA"))
											break;
										else
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue());
									}
								}

							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue().equals("NA")
													|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
															.getHeaderValue().isEmpty())
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue());
										}
									}
								}
							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue() != null)
										neList.add(
												listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue());
								}
							}
							List<JSONObject> objList = new ArrayList<>();
							fileCommGenerateResult = objGenerateCsvService.cslTemplete(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, neList, tempFolder.toString(), dateString, "RanConfig");
							for (int i = 0; i < ((ArrayList<String>) fileCommGenerateResult.get("fileName"))
									.size(); i++) {
								JSONObject temp = new JSONObject();
								temp.put("status", fileCommGenerateResult.get("status"));
								temp.put("fileName",
										((ArrayList<String>) fileCommGenerateResult.get("fileName")).get(i));
								fileCommGenerateResultList.add(temp);
								commFileExist = true;
								objList.add(temp);
							}
							for(JSONObject fileResult : objList) {
								if(fileResult.containsKey("status") && fileResult.get("status").equals(true)){
									reportsMap.put("ANCHOR",fileResult);
								} else {
									reportsMap.put("ANCHOR",fileResult);
									break;
								}
							}
						}
						commUploadPath = uploadPath.toString();

						JSONObject fileProcessResult = createExecutableFiles5G(ciqFileName, programId,
								uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "RANCONFIG",
								version);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", fileProcessResult.get("reason"));
							return mapObject;
						}
						else {
							JSONObject abc = new JSONObject();
							abc.put("status", true);
							fileCommGenerateResultList.add(abc);
						}
					}

					JSONObject fileALLGenerateResult = new JSONObject();
					List<JSONObject> fileALLGenerateResultList = new ArrayList<JSONObject>();
					String allUploadPath = "";
					if (programName.contains("5G")) {
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								.toString();
						if (version.contains("19")) {
							version = "19A";
						}
						if (version.contains("20.A")) {
							version = "20A";
						}
						if (version.contains("20.B")) {
							version = "20B";

						}
						if (version.contains("20.C")) {
							version = "20C";

						}
						if (version.contains("21.A")) {
							version = "21A";
						} else if(version.contains("21.B")) {
							version = "21B";
						} else if(version.contains("21.C")) {
							version = "21C";
						}else if (version.contains("21.D")) {
							version = "21D";
						} else if(version.contains("22.A")) {
							version = "22A";
						} else if(version.contains("22.C")) {
							version = "22C";
						}
						
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_All
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));
						File fileExists = new File(uploadPath.toString());
						if (!fileExists.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}

						if (version.equalsIgnoreCase("19A")) {
							fileALLGenerateResult = objGenerateCsvService.envFileGenerationFor5G(ciqFileName, version,
									enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
									remarks);
							fileALLGenerateResultList.add(fileALLGenerateResult);
							
							allFileExist = true;
						}
						//bbs
						//String vers = "20A";
						String vers =version;
						fileALLGenerateResult = objGenerateCsvService.envFileGenerationFor5G28ghz(ciqFileName, vers,
								enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
								remarks,programName);
						fileALLGenerateResultList.add(fileALLGenerateResult);
						

						allFileExist = true;

						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){

							String releaseVer = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();
							fileALLGenerateResult = objGenerateCsvService.generateTemplates(ciqFileName, version, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, remarks,
									releaseVer, neMappingEntity);
							for (int i = 0; i < ((ArrayList<String>) fileALLGenerateResult.get("fileName"))
									.size(); i++) {
								JSONObject temp = new JSONObject();
								temp.put("status", fileALLGenerateResult.get("status"));
								temp.put("fileName",
										((ArrayList<String>) fileALLGenerateResult.get("fileName")).get(i));
								fileALLGenerateResultList.add(temp);
								allFileExist = true;
							}
							
						}
						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){
						     //bbs
							//String ver = "20A";
							String ver =version;
							if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("22C"))) {
								fileALLGenerateResult = objGenerateCsvService.a1a2ConfigFileGeneration(ciqFileName, ver,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										remarks, " ", " ", " ");
								fileALLGenerateResultList.add(fileALLGenerateResult);
								fileALLGenerateResult = objGenerateCsvService.a1a2CreateFileGeneration(ciqFileName, ver,
										enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										remarks, " ", " ", " ");
								fileALLGenerateResultList.add(fileALLGenerateResult);
							}
							
							fileALLGenerateResult = objGenerateCsvService.routeFileGeneration(ciqFileName, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, " ", " ", " ");
							fileALLGenerateResultList.add(fileALLGenerateResult);
							
							/*if(!version.equals("21A")) {
								fileALLGenerateResult = objGenerateCsvService.gpScriptFileGeneration(ciqFileName, enbId,
										enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
										"XML", neMappingEntity, remarks, " ", " ", "RanConfig");
								
								for (int i = 0; i < ((ArrayList<String>) fileALLGenerateResult.get("fileName"))
										.size(); i++) {
									JSONObject temp = new JSONObject();
									temp.put("status", fileALLGenerateResult.get("status"));
									temp.put("fileName",
											((ArrayList<String>) fileALLGenerateResult.get("fileName")).get(i));
									fileALLGenerateResultList.add(temp);
								}
							}*/
							
							
							List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
									enbName, dbcollectionFileName);
							String marketType="";
							String retValue = "";
							if(listCIQDetailsModels.get(0).getCiqMap().containsKey("MarketType")) {
								marketType=listCIQDetailsModels.get(0).getCiqMap().get("MarketType").getHeaderValue();
							}
							if(listCIQDetailsModels.get(0).getCiqMap().containsKey("AU_RET")) {
								retValue = listCIQDetailsModels.get(0).getCiqMap().get("AU_RET").getHeaderValue();
							}
							if(!marketType.isEmpty() && marketType.equalsIgnoreCase("snap")) {
								fileALLGenerateResult = objGenerateCsvService.offsetFileGeneration(ciqFileName, enbId,
									enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, " ", " ", "RanConfig");
								fileALLGenerateResultList.add(fileALLGenerateResult);
								if(!retValue.isEmpty()&& !retValue.equals("TBD")) {
									fileALLGenerateResult = objGenerateCsvService.tilt(ciqFileName, enbId,
											enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
											neMappingEntity, remarks, " ", " ", "RanConfig");
									fileALLGenerateResultList.add(fileALLGenerateResult);
								}
							}
							//    commented for removing specific template generation as client asked
							//fileALLGenerateResult = objGenerateCsvService.additionalTemplete(version, ciqFileName,
							//		enbId, enbName, dbcollectionFileName, programId, uploadPath.toString(), sessionId,
							//		"XML", neMappingEntity, remarks, "", "", "");
							//fileALLGenerateResultList.add(fileALLGenerateResult);
							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
							String nrfreq = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND").getHeaderValue();
							neidau = neidau.replaceAll("^0+(?!$)", "");
							List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
									.getEnbTableDetails(ciqFileName, neidau, enbName, dbcollectionFileName);

							Set<String> neList = new LinkedHashSet<>();
							for (int i = 0; i < listCIQDetailsModell.size(); i++) {
								if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
											.getHeaderValue() != null) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID").getHeaderValue()
												.isEmpty()
												|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue().equals("NA"))
											break;
										else
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue());
									}
								}

							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue().equals("NA")
													|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
															.getHeaderValue().isEmpty())
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue());
										}
									}
								}
							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue() != null)
										neList.add(
												listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue());
								}
							}
							fileALLGenerateResult = objGenerateCsvService.cslTemplete(ciqFileName, enbId, enbName,
									dbcollectionFileName, programId, uploadPath.toString(), sessionId, "XML",
									neMappingEntity, remarks, neList, " ", " ", " ");
							for (int i = 0; i < ((ArrayList<String>) fileALLGenerateResult.get("fileName"))
									.size(); i++) {
								JSONObject temp = new JSONObject();
								temp.put("status", fileALLGenerateResult.get("status"));
								temp.put("fileName",
										((ArrayList<String>) fileALLGenerateResult.get("fileName")).get(i));
								fileALLGenerateResultList.add(temp);
								allFileExist = true;
							}
							
							List<JSONObject> objList = new ArrayList<>();
							// neList checking for not generating the gnode which is not having enbs
							if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("22C"))) {
								if (!neList.isEmpty()) {
									String folderName = objGenerateCsvService.folderName(enbId);
									for (String neid : neList) {

										fileALLGenerateResult = objGenerateCsvService.endcTemplate(nrfreq, folderName, neid,
												version, ciqFileName, enbId, enbName, dbcollectionFileName, programId,
												uploadPath.toString(), sessionId, "XML", neMappingEntity, remarks, "");

									}
								}
								if (neList.size() > 0) {
									for (int i = 0; i < ((ArrayList<String>) fileALLGenerateResult.get("fileName"))
											.size(); i++) {
										JSONObject temp = new JSONObject();
										temp.put("status", fileALLGenerateResult.get("status"));
										temp.put("fileName",
												((ArrayList<String>) fileALLGenerateResult.get("fileName")).get(i));
										fileALLGenerateResultList.add(temp);
										allFileExist = true;
										objList.add(temp);
									}
								}
								for(JSONObject fileResult : objList) {
									if(fileResult.containsKey("status") && fileResult.get("status").equals(true)){
										reportsMap.put("ENDC",fileResult);
									} else {
										reportsMap.put("ENDC",fileResult);
										break;
									}
								}
							}
							

						}
						allUploadPath = uploadPath.toString();

					}

					JSONObject fileENDCGenerateResult = new JSONObject();
					List<JSONObject> fileENDCGenerateResultList = new ArrayList<JSONObject>();
					String endcUploadPath = "";
					if (programName.contains("5G")) {
						String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								.toString();
						if (version.contains("19")) {
							version = "19A";
						}
						if (version.contains("20.A")) {
							version = "20A";
						}
						if (version.contains("20.B")) {
							version = "20B";

						}
						uploadPath.setLength(0);
						uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programStringId);
						uploadPath.append(Constants.PRE_MIGRATION_ENDC
								.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
								.replace("enbId", enbId).replace("version", version).replaceAll(" ", "_"));

						File fileExists = new File(uploadPath.toString());
						if (!fileExists.exists()) {
							FileUtil.createDirectory(uploadPath.toString());
						}

						if (CommonUtil.isValidObject(neMappingEntity)
								&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
								&& (neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
										|| neMappingEntity.getSiteConfigType()
												.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
							// if(!CommonUtil.isValidObject(neMappingEntity)){

							List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository
									.getEnbTableDetails(ciqFileName, enbId, enbName, dbcollectionFileName);
							String neidau = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
							String nrfreq = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND").getHeaderValue();
							neidau = neidau.replaceAll("^0+(?!$)", "");
							List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository
									.getEnbTableDetails(ciqFileName, neidau, enbName, dbcollectionFileName);

							Set<String> neList = new LinkedHashSet<>();
							for (int i = 0; i < listCIQDetailsModell.size(); i++) {
								if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Nokia eNB_ID")) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
											.getHeaderValue() != null) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID").getHeaderValue()
												.isEmpty()
												|| listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
														.getHeaderValue().equals("NA"))
											break;
										else
											neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Nokia eNB_ID")
													.getHeaderValue());
									}
								}

							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().containsKey("Samsung eNB_ID")) {
										if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
												.getHeaderValue() != null) {
											if (listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
													.getHeaderValue().equals("NA")
													|| listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
															.getHeaderValue().isEmpty())
												break;
											else
												neList.add(listCIQDetailsModell.get(i).getCiqMap().get("Samsung eNB_ID")
														.getHeaderValue());
										}
									}
								}
							}
							if (neList.size() == 0) {
								for (int i = 0; i < listCIQDetailsModell.size(); i++) {
									if (listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue() != null)
										neList.add(
												listCIQDetailsModell.get(i).getCiqMap().get("eNB_ID").getHeaderValue());
								}
							}
							// neList checking for not generating the gnode which is not having enbs
							if(!(version.equals("21A") || version.equals("21B") || version.equals("21C") || version.equals("22C"))) {
								if (!neList.isEmpty()) {
									File fileExists1 = new File(uploadPath.toString() + "Test");
									if (fileExists1.exists()) {
										FileUtils.cleanDirectory(fileExists1);
									}
									String folderName = objGenerateCsvService.folderName(enbId);
									for (String neid : neList) {

										fileENDCGenerateResult = objGenerateCsvService.endcTemplate(nrfreq, folderName,
												neid, version, ciqFileName, enbId, enbName, dbcollectionFileName, programId,
												uploadPath.toString(), sessionId, "XML", neMappingEntity, remarks,
												"RanConfig");

									}
								} else {
									endcFileExist = false;
								}
								List<JSONObject> objList = new ArrayList<>();
								if (neList.size() > 0) {
									for (int i = 0; i < ((ArrayList<String>) fileENDCGenerateResult.get("fileName"))
											.size(); i++) {
										JSONObject temp = new JSONObject();
										temp.put("status", fileENDCGenerateResult.get("status"));
										temp.put("fileName",
												((ArrayList<String>) fileENDCGenerateResult.get("fileName")).get(i));
										fileENDCGenerateResultList.add(temp);
										endcFileExist = true;
										objList.add(temp);
									}
								}
								for(JSONObject fileResult : objList) {
									if(fileResult.containsKey("status") && fileResult.get("status").equals(true)){
										reportsMap.put("ENDC",fileResult);
									} else {
										reportsMap.put("ENDC",fileResult);
										break;
									}
								}
							}
							

						}
						endcUploadPath = uploadPath.toString();

						JSONObject fileProcessResult = createExecutableFiles5G(ciqFileName, programId,
								uploadPath.toString(), neMappingEntity, enbId, enbName, sessionId, "ENDC", version);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason", fileProcessResult.get("reason"));
							return mapObject;
						}

					}

					///////
					logger.info("generateSingelEnodebFiles in GenerateCsvController fileEnvGenerateResultList : "
							+ fileEnvGenerateResultList.size() + ", fileCsvGenerateResultList: "
							+ fileCsvGenerateResultList.size() + ", fileCommGenerateResultList: "
							+ fileCommGenerateResultList.size());

					for (JSONObject object : fileEnvGenerateResultList) {
						if (object.get("status") != null && (boolean) object.get("status")) {
							fileGenerateEnvStatus = true;
							System.out.println("fileGenerateEnvStatus "+fileGenerateEnvStatus);
						} else if (object.get("status") != null && !(boolean) object.get("status")) {
							fileGenerateEnvStatus = false;
							System.out.println("fileGenerateEnvStatus "+fileGenerateEnvStatus);
							logger.info("generateSingelEnodebFiles in GenerateCsvController failed for file : "
									+ object.get("fileName"));
							break;
						}
					}

					for (JSONObject object : fileCsvGenerateResultList) {
						if (object.get("status") != null && (boolean) object.get("status")) {
							fileGenerateCsvStatus = true;
							System.out.println("fileGenerateCsvStatus "+fileGenerateCsvStatus);
						} else if (object.get("status") != null && !(boolean) object.get("status")) {
							fileGenerateCsvStatus = false;
							System.out.println("fileGenerateCsvStatus "+fileGenerateCsvStatus);
							logger.info("generateSingelEnodebFiles in GenerateCsvController failed for file : "
									+ object.get("fileName"));
							break;
						}
					}

					for (JSONObject object : fileCommGenerateResultList) {
						if (object.get("status") != null && (boolean) object.get("status")) {
							fileCommGenerateStatus = true;
							System.out.println("fileCommGenerateStatus "+fileCommGenerateStatus);
						} else if (object.get("status") != null && !(boolean) object.get("status")) {
							fileCommGenerateStatus = false;
							System.out.println("fileCommGenerateStatus "+fileCommGenerateStatus);
							logger.info("generateSingelEnodebFiles in GenerateCsvController failed for file : "
									+ object.get("fileName"));
							break;
						}
					}

					if (programName.contains("5G")) {
						boolean fileallgenerateStatus = false;
						for (JSONObject object : fileALLGenerateResultList) {
							if (object.get("status") != null && (boolean) object.get("status")) {
								fileallgenerateStatus = true;
							} else if (object.get("status") != null && !(boolean) object.get("status")) {
								fileallgenerateStatus = false;
								logger.info("generateSingelEnodebFiles in GenerateCsvController failed for file : "
										+ object.get("fileName"));
								break;
							}
						}
					}

					if (programName.contains("5G")) {
						boolean fileENDCgenerateStatus = false;
						for (JSONObject object : fileENDCGenerateResultList) {
							if (object.get("status") != null && (boolean) object.get("status")) {
								fileENDCgenerateStatus = true;
							} else if (object.get("status") != null && !(boolean) object.get("status")) {
								fileENDCgenerateStatus = false;
								logger.info("generateSingelEnodebFiles in GenerateCsvController failed for file : "
										+ object.get("fileName"));
								break;
							}
						}
					}

					logger.info("generateSingelEnodebFiles in GenerateCsvController fileGenerateEnvStatus : "
							+ fileGenerateEnvStatus + ", fileGenerateCsvStatus: " + fileGenerateCsvStatus
							+ ", fileCommGenerateStatus: " + fileCommGenerateStatus);
					
					HashMap<String, ArrayList<String>> map= new HashMap<>();
					ArrayList<String> temp1 =new ArrayList<String>();
					
					if (!(fileGenerateEnvStatus && fileGenerateCsvStatus && fileCommGenerateStatus)) {
						objFailedData.put(enbName, "dataFailed");
						if(!fileGenerateEnvStatus) {
							objFailedData.put("EnvStatus", "dataFailed");
						} else if(!fileGenerateCsvStatus) {
							objFailedData.put("CsvStatus", "dataFailed");
						} else if(!fileCommGenerateStatus) {
							objFailedData.put("CommGenerateStatus", "dataFailed");
						}
					}
//					JSONObject resultObj = null;
					if (objFailedData.size() > 0 || (mapObject != null && mapObject.containsKey("status")
							&& mapObject.get("status").equals(Constants.FAIL))) {
						mapObject.put("status", Constants.FAIL);
						if (!fileGenerateEnvStatus) {
							logger.error("afileGenerateEnvStatus**"+fileGenerateEnvStatus);
							mapObject.put("reason", "Failed to generate ENV file");
						} else if (!fileGenerateCsvStatus) {
							logger.error("fileGenerateCsvStatus**"+fileGenerateCsvStatus);
							if (!GenerateCsvServiceImpl.getIpPresent()) {
								mapObject.put("reason", "File cannot be generated. Pseudo IP is not present in CIQ");
							} else {
								mapObject.put("reason", "Failed to generate CSV file");
							}
						} else if (!fileCommGenerateStatus) {
							logger.error("fileCommGenerateStatus**"+fileCommGenerateStatus);
							mapObject.put("reason", "Failed to generate Comm File");
						} /*else {
							mapObject.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.FAILED_TO_GENERATE_CSV_FILES));
						}*/
						
					} else {
						JSONObject resultObj = null;
						if (envFileExist) {
							 resultObj = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_ENV, envUploadPath.toString(),
									ciqFileName, enbName, remarks, programDetailsEntity, fileEnvGenerateResultList,programName, siteName,dummy_IP);
							ArrayList<String> temp =new ArrayList<String>();
							temp=(ArrayList<String>) resultObj.get("ENV");
							reportsMap.put("zipENV",resultObj);
							System.out.println("Under the envFileExist");
							scheduleMap.put("EnvFileName", resultObj.get("zipFileName").toString());
							scheduleMap.put("EnvFilePath", envUploadPath.toString().substring(envUploadPath.toString().lastIndexOf("Customer")));
							map.put("ENV",temp);
							if (resultObj != null && resultObj.containsKey("status")
									&& resultObj.get("status").equals(Constants.FAIL)) {
								System.out.println("------18--------------");
								logger.error("ENV for failure");
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								logger.info(
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
												+ " " + Constants.FILE_TYPE_ENV);
								return mapObject;
							}
						} else {
							FileUtil.deleteFileOrFolder(envUploadPath.toString());
						}

						if (csvFileExist) {
							resultObj = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_CSV, csvUploadPath.toString(),
									ciqFileName, enbName, remarks, programDetailsEntity, fileCsvGenerateResultList,programName,siteName,dummy_IP);
							ArrayList<String> tempx =new ArrayList<String>();
							tempx=(ArrayList<String>) resultObj.get("CSV");
							map.put("CSV",tempx);
							if (resultObj != null && resultObj.containsKey("status")
									&& resultObj.get("status").equals(Constants.FAIL)) {
								System.out.println("--------19-----------");
								logger.error("CSV for failure");
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								logger.info(
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
												+ " " + Constants.FILE_TYPE_CSV);
								return mapObject;
							}
						} else {
							FileUtil.deleteFileOrFolder(csvUploadPath.toString());
						}

						if (commFileExist) {
							resultObj = doZipAndSaveAudit(sessionId, Constants.FILE_TYPE_COMM_SCRIPT,
									commUploadPath.toString(), ciqFileName, enbName, remarks, programDetailsEntity,
									fileCommGenerateResultList,programName,siteName,dummy_IP);
							ArrayList<String> tempy =new ArrayList<String>();
							tempy=(ArrayList<String>) resultObj.get("COMMISSION_SCRIPT");
							map.put("COMMOSION_SCRIPTS",tempy);
							if (resultObj != null && resultObj.containsKey("status")
									&& resultObj.get("status").equals(Constants.FAIL)) {
								System.out.println("-------20--------");
								logger.error("COMM for failure");
								mapObject.put("status", Constants.FAIL);
								mapObject.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE));
								logger.info(
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
												+ " " + Constants.FILE_TYPE_COMM_SCRIPT);
								return mapObject;
							}
						} else {
							FileUtil.deleteFileOrFolder(commUploadPath.toString());
						}

						if (programName.contains("5G")) {
							if (allFileExist) {
								resultObj = doZipAndSaveAudit(sessionId, "ALL", allUploadPath.toString(), ciqFileName,
										enbName, remarks, programDetailsEntity, fileALLGenerateResultList,programName,siteName,dummy_IP);
								if (resultObj != null && resultObj.containsKey("status")
										&& resultObj.get("status").equals(Constants.FAIL)) {
									System.out.println("-----21----------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									logger.info(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE) + " "
											+ Constants.FILE_TYPE_COMM_SCRIPT);
									return mapObject;
								}
							} else {
								FileUtil.deleteFileOrFolder(allUploadPath.toString());
							}

						}

						if (programName.contains("5G")) {
							if (endcFileExist) {
								resultObj = doZipAndSaveAudit(sessionId, "ENDC", endcUploadPath.toString(), ciqFileName,
										enbName, remarks, programDetailsEntity, fileENDCGenerateResultList,programName,siteName,dummy_IP);
								if (resultObj != null && resultObj.containsKey("status")
										&& resultObj.get("status").equals(Constants.FAIL)) {
									System.out.println("---------22---------");
									mapObject.put("status", Constants.FAIL);
									mapObject.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE));
									logger.info(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_GENERATE_FILE) + " " + Constants.FILE_TYPE_ENDC);
									return mapObject;
								}
							} else {
								FileUtil.deleteFileOrFolder(endcUploadPath.toString());
							}

						}
						
						finalResultMap.put(enbId, "Success");
						mapObject.put("CombinedResult", finalResultMap);
						
						mapObject.put("paths4G", map);
						if(check) {
							mapObject.put("status", Constants.SUCCESS);
						mapObject.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILES_GENERATE_SUCCESSFULLY));
						}else {
							System.out.println("-------D---------");
							mapObject.put("status", Constants.FAIL);
							mapObject.put("reason",
									"Failed to Generate Cell Grow Template");
						}
					}

				} // for loop closing
			} catch (Exception e) {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_CSV_FILES));
				logger.error("Exception in GenerateCsvController "
						+ ExceptionUtils.getFullStackTrace(e));
			}

		} catch (Exception e) {
			logger.error("Exception in generateSingelEnodebFiles in GenerateCsvController "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}

	/**
	 * delete duplicate rf Scripts in folder
	 * 
	 * @param rfscriptsFolder
	 * @return
	 */
	void deleteDuplicateScriptsDSS(File rfscriptsFolder) {
		File[] rfFiles = rfscriptsFolder.listFiles();
		ArrayList<String> deleteFiles = new ArrayList<>();
		HashMap<String, HashMap<String, String>> duplicateFile = new HashMap<>();
		for (File rfFile : rfFiles) {
			String[] fname = rfFile.getName().split("[_-]+");
			if (!ObjectUtils.isEmpty(fname) && fname.length > 3) {
				if (duplicateFile.containsKey(fname[2])) {
					if (duplicateFile.get(fname[2]).containsKey(fname[0])) {
						try {
							SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");
							Date d1 = sdformat.parse(fname[fname.length - 2]);
							String[] fname1 = duplicateFile.get(fname[2]).get(fname[0]).split("[_-]+");
							Date d2 = sdformat.parse(fname1[fname1.length - 2]);
							if (d1.compareTo(d2) > 0) {
								deleteFiles.add(duplicateFile.get(fname[2]).get(fname[0]));
								duplicateFile.get(fname[2]).put(fname[0], rfFile.getName());
							} else {
								deleteFiles.add(rfFile.getName());
							}
						} catch (Exception e) {
							logger.error("Exception in deleteDuplicateScriptsDSS in GenerateCsvController "
									+ ExceptionUtils.getFullStackTrace(e));
						}

					} else {
						duplicateFile.get(fname[2]).put(fname[0], rfFile.getName());
					}
				} else {
					HashMap<String, String> seqfile = new HashMap<>();
					seqfile.put(fname[0], rfFile.getName());
					duplicateFile.put(fname[2], seqfile);
				}
			}
		}

		String parent = rfscriptsFolder.getPath();
		for (String deletefile : deleteFiles) {
			FileUtil.deleteFileOrFolder(parent + "/" + deletefile);
		}
	}
	
	
	private boolean inSeq(int seqno, String[] seqList) {
		boolean inSeq = false;
		try {
			for(String seq : seqList) {
				if(seq.contains("-")) {
					String[] seqRange = seq.trim().split("-");
					if(Integer.valueOf(seqRange[0].trim())<=seqno && seqno<=Integer.valueOf(seqRange[1].trim())) {
						return true;
					}
				} else if(Integer.valueOf(seq.trim()) == seqno){
					return true;
				}
			}
		} catch(Exception e) {
			logger.error("Exception in GenerateCsvServiceImpl : inSeq() " + ExceptionUtils.getFullStackTrace(e));
		}
		return inSeq;
	}

	private JSONObject createExecutableFilesForNEGrowCBAND(String ciqFileName, Integer programId, String csvPath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String place, String programName, List<JSONObject> fileGenerateResultList) {
		JSONObject resultMap = new JSONObject();
		try {

			String connectionLocation = Constants.CONNECTION_LOCATION_NE;
			
			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
				String csrSleep = "";
				String bsmSleep = "";

				List<GrowConstantsEntity> objListProgDetails = fileUploadRepository.getGrowConstantsDetails();

				List<GrowConstantsEntity> csrCmds = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_CMD)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toList());

				Map<String, String> csrSleepMap = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

				List<GrowConstantsEntity> bsmCmds = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_CMD)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toList());

				Map<String, String> bsmSleepMap = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

				csrSleep = csrSleepMap.get(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME);
				bsmSleep = bsmSleepMap.get(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME);

				String btsId = neMappingEntity.getBtsId();

				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);

			}

			File generatedCSVFolder = new File(csvPath.toString());
			File[] generatedCSVFiles = generatedCSVFolder.listFiles();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO)) {
				connectionLocation = Constants.CONNECTION_LOCATION_SM;
				String mcmip = "";
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_VLSM_ID
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : neMappingEntity.getNetworkConfigEntity()
							.getNeDetails()) {
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
				neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();
				String configType = neMappingEntity.getSiteConfigType();
				boolean addDefaultCurlRule = false;
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_USM_ID) {
					mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
					addDefaultCurlRule = true;
					configType = "";
				}
				if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
					List<CIQDetailsModel> listCIQDetailsModel = null;
					createBashFromCSVCBand(csvPath, generatedCSVFiles, "CSV", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), listCIQDetailsModel,fileGenerateResultList);

					JSONObject fileProcessResult = new JSONObject();
					 if (programName.contains("5G-CBAND")) {
					fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
							"GROWVDU", connectionLocation, configType, addDefaultCurlRule, csvPath);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
							"GROWVDUPNP", connectionLocation, configType, addDefaultCurlRule, csvPath);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
							"GROWVDUCELL", connectionLocation, configType, addDefaultCurlRule, csvPath);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
					 }

					 if (programName.contains("5G-DSS")) {
						fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
							"GROWDSSPNP", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
								}
								return resultMap;
					}
											
					fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
													"GROWDSSAU", connectionLocation, configType, addDefaultCurlRule, csvPath);
				   if (fileProcessResult != null && fileProcessResult.containsKey("status")
													&& fileProcessResult.get("status").equals(Constants.FAIL)) {
				      resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
					resultMap.put("reason", fileProcessResult.get("reason"));
						}
					return resultMap;
											}
											
					fileProcessResult = runTestController.ConstantScriptCBand(programId, ciqFileName, enbId, sessionId,
												"GROWDSSAUPF", connectionLocation, configType, addDefaultCurlRule, csvPath);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
											&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
											resultMap.put("reason", fileProcessResult.get("reason"));
							}
					return resultMap;
				}
		}
			if (!csvPath.contains("AU")) {
					
					fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
							"GrowCell", connectionLocation, configType, addDefaultCurlRule, csvPath);
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));							}
							return resultMap;
						} 
						 logger.error("grow cell");
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"GrowEnb", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							return resultMap;						}
						
						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
								"pnp", connectionLocation, configType, addDefaultCurlRule, csvPath);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
						}
					}
				} else {
					logger.info("GenerateCsvController.createExecutableFiles() mcmip is not found");
				}
			}

		} catch (

		Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	private JSONObject createBashFromCSVCBand(String filePath, File[] files, String fileType, String mcmip, String enbId,
			String endName, String siteConfigType, List<CIQDetailsModel> listCIQDetailsModel, List<JSONObject> fileGenerateResultList) {
		String pnpFileName1 = "";
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String bashFileName1 = " ";
		String bashFileName2 = " ";
		String bashFileName3 = " ";

		try {
			logger.info("createBashFromCSV() called filePath: " + filePath + ", fileType: " + fileType + ", mcmip: "
					+ mcmip);
			List<String> filenameList = new ArrayList<>();
			for (JSONObject object : fileGenerateResultList) {
				if (object.get("fileName") != null && StringUtils.isNotEmpty((String) object.get("fileName"))) {
					filenameList.add(object.get("fileName").toString());
				}
			}
			if (files != null) {
				for (File file : files) {
					if(!filenameList.contains(file.getName())) {
						continue;
					}
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("csv")) {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						// bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
						// + StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".") + dateString
								+ ".sh";
						String csvFile = file.getName().toString();
						String csvFilePath = null;
						if (csvFile.contains("vDU_Cell")) {
							csvFilePath = filePath + "GrowvDUCELLBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						}else if (csvFile.contains("vDU_Grow")) {
							csvFilePath = filePath + "GrowvDUBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						}
						else if (csvFile.contains("vDU_pnp")) {
							csvFilePath = filePath + "GrowvDUpnpBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						}
						else if(csvFile.contains("pnpGrow")) {
							csvFilePath = filePath + "GrowDSSPNPBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}
						}
							else if (csvFile.contains("vDUCellGrow")) {
								csvFilePath = filePath + "GrowDSSAUBashFile/";
								File dest = new File(csvFilePath);

								if (!dest.exists()) {
									FileUtil.createDirectory(csvFilePath);
								}
							}

							else if (csvFile.contains("vDUGrow")) {
								csvFilePath = filePath + "GrowDSSAUPFBashFile/";
								File dest = new File(csvFilePath);

								if (!dest.exists()) {
									FileUtil.createDirectory(csvFilePath);
								}
							}
						resultMap = CommonUtil.createBashFromCSVCBand(filePath + file.getName(),
								csvFilePath + bashFileName, mcmip, enbId, listCIQDetailsModel, endName);

					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
	private JSONObject createExecutableFilesForNEGrowFSU(String ciqFileName, Integer programId, String csvPath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String place, String programName, List<JSONObject> fileGenerateResultList) {
		JSONObject resultMap = new JSONObject();
		try {

			String connectionLocation = Constants.CONNECTION_LOCATION_NE;
			
			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site)) {
				String csrSleep = "";
				String bsmSleep = "";

				List<GrowConstantsEntity> objListProgDetails = fileUploadRepository.getGrowConstantsDetails();

				List<GrowConstantsEntity> csrCmds = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_CMD)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toList());

				Map<String, String> csrSleepMap = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

				List<GrowConstantsEntity> bsmCmds = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_CMD)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toList());

				Map<String, String> bsmSleepMap = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME)
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

				csrSleep = csrSleepMap.get(Constants.ORAN_SPRINT_CSR_SCRIPT_TIME);
				bsmSleep = bsmSleepMap.get(Constants.ORAN_SPRINT_BSM_SCRIPT_TIME);

				String btsId = neMappingEntity.getBtsId();

				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_CSR_FILE_NAME + ".vbs", csrCmds,
						csrSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.PRECHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);
				CommonUtil.createVbsFromCmds(
						csvPath.toString() + Constants.SEPARATOR + Constants.POSTCHECK_BSM_FILE_NAME + ".vbs", bsmCmds,
						bsmSleep, btsId);

			}

			File generatedCSVFolder = new File(csvPath.toString());
			File[] generatedCSVFiles = generatedCSVFolder.listFiles();

			if (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
					|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO)) {
				connectionLocation = Constants.CONNECTION_LOCATION_SM;
				String mcmip = "";
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_VLSM_ID
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeDetails())) {
					for (NetworkConfigDetailsEntity detailsEntity : neMappingEntity.getNetworkConfigEntity()
							.getNeDetails()) {
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
				neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();
				String configType = neMappingEntity.getSiteConfigType();
				boolean addDefaultCurlRule = false;
				if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())
						&& CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity().getNeTypeEntity())
						&& neMappingEntity.getNetworkConfigEntity().getNeTypeEntity()
								.getId() == Constants.NW_CONFIG_USM_ID) {
					mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
					addDefaultCurlRule = true;
					configType = "";
				}
				if (CommonUtil.isValidObject(mcmip) && (mcmip.length() > 0)) {
					List<CIQDetailsModel> listCIQDetailsModel = null;
					JSONObject fileProcessResult = new JSONObject();
					if(csvPath.contains("CSV")) {
					createBashFromCSVFSU(csvPath, generatedCSVFiles, "CSV", mcmip, enbId, enbName,
							neMappingEntity.getSiteConfigType(), listCIQDetailsModel,fileGenerateResultList);
					createBashFromDegrow(csvPath, "DEGROW", mcmip, enbId, enbName,programName);
					int DeletionDays=0;
					OvAutomationModel ovAutomationModel = customerService.getOvAutomationTemplate();
					if (ovAutomationModel != null) { 
						
						DeletionDays=ovAutomationModel.getDeletionDays();
					}
					for(int i=0;i<=DeletionDays;i++) {
						String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
				        String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
				        String bashFileName = "NeCreationTimeBashFile"+i+"_"+ enbId+ dateString+".sh" ;
				        LocalDate todayDate = LocalDate.now();
				        LocalDate Date1 =todayDate.minusDays(i);
				       String  Date=Date1.toString();
					createBashForYangCommand(csvPath, generatedCSVFiles, "YANG", mcmip, enbId, enbName,programName,Date,bashFileName);
					}
					createBashForPackageInventory(csvPath, generatedCSVFiles, "YANG", mcmip, enbId, enbName,programName,"","");
					//createBashForYangCommand(csvPath, generatedCSVFiles, "YANG", mcmip, enbId, enbName,programName);

					fileProcessResult = runTestController.ConstantScriptFSU(programId, ciqFileName, enbId, sessionId,
							"GROWFSU", connectionLocation, configType, addDefaultCurlRule, csvPath);
					fileProcessResult = runTestController.ConstantScriptFSU(programId, ciqFileName, enbId, sessionId,
							"DeGrow", connectionLocation, configType, addDefaultCurlRule, csvPath);
					fileProcessResult = runTestController.ConstantScriptFSU(programId, ciqFileName, enbId, sessionId,
							"NECREATION", connectionLocation, configType, addDefaultCurlRule, csvPath);
					
					
					
					}
					else {
						createBashFromCSVFSU(csvPath, generatedCSVFiles, "COMM", mcmip, enbId, enbName,
								neMappingEntity.getSiteConfigType(), listCIQDetailsModel,fileGenerateResultList);
					fileProcessResult = runTestController.ConstantScriptFSU(programId, ciqFileName, enbId, sessionId,
							"COMM", connectionLocation, configType, addDefaultCurlRule, csvPath);
					
					}
					if (fileProcessResult != null && fileProcessResult.containsKey("status")
							&& fileProcessResult.get("status").equals(Constants.FAIL)) {
						resultMap.put("status", Constants.FAIL);
						if (fileProcessResult.containsKey("reason")) {
							resultMap.put("reason", fileProcessResult.get("reason"));
						}
						return resultMap;
					}
//					if (!csvPath.contains("AU")) {
//						//commented as requested by client
//						/*fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
//								"GrowCell", connectionLocation, configType, addDefaultCurlRule, csvPath);
//						if (fileProcessResult != null && fileProcessResult.containsKey("status")
//								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
//							resultMap.put("status", Constants.FAIL);
//							if (fileProcessResult.containsKey("reason")) {
//								resultMap.put("reason", fileProcessResult.get("reason"));
//							}
//							return resultMap;
//						}*/
//						fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
//								"GrowEnb", connectionLocation, configType, addDefaultCurlRule, csvPath);
//						if (fileProcessResult != null && fileProcessResult.containsKey("status")
//								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
//							resultMap.put("status", Constants.FAIL);
//							if (fileProcessResult.containsKey("reason")) {
//								resultMap.put("reason", fileProcessResult.get("reason"));
//							}
//							return resultMap;
//						}
//						//commented as requested by client
//						/*fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
//								"pnp", connectionLocation, configType, addDefaultCurlRule, csvPath);
//						if (fileProcessResult != null && fileProcessResult.containsKey("status")
//								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
//							resultMap.put("status", Constants.FAIL);
//							if (fileProcessResult.containsKey("reason")) {
//								resultMap.put("reason", fileProcessResult.get("reason"));
//							}
//							return resultMap;
//						}*/
//					}
				} else {
					logger.info("GenerateCsvController.createExecutableFiles() mcmip is not found");
				}
			}

		} catch (

		Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}
	
	private JSONObject createBashFromCSVFSU(String filePath, File[] files, String fileType, String mcmip, String enbId,
			String endName, String siteConfigType, List<CIQDetailsModel> listCIQDetailsModel, List<JSONObject> fileGenerateResultList) {
		String pnpFileName1 = "";
		JSONObject resultMap = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String bashFileName = " ";
		String bashFileName1 = " ";
		String bashFileName2 = " ";
		String bashFileName3 = " ";

		try {
			logger.info("createBashFromCSV() called filePath: " + filePath + ", fileType: " + fileType + ", mcmip: "
					+ mcmip);
			List<String> filenameList = new ArrayList<>();
			for (JSONObject object : fileGenerateResultList) {
				if (object.get("fileName") != null && StringUtils.isNotEmpty((String) object.get("fileName"))) {
					filenameList.add(object.get("fileName").toString());
				}
			}
			if(fileType.equalsIgnoreCase("CSV")) {
			if (files != null) {
				for (File file : files) {
					if(!filenameList.contains(file.getName())) {
						continue;
					}
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					if (fileExtension.equalsIgnoreCase("csv")) {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						// bashFileName = "BASH_" + fileType + "_" + siteConfigType + "_"
						// + StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						bashFileName = fileType + StringUtils.substringBeforeLast(file.getName(), ".") + dateString
								+ ".sh";
						String csvFile = file.getName().toString();
						String csvFilePath = null;
						if (csvFile.contains("FSU_TEMPLATE")) {
							csvFilePath = filePath + "FSUBashFile/";
							File dest = new File(csvFilePath);

							if (!dest.exists()) {
								FileUtil.createDirectory(csvFilePath);
							}

						}
						resultMap = CommonUtil.createBashFromCSVCBand(filePath + file.getName(),
								csvFilePath + bashFileName, mcmip, enbId, listCIQDetailsModel, endName);

					}
				}
			}}
			else if(fileType.equalsIgnoreCase("COMM")) {
			if (files != null) {
				for (File file : files) {
					String fileExtension = FilenameUtils.getExtension(file.getPath());
					String fileName =FilenameUtils.getName(file.getName());
					if ("COMM" == fileType) {
						dateString = "";
					}
					if (fileExtension.equalsIgnoreCase("xml")) {
						if(fileName.contains("COMM_NTP")) {
							siteConfigType = siteConfigType.replaceAll(" ", "");
							bashFileName = "BASH_" + fileType + "_" + "NTP" + "_"
									+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
							resultMap = CommonUtil.createBashFromXmlFSU(filePath + file.getName(), filePath + bashFileName,
									mcmip, enbId);
						}else {
						siteConfigType = siteConfigType.replaceAll(" ", "");
						bashFileName = "BASH_" + fileType + "_" + "administrative_unlock" + "_"
								+ StringUtils.substringBeforeLast(file.getName(), ".") + dateString + ".sh";
						resultMap = CommonUtil.createBashFromXmlFSU(filePath + file.getName(), filePath + bashFileName,
								mcmip, enbId);
					}
					}
				}
			}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in GenerateCsvController.createBashFromCSV() " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	private JSONObject createExecutableFilesCA(String ciqFileName, Integer programId, String generatedDirpath,
			NeMappingEntity neMappingEntity, String enbId, String enbName, String sessionId, String fileType,
			String version, String programName) {
		JSONObject resultMap = new JSONObject();
		String mcmip = null;
		try {
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			List<CIQDetailsModel> listCIQDetailsModel = null;
			if(programName.contains("5G-DSS")) {
				listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
			} else {

				 listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
						enbName, dbcollectionFileName);
			}
			
			String neid = "";
			if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
				//if (listCIQDetailsModel.get("eNBId")) {
					//neid = listCIQDetailsModel.get(0).get("eNBId").getHeaderValue();
					//neid = neid.replaceAll("^0+(?!$)", "");
					neid=enbId;
					neid = neid.replaceAll("^0+(?!$)", "");
				//}
			}
			String connectionLocation = Constants.CONNECTION_LOCATION_SM;
			logger.info("createExecutableFilesDSS() called programId: " + programId + ", ciqFileName: " + ciqFileName
					+ ", generatedDirpath: " + generatedDirpath + ", SiteConfigType: "
					+ neMappingEntity.getSiteConfigType() + ", enbId: " + enbId + ", enbName: " + enbName);

			StringBuilder CAScriptsPath = new StringBuilder();
			CAScriptsPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(neid).append(Constants.SEPARATOR);
			File CAScriptFolder = new File(CAScriptsPath.toString());

			StringBuilder CAScriptsPathdest = new StringBuilder();
			CAScriptsPathdest.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"))
					.append(enbId).append(Constants.SEPARATOR);
			File CAScriptFolderdest = new File(CAScriptsPathdest.toString());
			
			//For Multidirectory
			StringBuilder multidirCascripts = new StringBuilder(CAScriptsPath);
			StringBuilder multidirCascriptsprev = new StringBuilder(CAScriptsPath);
			boolean multidirbool = false;
			multidirCascripts.append(neid).append(Constants.SEPARATOR);
			File multidirCaScriptFolder = new File(multidirCascripts.toString());
			while(multidirCaScriptFolder.exists()) {
				multidirbool = true;
				multidirCascriptsprev.append(neid).append(Constants.SEPARATOR);
				multidirCascripts.append(neid).append(Constants.SEPARATOR);
				multidirCaScriptFolder = new File(multidirCascripts.toString());
			}
			if(multidirbool) {
				multidirCaScriptFolder = new File(multidirCascriptsprev.toString());
				FileUtils.copyDirectory(multidirCaScriptFolder, CAScriptFolder);
				FileUtil.deleteFileOrFolder(CAScriptsPath.append(neid).append(Constants.SEPARATOR).toString());
			}
			
			if (CAScriptFolder.exists()) {
				//deleteDuplicateScriptsDSS(CAScriptFolder);
				if(!neid.equals(enbId)) {
					if (CAScriptFolderdest.exists()) {
						FileUtil.deleteFileOrFolder(CAScriptFolderdest.getPath());
					}
					FileUtil.renameDir(CAScriptFolder.getPath(), CAScriptFolderdest.getPath());
				}				
			} else if (!CAScriptFolderdest.exists()) {
				logger.info("Exception in GenerateCsvController.createExecutableFiles5GDSS() ");
				resultMap.put("reason", "No RF scripts found for NE ID: " + neid);
				resultMap.put("status", Constants.FAIL);
				return resultMap;
			}

			File[] CaFiles = CAScriptFolderdest.listFiles();

			File generatedCommScriptFolder = new File(generatedDirpath.toString());
			File[] generatedCommScriptFiles = generatedCommScriptFolder.listFiles();

			
			String configType = null;
			boolean addDefaultCurlRule = false;

			mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();
			addDefaultCurlRule = true;
			configType = "";
			// }
			List<CIQDetailsModel> listCIQsheetDetailsModel = null;
			resultMap =createBashFromXml4GCA(CAScriptsPathdest.toString(), CaFiles, "CA", mcmip, enbId, enbName,
					neMappingEntity.getSiteConfigType(), programId, ciqFileName, listCIQDetailsModel,
					listCIQsheetDetailsModel);
			if(resultMap.containsKey("status") && resultMap.get("status").equals(Constants.FAIL)) {
				return resultMap;
			}
			JSONObject fileProcessResult = new JSONObject();
			// System.out.println(finalMap.get("timeStamp").toString());
			//fileProcessResult = runTestController.ConstantScript5GDSS(programId, ciqFileName, enbId, sessionId,
				//	"CA", connectionLocation, configType, addDefaultCurlRule, null, version);
			fileProcessResult = runTestController.ConstantScript(programId, ciqFileName, enbId, sessionId,
					"CA", connectionLocation, configType, addDefaultCurlRule,"nothing");
			
			if (fileProcessResult != null && fileProcessResult.containsKey("status")
					&& fileProcessResult.get("status").equals(Constants.FAIL)) {
				resultMap.put("status", Constants.FAIL);
				if (fileProcessResult.containsKey("reason")) {
					resultMap.put("reason", fileProcessResult.get("reason"));
				}
				return resultMap;
			}
		}

		catch (Exception e) {
			logger.error("Exception in GenerateCsvController.createExecutableFiles() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", e.getMessage());
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}
@SuppressWarnings({ "unchecked" })
	@PostMapping(value ="/uploadScript")
	public JSONObject UploadScript(@RequestPart(required = true, value = "file") MultipartFile UPLOADFile,
			@RequestParam("uploadScriptDetails") String uploadScriptDetails) {
		JSONObject result = null;
		JSONObject expiryDetails = null;
		NetworkConfigEntity neEntity = null;
		NeVersionEntity neVersionEntity = null;
		JSONObject obj=null;
		try {
			obj = (JSONObject) new JSONParser().parse(uploadScriptDetails);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject mapObject = new JSONObject();
		StringBuilder uploadPath = new StringBuilder();
		StringBuilder uploadDBPath = new StringBuilder();
		try {
			
			String sessionId=obj.get("sessionId").toString();
			String serviceToken=obj.get("serviceToken").toString();
			String scriptName=obj.get("scriptName").toString();
			String programName=obj.get("programName").toString();
			String neName=obj.get("neName").toString();
			String enbName=obj.get("neName").toString();
			String[] arrOfstring=enbName.split("_");
			String enbId=arrOfstring[0];
			enbId = enbId.replaceAll("^0+(?!$)", "");
			 String mcmip="";
			//String programId ="34";
			 int programId = Integer.parseInt(obj.get("programId").toString());
			//int programId = (Integer) obj.get("programId");
			String programId1=obj.get("programId").toString();
			String ciqFileName=obj.get("ciqName").toString();
			// check if session expired
//			
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String fileExtension1 = FilenameUtils.getExtension(UPLOADFile.getOriginalFilename());
			String fileName = StringUtils.substringBeforeLast(UPLOADFile.getOriginalFilename(), ".");
			String[] filenameSplit = fileName.split("[\\-\\_]");
			if (fileExtension1.equalsIgnoreCase("xml")) {
				Pattern pat = Pattern.compile("(^\\d+)[\\-\\_](\\d+)[\\-\\_](vDU|eNB|ACPF|AUPF|IAU|FSU)[\\-\\_]", Pattern.CASE_INSENSITIVE);
				Matcher matach = pat.matcher(fileName);
				if (!matach.find()) {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason", "RF Scripts File Name Invalid :"+"("+fileName+")");
					mapObject.put("sessionId", sessionId);
					mapObject.put("serviceToken", serviceToken);
					return mapObject;
					
				}
				

			}else {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Failed to Upload Script,the file should be .xml");
				mapObject.put("sessionId", sessionId);
				mapObject.put("serviceToken", serviceToken);
				return mapObject;
			}
			NeMappingModel neMappingModel = new NeMappingModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			//getting nemapping
			programDetailsEntity.setId(programId);
			neMappingModel.setProgramDetailsEntity(programDetailsEntity);
			neMappingModel.setEnbId(enbId);
			List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
			NeMappingEntity neMappingEntity = neMappingEntities.get(0);
			mcmip = neMappingEntity.getNetworkConfigEntity().getNeIp();//programId1
			//setting the path where to upload the filen
			uploadPath = uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId1).append(Constants.SEPARATOR).append(programName).append(Constants.SEPARATOR).append(scriptName);
			
			String tempPath = uploadPath.toString();
			File folder = new File(uploadPath.toString());
			if (folder.exists()) {
				FileUtil.deleteFileOrFolder(uploadPath.toString());
			}
			
			if (!folder.exists()) {
				FileUtil.createDirectory(uploadPath.toString());
			}
			//check wether the file got uploaded ?
			boolean status=	uploadFile(UPLOADFile, uploadPath.toString() );
			
			if(status ) {
				mapObject.put("status", Constants.SUCCESS);
			}
			String bashFileName = " ";
			String bashFileNametxt = " ";
			String folderName = null;
			bashFileName = scriptName+".sh";
			bashFileNametxt = scriptName+".txt";
			JSONObject resultMap = new JSONObject();
			
			folderName = "RF/";
			uploadPath.append("/");
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			List<CIQDetailsModel> listCIQDetailsModel = null;
			
			List<CIQDetailsModel> listCIQsheetDetailsModel = null;
			if (programName.contains("4G-USM-LIVE")) {
			resultMap = CommonUtil.createBashFromXml(uploadPath+ UPLOADFile.getOriginalFilename(), uploadPath + folderName + bashFileName,
					mcmip, enbId);
			}
			else if(programName.contains("5G-CBAND")) {
				listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId,
						dbcollectionFileName);
				if(filenameSplit[2].contains("eNB") || filenameSplit[2].contains("ACPF") || filenameSplit[2].contains("AUPF")) {
					//NeMappingModel neMappingModel = new NeMappingModel();
					//CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					programDetailsEntity.setId(programId);
					neMappingModel.setProgramDetailsEntity(programDetailsEntity);
					neMappingModel.setEnbId(filenameSplit[1].replaceAll("^0+(?!$)", ""));
					neMappingEntities = neMappingService.getNeMapping(neMappingModel);
					if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
							&& (CommonUtil.isValidObject(neMappingEntities.get(0))
									&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
									&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
						mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
					}
				}
				resultMap = CommonUtil.createBashFromXml5GCBAND(uploadPath+ UPLOADFile.getOriginalFilename(),
						uploadPath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, programId,
						ciqFileName, listCIQDetailsModel, listCIQsheetDetailsModel, filenameSplit[2], filenameSplit[1]);
				
			}else if(programName.contains("5G-MM")) {
			listCIQDetailsModel = objGenerateCsvService.getCIQDetailsModelList(enbId,
						dbcollectionFileName);
				resultMap = CommonUtil.createBashFromXml5G(uploadPath + UPLOADFile.getOriginalFilename(),
						uploadPath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, programId, ciqFileName,
						listCIQDetailsModel);
			}else if(programName.contains("DSS")) {
				listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
				String neid="";
				if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
					if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NEID")) {
						neid = listCIQDetailsModel.get(0).getCiqMap().get("NEID").getHeaderValue();
						neid = neid.replaceAll("^0+(?!$)", "");
					}
				}	
				listCIQsheetDetailsModel = fileUploadRepository.getEnbTableSheetDetailss(ciqFileName, "DSS_MOP_Parameters-1", neid, dbcollectionFileName);
				if((filenameSplit[2].contains("eNB") || filenameSplit[2].contains("FSU")) && listCIQDetailsModel.get(0).getCiqMap().containsKey("4GeNB")) {
					//NeMappingModel neMappingModel = new NeMappingModel();
					//CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					//programDetailsEntity.setProgramName("VZN-4G-USM-LIVE");
					programDetailsEntity.setId(34);
					neMappingModel.setProgramDetailsEntity(programDetailsEntity);
					neMappingModel.setEnbId(listCIQDetailsModel.get(0).getCiqMap().get("4GeNB").getHeaderValue().replaceAll("^0+(?!$)", ""));
					neMappingEntities = neMappingService.getNeMapping(neMappingModel);
				//	System.out.println("neMappingEntities.size()  :"+neMappingEntities.size());
					if ((CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size() > 0) 
							&& (CommonUtil.isValidObject(neMappingEntities.get(0))
									&& CommonUtil.isValidObject(neMappingEntities.get(0).getSiteConfigType())
									&& (neMappingEntities.get(0).getSiteConfigType().length() > 0))) {
						mcmip = neMappingEntities.get(0).getNetworkConfigEntity().getNeIp();
					//	System.out.println("mcmip"+mcmip);
					//	System.out.println(neMappingEntities.get(0).getNetworkConfigEntity().getProgramDetailsEntity().getProgramName());
					}
				}
				resultMap = CommonUtil.createBashFromXml5GDSS(uploadPath+ UPLOADFile.getOriginalFilename(),
						uploadPath + folderName + bashFileName, mcmip, enbId, bashFileName, enbId, programId,
						ciqFileName, listCIQDetailsModel, listCIQsheetDetailsModel, filenameSplit[2],filenameSplit[1]);
			}
			String scriptContent = null;
			String scriptContenttxt = null;
			uploadPath.append(folderName);
			scriptContent = uploadFileService.readContentFromFile(uploadPath.toString(), bashFileName);
			scriptContenttxt = uploadFileService.readContentFromFile(uploadPath.toString(), bashFileNametxt);
			String filePath = "";
			String scriptId = obj.get("scriptId").toString();
			String useCaseId = obj.get("useCaseId").toString();
			
			
			UploadFileEntity uploadFileEntity = uploadFileRepository.getUploadFileEntity(Integer.parseInt(scriptId));
			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepository
					.getUseCaseBuilderEntity(Integer.parseInt(useCaseId));
			if(programName.contains("5G-CBAND")||programName.contains("DSS")||programName.contains("5G-MM")) {
				filePath=LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
						+ uploadFileEntity.getFilePath() + "/" + useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			}else {
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.CUSTOMER + "/"
					+ Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION.replace("programId", programId1)
							.replace("migrationType", "Migration").replace("subType", "PreCheck")
					+ useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/";
			}
			StringBuilder sourceCurlFilename = new StringBuilder();
			sourceCurlFilename.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
			.append(Constants.CUSTOMER).append(Constants.SEPARATOR)
			.append(Constants.DUO_CURL_COMMAND_FILEPATH.replace("programId", programId1)
					.replace("filename", useCaseBuilderEntity.getCiqFileName())
					.replace("enbId", enbId))
			.append(Constants.SEPARATOR).append(useCaseBuilderEntity.getUseCaseName().trim()).append(Constants.SEPARATOR)
			.append(FilenameUtils.removeExtension(uploadFileEntity.getFileName())).append(".txt");
		
			
			boolean ShComplete= uploadFileService.saveViewScript(filePath, uploadFileEntity.getFileName(), scriptContent);
			boolean txtComplete=uploadFileService.saveViewScript(sourceCurlFilename.toString(), "", scriptContenttxt);
			if(ShComplete&&txtComplete) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "Script content Updated Successfully");
				mapObject.put("sessionId", sessionId);
				mapObject.put("serviceToken", serviceToken);
	
			}else
			{
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Failed to save script content");	
				mapObject.put("sessionId", sessionId);
				mapObject.put("serviceToken", serviceToken);
	
			}
			/*if (uploadFileService.saveViewScript(filePath, uploadFileEntity.getFileName(), scriptContent)) {
				mapObject.put("status", Constants.SUCCESS);
				mapObject.put("reason", "Failed to save script content");
			} else {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Failed to save script content");
			}if (uploadFileService.saveViewScript(sourceCurlFilename.toString(), "", scriptContenttxt)) {
				mapObject.put("status", Constants.SUCCESS);
			} else {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason", "Failed to save script content");
			}*/
		} catch (Exception e) {
			logger.info("Exception in load Upload Script : " + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("status", Constants.FAIL);
			//mapObject.put("sessionId", sessionId);
			//mapObject.put("serviceToken", serviceToken);
			mapObject.put("reason", "Failed to Upload Script");
		}
		return mapObject;
	}
	
	public boolean uploadFile(MultipartFile file, String uploadPath) {

		boolean uploadStatus = false;
		try {
			FileUtil.createDirectory(uploadPath);

			try {

				FileUtil.transferMultipartFile(file, uploadPath);

				uploadStatus = true;
			} catch (Exception e) {
				uploadStatus = false;
				logger.error("uploadFile() UploadFileServiceImpl" + ExceptionUtils.getFullStackTrace(e));
				FileUtil.deleteFileOrFolder(uploadPath);
			}

		} catch (Exception e) {
			uploadStatus = false;
			logger.error("Exception  uploadFile() in  UploadFileServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return uploadStatus;
	}

	
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/retryMilestoneUpdatepre")
	public JSONObject retryMilestoneUpdatepre(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
	
		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");
			String enbName=reRunTestParams.get("neName").toString();
			String[] arrOfstring=enbName.split("_");
			String enbId=arrOfstring[0];
			String programName=reRunTestParams.get("programName").toString();
			int programId = Integer.parseInt(reRunTestParams.get("programId").toString());
			GenerateInfoAuditEntity generateInfoAuditEntity = runTestRepository.getGenerateInfoAuditEntityEntity(runTestId);
			
			JSONObject ovUpdateJson=new JSONObject();
			
			ovUpdateJson.put("FileName",generateInfoAuditEntity.getFileName());
			ovUpdateJson.put("filePath",generateInfoAuditEntity.getFilePath());
			ovUpdateJson.put("programName",programName);
			ovUpdateJson.put("programId",programId);
			ovUpdateJson.put("ciqFileName",generateInfoAuditEntity.getCiqFileName());
			ovUpdateJson.put("enbName",enbName);
			ovUpdateJson.put("enbId",enbId);
		//	int premigration_Id=Integer.parseInt(doZipResultObject.get("premigration_Id").toString());
			
			JSONObject ENVuplaodResult =objGenerateCsvService.envUploadToOVFromPremigration(ovUpdateJson, generateInfoAuditEntity)	;
		
			//String[] RetryMilestone=new String[];
			resultMap.put("status", Constants.SUCCESS);
		resultMap.put("sessionId", sessionId);
		resultMap.put("serviceToken", serviceToken);
		resultMap.put("reason", "Retrying to Update the MileStone");
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateMilestone API" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}


}
