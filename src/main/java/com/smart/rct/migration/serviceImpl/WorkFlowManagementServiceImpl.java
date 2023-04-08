package com.smart.rct.migration.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang.StringUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.json.simple.JSONArray;
import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.controller.RunTestController;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.migration.repository.RunTestResultRepository;
import com.smart.rct.migration.repository.WorkFlowManagementRepository;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.postmigration.entity.Audit5GCBandIssueEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandIssueRepository;
import com.smart.rct.postmigration.repository.Audit5GDSSIssueRepository;
import com.smart.rct.premigration.controller.GenerateCsvController;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;


@Service
public class WorkFlowManagementServiceImpl implements WorkFlowManagementService {

	final static Logger logger = LoggerFactory.getLogger(WorkFlowManagementServiceImpl.class);

	@Autowired
	WorkFlowManagementRepository workFlowManagementRepository;

	@Autowired
	RunTestResultRepository runTestResultRepository;

	@Autowired
	GenerateCsvController generateCsvController;

	// @Autowired
	// WorkFlowManagementEntity WorkFlowManagementEntityThread;

	@Autowired
	WorkFlowManagementService workFlowManagementService;

	@Autowired
	RunTestService runTestService;

	@Autowired
	RunTestController runTestController;

	@Autowired
	NeMappingService neMappingService;

	@Autowired
	CommonUtil common;
	
	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	
	@Autowired
	Audit5GCBandIssueRepository audit5GCBandIssueRepository;
	
	@Autowired
	Audit5GDSSIssueRepository audit5GDSSIssueRepository;

