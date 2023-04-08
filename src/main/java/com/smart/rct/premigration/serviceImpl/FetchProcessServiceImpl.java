package com.smart.rct.premigration.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.OvReport;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CIQTemplateModel;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.FetchInfoModel;
import com.smart.rct.common.models.FetchOVResponseModel;
import com.smart.rct.common.models.KeyValuesModel;
import com.smart.rct.common.models.MileStonesModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvCiqDetailsModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NetworkConfigService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.common.service.RF_DB;
import com.smart.rct.configuration.DailyOvScheduleConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.controller.UploadCIQController;
import com.smart.rct.premigration.dto.CiqUploadAuditTrailDetailsDto;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.Ip;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.repositoryImpl.GenerateCsvRepositoryImpl;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repository.UserDetailsRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.EmailUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PasswordCrypt;

@Service
public class FetchProcessServiceImpl implements FetchProcessService {

	final static Logger logger = LoggerFactory.getLogger(FetchProcessServiceImpl.class);

	@Autowired
	public FileUploadService fileUploadService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	CustomerService customerService;

	@Autowired
	CiqUploadAuditTrailDetailsDto ciqUploadAuditTrailDetailsDto;

	@Autowired
	NetworkConfigService networkConfigService;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	UploadCIQController uploadCIQController;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	GenerateCsvRepositoryImpl generateCsvRepositoryImpl;

	@Autowired
	FetchProcessRepository fetchProcessRepository;

	@Autowired
	RF_DB rfDb;

	@Autowired
	EmailUtil email;

	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	@Autowired
	UserDetailsRepository userDetailsRepository;

	@Autowired
	DailyOvScheduleConfig dailyOvScheduleConfig;

