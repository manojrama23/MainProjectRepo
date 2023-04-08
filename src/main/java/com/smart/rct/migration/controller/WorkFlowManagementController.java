package com.smart.rct.migration.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.common.models.MileStonesModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.RetTestEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.migration.repository.RetRepository;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.repositoryImpl.UseCaseBuilderRepositoryImpl;
import com.smart.rct.migration.service.RetService;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.postmigration.service.Audit4GFsuIssueService;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.postmigration.service.Audit4GIssueService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.Audit5GCBandIssueService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;
import com.smart.rct.postmigration.service.Audit5GDSSIssueService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.controller.GenerateCsvController;
import com.smart.rct.premigration.controller.UploadCIQController;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.models.WorkFlowManagementPremigration;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class WorkFlowManagementController {

	final static Logger logger = LoggerFactory.getLogger(WorkFlowManagementController.class);
	
	@Autowired
	FetchProcessRepository fetchProcessRepository;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	UploadFileService uploadFileService;
	
	@Autowired
	RunTestService runTestService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	GenerateCsvController generateCsvController;

	@Autowired
	RunTestController runTestController;

	@Autowired
	WorkFlowManagementService workFlowManagementService;

	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	
	@Autowired
	UploadCIQController uploadCIQController;

	@Autowired
	UseCaseBuilderRepositoryImpl useCaseBuilderRepositoryImpl;

	@Autowired
	RunTestRepository runTestRepository;

	@Autowired
	NeMappingService neMappingService;
	
	@Autowired
	Audit4GSummaryService audit4GSummaryService;
	
	@Autowired
	Audit4GIssueService audit4GIssueService;
	
	@Autowired
	Audit4GFsuSummaryService audit4GFsuSummaryService;
	
	@Autowired
	Audit4GFsuIssueService audit4GFsuIssueService;
	
	@Autowired
	Audit5GCBandSummaryService audit5GCBandSummaryService;
	
	@Autowired
	Audit5GCBandIssueService audit5GCBandIssueService;
	
	@Autowired
	Audit5GDSSSummaryService audit5GDSSSummaryService;
	
	@Autowired
	Audit5GDSSIssueService audit5GDSSIssueService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	RetService retService;
	
	@Autowired
	RetRepository retRepository;
	
	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;
	
	/**
	 * This method will execute the Runtest do
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/workFlowManageRunTest")
	public JSONObject workFlowTest(@RequestBody JSONObject runTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		JSONObject negrowInputs = null;
		JSONObject premigrationInputs = null;
		JSONObject neStatusInputs = null;
		JSONObject preauditInputs = null;
		JSONObject migrationInputs = null;
		JSONObject postmigrationInputs = null;
		JSONObject fetchUseCaseInputs = null;
		String migrationType1 = null;
		String migrationSubType1 = null;
		String ciqFileName = null;
		String programName = null;
		String migrationType = null;
		String migrationSubType = null;
		String programId = null;
		String enbId = null;
		String enbName = null;
		Integer workFlowId = null;
		String state = null;
		String sanePassword = null;
		boolean useCurrPassword = true;
		// String state = "normal";
		List<Map<String, String>> enbList = null;
		Map<String, Integer> temp = new HashMap<>();
		WorkFlowManagementEntity WorkFlowManagementEntity = null;
		OvScheduledEntity ovScheduledEntity = null;
		MileStonesModel mileStonesModel = null;
		try {
			System.out.println("Starting work flow test");
			/*if(runTestParams.containsKey("sessionId")) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", "daa");
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			return resultMap;
			}*/
			
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			programId = runTestParams.get("programId").toString();
			//enbId = runTestParams.get("enbId").toString();
			//enbName = runTestParams.get("enbName").toString();
			programName = runTestParams.get("programName").toString();
			runTestParams.put("testname", "null");
			String testname = runTestParams.get("testname").toString();
			runTestParams.put("testDesc", "");
			// boolean wfmgenerateallgnbs = true;
			//boolean generateAllSites = (boolean) runTestParams.get("generateAllSites");
			
			String enbWfmStatus = workFlowManagementService.getRunnTestWfmEnbProgressStatus(runTestParams);
			if(enbWfmStatus.contains("Cannot initiate execution")) {
				resultMap.put("reason", "eNodeB is in progress, cannot initiate execution");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				return resultMap;
			}
			
			
			enbList =(List<Map<String, String>>)runTestParams.get("neDetails");
			if (runTestParams.containsKey("state") && runTestParams.get("state") != null
					&& StringUtils.isNotEmpty(runTestParams.get("state").toString())) {
				state = runTestParams.get("state").toString();
			}
			// String isTestNamePresent =
			// workFlowManagementService.getWFMRunTestEntity(Integer.valueOf(programId),
			// testname);
			// if (isTestNamePresent != Constants.SUCCESS) {
			// resultMap.put("status", Constants.FAIL);
			// resultMap.put("reason", isTestNamePresent);
			// resultMap.put("sessionId", sessionId);
			// resultMap.put("serviceToken", serviceToken);
			// return resultMap;
			// }
			if (runTestParams.containsKey("id") && runTestParams.get("id") != null
					&& StringUtils.isNotEmpty(runTestParams.get("id").toString())) {
				workFlowId = (Integer) runTestParams.get("id");
			}
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			if (runTestParams.containsKey("premigration") && runTestParams.get("premigration") != null) {
				premigrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("premigration")));
			}
			if (runTestParams.containsKey("negrow") && runTestParams.get("negrow") != null) {
				negrowInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("negrow")));

				/*JSONObject runTestFormDetails = (JSONObject) negrowInputs.get("runTestFormDetails");
				List<String> useCase = (List<String>) runTestFormDetails.get("useCase");
				String ver = null;
				if (programName.contains("VZN-5G-MM")) {

				//	ver = runTestFormDetails.get("lsmVersion").toString();
					
					NeMappingModel neMappingModel = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					programDetailsEntity.setId(Integer.parseInt(programId));
					neMappingModel.setProgramDetailsEntity(programDetailsEntity);
					neMappingModel.setEnbId(enbList.get(0).get("neId").toString());
					List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
					NeMappingEntity neMappingEntity = neMappingEntities.get(0);
					 ver = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().toString();
				//	String neName = neMappingEntity.getNetworkConfigEntity().getNeName().toString();

					if (ver.equals("20.B.0")) {
						ver = "20B";
					} else {
						ver = "20A";
					}
					String ver1 = ver;
					useCase = useCase.stream().map(usecase -> usecase.concat(ver1)).collect(Collectors.toList());
					// useCase.replaceAll(usecase -> usecase.concat(ver));
					runTestFormDetails.replace("useCase", useCase);
					negrowInputs.replace("runTestFormDetails", runTestFormDetails);
				}
				if (useCase.size() == 3) {
					String pnpUsecase = "pnp";
					if (programName.contains("VZN-5G-MM")) {
						pnpUsecase = "pnp" + ver;
					}

					useCase.clear();
					useCase.add(pnpUsecase);
					runTestFormDetails.replace("useCase", useCase);
					negrowInputs.replace("runTestFormDetails", runTestFormDetails);
				}*/

			}
                       if (runTestParams.containsKey("Nestatus") && runTestParams.get("Nestatus") != null) {
				neStatusInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("Nestatus")));
			}
			if (runTestParams.containsKey("preaudit") && runTestParams.get("preaudit") != null) {
				preauditInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("preaudit")));
			}
			if (runTestParams.containsKey("migration") && runTestParams.get("migration") != null) {
				migrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("migration")));
			}
			if (runTestParams.containsKey("postmigration") && runTestParams.get("postmigration") != null) {
				postmigrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("postmigration")));
			}
			if (premigrationInputs != null) {
				ciqFileName = premigrationInputs.get("ciqFileName").toString();
			}
			if (migrationInputs != null && !migrationInputs.isEmpty()) {
				migrationType = migrationInputs.get("migrationType").toString();
				migrationSubType = migrationInputs.get("migrationSubType").toString();
			}
			if (negrowInputs != null && !negrowInputs.isEmpty()) {
				migrationType1 = negrowInputs.get("migrationType").toString();
				migrationSubType1 = negrowInputs.get("migrationSubType").toString();
			}
			if ("normal".equalsIgnoreCase(state)) {

				if (negrowInputs != null) {
					WorkFlowManagementPremigration wfmObject = new WorkFlowManagementPremigration();
					Map inputJsonNEGrow = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) negrowInputs.get("runTestFormDetails")));
					List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonNEGrow
							.get("neDetails");
					for (int s = 0; s < neDetails.size(); s++) {
						Map<String, String> map = new HashMap<>();
						map = neDetails.get(s);
						String enbid = map.get("neId").toString();
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + enbid, wfmObject);
						inputJsonNEGrow.put("testDesc", "");
						// inputJsonPostMigration.put("testDesc", "");
						negrowInputs.put("runTestFormDetails", inputJsonNEGrow);
						// postmigrationInputs.put("runTestFormDetails", inputJsonPostMigration);
					}
				}
				if (neStatusInputs != null) {
					WorkFlowManagementPremigration wfmObject = new WorkFlowManagementPremigration();
					Map inputJsonneStatus= (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) neStatusInputs.get("runTestFormDetails")));
					List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonneStatus
							.get("neDetails");
					for (int s = 0; s < neDetails.size(); s++) {
						Map<String, String> map = new HashMap<>();
						map = neDetails.get(s);
						String enbid = map.get("neId").toString();
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + enbid, wfmObject);
						inputJsonneStatus.put("testDesc", "");
						neStatusInputs.put("runTestFormDetails", inputJsonneStatus);
					}
				}
				if (preauditInputs != null) {
					WorkFlowManagementPremigration wfmObject = new WorkFlowManagementPremigration();
					Map inputJsonPreAudit= (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) preauditInputs.get("runTestFormDetails")));
					List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonPreAudit
							.get("neDetails");
					for (int s = 0; s < neDetails.size(); s++) {
						Map<String, String> map = new HashMap<>();
						map = neDetails.get(s);
						String enbid = map.get("neId").toString();
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + enbid, wfmObject);
						inputJsonPreAudit.put("testDesc", "");
						preauditInputs.put("runTestFormDetails", inputJsonPreAudit);
					}
				}
				if (migrationInputs != null) {
					WorkFlowManagementPremigration wfmObject = new WorkFlowManagementPremigration();
					Map inputJsonMigration = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) migrationInputs.get("runTestFormDetails")));
					// Map inputJsonPostMigration = (JSONObject) new JSONParser()
					// .parse(runTestParams.toJSONString((Map)
					// postmigrationInputs.get("runTestFormDetails")));
					List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonMigration
							.get("neDetails");
					for (int s = 0; s < neDetails.size(); s++) {
						Map<String, String> map = new HashMap<>();
						map = neDetails.get(s);
						String enbid = map.get("neId").toString();
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + enbid, wfmObject);
						inputJsonMigration.put("testDesc", "");
						// inputJsonPostMigration.put("testDesc", "");
						migrationInputs.put("runTestFormDetails", inputJsonMigration);
						// postmigrationInputs.put("runTestFormDetails", inputJsonPostMigration);
					}
				}
				if (postmigrationInputs != null) {
					WorkFlowManagementPremigration wfmObject = new WorkFlowManagementPremigration();
					// Map inputJsonMigration = (JSONObject) new JSONParser()
					// .parse(runTestParams.toJSONString((Map)
					// migrationInputs.get("runTestFormDetails")));
					Map inputJsonPostMigration = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) postmigrationInputs.get("runTestFormDetails")));
					List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonPostMigration
							.get("neDetails");
					for (int s = 0; s < neDetails.size(); s++) {
						Map<String, String> map = new HashMap<>();
						map = neDetails.get(s);
						String enbid = map.get("neId").toString();
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + enbid, wfmObject);
						// inputJsonMigration.put("testDesc", "");
						inputJsonPostMigration.put("testDesc", "");
						// migrationInputs.put("runTestFormDetails", inputJsonMigration);
						postmigrationInputs.put("runTestFormDetails", inputJsonPostMigration);
					}
				}

				if (workFlowId != null && workFlowId.intValue() > 0) {
					WorkFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(workFlowId);
				} else {
					List<LinkedHashMap<String, String>> neDetails = new ArrayList<>();
					String preMigValue = "InputsRequired";
					String migValue = "InputsRequired";
					String postMigValue = "InputsRequired";
					String neGrowValue = "InputsRequired";
					String preAuditValue = "InputsRequired";
					String neStatusValue = "InputsRequired";
					if (premigrationInputs != null) {
						preMigValue = "InProgress";
					}
					if (migrationInputs != null) {
						Map inputJsonMigration = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) migrationInputs.get("runTestFormDetails")));
						neDetails = (List<LinkedHashMap<String, String>>) inputJsonMigration.get("neDetails");
						useCurrPassword = (boolean) inputJsonMigration.get("currentPassword");
						sanePassword = inputJsonMigration.get("password").toString();
						System.out.println("****** not yet started");
						migValue = "NotYetStarted";
					}
					if (postmigrationInputs != null) {
						Map inputJsonPostMigration = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) postmigrationInputs.get("runTestFormDetails")));
						neDetails = (List<LinkedHashMap<String, String>>) inputJsonPostMigration.get("neDetails");
						useCurrPassword = (boolean) inputJsonPostMigration.get("currentPassword");
						sanePassword = inputJsonPostMigration.get("password").toString();
						postMigValue = "NotYetStarted";
						if(premigrationInputs == null) {
							preMigValue = "NotExecuted";
							migValue = "NotExecuted";
							neGrowValue = "NotExecuted";
							
						}
						if (preauditInputs == null) {
							preAuditValue = "NotExecuted";

						}
						if (neStatusInputs == null) {
							neStatusValue = "NotExecuted";

						}
					}
					if (neStatusInputs != null) {
						Map inputJsonneStatus = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) neStatusInputs.get("runTestFormDetails")));
						neDetails = (List<LinkedHashMap<String, String>>) inputJsonneStatus.get("neDetails");
						useCurrPassword = (boolean) inputJsonneStatus.get("currentPassword");
						sanePassword = inputJsonneStatus.get("password").toString();
						neStatusValue = "NotYetStarted";
						if (premigrationInputs == null) {
							preMigValue = "NotExecuted";
							migValue = "NotExecuted";
							neGrowValue = "NotExecuted";

						}
						if (preauditInputs == null) {
							preAuditValue = "NotExecuted";

						}
						
						if (postmigrationInputs == null) {
							postMigValue = "NotExecuted";

						}
					}
					if (preauditInputs != null) {
						Map inputJsonPREAUDIt = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) preauditInputs.get("runTestFormDetails")));
						neDetails = (List<LinkedHashMap<String, String>>) inputJsonPREAUDIt.get("neDetails");
						useCurrPassword = (boolean) inputJsonPREAUDIt.get("currentPassword");
						sanePassword = inputJsonPREAUDIt.get("password").toString();
						preAuditValue = "NotYetStarted";
						if (premigrationInputs == null) {
							preMigValue = "NotExecuted";
							migValue = "NotExecuted";
							neGrowValue = "NotExecuted";

						}
						if (postmigrationInputs == null) {
							postMigValue = "NotExecuted";

						}
						if (neStatusInputs == null) {
							neStatusValue = "NotExecuted";

						}
					}
					if (negrowInputs != null) {
						Map inputJsonNeGrow = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) negrowInputs.get("runTestFormDetails")));
						neDetails = (List<LinkedHashMap<String, String>>) inputJsonNeGrow.get("neDetails");
						useCurrPassword = (boolean) inputJsonNeGrow.get("currentPassword");
						sanePassword = inputJsonNeGrow.get("password").toString();
						neGrowValue = "NotYetStarted";
					}
					
					
					if (!useCurrPassword && sanePassword.isEmpty()) {
						for(Map<String, String> enb : enbList) {
							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(Integer.parseInt(programId));
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel.setEnbId(enb.get("neId"));
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							if (CommonUtil.isValidObject(neMappingEntities) && !neMappingEntities.isEmpty()) {
								NeMappingEntity neMappingEntity = neMappingEntities.get(0);
								if (CommonUtil.isValidObject(neMappingEntity)
										&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
										&& (neMappingEntity.getSiteConfigType().length() > 0)) {
									String neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();
									String neName = neMappingEntity.getNetworkConfigEntity().getNeName();
									String lsmId = String.valueOf(neMappingEntity.getNetworkConfigEntity().getId());
									
									JSONObject runtestJson = null;
									if(migrationInputs != null) {
										runtestJson = migrationInputs;
									}
									if(negrowInputs != null && runtestJson == null) {
										runtestJson = negrowInputs;
									}
									if(postmigrationInputs != null && runtestJson == null) {
										runtestJson = postmigrationInputs;
									}
									if(preauditInputs != null && runtestJson == null) {
										runtestJson = preauditInputs;
									}
									if(neStatusInputs != null && runtestJson == null) {
										runtestJson = neStatusInputs;
									}
									Map inputJson = (JSONObject) new JSONParser()
											.parse(runtestJson.toJSONString((Map) runtestJson.get("runTestFormDetails")));
									
									inputJson.put("lsmVersion", neVersion);
									inputJson.put("lsmName", neName);	
									inputJson.put("lsmId", lsmId);
									runtestJson.put("runTestFormDetails", inputJson);
									JSONObject output = new JSONObject();
									output = runTestService.getSaneDetailsforPassword(runtestJson);

									if (!output.isEmpty()) {
										resultMap.put("status", "PROMPT");
										resultMap.put("password", output);
										resultMap.put("sessionId", sessionId);
										resultMap.put("serviceToken", serviceToken);
										resultMap.put("requestType", "RUN_TEST");

										return resultMap;
									}
								}
							}
						}						
					}
					
					//int size1 = neDetails.size();
					//String oldGnodeb = "0" + runTestParams.get("enbId").toString();
					List<String> notMappedNe = new ArrayList<>();
					NetworkConfigEntity previousNetworkConfigEntity = null;
					boolean differentSmVersion = false;
					for (Map<String, String> enb : enbList) {
						String singleGnob = "0" +  enb.get("neId");
						String modegnbName = enb.get("neName");
						String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
								.format(new Timestamp(System.currentTimeMillis()));
						String testName = modegnbName + "_" + timeStampDate;
						System.out.println("work flow test singleGnob : "+singleGnob);
						runTestParams.put("enbId", enb.get("neId"));
						runTestParams.put("enbName", modegnbName);
						runTestParams.put("testname", "WFM_" + testName);
						runTestParams.put("preMigStatus", preMigValue);
						runTestParams.put("MigStatus", migValue);
						runTestParams.put("postMigStatus", postMigValue);
						runTestParams.put("neGrowStatus", neGrowValue);
						runTestParams.put("preAuditStatus", preAuditValue);
						runTestParams.put("neStatusStatus", neStatusValue);
						if(programName.contains("5G-MM")) {
							runTestParams.put("siteName", enb.get("siteName"));
						}
						// code for Display Failure Logs
						String fileName = null;
						String folderName = null;
						ArrayList<String> namesList = new ArrayList<String>();
						namesList.add("pre");
						namesList.add("negrow");
						namesList.add("nestatus");
						namesList.add("preaudit");
						namesList.add("mig");
						namesList.add("post");
						ArrayList<String> arr = new ArrayList<String>();
						folderName = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "/ErrorLog/";
						for (int i = 0; i <= 5; i++) {
							String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
									.format(new Timestamp(System.currentTimeMillis()));
							fileName = singleGnob + "_" + timeStamp + ".log";
							arr.add(folderName + namesList.get(i) + fileName);
						}
						runTestParams.put("allFilePaths", arr);
						//if (!programName.contains("FSU")) {
							NeMappingModel neMappingModel = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
							programDetailsEntity.setId(Integer.parseInt(programId));
							neMappingModel.setProgramDetailsEntity(programDetailsEntity);
							neMappingModel.setEnbId(enb.get("neId").toString());
							List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
							if (CommonUtil.isValidObject(neMappingEntities) && !neMappingEntities.isEmpty()) {
								NeMappingEntity neMappingEntity = neMappingEntities.get(0);
								if (!CommonUtil.isValidObject(neMappingEntity)
										|| !CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
										|| !(neMappingEntity.getSiteConfigType().length() > 0)) {
									notMappedNe.add(enb.get("neId"));
									logger.error("Ne Mapping not found for " + enb.get("neName"));
									File f1 = new File(folderName);
									f1.mkdir();
									String failFileName = folderName + namesList.get(0) + fileName;
									if (failFileName != null) {
										File f = new File(failFileName);
										if (f.exists()) {
											commonUtil.appendMessage(folderName + namesList.get(0) + fileName, "",
													"Ne Mapping not found for " + enb.get("neName"), "", "");
										} else {
											if (createNewFile(failFileName)) {
												commonUtil.appendMessage(failFileName, "",
														"PREMIGRATION" + "\n" + "------------------------------" + "\n",
														"", "");
												commonUtil.appendMessage(failFileName, "",
														"Ne Mapping not found for " + enb.get("neName"), "", "");
											}
										}
									}
								} else {
									if(previousNetworkConfigEntity==null) {
										previousNetworkConfigEntity = neMappingEntity.getNetworkConfigEntity();
									}
									if(previousNetworkConfigEntity!=null && neMappingEntity.getNetworkConfigEntity()!=null && 
											!previousNetworkConfigEntity.getNeVersionEntity().getId().equals(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getId()) && 
											!differentSmVersion) {
										differentSmVersion = true;
									}
									String lsmVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity()
											.getNeVersion().toString();
									String lsmName = neMappingEntity.getNetworkConfigEntity().getNeName().toString();
									runTestParams.put("lsmVersion", lsmVersion);
									runTestParams.put("lsmName", lsmName);
								}								
							} else {
								notMappedNe.add(enb.get("neId"));
								if (folderName + namesList.get(0) + fileName != null) {
									File f = new File(folderName + namesList.get(0) + fileName);
									if (f.exists()) {
										commonUtil.appendMessage(folderName + namesList.get(0) + fileName, "",
												"Ne Mapping not found for " + enb.get("neName"), "", "");
									} else {
										if (createNewFile(folderName + namesList.get(0) + fileName)) {
											commonUtil.appendMessage(folderName + namesList.get(0) + fileName, "",
													"PREMIGRATION" + "\n" + "------------------------------" + "\n", "",
													"");
											commonUtil.appendMessage(folderName + namesList.get(0) + fileName, "",
													"Ne Mapping not found for " + enb.get("neName"), "", "");
										}
									}
								}
								logger.error("Ne Mapping not found for " + enb.get("neName"));
							}
						//}						
						if(runTestParams.containsKey("type") && runTestParams.get("type").equals("OV") && runTestParams.containsKey("wfmid")) {
							ovScheduledEntity = ovScheduledTaskService.getOvScheduledServiceDetails(runTestParams.get("trackerId").toString(),runTestParams.get("enbId").toString());
							ovScheduledEntity.setWorkFlowManagementEntity(null);
							ovScheduledEntity.setPreMigStatus(null);
							ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
							workFlowManagementService.deleteWfmrunTest(Integer.parseInt(runTestParams.get("wfmid").toString()));
						}
				        logger.error("DB LOGS:Before Creating Rows For WFM NE List : "+enbList);
						WorkFlowManagementEntity = workFlowManagementService.insertWorkManagementDetails(runTestParams);
						logger.error("DB LOGS:After Creating Rows For WFM NE List : "+enbList);
						if(runTestParams.containsKey("type")&& runTestParams.get("type").equals("OV")) {
							String nameOfSite = null;
							ovScheduledEntity = ovScheduledTaskService.getOvScheduledServiceDetails(runTestParams.get("trackerId").toString(),runTestParams.get("enbId").toString());
							ovScheduledEntity.setWorkFlowManagementEntity(WorkFlowManagementEntity);
							if(programName.contains("5G-MM"))
							{
								 nameOfSite = WorkFlowManagementEntity.getSiteName().toString();
								 ovScheduledEntity.setSiteName(nameOfSite);
							}
							if(programName.contains("USM")) {
								//dummy IP
								//if(null!= ovScheduledEntity.getIntegrationType().toString())
								premigrationInputs.put("integrationType", ovScheduledEntity.getIntegrationType().toString());

							} else {
								premigrationInputs.put("integrationType", "NA");
							}
							//ovScheduledEntity.setSiteName(nameOfSite);
							ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
						}
						WorkFlowManagementEntity.setPreErrorFile(folderName + namesList.get(0) + fileName);
						temp.put(singleGnob, WorkFlowManagementEntity.getId());

						// date.add(WorkFlowManagementEntity.getCreationDate().toString());

					}
					int enbListsize = enbList.size();
					enbList = enbList.stream().filter(x -> !notMappedNe.contains(x.get("neId"))).map(x -> x)
							.collect(Collectors.toList());
					if (enbList.size() != enbListsize) {
						for (String enbid : notMappedNe) {
							int wfmid = temp.get("0" + enbid);
							WorkFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(wfmid);
							WorkFlowManagementEntity.setPreMigStatus("Failure");
							WorkFlowManagementEntity.setStatus("Completed");
							WorkFlowManagementEntity.setNeGrowStatus("CannotStart");
							WorkFlowManagementEntity.setPostMigStatus("CannotStart");
							WorkFlowManagementEntity.setMigStatus("CannotStart");
							WorkFlowManagementEntity.setPreAuditStatus("CannotStart");
							WorkFlowManagementEntity.setNeUpStatus("CannotStart");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							if(runTestParams.containsKey("type") && runTestParams.get("type").equals("OV")) {
							ovScheduledEntity = ovScheduledTaskService.getOvDetails(wfmid);
							ovScheduledEntity.setPreMigGrowStatus("Failure");
							ovScheduledEntity.setEnvStatus("Failure");
							ovScheduledEntity.setPreMigStatus("Failure");
							ovScheduledEntity = ovScheduledTaskService.mergeOvScheduledDetails(ovScheduledEntity);
							}
						}
						if (enbList.isEmpty()) {
							for (String enbid : notMappedNe) {
								int wfmid = temp.get("0" + enbid);
								workFlowManagementService.deleteWfmrunTest(wfmid);
							}
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", "Selected Ne ids are not Mapped");
							return resultMap;
						}
					}
					if(differentSmVersion) {
						for (String enbid : notMappedNe) {
							int wfmid = temp.get("0" + enbid);
							workFlowManagementService.deleteWfmrunTest(wfmid);
						}
						for(Map<String, String> enb : enbList) {
							String singleGnob = "0" +  enb.get("neId");
							int wfmid = temp.get(singleGnob);
							workFlowManagementService.deleteWfmrunTest(wfmid);
						}
						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", "Selected Sites don't belong to the same SM Version");
						return resultMap;
					}					
				}
				

				// For Thread implementation

				workFlowManagementService.runWFM(premigrationInputs, migrationInputs, postmigrationInputs, negrowInputs,preauditInputs,neStatusInputs,
						enbList, programId, programName, temp);

				/*
				 * if(WorkFlowManagementEntity!=null) {
				 * workFlowManagementService.getWorkFlowManageStatus(WorkFlowManagementEntity);
				 * }
				 */
			} else if ("reRun".equalsIgnoreCase(state)) {
				WorkFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(workFlowId);

				if (premigrationInputs != null && WorkFlowManagementEntity != null) {
					// JSONObject preMigrationStatusDetails = generateCsvController
					// .generateCiqNeBasedFiles(premigrationInputs);
					JSONObject preMigrationStatusDetails = generateCsvController.generateFile(premigrationInputs);

					if (preMigrationStatusDetails != null && preMigrationStatusDetails.containsKey("status")
							&& Constants.SUCCESS.equalsIgnoreCase(preMigrationStatusDetails.get("status").toString())) {
						if (!ObjectUtils.isEmpty(GlobalStatusMap.WFM_PRE_MIG_USECASES)
								&& GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId)) {
							ConcurrentHashMap<String, WorkFlowManagementPremigration> WFM_PRE_MIG_USECASES = GlobalStatusMap.WFM_PRE_MIG_USECASES;
						}
					}
				}

				if (migrationInputs != null && WorkFlowManagementEntity != null)

				{
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
					if (delteResultTestParams != null
							&& "success".equalsIgnoreCase(delteResultTestParams.get("status").toString())) {

						JSONObject migrationStatusDetails = runTestController.runTest(migrationInputs);

						if (migrationStatusDetails != null && migrationStatusDetails.containsKey("status")
								&& Constants.SUCCESS
										.equalsIgnoreCase(migrationStatusDetails.get("status").toString())) {
							Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) migrationStatusDetails
									.get("runTestEntity");

							RunTestEntity runTestEntityMigration = runTestEntityMap.get(enbId);

							if (runTestEntityMigration != null && WorkFlowManagementEntity != null) {
								if (runTestEntityMigration != null && WorkFlowManagementEntity != null) {
									WorkFlowManagementEntity.setRunMigTestEntity(runTestEntityMigration);
									WorkFlowManagementEntity.setMigStatus("InProgress");
									WorkFlowManagementEntity = workFlowManagementService
											.mergeWorkFlowMangement(WorkFlowManagementEntity);
									while (WorkFlowManagementEntity.getRunMigTestEntity() != null
											&& WorkFlowManagementEntity.getRunMigTestEntity().getId() > 0) {
										RunTestEntity runTestEntityMigrationResult = workFlowManagementService
												.getRunTestEntity(runTestEntityMigration);

										if (runTestEntityMigrationResult != null && "Completed"
												.equalsIgnoreCase(runTestEntityMigrationResult.getProgressStatus())) {
											WorkFlowManagementEntity
													.setMigStatus(runTestEntityMigrationResult.getProgressStatus());
											WorkFlowManagementEntity = workFlowManagementService
													.mergeWorkFlowMangement(WorkFlowManagementEntity);
											break;
										}
										Thread.sleep(10000);
									}

								}
							}

						} else {

							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", migrationStatusDetails.get("reason").toString());
							return resultMap;
						}
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", "existing test not able to delete");
					}

				}

				if (postmigrationInputs != null && WorkFlowManagementEntity != null)

				{

					JSONObject postMigrationStatusDetails = runTestController.runTest(postmigrationInputs);

					if (postMigrationStatusDetails != null && postMigrationStatusDetails.containsKey("status")
							&& Constants.SUCCESS
									.equalsIgnoreCase(postMigrationStatusDetails.get("status").toString())) {
						Map<String, RunTestEntity> runTestEntityMap = (Map<String, RunTestEntity>) postMigrationStatusDetails
								.get("runTestEntity");

						RunTestEntity runTestEntityPostMigration = runTestEntityMap.get(enbId);

						if (runTestEntityPostMigration != null && WorkFlowManagementEntity != null) {
							WorkFlowManagementEntity.setRunPostMigTestEntity(runTestEntityPostMigration);

							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);

							WorkFlowManagementEntity.setPostMigStatus("InProgress");
							WorkFlowManagementEntity = workFlowManagementService
									.mergeWorkFlowMangement(WorkFlowManagementEntity);
							while (WorkFlowManagementEntity.getRunPostMigTestEntity() != null
									&& WorkFlowManagementEntity.getRunPostMigTestEntity().getId() > 0) {
								RunTestEntity runTestEntityPostMigrationResult = workFlowManagementService
										.getRunTestEntity(runTestEntityPostMigration);

								if (runTestEntityPostMigrationResult != null && "Completed"
										.equalsIgnoreCase(runTestEntityPostMigrationResult.getProgressStatus())) {
									WorkFlowManagementEntity
											.setPostMigStatus(runTestEntityPostMigrationResult.getProgressStatus());
									WorkFlowManagementEntity = workFlowManagementService
											.mergeWorkFlowMangement(WorkFlowManagementEntity);
									break;
								}
								Thread.sleep(10000);
							}

						}

					} else {

						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", postMigrationStatusDetails.get("reason").toString());
						return resultMap;
					}

				}

			}
			resultMap.put("status", Constants.SUCCESS);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.error("Exception in workFlowTest() in WorkFlowManagementController"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	// for run api
	@SuppressWarnings({ "unchecked", "unused", "static-access" })
	@PostMapping(value = "/runIndependent")
	public JSONObject runIndependent(@RequestBody JSONObject runTestParams) {
		String state = null;
		JSONObject neGrowInputs = null;
		JSONObject migrationInputs = null;
		JSONObject preAuditInputs = null;
		JSONObject neStatusInputs = null;
		JSONObject postmigrationInputs = null;
		WorkFlowManagementEntity WorkFlowManagementEntity = null;
		String sessionId = null;
		String serviceToken = null;
		String programId = null;
		String enbId = null;
		String programName = null;
		List<Map<String, String>> enbList = null;
		Integer workFlowId = null;
		JSONObject resultMap = new JSONObject();
		JSONObject premigrationInputs = null;
		JSONObject negrowInputs = null;

		try {
			enbList = (List<Map<String, String>>) runTestParams.get("neDetails");
			String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String enbName = enbList.get(0).get("neName").toString();
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			programId = runTestParams.get("programId").toString();
			// enbId = runTestParams.get("enbId").toString();
			// enbName = runTestParams.get("enbName").toString();
			programName = runTestParams.get("programName").toString();
			// String testname = runTestParams.get("testname").toString();
			String testname = "WFM_" + enbName + "_" + timeStampDate;
			runTestParams.put("testDesc", "");
			runTestParams.put("premigration", null);
			
			/*String enbWfmStatus = workFlowManagementService.getRunnTestWfmEnbProgressStatus(runTestParams);
			if(enbWfmStatus.contains("cannot initiate execution")) {
				resultMap.put("reason", "eNodeB is in progress, cannot initiate execution");
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				return resultMap;
			}*/

			//enbList = (List<Map<String, String>>) runTestParams.get("neDetails");
			if (runTestParams.containsKey("state") && runTestParams.get("state") != null
					&& StringUtils.isNotEmpty(runTestParams.get("state").toString())) {
				state = runTestParams.get("state").toString();
			}

			if (runTestParams.containsKey("id") && runTestParams.get("id") != null
					&& StringUtils.isNotEmpty(runTestParams.get("id").toString())) {
				workFlowId = (Integer) runTestParams.get("id");
			}
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			
			WorkFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(workFlowId);
			OvScheduledEntity ovScheduledEntity =ovScheduledTaskService.getOvDetails(workFlowId);
			
			RunTestEntity runtestNegrowent = null;
			RunTestEntity runtestMigent = null;
			RunTestEntity runtestPostmigent = null;
			RunTestEntity runtestPreAudit = null;
			RunTestEntity runtestNeStatus = null;
			runtestNegrowent = WorkFlowManagementEntity.getRunNEGrowEntity();
			runtestMigent = WorkFlowManagementEntity.getRunMigTestEntity();
			runtestPostmigent = WorkFlowManagementEntity.getRunPostMigTestEntity();
			runtestPreAudit = WorkFlowManagementEntity.getRunPreAuditTestEntity();
			runtestNeStatus = WorkFlowManagementEntity.getRunNEStatusTestEntity();

			if (runTestParams.containsKey("premigration") && runTestParams.get("premigration") != null) {
				premigrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("premigration")));
			}
			if (runTestParams.containsKey("negrow") && runTestParams.get("negrow") != null) {
				negrowInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("negrow")));
				

				JSONObject runTestFormDetails = (JSONObject) negrowInputs.get("runTestFormDetails");
				if(ovScheduledEntity!=null)
				{
					runTestFormDetails.put("trackerId", ovScheduledEntity.getTrackerId());
				}
				List<JSONObject> useCase = (List<JSONObject>) runTestFormDetails.get("useCase");
				List<JSONObject> scripts = (List<JSONObject>) runTestFormDetails.get("scripts");

				/*if (useCase.size() == 3) {
					for (JSONObject uc : useCase) {
						if (uc.get("useCaseName").toString().contains("pnp")) {
							String usecase = (String) uc.get("useCaseName");
							List<JSONObject> useCaseList = new ArrayList<>();
							useCaseList.add(uc);
							useCase = useCaseList;
							runTestFormDetails.replace("useCase", useCase);
							negrowInputs.replace("runTestFormDetails", runTestFormDetails);
						}
					}
					for (JSONObject sc : scripts) {
						if (sc.get("useCaseName").toString().contains("pnp")) {
							String script = (String) sc.get("useCaseName");
							List<JSONObject> scriptsList = new ArrayList<>();
							scriptsList.add(sc);
							scripts = scriptsList;
							runTestFormDetails.replace("scripts", scripts);
							negrowInputs.replace("runTestFormDetails", runTestFormDetails);
						}
					}
				}*/

			}
			if (runTestParams.containsKey("preaudit") && runTestParams.get("preaudit") != null) {
				preAuditInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("preaudit")));

			}else if (runTestParams.containsKey("preAudit") && runTestParams.get("preAudit") != null) {
				preAuditInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("preAudit")));

			}
			if (runTestParams.containsKey("nestatus") && runTestParams.get("nestatus") != null) {
				neStatusInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("nestatus")));

			}else if (runTestParams.containsKey("Nestatus") && runTestParams.get("Nestatus") != null) {
				neStatusInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("Nestatus")));

			}
			if (runTestParams.containsKey("migration") && runTestParams.get("migration") != null) {
				migrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("migration")));

			}
			if (runTestParams.containsKey("postmigration") && runTestParams.get("postmigration") != null) {
				postmigrationInputs = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("postmigration")));
			}

			//if (!programName.contains("FSU")) {
				NeMappingModel neMappingModel = new NeMappingModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(Integer.parseInt(programId));
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setEnbId(enbList.get(0).get("neId").toString());
				List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);
				String neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
						.toString();
				String neName = neMappingEntity.getNetworkConfigEntity().getNeName().toString();
				String lsmId = String.valueOf(neMappingEntity.getNetworkConfigEntity().getId());
				if (premigrationInputs != null) {
					premigrationInputs.put("neVersion", neVersion);

				}
				if (negrowInputs != null) {
					Map inputJsonNEGrowTemp = (JSONObject) new JSONParser()
							.parse(negrowInputs.toJSONString((Map) negrowInputs.get("runTestFormDetails")));
					inputJsonNEGrowTemp.put("lsmVersion", neVersion);
					inputJsonNEGrowTemp.put("lsmName", neName);
					inputJsonNEGrowTemp.put("lsmId", lsmId);
					inputJsonNEGrowTemp.put("testname", testname);
					inputJsonNEGrowTemp.put("testDesc", "Ran from WFM");
					negrowInputs.put("runTestFormDetails", inputJsonNEGrowTemp);
					WorkFlowManagementEntity.setNeGrowStatus("NotYetStarted");
					WorkFlowManagementEntity.setRunNEGrowEntity(null);
					String errorFilePath = WorkFlowManagementEntity.getNegrowErrorFile();
					FileUtil.deleteFileOrFolder(errorFilePath);
				}
				if (neStatusInputs != null) {
					Map inputJsonpnestatus = (JSONObject) new JSONParser().parse(
							neStatusInputs.toJSONString((Map) neStatusInputs.get("runTestFormDetails")));
					inputJsonpnestatus.put("lsmVersion", neVersion);
					inputJsonpnestatus.put("lsmName", neName);
					inputJsonpnestatus.put("lsmId", lsmId);
					inputJsonpnestatus.put("testname", testname);
					inputJsonpnestatus.put("testDesc", "Ran from WFM");
					if(ovScheduledEntity!=null)
					{
						inputJsonpnestatus.put("trackerId", ovScheduledEntity.getTrackerId());
					}
					neStatusInputs.put("runTestFormDetails", inputJsonpnestatus);
					//WorkFlowManagementEntity.setPostMigStatus("NotYetStarted");
					//WorkFlowManagementEntity.setRunPostMigTestEntity(null);
					WorkFlowManagementEntity.setNeUpStatus("NotYetStarted");
					WorkFlowManagementEntity.setRunNEStatusTestEntity(null);
					
					String errorFilePath = WorkFlowManagementEntity.getPREAUDITErrorFile();
					FileUtil.deleteFileOrFolder(errorFilePath);
				}
				if (preAuditInputs != null) {
					Map inputJsonpreAuditGrowTemp = (JSONObject) new JSONParser().parse(
							preAuditInputs.toJSONString((Map) preAuditInputs.get("runTestFormDetails")));
					inputJsonpreAuditGrowTemp.put("lsmVersion", neVersion);
					inputJsonpreAuditGrowTemp.put("lsmName", neName);
					inputJsonpreAuditGrowTemp.put("lsmId", lsmId);
					inputJsonpreAuditGrowTemp.put("testname", testname);
					inputJsonpreAuditGrowTemp.put("testDesc", "Ran from WFM");
					if(ovScheduledEntity!=null)
					{
						inputJsonpreAuditGrowTemp.put("trackerId", ovScheduledEntity.getTrackerId());
					}
					preAuditInputs.put("runTestFormDetails", inputJsonpreAuditGrowTemp);
					//WorkFlowManagementEntity.setPostMigStatus("NotYetStarted");
					//WorkFlowManagementEntity.setRunPostMigTestEntity(null);
					
					WorkFlowManagementEntity.setPreAuditStatus("NotYetStarted");
					WorkFlowManagementEntity.setRunPreAuditTestEntity(null);
					String errorFilePath = WorkFlowManagementEntity.getNEStatusErrorFile();
					FileUtil.deleteFileOrFolder(errorFilePath);
				}
			
				if (migrationInputs != null) {
					Map inputJsonmigGrowTemp = (JSONObject) new JSONParser()
							.parse(migrationInputs.toJSONString((Map) migrationInputs.get("runTestFormDetails")));
					inputJsonmigGrowTemp.put("lsmVersion", neVersion);
					inputJsonmigGrowTemp.put("lsmName", neName);
					inputJsonmigGrowTemp.put("lsmId", lsmId);
					inputJsonmigGrowTemp.put("testname", testname);
					inputJsonmigGrowTemp.put("testDesc", "Ran from WFM");
					if(ovScheduledEntity!=null)
					{
						inputJsonmigGrowTemp.put("trackerId", ovScheduledEntity.getTrackerId());
					}
					migrationInputs.put("runTestFormDetails", inputJsonmigGrowTemp);
					WorkFlowManagementEntity.setMigStatus("NotYetStarted");
					WorkFlowManagementEntity.setRunMigTestEntity(null);
					String errorFilePath = WorkFlowManagementEntity.getMigErrorFile();
					FileUtil.deleteFileOrFolder(errorFilePath);
				}
				if (postmigrationInputs != null) {
					Map inputJsonpostmigGrowTemp = (JSONObject) new JSONParser().parse(
							postmigrationInputs.toJSONString((Map) postmigrationInputs.get("runTestFormDetails")));
					inputJsonpostmigGrowTemp.put("lsmVersion", neVersion);
					inputJsonpostmigGrowTemp.put("lsmName", neName);
					inputJsonpostmigGrowTemp.put("lsmId", lsmId);
					inputJsonpostmigGrowTemp.put("testname", testname);
					inputJsonpostmigGrowTemp.put("testDesc", "Ran from WFM");
					if(ovScheduledEntity!=null)
					{
						inputJsonpostmigGrowTemp.put("trackerId", ovScheduledEntity.getTrackerId());
					}
					postmigrationInputs.put("runTestFormDetails", inputJsonpostmigGrowTemp);
					WorkFlowManagementEntity.setPostMigStatus("NotYetStarted");
					WorkFlowManagementEntity.setRunPostMigTestEntity(null);
					String errorFilePath = WorkFlowManagementEntity.getPostErrorFile();
					FileUtil.deleteFileOrFolder(errorFilePath);
				}
			//}
			
			// For Thread implementation
			WorkFlowManagementEntity.setStatus("InProgress");
			WorkFlowManagementEntity = workFlowManagementService.mergeWorkFlowMangement(WorkFlowManagementEntity);
			if (negrowInputs != null && runtestNegrowent != null && runtestNegrowent.getId() > 0) {
				deleteRuntestDetails(runtestNegrowent, Integer.parseInt(programId));
			}
			if (neStatusInputs != null && runtestNeStatus != null && runtestNeStatus.getId() > 0) {
				deleteRuntestDetails(runtestNeStatus, Integer.parseInt(programId));
			}
			if (preAuditInputs != null && runtestPreAudit != null && runtestPreAudit.getId() > 0) {
				deleteRuntestDetails(runtestPreAudit, Integer.parseInt(programId));
			}
			if (migrationInputs != null && runtestMigent != null && runtestMigent.getId() > 0) {
				deleteRuntestDetails(runtestMigent, Integer.parseInt(programId));
			}
			if (postmigrationInputs != null && runtestPostmigent != null && runtestPostmigent.getId() > 0) {
				deleteRuntestDetails(runtestPostmigent, Integer.parseInt(programId));
			}
			//workFlowManagementService.runIndependentWFM(sessionId, serviceToken, state, premigrationInputs,
			//		migrationInputs, postmigrationInputs, negrowInputs, enbList, programId, programName, workFlowId);
			String status = workFlowManagementService.continueWFM(workFlowId, negrowInputs, migrationInputs, postmigrationInputs, enbList,preAuditInputs,neStatusInputs);
			if(Constants.SUCCESS.equals(status)) {
				resultMap.put("status",Constants.SUCCESS);
				resultMap.put("reason", "Run Started Successfully");
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Rerun not continued");
				WorkFlowManagementEntity.setStatus("Completed");
				workFlowManagementService.mergeWorkFlowMangement(WorkFlowManagementEntity);
			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runIndependent() in WorkFlowManagementController"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/loadWfmrunTest")
	public JSONObject loadWfmrunTest(@RequestBody JSONObject loadRunTestParams) {
		JSONObject ciqList = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		Integer programId;
		String searchStatus = null;
		String toDate = null;
		String fromDate = null;
		String userName = null;
		// String migrationType = null;
		// String migrationSubType = null;
		boolean showUserData = false;
		try {
			sessionId = loadRunTestParams.get("sessionId").toString();
			serviceToken = loadRunTestParams.get("serviceToken").toString();
			customerId = (Integer) loadRunTestParams.get("customerId");
			programId = (Integer) loadRunTestParams.get("programId");
			searchStatus = loadRunTestParams.get("searchStatus").toString();
			// migrationType = loadRunTestParams.get("migrationType").toString();
			// migrationSubType = loadRunTestParams.get("migrationSubType").toString();

			if (loadRunTestParams.containsKey("userName")) {
				userName = loadRunTestParams.get("userName").toString();
			}
			if (loadRunTestParams.containsKey("showUserData")) {
				showUserData = (boolean) loadRunTestParams.get("showUserData");
			}

			JSONObject ciqDetails = new JSONObject();
			ciqDetails.put("sessionId", sessionId);
			ciqDetails.put("serviceToken", serviceToken);
			ciqDetails.put("searchStatus", "load");
			ciqDetails.put("programId", programId);
			ciqDetails.put("customerId", customerId);

			Map<String, Object> paginationData = (Map<String, Object>) loadRunTestParams.get("pagination");
			int count;
			int page = (Integer) paginationData.get("page");
			if (paginationData.get("count") instanceof String) {
				count = Integer.parseInt((String) paginationData.get("count"));
			} else {
				count = (Integer) paginationData.get("count");
			}

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			List<Map> neVersionNameUSecaseMap = runTestService.getSmList(programId);
			List<Map> negrowuseCaseLst = new ArrayList<>();
			List<Map> miguseCaseLst = new ArrayList<>();
			List<Map> postmiguseCaseLst = new ArrayList<>();
			List<Map> useCaseLstRanAtp = new ArrayList<>();
			List<Map> preaudituseCaseLst = new ArrayList<>();
			List<Map> neStatueuseCaseLst = new ArrayList<>();

			// miguseCaseLst = runTestService.getUseCaseList(programId, "migration",
			// "precheck");
			postmiguseCaseLst = runTestService.getUseCaseList(programId, "postmigration", "AUDIT");
			useCaseLstRanAtp = runTestService.getUseCaseList(programId, "postmigration", "RANATP");
			preaudituseCaseLst =runTestService.getUseCaseList(programId, "premigration", "PREAUDIT");
			neStatueuseCaseLst =runTestService.getUseCaseList(programId, "premigration", "NESTATUS");
			ciqList = uploadCIQController.getCiqList(ciqDetails);

			Map<String, Object> runTestDetailsMap = null;

			if (Constants.LOAD.equals(searchStatus) && !showUserData) {
				Date endDate = new Date();
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				WorkFlowManagementModel runTestModel = new WorkFlowManagementModel();
				runTestModel.setFromDate(fromDate);
				runTestModel.setToDate(toDate);
				runTestDetailsMap = workFlowManagementService.getWorkFlowManagementDetails(runTestModel, page, count,
						programId);
			} else if (Constants.SEARCH.equals(searchStatus)) {
				WorkFlowManagementModel runTestModel = new Gson().fromJson(
						loadRunTestParams.toJSONString((Map) loadRunTestParams.get("searchCriteria")),
						WorkFlowManagementModel.class);
				runTestDetailsMap = workFlowManagementService.getWorkFlowManagementDetails(runTestModel, page, count,
						programId);
			} else if (Constants.LOAD.equals(searchStatus) && showUserData) {
				Date endDate = new Date();
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				WorkFlowManagementModel runTestModel = new WorkFlowManagementModel();
				runTestModel.setFromDate(fromDate);
				runTestModel.setToDate(toDate);
				runTestModel.setUserName(userName);
				runTestDetailsMap = workFlowManagementService.getWorkFlowManagementDetails(runTestModel, page, count,
						programId);
			}
			
			List<WorkFlowManagementModel> wfmModelList = (List<WorkFlowManagementModel>) runTestDetailsMap.get("list");
			
			boolean isInProgress;
			try {
				isInProgress = wfmModelList.stream().filter(x -> x.getStatus().equals("InProgress")).findAny().isPresent();
			} catch(Exception e) {
				isInProgress = true;
				logger.error(ExceptionUtils.getMessage(e));
			}
			

			/*
			 * if (runTestDetailsMap != null &&
			 * runTestDetailsMap.containsKey("progressStatus")) { AtomicBoolean
			 * statusOfInprogress = (AtomicBoolean) runTestDetailsMap.get("progressStatus");
			 * isInProgress = statusOfInprogress.get(); }
			 */

			ciqList.put("sessionId", sessionId);
			ciqList.put("serviceToken", serviceToken);
			ciqList.put("status", Constants.SUCCESS);
			/*
			 * ciqList.put("runTestTableDetails", runTestDetails); ciqList.put("pageCount",
			 * pageCount);
			 */
			ciqList.put("runTestTableDetails", runTestDetailsMap.get("list"));
			ciqList.put("pageCount", runTestDetailsMap.get("paginationNumber"));
			ciqList.put("smVersion", neVersionNameUSecaseMap);
			ciqList.put("migusecaselist", miguseCaseLst);
			ciqList.put("negrowuseCaseLst", negrowuseCaseLst);
			ciqList.put("postmigusecaselist", postmiguseCaseLst);
			ciqList.put("ranAtpUseCaseList", useCaseLstRanAtp);
			ciqList.put("preaudituseCaseLst", preaudituseCaseLst);
			ciqList.put("neStatueuseCaseLst", neStatueuseCaseLst);
			// ciqList.put("useCaseList", useCaseLst);
			ciqList.put("isInProgress", isInProgress);

		} catch (Exception e) {
			ciqList.put("status", Constants.FAIL);
			logger.info(
					"Exception in loadrunTest in WorkFlowManagementController" + ExceptionUtils.getFullStackTrace(e));
		}
		return ciqList;
	}

	/**
	 * This method will delete WFMTestDetails
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/deleteWfmRunTestData")
	public JSONObject deleteWfmRunTestData(@RequestBody JSONObject runTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer workFlowId = null;
		Integer customerId;

		Integer programId;
		Integer history;
		try {
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			workFlowId = (Integer) runTestParams.get("id");

			programId = (Integer) runTestParams.get("programId");
			history = Integer.valueOf(LoadPropertyFiles.getInstance().getProperty("actionPerformed"));
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			int page = 1;
			int count = 10;
			customerId = 1;

			WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
					.getWorkFlowManagementEntity(workFlowId);
			
			String threadname = workFlowManagementEntity.getWfmThreadName();
			if(threadname != null && "InProgress".equals(workFlowManagementEntity.getStatus())) {
				Set<Thread> setThreads = Thread.getAllStackTraces().keySet();
				setThreads.stream().filter(t -> t.getName().equals(threadname)).findAny().ifPresent(Thread :: interrupt);
				Thread.sleep(1000);
				workFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(workFlowId);
			}
			
			
			RunTestEntity runTestMigEntity = workFlowManagementEntity.getRunMigTestEntity();
			RunTestEntity runTestPostMigEntity = workFlowManagementEntity.getRunPostMigTestEntity();
			RunTestEntity runTestNEGrowEntity = workFlowManagementEntity.getRunNEGrowEntity();
			RunTestEntity runtestrantAtpEntity = workFlowManagementEntity.getRunRanAtpTestEntity();
			RunTestEntity runtestrantpreaAudit = workFlowManagementEntity.getRunPreAuditTestEntity();
			RunTestEntity runtestrantneStatus = workFlowManagementEntity.getRunNEStatusTestEntity();
			
			
			String premigerrorfile = workFlowManagementEntity.getPreErrorFile();
			String negrowerrorfile = workFlowManagementEntity.getNegrowErrorFile();
			String migerrorfile = workFlowManagementEntity.getMigErrorFile();
			String postmigerrorfile = workFlowManagementEntity.getPostErrorFile();
			String preAuditErrorFile = workFlowManagementEntity.getPREAUDITErrorFile();
			String neStatusErrorFile = workFlowManagementEntity.getNEStatusErrorFile();

			boolean status = workFlowManagementService.deleteWfmrunTest(workFlowId);

			if (status) {
				if (runTestNEGrowEntity != null && runTestNEGrowEntity.getId() > 0) {
					deleteRuntestDetails(runTestNEGrowEntity, programId);
				}
				if (runTestMigEntity != null && runTestMigEntity.getId() > 0) {
					deleteRuntestDetails(runTestMigEntity, programId);
				}

				if (runTestPostMigEntity != null && runTestPostMigEntity.getId() > 0) {
					deleteRuntestDetails(runTestPostMigEntity, programId);
				}
				if (runtestrantAtpEntity != null && runtestrantAtpEntity.getId() > 0) {
					deleteRuntestDetails(runtestrantAtpEntity, programId);
				}
				if (runtestrantpreaAudit != null && runtestrantpreaAudit.getId() > 0) {
					deleteRuntestDetails(runtestrantpreaAudit, programId);
				}
				if (runtestrantneStatus != null && runtestrantneStatus.getId() > 0) {
					deleteRuntestDetails(runtestrantneStatus, programId);
				}
				
				FileUtil.deleteFileOrFolder(premigerrorfile);
				FileUtil.deleteFileOrFolder(negrowerrorfile);
				FileUtil.deleteFileOrFolder(migerrorfile);
				FileUtil.deleteFileOrFolder(postmigerrorfile);
				FileUtil.deleteFileOrFolder(preAuditErrorFile);
				FileUtil.deleteFileOrFolder(neStatusErrorFile);
			}

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", "Deleted Successfully");
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in deleteWfmRunTestData() in WorkFlowManagementController"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	public boolean deleteRuntestDetails(RunTestEntity runTestEntity, Integer programId) {
		boolean status = false;

		try {

			String threadname = runTestEntity.getruntestThreadName();
			if (threadname != null) {
				Set<Thread> setThreads = Thread.getAllStackTraces().keySet();
				setThreads.stream().filter(t -> t.getName().equals(threadname)).findAny().ifPresent(Thread::interrupt);
			}
			
			Set<RunTestResultEntity> runTestResultEntityList = runTestEntity.getRunTestResultEntity();
			String[] useCaseList = runTestEntity.getUseCase().split(",");
			audit4GSummaryService.deleteAuditSummaryReport(runTestEntity.getId());
			audit4GFsuSummaryService.deleteAuditSummaryReport(runTestEntity.getId());
			audit5GCBandSummaryService.deleteAuditSummaryReport(runTestEntity.getId());
			audit5GDSSSummaryService.deleteAuditSummaryReport(runTestEntity.getId());
			
			audit4GSummaryService.deleteAuditPassFailReport(runTestEntity.getId());
			audit5GCBandSummaryService.deleteAuditPassFailReport(runTestEntity.getId());
			audit5GDSSSummaryService.deleteAuditPassFailReport(runTestEntity.getId());
			audit4GFsuSummaryService.deleteAuditPassFailReport(runTestEntity.getId());
			
			auditCriticalParamsService.deleteAuditCriticalSummaryEntityByRunTestId(runTestEntity.getId());
			
			if(!runTestEntity.getMigrationSubType().equalsIgnoreCase("PREAUDIT")) {
			audit4GIssueService.deleteaudit4GIssueEntityByRunTestId(runTestEntity.getId());
			audit5GCBandIssueService.deleteaudit5GCBandIssueEntityByRunTestId(runTestEntity.getId());
			audit4GFsuIssueService.deleteaudit4GFsuIssueEntityByRunTestId(runTestEntity.getId());
			audit5GDSSIssueService.deleteaudit5GDSSIssueEntityByRunTestId(runTestEntity.getId());
			}
			
			status = runTestService.deleteRunTest(runTestEntity.getId());

			String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			if (status == true) {

				for (String useCase : useCaseList) {
					UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepositoryImpl.getUseCaseByName(useCase,
							runTestEntity.getMigrationType(), programId, runTestEntity.getMigrationSubType());
					if (CommonUtil.isValidObject(useCaseBuilderEntity)) {
						runTestRepository.updateUseCountForUseCase(useCaseBuilderEntity.getId(),
								Constants.USECOUNT_DECREMENT, 1);
					}
				}
				String currentScriptOutput = "";

				logger.info("RunTestController.deleteRunTestData() runTestResultEntityList size"
						+ runTestResultEntityList.size());

				for (RunTestResultEntity runTestResult : runTestResultEntityList) {
					currentScriptOutput = runTestResult.getCurrentScriptOutput();
					if (StringUtils.isNotEmpty(currentScriptOutput)) {
						String filePathToRemove = filePath + currentScriptOutput;
						File file = new File(filePathToRemove);
						if (file.isFile()) {
							file.delete();
						} else if (file.isDirectory()) {
							FileUtils.deleteDirectory(file);
						}
						logger.info(
								"WorkFlowManagementController.deleteRunTestData() deleting file/folder currentScriptOutput path: "
										+ filePathToRemove);
					}

				}

				if (StringUtils.isNotEmpty(runTestEntity.getGenerateScriptPath())) {
					String filePathToRemove = filePath + StringUtils
							.substringBeforeLast(runTestEntity.getGenerateScriptPath(), Constants.SEPARATOR);
					File file = new File(filePathToRemove);
					if (file.isFile()) {
						file.delete();
					} else if (file.isDirectory()) {
						FileUtils.deleteDirectory(file);
					}
					logger.info(
							"WorkFlowManagementController.deleteRunTestData() deleting file/folder GenerateScriptPath: "
									+ filePathToRemove);

				}

				if (StringUtils.isNotEmpty(runTestEntity.getOutputFilepath())) {
					String filePathToRemove = filePath + runTestEntity.getOutputFilepath();
					File file = new File(filePathToRemove);
					if (file.isFile()) {
						file.delete();
					} else if (file.isDirectory()) {
						FileUtils.deleteDirectory(file);
					}
					logger.info("WorkFlowManagementController.deleteRunTestData() deleting file/folder OutputFilepath: "
							+ filePathToRemove);
				}

				if (StringUtils.isNotEmpty(runTestEntity.getResultFilePath())
						&& StringUtils.isNotEmpty(runTestEntity.getResult())) {
					String filePathToRemove = filePath + runTestEntity.getResultFilePath() + Constants.SEPARATOR
							+ runTestEntity.getResult();
					File file = new File(filePathToRemove);
					if (file.isFile()) {
						file.delete();
					} else if (file.isDirectory()) {
						FileUtils.deleteDirectory(file);
					}
					logger.info("WorkFlowManagementController.deleteRunTestData() deleting file/folder ResultFilePath: "
							+ filePathToRemove);
				}
			}
		} catch (Exception e) {
			status = false;
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/downloadLoadFiles")
	public void downloadFiles(@RequestBody JSONObject downloadData,HttpServletResponse response) throws IOException {
		String fileAbsolutepath = null;
		File downloadFile = null;
		String programId = null;
		String prefileName = null;
		String prefilePath = null;
		String migfilepath = null;
		String postfilepath = null;
		String preAuditfilepath = null;
		String neStausfilepath = null;
		String nefilePath = null;
		String ranAtpFilePath = null;
		String commPath = null;
		String envPath = null;
		String csvPath = null;
		String commZipName = null;
		String envZipName = null;
		String csvZipName = null;
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		HashMap<String, ArrayList<String>> paths4G = new HashMap<>();
		ArrayList<String> arr1 = new ArrayList<>();
		ArrayList<String> arr2 = new ArrayList<>();
		ArrayList<String> arr3 = new ArrayList<>();
		ArrayList<String> arr4 = new ArrayList<>();
		try {
			StringBuilder zipFileFolderpath = new StringBuilder();
		//	Map pre = (Map) downloadData.get("premigration");
			if(downloadData.get("migration") != null) {
				Map mig = (Map) downloadData.get("migration");
				if(!mig.isEmpty() && mig.get("filePath")!=null) {
					migfilepath = mig.get("filePath").toString();
				}
			}
			if(downloadData.get("postmigration") != null) {
				Map post = (Map) downloadData.get("postmigration");
				if(!post.isEmpty() && post.get("filePath")!=null) {
					postfilepath = post.get("filePath").toString();
				}
			}
			if(downloadData.get("preaudit") != null) {
				Map prepost = (Map) downloadData.get("preaudit");
				if(!prepost.isEmpty() && prepost.get("filePath")!=null) {
					preAuditfilepath = prepost.get("filePath").toString();
				}
			}
			if(downloadData.get("nestatus") != null) {
				Map nestatus = (Map) downloadData.get("nestatus");
				if(!nestatus.isEmpty() && nestatus.get("filePath")!=null) {
					neStausfilepath = nestatus.get("filePath").toString();
				}
			}
			if(downloadData.get("negrow") != null) {
				Map negrow = (Map) downloadData.get("negrow");
				if(!negrow.isEmpty() && negrow.get("filePath")!=null) {
					nefilePath = negrow.get("filePath").toString();
				}
			}
			if(downloadData.get("ranatp") != null) {
				Map ranatp = (Map) downloadData.get("ranatp");
				if(!ranatp.isEmpty() && ranatp.get("filePath")!=null) {
					ranAtpFilePath = ranatp.get("filePath").toString();
				}
			}
			
			if (downloadData.get("filePathPre")!= null
					&& downloadData.get("fileNamePre")!= null) {
				prefilePath = downloadData.get("filePathPre").toString();
				prefileName = downloadData.get("fileNamePre").toString();
				arr1.add(prefilePath);
				arr1.add(prefileName);
				paths4G.put("pre1", arr1);
			}
			if (downloadData.get("commPath")!= null && downloadData.get("commZipName")!= null) {
				commPath = downloadData.get("commPath").toString();
				commZipName = downloadData.get("commZipName").toString();
				arr2.add(commPath);
				arr2.add(commZipName);
				paths4G.put("pre2", arr2);

			}

			if (downloadData.get("envPath")!= null && downloadData.get("envZipName")!= null) {
				envPath = downloadData.get("envPath").toString();
				envZipName = downloadData.get("envZipName").toString();
				arr3.add(envPath);
				arr3.add(envZipName);
				paths4G.put("pre3", arr3);

			}
			if (downloadData.get("csvPath")!= null && downloadData.get("csvZipName")!= null) {
				csvPath = downloadData.get("csvPath").toString();
				csvZipName = downloadData.get("csvZipName").toString();
				arr4.add(csvPath);
				arr4.add(csvZipName);
				paths4G.put("pre4", arr4);
			}

			int id = (int) downloadData.get("programId");
			programId = String.valueOf(id).toString();
			zipFileFolderpath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append("wfmdownloadscripts");

			File zipFileDir = new File(zipFileFolderpath.toString());
			if (!zipFileDir.exists()) {
				FileUtils.forceMkdir(zipFileDir); 
			}
			try {
				Iterator<Map.Entry<String, ArrayList<String>>> itr = paths4G.entrySet().iterator();

				while (itr.hasNext()) {
					Map.Entry<String, ArrayList<String>> entry = itr.next();
					ArrayList<String> filedata = new ArrayList<>();
					filedata = entry.getValue();
					String destFilePath = zipFileFolderpath.toString() + Constants.SEPARATOR + "PreMigration_Scripts";
					File destinationFilePath = new File(destFilePath);
					destinationFilePath.mkdirs();
					destinationFilePath.toString();
					String sourceFilePath = filedata.get(0) + filedata.get(1);

					makeUnzip(sourceFilePath, destFilePath);
				}
				String folderName="";
				// code for migration file copy
				if(migfilepath!=null)
				{
					folderName="Migration_Scripts";
					getFiles(migfilepath, programId, zipFileFolderpath.toString(),folderName);
				}
				// code for Postmigration file copy
				if(postfilepath!=null)
				{	
					folderName="PostMigration_Audit_Scripts";
					getFiles(postfilepath, programId, zipFileFolderpath.toString(),folderName);
				}
				if(preAuditfilepath!=null)
				{	
					folderName="Pre_Audit_Scripts";
					getFiles(preAuditfilepath, programId, zipFileFolderpath.toString(),folderName);
				}
				if(neStausfilepath!=null)
				{	
					folderName="NE_Status_Scripts";
					getFiles(neStausfilepath, programId, zipFileFolderpath.toString(),folderName);
				}
				// code for neGrow file copy
				if(nefilePath!=null)
				{
					folderName="NeGrow_Script";
					getFiles(nefilePath, programId, zipFileFolderpath.toString(),folderName);
				}
				//code for ranAtp file copy
				if(ranAtpFilePath!=null)
				{
					folderName="PostMigration_Ran_Atp_Scripts";
					getFiles(ranAtpFilePath, programId, zipFileFolderpath.toString(),folderName);
				}

				String zipFilepath = zipFileFolderpath.toString() + ".zip";
				boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath, zipFileFolderpath.toString());
				if (!status) {
					logger.info("downloadLogFileWFM() file not found:" + zipFilepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				downloadFile = new File(zipFilepath);
				fileAbsolutepath = zipFilepath;
				
				if (!downloadFile.exists()) {
					logger.info("downloadLogFileWFM() file not found:" + fileAbsolutepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				
				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				if (CommonUtil.isValidObject(zipFileFolderpath) && CommonUtil.isValidObject(zipFilepath)) {
					FileUtil.deleteFileOrFolder(zipFileFolderpath.toString());
					FileUtil.deleteFileOrFolder(zipFilepath);
				}		
			} catch (FileNotFoundException fne) {
				logger.error(
						"Exception in GenerateDownloadScripts(): " + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error(
						"Exception in GenerateDownloadScripts(): " + ExceptionUtils.getFullStackTrace(e));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"Error While Downloading\"}");
				out.close();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					logger.error(
							"Exception in RunTestController.downloadScripts(): " + ExceptionUtils.getFullStackTrace(e));
				}
			}

			
		} catch (Exception e) {
			logger.error("DownloadGeneratedScripts() file not found: :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}

	}

	private void makeUnzip(String sourcezip, String destinationFilePath) throws IOException {

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(sourcezip));
		ZipEntry zipEntry = zis.getNextEntry();
		File destFile = new File(destinationFilePath);
		while (zipEntry != null) {
		     File newFile = newFile(destFile, zipEntry);
		     if (zipEntry.isDirectory()) {
		         if (!newFile.isDirectory() && !newFile.mkdirs()) {
		             throw new IOException("Failed to create directory " + newFile);
		         }
		     } else {
		         File parent = newFile.getParentFile();
		         if (!parent.isDirectory() && !parent.mkdirs()) {
		             throw new IOException("Failed to create directory " + parent);
		         }
		         
		         FileOutputStream fos = new FileOutputStream(newFile);
		         int len;
		         while ((len = zis.read(buffer)) > 0) {
		             fos.write(buffer, 0, len);
		         }
		         fos.close();
		     }
		 zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	
	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}

	public void getFiles(String migfilepath, String programId, String zipFileFolderpath, String folderName) throws IOException {

		// code for migration file copy
		if (migfilepath != null && !migfilepath.isEmpty()) {
			String filenames[] = migfilepath.split(",");
			String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			for (String name : filenames) {
				File zipFileDir = new File(zipFileFolderpath + Constants.SEPARATOR + folderName);
				if (!zipFileDir.exists()) {
					FileUtils.forceMkdir(zipFileDir);
				}
				if (CommonUtil.isValidObject(name) && name.trim().length() > 0) {
					File file = new File(filePath + name);
					FileUtils.copyFileToDirectory(file, zipFileDir);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/stopWFM")
	public JSONObject stopWFM(@RequestBody JSONObject stopWFMparams) {
		JSONObject result = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId;
		String negrowStatus = null;
		String migStatus = null;
		String postMigStatus = null;
		String preAuditStatus = null;
		String neStatusStatus = null;
		try {
			sessionId = stopWFMparams.get("sessionId").toString();
			serviceToken = stopWFMparams.get("serviceToken").toString();
			programId = (Integer) stopWFMparams.get("programId");
			negrowStatus = stopWFMparams.get("neGrowStatus").toString();
			migStatus = stopWFMparams.get("migStatus").toString();
			postMigStatus = stopWFMparams.get("postMigStatus").toString();
			preAuditStatus = stopWFMparams.get("preAuditStatus").toString();
			neStatusStatus = stopWFMparams.get("neStatus").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			result.put("sessionId", sessionId);
			result.put("serviceToken", serviceToken);
			Integer wfid = (Integer)stopWFMparams.get("wfmid");
			WorkFlowManagementEntity workFlowManagementEntityThread = null;
			RunTestEntity runtestNegrowent = null;
			RunTestEntity runtestMigent = null;
			RunTestEntity runtestPostmigent = null;
			RunTestEntity runtestRanAtpent = null;
			RunTestEntity runtestPreAUdit = null;
			RunTestEntity runtestNeStatus = null;
			workFlowManagementEntityThread = workFlowManagementService.getWorkFlowManagementEntity(wfid);
			String threadname = workFlowManagementEntityThread.getWfmThreadName();
			if(threadname != null) {
				Set<Thread> setThreads = Thread.getAllStackTraces().keySet();
				setThreads.stream().filter(t -> t.getName().equals(threadname)).findAny().ifPresent(Thread :: interrupt);
			}
			Thread.sleep(1000);
			workFlowManagementEntityThread = workFlowManagementService.getWorkFlowManagementEntity(wfid);
			
			if("InProgress".equals(negrowStatus)) {
				runtestNegrowent = workFlowManagementEntityThread.getRunNEGrowEntity();
				workFlowManagementEntityThread.setRunNEGrowEntity(null);
				if(!workFlowManagementEntityThread.getNeUpStatus().equals(neStatusStatus)) {
					runtestNeStatus = workFlowManagementEntityThread.getRunNEStatusTestEntity();
					workFlowManagementEntityThread.setRunNEStatusTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getPreAuditStatus().equals(preAuditStatus)) {
					runtestPreAUdit = workFlowManagementEntityThread.getRunPreAuditTestEntity();
					workFlowManagementEntityThread.setRunPreAuditTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getMigStatus().equals(migStatus)) {
					runtestMigent = workFlowManagementEntityThread.getRunMigTestEntity();
					workFlowManagementEntityThread.setRunMigTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getPostMigStatus().equals(postMigStatus)) {
					runtestPostmigent = workFlowManagementEntityThread.getRunPostMigTestEntity();
					workFlowManagementEntityThread.setRunPostMigTestEntity(null);
				}
				workFlowManagementEntityThread.setNeGrowStatus("StoppedByUser");
				workFlowManagementEntityThread.setNeUpStatus(neStatusStatus);
				workFlowManagementEntityThread.setPreAuditStatus(preAuditStatus);
				workFlowManagementEntityThread.setMigStatus(migStatus);
				workFlowManagementEntityThread.setPostMigStatus(postMigStatus);
				workFlowManagementEntityThread.setStatus("Completed");
			}else if("InProgress".equals(neStatusStatus)) {
				runtestNeStatus = workFlowManagementEntityThread.getRunNEStatusTestEntity();
				workFlowManagementEntityThread.setRunNEStatusTestEntity(null);
				
				if(!workFlowManagementEntityThread.getPreAuditStatus().equals(preAuditStatus)) {
					runtestPreAUdit = workFlowManagementEntityThread.getRunPreAuditTestEntity();
					workFlowManagementEntityThread.setRunPreAuditTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getMigStatus().equals(migStatus)) {
					runtestMigent = workFlowManagementEntityThread.getRunMigTestEntity();
					workFlowManagementEntityThread.setRunMigTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getPostMigStatus().equals(postMigStatus)) {
					runtestPostmigent = workFlowManagementEntityThread.getRunPostMigTestEntity();
					workFlowManagementEntityThread.setRunPostMigTestEntity(null);
				}
				workFlowManagementEntityThread.setNeUpStatus("StoppedByUser");
				workFlowManagementEntityThread.setPreAuditStatus(preAuditStatus);
				workFlowManagementEntityThread.setMigStatus(migStatus);
				workFlowManagementEntityThread.setPostMigStatus(postMigStatus);
				workFlowManagementEntityThread.setStatus("Completed");
			} else if("InProgress".equals(preAuditStatus)) {
				runtestPreAUdit = workFlowManagementEntityThread.getRunPreAuditTestEntity();
				workFlowManagementEntityThread.setRunPreAuditTestEntity(null);
				
				if(!workFlowManagementEntityThread.getMigStatus().equals(migStatus)) {
					runtestMigent = workFlowManagementEntityThread.getRunMigTestEntity();
					workFlowManagementEntityThread.setRunMigTestEntity(null);
				}
				if(!workFlowManagementEntityThread.getPostMigStatus().equals(postMigStatus)) {
					runtestPostmigent = workFlowManagementEntityThread.getRunPostMigTestEntity();
					workFlowManagementEntityThread.setRunPostMigTestEntity(null);
				}
				
				workFlowManagementEntityThread.setPreAuditStatus("StoppedByUser");
				workFlowManagementEntityThread.setMigStatus(migStatus);
				workFlowManagementEntityThread.setPostMigStatus(postMigStatus);
				workFlowManagementEntityThread.setStatus("Completed");
			}else if("InProgress".equals(migStatus)) {
				runtestMigent = workFlowManagementEntityThread.getRunMigTestEntity();
				workFlowManagementEntityThread.setRunMigTestEntity(null);
				if(!workFlowManagementEntityThread.getPostMigStatus().equals(postMigStatus)) {
					runtestPostmigent = workFlowManagementEntityThread.getRunPostMigTestEntity();
					workFlowManagementEntityThread.setRunPostMigTestEntity(null);
				}
				workFlowManagementEntityThread.setMigStatus("StoppedByUser");
				workFlowManagementEntityThread.setPostMigStatus(postMigStatus);
				workFlowManagementEntityThread.setStatus("Completed");
			} else if("InProgress".equals(postMigStatus)) {
				workFlowManagementEntityThread.setPostMigStatus("StoppedByUser");
				runtestPostmigent = workFlowManagementEntityThread.getRunPostMigTestEntity();
				runtestRanAtpent = workFlowManagementEntityThread.getRunRanAtpTestEntity();
				workFlowManagementEntityThread.setRunRanAtpTestEntity(null);
				workFlowManagementEntityThread.setRunPostMigTestEntity(null);
				workFlowManagementEntityThread.setStatus("Completed");
			}
			workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntityThread);
			if (runtestNegrowent != null && runtestNegrowent.getId() > 0) {
				//System.out.println("-------------NeGrow");
				deleteRuntestDetails(runtestNegrowent, programId);
			}
			if (runtestNeStatus != null && runtestNeStatus.getId() > 0) {
				deleteRuntestDetails(runtestNeStatus, programId);
			}
			if (runtestPreAUdit != null && runtestPreAUdit.getId() > 0) {
				deleteRuntestDetails(runtestPreAUdit, programId);
			}
			if (runtestMigent != null && runtestMigent.getId() > 0) {
				deleteRuntestDetails(runtestMigent, programId);
			}

			if (runtestPostmigent != null && runtestPostmigent.getId() > 0) {
				deleteRuntestDetails(runtestPostmigent, programId);
			}
			if (runtestRanAtpent != null && runtestRanAtpent.getId() > 0) {
				deleteRuntestDetails(runtestRanAtpent, programId);
			}
			result.put("status", Constants.SUCCESS);
			result.put("reason", "WorkFLow Stopped Sucessfully");
		}
		catch(Exception e) {
			logger.error("Exception in stopWFM(): " + ExceptionUtils.getFullStackTrace(e));
			result.put("status", Constants.FAIL);
			result.put("reason", "Error occured while stopping Workflow");
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/continueWFM")
	public JSONObject continueWFM(@RequestBody JSONObject continueWFMparams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId;
		Integer wfid;
		String ciqFileName = null;
		String testname = null;
		List<Map<String, String>> enbList = null;
		try {
			sessionId = continueWFMparams.get("sessionId").toString();
			serviceToken = continueWFMparams.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			programId = (Integer) continueWFMparams.get("programId");
			wfid = (Integer)continueWFMparams.get("id");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			enbList = (List<Map<String, String>>) continueWFMparams.get("neDetails");
			testname = "WFM_" + enbList.get(0).get("neName") + "_" + timeStampDate;
			JSONObject negrowJson = null;
			JSONObject migrationJson = null;
			JSONObject postMigrationJson = null;
			JSONObject preAuditJson = null;
			JSONObject neupJson = null;
			String neGrowStatus = null;
			String migStatus = null;
			String postMigStatus = null;
			String preAuditStatus = null;
			String neupStatus = null;
			WorkFlowManagementEntity workFlowManagementEntity = null;
			RunTestEntity runtestNegrowent = null;
			RunTestEntity runtestMigent = null;
			RunTestEntity runtestPostmigent = null;
			RunTestEntity runtestpreAudit = null;
			RunTestEntity runtestNeUp = null;
			workFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(wfid);
			OvScheduledEntity ovScheduledEntity =ovScheduledTaskService.getOvDetails(wfid);

			neGrowStatus = workFlowManagementEntity.getNeGrowStatus();
			migStatus = workFlowManagementEntity.getMigStatus();
			postMigStatus = workFlowManagementEntity.getPostMigStatus();
			preAuditStatus= workFlowManagementEntity.getPreAuditStatus();
			neupStatus= workFlowManagementEntity.getNeUpStatus();
			runtestNegrowent = workFlowManagementEntity.getRunNEGrowEntity();
			runtestMigent = workFlowManagementEntity.getRunMigTestEntity();
			runtestPostmigent = workFlowManagementEntity.getRunPostMigTestEntity();
			runtestpreAudit=workFlowManagementEntity.getRunPreAuditTestEntity();
			runtestNeUp=workFlowManagementEntity.getRunNEStatusTestEntity();
			if (continueWFMparams.containsKey("negrow") && continueWFMparams.get("negrow") != null) {
				negrowJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("negrow")));
				
				if(ovScheduledEntity!=null)
				{
					negrowJson.put("trackerId", ovScheduledEntity.getTrackerId());
					negrowJson.put("type", "OV");

				}
				
				JSONObject runTestFormDetails = (JSONObject) negrowJson.get("runTestFormDetails");
				List<JSONObject> useCase = (List<JSONObject>) runTestFormDetails.get("useCase");
				List<JSONObject> scripts = (List<JSONObject>) runTestFormDetails.get("scripts");

				/*if (useCase.size() == 3) {
					for (JSONObject uc : useCase) {
						if (uc.get("useCaseName").toString().contains("pnp")) {
							String usecase = (String) uc.get("useCaseName");
							List<JSONObject> useCaseList = new ArrayList<>();
							useCaseList.add(uc);
							useCase = useCaseList;
							runTestFormDetails.replace("useCase", useCase);
							negrowJson.replace("runTestFormDetails", runTestFormDetails);
						}
					}
					for (JSONObject sc : scripts) {
						if (sc.get("useCaseName").toString().contains("pnp")) {
							String script = (String) sc.get("useCaseName");
							List<JSONObject> scriptsList = new ArrayList<>();
							scriptsList.add(sc);
							scripts = scriptsList;
							runTestFormDetails.replace("scripts", scripts);
							negrowJson.replace("runTestFormDetails", runTestFormDetails);
						}
					}
				}*/
			}
			if (continueWFMparams.containsKey("migration") && continueWFMparams.get("migration") != null) {
				migrationJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("migration")));
				if(ovScheduledEntity!=null)
				{
					migrationJson.put("trackerId", ovScheduledEntity.getTrackerId());
					migrationJson.put("type", "OV");

				}
			}
			if (continueWFMparams.containsKey("postmigration") && continueWFMparams.get("postmigration") != null) {
				postMigrationJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("postmigration")));
				if(ovScheduledEntity!=null)
				{
					postMigrationJson.put("trackerId", ovScheduledEntity.getTrackerId());
					postMigrationJson.put("type", "OV");

				}
			}
			if (continueWFMparams.containsKey("Nestatus") && continueWFMparams.get("Nestatus") != null) {
				neupJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("Nestatus")));
				if(ovScheduledEntity!=null)
				{
					neupJson.put("trackerId", ovScheduledEntity.getTrackerId());
					neupJson.put("type", "OV");

				}
			}
			if (continueWFMparams.containsKey("preaudit") && continueWFMparams.get("preaudit") != null) {
				preAuditJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("preaudit")));
				if(ovScheduledEntity!=null)
				{
					preAuditJson.put("trackerId", ovScheduledEntity.getTrackerId());
					preAuditJson.put("type", "OV");

				}
			}else if (continueWFMparams.containsKey("preAudit") && continueWFMparams.get("preAudit") != null) {
				preAuditJson = (JSONObject) new JSONParser()
						.parse(continueWFMparams.toJSONString((Map) continueWFMparams.get("preAudit")));
				if(ovScheduledEntity!=null)
				{
					preAuditJson.put("trackerId", ovScheduledEntity.getTrackerId());
					preAuditJson.put("type", "OV");

				}
			}
			NeMappingModel neMappingModel = new NeMappingModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(programId);
			neMappingModel.setProgramDetailsEntity(programDetailsEntity);
			neMappingModel.setEnbId(enbList.get(0).get("neId").toString());
			List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
			NeMappingEntity neMappingEntity = neMappingEntities.get(0);
			String neVersion = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
					.toString();
			String neName = neMappingEntity.getNetworkConfigEntity().getNeName().toString();
			String lsmId = String.valueOf(neMappingEntity.getNetworkConfigEntity().getId());
			if (negrowJson != null) {
				Map inputJsonNEGrowTemp = (JSONObject) new JSONParser()
						.parse(negrowJson.toJSONString((Map) negrowJson.get("runTestFormDetails")));
				inputJsonNEGrowTemp.put("lsmVersion", neVersion);
				inputJsonNEGrowTemp.put("lsmName", neName);
				inputJsonNEGrowTemp.put("lsmId", lsmId);
				inputJsonNEGrowTemp.put("testname", testname);
				inputJsonNEGrowTemp.put("testDesc", "Ran from WFM");
				negrowJson.put("runTestFormDetails", inputJsonNEGrowTemp);
				workFlowManagementEntity.setNeGrowStatus("NotYetStarted");
				workFlowManagementEntity.setRunNEGrowEntity(null);
				String errorFilePath = workFlowManagementEntity.getNegrowErrorFile();
				FileUtil.deleteFileOrFolder(errorFilePath);
			} else if("StoppedByUser".equals(neGrowStatus) || "NotYetStarted".equals(neGrowStatus)) {
				workFlowManagementEntity.setNeGrowStatus("InputsRequired");
			}
			if (migrationJson != null) {
				Map inputJsonmigGrowTemp = (JSONObject) new JSONParser()
						.parse(migrationJson.toJSONString((Map) migrationJson.get("runTestFormDetails")));
				inputJsonmigGrowTemp.put("lsmVersion", neVersion);
				inputJsonmigGrowTemp.put("lsmName", neName);
				inputJsonmigGrowTemp.put("lsmId", lsmId);
				inputJsonmigGrowTemp.put("testname", testname);
				inputJsonmigGrowTemp.put("testDesc", "Ran from WFM");
				migrationJson.put("runTestFormDetails", inputJsonmigGrowTemp);
				workFlowManagementEntity.setMigStatus("NotYetStarted");
				workFlowManagementEntity.setRunMigTestEntity(null);
				String errorFilePath = workFlowManagementEntity.getMigErrorFile();
				FileUtil.deleteFileOrFolder(errorFilePath);
			} else if("StoppedByUser".equals(migStatus) || "NotYetStarted".equals(migStatus)) {
				workFlowManagementEntity.setMigStatus("InputsRequired");
			}
			if (postMigrationJson != null) {
				Map inputJsonpostmigGrowTemp = (JSONObject) new JSONParser().parse(
						postMigrationJson.toJSONString((Map) postMigrationJson.get("runTestFormDetails")));
				inputJsonpostmigGrowTemp.put("lsmVersion", neVersion);
				inputJsonpostmigGrowTemp.put("lsmName", neName);
				inputJsonpostmigGrowTemp.put("lsmId", lsmId);
				inputJsonpostmigGrowTemp.put("testname", testname);
				inputJsonpostmigGrowTemp.put("testDesc", "Ran from WFM");
				postMigrationJson.put("runTestFormDetails", inputJsonpostmigGrowTemp);
				workFlowManagementEntity.setPostMigStatus("NotYetStarted");
				workFlowManagementEntity.setRunPostMigTestEntity(null);
				String errorFilePath = workFlowManagementEntity.getPostErrorFile();
				FileUtil.deleteFileOrFolder(errorFilePath);
			} else if("StoppedByUser".equals(postMigStatus) || "NotYetStarted".equals(postMigStatus)) {
				workFlowManagementEntity.setPostMigStatus("InputsRequired");
			}
			if (preAuditJson != null) {
				Map inputJsonpreauditGrowTemp = (JSONObject) new JSONParser().parse(
						preAuditJson.toJSONString((Map) preAuditJson.get("runTestFormDetails")));
				inputJsonpreauditGrowTemp.put("lsmVersion", neVersion);
				inputJsonpreauditGrowTemp.put("lsmName", neName);
				inputJsonpreauditGrowTemp.put("lsmId", lsmId);
				inputJsonpreauditGrowTemp.put("testname", testname);
				inputJsonpreauditGrowTemp.put("testDesc", "Ran from WFM");
				preAuditJson.put("runTestFormDetails", inputJsonpreauditGrowTemp);
				workFlowManagementEntity.setPreAuditStatus("NotYetStarted"); 
				workFlowManagementEntity.setRunPreAuditTestEntity(null);
				String errorFilePath = workFlowManagementEntity.getPREAUDITErrorFile();
				FileUtil.deleteFileOrFolder(errorFilePath);
			} else if("StoppedByUser".equals(preAuditStatus) || "NotYetStarted".equals(preAuditStatus)) {
				workFlowManagementEntity.setPreAuditStatus("InputsRequired");
			}
			if (neupJson != null) {
				Map inputJsonneupjsonGrowTemp = (JSONObject) new JSONParser().parse(
						preAuditJson.toJSONString((Map) neupJson.get("runTestFormDetails")));
				inputJsonneupjsonGrowTemp.put("lsmVersion", neVersion);
				inputJsonneupjsonGrowTemp.put("lsmName", neName);
				inputJsonneupjsonGrowTemp.put("lsmId", lsmId);
				inputJsonneupjsonGrowTemp.put("testname", testname);
				inputJsonneupjsonGrowTemp.put("testDesc", "Ran from WFM");
				neupJson.put("runTestFormDetails", inputJsonneupjsonGrowTemp);
				workFlowManagementEntity.setNeUpStatus("NotYetStarted");
				workFlowManagementEntity.setRunNEStatusTestEntity(null);
				String errorFilePath = workFlowManagementEntity.getNEStatusErrorFile();
				FileUtil.deleteFileOrFolder(errorFilePath);
			} else if("StoppedByUser".equals(neupStatus) || "NotYetStarted".equals(neupStatus)) {
				workFlowManagementEntity.setNeUpStatus("InputsRequired");
			}
			workFlowManagementEntity.setStatus("InProgress");
			workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
			if (negrowJson!=null && runtestNegrowent != null && runtestNegrowent.getId() > 0) {
				deleteRuntestDetails(runtestNegrowent, programId);
			}
			if (neupJson!=null && runtestNeUp != null && runtestNeUp.getId() > 0) {
				deleteRuntestDetails(runtestNeUp, programId);
			}
			if (preAuditJson!=null && runtestpreAudit != null && runtestpreAudit.getId() > 0) {
				deleteRuntestDetails(runtestpreAudit, programId);
			}
			if (migrationJson!=null && runtestMigent != null && runtestMigent.getId() > 0) {
				deleteRuntestDetails(runtestMigent, programId);
			}
			if (postMigrationJson!=null && runtestPostmigent != null && runtestPostmigent.getId() > 0) {
				deleteRuntestDetails(runtestPostmigent, programId);
			}
			
			String status = workFlowManagementService.continueWFM(wfid, negrowJson, migrationJson, postMigrationJson, enbList,preAuditJson,neupJson);
			if(Constants.SUCCESS.equals(status)) {
				resultMap.put("status",Constants.SUCCESS);
				resultMap.put("reason", "WorkFlow continued Successfully");
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "WorkFlow not continued");
				workFlowManagementEntity.setStatus("Completed");
				workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
			}
		} catch(Exception e) {
			System.out.println(e);
			resultMap.put("status",Constants.FAIL);
			resultMap.put("reason", e.getMessage());
		}
		
		return resultMap;
	}
	
	// @SuppressWarnings({ "static-access" })
	@PostMapping(value = "/downloadReportsWFM")
	public void downloadReportsWFM(@RequestBody JSONObject fileDetails, HttpServletResponse response)
			throws IOException {
		String filePath = null;
		int wfid;
		try {

			wfid = Integer.parseInt(fileDetails.get("wfid").toString());
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			WorkFlowManagementEntity workFlowManagementEntity;
			FileInputStream inputStream = null;
			OutputStream outStream = null;
			File downloadFile = null;
			String fileAbsolutepath = "";
			String zipFileFolderpath = "";
			String zipFilepath = "";
			RunTestEntity runtestNegrowent = null;
			RunTestEntity runtestMigent = null;
			RunTestEntity runtestPostmigent = null;
			RunTestEntity runtestRanAtpent = null;
			workFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(wfid);
			runtestNegrowent = workFlowManagementEntity.getRunNEGrowEntity();
			runtestMigent = workFlowManagementEntity.getRunMigTestEntity();
			runtestPostmigent = workFlowManagementEntity.getRunPostMigTestEntity();
			runtestRanAtpent = workFlowManagementEntity.getRunRanAtpTestEntity();

			try {
				zipFileFolderpath = filePath + "downloadReports";

				if (runtestPostmigent != null) {

					copyReportFiles(runtestPostmigent, zipFileFolderpath, filePath, "PostMigration");
				}

				if (runtestRanAtpent != null) {
					copyReportFiles(runtestRanAtpent, zipFileFolderpath, filePath, "PostMigration");
				}

				zipFilepath = zipFileFolderpath + ".zip";
				boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath, zipFileFolderpath);
				if (!status) {
					logger.info("downloadReportsWFM() file not found:" + zipFilepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				downloadFile = new File(zipFilepath);
				fileAbsolutepath = zipFilepath;

				if (!downloadFile.exists()) {
					logger.info("downloadReportsWFM() file not found:" + fileAbsolutepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}

				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				if (CommonUtil.isValidObject(zipFileFolderpath) && CommonUtil.isValidObject(zipFilepath)) {
					FileUtil.deleteFileOrFolder(zipFileFolderpath);
					FileUtil.deleteFileOrFolder(zipFilepath);
				}
			} catch (FileNotFoundException fne) {
				logger.error("Exception in downloadReportsWFM(): " + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error("Exception in downloadReportsWFM(): " + ExceptionUtils.getFullStackTrace(e));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"Error While Downloading\"}");
				out.close();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					logger.error("Exception in WorkFlowManagementController.downloadReportsWFM(): "
							+ ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in downloadReportsWFM() :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}
	}

	public void copyReportFiles(RunTestEntity runtestEntity, String zipFileFolderpath, String filePath,
			String foldername) throws Exception {
		int id = runtestEntity.getId();

		List<RunTestResultEntity> runTestResultEntity = runTestService.getRunTestResult(id);
		String srcFilePath = null;
		String fileName = null;
		String partFilePath = null;
		for (RunTestResultEntity runTestResult : runTestResultEntity) {
			fileName = runTestResult.getRunTestEntity().getResult();
			partFilePath = runTestResult.getRunTestEntity().getResultFilePath();
			srcFilePath = partFilePath + "/" + fileName;
		}
		File zipFileDir = new File(zipFileFolderpath + Constants.SEPARATOR + foldername);
		if (!zipFileDir.exists()) {
			FileUtils.forceMkdir(zipFileDir);
		}

		File srcfile = new File(filePath + srcFilePath);
		if (srcfile.exists()) {
			FileUtils.copyFileToDirectory(srcfile, zipFileDir);
		}
		// File[] fileList = srcfile.listFiles();
		// for (File sinfile : fileList) {
		// FileUtils.copyFileToDirectory(sinfile, zipFileDir);
		// }
	}
		
	@SuppressWarnings({ "static-access" })
	@PostMapping(value = "/downloadLogsWFM")
	public void downloadLogFileWFM(@RequestBody JSONObject fileDetails, HttpServletResponse response) throws IOException{
		String filePath = null;
		int wfid;
		try {
			wfid = Integer.parseInt(fileDetails.get("wfid").toString());
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			WorkFlowManagementEntity workFlowManagementEntity;
			FileInputStream inputStream = null;
			OutputStream outStream = null;
			File downloadFile = null;
			String fileAbsolutepath = "";
			String zipFileFolderpath = "";
			String zipFilepath = "";
			RunTestEntity runtestNegrowent = null;
			RunTestEntity runtestMigent = null;
			RunTestEntity runtestPostmigent = null;
			RunTestEntity runtestRanAtpent = null;
			RunTestEntity runtestPreAudeit = null;
			RunTestEntity runtestneStatus = null;
			workFlowManagementEntity = workFlowManagementService.getWorkFlowManagementEntity(wfid);
			runtestNegrowent = workFlowManagementEntity.getRunNEGrowEntity();
			runtestMigent = workFlowManagementEntity.getRunMigTestEntity();
			runtestPostmigent = workFlowManagementEntity.getRunPostMigTestEntity();
			runtestRanAtpent = workFlowManagementEntity.getRunRanAtpTestEntity();
			runtestPreAudeit = workFlowManagementEntity.getRunPreAuditTestEntity();
			runtestneStatus = workFlowManagementEntity.getRunNEStatusTestEntity();
			try {
				zipFileFolderpath = filePath + "download";
				if(runtestNegrowent != null) {
					copylogfiles(runtestNegrowent, zipFileFolderpath, filePath, "NEgrow");
				}
				if(runtestMigent != null) {
					copylogfiles(runtestMigent, zipFileFolderpath, filePath, "Migration");
				}
				if(runtestPostmigent != null) {
					copylogfiles(runtestPostmigent, zipFileFolderpath, filePath, "PostMigration_Audit");
				}
				if(runtestRanAtpent != null) {
					copylogfiles(runtestRanAtpent, zipFileFolderpath, filePath, "PostMigration_RAN_ATP");
				}
				if(runtestPreAudeit != null) {
					copylogfiles(runtestPreAudeit, zipFileFolderpath, filePath, "Pre_Audit");
				}
				if(runtestneStatus != null) {
					copylogfiles(runtestneStatus, zipFileFolderpath, filePath, "NE_Status");
				}
				
				zipFilepath = zipFileFolderpath + ".zip";
				boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath, zipFileFolderpath);
				if (!status) {
					logger.info("downloadLogFileWFM() file not found:" + zipFilepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				downloadFile = new File(zipFilepath);
				fileAbsolutepath = zipFilepath;
				
				if (!downloadFile.exists()) {
					logger.info("downloadLogFileWFM() file not found:" + fileAbsolutepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				
				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				if (CommonUtil.isValidObject(zipFileFolderpath) && CommonUtil.isValidObject(zipFilepath)) {
					FileUtil.deleteFileOrFolder(zipFileFolderpath);
					FileUtil.deleteFileOrFolder(zipFilepath);
				}				
			} catch (FileNotFoundException fne) {
				logger.error(
						"Exception in downloadLogFileWFM(): " + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error(
						"Exception in downloadLogFileWFM(): " + ExceptionUtils.getFullStackTrace(e));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"Error While Downloading\"}");
				out.close();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					logger.error(
							"Exception in RunTestController.downloadScripts(): " + ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in downloadLogFileWFM() :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}
	}
	
	public void copylogfiles(RunTestEntity runtestEntity, String  zipFileFolderpath, String filePath, String foldername) throws Exception {
		int id = runtestEntity.getId();
		
		List<RunTestResultEntity> runTestResultEntity = runTestService.getRunTestResult(id);
		String srcFilePath = null;
		for (RunTestResultEntity runTestResult : runTestResultEntity) {
			srcFilePath = runTestResult.getCurrentScriptOutput();
		}
		File zipFileDir = new File(zipFileFolderpath + Constants.SEPARATOR + foldername);
		if (!zipFileDir.exists()) {
			FileUtils.forceMkdir(zipFileDir);
		}
		File srcfile = new File(filePath + srcFilePath);
		File[] fileList = srcfile.listFiles();
		for (File sinfile : fileList) {
			FileUtils.copyFileToDirectory(sinfile, zipFileDir);
		}
	}
	
		public Boolean createNewFile(String checkPath) {
		File file1 = new File(checkPath); // initialize File object and passing path as argument
		Boolean result = null;
		try {
			result = file1.createNewFile(); // creates a new file
		} catch (IOException e) {
			e.printStackTrace(); // prints exception if any
		}

		return result;
	}
		
		/**
		 * This method will execute the Runtest do
		 * 
		 * @param runTestParams
		 * @return JSONObject
		 */
		@SuppressWarnings({ "unchecked" })
		@PostMapping(value = "/reRunContinueWfmTest")
		public JSONObject reRunContinueWfm(@RequestBody JSONObject runTestParams) {
			JSONObject resultMap = new JSONObject();
			JSONObject expiryDetails = null;
			String sessionId = null;
			String serviceToken = null;
			try {

				sessionId = runTestParams.get("sessionId").toString();
				serviceToken = runTestParams.get("serviceToken").toString();

				expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

				if (expiryDetails != null) {
					return expiryDetails;
				}
				String runType = runTestParams.get("runType").toString();
				int runTestId = (int) runTestParams.get("runTestId");
				int id = (int) runTestParams.get("id");
				String migrationSubType= runTestParams.get("migrationSubType").toString();
				WorkFlowManagementEntity workFlowEntity=workFlowManagementService.getWorkFlowManagementEntity(id);
				
				if(workFlowEntity!=null) {
					if(migrationSubType.equalsIgnoreCase("NEGrow")) {
						workFlowEntity.setNeGrowStatus("InProgress");
						workFlowEntity.setStatus("InProgress");
					}else {
					workFlowEntity.setMigStatus("InProgress");
					workFlowEntity.setStatus("InProgress");
					}
					workFlowEntity = workFlowManagementService
							.mergeWorkFlowMangement(workFlowEntity);
					LinkedHashMap<String, String> skipandContinueIdMap = workFlowManagementService
							.getSkipandContinueId(runTestId, runType);
					String skipScriptIds = skipandContinueIdMap.get("skipScriptIds");
					String reRunScriptID = skipandContinueIdMap.get("reRunScriptID");
					runTestParams.put("skipScriptIds", skipScriptIds);
					runTestParams.put("reRunScriptID", reRunScriptID);
					
					
					resultMap=runTestController.runReTest(runTestParams);
					
					if (resultMap != null && resultMap.containsKey("status")
							&& Constants.SUCCESS
									.equalsIgnoreCase(resultMap.get("status").toString())) {
						if(migrationSubType.equalsIgnoreCase("NEGrow")) {
							
							workFlowManagementService.getreRunNEStatus(workFlowEntity);
							
						}else {
							workFlowManagementService.getreRunMigStatus(workFlowEntity);
						}
						
						
					} else {

						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", resultMap.get("reason").toString());
						return resultMap;
					}
				}
				

			} catch (Exception e) {
				logger.error("Exception in WorkFlowManagementController.reRunContinueWfm(): "
						+ ExceptionUtils.getFullStackTrace(e));
			}
			return resultMap;
		}
		
		@SuppressWarnings({ "unchecked", "static-access" })
		@PostMapping(value = "/generateScriptWFM")
		public JSONObject generateScriptWFM(@RequestBody JSONObject generateParams) {
			JSONObject resultMap = new JSONObject();
			JSONObject expiryDetails = null;
			String sessionId = null;
			String serviceToken = null;
			try {
				sessionId = generateParams.get("sessionId").toString();
				serviceToken = generateParams.get("serviceToken").toString();

				expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

				if (expiryDetails != null) {
					return expiryDetails;
				}
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Generated Files Successfully");
				
			}catch (Exception e) {
				logger.error("Exception in WorkFlowManagementController.generateScriptWFM(): "
						+ ExceptionUtils.getFullStackTrace(e));
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Failed to Generate Script");
			}
			
			return resultMap;
		}
		
		@SuppressWarnings({ "unchecked", "static-access" })
		@PostMapping(value = "/bulkCiqWithWFMTest")
		public JSONObject bulkCiqWithWFMTest(@RequestBody JSONObject runTestParams) {
			JSONObject resultMap = new JSONObject();
			JSONObject expiryDetails = null;
			String sessionId = null;
			String serviceToken = null;
			String neIdData=null;
			String programId=null;
			try {
				sessionId = runTestParams.get("sessionId").toString();
				serviceToken = runTestParams.get("serviceToken").toString();
				neIdData = runTestParams.get("neIdData").toString();
				programId=runTestParams.get("programId").toString();
				expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

				/*if (expiryDetails != null) {
					return expiryDetails;
				}*/
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				if(StringUtils.isNotEmpty(neIdData))
				{
					HashSet<String> setNeIds = Stream.of(neIdData.split(","))
                            .collect(Collectors.toCollection(HashSet::new));
					Map<String, List<NeMappingEntity>> ciqDataMap=fileUploadService.getCiqWithNeMappinDetails(setNeIds,programId);
					StringBuilder objStringBuilder=new StringBuilder();
					AtomicBoolean neStatus=new AtomicBoolean();
					if(!ObjectUtils.isEmpty(ciqDataMap))
					{
						
						for(Map.Entry<String, List<NeMappingEntity>>  mapCiqData:ciqDataMap.entrySet())
						{
							String ciqName=mapCiqData.getKey();
							List<NeMappingEntity> neDetails=mapCiqData.getValue();
							
							//need to see
							JSONObject newRunTestParams=runTestParams;
							List<String> neIdsData=new ArrayList<>();
							JSONObject newJSONObject=getJsonObjectDetails(newRunTestParams, ciqName, neDetails,neIdsData);
							
							JSONObject statusJSONObject=	workFlowTest(newRunTestParams);
							
							if(!ObjectUtils.isEmpty(statusJSONObject.get("status")) && !Constants.SUCCESS.equalsIgnoreCase(statusJSONObject.get("status").toString()))
							{
								
								if(!ObjectUtils.isEmpty(statusJSONObject.get("reason")) )
								{
									resultMap.put("reason", statusJSONObject.get("reason"));
									neStatus.set(true);
								}
							}
							
						}
					}else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "Ciq Not Mapped with Ne Details");
						return resultMap;
					}
					
					if(neStatus.get())
					{
						resultMap.put("status", Constants.FAIL);
						return resultMap;
					}
					
				}
				
				
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				
			}catch (Exception e) {
				logger.error( ExceptionUtils.getFullStackTrace(e));
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			}
			
			return resultMap;
		}
		
	public JSONObject getJsonObjectDetails(JSONObject runTestParams, String ciqName, List<NeMappingEntity> neDetails,List<String> neIdsData) {
		try {
			Integer programId = null;
			String programName = null;
			String enbName="";
			//String NEName=null;
			List<Map<String, String>> listDetails=new ArrayList<>();
			for(NeMappingEntity neMappingEntity:neDetails)
			{
				programName = runTestParams.get("programName").toString();
				programId = (Integer) runTestParams.get("programId");
				Map<String, String> mapDetails=new HashMap<>();
				mapDetails.put("neId", neMappingEntity.getEnbId());
				mapDetails.put("neName", neMappingEntity.getEnbId());
				String NE =mapDetails.put("neId", neMappingEntity.getEnbId());
				String Name=mapDetails.put("neName", neMappingEntity.getSiteName());
				System.out.println(Name);
				String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqName);
				if (Name== NE) {
					if(programName.contains("5G-DSS")) {
						
						
						List<CIQDetailsModel> listCIQDetailsModelDay1 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, NE, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
						
						Name=listCIQDetailsModelDay1.get(0).getCiqMap().get("NEName").getHeaderValue().trim();
						mapDetails.put("neName",Name );
					}
					else if(programName.contains("5G-CBAND")) {
						List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, NE, enbName, dbcollectionFileName, "Day0_1", "");
						Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("neName").getHeaderValue().trim();
						mapDetails.put("neName", Name);
					}
					else if(programName.contains("5G-MM")) {
						List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, NE, enbName, dbcollectionFileName, "5GNRCIQAU", "");
						Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("GNB AU Name").getHeaderValue().trim();
						mapDetails.put("neName", Name);
					}
					else if(programName.contains("VZN-4G-USM-LIVE")) {
						List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, NE, enbName, dbcollectionFileName, "IPPLAN", "");
						Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("eNB_Name").getHeaderValue().trim();
						mapDetails.put("neName", Name);
					}
					else if(programName.contains("VZN-4G-FSU")) {
					//listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "FSU10", "");

						List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, NE, enbName, dbcollectionFileName, "FSUCIQ", "");
						Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("NE_Name").getHeaderValue().trim();
						System.out.println(Name);
						mapDetails.put("neName", Name);
					}
					else {
						mapDetails.put("neName", "Bulk NE Search");
						
					}
				}
				if(StringUtils.isNotEmpty(neMappingEntity.getSiteName()))
				{
					mapDetails.put("siteName", neMappingEntity.getSiteName());
				}
				neIdsData.add(neMappingEntity.getEnbId());
				//listDetails.add(mapDetails);
				if(!listDetails.contains(mapDetails)) {
					listDetails.add(mapDetails);
				}
			}
			
			if(!ObjectUtils.isEmpty(runTestParams.get("premigration")))
			{
				JSONObject premigrationJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("premigration")));
			premigrationJsonObject.put("neDetails", listDetails);
			premigrationJsonObject.put("ciqFileName", ciqName);
			premigrationJsonObject.put("ciqName", ciqName);
			runTestParams.put("premigration", premigrationJsonObject);
			}
			
			if(!ObjectUtils.isEmpty(runTestParams.get("migration")))
			{
				JSONObject migrationJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("migration")));
				JSONObject runTestFormsJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) migrationJsonObject.get("runTestFormDetails")));
						runTestFormsJsonObject.put("neDetails", listDetails);
						runTestFormsJsonObject.put("ciqName", ciqName);
						migrationJsonObject.put("runTestFormDetails", runTestFormsJsonObject);
				
				runTestParams.put("migration", migrationJsonObject);
			}
			if(!ObjectUtils.isEmpty(runTestParams.get("preaudit")))
			{
				JSONObject preauditJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("preaudit")));
				JSONObject runTestFormsJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) preauditJsonObject.get("runTestFormDetails")));
						runTestFormsJsonObject.put("neDetails", listDetails);
						runTestFormsJsonObject.put("ciqName", ciqName);
						preauditJsonObject.put("runTestFormDetails", runTestFormsJsonObject);
				        runTestParams.put("preaudit", preauditJsonObject);
			}
			if(!ObjectUtils.isEmpty(runTestParams.get("Nestatus")))
			{
				JSONObject neStaqtusJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("Nestatus")));
				JSONObject runTestFormsJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) neStaqtusJsonObject.get("runTestFormDetails")));
						runTestFormsJsonObject.put("neDetails", listDetails);
						runTestFormsJsonObject.put("ciqName", ciqName);
						neStaqtusJsonObject.put("runTestFormDetails", runTestFormsJsonObject);
				        runTestParams.put("Nestatus", neStaqtusJsonObject);
			}
			
			if(!ObjectUtils.isEmpty(runTestParams.get("negrow")))
			{
				JSONObject neGrowJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("negrow")));
				JSONObject runTestFormsJsonObject=(JSONObject) new JSONParser()
				.parse(runTestParams.toJSONString((Map) neGrowJsonObject.get("runTestFormDetails")));
				runTestFormsJsonObject.put("neDetails", listDetails);
				runTestFormsJsonObject.put("ciqName", ciqName);
				neGrowJsonObject.put("runTestFormDetails", runTestFormsJsonObject);
				runTestParams.put("negrow", neGrowJsonObject);
			}
			
			if(!ObjectUtils.isEmpty(runTestParams.get("postmigration")))
			{
				JSONObject postmigrationJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("postmigration")));
				JSONObject runTestFormsJsonObject=(JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) postmigrationJsonObject.get("runTestFormDetails")));
						runTestFormsJsonObject.put("neDetails", listDetails);
						runTestFormsJsonObject.put("ciqName", ciqName);
						postmigrationJsonObject.put("runTestFormDetails", runTestFormsJsonObject);
				        runTestParams.put("postmigration", postmigrationJsonObject);
			}
			
			runTestParams.put("neDetails", listDetails);
			runTestParams.put("ciqName", ciqName);
			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

		return runTestParams;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/getInProgressNeData")
	public JSONObject getInProgressNeData(@RequestBody JSONObject getInProgressNeDataParams) {
		JSONObject neList = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		Integer programId;
		String toDate = null;
		String fromDate = null;
		List<String> userNameList = null;
		try {
			sessionId = getInProgressNeDataParams.get("sessionId").toString();
			serviceToken = getInProgressNeDataParams.get("serviceToken").toString();
			customerId = (Integer) getInProgressNeDataParams.get("customerId");
			programId = (Integer) getInProgressNeDataParams.get("programId");
			userNameList = (List<String>)getInProgressNeDataParams.get("userNameList");
			if(getInProgressNeDataParams.containsKey("fromDate")) {
				fromDate = getInProgressNeDataParams.get("fromDate").toString();
			}
			if(getInProgressNeDataParams.containsKey("toDate")) {
				toDate = getInProgressNeDataParams.get("toDate").toString();
			}
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Date endDate = new Date();
			if(toDate==null || toDate.isEmpty()) {
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			}
			if(fromDate==null || fromDate.isEmpty()) {
				fromDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			}
			WorkFlowManagementModel runTestModel = new WorkFlowManagementModel();
			runTestModel.setFromDate(fromDate);
			runTestModel.setToDate(toDate);
			List<Map<String, String>> neListMap = workFlowManagementService.getInProgressWorkFlowManagementDetails(runTestModel, programId, userNameList);
			
			neList.put("neListMap", neListMap);
			neList.put("sessionId", sessionId);
			neList.put("serviceToken", serviceToken);
			neList.put("status", Constants.SUCCESS);
			

		} catch (Exception e) {
			neList.put("status", Constants.FAIL);
			logger.info(
					"Exception in loadrunTest in WorkFlowManagementController" + ExceptionUtils.getFullStackTrace(e));
		}
		return neList;
	}

	@PostMapping(value = "/stopBulkNeData")
	public JSONObject stopBulkNeData(@RequestBody JSONObject stopBulkNeDataNeDataParams) {
		JSONObject result = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		List<Map<String, String>> neListMap = null;
		try {
			sessionId = stopBulkNeDataNeDataParams.get("sessionId").toString();
			serviceToken = stopBulkNeDataNeDataParams.get("serviceToken").toString();
			programId = (Integer) stopBulkNeDataNeDataParams.get("programId");
			neListMap = (List<Map<String, String>>) stopBulkNeDataNeDataParams.get("neListMap");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			for(Map<String, String> mapDetails : neListMap) {
				Integer wfid = NumberUtils.toInt(mapDetails.get("wfmId"));
				WorkFlowManagementEntity workFlowManagementEntityThread = null;
				workFlowManagementEntityThread = workFlowManagementService.getWorkFlowManagementEntity(wfid);
				String neGrowStatus = workFlowManagementEntityThread.getNeGrowStatus();
				String migStatus = workFlowManagementEntityThread.getMigStatus();
				String postMigStatus = workFlowManagementEntityThread.getPostMigStatus();
				String preauditStaus = workFlowManagementEntityThread.getPreAuditStatus();
				String neupstatus = workFlowManagementEntityThread.getNeUpStatus();
				JSONObject stopWfmparams = new JSONObject();
				stopWfmparams.put("sessionId", sessionId);
				stopWfmparams.put("serviceToken", serviceToken);
				stopWfmparams.put("programId", programId);
				stopWfmparams.put("neGrowStatus", neGrowStatus);
				stopWfmparams.put("migStatus", migStatus);
				stopWfmparams.put("postMigStatus", postMigStatus);
				stopWfmparams.put("preAuditStatus", preauditStaus);
				stopWfmparams.put("neStatus", neupstatus);
				stopWfmparams.put("wfmid", wfid);
				JSONObject stopResult = stopWFM(stopWfmparams);
				if(stopResult.get("status").equals(Constants.FAIL)) {
					result.put("status", Constants.FAIL);
					result.put("reason", "Failed to stop : " + workFlowManagementEntityThread.getEnbId());
				}
			}
			result.put("sessionId", sessionId);
			result.put("serviceToken", serviceToken);
			result.put("status", Constants.SUCCESS);
			result.put("reason", "Stopped Bulk Ne Successfully");
			
		} catch(Exception e) {
			result.put("status", Constants.FAIL);
			result.put("reason", "Failed to stop Bulk Ne");
			logger.error("Exception in stopBulkNeData() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@PostMapping(value = "/getDuoExecErrorNeList")
	public JSONObject getDuoExecErrorNeList(@RequestBody JSONObject getDuoExecErrorNeListParams) {
		JSONObject neList = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		//Integer customerId = null;
		Integer programId;
		String programName = null;
		String toDate = null;
		String fromDate = null;
		List<String> userNameList = null;
		try {
			sessionId = getDuoExecErrorNeListParams.get("sessionId").toString();
			serviceToken = getDuoExecErrorNeListParams.get("serviceToken").toString();
			//customerId = (Integer) getDuoExecErrorNeListParams.get("customerId");
			programId = (Integer) getDuoExecErrorNeListParams.get("programId");
			userNameList = (List<String>)getDuoExecErrorNeListParams.get("userNameList");
			if(getDuoExecErrorNeListParams.containsKey("fromDate")) {
				fromDate = getDuoExecErrorNeListParams.get("fromDate").toString();
			}
			if(getDuoExecErrorNeListParams.containsKey("toDate")) {
				toDate = getDuoExecErrorNeListParams.get("toDate").toString();
			}
			programName = getDuoExecErrorNeListParams.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Date endDate = new Date();
			if(toDate==null || toDate.isEmpty()) {
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			}
			if(fromDate==null || fromDate.isEmpty()) {
				fromDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			}
			WorkFlowManagementModel runTestModel = new WorkFlowManagementModel();
			runTestModel.setFromDate(fromDate);
			runTestModel.setToDate(toDate);
			String useCaseName = "vDUInstantiation";
			if(programName.contains("5G-CBAND")) {
				List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_CBAND, AuditConstants.USECASE_5G_CBAND);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					useCaseName = auditConstantsList.get(0).getParameterValue().trim();
				}
			} else if(programName.contains("5G-DSS")) {
				List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_5G_DSS, AuditConstants.USECASE_5G_DSS);
				if(!ObjectUtils.isEmpty(auditConstantsList)) {
					useCaseName = auditConstantsList.get(0).getParameterValue().trim();
				}
			}
			
			List<Map<String, String>> neListMap = workFlowManagementService.getDuoExecErrorSiteList(runTestModel, programId, userNameList, programName, useCaseName);
			
			neList.put("neListMap", neListMap);
			neList.put("sessionId", sessionId);
			neList.put("serviceToken", serviceToken);
			neList.put("status", Constants.SUCCESS);
			

		} catch (Exception e) {
			neList.put("status", Constants.FAIL);
			logger.info(
					"Exception in loadrunTest in WorkFlowManagementController" + ExceptionUtils.getFullStackTrace(e));
		}
		return neList;
	}
	
		@SuppressWarnings({ "unchecked", "static-access" })
		@PostMapping(value = "/runTestRETWFM")
		public JSONObject workFlowRETTest(@RequestPart(required = false, value = "UPLOAD") MultipartFile ret,
			@RequestParam("retRunTest") String retRunTest) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		JSONObject negrowInputs = null;
		JSONObject premigrationInputs = null;
		JSONObject migrationInputs = null;
		JSONObject postmigrationInputs = null;
		JSONObject fetchUseCaseInputs = null;
		String migrationType1 = null;
		String migrationSubType1 = null;
		String ciqFileName = null;
		String programName = null;
		String migrationType = null;
		String migrationSubType = null;
		String programId = null;
		String enbId = null;
		String enbName = null;
		Integer workFlowId = null;
		String state = null;
		String sanePassword = null;
		boolean useCurrPassword = true;
		boolean rfScriptsFlag = false;

		List<Map<String, String>> enbList = null;
		Map<String, Integer> temp = new HashMap<>();
		WorkFlowManagementEntity WorkFlowManagementEntity = null;
		try {
			if (ret != null && uploadFileService.isFileEmpty(ret)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPLOAD_SCRIPT_EMPTY));
				return resultMap;
			}
			// System.out.println("Multipart ret : " + ret.toString());
			JSONParser parser = new JSONParser();
			JSONObject runTestParams = (JSONObject) parser.parse(retRunTest);

			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			programId = runTestParams.get("programId").toString();
			programName = runTestParams.get("programName").toString();
			runTestParams.put("testname", "null");
			String testname = runTestParams.get("testname").toString();
			runTestParams.put("testDesc", "");

			// Map<String, RunTestEntity> runTestEntity =
			// runTestService.insertRunTestDetails(runTestParams, "");

			int programIds = Integer.parseInt(runTestParams.get("programId").toString());
			List<Map> neList = (List<Map>) runTestParams.get("neDetails");
			Map runs = (Map) runTestParams.get("runTestFormDetails");
			for (Map enb : neList) {
				enbId = (String) enb.get("neId");
				enbName = (String) enb.get("neName");
			}
			if (ret != null) {
				String fileName = ret.getOriginalFilename();
				System.out.println("FileN " + fileName);
				String fileExtension2 = FilenameUtils.getExtension(fileName);

				if (!fileName.contains(enbId) || !fileExtension2.contains("xlsx")) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Incorrect file uploaded");
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
			}
			List<RetTestEntity> auditRetDetailsEntityList1 = retRepository.getAuditRetEntity(enbId);
			Set<String> uqi = new HashSet<>();
			for (RetTestEntity ciqData : auditRetDetailsEntityList1) {
				if (!ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {
					uqi.add(ciqData.getUniqueId().trim());
				}
			}
			int max = 0;
			for (String r : uqi) {
				if (NumberUtils.isNumber(r)) {
					int s = Integer.parseInt(r);

					if (s > max) {
						max = s;
					}
				}
			}

			if (ret != null && !ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {

				max = max + 1;

			}
			if (ret != null && ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {

				max = max + 1;

			}
			if (ret == null && !ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {

				max = max + 1;

			}

			if (ret == null && ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {

				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Upload Ret Form Is NOT FOUND");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				return resultMap;

			}

			// int uniqueId = runTestEntity.get(enbId).getId();
			String userName = null;
			if (runTestParams.containsKey("userName")) {
				userName = runTestParams.get("userName").toString();
			}
			int uniqueId = max;
			// System.out.println("runTestEntity.get(enbId).getId() " +
			// runTestEntity.get(enbId).getId());
			String result = null;
			String retFileContent = null;
			Workbook workbook = null;
			StringBuffer sb = new StringBuffer();
			String line;
			try {
				if (ret != null) {

					String fileN = ret.getOriginalFilename();
					System.out.println("FileN " + fileN);
					String fileExtension = FilenameUtils.getExtension(fileN);

					if (fileN.contains(enbId) && fileExtension.contains("xlsx")) {

						InputStream is = ret.getInputStream();

						workbook = new XSSFWorkbook(ret.getInputStream());
						XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

						boolean fileSave = false;
						Integer pId = Integer.parseInt(programId);
						fileSave = retService.saveRETform(sheet, fileN, pId, userName, enbId, uniqueId);
						System.out.println("Out from saveRetForm");

						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason", GlobalInitializerListener.faultCodeMap
								.get(FaultCodes.CREATED_CHECKLIST_DETAILS_SUCCESSFULLY));
						commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CHECK_LIST,
								Constants.ACTION_SAVE, "File Uploaded Successfully" + fileN, sessionId);
						if (fileSave) {
							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.FILE_DETAILS_SAVED_SUCCESSFULLY));
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							System.out.println("true file save");

						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Incorrect file uploaded");
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							System.out.println("False file save");
						}

					} else {
						logger.info("File Is Incorrect");
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "Incorrect file uploaded");
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
					}
				}

			} catch (IOException e) {
				logger.info("Exception in runTestRET() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				if (workbook != null) {
					try {
						workbook.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// String htmlOutputFileName = "";
			// if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION) &&
			// "AUDIT".equalsIgnoreCase(migrationSubType)) {
			// String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new
			// Date());
			// htmlOutputFileName = "_RET_AUDIT_" + timeStamp + ".html";
			// System.out.println(htmlOutputFileName);
			// }

			JSONObject statusJSONObject = workFlowTest(runTestParams);
			if (statusJSONObject.get("status").equals(Constants.SUCCESS)) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "RET Sucessfully executed");
				System.out.println("True status");

			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Unable to execute ret workflow management");
				System.out.println("False status");
			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", "Unable to start workflow management");
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
		

}
