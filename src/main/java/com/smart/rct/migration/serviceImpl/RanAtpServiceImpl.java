package com.smart.rct.migration.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseCmdRuleEntity;
import com.smart.rct.migration.entity.UseCaseFileRuleEntity;
import com.smart.rct.migration.entity.UseCaseShellRuleEntity;
import com.smart.rct.migration.entity.UseCaseXmlRuleEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.repository.RunTestResultRepository;
import com.smart.rct.migration.repository.UploadFileRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.service.RanAtpService;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.postmigration.service.ReportsService;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.CIQUploadRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PasswordCrypt;

@Service
public class RanAtpServiceImpl implements RanAtpService {

	private static final Logger logger = LoggerFactory.getLogger(RanAtpServiceImpl.class);

	@Autowired
	RunTestRepository runTestRepository;

	@Autowired
	RunTestServiceImpl runTestServiceImpl;

	@Autowired
	RunTestResultRepository runTestResultRepository;

	@Autowired
	CIQUploadRepository ciqUploadRepository;

	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;

	@Autowired
	UseCaseBuilderService useCaseBuilderService;

	@Autowired
	UploadFileRepository uploadFileRepository;

	@Autowired
	public UploadFileServiceImpl uploadFileServiceImpl;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	
	@Autowired
	ReportsService reportService;

	@SuppressWarnings("null")
	@Override
	public JSONObject getSaneDetailsforPassword(JSONObject runTestParams) {

		Map run = (Map) runTestParams.get("runTestFormDetails");
		int lsmId = Integer.parseInt(run.get("lsmId").toString());
		boolean useCurrPassword = (boolean) run.get("currentPassword");

		NetworkConfigEntity networkConfigEntity = runTestRepository.getNeType(lsmId);

		JSONObject result = new JSONObject();

		List<NetworkConfigDetailsEntity> neDetailsLst = networkConfigEntity.getNeDetails();
		for (NetworkConfigDetailsEntity neDetails : neDetailsLst) {
			if (useCurrPassword == false && neDetails.getServerTypeEntity().getServerType().equalsIgnoreCase("SANE")) {

				String serverName = neDetails.getServerName();
				String serverIP = neDetails.getServerIp();
				result.put("serverName", serverName);
				result.put("serverIp", serverIP);
				return result;

			}
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, RunTestEntity> insertRunTestDetails(JSONObject runTestParams, String perlPath) {
		Date creationDate = new Date();
		Map<String, RunTestEntity> runTestEntityMap = new HashMap<>();
		try {

			@SuppressWarnings("unchecked")

			String serviceToken = runTestParams.get("serviceToken").toString();
			Map run = (Map) runTestParams.get("runTestFormDetails");
			List<JSONObject> useCaseList = (List) run.get("useCase");
			List<Map> neList = (List<Map>) run.get("neDetails");
			String userName = runTestParams.get("userName").toString();

			String testname = run.get("testname").toString();
			String testDesc = run.get("testDesc").toString();
			String lsmVersion = run.get("lsmVersion").toString();
			String lsmName = run.get("lsmName").toString();
			String ciqName = run.get("ciqName").toString();
			String checklistFileName = run.get("checklistFileName").toString();
			int customerId = Integer.parseInt(runTestParams.get("customerId").toString());
			String usecase = "";
			String useCaseDetails = "";
			int count = 0;
			int programId = Integer.parseInt(runTestParams.get("programId").toString());
			String migrationType = runTestParams.get("migrationType").toString();
			String migrationSubType = runTestParams.get("migrationSubType").toString();

			if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
				migrationType = "PostMigration";
			}

			if ("RANATP".equalsIgnoreCase(migrationSubType)) {
				migrationSubType = "RanATP";
			}

			for (JSONObject ucase : useCaseList) {
				if (useCaseList.size() == count) {
					String uName = ucase.get("useCaseName").toString();
					usecase = usecase + uName;
					String uId = ucase.get("useCaseId").toString();
					String uSeq = ucase.get("executionSequence").toString();
					useCaseDetails = useCaseDetails + uName + "?" + uId + "?" + uSeq;
				} else {
					String uName = ucase.get("useCaseName").toString();
					usecase = usecase + uName + ",";
					String uId = ucase.get("useCaseId").toString();
					String uSeq = ucase.get("executionSequence").toString();
					useCaseDetails = useCaseDetails + uName + "?" + uId + "?" + uSeq + ",";
				}

			}

			CustomerDetailsEntity customerDetailsEntity = runTestServiceImpl.getCustomerDetailsEntity(programId);
			usecase = usecase.substring(0, usecase.length() - 1);

			StringBuilder comFilePath = new StringBuilder();
			comFilePath = comFilePath.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);

			for (Map neid : neList) {

				String neName = neid.get("neName").toString();
				String neId = neid.get("neId").toString();

				StringBuilder outputFilePath = comFilePath
						.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
								.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
								.replace("subtype", migrationSubType).replaceAll(" ", "_"));

				File outFilePath = new File(outputFilePath.toString());
				outFilePath.mkdirs();

				String outputFileName = outputFilePath + serviceToken + "_" + "output.txt";

				RunTestEntity runTestEntity = new RunTestEntity();
				runTestEntity.setCreationDate(creationDate);
				runTestEntity.setCustomerId(customerId);
				runTestEntity.setLsmName(lsmName);
				runTestEntity.setLsmVersion(lsmVersion);
				runTestEntity.setCiqName(ciqName);
				runTestEntity.setChecklistFileName(checklistFileName);
				runTestEntity.setTestName(testname);
				runTestEntity.setTestDescription(testDesc);
				runTestEntity.setMigrationType(migrationType);
				runTestEntity.setMigrationSubType(migrationSubType);
				runTestEntity.setUseCase(usecase);
				runTestEntity.setCustomerDetailsEntity(customerDetailsEntity);
				runTestEntity.setStatus("InProgress");
				runTestEntity.setNeName(neName);
				runTestEntity.setOutputFilepath(outputFileName);
				runTestEntity.setProgressStatus("InProgress");

				runTestEntity.setUseCaseDetails(useCaseDetails);
				runTestEntity.setResult(perlPath);
				runTestEntity.setUserName(userName);
				runTestEntityMap.put(neId, runTestRepository.createRunTest(runTestEntity));
			}

		}