	@Override
	public RunTestEntity getRunTestEntity(RunTestEntity runTestEntity) {
		// TODO Auto-generated method stub
		RunTestEntity runTestEntityResult = null;
		try {

			runTestEntityResult = workFlowManagementRepository.getRunTestEntity(runTestEntity);

		} catch (Exception e) {
			logger.error("Exception in getRunTestEntity()   WorkFlowManagementServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return runTestEntityResult;
	}

	// code for display failure
	public Boolean createNewFile(String checkPath) {
		String folderName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "/ErrorLog/";
		File f1 = new File(folderName);
		f1.mkdir();
		File file1 = new File(checkPath); // initialize File object and passing path as argument
		Boolean result = null;
		try {
			result = file1.createNewFile(); // creates a new file
		} catch (IOException e) {
			e.printStackTrace(); // prints exception if any
		}

		return result;
	}

	@Override
	public WorkFlowManagementEntity insertWorkManagementDetails(JSONObject runTestParams) {
		// TODO Auto-generated method stub
		WorkFlowManagementEntity workFlowManagementEntity = new WorkFlowManagementEntity();
		try {
			System.out.println("runTestParams in insertWorkManagementDetails : " + runTestParams.toJSONString());
			String lsmVersion = null;
			String lsmName = null;
			ArrayList<String> arr = new ArrayList<String>();
			String programName = runTestParams.get("programName").toString();
			String preMigStatus = runTestParams.get("preMigStatus").toString();
			String MigStatus = runTestParams.get("MigStatus").toString();
			String postMigStatus = runTestParams.get("postMigStatus").toString();
			String neGrowStatus = runTestParams.get("neGrowStatus").toString();
			String preAuditStatus = runTestParams.get("preAuditStatus").toString();
			String neStatusStatus = runTestParams.get("neStatusStatus").toString();
			String enbId = runTestParams.get("enbId").toString();
			System.out.println("calling insertWorkManagementDetails enbId " + enbId);
			Date creationDate = new Date();
			String userName = runTestParams.get("userName").toString();
			String testname = runTestParams.get("testname").toString();
			String testDesc = runTestParams.get("testDesc").toString();
			//if (!programName.contains("FSU") && 
			if(runTestParams.containsKey("lsmVersion")
					&& runTestParams.containsKey("lsmName")) {
				lsmVersion = runTestParams.get("lsmVersion").toString();
				lsmName = runTestParams.get("lsmName").toString();
			}
			String ciqName = runTestParams.get("ciqName").toString();
			String neName = runTestParams.get("enbName").toString();
			String neId = runTestParams.get("enbId").toString();
			System.out.println("neId**************:" + neId);
			System.out.println("ciqName**************:" + ciqName + "###");

			if (runTestParams.containsKey("type") && runTestParams.get("type").equals("OV")
					&& null == runTestParams.get("enbName")) {
				System.out.println("is it from OV**************:");
				neName = "";
			} else {
				System.out.println("is not from OV**************:");
				if(null!=runTestParams.get("enbName")) {
					
					neName = runTestParams.get("enbName").toString();
				}
			}
			System.out.println("neName**************>>:" + neName + "<<###");

			Map premigration = (Map) runTestParams.get("premigration");
			String integrationType = "Legacy IP";
			if (runTestParams.containsKey("premigration") && runTestParams.get("premigration") != null) {
			if (premigration.containsKey("integrationType") && null != premigration.get("integrationType")) {
				Object object = premigration.get("integrationType");
				if (null != object) {
					integrationType = (String) object;
				}
			}
			}
			String siteName = null;
			if(programName.contains("5G-MM")) {
				siteName = runTestParams.get("siteName").toString();
			}
			int customerId = Integer.parseInt(runTestParams.get("customerId").toString());
			int programId = Integer.parseInt(runTestParams.get("programId").toString());
			CustomerDetailsEntity customerDetailsEntity = getCustomerDetailsEntity(programId);
			workFlowManagementEntity.setCreationDate(creationDate);
			workFlowManagementEntity.setCustomerId(customerId);
			//if (!programName.contains("FSU")) {
				workFlowManagementEntity.setLsmName(lsmName);
				workFlowManagementEntity.setLsmVersion(lsmVersion);
			//}
			if(programName.contains("5G-MM")) {
				workFlowManagementEntity.setSiteName(siteName);
			}
			workFlowManagementEntity.setCiqName(ciqName);
			workFlowManagementEntity.setTestName(testname);
			workFlowManagementEntity.setTestDescription(testDesc);
			workFlowManagementEntity.setCustomerDetailsEntity(customerDetailsEntity);
			// workFlowManagementEntity.setNeGrowStatus("NotYetStarted");
			// workFlowManagementEntity.setMigStatus("NotYetStarted");
			// workFlowManagementEntity.setPreMigStatus("Completed");
			// workFlowManagementEntity.setPostMigStatus("NotYetStarted");
			workFlowManagementEntity.setNeGrowStatus(neGrowStatus);
			workFlowManagementEntity.setMigStatus(MigStatus);
			workFlowManagementEntity.setPreMigStatus(preMigStatus);
			workFlowManagementEntity.setNeUpStatus(neStatusStatus);
			workFlowManagementEntity.setPostMigStatus(postMigStatus);
			workFlowManagementEntity.setNeName(neName);
			workFlowManagementEntity.setUserName(userName);
			workFlowManagementEntity.setEnbId(enbId);
			// if(runTestParams.containsKey("type") &&
			// runTestParams.get("type").equals("OV")) {
			if (programName.contains("4G-USM-LIVE")) {
				workFlowManagementEntity.setIntegrationType(integrationType);
				System.out.println("Integration type insertwork management details : "
						+ workFlowManagementEntity.getIntegrationType());
			} else {
				workFlowManagementEntity.setIntegrationType("NA");
			}
			// }
                        workFlowManagementEntity.setPreAuditStatus(preAuditStatus);
			workFlowManagementEntity.setStatus("InProgress");
			arr = (ArrayList<String>) runTestParams.get("allFilePaths");
			System.out.println(enbId+"****Integration type in wfm service imp***" + integrationType);
			// code for display failure logs
			if (arr != null && arr.size() == 6) {
				workFlowManagementEntity.setPreErrorFile(arr.get(0));
				workFlowManagementEntity.setNegrowErrorFile(arr.get(1));
				workFlowManagementEntity.setNEStatusErrorFile(arr.get(2));
				workFlowManagementEntity.setPREAUDITErrorFile(arr.get(3));
				
				workFlowManagementEntity.setMigErrorFile(arr.get(4));
				workFlowManagementEntity.setPostErrorFile(arr.get(5));
			}
			workFlowManagementEntity.setSiteReportStatus("NotExecuted");
			workFlowManagementEntity = workFlowManagementRepository.createWorkFlowMangement(workFlowManagementEntity);
			System.out.println("After calling insertWorkManagementDetails enbId " + enbId);
		} catch (Exception e) {
			logger.error("Exception in insertWorkManagementDetails()   WorkFlowManagementServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return workFlowManagementEntity;
	}

	@Override
	public WorkFlowManagementEntity mergeWorkFlowMangement(WorkFlowManagementEntity workFlowManagementEntity) {
		WorkFlowManagementEntity workFlowManagementEntityUpdate = null;
		try {
			System.out.println("Start mergeWorkFlowMangement " + workFlowManagementEntity.getEnbId());
			workFlowManagementEntityUpdate = workFlowManagementRepository
					.createWorkFlowMangement(workFlowManagementEntity);
		} catch (Exception e) {
			logger.error("Exception in mergeWorkFlowMangement()   WorkFlowManagementServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return workFlowManagementEntityUpdate;
	}

	public CustomerDetailsEntity getCustomerDetailsEntity(int programId) {
		CustomerDetailsEntity customerDetailsEntity = null;
		try {
			customerDetailsEntity = runTestResultRepository.getCustomerDetailsEntity(programId);
		} catch (Exception e) {
			logger.error("Exception getCustomerDetailsEntity() in RunTestServiceImpl  : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntity;
	}

	@Override
	public Map<String, Object> getWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, int page, int count,
			Integer programId) {

		Map<String, Object> objMap = new HashMap<String, Object>();
		List<WorkFlowManagementEntity> runTestDetails = null;
		List<WorkFlowManagementModel> objRunTestModelList = new ArrayList<>();
		try {
			Map<String, Object> resultMap = workFlowManagementRepository.getWorkFlowManagementDetails(runTestModel,
					page, count, programId);
			if (resultMap != null && resultMap.get("list") != null) {
				runTestDetails = (List<WorkFlowManagementEntity>) resultMap.get("list");
				if (runTestDetails != null && !runTestDetails.isEmpty()) {
					for (WorkFlowManagementEntity runTestEntity : runTestDetails) {
						WorkFlowManagementModel runModel = new WorkFlowManagementModel();
						runModel.setTestName(runTestEntity.getTestName());
						runModel.setIntegrationType(runTestEntity.getIntegrationType());
						runModel.setLsmVersion(runTestEntity.getLsmVersion());
						runModel.setLsmName(runTestEntity.getLsmName());
						runModel.setCiqName(runTestEntity.getCiqName());
						runModel.setNeName(runTestEntity.getNeName());
						runModel.setId(runTestEntity.getId());
						runModel.setUserName(runTestEntity.getUserName());
						runModel.setPreMigStatus(runTestEntity.getPreMigStatus());
						//new
						runModel.setPreAuditStatus(runTestEntity.getPreAuditStatus());
						runModel.setNeStatus(runTestEntity.getNeUpStatus());
						runModel.setNeGrowStatus(runTestEntity.getNeGrowStatus());
						runModel.setMigStatus(runTestEntity.getMigStatus());
						runModel.setEnbId(runTestEntity.getEnbId());
						runModel.setEnvPath(runTestEntity.getEnvPath());
						runModel.setPostMigStatus(runTestEntity.getPostMigStatus());
						runModel.setCommPath(runTestEntity.getCommPath());
						runModel.setCommZipName(runTestEntity.getCommZipName());
						runModel.setEnvZipName(runTestEntity.getEnvZipName());
						runModel.setEnvPath(runTestEntity.getEnvPath());
						runModel.setCsvPath(runTestEntity.getCsvPath());
						runModel.setCsvZipName(runTestEntity.getCsvZipName());
						runModel.setFileNamePre(runTestEntity.getFileNamePre());
						runModel.setFilePathPre(runTestEntity.getFilePathPre());
						runModel.setStatus(runTestEntity.getStatus());
						// code for display failure logs
						runModel.setPreErrorFile(runTestEntity.getPreErrorFile());
						runModel.setNeGrowErrorFile(runTestEntity.getNegrowErrorFile());
						//new
						runModel.setPreAuditErrorFile(runTestEntity.getPREAUDITErrorFile());
						runModel.setNeStatusErrorFile(runTestEntity.getNEStatusErrorFile());
						runModel.setMigErrorFile(runTestEntity.getMigErrorFile());
						runModel.setPostErrorFile(runTestEntity.getPostErrorFile());
						runModel.setSiteName(runTestEntity.getSiteName());
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String sCreationDate = dateFormat.format(runTestEntity.getCreationDate());

						runModel.setCreationDate(sCreationDate);
						runModel.setTestDescription(runTestEntity.getTestDescription());

						if (runTestEntity.getRunNEGrowEntity() != null
								&& runTestEntity.getRunNEGrowEntity().getId() > 0) {
							RunTestModel negrowRunTestModel = getRuntestModel(runTestEntity.getRunNEGrowEntity());

							runModel.setNegrowRunTestModel(negrowRunTestModel);
						}
						//new
						if (runTestEntity.getRunPreAuditTestEntity() != null
								&& runTestEntity.getRunPreAuditTestEntity().getId() > 0) {
							RunTestModel preAuditRunTestModel = getRuntestModel(runTestEntity.getRunPreAuditTestEntity());

							runModel.setPreAuditMigrationRunTestModel(preAuditRunTestModel);
						}
						if (runTestEntity.getRunNEStatusTestEntity() != null
								&& runTestEntity.getRunNEStatusTestEntity().getId() > 0) {
							RunTestModel neStatusRunTestModel = getRuntestModel(runTestEntity.getRunNEStatusTestEntity());

							runModel.setNeStatusRunTestModel(neStatusRunTestModel);
						}
						if (runTestEntity.getRunMigTestEntity() != null
								&& runTestEntity.getRunMigTestEntity().getId() > 0) {
							RunTestModel migRunTestModel = getRuntestModel(runTestEntity.getRunMigTestEntity());

							runModel.setMigrationRunTestModel(migRunTestModel);
						}

						if (runTestEntity.getRunPostMigTestEntity() != null
								&& runTestEntity.getRunPostMigTestEntity().getId() > 0) {
							RunTestModel PostMigRunTestModel = getRuntestModel(runTestEntity.getRunPostMigTestEntity());

							runModel.setPostMigrationRunTestModel(PostMigRunTestModel);
						}

						if (runTestEntity.getRunRanAtpTestEntity() != null
								&& runTestEntity.getRunRanAtpTestEntity().getId() > 0) {
							RunTestModel ranAtpRunTestModel = getRuntestModel(runTestEntity.getRunRanAtpTestEntity());
							runModel.setranAtpRunTestModel(ranAtpRunTestModel);
						}
						if (ObjectUtils.isEmpty(runTestEntity.getRunNEGrowEntity())) {
							runModel.setInputRequired("NEGROW");
						} else if (ObjectUtils.isEmpty(runTestEntity.getRunNEStatusTestEntity())) {
							runModel.setInputRequired("NESTATUS");
						} else if (ObjectUtils.isEmpty(runTestEntity.getRunPreAuditTestEntity())) {
							runModel.setInputRequired("PREAUDIT");
						}else if (ObjectUtils.isEmpty(runTestEntity.getRunMigTestEntity())) {
							runModel.setInputRequired("MIG");
						} else if (ObjectUtils.isEmpty(runTestEntity.getRunPostMigTestEntity())) {
							runModel.setInputRequired("PMIG");
						}
						runModel.setSiteReportStatus(runTestEntity.getSiteReportStatus());
						if("Failure".equalsIgnoreCase(runModel.getPostMigStatus()) || "Completed".equalsIgnoreCase(runModel.getPostMigStatus()) || "Success".equalsIgnoreCase(runModel.getPostMigStatus()) )
						{
							if("NotExecuted".equalsIgnoreCase(runModel.getSiteReportStatus()))
							{
								runModel.setSiteReportStatus("InputsRequired");
							}
						}
						runModel.setOvSiteReportStatus(runTestEntity.getOvSiteReportStatus());
						runModel.setSiteReportId(runTestEntity.getSiteReportId());
						objRunTestModelList.add(runModel);
					}
				}
			}
			objMap.put("list", objRunTestModelList);
			objMap.put("paginationNumber", resultMap.get("paginationNumber"));
		} catch (Exception e) {
			logger.error("Exception  in RunTestServiceImpl getWorkFlowManagementDetails():"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	public RunTestModel getRuntestModel(RunTestEntity runTestEntity) {
		// TODO Auto-generated method stub
		RunTestModel runModel = new RunTestModel();
		try {

			runModel.setTestName(runTestEntity.getTestName());
			runModel.setLsmVersion(runTestEntity.getLsmVersion());
			runModel.setLsmName(runTestEntity.getLsmName());
			runModel.setProgressStatus(runTestEntity.getProgressStatus());
			runModel.setStatus(runTestEntity.getStatus());
			runModel.setCiqName(runTestEntity.getCiqName());
			runModel.setNeName(runTestEntity.getNeName());
			runModel.setUseCase(runTestEntity.getUseCase());
			runModel.setId(runTestEntity.getId());
			runModel.setOutputFilepath(runTestEntity.getOutputFilepath());
			runModel.setResult(runTestEntity.getResult());
			runModel.setMigrationType(runTestEntity.getMigrationType());
			runModel.setMigrationSubType(runTestEntity.getMigrationSubType());
			runModel.setChecklistFileName(runTestEntity.getChecklistFileName());
			runModel.setUserName(runTestEntity.getUserName());
			runModel.setMigStatusDesc(runTestEntity.getMigStatusDesc());
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sCreationDate = dateFormat.format(runTestEntity.getCreationDate());

			runModel.setCreationDate(sCreationDate);
			runModel.setTestDescription(runTestEntity.getTestDescription());
			runModel.setResultFilePath(runTestEntity.getResultFilePath());
			runModel.setGenerateScriptPath(runTestEntity.getGenerateScriptPath());
			runModel.setFailedScript(runTestEntity.getFailedScript());
			runModel.setOvUpdateStatus(runTestEntity.getOvUpdateStatus());
			runModel.setProgressCount(runTestEntity.getProgressScript());
			runModel.setTotalRFScript(runTestEntity.getTotalRFScript());
			runModel.setTotalScript(runTestEntity.getTotalScript());
			runModel.setTestInfo(runTestEntity.getTestInfo());
		} catch (Exception e) {
			logger.error("Exception  in RunTestServiceImpl getRuntestModel():" + ExceptionUtils.getFullStackTrace(e));
		}
		return runModel;
	}

	@Override
	public void getWorkFlowManageStatus(WorkFlowManagementEntity workFlowManagementEntity) {

		try {

			ExecutorService executorservice = Executors.newFixedThreadPool(1);

			executorservice.submit(() -> {

				List<Integer> runtestIdList = new ArrayList<>();

				if (workFlowManagementEntity.getRunMigTestEntity() != null
						&& workFlowManagementEntity.getRunMigTestEntity().getId() > 0) {
					runtestIdList.add(workFlowManagementEntity.getRunMigTestEntity().getId());
				}

				if (workFlowManagementEntity.getRunPostMigTestEntity() != null
						&& workFlowManagementEntity.getRunPostMigTestEntity().getId() > 0) {
					runtestIdList.add(workFlowManagementEntity.getRunPostMigTestEntity().getId());
				}

				while (!ObjectUtils.isEmpty(runtestIdList)) {

					List<RunTestEntity> runTestEntityPostMigrationResult = workFlowManagementRepository
							.getRunTestListWfm(runtestIdList);

					AtomicBoolean statusOfRuntest = new AtomicBoolean();
					List<Integer> testCompletedIdList = new ArrayList<>();
					if (!ObjectUtils.isEmpty(runTestEntityPostMigrationResult)) {
						for (RunTestEntity objRunTestEntity : runTestEntityPostMigrationResult) {
							if (!"Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())) {
								statusOfRuntest.getAndSet(true);

							}
							if ("Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())
									&& !testCompletedIdList.contains(objRunTestEntity.getId())) {
								if ("precheck".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									workFlowManagementEntity.setMigStatus(objRunTestEntity.getStatus());
								} else if ("Audit".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									workFlowManagementEntity.setPostMigStatus(objRunTestEntity.getStatus());
									if("Failure".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) )
									{
										if("NotExecuted".equalsIgnoreCase(workFlowManagementEntity.getSiteReportStatus()))
										{
											workFlowManagementEntity.setSiteReportStatus("InputsRequired");
										}
									}
								} else if ("NEGrow".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									workFlowManagementEntity.setPostMigStatus(objRunTestEntity.getStatus());
								}
								workFlowManagementRepository.createWorkFlowMangement(workFlowManagementEntity);

								testCompletedIdList.add(objRunTestEntity.getId());

							}
						}
					}

					if (!statusOfRuntest.get()) {
						break;
					}

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						ExceptionUtils.getFullStackTrace(e);
					}
				}

			});
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public WorkFlowManagementEntity getWorkFlowManagementEntity(Integer workFlowId) {
		WorkFlowManagementEntity workFlowManagementEntity = null;
		try {
			workFlowManagementEntity = workFlowManagementRepository.getWorkFlowManagementEntity(workFlowId);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return workFlowManagementEntity;
	}

	@Override
	public boolean deleteWfmrunTest(int wfmRunTestId) {
		// TODO Auto-generated method stub
		return workFlowManagementRepository.deleteWfmrunTest(wfmRunTestId);
	}

	@Override
	public Map<String, List<Map>> formUseCases(List<Map> migrationUseCases) {
		Map<String, List<Map>> objScriptList = new LinkedHashMap<String, List<Map>>();
		List<Map> useCaseDetails = new ArrayList<>();
		List<Map> scripts = new ArrayList<>();
		for (Map usecase : migrationUseCases) {
			if (usecase != null) {
				Map<String, String> usecaseMap = new HashMap<>();
				usecaseMap.put("useCaseName", usecase.get("useCaseName").toString());
				usecaseMap.put("useCaseId", usecase.get("useCaseId").toString());
				usecaseMap.put("executionSequence", usecase.get("executionSequence").toString());
				usecaseMap.put("ucSleepInterval", usecase.get("ucSleepInterval").toString());
				useCaseDetails.add(usecaseMap);
			}
			List<Map> scriptsMap = new ArrayList<>();
			scriptsMap = (List<Map>) usecase.get("scripts");
			if (scriptsMap != null) {
				for (Map script : scriptsMap) {
					Map<String, String> scriptsList = new HashMap<>();
					scriptsList.put("useCaseName", usecase.get("useCaseName").toString());
					scriptsList.put("scriptName", script.get("scriptName").toString());
					scriptsList.put("scriptId", script.get("scriptId").toString());
					scriptsList.put("scriptExeSequence", script.get("scriptExeSequence").toString());
					scriptsList.put("scriptSleepInterval", script.get("scriptSleepInterval").toString());
					scriptsList.put("useGeneratedScript", script.get("useGeneratedScript").toString());
					scripts.add(scriptsList);
				}

			}
		}
		objScriptList.put("useCase", useCaseDetails);
		objScriptList.put("scripts", scripts);
		return objScriptList;

	}

	@Override
	public String getWFMRunTestEntity(int programId, String testname) {
		try {
			boolean isTestNamePresent = workFlowManagementRepository.getWFMRunTestEntity(programId, testname);
			if (isTestNamePresent) {
				return GlobalInitializerListener.faultCodeMap.get(FaultCodes.TEST_NAME_ALREADY_EXISTS);
			}
		} catch (Exception e) {
			logger.error(
					"Exception RunTestServiceImpl in getWFMRunTestEntity() " + ExceptionUtils.getFullStackTrace(e));
		}

		return Constants.SUCCESS;
	}

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@Override
	public JSONObject runIndependentWFM(String sessionId, String serviceToken, String state,
			JSONObject premigrationInputs, JSONObject migrationInputs, JSONObject postmigrationInputs,
			JSONObject negrowInputs, List<Map<String, String>> enbList, String programId, String programName,
			Integer workFlowId) {
		JSONObject resultMap = new JSONObject();

		try {
			ExecutorService executorservice = Executors.newFixedThreadPool(enbList.size());
			for (Map<String, String> enbMap : enbList) {
				executorservice.submit(() -> {
					Thread.currentThread().setName("WFM_" + workFlowId);
					WorkFlowManagementEntity WorkFlowManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(workFlowId);
					WorkFlowManagementEntity = workFlowManagementService
							.mergeWorkFlowMangement(WorkFlowManagementEntity);
					JSONObject preMigrationStatusDetails = null;
					if (premigrationInputs != null && WorkFlowManagementEntity != null) {
						String envPath = null;
						String envZip = null;
						String notYetStatus = "NotYetStarted";
						WorkFlowManagementEntity.setPreMigStatus(notYetStatus.toString());
						WorkFlowManagementEntity = workFlowManagementService
								.mergeWorkFlowMangement(WorkFlowManagementEntity);
						if (!programName.contains("VZN-5G-MM") && !programName.contains("VZN-5G-DSS")
								&& !programName.contains("FSU") && !programName.contains("5G-CBAND")) {
							preMigrationStatusDetails = generateCsvController
									.generateCiqNeBasedFiles(premigrationInputs);
						} else if (programName.contains("FSU")) {
							premigrationInputs.put("fileType", "ENV");
							preMigrationStatusDetails = generateCsvController.generateFile(premigrationInputs);
							if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
									&& Constants.SUCCESS
											.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
								ArrayList<String> filePathNames = new ArrayList<String>();
								ArrayList<String> zipFileName1 = new ArrayList<String>();
								if (preMigrationStatusDetails.containsKey("fPath")
										&& preMigrationStatusDetails.containsKey("zipFileName")) {
									filePathNames = (ArrayList<String>) preMigrationStatusDetails.get("fPath");
									zipFileName1 = (ArrayList<String>) preMigrationStatusDetails.get("zipFileName");
								}
								if (filePathNames.size() > 0)
									envPath = filePathNames.get(0);
								if (zipFileName1.size() > 0)
									envZip = zipFileName1.get(0);

								premigrationInputs.put("fileType", "CSV");
								preMigrationStatusDetails = generateCsvController.generateFile(premigrationInputs);
							}
						} else {
							preMigrationStatusDetails = generateCsvController.generateFile(premigrationInputs);
						}

						if ("Success".equalsIgnoreCase((String) preMigrationStatusDetails.get("status"))) {
							WorkFlowManagementEntity.setPreMigStatus("Completed");
							String efile = WorkFlowManagementEntity.getPreErrorFile();
							File myObj = new File(efile);
							if (myObj.exists())
								myObj.delete();
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);

						} else {
							WorkFlowManagementEntity.setPreMigStatus("Failure");
							String reason = preMigrationStatusDetails.get("reason").toString();
							if (preMigrationStatusDetails.containsKey("errorMsg") && StringUtils
									.isNotEmpty(preMigrationStatusDetails.get("errorMsg").toString())) {
								reason=preMigrationStatusDetails.get("errorMsg").toString();
							}
							String checkPath = WorkFlowManagementEntity.getPreErrorFile();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"PREMIGRATION" + "\n" + "------------------------------" + "\n", "",
												"");
										common.appendMessage(checkPath, "", reason, "", "");
									}

								}
							}
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
						}

					}

					if (negrowInputs != null && WorkFlowManagementEntity != null) {
						String neGrowState = "";
						String neGrowStatus = WorkFlowManagementEntity.getNeGrowStatus().toString();
						if (neGrowStatus.equalsIgnoreCase("InputsRequired")) {
							neGrowState = "runIndependent";
						}
						if (neGrowStatus.equalsIgnoreCase("Completed") || neGrowStatus.equalsIgnoreCase("Failure")) {
							neGrowState = "reRunIndependent";
						}
						if (!neGrowState.equalsIgnoreCase("runIndependent")) {
							Integer runTestIdNeGrow = WorkFlowManagementEntity.getRunNEGrowEntity().getId();
							WorkFlowManagementEntity.setRunNEGrowEntity(null);
							WorkFlowManagementEntity.setNeGrowStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							JSONObject delteTestParams = new JSONObject();
							delteTestParams.put("sessionId", sessionId);
							delteTestParams.put("serviceToken", serviceToken);
							delteTestParams.put("migrationType", "premigration");
							delteTestParams.put("migrationSubType", "NEGrow");
							delteTestParams.put("programId", Integer.parseInt(programId));
							delteTestParams.put("id", runTestIdNeGrow);
							JSONObject delteResultTestParams = runTestController.deleteRunTest(delteTestParams);
						}
						if (neGrowState.equalsIgnoreCase("runIndependent")) {
							WorkFlowManagementEntity.setNeGrowStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
						}
						JSONObject neGrowStatusDetails = runTestController.runTest(negrowInputs);
						if (neGrowStatusDetails != null && neGrowStatusDetails.containsKey("status")
								&& Constants.SUCCESS.equalsIgnoreCase(neGrowStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) neGrowStatusDetails
									.get("runTestEntity");
							RunTestEntity runTestEntityNeGrow = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());
							if (runTestEntityNeGrow != null && WorkFlowManagementEntity != null) {
								WorkFlowManagementEntity.setRunNEGrowEntity(runTestEntityNeGrow);
								WorkFlowManagementEntity.setNeGrowStatus("InProgress");
								WorkFlowManagementEntity.setStatus("InProgress");
								WorkFlowManagementEntity = workFlowManagementService
										.mergeWorkFlowMangement(WorkFlowManagementEntity);
								while (WorkFlowManagementEntity.getRunNEGrowEntity() != null
										&& WorkFlowManagementEntity.getRunNEGrowEntity().getId() > 0) {
									RunTestEntity runTestEntityNeGrowResult = workFlowManagementService
											.getRunTestEntity(runTestEntityNeGrow);

									if (runTestEntityNeGrowResult != null && "Completed"
											.equalsIgnoreCase(runTestEntityNeGrowResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntityNeGrowResult.getStatus())) {
											String efile = WorkFlowManagementEntity.getNegrowErrorFile();
											File myObj = new File(efile);
											if (myObj.exists())
												myObj.delete();
											WorkFlowManagementEntity.setNeGrowStatus("Completed");
											WorkFlowManagementEntity.setStatus("Completed");
										} else {
											WorkFlowManagementEntity
													.setNeGrowStatus(runTestEntityNeGrowResult.getStatus());
											WorkFlowManagementEntity.setStatus("Completed");
											if (WorkFlowManagementEntity.getRunMigTestEntity() == null
													&& !"Failure".equals(WorkFlowManagementEntity.getMigStatus())) {
												WorkFlowManagementEntity.setMigStatus("InputsRequired");
											}
											if (WorkFlowManagementEntity.getRunPostMigTestEntity() == null
													&& !"Failure".equals(WorkFlowManagementEntity.getPostMigStatus())) {
												WorkFlowManagementEntity.setPostMigStatus("InputsRequired");
											}
											workFlowManagementService.mergeWorkFlowMangement(WorkFlowManagementEntity);
											return Constants.FAIL;
										}
										WorkFlowManagementEntity = workFlowManagementService
												.mergeWorkFlowMangement(WorkFlowManagementEntity);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {

							WorkFlowManagementEntity = workFlowManagementService
									.getWorkFlowManagementEntity(workFlowId);
							WorkFlowManagementEntity.setNeGrowStatus("Failure");
							WorkFlowManagementEntity.setStatus("Completed");
							if (WorkFlowManagementEntity.getRunMigTestEntity() == null
									&& !"Failure".equals(WorkFlowManagementEntity.getMigStatus())) {
								WorkFlowManagementEntity.setMigStatus("InputsRequired");
							}
							if (WorkFlowManagementEntity.getRunPostMigTestEntity() == null
									&& !"Failure".equals(WorkFlowManagementEntity.getPostMigStatus())) {
								WorkFlowManagementEntity.setPostMigStatus("InputsRequired");
							}
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							String reason = neGrowStatusDetails.get("reason").toString();
							String checkPath = WorkFlowManagementEntity.getNegrowErrorFile();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"NEGROW" + "\n" + "--------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}

					// migration
					if (migrationInputs != null && WorkFlowManagementEntity != null) {
						String migState = "";
						String migStatus = WorkFlowManagementEntity.getMigStatus().toString();
						if (migStatus.equalsIgnoreCase("InputsRequired")) {
							migState = "runIndependent";
						}
						if (migStatus.equalsIgnoreCase("Completed") || migStatus.equalsIgnoreCase("Failure")) {
							migState = "reRunIndependent";
						}
						if (!migState.equalsIgnoreCase("runIndependent")) {
							Integer runTestIdMig = WorkFlowManagementEntity.getRunMigTestEntity().getId();
							WorkFlowManagementEntity.setRunMigTestEntity(null);
							WorkFlowManagementEntity.setMigStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							JSONObject delteTestParams = new JSONObject();
							delteTestParams.put("sessionId", sessionId);
							delteTestParams.put("serviceToken", serviceToken);
							delteTestParams.put("migrationType", "Migration");
							delteTestParams.put("migrationSubType", "PreCheck");
							delteTestParams.put("programId", Integer.parseInt(programId));
							delteTestParams.put("id", runTestIdMig);
							JSONObject delteResultTestParams = runTestController.deleteRunTest(delteTestParams);
						}
						if (migState.equalsIgnoreCase("runIndependent")) {
							WorkFlowManagementEntity.setMigStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
						}
						// if (delteResultTestParams != null
						// &&
						// "success".equalsIgnoreCase(delteResultTestParams.get("status").toString())) {
						JSONObject migrationStatusDetails = runTestController.runTest(migrationInputs);
						if (migrationStatusDetails != null && migrationStatusDetails.containsKey("status")
								&& Constants.SUCCESS
										.equalsIgnoreCase(migrationStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) migrationStatusDetails
									.get("runTestEntity");
							RunTestEntity runTestEntityMigration = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());
							if (runTestEntityMigration != null && WorkFlowManagementEntity != null) {
								if (runTestEntityMigration != null && WorkFlowManagementEntity != null) {
									WorkFlowManagementEntity.setRunMigTestEntity(runTestEntityMigration);
									WorkFlowManagementEntity.setMigStatus("InProgress");
									WorkFlowManagementEntity.setStatus("InProgress");
									WorkFlowManagementEntity = workFlowManagementService
											.mergeWorkFlowMangement(WorkFlowManagementEntity);
									while (WorkFlowManagementEntity.getRunMigTestEntity() != null
											&& WorkFlowManagementEntity.getRunMigTestEntity().getId() > 0) {
										RunTestEntity runTestEntityMigrationResult = workFlowManagementService
												.getRunTestEntity(runTestEntityMigration);

										// if (runTestEntityMigrationResult != null && ("Success"
										// .equalsIgnoreCase(runTestEntityMigrationResult.getStatus())
										// || "Failure".equalsIgnoreCase(runTestEntityMigrationResult.getStatus()))) {
										if (runTestEntityMigrationResult != null && "Completed"
												.equalsIgnoreCase(runTestEntityMigrationResult.getProgressStatus())) {
											if ("Success".equalsIgnoreCase(runTestEntityMigrationResult.getStatus())) {
												String efile = WorkFlowManagementEntity.getMigErrorFile();
												File myObj = new File(efile);
												if (myObj.exists())
													myObj.delete();
												WorkFlowManagementEntity.setMigStatus("Completed");
												WorkFlowManagementEntity.setStatus("Completed");
											} else {
												WorkFlowManagementEntity
														.setMigStatus(runTestEntityMigrationResult.getStatus());
												if (runTestEntityMigrationResult.getId() > 0) {
													String errorFilePath = WorkFlowManagementEntity.getMigErrorFile();
													writeMessageInfo(runTestEntityMigrationResult.getId(),
															errorFilePath);
												}
												WorkFlowManagementEntity.setStatus("Completed");
												if (WorkFlowManagementEntity.getRunNEGrowEntity() == null && !"Failure"
														.equals(WorkFlowManagementEntity.getNeGrowStatus())) {
													WorkFlowManagementEntity.setNeGrowStatus("InputsRequired");
												}
												if (WorkFlowManagementEntity.getRunPostMigTestEntity() == null
														&& !"Failure"
																.equals(WorkFlowManagementEntity.getPostMigStatus())) {
													WorkFlowManagementEntity.setPostMigStatus("InputsRequired");
												}
												workFlowManagementService
														.mergeWorkFlowMangement(WorkFlowManagementEntity);
												return Constants.FAIL;
											}
											WorkFlowManagementEntity = workFlowManagementService
													.mergeWorkFlowMangement(WorkFlowManagementEntity);
											break;
										}
										Thread.sleep(10000);
									}
								}
							}
						} else {
							WorkFlowManagementEntity = workFlowManagementService
									.getWorkFlowManagementEntity(workFlowId);
							WorkFlowManagementEntity.setMigStatus("Failure");
							WorkFlowManagementEntity.setStatus("Completed");
							if (WorkFlowManagementEntity.getRunNEGrowEntity() == null
									&& !"Failure".equals(WorkFlowManagementEntity.getNeGrowStatus())) {
								WorkFlowManagementEntity.setNeGrowStatus("InputsRequired");
							}
							if (WorkFlowManagementEntity.getRunPostMigTestEntity() == null
									&& !"Failure".equals(WorkFlowManagementEntity.getPostMigStatus())) {
								WorkFlowManagementEntity.setPostMigStatus("InputsRequired");
							}
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							String checkPath = WorkFlowManagementEntity.getMigErrorFile();
							String reason = migrationStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"MIGRATION" + "\n" + "--------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
						// } else {
						// resultMap.put("status", Constants.FAIL);
						// resultMap.put("sessionId", sessionId);
						// resultMap.put("serviceToken", serviceToken);
						// resultMap.put("reason", "existing test not able to delete");
						// }

					}

					// post migration
					if (postmigrationInputs != null && WorkFlowManagementEntity != null) {
						String postMigState = "";
						String postMigStatus = WorkFlowManagementEntity.getPostMigStatus().toString();
						if (postMigStatus.equalsIgnoreCase("InputsRequired")) {
							postMigState = "runIndependent";
						}
						if (postMigStatus.equalsIgnoreCase("Completed") || postMigStatus.equalsIgnoreCase("Failure")) {
							postMigState = "reRunIndependent";
						}
						if (!postMigState.equalsIgnoreCase("runIndependent")) {
							Integer runTestIdPostMig = WorkFlowManagementEntity.getRunPostMigTestEntity().getId();
							WorkFlowManagementEntity workFlowManagementEntityThread = workFlowManagementService
									.getWorkFlowManagementEntity(runTestIdPostMig);

							WorkFlowManagementEntity.setRunPostMigTestEntity(null);
							WorkFlowManagementEntity.setPostMigStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							JSONObject delteTestParams = new JSONObject();
							delteTestParams.put("sessionId", sessionId);
							delteTestParams.put("serviceToken", serviceToken);
							delteTestParams.put("migrationType", "PostMigration");
							delteTestParams.put("migrationSubType", "Audit");
							delteTestParams.put("programId", Integer.parseInt(programId));
							delteTestParams.put("id", runTestIdPostMig);
							JSONObject delteResultTestParams = runTestController.deleteRunTest(delteTestParams);
						}
						if (postMigState.equalsIgnoreCase("runIndependent")) {
							WorkFlowManagementEntity.setPostMigStatus("NotYetStarted");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
						}
						JSONObject postMigrationStatusDetails = runTestController.runTest(postmigrationInputs);

						if (postMigrationStatusDetails != null && postMigrationStatusDetails.containsKey("status")
								&& Constants.SUCCESS
										.equalsIgnoreCase(postMigrationStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationStatusDetails
									.get("runTestEntity");

							RunTestEntity runTestEntityPostMigration = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());

							if (runTestEntityPostMigration != null && WorkFlowManagementEntity != null) {
								WorkFlowManagementEntity.setRunPostMigTestEntity(runTestEntityPostMigration);

								WorkFlowManagementEntity = workFlowManagementService
										.mergeWorkFlowMangement(WorkFlowManagementEntity);

								WorkFlowManagementEntity.setPostMigStatus("InProgress");
								WorkFlowManagementEntity.setStatus("InProgress");
								WorkFlowManagementEntity = workFlowManagementService
										.mergeWorkFlowMangement(WorkFlowManagementEntity);
								while (WorkFlowManagementEntity.getRunPostMigTestEntity() != null
										&& WorkFlowManagementEntity.getRunPostMigTestEntity().getId() > 0) {
									RunTestEntity runTestEntityPostMigrationResult = workFlowManagementService
											.getRunTestEntity(runTestEntityPostMigration);

									if (runTestEntityPostMigrationResult != null && "Completed"
											.equalsIgnoreCase(runTestEntityPostMigrationResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntityPostMigrationResult.getStatus())) {
											String efile = WorkFlowManagementEntity.getPostErrorFile();
											File myObj = new File(efile);
											if (myObj.exists())
												myObj.delete();
											//bala
											WorkFlowManagementEntity.setPostMigStatus("Completed");
											if("Failure".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) )
											{
												if("NotExecuted".equalsIgnoreCase(WorkFlowManagementEntity.getSiteReportStatus()))
												{
													WorkFlowManagementEntity.setSiteReportStatus("InputsRequired");
												}
											}
											WorkFlowManagementEntity.setStatus("Completed");
										} else {
											String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
													+ runTestEntityPostMigrationResult.getResultFilePath().toString()
													+ "/" + runTestEntityPostMigrationResult.getResult().toString();
											File htmlFile = new File(filePath);
											// int wfmid = temp.get("0" + gndb);

											if (!htmlFile.exists() || runTestEntityPostMigrationResult.getResult()
													.toString().isEmpty()) {
												String checkPath = WorkFlowManagementEntity.getPostErrorFile();
												;
												String reason = "html result file not generated";
												if (checkPath != null) {
													File f = new File(checkPath);
													if (f.exists()) {
														common.appendMessage(checkPath, "", reason, "", "");
													} else {
														if (createNewFile(checkPath)) {
															common.appendMessage(checkPath, "",
																	"POSTMIGRATION" + "\n"
																			+ "------------------------------" + "\n",
																	"", "");
															common.appendMessage(checkPath, "", reason, "", "");
														}
													}
												}

											}
											//bala
											WorkFlowManagementEntity
													.setPostMigStatus(runTestEntityPostMigrationResult.getStatus());
											if("Failure".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) )
											{
												if("NotExecuted".equalsIgnoreCase(WorkFlowManagementEntity.getSiteReportStatus()))
												{
													WorkFlowManagementEntity.setSiteReportStatus("InputsRequired");
												}
											}
											WorkFlowManagementEntity.setStatus("Completed");
											workFlowManagementService.mergeWorkFlowMangement(WorkFlowManagementEntity);
											return Constants.FAIL;
										}
										WorkFlowManagementEntity = workFlowManagementService
												.mergeWorkFlowMangement(WorkFlowManagementEntity);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {
							WorkFlowManagementEntity = workFlowManagementService
									.getWorkFlowManagementEntity(workFlowId);
							WorkFlowManagementEntity.setPostMigStatus("Failure");
							if("Failure".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(WorkFlowManagementEntity.getPostMigStatus()) )
							{
								if("NotExecuted".equalsIgnoreCase(WorkFlowManagementEntity.getSiteReportStatus()))
								{
									WorkFlowManagementEntity.setSiteReportStatus("InputsRequired");
								}
							}
							WorkFlowManagementEntity.setStatus("Completed");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);

							String checkPath = WorkFlowManagementEntity.getPostErrorFile();
							String reason = postMigrationStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"POSTMIGRATION" + "\n" + "------------------------------" + "\n", "",
												"");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}
					if (postmigrationInputs != null && postmigrationInputs.containsKey("useCaseName")
							&& postmigrationInputs.get("useCaseName").toString().contains("Ran_Atp")) {
						String programname = postmigrationInputs.get("programName").toString();
						if (programname.contains("VZN-4G-USM-LIVE") && postmigrationInputs != null) {
							JSONObject runTestFormDetails = (JSONObject) postmigrationInputs.get("runTestFormDetails");
							List<JSONObject> useCase = (List<JSONObject>) runTestFormDetails.get("useCase");

							JSONObject bandNameParams = new JSONObject();
							bandNameParams.put("sessionId", postmigrationInputs.get("sessionId").toString());
							bandNameParams.put("serviceToken", postmigrationInputs.get("serviceToken").toString());
							bandNameParams.put("programId", postmigrationInputs.get("programId").toString());

							bandNameParams.put("ciqName", runTestFormDetails.get("ciqName").toString());
							List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) runTestFormDetails
									.get("neDetails");
							// String enbId = neDetails.get(0).get("neId");
							JSONObject bandNames = new JSONObject();
							// JSONArray bandName = new JSONArray();
							List<String> bandName = new LinkedList<>();
							Map<String, List<String>> bandNameList = new LinkedHashMap<>();
							String[] bandName1 = null;
							// neDetails
							for (Map<String, String> enb : enbList) {
								// Map<String, String> enb = neDetails.get(0);
								String enbId = enb.get("neId").toString();
								bandNameParams.put("eNBId", enbId);
								bandNames = runTestController.getBandName(bandNameParams);
								bandName1 = (String[]) bandNames.get("bandName");
								for (String str : bandName1) {
									bandName.add(str);
								}
								// JSONArray bandName = new JSONArray(bandName1);

								bandNameList.put(enbId, bandName);
							}

							/*
							 * for (Map<String, String> enb : enbList) { // String oldGnodeb =
							 * "0".concat(gnodebIdList.get(0)); Map<String, String> map = new HashMap<>();
							 * String singleGnob = "0" + enb.get("neId"); }
							 */

							// useCase.clear();
							List<Map> useCaseLst = new ArrayList<>();
							List<Map> sriptsDetails = new ArrayList<>();
							useCaseLst = runTestService.getUseCaseList(34, "postmigration", "RANATP");
							Map usecaseScriptDetails = useCaseLst.get(0);
							sriptsDetails = (List<Map>) usecaseScriptDetails.get("scripts");
							String useCaseName = (String) usecaseScriptDetails.get("useCaseName");
							sriptsDetails.get(0).put("useCaseName", useCaseName);
							List<JSONObject> sriptsDetailsList = sriptsDetails.stream()
									.map((map) -> new JSONObject(map)).collect(Collectors.toList());

							JSONObject singleUsecase = useCase.get(0);
							singleUsecase.put("useCaseName", (String) usecaseScriptDetails.get("useCaseName"));
							singleUsecase.put("useCaseId", usecaseScriptDetails.get("useCaseId"));
							singleUsecase.put("ucSleepInterval", usecaseScriptDetails.get("ucSleepInterval"));
							singleUsecase.put("executionSequence", usecaseScriptDetails.get("executionSequence"));
							List<JSONObject> useCases = new LinkedList<>();
							useCases.add(singleUsecase);
							useCase = useCases;

							runTestFormDetails.replace("useCase", useCase);
							runTestFormDetails.put("bandName", bandNameList);
							/*
							 * List<JSONObject> ls = new LinkedList<>(); JSONObject staticScripts = new
							 * JSONObject(); staticScripts.put("useCaseName", "RAN_ATP");
							 * staticScripts.put("scriptName", "ran_atp.pl"); staticScripts.put("scriptId",
							 * 2573); staticScripts.put("scriptExeSequence", 121);
							 * staticScripts.put("scriptSleepInterval", 1000);
							 * staticScripts.put("useGeneratedScript", "NO"); ls.add(staticScripts); scripts
							 * = ls;
							 */
							runTestFormDetails.put("scripts", sriptsDetailsList);
							postmigrationInputs.replace("runTestFormDetails", runTestFormDetails);

							postmigrationInputs.put("migrationSubType", "RANATP");
							JSONObject postMigrationRANATPStatusDetails = runTestController
									.runTestRanAtpWFM(postmigrationInputs);

							if (postMigrationRANATPStatusDetails != null
									&& postMigrationRANATPStatusDetails.containsKey("status")
									&& Constants.SUCCESS.equalsIgnoreCase(
											postMigrationRANATPStatusDetails.get("status").toString())) {

								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationRANATPStatusDetails
										.get("runTestEntity");
								LinkedList<Map<String, RunTestEntity>> allrunTestEntityMap = (LinkedList<Map<String, RunTestEntity>>) postMigrationRANATPStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) postMigrationRANATPStatusDetails
										.get("combinedresult");

								// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
								int count = 0;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);
									WorkFlowManagementEntity = workFlowManagementService
											.getWorkFlowManagementEntity(workFlowId);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityPostMigrationRANATP = runTestEntityMap.get(gndb);

									// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

									if (runTestEntityPostMigrationRANATP != null && WorkFlowManagementEntity != null) {
										WorkFlowManagementEntity
												.setRunRanAtpTestEntity(runTestEntityPostMigrationRANATP);

										WorkFlowManagementEntity = workFlowManagementService
												.mergeWorkFlowMangement(WorkFlowManagementEntity);

										// workFlowManagementEntityThread.setPostMigStatus("InProgress");
										WorkFlowManagementEntity = workFlowManagementService
												.mergeWorkFlowMangement(WorkFlowManagementEntity);
										while (WorkFlowManagementEntity.getRunRanAtpTestEntity() != null
												&& WorkFlowManagementEntity.getRunRanAtpTestEntity().getId() > 0) {
											RunTestEntity runTestEntityPostMigrationRANATPResult = workFlowManagementService
													.getRunTestEntity(runTestEntityPostMigrationRANATP);

											if (runTestEntityPostMigrationRANATPResult != null && "Completed"
													.equalsIgnoreCase(runTestEntityPostMigrationRANATPResult
															.getProgressStatus())) {
												if ("Success".equalsIgnoreCase(
														runTestEntityPostMigrationRANATPResult.getStatus())) {
													/*
													 * workFlowManagementEntityThread.setPostMigStatus("Completed");
													 */

												} else {
													/*
													 * workFlowManagementEntityThread
													 * .setPostMigStatus(runTestEntityPostMigrationRANATPResult.
													 * getStatus()) ;
													 */
												}
												WorkFlowManagementEntity = workFlowManagementService
														.mergeWorkFlowMangement(WorkFlowManagementEntity);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {
								// return Constants.FAIL;
							}

						}

					}
					WorkFlowManagementEntity.setStatus("Completed");
					WorkFlowManagementEntity = workFlowManagementService
							.mergeWorkFlowMangement(WorkFlowManagementEntity);
					return Constants.SUCCESS;
				});
			}
			executorservice.shutdown();

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@Override
	public String runWFM(JSONObject premigrationInputsThread, JSONObject migrationInputsThread,
			JSONObject postmigrationInputsThread, JSONObject negrowInputsThread,JSONObject preaAuditInputsThread, JSONObject neStatusInputsThread, List<Map<String, String>> enbList,
			String programid, String programname, Map<String, Integer> temp) {
		try {
			HashMap<String, HashMap<String, ArrayList<String>>> filedata = new HashMap<String, HashMap<String, ArrayList<String>>>();
			ExecutorService executorservice = Executors.newFixedThreadPool(enbList.size());

			for (Map<String, String> enbMap : enbList) {
				executorservice.submit(() -> {
					Thread.currentThread().setName("WFM_" + temp.get("0" + enbMap.get("neId")));
					logger.error("-----------" + Thread.currentThread().getId() + "-------" + Thread.currentThread().getName());
					JSONObject premigrationInput = null;
					JSONObject negrowInput = null;
					JSONObject migrationInput = null;
					JSONObject postmigrationInput = null;
					JSONObject preAuditInput = null;
					JSONObject neStatusInput = null;
					// GenerateCsvController generateCsvController = new GenerateCsvController();
					WorkFlowManagementEntity workFlowManagementEntityThread = workFlowManagementService
							.getWorkFlowManagementEntity(temp.get("0" + enbMap.get("neId")));
					try {
						LocalDateTime lt1 = LocalDateTime.now();
						workFlowManagementEntityThread.setWfmThreadName("WFM_" + temp.get("0" + enbMap.get("neId")));
						workFlowManagementEntityThread = workFlowManagementService
								.mergeWorkFlowMangement(workFlowManagementEntityThread);

						// RunTestController runTestController = new RunTestController();
						List<Map<String, String>> singleEnbList = new ArrayList<>();
						singleEnbList.add(enbMap);
						if (premigrationInputsThread != null) {
							premigrationInput = new JSONObject(premigrationInputsThread);
						}
						if (negrowInputsThread != null) {
							negrowInput = new JSONObject(negrowInputsThread);
						}
						if (neStatusInputsThread != null) {
							neStatusInput = new JSONObject(neStatusInputsThread);
						}
						if (preaAuditInputsThread != null) {
							preAuditInput = new JSONObject(preaAuditInputsThread);
						}
						if (migrationInputsThread != null) {
							migrationInput = new JSONObject(migrationInputsThread);
						}
						if (postmigrationInputsThread != null) {
							postmigrationInput = new JSONObject(postmigrationInputsThread);
						}

						//if (!programname.contains("FSU")) {
							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(Integer.parseInt(programid));
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel.setEnbId(enbMap.get("neId").toString());
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							NeMappingEntity neMappingEntity = neMappingEntities.get(0);
							String neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
									.toString();
							String neName = neMappingEntity.getNetworkConfigEntity().getNeName().toString();
							String lsmId = String.valueOf(neMappingEntity.getNetworkConfigEntity().getId());
							if (premigrationInput != null) {
								premigrationInput.put("neVersion", neVersion);
							}
							if (migrationInput != null) {
								Map inputJsonmigGrowTemp = (JSONObject) new JSONParser()
										.parse(migrationInput.toJSONString((Map) migrationInput.get("runTestFormDetails")));
								inputJsonmigGrowTemp.put("lsmVersion", neVersion);
								inputJsonmigGrowTemp.put("lsmName", neName);
								inputJsonmigGrowTemp.put("lsmId", lsmId);
								migrationInput.put("runTestFormDetails", inputJsonmigGrowTemp);
							}
							if (postmigrationInput != null) {
								Map inputJsonpostmigGrowTemp = (JSONObject) new JSONParser().parse(postmigrationInput
										.toJSONString((Map) postmigrationInput.get("runTestFormDetails")));
								inputJsonpostmigGrowTemp.put("lsmVersion", neVersion);
								inputJsonpostmigGrowTemp.put("lsmName", neName);
								inputJsonpostmigGrowTemp.put("lsmId", lsmId);
								postmigrationInput.put("runTestFormDetails", inputJsonpostmigGrowTemp);
							}
							if (preAuditInput != null) {
								Map inputJsonpreAuditGrowTemp = (JSONObject) new JSONParser().parse(preAuditInput
										.toJSONString((Map) preAuditInput.get("runTestFormDetails")));
								inputJsonpreAuditGrowTemp.put("lsmVersion", neVersion);
								inputJsonpreAuditGrowTemp.put("lsmName", neName);
								inputJsonpreAuditGrowTemp.put("lsmId", lsmId);
								preAuditInput.put("runTestFormDetails", inputJsonpreAuditGrowTemp);
							}
							if (neStatusInput != null) {
								Map inputJsonNeStatusGrowTemp = (JSONObject) new JSONParser().parse(neStatusInput
										.toJSONString((Map) neStatusInput.get("runTestFormDetails")));
								inputJsonNeStatusGrowTemp.put("lsmVersion", neVersion);
								inputJsonNeStatusGrowTemp.put("lsmName", neName);
								inputJsonNeStatusGrowTemp.put("lsmId", lsmId);
								neStatusInput.put("runTestFormDetails", inputJsonNeStatusGrowTemp);
							}
							if (negrowInput != null) {
								Map inputJsonNEGrowTemp = (JSONObject) new JSONParser()
										.parse(negrowInput.toJSONString((Map) negrowInput.get("runTestFormDetails")));
								inputJsonNEGrowTemp.put("lsmVersion", neVersion);
								inputJsonNEGrowTemp.put("lsmName", neName);
								inputJsonNEGrowTemp.put("lsmId", lsmId);
								List<String> useCase = (List<String>) inputJsonNEGrowTemp.get("useCase");
								if (programname.contains("VZN-5G-MM")) {
									if (neVersion.equals("20.B.0")) {
										neVersion = "20B";
									}
									else if(neVersion.equals("20.C.0")) {
										neVersion = "20C";
									} else if(neVersion.contains("21.A")) {
										neVersion = "21A";
									}else if(neVersion.contains("21.B")) {
										neVersion = "21B";
									}else if(neVersion.contains("21.C")) {
										neVersion = "21C";
									}else if(neVersion.contains("21.D")) {
										neVersion = "21D";
									}//22A
									else if(neVersion.contains("22.A")) {
										neVersion = "22A";
									}else if(neVersion.contains("22.C")) {
										neVersion = "22C";
									}
									else {
										neVersion = "20A";
									}
									String ver1 = neVersion;
									useCase = useCase.stream().map(usecase -> usecase.concat(ver1))
											.collect(Collectors.toList());
									// useCase.replaceAll(usecase -> usecase.concat(ver));
									inputJsonNEGrowTemp.replace("useCase", useCase);
								}
							/*	if (!programname.contains("VZN-4G-FSU")) {
								if (useCase.size() == 3) {
									String pnpUsecase = "pnp";
									if (programname.contains("VZN-5G-MM")) {
										pnpUsecase = "pnp" + neVersion;
									}

									useCase.clear();
									useCase.add(pnpUsecase);
									inputJsonNEGrowTemp.replace("useCase", useCase);
								}
								}*/
								negrowInput.put("runTestFormDetails", inputJsonNEGrowTemp);
							}
						//}

						if (premigrationInput != null) {
							LocalDateTime lpre1 = LocalDateTime.now();
							String envPath = null;
							String envZip = null;
							String CommPath = null;
							String CommZip = null;
							JSONObject preMigrationStatusDetails = new JSONObject();
							LinkedHashMap<String, String> enbResultMap = new LinkedHashMap<>();
							premigrationInput.put("neDetails", singleEnbList);
							premigrationInput.put("wfmStatus", "true");
							if (!programname.contains("VZN-5G-MM") && !programname.contains("VZN-5G-DSS")
									&& !programname.contains("FSU") && !programname.contains("5G-CBAND")) {
								for (Map<String, String> enb : singleEnbList) {
									premigrationInput.put("enbId", enb.get("neId"));
									premigrationInput.put("enbName", enb.get("neName"));
									premigrationInput.remove("neDetails");
									preMigrationStatusDetails = generateCsvController
											.generateCiqNeBasedFiles(premigrationInput);
									if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
											&& Constants.SUCCESS
													.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
										LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) preMigrationStatusDetails
												.get("CombinedResult");
										enbResultMap.putAll(enbResult);
									} else {
										break;
									}
								}
							} else if (programname.contains("FSU")) {
								premigrationInput.put("fileType", "ENV");
								preMigrationStatusDetails = generateCsvController.generateFile(premigrationInput);
								if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
										&& Constants.SUCCESS
												.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
									ArrayList<String> filePathNames = new ArrayList<String>();
									ArrayList<String> zipFileName1 = new ArrayList<String>();
									if (preMigrationStatusDetails.containsKey("fPath")
											&& preMigrationStatusDetails.containsKey("zipFileName")) {
										filePathNames = (ArrayList<String>) preMigrationStatusDetails.get("fPath");
										zipFileName1 = (ArrayList<String>) preMigrationStatusDetails.get("zipFileName");
									}
									if (filePathNames.size() > 0)
										envPath = filePathNames.get(0);
									if (zipFileName1.size() > 0)
										envZip = zipFileName1.get(0);
									premigrationInput.put("fileType", "COMMISSION_SCRIPT");
									preMigrationStatusDetails = generateCsvController.generateFile(premigrationInput);
									if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
											&& Constants.SUCCESS
													.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
										ArrayList<String> filePathNames2 = new ArrayList<String>();
										ArrayList<String> zipFileName2 = new ArrayList<String>();
										if (preMigrationStatusDetails.containsKey("fPath")
												&& preMigrationStatusDetails.containsKey("zipFileName")) {
											filePathNames2 = (ArrayList<String>) preMigrationStatusDetails.get("fPath");
											zipFileName2 = (ArrayList<String>) preMigrationStatusDetails.get("zipFileName");
										}
										if (filePathNames2.size() > 0)
											CommPath = filePathNames2.get(0);
										if (zipFileName2.size() > 0)
											CommZip = zipFileName2.get(0);

									premigrationInput.put("fileType", "CSV");
									preMigrationStatusDetails = generateCsvController.generateFile(premigrationInput);
									}
								}
							} else {
								preMigrationStatusDetails = generateCsvController.generateFile(premigrationInput);
							}
							if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
									&& Constants.SUCCESS
											.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
								LinkedHashMap<String, String> enbResult = new LinkedHashMap<>();
								if (!programname.contains("VZN-5G-MM") && !programname.contains("VZN-5G-DSS")
										&& !programname.contains("FSU") && !programname.contains("5G-CBAND")) {
									enbResult = enbResultMap;
								} else {
									enbResult = (LinkedHashMap<String, String>) preMigrationStatusDetails
											.get("CombinedResult");
								}
								int count = 0;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									String zipFileName = null;
									String fPath = null;
									ArrayList<String> filePathNames = new ArrayList<String>();
									ArrayList<String> zipFileName1 = new ArrayList<String>();
									if (preMigrationStatusDetails.containsKey("fPath")
											&& preMigrationStatusDetails.containsKey("zipFileName")) {
										filePathNames = (ArrayList<String>) preMigrationStatusDetails.get("fPath");
										zipFileName1 = (ArrayList<String>) preMigrationStatusDetails.get("zipFileName");
									}
									if (filePathNames.size() > 0)
										fPath = filePathNames.get(0);
									if (zipFileName1.size() > 0)
										zipFileName = zipFileName1.get(0);

									if (!programname.contains("FSU")) {
										workFlowManagementEntityThread.setFileNamePre(zipFileName);
										workFlowManagementEntityThread.setFilePathPre(fPath);
									} else {
										workFlowManagementEntityThread.setCsvPath(fPath);
										workFlowManagementEntityThread.setCsvZipName(zipFileName);
										workFlowManagementEntityThread.setEnvPath(envPath);
										workFlowManagementEntityThread.setEnvZipName(envZip);
										workFlowManagementEntityThread.setCommPath(CommPath);
										workFlowManagementEntityThread.setCommZipName(CommZip);
									}

									count++;
									if (preMigrationStatusDetails.containsKey("paths4G")) {
										HashMap<String, ArrayList<String>> map = new HashMap<>();
										map = (HashMap<String, ArrayList<String>>) preMigrationStatusDetails.get("paths4G");
										ArrayList<String> arr = new ArrayList<>();
										arr = map.get("COMMOSION_SCRIPTS");
										if (arr != null && arr.size() == 2) {
											workFlowManagementEntityThread.setCommPath(arr.get(0));
											workFlowManagementEntityThread.setCommZipName(arr.get(1));
										}
										arr = map.get("ENV");

										if (arr != null && arr.size() == 2) {
											workFlowManagementEntityThread.setEnvPath(arr.get(0));
											workFlowManagementEntityThread.setEnvZipName(arr.get(1));
										}
										arr = map.get("CSV");

										if (arr != null && arr.size() == 2) {
											workFlowManagementEntityThread.setCsvPath(arr.get(0));
											workFlowManagementEntityThread.setCsvZipName(arr.get(1));
										}
									}
							/*	if(preMigrationStatusDetails.containsValue("Failed to Generate Cell Grow Template")) {
																			Date today1 = new Date();
										String time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();
										logger.error("premigration is completed at:"+time);
										workFlowManagementEntityThread.setPreMigStatus("Completed");
																				Date today2 = new Date();
										String time1 = today2.getHours() + ":" + today2.getMinutes() + ":" + today2.getSeconds();
										logger.error("After premigration is completed at:"+time1);
									workFlowManagementEntityThread.setTestDescription("cell grow fail");
										 }
										else
										 {		*/									Date today1 = new Date();
											String time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();
											logger.error("Cell grow is success premigration is completed at:"+time);
											 workFlowManagementEntityThread.setPreMigStatus("Completed");
											 												Date today2 = new Date();
												String time1 = today2.getHours() + ":" + today2.getMinutes() + ":" + today2.getSeconds();
												logger.error("cell grow is success After premigration is completed at:"+time1);
									//	 }
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
							} else {
								// resultMap.put("status", Constants.FAIL);
								// resultMap.put("sessionId", sessionId);
								// resultMap.put("serviceToken", serviceToken);
								// resultMap.put("reason", preMigrationStatusDetails.get("reason").toString());
								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									//OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									workFlowManagementEntityThread.setPreMigStatus("Failure");
									workFlowManagementEntityThread.setStatus("Completed");
									workFlowManagementEntityThread.setNeUpStatus("CannotStart");
									workFlowManagementEntityThread.setPreAuditStatus("CannotStart");
									workFlowManagementEntityThread.setNeGrowStatus("CannotStart");
									workFlowManagementEntityThread.setPostMigStatus("CannotStart");
									workFlowManagementEntityThread.setMigStatus("CannotStart");
									// display failure logs
									String reason = preMigrationStatusDetails.get("reason").toString();
									if (preMigrationStatusDetails.containsKey("errorMsg") && StringUtils
											.isNotEmpty(preMigrationStatusDetails.get("errorMsg").toString())) {
										reason=preMigrationStatusDetails.get("errorMsg").toString();
									}
									String checkPath = workFlowManagementEntityThread.getPreErrorFile();
									
									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "",
														"PREMIGRATION" + "\n" + "------------------------------" + "\n", "",
														"");
												common.appendMessage(checkPath, "", reason, "", "");
											}

										}
									}	
//									ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
//									ovScheduledEntity.setPreMigGrowStatus("Failure");
//									ovScheduledEntity.setEnvStatus("Failure");
//									ovScheduledEntity.setPreMigStatus("Failure");
//									ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);  
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lpre2 = LocalDateTime.now();
								long dshsec = Duration.between(lpre1, lpre2).getSeconds();
								logger.error("Duration for thread in Premigration WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								
								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								return Constants.FAIL;
							}
							
							LocalDateTime lpre2 = LocalDateTime.now();
							long dshsec = Duration.between(lpre1, lpre2).getSeconds();
							logger.error("Duration for thread in Premigration WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
									dshsec/3600, (dshsec%3600)/60, dshsec%60));
							
						}

						// NEGrow

						/*
						 * for(int i=o;i<3;i++) {
						 * 
						 * }
						 */if (premigrationInput != null && negrowInput != null)// && WorkFlowManagementEntity != null)

						{
							LocalDateTime lne1 = LocalDateTime.now();
							Map inputJsonNEGrow = (JSONObject) new JSONParser()
									.parse(negrowInput.toJSONString((Map) negrowInput.get("runTestFormDetails")));
							inputJsonNEGrow.put("neDetails", singleEnbList);
							negrowInput.put("runTestFormDetails", inputJsonNEGrow);

							// JSONObject negrowStatusDetails = runTestController.runTest(negrowInputs);
							JSONObject negrowStatusDetails = new JSONObject();
							// if (programName.contains("VZN-5G-MM")) {

							//System.out.println("----------------------------------" + negrowInput);
							negrowStatusDetails = runTestController.runTestWFM(negrowInput, programid,
									negrowInput.get("migrationSubType").toString(),
									premigrationInput.get("ciqFileName").toString());
							// }
							// else {
							// negrowStatusDetails = runTestController.runTest(negrowInputs);
							// }

							if (negrowStatusDetails != null && negrowStatusDetails.containsKey("status")
									&& Constants.SUCCESS.equalsIgnoreCase(negrowStatusDetails.get("status").toString())) {
								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) negrowStatusDetails
										.get("runTestEntity");
								List<Map<String, RunTestEntity>> allrunTestEntityMap = (List<Map<String, RunTestEntity>>) negrowStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) negrowStatusDetails
										.get("combinedresult");
								/*
								 * enbResult.keySet().forEach(genbId -> { String result =
								 * enbResult.get(genbId).toString(); System.out.println("key: " +
								 * genbId.toString() + " value: " + result);
								 * 
								 * 
								 * });
								 */
								int count = 0;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityNEGrow = runTestEntityMap.get(gndb);

									if (runTestEntityNEGrow != null && workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread.setRunNEGrowEntity(runTestEntityNEGrow);
										workFlowManagementEntityThread.setNeGrowStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunNEGrowEntity() != null
												&& workFlowManagementEntityThread.getRunNEGrowEntity().getId() > 0) {
											RunTestEntity runTestEntityNEGrowResult = workFlowManagementService
													.getRunTestEntity(runTestEntityNEGrow);

											if (runTestEntityNEGrowResult != null && "Completed"
													.equalsIgnoreCase(runTestEntityNEGrowResult.getProgressStatus())) {
												if ("Success".equalsIgnoreCase(runTestEntityNEGrowResult.getStatus())) {
													workFlowManagementEntityThread.setNeGrowStatus("Completed");
												} else {
													workFlowManagementEntityThread
															.setNeGrowStatus(runTestEntityNEGrowResult.getStatus());
													workFlowManagementEntityThread.setStatus("Completed");
													if (workFlowManagementEntityThread.getRunNEStatusTestEntity() == null
															&& !"Failure".equals(workFlowManagementEntityThread.getNeUpStatus())) {
														workFlowManagementEntityThread.setNeUpStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getPreAuditStatus())) {
														workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getMigStatus())) {
														workFlowManagementEntityThread.setMigStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getPostMigStatus())) {
														workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
													}
													workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													return Constants.FAIL;
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;

											}
											Thread.sleep(10000);
										}

									}
								}
							} else {

								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);

									// code for display failure
									String checkPath = workFlowManagementEntityThread.getNegrowErrorFile();
									String reason = negrowStatusDetails.get("reason").toString();
									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "",
														"NEGROW" + "\n" + "--------------------------" + "\n", "", "");
												common.appendMessage(checkPath, "", reason, "", "");
											}
										}
									}
									workFlowManagementEntityThread.setNeGrowStatus("Failure");
									workFlowManagementEntityThread.setStatus("Completed");
									if (workFlowManagementEntityThread.getRunNEStatusTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getNeUpStatus())) {
										workFlowManagementEntityThread.setNeUpStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getPreAuditStatus())) {
										workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunMigTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getMigStatus())) {
										workFlowManagementEntityThread.setMigStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
										workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
									}
									ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
									ovScheduledEntity.setNeGrowStatus("Failure");
									ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity); 
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lne2 = LocalDateTime.now();
								long dshsec = Duration.between(lne1, lne2).getSeconds();
								logger.error("Duration for thread in NeGrow WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								
								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								return Constants.FAIL;
							}
							
							LocalDateTime lne2 = LocalDateTime.now();
							long dshsec = Duration.between(lne1, lne2).getSeconds();
							logger.error("Duration for thread in NeGrow WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
									dshsec/3600, (dshsec%3600)/60, dshsec%60));

						}
						 JSONObject neStatusDetails = new JSONObject();
							
							if (neStatusInput != null)

						{

							LocalDateTime lpost1 = LocalDateTime.now();
							Map inputJsonNeStatus = (JSONObject) new JSONParser()
									.parse(neStatusInput.toJSONString((Map) neStatusInput.get("runTestFormDetails")));
							inputJsonNeStatus.put("neDetails", singleEnbList);
							neStatusInput.put("runTestFormDetails", inputJsonNeStatus);
							neStatusDetails = runTestController.runTestWFM(neStatusInput, programid, "", "");
							

							if (neStatusDetails != null && neStatusDetails.containsKey("status")
									&& Constants.SUCCESS.equalsIgnoreCase(neStatusDetails.get("status").toString())) {
								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) neStatusDetails
										.get("runTestEntity");
								List<Map<String, RunTestEntity>> allrunTestEntityMap = (List<Map<String, RunTestEntity>>) neStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) neStatusDetails
										.get("combinedresult");

								// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
								int count = 0;
								String filePath = null;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									// System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityNEUPStatus = runTestEntityMap.get(gndb);

									// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

									if (runTestEntityNEUPStatus != null && workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread.setRunNEStatusTestEntity(runTestEntityNEUPStatus);

										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										workFlowManagementEntityThread.setNeUpStatus("InProgress");

										// workFlowManagementEntityThread.setPostMigStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunNEStatusTestEntity() != null
												&& workFlowManagementEntityThread.getRunNEStatusTestEntity()
														.getId() > 0) {
											RunTestEntity runTestEntityneStatusResult = workFlowManagementService
													.getRunTestEntity(runTestEntityNEUPStatus);

											if (runTestEntityneStatusResult != null && "Completed".equalsIgnoreCase(
													runTestEntityneStatusResult.getProgressStatus())) {
												if ("Success"
														.equalsIgnoreCase(runTestEntityneStatusResult.getStatus())) {

													workFlowManagementEntityThread.setNeUpStatus("Completed");
												
												} else {
													
													workFlowManagementEntityThread
															.setNeUpStatus(runTestEntityneStatusResult.getStatus());
													if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getPreAuditStatus())) {
														workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getMigStatus())) {
														workFlowManagementEntityThread.setMigStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
															&& !"Failure".equals(workFlowManagementEntityThread
																	.getPostMigStatus())) {
														workFlowManagementEntityThread
																.setPostMigStatus("InputsRequired");
													}
													workFlowManagementEntityThread.setStatus("Completed");
													workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													return Constants.FAIL;
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {

								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);

									// code for display failure logs
									String checkPath = workFlowManagementEntityThread.getNEStatusErrorFile();
									String reason = neStatusDetails.get("reason").toString();

									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "",
														"NESTATUS" + "\n" + "------------------------------" + "\n", "",
														"");
												common.appendMessage(checkPath, "", reason, "", "");
											}
										}
									}
									workFlowManagementEntityThread.setNeUpStatus("Failure");
									if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
											&& !"Failure".equals(
													workFlowManagementEntityThread.getPreAuditStatus())) {
										workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunMigTestEntity() == null
											&& !"Failure".equals(
													workFlowManagementEntityThread.getMigStatus())) {
										workFlowManagementEntityThread.setMigStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread
													.getPostMigStatus())) {
										workFlowManagementEntityThread
												.setPostMigStatus("InputsRequired");
									}
									workFlowManagementEntityThread.setStatus("Completed");
									/*
									 * ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
									 * ovScheduledEntity.setPostMigAuditStatus("Failure"); ovScheduledEntity =
									 * ovScheduledTaskService .mergeOvScheduledDetails(ovScheduledEntity);
									 */
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lpost2 = LocalDateTime.now();
								long dshsec = Duration.between(lpost1, lpost2).getSeconds();
								logger.error("Duration for thread in NE-STATUS WFM - NE : " + enbMap.get("neId")
										+ "(HH:mm:ss) : " + String.format("%d:%02d:%02d", dshsec / 3600,
												(dshsec % 3600) / 60, dshsec % 60));

								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : "
										+ String.format("%d:%02d:%02d", dshsec / 3600, (dshsec % 3600) / 60,
												dshsec % 60));
								return Constants.FAIL;
							}

							LocalDateTime lpost2 = LocalDateTime.now();
							long dshsec = Duration.between(lpost1, lpost2).getSeconds();
							logger.error("Duration for thread in NE-STATUS WFM - NE : " + enbMap.get("neId")
									+ "(HH:mm:ss) : "
									+ String.format("%d:%02d:%02d", dshsec / 3600, (dshsec % 3600) / 60, dshsec % 60));

						}
						 
						 JSONObject preAuditStatusDetails = new JSONObject();
							
							if (preAuditInput != null)

						{

							LocalDateTime lpost1 = LocalDateTime.now();
							Map inputJsonpostmigGrow = (JSONObject) new JSONParser()
									.parse(preAuditInput.toJSONString((Map) preAuditInput.get("runTestFormDetails")));
							inputJsonpostmigGrow.put("neDetails", singleEnbList);
							preAuditInput.put("runTestFormDetails", inputJsonpostmigGrow);
						
							preAuditStatusDetails = runTestController.runTestWFM(preAuditInput, programid, "", "");
							
							if (preAuditStatusDetails != null && preAuditStatusDetails.containsKey("status")
									&& Constants.SUCCESS
											.equalsIgnoreCase(preAuditStatusDetails.get("status").toString())) {
								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) preAuditStatusDetails
										.get("runTestEntity");
								List<Map<String, RunTestEntity>> allrunTestEntityMap = (List<Map<String, RunTestEntity>>) preAuditStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) preAuditStatusDetails
										.get("combinedresult");
								
								int count = 0;
								String filePath = null;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									// System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntitypreAudit = runTestEntityMap.get(gndb);

									// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

									if (runTestEntitypreAudit != null && workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread.setRunPreAuditTestEntity(runTestEntitypreAudit);

										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										workFlowManagementEntityThread.setPreAuditStatus("InProgress");

										// workFlowManagementEntityThread.setPostMigStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunPreAuditTestEntity() != null
												&& workFlowManagementEntityThread.getRunPreAuditTestEntity()
														.getId() > 0) {
											RunTestEntity runTestEntitypreAuditResult = workFlowManagementService
													.getRunTestEntity(runTestEntitypreAudit);

											if (runTestEntitypreAuditResult != null && "Completed".equalsIgnoreCase(
													runTestEntitypreAuditResult.getProgressStatus())) {
												if ("Success"
														.equalsIgnoreCase(runTestEntitypreAuditResult.getStatus())) {
													
													workFlowManagementEntityThread.setPreAuditStatus("Completed");
													/*if ("Failure".equalsIgnoreCase(
															workFlowManagementEntityThread.getPreAuditStatus())
															|| "Completed".equalsIgnoreCase(
																	workFlowManagementEntityThread.getPreAuditStatus())
															|| "Success".equalsIgnoreCase(workFlowManagementEntityThread
																	.getPreAuditStatus())) {
														if ("NotExecuted".equalsIgnoreCase(
																workFlowManagementEntityThread.getSiteReportStatus())) {
															workFlowManagementEntityThread
																	.setSiteReportStatus("InputsRequired");
														}
													}*/
												} else {
													filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
															+ runTestEntitypreAuditResult.getResultFilePath().toString()
															+ "/" + runTestEntitypreAuditResult.getResult().toString();
													File htmlFile = new File(filePath);
													if (!htmlFile.exists() || runTestEntitypreAuditResult.getResult()
															.toString().isEmpty()) {
														String checkPath = workFlowManagementEntityThread
																.getPREAUDITErrorFile();
														String reason = "html result file not generated";
														if (checkPath != null) {
															File f = new File(checkPath);
															if (f.exists()) {
																common.appendMessage(checkPath, "", reason, "", "");
															} else {
																if (createNewFile(checkPath)) {
																	common.appendMessage(checkPath, "",
																			"PREAUDIT" + "\n"
																					+ "------------------------------"
																					+ "\n",
																			"", "");
																	common.appendMessage(checkPath, "", reason, "", "");
																}
															}
														}

													}
													workFlowManagementEntityThread
															.setPreAuditStatus(runTestEntitypreAuditResult.getStatus());
													
													if (workFlowManagementEntityThread.getRunMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getMigStatus())) {
														workFlowManagementEntityThread.setMigStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getPostMigStatus())) {
														workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
													}

													/*if ("Failure".equalsIgnoreCase(
															workFlowManagementEntityThread.getPreAuditStatus())
															|| "Completed".equalsIgnoreCase(
																	workFlowManagementEntityThread.getPreAuditStatus())
															|| "Success".equalsIgnoreCase(workFlowManagementEntityThread
																	.getPreAuditStatus())) {
														if ("NotExecuted".equalsIgnoreCase(
																workFlowManagementEntityThread.getSiteReportStatus())) {
															workFlowManagementEntityThread
																	.setSiteReportStatus("InputsRequired");
														}
													}*/

													workFlowManagementEntityThread.setStatus("Completed");
													workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													return Constants.FAIL;
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {

								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);

									// code for display failure logs
									String checkPath = workFlowManagementEntityThread.getPREAUDITErrorFile();
									String reason = preAuditStatusDetails.get("reason").toString();

									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "", "PREAUDIT" + "\n"
														+ "------------------------------" + "\n", "", "");
												common.appendMessage(checkPath, "", reason, "", "");
											}
										}
									}
									workFlowManagementEntityThread.setPreAuditStatus("Failure");
									if ("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPreAuditStatus())
											|| "Completed"
													.equalsIgnoreCase(workFlowManagementEntityThread.getPreAuditStatus())
											|| "Success".equalsIgnoreCase(
													workFlowManagementEntityThread.getPreAuditStatus())) {
										/*if ("NotExecuted".equalsIgnoreCase(
												workFlowManagementEntityThread.getSiteReportStatus())) {
											workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
										}*/
										if (workFlowManagementEntityThread.getRunMigTestEntity() == null
												&& !"Failure".equals(
														workFlowManagementEntityThread.getMigStatus())) {
											workFlowManagementEntityThread.setMigStatus("InputsRequired");
										}
										if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
												&& !"Failure".equals(
														workFlowManagementEntityThread.getPostMigStatus())) {
											workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
										}
									}
									workFlowManagementEntityThread.setStatus("Completed");
									/*ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
									ovScheduledEntity.setPostMigAuditStatus("Failure");
									ovScheduledEntity = ovScheduledTaskService
											.mergeOvScheduledDetails(ovScheduledEntity);*/
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lpost2 = LocalDateTime.now();
								long dshsec = Duration.between(lpost1, lpost2).getSeconds();
								logger.error("Duration for thread in PRE-Audit WFM - NE : "
										+ enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d",
												dshsec / 3600, (dshsec % 3600) / 60, dshsec % 60));

								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : "
										+ String.format("%d:%02d:%02d", dshsec / 3600, (dshsec % 3600) / 60,
												dshsec % 60));
								return Constants.FAIL;
							}

							LocalDateTime lpost2 = LocalDateTime.now();
							long dshsec = Duration.between(lpost1, lpost2).getSeconds();
							logger.error("Duration for thread in PRE-Audit WFM - NE : " + enbMap.get("neId")
									+ "(HH:mm:ss) : "
									+ String.format("%d:%02d:%02d", dshsec / 3600, (dshsec % 3600) / 60, dshsec % 60));

						}

						JSONObject migrationStatusDetails = new JSONObject();
						if (premigrationInput != null && migrationInput != null)// && WorkFlowManagementEntity != null)

						{
							LocalDateTime lmig1 = LocalDateTime.now();
							Map inputJsonmigGrow = (JSONObject) new JSONParser()
									.parse(migrationInput.toJSONString((Map) migrationInput.get("runTestFormDetails")));
							inputJsonmigGrow.put("neDetails", singleEnbList);
							migrationInput.put("runTestFormDetails", inputJsonmigGrow);
							// if (programName.contains("VZN-5G-MM")) {
														Date today1 = new Date();
							String time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();
							logger.error("DB LOGS:migrationstatusdetails of runtestwfkm is started at:"+time+"NE ID : "+enbMap.get("neId"));
							migrationStatusDetails = runTestController.runTestWFM(migrationInput, programid,
									migrationInput.get("migrationSubType").toString(),
									premigrationInput.get("ciqFileName").toString());
																Date today2 = new Date();
							String time1 = today2.getHours() + ":" + today2.getMinutes() + ":" + today2.getSeconds();
							logger.error("DB LOGS:migrationstatus details of runwfm is completed at:"+time1+"NE ID : "+enbMap.get("neId"));
							// }
							// else {
							// migrationStatusDetails = runTestController.runTest(migrationInputs);
							//
							// }

							if (migrationStatusDetails != null && migrationStatusDetails.containsKey("status")
									&& Constants.SUCCESS
											.equalsIgnoreCase(migrationStatusDetails.get("status").toString())) {
								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) migrationStatusDetails
										.get("runTestEntity");
								List<Map<String, RunTestEntity>> allrunTestEntityMap = (List<Map<String, RunTestEntity>>) migrationStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) migrationStatusDetails
										.get("combinedresult");

								// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
								int count = 0;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityMigration = runTestEntityMap.get(gndb);

									if (runTestEntityMigration != null && workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread.setRunMigTestEntity(runTestEntityMigration);
										workFlowManagementEntityThread.setMigStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunMigTestEntity() != null
												&& workFlowManagementEntityThread.getRunMigTestEntity().getId() > 0) {
											RunTestEntity runTestEntityMigrationResult = workFlowManagementService
													.getRunTestEntity(runTestEntityMigration);

											if (runTestEntityMigrationResult != null && "Completed"
													.equalsIgnoreCase(runTestEntityMigrationResult.getProgressStatus())) {
												if ("Success".equalsIgnoreCase(runTestEntityMigrationResult.getStatus())) {
													workFlowManagementEntityThread.setMigStatus("Completed");
												} else {
													workFlowManagementEntityThread
															.setMigStatus(runTestEntityMigrationResult.getStatus());
													/*if (runTestEntityMigrationResult.getId() > 0) {
														String errorFilePath = workFlowManagementEntityThread
																.getMigErrorFile();
														writeMessageInfo(runTestEntityMigrationResult.getId(),
																errorFilePath);
													}*/
													workFlowManagementEntityThread.setStatus("Completed");
													if (workFlowManagementEntityThread.getRunNEGrowEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getNeGrowStatus())) {
														workFlowManagementEntityThread.setNeGrowStatus("InputsRequired");
													}
													if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
															&& !"Failure".equals(
																	workFlowManagementEntityThread.getPostMigStatus())) {
														workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
													}
													workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													return Constants.FAIL;
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {

								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);

									// display failure logs
									String checkPath = workFlowManagementEntityThread.getMigErrorFile();
									String reason = migrationStatusDetails.get("reason").toString();
									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "",
														"MIGRATION" + "\n" + "--------------------------" + "\n", "", "");
												common.appendMessage(checkPath, "", reason, "", "");
											}
										}
									}
									workFlowManagementEntityThread.setMigStatus("Failure");
									workFlowManagementEntityThread.setStatus("Completed");
									if (workFlowManagementEntityThread.getRunNEGrowEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getNeGrowStatus())) {
										workFlowManagementEntityThread.setNeGrowStatus("InputsRequired");
									}
									if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
											&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
										workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
									}
									ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
									ovScheduledEntity.setMigStatus("Failure");
									ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lmig2 = LocalDateTime.now();
								long dshsec = Duration.between(lmig1, lmig2).getSeconds();
								logger.error("Duration for thread in Migration WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								
								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								return Constants.FAIL;
							}
							
							LocalDateTime lmig2 = LocalDateTime.now();
							long dshsec = Duration.between(lmig1, lmig2).getSeconds();
							logger.error("Duration for thread in Migration WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
									dshsec/3600, (dshsec%3600)/60, dshsec%60));

						}

						JSONObject postMigrationStatusDetails = new JSONObject();
						JSONObject postMigrationRANATPStatusDetails = new JSONObject();

						if (postmigrationInput != null)// && WorkFlowManagementEntity != null)

						{
							LocalDateTime lpost1 = LocalDateTime.now();
							Map inputJsonpostmigGrow = (JSONObject) new JSONParser().parse(
									postmigrationInput.toJSONString((Map) postmigrationInput.get("runTestFormDetails")));
							inputJsonpostmigGrow.put("neDetails", singleEnbList);
							postmigrationInput.put("runTestFormDetails", inputJsonpostmigGrow);
							// if (programName.contains("VZN-5G-MM")) {
							postMigrationStatusDetails = runTestController.runTestWFM(postmigrationInput, programid, "",
									"");
							// }
							// else {
							// postMigrationStatusDetails = runTestController.runTest(postmigrationInputs);
							//
							// }

							if (postMigrationStatusDetails != null && postMigrationStatusDetails.containsKey("status")
									&& Constants.SUCCESS
											.equalsIgnoreCase(postMigrationStatusDetails.get("status").toString())) {
								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationStatusDetails
										.get("runTestEntity");
								List<Map<String, RunTestEntity>> allrunTestEntityMap = (List<Map<String, RunTestEntity>>) postMigrationStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) postMigrationStatusDetails
										.get("combinedresult");

								// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
								int count = 0;
								String filePath = null;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);

									int wfmid = temp.get("0" + gndb);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(gndb);

									// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

									if (runTestEntityPostMigration != null && workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread.setRunPostMigTestEntity(runTestEntityPostMigration);

										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);

										workFlowManagementEntityThread.setPostMigStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunPostMigTestEntity() != null
												&& workFlowManagementEntityThread.getRunPostMigTestEntity().getId() > 0) {
											RunTestEntity runTestEntityPostMigrationResult = workFlowManagementService
													.getRunTestEntity(runTestEntityPostMigration);

											if (runTestEntityPostMigrationResult != null && "Completed".equalsIgnoreCase(
													runTestEntityPostMigrationResult.getProgressStatus())) {
												if ("Success"
														.equalsIgnoreCase(runTestEntityPostMigrationResult.getStatus())) {
													workFlowManagementEntityThread.setPostMigStatus("Completed");
													if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
													{
														if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
														{
															workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
														}
													}
												} else {
													filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
															+ runTestEntityPostMigrationResult.getResultFilePath()
																	.toString()
															+ "/" + runTestEntityPostMigrationResult.getResult().toString();
													File htmlFile = new File(filePath);
													if (!htmlFile.exists() || runTestEntityPostMigrationResult.getResult()
															.toString().isEmpty()) {
														String checkPath = workFlowManagementEntityThread
																.getPostErrorFile();
														String reason = "html result file not generated";
														if (checkPath != null) {
															File f = new File(checkPath);
															if (f.exists()) {
																common.appendMessage(checkPath, "", reason, "", "");
															} else {
																if (createNewFile(checkPath)) {
																	common.appendMessage(checkPath, "",
																			"POSTMIGRATION" + "\n"
																					+ "------------------------------"
																					+ "\n",
																			"", "");
																	common.appendMessage(checkPath, "", reason, "", "");
																}
															}
														}

													}
													workFlowManagementEntityThread
															.setPostMigStatus(runTestEntityPostMigrationResult.getStatus());
													
													if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
													{
														if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
														{
															workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
														}
													}

													workFlowManagementEntityThread.setStatus("Completed");
													workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													return Constants.FAIL;
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {

								for (Map<String, String> enb : singleEnbList) {
									int wfmid = temp.get("0" + enb.get("neId"));
									OvScheduledEntity ovScheduledEntity = null;
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfmid);

									// code for display failure logs
									String checkPath = workFlowManagementEntityThread.getPostErrorFile();
									String reason = postMigrationStatusDetails.get("reason").toString();

									if (checkPath != null) {
										File f = new File(checkPath);
										if (f.exists()) {
											common.appendMessage(checkPath, "", reason, "", "");
										} else {
											if (createNewFile(checkPath)) {
												common.appendMessage(checkPath, "",
														"POSTMIGRATION" + "\n" + "------------------------------" + "\n",
														"", "");
												common.appendMessage(checkPath, "", reason, "", "");
											}
										}
									}
									workFlowManagementEntityThread.setPostMigStatus("Failure");
									if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
									{
										if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
										{
											workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
										}
									}
									workFlowManagementEntityThread.setStatus("Completed");
									ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
									ovScheduledEntity.setPostMigAuditStatus("Failure");
									ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity); 
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
								}
								LocalDateTime lpost2 = LocalDateTime.now();
								long dshsec = Duration.between(lpost1, lpost2).getSeconds();
								logger.error("Duration for thread in PostMigration-Audit WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								
								LocalDateTime lt2 = LocalDateTime.now();
								dshsec = Duration.between(lt1, lt2).getSeconds();
								logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
										dshsec/3600, (dshsec%3600)/60, dshsec%60));
								return Constants.FAIL;
							}
							
							LocalDateTime lpost2 = LocalDateTime.now();
							long dshsec = Duration.between(lpost1, lpost2).getSeconds();
							logger.error("Duration for thread in PostMigration-Audit WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
									dshsec/3600, (dshsec%3600)/60, dshsec%60));
						}
						if (postmigrationInput != null
								&& postmigrationInput.get("useCaseName").toString().contains("Ran_Atp")) {
							LocalDateTime lranatp1 = LocalDateTime.now();
							if (programname.contains("VZN-4G-USM-LIVE")) {
								JSONObject runTestFormDetails = (JSONObject) postmigrationInput.get("runTestFormDetails");
								List<JSONObject> useCase = (List<JSONObject>) runTestFormDetails.get("useCase");

								JSONObject bandNameParams = new JSONObject();
								bandNameParams.put("sessionId", postmigrationInput.get("sessionId").toString());
								bandNameParams.put("serviceToken", postmigrationInput.get("serviceToken").toString());
								bandNameParams.put("programId", postmigrationInput.get("programId").toString());

								bandNameParams.put("ciqName", runTestFormDetails.get("ciqName").toString());
								List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) runTestFormDetails
										.get("neDetails");
								// String enbId = neDetails.get(0).get("neId");
								JSONObject bandNames = new JSONObject();
								// JSONArray bandName = new JSONArray();
								List<String> bandName = new LinkedList<>();
								Map<String, List<String>> bandNameList = new LinkedHashMap<>();
								String[] bandName1 = null;
								// neDetails
								for (Map<String, String> enb : singleEnbList) {
									// Map<String, String> enb = neDetails.get(0);
									String enbId = enb.get("neId").toString();
									bandNameParams.put("eNBId", enbId);
									bandNames = runTestController.getBandName(bandNameParams);
									bandName1 = (String[]) bandNames.get("bandName");
									for (String str : bandName1) {
										bandName.add(str);
									}
									// JSONArray bandName = new JSONArray(bandName1);

									bandNameList.put(enbId, bandName);
								}

								/*
								 * for (Map<String, String> enb : enbList) { // String oldGnodeb =
								 * "0".concat(gnodebIdList.get(0)); Map<String, String> map = new HashMap<>();
								 * String singleGnob = "0" + enb.get("neId"); }
								 */

								// useCase.clear();
								List<Map> useCaseLst = new ArrayList<>();
								List<Map> sriptsDetails = new ArrayList<>();
								useCaseLst = runTestService.getUseCaseList(34, "postmigration", "RANATP");
								Map usecaseScriptDetails = useCaseLst.get(0);
								sriptsDetails = (List<Map>) usecaseScriptDetails.get("scripts");
								String useCaseName = (String) usecaseScriptDetails.get("useCaseName");
								sriptsDetails.get(0).put("useCaseName", useCaseName);
								List<JSONObject> sriptsDetailsList = sriptsDetails.stream()
										.map((map) -> new JSONObject(map)).collect(Collectors.toList());

								JSONObject singleUsecase = useCase.get(0);
								singleUsecase.put("useCaseName", (String) usecaseScriptDetails.get("useCaseName"));
								singleUsecase.put("useCaseId", usecaseScriptDetails.get("useCaseId"));
								singleUsecase.put("ucSleepInterval", usecaseScriptDetails.get("ucSleepInterval"));
								singleUsecase.put("executionSequence", usecaseScriptDetails.get("executionSequence"));
								List<JSONObject> useCases = new LinkedList<>();
								useCases.add(singleUsecase);
								useCase = useCases;

								runTestFormDetails.replace("useCase", useCase);
								runTestFormDetails.put("bandName", bandNameList);
								/*
								 * List<JSONObject> ls = new LinkedList<>(); JSONObject staticScripts = new
								 * JSONObject(); staticScripts.put("useCaseName", "RAN_ATP");
								 * staticScripts.put("scriptName", "ran_atp.pl"); staticScripts.put("scriptId",
								 * 2573); staticScripts.put("scriptExeSequence", 121);
								 * staticScripts.put("scriptSleepInterval", 1000);
								 * staticScripts.put("useGeneratedScript", "NO"); ls.add(staticScripts); scripts
								 * = ls;
								 */
								runTestFormDetails.put("scripts", sriptsDetailsList);
								postmigrationInput.replace("runTestFormDetails", runTestFormDetails);

								postmigrationInput.put("migrationSubType", "RANATP");
								postMigrationRANATPStatusDetails = runTestController.runTestRanAtpWFM(postmigrationInput);

								if (postMigrationRANATPStatusDetails != null
										&& postMigrationRANATPStatusDetails.containsKey("status")
										&& Constants.SUCCESS.equalsIgnoreCase(
												postMigrationRANATPStatusDetails.get("status").toString())) {

									Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationRANATPStatusDetails
											.get("runTestEntity");
									LinkedList<Map<String, RunTestEntity>> allrunTestEntityMap = (LinkedList<Map<String, RunTestEntity>>) postMigrationRANATPStatusDetails
											.get("allrunTestEntity");

									LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) postMigrationRANATPStatusDetails
											.get("combinedresult");

									// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
									int count = 0;
									for (Object key : enbResult.keySet()) {
										String gndb = (String) key;
										Object gndbResult = enbResult.get(gndb);

										//System.out.println("key: " + gndb + " value: " + gndbResult);

										int wfmid = temp.get("0" + gndb);
										workFlowManagementEntityThread = workFlowManagementService
												.getWorkFlowManagementEntity(wfmid);
										runTestEntityMap = allrunTestEntityMap.get(count);
										count++;
										RunTestEntity runTestEntityPostMigrationRANATP = runTestEntityMap.get(gndb);

										// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

										if (runTestEntityPostMigrationRANATP != null
												&& workFlowManagementEntityThread != null) {
											workFlowManagementEntityThread
													.setRunRanAtpTestEntity(runTestEntityPostMigrationRANATP);

											workFlowManagementEntityThread = workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);

											// workFlowManagementEntityThread.setPostMigStatus("InProgress");
											workFlowManagementEntityThread = workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											while (workFlowManagementEntityThread.getRunRanAtpTestEntity() != null
													&& workFlowManagementEntityThread.getRunRanAtpTestEntity()
															.getId() > 0) {
												RunTestEntity runTestEntityPostMigrationRANATPResult = workFlowManagementService
														.getRunTestEntity(runTestEntityPostMigrationRANATP);

												if (runTestEntityPostMigrationRANATPResult != null && "Completed"
														.equalsIgnoreCase(runTestEntityPostMigrationRANATPResult
																.getProgressStatus())) {
													if ("Success".equalsIgnoreCase(
															runTestEntityPostMigrationRANATPResult.getStatus())) {
														/*
														 * workFlowManagementEntityThread.setPostMigStatus("Completed");
														 */

													} else {
														/*
														 * workFlowManagementEntityThread
														 * .setPostMigStatus(runTestEntityPostMigrationRANATPResult.
														 * getStatus()) ;
														 */
													}
													workFlowManagementEntityThread = workFlowManagementService
															.mergeWorkFlowMangement(workFlowManagementEntityThread);
													break;
												}
												Thread.sleep(10000);
											}

										}
									}

								} else {

									for (Map<String, String> enb : singleEnbList) {
										int wfmid = temp.get("0" + enb.get("neId"));
										workFlowManagementEntityThread = workFlowManagementService
												.getWorkFlowManagementEntity(wfmid);

										// code for display failure logs
										String checkPath = workFlowManagementEntityThread.getPostErrorFile();
										String reason = postMigrationRANATPStatusDetails.get("reason").toString();
										if (checkPath != null) {
											File f = new File(checkPath);
											if (f.exists()) {
												common.appendMessage(checkPath, "", reason, "", "");
											} else {
												if (createNewFile(checkPath)) {
													common.appendMessage(checkPath, "", "POSTMIGRATION" + "\n"
															+ "------------------------------" + "\n", "", "");
													common.appendMessage(checkPath, "", reason, "", "");
												}
											}
										}
										/*
										 * workFlowManagementEntityThread.setPostMigStatus("Failure");
										 * workFlowManagementEntityThread.setStatus("Completed");
										 * workFlowManagementEntityThread = workFlowManagementService
										 * .mergeWorkFlowMangement(workFlowManagementEntityThread);
										 */
									}
									// return Constants.FAIL;
									// return Constants.FAIL;
								} // else end

							}
							
							LocalDateTime lranatp2 = LocalDateTime.now();
							long dshsec = Duration.between(lranatp1, lranatp2).getSeconds();
							logger.error("Duration for thread in PostMigration-RanAtp WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
									dshsec/3600, (dshsec%3600)/60, dshsec%60));

						} // checking ranatp usecase presence
							// }

						// }
						workFlowManagementEntityThread.setStatus("Completed");
						workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntityThread);
						
						LocalDateTime lt2 = LocalDateTime.now();
						long dshsec = Duration.between(lt1, lt2).getSeconds();
						logger.error("Duration for thread in WFM - NE : " + enbMap.get("neId") + "(HH:mm:ss) : " + String.format("%d:%02d:%02d", 
								dshsec/3600, (dshsec%3600)/60, dshsec%60));
						
					} catch(Exception e) {
						if(workFlowManagementEntityThread == null) {
							workFlowManagementEntityThread = workFlowManagementService.getWorkFlowManagementEntity(temp.get("0" + enbMap.get("neId")));
						}
						if(workFlowManagementEntityThread != null) {
							workFlowManagementEntityThread.setStatus("Completed");
							workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntityThread);
						}
						logger.error("Exception in WorkFlowManagementServiceImpl continueWFM() : " + ExceptionUtils.getFullStackTrace(e));
					}					
					return Constants.SUCCESS;
				});
			}
			executorservice.shutdown();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return Constants.SUCCESS;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public String continueWFM(Integer wfid, JSONObject negrowJson, JSONObject migrationJson, JSONObject postMigJson,
			List<Map<String, String>> enbList,JSONObject preAuditInputs, JSONObject neStatusInputs) {
		try {
			ExecutorService executorservice = Executors.newFixedThreadPool(1);
			executorservice.submit(() -> {
				Thread.currentThread().setName("WFM_" + wfid);
				WorkFlowManagementEntity workFlowManagementEntityThread = workFlowManagementService
						.getWorkFlowManagementEntity(wfid);
				try {
					if (negrowJson != null && workFlowManagementEntityThread != null) {
						JSONObject neGrowStatusDetails = runTestController.runTest(negrowJson);
						if (neGrowStatusDetails != null && neGrowStatusDetails.containsKey("status")
								&& Constants.SUCCESS.equalsIgnoreCase(neGrowStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) neGrowStatusDetails
									.get("runTestEntity");
							RunTestEntity runTestEntityNeGrow = runTestEntityMap.get(enbList.get(0).get("neId"));
							if (runTestEntityNeGrow != null && workFlowManagementEntityThread != null) {
								workFlowManagementEntityThread.setRunNEGrowEntity(runTestEntityNeGrow);
								workFlowManagementEntityThread.setNeGrowStatus("InProgress");
								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);
								while (workFlowManagementEntityThread.getRunNEGrowEntity() != null
										&& workFlowManagementEntityThread.getRunNEGrowEntity().getId() > 0) {
									RunTestEntity runTestEntityNeGrowResult = workFlowManagementService
											.getRunTestEntity(runTestEntityNeGrow);

									if (runTestEntityNeGrowResult != null && "Completed"
											.equalsIgnoreCase(runTestEntityNeGrowResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntityNeGrowResult.getStatus())) {
											workFlowManagementEntityThread.setNeGrowStatus("Completed");
										} else {
											workFlowManagementEntityThread
													.setNeGrowStatus(runTestEntityNeGrowResult.getStatus());
											workFlowManagementEntityThread.setStatus("Completed");
											if (workFlowManagementEntityThread.getRunNEStatusTestEntity() == null
													&& !"Failure".equals(workFlowManagementEntityThread.getNeUpStatus())) {
												workFlowManagementEntityThread.setNeUpStatus("InputsRequired");
											}
											if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
													&& !"Failure".equals(workFlowManagementEntityThread.getPreAuditStatus())) {
												workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
											}
											if (workFlowManagementEntityThread.getRunMigTestEntity() == null
													&& !"Failure".equals(workFlowManagementEntityThread.getMigStatus())) {
												workFlowManagementEntityThread.setMigStatus("InputsRequired");
											}
											if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
													&& !"Failure"
															.equals(workFlowManagementEntityThread.getPostMigStatus())) {
												workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
											}
											workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											return Constants.FAIL;
										}
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {
							workFlowManagementEntityThread.setNeGrowStatus("Failure");
							workFlowManagementEntityThread.setStatus("Completed");
							if (workFlowManagementEntityThread.getRunNEStatusTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getNeUpStatus())) {
								workFlowManagementEntityThread.setNeUpStatus("InputsRequired");
							}
							if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getPreAuditStatus())) {
								workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
							}
							if (workFlowManagementEntityThread.getRunMigTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getMigStatus())) {
								workFlowManagementEntityThread.setMigStatus("InputsRequired");
							}
							if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
								workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
							}
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);
							// code for display failure
							String checkPath = workFlowManagementEntityThread.getNegrowErrorFile();
							String reason = neGrowStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"NEGROW" + "\n" + "--------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}
					if (neStatusInputs != null && workFlowManagementEntityThread != null) {
						JSONObject neStatusDetails = runTestController.runTest(neStatusInputs);

						if (neStatusDetails != null && neStatusDetails.containsKey("status")
								&& Constants.SUCCESS.equalsIgnoreCase(neStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) neStatusDetails
									.get("runTestEntity");

							RunTestEntity runTestEntityneStatus = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());

							if (runTestEntityneStatus != null && workFlowManagementEntityThread != null) {
								// workFlowManagementEntityThread.setRunPostMigTestEntity(runTestEntitypreAudit);
								workFlowManagementEntityThread.setRunNEStatusTestEntity(runTestEntityneStatus);

								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);

								workFlowManagementEntityThread.setNeUpStatus("InProgress");
								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);
								while (workFlowManagementEntityThread.getRunNEStatusTestEntity() != null
										&& workFlowManagementEntityThread.getRunNEStatusTestEntity().getId() > 0) {
									RunTestEntity runTestEntityNeStatusResult = workFlowManagementService
											.getRunTestEntity(runTestEntityneStatus);

									if (runTestEntityNeStatusResult != null && "Completed"
											.equalsIgnoreCase(runTestEntityNeStatusResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntityNeStatusResult.getStatus())) {
											workFlowManagementEntityThread.setNeUpStatus("Completed");
											
										} else {
											

											workFlowManagementEntityThread
													.setNeUpStatus(runTestEntityNeStatusResult.getStatus());
											if ("Failure".equalsIgnoreCase(
													workFlowManagementEntityThread.getNeUpStatus())
													|| "Completed".equalsIgnoreCase(
															workFlowManagementEntityThread.getNeUpStatus())
													|| "Success".equalsIgnoreCase(
															workFlowManagementEntityThread.getNeUpStatus())) {
												if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
														&& !"Failure".equals(
																workFlowManagementEntityThread.getPreAuditStatus())) {
													workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
												}
												if (workFlowManagementEntityThread.getRunMigTestEntity() == null
														&& !"Failure".equals(
																workFlowManagementEntityThread.getMigStatus())) {
													workFlowManagementEntityThread.setMigStatus("InputsRequired");
												}
												if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
														&& !"Failure".equals(
																workFlowManagementEntityThread.getPostMigStatus())) {
													workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
												}
											}
											workFlowManagementEntityThread.setStatus("Completed");
											workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											return Constants.FAIL;
										}
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {
							workFlowManagementEntityThread.setNeUpStatus("Failure");
							if ("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getNeUpStatus())
									|| "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getNeUpStatus())
									|| "Success".equalsIgnoreCase(workFlowManagementEntityThread.getNeUpStatus())) {
								if (workFlowManagementEntityThread.getRunPreAuditTestEntity() == null
										&& !"Failure".equals(
												workFlowManagementEntityThread.getPreAuditStatus())) {
									workFlowManagementEntityThread.setPreAuditStatus("InputsRequired");
								}
								if (workFlowManagementEntityThread.getRunMigTestEntity() == null
										&& !"Failure".equals(
												workFlowManagementEntityThread.getMigStatus())) {
									workFlowManagementEntityThread.setMigStatus("InputsRequired");
								}
								if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
										&& !"Failure".equals(
												workFlowManagementEntityThread.getPostMigStatus())) {
									workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
								}
							}
							workFlowManagementEntityThread.setStatus("Completed");
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);
							// code for display failure logs
							String checkPath = workFlowManagementEntityThread.getNEStatusErrorFile();
							String reason = neStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"NESTATUS" + "\n" + "------------------------------" + "\n", "",
												"");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}
					if (preAuditInputs != null && workFlowManagementEntityThread != null) {
						JSONObject preAuditStatusDetails = runTestController.runTest(preAuditInputs);

						if (preAuditStatusDetails != null && preAuditStatusDetails.containsKey("status")
								&& Constants.SUCCESS.equalsIgnoreCase(preAuditStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) preAuditStatusDetails
									.get("runTestEntity");

							RunTestEntity runTestEntitypreAudit = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());

							if (runTestEntitypreAudit != null && workFlowManagementEntityThread != null) {
								// workFlowManagementEntityThread.setRunPostMigTestEntity(runTestEntitypreAudit);
								workFlowManagementEntityThread.setRunPreAuditTestEntity(runTestEntitypreAudit);

								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);

								workFlowManagementEntityThread.setPreAuditStatus("InProgress");
								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);
								while (workFlowManagementEntityThread.getRunPreAuditTestEntity() != null
										&& workFlowManagementEntityThread.getRunPreAuditTestEntity().getId() > 0) {
									RunTestEntity runTestEntitypreAuditResult = workFlowManagementService
											.getRunTestEntity(runTestEntitypreAudit);

									if (runTestEntitypreAuditResult != null && "Completed"
											.equalsIgnoreCase(runTestEntitypreAuditResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntitypreAuditResult.getStatus())) {
											workFlowManagementEntityThread.setPreAuditStatus("Completed");
											
										} else {
											String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
													+ runTestEntitypreAuditResult.getResultFilePath().toString() + "/"
													+ runTestEntitypreAuditResult.getResult().toString();
											File htmlFile = new File(filePath);
											if (!htmlFile.exists()
													|| runTestEntitypreAuditResult.getResult().toString().isEmpty()) {
												String checkPath = workFlowManagementEntityThread
														.getPREAUDITErrorFile();
												String reason = "html result file not generated";
												if (checkPath != null) {
													File f = new File(checkPath);
													if (f.exists()) {
														common.appendMessage(checkPath, "", reason, "", "");
													} else {
														if (createNewFile(checkPath)) {
															common.appendMessage(
																	checkPath, "", "PREAUDIT" + "\n"
																			+ "------------------------------" + "\n",
																	"", "");
															common.appendMessage(checkPath, "", reason, "", "");
														}
													}
												}

											}

											workFlowManagementEntityThread
													.setPreAuditStatus(runTestEntitypreAuditResult.getStatus());
											if ("Failure".equalsIgnoreCase(
													workFlowManagementEntityThread.getPreAuditStatus())
													|| "Completed".equalsIgnoreCase(
															workFlowManagementEntityThread.getPreAuditStatus())
													|| "Success".equalsIgnoreCase(
															workFlowManagementEntityThread.getPreAuditStatus())) {
												if (workFlowManagementEntityThread.getRunMigTestEntity() == null
														&& !"Failure".equals(workFlowManagementEntityThread.getMigStatus())) {
													workFlowManagementEntityThread.setMigStatus("InputsRequired");
												}
												if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
														&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
													workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
												}
											}
											workFlowManagementEntityThread.setStatus("Completed");
											workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											return Constants.FAIL;
										}
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {
							workFlowManagementEntityThread.setPreAuditStatus("Failure");
							if ("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPreAuditStatus())
									|| "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPreAuditStatus())
									|| "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPreAuditStatus())) {
								if (workFlowManagementEntityThread.getRunMigTestEntity() == null
										&& !"Failure".equals(workFlowManagementEntityThread.getMigStatus())) {
									workFlowManagementEntityThread.setMigStatus("InputsRequired");
								}
								if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
										&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
									workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
								}
							}
							workFlowManagementEntityThread.setStatus("Completed");
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);
							// code for display failure logs
							String checkPath = workFlowManagementEntityThread.getPREAUDITErrorFile();
							String reason = preAuditStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"PREAUDIT" + "\n" + "------------------------------" + "\n", "",
												"");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}
					if (migrationJson != null && workFlowManagementEntityThread != null) {
						Map inputJsonMigration = (JSONObject) new JSONParser()
								.parse(migrationJson.toJSONString((Map) migrationJson.get("runTestFormDetails")));
						List<Map> scriptSeqDetails = (List<Map>) inputJsonMigration.get("scripts");
						if (scriptSeqDetails.isEmpty()) {
							workFlowManagementEntityThread.setMigStatus("Failure");
							workFlowManagementEntityThread.setStatus("Completed");
							if (workFlowManagementEntityThread.getRunNEGrowEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getNeGrowStatus())) {
								workFlowManagementEntityThread.setNeGrowStatus("InputsRequired");
							}
							if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
								workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
							}
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);
							String checkPath = workFlowManagementEntityThread.getMigErrorFile();
							String reason = "No Scripts found to run";
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"MIGRATION" + "\n" + "--------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
						JSONObject migrationStatusDetails = runTestController.runTest(migrationJson);
						if (migrationStatusDetails != null && migrationStatusDetails.containsKey("status")
								&& Constants.SUCCESS.equalsIgnoreCase(migrationStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) migrationStatusDetails
									.get("runTestEntity");
							RunTestEntity runTestEntityMigration = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());
							if (runTestEntityMigration != null && workFlowManagementEntityThread != null) {
								if (runTestEntityMigration != null && workFlowManagementEntityThread != null) {
									workFlowManagementEntityThread.setRunMigTestEntity(runTestEntityMigration);
									workFlowManagementEntityThread.setMigStatus("InProgress");
									workFlowManagementEntityThread = workFlowManagementService
											.mergeWorkFlowMangement(workFlowManagementEntityThread);
									while (workFlowManagementEntityThread.getRunMigTestEntity() != null
											&& workFlowManagementEntityThread.getRunMigTestEntity().getId() > 0) {
										RunTestEntity runTestEntityMigrationResult = workFlowManagementService
												.getRunTestEntity(runTestEntityMigration);

										// if (runTestEntityMigrationResult != null && ("Success"
										// .equalsIgnoreCase(runTestEntityMigrationResult.getStatus())
										// || "Failure".equalsIgnoreCase(runTestEntityMigrationResult.getStatus()))) {
										if (runTestEntityMigrationResult != null && "Completed"
												.equalsIgnoreCase(runTestEntityMigrationResult.getProgressStatus())) {
											if ("Success".equalsIgnoreCase(runTestEntityMigrationResult.getStatus())) {
												workFlowManagementEntityThread.setMigStatus("Completed");
											} else {
												workFlowManagementEntityThread
														.setMigStatus(runTestEntityMigrationResult.getStatus());
												/*if (runTestEntityMigrationResult.getId() > 0) {
													String errorFilePath = workFlowManagementEntityThread.getMigErrorFile();
													writeMessageInfo(runTestEntityMigrationResult.getId(), errorFilePath);
												}*/
												workFlowManagementEntityThread.setStatus("Completed");
												if (workFlowManagementEntityThread.getRunNEGrowEntity() == null
														&& !"Failure"
																.equals(workFlowManagementEntityThread.getNeGrowStatus())) {
													workFlowManagementEntityThread.setNeGrowStatus("InputsRequired");
												}
												if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
														&& !"Failure".equals(
																workFlowManagementEntityThread.getPostMigStatus())) {
													workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
												}
												workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												return Constants.FAIL;
											}
											workFlowManagementEntityThread = workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											break;
										}
										Thread.sleep(10000);
									}
								}
							}
						} else {
							workFlowManagementEntityThread.setMigStatus("Failure");
							workFlowManagementEntityThread.setStatus("Completed");
							if (workFlowManagementEntityThread.getRunNEGrowEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getNeGrowStatus())) {
								workFlowManagementEntityThread.setNeGrowStatus("InputsRequired");
							}
							if (workFlowManagementEntityThread.getRunPostMigTestEntity() == null
									&& !"Failure".equals(workFlowManagementEntityThread.getPostMigStatus())) {
								workFlowManagementEntityThread.setPostMigStatus("InputsRequired");
							}
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);

							// display failure logs
							String checkPath = workFlowManagementEntityThread.getMigErrorFile();
							String reason = migrationStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"MIGRATION" + "\n" + "--------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}

					if (postMigJson != null && workFlowManagementEntityThread != null) {
						JSONObject postMigrationStatusDetails = runTestController.runTest(postMigJson);

						if (postMigrationStatusDetails != null && postMigrationStatusDetails.containsKey("status")
								&& Constants.SUCCESS
										.equalsIgnoreCase(postMigrationStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationStatusDetails
									.get("runTestEntity");

							RunTestEntity runTestEntityPostMigration = runTestEntityMap
									.get(enbList.get(0).get("neId").toString());

							if (runTestEntityPostMigration != null && workFlowManagementEntityThread != null) {
								workFlowManagementEntityThread.setRunPostMigTestEntity(runTestEntityPostMigration);

								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);

								workFlowManagementEntityThread.setPostMigStatus("InProgress");
								workFlowManagementEntityThread = workFlowManagementService
										.mergeWorkFlowMangement(workFlowManagementEntityThread);
								while (workFlowManagementEntityThread.getRunPostMigTestEntity() != null
										&& workFlowManagementEntityThread.getRunPostMigTestEntity().getId() > 0) {
									RunTestEntity runTestEntityPostMigrationResult = workFlowManagementService
											.getRunTestEntity(runTestEntityPostMigration);

									if (runTestEntityPostMigrationResult != null && "Completed"
											.equalsIgnoreCase(runTestEntityPostMigrationResult.getProgressStatus())) {
										if ("Success".equalsIgnoreCase(runTestEntityPostMigrationResult.getStatus())) {
											workFlowManagementEntityThread.setPostMigStatus("Completed");
											if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
											{
												if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
												{
													workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
												}
											}
										} else {
											String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
													+ runTestEntityPostMigrationResult.getResultFilePath().toString() + "/"
													+ runTestEntityPostMigrationResult.getResult().toString();
											File htmlFile = new File(filePath);
											if (!htmlFile.exists()
													|| runTestEntityPostMigrationResult.getResult().toString().isEmpty()) {
												String checkPath = workFlowManagementEntityThread.getPostErrorFile();
												String reason = "html result file not generated";
												if (checkPath != null) {
													File f = new File(checkPath);
													if (f.exists()) {
														common.appendMessage(checkPath, "", reason, "", "");
													} else {
														if (createNewFile(checkPath)) {
															common.appendMessage(checkPath, "",
																	"POSTMIGRATION" + "\n"
																			+ "------------------------------" + "\n",
																	"", "");
															common.appendMessage(checkPath, "", reason, "", "");
														}
													}
												}

											}

											workFlowManagementEntityThread
													.setPostMigStatus(runTestEntityPostMigrationResult.getStatus());
											if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
											{
												if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
												{
													workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
												}
											}
											workFlowManagementEntityThread.setStatus("Completed");
											workFlowManagementService
													.mergeWorkFlowMangement(workFlowManagementEntityThread);
											return Constants.FAIL;
										}
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										break;
									}
									Thread.sleep(10000);
								}
							}
						} else {
							workFlowManagementEntityThread.setPostMigStatus("Failure");
							if("Failure".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntityThread.getPostMigStatus()) )
							{
								if("NotExecuted".equalsIgnoreCase(workFlowManagementEntityThread.getSiteReportStatus()))
								{
									workFlowManagementEntityThread.setSiteReportStatus("InputsRequired");
								}
							}
							workFlowManagementEntityThread.setStatus("Completed");
							workFlowManagementEntityThread = workFlowManagementService
									.mergeWorkFlowMangement(workFlowManagementEntityThread);
							// code for display failure logs
							String checkPath = workFlowManagementEntityThread.getPostErrorFile();
							String reason = postMigrationStatusDetails.get("reason").toString();
							if (checkPath != null) {
								File f = new File(checkPath);
								if (f.exists()) {
									common.appendMessage(checkPath, "", reason, "", "");
								} else {
									if (createNewFile(checkPath)) {
										common.appendMessage(checkPath, "",
												"POSTMIGRATION" + "\n" + "------------------------------" + "\n", "", "");
										common.appendMessage(checkPath, "", reason, "", "");
									}
								}
							}
							return Constants.FAIL;
						}
					}

					if (postMigJson != null && postMigJson.containsKey("useCaseName")
							&& postMigJson.get("useCaseName").toString().contains("Ran_Atp")) {
						String programname = postMigJson.get("programName").toString();

						if (programname.contains("VZN-4G-USM-LIVE") && postMigJson != null) {
							JSONObject runTestFormDetails = (JSONObject) postMigJson.get("runTestFormDetails");
							List<JSONObject> useCase = (List<JSONObject>) runTestFormDetails.get("useCase");

							JSONObject bandNameParams = new JSONObject();
							bandNameParams.put("sessionId", postMigJson.get("sessionId").toString());
							bandNameParams.put("serviceToken", postMigJson.get("serviceToken").toString());
							bandNameParams.put("programId", postMigJson.get("programId").toString());

							bandNameParams.put("ciqName", runTestFormDetails.get("ciqName").toString());
							List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) runTestFormDetails
									.get("neDetails");
							// String enbId = neDetails.get(0).get("neId");
							JSONObject bandNames = new JSONObject();
							// JSONArray bandName = new JSONArray();
							List<String> bandName = new LinkedList<>();
							Map<String, List<String>> bandNameList = new LinkedHashMap<>();
							String[] bandName1 = null;
							// neDetails
							for (Map<String, String> enb : enbList) {
								// Map<String, String> enb = neDetails.get(0);
								String enbId = enb.get("neId").toString();
								bandNameParams.put("eNBId", enbId);
								bandNames = runTestController.getBandName(bandNameParams);
								bandName1 = (String[]) bandNames.get("bandName");
								for (String str : bandName1) {
									bandName.add(str);
								}
								// JSONArray bandName = new JSONArray(bandName1);

								bandNameList.put(enbId, bandName);
							}

							/*
							 * for (Map<String, String> enb : enbList) { // String oldGnodeb =
							 * "0".concat(gnodebIdList.get(0)); Map<String, String> map = new HashMap<>();
							 * String singleGnob = "0" + enb.get("neId"); }
							 */

							// useCase.clear();
							List<Map> useCaseLst = new ArrayList<>();
							List<Map> sriptsDetails = new ArrayList<>();
							useCaseLst = runTestService.getUseCaseList(34, "postmigration", "RANATP");
							Map usecaseScriptDetails = useCaseLst.get(0);
							sriptsDetails = (List<Map>) usecaseScriptDetails.get("scripts");
							String useCaseName = (String) usecaseScriptDetails.get("useCaseName");
							sriptsDetails.get(0).put("useCaseName", useCaseName);
							List<JSONObject> sriptsDetailsList = sriptsDetails.stream().map((map) -> new JSONObject(map))
									.collect(Collectors.toList());

							JSONObject singleUsecase = useCase.get(0);
							singleUsecase.put("useCaseName", (String) usecaseScriptDetails.get("useCaseName"));
							singleUsecase.put("useCaseId", usecaseScriptDetails.get("useCaseId"));
							singleUsecase.put("ucSleepInterval", usecaseScriptDetails.get("ucSleepInterval"));
							singleUsecase.put("executionSequence", usecaseScriptDetails.get("executionSequence"));
							List<JSONObject> useCases = new LinkedList<>();
							useCases.add(singleUsecase);
							useCase = useCases;

							runTestFormDetails.replace("useCase", useCase);
							runTestFormDetails.put("bandName", bandNameList);
							/*
							 * List<JSONObject> ls = new LinkedList<>(); JSONObject staticScripts = new
							 * JSONObject(); staticScripts.put("useCaseName", "RAN_ATP");
							 * staticScripts.put("scriptName", "ran_atp.pl"); staticScripts.put("scriptId",
							 * 2573); staticScripts.put("scriptExeSequence", 121);
							 * staticScripts.put("scriptSleepInterval", 1000);
							 * staticScripts.put("useGeneratedScript", "NO"); ls.add(staticScripts); scripts
							 * = ls;
							 */
							runTestFormDetails.put("scripts", sriptsDetailsList);
							postMigJson.replace("runTestFormDetails", runTestFormDetails);

							postMigJson.put("migrationSubType", "RANATP");
							JSONObject postMigrationRANATPStatusDetails = runTestController.runTestRanAtpWFM(postMigJson);

							if (postMigrationRANATPStatusDetails != null
									&& postMigrationRANATPStatusDetails.containsKey("status") && Constants.SUCCESS
											.equalsIgnoreCase(postMigrationRANATPStatusDetails.get("status").toString())) {

								Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationRANATPStatusDetails
										.get("runTestEntity");
								LinkedList<Map<String, RunTestEntity>> allrunTestEntityMap = (LinkedList<Map<String, RunTestEntity>>) postMigrationRANATPStatusDetails
										.get("allrunTestEntity");

								LinkedHashMap<String, String> enbResult = (LinkedHashMap<String, String>) postMigrationRANATPStatusDetails
										.get("combinedresult");

								// RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);
								int count = 0;
								for (Object key : enbResult.keySet()) {
									String gndb = (String) key;
									Object gndbResult = enbResult.get(gndb);

									//System.out.println("key: " + gndb + " value: " + gndbResult);
									workFlowManagementEntityThread = workFlowManagementService
											.getWorkFlowManagementEntity(wfid);
									runTestEntityMap = allrunTestEntityMap.get(count);
									count++;
									RunTestEntity runTestEntityPostMigrationRANATP = runTestEntityMap.get(gndb);

									// RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

									if (runTestEntityPostMigrationRANATP != null
											&& workFlowManagementEntityThread != null) {
										workFlowManagementEntityThread
												.setRunRanAtpTestEntity(runTestEntityPostMigrationRANATP);

										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);

										// workFlowManagementEntityThread.setPostMigStatus("InProgress");
										workFlowManagementEntityThread = workFlowManagementService
												.mergeWorkFlowMangement(workFlowManagementEntityThread);
										while (workFlowManagementEntityThread.getRunRanAtpTestEntity() != null
												&& workFlowManagementEntityThread.getRunRanAtpTestEntity().getId() > 0) {
											RunTestEntity runTestEntityPostMigrationRANATPResult = workFlowManagementService
													.getRunTestEntity(runTestEntityPostMigrationRANATP);

											if (runTestEntityPostMigrationRANATPResult != null
													&& "Completed".equalsIgnoreCase(
															runTestEntityPostMigrationRANATPResult.getProgressStatus())) {
												if ("Success".equalsIgnoreCase(
														runTestEntityPostMigrationRANATPResult.getStatus())) {
													/*
													 * workFlowManagementEntityThread.setPostMigStatus("Completed");
													 */

												} else {
													/*
													 * workFlowManagementEntityThread
													 * .setPostMigStatus(runTestEntityPostMigrationRANATPResult.
													 * getStatus()) ;
													 */
												}
												workFlowManagementEntityThread = workFlowManagementService
														.mergeWorkFlowMangement(workFlowManagementEntityThread);
												break;
											}
											Thread.sleep(10000);
										}

									}
								}

							} else {
								// return Constants.FAIL;
							}

						}

					}
					workFlowManagementEntityThread.setStatus("Completed");
					workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntityThread);
				} catch(Exception e) {
					if(workFlowManagementEntityThread == null) {
						workFlowManagementEntityThread = workFlowManagementService.getWorkFlowManagementEntity(wfid);
					}
					if(workFlowManagementEntityThread != null) {
						workFlowManagementEntityThread.setStatus("Completed");
						workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntityThread);
					}
					logger.error("Exception in WorkFlowManagementServiceImpl continueWFM() : " + ExceptionUtils.getFullStackTrace(e));
				}
				return Constants.SUCCESS;
			});
			executorservice.shutdown();
		} catch (Exception e) {
			System.out.println(e);
			return Constants.FAIL;
		}

		return Constants.SUCCESS;
	}

	@Override
	public LinkedHashMap<String, String> getSkipandContinueId(int runTestId, String runType) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> mapDetails = new LinkedHashMap<String, String>();
		try {
			RunTestEntity runTestEntity = runTestService.getRunTestEntity(runTestId);

			if (runTestEntity != null && !ObjectUtils.isEmpty(runTestEntity.getRunTestResultEntity())) {
				StringBuilder skipIdBuilder = new StringBuilder();
				String reRunScriptID = null;
				Set<RunTestResultEntity> resultInfo = runTestEntity.getRunTestResultEntity();
				LinkedHashSet<RunTestResultEntity> sortedEntity = resultInfo.stream()
						.sorted(Comparator.comparingInt(RunTestResultEntity::getId))
						.collect(Collectors.toCollection(LinkedHashSet::new));

				if (!ObjectUtils.isEmpty(sortedEntity)) {

					int i = 0;
					for (RunTestResultEntity objRunTestResultEntity : sortedEntity) {
						i++;
						if (!ObjectUtils.isEmpty(objRunTestResultEntity.getUseCaseBuilderEntity())
								&& !ObjectUtils.isEmpty(objRunTestResultEntity.getUploadFileEntity())) {
							String useCaseId = String.valueOf(objRunTestResultEntity.getUseCaseBuilderEntity().getId());
							String scriptId = String.valueOf(objRunTestResultEntity.getUploadFileEntity().getId());
							String useCaseScriptId = useCaseId + "_" + scriptId;
							if (sortedEntity.size() == i && "RERUNSCRIPT".equalsIgnoreCase(runType)) {
								reRunScriptID = useCaseScriptId;
								break;
							} else {
								skipIdBuilder.append(useCaseScriptId);
								skipIdBuilder.append(",");

							}
						}
					}
					if (skipIdBuilder.toString().endsWith(",")) {
						skipIdBuilder.setLength(skipIdBuilder.length() - 1);
					}

				}

				mapDetails.put("skipScriptIds", skipIdBuilder.toString());
				mapDetails.put("reRunScriptID", reRunScriptID);
			}
		} catch (Exception e) {
			mapDetails.put("skipScriptIds", null);
			mapDetails.put("reRunScriptID", null);
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return mapDetails;
	}

	@Override
	public void getreRunMigStatus(WorkFlowManagementEntity workFlowManagementEntity) {

		try {

			ExecutorService executorservice = Executors.newFixedThreadPool(1);

			executorservice.submit(() -> {

				List<Integer> runtestIdList = new ArrayList<>();

				if (workFlowManagementEntity.getRunMigTestEntity() != null
						&& workFlowManagementEntity.getRunMigTestEntity().getId() > 0) {
					runtestIdList.add(workFlowManagementEntity.getRunMigTestEntity().getId());
				}

				/*
				 * if (workFlowManagementEntity.getRunPostMigTestEntity() != null &&
				 * workFlowManagementEntity.getRunPostMigTestEntity().getId() > 0) {
				 * runtestIdList.add(workFlowManagementEntity.getRunPostMigTestEntity().getId())
				 * ; }
				 */

				while (!ObjectUtils.isEmpty(runtestIdList)) {

					List<RunTestEntity> runTestEntityPostMigrationResult = workFlowManagementRepository
							.getRunTestListWfm(runtestIdList);

					AtomicBoolean statusOfRuntest = new AtomicBoolean();
					List<Integer> testCompletedIdList = new ArrayList<>();
					if (!ObjectUtils.isEmpty(runTestEntityPostMigrationResult)) {
						for (RunTestEntity objRunTestEntity : runTestEntityPostMigrationResult) {
							if (!"Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())) {
								statusOfRuntest.getAndSet(true);

							}
							if ("Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())
									&& !testCompletedIdList.contains(objRunTestEntity.getId())) {
								if ("precheck".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setMigStatus("Completed");
									} else {
										workFlowManagementEntity.setMigStatus(objRunTestEntity.getStatus());										
									}
									workFlowManagementEntity.setStatus("Completed");
								} else if ("Audit".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setPostMigStatus("Completed");
										if("Failure".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) )
										{
											if("NotExecuted".equalsIgnoreCase(workFlowManagementEntity.getSiteReportStatus()))
											{
												workFlowManagementEntity.setSiteReportStatus("InputsRequired");
											}
										}
									} else {
										workFlowManagementEntity.setPostMigStatus(objRunTestEntity.getStatus());
										if("Failure".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) )
										{
											if("NotExecuted".equalsIgnoreCase(workFlowManagementEntity.getSiteReportStatus()))
											{
												workFlowManagementEntity.setSiteReportStatus("InputsRequired");
											}
										}
									}
									workFlowManagementEntity.setStatus("Completed");
								} else if ("NEGrow".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setNeGrowStatus("Completed");
									} else {
										workFlowManagementEntity.setNeGrowStatus(objRunTestEntity.getStatus());										
									}
									workFlowManagementEntity.setStatus("Completed");
								}
								workFlowManagementRepository.createWorkFlowMangement(workFlowManagementEntity);

								testCompletedIdList.add(objRunTestEntity.getId());

							}
						}
					}

					if (!statusOfRuntest.get()) {
						break;
					}

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						ExceptionUtils.getFullStackTrace(e);
					}
				}

			});
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	@Override
	public void getreRunNEStatus(WorkFlowManagementEntity workFlowManagementEntity) {

		try {

			ExecutorService executorservice = Executors.newFixedThreadPool(1);

			executorservice.submit(() -> {

				List<Integer> runtestIdList = new ArrayList<>();

				/*if (workFlowManagementEntity.getRunMigTestEntity() != null
						&& workFlowManagementEntity.getRunMigTestEntity().getId() > 0) {
					runtestIdList.add(workFlowManagementEntity.getRunMigTestEntity().getId());
				}*/
				if (workFlowManagementEntity.getRunNEGrowEntity() != null
						&& workFlowManagementEntity.getRunNEGrowEntity().getId() > 0) {
					runtestIdList.add(workFlowManagementEntity.getRunNEGrowEntity().getId());
				}

				/*
				 * if (workFlowManagementEntity.getRunPostMigTestEntity() != null &&
				 * workFlowManagementEntity.getRunPostMigTestEntity().getId() > 0) {
				 * runtestIdList.add(workFlowManagementEntity.getRunPostMigTestEntity().getId())
				 * ; }
				 */

				while (!ObjectUtils.isEmpty(runtestIdList)) {

					List<RunTestEntity> runTestEntityPostMigrationResult = workFlowManagementRepository
							.getRunTestListWfm(runtestIdList);

					AtomicBoolean statusOfRuntest = new AtomicBoolean();
					List<Integer> testCompletedIdList = new ArrayList<>();
					if (!ObjectUtils.isEmpty(runTestEntityPostMigrationResult)) {
						for (RunTestEntity objRunTestEntity : runTestEntityPostMigrationResult) {
							if (!"Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())) {
								statusOfRuntest.getAndSet(true);

							}
							if ("Completed".equalsIgnoreCase(objRunTestEntity.getProgressStatus())
									&& !testCompletedIdList.contains(objRunTestEntity.getId())) {
								if ("precheck".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setMigStatus("Completed");
									} else {
										workFlowManagementEntity.setMigStatus(objRunTestEntity.getStatus());										
									}
									workFlowManagementEntity.setStatus("Completed");
								} else if ("Audit".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setPostMigStatus("Completed");
										if("Failure".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) )
										{
											if("NotExecuted".equalsIgnoreCase(workFlowManagementEntity.getSiteReportStatus()))
											{
												workFlowManagementEntity.setSiteReportStatus("InputsRequired");
											}
										}
									} else {
										workFlowManagementEntity.setPostMigStatus(objRunTestEntity.getStatus());
										if("Failure".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Completed".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) || "Success".equalsIgnoreCase(workFlowManagementEntity.getPostMigStatus()) )
										{
											if("NotExecuted".equalsIgnoreCase(workFlowManagementEntity.getSiteReportStatus()))
											{
												workFlowManagementEntity.setSiteReportStatus("InputsRequired");
											}
										}
									}
									workFlowManagementEntity.setStatus("Completed");
								} else if ("NEGrow".equalsIgnoreCase(objRunTestEntity.getMigrationSubType())) {
									if ("Success".equalsIgnoreCase(objRunTestEntity.getStatus())) {
										workFlowManagementEntity.setNeGrowStatus("Completed");
									} else {
										workFlowManagementEntity.setNeGrowStatus(objRunTestEntity.getStatus());										
									}
									workFlowManagementEntity.setStatus("Completed");
								}
								workFlowManagementRepository.createWorkFlowMangement(workFlowManagementEntity);

								testCompletedIdList.add(objRunTestEntity.getId());

							}
						}
					}

					if (!statusOfRuntest.get()) {
						break;
					}

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						ExceptionUtils.getFullStackTrace(e);
					}
				}

			});
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	@Override
	public String getRunnTestWfmEnbProgressStatus(JSONObject runTestParams) {

		try {
			String programId = runTestParams.get("programId").toString();
			// Map run = (Map) runTestParams.get("runTestFormDetails");
			List<Map> neList = (List<Map>) runTestParams.get("neDetails");

			for (Map neid : neList) {

				String neName = "";
				Object object = neid.get("neName");
				System.out.println("NeName-object**********************:" + object);
				if (null != object && org.apache.commons.lang.StringUtils.isNotEmpty(neid.get("neName").toString())) {
					neName = neid.get("neName").toString();
				}
				Object neId = neid.get("neId");
				System.out.println("NeName**********************:" + neName);
				boolean inProgressStatus = workFlowManagementRepository.getWfmEnbStatus(Integer.valueOf(programId),
						neName, neId);
				System.out.println(" inProgressStatus NeName**********************:" + inProgressStatus);
				if (inProgressStatus) {
					return GlobalInitializerListener.faultCodeMap.get(FaultCodes.CANNOT_INITIATE_EXECUTION);
				}
			}
		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getRuntestEnbProgressStatus() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return Constants.SUCCESS;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeMessageInfo(Integer runtestId, String checkPath) {
		try {
			List<RunTestResultEntity> runTestResultEntityList = runTestService.getRunTestResult(runtestId);
			List<Map> result = runTestService.getMessageInfo(runTestResultEntityList);
			if (checkPath != null) {
				File f = new File(checkPath);
				if (f.exists()) {
					FileUtil.deleteFileOrFolder(checkPath);
				}
				if (createNewFile(checkPath)) {
					common.appendMessage(checkPath, "", "MIGRATION" + "\n" + "--------------------------" + "\n", "",
							"");
					int count = 1;
					for (Map scriptinfo : result) {
						common.appendMessage(checkPath, "",
								count + ". " + scriptinfo.get("scriptName") + "\n" + "--------------------------" + "\n"
										+ scriptinfo.get("scriptOutput") + "\n" + "--------------------------" + "\n"
										+ "Info: " + scriptinfo.get("saneIssue").toString().toUpperCase() + "\n",
								"", "");
						count++;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementServiceImpl writeMessageInfo() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getInProgressWorkFlowManagementDetails(WorkFlowManagementModel runTestModel,	Integer programId, List<String> userNameList) {
		List<Map<String, String>> result = new ArrayList<>();
		List<WorkFlowManagementEntity> runTestDetails = null;
		try {
			Map<String, Object> resultMap = workFlowManagementRepository.getInProgressWorkFlowManagementDetails(runTestModel,programId,userNameList);
			if (resultMap != null && resultMap.get("list") != null) {
				runTestDetails = (List<WorkFlowManagementEntity>) resultMap.get("list");
				if (runTestDetails != null && !runTestDetails.isEmpty()) {
					for (WorkFlowManagementEntity runTestEntity : runTestDetails) {
						Map<String, String> mapDetails =  new HashMap<>();
						mapDetails.put("neId", runTestEntity.getEnbId());
						mapDetails.put("neName", runTestEntity.getNeName());
						mapDetails.put("wfmId", runTestEntity.getId().toString());
						mapDetails.put("timeStamp", runTestEntity.getCreationDate().toString());
						mapDetails.put("userName", runTestEntity.getUserName());
						result.add(mapDetails);
					}
				}
			}
		} catch (Exception e){
			logger.error("Exception in getInProgressWorkFlowManagementDetails() : " + ExceptionUtils.getFullStackTrace(e)); 
		}
		return result;
	}
	
	@Override
	public List<Map<String, String>> getDuoExecErrorSiteList(WorkFlowManagementModel runTestModel,Integer programId, List<String> userNameList,
			String programName, String useCaseName) {
		List<Map<String, String>> result = new ArrayList<>();
		List<Audit5GCBandSummaryEntity> summaryDetails = null;
		try {
			List<String> neIdList = new ArrayList<>();
			Map<String, Object> resultMap = workFlowManagementRepository.getDuoExecErrorSiteList(runTestModel, programId, userNameList, useCaseName);
			if (resultMap != null && resultMap.get("list") != null) {
				summaryDetails = (List<Audit5GCBandSummaryEntity>)resultMap.get("list");
				if (summaryDetails != null && !summaryDetails.isEmpty()) {
					for (Audit5GCBandSummaryEntity summary : summaryDetails) {
						if(neIdList.contains(summary.getNeId())) {
							continue;
						} else {
							neIdList.add(summary.getNeId());
						}
						if(programName.contains("5G-CBAND")) {
							List<Audit5GCBandIssueEntity> audit5GCbandIssueEntityList = audit5GCBandIssueRepository.getAudit5GCBandIssueEntityList(summary.getNeId());
							if(audit5GCbandIssueEntityList != null && !audit5GCbandIssueEntityList.isEmpty()) {
								Audit5GCBandIssueEntity audit5GCBandIssueEntity = audit5GCbandIssueEntityList.get(0);
								if(!audit5GCBandIssueEntity.getRunTestEntity().getId().equals(summary.getRunTestEntity().getId())) {
									continue;
								}
							}
						} else if(programName.contains("5G-DSS")) {
							List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = audit5GDSSIssueRepository.getAudit5GDSSIssueEntityList(summary.getNeId());
							if(audit5GDSSIssueEntityList != null && !audit5GDSSIssueEntityList.isEmpty()) {
								Audit5GDSSIssueEntity audit5GDSSIssueEntity = audit5GDSSIssueEntityList.get(0);
								if(!audit5GDSSIssueEntity.getRunTestEntity().getId().equals(summary.getRunTestEntity().getId())) {
									continue;
								}
							}
						}
						
						Map<String, String> mapDetails =  new HashMap<>();
						mapDetails.put("neId", summary.getNeId());
						mapDetails.put("neName", summary.getRunTestEntity().getNeName());
						mapDetails.put("timeStamp", summary.getRunTestEntity().getCreationDate().toString());
						mapDetails.put("userName", summary.getRunTestEntity().getUserName());
						result.add(mapDetails);
					}
				}
			}
		} catch (Exception e){
			logger.error("Exception in getInProgressWorkFlowManagementDetails() : " + ExceptionUtils.getFullStackTrace(e)); 
		}
		return result;
	}
}