	@SuppressWarnings("unchecked")
	@Override
	public void fetchExtraction(JSONObject fetchCiqDetails, JSONObject resultMap, String sessionId, String serviceToken,
			FetchDetailsModel fetchDetailsModel) {
		String uniqId = DateUtil.dateToString(new Date(), "yyyyMMdd_HH:mm:ss");
		List<String> marketNamesList = fetchDetailsModel.getMarket();
		ConcurrentHashMap<String, FetchInfoModel> infoDetailsMap = new ConcurrentHashMap<>();
		if (ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList())) {
			fetchDetailsModel.setRfScriptList(new ArrayList<String>());
		}
		CopyOnWriteArrayList<String> scriptsValidationList = new CopyOnWriteArrayList<String>(
				fetchDetailsModel.getRfScriptList());
		CopyOnWriteArrayList<CiqUploadAuditTrailDetEntity> ovAuditList = new CopyOnWriteArrayList<CiqUploadAuditTrailDetEntity>();
		StringBuffer infoBuffer = new StringBuffer();
		StringBuffer infoFailureBuffer = new StringBuffer();
		if (!ObjectUtils.isEmpty(marketNamesList)) {
			ExecutorService executorservice = Executors.newFixedThreadPool(marketNamesList.size());
			CountDownLatch latch = new CountDownLatch(marketNamesList.size());
			for (String marketName : marketNamesList) {
				FetchInfoModel infoDetails = new FetchInfoModel();
				infoDetails.setMarketName(marketName);
				infoDetailsMap.put(marketName, infoDetails);
				executorservice.submit(() -> {
					try {

						String programName;
						Integer scriptNetworkConfigId;
						Integer ciqNetworkConfigId;
						// boolean isAllowDuplicate;
						MultipartFile ciqFile;
						MultipartFile scriptFile;
						String remarks;
						String activate;
						// String scriptFileNames;
						long startTime;
						long ciqFetchTime;
						long rfScriptsFetchTime;
						Integer programId;
						String ciqFileName = null;
						String scriptFileNames = "";
						MultipartFile checkListFile = null;

						boolean isAllowDuplicate = Boolean.valueOf(fetchCiqDetails.get("allowDuplicate").toString());
						programName = fetchCiqDetails.get("programName").toString();
						programId = Integer.parseInt(fetchCiqDetails.get("programId").toString());
						ciqNetworkConfigId = Integer.parseInt(fetchCiqDetails.get("ciqNetworkConfigId").toString());
						scriptNetworkConfigId = Integer
								.parseInt(fetchCiqDetails.get("scriptNetworkConfigId").toString());
						if (!CommonUtil.isValidObject(ciqNetworkConfigId)
								|| !CommonUtil.isValidObject(scriptNetworkConfigId)) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));

							// return resultMap;
						}
						remarks = fetchCiqDetails.get("remarks").toString();
						activate = fetchCiqDetails.get("activate").toString();

						Map<String, Object> objMap = new HashMap<String, Object>();
						NetworkConfigModel networkConfigModel = new NetworkConfigModel();
						networkConfigModel.setId(ciqNetworkConfigId);
						List<NetworkConfigEntity> networkConfigEntities = networkConfigService
								.getNetworkConfigDetails(networkConfigModel);

						StringBuilder filePath = new StringBuilder();
						StringBuilder ciqFilePath = new StringBuilder();
						StringBuilder scriptFilePath = new StringBuilder();
						StringBuilder checklistFilePath = new StringBuilder();

						StringBuilder ciqFileTempPath = new StringBuilder();
						StringBuilder scriptFileTempPath = new StringBuilder();
						StringBuilder checklistFileTempPath = new StringBuilder();

						String ciqFileFetchPath = "";
						String scriptFileFetchPath = "";
						String checkListFileFetchPath = "";

						filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
								.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
								.append(Constants.SEPARATOR);

						JSONObject fileProcessResult = new JSONObject();

						if (networkConfigEntities != null && networkConfigEntities.size() > 0) {
							objMap.put("port", Integer
									.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").toString()));
							ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(programId);
							programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
							programTemplateModel.setLabel(Constants.CIQ_FILE_PATH);
							List<ProgramTemplateEntity> entities = customerService
									.getProgTemplateDetails(programTemplateModel);
							if (CommonUtil.isValidObject(entities) && entities.size() > 0
									&& CommonUtil.isValidObject(entities.get(0).getValue())
									&& entities.get(0).getValue().length() > 0) {
								objMap.put("sourcePath", entities.get(0).getValue());
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.CIQ_FILE_PATH);
								infoDetailsMap.get(marketName)
										.setReason(GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.CIQ_FILE_PATH);
								infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								Thread.currentThread().stop();
								// return resultMap;
							}

							programTemplateModel.setLabel(Constants.CIQ_NAME_TEMPLATE);
							entities = customerService.getProgTemplateDetails(programTemplateModel);
							if (CommonUtil.isValidObject(entities) && entities.size() > 0
									&& CommonUtil.isValidObject(entities.get(0).getValue())
									&& entities.get(0).getValue().length() > 0) {
								objMap.put("fileName", entities.get(0).getValue());
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.CIQ_NAME_TEMPLATE);
								infoDetailsMap.get(marketName)
										.setReason(GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.CIQ_FILE_PATH);
								infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								latch.countDown();
								Thread.currentThread().stop();
								// return resultMap;
							}
							File dir = new File(filePath.toString());
							if (!dir.exists()) {
								FileUtil.createDirectory(filePath.toString());
							}
							objMap.put("destinationPath", filePath.toString());
							startTime = System.currentTimeMillis();
							Map<String, Object> result = fileUploadService.fetchFileFromServer(
									networkConfigEntities.get(0), objMap, marketName, fetchDetailsModel, "CIQ");
							ciqFetchTime = System.currentTimeMillis();
							logger.info("UploadCIQController.fetchPreMigrationFiles() time taken for fetching ciq: "
									+ (ciqFetchTime - startTime) + "ms");

							
							if (CommonUtil.isValidObject(result) && result.containsKey("reason")) {
								if (result.get("reason").equals("No such file")) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_NOT_FOUND));
									infoDetailsMap.get(marketName)
											.setReason(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_NOT_FOUND));
									infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								} else if (result.get("reason").equals("Multiple CIQ Files in the path")) {
									infoDetailsMap.get(marketName).setReason("Multiple CIQ Files in the path");
									infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								}
								else if(result.get("reason").equals("CIQ format is not correct")) {
									infoDetailsMap.get(marketName).setReason("CIQ format is not correct");
									infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								}
								latch.countDown();
								Thread.currentThread().stop();
								// return resultMap;
							} else {
								List<String> fileList = (List<String>) result.get("fileList");
								if (commonUtil.isValidObject(fileList) && fileList.size() > 0) {
									String fileName = fileList.get(0);

									File file = new File(filePath.toString() + "" + fileName);

									ciqFilePath.append(filePath)
											.append(Constants.PRE_MIGRATION_CIQ
													.replace("filename",
															StringUtils.substringBeforeLast(fileName.toString(), "."))
													.replaceAll(" ", "_"));
									scriptFilePath.append(filePath)
											.append(Constants.PRE_MIGRATION_SCRIPT
													.replace("filename",
															StringUtils.substringBeforeLast(fileName.toString(), "."))
													.replaceAll(" ", "_"));
									checklistFilePath.append(filePath)
											.append(Constants.PRE_MIGRATION_CHECKLIST
													.replace("filename",
															StringUtils.substringBeforeLast(fileName.toString(), "."))
													.replaceAll(" ", "_"));

									ciqFileTempPath.append(ciqFilePath.toString()).append(Constants.TEMP);
									scriptFileTempPath.append(scriptFilePath.toString()).append(Constants.TEMP);
									checklistFileTempPath.append(checklistFilePath.toString()).append(Constants.TEMP);

									ciqFileFetchPath = ciqFilePath.toString();
									scriptFileFetchPath = scriptFilePath.toString();
									checkListFileFetchPath = checklistFilePath.toString();

									File ciqDir = new File(ciqFilePath.toString());
									if (ciqDir.exists()) {
										if (!isAllowDuplicate) {
											/*
											 * resultMap = CommonUtil .buildResponseJson(Constants.CONFIRM,
											 * GlobalInitializerListener.faultCodeMap
											 * .get(FaultCodes.CIQ_EXCEL_DUPLICATE), sessionId, serviceToken);
											 */
											// resultMap.put("completedMarkets", completedMarketsList);
											isAllowDuplicate = true;
											// return resultMap;
										}

									}

									if (file.exists()) {
										FileInputStream input = new FileInputStream(file);
										MultipartFile multipartFile = new MockMultipartFile(
												filePath.toString() + "" + fileName, fileName, "text/plain",
												IOUtils.toByteArray(input));
										ciqFile = multipartFile;
										if (CommonUtil.isValidObject(multipartFile) && fileUploadService
												.uploadMultipartFile(ciqFile, ciqFileTempPath.toString())) {
											file.delete();
											ciqFileName = multipartFile.getOriginalFilename();
											fileProcessResult = uploadCIQController.preMigrationFileProcess(ciqFile,
													ciqFilePath, ciqFileTempPath, isAllowDuplicate, "CIQ", programId,
													ciqFileName, programName);
											infoDetailsMap.get(marketName).setCiqName(ciqFileName);
											if (fileProcessResult != null && fileProcessResult.containsKey("status")
													&& fileProcessResult.get("status").equals(Constants.FAIL)) {
												deleteCiqDir(programId, ciqFileName);
												resultMap.put("status", Constants.FAIL);
												infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
												if (fileProcessResult.containsKey("reason")) {
													resultMap.put("reason", fileProcessResult.get("reason"));
													infoDetailsMap.get(marketName)
															.setReason(fileProcessResult.get("reason").toString());

												}
												latch.countDown();
												Thread.currentThread().stop();
												// return resultMap;
											}
										} else {
											deleteCiqDir(programId, ciqFileName);
											resultMap.put("status", Constants.FAIL);
											resultMap.put("reason", GlobalInitializerListener.faultCodeMap
													.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));

											infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
											infoDetailsMap.get(marketName)
													.setReason(GlobalInitializerListener.faultCodeMap
															.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
											latch.countDown();
											Thread.currentThread().stop();
											// return resultMap;
										}
									}
								} else {
									deleteCiqDir(programId, ciqFileName);
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
									infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
									infoDetailsMap.get(marketName).setReason(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
									latch.countDown();
									Thread.currentThread().stop();
									// return resultMap;
								}
							}
						} else {
							deleteCiqDir(programId, ciqFileName);
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));

							infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
							infoDetailsMap.get(marketName).setReason(
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
							latch.countDown();
							Thread.currentThread().stop();
							// return resultMap;
						}

						if (!CommonUtil.isValidObject(ciqFileName)) {
							deleteCiqDir(programId, ciqFileName);
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
							infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
							infoDetailsMap.get(marketName).setReason(
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
							latch.countDown();
							Thread.currentThread().stop();
							// return resultMap;
						}

						networkConfigModel = new NetworkConfigModel();
						networkConfigModel.setId(scriptNetworkConfigId);
						networkConfigEntities = networkConfigService.getNetworkConfigDetails(networkConfigModel);
						if (networkConfigEntities != null && networkConfigEntities.size() > 0) {
							objMap.put("port", Integer
									.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").toString()));
							ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(programId);
							programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
							programTemplateModel.setLabel(Constants.SCRIPT_FILE_PATH);
							List<ProgramTemplateEntity> entities = customerService
									.getProgTemplateDetails(programTemplateModel);
							String todayDateFolderName = DateUtil.dateToString(new Date(), Constants.MM_DD_YY);
							if (CommonUtil.isValidObject(entities) && entities.size() > 0
									&& CommonUtil.isValidObject(entities.get(0).getValue())
									&& entities.get(0).getValue().length() > 0) {
								String sourcePath = entities.get(0).getValue().replaceAll("date", todayDateFolderName);
								objMap.put("sourcePath", sourcePath);
							} else {
								deleteCiqDir(programId, ciqFileName);
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.SCRIPT_FILE_PATH);
								infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								infoDetailsMap.get(marketName)
										.setReason(GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.SCRIPT_FILE_PATH);
								latch.countDown();
								Thread.currentThread().stop();
								// return resultMap;
							}

							programTemplateModel.setLabel(Constants.SCRIPT_NAME_TEMPLATE);
							entities = customerService.getProgTemplateDetails(programTemplateModel);
							if (CommonUtil.isValidObject(entities) && entities.size() > 0
									&& CommonUtil.isValidObject(entities.get(0).getValue())
									&& entities.get(0).getValue().length() > 0) {
								objMap.put("fileName", entities.get(0).getValue());
							} else {
								deleteCiqDir(programId, ciqFileName);
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.SCRIPT_NAME_TEMPLATE);
								infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								infoDetailsMap.get(marketName)
										.setReason(GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
												+ Constants.SCRIPT_NAME_TEMPLATE);
								latch.countDown();
								Thread.currentThread().stop();
								// return resultMap;
							}
							File dir = new File(scriptFileTempPath.toString());
							if (!dir.exists()) {
								FileUtil.createDirectory(scriptFileTempPath.toString());
							}
							objMap.put("destinationPath", scriptFilePath.toString());
							startTime = System.currentTimeMillis();

							if (ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList())) {

								if (isAllowDuplicate)

								{
									CiqUploadAuditTrailDetEntity existCiqUploadAuditTrailDetEntity = fileUploadRepository
											.getCiqAuditBasedONFileNameAndProgram(ciqFileName, programId);
									if (existCiqUploadAuditTrailDetEntity != null && StringUtils
											.isNotEmpty(existCiqUploadAuditTrailDetEntity.getScriptFileName())) {
										scriptFileNames = existCiqUploadAuditTrailDetEntity.getScriptFileName();
										scriptFileNames = scriptFileNames + ",";

									}
								}
							} else {

								Map<String, Object> result = fileUploadService.fetchFileFromServer(
										networkConfigEntities.get(0), objMap, marketName, fetchDetailsModel,
										"RF_SCRIPTS");
								rfScriptsFetchTime = System.currentTimeMillis();
								logger.info(
										"UploadCIQController.fetchPreMigrationFiles() time taken for fetching RF script Files: "
												+ (rfScriptsFetchTime - startTime) + "ms");
								if (CommonUtil.isValidObject(result) && result.containsKey("reason")
										&& result.get("reason").equals("No such file")) {
									// deleteCiqDir(programId, ciqFileName);
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND));
									infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
									infoDetailsMap.get(marketName).setReason(GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND));

									if (isAllowDuplicate)

									{
										CiqUploadAuditTrailDetEntity existCiqUploadAuditTrailDetEntity = fileUploadRepository
												.getCiqAuditBasedONFileNameAndProgram(ciqFileName, programId);
										if (existCiqUploadAuditTrailDetEntity != null && StringUtils
												.isNotEmpty(existCiqUploadAuditTrailDetEntity.getScriptFileName())) {
											scriptFileNames = existCiqUploadAuditTrailDetEntity.getScriptFileName();
											scriptFileNames = scriptFileNames + ",";

										}
									}
									// latch.countDown();
									// Thread.currentThread().stop();
									// return resultMap;
								} else {

									List<String> existingScripts = new LinkedList<>();

									if (isAllowDuplicate)

									{
										CiqUploadAuditTrailDetEntity existCiqUploadAuditTrailDetEntity = fileUploadRepository
												.getCiqAuditBasedONFileNameAndProgram(ciqFileName, programId);
										if (existCiqUploadAuditTrailDetEntity != null && StringUtils
												.isNotEmpty(existCiqUploadAuditTrailDetEntity.getScriptFileName())) {
											existingScripts = new LinkedList(Arrays.asList(
													existCiqUploadAuditTrailDetEntity.getScriptFileName().split(",")));
										}
									}
									List<String> fileList = (List<String>) result.get("fileList");
									if (commonUtil.isValidObject(fileList) && fileList.size() > 0) {
										for (String fileName : fileList) {
											File file = new File(scriptFilePath.toString() + fileName);
											if (file.exists()) {
												scriptFileNames = scriptFileNames + fileName + ",";
												if (existingScripts.contains(fileName)) {
													existingScripts.remove(fileName);
												}
												FileInputStream input = new FileInputStream(file);
												MultipartFile multipartFile = new MockMultipartFile(
														scriptFileTempPath.toString() + "" + fileName, fileName,
														"text/plain", IOUtils.toByteArray(input));
												scriptFile = multipartFile;
												if (CommonUtil.isValidObject(multipartFile)
														&& fileUploadService.uploadMultipartFile(multipartFile,
																scriptFileTempPath.toString())) {

													fileProcessResult = uploadCIQController.preMigrationFileProcess(
															multipartFile, scriptFilePath, scriptFileTempPath,
															isAllowDuplicate, "SCRIPT", programId, ciqFileName,
															programName);
													if (fileProcessResult != null
															&& fileProcessResult.containsKey("status")
															&& fileProcessResult.get("status").equals(Constants.FAIL)
															&& fileProcessResult.get("reason")
																	.equals("File Already Exist")) {
														/*
														 * resultMap = CommonUtil.buildResponseJson(Constants.CONFIRM,
														 * GlobalInitializerListener.faultCodeMap
														 * .get(FaultCodes.SCRIPT_FILE_DUPLICATE), sessionId,
														 * serviceToken); return resultMap;
														 */
														latch.countDown();
													}
													String fileExtension = FilenameUtils
															.getExtension(scriptFilePath.toString());
													List<String> zipExtensions = Arrays.asList("tar.gz", "tgz", "gz",
															"zip", "7z");
													List<String> txtExtensions = Arrays.asList("txt");
													long underScoreCharCount = scriptFile.getOriginalFilename().chars()
															.filter(num -> num == '_').count();
													if (zipExtensions.contains(fileExtension)) {
														String unzipDirPath = scriptFilePath.toString()
																.replace(scriptFile.getOriginalFilename(), "");
														String folderName = "";
														if (fileName.contains("_") && !fileName.contains("ENDC")) {
															folderName = StringUtils.substringBeforeLast(fileName, "_");
															if (CommonUtil.isValidObject(folderName)
																	&& folderName.contains("_")) {
																folderName = StringUtils.substringAfter(folderName,
																		"_");
															} else {
																folderName = StringUtils.substringBefore(folderName,
																		" ");
															}
															if (CommonUtil.isValidObject(folderName)) {
																unzipDirPath = StringUtils.substringBeforeLast(
																		scriptFilePath.toString(), "/")
																		+ Constants.SEPARATOR + folderName;
															}
														} else {
															unzipDirPath = StringUtils
																	.substringBeforeLast(scriptFilePath.toString(), "/")
																	+ Constants.SEPARATOR
																	+ FilenameUtils.removeExtension(
																			scriptFile.getOriginalFilename());
														}
														logger.info("fetchPreMigrationFiles() folderName:" + folderName
																+ ", unzipDirPath" + unzipDirPath);
														File unzipDir = new File(unzipDirPath);
														if (unzipDir.exists()) {
															FileUtil.deleteFileOrFolder(unzipDirPath);
														}
														if (!unzipDir.exists()) {
															FileUtil.createDirectory(unzipDirPath);
														}

														boolean unzipStatus = fileUploadService
																.unzipFile(scriptFilePath.toString(), unzipDirPath);
														if (!unzipStatus) {
															deleteCiqDir(programId, ciqFileName);

															/*
															 * resultMap =
															 * CommonUtil.buildResponseJson(Constants.CONFIRM,
															 * GlobalInitializerListener.faultCodeMap
															 * .get(FaultCodes.FAILED_TO_UNZIP_SCRIPT_FILE), sessionId,
															 * serviceToken);
															 */

															infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
															infoDetailsMap.get(marketName).setReason(
																	GlobalInitializerListener.faultCodeMap.get(
																			FaultCodes.FAILED_TO_UNZIP_SCRIPT_FILE));
															latch.countDown();
															Thread.currentThread().stop();
															// return resultMap;
														}
													} else if (underScoreCharCount >= 3
															&& txtExtensions.contains(fileExtension)) {
														String fileMoveDirPath = scriptFilePath.toString()
																.replace(scriptFile.getOriginalFilename(), "");
														String folderName = "";
														if (scriptFile.getOriginalFilename().contains("_")) {
															folderName = scriptFile.getOriginalFilename().toString()
																	.substring(
																			StringUtils.ordinalIndexOf(scriptFile
																					.getOriginalFilename().toString(),
																					"_", 2) + 1,
																			StringUtils.ordinalIndexOf(scriptFile
																					.getOriginalFilename().toString(),
																					"_", 3));
															if (CommonUtil.isValidObject(folderName)) {
																fileMoveDirPath = StringUtils.substringBeforeLast(
																		scriptFilePath.toString(), "/")
																		+ Constants.SEPARATOR + folderName;
															}
															logger.info("uploadCIQFile() folderName:" + folderName
																	+ ", fileMoveDirPath" + fileMoveDirPath);
															File fileMoveDir = new File(fileMoveDirPath);
															if (!fileMoveDir.exists()) {
																FileUtil.createDirectory(fileMoveDirPath);
															}
															FileUtils.copyFileToDirectory(
																	new File(scriptFilePath.toString()), fileMoveDir);
															// FileUtil.deleteFileOrFolder(scriptFilePath.toString());
														}
													}
												}
											}
											String temp = scriptFilePath.toString().replaceAll(fileName, "");
											scriptFilePath = new StringBuilder();
											scriptFilePath.append(temp);
											String removeExtentionFileName = FilenameUtils.removeExtension(fileName);
											if (!ObjectUtils.isEmpty(scriptsValidationList)
													&& scriptsValidationList.contains(removeExtentionFileName)) {
												scriptsValidationList.remove(removeExtentionFileName);
											}
										}

										if (!ObjectUtils.isEmpty(existingScripts)) {
											scriptFileNames = scriptFileNames + String.join(",", existingScripts) + ",";
										}

									} else {
										// deleteCiqDir(programId, ciqFileName);
										resultMap.put("status", Constants.FAIL);
										resultMap.put("reason", GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND));
										infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
										infoDetailsMap.get(marketName).setReason(GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND));

										if (isAllowDuplicate)

										{
											CiqUploadAuditTrailDetEntity existCiqUploadAuditTrailDetEntity = fileUploadRepository
													.getCiqAuditBasedONFileNameAndProgram(ciqFileName, programId);
											if (existCiqUploadAuditTrailDetEntity != null && StringUtils.isNotEmpty(
													existCiqUploadAuditTrailDetEntity.getScriptFileName())) {
												scriptFileNames = existCiqUploadAuditTrailDetEntity.getScriptFileName();
												scriptFileNames = scriptFileNames + ",";

											}
										}
										// return resultMap;
									}
								}
							}
						} else {
							deleteCiqDir(programId, ciqFileName);
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
							infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
							infoDetailsMap.get(marketName).setReason(
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
							latch.countDown();
							Thread.currentThread().stop();
							// return resultMap;
						}

						if (!CommonUtil.isValidObject(checkListFile)) {
							CiqUploadAuditTrailDetEntity auditTrailDetEntity = fileUploadService
									.getLatestCheckListByProgram(programId);
							if (CommonUtil.isValidObject(auditTrailDetEntity)
									&& CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFileName())
									&& auditTrailDetEntity.getChecklistFileName().length() > 0
									&& CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFilePath())) {

								String checkListPath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
										+ auditTrailDetEntity.getChecklistFilePath()
										+ auditTrailDetEntity.getChecklistFileName();
								File latestCheckListFile = new File(checkListPath);
								if (latestCheckListFile.exists()) {
									FileInputStream input = new FileInputStream(latestCheckListFile);
									MultipartFile multipartFile = new MockMultipartFile(
											checklistFileTempPath.toString() + ""
													+ auditTrailDetEntity.getChecklistFileName(),
											auditTrailDetEntity.getChecklistFileName(), "text/plain",
											IOUtils.toByteArray(input));
									checkListFile = multipartFile;
								}
							}

						}

						if (CommonUtil.isValidObject(checkListFile) && fileUploadService
								.uploadMultipartFile(checkListFile, checklistFileTempPath.toString())) {
							fileProcessResult = uploadCIQController.preMigrationFileProcess(checkListFile,
									checklistFilePath, checklistFileTempPath, isAllowDuplicate, "CHECKLIST", programId,
									ciqFileName, programName);
							if (fileProcessResult != null && fileProcessResult.containsKey("status")
									&& fileProcessResult.get("status").equals(Constants.FAIL)) {
								deleteCiqDir(programId, ciqFileName);
								resultMap.put("status", Constants.FAIL);
								infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
								if (fileProcessResult.containsKey("reason")) {
									resultMap.put("reason", fileProcessResult.get("reason"));
									infoDetailsMap.get(marketName)
											.setReason(fileProcessResult.get("reason").toString());
								}

								latch.countDown();
								Thread.currentThread().stop();
								// return resultMap;
							}
						} else {
							/*
							 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
							 * Constants.FAIL); resultMap.put("reason",
							 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
							 * CHECKLIST_FILE_NOT_FOUND)); return resultMap;
							 */
						}

						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.SUCCESS)) {
							CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
							CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
							customerDetailsModel.setId(programId);
							List<CustomerDetailsEntity> detailsEntities = customerService
									.getCustomerDetailsList(customerDetailsModel);
							if (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
								ciqUploadAuditTrailDetModel.setProgramDetailsEntity(detailsEntities.get(0));
							}

							String ciqFileSavePath = ciqFileFetchPath;
							String scriptFileSavePath = scriptFileFetchPath;
							ciqFileSavePath = ciqFileSavePath
									.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
							scriptFileSavePath = scriptFileSavePath
									.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
							ciqUploadAuditTrailDetModel.setCiqFilePath(ciqFileSavePath);
							ciqUploadAuditTrailDetModel.setScriptFilePath(scriptFileSavePath);
 
							if (CommonUtil.isValidObject(checkListFile)) {
								String checkListFileSavePath = checkListFileFetchPath;
								checkListFileSavePath = checkListFileSavePath
										.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
								ciqUploadAuditTrailDetModel.setChecklistFilePath(checkListFileSavePath);
							} else {
								ciqUploadAuditTrailDetModel.setChecklistFilePath("");
							}
							if (fetchCiqDetails.get("fileSourceType").equals("OV- Force Fetch")
									|| fetchCiqDetails.get("fileSourceType").equals("OV- Auto Fetch")) {
								ciqUploadAuditTrailDetModel.setFileSourceType(fetchCiqDetails.get("fileSourceType").toString());
							}else
								ciqUploadAuditTrailDetModel.setFileSourceType(Constants.FETCH + "_" + uniqId);
							ciqUploadAuditTrailDetModel.setCiqVersion(Constants.CIQ_VERSION_ORIGINAL);
							ciqUploadAuditTrailDetModel.setCiqFileName(ciqFileName);
							if (CommonUtil.isValidObject(scriptFileNames) && scriptFileNames.length() > 0) {
								ciqUploadAuditTrailDetModel
										.setScriptFileName(scriptFileNames.substring(0, scriptFileNames.length() - 1));
							}
							if (CommonUtil.isValidObject(checkListFile)) {
								ciqUploadAuditTrailDetModel.setChecklistFileName(checkListFile.getOriginalFilename());
							} else {
								ciqUploadAuditTrailDetModel.setChecklistFileName("");
							}
							ciqUploadAuditTrailDetModel.setRemarks(remarks);
							CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = ciqUploadAuditTrailDetailsDto
									.getCiqUploadAuditTrailDetEntity(ciqUploadAuditTrailDetModel, sessionId);
							fileUploadService.createCiqAudit(ciqUploadAuditTrailDetEntity);
							ovAuditList.add(ciqUploadAuditTrailDetEntity);
							commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
									Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD, Constants.ACTION_FETCH,
									"PreMigration Files Fetched Successfully For: " + ciqFileName, sessionId);
							if (CommonUtil.isValidObject(activate) && activate.equalsIgnoreCase("true")) {
								commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
										Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD, Constants.ACTION_FETCH,
										"PreMigration Files Fetched are Activated Successfully For: " + ciqFileName,
										sessionId);
							}
							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.CIQ_FILE_FETCHED_SUCCESSFULLY));
							infoDetailsMap.get(marketName).setStatus(Constants.SUCCESS);
							infoDetailsMap.get(marketName).setReason(GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.CIQ_FILE_FETCHED_SUCCESSFULLY));
							if (isAllowDuplicate) {
								infoDetailsMap.get(marketName).setUpdatedWithNewCiq("true");
							}
							latch.countDown();
						} else {
							deleteCiqDir(programId, ciqFileName);
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
								infoDetailsMap.get(marketName).setReason(fileProcessResult.get("reason").toString());

							}

							infoDetailsMap.get(marketName).setStatus(Constants.FAIL);
							latch.countDown();
							Thread.currentThread().stop();
						}

					} catch (Exception e) {
						latch.countDown();
						logger.info("Exception in deleteCiqDir in UploadCIQController "
								+ ExceptionUtils.getFullStackTrace(e));
					}
					// completedMarketsList.add(marketName);
					// return resultMap;
				});
			}
			try {
				latch.await();
				executorservice.shutdown();
				List<String> listReason = new ArrayList<>();
				String failMessage = null;
				listReason.add("Multiple CIQ Files in the path");
				listReason.add("CIQ format is not correct");
				listReason.add("CIQ is encrypted we can not process");
				listReason.add("CIQ file not found");
				AtomicBoolean finalStatus = new AtomicBoolean();
				AtomicBoolean finalFailureStatus = new AtomicBoolean();
				AtomicBoolean failureStatus1 = new AtomicBoolean();
				AtomicBoolean failureStatus2 = new AtomicBoolean();
				Set<String> reasonSet = new HashSet<>();
				if (infoDetailsMap.size() > 0) {
					for (Map.Entry<String, FetchInfoModel> info : infoDetailsMap.entrySet()) {
						if (Constants.FAIL.equalsIgnoreCase(info.getValue().getStatus())) {
							if (StringUtils.isNotEmpty(info.getValue().getCiqName())) {
								infoBuffer.append(info.getValue().getMarketName() + " " + info.getValue().getCiqName()
										+ " " + Constants.ACTION_FETCH + " " + Constants.FAIL);
								infoBuffer.append(" Due to " + info.getValue().getReason());
								infoFailureBuffer.append(info.getValue().getMarketName() + " " + info.getValue().getCiqName()
										+ " " + Constants.ACTION_FETCH + " " + Constants.FAIL);
								infoFailureBuffer.append(" Due to " + info.getValue().getReason());
								if(listReason.contains(info.getValue().getReason())){
									failMessage = info.getValue().getReason();
									reasonSet.add(info.getValue().getReason());
									failureStatus1.getAndSet(true);
								}
								else if(info.getValue().getReason().contains("Columns Not Matched With Program Template") || info.getValue().getReason().contains("Column Not Matched With Program Template")) {
									failMessage = "Columns do not match with the program validate template";
									failureStatus2.getAndSet(true);
								}
								
							} else {
								infoBuffer.append(info.getValue().getMarketName() + " " + Constants.ACTION_FETCH + " "
										+ Constants.FAIL);
								infoBuffer.append(" Due to " + info.getValue().getReason());
								infoFailureBuffer.append(info.getValue().getMarketName() + " " + Constants.ACTION_FETCH + " "
										+ Constants.FAIL);
								infoFailureBuffer.append(" Due to " + info.getValue().getReason());
								
								if(listReason.contains(info.getValue().getReason())){
									failMessage = info.getValue().getReason();
									reasonSet.add(info.getValue().getReason());
									failureStatus1.getAndSet(true);
								}
								else if(info.getValue().getReason().contains("Columns Not Matched With Program Template") || info.getValue().getReason().contains("Column Not Matched With Program Template")) {
									failMessage = "Columns do not match with the program validate template";
									failureStatus2.getAndSet(true);
								}
							}

							infoBuffer.append("\n");
							infoFailureBuffer.append("\n");
							finalFailureStatus.getAndSet(true);

						}

						if ("true".equalsIgnoreCase(info.getValue().getUpdatedWithNewCiq())) {
							infoBuffer.append(info.getValue().getCiqName() + " Updated with New CIQ");
							infoBuffer.append("\n");
						}

						if (Constants.SUCCESS.equalsIgnoreCase(info.getValue().getStatus())) {
							finalStatus.getAndSet(true);
						}
					}

				}

				if (!ObjectUtils.isEmpty(scriptsValidationList)) {
					infoBuffer.append("\n");
					infoBuffer.append(String.join(",", scriptsValidationList) + " Scripts not found");
				}
				if (finalStatus.get() && !finalFailureStatus.get()) {
					if(!ObjectUtils.isEmpty(scriptsValidationList))
					{
						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason","Fetch completed, few CIQ files not fetched. Please check the reason in the info icon.");
					}else {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CIQ_FILE_FETCHED_SUCCESSFULLY));
					}
				}
				else if (finalStatus.get() && finalFailureStatus.get()) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason","Fetch Completed,Few Script Files Not Fetched. Please Check Reason in Info Icon");
					resultMap.put("infoFailureLog", infoFailureBuffer);
				}
				else if (!finalStatus.get() && finalFailureStatus.get()) {
					if(failureStatus1.get()) {
						
						if (reasonSet.size() == 1) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Failed to fetch - " + failMessage);
						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Failed to fetch -  " + " file format is not proper");
						}
					}
					else if(failureStatus2.get()) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason","Failed to fetch: Columns do not match with the program validate template.");
					}
					else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
					}
				}
				
				else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
				}

				fileUploadRepository.updateInfo(Constants.FETCH + "_" + uniqId, infoBuffer.toString());
				/*
				 * resultMap.put("scriptsNotAvailable", scriptsValidationList);
				 * resultMap.put("fetchInfo", infoDetailsMap);
				 */
				resultMap.put("infoFailureLog", infoBuffer.toString());
				resultMap.put("ovAuditList", ovAuditList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// return resultMap;
	}

	private void deleteCiqDir(Integer programId, String ciqFileName) {
		boolean status = false;
		try {
			if (CommonUtil.isValidObject(programId) && CommonUtil.isValidObject(ciqFileName)) {
				String folderName = StringUtils.substringBeforeLast(ciqFileName.toString(), ".").replaceAll(" ", "_");

				StringBuilder fileInputPath = new StringBuilder();
				fileInputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
						.append(Constants.SEPARATOR).append(Constants.PRE_MIGRATION_INPUT);
				fileInputPath.append(folderName);

				logger.info("deleteCiq fileInputPath: " + fileInputPath.toString());
				File fileInputDir = new File(fileInputPath.toString());
				if (fileInputDir.exists()) {
					FileUtils.deleteDirectory(fileInputDir);
				}

				/*
				 * StringBuilder fileOutputPath = new StringBuilder();
				 * fileOutputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"
				 * )) .append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
				 * .append(Constants.SEPARATOR).append(Constants.PRE_MIGRATION_OUTPUT);
				 * fileOutputPath.append(folderName); logger.info("deleteCiq fileOutputPath: " +
				 * fileInputPath.toString()); File fileOutputDir = new
				 * File(fileOutputPath.toString()); if (fileOutputDir.exists()) {
				 * FileUtils.deleteDirectory(fileOutputDir); }
				 */
			}
			status = true;
		} catch (Exception e) {
			logger.info("Exception in deleteCiqDir in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
		}
		// return status;
	}

	public String getAuthencationString() {
		StringBuilder authString = new StringBuilder();
		try {
			OvGeneralEntity objOvGeneralEntityUserName = customerRepository.getOvlabelTemplate(Constants.OV_USERNAME);
			OvGeneralEntity objOvGeneralEntityPassword = customerRepository.getOvlabelTemplate(Constants.OV_PASSWORD);
			authString.append(objOvGeneralEntityUserName.getValue());
			authString.append(":");
			authString.append(objOvGeneralEntityPassword.getValue());
		} catch (Exception e) {
			logger.info("Exception in getAuthencationString in FetchProcessServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return authString.toString();
	}

	@Override
	@SuppressWarnings({ "unused", "unchecked" })
	public JSONObject getOvFetchDetails(String fetchType, String remarks, String programName,String fetchDate) {
		JSONObject objStatus = new JSONObject();
		AtomicInteger statusCount = new AtomicInteger();
		AtomicInteger statusCountt = new AtomicInteger();
		List<OvReport> list = new ArrayList<>();

		try {
			OvInteractionModel ovInteractionModel = customerService.getOvInteractionTemplate();
			OvAutomationModel ovAutomationModel = customerService.getOvAutomationTemplate();
			System.out.println("Inside getOvFetchDetails");
			if (!ObjectUtils.isEmpty(ovInteractionModel) && StringUtils.isNotEmpty(ovInteractionModel.getFetch())) {
				if (Constants.OV_INTRACTION_ON.equalsIgnoreCase(ovInteractionModel.getFetch())) {
					if (ovAutomationModel != null && ovAutomationModel.getFetch().equalsIgnoreCase("ON")&&fetchType.equals("OV- Auto Fetch")
							|| fetchType.equals("OV- Force Fetch")&& ovAutomationModel != null && ovAutomationModel.getFetch().equalsIgnoreCase("OFF")) {
						String authStr = getAuthencationString();
						objStatus.put("fetchIntraction", Constants.OV_INTRACTION_ON);

						List<TrackerDetailsModel> allInfoTrakerList = new ArrayList<>();
						// TrackerId Rest Implementation
						JSONObject trakerIdDetails = getTrakerIdList(authStr,programName,fetchDate,fetchType);
						//dummy IP
						objStatus.put("integrationType", trakerIdDetails.get("integrationType").toString());
						logger.error("integrationType " +trakerIdDetails.get("integrationType").toString());
						logger.error("Tracker details:" + trakerIdDetails);
						if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerList")) {
							logger.error("Inside Tracker details");
							List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
									.get("trakerList");
							logger.error("Tracker detailsList:" + trakerdetails);
							if (!ObjectUtils.isEmpty(trakerdetails)) {
								logger.error("Inside Tracker detailsList");
								for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails) {
									//OvReport ovDetails = null;

//									if (programName.contains("USM-LIVE") && Objects.isNull(ovDetails))
//										ovDetails = fetchProcessRepository.getOvReportsDetails(locTrackerDetailsModel.getTrackerId(),locTrackerDetailsModel.getPrelatedEnbIds());
//									if (programName.contains("MM")&& Objects.isNull(ovDetails))
//										ovDetails = fetchProcessRepository.getOvReportsDetails(locTrackerDetailsModel.getTrackerId(),locTrackerDetailsModel.getPrelatedGnbIds());
//									if (programName.contains("DSS")&& Objects.isNull(ovDetails))
//										ovDetails = fetchProcessRepository.getOvReportsDetails(locTrackerDetailsModel.getTrackerId(),locTrackerDetailsModel.getDssIds());
//									if (programName.contains("CBAND")&& Objects.isNull(ovDetails))
//										ovDetails = fetchProcessRepository.getOvReportsDetails(locTrackerDetailsModel.getTrackerId(),locTrackerDetailsModel.getcBandIds());
//									if (programName.contains("FSU")&& Objects.isNull(ovDetails))
//										ovDetails = fetchProcessRepository.getOvReportsDetails(locTrackerDetailsModel.getTrackerId(),locTrackerDetailsModel.getFsuIds());									
//									
//									if(Objects.isNull(ovDetails))
//										ovDetails  = new OvReport();
									
									JSONObject workPlanDetailsObject = getWorkPlanIdList(authStr,
											locTrackerDetailsModel.getTrackerId(), "1");
									
//									ovDetails.setTrackerid(locTrackerDetailsModel.getTrackerId());
//									ovDetails.setProgramName(programName);
//									ovDetails.setSiteName(locTrackerDetailsModel.getSiteName());
//									if (programName.contains("USM-LIVE"))
//										ovDetails.setEnbId(locTrackerDetailsModel.getPrelatedEnbIds());
//									if (programName.contains("MM"))
//										ovDetails.setEnbId(locTrackerDetailsModel.getPrelatedGnbIds());
//									if (programName.contains("DSS"))
//										ovDetails.setEnbId(locTrackerDetailsModel.getDssIds());
//									if (programName.contains("CBAND"))
//										ovDetails.setEnbId(locTrackerDetailsModel.getcBandIds());
//									if (programName.contains("FSU"))
//										ovDetails.setEnbId(locTrackerDetailsModel.getFsuIds());
									
									//logger.error("workPlanDetailsObject:" + workPlanDetailsObject);
									if (workPlanDetailsObject != null
											&& workPlanDetailsObject.containsKey("workPlanDetails")) {
										logger.error("Inside workPlanDetails");
										statusCount.getAndIncrement();
										TrackerDetailsModel workTrackerDetailsModel = (TrackerDetailsModel) workPlanDetailsObject
												.get("workPlanDetails");

										if (!ObjectUtils.isEmpty(workTrackerDetailsModel)
												&& StringUtils.isNotEmpty(workTrackerDetailsModel.getWorkPlanId())) {
											JSONObject taskPlanDetailsObject = getTasksList(authStr,
													workTrackerDetailsModel.getWorkPlanId(), programName,
													locTrackerDetailsModel.getIntegrationType());
											System.out.println("taskPlanDetailsObject " + taskPlanDetailsObject);
											// ovDetails.setWorkplanid(workTrackerDetailsModel.getWorkPlanId());
											TrackerDetailsModel taskTrackerDetailsModel = (TrackerDetailsModel) taskPlanDetailsObject
													.get("taskDetailsModel");
											//logger.error("taskTrackerDetailsModel:" + taskTrackerDetailsModel);

											if (taskTrackerDetailsModel != null && StringUtils
													.isNotEmpty(taskTrackerDetailsModel.getCommissionDate())) {
												System.out.println("it is calling the 3rd api");
//												ovDetails.setCommissionDate(taskTrackerDetailsModel.getCommissionDate());
//												ovDetails.setCiqValidateFinish(taskTrackerDetailsModel.getCiqValidateFinish());
//												ovDetails.setScriptDevlopement(taskTrackerDetailsModel.getScriptDevlopement());
//												ovDetails.setCommissioningCiq(taskTrackerDetailsModel.getCommissioningCiq());
//												ovDetails.setReason(taskTrackerDetailsModel.getReason());
												logger.error("taskTrackerDetails for Commission details");
												TrackerDetailsModel finalTrackerDetailsModel = new TrackerDetailsModel();
												statusCountt.getAndIncrement();
												finalTrackerDetailsModel = locTrackerDetailsModel;
												finalTrackerDetailsModel
														.setCommissionDate(taskTrackerDetailsModel.getCommissionDate());
												finalTrackerDetailsModel.setStatus(taskTrackerDetailsModel.getStatus());
												System.out.println("finalTrackerDetailsModel "+finalTrackerDetailsModel.getStatus());
												finalTrackerDetailsModel
														.setWorkPlanId(workTrackerDetailsModel.getWorkPlanId());
												finalTrackerDetailsModel.setWorkPlanDetailsJson(
														workTrackerDetailsModel.getWorkPlanDetailsJson());
												finalTrackerDetailsModel.setCommissionDetailsJson(
														taskTrackerDetailsModel.getCommissionDetailsJson());
												allInfoTrakerList.add(finalTrackerDetailsModel);
											} else {
												logger.error("Commission Date for "
														+ locTrackerDetailsModel.getTrackerId() + " is missing");
											}
										} else {
											logger.error("WorkPlanId for " + locTrackerDetailsModel.getTrackerId()
													+ " is missing");
										}
									}
									
									//OvReport result = fetchProcessRepository.saveOvReportsDetails(ovDetails);
									//triger email
									//list.add(result);
								}
								//sendEmail(list,programName,"");
							} else {
								logger.error("No Tracker Details Found");
								//trigger email
								//sendEmail(list,programName,"No Tracker Details Found");
							}
						} else {
							logger.error("Failed to Fetch Tracker Details - No response from OV");
							//trigger email
							//sendEmail(list,programName,"Failed to Fetch Tracker Details - No response from OV");
						}

						if (statusCountt.get() == 0 && trakerIdDetails != null) {
							logger.error("Failed to Fetch Task Details - No response from OV");
							//sendEmail(list,programName,"Failed to Fetch Task Details - No response from OV");
						}
						if (statusCount.get() == 0 && trakerIdDetails != null) {
							logger.error("Failed to Fetch WorkPlan Details - No response from OV");
							//sendEmail(list,programName,"Failed to Fetch WorkPlan Details - No response from OV");
						}
						
						shedulingFetchDetails(allInfoTrakerList, fetchType, remarks, programName);
						objStatus.put("trakerDetails", allInfoTrakerList);
						objStatus.put("statusCode", trakerIdDetails.get("statusCode").toString());
					}else {
						objStatus.put("reason", "SRCT Fetch Automation is OFF");
						//sendEmail(list,programName,"SRCT Fetch Automation is OFF for "+programName);
					}
				} else {
					objStatus.put("fetchIntraction", Constants.OV_INTRACTION_OFF);
					//sendEmail(list,programName,"Fetch for Overall Interaction is OFF for "+programName);
				}
			}
			
		} catch (HttpClientErrorException e) {
			objStatus.put("statusCode", e.getStatusCode());
			objStatus.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.info("Exception in deleteCiqDir in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
		}
		return objStatus;
	}

	/*public void sendEmail(List<OvReport> result,String pgName,String reason) {
		String enbdata = null;
		try {
		CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(pgName);
		ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
		programTemplateModel.setProgramDetailsEntity(programmeEntity);
		programTemplateModel.setConfigType("s&r");
		List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
				.getScheduledDaysDetails(programTemplateModel);
	
		String emailIds = listProgramTemplateEntity.stream()
				.filter(entity -> Constants.MAIL_CONFIGURATION.equalsIgnoreCase(entity.getLabel()))
				.map(entity -> entity.getValue()).findFirst().get();
		
			if (reason.equals("")) {
				enbdata = validateOvData(result, pgName);
			}
					String[] toList = emailIds.split(",");
					StringBuilder bodyText = new StringBuilder();
					if(enbdata!=null) {
						bodyText.append("Hi" + ",");
						bodyText.append("<br/><br/>");
						bodyText.append(enbdata);
						bodyText.append("<br/><br/>");
						bodyText.append("Regards");
						bodyText.append("<br/>");
						bodyText.append("SMART Administrator");
					}
					else {
						bodyText.append("Hi" + ",");
						bodyText.append("<br/><br/>");
						bodyText.append(reason);
						bodyText.append("<br/><br/>");
						bodyText.append("Regards");
						bodyText.append("<br/>");
						bodyText.append("SMART Administrator");

					}
					String subject = "OV Failure Reason";
					//email.sendEmail(toList, null, null, subject, bodyText.toString(), null, null,
						//	true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}*/
	
	/*private String validateOvData(List<OvReport> result, String pgName) {
		
		//String string = null;
		StringBuilder reason = new StringBuilder();
		
		for(OvReport data: result) {
			
			if (data.getEnbId() != null && data.equals(pgName)) {
				if (Objects.isNull(data.getWorkplanid())) {
					reason.append("WorkPlanId for " + data.getEnbId() + " is not present");
					reason.append("\n");
				}
				else if (Objects.isNull(data.getCommissionDate())) {
					reason.append("Commission Date for " + data.getEnbId() + " is not present");
					reason.append("\n");
				}
				else if (data.getReason()!=null) {
					reason.append(data.getEnbId() + " failed due to " + data.getReason());
					reason.append("\n");
				}
				else if (Objects.isNull(data.getCiqValidateFinish())||Objects.isNull(data.getCommissioningCiq())||Objects.isNull(data.getScriptDevlopement())) {
					reason.append("Milestones are not present "+ data.getEnbId());
					reason.append("\n");
				}
				
			}
			
				
		}
		
		return reason.toString();
	}*/

	@SuppressWarnings("unchecked")
	public JSONObject getTrakerIdList(String authStr,String programName,String fetchDate,String fetchType) {
		// TODO Auto-generated method stub
		JSONObject objTrackerIdDetails = new JSONObject();
		String URL = null;
		try {
			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(objOvGeneralEntity.getValue());
			if (fetchType.equals("OV- Auto Fetch")) {
				
					CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(programName);
					ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
					programTemplateModel.setProgramDetailsEntity(programmeEntity);
					programTemplateModel.setConfigType("s&r");
					List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
							.getScheduledDaysDetails(programTemplateModel);
					fetchDate = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.FETCH_DATE.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().get();
				
			}
				if (programName.contains("MM")) {
					// view=L%3ASRCTmmW&filter=L%3ASRCTmmWD0'
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCT_mmW&filter=L:mmW:"
							+ fetchDate.toString();
				}
				if (programName.contains("USM-LIVE")) {
					// view=L%3ASRCT4GLTE&filter=L%3ASRCT4GLTED0
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCT_LTE&filter=L:LTE:"
							+ fetchDate.toString();
				}
				if (programName.contains("DSS")) {
					// view=L%3ASRCTDSS&filter=L%3ASRCTDSSD2'
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCTDSS&filter=L:SRCTDSS"
							+ fetchDate.replace("-", "");
				}
				if (programName.contains("CBAND")) {
					// view=L%3ASRCTCBAND&filter=L%3ASRCTCBANDD5'
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCTCBAND&filter=L:SRCTCBAND"
							+ fetchDate.replace("-", "");
				}
				if (programName.contains("FSU")) {
					// view=L%3ASRCTCBAND&filter=L%3ASRCTCBANDD5'
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCT_FSU&filter=L:FSU:"
							+ fetchDate.toString();
				}
			urlBuilder.append(URL);
			String trakerIdUrl = urlBuilder.toString();
			logger.error("Tracker URL (1st API) : "+trakerIdUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			// create headers
			headers.add("Authorization", "Basic " + base64Creds);
			HttpEntity requestEntity = new HttpEntity<>(headers);
			HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			logger.error("RequestEntity:  from SRCT",requestEntity);
			
			ResponseEntity<String> response = restTemplate.exchange(trakerIdUrl, HttpMethod.GET, requestEntity,
					String.class);
			logger.error("Response :From OV ",response);
			int statusCode = response.getStatusCodeValue();

			if (200 == statusCode) {
				String trackerIdJson = response.getBody();
				List<HashMap<String, Object>> trakerList = new ObjectMapper().readValue(trackerIdJson,
						new TypeReference<List<HashMap<String, Object>>>() {
						});
				logger.error("OV Responce Json :"+ trackerIdJson);
				List<TrackerDetailsModel> listTrackerDetailsModel = new ArrayList<>();

				if (!ObjectUtils.isEmpty(trakerList)) {
					logger.error("Inside Loop :");
					for (HashMap<String, Object> entryData : trakerList) {
						if (entryData.containsKey("TRACKOR_ID")
								&& StringUtils.isNotEmpty(entryData.get("TRACKOR_ID").toString())) {
							TrackerDetailsModel trackerDetailsModel = new TrackerDetailsModel();
							trackerDetailsModel.setTrackerId(entryData.get("TRACKOR_ID").toString());
							trackerDetailsModel
									.setSiteName((Objects.isNull(entryData.get("Candidate.C_CANDIDATE_NAME")) ? ""
											: entryData.get("Candidate.C_CANDIDATE_NAME").toString()));
							// logger.info("Tracked Id :"+ entryData.containsKey("TRACKOR_ID"));
							if (entryData.containsKey("P_MIGRATION_STRATEGY")) {
								System.out.println("P_MIGRATION_STRATEGY:" + entryData.get("P_MIGRATION_STRATEGY"));
								if (null != entryData.get("P_MIGRATION_STRATEGY")) {
									System.out.println("P_MIGRATION_STRATEGY:" + entryData.get("P_MIGRATION_STRATEGY"));
									if ("null".equalsIgnoreCase(entryData.get("P_MIGRATION_STRATEGY").toString())) {
										System.out.println("P_MIGRATION_STRATEGY:null as string");
									}
									if (null == entryData.get("P_MIGRATION_STRATEGY").toString()) {
										System.out.println("P_MIGRATION_STRATEGY:null");
									}
								}
							}
							// dummy IP
							if (entryData.containsKey("P_MIGRATION_STRATEGY")
									&& Objects.nonNull(entryData.get("P_MIGRATION_STRATEGY"))) {
								trackerDetailsModel
										.setIntegrationType(entryData.get("P_MIGRATION_STRATEGY").toString());
								String strategy = trackerDetailsModel.getIntegrationType().toString().toLowerCase();
								if (strategy.contains("pseudo")) {
									trackerDetailsModel.setIntegrationType("Pseudo IP");
								} else {
									trackerDetailsModel.setIntegrationType("Legacy IP");
								}
								System.out.println("Migration Strategy (Fetch process) "
										+ trackerDetailsModel.getIntegrationType().toString());
								objTrackerIdDetails.put("integrationType",
										trackerDetailsModel.getIntegrationType().toString());
							} else {
								trackerDetailsModel.setIntegrationType("migrationStrategyNotFound");
								System.out.println("migrationStrategyNotFound");
								// objTrackerIdDetails.put("integrationType", "migrationStrategyNotFound");
							}
							if (entryData.containsKey("P_4G_LTE_FINAL") && Objects.nonNull(entryData.get("P_RELATED_ENB_IDS"))
									&& StringUtils.isNotEmpty(entryData.get("P_4G_LTE_FINAL").toString())
									&& "1".equalsIgnoreCase(entryData.get("P_4G_LTE_FINAL").toString())) {
								if (entryData.containsKey("P_RELATED_ENB_IDS") && Objects.nonNull(entryData.get("P_RELATED_ENB_IDS"))
										&& StringUtils.isNotEmpty(entryData.get("P_RELATED_ENB_IDS").toString())) {
									trackerDetailsModel
											.setPrelatedEnbIds(entryData.get("P_RELATED_ENB_IDS").toString());
									logger.error("P_RELATED_ENB_IDS  :"+ entryData.containsKey("P_RELATED_ENB_IDS"));
								}else
									logger.error("P_RELATED_ENB_IDS  is missing or must be null");
							}else {
								logger.error("P_4G_LTE_FINAL key is missing or must null");
							}
							if (entryData.containsKey("P_5G_MMW_FINAL") && Objects.nonNull(entryData.get("P_5G_MMW_FINAL"))
									&& StringUtils.isNotEmpty(entryData.get("P_5G_MMW_FINAL").toString())
									&& "1".equalsIgnoreCase(entryData.get("P_5G_MMW_FINAL").toString())) {
								if (entryData.containsKey("P_SAMSUNG_DUX") && Objects.nonNull(entryData.get("P_SAMSUNG_DUX"))
										&& StringUtils.isNotEmpty(entryData.get("P_SAMSUNG_DUX").toString())) {
									trackerDetailsModel.setPrelatedGnbIds(entryData.get("P_SAMSUNG_DUX").toString());
									logger.error("P_SAMSUNG_DUX  :"+ entryData.containsKey("P_SAMSUNG_DUX"));
								}else
									logger.error("P_SAMSUNG_DUX is missing or must be null ");
							}else {
								logger.error("P_5G_MMW_FINAL key is missing or must null");
							}
							if (entryData.containsKey("P_4G_LTE_FINAL") && Objects.nonNull(entryData.get("P_4G_LTE_FINAL"))
									&& StringUtils.isNotEmpty(entryData.get("P_4G_LTE_FINAL").toString())
									&& "1".equalsIgnoreCase(entryData.get("P_4G_LTE_FINAL").toString())) {
								if (entryData.containsKey("P_FSU_ID") && Objects.nonNull(entryData.get("P_FSU_ID"))
										&& StringUtils.isNotEmpty(entryData.get("P_FSU_ID").toString())) {
									trackerDetailsModel.setFsuIds(entryData.get("P_FSU_ID").toString());
									//System.out.println("fu id@@@@@@@@@@@@@@@ : "+ trackerDetailsModel.getFsuIds().toString());
									//logger.info("P_SAMSUNG_FSU  :"+ entryData.containsKey("P_SAMSUNG_FSU"));
								}else
									logger.error("P_SAMSUNG_FSU  is missing or must be null");
							}else {
								logger.error("P_4G_FSU_FINAL key is missing or must null");
							}
							if (entryData.containsKey("P_5G_DSS_FINAL") && Objects.nonNull(entryData.get("P_5G_DSS_FINAL"))
									&& StringUtils.isNotEmpty(entryData.get("P_5G_DSS_FINAL").toString())
									&& "1".equalsIgnoreCase(entryData.get("P_5G_DSS_FINAL").toString())) {
								if (entryData.containsKey("P_VDU_ID_TYPE_1_DSS") && Objects.nonNull(entryData.get("P_VDU_ID_TYPE_1_DSS"))
										&& StringUtils.isNotEmpty(entryData.get("P_VDU_ID_TYPE_1_DSS").toString())) {
									trackerDetailsModel.setDssIds(entryData.get("P_VDU_ID_TYPE_1_DSS").toString());
									//logger.error("P_SAMSUNG_VDU-ID  :"+ entryData.containsKey("P_SAMSUNG_VDU-ID"));

								}else
									logger.error("P_VDU_ID_TYPE_1_DSS is missing or must be null");

							}else {
								logger.error("P_5G_DSS_FINAL key is missing or must be null");
							}
							
							if (entryData.containsKey("P_5G_CBAND_FINAL") && Objects.nonNull(entryData.get("P_5G_CBAND_FINAL"))
									&& StringUtils.isNotEmpty(entryData.get("P_5G_CBAND_FINAL").toString())
									&& "1".equalsIgnoreCase(entryData.get("P_5G_CBAND_FINAL").toString())) {
								if (entryData.containsKey("P_VDU_TYPE_2__VDU_ID_CBAND") && Objects.nonNull(entryData.get("P_VDU_TYPE_2__VDU_ID_CBAND"))
										&& StringUtils.isNotEmpty(entryData.get("P_VDU_TYPE_2__VDU_ID_CBAND").toString())) {
									trackerDetailsModel.setcBandIds(entryData.get("P_VDU_TYPE_2__VDU_ID_CBAND").toString());
									//logger.error("P_SAMSUNG_CBAND-ID  :"+ entryData.containsKey("P_SAMSUNG_CBAND-ID"));

								}else
									logger.error("P_VDU_TYPE_2__VDU_ID_CBAND is missing or must be null ");

							}else {
								logger.error("P_5G_CBAND_FINAL key is missing or must be null");
							}
							StringBuilder reqResponseJson = new StringBuilder();
							reqResponseJson.append(Constants.REQUEST);
							reqResponseJson.append("\n");
							reqResponseJson.append(trakerIdUrl);
							reqResponseJson.append("\n");
							reqResponseJson.append(Constants.RESPONSE);
							reqResponseJson.append("\n");
							reqResponseJson.append(new ObjectMapper().writeValueAsString(entryData));
							reqResponseJson.append("\n");
							trackerDetailsModel.setTrackerDetailsJson(reqResponseJson.toString());

							listTrackerDetailsModel.add(trackerDetailsModel);

						}
					}

				}

				objTrackerIdDetails.put("trakerList", listTrackerDetailsModel);
				objTrackerIdDetails.put("statusCode", response.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			objTrackerIdDetails.put("statusCode", e.getStatusCode());
			objTrackerIdDetails.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.info(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return objTrackerIdDetails;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getWorkPlanIdList(String authStr, String trackerId, String page) {
		// TODO Auto-generated method stub
		JSONObject objTrackerIdDetails = new JSONObject();
		try {

			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(objOvGeneralEntity.getValue());
			urlBuilder.append(Constants.OV_WORKPLANID_URL);
			urlBuilder.append("?");
			urlBuilder.append("trackor_id=");
			urlBuilder.append(trackerId);
			urlBuilder.append("&");
			urlBuilder.append("page=");
			urlBuilder.append(page);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			// create headers
			headers.add("Authorization", "Basic " + base64Creds);
			@SuppressWarnings("rawtypes")
			HttpEntity requestEntity = new HttpEntity(headers);
			System.out.println("2nd Api : " + urlBuilder);
			HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET,
					requestEntity, String.class);
			int statusCode = response.getStatusCodeValue();
			System.out.println("Response for 2nd API : " + response);
			if (200 == statusCode) {
				String trackerIdJson = response.getBody();
				List<HashMap<String, String>> workPlanList = new ObjectMapper().readValue(trackerIdJson,
						new TypeReference<List<HashMap<String, String>>>() {
						});

				TrackerDetailsModel trackerDetailsModel = new TrackerDetailsModel();
				if (!ObjectUtils.isEmpty(workPlanList)) {

					for (HashMap<String, String> entryData : workPlanList) {
						if(entryData.containsKey("active")&& StringUtils.isNotEmpty(entryData.get("active")) &&
								entryData.get("active").equalsIgnoreCase("true"))
						{
						if (entryData.containsKey("id") && StringUtils.isNotEmpty(entryData.get("id"))) {
							trackerDetailsModel.setWorkPlanId(entryData.get("id"));
							trackerDetailsModel.setWorkPlanStatus(entryData.get("active"));
							System.out.println("workplan Status : "+ entryData.get("active"));
							StringBuilder reqResponseJson = new StringBuilder();
							reqResponseJson.append(Constants.REQUEST);
							reqResponseJson.append("\n");
							reqResponseJson.append(urlBuilder.toString());
							reqResponseJson.append("\n");
							reqResponseJson.append(Constants.RESPONSE);
							reqResponseJson.append("\n");
							reqResponseJson.append(new ObjectMapper().writeValueAsString(entryData));
							reqResponseJson.append("\n");
							trackerDetailsModel.setWorkPlanDetailsJson(reqResponseJson.toString());
							break;

						}
					}
					}
				}
				objTrackerIdDetails.put("workPlanDetails", trackerDetailsModel);
				objTrackerIdDetails.put("statusCode", response.getStatusCode());
			}

		} catch (HttpClientErrorException e) {
			objTrackerIdDetails.put("statusCode", e.getStatusCode());
			objTrackerIdDetails.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.error(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return objTrackerIdDetails;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getTasksList(String authStr, String workPlanId, String programName, String integrationType) {
		// TODO Auto-generated method stub
		JSONObject objTrackerIdDetails = new JSONObject();
		boolean commissionOrderStatus = false;
		String order_number = null;
		try {

			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(programName);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setConfigType("s&r");
			MileStonesModel MileStonesModel = customerService.getMileStonesTemplate(programTemplateModel);

			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(objOvGeneralEntity.getValue());
			urlBuilder.append(Constants.OV_TASKS_URL);
			urlBuilder.append(workPlanId);
			urlBuilder.append("/");
			urlBuilder.append("tasks");

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			// create headers
			headers.add("Authorization", "Basic " + base64Creds);
			@SuppressWarnings("rawtypes")
			HttpEntity requestEntity = new HttpEntity(headers);
			HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			System.out.println("3rd API " + urlBuilder);
			ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET,
					requestEntity, String.class);
			int statusCode = response.getStatusCodeValue();

			if (200 == statusCode) {
				System.out.println("Response for 3rd API Status 200 OK ");
				String taskIdJson = response.getBody();
				List<HashMap<String, Object>> taskList = new ObjectMapper().readValue(taskIdJson,
						new TypeReference<List<HashMap<String, Object>>>() {
						});

				if (programName.contains("USM-LIVE")) {
					order_number = MileStonesModel.getCommisionStart().toString();
					System.out.println("order number of 4gusm : " + order_number);
				}
				if (programName.contains("MM")) {
					order_number = MileStonesModel.getCommisionStart().toString();
					System.out.println("order number of 5gMM : " + order_number);
				}
				if (programName.contains("CBAND")) {
					order_number = Constants.COMMISSION_ORDER_NO_CBAND;
					System.out.println("order number of CBAND : " + order_number);
				}
				if (programName.contains("DSS")) {
					order_number = Constants.COMMISSION_ORDER_NO_DSS;
					System.out.println("order number of DSS : " + order_number);
				}
				if (programName.contains("FSU")) {
					order_number = MileStonesModel.getCommisionStart().toString();
					System.out.println("order number of FSU : " +order_number);
				}

				TrackerDetailsModel taskDetailsModel = new TrackerDetailsModel();
				if (!ObjectUtils.isEmpty(taskList)) {

					for (HashMap<String, Object> entryData : taskList) {
						if (entryData.containsKey("order_number") && !ObjectUtils.isEmpty(entryData.get("order_number"))
								&& order_number!=null && order_number
										.equalsIgnoreCase(entryData.get("order_number").toString())
								&& entryData.containsKey("projected_finish_date")
								&& !ObjectUtils.isEmpty(entryData.get("projected_finish_date"))) {

							commissionOrderStatus = true;
							StringBuilder orderNumbersJson = new StringBuilder();
							Map<String, String> statusdata = commissionDateValidation(taskList, workPlanId,
									orderNumbersJson, programName, integrationType);
							System.out.println("statusdata " + statusdata);
							if (statusdata.containsKey("status")
									&& Constants.SUCCESS.equalsIgnoreCase(statusdata.get("status"))) {
								String validationStatus = integrationDateValidation(
										entryData.get("projected_finish_date").toString());
								if (Constants.SUCCESS.equalsIgnoreCase(validationStatus)) {
									taskDetailsModel
											.setCommissionDate(entryData.get("projected_finish_date").toString());
									taskDetailsModel.setStatus(statusdata.get("status"));
									taskDetailsModel.setCiqCreated(statusdata.get("commissioningCiq"));
									if (!programName.contains("FSU")) {
									taskDetailsModel.setScriptDevlopement(statusdata.get("scriptDevlopement"));
									taskDetailsModel.setCiqValidate(statusdata.get("ciqValidateFinish"));
									}
									taskDetailsModel.setReason(null);
								} else {
									taskDetailsModel.setStatus(validationStatus);
									taskDetailsModel.setReason("Commission days difference is more than 5 days ");
									taskDetailsModel
											.setCommissionDate(entryData.get("projected_finish_date").toString());
								}

							} else {
								System.out.println("Status isn't success for workPlanId : " + workPlanId);
								// taskDetailsModel.setPrelatedEnbIds(statusdata.get(""));
								// System.out.println();
								taskDetailsModel.setStatus(statusdata.get("status"));
								taskDetailsModel.setCommissionDate(entryData.get("projected_finish_date").toString());
								taskDetailsModel.setCiqCreated(statusdata.get("commissioningCiq"));
								taskDetailsModel.setScriptDevlopement(statusdata.get("scriptDevlopement"));
								taskDetailsModel.setCiqValidate(statusdata.get("ciqValidateFinish"));
								taskDetailsModel.setReason(statusdata.get("reason"));
							}

							StringBuilder reqResponseJson = new StringBuilder();
							reqResponseJson.append(Constants.REQUEST);
							reqResponseJson.append("\n");
							reqResponseJson.append(urlBuilder.toString());
							reqResponseJson.append("\n");
							reqResponseJson.append(Constants.RESPONSE);
							reqResponseJson.append("\n");
							reqResponseJson.append(new ObjectMapper().writeValueAsString(entryData));
							reqResponseJson.append("\n");
							reqResponseJson.append(orderNumbersJson.toString());
							taskDetailsModel.setCommissionDetailsJson(reqResponseJson.toString());
							break;
						}
					}
					if (!commissionOrderStatus) {
						logger.error("order number:"+ order_number +",projected_finish_date is missing in Commission Dates API for WorkPlan id "+ workPlanId);
					}

				}
				objTrackerIdDetails.put("taskDetailsModel", taskDetailsModel);
				objTrackerIdDetails.put("statusCode", response.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			objTrackerIdDetails.put("statusCode", e.getStatusCode());
			objTrackerIdDetails.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.error(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return objTrackerIdDetails;
	}

	public Map<String, String> commissionDateValidation(List<HashMap<String, Object>> taskList, String workPlanId,
			StringBuilder orderNumbersJson, String programName, String integrationType) {
		String status = "";
		Map<String, String> data = new HashMap<>();
		try {
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(programName);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setConfigType("s&r");
			MileStonesModel MileStonesModel = customerService.getMileStonesTemplate(programTemplateModel);
			String ciqPrepareToRfDbDate = null;
			String rfScriptsReadyDate = null;
			String ciqReadyToRfDb = null;
			boolean ciqPrepareOrderStatus = false;
			boolean uploadOrderStatus = false;
			boolean ciqReadyOrderStatus = false;
			String CIQ_CREATED_ORDERNO = null;
			String SCRIPTS_DEVELOPMENT_ORDERNO = null;
			String CIQ_VALIDATE_ORDERNO = null;
			String migrationType = null;
			if (programName.contains("USM-LIVE")) {
				CIQ_CREATED_ORDERNO = MileStonesModel.getComissionCIQ().toString();
				SCRIPTS_DEVELOPMENT_ORDERNO = MileStonesModel.getScriptDevelop().toString();
				CIQ_VALIDATE_ORDERNO = MileStonesModel.getCIQValidate().toString();
				migrationType = integrationType;
				System.out.println("CIQ_CREATED_ORDERNO : " + CIQ_CREATED_ORDERNO + " SCRIPTS_DEVELOPMENT_ORDERNO: "
						+ SCRIPTS_DEVELOPMENT_ORDERNO + "CIQ_VALIDATE_ORDERNO: " + CIQ_VALIDATE_ORDERNO);
			}
			if (programName.contains("MM")) {
				CIQ_CREATED_ORDERNO = MileStonesModel.getComissionCIQ().toString();
				SCRIPTS_DEVELOPMENT_ORDERNO = MileStonesModel.getScriptDevelop().toString();
				CIQ_VALIDATE_ORDERNO = MileStonesModel.getCIQValidate().toString();
			}
			if (programName.contains("CBAND")) {
				CIQ_CREATED_ORDERNO = Constants.CIQ_CREATED_ORDERNO_CBAND;
				SCRIPTS_DEVELOPMENT_ORDERNO = Constants.SCRIPTS_DEVELOPMENT_ORDERNO_CBAND;
				CIQ_VALIDATE_ORDERNO = Constants.CIQ_VALIDATE_ORDERNO_CBAND;
			}
			if (programName.contains("DSS")) {
				CIQ_CREATED_ORDERNO = Constants.CIQ_CREATED_ORDERNO_DSS;
				SCRIPTS_DEVELOPMENT_ORDERNO = Constants.SCRIPTS_DEVELOPMENT_ORDERNO_DSS;
				CIQ_VALIDATE_ORDERNO = Constants.CIQ_VALIDATE_ORDERNO_DSS;
			}
			if (programName.contains("FSU")) {
				CIQ_CREATED_ORDERNO = MileStonesModel.getComissionCIQ().toString();
				//SCRIPTS_DEVELOPMENT_ORDERNO = MileStonesModel.getScriptDevelop().toString();
				//CIQ_VALIDATE_ORDERNO = MileStonesModel.getCIQValidate().toString();
			}

			for (HashMap<String, Object> entryData : taskList) {
				if (entryData.containsKey("order_number") && !ObjectUtils.isEmpty(entryData.get("order_number"))
						&& entryData.containsKey("projected_finish_date") && Objects.nonNull(entryData.get("projected_finish_date"))
						&& !ObjectUtils.isEmpty(entryData.get("projected_finish_date"))) {

					if (CIQ_CREATED_ORDERNO!=null && CIQ_CREATED_ORDERNO
							.equalsIgnoreCase(entryData.get("order_number").toString())) {
						ciqPrepareOrderStatus =true;
						ciqPrepareToRfDbDate = entryData.get("projected_finish_date").toString();
						orderNumbersJson.append("\n");
						orderNumbersJson.append("Commissioning CIQ Status JSON");
						orderNumbersJson.append("\n");
						orderNumbersJson.append(new ObjectMapper().writeValueAsString(entryData));
						orderNumbersJson.append("\n");
						System.out.println("order number:"+CIQ_CREATED_ORDERNO +" ,projected_finish_date is"+ ciqPrepareToRfDbDate+ "for id"+workPlanId);
						 
					} else if (SCRIPTS_DEVELOPMENT_ORDERNO!=null && SCRIPTS_DEVELOPMENT_ORDERNO
							.equalsIgnoreCase(entryData.get("order_number").toString()) && !programName.contains("FSU")) {
						uploadOrderStatus = true;
						rfScriptsReadyDate = entryData.get("projected_finish_date").toString();
						orderNumbersJson.append("\n");
						orderNumbersJson.append("RFSCRIPTS Status Json");
						orderNumbersJson.append("\n");
						orderNumbersJson.append(new ObjectMapper().writeValueAsString(entryData));
						orderNumbersJson.append("\n");
						System.out.println("order number:"+SCRIPTS_DEVELOPMENT_ORDERNO +" ,projected_finish_date is"+ rfScriptsReadyDate);
					}
					else if (CIQ_VALIDATE_ORDERNO!=null && CIQ_VALIDATE_ORDERNO
							.equalsIgnoreCase(entryData.get("order_number").toString())&&!programName.contains("FSU")) {
						ciqReadyOrderStatus = true;
						ciqReadyToRfDb = entryData.get("projected_finish_date").toString();
						orderNumbersJson.append("\n");
						orderNumbersJson.append("CIQ  Validate Status Json");
						orderNumbersJson.append("\n");
						orderNumbersJson.append(new ObjectMapper().writeValueAsString(entryData));
						orderNumbersJson.append("\n");
						System.out.println("order number:"+CIQ_VALIDATE_ORDERNO +" ,projected_finish_date is"+ ciqReadyToRfDb);
					}

				}
			}
			if(!ciqPrepareOrderStatus)
				logger.error("order number:"+CIQ_CREATED_ORDERNO +" ,actual_finish_date is missing in Commission Dates API for WorkPlan id "+ workPlanId);
			if(!uploadOrderStatus && !programName.contains("FSU"))
				logger.error("order number:"+SCRIPTS_DEVELOPMENT_ORDERNO+" ,actual_finish_date is missing in Commission Dates API for WorkPlan id "+ workPlanId);
			if(!ciqReadyOrderStatus && !programName.contains("FSU"))
				logger.error("order number:"+CIQ_VALIDATE_ORDERNO+" ,actual_finish_date is missing in Commission Dates API for WorkPlan id "+ workPlanId);

			Date todayDate = DateUtil.stringToDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD),
					Constants.YYYY_MM_DD);
			StringBuilder statusMessage = new StringBuilder();
			AtomicInteger statusCount = new AtomicInteger();
			if (StringUtils.isNotEmpty(ciqPrepareToRfDbDate)) {
				data.put("commissioningCiq", ciqPrepareToRfDbDate);
				Date ciqPrepareToRfDbDateFormat = DateUtil.stringToDate(ciqPrepareToRfDbDate, Constants.YYYY_MM_DD);
				int result = todayDate.compareTo(ciqPrepareToRfDbDateFormat);
				System.out.println("ciqPrepareToRfDbDateFormat Comm CIQ "+ciqPrepareToRfDbDateFormat);
				System.out.println("todayDate Comm CIQ "+todayDate);
				if (result < 0) {
					//statusMessage.append("Comm CIQ Not Completed,");
					statusMessage.append("OV Milestone "+ CIQ_CREATED_ORDERNO +" date isn't actualized by RF. ");
				} else {
					statusCount.getAndIncrement();
				}
			} else {
				//statusMessage.append("Comm CIQ is Not Completed,");
				statusMessage.append("OV Milestone "+ CIQ_CREATED_ORDERNO +" date isn't actualized by RF. ");
			}

			if (StringUtils.isNotEmpty(rfScriptsReadyDate)) {
				data.put("scriptDevlopement", rfScriptsReadyDate);
				Date rfScriptsReadyDateFormat = DateUtil.stringToDate(rfScriptsReadyDate, Constants.YYYY_MM_DD);
				int result = todayDate.compareTo(rfScriptsReadyDateFormat);
				System.out.println("todayDate "+todayDate);
				System.out.println("rfScriptsReadyDateFormat "+rfScriptsReadyDateFormat);
				if (result < 0) {
					//statusMessage.append("Script Dev Not Completed,");
					statusMessage.append("OV Milestone "+SCRIPTS_DEVELOPMENT_ORDERNO+ " date isn't actualized by RF. ");
				} else {
					statusCount.getAndIncrement();
				}
			} else {
				//statusMessage.append("Script Dev Not Completed,");
				statusMessage.append("OV Milestone "+SCRIPTS_DEVELOPMENT_ORDERNO+ " date isn't actualized by RF. ");
			}
			System.out.println("Before checking ciqReadyToRfDb "+ciqReadyToRfDb);
			if (StringUtils.isNotEmpty(ciqReadyToRfDb)) {
				data.put("ciqValidateFinish", ciqReadyToRfDb);
				Date ciqReadyToRfDbFormat = DateUtil.stringToDate(ciqReadyToRfDb, Constants.YYYY_MM_DD);
				System.out.println("ciqReadyToRfDbFormat (commission) "+ciqReadyToRfDbFormat);
				int result = todayDate.compareTo(ciqReadyToRfDbFormat);
				System.out.println("todayDate (commission) "+todayDate);
				if (result < 0) {
					//statusMessage.append("CIQ Validate Not Completed.");
					statusMessage.append("OV Milestone : "+CIQ_VALIDATE_ORDERNO+ " date isn't actualized by RF. ");
				} else {
					//System.out.println("Commission has started");
					statusCount.getAndIncrement();
				}
			} else {
				//statusMessage.append("CIQ Validate is Not Completed.");
				statusMessage.append("OV Milestone : "+CIQ_VALIDATE_ORDERNO+ " date isn't actualized by RF. ");
			}
			if (programName.contains("4G-USM")){
				if(migrationType.equalsIgnoreCase("migrationStrategyNotFound")) {
						statusMessage.append("OV Milestone : P_MIGRATION_STRATEGY is empty. ");
				} else {
						statusCount.getAndIncrement();
					}
			}
			/*if (3 == statusCount.get()) {
				status = Constants.SUCCESS;
				data.put("status", status);
			} else {
				status = Constants.FAIL + " - " + statusMessage.toString();
				data.put("reason", status);
				data.put("status", status);
			}*/

			if (4 == statusCount.get() && programName.contains("4G-USM")) {
				status = Constants.SUCCESS;
				data.put("status", status);
			} else if (1 == statusCount.get() && programName.contains("FSU")) {
				status = Constants.SUCCESS;
				data.put("status", status);
			}else if (3 == statusCount.get() && !programName.contains("4G-USM") && !programName.contains("FSU")) {
				status = Constants.SUCCESS;
				data.put("status", status);
			} else {
				status = Constants.FAIL + " - " + statusMessage.toString();
				data.put("reason", status);
				data.put("status", status);
			}
		} catch (Exception e) {
			status = Constants.FAIL;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return data;
	}

	public HttpComponentsClientHttpRequestFactory getHttpsConfiguration()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

		BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
				socketFactoryRegistry);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
				.setConnectionManager(connectionManager).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setHttpClient(httpClient);
		return requestFactory;
	}

	@Override
	public void shedulingFetchDetails(List<TrackerDetailsModel> trackerDetailsModellist, String fetchType,
			String remarks, String programName) {
		try {

			if (!ObjectUtils.isEmpty(trackerDetailsModellist)) {
				List<FetchOVResponseModel> list4GFetchOVResponseMdel = new ArrayList<>();
				List<FetchOVResponseModel> list5GFetchOVResponseMdel = new ArrayList<>();
				List<FetchOVResponseModel> list5GDssFetchOVResponseMdel = new ArrayList<>();
				List<FetchOVResponseModel> listFsuFetchOVResponseMdel = new ArrayList<>();
				List<FetchOVResponseModel> listCbandFetchOVResponseMdel = new ArrayList<>();

				for (TrackerDetailsModel trackerDetailsModel : trackerDetailsModellist) {
					if (Constants.USM_LIVE_4G.equalsIgnoreCase(programName)) {
						FetchOVResponseModel neIds4GFetchOVResponseModel = getConvertedOvResponseDetails(
								trackerDetailsModel, Constants.USM_LIVE_4G);

						if (!ObjectUtils.isEmpty(neIds4GFetchOVResponseModel)
								&& !ObjectUtils.isEmpty(neIds4GFetchOVResponseModel.getNeidList())
								&& !ObjectUtils.isEmpty(neIds4GFetchOVResponseModel.getMarket())) {
							list4GFetchOVResponseMdel.add(neIds4GFetchOVResponseModel);

						}
					} else if (Constants.MM_5G.equalsIgnoreCase(programName)) {
						FetchOVResponseModel neIds5GFetchOVResponseModel = getConvertedOvResponseDetails(
								trackerDetailsModel, Constants.MM_5G);
						if ((!ObjectUtils.isEmpty(neIds5GFetchOVResponseModel)
								&& !ObjectUtils.isEmpty(neIds5GFetchOVResponseModel.getNeidList())
								&& !ObjectUtils.isEmpty(neIds5GFetchOVResponseModel.getMarket()))) {
							list5GFetchOVResponseMdel.add(neIds5GFetchOVResponseModel);

						}
					} else if (Constants.DSS_5G.equalsIgnoreCase(programName)) {
						FetchOVResponseModel neIds5GDssFetchOVResponseModel = getConvertedOvResponseDetails(
								trackerDetailsModel, Constants.DSS_5G);
						if ((!ObjectUtils.isEmpty(neIds5GDssFetchOVResponseModel)
								&& !ObjectUtils.isEmpty(neIds5GDssFetchOVResponseModel.getNeidList())
								&& !ObjectUtils.isEmpty(neIds5GDssFetchOVResponseModel.getMarket()))) {
							list5GDssFetchOVResponseMdel.add(neIds5GDssFetchOVResponseModel);

						}
					} else if (Constants.FSU_4G.equalsIgnoreCase(programName)) {
						FetchOVResponseModel neIds4GFsuFetchOVResponseModel = getConvertedOvResponseDetails(
								trackerDetailsModel, Constants.FSU_4G);
						if (!ObjectUtils.isEmpty(neIds4GFsuFetchOVResponseModel)
								&& !ObjectUtils.isEmpty(neIds4GFsuFetchOVResponseModel.getNeidList())
								&& !ObjectUtils.isEmpty(neIds4GFsuFetchOVResponseModel.getMarket())) {
							listFsuFetchOVResponseMdel.add(neIds4GFsuFetchOVResponseModel);

						}
					} else if (Constants.CBAND_5G.equalsIgnoreCase(programName)) {
						FetchOVResponseModel neIds5GCbandFetchOVResponseModel = getConvertedOvResponseDetails(
								trackerDetailsModel, Constants.CBAND_5G);
						if ((!ObjectUtils.isEmpty(neIds5GCbandFetchOVResponseModel)
								&& !ObjectUtils.isEmpty(neIds5GCbandFetchOVResponseModel.getNeidList())
								&& !ObjectUtils.isEmpty(neIds5GCbandFetchOVResponseModel.getMarket()))) {
							listCbandFetchOVResponseMdel.add(neIds5GCbandFetchOVResponseModel);

						}
					}
				}
				if (!ObjectUtils.isEmpty(list4GFetchOVResponseMdel)) {
					// RF Db Code Merge from supriya
					ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
					OvGeneralEntity fetchDays = customerRepository.getOvlabelTemplate(Constants.OV_FETCH_DAYS);

					programTemplateModel.setProgramDetailsEntity(list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity());
					programTemplateModel.setConfigType("s&r");
					List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
							.getScheduledDaysDetails(programTemplateModel);

					
					String rfDBInteraction = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.FETCH_FROM_RFDB.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().get();
					if(rfDBInteraction.equalsIgnoreCase("OFF")) {
					ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
							list4GFetchOVResponseMdel,fetchType,remarks);
					if (!ObjectUtils.isEmpty(ciqMapDetails)) {
						saveScheduledDetails5Gmm(list4GFetchOVResponseMdel, ciqMapDetails,programName);
					}
					}else {
					JSONObject obj = createExcelFromRFDB(list4GFetchOVResponseMdel,fetchDays.getValue());
					String ciqName = obj.get("ciqFileName").toString();
					String ciqPath = obj.get("ciqFilePath").toString();
					// Bala 
					List<String> listOfNonExistingEnbs= (List<String>) obj.get("listOfNonExistingEnbs");
					String scriptPath = obj.get("scriptPath").toString();
					String scriptFiles = "";
					if (!ObjectUtils.isEmpty(obj.get("scriptFiles"))) {
						scriptFiles = obj.get("scriptFiles").toString();
					}
					File ciqDir = new File(obj.get("ciqFilePath").toString());
					boolean isAllowDuplicate = false;
					if (ciqDir.exists()) {
						if (!isAllowDuplicate) {
							isAllowDuplicate = true;
						}

					}
					StringBuilder filePath = new StringBuilder();
					filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
							.append(Constants.SEPARATOR).append(obj.get("ciqFilePath").toString());
					File file = new File(filePath + "" + obj.get("ciqFileName"));
					FileInputStream input = new FileInputStream(file);
					MultipartFile multipartFile = new MockMultipartFile(filePath.toString() + "" + ciqName, ciqName,
							"text/plain", IOUtils.toByteArray(input));
					String ciqFileName = multipartFile.getOriginalFilename();
					StringBuilder sbPath = new StringBuilder();
					sbPath.append(filePath);
					StringBuilder ciqFileTempPath = sbPath.append(Constants.SEPARATOR).append("TEMP/");
					if (CommonUtil.isValidObject(ciqName)
							&& fileUploadService.uploadMultipartFile(multipartFile, ciqFileTempPath.toString())) {
						JSONObject fileProcessResult = uploadCIQController.preMigrationFileProcess(multipartFile,
								filePath, ciqFileTempPath, isAllowDuplicate, "CIQ",
								list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(), ciqFileName,
								Constants.USM_LIVE_4G);
						CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
						CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
						customerDetailsModel.setId(list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId());
						List<CustomerDetailsEntity> detailsEntities = customerService
								.getCustomerDetailsList(customerDetailsModel);
						if (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
							ciqUploadAuditTrailDetModel.setProgramDetailsEntity(detailsEntities.get(0));
						}
						String ciqFileSavePath = filePath.toString();
						String scriptFileSavePath = " ";

						ciqFileSavePath = ciqFileSavePath
								.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
						scriptFileSavePath = scriptPath
								.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
						scriptFileSavePath = StringUtils.substringBeforeLast(scriptFileSavePath, "/");
						ciqUploadAuditTrailDetModel.setCiqFilePath(ciqPath);
						ciqUploadAuditTrailDetModel.setScriptFilePath(scriptFileSavePath);
						ciqUploadAuditTrailDetModel.setScriptFileName(scriptFiles);
						ciqUploadAuditTrailDetModel.setChecklistFilePath("");
						ciqUploadAuditTrailDetModel.setChecklistFileName("");
						ciqUploadAuditTrailDetModel.setFileSourceType(fetchType);
						ciqUploadAuditTrailDetModel.setCiqVersion(Constants.CIQ_VERSION_ORIGINAL);
						ciqUploadAuditTrailDetModel.setCiqFileName(multipartFile.getOriginalFilename());

						ciqUploadAuditTrailDetModel.setRemarks(remarks);
						CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = ciqUploadAuditTrailDetailsDto
								.getCiqUploadAuditTrailDetEntity(ciqUploadAuditTrailDetModel, "");
						fileUploadService.createCiqAudit(ciqUploadAuditTrailDetEntity);
					}
					
					saveScheduledDetails(list4GFetchOVResponseMdel, ciqName, ciqPath,listOfNonExistingEnbs,programName);
					}
				}
				if (!ObjectUtils.isEmpty(list5GFetchOVResponseMdel)) {
					// RF Db Code Merge from supriya

					ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
							list5GFetchOVResponseMdel,fetchType,remarks);
					if (!ObjectUtils.isEmpty(ciqMapDetails)) {
						saveScheduledDetails5Gmm(list5GFetchOVResponseMdel, ciqMapDetails,programName);
					}

				}
				
				if (!ObjectUtils.isEmpty(list5GDssFetchOVResponseMdel)) {
					// RF Db Code Merge from supriya

					ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
							list5GDssFetchOVResponseMdel,fetchType,remarks);
					if (!ObjectUtils.isEmpty(ciqMapDetails)) {
						saveScheduledDetails5Gmm(list5GDssFetchOVResponseMdel, ciqMapDetails,programName);
					}

				}
				
				if(!ObjectUtils.isEmpty(listFsuFetchOVResponseMdel))
				{
					ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
							listFsuFetchOVResponseMdel,fetchType,remarks);
					if (!ObjectUtils.isEmpty(ciqMapDetails)) {
						saveScheduledDetails5Gmm(listFsuFetchOVResponseMdel, ciqMapDetails,programName);
					}
				}
				
				if (!ObjectUtils.isEmpty(listCbandFetchOVResponseMdel)) {

					ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
							listCbandFetchOVResponseMdel, fetchType, remarks);
					if (!ObjectUtils.isEmpty(ciqMapDetails)) {
						saveScheduledDetails5Gmm(listCbandFetchOVResponseMdel, ciqMapDetails,programName);
					}
				}

			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public OvScheduledEntity getOvEnvUploadDetails(OvScheduledEntity ovScheduledEntity) {
		JSONObject objStatus = new JSONObject();
		StringBuilder envUpdateJson = new StringBuilder();
		try {

			OvInteractionModel ovInteractionModel = customerService.getOvInteractionTemplate();
			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(programmeEntity);
			programTemplateModel.setConfigType("s&r");
			MileStonesModel MileStonesModel = customerService.getMileStonesTemplate(programTemplateModel);
			if (!ObjectUtils.isEmpty(ovInteractionModel) && StringUtils.isNotEmpty(ovInteractionModel.getEnvExport())
					&& ovInteractionModel.getEnvExport().equals("ON")) {
				OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder.append(objOvGeneralEntity.getValue());
				urlBuilder.append(Constants.ENV_UPLOAD_URL);
				String url = urlBuilder.toString();
				System.out.println("Ov upload url : "+url);
				String authStr = getAuthencationString();
				StringBuilder filePath = new StringBuilder();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

				headers.add("Authorization", "Basic " + base64Creds);

				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				filePath.append("/opt/rct/Samsung/SMART/").append(ovScheduledEntity.getEnvFilePath()).append(ovScheduledEntity.getEnvFileName());
				FileSystemResource file = new FileSystemResource(filePath.toString());
				body.add("file", file);

				Map<String, String> urlParams = new HashMap<>();
				urlParams.put("trackor_id", ovScheduledEntity.getTrackerId());
				//urlParams.put("field_name", "P_LTE_ENV");
				urlParams.put("field_name",MileStonesModel.getEnvMode().toString());
				
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
				HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
				
				UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
						// add Querry params
						.queryParam("file_name", file.getFilename());

				System.out.println(builder.buildAndExpand(urlParams).toUri());
				envUpdateJson.append("Request:").append(builder.buildAndExpand(urlParams).toUri()).append("\n");
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				logger.error("request Entity: "+ requestEntity);
				ResponseEntity<String> response = restTemplate.postForEntity(builder.buildAndExpand(urlParams).toUri(),
						requestEntity, String.class);
				logger.error("ENV Upload API Response: "+ response);
				int statusCode = response.getStatusCodeValue();
				logger.error("Status Code for Upload ENV: "+ statusCode);
				if (200 == statusCode) {
					ovScheduledEntity.setEnvExportStatus("Uploaded to OV and OV ACK");

					objStatus.put("statusCode", response.getStatusCode());
					objStatus.put("response", response.getBody());
				} else
					ovScheduledEntity.setEnvExportStatus("Uploaded to OV and OV NACK");
				envUpdateJson.append("Response:").append(response.getStatusCode());
			} else
				ovScheduledEntity.setEnvExportStatus("Not Uploaded and OV Off");

			ovScheduledEntity.setEnvUploadJson(envUpdateJson.toString());
			ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);

		} catch (HttpClientErrorException e) {
			objStatus.put("statusCode", e.getStatusCode());
			objStatus.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.error("Exception in deleteCiqDir in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
		}
		return ovScheduledEntity;

	}

	
	

	@Override
	@SuppressWarnings("unchecked")
	public OvScheduledEntity statusUpdateApi(OvScheduledEntity ovScheduledEntity, String type,String type2,String programName) {
		JSONObject objTrackerIdDetails = new JSONObject();
		StringBuilder statusJson = new StringBuilder();
		String orderid = null;
		try {
			OvInteractionModel ovInteractionModel = customerService.getOvInteractionTemplate();
			
			//if (ovScheduledEntity.getCustomerDetailsEntity().getProgramName() != null) 
				logger.error("Inside status update api. Check for type : "+type);
				logger.error("Inside status update api. Check for type2 : "+type2);
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(ovScheduledEntity.getCustomerDetailsEntity().getProgramName());
				ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
				programTemplateModel.setProgramDetailsEntity(programmeEntity);
				programTemplateModel.setConfigType("s&r");
				MileStonesModel MileStonesModel = customerService.getMileStonesTemplate(programTemplateModel);
			KeyValuesModel KeyValuesModel = customerService.getKeyValuesTemplate(programTemplateModel);
			System.out.println("for env : "+KeyValuesModel.getGeneratENV());
			System.out.println("for grow : "+KeyValuesModel.getGrowElements());
			System.out.println("for ciq : "+KeyValuesModel.getCIQIntegration());
			System.out.println("for mig : "+KeyValuesModel.getMigrationCI());
			//System.out.println("milestones : " +MileStonesModel.getGeneratENVDate().toString());

			if (!ObjectUtils.isEmpty(ovInteractionModel) && StringUtils.isNotEmpty(ovInteractionModel.getPreMigration())
					&& ovInteractionModel.getPreMigration().equals("ON") && type.equals("ENV") && !ObjectUtils.isEmpty(KeyValuesModel) 
					&& StringUtils.isNotEmpty(KeyValuesModel.getGeneratENV()) && KeyValuesModel.getGeneratENV().equals("ON")) {
				
				
				if (programName.contains("USM-LIVE"))
					orderid = MileStonesModel.getGeneratENVDate().toString();
				if (programName.contains("MM"))
					orderid = MileStonesModel.getGeneratENVDate().toString();
				if (programName.contains("DSS"))
					orderid = Constants.ENV_STATUS_ORDERID_DSS; 
				if (programName.contains("CBAND"))
					orderid = Constants.ENV_STATUS_ORDERID_CBAND; 
				if (programName.contains("4G-FSU")) {
					orderid = MileStonesModel.getGeneratENVDate().toString();
				}
				logger.error("order Id for ENV - Ananya " + orderid);
			} 
			else if (!ObjectUtils.isEmpty(ovInteractionModel)
					&& StringUtils.isNotEmpty(ovInteractionModel.getPreMigration())
					&& ovInteractionModel.getPreMigration().equals("ON") && type.equals("GROW") && !ObjectUtils.isEmpty(KeyValuesModel) 
					&& StringUtils.isNotEmpty(KeyValuesModel.getGrowElements()) && KeyValuesModel.getGrowElements().equals("ON") ) {
				if (programName.contains("USM-LIVE"))
					orderid = MileStonesModel.getGrowElementsDate().toString();
				if (programName.contains("MM"))
					orderid = MileStonesModel.getGrowElementsDate().toString();
				if (programName.contains("DSS"))
					orderid = Constants.GROW_STATUS_ORDERID_DSS;
				if (programName.contains("CBAND") )
					orderid = Constants.GROW_STATUS_ORDERID_CBAND;
				if (programName.contains("FSU"))
				{
					orderid = MileStonesModel.getGrowElementsDate().toString();
				}
				logger.error("order Id for GROW - Ananya " + orderid);
			}
			else if (!ObjectUtils.isEmpty(ovInteractionModel)
					&& StringUtils.isNotEmpty(ovInteractionModel.getPreMigration())
					&& ovInteractionModel.getPreMigration().equals("ON") && type.equals("CIQ") && !ObjectUtils.isEmpty(KeyValuesModel) 
					&& StringUtils.isNotEmpty(KeyValuesModel.getCIQIntegration()) && KeyValuesModel.getCIQIntegration().equals("ON")  ) {
				if(programName.contains("USM-LIVE"))
					orderid = MileStonesModel.getCIQIntegrationDate().toString();
				if(programName.contains("MM") )
					orderid = MileStonesModel.getCIQIntegrationDate().toString();
				if(programName.contains("DSS"))
					orderid =Constants.CIQ_STATUS_ORDERID_DSS;
				if(programName.contains("CBAND"))
					orderid = Constants.CIQ_STATUS_ORDERID_CBAND;
				if(programName.contains("FSU")) {
					orderid = MileStonesModel.getCIQIntegrationDate().toString();
				}
				logger.error("order Id for CIQ - Ananya " + orderid);
			}
			else if (!ObjectUtils.isEmpty(ovInteractionModel) && StringUtils.isNotEmpty(ovInteractionModel.getPreMigration())
					&& ovInteractionModel.getMigration().equals("ON") && type.equals("Migration") && !ObjectUtils.isEmpty(KeyValuesModel) 
					&& StringUtils.isNotEmpty(KeyValuesModel.getMigrationCI()) && KeyValuesModel.getMigrationCI().equals("ON") ) {
				if (programName.contains("USM-LIVE"))
					orderid = MileStonesModel.getMigrationCIDate().toString();
				if (programName.contains("MM"))
					orderid =Constants.MIG_STATUS_ORDERID_MM;
				if (programName.contains("DSS"))
					orderid = Constants.MIG_STATUS_ORDERID_DSS;
				if (programName.contains("CBAND"))
					orderid = Constants.MIG_STATUS_ORDERID_CBAND;
				if (programName.contains("FSU"))
				{
					orderid = Constants.MIG_STATUS_ORDERID_FSU;
				}
				logger.error("order Id for MIG - Ananya " + orderid);
			}//else if (!ObjectUtils.isEmpty(ovInteractionModel) && StringUtils.isNotEmpty(ovInteractionModel.getPreMigration())
//					&& ovInteractionModel.getPostAudit().equals("ON") && type.equals("PostMigration")) {
//				if(programName.contains("USM-LIVE"))
//					orderid = Constants.ENV_STATUS_ORDERID_LTE;
//				if(programName.contains("MM"))
//					orderid = Constants.ENV_STATUS_ORDERID_MM;
//				if(programName.contains("DSS"))
//					orderid = Constants.ENV_STATUS_ORDERID_DSS;
//				if(programName.contains("CBAND"))
//					orderid = Constants.ENV_STATUS_ORDERID_CBAND;
//			}
			
				String authStr = getAuthencationString();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.setContentType(MediaType.APPLICATION_JSON);
				String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
				// create headers
				headers.add("Authorization", "Basic " + base64Creds);
				OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder.append(objOvGeneralEntity.getValue());
				urlBuilder.append(Constants.OV_STATUS_UPDATE_URL);
				urlBuilder.append(ovScheduledEntity.getTrackerId());
				urlBuilder.append("/wps/");
				urlBuilder.append(Constants.PROJECT_WORKPLAN);
				urlBuilder.append("/tasks?order_number=");
				//test
				//orderid = null;
				urlBuilder.append(orderid);
				JSONObject body = new JSONObject();
				body.put("id", ovScheduledEntity.getTrackerId());
				if(type2.equals("edit") && type.equals("ENV")) {
					body.put("actual_finish_date",ovScheduledEntity.getEnvGenerationDate().split(" ")[0]);
				}else if(type2.equals("edit") && type.equals("GROW")) {
					body.put("actual_finish_date",ovScheduledEntity.getPreMigGrowGenerationDate().split(" ")[0]);
				}else if (type2.equals("edit") && type.equals("CIQ")) {
					body.put("actual_finish_date",ovScheduledEntity.getCiqGenerationDate().split(" ")[0]);
				}else
					body.put("actual_finish_date", DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD));
				statusJson.append("Request:").append(body).append("\n");
				System.out.println("to OV : "+ statusJson.toString());
				System.out.println("checking request"+body.toJSONString());
				HttpEntity<JSONObject> requestEntity = new HttpEntity<>(body, headers);
				//HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
				requestFactory = getHttpsConfiguration();
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				System.out.println(urlBuilder);
				System.out.println("request Entity : "+ requestEntity);
				ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.PATCH,
						requestEntity, String.class);
				logger.error("Update Status API Response: "+ response);
				int statusCode = response.getStatusCodeValue();

				if (200 == statusCode) {

				objTrackerIdDetails.put("responseBody", response.getBody());
				objTrackerIdDetails.put("statusCode", response.getStatusCode());
				}
				statusJson.append("Response:").append(response.getBody());
				if (type.equals("ENV")) {
					logger.error("Update Status For type ENV");
					ovScheduledEntity.setEnvStatusJson(statusJson.toString());
					if(!type2.equals("edit")) {
					ovScheduledEntity
							.setEnvGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					}else
						ovScheduledEntity.setEnvGenerationDate(ovScheduledEntity.getEnvGenerationDate());
					
				} else if (type.equals("GROW")) {
					logger.error("Update Status For type GROW");
					ovScheduledEntity.setPreMigGrowJson(statusJson.toString());
					if(!type2.equals("edit")) {
					ovScheduledEntity
							.setPreMigGrowGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					}else {
						ovScheduledEntity
						.setPreMigGrowGenerationDate(ovScheduledEntity.getPreMigGrowGenerationDate());
					}

				} else if (type.equals("CIQ")) {
					logger.error("Update Status For type CIQ");
					ovScheduledEntity.setCiqUpdateJson(statusJson.toString());
					if(!type2.equals("edit")) {
						ovScheduledEntity.setCiqGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
					}
					else {
						ovScheduledEntity.setCiqGenerationDate(ovScheduledEntity.getCiqGenerationDate());
					}
				}else if (type.equals("Migration")) {
					logger.error("Update Status For type Migration");
					//ovScheduledEntity.setCiqUpdateJson(statusJson.toString());
					if(!type2.equals("edit")) {
						ovScheduledEntity.setMigrationCompleteTime(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
						}
					}else {
						ovScheduledEntity
						.setMigrationCompleteTime(ovScheduledEntity.getMigrationCompleteTime());
					}
				
				ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
			
	} catch (HttpClientErrorException e) {
			objTrackerIdDetails.put("statusCode", e.getStatusCode());
			objTrackerIdDetails.put("response", e.getResponseBodyAsString());
			logger.error("HttpClientErrorException "+e);
		} catch (Exception e) {
			logger.error(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
			logger.error("Exception Mee "+e);
			System.out.println("exception here Mee : "+ ExceptionUtils.getFullStackTrace(e));
		}

		return ovScheduledEntity;
	}

	public void saveScheduledDetails(List<FetchOVResponseModel> listFetchOVResponseMdel, String ciqName,
			String ciqPath,List<String> listOfNonExistingEnbs,String programName) {

		try {

			if (!ObjectUtils.isEmpty(listFetchOVResponseMdel)) {
				for (FetchOVResponseModel objFetchOVResponseMdel : listFetchOVResponseMdel) {
					if (!ObjectUtils.isEmpty(objFetchOVResponseMdel.getNeidList())) {

						for (String neId : objFetchOVResponseMdel.getNeidList()) {
							if(!ObjectUtils.isEmpty(listOfNonExistingEnbs) && listOfNonExistingEnbs.contains(neId))
							{
								OvScheduledEntity objOvScheduledEntity = fetchProcessRepository.getScheduledRecord(neId,
										objFetchOVResponseMdel.getTrackerId());
								if (ObjectUtils.isEmpty(objOvScheduledEntity)) {
									OvScheduledEntity persistOvScheduledEntity = getFailureOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId, new OvScheduledEntity(), null, null);

									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								}else {
									OvScheduledEntity persistOvScheduledEntity = getFailureOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId, objOvScheduledEntity, null, null);

									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								}
							}else {
							OvScheduledEntity objOvScheduledEntity = fetchProcessRepository.getScheduledRecord(neId,
									objFetchOVResponseMdel.getTrackerId());
							if (!ObjectUtils.isEmpty(objOvScheduledEntity)) {
								OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(
										objFetchOVResponseMdel, neId, objOvScheduledEntity, ciqName, ciqPath,programName);

								fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
							} else {
								OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(
										objFetchOVResponseMdel, neId, new OvScheduledEntity(), ciqName, ciqPath,programName);
								fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
							}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	public void saveScheduledDetails5Gmm(List<FetchOVResponseModel> listFetchOVResponseMdel,
			ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails,String programName) {

		try {

			if (!ObjectUtils.isEmpty(listFetchOVResponseMdel)) {
				for (FetchOVResponseModel objFetchOVResponseMdel : listFetchOVResponseMdel) {
					if (!ObjectUtils.isEmpty(objFetchOVResponseMdel.getNeidList())) {

						for (String neId : objFetchOVResponseMdel.getNeidList()) {
							System.out.println("Ne IDs in saveScheduledDetails5Gmm " + neId);
							if (ciqMapDetails.containsKey(neId)) {
								System.out.println("Ciq map details contains NeId " + neId);
								OvScheduledEntity objOvScheduledEntity = fetchProcessRepository.getScheduledRecord(neId,
										objFetchOVResponseMdel.getTrackerId());
								if (!ObjectUtils.isEmpty(objOvScheduledEntity)) {
									OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId, objOvScheduledEntity,
											ciqMapDetails.get(neId).getCiqName(), ciqMapDetails.get(neId).getCiqPath(),programName);

									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								} else {
									OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId, new OvScheduledEntity(),
											ciqMapDetails.get(neId).getCiqName(), ciqMapDetails.get(neId).getCiqPath(),programName);
									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								}
							}else {
								OvScheduledEntity objOvScheduledEntity = fetchProcessRepository.getScheduledRecord(neId,
										objFetchOVResponseMdel.getTrackerId());
								if (ObjectUtils.isEmpty(objOvScheduledEntity)) {
									OvScheduledEntity persistOvScheduledEntity = getFailureOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId, new OvScheduledEntity(), null, null);

									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								}else {
									OvScheduledEntity persistOvScheduledEntity = getFailureOvScheduledEntityDetails(
											objFetchOVResponseMdel, neId,objOvScheduledEntity, null, null);

									fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
								}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

	}

	private OvScheduledEntity getOvScheduledEntityDetails(FetchOVResponseModel objFetchOVResponseMdel, String neId,
			OvScheduledEntity persistOvScheduledEntity, String ciqName, String ciqPath,String programName) {
		try {

			CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(programName);
			ProgramTemplateModel programTemplateModelNew = new ProgramTemplateModel();
			programTemplateModelNew.setProgramDetailsEntity(programmeEntity);
			programTemplateModelNew.setConfigType("s&r");
			MileStonesModel MileStonesModel = customerService.getMileStonesTemplate(programTemplateModelNew);
			if (Constants.SUCCESS.equalsIgnoreCase(objFetchOVResponseMdel.getStatus())) {
				persistOvScheduledEntity.setCustomerDetailsEntity(objFetchOVResponseMdel.getCustomerDetailsEntity());
				persistOvScheduledEntity.setFetchDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
				persistOvScheduledEntity.setTrackerId(objFetchOVResponseMdel.getTrackerId());
				persistOvScheduledEntity.setSiteName(objFetchOVResponseMdel.getSiteName());
				persistOvScheduledEntity.setWorkPlanID(objFetchOVResponseMdel.getWorkPlanId());
				persistOvScheduledEntity.setNeId(neId);
				persistOvScheduledEntity.setCiqName(ciqName);
				persistOvScheduledEntity.setCiqFilePath(ciqPath);
				if(programName.contains("USM-LIVE"))
				{
				persistOvScheduledEntity.setOrderNumber(Constants.COMMISSION_ORDER_NO_LTE);
				//dummy IP
				persistOvScheduledEntity.setIntegrationType(objFetchOVResponseMdel.getIntegrationType());
				} else {
					persistOvScheduledEntity.setIntegrationType("NA");
				}
				if (programName.contains("MM"))
					persistOvScheduledEntity.setOrderNumber(Constants.COMMISSION_ORDER_NO_MM);
				if (programName.contains("CBAND"))
					persistOvScheduledEntity.setOrderNumber(Constants.COMMISSION_ORDER_NO_CBAND);
				if (programName.contains("DSS"))
					persistOvScheduledEntity.setOrderNumber(Constants.COMMISSION_ORDER_NO_DSS);
				if (programName.contains("FSU"))
					persistOvScheduledEntity.setOrderNumber(Constants.COMMISSION_ORDER_NO_FSU);
				
				persistOvScheduledEntity.setFetchRemarks(Constants.COMPLETED);
				persistOvScheduledEntity.setFetchDetailsJson(objFetchOVResponseMdel.getFetchJson());
				persistOvScheduledEntity.setCiqGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));

				// sheduled times

				if (!schedulingValidationCheck(persistOvScheduledEntity.getMigrationScheduledTime(),
						objFetchOVResponseMdel.getCommissionDate())) {
					ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();

					programTemplateModel.setProgramDetailsEntity(objFetchOVResponseMdel.getCustomerDetailsEntity());
					programTemplateModel.setConfigType("s&r");

					List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
							.getScheduledDaysDetails(programTemplateModel);

					// premigrationscheduled days
					String preMigraSchedule = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.PREMIGRATION_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
					/*
					 * String envExportSchedule = listProgramTemplateEntity.stream() .filter(entity
					 * -> Constants.ENV_EXPORT_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
					 * .map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
					 */
					String neGrowSchedule = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.NE_GROW_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
					String migraSchedule = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.MIGRATION_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
					String postMigrationAuditSchedule = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.POST_MIGRATION_AUDIT_SCHEDULE
									.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);

					String postMigrationRanAtpSchedule = listProgramTemplateEntity.stream()
							.filter(entity -> Constants.POST_MIGRATION_RANATP_SCHEDULE
									.equalsIgnoreCase(entity.getLabel()))
							.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);

					if (StringUtils.isNotEmpty(preMigraSchedule)) {
						if (preMigraSchedule.contains(",")) {
							String[] datesDetails = preMigraSchedule.split(",");
							Date shedule1 = getScheduledDate(datesDetails[0],
									objFetchOVResponseMdel.getCommissionDate());
							Date shedule2 = getScheduledDate(datesDetails[1],
									objFetchOVResponseMdel.getCommissionDate());
							if (shedule1.after(shedule2)) {
								System.out.println("Shedule1 IF Loop "+DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								System.out.println("Shedule2 IF Loop "+DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPremigrationScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPremigrationReScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
								persistOvScheduledEntity.setEnvStatus(Constants.SCHEDULED);
//								persistOvScheduledEntity
//										.setEnvGenerationDate(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPreMigGrowStatus(Constants.SCHEDULED);
//								persistOvScheduledEntity.setPreMigGrowGenerationDate(
//										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

							} else if (shedule1.before(shedule2)) {
								System.out.println("Shedule1 ESLE-IF Loop "+DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								System.out.println("Shedule2 ELSE-IF Loop "+DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPremigrationScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPremigrationReScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
								persistOvScheduledEntity.setEnvStatus(Constants.SCHEDULED);
//								persistOvScheduledEntity
//										.setEnvGenerationDate(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPreMigGrowStatus(Constants.SCHEDULED);
//								persistOvScheduledEntity.setPreMigGrowGenerationDate(
//										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							}
						} else if ("OFF".equalsIgnoreCase(preMigraSchedule)) {
							persistOvScheduledEntity.setPremigrationScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setPreMigStatus(Constants.NOT_SCHEDULED);

							persistOvScheduledEntity.setEnvStatus(Constants.NOT_SCHEDULED);
							//persistOvScheduledEntity.setEnvGenerationDate(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setPreMigGrowStatus(Constants.NOT_SCHEDULED);
							//persistOvScheduledEntity.setPreMigGrowGenerationDate(Constants.OV_SCHEDULED_TIME_OFF);

						} else {
							Date shedule1 = getScheduledDate(preMigraSchedule,
									objFetchOVResponseMdel.getCommissionDate());
							persistOvScheduledEntity.setPremigrationScheduledTime(
									DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
							persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
							persistOvScheduledEntity.setEnvStatus(Constants.SCHEDULED);
//							persistOvScheduledEntity
//									.setEnvGenerationDate(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setPreMigGrowStatus(Constants.SCHEDULED);
//							persistOvScheduledEntity
//									.setPreMigGrowGenerationDate(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							
							System.out.println("Shedule1 else Loop "+DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							System.out.println("shedule1 "+shedule1);
							//System.out.println("Shedule2 IF Loop "+DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						}
					}

					/*
					 * if (StringUtils.isNotEmpty(envExportSchedule)) { if
					 * (envExportSchedule.contains(",")) { String[] datesDetails =
					 * envExportSchedule.split(","); Date shedule1 =
					 * getScheduledDate(datesDetails[0],
					 * objFetchOVResponseMdel.getCommissionDate()); Date shedule2 =
					 * getScheduledDate(datesDetails[1],
					 * objFetchOVResponseMdel.getCommissionDate()); if (shedule1.after(shedule2)) {
					 * persistOvScheduledEntity
					 * .setEnvFileExportScheduledTime(DateUtil.dateToString(shedule2,
					 * Constants.YYYY_MM_DD)); persistOvScheduledEntity
					 * .setEnvFileExportReScheduledTime(DateUtil.dateToString(shedule1,
					 * Constants.YYYY_MM_DD));
					 * persistOvScheduledEntity.setEnvExportStatus(Constants.SCHEDULED);
					 * 
					 * } else if (shedule1.before(shedule2)) {
					 * 
					 * persistOvScheduledEntity
					 * .setEnvFileExportScheduledTime(DateUtil.dateToString(shedule1,
					 * Constants.YYYY_MM_DD)); persistOvScheduledEntity
					 * .setEnvFileExportReScheduledTime(DateUtil.dateToString(shedule2,
					 * Constants.YYYY_MM_DD));
					 * persistOvScheduledEntity.setEnvExportStatus(Constants.SCHEDULED); } } else if
					 * ("OFF".equalsIgnoreCase(envExportSchedule)) {
					 * persistOvScheduledEntity.setEnvFileExportScheduledTime(Constants.
					 * OV_SCHEDULED_TIME_OFF);
					 * persistOvScheduledEntity.setEnvExportStatus(Constants.NOT_SCHEDULED);
					 * 
					 * } else { Date shedule1 = getScheduledDate(envExportSchedule,
					 * objFetchOVResponseMdel.getCommissionDate()); persistOvScheduledEntity
					 * .setEnvFileExportScheduledTime(DateUtil.dateToString(shedule1,
					 * Constants.YYYY_MM_DD));
					 * persistOvScheduledEntity.setEnvExportStatus(Constants.SCHEDULED); } }
					 */

					if (StringUtils.isNotEmpty(neGrowSchedule)) {
						if (neGrowSchedule.contains(",")) {
							String[] datesDetails = neGrowSchedule.split(",");
							Date shedule1 = getScheduledDate(datesDetails[0],
									objFetchOVResponseMdel.getCommissionDate());
							Date shedule2 = getScheduledDate(datesDetails[1],
									objFetchOVResponseMdel.getCommissionDate());
							if (shedule1.after(shedule2)) {
								persistOvScheduledEntity
										.setNeGrowScheduledTime(DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setNeGrowReScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setNeGrowStatus(Constants.SCHEDULED);

							} else if (shedule1.before(shedule2)) {

								persistOvScheduledEntity
										.setNeGrowScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setNeGrowReScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setNeGrowStatus(Constants.SCHEDULED);
							}
						} else if ("OFF".equalsIgnoreCase(neGrowSchedule)) {
							persistOvScheduledEntity.setNeGrowScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setNeGrowStatus(Constants.NOT_SCHEDULED);

						} else {
							Date shedule1 = getScheduledDate(neGrowSchedule,
									objFetchOVResponseMdel.getCommissionDate());
							persistOvScheduledEntity
									.setNeGrowScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setNeGrowStatus(Constants.SCHEDULED);
						}
					}

					if (StringUtils.isNotEmpty(migraSchedule)) {
						if (migraSchedule.contains(",")) {
							String[] datesDetails = migraSchedule.split(",");
							Date shedule1 = getScheduledDate(datesDetails[0],
									objFetchOVResponseMdel.getCommissionDate());
							Date shedule2 = getScheduledDate(datesDetails[1],
									objFetchOVResponseMdel.getCommissionDate());
							if (shedule1.after(shedule2)) {
								persistOvScheduledEntity.setMigrationScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setMigrationReScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setMigStatus(Constants.SCHEDULED);

							} else if (shedule1.before(shedule2)) {

								persistOvScheduledEntity.setMigrationScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setMigrationReScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setMigStatus(Constants.SCHEDULED);
							}
						} else if ("OFF".equalsIgnoreCase(migraSchedule)) {
							persistOvScheduledEntity.setMigrationScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setMigStatus(Constants.NOT_SCHEDULED);

						} else {
							Date shedule1 = getScheduledDate(migraSchedule, objFetchOVResponseMdel.getCommissionDate());
							persistOvScheduledEntity
									.setMigrationScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setMigStatus(Constants.SCHEDULED);
						}
					}

					if (StringUtils.isNotEmpty(postMigrationAuditSchedule)) {
						if (postMigrationAuditSchedule.contains(",")) {
							String[] datesDetails = postMigrationAuditSchedule.split(",");
							Date shedule1 = getScheduledDate(datesDetails[0],
									objFetchOVResponseMdel.getCommissionDate());
							Date shedule2 = getScheduledDate(datesDetails[1],
									objFetchOVResponseMdel.getCommissionDate());
							if (shedule1.after(shedule2)) {
								persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPostmigrationAuditReScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPostMigAuditStatus(Constants.SCHEDULED);

							} else if (shedule1.before(shedule2)) {

								persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPostmigrationAuditReScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
							}
						} else if ("OFF".equalsIgnoreCase(postMigrationAuditSchedule)) {
							persistOvScheduledEntity
									.setPostmigrationAuditScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setPostMigAuditStatus(Constants.NOT_SCHEDULED);

						} else {
							Date shedule1 = getScheduledDate(postMigrationAuditSchedule,
									objFetchOVResponseMdel.getCommissionDate());
							persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
									DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setPostMigAuditStatus(Constants.SCHEDULED);
						}
					}

					if (StringUtils.isNotEmpty(postMigrationRanAtpSchedule)) {
						if (postMigrationRanAtpSchedule.contains(",")) {
							String[] datesDetails = postMigrationRanAtpSchedule.split(",");
							Date shedule1 = getScheduledDate(datesDetails[0],
									objFetchOVResponseMdel.getCommissionDate());
							Date shedule2 = getScheduledDate(datesDetails[1],
									objFetchOVResponseMdel.getCommissionDate());
							if (shedule1.after(shedule2)) {
								persistOvScheduledEntity
										.setRanAtpScheduledTime(DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setRanAtpReScheduledTime(
										DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.SCHEDULED);

							} else if (shedule1.before(shedule2)) {

								persistOvScheduledEntity
										.setRanAtpScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setRanAtpReScheduledTime(
										DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
								persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.SCHEDULED);
							}
						} else if ("OFF".equalsIgnoreCase(postMigrationRanAtpSchedule)) {
							persistOvScheduledEntity.setRanAtpScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
							persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.NOT_SCHEDULED);

						} else {
							Date shedule1 = getScheduledDate(postMigrationRanAtpSchedule,
									objFetchOVResponseMdel.getCommissionDate());
							persistOvScheduledEntity
									.setRanAtpScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
							persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.SCHEDULED);
						}
					}
				}
			} else {
				persistOvScheduledEntity.setCustomerDetailsEntity(objFetchOVResponseMdel.getCustomerDetailsEntity());
				persistOvScheduledEntity.setFetchDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
				persistOvScheduledEntity.setTrackerId(null);
				persistOvScheduledEntity.setSiteName(objFetchOVResponseMdel.getSiteName());
				persistOvScheduledEntity.setWorkPlanID(null);
				persistOvScheduledEntity.setNeId(neId);
				persistOvScheduledEntity.setCiqName(null);
				persistOvScheduledEntity.setCiqFilePath(null);
				persistOvScheduledEntity.setOrderNumber(null);
				persistOvScheduledEntity.setPremigrationScheduledTime(null);
				persistOvScheduledEntity.setPreMigStatus(null);
				persistOvScheduledEntity.setEnvStatus(null);
				persistOvScheduledEntity.setPreMigGrowStatus(null);
				persistOvScheduledEntity.setNeGrowScheduledTime(null);
				persistOvScheduledEntity.setNeGrowStatus(null);
				persistOvScheduledEntity.setMigrationScheduledTime(null);
				persistOvScheduledEntity.setMigStatus(null);
				persistOvScheduledEntity
						.setPostmigrationAuditScheduledTime(null);
				persistOvScheduledEntity.setPostMigAuditStatus(null);
				persistOvScheduledEntity.setRanAtpScheduledTime(null);
				persistOvScheduledEntity.setPostMigRanAtpStatus(null);
				persistOvScheduledEntity.setFetchRemarks(objFetchOVResponseMdel.getStatus());
				persistOvScheduledEntity.setFetchDetailsJson(null);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return persistOvScheduledEntity;
	}

	public boolean schedulingValidationCheck(String previosScheduledDate, String currentScheduledDate) {
		boolean status = false;
		if (StringUtils.isNotEmpty(previosScheduledDate) && StringUtils.isNotEmpty(currentScheduledDate)
				&& previosScheduledDate.equalsIgnoreCase(currentScheduledDate)) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}

	public static Date getScheduledDate(String shedulingDays, String commssioningDate) {
		Date date = null;
		try {
			if (StringUtils.isNotEmpty(shedulingDays)) {
				String firstDate = shedulingDays.replaceAll("D-", "");
				Date migrDate = DateUtil.stringToDate(commssioningDate, Constants.YYYY_MM_DD);
				System.out.println("Migration Date for OV:"+migrDate);
				Calendar c = Calendar.getInstance();
				c.setTime(migrDate);
				Integer pastHistory = Integer.parseInt(firstDate);
				c.add(Calendar.DATE, -pastHistory); //for now
				date = c.getTime();
				System.out.println("shedulingDays "+shedulingDays);
				System.out.println("commssioningDate "+commssioningDate+" , pastHistory "+pastHistory+" ,migrDate "+migrDate+" ,firstDate"+firstDate);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return date;
	}

	public FetchOVResponseModel getConvertedOvResponseDetails(TrackerDetailsModel fetchOVMapModel, String generation) {
		FetchOVResponseModel fetchOVResponseMdel = new FetchOVResponseModel();
		try {

			if (!ObjectUtils.isEmpty(fetchOVMapModel) && StringUtils.isNotEmpty(fetchOVMapModel.getPrelatedEnbIds())
					&& Constants.USM_LIVE_4G.equalsIgnoreCase(generation)) {
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(generation);
				String[] enbIdsArray = fetchOVMapModel.getPrelatedEnbIds().split(",");
				LinkedHashSet<String> markets = new LinkedHashSet<>();
				LinkedHashSet<String> enbIds = new LinkedHashSet<>();
				for (String enbId : enbIdsArray) {
					if (enbId.contains("(") && enbId.contains(")")) {
						String enbIdValue = enbId.substring(enbId.indexOf("(") + 1, enbId.indexOf(")"))
								.replaceAll("^0+(?!$)", "");
						if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 5) {
							String marketId = enbIdValue.substring(0, 2);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						} else if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 6) {
							String marketId = enbIdValue.substring(0, 3);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						}
					}
				}

				StringBuilder fetchJsonbuilder = new StringBuilder();
				fetchJsonbuilder.append(fetchOVMapModel.getTrackerDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getWorkPlanDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getCommissionDetailsJson());
				fetchOVResponseMdel.setFetchJson(fetchJsonbuilder.toString());
				fetchOVResponseMdel.setMarket(markets);
				fetchOVResponseMdel.setSiteName(fetchOVMapModel.getSiteName());
				fetchOVResponseMdel.setNeidList(enbIds);
				fetchOVResponseMdel.setProgramName(programmeEntity.getProgramName());
				fetchOVResponseMdel.setCommissionDate(fetchOVMapModel.getCommissionDate());
				fetchOVResponseMdel.setCustomerDetailsEntity(programmeEntity);
				fetchOVResponseMdel.setTrackerId(fetchOVMapModel.getTrackerId());
				fetchOVResponseMdel.setWorkPlanId(fetchOVMapModel.getWorkPlanId());
				fetchOVResponseMdel.setStatus(fetchOVMapModel.getStatus());
				if (fetchOVResponseMdel.getProgramName().contains("USM")) {
					fetchOVResponseMdel.setIntegrationType(fetchOVMapModel.getIntegrationType()); //dummy IP
				} else
					fetchOVResponseMdel.setIntegrationType("NA"); //dummy IP

				System.out.println("the id :" + fetchOVResponseMdel.getNeidList()  );
			}

			if (!ObjectUtils.isEmpty(fetchOVMapModel) && StringUtils.isNotEmpty(fetchOVMapModel.getFsuIds())
					&& Constants.FSU_4G.equalsIgnoreCase(generation)) {
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(generation);
				String[] enbIdsArray = fetchOVMapModel.getFsuIds().split(",");
				LinkedHashSet<String> markets = new LinkedHashSet<>();
				LinkedHashSet<String> enbIds = new LinkedHashSet<>();
				for (String enbId : enbIdsArray) {
					if (enbId.contains("(") && enbId.contains(")")) {
						String enbValue = enbId.substring(enbId.indexOf("(") + 1, enbId.indexOf(")"))
								.replaceAll("^0+(?!$)", "");
						//remove 001;
						String enbIdValue = enbValue; //+"001";
						if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() < 9) {
							String marketId = enbIdValue.substring(0, 2);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						} else if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 9) {
							String marketId = enbIdValue.substring(0, 3);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						}
					}else {
						String enbValue = enbId.replaceAll("^0+(?!$)", "");
						String enbIdValue = enbValue; //+"001";
						if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() < 9) {
							String marketId = enbIdValue.substring(0, 2);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						} else if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 9) {
							String marketId = enbIdValue.substring(0, 3);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
							enbIds.add(enbIdValue);

						}
					}
				}

				StringBuilder fetchJsonbuilder = new StringBuilder();
				fetchJsonbuilder.append(fetchOVMapModel.getTrackerDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getWorkPlanDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getCommissionDetailsJson());
				fetchOVResponseMdel.setFetchJson(fetchJsonbuilder.toString());
				fetchOVResponseMdel.setMarket(markets);
				fetchOVResponseMdel.setNeidList(enbIds);
				fetchOVResponseMdel.setSiteName(fetchOVMapModel.getSiteName());
				fetchOVResponseMdel.setProgramName(programmeEntity.getProgramName());
				fetchOVResponseMdel.setCommissionDate(fetchOVMapModel.getCommissionDate());
				fetchOVResponseMdel.setCustomerDetailsEntity(programmeEntity);
				fetchOVResponseMdel.setTrackerId(fetchOVMapModel.getTrackerId());
				fetchOVResponseMdel.setWorkPlanId(fetchOVMapModel.getWorkPlanId());
				fetchOVResponseMdel.setStatus(fetchOVMapModel.getStatus());
				System.out.println("the fsu id :" + fetchOVResponseMdel.getNeidList());
			}
			if (!ObjectUtils.isEmpty(fetchOVMapModel) && StringUtils.isNotEmpty(fetchOVMapModel.getPrelatedGnbIds())
					&& Constants.MM_5G.equalsIgnoreCase(generation)) {
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(generation);
				String[] enbIdsArray = fetchOVMapModel.getPrelatedGnbIds().split(";");
				LinkedHashSet<String> markets = new LinkedHashSet<>();
				LinkedHashSet<String> enbIds = new LinkedHashSet<>();
				for (String enbId : enbIdsArray) {
						
						if (StringUtils.isNotEmpty(enbId)) {
							String marketId = enbId.substring(0, 2);
							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							} else {
								marketId = enbId.substring(0, 3);
								ipDetails = generateCsvRepositoryImpl.getip(marketId);
								if (!ObjectUtils.isEmpty(ipDetails)) {
									markets.add(ipDetails.getMarketName());
								}
							}
							enbIds.add(enbId);

						}
					
				}
				
//				for (String enbId : enbIdsArray) {
//					if (enbId.contains("(") && enbId.contains(")")) {
//						String enbIdValue = enbId.substring(enbId.indexOf("(") + 1, enbId.indexOf(")"))
//								.replaceAll("^0+(?!$)", "");
//						if (StringUtils.isNotEmpty(enbIdValue)) {
//							String marketId = enbIdValue.substring(0, 2);
//							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
//							if (!ObjectUtils.isEmpty(ipDetails)) {
//								markets.add(ipDetails.getMarketName());
//							} else {
//								marketId = enbIdValue.substring(0, 3);
//								ipDetails = generateCsvRepositoryImpl.getip(marketId);
//								if (!ObjectUtils.isEmpty(ipDetails)) {
//									markets.add(ipDetails.getMarketName());
//								}
//							}
//							enbIds.add(enbIdValue);
//
//						}
//					}
//				}
				StringBuilder fetchJsonbuilder = new StringBuilder();
				fetchJsonbuilder.append(fetchOVMapModel.getTrackerDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getWorkPlanDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getCommissionDetailsJson());
				fetchOVResponseMdel.setFetchJson(fetchJsonbuilder.toString());
				fetchOVResponseMdel.setMarket(markets);
				fetchOVResponseMdel.setNeidList(enbIds);
				fetchOVResponseMdel.setSiteName(fetchOVMapModel.getSiteName());
				fetchOVResponseMdel.setProgramName(programmeEntity.getProgramName());
				fetchOVResponseMdel.setCommissionDate(fetchOVMapModel.getCommissionDate());
				fetchOVResponseMdel.setCustomerDetailsEntity(programmeEntity);
				fetchOVResponseMdel.setTrackerId(fetchOVMapModel.getTrackerId());
				fetchOVResponseMdel.setWorkPlanId(fetchOVMapModel.getWorkPlanId());
				fetchOVResponseMdel.setStatus(fetchOVMapModel.getStatus());
			}

			if (!ObjectUtils.isEmpty(fetchOVMapModel) && StringUtils.isNotEmpty(fetchOVMapModel.getDssIds())
					&& Constants.DSS_5G.equalsIgnoreCase(generation)) {
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(generation);
				String[] enbIdsArray = fetchOVMapModel.getDssIds().split(",");
				LinkedHashSet<String> markets = new LinkedHashSet<>();
				LinkedHashSet<String> enbIds = new LinkedHashSet<>();
//				for (String enbId : enbIdsArray) {
//					if (enbId.contains("(") && enbId.contains(")")) {
//						String enbIdValue = enbId.substring(enbId.indexOf("(") + 1, enbId.indexOf(")"))
//								.replaceAll("^0+(?!$)", "");
//						if (StringUtils.isNotEmpty(enbIdValue)) {
//							String marketId = enbIdValue.substring(0, 2);
//							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
//							if (!ObjectUtils.isEmpty(ipDetails)) {
//								markets.add(ipDetails.getMarketName());
//							} else {
//								marketId = enbIdValue.substring(0, 3);
//								ipDetails = generateCsvRepositoryImpl.getip(marketId);
//								if (!ObjectUtils.isEmpty(ipDetails)) {
//									markets.add(ipDetails.getMarketName());
//								}
//							}
//							enbIds.add(enbIdValue);
//
//						}
//					}
//				}
				for (String enbId : enbIdsArray) {

					if (StringUtils.isNotEmpty(enbId)) {
						String marketId = enbId.substring(0, 2);
						Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
						if (!ObjectUtils.isEmpty(ipDetails)) {
							markets.add(ipDetails.getMarketName());
						} else {
							marketId = enbId.substring(0, 3);
							ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
						}
						enbIds.add(enbId);

					}

				}
				StringBuilder fetchJsonbuilder = new StringBuilder();
				fetchJsonbuilder.append(fetchOVMapModel.getTrackerDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getWorkPlanDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getCommissionDetailsJson());
				fetchOVResponseMdel.setFetchJson(fetchJsonbuilder.toString());
				fetchOVResponseMdel.setMarket(markets);
				fetchOVResponseMdel.setNeidList(enbIds);
				fetchOVResponseMdel.setSiteName(fetchOVMapModel.getSiteName());
				fetchOVResponseMdel.setProgramName(programmeEntity.getProgramName());
				fetchOVResponseMdel.setCommissionDate(fetchOVMapModel.getCommissionDate());
				fetchOVResponseMdel.setCustomerDetailsEntity(programmeEntity);
				fetchOVResponseMdel.setTrackerId(fetchOVMapModel.getTrackerId());
				fetchOVResponseMdel.setWorkPlanId(fetchOVMapModel.getWorkPlanId());
				fetchOVResponseMdel.setStatus(fetchOVMapModel.getStatus());
			}

			if (!ObjectUtils.isEmpty(fetchOVMapModel) && StringUtils.isNotEmpty(fetchOVMapModel.getcBandIds())
					&& Constants.CBAND_5G.equalsIgnoreCase(generation)) {
				CustomerDetailsEntity programmeEntity = fetchProcessRepository.getProgrammeDetails(generation);
				String[] enbIdsArray = fetchOVMapModel.getcBandIds().split(",");
				LinkedHashSet<String> markets = new LinkedHashSet<>();
				LinkedHashSet<String> enbIds = new LinkedHashSet<>();
//				for (String enbId : enbIdsArray) {
//					if (enbId.contains("(") && enbId.contains(")")) {
//						String enbIdValue = enbId.substring(enbId.indexOf("(") + 1, enbId.indexOf(")"))
//								.replaceAll("^0+(?!$)", "");
//						if (StringUtils.isNotEmpty(enbIdValue)) {
//							String marketId = enbIdValue.substring(0, 2);
//							Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
//							if (!ObjectUtils.isEmpty(ipDetails)) {
//								markets.add(ipDetails.getMarketName());
//							} else {
//								marketId = enbIdValue.substring(0, 3);
//								ipDetails = generateCsvRepositoryImpl.getip(marketId);
//								if (!ObjectUtils.isEmpty(ipDetails)) {
//									markets.add(ipDetails.getMarketName());
//								}
//							}
//							enbIds.add(enbIdValue);
//
//						}
//					}
//				}
				for (String enbId : enbIdsArray) {

					if (StringUtils.isNotEmpty(enbId)) {
						String marketId = enbId.substring(0, 2);
						Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
						if (!ObjectUtils.isEmpty(ipDetails)) {
							markets.add(ipDetails.getMarketName());
						} else {
							marketId = enbId.substring(0, 3);
							ipDetails = generateCsvRepositoryImpl.getip(marketId);
							if (!ObjectUtils.isEmpty(ipDetails)) {
								markets.add(ipDetails.getMarketName());
							}
						}
						enbIds.add(enbId);

					}

				}
				StringBuilder fetchJsonbuilder = new StringBuilder();
				fetchJsonbuilder.append(fetchOVMapModel.getTrackerDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getWorkPlanDetailsJson());
				fetchJsonbuilder.append(fetchOVMapModel.getCommissionDetailsJson());
				fetchOVResponseMdel.setFetchJson(fetchJsonbuilder.toString());
				fetchOVResponseMdel.setMarket(markets);
				fetchOVResponseMdel.setNeidList(enbIds);
				fetchOVResponseMdel.setSiteName(fetchOVMapModel.getSiteName());
				fetchOVResponseMdel.setProgramName(programmeEntity.getProgramName());
				fetchOVResponseMdel.setCommissionDate(fetchOVMapModel.getCommissionDate());
				fetchOVResponseMdel.setCustomerDetailsEntity(programmeEntity);
				fetchOVResponseMdel.setTrackerId(fetchOVMapModel.getTrackerId());
				fetchOVResponseMdel.setWorkPlanId(fetchOVMapModel.getWorkPlanId());
				fetchOVResponseMdel.setStatus(fetchOVMapModel.getStatus());
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return fetchOVResponseMdel;
	}

	@Override
	public JSONObject createExcelFromRFDB(List<FetchOVResponseModel> listFetchOVResponseMdel, String days) {
		JSONObject jsonobject = new JSONObject();
		Map<String, Object> objMap = new HashMap<String, Object>();
		JSONObject resultMap = new JSONObject();
		StringBuilder filePath = new StringBuilder();
		String scriptFileNames = "";
		List<String> listOfNonExistingEnbs = new ArrayList<>();

		MultipartFile scriptFile;

		try {
			Workbook workbook = new XSSFWorkbook();
			ObjectMapper mapper = new ObjectMapper();
			Sheet sheet = workbook.createSheet("Upstate NY CIQ");
			Sheet sheet1 = workbook.createSheet("IP_PLAN");
			Sheet sheet2 = workbook.createSheet("MME IP'S");
			Row headerRow = sheet.createRow(0);
			Row headerRowChild1 = sheet1.createRow(0);
			Row headerRowChild2 = sheet2.createRow(0);
			LinkedHashSet<String> neList = new LinkedHashSet<>();
			ProgramTemplateEntity objProgramTemplateEntity = fileUploadRepository.getProgramTemplate(
					listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(), Constants.CIQ_VALIDATE_TEMPLATE);
			JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());
			for (FetchOVResponseModel fetchOVResponseModel : listFetchOVResponseMdel) {
				neList.addAll(fetchOVResponseModel.getNeidList());
			}
			List<String> neLists = new ArrayList<>(neList);
			List<CIQTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("sheets").toString(),
					new TypeReference<List<CIQTemplateModel>>() {
					});
			for (CIQTemplateModel objLocCIQTemplateModel : myCIQTemplateModel) {
				if (objLocCIQTemplateModel.getSheetName().equalsIgnoreCase("Upstate NY CIQ")) {
					int flag = 0;
					ArrayList<Row> rowList = new ArrayList<>();
					for (int i = 0; i < objLocCIQTemplateModel.getColumns().size(); i++) {
						Cell cell = headerRow.createCell(i);
						cell.setCellValue(objLocCIQTemplateModel.getColumns().get(i).getColumnName());
						List<String> mmeIPVal= new ArrayList<>();
						if(objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName().equals("administrative-state")) {

							mmeIPVal = rfDb.getMMEIPVal(neLists, "Samsung_eNB_ID", "RFDBEntity",
									"adminiState");
						}else
						mmeIPVal = rfDb.getMMEIPVal(neLists, "Samsung_eNB_ID", "RFDBEntity",
								objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName());
						if (mmeIPVal != null) {
							for (int k = 0; k < mmeIPVal.size() && flag != 1; k++) {
								Row row = sheet.createRow(k + 1);
								rowList.add(row);
							}
							flag = 1;
							for (int j = 0; j < mmeIPVal.size(); j++) {
								String cellValue = String.valueOf(mmeIPVal.get(j));
								cellValue = cellValue == "null" ? "" : cellValue;
								rowList.get(j).createCell(i).setCellValue(cellValue);
							}

						}

					}
				} else if (objLocCIQTemplateModel.getSheetName().equalsIgnoreCase("IP_PLAN")) {
					int flag = 0;
					ArrayList<Row> rowList = new ArrayList<>();
					for (int i = 0; i < objLocCIQTemplateModel.getColumns().size(); i++) {
						Cell cell = headerRowChild1.createCell(i);
						cell.setCellValue(objLocCIQTemplateModel.getColumns().get(i).getColumnName());
						
						
						List<String> mmeIPVal= new ArrayList<>();
						if(objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName().equals("eNB_OAM/S&B_VLAN_prefix(/30)")) {

							mmeIPVal = rfDb.getMMEIPVal(neLists, "eNB_ID", "IPPLANEntity",
									"vlanprefix");
						}else if(objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName().equals("eNB_OAM_IP&eNB_S&B_IP")){
							mmeIPVal = rfDb.getMMEIPVal(neLists, "eNB_ID", "IPPLANEntity",
									"oamIP");
						}else if(objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName().equals("OAM_Gateway_IP_/eNB_S&B_Gateway IP")){
							mmeIPVal = rfDb.getMMEIPVal(neLists, "eNB_ID", "IPPLANEntity",
									"oamGatewayIP");
						}else {
						mmeIPVal = rfDb.getMMEIPVal(neLists, "eNB_ID", "IPPLANEntity",
								objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName());
						}
						if (objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName().equals("eNB_ID")) {
							for (String ne : neLists) {
								if (!mmeIPVal.contains(ne)) {
									listOfNonExistingEnbs.add(ne);
								}
							}
						}
						if (mmeIPVal != null) {
							for (int k = 0; k < mmeIPVal.size() && flag != 1; k++) {
								Row row = sheet1.createRow(k + 1);
								rowList.add(row);
							}
							flag = 1;
							for (int j = 0; j < mmeIPVal.size(); j++) {
								rowList.get(j).createCell(i).setCellValue(String.valueOf(mmeIPVal.get(j)));
							}
						}

					}
				} else if (objLocCIQTemplateModel.getSheetName().equalsIgnoreCase("MME IP'S")) {
					int flag = 0;
					ArrayList<Row> rowList = new ArrayList<>();
					ArrayList<String> marketPre = new ArrayList<>();
					for (String neValue : neLists) {
						String val = neValue.replaceAll("^0+(?!$)", "");
						if (StringUtils.isNotEmpty(val) && val.length() == 5)
							marketPre.add(val.substring(0, 2));
						else
							marketPre.add(val.substring(0, 3));
					}
					for (int i = 0; i < objLocCIQTemplateModel.getColumns().size(); i++) {
						Cell cell = headerRowChild2.createCell(i);
						cell.setCellValue(objLocCIQTemplateModel.getColumns().get(i).getColumnName());

						List<String> mmeIPVal = rfDb.getMMEIPVal(marketPre, "Market_Prefix", "MMEIPEntity",
								objLocCIQTemplateModel.getColumns().get(i).getColumnAliasName());
						if (mmeIPVal != null) {
							for (int k = 0; k < mmeIPVal.size() && flag != 1; k++) {
								Row row = sheet2.createRow(k + 1);
								rowList.add(row);
							}
							flag = 1;
							for (int j = 0; j < mmeIPVal.size(); j++) {
								rowList.get(j).createCell(i).setCellValue(String.valueOf(mmeIPVal.get(j)));
							}
						}

					}
				}
			}
			Date currentDate = new Date();
			Calendar c = Calendar.getInstance();

			Integer pastHistory = Integer.parseInt(days);
			c.setTime(currentDate);
			c.add(Calendar.DATE, pastHistory);
			Integer count = fileUploadRepository.getciqDetails(new SimpleDateFormat("yyyy_MM_dd").format(currentDate)
					+ "_" + new SimpleDateFormat("yyyy_MM_dd").format(c.getTime()));
			String ciqFileNameWithTime = "";

			ciqFileNameWithTime = "4G_CIQ_" + new SimpleDateFormat("yyyy_MM_dd").format(currentDate) + "_"
					+ new SimpleDateFormat("yyyy_MM_dd").format(c.getTime()) + "_" + "0" + (count + 1) + ".xlsx";

			StringBuilder path = new StringBuilder();
			path.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR)
					.append(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId())
					.append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_CIQ
							.replace("filename", StringUtils.substringBeforeLast(ciqFileNameWithTime.toString(), "."))
							.replaceAll(" ", "_"))
					.append(Constants.SEPARATOR);
			String ciqFilePath = Constants.CUSTOMER + Constants.SEPARATOR
					+ listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId() + Constants.SEPARATOR
					+ Constants.PRE_MIGRATION_CIQ
							.replace("filename", StringUtils.substringBeforeLast(ciqFileNameWithTime.toString(), "."))
							.replaceAll(" ", "_");
			File ciqDirectory = new File(path.toString());
			if (!ciqDirectory.exists()) {
				ciqDirectory.mkdirs();
			}
			path = path.append(ciqFileNameWithTime);
			jsonobject.put("ciqFilePath", ciqFilePath);
			jsonobject.put("ciqFileName", ciqFileNameWithTime);
			jsonobject.put("listOfNonExistingEnbs", listOfNonExistingEnbs);
			

			FileOutputStream fileOut = new FileOutputStream(path.toString());
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR)
					.append(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId())
					.append(Constants.SEPARATOR);
			for (FetchOVResponseModel fetchScriptModel : listFetchOVResponseMdel) {
				StringBuilder scriptFilePath = new StringBuilder();
				StringBuilder scriptFileTempPath = new StringBuilder();
				objMap.put("port",
						Integer.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").toString()));
				ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId());
				programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
				programTemplateModel.setLabel(Constants.SCRIPT_FILE_PATH);
				List<ProgramTemplateEntity> entities = customerService.getProgTemplateDetails(programTemplateModel);
				String todayDateFolderName = DateUtil.dateToString(new Date(), Constants.MM_DD_YY);
				if (CommonUtil.isValidObject(entities) && entities.size() > 0
						&& CommonUtil.isValidObject(entities.get(0).getValue())
						&& entities.get(0).getValue().length() > 0) {
					String sourcePath = entities.get(0).getValue().replaceAll("date", todayDateFolderName);
					objMap.put("sourcePath", sourcePath);
				} else {
					deleteCiqDir(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(),
							ciqFileNameWithTime);
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
									+ Constants.SCRIPT_FILE_PATH);

				}

				programTemplateModel.setLabel(Constants.SCRIPT_NAME_TEMPLATE);
				entities = customerService.getProgTemplateDetails(programTemplateModel);
				if (CommonUtil.isValidObject(entities) && entities.size() > 0
						&& CommonUtil.isValidObject(entities.get(0).getValue())
						&& entities.get(0).getValue().length() > 0) {
					objMap.put("fileName", entities.get(0).getValue());
				} else {
					deleteCiqDir(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(),
							ciqFileNameWithTime);
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
									+ Constants.SCRIPT_NAME_TEMPLATE);

				}
				scriptFilePath.append(filePath)
						.append(Constants.PRE_MIGRATION_SCRIPT
								.replace("filename",
										StringUtils.substringBeforeLast(ciqFileNameWithTime.toString(), "."))
								.replaceAll(" ", "_"));

				scriptFileTempPath.append(scriptFilePath.toString()).append(Constants.TEMP);
				File dir = new File(scriptFileTempPath.toString());
				if (!dir.exists()) {
					FileUtil.createDirectory(scriptFileTempPath.toString());
				}
				objMap.put("destinationPath", scriptFilePath.toString());
				NetworkConfigModel networkConfigModel = new NetworkConfigModel();
				networkConfigModel.setProgramDetailsEntity(programDetailsEntity);
				Map<String, Object> objNetworkConfigMap = networkConfigService
						.getNetworkConfigDetails(networkConfigModel, 1, 10, null);
				List<NetworkConfigEntity> networkConfigList = (List<NetworkConfigEntity>) objNetworkConfigMap
						.get("networkConfigList");
				FetchDetailsModel fetchDetailsModel = new FetchDetailsModel();
				List<String> scriptList = new ArrayList<>();
				scriptList.addAll(fetchScriptModel.getNeidList());
				fetchDetailsModel.setRfScriptList(scriptList);
				String market = StringUtils.substringBeforeLast(fetchScriptModel.getMarket().toString(), "]");
				String marketValue = StringUtils.substringAfter(market, "[");
				CopyOnWriteArrayList<String> scriptsValidationList = new CopyOnWriteArrayList<String>(
						fetchDetailsModel.getRfScriptList());
				Map<String, Object> result = fileUploadService.fetchFileFromServer(networkConfigList.get(0), objMap,
						marketValue, fetchDetailsModel, "RF_SCRIPTS");

				jsonobject.put("scriptPath", scriptFilePath);
				List<String> fileList = (List<String>) result.get("fileList");
				if (commonUtil.isValidObject(fileList) && fileList.size() > 0) {
					for (String fileName : fileList) {
						File file = new File(scriptFilePath.toString() + fileName);
						if (file.exists()) {
							scriptFileNames = scriptFileNames + fileName + ",";

							FileInputStream input = new FileInputStream(file);
							MultipartFile multipartFile = new MockMultipartFile(
									scriptFileTempPath.toString() + "" + fileName, fileName, "text/plain",
									IOUtils.toByteArray(input));
							scriptFile = multipartFile;
							if (CommonUtil.isValidObject(multipartFile) && fileUploadService
									.uploadMultipartFile(multipartFile, scriptFileTempPath.toString())) {

								uploadCIQController.preMigrationFileProcess(multipartFile, scriptFilePath,
										scriptFileTempPath, true, "SCRIPT",
										listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(),
										ciqFileNameWithTime, Constants.USM_LIVE_4G);
								String fileExtension = FilenameUtils.getExtension(scriptFilePath.toString());
								List<String> zipExtensions = Arrays.asList("tar.gz", "tgz", "gz", "zip", "7z");
								List<String> txtExtensions = Arrays.asList("txt");
								long underScoreCharCount = scriptFile.getOriginalFilename().chars()
										.filter(num -> num == '_').count();
								if (zipExtensions.contains(fileExtension)) {
									String unzipDirPath = scriptFilePath.toString()
											.replace(scriptFile.getOriginalFilename(), "");
									String folderName = "";
									if (fileName.contains("_")) {
										folderName = StringUtils.substringBeforeLast(fileName, "_");
										if (CommonUtil.isValidObject(folderName) && folderName.contains("_")) {
											folderName = StringUtils.substringAfter(folderName, "_");
										} else {
											folderName = StringUtils.substringBefore(folderName, " ");
										}
										if (CommonUtil.isValidObject(folderName)) {
											unzipDirPath = StringUtils.substringBeforeLast(scriptFilePath.toString(),
													"/") + Constants.SEPARATOR + folderName;
										}
									} else {
										unzipDirPath = StringUtils.substringBeforeLast(scriptFilePath.toString(), "/")
												+ Constants.SEPARATOR
												+ FilenameUtils.removeExtension(scriptFile.getOriginalFilename());
									}
									logger.info("fetchPreMigrationFiles() folderName:" + folderName + ", unzipDirPath"
											+ unzipDirPath);
									File unzipDir = new File(unzipDirPath);
									if (!unzipDir.exists()) {
										FileUtil.createDirectory(unzipDirPath);
									}

									boolean unzipStatus = fileUploadService.unzipFile(scriptFilePath.toString(),
											unzipDirPath);
									if (!unzipStatus) {
										deleteCiqDir(listFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(),
												ciqFileNameWithTime);

									}
								} else if (underScoreCharCount >= 3 && txtExtensions.contains(fileExtension)) {
									String fileMoveDirPath = scriptFilePath.toString()
											.replace(scriptFile.getOriginalFilename(), "");
									String folderName = "";
									if (scriptFile.getOriginalFilename().contains("_")) {
										folderName = scriptFile.getOriginalFilename().toString().substring(
												StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(),
														"_", 2) + 1,
												StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(),
														"_", 3));
										if (CommonUtil.isValidObject(folderName)) {
											fileMoveDirPath = StringUtils.substringBeforeLast(scriptFilePath.toString(),
													"/") + Constants.SEPARATOR + folderName;
										}
										logger.info("uploadCIQFile() folderName:" + folderName + ", fileMoveDirPath"
												+ fileMoveDirPath);
										File fileMoveDir = new File(fileMoveDirPath);
										if (!fileMoveDir.exists()) {
											FileUtil.createDirectory(fileMoveDirPath);
										}
										FileUtils.copyFileToDirectory(new File(scriptFilePath.toString()), fileMoveDir);
										// FileUtil.deleteFileOrFolder(scriptFilePath.toString());
									}
								}
							}
						}
						String temp = scriptFilePath.toString().replaceAll(fileName, "");
						scriptFilePath = new StringBuilder();
						scriptFilePath.append(temp);
						String removeExtentionFileName = FilenameUtils.removeExtension(fileName);
						if (!ObjectUtils.isEmpty(scriptsValidationList)
								&& scriptsValidationList.contains(removeExtentionFileName)) {
							scriptsValidationList.remove(removeExtentionFileName);
						}

					}
					jsonobject.put("scriptFiles", scriptFileNames.substring(0, scriptFileNames.length() - 1));
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return jsonobject;
	}

	public String integrationDateValidation(String integrationDate) {

		String status = "";
		try {
			Date todayDate = DateUtil.stringToDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD),
					Constants.YYYY_MM_DD);
			Date commssionDate = DateUtil.stringToDate(integrationDate, Constants.YYYY_MM_DD);
			long diff = commssionDate.getTime() - todayDate.getTime();
			long daysDifference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if (daysDifference <= 5) {
				status = Constants.SUCCESS;
			} else {
				status = Constants.FAIL;
			}

		} catch (Exception e) {
			status = Constants.FAIL;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public ConcurrentHashMap<String, OvCiqDetailsModel> fetch5GDetails(
			List<FetchOVResponseModel> list5gFetchOVResponseMdel, String fetchType, String remarks) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = new ConcurrentHashMap<>();
		try {
			JSONObject objFetchRequest = new JSONObject();
			List<String> market = list5gFetchOVResponseMdel.stream()
					.filter(model -> !ObjectUtils.isEmpty(model.getMarket()))
					.flatMap(model -> model.getMarket().stream().distinct()).distinct().collect(Collectors.toList());
			List<String> rfScriptList = list5gFetchOVResponseMdel.stream()
					.filter(model -> !ObjectUtils.isEmpty(model.getNeidList()))
					.flatMap(model -> model.getNeidList().stream().distinct()).distinct().collect(Collectors.toList());
			List<NetworkConfigEntity> networkConfigList = fetchProcessRepository
					.getNetworkConfigDetailsForOv(list5gFetchOVResponseMdel.get(0));
			int ciqNetworkConfigId = 0;
			int scriptNetworkConfigId = 0;
			if (!ObjectUtils.isEmpty(networkConfigList)) {

				for (NetworkConfigEntity locNetworkConfigEntity : networkConfigList) {
					if (Constants.FETCH_NE.equalsIgnoreCase(locNetworkConfigEntity.getNeTypeEntity().getNeType())) {
						ciqNetworkConfigId = locNetworkConfigEntity.getId();
					}
					if (Constants.SCRIPT_NE.equalsIgnoreCase(locNetworkConfigEntity.getNeTypeEntity().getNeType())) {
						scriptNetworkConfigId = locNetworkConfigEntity.getId();
					}
				}
			}
			if (ciqNetworkConfigId > 0 && scriptNetworkConfigId > 0) {
				objFetchRequest.put("ciqNetworkConfigId", ciqNetworkConfigId);
				objFetchRequest.put("scriptNetworkConfigId", scriptNetworkConfigId);
			}
			JSONObject loginDetails = new JSONObject();
			UserDetailsEntity userLogin = userDetailsRepository.getUserByRole("vzwadmin");
			if (!ObjectUtils.isEmpty(userLogin)) {
				loginDetails.put("username", userLogin.getUserName());
				loginDetails.put("password", PasswordCrypt.decrypt(userLogin.getPassword()));
			}
			User loginUser = dailyOvScheduleConfig.Ovlogin(loginDetails);

			if (!ObjectUtils.isEmpty(loginUser)) {
				objFetchRequest.put("sessionId", loginUser.getTokenKey());
				objFetchRequest.put("serviceToken", loginUser.getServiceToken());
			}
			objFetchRequest.put("market", market);
			objFetchRequest.put("rfScriptList", rfScriptList);
			objFetchRequest.put("fileSourceType", fetchType);
			objFetchRequest.put("customerName", "");
			objFetchRequest.put("customerId",
					list5gFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getCustomerEntity().getId());
			objFetchRequest.put("programName",
					list5gFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getProgramName());
			objFetchRequest.put("programId", list5gFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId());
			objFetchRequest.put("activate", false);
			objFetchRequest.put("remarks", remarks);
			objFetchRequest.put("allowDuplicate", false);
			if (Constants.DSS_5G.equalsIgnoreCase(list5gFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getProgramName())) {
				List<String> neIdList = getDssNeids(rfScriptList);
				objFetchRequest.put("rfScriptList", neIdList);
			}//else if (Constants.CBAND_5G.equalsIgnoreCase(list5gFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getProgramName())) {
//				List<String> neIdList = getDssNeids(rfScriptList);
//				objFetchRequest.put("rfScriptList", neIdList);
//			}

			JSONObject objresultJson = uploadCIQController.fetchPreMigrationFiles(objFetchRequest);
			if (!ObjectUtils.isEmpty(objresultJson) && objresultJson.containsKey("ovAuditList")
					&& !ObjectUtils.isEmpty(objresultJson.get("ovAuditList"))) {
				List<CiqUploadAuditTrailDetEntity> ciqDetails = (List<CiqUploadAuditTrailDetEntity>) objresultJson
						.get("ovAuditList");
				// Checking whether ID is present in CIQ and returning back the details

				for (CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity : ciqDetails) {
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ciqUploadAuditTrailDetEntity.getProgramDetailsEntity().getId()),
							ciqUploadAuditTrailDetEntity.getCiqFileName());
					List<EnodebDetails> gnbDetails = fetchProcessRepository
							.getEnbDetails(ciqUploadAuditTrailDetEntity.getCiqFileName(), dbcollectionFileName);
					if (!ObjectUtils.isEmpty(gnbDetails)) {
						for (EnodebDetails objDetails : gnbDetails) {
							if (rfScriptList.contains(objDetails.geteNBId())) {
								OvCiqDetailsModel ovCiqDetailsModel = new OvCiqDetailsModel();
								ovCiqDetailsModel.setCiqName(ciqUploadAuditTrailDetEntity.getCiqFileName());
								ovCiqDetailsModel.setCiqPath(ciqUploadAuditTrailDetEntity.getCiqFilePath());
								ovCiqDetailsModel.setGnbId(objDetails.geteNBId());
								ciqMapDetails.put(objDetails.geteNBId(), ovCiqDetailsModel);
							}
						}
					}

				}
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ciqMapDetails;
	}
	
	
	private OvScheduledEntity getFailureOvScheduledEntityDetails(FetchOVResponseModel objFetchOVResponseMdel, String neId,
			OvScheduledEntity persistOvScheduledEntity, String ciqName, String ciqPath) {

		try {
			persistOvScheduledEntity.setFetchDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
			persistOvScheduledEntity.setCiqName(ciqName);
			persistOvScheduledEntity.setCiqFilePath(ciqPath);
			persistOvScheduledEntity.setNeId(neId);
			persistOvScheduledEntity.setSiteName(objFetchOVResponseMdel.getSiteName());
			persistOvScheduledEntity.setFetchRemarks(Constants.CIQ_RFDB_FAILED);
			persistOvScheduledEntity.setTrackerId(objFetchOVResponseMdel.getTrackerId());
			persistOvScheduledEntity.setWorkPlanID(objFetchOVResponseMdel.getWorkPlanId());

			

			persistOvScheduledEntity.setPreMigStatus(Constants.CANCELED);
			persistOvScheduledEntity.setEnvStatus(Constants.CANCELED);
			persistOvScheduledEntity.setPreMigGrowStatus(Constants.CANCELED);
			persistOvScheduledEntity.setNeGrowStatus(Constants.CANCELED);
			persistOvScheduledEntity.setMigStatus(Constants.CANCELED);
			persistOvScheduledEntity.setPostMigAuditStatus(Constants.CANCELED);
			persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.CANCELED);
			persistOvScheduledEntity.setCustomerDetailsEntity(objFetchOVResponseMdel.getCustomerDetailsEntity());

			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();

			programTemplateModel.setProgramDetailsEntity(objFetchOVResponseMdel.getCustomerDetailsEntity());
			programTemplateModel.setConfigType("s&r");

			List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
					.getScheduledDaysDetails(programTemplateModel);

			// premigrationscheduled days
			String preMigraSchedule = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.PREMIGRATION_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
			/*
			 * String envExportSchedule = listProgramTemplateEntity.stream() .filter(entity
			 * -> Constants.ENV_EXPORT_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
			 * .map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
			 */
			String neGrowSchedule = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.NE_GROW_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
			String migraSchedule = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.MIGRATION_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);
			String postMigrationAuditSchedule = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.POST_MIGRATION_AUDIT_SCHEDULE.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);

			String postMigrationRanAtpSchedule = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.POST_MIGRATION_RANATP_SCHEDULE
							.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().map(String::new).orElse(null);

			if (StringUtils.isNotEmpty(preMigraSchedule)) {
				if (preMigraSchedule.contains(",")) {
					String[] datesDetails = preMigraSchedule.split(",");
					Date shedule1 = getScheduledDate(datesDetails[0],
							objFetchOVResponseMdel.getCommissionDate());
					Date shedule2 = getScheduledDate(datesDetails[1],
							objFetchOVResponseMdel.getCommissionDate());
					if (shedule1.after(shedule2)) {
						persistOvScheduledEntity.setPremigrationScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPremigrationReScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
						persistOvScheduledEntity.setEnvStatus(Constants.SCHEDULED);
//						persistOvScheduledEntity
//								.setEnvGenerationDate(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPreMigGrowStatus(Constants.SCHEDULED);
//						persistOvScheduledEntity.setPreMigGrowGenerationDate(
//								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

					} else if (shedule1.before(shedule2)) {

						persistOvScheduledEntity.setPremigrationScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPremigrationReScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						
					}
				} else if ("OFF".equalsIgnoreCase(preMigraSchedule)) {
					persistOvScheduledEntity.setPremigrationScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);
					

				} else {
					Date shedule1 = getScheduledDate(preMigraSchedule,
							objFetchOVResponseMdel.getCommissionDate());
					persistOvScheduledEntity.setPremigrationScheduledTime(
							DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
				
				}
			}

		

			if (StringUtils.isNotEmpty(neGrowSchedule)) {
				if (neGrowSchedule.contains(",")) {
					String[] datesDetails = neGrowSchedule.split(",");
					Date shedule1 = getScheduledDate(datesDetails[0],
							objFetchOVResponseMdel.getCommissionDate());
					Date shedule2 = getScheduledDate(datesDetails[1],
							objFetchOVResponseMdel.getCommissionDate());
					if (shedule1.after(shedule2)) {
						persistOvScheduledEntity
								.setNeGrowScheduledTime(DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setNeGrowReScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

					} else if (shedule1.before(shedule2)) {

						persistOvScheduledEntity
								.setNeGrowScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setNeGrowReScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
					}
				} else if ("OFF".equalsIgnoreCase(neGrowSchedule)) {
					persistOvScheduledEntity.setNeGrowScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);

				} else {
					Date shedule1 = getScheduledDate(neGrowSchedule,
							objFetchOVResponseMdel.getCommissionDate());
					persistOvScheduledEntity
							.setNeGrowScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
				}
			}

			if (StringUtils.isNotEmpty(migraSchedule)) {
				if (migraSchedule.contains(",")) {
					String[] datesDetails = migraSchedule.split(",");
					Date shedule1 = getScheduledDate(datesDetails[0],
							objFetchOVResponseMdel.getCommissionDate());
					Date shedule2 = getScheduledDate(datesDetails[1],
							objFetchOVResponseMdel.getCommissionDate());
					if (shedule1.after(shedule2)) {
						persistOvScheduledEntity.setMigrationScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setMigrationReScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

					} else if (shedule1.before(shedule2)) {

						persistOvScheduledEntity.setMigrationScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setMigrationReScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
					}
				} else if ("OFF".equalsIgnoreCase(migraSchedule)) {
					persistOvScheduledEntity.setMigrationScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);

				} else {
					Date shedule1 = getScheduledDate(migraSchedule, objFetchOVResponseMdel.getCommissionDate());
					persistOvScheduledEntity
							.setMigrationScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
				}
			}

			if (StringUtils.isNotEmpty(postMigrationAuditSchedule)) {
				if (postMigrationAuditSchedule.contains(",")) {
					String[] datesDetails = postMigrationAuditSchedule.split(",");
					Date shedule1 = getScheduledDate(datesDetails[0],
							objFetchOVResponseMdel.getCommissionDate());
					Date shedule2 = getScheduledDate(datesDetails[1],
							objFetchOVResponseMdel.getCommissionDate());
					if (shedule1.after(shedule2)) {
						persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPostmigrationAuditReScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

					} else if (shedule1.before(shedule2)) {

						persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setPostmigrationAuditReScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
					}
				} else if ("OFF".equalsIgnoreCase(postMigrationAuditSchedule)) {
					persistOvScheduledEntity
							.setPostmigrationAuditScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);

				} else {
					Date shedule1 = getScheduledDate(postMigrationAuditSchedule,
							objFetchOVResponseMdel.getCommissionDate());
					persistOvScheduledEntity.setPostmigrationAuditScheduledTime(
							DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
				}
			}

			if (StringUtils.isNotEmpty(postMigrationRanAtpSchedule)) {
				if (postMigrationRanAtpSchedule.contains(",")) {
					String[] datesDetails = postMigrationRanAtpSchedule.split(",");
					Date shedule1 = getScheduledDate(datesDetails[0],
							objFetchOVResponseMdel.getCommissionDate());
					Date shedule2 = getScheduledDate(datesDetails[1],
							objFetchOVResponseMdel.getCommissionDate());
					if (shedule1.after(shedule2)) {
						persistOvScheduledEntity
								.setRanAtpScheduledTime(DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setRanAtpReScheduledTime(
								DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));

					} else if (shedule1.before(shedule2)) {

						persistOvScheduledEntity
								.setRanAtpScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
						persistOvScheduledEntity.setRanAtpReScheduledTime(
								DateUtil.dateToString(shedule2, Constants.YYYY_MM_DD));
					}
				} else if ("OFF".equalsIgnoreCase(postMigrationRanAtpSchedule)) {
					persistOvScheduledEntity.setRanAtpScheduledTime(Constants.OV_SCHEDULED_TIME_OFF);

				} else {
					Date shedule1 = getScheduledDate(postMigrationRanAtpSchedule,
							objFetchOVResponseMdel.getCommissionDate());
					persistOvScheduledEntity
							.setRanAtpScheduledTime(DateUtil.dateToString(shedule1, Constants.YYYY_MM_DD));
				}
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return persistOvScheduledEntity;
	}
	
	
	public List<String> getDssNeids(List<String> dssIdsList)
    {
    		List<String> dataList = new ArrayList<>();
    		String neId = null;
    		try {
			if (!ObjectUtils.isEmpty(dssIdsList))
			{
				for(String dssId:dssIdsList)
				{
					if(StringUtils.isNotEmpty(dssId))
					{

						String enbIdValue = dssId.trim().replaceAll("^0+(?!$)", "");
						if (dssId.length() == 10) {
							neId = enbIdValue.substring(2, enbIdValue.length());
						} else
							neId = enbIdValue.substring(3, enbIdValue.length());
						dataList.add(neId);
					}
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return dataList;
	}
}