		catch (Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in insertRunTestDetails() " + ExceptionUtils.getFullStackTrace(e));
		}
		return runTestEntityMap;

	}

	public int getScriptExeSeq(List<Map> scriptSeqDetails, int scriptId) {

		int scriptExecution = 1;
		try {
			if (scriptSeqDetails != null) {
				for (Map scriptInfo : scriptSeqDetails) {

					int mapScriptId = Integer.parseInt(scriptInfo.get("scriptId").toString());

					if (mapScriptId == scriptId) {
						scriptExecution = Integer.parseInt(scriptInfo.get("scriptExeSequence").toString());

					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getScriptExeSeq() " + ExceptionUtils.getFullStackTrace(e));
		}
		return scriptExecution;

	}

	public int getUCSleepInterval(List<LinkedHashMap> ucDetails, int scriptId) {

		int sleepInterval = 0;
		try {
			if (ucDetails != null) {
				for (Map ucInfo : ucDetails) {

					int mapScriptId = Integer.parseInt(ucInfo.get("useCaseId").toString());

					if (mapScriptId == scriptId) {

						String tmpSleepTime = ucInfo.get("ucSleepInterval").toString();
						if (!tmpSleepTime.isEmpty()) {
							sleepInterval = Integer.parseInt(tmpSleepTime);
						} else {
							sleepInterval = 0;
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getUCSleepInterval() " + ExceptionUtils.getFullStackTrace(e));
		}
		return sleepInterval;

	}

	@Override
	public String getRuntestExecResult(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntityMap,
			String runType, String xlsOutputFileName, String opsAtpFileContent, String originalFileName)
			throws RctException {

		Map run = (Map) runTestParams.get("runTestFormDetails");
		String testname = run.get("testname").toString();
		List<Map> scriptSeqDetails = (List<Map>) run.get("scripts");
		List<Map> neList = (List<Map>) run.get("neDetails");
		List<LinkedHashMap> useCaseList = (List) run.get("useCase");
		int programId = Integer.parseInt(runTestParams.get("programId").toString());
		boolean useCurrPassword = (boolean) run.get("currentPassword");
		String serviceToken = runTestParams.get("serviceToken").toString();
		String sanePassword = run.get("password").toString();
		String migType = runTestParams.get("migrationType").toString();
		String migSubType = runTestParams.get("migrationSubType").toString();
		String ciqName = run.get("ciqName").toString();
		ciqName = String.valueOf(programId) + "_" + ciqName;
		String userName = runTestParams.get("userName").toString();

		List<String> bandName = (List) run.get("bandName");

		String checklistFileName = run.get("checklistFileName").toString();
		String ciqFileName = run.get("ciqName").toString();
		String sProgramId = runTestParams.get("programId").toString();
		String remarks = run.get("testDesc").toString();

		String sessionId = runTestParams.get("sessionId").toString();

		CustomerDetailsEntity customerDetailsEntity = runTestServiceImpl.getCustomerDetailsEntity(programId);

		try {
			ExecutorService executorservice = Executors.newFixedThreadPool(neList.size());
			for (Map usecase : useCaseList) {
				Integer useCaseId = Integer.valueOf(usecase.get("useCaseId").toString());
				runTestRepository.updateUseCountForUseCase(useCaseId, Constants.USECOUNT_INCREMENT, neList.size());
			}
			for (Map neid : neList) {

				executorservice.submit(() -> {

					StringBuilder ranatpXlsFilePath = new StringBuilder();
					StringBuilder ranatpXlsFileName = new StringBuilder();
					String neId = neid.get("neId").toString();
					String neName = neid.get("neName").toString();
					String lsmName ="";
					String lsmVersion ="";
					int lsmId=0;
					NeMappingModel neMappingModelversion = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
					programDetailsEntityversion.setId(programId);
					neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
					neMappingModelversion.setEnbId(neId);
					NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
							.getNetWorkEntityDetails(neMappingModelversion);
					if(neMappingEntitiesForVersion!=null) {
						lsmVersion=neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
					lsmId=neMappingEntitiesForVersion.getId();
					}
					if(run.get("lsmName").toString().isEmpty()) {
						lsmName=neMappingEntitiesForVersion.getNeName();
					}else {
						lsmName=run.get("lsmName").toString();
						lsmVersion=run.get("lsmVersion").toString();
						NetworkConfigEntity neConfigEntity= runTestRepository
								.getNEConfigEntity(lsmVersion,lsmName,programDetailsEntityversion);
						lsmId=neConfigEntity.getId();
					}
					NetworkConfigEntity networkConfigEntity = runTestRepository.getNeType(lsmId);
					NetworkConfigEntity neEntity = uploadFileServiceImpl.getNeEntity(lsmVersion, lsmName, programId);

					String migrationType = null;
					String migrationSubType = null;

					if (migType.equalsIgnoreCase(Constants.MIGRATION)) {
						migrationType = "Migration";
					} else if (migType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
						migrationType = "PostMigration";
					}

					if ("precheck".equalsIgnoreCase(migSubType)) {
						migrationSubType = "PreCheck";
					} else if ("commission".equalsIgnoreCase(migSubType)) {
						migrationSubType = "Commission";
					} else if ("postcheck".equalsIgnoreCase(migSubType)) {
						migrationSubType = "PostCheck";
					} else if ("AUDIT".equalsIgnoreCase(migSubType)) {
						migrationSubType = "Audit";
					} else if ("RANATP".equalsIgnoreCase(migSubType)) {
						migrationSubType = "RanATP";
					}

					List<String> bandNameList = runTestServiceImpl.getBandColumnValuesBySheet(neId, ciqFileName,
							sProgramId, bandName);
					StringBuilder bandNameStr = new StringBuilder();

					for (String singleBandName : bandNameList) {
						/*
						 * if(singleBandName.contains("AWS-3")) { singleBandName = "AWS3"; }
						 * if(singleBandName.contains("CBRS")) { singleBandName = "CBRS"; }
						 * if(singleBandName.contains("LAA")) { singleBandName = "LAA"; }
						 */
						bandNameStr.append(singleBandName).append(",");

					}
					logger.info("band name representation"+bandNameStr.toString());
					logger.error("band name representation"+bandNameStr.toString());
					String generateScriptPath = "";

					try {

						// Input file

						StringBuilder destPath = new StringBuilder();
						destPath = destPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"));

						destPath.append(Constants.CUSTOMER)
								.append(Constants.RAN_ATP_INPUT.replace("programId", Integer.toString(programId))
										.replace("migrationType", migrationType).replace("neId", neId)
										.replace("subType", migrationSubType));

						File inputfolder = new File(destPath.toString());
						if (!inputfolder.exists()) {
							FileUtil.createDirectory(destPath.toString());
						}
						String descFilePath = null;
						if (opsAtpFileContent != null) {
							String descFileName = serviceToken + "_" + originalFileName;
							descFilePath = destPath.toString() + "/" + descFileName;
							File uploadfile = new File(descFilePath);
							if (!uploadfile.exists()) {
								uploadfile.createNewFile();
							}

							FileWriter fw = new FileWriter(descFilePath);
							fw.write(opsAtpFileContent);
							fw.close();
						}

						logger.info("RanAtpServiceImpl.getRuntestExecResult() descFilePath: " + descFilePath);

						// generate script path
						StringBuilder generatedPath = new StringBuilder();
						generatedPath = generatedPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"));
						generatedPath.append(Constants.CUSTOMER)
								.append(Constants.GENERATE_SCRIPT.replace("programId", sProgramId)
										.replace("migrationType", migrationType).replace("neId", neId)
										.replace("subType", migrationSubType));

						String finalStatusResult = "Success";
						int iUseCaseId = 0;
						Map<String, String> generateScriptMap = new HashMap<>();
						Map<String, String> scriptNameMap = new HashMap<>();
						if (scriptSeqDetails != null) {

							for (Map scriptInfoDetails : scriptSeqDetails) {
								String useGeneratedScriptsCheck = scriptInfoDetails.get("useGeneratedScript")
										.toString();
								String sScriptName = scriptInfoDetails.get("scriptName").toString();
								String useCaseNameInScript = scriptInfoDetails.get("useCaseName").toString();
								generateScriptMap.put(useCaseNameInScript + "_" + sScriptName,
										useGeneratedScriptsCheck);
								scriptNameMap.put(useCaseNameInScript + "_" + sScriptName, sScriptName);
							}
						}

						ArrayList scriptFinalLst = null;

						// Use case loop
						TreeSet<String> finalListFile = new TreeSet<>();
						scriptFinalLst = new ArrayList<ArrayList>();

						for (Map usecase : useCaseList) {
							Integer useCaseExeSeq = Integer.valueOf(usecase.get("executionSequence").toString());
							String useCaseName = (String) usecase.get("useCaseName");
							iUseCaseId = Integer.valueOf(usecase.get("useCaseId").toString());
							List<UseCaseBuilderParamEntity> scriptDetails = runTestRepository
									.getScriptDetails(iUseCaseId);

							Map<Integer, Integer> scriptIdAndSeqmap = new HashMap<>();
							for (UseCaseBuilderParamEntity scriptInfo : scriptDetails) {
								List scriptLst = new ArrayList<>();
								Integer scriptId = scriptInfo.getScriptsDetails().getId();

								int scriptUiExeSeq = getScriptExeSeq(scriptSeqDetails, scriptId);

								Integer scriptExeSeq = scriptUiExeSeq;

								Integer uploadedscriptId = scriptInfo.getId();
								if (scriptExeSeq != 0) {

									scriptLst.add(scriptId);
									scriptLst.add(scriptExeSeq);
									scriptLst.add(uploadedscriptId);
									scriptFinalLst.add(scriptLst);
									scriptIdAndSeqmap.put(scriptId, scriptExeSeq);

								}
							}

							// Script loop
							for (UseCaseBuilderParamEntity scriptEntity : scriptDetails) {
								int scriptId = scriptEntity.getScriptsDetails().getId();

								int scriptExeSeq = scriptIdAndSeqmap.get(scriptId);

								// int scriptExeSeq = scriptEntity.getExecutionSequence();

								UploadFileEntity uploadFileEntity = runTestRepository.getScriptInfo(scriptId);

								String dbcollectionFileName = CommonUtil
										.createMongoDbFileName(String.valueOf(programId), ciqFileName);
								String vznEnbIP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
										Constants.VZ_GROW_IPPLAN, neId, Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP);
								String eNB_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
										Constants.SPT_GROW_SHEET_FDD_TDD, neId,
										Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);
								String enbIP = "127.0.0.1";

								if (StringUtils.isNotEmpty(vznEnbIP)) {
									enbIP = vznEnbIP;
								} else if (StringUtils.isNotEmpty(eNB_OAM_IP)) {
									String sprintEnbIP = CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3);
									if (StringUtils.isNotEmpty(sprintEnbIP)) {
										enbIP = sprintEnbIP;
									} else {
										enbIP = eNB_OAM_IP;
									}
								}
								String neType = Constants.NE_TYPE_ENB;
								String useGeneratedScriptsCheck = generateScriptMap
										.get(useCaseName + "_" + uploadFileEntity.getFileName());

								String[] scriptNameSplit = uploadFileEntity.getFileName().split("\\.");
								String checkLsmName = null;
								if (useGeneratedScriptsCheck != null
										&& "YES".equalsIgnoreCase(useGeneratedScriptsCheck.trim())) {
									File folder = new File(generatedPath.toString());
									if (!folder.exists()) {
										FileUtil.createDirectory(generatedPath.toString());
									}

									File[] listOfFiles = folder.listFiles();
									ArrayList<String> listOfFile = new ArrayList<>();
									if (listOfFiles.length > 0) {
										for (File file : listOfFiles) {
											listOfFile.add(file.getName());
										}

										String sLsmName = lsmName;
										String sUseCaseName = useCaseName;

										if (sLsmName.contains("_")) {
											sLsmName = runTestServiceImpl.charRemoveAt(sLsmName);
										}

										if (sUseCaseName.contains("_")) {
											sUseCaseName = runTestServiceImpl.charRemoveAt(sUseCaseName);
										}

										if (scriptNameSplit[0].contains("_")) {
											scriptNameSplit[0] = runTestServiceImpl.charRemoveAt(scriptNameSplit[0]);
										}

										sLsmName = sLsmName.replaceAll("\\s", "");
										sUseCaseName = sUseCaseName.replaceAll("\\s", "");
										scriptNameSplit[0] = scriptNameSplit[0].replaceAll("\\s", "");
										useCaseName = useCaseName.trim().replaceAll("\\s", "_");

										String filePathwithName = scriptExeSeq + "_" + sLsmName + "_" + sUseCaseName
												+ "_" + iUseCaseId + "_" + scriptNameSplit[0] + "_" + scriptId + "."
												+ scriptNameSplit[1];
										if (listOfFile.contains(filePathwithName)) {
											// do nothing
										} else {
											runTestServiceImpl.generateScriptWithOutExe(neId, neName, sProgramId,
													migrationType, migrationSubType, uploadFileEntity, enbIP, neEntity,
													useCaseName, sessionId, lsmName, iUseCaseId, useCaseExeSeq,
													scriptId, scriptExeSeq, lsmId, useCurrPassword, sanePassword,
													userName, lsmVersion, ciqFileName, neType, false);
										}

									} else {
										runTestServiceImpl.generateScriptWithOutExe(neId, neName, sProgramId,
												migrationType, migrationSubType, uploadFileEntity, enbIP, neEntity,
												useCaseName, sessionId, lsmName, iUseCaseId, useCaseExeSeq, scriptId,
												scriptExeSeq, lsmId, useCurrPassword, sanePassword, userName,
												lsmVersion, ciqFileName, neType, false);
									}

								} else {
									runTestServiceImpl.generateScriptWithOutExe(neId, neName, sProgramId, migrationType,
											migrationSubType, uploadFileEntity, enbIP, neEntity, useCaseName, sessionId,
											lsmName, iUseCaseId, useCaseExeSeq, scriptId, scriptExeSeq, lsmId,
											useCurrPassword, sanePassword, userName, lsmVersion, ciqFileName, neType,
											false);
								}

								checkLsmName = lsmName;

								if (checkLsmName.contains("_")) {
									checkLsmName = runTestServiceImpl.charRemoveAt(checkLsmName);
								}

								checkLsmName = checkLsmName.replaceAll("\\s", "");

								File folder = new File(generatedPath.toString());
								File[] listOfFiles = folder.listFiles();

								if (listOfFiles.length > 0) {
									for (File file : listOfFiles) {
										String fileName = file.getName();
										if (fileName.contains(checkLsmName)
												&& fileName.contains(Integer.toString(iUseCaseId))
												&& fileName.contains(Integer.toString(scriptId))) {
											finalListFile.add(fileName);
										} else {
											// do nothing
										}
									}
								}
							}
						}

						Map<Integer, String> scriptExehashMap = new HashMap<>();

						for (String fileName : finalListFile) {

							String[] scriptExeSeq = fileName.split("_");
							int iScriptExeSeq = Integer.parseInt(scriptExeSeq[0]);

							scriptExehashMap.put(iScriptExeSeq, fileName);

						}

						Map<Integer, String> scriptexeSortedByExeSeqMap = scriptExehashMap.entrySet().stream()
								.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
										Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

						// updating runTestEntity to update the generated script path before running the
						// run test
						mainloop1: for (Entry<Integer, String> file : scriptexeSortedByExeSeqMap.entrySet()) {
							String orderFileName = file.getValue();
							String fileNameWithPath = generatedPath.toString() + "/" + orderFileName;
							String filePathWithRunTestId = generatedPath.toString()
									+ runTestEntityMap.get(neId).getId();
							File dir = new File(filePathWithRunTestId);
							if (!dir.exists()) {
								FileUtil.createDirectory(filePathWithRunTestId);
							}
							FileUtils.copyFileToDirectory(new File(fileNameWithPath), dir);
							String filePath = filePathWithRunTestId.toString() + "/" + orderFileName;
							generateScriptPath = generateScriptPath
									+ filePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "")
									+ ",";
							logger.info("RunTestServiceImpl.getRuntestExecResult() generateScriptPath: "
									+ generateScriptPath);
							if (StringUtils.isNotEmpty(generateScriptPath)) {
								runTestEntityMap.get(neId).setGenerateScriptPath(
										generateScriptPath.substring(0, generateScriptPath.length() - 1));
								runTestRepository.updateRunTest(runTestEntityMap.get(neId));
							}
						}

						mainloop1: for (Entry<Integer, String> file : scriptexeSortedByExeSeqMap.entrySet()) {

							String orderFileName = file.getValue();
							String fileNameWithPath = generatedPath.toString() + "/" + orderFileName;

							String output = "";
							StringBuilder newOutputFileDBPath = new StringBuilder();
							StringBuilder newOutputFilePath = new StringBuilder();

							String[] useCaseNameSplit = orderFileName.split("_");
							int iScriptExeSeq = Integer.parseInt(useCaseNameSplit[0]);
							String useCaseName = useCaseNameSplit[2];
							int scriptId = Integer.parseInt(useCaseNameSplit[5].split("\\.")[0]);
							String descFinalPath = "";
							String scriptFileName = useCaseNameSplit[4];
							iUseCaseId = Integer.parseInt(useCaseNameSplit[3]);

							UploadFileEntity scriptEntity = runTestRepository.getScriptInfo(scriptId);

							StringBuilder outputFilePath = new StringBuilder();

							StringBuilder comFilePath = new StringBuilder();
							String scriptArguments = scriptEntity.getArguments();

							// Template changes starts
							String connectionLocationUserName = null;
							String connectionLocationPwd = "";
							String connectionSudoPassword = "";

							ProgramTemplateEntity programTemplateEntity = fileUploadRepository
									.getProgramTemplate(programId, Constants.SCRIPT_STORE_TEMPLATE);
							if (programTemplateEntity != null) {
								if (programTemplateEntity.getValue() != null
										&& !programTemplateEntity.getValue().trim().isEmpty()) {
									JSONObject objData = CommonUtil.parseDataToJSON(programTemplateEntity.getValue());
									HashMap connLocation = (HashMap) objData.get("connLocation");

									// NE
									HashMap ne = (HashMap) connLocation.get("NE");
									connectionSudoPassword = ne.get("sudoPassword").toString();
									connectionLocationUserName = ne.get("username").toString();
									connectionLocationPwd = ne.get("password").toString();

								}
							}
							// Template changes end

							// kannan starts
							List<CheckListDetailsModel> sheetList = ciqUploadRepository.getCheckListAllSheetNames(
									checklistFileName, CommonUtil.createMongoDbFileNameCheckList(sProgramId,
											checklistFileName, ciqFileName));
							Set<String> sheetDetails = sheetList.stream().map(x -> x.getSheetName())
									.collect(Collectors.toSet());

							RunTestEntity runTestEntityDetails = runTestRepository.getRunTestEntityDetails(programId,
									migrationType, migrationSubType, testname, neName);

							if (sheetDetails != null) {
								for (String sheet : sheetDetails) {
									ciqUploadRepository
											.insertChecklistDetails(
													CommonUtil.createMongoDbFileNameCheckList(sProgramId,
															checklistFileName, ciqFileName),
													sheet, neName, remarks, runTestEntityDetails.getId());
								}
							}
							// kannan ends

							comFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
									.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId);

							if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {

								outputFilePath.append(comFilePath)
										.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
												.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
												.replace("subtype", migrationSubType).replaceAll(" ", "_"));

								newOutputFilePath.append(comFilePath)
										.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
												.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
												.replace("subtype", migrationSubType).replaceAll(" ", "_"))
										.append(testname).append("_").append(StringUtils.substringBeforeLast(neId, "."))
										.append("_").append(neName).append("/");

								newOutputFileDBPath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
										.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
										.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
												.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
												.replace("subtype", migrationSubType).replaceAll(" ", "_"))
										.append(testname).append("_").append(StringUtils.substringBeforeLast(neId, "."))
										.append("_").append(neName).append("/");

								String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Date());

								ranatpXlsFileName.setLength(0);
								ranatpXlsFileName.append("RANATP_" + neId + "_" + timeStamp + ".xlsx");

								ranatpXlsFilePath.setLength(0);
								ranatpXlsFilePath.append(comFilePath)
										.append(Constants.RUN_MIGRATION_OUTPUT.replace("migration", migrationType)
												.replace("enbId", StringUtils.substringBeforeLast(neId, "."))
												.replace("subtype", migrationSubType).replaceAll(" ", "_"))
										.append(ranatpXlsFileName.toString());

								File newFilePath = new File(newOutputFilePath.toString());
								newFilePath.mkdirs();

								File outFilePath = new File(outputFilePath.toString());
								outFilePath.mkdirs();

								String outputFileName = outputFilePath + serviceToken + "_" + "output.txt";

								// check if it is needed

								/*
								 * String dbcollectionFileName = CommonUtil
								 * .createMongoDbFileName(String.valueOf(programId), ciqFileName);
								 * List<CIQDetailsModel> resultList = runTestServiceImpl
								 * .getCIQDetailsModelList(neId, dbcollectionFileName);
								 * 
								 * NetworkConfigEntity networkConfigEntity = runTestRepository
								 * .getNeType(lsmId); String cduIp = runTestServiceImpl.getEnbOamIp(resultList);
								 * String eNodeID = neId; String eNodeName = neName;
								 */

								// checf id it is needed ends

								// String subtype = null;

								String arguments[] = scriptArguments.split(Constants.DELIMITER);
								String finalArguments = "";
								for (String argument : arguments) {
									argument = argument.trim();
									String argPreceeding = "";
									if (argument.contains(" ") && argument.contains("-") && argument.startsWith("-")) {
										argPreceeding = StringUtils.substringBefore(argument, " ");
										argPreceeding = argPreceeding.trim();
										argument = StringUtils.substringAfter(argument, " ");
										argument = argument.trim();
									}
									String newArgument = "";
									if (Constants.BANDS.equalsIgnoreCase(argument)) {
										newArgument = bandNameStr.toString();
										if (StringUtils.isNotEmpty(newArgument)) {
											newArgument = newArgument.substring(0, newArgument.length() - 1);
										}
									} else if (Constants.EXCEL_FILE.equalsIgnoreCase(argument)) {
										newArgument = ranatpXlsFileName.toString();
									} else if (Constants.OPS_ATP_INPUT_FILE.equalsIgnoreCase(argument)) {
										// logger.info("RanAtpServiceImpl.getRuntestExecResult() descFilePath:
										// "+descFilePath);
										if (originalFileName == null) {
											descFilePath = "";
										}
										newArgument = descFilePath;
									} else {
										UserDetailsEntity userDetailsEntity = userActionRepositoryImpl
												.getUserDetailsBasedName(userName);
										String saneUserName = "";
										String sanePwd = "";
										if (CommonUtil.isValidObject(userDetailsEntity)) {
											saneUserName = userDetailsEntity.getVpnUserName();
										}
										if (useCurrPassword == true) {
											sanePwd = PasswordCrypt.decrypt(userDetailsEntity.getVpnPassword());
										} else {
											sanePwd = sanePassword;
										}
										newArgument = commonUtil.getArgumentValue(Integer.toString(programId),
												ciqFileName, neId, neName, networkConfigEntity, migrationType,
												migrationSubType, argument, userName, sessionId,
												connectionLocationUserName, connectionLocationPwd,
												connectionSudoPassword, saneUserName, sanePwd);
									}
									if (argPreceeding != null) {
										if (argPreceeding.trim().contains("-")) {
											finalArguments = finalArguments + " " + argPreceeding + " ";
										}
									}
									if (newArgument != null) {
										newArgument = "'" + newArgument + "'";
									}
									finalArguments = finalArguments + newArgument + " ";

								}

								if ("RANATP".equalsIgnoreCase(migrationSubType)) {
									logger.info("RanAtpServiceImpl.getRuntestExecResult() finalArguments: "
											+ finalArguments);
									logger.error("RanAtpServiceImpl.getRuntestExecResult() finalArguments: "
											+ finalArguments);
									output = CommonUtil.executeCommand(fileNameWithPath, outputFileName,
											finalArguments);

									String timeStamp1 = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
											.format(new Timestamp(System.currentTimeMillis()));
									String newOutputFileName = serviceToken + "_" + useCaseName + "_" + scriptFileName
											+ "_" + timeStamp1 + ".txt";
									descFinalPath = newOutputFilePath.toString() + newOutputFileName;
									FileWriter fileWriter = new FileWriter(descFinalPath);
									fileWriter.write(output);
									fileWriter.close();

									CommonUtil.removeCtrlChars(outputFileName);
									CommonUtil.removeCtrlChars(descFinalPath);

								}

								String path = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "Customer/"
										+ programId + "/" + migrationType + "/" + neId + "/" + migrationSubType + "/"
										+ "Output" + "/";

								File duplicatePath = new File(path);
								duplicatePath.mkdirs();

							}

							// complted executing

							UseCaseBuilderParamEntity useCaseBuilderParamEntity = null;

							for (int j = 0; j < scriptFinalLst.size(); j++) {
								ArrayList scritlst = (ArrayList) scriptFinalLst.get(j);
								int scriptID = (int) scritlst.get(0);

								if (scriptID == scriptEntity.getId()) {
									int scrpitid12 = (int) scritlst.get(2);
									useCaseBuilderParamEntity = useCaseBuilderRepository
											.getUseCaseBuilderParamEntity(scrpitid12);
									break;
								}
							}

							LinkedHashSet<UseCaseCmdRuleEntity> useCaseCmdRuleEntityset = useCaseBuilderParamEntity
									.getUseCaseCmdRuleEntitySet().parallelStream()
									.sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
									.collect((Collectors.toCollection(LinkedHashSet::new)));
							;

							LinkedHashSet<UseCaseShellRuleEntity> useCaseShellRuleEntityset = useCaseBuilderParamEntity
									.getUseCaseShellRuleEntitySet().parallelStream()
									.sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
									.collect((Collectors.toCollection(LinkedHashSet::new)));
							;

							LinkedHashSet<UseCaseFileRuleEntity> useCaseFileRuleEntityset = useCaseBuilderParamEntity
									.getUseCaseFileRuleEntitySet().parallelStream()
									.sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
									.collect((Collectors.toCollection(LinkedHashSet::new)));
							;

							LinkedHashSet<UseCaseXmlRuleEntity> useCaseXmlRuleEntityset = useCaseBuilderParamEntity
									.getUseCaseXmlRuleEntitySet().parallelStream()
									.sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
									.collect((Collectors.toCollection(LinkedHashSet::new)));
							;

							RunTestEntity runTestEntity = runTestServiceImpl
									.getRunTestEntity(runTestEntityMap.get(neId).getId());

							UseCaseBuilderEntity useCaseBuilderEntitybyId = useCaseBuilderService
									.getUseCaseBuilderEntity(iUseCaseId);

							UploadFileEntity uploadFileEntity = uploadFileRepository.getUploadFileEntity(scriptId);

							for (UseCaseXmlRuleEntity xml : useCaseXmlRuleEntityset) {
								XmlRuleBuilderEntity xmlRuleEntity = xml.getXmlRuleBuilderEntity();
								int xmlRuleOccurenece = xml.getXmlRuleSequence();
								String xmlResult = "FAIL";

								xmlResult = runTestServiceImpl.xmlParsing(xmlRuleEntity, output, xmlRuleOccurenece,
										neName, neId, null, null);

								Object obj = new JSONParser().parse(xmlResult);
								JSONObject jo = (JSONObject) obj;
								xmlResult = (String) jo.get("outputResult");
								Map ruleResultJsonMap = (Map) jo.get("ruleResultJson");
								String ruleResultJson = ruleResultJsonMap.toString();

								if (!"PASS".equalsIgnoreCase(xmlResult)) {
									finalStatusResult = "Failure";
								}
								RunTestResultEntity runTestResultEntity = new RunTestResultEntity();
								runTestResultEntity.setRunTestEntity(runTestEntity);

								runTestResultEntity.setUseCaseBuilderEntity(useCaseBuilderEntitybyId);
								runTestResultEntity.setUploadFileEntity(uploadFileEntity);

								runTestResultEntity.setXmlRuleBuilderEntity(xmlRuleEntity);

								runTestResultEntity.setCurrentResult(xmlResult);
								runTestResultEntity.setCurrentScriptOutput(newOutputFileDBPath.toString());
								runTestResultEntity.setCustomerDetailsEntity(customerDetailsEntity);
								runTestResultEntity.setMigrationType(migrationType);
								runTestResultEntity.setMigrationSubType(migrationSubType);
								runTestResultEntity.setNeName(neId);
								runTestResultEntity.setRuleResultJson(ruleResultJson);
								runTestResultEntity.setScriptExeSeq(iScriptExeSeq);

								runTestResultRepository.createRunTestResult(runTestResultEntity);

							}

							for (UseCaseShellRuleEntity j : useCaseShellRuleEntityset) {
								ShellCmdRuleBuilderEntity shellRuleEntity = j.getShellRuleBuilderEntity();
								int shellRuleOccurence = j.getShellRuleSequence();
								String shellResult = "FAIL";

								shellResult = runTestServiceImpl.shellProcess(shellRuleEntity, output,
										shellRuleOccurence, neName, neId);

								if (!"PASS".equalsIgnoreCase(shellResult)) {
									finalStatusResult = "Failure";
								}

								RunTestResultEntity runTestResultEntity = new RunTestResultEntity();
								runTestResultEntity.setRunTestEntity(runTestEntity);

								runTestResultEntity.setUseCaseBuilderEntity(useCaseBuilderEntitybyId);
								runTestResultEntity.setUploadFileEntity(uploadFileEntity);

								runTestResultEntity.setShellCmdRuleBuilderEntity(shellRuleEntity);

								runTestResultEntity.setCurrentResult(shellResult);
								runTestResultEntity.setCurrentScriptOutput(newOutputFileDBPath.toString());
								runTestResultEntity.setCustomerDetailsEntity(customerDetailsEntity);
								runTestResultEntity.setMigrationType(migrationType);
								runTestResultEntity.setMigrationSubType(migrationSubType);
								runTestResultEntity.setNeName(neId);
								runTestResultEntity.setScriptExeSeq(iScriptExeSeq);

								runTestResultRepository.createRunTestResult(runTestResultEntity);

							}

							for (UseCaseCmdRuleEntity j : useCaseCmdRuleEntityset) {
								CmdRuleBuilderEntity cmdRuleEntity = j.getCmdRuleBuilderEntity();
								int cmRuleOccurence = j.getCommandRuleSequence();
								String cmdResult = "FAIL";

								cmdResult = runTestServiceImpl.cliProcess(cmdRuleEntity, output, cmRuleOccurence,
										neName, neId);

								Object obj = new JSONParser().parse(cmdResult);
								JSONObject jo = (JSONObject) obj;
								cmdResult = (String) jo.get("outputResult");
								Map ruleResultJsonMap = (Map) jo.get("ruleResultJson");
								String ruleResultJson = ruleResultJsonMap.toString();

								if (!"PASS".equalsIgnoreCase(cmdResult)) {
									finalStatusResult = "Failure";
								}

								RunTestResultEntity runTestResultEntity = new RunTestResultEntity();
								runTestResultEntity.setRunTestEntity(runTestEntity);

								runTestResultEntity.setUseCaseBuilderEntity(useCaseBuilderEntitybyId);
								runTestResultEntity.setUploadFileEntity(uploadFileEntity);

								runTestResultEntity.setCmdRuleBuilderEntity(cmdRuleEntity);

								runTestResultEntity.setCurrentResult(cmdResult);
								runTestResultEntity.setCurrentScriptOutput(newOutputFileDBPath.toString());
								runTestResultEntity.setCustomerDetailsEntity(customerDetailsEntity);
								runTestResultEntity.setMigrationType(migrationType);
								runTestResultEntity.setMigrationSubType(migrationSubType);
								runTestResultEntity.setNeName(neId);
								runTestResultEntity.setRuleResultJson(ruleResultJson);
								runTestResultEntity.setScriptExeSeq(iScriptExeSeq);

								runTestResultRepository.createRunTestResult(runTestResultEntity);

							}
							StringBuilder fileFinaloutput = new StringBuilder();
							for (UseCaseFileRuleEntity entity : useCaseFileRuleEntityset) {
								FileRuleBuilderEntity fileRuleEntity = entity.getFileRuleBuilderEntity();

								String fileName1 = "admin_" + fileRuleEntity.getFileName();
								int count = 2;
								String serachParam = fileRuleEntity.getSearchParameter();
								String status = fileRuleEntity.getStatus();
								String finalResult = "FAIL";
								String outputString = null;

								boolean result = runTestServiceImpl.fileExists(fileName1);
								if (result) {
									Map finalResultMap = runTestServiceImpl.paramCount(fileName1, count, serachParam,
											status);
									outputString = (String) finalResultMap.get("outputString");

									fileFinaloutput = fileFinaloutput.append("\nfileName :").append(fileName1)
											.append("\n").append(outputString);

									FileWriter fileWriter = new FileWriter(descFinalPath, true);
									fileWriter.write(output);
									fileWriter.close();

									finalResult = (String) finalResultMap.get("status");
								}

								if (!"PASS".equalsIgnoreCase(finalResult)) {
									finalStatusResult = "Failure";
								}
								RunTestResultEntity runTestResultEntity = new RunTestResultEntity();
								runTestResultEntity.setRunTestEntity(runTestEntity);

								runTestResultEntity.setUseCaseBuilderEntity(useCaseBuilderEntitybyId);
								runTestResultEntity.setUploadFileEntity(uploadFileEntity);

								runTestResultEntity.setFileRuleBuilderEntity(fileRuleEntity);

								runTestResultEntity.setCurrentResult(finalResult);
								runTestResultEntity.setCurrentScriptOutput(newOutputFileDBPath.toString());
								runTestResultEntity.setCustomerDetailsEntity(customerDetailsEntity);
								runTestResultEntity.setMigrationType(migrationType);
								runTestResultEntity.setMigrationSubType(migrationSubType);
								runTestResultEntity.setNeName(neId);
								runTestResultEntity.setScriptExeSeq(iScriptExeSeq);

								if ("RERUN".equals(runType)) {
									List<RunTestResultEntity> testResultEntity = runTestResultRepository
											.getPreviousRunTestResult(runTestEntityMap.get(neId).getId());
									for (RunTestResultEntity rtre : testResultEntity) {
										rtre.setPreviousResult(rtre.getCurrentResult());
										rtre.setCurrentResult(finalResult);
										rtre.setPreviousScriptOutput(rtre.getCurrentScriptOutput());
										rtre.setCurrentScriptOutput(output);

										runTestResultRepository.updateValueToPrevious(rtre);

										RunTestEntity runTest = runTestRepository
												.getRunTestEntity(runTestEntityMap.get(neId).getId());
										runTest.setCreationDate(new Date());
										runTestRepository.updateRunTest(runTest);

									}
								} else {

									runTestResultRepository.createRunTestResult(runTestResultEntity);
								}

							}

							if (useCaseFileRuleEntityset.isEmpty() && useCaseCmdRuleEntityset.isEmpty()
									&& useCaseXmlRuleEntityset.isEmpty()) {
								RunTestResultEntity runTestResultEntity = new RunTestResultEntity();

								runTestResultEntity.setRunTestEntity(runTestEntity);

								runTestResultEntity.setUseCaseBuilderEntity(useCaseBuilderEntitybyId);
								runTestResultEntity.setUploadFileEntity(uploadFileEntity);

								runTestResultEntity.setCurrentResult("PASS");
								runTestResultEntity.setCurrentScriptOutput(newOutputFileDBPath.toString());
								runTestResultEntity.setCustomerDetailsEntity(customerDetailsEntity);
								runTestResultEntity.setMigrationType(migrationType);
								runTestResultEntity.setMigrationSubType(migrationSubType);
								runTestResultEntity.setNeName(neId);
								runTestResultEntity.setScriptExeSeq(iScriptExeSeq);

								runTestResultRepository.createRunTestResult(runTestResultEntity);
							}
							// kannan

							// swetha : If we are getting word "failed" from perl executed output we need to
							// fail the run
							/*
							 * if(StringUtils.isNotEmpty(output) && StringUtils.containsIgnoreCase(output,
							 * Constants.RAN_ATP_FAIL)){ finalStatusResult = "Failure";
							 * ranatpXlsFileName.setLength(0); }
							 */
						}

						int ucsleep = getUCSleepInterval(useCaseList, iUseCaseId);

						Thread.sleep(ucsleep);

						String fileSavePath = StringUtils.substringBeforeLast(ranatpXlsFilePath.toString(), "/");
						fileSavePath = fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"),
								"");

						runTestEntityMap.get(neId).setResult(ranatpXlsFileName.toString());
						runTestEntityMap.get(neId).setResultFilePath(fileSavePath);
						runTestEntityMap.get(neId).setProgressStatus("Completed");
						runTestEntityMap.get(neId).setStatus(finalStatusResult);
						
						User user = UserSessionPool.getInstance().getSessionUser(sessionId);
						Map<String, String> scriptFilesDetails = new HashMap<>();
						reportService.insertPostMigAuditReportDetails(neId, programId, customerDetailsEntity.getProgramName(), user.getUserName(),
								ciqFileName, scriptFilesDetails, finalStatusResult, migrationSubType);
						
						logger.info(
								"RunTestServiceImpl.getRuntestExecResult() generateScriptPath: " + generateScriptPath);
						if (StringUtils.isNotEmpty(generateScriptPath)) {
							runTestEntityMap.get(neId).setGenerateScriptPath(
									generateScriptPath.substring(0, generateScriptPath.length() - 1));
						}
						runTestRepository.updateRunTest(runTestEntityMap.get(neId));
					} catch (InterruptedException e) {
						logger.error("Exception RunTestServiceImpl in getRuntestExecResult() "
								+ ExceptionUtils.getFullStackTrace(e));
					}
					return Constants.SUCCESS;

				});

			}
		} catch (

		Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in getRuntestExecResult() " + ExceptionUtils.getFullStackTrace(e));

			return e.getMessage();
		}

		return Constants.SUCCESS;
	}

	public String executePlAuditCommand(String command, String outputFileName, String cduIp, String eNodeID,
			String eNodeName, NetworkConfigEntity networkConfigEntity, String htmlOutputFileName,
			String opsAtpFileContent) {
		StringBuilder output = new StringBuilder();

		String cascadeId = eNodeID;
		String adid = eNodeID;
		String lsmUserName = networkConfigEntity.getNeUserName();
		String lsmPassword = networkConfigEntity.getNePassword();
		String dir = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.SEPARATOR + "AuditTest";
		String market = networkConfigEntity.getNeMarket().replaceAll(" ", "_");

		Process p;
		try {
			Runtime.getRuntime().exec("chmod -R 777 " + command);
			Thread.sleep(2000);

			StringBuilder cmd = new StringBuilder();

			cmd.append("perl").append(" ").append(command).append(" ").append(cduIp).append(" ").append(eNodeID)
					.append(" ").append(eNodeName).append(" ").append(cascadeId).append(" ").append(adid).append(" ")
					.append(lsmUserName).append(" ").append(lsmPassword).append(" ").append(dir).append(" ")
					.append(market).append(" ").append(htmlOutputFileName).append(" ").append(opsAtpFileContent);

			p = Runtime.getRuntime().exec(cmd.toString());

			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			OutputStream os = new FileOutputStream(outputFileName);

			if (reader != null) {
				String line = "";
				while ((line = reader.readLine()) != null) {
					String lineData = line + "\n";
					String resultString = lineData.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
					output.append(resultString);
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			}
			if (error != null) {
				String line = "";
				while ((line = error.readLine()) != null) {
					String lineData = line + "\n";
					String resultString = lineData.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
					output.append(resultString);
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			}
			os.close();
		} catch (Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in executePlAuditCommand() " + ExceptionUtils.getFullStackTrace(e));
		}

		return output.toString();

	}

	public String executePlRanAtpCommand(String command, String outputFileName, String cduIp, String eNodeID,
			String eNodeName, NetworkConfigEntity networkConfigEntity, String htmlOutputFileName,
			String opsAtpFileContent, String bandNameStr) {
		StringBuilder output = new StringBuilder();

		String cascadeId = eNodeID;
		String adid = eNodeID;
		String lsmUserName = networkConfigEntity.getNeUserName();
		String lsmPassword = networkConfigEntity.getNePassword();
		String dir = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + Constants.SEPARATOR + "ATPTest";
		String market = networkConfigEntity.getNeMarket().replaceAll(" ", "_");

		Process p;
		try {
			Runtime.getRuntime().exec("chmod -R 777 " + command);
			Thread.sleep(2000);

			StringBuilder cmd = new StringBuilder();

			cmd.append("perl").append(" ").append(command).append(" ").append(cduIp).append(" ").append(eNodeID)
					.append(" ").append(eNodeName).append(" ").append(cascadeId).append(" ").append(adid).append(" ")
					.append(lsmUserName).append(" ").append(lsmPassword).append(" ").append(dir).append(" ")
					.append(market).append(" ").append(htmlOutputFileName).append(" ").append(bandNameStr).append(" ")
					.append(opsAtpFileContent);

			p = Runtime.getRuntime().exec(cmd.toString());

			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			OutputStream os = new FileOutputStream(outputFileName);

			if (reader != null) {
				String line = "";
				while ((line = reader.readLine()) != null) {
					String lineData = line + "\n";
					String resultString = lineData.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
					output.append(resultString);
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			}
			if (error != null) {
				String line = "";
				while ((line = error.readLine()) != null) {
					String lineData = line + "\n";
					String resultString = lineData.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
					output.append(resultString);
					os.write(resultString.getBytes(), 0, resultString.length());
				}
			}
			os.close();
		} catch (Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in executePlRanAtpCommand() " + ExceptionUtils.getFullStackTrace(e));
		}

		return output.toString();

	}

}