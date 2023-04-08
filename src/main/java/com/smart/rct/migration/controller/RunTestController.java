package com.smart.rct.migration.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RetTestEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseXmlRuleEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleModel;
import com.smart.rct.migration.model.FileRuleModel;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.model.ShellRuleModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.migration.model.XmlRuleModel;
import com.smart.rct.migration.repository.CmdRuleBuilderRepository;
import com.smart.rct.migration.repository.RetRepository;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.repositoryImpl.UseCaseBuilderRepositoryImpl;
import com.smart.rct.migration.service.RanAtpService;
import com.smart.rct.migration.service.RetService;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.migration.service.XmlRuleBuilderService;
import com.smart.rct.postmigration.dto.Audit4GFsuSummaryDto;
import com.smart.rct.postmigration.dto.Audit4GSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GCBandSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GDSSSummaryDto;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSuccessSummaryModel;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;
import com.smart.rct.postmigration.service.Audit4GFsuIssueService;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.postmigration.service.Audit4GIssueService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.Audit5GCBandIssueService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;
import com.smart.rct.postmigration.service.Audit5GDSSIssueService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.controller.UploadCIQController;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.models.WorkFlowManagementPremigration;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PasswordCrypt;

import io.micrometer.shaded.org.pcollections.Empty;

//import org.apache.commons.io.comparator.LastModifiedFileComparator;
@RestController
public class RunTestController {

	private static final Logger logger = LoggerFactory.getLogger(RunTestController.class);

	@Autowired
	RunTestService runTestService;

	@Autowired
	Audit4GFsuSummaryDto audit4GFsuSummaryDto;

	@Autowired
	Audit5GDSSSummaryDto audit5GDSSSummaryDto;

	@Autowired
	Audit4GSummaryDto audit4GSummaryDto;

	@Autowired
	Audit5GCBandSummaryDto audit5GCBandSummaryDto;

	@Autowired
	FileUploadService fileUploadService;

	@Autowired
	RanAtpService ranAtpService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	UseCaseBuilderService useCaseBuilderService;

	@Autowired
	UploadCIQController uploadCIQController;

	@Autowired
	UploadFileService uploadFileService;

	@Autowired
	CmdRuleBuilderRepository cmdRuleBuilderRepository;

	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;

	@Autowired
	UseCaseBuilderRepositoryImpl useCaseBuilderRepositoryImpl;

	@Autowired
	RunTestRepository runTestRepository;

	@Autowired
	XmlRuleBuilderService xmlRuleBuilderService;

	@Autowired
	WorkFlowManagementService workFlowManagementService;

	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;

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
	RetService retService;
	@Autowired
	RetRepository retRepository;

	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;

	/**
	 * This method will load RunTest table details and NetworkType, LSM Versions,
	 * LSM Names and Use Cases
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */

	@PostMapping(value = "/oss/netconf/DU_10010", produces = MediaType.APPLICATION_XML_VALUE)
	public String postResponseXmlContent(@RequestBody String loginForm) {
		return "bbsss";
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/loadrunTest")
	public JSONObject loadrunTest(@RequestBody JSONObject loadRunTestParams) {
		JSONObject ciqList = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String migrationType = null;
		String migrationSubType = null;
		Integer programId;
		Integer history;
		String searchStatus = null;
		String toDate = null;
		String fromDate = null;
		String userName = null;
		boolean showUserData = false;
		boolean wfmKey = false;
		try {
			sessionId = loadRunTestParams.get("sessionId").toString();
			serviceToken = loadRunTestParams.get("serviceToken").toString();
			customerId = (Integer) loadRunTestParams.get("customerId");
			migrationType = loadRunTestParams.get("migrationType").toString();
			migrationSubType = loadRunTestParams.get("migrationSubType").toString();
			programId = (Integer) loadRunTestParams.get("programId");
			searchStatus = loadRunTestParams.get("searchStatus").toString();

			if (loadRunTestParams.containsKey("showUserData")) {
				showUserData = (boolean) loadRunTestParams.get("showUserData");
			}
			if (loadRunTestParams.containsKey("wfmKey")) {
				wfmKey = (boolean) loadRunTestParams.get("wfmKey");
			}
			if (loadRunTestParams.containsKey("userName")) {
				userName = loadRunTestParams.get("userName").toString();
			}
			/*
			 * if("premigration".equalsIgnoreCase(migrationType)) { migrationType =
			 * "PreMigration"; }else if("migration".equalsIgnoreCase(migrationType)) {
			 * migrationType = "Migration"; }else
			 * if("postmigration".equalsIgnoreCase(migrationType)) { migrationType =
			 * "PostMigration"; }
			 * 
			 * if ("precheck".equalsIgnoreCase(migrationSubType)) { migrationSubType =
			 * "PreCheck"; } else if ("commission".equalsIgnoreCase(migrationSubType)) {
			 * migrationSubType = "Commission"; } else if
			 * ("postcheck".equalsIgnoreCase(migrationSubType)) { migrationSubType =
			 * "PostCheck"; } else if ("AUDIT".equalsIgnoreCase(migrationSubType)) {
			 * migrationSubType = "Audit"; }else if
			 * ("RANATP".equalsIgnoreCase(migrationSubType)) { migrationSubType = "RanATP";
			 * }
			 */

			JSONObject ciqDetails = new JSONObject();
			ciqDetails.put("sessionId", sessionId);
			ciqDetails.put("serviceToken", serviceToken);
			ciqDetails.put("searchStatus", "load");
			ciqDetails.put("programId", programId);
			ciqDetails.put("customerId", customerId);

			history = Integer.valueOf(LoadPropertyFiles.getInstance().getProperty("actionPerformed"));

			Map<String, Object> paginationData = (Map<String, Object>) loadRunTestParams.get("pagination");
			int count;
			int page;
			if (null != paginationData) {

				page = (Integer) paginationData.get("page");
				if (paginationData.get("count") instanceof String) {
					count = Integer.parseInt((String) paginationData.get("count"));
				} else {
					count = (Integer) paginationData.get("count");
				}
			} else {
				count = 10;
				page = 1;
			}

			/*
			 * String pagination = loadRunTestParams.get("pagination").toString(); int
			 * rowCount; int pageCount; PaginationModel paginationModel = new
			 * Gson().fromJson(pagination, PaginationModel.class); RunTestModel
			 * runTestModels = new Gson().fromJson( loadRunTestParams.toJSONString((Map)
			 * loadRunTestParams.get("searchCriteria")), RunTestModel.class); rowCount =
			 * runTestService.loadRunTestDetails(programId, migrationType, migrationSubType,
			 * customerId, history, runTestModels); pageCount =
			 * CommonUtil.getPageCount(rowCount,
			 * Integer.parseInt(paginationModel.getCount()));
			 */

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			List<Map> neVersionNameUSecaseMap = runTestService.getSmList(programId);

			List<Map> useCaseLst = new ArrayList<>();

			if (!(StringUtils.isNotEmpty(migrationSubType) && "NEGrow".equalsIgnoreCase(migrationSubType))) {
				if (!(StringUtils.isNotEmpty(migrationSubType) && ("precheck".equalsIgnoreCase(migrationSubType)
						|| "Audit".equalsIgnoreCase(migrationSubType)))) {

					useCaseLst = runTestService.getUseCaseList(programId, migrationType, migrationSubType);
				}
			}

			ciqList = uploadCIQController.getCiqList(ciqDetails);

			/*
			 * List<RunTestModel> runTestDetails;
			 * 
			 * if ("search".equalsIgnoreCase(searchStatus)) { RunTestModel runTestModel;
			 * 
			 * runTestModel = new Gson().fromJson( loadRunTestParams.toJSONString((Map)
			 * loadRunTestParams.get("searchCriteria")), RunTestModel.class);
			 * 
			 * runTestDetails = runTestService.getRunTestSearchDetails(runTestModel, page,
			 * count, customerId, programId, migrationType, migrationSubType, history); }
			 * else {
			 * 
			 * runTestDetails = runTestService.getRunTestDetails(page, count, customerId,
			 * programId, migrationType, migrationSubType, history); }
			 */

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
				RunTestModel runTestModel = new RunTestModel();
				runTestModel.setFromDate(fromDate);
				runTestModel.setToDate(toDate);
				runTestDetailsMap = runTestService.getRunTestDetails(runTestModel, page, count, programId,
						migrationType, migrationSubType, wfmKey);
			} else if (Constants.SEARCH.equals(searchStatus)) {
				RunTestModel runTestModel = new Gson().fromJson(
						loadRunTestParams.toJSONString((Map) loadRunTestParams.get("searchCriteria")),
						RunTestModel.class);
				runTestDetailsMap = runTestService.getRunTestDetails(runTestModel, page, count, programId,
						migrationType, migrationSubType, wfmKey);
			} else if (Constants.LOAD.equals(searchStatus) && showUserData) {
				Date endDate = new Date();
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				RunTestModel runTestModel = new RunTestModel();
				runTestModel.setFromDate(fromDate);
				runTestModel.setToDate(toDate);
				runTestModel.setUserName(userName);
				runTestDetailsMap = runTestService.getRunTestDetails(runTestModel, page, count, programId,
						migrationType, migrationSubType, wfmKey);
			}
			boolean isInProgress = false;

			if (runTestDetailsMap != null && runTestDetailsMap.containsKey("progressStatus")) {
				AtomicBoolean statusOfInprogress = (AtomicBoolean) runTestDetailsMap.get("progressStatus");
				isInProgress = statusOfInprogress.get();
			}

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
			ciqList.put("useCaseList", useCaseLst);
			ciqList.put("isInProgress", isInProgress);

		} catch (Exception e) {
			ciqList.put("status", Constants.FAIL);
			logger.info("Exception in loadrunTest in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return ciqList;
	}

	/**
	 * This method will load RunTest table details and NetworkType, LSM Versions,
	 * LSM Names and Use Cases
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/loadUsecase")
	public JSONObject loadUsecase(@RequestBody JSONObject loadUsecaseParams) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String migrationType = null;
		String migrationSubType = null;
		Integer programId;
		String ciqFName = null;

		try {
			sessionId = loadUsecaseParams.get("sessionId").toString();
			serviceToken = loadUsecaseParams.get("serviceToken").toString();
			migrationType = loadUsecaseParams.get("migrationType").toString();
			migrationSubType = loadUsecaseParams.get("migrationSubType").toString();
			programId = (Integer) loadUsecaseParams.get("programId");

			int ciqFNameindex = loadUsecaseParams.get("ciqName").toString().indexOf(".xls");
			ciqFName = loadUsecaseParams.get("ciqName").toString().substring(0, ciqFNameindex);

			List<Map> neList = (List<Map>) loadUsecaseParams.get("neDetails");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			List<Map> useCaseLst = runTestService.getUseCaseList(programId, migrationType, migrationSubType, ciqFName,
					neList);

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("useCaseList", useCaseLst);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in loadrunTest in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will execute the Runtest do
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/runTest")
	public JSONObject runTest(@RequestBody JSONObject runTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		boolean rfScriptsFlag = false;
		boolean Type = false;
		String serviceToken = null;
		boolean useCurrPassword;
		try {
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();

			Map run = (Map) runTestParams.get("runTestFormDetails");
			useCurrPassword = (boolean) run.get("currentPassword");
			String sanePassword = run.get("password").toString();
			String subType = runTestParams.get("migrationSubType").toString();
			String programName = runTestParams.get("programName").toString();
			if (run.containsKey("prePostAuditFlag") && run.get("prePostAuditFlag") != null) {
				Type = (boolean) run.get("prePostAuditFlag");
			}
			int programId = Integer.parseInt(runTestParams.get("programId").toString());
			JSONObject migrationInputs = null;
			JSONObject postmigrationInputs = null;
			List<Map<String, String>> enbList = null;

			// for running pre && post Audit through migration only for dss
			if ((programName.equalsIgnoreCase("VZN-5G-DSS") || (programName.equalsIgnoreCase("VZN-5G-CBAND")))
					&& Type == (true)) {
				// migrationInputs.putAll(runTestParams);
				JSONObject runTestFormDetails1 = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));

				// runTestFormDetails.
				Map<String, RunTestEntity> runTestEntity1 = null;

				if (runTestParams.get("migrationType").equals("migration")) {
					migrationInputs = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) runTestParams));
				}
				int flag;
				JSONObject list4 = null;
				String testName = (String) runTestFormDetails1.get("testname");
				LinkedHashMap<String, String> list3 = new LinkedHashMap<String, String>();
				if (!testName.contains("WFM_")) {
					// LinkedHashMap<String, String> list3 = new LinkedHashMap<String, String>();
					list3 = (LinkedHashMap<String, String>) runTestParams.get("runTestFormDetails");
					flag = 1;
				} else {
					list4 = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
					flag = 2;
				}
				// Map enbList1 =( Map) runTestParams.get("neDetails");
				// String enbName = ((Map) enbList1.get(0)).get("neName").toString();
				runTestFormDetails1.remove("scripts");
				List<String> neList1 = new ArrayList<>();
				List<Map> neList2 = (List<Map>) run.get("neDetails");
				for (Map neid : neList2) {
					String i = neid.get("neId").toString();
					NetworkConfigEntity neMappingEntitiesForVersion1 = null;
					NeMappingModel neMappingModelversion1 = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntityversion1 = new CustomerDetailsEntity();
					programDetailsEntityversion1.setId(programId);
					neMappingModelversion1.setProgramDetailsEntity(programDetailsEntityversion1);
					neMappingModelversion1.setEnbId(i);
					NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
							.getNetWorkEntityDetails(neMappingModelversion1);
					String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
					if (!lsmVersion.contains("21.B")) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", "Selected NeId is not map to 21.B");
						return resultMap;

					}

					neList1.add(i);
				}
				List<Map> useCaseLst = null;
				List<Map> scripts = null;
				// if(migrationType.equalsIgnoreCase("PostMigration") &&
				// migrationSubType.equalsIgnoreCase("Audit")){

				// neList1 = (List) run.get("neDetails");
				String migrationType = "postmigration";
				String migrationSubType = "AUDIT";
				useCaseLst = runTestService.getPostMigrationUseCaseList(programId, migrationType, migrationSubType,
						neList1);
				runTestParams.put("migrationSubType", "AUDIT");
				runTestParams.put("migrationType", "postmigration");
				Map<String, String> mapDetails = new HashMap<>();
				Map<String, String> mapDetailsScript = new HashMap<>();
				String timeStampDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
				String lsmVersion1 = (String) runTestFormDetails1.get("lsmVersion1");
				List<Map<String, String>> listDetails = new ArrayList<>();
				List<Map<String, String>> ScriptDetails = new ArrayList<>();
				for (Map usecase : useCaseLst) {
					if (usecase.get("useCaseName").equals("eNB_PreAudit")) {
						String usecaseName = (String) usecase.get("useCaseName");
						mapDetails.put("useCaseName", usecaseName);
						int useCaseId = (int) usecase.get("useCaseId");
						String usID = String.valueOf(useCaseId);
						mapDetails.put("useCaseId", usID);
						int executionSequence = (int) usecase.get("executionSequence");
						String exeSeq = String.valueOf(executionSequence);
						mapDetails.put("executionSequence", exeSeq);
						String ucSleepInterval = (String) usecase.get("ucSleepInterval");
						mapDetails.put("ucSleepInterval", ucSleepInterval);
						listDetails.add(mapDetails);
						runTestFormDetails1.put("useCase", listDetails);
						scripts = (List<Map>) usecase.get("scripts");

						for (Map Script : scripts) {
							String scriptName = (String) Script.get("scriptName");
							mapDetailsScript.put("scriptName", scriptName);
							mapDetailsScript.put("useCaseName", usecaseName);
							int scriptId = (int) Script.get("scriptId");
							String scriptID = String.valueOf(scriptId);
							mapDetailsScript.put("scriptId", scriptID);
							String scriptSleepInterval = (String) Script.get("scriptSleepInterval");
							String scriptsleepID = String.valueOf(scriptSleepInterval);
							mapDetailsScript.put("scriptSleepInterval", scriptsleepID);
							String useGeneratedScript = (String) Script.get("useGeneratedScript");
							mapDetailsScript.put("useGeneratedScript", useGeneratedScript);
							int scriptExeSequence = (int) Script.get("scriptExeSequence");
							String scripexeseqID = String.valueOf(scriptExeSequence);
							mapDetailsScript.put("scriptExeSequence", scripexeseqID);
							ScriptDetails.add(mapDetailsScript);

						}
						runTestFormDetails1.put("scripts", ScriptDetails);
						testName = (String) runTestFormDetails1.get("testname");
						if (!testName.contains("WFM_")) {
							runTestFormDetails1.put("lsmId", "");
						}
						// runTestFormDetails1.put("lsmVersion",lsmVersion1 );
						runTestFormDetails1.put("testname", "Pre-Audit");
					}
				}
				runTestParams.put("runTestFormDetails", runTestFormDetails1);
				if (runTestParams.get("migrationType").equals("postmigration")) {
					postmigrationInputs = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) runTestParams));
				}
				List<Map<String, String>> RunTestParams = new ArrayList<>();
				RunTestParams.add(postmigrationInputs);
				RunTestParams.add(migrationInputs);
				// RunTestParams.add(postmigrationInputs);
				Map<String, Object> auditoutput = new HashMap<>();
				// to run the PMP
				int call = 0;
				for (int i = 0; i < RunTestParams.size(); i++) {
					runTestParams = (JSONObject) (RunTestParams.get(i));
					if (i == 0) {
						call = i;
						String testname = "PreEnbAudit" + "_" + timeStampDate;

						Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");
						runTestFormDetails.put("testname", testname);
						runTestParams.put("runTestFormDetails", runTestFormDetails);
					}
					if (i == 1) {
						if (flag == 1) {
							runTestParams.put("runTestFormDetails", list3);
						} else {
							runTestParams.put("runTestFormDetails", list4);
						}
						call = i;
					}
					run = (Map) runTestParams.get("runTestFormDetails");
					migrationType = runTestParams.get("migrationType").toString();
					if (migrationType.equals("migration")) {
						rfScriptsFlag = (boolean) run.get("rfScriptFlag");
					}
					if (migrationType.equalsIgnoreCase("PreMigration")) {
						migrationType = "Premigration";
					}
					subType = runTestParams.get("migrationSubType").toString();
					expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

					if (expiryDetails != null) {
						return expiryDetails;
					}

					JSONObject output = new JSONObject();

					if (useCurrPassword == false && sanePassword.isEmpty()) {

						output = runTestService.getSaneDetailsforPassword(runTestParams);

						if (!output.isEmpty()) {
							resultMap.put("status", "PROMPT");
							resultMap.put("password", output);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("requestType", "RUN_TEST");

							return resultMap;
						}
					}

					int programIds = Integer.parseInt(runTestParams.get("programId").toString());
					List<Map> neList = (List<Map>) run.get("neDetails");
					Map runs = (Map) runTestParams.get("runTestFormDetails");
					if (!runs.get("lsmVersion").toString().isEmpty()
							|| migrationType.equalsIgnoreCase("postmigration")) {
						int count = 0;
						NetworkConfigEntity previousNetworkConfigEntity = null;
						for (Map neid : neList) {
							NeMappingModel neMappingModelversion = new NeMappingModel();
							CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
							programDetailsEntityversion.setId(programIds);
							neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
							neMappingModelversion.setEnbId(neid.get("neId").toString());
							NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
									.getNetWorkEntityDetails(neMappingModelversion);
							if (migrationType.equalsIgnoreCase("postmigration")) {
								if (count == 0) {
									previousNetworkConfigEntity = neMappingEntitiesForVersion;
								} else {
									if (previousNetworkConfigEntity != null && neMappingEntitiesForVersion != null
											&& (!previousNetworkConfigEntity.getId()
													.equals(neMappingEntitiesForVersion.getId()))) {
										resultMap.put("status", Constants.FAIL);
										resultMap.put("sessionId", sessionId);
										resultMap.put("serviceToken", serviceToken);
										resultMap.put("reason", "Selected Sites don't belong to the same SM Version");
										return resultMap;
									}
									previousNetworkConfigEntity = neMappingEntitiesForVersion;
								}
								count++;
							}
							if (neMappingEntitiesForVersion != null) {
								String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
								if (!lsmVersion.equals(runs.get("lsmVersion"))
										&& !runs.get("lsmVersion").toString().isEmpty()) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									resultMap.put("reason", "Selected Sites don't belong to the selected SM Version");
									return resultMap;
								}
							} else {
								if (runs.get("lsmVersion").toString().isEmpty()
										|| run.get("lsmName").toString().isEmpty()) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									resultMap.put("reason", "NE is not Mapped");
									return resultMap;
								}
							}
						}
					}
					String htmlOutputFileName = "";
					if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION) && "AUDIT".equalsIgnoreCase(subType)) {
						String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
						htmlOutputFileName = "_ORAN_AUDIT_" + timeStamp + ".html";
					}

					String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

					List<Map> scriptSeqDetails = (List<Map>) run.get("scripts");
					if (scriptSeqDetails.isEmpty()) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "No Scripts found to run");
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						return resultMap;
					}
					int xmlScriptsCount = 0;
					if (programName.contains("4G-USM-LIVE") && "Audit".equalsIgnoreCase(subType)) {

						for (Map scriptInfoDetails : scriptSeqDetails) {
							String scriptname = scriptInfoDetails.get("scriptName").toString();
							if (FilenameUtils.getExtension(scriptname).equalsIgnoreCase("xml")) {
								xmlScriptsCount++;
							}
						}
						if (scriptSeqDetails.size() != xmlScriptsCount && xmlScriptsCount != 0) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Select Usecases containing same type of extension");
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							return resultMap;
						}
					}
					int time = 0;

					while (!enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {
						Thread.sleep(5000);
						time++;
						enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);
						if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {
							break;
						} else if (time == 12) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", "Script is taking longer time to execute then usual");
							return resultMap;
						}
					}
					if (call == 0) {
						if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

							programId = Integer.parseInt(runTestParams.get("programId").toString());
							Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

							String testname = runTestFormDetails.get("testname").toString();

							if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
								migrationType = "Migration";
							} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
								migrationType = "PostMigration";
							} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
								migrationType = "Premigration";
							}

							if ("precheck".equalsIgnoreCase(subType)) {
								subType = "PreCheck";
							} else if ("commission".equalsIgnoreCase(subType)) {
								subType = "Commission";
							} else if ("postcheck".equalsIgnoreCase(subType)) {
								subType = "PostCheck";
							} else if ("AUDIT".equalsIgnoreCase(subType)) {
								subType = "Audit";
							} else if ("RANATP".equalsIgnoreCase(subType)) {
								subType = "RanATP";
							} else if ("NEGrow".equalsIgnoreCase(subType)) {
								subType = "NEGrow";
							}

							String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType,
									subType, testname);
							if (isTestNamePresent != Constants.SUCCESS) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", isTestNamePresent);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								return resultMap;
							}
							List<LinkedHashMap> useCaseList = (List) run.get("useCase");
							ArrayList<String> useList = new ArrayList<>();
							for (Map usecase : useCaseList) {
								useList.add(usecase.get("useCaseName").toString());
							}
							System.out.println(useCaseList);
							if (!(programName.equals("VZN-4G-FSU"))) {
								for (Map usecase : useCaseList) {
									String useCaseName = usecase.get("useCaseName").toString();
									String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
									if (useCaseName.contains("CommissionScriptUsecase")
											&& !useList.contains(rfUseCaseName)) {
										String newUseCaseName = useCaseName.replace("CommissionScriptUsecase",
												"RFUsecase");
										boolean isuseCasePresent = runTestService.getusecaseDetails(programId,
												migrationType, subType, newUseCaseName);
										System.out.println(isuseCasePresent);
										if (!isuseCasePresent) {
											resultMap.put("status", Constants.FAIL);
											resultMap.put("reason",
													"Please complete the RF Scipts execution to execute the commission scripts");
											resultMap.put("sessionId", sessionId);
											resultMap.put("serviceToken", serviceToken);
											return resultMap;
										}
									}

								}
							}

							Map<String, RunTestEntity> runTestEntity = runTestService
									.insertRunTestDetails(runTestParams, "");
							Set<String> runtestNeidList = runTestEntity.keySet();
							if (runtestNeidList.size() != neList.size()) {
								List<Map> newNeidList = new ArrayList<>();
								for (Map neid : neList) {
									if (runtestNeidList.contains(neid.get("neId"))) {
										newNeidList.add(neid);
									}
								}
								runs.put("neDetails", newNeidList);
								runTestParams.put("runTestFormDetails", runs);
							}
							String result = null;

							if ("Audit".equalsIgnoreCase(subType) && (programName.contains("5G") || xmlScriptsCount != 0
									|| programName.contains("4G-FSU"))) {
								result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity,
										"CURRENT", htmlOutputFileName, null, rfScriptsFlag);

							} else {
								result = runTestService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
										htmlOutputFileName, null, rfScriptsFlag);
							}
							resultMap.put("runTestEntity", runTestEntity);
							resultMap.put("password", output);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							if (result.equalsIgnoreCase(Constants.SUCCESS)) {
								resultMap.put("status", Constants.SUCCESS);
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
							}

						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", enbStatus);
						}
					} else if (call == 1) {
						if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

							programId = Integer.parseInt(runTestParams.get("programId").toString());
							Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

							String testname = runTestFormDetails.get("testname").toString();

							if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
								migrationType = "Migration";
							} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
								migrationType = "PostMigration";
							} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
								migrationType = "Premigration";
							}

							if ("precheck".equalsIgnoreCase(subType)) {
								subType = "PreCheck";
							} else if ("commission".equalsIgnoreCase(subType)) {
								subType = "Commission";
							} else if ("postcheck".equalsIgnoreCase(subType)) {
								subType = "PostCheck";
							} else if ("AUDIT".equalsIgnoreCase(subType)) {
								subType = "Audit";
							} else if ("RANATP".equalsIgnoreCase(subType)) {
								subType = "RanATP";
							} else if ("NEGrow".equalsIgnoreCase(subType)) {
								subType = "NEGrow";
							}

							String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType,
									subType, testname);
							if (isTestNamePresent != Constants.SUCCESS) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", isTestNamePresent);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								return resultMap;
							}
							List<LinkedHashMap> useCaseList = (List) run.get("useCase");
							ArrayList<String> useList = new ArrayList<>();
							for (Map usecase : useCaseList) {
								useList.add(usecase.get("useCaseName").toString());
							}
							System.out.println(useCaseList);
							if (!(programName.equals("VZN-4G-FSU"))) {
								for (Map usecase : useCaseList) {
									String useCaseName = usecase.get("useCaseName").toString();
									String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
									if (useCaseName.contains("CommissionScriptUsecase")
											&& !useList.contains(rfUseCaseName)) {
										String newUseCaseName = useCaseName.replace("CommissionScriptUsecase",
												"RFUsecase");
										boolean isuseCasePresent = runTestService.getusecaseDetails(programId,
												migrationType, subType, newUseCaseName);
										System.out.println(isuseCasePresent);
										if (!isuseCasePresent) {
											resultMap.put("status", Constants.FAIL);
											resultMap.put("reason",
													"Please complete the RF Scipts execution to execute the commission scripts");
											resultMap.put("sessionId", sessionId);
											resultMap.put("serviceToken", serviceToken);
											return resultMap;
										}
									}

								}
							}
							runTestEntity1 = runTestService.insertRunTestDetails(runTestParams, "");
							Set<String> runtestNeidList = runTestEntity1.keySet();
							if (runtestNeidList.size() != neList.size()) {
								List<Map> newNeidList = new ArrayList<>();
								for (Map neid : neList) {
									if (runtestNeidList.contains(neid.get("neId"))) {
										newNeidList.add(neid);
									}
								}
								runs.put("neDetails", newNeidList);
								runTestParams.put("runTestFormDetails", runs);
							}
							String result = null;

							if ("Audit".equalsIgnoreCase(subType) && (programName.contains("5G") || xmlScriptsCount != 0
									|| programName.contains("4G-FSU"))) {
								/*
								 * JSONObject resultAudit =
								 * runTestService.getRuntestExecResult5GDSS(runTestParams, runTestEntity1,
								 * "CURRENT", htmlOutputFileName, null, rfScriptsFlag, auditoutput); result =
								 * (String) resultAudit.get("Status");
								 */
								result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity1,
										"CURRENT", htmlOutputFileName, null, rfScriptsFlag);

							} else {
								result = runTestService.getRuntestExecResult(runTestParams, runTestEntity1, "CURRENT",
										htmlOutputFileName, null, rfScriptsFlag);
							}
							resultMap.put("runTestEntity", runTestEntity1);
							resultMap.put("password", output);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							if (result.equalsIgnoreCase(Constants.SUCCESS)) {
								resultMap.put("status", Constants.SUCCESS);
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
							}

						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", enbStatus);
						}
					}

					commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
							Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);

				}
			} else {
				String migrationType = runTestParams.get("migrationType").toString();
				if (migrationType.equals("migration")) {
					rfScriptsFlag = (boolean) run.get("rfScriptFlag");
				}
				if (migrationType.equalsIgnoreCase("PreMigration")) {
					migrationType = "Premigration";
				}

				expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

				if (expiryDetails != null) {
					return expiryDetails;
				}

				JSONObject output = new JSONObject();

				if (useCurrPassword == false && sanePassword.isEmpty()) {

					output = runTestService.getSaneDetailsforPassword(runTestParams);

					if (!output.isEmpty()) {
						resultMap.put("status", "PROMPT");
						resultMap.put("password", output);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("requestType", "RUN_TEST");

						return resultMap;
					}
				}
				int programIds = Integer.parseInt(runTestParams.get("programId").toString());
				List<Map> neList = (List<Map>) run.get("neDetails");
				Map runs = (Map) runTestParams.get("runTestFormDetails");
				if (!runs.get("lsmVersion").toString().isEmpty() || migrationType.equalsIgnoreCase("postmigration")
						|| (subType.equalsIgnoreCase("PREAUDIT") || subType.equalsIgnoreCase("NESTATUS"))) {
					int count = 0;
					NetworkConfigEntity previousNetworkConfigEntity = null;
					for (Map neid : neList) {
						NeMappingModel neMappingModelversion = new NeMappingModel();
						CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
						programDetailsEntityversion.setId(programIds);
						neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
						neMappingModelversion.setEnbId(neid.get("neId").toString());
						NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
								.getNetWorkEntityDetails(neMappingModelversion);
						if (migrationType.equalsIgnoreCase("postmigration")
								|| (migrationType.equalsIgnoreCase("Premigration")
										&& subType.equalsIgnoreCase("PREAUDIT"))
								|| (migrationType.equalsIgnoreCase("Premigration")
										&& subType.equalsIgnoreCase("NESTATUS"))) {
							if (count == 0) {
								previousNetworkConfigEntity = neMappingEntitiesForVersion;
							} else {
								if (previousNetworkConfigEntity != null && neMappingEntitiesForVersion != null
										&& (!previousNetworkConfigEntity.getId()
												.equals(neMappingEntitiesForVersion.getId()))) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									resultMap.put("reason", "Selected Sites don't belong to the same SM Version");
									return resultMap;
								}
								previousNetworkConfigEntity = neMappingEntitiesForVersion;
							}
							count++;
						}
						if (neMappingEntitiesForVersion != null) {
							String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
							if (!lsmVersion.equals(runs.get("lsmVersion"))
									&& !runs.get("lsmVersion").toString().isEmpty()) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "Selected Sites don't belong to the selected SM Version");
								return resultMap;
							}
						} else {
							if (runs.get("lsmVersion").toString().isEmpty()
									|| run.get("lsmName").toString().isEmpty()) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "NE is not Mapped");
								return resultMap;
							}
						}
					}
				}
				String htmlOutputFileName = "";
				if ((migrationType.equalsIgnoreCase(Constants.POST_MIGRATION) && "AUDIT".equalsIgnoreCase(subType))
						|| ("PREAUDIT".equalsIgnoreCase(subType)) || ("NESTATUS".equalsIgnoreCase(subType))) {
					String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
					htmlOutputFileName = "_ORAN_AUDIT_" + timeStamp + ".html";
				}

				String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

				List<Map> scriptSeqDetails = (List<Map>) run.get("scripts");
				if (scriptSeqDetails.isEmpty()) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "No Scripts found to run");
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
				int xmlScriptsCount = 0;
				if (programName.contains("4G-USM-LIVE") && ("Audit".equalsIgnoreCase(subType)
						|| "preaudit".equalsIgnoreCase(subType) || "nestatus".equalsIgnoreCase(subType))) {

					for (Map scriptInfoDetails : scriptSeqDetails) {
						String scriptname = scriptInfoDetails.get("scriptName").toString();
						if (FilenameUtils.getExtension(scriptname).equalsIgnoreCase("xml")) {
							xmlScriptsCount++;
						}
					}
					if (scriptSeqDetails.size() != xmlScriptsCount && xmlScriptsCount != 0) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", "Select Usecases containing same type of extension");
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						return resultMap;
					}
				}

				if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

					programId = Integer.parseInt(runTestParams.get("programId").toString());
					Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

					String testname = runTestFormDetails.get("testname").toString();

					if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
						migrationType = "Migration";
					} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
						migrationType = "PostMigration";
					} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
						migrationType = "Premigration";
					}

					if ("precheck".equalsIgnoreCase(subType)) {
						subType = "PreCheck";
					} else if ("commission".equalsIgnoreCase(subType)) {
						subType = "Commission";
					} else if ("postcheck".equalsIgnoreCase(subType)) {
						subType = "PostCheck";
					} else if ("AUDIT".equalsIgnoreCase(subType)) {
						subType = "Audit";
					} else if ("RANATP".equalsIgnoreCase(subType)) {
						subType = "RanATP";
					} else if ("NEGrow".equalsIgnoreCase(subType)) {
						subType = "NEGrow";
					} else if ("PREAUDIT".equalsIgnoreCase(subType)) {
						subType = "PREAUDIT";
					} else if ("NESTATUS".equalsIgnoreCase(subType)) {
						subType = "NESTATUS";
					}

					/*
					 * List<Map> scriptSeqDetails = (List<Map>) run.get("scripts"); ArrayList
					 * scriptIdList = new ArrayList(); int i = 0;
					 * 
					 * for (Map scriptInfoDetails : scriptSeqDetails) { String scriptId =
					 * scriptInfoDetails.get("scriptExeSequence").toString(); if (i == 0) {
					 * scriptIdList.add(scriptId); i = 1; } else if
					 * (scriptIdList.contains(scriptId)) { // break; if (scriptId.equals("0")) {
					 * scriptIdList.add(scriptId); } else { resultMap.put("status", Constants.FAIL);
					 * resultMap.put("reason",
					 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
					 * DUPLICATE_SCRIPT_EXEC_SEQ)); resultMap.put("sessionId", sessionId);
					 * resultMap.put("serviceToken", serviceToken); return resultMap; } } else {
					 * scriptIdList.add(scriptId); } }
					 */

					/*
					 * ArrayList scriptIdList = new ArrayList(); List<LinkedHashMap> useCaseList =
					 * (List) run.get("useCase"); int i = 0; for (Map usecase : useCaseList) {
					 * 
					 * int iUseCaseId = Integer.valueOf(usecase.get("useCaseId").toString());
					 * List<UseCaseBuilderParamEntity> scriptDetails = runTestRepository
					 * .getScriptDetails(iUseCaseId);
					 * 
					 * for (UseCaseBuilderParamEntity scriptInfo : scriptDetails) { Integer scriptId
					 * = scriptInfo.getScriptsDetails().getId(); if(i==0) {
					 * scriptIdList.add(scriptId); i=1; }else if(scriptIdList.contains(scriptId)) {
					 * //break; resultMap.put("status", Constants.FAIL); resultMap.put("reason",
					 * "Script has attached with more than one use case");
					 * resultMap.put("sessionId", sessionId); resultMap.put("serviceToken",
					 * serviceToken); return resultMap; }else { scriptIdList.add(scriptId); } } }
					 */

					String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType, subType,
							testname);
					if (isTestNamePresent != Constants.SUCCESS) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", isTestNamePresent);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						return resultMap;
					}
					List<LinkedHashMap> useCaseList = (List) run.get("useCase");
					ArrayList<String> useList = new ArrayList<>();
					for (Map usecase : useCaseList) {
						useList.add(usecase.get("useCaseName").toString());
					}
					System.out.println(useCaseList);
					if (!(programName.equals("VZN-4G-FSU"))) {
						for (Map usecase : useCaseList) {
							String useCaseName = usecase.get("useCaseName").toString();
							String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
							if (useCaseName.contains("CommissionScriptUsecase") && !useList.contains(rfUseCaseName)) {
								String newUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
								boolean isuseCasePresent = runTestService.getusecaseDetails(programId, migrationType,
										subType, newUseCaseName);
								System.out.println(isuseCasePresent);
								if (!isuseCasePresent) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											"Please complete the RF Scipts execution to execute the commission scripts");
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									return resultMap;
								}
							}

						}
					}
					Map<String, RunTestEntity> runTestEntity = runTestService.insertRunTestDetails(runTestParams, "");
					Set<String> runtestNeidList = runTestEntity.keySet();
					if (runtestNeidList.size() != neList.size()) {
						List<Map> newNeidList = new ArrayList<>();
						for (Map neid : neList) {
							if (runtestNeidList.contains(neid.get("neId"))) {
								newNeidList.add(neid);
							}
						}
						runs.put("neDetails", newNeidList);
						runTestParams.put("runTestFormDetails", runs);
					}
					String result = null;

					if (("Audit".equalsIgnoreCase(subType) || "PREAUDIT".equalsIgnoreCase(subType))
							&& (programName.contains("5G") || xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
						result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity, "CURRENT",
								htmlOutputFileName, null, rfScriptsFlag);
					} else if ("NESTATUS".equalsIgnoreCase(subType)
							&& (programName.contains("5G") || xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
						result = runTestService.getRuntestExecResultNeUp(runTestParams, runTestEntity, "CURRENT",
								htmlOutputFileName, null, rfScriptsFlag);

					} else {
						result = runTestService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
								htmlOutputFileName, null, rfScriptsFlag);
					}
					resultMap.put("runTestEntity", runTestEntity);
					resultMap.put("password", output);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					if (result.equalsIgnoreCase(Constants.SUCCESS)) {
						resultMap.put("status", Constants.SUCCESS);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
					}

				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", enbStatus);
				}
				commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
						Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);
				// bala added
				/*
				 * if("PreCheck".equalsIgnoreCase(subType) &&
				 * XmlCommandsConstants.SCRIFT_FAIL_DETAILS.contains(serviceToken)) {
				 * resultMap.put("failureScriptDetails",
				 * XmlCommandsConstants.SCRIFT_FAIL_DETAILS.get(serviceToken)); }
				 */

			}
		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/runTestWFM")
	public JSONObject runTestWFM(@RequestBody JSONObject runTestParams, String programId2, String migrationSubType,
			String ciqFileName) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		boolean rfScriptsFlag = false;
		String serviceToken = null;
		boolean useCurrPassword;
		boolean Type = false;
		HashMap<String, String> testNames = new HashMap<String, String>();
		LinkedHashMap<String, String> finalResultMap = new LinkedHashMap<String, String>();
		List<Map<String, RunTestEntity>> allrunTestEntity = new LinkedList<Map<String, RunTestEntity>>();
		try {
			Map initialinputJsonMigration = (JSONObject) new JSONParser()
					.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
			if (runTestParams.containsKey("type") && runTestParams.get("type").equals("OV")) {
				initialinputJsonMigration.put("testDesc", "Ran from Automation");
			} else
				initialinputJsonMigration.put("testDesc", "Ran from WFM");
			runTestParams.put("runTestFormDetails", initialinputJsonMigration);
			// runTestParams.put("runTestFormDetails", initialinputJsonMigration);
			Map run1 = (Map) runTestParams.get("runTestFormDetails");
			String programNameDSS = runTestParams.get("programName").toString();
			if (run1.containsKey("prePostAuditFlag") && run1.get("prePostAuditFlag") != null) {
				Type = (boolean) run1.get("prePostAuditFlag");
			}
			String subType = runTestParams.get("migrationSubType").toString();
			String programName = runTestParams.get("programName").toString();
			if ((programNameDSS.equalsIgnoreCase("VZN-5G-DSS") || programNameDSS.equalsIgnoreCase("VZN-5G-CBAND"))
					&& Type == (true)) {

				Map inputJsonMigration = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));

				Map tempJson = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
				List<LinkedHashMap<String, String>> neList = (List<LinkedHashMap<String, String>>) inputJsonMigration
						.get("neDetails");
				List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonMigration
						.get("neDetails");
				List<LinkedHashMap<String, String>> staticneDetails = neDetails;
				for (int s = 0; s < neList.size(); s++) {
					// List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String,
					// String>>) inputJsonMigration
					// .get("neDetails");
					// List<LinkedHashMap<String, String>> staticneDetails = neDetails;
					// Map<String, String> singleNeId = new HashMap<>();
					// LinkedHashMap<String, String> singleNeId = new LinkedHashMap<>();
					List<Map> singleNeId = new ArrayList<>();
					Map<String, String> door = new HashMap<>();
					door = staticneDetails.get(s);
					singleNeId.add(door);
					inputJsonMigration.put("neDetails", singleNeId);
					String enodebName = door.get("neName").toString();
					String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
							.format(new Timestamp(System.currentTimeMillis()));
					String testName = "WFM_" + enodebName + "_" + timeStampDate;

					testNames.put(door.get("neId").toString(), testName);
					inputJsonMigration.put("testname", testName);

					runTestParams.put("runTestFormDetails", inputJsonMigration);

					if (!runTestParams.get("migrationType").toString().equalsIgnoreCase("postmigration")) {
						if (!(s == 0)) {
							runTestParams.put("runTestFormDetails", tempJson);

						}
					}
					sessionId = runTestParams.get("sessionId").toString();
					serviceToken = runTestParams.get("serviceToken").toString();

					Map run = (Map) runTestParams.get("runTestFormDetails");
					useCurrPassword = (boolean) run.get("currentPassword");
					String sanePassword = run.get("password").toString();

					String migrationType = runTestParams.get("migrationType").toString();
					if (migrationType.equals("migration")) {
						rfScriptsFlag = (boolean) run.get("rfScriptFlag");
					}
					if (migrationType.equalsIgnoreCase("PreMigration")) {
						migrationType = "Premigration";
					}
					subType = runTestParams.get("migrationSubType").toString();
					programName = runTestParams.get("programName").toString();

					expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

					if (expiryDetails != null) {
						return expiryDetails;
					}

					String enbId = null;
					String enbName = null;

					// !jsonObj.get("itemName").toString().isEmpty())
					LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();
					Map<String, String> neMap = new HashMap<>();
					neMap = neList.get(s);
					enbId = neMap.get("neId").toString();
					enbName = neMap.get("neName").toString();
					if (!migrationType.equalsIgnoreCase("postmigration")) {
						if (!ObjectUtils.isEmpty(GlobalStatusMap.WFM_PRE_MIG_USECASES)
								&& GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + enbId)) {
							ConcurrentHashMap<String, WorkFlowManagementPremigration> WFM_PRE_MIG_USECASES = GlobalStatusMap.WFM_PRE_MIG_USECASES;
							List<Map> useCaseList = runTestService.getMigrationUseCaseListWFM(
									Integer.valueOf(programId2), migrationType, migrationSubType, ciqFileName, enbId,
									programName);
							WorkFlowManagementPremigration generated_usecases = WFM_PRE_MIG_USECASES
									.get(sessionId + enbId);
							List<Map> finalList = new ArrayList<>();
							for (Map usecase : useCaseList) {
								if (generated_usecases.getUsecases().contains(usecase.get("useCaseName"))) {
									finalList.add(usecase);
								}
							}
							// Map inputJsonMigration = (JSONObject) new JSONParser()
							// .parse(runTestParams.toJSONString((Map)
							// runTestParams.get("runTestFormDetails")));
							Map inputJsonMigration1 = (JSONObject) new JSONParser()
									.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
							List<String> inputusecases = (List<String>) inputJsonMigration1.get("useCase");
							List<Map> migrationUseCases = new ArrayList<>();
							for (String inputusecase : inputusecases) {
								for (Map map : finalList) {
									if (map.get("useCaseName").toString().contains(inputusecase.toString())) {
										Map migMap = new HashMap();
										migrationUseCases.add(map);
									}
								}
							}
							JSONObject json = new JSONObject();

							// logic for adjusting the usecases
							Map<String, List<Map>> objScriptList = new LinkedHashMap<String, List<Map>>();
							Date today1 = new Date();
							String time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();

							logger.error("Forming usecase Started at:" + time);
							objScriptList = workFlowManagementService.formUseCases(migrationUseCases);
							Date today2 = new Date();
							String time1 = today2.getHours() + ":" + today2.getMinutes() + ":" + today2.getSeconds();

							logger.error("forming usecases completed at:" + time1);
							List<Map> finaluseCases = objScriptList.get("useCase");
							List<Map> finalScripts = objScriptList.get("scripts");
							// System.out.println("migration initial input:" + runTestParams);

							inputJsonMigration.put("useCase", finaluseCases);
							inputJsonMigration.put("scripts", finalScripts);
							runTestParams.put("runTestFormDetails", inputJsonMigration);
							// System.out.println("finalusecases:" + runTestParams);
						}
					}
					JSONObject migrationInputs = null;
					JSONObject postmigrationInputs = null;
					List<Map<String, String>> enbList = null;
					sessionId = runTestParams.get("sessionId").toString();
					sanePassword = run1.get("password").toString();
					useCurrPassword = (boolean) run1.get("currentPassword");
					int programId = Integer.parseInt(runTestParams.get("programId").toString());
					JSONObject runTestFormDetails1 = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));

					// runTestFormDetails.
					Map<String, RunTestEntity> runTestEntity1 = null;

					if (runTestParams.get("migrationType").equals("migration")) {
						migrationInputs = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) runTestParams));
					}
					JSONObject list3 = (JSONObject) new JSONParser()
							.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
					// LinkedHashMap<String, String> list3 = new LinkedHashMap<String, String>();
					// list3 = (LinkedHashMap<String, String>)
					// runTestParams.get("runTestFormDetails");
					// Map enbList1 =( Map) runTestParams.get("neDetails");
					// String enbName = ((Map) enbList1.get(0)).get("neName").toString();
					runTestFormDetails1.remove("scripts");
					List<String> neList1 = new ArrayList<>();
					List<Map> neList2 = (List<Map>) run1.get("neDetails");

					String i = neMap.get("neId").toString();
					enodebName = neMap.get("neName").toString();
					NetworkConfigEntity neMappingEntitiesForVersion1 = null;
					NeMappingModel neMappingModelversion1 = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntityversion1 = new CustomerDetailsEntity();
					programDetailsEntityversion1.setId(programId);
					neMappingModelversion1.setProgramDetailsEntity(programDetailsEntityversion1);
					neMappingModelversion1.setEnbId(i);
					NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
							.getNetWorkEntityDetails(neMappingModelversion1);
					String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
					if (!lsmVersion.contains("21.B")) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", "Selected NeId is not map to 21.B");
						return resultMap;

					}

					neList1.add(i);

					List<Map> useCaseLst = null;
					List<Map> scripts = null;
					// if(migrationType.equalsIgnoreCase("PostMigration") &&
					// migrationSubType.equalsIgnoreCase("Audit")){

					// neList1 = (List) run.get("neDetails");

					migrationType = "postmigration";
					migrationSubType = "AUDIT";
					useCaseLst = runTestService.getPostMigrationUseCaseList(programId, migrationType, migrationSubType,
							neList1);
					runTestParams.put("migrationSubType", "AUDIT");
					runTestParams.put("migrationType", "postmigration");
					Map<String, String> mapDetails = new HashMap<>();

					timeStampDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
					String lsmVersion1 = (String) runTestFormDetails1.get("lsmVersion1");
					// testName = "WFM_" + enodebName + "_" + timeStampDate;
					List<Map<String, String>> listDetails = new ArrayList<>();
					List<Map<String, String>> ScriptDetails = new ArrayList<>();
					for (Map usecase : useCaseLst) {
						if (usecase.get("useCaseName").equals("eNB_PreAudit")) {
							String usecaseName = (String) usecase.get("useCaseName");
							mapDetails.put("useCaseName", usecaseName);
							int useCaseId = (int) usecase.get("useCaseId");
							String usID = String.valueOf(useCaseId);
							mapDetails.put("useCaseId", usID);
							int executionSequence = (int) usecase.get("executionSequence");
							String exeSeq = String.valueOf(executionSequence);
							mapDetails.put("executionSequence", exeSeq);
							String ucSleepInterval = (String) usecase.get("ucSleepInterval");
							mapDetails.put("ucSleepInterval", ucSleepInterval);
							listDetails.add(mapDetails);
							runTestFormDetails1.put("useCase", listDetails);
							scripts = (List<Map>) usecase.get("scripts");

							for (Map Script : scripts) {
								Map<String, String> mapDetailsScript = new HashMap<>();
								String scriptName = (String) Script.get("scriptName");
								mapDetailsScript.put("scriptName", scriptName);
								mapDetailsScript.put("useCaseName", usecaseName);
								int scriptId = (int) Script.get("scriptId");
								String scriptID = String.valueOf(scriptId);
								mapDetailsScript.put("scriptId", scriptID);
								String scriptSleepInterval = (String) Script.get("scriptSleepInterval");
								String scriptsleepID = String.valueOf(scriptSleepInterval);
								mapDetailsScript.put("scriptSleepInterval", scriptsleepID);
								String useGeneratedScript = (String) Script.get("useGeneratedScript");
								mapDetailsScript.put("useGeneratedScript", useGeneratedScript);
								int scriptExeSequence = (int) Script.get("scriptExeSequence");
								String scripexeseqID = String.valueOf(scriptExeSequence);
								mapDetailsScript.put("scriptExeSequence", scripexeseqID);
								ScriptDetails.add(mapDetailsScript);

							}
							runTestFormDetails1.put("scripts", ScriptDetails);
							// if(!(runTestFormDetails1.get("testname").equals(""))
							// runTestFormDetails1.put("lsmId", "");
							// runTestFormDetails1.put("lsmVersion",lsmVersion1 );
							runTestFormDetails1.put("testname", "Pre-Audit");
						}
					}
					runTestParams.put("runTestFormDetails", runTestFormDetails1);
					if (runTestParams.get("migrationType").equals("postmigration")) {
						postmigrationInputs = (JSONObject) new JSONParser()
								.parse(runTestParams.toJSONString((Map) runTestParams));
					}
					List<Map<String, String>> RunTestParams = new ArrayList<>();
					RunTestParams.add(postmigrationInputs);
					RunTestParams.add(migrationInputs);
					// RunTestParams.add(postmigrationInputs);
					Map<String, Object> auditoutput = new HashMap<>();
					// to run the PMP
					int call = 0;
					for (int j = 0; j < RunTestParams.size(); j++) {
						runTestParams = (JSONObject) (RunTestParams.get(j));
						if (j == 0) {
							call = j;
							String testname = "PreEnbAudit" + "_" + timeStampDate;

							Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");
							runTestFormDetails.put("testname", testname);
							runTestParams.put("runTestFormDetails", runTestFormDetails);
						}
						if (j == 1) {

							runTestParams.put("runTestFormDetails", list3);
							Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");
							runTestFormDetails.put("testname", testName);
							runTestParams.put("runTestFormDetails", runTestFormDetails);
							call = j;
						}
						run1 = (Map) runTestParams.get("runTestFormDetails");
						migrationType = runTestParams.get("migrationType").toString();
						if (migrationType.equals("migration")) {
							rfScriptsFlag = (boolean) run1.get("rfScriptFlag");
						}
						if (migrationType.equalsIgnoreCase("PreMigration")) {
							migrationType = "Premigration";
						}
						subType = runTestParams.get("migrationSubType").toString();
						expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

						if (expiryDetails != null) {
							return expiryDetails;
						}

						JSONObject output = new JSONObject();

						if (useCurrPassword == false && sanePassword.isEmpty()) {

							output = runTestService.getSaneDetailsforPassword(runTestParams);

							if (!output.isEmpty()) {
								resultMap.put("status", "PROMPT");
								resultMap.put("password", output);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("requestType", "RUN_TEST");

								return resultMap;
							}
						}

						int programIds = Integer.parseInt(runTestParams.get("programId").toString());
						List<Map> neList4 = (List<Map>) run1.get("neDetails");
						Map runs = (Map) runTestParams.get("runTestFormDetails");
						if (!runs.get("lsmVersion").toString().isEmpty()
								|| migrationType.equalsIgnoreCase("postmigration")) {
							int count = 0;
							NetworkConfigEntity previousNetworkConfigEntity = null;
							for (Map neid : neList4) {
								NeMappingModel neMappingModelversion = new NeMappingModel();
								CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
								programDetailsEntityversion.setId(programIds);
								neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
								neMappingModelversion.setEnbId(neid.get("neId").toString());
								neMappingEntitiesForVersion = runTestRepository
										.getNetWorkEntityDetails(neMappingModelversion);
								if (migrationType.equalsIgnoreCase("postmigration")) {
									if (count == 0) {
										previousNetworkConfigEntity = neMappingEntitiesForVersion;
									} else {
										if (previousNetworkConfigEntity != null && neMappingEntitiesForVersion != null
												&& (!previousNetworkConfigEntity.getId()
														.equals(neMappingEntitiesForVersion.getId()))) {
											resultMap.put("status", Constants.FAIL);
											resultMap.put("sessionId", sessionId);
											resultMap.put("serviceToken", serviceToken);
											resultMap.put("reason",
													"Selected Sites don't belong to the same SM Version");
											return resultMap;
										}
										previousNetworkConfigEntity = neMappingEntitiesForVersion;
									}
									count++;
								}
								if (neMappingEntitiesForVersion != null) {
									lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
									if (!lsmVersion.equals(runs.get("lsmVersion"))
											&& !runs.get("lsmVersion").toString().isEmpty()) {
										resultMap.put("status", Constants.FAIL);
										resultMap.put("sessionId", sessionId);
										resultMap.put("serviceToken", serviceToken);
										resultMap.put("reason",
												"Selected Sites don't belong to the selected SM Version");
										return resultMap;
									}
								} else {
									if (runs.get("lsmVersion").toString().isEmpty()
											|| run1.get("lsmName").toString().isEmpty()) {
										resultMap.put("status", Constants.FAIL);
										resultMap.put("sessionId", sessionId);
										resultMap.put("serviceToken", serviceToken);
										resultMap.put("reason", "NE is not Mapped");
										return resultMap;
									}
								}
							}
						}
						String htmlOutputFileName = "";
						if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)
								&& "AUDIT".equalsIgnoreCase(subType)) {
							String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
							htmlOutputFileName = "_ORAN_AUDIT_" + timeStamp + ".html";
						}

						String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

						List<Map> scriptSeqDetails = (List<Map>) run1.get("scripts");
						if (scriptSeqDetails.isEmpty()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "No Scripts found to run");
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							return resultMap;
						}
						int xmlScriptsCount = 0;
						if (programName.contains("4G-USM-LIVE") && "Audit".equalsIgnoreCase(subType)) {

							for (Map scriptInfoDetails : scriptSeqDetails) {
								String scriptname = scriptInfoDetails.get("scriptName").toString();
								if (FilenameUtils.getExtension(scriptname).equalsIgnoreCase("xml")) {
									xmlScriptsCount++;
								}
							}
							if (scriptSeqDetails.size() != xmlScriptsCount && xmlScriptsCount != 0) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Select Usecases containing same type of extension");
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								return resultMap;
							}
						}
						int time = 0;

						while (!enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {
							Thread.sleep(5000);
							time++;
							enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);
							if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {
								break;
							} else if (time == 12) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "Script is taking longer time to execute then usual");
								return resultMap;
							}
						}
						if (call == 0) {
							if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

								programId = Integer.parseInt(runTestParams.get("programId").toString());
								Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

								String testname = runTestFormDetails.get("testname").toString();

								if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
									migrationType = "Migration";
								} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
									migrationType = "PostMigration";
								} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
									migrationType = "Premigration";
								}

								if ("precheck".equalsIgnoreCase(subType)) {
									subType = "PreCheck";
								} else if ("commission".equalsIgnoreCase(subType)) {
									subType = "Commission";
								} else if ("postcheck".equalsIgnoreCase(subType)) {
									subType = "PostCheck";
								} else if ("AUDIT".equalsIgnoreCase(subType)) {
									subType = "Audit";
								} else if ("RANATP".equalsIgnoreCase(subType)) {
									subType = "RanATP";
								} else if ("NEGrow".equalsIgnoreCase(subType)) {
									subType = "NEGrow";
								}

								String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType,
										subType, testname);
								if (isTestNamePresent != Constants.SUCCESS) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", isTestNamePresent);
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									return resultMap;
								}
								List<LinkedHashMap> useCaseList = (List) run1.get("useCase");
								ArrayList<String> useList = new ArrayList<>();
								for (Map usecase : useCaseList) {
									useList.add(usecase.get("useCaseName").toString());
								}
								System.out.println(useCaseList);
								if (!(programName.equals("VZN-4G-FSU"))) {
									for (Map usecase : useCaseList) {
										String useCaseName = usecase.get("useCaseName").toString();
										String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase",
												"RFUsecase");
										if (useCaseName.contains("CommissionScriptUsecase")
												&& !useList.contains(rfUseCaseName)) {
											String newUseCaseName = useCaseName.replace("CommissionScriptUsecase",
													"RFUsecase");
											boolean isuseCasePresent = runTestService.getusecaseDetails(programId,
													migrationType, subType, newUseCaseName);
											System.out.println(isuseCasePresent);
											if (!isuseCasePresent) {
												resultMap.put("status", Constants.FAIL);
												resultMap.put("reason",
														"Please complete the RF Scipts execution to execute the commission scripts");
												resultMap.put("sessionId", sessionId);
												resultMap.put("serviceToken", serviceToken);
												return resultMap;
											}
										}

									}
								}

								Map<String, RunTestEntity> runTestEntity = runTestService
										.insertRunTestDetails(runTestParams, "");
								Set<String> runtestNeidList = runTestEntity.keySet();
								if (runtestNeidList.size() != neList.size()) {
									List<Map> newNeidList = new ArrayList<>();
									for (Map neid : neList) {
										if (runtestNeidList.contains(neid.get("neId"))) {
											newNeidList.add(neid);
										}
									}
									runs.put("neDetails", newNeidList);
									runTestParams.put("runTestFormDetails", runs);
								}
								String result = null;

								if ("Audit".equalsIgnoreCase(subType) && (programName.contains("5G")
										|| xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
									result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity,
											"CURRENT", htmlOutputFileName, null, rfScriptsFlag);

								} else {
									result = runTestService.getRuntestExecResult(runTestParams, runTestEntity,
											"CURRENT", htmlOutputFileName, null, rfScriptsFlag);
								}
								resultMap.put("runTestEntity", runTestEntity);
								resultMap.put("password", output);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								if (result.equalsIgnoreCase(Constants.SUCCESS)) {
									resultMap.put("status", Constants.SUCCESS);
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
								}

							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", enbStatus);
							}
						} else if (call == 1) {
							if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

								programId = Integer.parseInt(runTestParams.get("programId").toString());
								Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

								String testname = runTestFormDetails.get("testname").toString();

								if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
									migrationType = "Migration";
								} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
									migrationType = "PostMigration";
								} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
									migrationType = "Premigration";
								}

								if ("precheck".equalsIgnoreCase(subType)) {
									subType = "PreCheck";
								} else if ("commission".equalsIgnoreCase(subType)) {
									subType = "Commission";
								} else if ("postcheck".equalsIgnoreCase(subType)) {
									subType = "PostCheck";
								} else if ("AUDIT".equalsIgnoreCase(subType)) {
									subType = "Audit";
								} else if ("RANATP".equalsIgnoreCase(subType)) {
									subType = "RanATP";
								} else if ("NEGrow".equalsIgnoreCase(subType)) {
									subType = "NEGrow";
								}

								String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType,
										subType, testname);
								if (isTestNamePresent != Constants.SUCCESS) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", isTestNamePresent);
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									return resultMap;
								}
								List<LinkedHashMap> useCaseList = (List) run1.get("useCase");
								ArrayList<String> useList = new ArrayList<>();
								for (Map usecase : useCaseList) {
									useList.add(usecase.get("useCaseName").toString());
								}
								System.out.println(useCaseList);
								if (!(programName.equals("VZN-4G-FSU"))) {
									for (Map usecase : useCaseList) {
										String useCaseName = usecase.get("useCaseName").toString();
										String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase",
												"RFUsecase");
										if (useCaseName.contains("CommissionScriptUsecase")
												&& !useList.contains(rfUseCaseName)) {
											String newUseCaseName = useCaseName.replace("CommissionScriptUsecase",
													"RFUsecase");
											boolean isuseCasePresent = runTestService.getusecaseDetails(programId,
													migrationType, subType, newUseCaseName);
											System.out.println(isuseCasePresent);
											if (!isuseCasePresent) {
												resultMap.put("status", Constants.FAIL);
												resultMap.put("reason",
														"Please complete the RF Scipts execution to execute the commission scripts");
												resultMap.put("sessionId", sessionId);
												resultMap.put("serviceToken", serviceToken);
												return resultMap;
											}
										}

									}
								}
								runTestEntity1 = runTestService.insertRunTestDetails(runTestParams, "");
								Set<String> runtestNeidList = runTestEntity1.keySet();
								if (runtestNeidList.size() != neList.size()) {
									List<Map> newNeidList = new ArrayList<>();
									for (Map neid : neList) {
										if (runtestNeidList.contains(neid.get("neId"))) {
											newNeidList.add(neid);
										}
									}
									runs.put("neDetails", newNeidList);
									runTestParams.put("runTestFormDetails", runs);
								}
								String result = null;
								allrunTestEntity.add(runTestEntity1);
								if ("Audit".equalsIgnoreCase(subType) && (programName.contains("5G")
										|| xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
									/*
									 * JSONObject resultAudit =
									 * runTestService.getRuntestExecResult5GDSS(runTestParams, runTestEntity1,
									 * "CURRENT", htmlOutputFileName, null, rfScriptsFlag, auditoutput); result =
									 * (String) resultAudit.get("Status");
									 */
									result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity1,
											"CURRENT", htmlOutputFileName, null, rfScriptsFlag);

								} else {
									result = runTestService.getRuntestExecResult(runTestParams, runTestEntity1,
											"CURRENT", htmlOutputFileName, null, rfScriptsFlag);
								}
								finalResultMap.put(enbId, result);
								resultMap.put("testnames", testNames);
								resultMap.put("runTestEntity", runTestEntity1);
								resultMap.put("password", output);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								if (result.equalsIgnoreCase(Constants.SUCCESS)) {
									resultMap.put("status", Constants.SUCCESS);
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
								}

							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", enbStatus);
							}
						}

						commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
								Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);

					}

				} // closing of for loop

			} else {
				Map inputJsonMigration = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));

				Map tempJson = (JSONObject) new JSONParser()
						.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
				List<LinkedHashMap<String, String>> neList = (List<LinkedHashMap<String, String>>) inputJsonMigration
						.get("neDetails");
				List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) inputJsonMigration
						.get("neDetails");
				List<LinkedHashMap<String, String>> staticneDetails = neDetails;
				for (int s = 0; s < neList.size(); s++) {
					// List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String,
					// String>>) inputJsonMigration
					// .get("neDetails");
					// List<LinkedHashMap<String, String>> staticneDetails = neDetails;
					// Map<String, String> singleNeId = new HashMap<>();
					// LinkedHashMap<String, String> singleNeId = new LinkedHashMap<>();
					List<Map> singleNeId = new ArrayList<>();
					Map<String, String> door = new HashMap<>();
					door = staticneDetails.get(s);
					singleNeId.add(door);
					inputJsonMigration.put("neDetails", singleNeId);
					String enodebName = door.get("neName").toString();
					String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
							.format(new Timestamp(System.currentTimeMillis()));
					String testName = "WFM_" + enodebName + "_" + timeStampDate;

					testNames.put(door.get("neId").toString(), testName);
					inputJsonMigration.put("testname", testName);

					runTestParams.put("runTestFormDetails", inputJsonMigration);

					if (!runTestParams.get("migrationType").toString().equalsIgnoreCase("postmigration")
							&& !runTestParams.get("migrationSubType").toString().equalsIgnoreCase("PREAUDIT")
							&& !runTestParams.get("migrationSubType").toString().equalsIgnoreCase("NESTATUS")) {
						if (!(s == 0)) {
							runTestParams.put("runTestFormDetails", tempJson);

						}
					}
					sessionId = runTestParams.get("sessionId").toString();
					serviceToken = runTestParams.get("serviceToken").toString();

					Map run = (Map) runTestParams.get("runTestFormDetails");
					useCurrPassword = (boolean) run.get("currentPassword");
					String sanePassword = run.get("password").toString();

					String migrationType = runTestParams.get("migrationType").toString();
					if (migrationType.equals("migration")) {
						rfScriptsFlag = (boolean) run.get("rfScriptFlag");
					}
					if (migrationType.equalsIgnoreCase("PreMigration")) {
						migrationType = "Premigration";
					}
					subType = runTestParams.get("migrationSubType").toString();
					programName = runTestParams.get("programName").toString();
					if (subType.equalsIgnoreCase("preaudit")) {
						subType = "PREAUDIT";
					}
					if (subType.equalsIgnoreCase("NESTATUS")) {
						subType = "NESTATUS";
					}
					expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

					if (expiryDetails != null) {
						return expiryDetails;
					}
					// for wfm handling of multiple usecases
					// Map tempJson = (JSONObject) new JSONParser()
					// .parse(runTestParams.toJSONString((Map)
					// runTestParams.get("runTestFormDetails")));
					String enbId = null;
					String enbName = null;
					// Map inputJsonMigration = (JSONObject) new JSONParser()
					// .parse(runTestParams.toJSONString((Map)
					// runTestParams.get("runTestFormDetails")));
					// List<LinkedHashMap<String, String>> neList = (List<LinkedHashMap<String,
					// String>>) inputJsonMigration
					// .get("neDetails");
					// for (int s = 0; s < neList.size(); s++) {
					// if(!(s==0))
					// {
					// runTestParams.put("runTestFormDetails", tempJson);
					//
					// }
					// JSONObject jsonObj = (JSONObject) neList.get(s);
					// if(null!=jsonObj.get("itemName") &&
					// !jsonObj.get("itemName").toString().isEmpty())
					LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();
					Map<String, String> neMap = new HashMap<>();
					neMap = neList.get(s);
					enbId = neMap.get("neId").toString();
					enbName = neMap.get("neName").toString();
					if (!migrationType.equalsIgnoreCase("postmigration")
							&& !(migrationType.equalsIgnoreCase("premigration") && subType.equalsIgnoreCase("PREAUDIT"))
							&& !(migrationType.equalsIgnoreCase("premigration")
									&& subType.equalsIgnoreCase("NESTATUS"))) {
						if (!ObjectUtils.isEmpty(GlobalStatusMap.WFM_PRE_MIG_USECASES)
								&& GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + enbId)) {
							ConcurrentHashMap<String, WorkFlowManagementPremigration> WFM_PRE_MIG_USECASES = GlobalStatusMap.WFM_PRE_MIG_USECASES;
							logger.error("DB LOGS:Before getMigrationUseCaseListWFM from db  : "+"NE ID : "+enbId);
							List<Map> useCaseList = runTestService.getMigrationUseCaseListWFM(
									Integer.valueOf(programId2), migrationType, migrationSubType, ciqFileName, enbId,
									programName);
							logger.error("DB LOGS:After getMigrationUseCaseListWFM from db  : "+"NE ID : "+enbId);
							WorkFlowManagementPremigration generated_usecases = WFM_PRE_MIG_USECASES
									.get(sessionId + enbId);
							List<Map> finalList = new ArrayList<>();
							for (Map usecase : useCaseList) {
								if (generated_usecases.getUsecases().contains(usecase.get("useCaseName"))) {
									finalList.add(usecase);
								}
							}
							// Map inputJsonMigration = (JSONObject) new JSONParser()
							// .parse(runTestParams.toJSONString((Map)
							// runTestParams.get("runTestFormDetails")));
							Map inputJsonMigration1 = (JSONObject) new JSONParser()
									.parse(runTestParams.toJSONString((Map) runTestParams.get("runTestFormDetails")));
							List<String> inputusecases = (List<String>) inputJsonMigration1.get("useCase");
							List<Map> migrationUseCases = new ArrayList<>();
							for (String inputusecase : inputusecases) {
								for (Map map : finalList) {
									if (map.get("useCaseName").toString().contains(inputusecase.toString())) {
										Map migMap = new HashMap();
										migrationUseCases.add(map);
									}
								}
							}
							JSONObject json = new JSONObject();

							// logic for adjusting the usecases
							Map<String, List<Map>> objScriptList = new LinkedHashMap<String, List<Map>>();
							objScriptList = workFlowManagementService.formUseCases(migrationUseCases);
							List<Map> finaluseCases = objScriptList.get("useCase");
							List<Map> finalScripts = objScriptList.get("scripts");
							// System.out.println("migration initial input:" + runTestParams);

							inputJsonMigration.put("useCase", finaluseCases);
							inputJsonMigration.put("scripts", finalScripts);
							runTestParams.put("runTestFormDetails", inputJsonMigration);
							// System.out.println("finalusecases:" + runTestParams);
						}
					}

					JSONObject output = new JSONObject();
					if (useCurrPassword == false && sanePassword.isEmpty()) {

						output = runTestService.getSaneDetailsforPassword(runTestParams);

						if (!output.isEmpty()) {
							resultMap.put("status", "PROMPT");
							resultMap.put("password", output);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("requestType", "RUN_TEST");

							return resultMap;
						}
					}

					String htmlOutputFileName = "";
					if ((migrationType.equalsIgnoreCase(Constants.POST_MIGRATION) && "AUDIT".equalsIgnoreCase(subType))
							|| (migrationType.equalsIgnoreCase("premigration") && subType.equalsIgnoreCase("PREAUDIT"))
							|| (migrationType.equalsIgnoreCase("premigration")
									&& subType.equalsIgnoreCase("NESTATUS"))) {
						String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
						htmlOutputFileName = "_ORAN_AUDIT_" + timeStamp + ".html";
					}

					String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

					if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

						int programId = Integer.parseInt(runTestParams.get("programId").toString());
						Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

						String testname = runTestFormDetails.get("testname").toString();

						if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
							migrationType = "Migration";
						} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
							migrationType = "PostMigration";
						} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
							migrationType = "Premigration";
						}

						if ("precheck".equalsIgnoreCase(subType)) {
							subType = "PreCheck";
						} else if ("commission".equalsIgnoreCase(subType)) {
							subType = "Commission";
						} else if ("postcheck".equalsIgnoreCase(subType)) {
							subType = "PostCheck";
						} else if ("AUDIT".equalsIgnoreCase(subType)) {
							subType = "Audit";
						} else if ("RANATP".equalsIgnoreCase(subType)) {
							subType = "RanATP";
						} else if ("NEGrow".equalsIgnoreCase(subType)) {
							subType = "NEGrow";
						} else if ("PREAUDIT".equalsIgnoreCase(subType)) {
							subType = "PREAUDIT";
						} else if ("NESTATUS".equalsIgnoreCase(subType)) {
							subType = "NESTATUS";
						}

						// List<Map> scriptSeqDetails = (List<Map>) run.get("scripts");
						List<Map> scriptSeqDetails = (List<Map>) inputJsonMigration.get("scripts");

						ArrayList scriptIdList = new ArrayList();
						int i = 0;

						if (scriptSeqDetails.isEmpty()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "No Scripts found to run");
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							return resultMap;
						}
						int xmlScriptsCount = 0;
						if (programName.contains("4G-USM-LIVE") && ("Audit".equalsIgnoreCase(subType)
								|| "NESTATUS".equalsIgnoreCase(subType) || "PREAUDIT".equalsIgnoreCase(subType))) {

							for (Map scriptInfoDetails : scriptSeqDetails) {
								String scriptname = scriptInfoDetails.get("scriptName").toString();
								if (FilenameUtils.getExtension(scriptname).equalsIgnoreCase("xml")) {
									xmlScriptsCount++;
								}
							}
							if (scriptSeqDetails.size() != xmlScriptsCount && xmlScriptsCount != 0) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", "Select Usecases containing same type of extension");
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								return resultMap;
							}
						}
						for (Map scriptInfoDetails : scriptSeqDetails) {
							String scriptId = scriptInfoDetails.get("scriptExeSequence").toString();
							if (i == 0) {
								scriptIdList.add(scriptId);
								i = 1;
							} else if (scriptIdList.contains(scriptId)) {
								// break;
								if (scriptId.equals("0")) {
									scriptIdList.add(scriptId);
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason", GlobalInitializerListener.faultCodeMap
											.get(FaultCodes.DUPLICATE_SCRIPT_EXEC_SEQ));
									resultMap.put("sessionId", sessionId);
									resultMap.put("serviceToken", serviceToken);
									return resultMap;
								}
							} else {
								scriptIdList.add(scriptId);
							}
						}

						// String isTestNamePresent = runTestService.getRunTestEntity(programId,
						// migrationType, subType,
						// testname);
						// if (isTestNamePresent != Constants.SUCCESS) {
						// resultMap.put("status", Constants.FAIL);
						// resultMap.put("reason", isTestNamePresent);
						// resultMap.put("sessionId", sessionId);
						// resultMap.put("serviceToken", serviceToken);
						// return resultMap;
						// }
						//logger
						Map run123134 = (Map) runTestParams.get("runTestFormDetails");
						List<Map> neList13123132 = (List<Map>) run.get("neDetails");
						  logger.error("DB LOGS:Before Creating Rows from individual page NE List : "+neList13123132);
						Map<String, RunTestEntity> runTestEntity = runTestService.insertRunTestDetails(runTestParams,
								"");
						 logger.error("DB LOGS:After Creating Rows from individual page NE List : "+neList13123132);
						allrunTestEntity.add(runTestEntity);
						String result = null;

						if (("Audit".equalsIgnoreCase(subType) || "PREAUDIT".equalsIgnoreCase(subType))
								&& (programName.contains("5G") || xmlScriptsCount != 0
										|| programName.contains("4G-FSU"))) {
							result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity, "CURRENT",
									htmlOutputFileName, null, rfScriptsFlag);
						} else if ("NESTATUS".equalsIgnoreCase(subType) && (programName.contains("5G")
								|| xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
							result = runTestService.getRuntestExecResultNeUp(runTestParams, runTestEntity, "CURRENT",
									htmlOutputFileName, null, rfScriptsFlag);

						} else {
							result = runTestService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
									htmlOutputFileName, null, rfScriptsFlag);
						}
						finalResultMap.put(enbId, result);
						resultMap.put("testnames", testNames);
						resultMap.put("runTestEntity", runTestEntity);
						resultMap.put("password", output);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						if (result.equalsIgnoreCase(Constants.SUCCESS)) {
							resultMap.put("status", Constants.SUCCESS);
						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
						}

					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("reason", enbStatus);
					}
					commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
							Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);

				} // closing of for loop

			} // need to call the next call in this thread here

		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.error("Exception in runTestWFM() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.error("Exception in runTestWFM() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		resultMap.put("allrunTestEntity", allrunTestEntity);
		resultMap.put("combinedresult", finalResultMap);
		return resultMap;

	}

	/**
	 * This method will execute the Runtest do
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/reRunContinueTest")
	public JSONObject runReTest(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		boolean rfScriptsFlag = false;
		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");

			JSONObject runTestParams = runTestService.getInputRuntestJson(runTestId);
			RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
			if (!ObjectUtils.isEmpty(runTestParams)) {
				Map run = (Map) runTestParams.get("runTestFormDetails");
				useCurrPassword = (boolean) run.get("currentPassword");
				String sanePassword = run.get("password").toString();
				// rfScriptsFlag = (boolean) run.get("rfScriptFlag");

				String migrationType = runTestParams.get("migrationType").toString();
				if (migrationType.equals("migration")) {
					rfScriptsFlag = (boolean) run.get("rfScriptFlag");
				}
				String subType = runTestParams.get("migrationSubType").toString();
				if (migrationType.equalsIgnoreCase("PreMigration")) {
					migrationType = "Premigration";
				}

				JSONObject output = new JSONObject();

				String htmlOutputFileName = "";

				String enbStatus = runTestService.getRunTestEnbProgressStatus(runtestEntity);

				if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

					int programId = Integer.parseInt(runTestParams.get("programId").toString());
					Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

					String testname = runTestFormDetails.get("testname").toString();

					if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
						migrationType = "Migration";
					} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
						migrationType = "PostMigration";
					} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
						migrationType = "Premigration";
					}

					if ("precheck".equalsIgnoreCase(subType)) {
						subType = "PreCheck";
					} else if ("commission".equalsIgnoreCase(subType)) {
						subType = "Commission";
					} else if ("postcheck".equalsIgnoreCase(subType)) {
						subType = "PostCheck";
					} else if ("AUDIT".equalsIgnoreCase(subType)) {
						subType = "Audit";
					} else if ("RANATP".equalsIgnoreCase(subType)) {
						subType = "RanATP";
					} else if ("NEGrow".equalsIgnoreCase(subType)) {
						subType = "NEGrow";
					}

					String skipScriptIds = reRunTestParams.get("skipScriptIds").toString();

					Map<String, Object> reRunDetails = new LinkedHashMap<>();

					List<String> useCaseScriptIds = new ArrayList<>();

					if (StringUtils.isNotEmpty(skipScriptIds)) {
						useCaseScriptIds = Arrays.asList(skipScriptIds.split(","));
					}

					reRunDetails.put("skipIds", useCaseScriptIds);
					// List<Integer> objSkipList=(List<Integer>)reRunDetails.get("skipIds");
					String runType = reRunTestParams.get("runType").toString();

					Map<String, RunTestEntity> runTestEntity = runTestService.getRunTestDetailsMap(runTestParams,
							runTestId);

					String result = runTestService.getRuntestExecResult(runTestParams, runTestEntity, runType,
							htmlOutputFileName, reRunDetails, rfScriptsFlag);

					resultMap.put("password", output);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					if (result.equalsIgnoreCase(Constants.SUCCESS)) {
						resultMap.put("status", Constants.SUCCESS);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
					}

				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", enbStatus);
				}
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Some Problem in Input");
			}
			/*
			 * commonUtil.saveAudit(Constants.EVENT_MIGRATION,
			 * Constants.EVENT_MIGRATION_RUN_TEST, Constants.ACTION_EXECUTE,
			 * "Run Test Executed Successfully", sessionId);
			 */
			// bala added
			/*
			 * if("PreCheck".equalsIgnoreCase(subType) &&
			 * XmlCommandsConstants.SCRIFT_FAIL_DETAILS.contains(serviceToken)) {
			 * resultMap.put("failureScriptDetails",
			 * XmlCommandsConstants.SCRIFT_FAIL_DETAILS.get(serviceToken)); }
			 */
		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/retryMilestoneUpdate")
	public JSONObject retryMilestoneUpdate(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		String date2 = DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");

		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			// String[] RetryMilestone=new String[];
			int i = 0;
			List<LinkedHashMap> milestones = (List) reRunTestParams.get("milestones");
			String[] RetryMilestone = new String[milestones.size()];
			for (LinkedHashMap milestone : milestones) {

				String Mile = milestone.get("name").toString();
				RetryMilestone[i] = Mile;
				i++;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");
			JSONObject ovUpdateJson = new JSONObject();
			boolean RSSI = false;
			String useCaseName = reRunTestParams.get("useCaseName").toString();
			if (useCaseName.contains("rssiImbalance_RangeCheck")) {
				RSSI = true;
			}
			String migrationSubType = reRunTestParams.get("migrationSubType").toString();
			String programName = reRunTestParams.get("programName").toString();
			// int programId=reRunTestParams.get("programId");
			int programId = Integer.parseInt(reRunTestParams.get("programId").toString());
			String ciqFileName = reRunTestParams.get("ciqName").toString();

			ovUpdateJson.put("programName", programName);
			ovUpdateJson.put("programId", programId);
			ovUpdateJson.put("ciqFileName", ciqFileName);
			String neName = reRunTestParams.get("neName").toString();
			if (migrationSubType.equalsIgnoreCase("AUDIT")) {

				String[] arrOfstring = neName.split("_");
				String Neid = arrOfstring[0];
				RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
				Map<String, RunTestEntity> runTestEntity = runTestService.getRunTestDetailsMapOVupdateforAudit(Neid,
						runTestId);
				ovUpdateJson.put("neid", Neid);
				ovUpdateJson.put("enbName", neName);
				JSONObject trakerIdDetails = runTestService.getTrakerIdList(ovUpdateJson, runTestEntity);
				logger.error("Tracker details:" + trakerIdDetails);
				String TrackerID = "";
				String WorkplanID = "";
				if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerjson")) {

					List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
							.get("trakerjson");
					if (!ObjectUtils.isEmpty(trakerdetails)) {
						for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails) {
							TrackerID = locTrackerDetailsModel.getTrackerId();
							logger.error("TrackerID:" + TrackerID);
						}
						for (LinkedHashMap milestone : milestones) {
							String Mile = milestone.get("name").toString();

							if (Mile.contains("P_PREEXISTING_RSSI")) {
								List<OvTestResultEntity> runTestResultEntityList = runTestService
										.getOvRunTestResult(runTestId);
								String milestone1 = null;
								for (OvTestResultEntity entity : runTestResultEntityList) {
									if (null != entity.getRssiContant()) {
										String rssiContent = entity.getRssiContant().toString();
										if (rssiContent != null && !rssiContent.isEmpty()) {
											milestone1 = rssiContent;
										}
									}

								}
								JSONObject updateJsonAPI = runTestService.patchMileStoneRSSI(TrackerID,
										migrationSubType, programId, runTestEntity, Neid, milestone1);
								runTestEntity.get(Neid).setProgressStatus("Completed");
								runTestRepository.updateRunTest(runTestEntity.get(Neid));
							} else if (Mile.contains("AUDIT")) {
								JSONObject updateJsonAPI = runTestService.HTMLUploadToOV(ovUpdateJson, runTestEntity);
								runTestEntity.get(Neid).setProgressStatus("Completed");
								runTestRepository.updateRunTest(runTestEntity.get(Neid));
							} else {
								JSONObject workPlanDetailsObject = runTestService.getWorkPlanIdList(TrackerID);
								if (workPlanDetailsObject != null
										&& workPlanDetailsObject.containsKey("workPlanjson")) {

									TrackerDetailsModel workTrackerDetailsModel = (TrackerDetailsModel) workPlanDetailsObject
											.get("workPlanjson");
									if (!ObjectUtils.isEmpty(workTrackerDetailsModel)) {
										WorkplanID = workTrackerDetailsModel.getWorkPlanId();
										logger.error("Workplan ID:" + WorkplanID);
										JSONObject updateJsonAPI = runTestService.patchMileStone(WorkplanID,
												migrationSubType, programId, runTestEntity, Neid, RetryMilestone);

										runTestEntity.get(Neid).setProgressStatus("Completed");
										runTestRepository.updateRunTest(runTestEntity.get(Neid));
									} else {
										for (int j = 0; j <= RetryMilestone.length - 1; j++) {
											OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
											ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
													+ "failed to fetch the workplan ID for Tracker ID:" + TrackerID
													+ " Responce is empty");
											RunTestEntity runTestEntity1 = runTestService
													.getRunTestEntity(runTestEntity.get(Neid).getId());
											ovTestResultEntity.setRunTestEntity(runTestEntity1);
											ovTestResultEntity.setMilestone(RetryMilestone[j]);
											runTestRepository.updateRunTestov(ovTestResultEntity);
											runTestEntity.get(Neid).setProgressStatus("Completed");
											runTestEntity.get(Neid).setOvUpdateStatus("Failure");
											runTestRepository.updateRunTest(runTestEntity.get(Neid));
										}
										// runTestEntity.get(Neid).setOvUpdateReason("["+date2+"]"+"-"+"failed to fetch
										// the workplan ID for Tracker ID:"+TrackerID+" Responce is empty");
										logger.error("fail in 2nd Api");

									}
								} else {
									for (int j = 0; j <= RetryMilestone.length - 1; j++) {
										OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
										ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
												+ "failed to fetch the workplan ID for Tracker ID:" + TrackerID);
										RunTestEntity runTestEntity1 = runTestService
												.getRunTestEntity(runTestEntity.get(Neid).getId());
										ovTestResultEntity.setRunTestEntity(runTestEntity1);
										ovTestResultEntity.setRunTestEntity(runTestEntity1);
										ovTestResultEntity.setMilestone(RetryMilestone[j]);
										runTestRepository.updateRunTestov(ovTestResultEntity);
										runTestEntity.get(Neid).setProgressStatus("Completed");
										runTestEntity.get(Neid).setOvUpdateStatus("Failure");
										runTestRepository.updateRunTest(runTestEntity.get(Neid));
									}
									// runTestEntity.get(Neid).setOvUpdateReason("failed to fetch the workplan ID
									// for Tracker ID:"+TrackerID);
									logger.error("fail in 2nd Api");

								}

							}
						}
					} else {
						for (int j = 0; j <= RetryMilestone.length - 1; j++) {
							OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
							ovTestResultEntity.setCurrentResult(
									"[" + date2 + "]" + "-" + "failed to fetch the Tracker ID for ID: " + Neid);
							RunTestEntity runTestEntity1 = runTestService
									.getRunTestEntity(runTestEntity.get(Neid).getId());
							ovTestResultEntity.setRunTestEntity(runTestEntity1);
							ovTestResultEntity.setMilestone(RetryMilestone[j]);
							runTestRepository.updateRunTestov(ovTestResultEntity);
							runTestEntity.get(Neid).setProgressStatus("Completed");
							runTestEntity.get(Neid).setOvUpdateStatus("Failure");
							runTestRepository.updateRunTest(runTestEntity.get(Neid));
						}
						// runTestRepository.updateRunTest(runTestEntity.get(Neid));
						logger.error("fail in 1st Api");

					}
				} else {
					for (int j = 0; j <= RetryMilestone.length - 1; j++) {
						OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
						if (trakerIdDetails.containsKey("reason")) {
							String reason = trakerIdDetails.get("reason").toString();
							ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-" + reason);
						} else {
							ovTestResultEntity
									.setCurrentResult("[" + date2 + "]" + "-" + "failed to fetch the Tracker ID");
						}

						RunTestEntity runTestEntity1 = runTestService.getRunTestEntity(runTestEntity.get(Neid).getId());
						ovTestResultEntity.setRunTestEntity(runTestEntity1);
						ovTestResultEntity.setMilestone(RetryMilestone[j]);
						runTestRepository.updateRunTestov(ovTestResultEntity);
						runTestEntity.get(Neid).setProgressStatus("Completed");
						runTestEntity.get(Neid).setOvUpdateStatus("Failure");
						runTestRepository.updateRunTest(runTestEntity.get(Neid));
						runTestRepository.updateRunTest(runTestEntity.get(Neid));
					}
					logger.error("fail in 1st Api");

				}

			} else {
				JSONObject runTestParams = runTestService.getInputRuntestJson(runTestId);
				RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
				Map<String, RunTestEntity> runTestEntity = runTestService.getRunTestDetailsMapOVupdate(runTestParams,
						runTestId);
				Map run = (Map) runTestParams.get("runTestFormDetails");
				List<LinkedHashMap> useCaseList = (List) run.get("useCase");
				List<Map> neList = (List<Map>) run.get("neDetails");
				ovUpdateJson.put("enbName", neName);
				for (Map neid : neList) {
					String neId = neid.get("neId").toString();
					ovUpdateJson.put("neid", neId);
					JSONObject trakerIdDetails = runTestService.getTrakerIdList(ovUpdateJson, runTestEntity);
					logger.error("Tracker details:" + trakerIdDetails);
					String TrackerID = "";
					String WorkplanID = "";
					if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerjson")) {
						List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
								.get("trakerjson");
						if (!ObjectUtils.isEmpty(trakerdetails)) {
							for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails) {
								TrackerID = locTrackerDetailsModel.getTrackerId();
								logger.error("TrackerID:" + TrackerID);
							}
							JSONObject workPlanDetailsObject = runTestService.getWorkPlanIdList(TrackerID);
							if (workPlanDetailsObject != null && workPlanDetailsObject.containsKey("workPlanjson")) {

								TrackerDetailsModel workTrackerDetailsModel = (TrackerDetailsModel) workPlanDetailsObject
										.get("workPlanjson");
								if (!ObjectUtils.isEmpty(workTrackerDetailsModel)) {

									WorkplanID = workTrackerDetailsModel.getWorkPlanId();
									logger.error("WorkplanID:" + WorkplanID);
									JSONObject updateJsonAPI = runTestService.patchMileStone(WorkplanID,
											migrationSubType, programId, runTestEntity, neId, RetryMilestone);
									runTestEntity.get(neId).setProgressStatus("Completed");
									runTestRepository.updateRunTest(runTestEntity.get(neId));
								} else {
									for (int j = 0; j <= RetryMilestone.length - 1; j++) {
										OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
										ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
												+ "failed to fetch the workplan ID ,Response is empty for Tracker ID:"
												+ TrackerID);
										RunTestEntity runTestEntity1 = runTestService
												.getRunTestEntity(runTestEntity.get(neId).getId());
										ovTestResultEntity.setRunTestEntity(runTestEntity1);
										ovTestResultEntity.setMilestone(RetryMilestone[j]);
										runTestRepository.updateRunTestov(ovTestResultEntity);
										runTestEntity.get(neId).setProgressStatus("Completed");
										runTestEntity.get(neId).setOvUpdateStatus("Failure");
										runTestRepository.updateRunTest(runTestEntity.get(neId));
									}
									logger.error("fail in 2nd Api");

								}
							} else {
								for (int j = 0; j <= RetryMilestone.length - 1; j++) {
									OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
									ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
											+ "failed to fetch the workplan ID for Tracker ID:" + TrackerID);
									RunTestEntity runTestEntity1 = runTestService
											.getRunTestEntity(runTestEntity.get(neId).getId());
									ovTestResultEntity.setRunTestEntity(runTestEntity1);
									ovTestResultEntity.setMilestone(RetryMilestone[j]);
									runTestRepository.updateRunTestov(ovTestResultEntity);
									runTestEntity.get(neId).setProgressStatus("Completed");
									runTestEntity.get(neId).setOvUpdateStatus("Failure");
									runTestRepository.updateRunTest(runTestEntity.get(neId));
								}
								logger.error("fail in 2nd Api");

							}
						} else {
							for (int j = 0; j <= RetryMilestone.length - 1; j++) {
								OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
								ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
										+ "failed to fetch the Tracker ID/ No Tracker ID on OV for : " + neId);
								RunTestEntity runTestEntity1 = runTestService
										.getRunTestEntity(runTestEntity.get(neId).getId());
								ovTestResultEntity.setRunTestEntity(runTestEntity1);
								ovTestResultEntity.setMilestone(RetryMilestone[j]);
								runTestRepository.updateRunTestov(ovTestResultEntity);
								runTestEntity.get(neId).setProgressStatus("Completed");
								runTestEntity.get(neId).setOvUpdateStatus("Failure");
								runTestRepository.updateRunTest(runTestEntity.get(neId));
							}
							logger.error("fail in 1st Api");

						}
					} else {
						for (int j = 0; j <= RetryMilestone.length - 1; j++) {
							OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
							ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
									+ "failed to fetch the Tracker ID/ No Tracker ID on OV for: " + neId);
							RunTestEntity runTestEntity1 = runTestService
									.getRunTestEntity(runTestEntity.get(neId).getId());
							ovTestResultEntity.setRunTestEntity(runTestEntity1);
							ovTestResultEntity.setMilestone(RetryMilestone[j]);
							runTestRepository.updateRunTestov(ovTestResultEntity);
							runTestEntity.get(neId).setProgressStatus("Completed");
							runTestEntity.get(neId).setOvUpdateStatus("Failure");
							runTestRepository.updateRunTest(runTestEntity.get(neId));
						}
						logger.error("fail in 1st Api");

					}
				}
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", "Retrying to Update the MileStone");
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateMilestone API"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/retryFailedMilestoneUpdate")
	public JSONObject retryFailedMilestoneUpdate(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		String date2 = DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");

		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");
			JSONObject ovUpdateJson = new JSONObject();
			// boolean status = runTestService.deleteOvRunTestResult(runTestId);
			String migrationSubType = reRunTestParams.get("migrationSubType").toString();
			String programName = reRunTestParams.get("programName").toString();
			// int programId=reRunTestParams.get("programId");
			int programId = Integer.parseInt(reRunTestParams.get("programId").toString());
			String ciqFileName = reRunTestParams.get("ciqName").toString();
			String Milestone = reRunTestParams.get("failedMilestone").toString();
			boolean RSSI = false;
			String useCaseName = reRunTestParams.get("useCaseName").toString();
			if (useCaseName.contains("rssiImbalance_RangeCheck")) {
				RSSI = true;
			}
			ovUpdateJson.put("programName", programName);
			ovUpdateJson.put("programId", programId);
			ovUpdateJson.put("ciqFileName", ciqFileName);
			String neName = reRunTestParams.get("neName").toString();
			if (migrationSubType.equalsIgnoreCase("AUDIT")) {

				String[] arrOfstring = neName.split("_");
				String Neid = arrOfstring[0];
				RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
				Map<String, RunTestEntity> runTestEntity = runTestService.getRunTestDetailsMapOVupdateforAudit(Neid,
						runTestId);
				ovUpdateJson.put("neid", Neid);
				ovUpdateJson.put("enbName", neName);
				JSONObject trakerIdDetails = runTestService.getTrakerIdList(ovUpdateJson, runTestEntity);
				logger.error("Tracker details:" + trakerIdDetails);
				String TrackerID = "";
				String WorkplanID = "";
				if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerjson")) {
					List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
							.get("trakerjson");
					if (!ObjectUtils.isEmpty(trakerdetails)) {
						for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails) {
							TrackerID = locTrackerDetailsModel.getTrackerId();
							logger.error("TrackerID:" + TrackerID);
						}
						if (Milestone.contains("P_PREEXISTING_RSSI")) {
							List<OvTestResultEntity> runTestResultEntityList = runTestService
									.getOvRunTestResult(runTestId);
							String milestone = null;
							for (OvTestResultEntity entity : runTestResultEntityList) {
								String rssiContent = entity.getRssiContant().toString();
								if (rssiContent != null && !rssiContent.isEmpty()) {
									milestone = rssiContent;
								}
							}
							JSONObject updateJsonAPI = runTestService.patchMileStoneRSSI(TrackerID, migrationSubType,
									programId, runTestEntity, Neid, milestone);
							runTestEntity.get(Neid).setProgressStatus("Completed");
							runTestRepository.updateRunTest(runTestEntity.get(Neid));
						} else if (Milestone.contains("AUDIT")) {
							JSONObject updateJsonAPI = runTestService.HTMLUploadToOV(ovUpdateJson, runTestEntity);
							runTestEntity.get(Neid).setProgressStatus("Completed");
							runTestRepository.updateRunTest(runTestEntity.get(Neid));
						} else {
							JSONObject workPlanDetailsObject = runTestService.getWorkPlanIdList(TrackerID);
							if (workPlanDetailsObject != null && workPlanDetailsObject.containsKey("workPlanjson")) {

								TrackerDetailsModel workTrackerDetailsModel = (TrackerDetailsModel) workPlanDetailsObject
										.get("workPlanjson");
								if (!ObjectUtils.isEmpty(workTrackerDetailsModel)) {
									WorkplanID = workTrackerDetailsModel.getWorkPlanId();
									logger.error("Workplan ID:" + WorkplanID);
									JSONObject updateJsonAPI = runTestService.patchMileStoneIndividual(WorkplanID,
											migrationSubType, programId, runTestEntity, Neid, Milestone);

									runTestEntity.get(Neid).setProgressStatus("Completed");
									runTestRepository.updateRunTest(runTestEntity.get(Neid));
								} else {
									OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
									ovTestResultEntity.setCurrentResult(
											"[" + date2 + "]" + "-" + "failed to fetch the workplan ID for Tracker ID:"
													+ TrackerID + " Responce is empty");
									RunTestEntity runTestEntity1 = runTestService
											.getRunTestEntity(runTestEntity.get(Neid).getId());
									ovTestResultEntity.setRunTestEntity(runTestEntity1);
									ovTestResultEntity.setMilestone(Milestone);
									runTestRepository.updateRunTestov(ovTestResultEntity);
									runTestEntity.get(Neid).setProgressStatus("Completed");
									runTestEntity.get(Neid).setOvUpdateStatus("Failure");
									logger.error("fail in 2nd Api");
									runTestRepository.updateRunTest(runTestEntity.get(Neid));

								}
							} else {
								OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
								ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
										+ "failed to fetch the workplan ID for Tracker ID:" + TrackerID);
								RunTestEntity runTestEntity1 = runTestService
										.getRunTestEntity(runTestEntity.get(Neid).getId());
								ovTestResultEntity.setRunTestEntity(runTestEntity1);
								ovTestResultEntity.setMilestone(Milestone);
								runTestRepository.updateRunTestov(ovTestResultEntity);
								runTestEntity.get(Neid).setProgressStatus("Completed");
								runTestEntity.get(Neid).setOvUpdateStatus("Failure");
								// runTestEntity.get(Neid).setOvUpdateReason("failed to fetch the workplan ID
								// for Tracker ID:"+TrackerID);
								logger.error("fail in 2nd Api");
								runTestRepository.updateRunTest(runTestEntity.get(Neid));
							}

						}
					} else {
						OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
						ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
								+ "failed to fetch the Tracker ID/ No Tracker ID on OV for: " + Neid);
						RunTestEntity runTestEntity1 = runTestService.getRunTestEntity(runTestEntity.get(Neid).getId());
						ovTestResultEntity.setRunTestEntity(runTestEntity1);
						ovTestResultEntity.setMilestone(Milestone);
						runTestRepository.updateRunTestov(ovTestResultEntity);
						runTestEntity.get(Neid).setProgressStatus("Completed");
						runTestEntity.get(Neid).setOvUpdateStatus("Failure");
						// runTestRepository.updateRunTest(runTestEntity.get(Neid));
						logger.error("fail in 1st Api");
						runTestRepository.updateRunTest(runTestEntity.get(Neid));
					}
				} else {
					OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
					if (trakerIdDetails.containsKey("reason")) {
						String reason = trakerIdDetails.get("reason").toString();
						ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-" + reason);
					} else {
						ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
								+ "failed to fetch the Tracker ID/ No Tracker ID on OV for: " + Neid);
					}
					RunTestEntity runTestEntity1 = runTestService.getRunTestEntity(runTestEntity.get(Neid).getId());
					ovTestResultEntity.setRunTestEntity(runTestEntity1);
					ovTestResultEntity.setMilestone(Milestone);
					runTestRepository.updateRunTestov(ovTestResultEntity);
					runTestEntity.get(Neid).setProgressStatus("Completed");
					runTestEntity.get(Neid).setOvUpdateStatus("Failure");
					runTestRepository.updateRunTest(runTestEntity.get(Neid));
					logger.error("fail in 1st Api");
					runTestRepository.updateRunTest(runTestEntity.get(Neid));
				}

			} else {
				JSONObject runTestParams = runTestService.getInputRuntestJson(runTestId);
				RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
				Map<String, RunTestEntity> runTestEntity = runTestService.getRunTestDetailsMapOVupdate(runTestParams,
						runTestId);
				Map run = (Map) runTestParams.get("runTestFormDetails");
				List<LinkedHashMap> useCaseList = (List) run.get("useCase");
				List<Map> neList = (List<Map>) run.get("neDetails");
				ovUpdateJson.put("enbName", neName);
				for (Map neid : neList) {
					String neId = neid.get("neId").toString();
					ovUpdateJson.put("neid", neId);
					JSONObject trakerIdDetails = runTestService.getTrakerIdList(ovUpdateJson, runTestEntity);
					logger.error("Tracker details:" + trakerIdDetails);
					String TrackerID = "";
					String WorkplanID = "";
					if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerjson")) {
						List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
								.get("trakerjson");
						if (!ObjectUtils.isEmpty(trakerdetails)) {
							for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails) {
								TrackerID = locTrackerDetailsModel.getTrackerId();
								logger.error("TrackerID:" + TrackerID);
							}
							JSONObject workPlanDetailsObject = runTestService.getWorkPlanIdList(TrackerID);
							if (workPlanDetailsObject != null && workPlanDetailsObject.containsKey("workPlanjson")) {

								TrackerDetailsModel workTrackerDetailsModel = (TrackerDetailsModel) workPlanDetailsObject
										.get("workPlanjson");
								if (!ObjectUtils.isEmpty(workTrackerDetailsModel)) {

									WorkplanID = workTrackerDetailsModel.getWorkPlanId();
									logger.error("WorkplanID:" + WorkplanID);
									JSONObject updateJsonAPI = runTestService.patchMileStoneIndividual(WorkplanID,
											migrationSubType, programId, runTestEntity, neId, Milestone);
									runTestEntity.get(neId).setProgressStatus("Completed");
									runTestRepository.updateRunTest(runTestEntity.get(neId));
								} else {
									OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
									ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
											+ "failed to fetch the workplan ID ,Response is empty for Tracker ID:"
											+ TrackerID);
									RunTestEntity runTestEntity1 = runTestService
											.getRunTestEntity(runTestEntity.get(neId).getId());
									ovTestResultEntity.setRunTestEntity(runTestEntity1);
									ovTestResultEntity.setMilestone(Milestone);
									runTestRepository.updateRunTestov(ovTestResultEntity);
									runTestEntity.get(neId).setProgressStatus("Completed");
									runTestEntity.get(neId).setOvUpdateStatus("Failure");
									// runTestEntity.get(neId).setOvUpdateReason("failed to fetch the workplan ID
									// for Tracker ID:"+TrackerID+" ,Response is empty");
									logger.error("fail in 2nd Api");
									runTestRepository.updateRunTest(runTestEntity.get(neId));

								}
							} else {
								OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
								ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
										+ "failed to fetch the workplan ID for Tracker ID:" + TrackerID);
								RunTestEntity runTestEntity1 = runTestService
										.getRunTestEntity(runTestEntity.get(neId).getId());
								ovTestResultEntity.setRunTestEntity(runTestEntity1);
								ovTestResultEntity.setMilestone(Milestone);
								runTestRepository.updateRunTestov(ovTestResultEntity);
								runTestEntity.get(neId).setProgressStatus("Completed");
								runTestEntity.get(neId).setOvUpdateStatus("Failure");
								// runTestEntity.get(neId).setOvUpdateReason("failed to fetch the workplan ID
								// for Tracker ID:"+TrackerID);
								logger.error("fail in 2nd Api");
								runTestRepository.updateRunTest(runTestEntity.get(neId));
							}
						} else {
							OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
							ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
									+ "failed to fetch the Tracker ID/ No Tracker ID on OV for: " + neId);
							RunTestEntity runTestEntity1 = runTestService
									.getRunTestEntity(runTestEntity.get(neId).getId());
							ovTestResultEntity.setRunTestEntity(runTestEntity1);
							ovTestResultEntity.setMilestone(Milestone);
							runTestRepository.updateRunTestov(ovTestResultEntity);
							runTestEntity.get(neId).setProgressStatus("Completed");
							runTestEntity.get(neId).setOvUpdateStatus("Failure");
							// runTestEntity.get(neId).setOvUpdateReason("failed to fetch the Tracker ID
							// ,Responce is Empty" );
							logger.error("fail in 1st Api");
							runTestRepository.updateRunTest(runTestEntity.get(neId));
						}
					} else {
						OvTestResultEntity ovTestResultEntity = new OvTestResultEntity();
						ovTestResultEntity.setCurrentResult("[" + date2 + "]" + "-"
								+ "failed to fetch the Tracker ID/ No Tracker ID on OV for: " + neId);
						RunTestEntity runTestEntity1 = runTestService.getRunTestEntity(runTestEntity.get(neId).getId());
						ovTestResultEntity.setRunTestEntity(runTestEntity1);
						ovTestResultEntity.setMilestone(Milestone);
						runTestRepository.updateRunTestov(ovTestResultEntity);
						runTestEntity.get(neId).setProgressStatus("Completed");
						runTestEntity.get(neId).setOvUpdateStatus("Failure");
						// runTestEntity.get(neId).setOvUpdateReason("failed to fetch the Tracker ID "
						// );
						logger.error("fail in 1st Api");
						runTestRepository.updateRunTest(runTestEntity.get(neId));
					}
				}
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", "Retrying to Update the MileStone");
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateMilestone API"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	/**
	 * This method will execute the Runtest
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */

	@SuppressWarnings({ "unchecked" })

	@PostMapping(value = "/generateScript")
	public JSONObject generateScript(@RequestBody JSONObject runTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			String programName = runTestParams.get("programName").toString();
			String migType = runTestParams.get("migrationType").toString();
			String subType = runTestParams.get("migrationSubType").toString();

			if (expiryDetails != null) {
				return expiryDetails;
			}
			String result = null;
			if (programName.contains("5G") || programName.contains("4G")) {

				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("requestType", "GENERATE");
				resultMap.put("reason", "Generated  Scripts Successfully");
				return resultMap;
			}
			if ("postmigration".equalsIgnoreCase(migType) && "Audit".equalsIgnoreCase(subType)
					&& programName.contains("5G")) {
				result = runTestService.generateScript5GAudit(runTestParams);
			} else {
				result = runTestService.generateScript(runTestParams);
			}

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("requestType", "GENERATE");
			if (result.equalsIgnoreCase(Constants.SUCCESS)) {
				resultMap.put("status", Constants.SUCCESS);
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
			}

		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	// usecase creation for 5g

	public JSONObject ConstantScript5G(int programId, String ciqFName, String neId, String sessionId,
			String constantFile, String connectionLocation, String configType, boolean addDefaultCurlRule, String time,
			String version, String fileType) {
		ArrayList<String> bashfilename = new ArrayList();
		ArrayList<String> array = new ArrayList<>();
		try {
			ciqFName = StringUtils.substringBeforeLast(ciqFName, ".");
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			StringBuilder constantScriptsFilePath = new StringBuilder();
			StringBuilder dBconstantScriptsFilePath = new StringBuilder();

			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);

			String basePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");

			String useCaseName = null;

			if ("RFNBR".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("NBR")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("NBR")
						.append(Constants.SEPARATOR);

				useCaseName = "RF_Scripts_Usecase" + neId + dateString;
			}
			/*
			 * if ("RF5G5G".equalsIgnoreCase(constantFile)) {
			 * 
			 * constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(
			 * Constants.SEPARATOR) .append(programId).append(Constants.SEPARATOR)
			 * .append(Constants.PRE_MIGRATION_SCRIPT.replace("filename",
			 * ciqFName).replaceAll(" ", "_"))
			 * .append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append(
			 * "5G5G") .append(Constants.SEPARATOR);
			 * 
			 * dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.
			 * CUSTOMER)
			 * .append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
			 * .append(Constants.PRE_MIGRATION_SCRIPT.replace("filename",
			 * ciqFName).replaceAll(" ", "_"))
			 * .append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append(
			 * "5G5G") .append(Constants.SEPARATOR); ;
			 * 
			 * useCaseName = "5G-5G_NBR_RF_Scripts_Usecase" + neId + dateString; }
			 */

			if ("CSL".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_Commission;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("CSL_UseCase")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("CSL_UseCase")
						.append(Constants.SEPARATOR);

				useCaseName = Constants.CSL_Usecase + "_" + neId + dateString;
			} else if ("A1A2".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_Commission;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("A1A2_UseCase")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("A1A2_UseCase")
						.append(Constants.SEPARATOR);

				useCaseName = Constants.ACPF_A1A2_Config_Usecase + "_" + neId + dateString;
			}

			else if ("AUCOMM".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_Commission;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("AU_ROUTE_UseCase").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("AU_ROUTE_UseCase").append(Constants.SEPARATOR);

				useCaseName = Constants.AU_Commision_Usecase + "_" + neId + dateString;
			} else if ("AUGPSCRIPT".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_Commission;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("AU_GPScript_UseCase").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("AU_GPScript_UseCase").append(Constants.SEPARATOR);

				useCaseName = Constants.GP_SCRIPT_Usecase + "_" + neId + dateString;
			} else if ("ANC".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_Commission;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("Anchor_CSL_UseCase").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR)
						.append("Anchor_CSL_UseCase").append(Constants.SEPARATOR);

				useCaseName = Constants.Anchor_CSL_UseCase + "_" + neId + dateString;
			} else if ("ENDC".equalsIgnoreCase(constantFile)) {
				String path = null;
				if (fileType.contains("ALL"))
					path = Constants.PRE_MIGRATION_All;
				else
					path = Constants.PRE_MIGRATION_ENDC;
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("ENDC_UseCase")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(path.replace("filename", ciqFName).replace("version", version).replace("enbId", neId)
								.replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("ENDC_UseCase")
						.append(Constants.SEPARATOR);

				useCaseName = Constants.ENDC_X2_UseCase + "_" + neId + dateString;
			} else if ("AU_CaCell".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath
						.append(basePath).append(Constants.CUSTOMER).append(
								Constants.SEPARATOR)
						.append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE
								.replace("filename", StringUtils.substringBeforeLast(ciqFName.toString(), "."))
								.replace("enbId", neId).replace("version", version).replaceAll(" ", "_"))
						.append("AU_CaCell_BashFile").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE.replace("filename", ciqFName)
								.replace("version", version).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("AU_CaCell_UseCase").append(Constants.SEPARATOR);

				useCaseName = "AU_CaCell_UseCase" + "_" + neId + dateString;
			} else if ("AU_20A".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE.replace("filename", ciqFName)
								.replace("version", version).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("AU_20A_BashFile").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE.replace("filename", ciqFName)
								.replace("version", version).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("AU_20A_UseCase").append(Constants.SEPARATOR);

				useCaseName = "AU_20A_UseCase" + "_" + neId + dateString;
			} else if ("pnp".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE.replace("filename", ciqFName)
								.replace("version", version).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("pnp_UseCase")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(Constants.PRE_MIGRATION_TEMPLATE.replace("filename", ciqFName)
								.replace("version", version).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("Test").append(Constants.SEPARATOR).append("pnp_UseCase")
						.append(Constants.SEPARATOR);

				useCaseName = "pnp_UseCase" + "_" + neId + dateString;
			}

			logger.info("RunTestController.ConstantScript useCaseName: {}", useCaseName);

			File directory = new File(constantScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				for (String singleFile : filename) {
					if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
						bashfilename.add(singleFile);
					}
				}
			}

			List<UploadFileEntity> uploadFileEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			ArrayList fileNameList = new ArrayList();
			if (!uploadFileEntity.isEmpty()) {
				for (UploadFileEntity SingleUploadFileEntity : uploadFileEntity) {
					fileNameList.add(SingleUploadFileEntity.getFileName());
				}
			}

			// Delete upload script

			ArrayList<Integer> number = new ArrayList<>();
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<RunTestEntity> runTestEntity = useCaseBuilderService.getRunTestDetails(useCaseName);
			if (CommonUtil.isValidObject(runTestEntity)) {
				for (RunTestEntity runTestEntitys : runTestEntity) {
					if (runTestEntitys.getStatus().equalsIgnoreCase("InProgress")) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
										+ " as NE is in progress",
								sessionId, "");
					} else {
						useCaseBuilderRepository.deleteruntestResult(useCaseBuilder.getId());
					}
				}

			}

			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());

			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {

				UploadFileEntity deleteUploadFileEntity = uploadFileService
						.getUploadScriptByPath(dBconstantScriptsFilePath, uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity)
						&& deleteUploadFileEntity.getFileName().contains(dateString)) {
					if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
						if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
								&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
							List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
									.getUseCaseXmlRuleEntityList(useCaseBuilderParamEntity.getId());
							if (CommonUtil.isValidObject(useCaseXmlRuleEntity)) {
								for (UseCaseXmlRuleEntity useCaseXmlRuleEntitys : useCaseXmlRuleEntity) {
									useCaseBuilderRepository.deleteUseCaseXmlRule(useCaseXmlRuleEntitys.getId());
								}
							}
							useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
						}
					}
					uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
				}
			}
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				useCaseBuilderRepository.deleteUseCaseBuilder(useCaseBuilder.getId());
			}

			for (String file : bashfilename) {

				UploadFileEntity createUploadFileEntity = new UploadFileEntity();
				createUploadFileEntity.setFileName(file);
				createUploadFileEntity.setFilePath(dBconstantScriptsFilePath.toString());
				createUploadFileEntity.setNeListEntity(null);
				createUploadFileEntity.setUploadedBy(Constants.UPLOADED_BY);
				createUploadFileEntity.setRemarks(Constants.REMARKS);
				createUploadFileEntity.setUseCount(0);
				createUploadFileEntity.setCustomerId(2);
				createUploadFileEntity.setCreationDate(new Date());
				createUploadFileEntity.setProgram(customerDetailsEntity.getProgramName());
				createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
				createUploadFileEntity.setState(Constants.STATE);
				createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				createUploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				createUploadFileEntity.setNeVersion(null);
				createUploadFileEntity.setScriptType(Constants.SCRIPT_TYPE);
				createUploadFileEntity.setConnectionLocation(connectionLocation);
				createUploadFileEntity.setConnectionLocationUserName(Constants.CONNECTION_LOCATION_USER_NAME);
				createUploadFileEntity.setPrompt(Constants.PROMPT);
				createUploadFileEntity
						.setConnectionLocationPwd(PasswordCrypt.encrypt(Constants.CONNECTION_LOCATION_PWD));
				createUploadFileEntity.setConnectionTerminal(Constants.CONNECTION_TERMINAL);
				createUploadFileEntity.setConnectionTerminalUserName(Constants.CONNECTION_TERMINAL_USER_NAME);
				createUploadFileEntity
						.setConnectionTerminalPwd(PasswordCrypt.encrypt(Constants.CONNECTION_TERMINAL_PWD));

				uploadFileService.createUploadScript(createUploadFileEntity);
			}

			// Insert Use case

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<UploadFileEntity> uploadEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			uploadEntity = uploadEntity.stream().filter(x -> x.getFileName().contains(dateString))
					.collect(Collectors.toList());
			if ("AUGPSCRIPT".equalsIgnoreCase(constantFile)) {
				ArrayList<UploadFileEntity> uploadEntityTemp = new ArrayList<>();
				String[] orderList = { "AU_GPScript_CA", "AU_GPScript_MAIN_GP", "BAND_COMBINATION" };
				for (String str : orderList) {
					for (UploadFileEntity uploadFile : uploadEntity) {
						if (uploadFile.getFileName().contains(str)) {
							uploadEntityTemp.add(uploadFile);
						}
					}
				}
				uploadEntity = uploadEntityTemp;
			}

			logger.info("RunTestController.ConstantScript dBconstantScriptsFilePath: {}",
					dBconstantScriptsFilePath.toString());
			logger.info("RunTestController.ConstantScript uploadEntity size: {}", uploadEntity.size());

			HashMap<String, Integer> scriptcounter = new HashMap<>();
			try {
				if (useCaseBuilderEntity != null) {
					logger.info("RunTestController.ConstantScript usecase found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					for (UploadFileEntity uploadFile : uploadEntity) {

						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							return CommonUtil.buildResponseJson(Constants.FAIL, "Check List script is empty", sessionId,
									"");
						}

						if (checkListScriptDetails.size() > 1) {
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						if (scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) == null) {
							scriptcounter.put(checkListScriptDetails.get(0).getScriptName(), 0);
						} else {
							scriptcounter.put(checkListScriptDetails.get(0).getScriptName(),
									scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) + 1);
						}

						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilderEntity.getId(), uploadFile.getId());
						if (useCaseBuilderParamEntity != null) {
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							useCaseScriptsModel.setScriptId(useCaseBuilderParamEntity.getId().toString());
							useCaseScriptsModel.setScriptName(uploadFile.getFileName());
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel
									.setScriptSequence(useCaseBuilderParamEntity.getExecutionSequence().toString());
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
						} else {

							String scriptExeSeq;
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							String fileNameWithExeSeq = uploadFile.getFileName();
							String[] exeSeqSplit = fileNameWithExeSeq.split("_");

							if (exeSeqSplit[2].contains("-")) {
								String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
								boolean numeric = true;
								numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = internalExeSeqSplit[0];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq()
											+ scriptcounter.get(checkListScriptDetails.get(0).getScriptName()));

							} else {
								boolean numeric = true;
								numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = exeSeqSplit[2];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq()
											+ scriptcounter.get(checkListScriptDetails.get(0).getScriptName()));
							}

							useCaseScriptsModel.setScriptId(null);
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel.setScriptSequence(scriptExeSeq);
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
							i++;
						}

					}

					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();

					useCaseBuilderModel.setId(useCaseBuilderEntity.getId().toString());
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
					useCaseBuilderModel.setExecutionSequence(useCaseBuilderEntity.getExecutionSequence().toString());
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
							programId, Constants.SUB_TYPE, sessionId);
					// useCaseForWfm.add(useCaseBuilderEntity.getUseCaseName());
					// GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId,useCaseForWfm);
					if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
						WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
								.get(sessionId + neId);
						List<String> useCaseList = wfmUsecases.getUsecases();
						useCaseList.add(useCaseName);
						wfmUsecases.setUsecases(useCaseList);
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
					}

					logger.info("RunTestController.ConstantScript updating usecase builder done: {}", useCaseName);
				} else {
					logger.info("RunTestController.ConstantScript usecase not found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					ArrayList scriptSeqList = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					List<UploadFileEntity> up1 = new ArrayList<>();
					ArrayList<Integer> arI = new ArrayList<>();
					ArrayList<Integer> arI2 = new ArrayList<>();
					for (UploadFileEntity uploadFile : uploadEntity) {
						String rfFileName = uploadFile.getFileName();
						if (rfFileName.contains("4G5G")) {
							// String rfFileNameAfter = StringUtils.substringAfter(rfFileName,
							// "BASH_RF_NB-IoTAdd_");
							// String rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							String[] rfFileName1 = rfFileName.split("_");
							String rfFileNameFinal = rfFileName1[0];
							// String rfFileNameFinal = rfFileName.replaceAll("[\\D]", "");
							// String rfFileNameFinal = rfFileName.substring(0, 2);
							// up1.add(rfFileName);
							arI.add(Integer.parseInt(rfFileNameFinal));
							rfFileNameFinal = null;
						}
						if (rfFileName.contains("5G5G")) {
							// String rfFileNameAfter = StringUtils.substringAfter(rfFileName,
							// "BASH_RF_NB-IoTAdd_");
							// String rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							String[] rfFileName1 = rfFileName.split("_");
							String rfFileNameFinal = rfFileName1[0];
							// String rfFileNameFinal = rfFileName.replaceAll("[\\D]", "");
							// String rfFileNameFinal = rfFileName.substring(0, 2);
							arI2.add(Integer.parseInt(rfFileNameFinal));
							rfFileNameFinal = null;
						}
					}
					Collections.sort(arI);
					Collections.sort(arI2);
					// System.out.println(arI);
					for (UploadFileEntity uploadFile : uploadEntity) {
						xmlRuleModel = new ArrayList();
						List<CheckListScriptDetEntity> checkListScriptDetails = new ArrayList<>();
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						// CheckListScriptDetEntity checkListScriptDetEntity = new
						// CheckListScriptDetEntity();
						// checkListScriptDetEntity.setCheckListFileName("anupama.xlsx");
						// checkListScriptDetEntity.setConfigType("NB-IoT Add");
						// checkListScriptDetEntity.setScriptExeSeq(200);
						// checkListScriptDetEntity.setScriptName("1_57009_10_eNB_merge_script-20200831_5G5G_RFUsecase_07000020001__09112020.sh");
						// checkListScriptDetEntity.setStepIndex(1);
						// checkListScriptDetails.add(checkListScriptDetEntity);

						if (checkListScriptDetEntity == null) {
							logger.info("RunTestController.ConstantScript Check List script is empty for the program");
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							logger.info(
									"RunTestController.ConstantScript Check List script is empty for file: {} in Checklist :{} ",
									uploadFile.getFileName(), checkListScriptDetEntity.getCheckListFileName());

							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for file: " + uploadFile.getFileName()
											+ " in Checklist :" + checkListScriptDetEntity.getCheckListFileName(),
									sessionId, "");
						}

						if (checkListScriptDetails.size() > 1) {
							logger.info(
									"RunTestController.ConstantScript Check List script having more than one record for file: {} ",
									uploadFile.getFileName());
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						if (scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) == null) {
							scriptcounter.put(checkListScriptDetails.get(0).getScriptName(), 0);
						} else {
							scriptcounter.put(checkListScriptDetails.get(0).getScriptName(),
									scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) + 1);
						}

						String scriptExeSeq;
						// String fseq = "10";
						String firstName;

						UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

						Map<String, String> scriptDetails = new HashMap<>();
						scriptDetails.put("scriptName", uploadFile.getFileName());
						scriptDetails.put("scriptFileId", uploadFile.getId().toString());

						String fileNameWithExeSeq = uploadFile.getFileName();
						String[] exeSeqSplit = fileNameWithExeSeq.split("_");

						// if (exeSeqSplit[2].contains("-")) {
						// String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
						// boolean numeric = true;
						// numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
						// if (numeric) {
						// scriptExeSeq = internalExeSeqSplit[0];
						// }
						/*
						 * else { String fileName = uploadFile.getFileName(); if
						 * (fileName.contains("BASH_RF_NB")) { String fileAfter =
						 * StringUtils.substringAfter(fileName, "BASH_RF_NB-IoTAdd_"); fseq =
						 * StringUtils.substringBefore(fileAfter, "-");
						 * 
						 * if(fileName.contains("BASH_RF_NB")) { fseq =
						 * Integer.toString((Integer.parseInt(fseq)+1)); }
						 * 
						 * 
						 * fseq = Integer.toString((arI2.indexOf(Integer.parseInt(fseq))) + 1); while
						 * (array.contains(fseq)) { fseq = Integer.toString(Integer.parseInt(fseq) + 1);
						 * } array.add(fseq); System.out.println(fseq); }
						 * 
						 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
						 * List sortedlist = new ArrayList<>(scriptSeqList);
						 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
						 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
						 * Integer.toString(maxExeSeq + 1); }
						 * 
						 * // scriptExeSeq = Integer.toString(maxScriptSeqId + i);
						 */
						if (true) {
							String rfFileName = uploadFile.getFileName();
							if ("RFNBR".equalsIgnoreCase(constantFile)) {
								String fileName = StringUtils.substringBeforeLast(rfFileName, ".");
								Pattern pat = Pattern
										.compile("(^\\d+)[\\-\\_]+(\\d+)[\\-\\_]+(eNB|ACPF|AUPF|AU)[\\-\\_]");
								Matcher mat = pat.matcher(fileName);
								if (mat.find()) {
									String sp[] = fileName.split("[\\-\\_]+");
									scriptExeSeq = sp[0];
									if (scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) != 0) {
										scriptcounter.put(checkListScriptDetails.get(0).getScriptName(),
												scriptcounter.get(checkListScriptDetails.get(0).getScriptName()) - 1);
									} else {
										scriptcounter.remove(checkListScriptDetails.get(0).getScriptName());
									}
								} else {
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq()
											+ scriptcounter.get(checkListScriptDetails.get(0).getScriptName()));
								}
							} else {
								scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq()
										+ scriptcounter.get(checkListScriptDetails.get(0).getScriptName()));
							}
						}
						// }
						// }

						else {
							boolean numeric = true;
							numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = exeSeqSplit[2];
							} else {
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq()
										+ scriptcounter.get(checkListScriptDetails.get(0).getScriptName()));
							}
						}

						logger.info("RunTestController.ConstantScript addDefaultCurlRule: {}, so adding curl rule",
								addDefaultCurlRule);

						if (CommonUtil.isValidObject(xmlRuleModel) && addDefaultCurlRule) {
							XmlRuleBuilderEntity xmlRuleBuilderEntity = xmlRuleBuilderService.findByRuleName(programId,
									"curl");
							if (CommonUtil.isValidObject(xmlRuleBuilderEntity)) {
								XmlRuleModel ruleModel = new XmlRuleModel();
								ruleModel.setXmlId(String.valueOf(xmlRuleBuilderEntity.getId()));
								Map<String, String> xmlDetails = new HashMap<String, String>();
								xmlDetails.put("xmlName", xmlRuleBuilderEntity.getRuleName());
								ruleModel.setXmlDetails(xmlDetails);
								ruleModel.setXmlSequence("1");
								xmlRuleModel.add(ruleModel);
							}
						}

						useCaseScriptsModel.setScriptId(null);
						useCaseScriptsModel.setScript(scriptDetails);
						useCaseScriptsModel.setCmdRules(cmdRuleModel);
						useCaseScriptsModel.setXmlRules(xmlRuleModel);
						useCaseScriptsModel.setFileRules(fileRuleModel);
						useCaseScriptsModel.setShellRules(shellRuleModel);
						useCaseScriptsModel.setScriptSequence(scriptExeSeq);
						useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

						scriptSeqList.add(scriptExeSeq);
						scriptList.add(useCaseScriptsModel);
						i++;
					}

					int maxId = useCaseBuilderRepository.getMaxUseCaseId();
					maxId = maxId + 1;
					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseName);
					useCaseBuilderModel.setExecutionSequence(Integer.toString(maxId));
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
							programId, Constants.SUB_TYPE, sessionId);
					// for work flow management
					if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
						WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
								.get(sessionId + neId);
						List<String> useCaseList = wfmUsecases.getUsecases();
						useCaseList.add(useCaseName);
						wfmUsecases.setUsecases(useCaseList);
						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
					}
					logger.info("RunTestController.ConstantScript create usecase builder done: {}", useCaseName);

				}

			} catch (Exception e) {
				logger.error(
						"Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			// Copy File

			for (UploadFileEntity entity : uploadEntity) {

				String srcPath = constantScriptsFilePath.toString() + entity.getFileName();

				StringBuilder desBuilderPath = new StringBuilder();

				desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
								.replace("programId", Integer.toString(programId))
								.replace("migrationType", Constants.MIGRATION_TYPE)
								.replace("subType", Constants.SUB_TYPE))
						.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);

				File dest = new File(desBuilderPath.toString() + entity.getFileName());

				if (!dest.exists()) {
					FileUtil.createDirectory(desBuilderPath.toString());
				}

				File source = new File(srcPath);
				FileUtils.copyFile(source, dest);

				// ForDuo
				savecurlCommandMig(source, basePath, useCaseName, neId, programId, ciqFName);
			}

		} catch (Exception e) {
			logger.error("Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	public void savecurlCommandMig(File source, String basePath, String useCaseName, String neId, int programId,
			String ciqFileName) {
		try {
			String sourceCurl = FilenameUtils.removeExtension(source.getPath()) + ".txt";
			StringBuilder desBuilderPath = new StringBuilder();
			desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
					.append(Constants.DUO_CURL_COMMAND_FILEPATH.replace("programId", Integer.toString(programId))
							.replace("filename", ciqFileName).replace("enbId", neId))
					.append(Constants.SEPARATOR).append(useCaseName.trim()).append(Constants.SEPARATOR);
			File sourceCurlFile = new File(sourceCurl);
			File destCurlFile = new File(desBuilderPath.toString() + sourceCurlFile.getName());
			if (!destCurlFile.exists()) {
				FileUtil.createDirectory(desBuilderPath.toString());
			}
			FileUtils.copyFile(sourceCurlFile, destCurlFile);
			FileUtil.deleteFileOrFolder(sourceCurl);
		} catch (Exception e) {
			logger.error(
					"Exception in savecurlCommandMig() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
	}

	// uscase creartion for 5G-DSS
	public JSONObject ConstantScript5GDSS(int programId, String ciqFName, String neId, String sessionId,
			String constantFile, String connectionLocation, String configType, boolean addDefaultCurlRule, String time,
			String version) {
		ArrayList<String> bashfilename = new ArrayList();
		ArrayList<String> array = new ArrayList<>();
		try {
			ciqFName = StringUtils.substringBeforeLast(ciqFName, ".");
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			StringBuilder constantScriptsFilePath = new StringBuilder();
			StringBuilder dBconstantScriptsFilePath = new StringBuilder();
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);
			String basePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			String useCaseName = null;
			if ("Pre-Check".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Pre-Check")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Pre-Check")
						.append(Constants.SEPARATOR);

				useCaseName = "Pre-Check_RF_Scripts_Usecase" + neId + dateString;
			} else if ("Cutover".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Cutover")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Cutover")
						.append(Constants.SEPARATOR);
				;

				useCaseName = "Cutover_RF_Scripts_Usecase" + neId + dateString;
			} else if ("Rollback".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Rollback")
						.append(Constants.SEPARATOR);
				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Rollback")
						.append(Constants.SEPARATOR);
				;

				useCaseName = "Rollback_RF_Scripts_Usecase" + neId + dateString;
			} else if ("Extended".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Extended")
						.append(Constants.SEPARATOR);
				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("Extended")
						.append(Constants.SEPARATOR);
				;

				useCaseName = "Extended_Usecase" + neId + dateString;
			}

			logger.info("RunTestController.ConstantScript useCaseName: {}", useCaseName);

			File directory = new File(constantScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				for (String singleFile : filename) {
					if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
						bashfilename.add(singleFile);
					}
				}
			}

			List<UploadFileEntity> uploadFileEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			ArrayList fileNameList = new ArrayList();
			if (!uploadFileEntity.isEmpty()) {
				for (UploadFileEntity SingleUploadFileEntity : uploadFileEntity) {
					fileNameList.add(SingleUploadFileEntity.getFileName());
				}
			}

			// Delete upload script
			ArrayList<Integer> number = new ArrayList<>();
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<RunTestEntity> runTestEntity = useCaseBuilderService.getRunTestDetails(useCaseName);
			if (CommonUtil.isValidObject(runTestEntity)) {
				for (RunTestEntity runTestEntitys : runTestEntity) {
					if (runTestEntitys.getStatus().equalsIgnoreCase("InProgress")) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
										+ " as NE is in progress",
								sessionId, "");
					} else {
						useCaseBuilderRepository.deleteruntestResult(useCaseBuilder.getId());
					}
				}

			}

			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());

			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {

				UploadFileEntity deleteUploadFileEntity = uploadFileService
						.getUploadScriptByPath(dBconstantScriptsFilePath, uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity)
						&& deleteUploadFileEntity.getFileName().contains(dateString)) {
					if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
						if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
								&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
							List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
									.getUseCaseXmlRuleEntityList(useCaseBuilderParamEntity.getId());
							if (CommonUtil.isValidObject(useCaseXmlRuleEntity)) {
								for (UseCaseXmlRuleEntity useCaseXmlRuleEntitys : useCaseXmlRuleEntity) {
									useCaseBuilderRepository.deleteUseCaseXmlRule(useCaseXmlRuleEntitys.getId());
								}
							}
							useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
						}
					}
					uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
				}
			}
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				useCaseBuilderRepository.deleteUseCaseBuilder(useCaseBuilder.getId());
			}

			for (String file : bashfilename) {

				UploadFileEntity createUploadFileEntity = new UploadFileEntity();
				createUploadFileEntity.setFileName(file);
				createUploadFileEntity.setFilePath(dBconstantScriptsFilePath.toString());
				createUploadFileEntity.setNeListEntity(null);
				createUploadFileEntity.setUploadedBy(Constants.UPLOADED_BY);
				createUploadFileEntity.setRemarks(Constants.REMARKS);
				createUploadFileEntity.setUseCount(0);
				createUploadFileEntity.setCustomerId(2);
				createUploadFileEntity.setCreationDate(new Date());
				createUploadFileEntity.setProgram(customerDetailsEntity.getProgramName());
				createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
				createUploadFileEntity.setState(Constants.STATE);
				createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				createUploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				createUploadFileEntity.setNeVersion(null);
				createUploadFileEntity.setScriptType(Constants.SCRIPT_TYPE);
				createUploadFileEntity.setConnectionLocation(connectionLocation);
				createUploadFileEntity.setConnectionLocationUserName(Constants.CONNECTION_LOCATION_USER_NAME);
				createUploadFileEntity.setPrompt(Constants.PROMPT);
				createUploadFileEntity
						.setConnectionLocationPwd(PasswordCrypt.encrypt(Constants.CONNECTION_LOCATION_PWD));
				createUploadFileEntity.setConnectionTerminal(Constants.CONNECTION_TERMINAL);
				createUploadFileEntity.setConnectionTerminalUserName(Constants.CONNECTION_TERMINAL_USER_NAME);
				createUploadFileEntity
						.setConnectionTerminalPwd(PasswordCrypt.encrypt(Constants.CONNECTION_TERMINAL_PWD));

				uploadFileService.createUploadScript(createUploadFileEntity);
			}

			// Insert Use case

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<UploadFileEntity> uploadEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			uploadEntity = uploadEntity.stream().filter(x -> x.getFileName().contains(dateString))
					.collect(Collectors.toList());

			logger.info("RunTestController.ConstantScript dBconstantScriptsFilePath: {}",
					dBconstantScriptsFilePath.toString());
			logger.info("RunTestController.ConstantScript uploadEntity size: {}", uploadEntity.size());

			HashMap<String, Integer> scriptcounter = new HashMap<>();
			try {
				if (useCaseBuilderEntity != null) {
					logger.info("RunTestController.ConstantScript usecase found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();

					for (UploadFileEntity uploadFile : uploadEntity) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilderEntity.getId(), uploadFile.getId());
						if (useCaseBuilderParamEntity != null) {
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							useCaseScriptsModel.setScriptId(useCaseBuilderParamEntity.getId().toString());
							useCaseScriptsModel.setScriptName(uploadFile.getFileName());
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel
									.setScriptSequence(useCaseBuilderParamEntity.getExecutionSequence().toString());
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
						} else {

							String scriptExeSeq = "0";
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							String fileNameWithExeSeq = uploadFile.getFileName();
							String[] exeSeqSplit = fileNameWithExeSeq.split("_");

							if (constantFile.equalsIgnoreCase("Pre-Check") || constantFile.equalsIgnoreCase("Cutover")
									|| constantFile.equalsIgnoreCase("Rollback")
									|| constantFile.equalsIgnoreCase("Extended")) {
								scriptExeSeq = uploadFile.getFileName().split("\\D.*")[0];
							}

							useCaseScriptsModel.setScriptId(null);
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel.setScriptSequence(scriptExeSeq);
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);

						}

					}

					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();

					useCaseBuilderModel.setId(useCaseBuilderEntity.getId().toString());
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
					useCaseBuilderModel.setExecutionSequence(useCaseBuilderEntity.getExecutionSequence().toString());
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
							programId, Constants.SUB_TYPE, sessionId);

					// For WorkFlowManagement

					if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {

						WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES

								.get(sessionId + neId);

						List<String> useCaseList = wfmUsecases.getUsecases();

						useCaseList.add(useCaseName);

						wfmUsecases.setUsecases(useCaseList);

						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);

					}

					logger.info("RunTestController.ConstantScript updating usecase builder done: {}", useCaseName);
				} else {
					logger.info("RunTestController.ConstantScript usecase not found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					ArrayList scriptSeqList = new ArrayList();
					int i = 1;
					for (UploadFileEntity uploadFile : uploadEntity) {
						xmlRuleModel = new ArrayList();
						String scriptExeSeq = "0";

						UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

						Map<String, String> scriptDetails = new HashMap<>();
						scriptDetails.put("scriptName", uploadFile.getFileName());
						scriptDetails.put("scriptFileId", uploadFile.getId().toString());

						String fileNameWithExeSeq = uploadFile.getFileName();

						if (constantFile.equalsIgnoreCase("Pre-Check") || constantFile.equalsIgnoreCase("Cutover")
								|| constantFile.equalsIgnoreCase("Rollback")
								|| constantFile.equalsIgnoreCase("Extended")) {
							scriptExeSeq = uploadFile.getFileName().split("\\D.*")[0];
						}

						logger.info("RunTestController.ConstantScript addDefaultCurlRule: {}, so adding curl rule",
								addDefaultCurlRule);

						if (CommonUtil.isValidObject(xmlRuleModel) && addDefaultCurlRule) {
							XmlRuleBuilderEntity xmlRuleBuilderEntity = xmlRuleBuilderService.findByRuleName(programId,
									"curl");
							if (CommonUtil.isValidObject(xmlRuleBuilderEntity)) {
								XmlRuleModel ruleModel = new XmlRuleModel();
								ruleModel.setXmlId(String.valueOf(xmlRuleBuilderEntity.getId()));
								Map<String, String> xmlDetails = new HashMap<String, String>();
								xmlDetails.put("xmlName", xmlRuleBuilderEntity.getRuleName());
								ruleModel.setXmlDetails(xmlDetails);
								ruleModel.setXmlSequence("1");
								xmlRuleModel.add(ruleModel);
							}
						}

						useCaseScriptsModel.setScriptId(null);
						useCaseScriptsModel.setScript(scriptDetails);
						useCaseScriptsModel.setCmdRules(cmdRuleModel);
						useCaseScriptsModel.setXmlRules(xmlRuleModel);
						useCaseScriptsModel.setFileRules(fileRuleModel);
						useCaseScriptsModel.setShellRules(shellRuleModel);
						useCaseScriptsModel.setScriptSequence(scriptExeSeq);
						useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

						scriptSeqList.add(scriptExeSeq);
						scriptList.add(useCaseScriptsModel);
						i++;
					}

					int maxId = useCaseBuilderRepository.getMaxUseCaseId();
					maxId = maxId + 1;
					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseName);
					useCaseBuilderModel.setExecutionSequence(Integer.toString(maxId));
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					// for work flow management

					if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {

						WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES

								.get(sessionId + neId);

						List<String> useCaseList = wfmUsecases.getUsecases();

						useCaseList.add(useCaseName);

						wfmUsecases.setUsecases(useCaseList);

						GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);

					}

					useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
							programId, Constants.SUB_TYPE, sessionId);
					logger.info("RunTestController.ConstantScript create usecase builder done: {}", useCaseName);

				}

			} catch (Exception e) {
				logger.error(
						"Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			// Copy File

			for (UploadFileEntity entity : uploadEntity) {

				String srcPath = constantScriptsFilePath.toString() + entity.getFileName();

				StringBuilder desBuilderPath = new StringBuilder();

				desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
								.replace("programId", Integer.toString(programId))
								.replace("migrationType", Constants.MIGRATION_TYPE)
								.replace("subType", Constants.SUB_TYPE))
						.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);

				File dest = new File(desBuilderPath.toString() + entity.getFileName());

				if (!dest.exists()) {
					FileUtil.createDirectory(desBuilderPath.toString());
				}

				File source = new File(srcPath);
				FileUtils.copyFile(source, dest);

				// ForDuo
				savecurlCommandMig(source, basePath, useCaseName, neId, programId, ciqFName);
			}

		} catch (Exception e) {
			logger.error("Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	// @PostMapping(value = "/generateScript")
	// public void generateScript(@RequestBody JSONObject runTestParams) {
	public JSONObject ConstantScript(int programId, String ciqFName, String neId, String sessionId, String constantFile,
			String connectionLocation, String configType, boolean addDefaultCurlRule, String csvPath) {

		ArrayList<String> bashfilename = new ArrayList();
		ArrayList<String> array = new ArrayList<>();

		try {
			ciqFName = StringUtils.substringBeforeLast(ciqFName, ".");
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));

			/*
			 * int programId = 3; String ciqFName = "UNY-NE-VZ_CIQ_Ver2.91_04012019"; String
			 * neId = "57170"; String sessionId =runTestParams.get("sessionId").toString();
			 * String constantFile = "RF";
			 */

			StringBuilder constantScriptsFilePath = new StringBuilder();
			StringBuilder dBconstantScriptsFilePath = new StringBuilder();

			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);

			String basePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");

			String useCaseName = null;

			if ("RF".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("RF")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("RF")
						.append(Constants.SEPARATOR);

				useCaseName = Constants.RF_USECASE + "_" + neId + dateString;
				;

			} else if ("COMM".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId)
						.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("RF").append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId)
						.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append("RF").append(Constants.SEPARATOR);

				useCaseName = Constants.COMMISION_USECASE + "_" + neId + dateString;
				;
			} else if ("CA".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("CA")
						.append(Constants.SEPARATOR);
				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId).append(Constants.SEPARATOR).append("CA")
						.append(Constants.SEPARATOR);
				;

				useCaseName = "CA_Usecase" + neId + dateString;
			} else if ("ENDC".equalsIgnoreCase(constantFile)) {

				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId + "_ENDC").append(Constants.SEPARATOR).append("ENDC")
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
						.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR).append(neId + "_ENDC").append(Constants.SEPARATOR).append("ENDC")
						.append(Constants.SEPARATOR);

				useCaseName = Constants.ENDC_USECASE + "_" + neId + dateString;
				;

			}

			else if ("GROWCELL".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.GROWCELLUSECASE + "_" + neId + dateString;
				;
			} else if ("GROWENB".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_GROW_ENB_NEGROW.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_GROW_ENB_NEGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.GROWENBUSECASE + "_" + neId + dateString;
				;
			} else if ("DeGrow".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_DEGROW_NEGROW.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_DEGROW_NEGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.DEGROWENBUSECASE + "_" + neId + dateString;
				;
			} else if ("NECREATION".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)//
						.append(programId).append(Constants.PRE_MIGRATION_NECREATION.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_NECREATION
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.CREATIONNEUSECASE + "_" + neId + dateString;

				;
			} else if ("AUCaCell".equalsIgnoreCase(constantFile)) {
				if (csvPath.contains("20B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20BUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20BUseCase" + "_" + neId + dateString;
					}

				} else if (csvPath.contains("20C")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20CUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21A")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21AUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21B")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21BUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21BUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21C")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21CUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21D")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21DUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell21DUseCase" + "_" + neId + dateString;

					}
				} // 22A
				else if (csvPath.contains("22A")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell22AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell22AUseCase" + "_" + neId + dateString;

					}
				}else if (csvPath.contains("22C")) {

					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell22CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell22CUseCase" + "_" + neId + dateString;

					}
				}

				else {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_CELL_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "AUCaCell20AUseCase" + "_" + neId + dateString;

					}
				}

			} else if ("AU20B".equalsIgnoreCase(constantFile) || "AU20A".equalsIgnoreCase(constantFile)
					|| "AU20C".equalsIgnoreCase(constantFile) || "AU21A".equalsIgnoreCase(constantFile)
					|| "AU21B".equalsIgnoreCase(constantFile) || "AU21C".equalsIgnoreCase(constantFile)
					|| "AU21D".equalsIgnoreCase(constantFile) || "AU22A".equalsIgnoreCase(constantFile) || "AU22C".equalsIgnoreCase(constantFile)) { // 22A
				if (csvPath.contains("20B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20BUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20BUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("20C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20CUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21A")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21AUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21BUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21BUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21CUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21D")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21DUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU21DUseCase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("22A")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU22AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU22AUseCase" + "_" + neId + dateString;

					}
				}else if (csvPath.contains("22C")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU22CUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU22CUseCase" + "_" + neId + dateString;

					}
				}

				else {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20AUseCase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_GROW_ENB_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						useCaseName = "AU20AUseCase" + "_" + neId + dateString;

					}
				}

			} else if ("pnp5G".equalsIgnoreCase(constantFile)) {
				if (csvPath.contains("20B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("20C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21A")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21AUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21D")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21DUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp21DUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("22A")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp22AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp22AUsecase" + "_" + neId + dateString;

					}
				}else if (csvPath.contains("22C")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp22CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp22CUsecase" + "_" + neId + dateString;

					}
				}

				else {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20AUsecase" + "_" + neId + dateString;

					}
				}

			} else if ("DeGrow5G".equalsIgnoreCase(constantFile)) {
				if (csvPath.contains("20B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE20BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE20BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("20C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE20CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_20C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE20CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21A")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21AUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21B_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21D")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21DUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_21D_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE21DUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("22A")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE22AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE22AUsecase" + "_" + neId + dateString;

					}
				}else if (csvPath.contains("22C")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE22CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_DEGROW_22C_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "DeleteNE22CUsecase" + "_" + neId + dateString;

					}
				}

				else {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename", ciqFName)
										.replace("version", "20A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "pnp20AUsecase" + "_" + neId + dateString;

					}
				}

			} else if ("NECREATION5G".equalsIgnoreCase(constantFile)) {// useCaseName = Constants.CREATIONNEUSECASE +
																		// "_" + neId + dateString;NECREATION
				if (csvPath.contains("20B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime20BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20B_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "20B").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20B_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "20B").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime20BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("20C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "20C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime20CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "20C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_20C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "20C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime20CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21A")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21A_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21A").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21A_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21A").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21AUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21B")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21B_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21B").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21BUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21B_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21B").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21B_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21B").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21BUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21C")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21CUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("21D")) {
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21D_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "21D").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21DUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21D_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21D").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_21D_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "21D").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime21DUsecase" + "_" + neId + dateString;

					}
				} else if (csvPath.contains("22A")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22A_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22A").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime22AUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22A_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "22A").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22A_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "22A").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime22AUsecase" + "_" + neId + dateString;

					}
				}else if (csvPath.contains("22C")) { // 22A
					if (!csvPath.contains("ALL")) {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22C_NEGROW_5G.replace("filename", ciqFName)
										.replace("version", "22C").replace("enbId", neId).replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime22CUsecase" + "_" + neId + dateString;
					} else {
						constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
								.append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "22C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);

						dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
								.append(Constants.SEPARATOR).append(programId)
								.append(Constants.PRE_MIGRATION_NECREATION_22C_NEGROW_5G_ALL
										.replace("filename", ciqFName).replace("version", "22C").replace("enbId", neId)
										.replaceAll(" ", "_"))
								.append(Constants.SEPARATOR);
						useCaseName = "NeCreationTime22CUsecase" + "_" + neId + dateString;

					}
				}

				/*
				 * else { if (!csvPath.contains("ALL")) {
				 * constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(
				 * Constants.SEPARATOR) .append(programId)
				 * .append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename",
				 * ciqFName) .replace("version", "20A").replace("enbId", neId).replaceAll(" ",
				 * "_")) .append(Constants.SEPARATOR);
				 * 
				 * dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.
				 * CUSTOMER) .append(Constants.SEPARATOR).append(programId)
				 * .append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G.replace("filename",
				 * ciqFName) .replace("version", "20A").replace("enbId", neId).replaceAll(" ",
				 * "_")) .append(Constants.SEPARATOR); useCaseName = Constants.CREATIONNEUSECASE
				 * + "_" + neId + dateString; } else {
				 * constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(
				 * Constants.SEPARATOR) .append(programId)
				 * .append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename",
				 * ciqFName) .replace("version", "20A").replace("enbId", neId).replaceAll(" ",
				 * "_")) .append(Constants.SEPARATOR);
				 * 
				 * dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.
				 * CUSTOMER) .append(Constants.SEPARATOR).append(programId)
				 * .append(Constants.PRE_MIGRATION_PNP_20A_NEGROW_5G_ALL.replace("filename",
				 * ciqFName) .replace("version", "20A").replace("enbId", neId).replaceAll(" ",
				 * "_")) .append(Constants.SEPARATOR); useCaseName = Constants.CREATIONNEUSECASE
				 * + "_" + neId + dateString;
				 * 
				 * } }
				 */

			} else if ("pnp".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_PNP_NEGROW.replace("filename", ciqFName)
								.replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_PNP_NEGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.PNPUSECASE + "_" + neId + dateString;
				;
			}

			logger.info("RunTestController.ConstantScript useCaseName: {}", useCaseName);

			File directory = new File(constantScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				for (String singleFile : filename) {
					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")
							|| constantFile.equalsIgnoreCase("CA") || constantFile.equalsIgnoreCase("ENDC")) {

						if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
							bashfilename.add(singleFile);
						}
					}

					else if (constantFile.equalsIgnoreCase("GROWCELL")) {
						if (singleFile.contains("GROW_CELL")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWENB")) {
						if (singleFile.contains("GROW_ENB")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("DeGrow5G")) {// for 5G
						if (singleFile.contains("DeGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("DeGrow")) { // for 4G-usm live
						if (singleFile.contains("DeGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("NECREATION")) { // for 4G-usm live
						if (singleFile.contains("NeCreation") || singleFile.contains("PackageInventory")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("NECREATION5G")) {// NECREATION5G for 5G
						if (singleFile.contains("NeCreation") || singleFile.contains("PackageInventory")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("pnp")) {
						if (singleFile.contains("pnp_macro")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU20B")) {
						if (singleFile.contains("AU_20B")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU20A")) {
						if (singleFile.contains("AU_20A")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU20C")) {
						if (singleFile.contains("AU_20C")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU21A")) {
						if (singleFile.contains("AU_21A")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU21B")) {
						if (singleFile.contains("AU_21B")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU21C")) {
						if (singleFile.contains("AU_21C")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU21D")) {
						if (singleFile.contains("AU_21D")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("AU22A")) { // 22A
						if (singleFile.contains("AU_22A")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					}else if (constantFile.equalsIgnoreCase("AU22C")) { // 22A
						if (singleFile.contains("AU_22C")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					}

					else if (constantFile.equalsIgnoreCase("AUCaCell")) {
						if (singleFile.contains("AU_CaCell")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("pnp5G")) {
						if (singleFile.contains("pnp20B_macro") || singleFile.contains("pnp20A_macro")
								|| singleFile.contains("pnp20C_macro") || singleFile.contains("pnp21A_macro")
								|| singleFile.contains("pnp21B_macro") || singleFile.contains("pnp21C_macro")
								|| singleFile.contains("pnp21D_macro") || singleFile.contains("pnp22A_macro") || singleFile.contains("pnp22C_macro")) { // 22A
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					}

				}
			}
			List<UploadFileEntity> uploadFileEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			ArrayList fileNameList = new ArrayList();
			if (!uploadFileEntity.isEmpty()) {
				for (UploadFileEntity SingleUploadFileEntity : uploadFileEntity) {
					fileNameList.add(SingleUploadFileEntity.getFileName());
				}
			}

			// Delete upload script
			ArrayList<Integer> number = new ArrayList<>();
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<RunTestEntity> runTestEntity = useCaseBuilderService.getRunTestDetails(useCaseName);
			if (CommonUtil.isValidObject(runTestEntity)) {
				for (RunTestEntity runTestEntitys : runTestEntity) {
					if (runTestEntitys.getStatus().equalsIgnoreCase("InProgress")) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
										+ " as NE is in progress",
								sessionId, "");
					} else {
						useCaseBuilderRepository.deleteruntestResult(useCaseBuilder.getId());
					}
				}

			}

			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());

			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {

				UploadFileEntity deleteUploadFileEntity = uploadFileService
						.getUploadScriptByPath(dBconstantScriptsFilePath, uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity)
						&& deleteUploadFileEntity.getFileName().contains(dateString)) {
					if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
						if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
								&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
							List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
									.getUseCaseXmlRuleEntityList(useCaseBuilderParamEntity.getId());
							if (CommonUtil.isValidObject(useCaseXmlRuleEntity)) {
								for (UseCaseXmlRuleEntity useCaseXmlRuleEntitys : useCaseXmlRuleEntity) {
									useCaseBuilderRepository.deleteUseCaseXmlRule(useCaseXmlRuleEntitys.getId());
								}
							}
							useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
						}
					}
					uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
				}
			}
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				useCaseBuilderRepository.deleteUseCaseBuilder(useCaseBuilder.getId());
			}

			for (String file : bashfilename) {

				UploadFileEntity createUploadFileEntity = new UploadFileEntity();
				createUploadFileEntity.setFileName(file);
				createUploadFileEntity.setFilePath(dBconstantScriptsFilePath.toString());
				createUploadFileEntity.setNeListEntity(null);
				createUploadFileEntity.setUploadedBy(Constants.UPLOADED_BY);
				createUploadFileEntity.setRemarks(Constants.REMARKS);
				createUploadFileEntity.setUseCount(0);
				createUploadFileEntity.setCustomerId(2);
				createUploadFileEntity.setCreationDate(new Date());
				createUploadFileEntity.setProgram(customerDetailsEntity.getProgramName());
				// createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
				createUploadFileEntity.setState(Constants.STATE);
				// createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				if (file.contains("COMM") || file.contains("RF") || file.contains("ENDC")) {
					createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
					createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				} else {
					createUploadFileEntity.setMigrationType(Constants.PREMIGRATION_TYPE);
					createUploadFileEntity.setSubType(Constants.PREMIG_SUB_TYPE);
				}

				createUploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				createUploadFileEntity.setNeVersion(null);
				createUploadFileEntity.setScriptType(Constants.SCRIPT_TYPE);
				createUploadFileEntity.setConnectionLocation(connectionLocation);
				createUploadFileEntity.setConnectionLocationUserName(Constants.CONNECTION_LOCATION_USER_NAME);
				createUploadFileEntity.setPrompt(Constants.PROMPT);
				createUploadFileEntity
						.setConnectionLocationPwd(PasswordCrypt.encrypt(Constants.CONNECTION_LOCATION_PWD));
				createUploadFileEntity.setConnectionTerminal(Constants.CONNECTION_TERMINAL);
				createUploadFileEntity.setConnectionTerminalUserName(Constants.CONNECTION_TERMINAL_USER_NAME);
				createUploadFileEntity
						.setConnectionTerminalPwd(PasswordCrypt.encrypt(Constants.CONNECTION_TERMINAL_PWD));

				/*
				 * if (!fileNameList.isEmpty()) { if (fileNameList.contains(file)) { // Do
				 * nothing } else {
				 * uploadFileService.createUploadScript(createUploadFileEntity); } } else {
				 */
				uploadFileService.createUploadScript(createUploadFileEntity);
				// }
			}

			// Insert Use case

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<UploadFileEntity> uploadEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			uploadEntity = uploadEntity.stream().filter(x -> x.getFileName().contains(dateString))
					.collect(Collectors.toList());

			logger.info("RunTestController.ConstantScript dBconstantScriptsFilePath: {}",
					dBconstantScriptsFilePath.toString());
			logger.info("RunTestController.ConstantScript uploadEntity size: {}", uploadEntity.size());
			try {
				if (useCaseBuilderEntity != null) {
					logger.info("RunTestController.ConstantScript usecase found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					for (UploadFileEntity uploadFile : uploadEntity) {

						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							return CommonUtil.buildResponseJson(Constants.FAIL, "Check List script is empty", sessionId,
									"");
						}

						if (checkListScriptDetails.size() > 1) {
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilderEntity.getId(), uploadFile.getId());
						if (useCaseBuilderParamEntity != null) {
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							useCaseScriptsModel.setScriptId(useCaseBuilderParamEntity.getId().toString());
							useCaseScriptsModel.setScriptName(uploadFile.getFileName());
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel
									.setScriptSequence(useCaseBuilderParamEntity.getExecutionSequence().toString());
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
						} else {

							String scriptExeSeq;
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							String fileNameWithExeSeq = uploadFile.getFileName();
							String[] exeSeqSplit = fileNameWithExeSeq.split("_");

							if (exeSeqSplit[2].contains("-")) {
								String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
								boolean numeric = true;
								numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = internalExeSeqSplit[0];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());

							} else {
								boolean numeric = true;
								numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = exeSeqSplit[2];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}

							useCaseScriptsModel.setScriptId(null);
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel.setScriptSequence(scriptExeSeq);
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
							i++;
						}

					}

					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();

					useCaseBuilderModel.setId(useCaseBuilderEntity.getId().toString());
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
					useCaseBuilderModel.setExecutionSequence(useCaseBuilderEntity.getExecutionSequence().toString());
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")
							|| constantFile.equalsIgnoreCase("ENDC")) {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript updating usecase builder done: {}", useCaseName);
				} else {
					logger.info("RunTestController.ConstantScript usecase not found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					ArrayList scriptSeqList = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					String test = "";
					String rfFileNameFinal = "";
					ArrayList<Integer> arI = new ArrayList<>();
					for (UploadFileEntity uploadFile : uploadEntity) {
						String rfFileName = uploadFile.getFileName();
						if (rfFileName.contains("BASH_RF_NB")) {
							String rfFileNameAfter = StringUtils.substringAfter(rfFileName, "BASH_RF_NB-IoTAdd_");
							if (rfFileNameAfter.contains("_")) {
								test = StringUtils.substringBefore(rfFileNameAfter, "_");
							}
							if (Pattern.matches("[0-9]*", test)) {
								rfFileNameFinal = test;
							} else {
								rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							}
							arI.add(Integer.parseInt(rfFileNameFinal));
						} else if (rfFileName.contains("BASH_CA_")) {
							String rfFileNameAfter = StringUtils.substringAfter(rfFileName, "BASH_CA_");
							if (rfFileNameAfter.contains("_")) {
								test = StringUtils.substringBefore(rfFileNameAfter, "_");
							}
							if (Pattern.matches("[0-9]*", test)) {
								rfFileNameFinal = test;
							} else {
								rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							}
							arI.add(Integer.parseInt(rfFileNameFinal));
						} else if (rfFileName.contains("BASH_ENDC_")) {
							String rfFileNameAfter = StringUtils.substringAfter(rfFileName, "BASH_ENDC_");
							if (rfFileNameAfter.contains("_")) {
								test = StringUtils.substringBefore(rfFileNameAfter, "_");
							}
							if (Pattern.matches("[0-9]*", test)) {
								rfFileNameFinal = test;
							} else {
								rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							}
							arI.add(Integer.parseInt(rfFileNameFinal));
						}
					}
					Collections.sort(arI);
					// System.out.println(arI);
					for (UploadFileEntity uploadFile : uploadEntity) {
						xmlRuleModel = new ArrayList();
						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							logger.info("RunTestController.ConstantScript Check List script is empty for the program");
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							logger.info(
									"RunTestController.ConstantScript Check List script is empty for file: {} in Checklist :{} ",
									uploadFile.getFileName(), checkListScriptDetEntity.getCheckListFileName());

							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for file: " + uploadFile.getFileName()
											+ " in Checklist :" + checkListScriptDetEntity.getCheckListFileName(),
									sessionId, "");
						}

						if (checkListScriptDetails.size() > 1) {
							logger.info(
									"RunTestController.ConstantScript Check List script having more than one record for file: {} ",
									uploadFile.getFileName());
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						String scriptExeSeq;
						String fseq = "";
						String firstName;

						UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

						Map<String, String> scriptDetails = new HashMap<>();
						scriptDetails.put("scriptName", uploadFile.getFileName());
						scriptDetails.put("scriptFileId", uploadFile.getId().toString());

						String fileNameWithExeSeq = uploadFile.getFileName();
						String[] exeSeqSplit = fileNameWithExeSeq.split("_");

						if (exeSeqSplit[2].contains("-")) {
							String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
							boolean numeric = true;
							numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = internalExeSeqSplit[0];
							} else {
								String fileName = uploadFile.getFileName();
								if (fileName.contains("BASH_RF_NB")) {
									String fileAfter = StringUtils.substringAfter(fileName, "BASH_RF_NB-IoTAdd_");
									if (fileAfter.contains("_")) {
										fseq = StringUtils.substringBefore(fileAfter, "_");
									}
									if (Pattern.matches("[0-9]*", fseq)) {

									} else {
										fseq = StringUtils.substringBefore(fileAfter, "-");
									}
									/*
									 * if(fileName.contains("BASH_RF_NB")) { fseq =
									 * Integer.toString((Integer.parseInt(fseq)+1)); }
									 */

									fseq = Integer.toString((arI.indexOf(Integer.parseInt(fseq))) + 1);
									while (array.contains(fseq)) {
										fseq = Integer.toString(Integer.parseInt(fseq) + 1);
									}
									array.add(fseq);
									// System.out.println(fseq);
								}
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								else {
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
								}
							}
						} else if (constantFile.equalsIgnoreCase("CA")) {
							String fileName = uploadFile.getFileName();
							String fileAfter = StringUtils.substringAfter(fileName, "BASH_CA_");
							if (fileAfter.contains("_")) {
								fseq = StringUtils.substringBefore(fileAfter, "_");
							}
							if (Pattern.matches("[0-9]*", fseq)) {

							} else {
								fseq = StringUtils.substringBefore(fileAfter, "_");
							}
							/*
							 * if(fileName.contains("BASH_RF_NB")) { fseq =
							 * Integer.toString((Integer.parseInt(fseq)+1)); }
							 */

							fseq = Integer.toString((arI.indexOf(Integer.parseInt(fseq))) + 22);
							while (array.contains(fseq)) {
								fseq = Integer.toString(Integer.parseInt(fseq) + 2);
							}
							array.add(fseq);
							// System.out.println(fseq);
						} else if (constantFile.equalsIgnoreCase("ENDC")) {
							String fileName = uploadFile.getFileName();
							String fileAfter = StringUtils.substringAfter(fileName, "BASH_ENDC_");
							if (fileAfter.contains("_")) {
								fseq = StringUtils.substringBefore(fileAfter, "_");
							}
							if (Pattern.matches("[0-9]*", fseq)) {

							} else {
								fseq = StringUtils.substringBefore(fileAfter, "_");
							}
							/*
							 * if(fileName.contains("BASH_RF_NB")) { fseq =
							 * Integer.toString((Integer.parseInt(fseq)+1)); }
							 */

							fseq = Integer.toString((arI.indexOf(Integer.parseInt(fseq))) + 500);
							while (array.contains(fseq)) {
								fseq = Integer.toString(Integer.parseInt(fseq) + 1);
							}
							array.add(fseq);
							// System.out.println(fseq);
						}

						else {
							boolean numeric = true;
							numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = exeSeqSplit[2];
							} else {
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}
						}

						logger.info("RunTestController.ConstantScript addDefaultCurlRule: {}, so adding curl rule",
								addDefaultCurlRule);

						if (CommonUtil.isValidObject(xmlRuleModel) && addDefaultCurlRule) {
							XmlRuleBuilderEntity xmlRuleBuilderEntity = xmlRuleBuilderService.findByRuleName(programId,
									"curl");
							if (CommonUtil.isValidObject(xmlRuleBuilderEntity)) {
								XmlRuleModel ruleModel = new XmlRuleModel();
								ruleModel.setXmlId(String.valueOf(xmlRuleBuilderEntity.getId()));
								Map<String, String> xmlDetails = new HashMap<String, String>();
								xmlDetails.put("xmlName", xmlRuleBuilderEntity.getRuleName());
								ruleModel.setXmlDetails(xmlDetails);
								ruleModel.setXmlSequence("1");
								xmlRuleModel.add(ruleModel);
							}
						}

						useCaseScriptsModel.setScriptId(null);
						useCaseScriptsModel.setScript(scriptDetails);
						useCaseScriptsModel.setCmdRules(cmdRuleModel);
						useCaseScriptsModel.setXmlRules(xmlRuleModel);
						useCaseScriptsModel.setFileRules(fileRuleModel);
						useCaseScriptsModel.setShellRules(shellRuleModel);
						useCaseScriptsModel.setScriptSequence(fseq);
						useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

						scriptSeqList.add(fseq);
						scriptList.add(useCaseScriptsModel);
						i++;
					}
					int maxId = useCaseBuilderRepository.getMaxUseCaseId();
					maxId = maxId + 1;
					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseName);
					useCaseBuilderModel.setExecutionSequence(Integer.toString(maxId));
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")
							|| constantFile.equalsIgnoreCase("ENDC")) {
						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {

						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript create usecase builder done: {}", useCaseName);

				}

			} catch (Exception e) {
				logger.error(
						"Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			// Copy File

			for (UploadFileEntity entity : uploadEntity) {

				String srcPath = constantScriptsFilePath.toString() + entity.getFileName();

				StringBuilder desBuilderPath = new StringBuilder();

				if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")
						|| constantFile.equalsIgnoreCase("ENDC")) {
					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.MIGRATION_TYPE)
									.replace("subType", Constants.SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				} else {

					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.PREMIGRATION_TYPE)
									.replace("subType", Constants.PREMIG_SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				}

				File dest = new File(desBuilderPath.toString() + entity.getFileName());

				if (!dest.exists()) {
					FileUtil.createDirectory(desBuilderPath.toString());
				}

				File source = new File(srcPath);
				FileUtils.copyFile(source, dest);

				// ForDuo
				savecurlCommandMig(source, basePath, useCaseName, neId, programId, ciqFName);
			}

		} catch (Exception e) {
			logger.error("Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	/**
	 * This method will execute the Runtest
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/runTestRanAtp")

	public JSONObject runTestRanAtp(@RequestPart(required = false, value = "UPLOAD") MultipartFile opsAtp,
			@RequestParam("ranAtpRunTest") String ranAtpRunTest) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		try {

			if (opsAtp != null && uploadFileService.isFileEmpty(opsAtp)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPLOAD_SCRIPT_EMPTY));
				return resultMap;
			}

			JSONParser parser = new JSONParser();
			JSONObject runTestParams = (JSONObject) parser.parse(ranAtpRunTest);

			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();

			String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

			if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

				Map run = (Map) runTestParams.get("runTestFormDetails");
				useCurrPassword = (boolean) run.get("currentPassword");
				String sanePassword = run.get("password").toString();

				String migrationType = runTestParams.get("migrationType").toString();
				String subType = runTestParams.get("migrationSubType").toString();
				int programId = Integer.parseInt(runTestParams.get("programId").toString());
				String testname = run.get("testname").toString();

				if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
					migrationType = "Migration";
				} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
					migrationType = "PostMigration";
				} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
					migrationType = "PreMigration";
				}

				if ("precheck".equalsIgnoreCase(subType)) {
					subType = "PreCheck";
				} else if ("commission".equalsIgnoreCase(subType)) {
					subType = "Commission";
				} else if ("postcheck".equalsIgnoreCase(subType)) {
					subType = "PostCheck";
				} else if ("AUDIT".equalsIgnoreCase(subType)) {
					subType = "Audit";
				} else if ("RANATP".equalsIgnoreCase(subType)) {
					subType = "RanATP";
				} else if ("NEGrow".equalsIgnoreCase(subType)) {
					subType = "NEGrow";
				}

				expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

				if (expiryDetails != null) {
					return expiryDetails;
				}

				JSONObject output = new JSONObject();
				if (useCurrPassword == false && sanePassword.isEmpty()) {

					output = ranAtpService.getSaneDetailsforPassword(runTestParams);

					if (!output.isEmpty()) {
						resultMap.put("status", "PROMPT");
						resultMap.put("password", output);
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						resultMap.put("requestType", "RUN_TEST");

						return resultMap;
					}
				}

				String opsAtpFileContent = null;

				StringBuffer sb = new StringBuffer();
				BufferedReader br;
				String line;
				try {
					if (opsAtp != null) {
						InputStream is = opsAtp.getInputStream();
						br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							sb.append(line).append("\n");
						}
						opsAtpFileContent = sb.toString();
						br.close();
					}

				} catch (IOException e) {
					logger.info(
							"Exception in runTestRanAtp() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				}

				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());

				String htmlOutputFileName = "oRAN_ATP_" + timeStamp + ".xlsx";

				// Map<String, RunTestEntity> runTestEntity =
				// ranAtpService.insertRunTestDetails(runTestParams,htmlOutputFileName);

				String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType, subType, testname);
				if (isTestNamePresent != Constants.SUCCESS) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.TEST_NAME_PRESENT));
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
				int programIds = Integer.parseInt(runTestParams.get("programId").toString());
				List<Map> neList = (List<Map>) run.get("neDetails");
				Map runs = (Map) runTestParams.get("runTestFormDetails");
				if (!runs.get("lsmVersion").toString().isEmpty() || migrationType.equalsIgnoreCase("postmigration")) {
					for (Map neid : neList) {
						NeMappingModel neMappingModelversion = new NeMappingModel();
						CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
						programDetailsEntityversion.setId(programIds);
						neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
						neMappingModelversion.setEnbId(neid.get("neId").toString());
						NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
								.getNetWorkEntityDetails(neMappingModelversion);
						if (neMappingEntitiesForVersion != null) {
							String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
							if (!lsmVersion.equals(runs.get("lsmVersion"))
									&& !runs.get("lsmVersion").toString().isEmpty()) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "Selected Sites don't belong to the selected SM Version");
								return resultMap;
							}
						} else {
							if (runs.get("lsmVersion").toString().isEmpty()
									|| run.get("lsmName").toString().isEmpty()) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "NE is not Mapped");
								return resultMap;
							}
						}
					}
				}

				Map<String, RunTestEntity> runTestEntity = ranAtpService.insertRunTestDetails(runTestParams, "");

				String result;
				if (opsAtp == null) {
					result = ranAtpService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
							htmlOutputFileName, null, null);
				} else {
					result = ranAtpService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
							htmlOutputFileName, opsAtpFileContent, opsAtp.getOriginalFilename());
				}

				resultMap.put("password", output);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				if ("Success".equalsIgnoreCase(result)) {
					resultMap.put("status", Constants.SUCCESS);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", result);
				}

				commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
						Constants.ACTION_EXECUTE, "Run Test for RanATp Executed Successfully", sessionId);
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", enbStatus);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTestRanAtp() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will execute the Runtest for WFM
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */

	public JSONObject runTestRanAtpWFM(@RequestBody JSONObject runTestParams) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		MultipartFile opsAtp = null;
		boolean useCurrPassword;
		LinkedHashMap<String, String> finalResultMap = new LinkedHashMap<String, String>();
		List<Map<String, RunTestEntity>> allrunTestEntity = new LinkedList<Map<String, RunTestEntity>>();

		try {

			if (opsAtp != null && uploadFileService.isFileEmpty(opsAtp)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPLOAD_SCRIPT_EMPTY));
				return resultMap;
			}

			/*
			 * JSONParser parser = new JSONParser(); JSONObject runTestParams = (JSONObject)
			 * parser.parse(ranAtpRunTest); d
			 */
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();

			HashMap<String, String> testNames = new HashMap<String, String>();

			JSONObject runTestFormDetails = (JSONObject) runTestParams.get("runTestFormDetails");
			// List<String> useCase = (List<String>) runTestFormDetails.get("useCase");
			List<LinkedHashMap<String, String>> neDetails = (List<LinkedHashMap<String, String>>) runTestFormDetails
					.get("neDetails");
			Map<String, List<String>> bandNameList = (Map<String, List<String>>) runTestFormDetails.get("bandName");
			for (int v = 0; v < neDetails.size(); v++) {
				List<Map> singleNeId = new ArrayList<>();
				Map<String, String> door = new HashMap<>();
				door = neDetails.get(v);
				singleNeId.add(door);
				runTestFormDetails.put("neDetails", singleNeId);
				String enodebName = door.get("neName").toString();
				String timeStampDate = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
						.format(new Timestamp(System.currentTimeMillis()));
				String testName = "WFM_" + enodebName + "_" + timeStampDate;

				testNames.put(door.get("neId").toString(), testName);

				// runTestParams.put("runTestFormDetails", runTestFormDetails);

				// JSONObject bandNames = new JSONObject();
				// List<Map> singleNeId = new ArrayList<>();
				// Map<String, String> enb = neDetails.get(v);

				// singleNeId.add(door);
				// runTestFormDetails.put("neDetails", singleNeId);
				// Map<String, String[]> bandNameList = new LinkedHashMap<>();
				List<String> bandName = bandNameList.get(door.get("neId"));
				runTestFormDetails.put("bandName", bandName);
				runTestFormDetails.put("testname", testName);
				runTestParams.put("runTestFormDetails", runTestFormDetails);
				String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

				if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

					Map run = (Map) runTestParams.get("runTestFormDetails");
					useCurrPassword = (boolean) run.get("currentPassword");
					String sanePassword = run.get("password").toString();

					String migrationType = runTestParams.get("migrationType").toString();
					String subType = runTestParams.get("migrationSubType").toString();
					int programId = Integer.parseInt(runTestParams.get("programId").toString());
					String testname = run.get("testname").toString();

					if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
						migrationType = "Migration";
					} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
						migrationType = "PostMigration";
					} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
						migrationType = "PreMigration";
					}

					if ("precheck".equalsIgnoreCase(subType)) {
						subType = "PreCheck";
					} else if ("commission".equalsIgnoreCase(subType)) {
						subType = "Commission";
					} else if ("postcheck".equalsIgnoreCase(subType)) {
						subType = "PostCheck";
					} else if ("AUDIT".equalsIgnoreCase(subType)) {
						subType = "Audit";
					} else if ("RANATP".equalsIgnoreCase(subType)) {
						subType = "RanATP";
					} else if ("NEGrow".equalsIgnoreCase(subType)) {
						subType = "NEGrow";
					}

					expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

					if (expiryDetails != null) {
						return expiryDetails;
					}

					JSONObject output = new JSONObject();
					if (useCurrPassword == false && sanePassword.isEmpty()) {

						output = ranAtpService.getSaneDetailsforPassword(runTestParams);

						if (!output.isEmpty()) {
							resultMap.put("status", "PROMPT");
							resultMap.put("password", output);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("requestType", "RUN_TEST");

							return resultMap;
						}
					}

					String opsAtpFileContent = null;

					StringBuffer sb = new StringBuffer();
					BufferedReader br;
					String line;
					try {
						if (opsAtp != null) {
							InputStream is = opsAtp.getInputStream();
							br = new BufferedReader(new InputStreamReader(is));
							while ((line = br.readLine()) != null) {
								sb.append(line).append("\n");
							}
							opsAtpFileContent = sb.toString();
							br.close();
						}

					} catch (IOException e) {
						logger.info("Exception in runTestRanAtp() in RunTestController"
								+ ExceptionUtils.getFullStackTrace(e));
					}

					String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());

					String htmlOutputFileName = "oRAN_ATP_" + timeStamp + ".xlsx";

					// Map<String, RunTestEntity> runTestEntity =
					// ranAtpService.insertRunTestDetails(runTestParams,htmlOutputFileName);

					String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType, subType,
							testname);
					if (isTestNamePresent != Constants.SUCCESS) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.TEST_NAME_PRESENT));
						resultMap.put("sessionId", sessionId);
						resultMap.put("serviceToken", serviceToken);
						return resultMap;
					}

					// Map<String, RunTestEntity> runTestEntity =
					// ranAtpService.insertRunTestDetails(runTestParams, "");
					Map<String, RunTestEntity> runTestEntity = ranAtpService.insertRunTestDetails(runTestParams, "");
					allrunTestEntity.add(runTestEntity);

					String result;
					if (opsAtp == null) {
						result = ranAtpService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
								htmlOutputFileName, null, null);
					} else {
						result = ranAtpService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
								htmlOutputFileName, opsAtpFileContent, opsAtp.getOriginalFilename());
					}
					finalResultMap.put(door.get("neId").toString(), result);
					resultMap.put("testnames", testNames);
					resultMap.put("runTestEntity", runTestEntity);
					resultMap.put("password", output);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					if (result.equalsIgnoreCase(Constants.SUCCESS)) {
						resultMap.put("status", Constants.SUCCESS);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", enbStatus);
				}
				commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
						Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);

			}

		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.error("Exception in runTestWFM() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.error("Exception in runTestWFM() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		resultMap.put("allrunTestEntity", allrunTestEntity);
		resultMap.put("combinedresult", finalResultMap);
		return resultMap;

	}

	/**
	 * This method will delete runTestDetails
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/deleteRunTestData")
	public JSONObject deleteRunTest(@RequestBody JSONObject runTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		Integer customerId;
		String migrationType = null;
		String migrationSubType = null;
		String programName = null;
		Integer programId;
		Integer history;
		try {
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			runTestId = (Integer) runTestParams.get("id");
			migrationType = runTestParams.get("migrationType").toString();
			migrationSubType = runTestParams.get("migrationSubType").toString();
			programId = (Integer) runTestParams.get("programId");
			history = Integer.valueOf(LoadPropertyFiles.getInstance().getProperty("actionPerformed"));
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			programName = runTestParams.get("programName").toString();
			String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");

			if (expiryDetails != null) {
				return expiryDetails;
			}

			int page = 1;
			int count = 10;
			customerId = 1;

			RunTestEntity runTestEntity = runTestService.getRunTestEntity(runTestId);
			String testName = runTestEntity.getTestName();
			if (testName.contains("WFM")) {
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", "Please delete from workflow management");
				return resultMap;
			}

			String threadname = runTestEntity.getruntestThreadName();
			// System.out.println(threadname);
			if (threadname != null) {
				Set<Thread> setThreads = Thread.getAllStackTraces().keySet();
				// setThreads.forEach(x ->
				// System.out.println("********************"+x.getName()));
				setThreads.stream().filter(t -> t.getName().equals(threadname)).findAny().ifPresent(Thread::interrupt);
			}
			List<RunTestResultEntity> runTestResultEntityList = runTestService.getRunTestResult(runTestId);
			String[] useCaseList = runTestEntity.getUseCase().split(",");
			if (programName.contains("4G-USM-LIVE")) {
				audit4GSummaryService.deleteAuditSummaryReport(runTestId);
				audit4GSummaryService.deleteAuditPassFailReport(runTestId);
				auditCriticalParamsService.deleteAuditCriticalSummaryEntityByRunTestId(runTestId);
				if (!migrationSubType.equalsIgnoreCase("PREAUDIT")) {
					audit4GIssueService.deleteaudit4GIssueEntityByRunTestId(runTestId);
				}

			} else if (programName.contains("5G-CBAND")) {
				audit5GCBandSummaryService.deleteAuditSummaryReport(runTestId);
				audit5GCBandSummaryService.deleteAuditPassFailReport(runTestId);
				auditCriticalParamsService.deleteAuditCriticalSummaryEntityByRunTestId(runTestId);
				if (!migrationSubType.equalsIgnoreCase("PREAUDIT")) {
					audit5GCBandIssueService.deleteaudit5GCBandIssueEntityByRunTestId(runTestId);
				}

			} else if (programName.contains("5G-DSS")) {
				audit5GDSSSummaryService.deleteAuditSummaryReport(runTestId);
				audit5GDSSSummaryService.deleteAuditPassFailReport(runTestId);
				auditCriticalParamsService.deleteAuditCriticalSummaryEntityByRunTestId(runTestId);
				if (!migrationSubType.equalsIgnoreCase("PREAUDIT")) {
					audit5GDSSIssueService.deleteaudit5GDSSIssueEntityByRunTestId(runTestId);
				}

			} else if (programName.contains("4G-FSU")) {
				audit4GFsuSummaryService.deleteAuditSummaryReport(runTestId);
				audit4GFsuSummaryService.deleteAuditPassFailReport(runTestId);
				auditCriticalParamsService.deleteAuditCriticalSummaryEntityByRunTestId(runTestId);
				if (!migrationSubType.equalsIgnoreCase("PREAUDIT")) {
					audit4GFsuIssueService.deleteaudit4GFsuIssueEntityByRunTestId(runTestId);
				}

			}

			boolean status = runTestService.deleteRunTest(runTestId);

			List<RunTestModel> runTestDetails = runTestService.getRunTestDetails(page, count, customerId, programId,
					migrationType, migrationSubType, history);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("runTestTableDetails", runTestDetails);
			if (status == true) {

				for (String useCase : useCaseList) {
					UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepositoryImpl.getUseCaseByName(useCase,
							migrationType, programId, migrationSubType);
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
								"RunTestController.deleteRunTestData() deleting file/folder currentScriptOutput path: "
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
					logger.info("RunTestController.deleteRunTestData() deleting file/folder GenerateScriptPath: "
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
					logger.info("RunTestController.deleteRunTestData() deleting file/folder OutputFilepath: "
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
					logger.info("RunTestController.deleteRunTestData() deleting file/folder ResultFilePath: "
							+ filePathToRemove);
				}
				resultMap.put("status", Constants.SUCCESS);
				commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
						Constants.ACTION_DELETE, "Run Test Deleted Successfully", sessionId);
			} else {
				resultMap.put("status", Constants.FAIL);
			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.info("Exception in deleteRunTestData() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	/**
	 * This method will show runTestResult
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/ruleResult")
	public JSONObject runTestViewResult(@RequestBody JSONObject runTestParams) {
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		JSONObject result = null;
		String pgmName = null;
		boolean progressStatus = false;
		try {
			sessionId = runTestParams.get("sessionId").toString();
			serviceToken = runTestParams.get("serviceToken").toString();
			runTestId = (Integer) runTestParams.get("testId");
			pgmName = runTestParams.get("programName").toString();
			String neName = runTestParams.get("NEName").toString();
			String smName = runTestParams.get("SMName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			if (runTestEntity.getProgressStatus().equalsIgnoreCase("Completed")) {
				progressStatus = true;
			}

			List<RunTestResultEntity> runTestResultEntityList = runTestService.getRunTestResult(runTestId);
			JSONObject runTestParamsResult = runTestService.getInputRuntestJson(runTestId);
			List<Map> scriptList = null;
			if (null != runTestParamsResult && !runTestParamsResult.isEmpty()
					&& runTestParamsResult.containsKey("runTestFormDetails")) {
				Map run = (Map) runTestParamsResult.get("runTestFormDetails");
				scriptList = (List<Map>) run.get("scripts");
			}
			int count = 0;

			if (scriptList != null) {
				for (Map entry : scriptList) {
					String scriptExeSequence = entry.get("scriptExeSequence").toString();
					if (!scriptExeSequence.equals("0")) {
						count++;
					}
				}
			}
			Integer seq = runTestResultEntityList.get(runTestResultEntityList.size() - 1).getScriptExeSeq();

			result = runTestService.getResult(runTestResultEntityList, neName);
			result.put("CurrentSeq", seq);
			result.put("sessionId", sessionId);
			result.put("serviceToken", serviceToken);
			result.put("programName", pgmName);
			result.put("NEName", neName);
			result.put("SMName", smName);
			result.put("isProcessCompleted", progressStatus);
			result.put("Scriptcount", count);
			if (StringUtils.isNotEmpty(runTestEntity.getFailedScript())
					&& "true".equalsIgnoreCase(runTestEntity.getFailedScript())) {
				result.put("failedScript", true);
			} else {
				result.put("failedScript", false);
			}

		} catch (Exception e) {
			result.put("status", Constants.FAIL);
			logger.info("Exception in runTestViewResult() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return result;

	}

	/**
	 * This method will show runTestResult
	 * 
	 * @param scriptParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/scriptResult")
	public JSONObject scriptResult(@RequestBody JSONObject scriptParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		Integer useCaseId = null;
		Integer scriptId = null;
		String sUseCaseName = null;
		String sScriptName = null;
		try {
			sessionId = scriptParams.get("sessionId").toString();
			serviceToken = scriptParams.get("serviceToken").toString();
			runTestId = Integer.parseInt(scriptParams.get("testId").toString());
			useCaseId = Integer.parseInt(scriptParams.get("UseCaseId").toString());
			scriptId = Integer.parseInt(scriptParams.get("ScriptId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			sUseCaseName = scriptParams.get("UseCaseName").toString();
			sScriptName = scriptParams.get("ScriptName").toString();

			if (expiryDetails != null) {
				return expiryDetails;
			}

			String output = runTestService.getScriptOutput(runTestId, useCaseId, scriptId, sUseCaseName, sScriptName);

			// resultMap = runTestService.getResult(runTestResultEntityList);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("output", output);
			resultMap.put("status", "SUCCESS");

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in scriptResult() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	/**
	 * This method will execute the reRunTest
	 * 
	 * @param runTestParams
	 * @return reRunTest result JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/reRunTest")
	public JSONObject reRunTest(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		int runTestId;
		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			String useCaseDetails = (String) reRunTestParams.get("useCaseDetails");
			runTestId = (int) reRunTestParams.get("runTestId");

			String[] usecases = useCaseDetails.split(",");
			List<LinkedHashMap> useCaseList = new ArrayList<>();
			for (String usecase : usecases) {
				String[] useCaseDetail = usecase.split("\\?");
				String useCaseName = useCaseDetail[0];
				String useCaseId = useCaseDetail[1];
				String useCaseSeq = useCaseDetail[2];
				LinkedHashMap useCaseMap = new LinkedHashMap();
				useCaseMap.put("useCaseId", useCaseId);
				useCaseMap.put("useCaseName", useCaseName);
				useCaseMap.put("executionSequence", useCaseSeq);
				useCaseList.add(useCaseMap);
			}

			RunTestEntity runTestEntity = runTestService.getRunTestEntity(runTestId);

			// runTestService.getRuntestExecResult(useCaseList, runTestEntity,"RERUN"); //
			// uncomment this for rerun

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
					Constants.ACTION_EXECUTE, "Re Run Test Executed Successfully", sessionId);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in reRunTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/forceComplete")
	public JSONObject getForceComplete(@RequestBody JSONObject forceCompleteDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		int runTestId;
		Integer wfmTestId = null;
		// int wfmTestId;
		String status = "";
		String remarks = "";
		int zero = 0;
		boolean progressStatus = false;
		try {
			
			sessionId = forceCompleteDetails.get("sessionId").toString();
			serviceToken = forceCompleteDetails.get("serviceToken").toString();

			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			String currentRole = user.getRole();


			
			
			  resultMap.put("sessionId", sessionId);
			  resultMap.put("serviceToken",serviceToken);
			 
			  
			 
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			runTestId = (int) forceCompleteDetails.get("runTestId");
			status = (String) forceCompleteDetails.get("postMigStatus");
			remarks = (String) forceCompleteDetails.get("remarks");

			if (forceCompleteDetails.containsKey("wfmId") && null != forceCompleteDetails.get("wfmId")) {
				wfmTestId = (int) forceCompleteDetails.get("wfmId");
			}

			User user1 = UserSessionPool.getInstance().getSessionUser(sessionId);
			System.out.println(user1.toString());
			Integer currentRole1 = user1.getRoleId();
			if (!currentRole.equalsIgnoreCase("Commission Manager")) {

				String reason = GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNAUTHORIZED_ACCESS);
				resultMap.put(status, "Fail");
				resultMap.put(reason, reason);

			} else {

				JSONObject runTestResultEntityList = runTestService.getForceRunTestResult(status, runTestId);

				// if (wfmTestId != 0) {
				JSONObject wfhResultEntityList = runTestService.getWFMRunTestResult(status, wfmTestId);
				// }
				resultMap.put("status", Constants.SUCCESS);
				String s = "Successfully Status is changed";
						

				resultMap.put("reason","Successfully Status is changed");

			}
		} catch (Exception e) {
			/*
			 * logger.error("Exception ForceController  " +
			 * ExceptionUtils.getFullStackTrace(e)); resultMap.put("sessionId", sessionId);
			 * resultMap.put("serviceToken", serviceToken); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason", "Failed");
			 */
		}
		return resultMap;
	}

	/**
	 * This method will load running logs
	 * 
	 * @param loadRunningLogParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getRunningLogs")
	public JSONObject getRunningLogs(@RequestBody JSONObject loadRunningLogParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		int runTestId;
		boolean progressStatus = false;
		try {
			sessionId = loadRunningLogParams.get("sessionId").toString();
			serviceToken = loadRunningLogParams.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			runTestId = (int) loadRunningLogParams.get("runTestId");

			String filePath = runTestService.loadRunningLog(runTestId);
			if (filePath.contains("Premigration")) {
				filePath = filePath.replace("Premigration", "PreMigration");
			}
			if (filePath.contains("preaudit")) {
				filePath = filePath.replace("preaudit", "PREAUDIT");
			}

			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			if (runTestEntity.getProgressStatus().equalsIgnoreCase("Completed")) {
				progressStatus = true;
			}

			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath;
			String output = runTestService.readOutputFile(filePath);

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("runningLog", output);
			resultMap.put("isProcessCompleted", progressStatus);
			// commonUtil.saveAudit(Constants.EVENT_MIGRATION,
			// Constants.EVENT_MIGRATION_RUN_TEST,
			// Constants.ACTION_EXECUTE, "Show Running Log Successfully", sessionId);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in loadRunningLog() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.LOAD_FAILED));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getneoutputlog")
	public JSONObject getneoplogs(@RequestBody JSONObject loadRunningLogParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		int runTestId;
		boolean progressStatus = false;
		try {
			sessionId = loadRunningLogParams.get("sessionId").toString();
			serviceToken = loadRunningLogParams.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			runTestId = (int) loadRunningLogParams.get("runTestId");

			String filePath = runTestService.loadRunningLog(runTestId);
			String nefilePath = runTestService.loadneOplogs(runTestId);
			if (filePath.contains("Premigration")) {
				filePath = filePath.replace("Premigration", "PreMigration");
			}
			if (filePath.contains("preaudit")) {
				filePath = filePath.replace("preaudit", "PREAUDIT");
			}

			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			if (runTestEntity.getProgressStatus().equalsIgnoreCase("Completed")) {
				progressStatus = true;
			}

			nefilePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + nefilePath;
			String output = runTestService.readOutputFile(nefilePath);

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("neOutputLog", output);
			resultMap.put("isProcessCompleted", progressStatus);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in loadRunningLog() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.LOAD_FAILED));
		}
		return resultMap;

	}

	/**
	 * This method will load check connection logs
	 * 
	 * @param loadConnectionLogParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getConnectionLog")
	public JSONObject getConnectionLog(@RequestBody JSONObject loadConnectionLogParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = loadConnectionLogParams.get("sessionId").toString();
			serviceToken = loadConnectionLogParams.get("serviceToken").toString();

			int programId = (int) loadConnectionLogParams.get("programId");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			String migrationType = loadConnectionLogParams.get("migrationType").toString();
			String subType = loadConnectionLogParams.get("migrationSubType").toString();

			if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
				migrationType = "Migration";
			} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
				migrationType = "PostMigration";
			} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
				migrationType = "PreMigration";
			}

			StringBuilder outputFilePath = new StringBuilder();

			outputFilePath = outputFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR).append(migrationType).append(Constants.CHECK_CONNECTION_OUTPUT)
					.append(Constants.SEPARATOR);

			String outputFileName = outputFilePath + sessionId + "_output.txt";

			// to check Ne connection

			String output = runTestService.readOutputFile(outputFileName);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("connectionLog", output);
			commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
					Constants.ACTION_EXECUTE, "Show check connection Log Successfully", sessionId);
		} catch (Exception e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in getConnectionLog() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.LOAD_FAILED));
		}
		return resultMap;

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/downloadScripts")
	public void downloadScripts(@RequestBody JSONObject fileDetails, HttpServletResponse response) throws IOException {
		String generateScriptPath = null;
		String sessionId = null;
		String serviceToken = null;
		String filePath = null;
		try {
			sessionId = fileDetails.get("sessionId").toString();
			serviceToken = fileDetails.get("serviceToken").toString();
			generateScriptPath = fileDetails.get("generateScriptPath").toString();
			FileInputStream inputStream = null;
			OutputStream outStream = null;
			File downloadFile = null;
			String fileAbsolutepath = "";
			String zipFileFolderpath = "";
			String zipFilepath = "";
			File zipFileDir = null;
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			try {
				if (generateScriptPath.contains(",")) {
					zipFileFolderpath = filePath + "download";
					String filenames[] = generateScriptPath.split(",");
					zipFileDir = new File(zipFileFolderpath);
					if (!zipFileDir.exists()) {
						FileUtils.forceMkdir(zipFileDir);
					}
					for (String name : filenames) {
						if (CommonUtil.isValidObject(name) && name.trim().length() > 0) {
							File file = new File(filePath + name);
							FileUtils.copyFileToDirectory(file, zipFileDir);
						}
					}
					zipFilepath = zipFileFolderpath + ".zip";
					boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath.toString(), zipFileFolderpath);
					if (!status) {
						logger.info("downloadScripts() file not found:" + fileAbsolutepath);
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						out.println("{\"status\": \"File Not Found\"}");
						out.close();
						return;
					}
					downloadFile = new File(zipFilepath);
					fileAbsolutepath = zipFilepath;
				} else {
					fileAbsolutepath = filePath + generateScriptPath;
					downloadFile = new File(fileAbsolutepath);
				}
				logger.info("downloadScripts() file path:" + fileAbsolutepath);

				if (!downloadFile.exists()) {
					logger.info("downloadScripts() file not found:" + fileAbsolutepath);
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
						"Exception in RunTestController.downloadScripts(): " + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error(
						"Exception in RunTestController.downloadScripts(): " + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception in downloadScripts :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}
	}

	/**
	 * This method will download logs
	 * 
	 * @param downloadLogParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/downloadLogs")
	public void downloadLogFile(@RequestBody JSONObject fileDetails, HttpServletResponse response) throws IOException {
		String filePath = null;
		int runTestId;
		try {
			runTestId = Integer.parseInt(fileDetails.get("runTestId").toString());
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");
			FileInputStream inputStream = null;
			String migrationSubtype = null;
			try {

				List<RunTestResultEntity> runTestResultEntity = runTestService.getRunTestResult(runTestId);
				String srcFilePath = null;
				for (RunTestResultEntity runTestResult : runTestResultEntity) {
					srcFilePath = runTestResult.getCurrentScriptOutput();
					migrationSubtype = runTestResult.getMigrationSubType();
				}

				String[] zipFileName = srcFilePath.split("/");
				int fileLength = zipFileName.length;

				String sZipFileName = zipFileName[fileLength - 1];

				srcFilePath = filePath + srcFilePath;

				String desZipPath = filePath + "Download";
				File folder = new File(desZipPath);

				if (!folder.exists()) {
					FileUtil.createDirectory(desZipPath);
				}
				String finalZipFilePath = desZipPath + "/" + sZipFileName + ".zip";
				if ("RANATP".equalsIgnoreCase(migrationSubtype)) {
					String desZipPath1 = filePath + "Download" + File.separator + sZipFileName;
					File folder1 = new File(desZipPath1);

					if (!folder1.exists()) {
						FileUtil.createDirectory(desZipPath1);
					}
					File srcFilePathFolder = new File(srcFilePath);

					String[] children1 = srcFilePathFolder.list();
					if (children1 != null && children1.length > 0) {
						for (String name : children1) {
							if (CommonUtil.isValidObject(name) && name.trim().length() > 0) {
								File file = new File(srcFilePathFolder + File.separator + name);
								FileUtils.copyFileToDirectory(file, folder1);
							}
						}
					}

					// FileUtils.copyFileToDirectory(srcFilePathFolder, folder1);
					File parentFile = srcFilePathFolder.getParentFile();
					File[] parentFiles = srcFilePathFolder.getParentFile().listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {

							if (name.startsWith("Debug") || name.startsWith("POWER") || name.startsWith("RTRV")) {
								return true;
							}

							return false;

						}
					});

					if (parentFiles != null) {
						Arrays.sort(parentFiles, Comparator.comparingLong(File::lastModified).reversed());
						boolean debug = false;
						boolean power = false;
						boolean rtrv = false;
						for (File fileDetailName : parentFiles) {
							String filename = fileDetailName.getName();
							if (filename.startsWith("Debug") && !debug) {
								FileUtils.copyFileToDirectory(fileDetailName, folder1);
								debug = true;
							} else if (filename.startsWith("POWER") && !power) {
								FileUtils.copyFileToDirectory(fileDetailName, folder1);
								power = true;
							} else if (filename.startsWith("RTRV") && !rtrv) {
								FileUtils.copyFileToDirectory(fileDetailName, folder1);
								rtrv = true;
							}

						}

					}

					// Arrays.sort(parentFiles,LastModified);
					/*
					 * String[] children = parentFile.list(new FilenameFilter() {
					 * 
					 * @Override public boolean accept(File dir, String name) {
					 * 
					 * if
					 * (name.startsWith("Debug")||name.startsWith("POWER")||name.startsWith("RTRV"))
					 * { return true; }
					 * 
					 * return false;
					 * 
					 * } }); if(children!=null && children.length>0) { for (String name : children)
					 * { if (CommonUtil.isValidObject(name) && name.trim().length() > 0) { File file
					 * = new File(parentFile +File.separator+ name);
					 * FileUtils.copyFileToDirectory(file, folder1); } } }
					 */

					CommonUtil.createZipFileOfDirectory(finalZipFilePath, desZipPath1);
				} else

				{

					CommonUtil.createZipFileOfDirectory(finalZipFilePath, srcFilePath);
				}
				// downloading the zip
				File file = new File(finalZipFilePath);
				if (!file.exists()) {
					// System.out.println("file not found");
				}
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + desZipPath + "\"");

				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.flush();

				if (CommonUtil.isValidObject(finalZipFilePath)) {
					FileUtil.deleteFileOrFolder(finalZipFilePath);
					FileUtil.deleteFileOrFolder(desZipPath);
				}

			} catch (FileNotFoundException fne) {
				logger.error("Exception in downloadFile :" + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(e));
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
							"Exception in downloadFile() RunTestController:" + ExceptionUtils.getFullStackTrace(e));
				}

			}
		} catch (Exception e) {
			logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}
	}

	/**
	 * This method will download logs
	 * 
	 * @param downloadLogParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/downloadHtml")
	public void downloadHtmlFile(@RequestBody JSONObject fileDetails, HttpServletResponse response) throws IOException {
		String fileName = null;
		String filePath = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = fileDetails.get("sessionId").toString();
			serviceToken = fileDetails.get("serviceToken").toString();
			fileName = fileDetails.get("fileName").toString();
			// filePath = fileDetails.get("filePath").toString();
			String[] fileExtension = fileName.split("\\.");

			if ("html".equalsIgnoreCase(fileExtension[1])) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "AuditTest/" + fileName;
			} else if ("xlsx".equalsIgnoreCase(fileExtension[1])) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + "ATPTest/" + fileName;
			}

			FileInputStream inputStream = null;
			OutputStream outStream = null;
			File downloadFile = null;
			String fileAbsolutepath = "";
			String zipFileFolderpath = "";
			String zipFilepath = "";
			try {

				if (filePath.endsWith(Constants.SEPARATOR)) {
					fileAbsolutepath = filePath + fileName;
					downloadFile = new File(fileAbsolutepath);
				} else {
					fileAbsolutepath = filePath;
					downloadFile = new File(fileAbsolutepath);
				}

				logger.info("downloadFile() file path:" + fileAbsolutepath);

				if (!downloadFile.exists()) {
					logger.info("downloadFile() file not found:" + fileAbsolutepath);
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
				logger.error("Exception in downloadFile :" + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(e));
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
							"Exception in downloadFile() RunTestController:" + ExceptionUtils.getFullStackTrace(e));
				}

			}
		} catch (Exception e) {
			logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/checkConnection")
	public JSONObject checkConnection(@RequestBody JSONObject connectionParams) {

		JSONObject resultMap = new JSONObject();

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		boolean useCurrPassword;
		try {

			Map run = (Map) connectionParams.get("runTestFormDetails");
			String sanePassword = run.get("password").toString();
			useCurrPassword = (boolean) run.get("currentPassword");
			sessionId = connectionParams.get("sessionId").toString();
			serviceToken = connectionParams.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			// List<String> neList = (List) run.get("neName");

			JSONObject output = new JSONObject();
			if (useCurrPassword == false && sanePassword.isEmpty()) {

				output = runTestService.getSaneDetailsforPassword(connectionParams);
				if (!output.isEmpty()) {
					resultMap.put("status", "PROMPT");
					resultMap.put("password", output);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("requestType", "CHECK_CONNECTION");
					return resultMap;
				}
			}
			String result = runTestService.getConnection(connectionParams);

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("requestType", "CHECK_CONNECTION");

		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}

		catch (Exception e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in checkConnection() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}

		return resultMap;
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/fetchBandName")
	public JSONObject getBandName(@RequestBody JSONObject bandNameParams) {

		JSONObject resultMap = new JSONObject();

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		String enbId;
		String ciqFileName;
		String programId;
		try {

			sessionId = bandNameParams.get("sessionId").toString();
			serviceToken = bandNameParams.get("serviceToken").toString();
			enbId = bandNameParams.get("eNBId").toString();
			ciqFileName = bandNameParams.get("ciqName").toString();
			programId = bandNameParams.get("programId").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			String[] bandNames = runTestService.getBandName(enbId, ciqFileName, programId);
			List<String> newBands = new ArrayList<String>();

			if (CommonUtil.isValidObject(bandNames)) {
				for (String bandName : bandNames) {
					if (bandName.contains("MHz") || bandName.contains("LTE")) {
						bandName = bandName.replaceAll("MHz", "").replaceAll("LTE", "");
					}
					if (bandName.contains("AWS-1") || bandName.contains("AWS-2") || bandName.contains("AWS-3")) {
						bandName = "AWS";
					}
					/*
					 * if (bandName.contains("AWS-3")) { bandName = "AWS-3"; }
					 */
					if (bandName.contains("PCS")) {
						bandName = "PCS";
					}
					if (bandName.contains("AWS3")) {
						bandName = "AWS3";
					}

					if (bandName.contains("850")) {
						bandName = "850";
					}

					if (bandName.contains("700")) {
						bandName = "700";
					}
					if (!CommonUtil.isValidObject(newBands) || !newBands.contains(bandName)) {
						newBands.add(bandName);
					}
				}
			}

			if (CommonUtil.isValidObject(newBands) && newBands.size() > 0) {
				String[] newBandNames = newBands.stream().toArray(String[]::new);
				resultMap.put("bandName", newBandNames);
			} else {
				resultMap.put("bandName", new String[0]);
			}

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);

		} catch (Exception e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in getBandName() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}

		return resultMap;

	}

	/**
	 * This method will return NeConfigDetails
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getNeConfigDetails")
	public JSONObject getNeConfigDetails(@RequestBody JSONObject enbParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = enbParams.get("sessionId").toString();
			serviceToken = enbParams.get("serviceToken").toString();

			Map run = (Map) enbParams.get("enbParamsDetails");
			String enbId = run.get("enbId").toString();
			int programId = (int) run.get("programId");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			NeMappingModel neMappingModelversion = new NeMappingModel();
			CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
			programDetailsEntityversion.setId(programId);
			neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
			neMappingModelversion.setEnbId(enbId);
			NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
					.getNetWorkEntityDetails(neMappingModelversion);

			if (!ObjectUtils.isEmpty(neMappingEntitiesForVersion)) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("lsmVersion", neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion());
				resultMap.put("lsmName", neMappingEntitiesForVersion.getNeName());
				resultMap.put("lsmId", neMappingEntitiesForVersion.getId());
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			} else {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason", "Ne Not Mapped");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	/**
	 * This method will return Migrationtion UseCases
	 * 
	 * @param runTestParams
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getMigUseCases")
	public JSONObject getMigUseCases(@RequestBody JSONObject enbParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String migrationType = null;
		String migrationSubType = null;
		int programId;
		Integer history;
		String searchStatus = null;
		String toDate = null;
		String fromDate = null;
		String ciqFileName = null;
		List<String> enbId = null;
		String programName = null;
		try {
			sessionId = enbParams.get("sessionId").toString();
			serviceToken = enbParams.get("serviceToken").toString();
			customerId = (Integer) enbParams.get("customerId");
			migrationType = enbParams.get("migrationType").toString();
			migrationSubType = enbParams.get("migrationSubType").toString();
			programId = (Integer) enbParams.get("programId");
			ciqFileName = enbParams.get("ciqFileName").toString();
			enbId = (List) enbParams.get("enbID");
			programName = enbParams.get("programName").toString();
			// searchStatus = enbParams.get("searchStatus").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = StringUtils.substringBeforeLast(ciqFileName, ".");
			}
			if (migrationType.equalsIgnoreCase("PreMigration")) {
				migrationType = "Premigration";
			}
			if (migrationSubType.equalsIgnoreCase("NESTATUS")) {
				migrationSubType = "NESTATUS";
			}

			List<Map> useCaseLst = null;
			if (migrationType.equalsIgnoreCase("PostMigration") && migrationSubType.equalsIgnoreCase("Audit")) {
				useCaseLst = runTestService.getPostMigrationUseCaseList(programId, migrationType, migrationSubType,
						enbId);

			} else if (migrationType.equalsIgnoreCase("premigration")
					&& ((migrationSubType.equalsIgnoreCase("PREAUDIT"))
							|| (migrationSubType.equalsIgnoreCase("NESTATUS")))) {
				useCaseLst = runTestService.getPostMigrationUseCaseList(programId, migrationType, migrationSubType,
						enbId);

			} else {
				useCaseLst = runTestService.getMigrationUseCaseList(programId, migrationType, migrationSubType,
						ciqFileName, enbId, programName);
			}
			if (programName.contains("5G-MM") && migrationType.equalsIgnoreCase("PostMigration")) {
				useCaseLst = reorderUseCaseList(useCaseLst);
			}
			if (!ObjectUtils.isEmpty(useCaseLst) || (migrationType.equalsIgnoreCase("PostMigration")
					&& migrationSubType.equalsIgnoreCase("Audit") && useCaseLst != null)) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("useCaseList", useCaseLst);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "UseCases Not Mapped");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
			}

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	public List<Map> reorderUseCaseList(List<Map> useCaseLst) {
		List<Map> ucbMapList = new ArrayList<>();
		List<Map> ucbMapListENDC = new ArrayList<>();
		for (Map ucbMaptem : useCaseLst) {
			if (ucbMaptem.get("useCaseName").toString().toUpperCase().contains("ENDC")) {
				ucbMapListENDC.add(ucbMaptem);
			} else {
				ucbMapList.add(ucbMaptem);
			}
		}
		ucbMapList.addAll(ucbMapListENDC);
		return ucbMapList;
	}

	@PostMapping(value = "/messageInfo")
	public JSONObject messageInfo(@RequestBody JSONObject messageInfoParams) {
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		JSONObject resultMap = new JSONObject();
		List result = new ArrayList<>();
		try {
			sessionId = messageInfoParams.get("sessionId").toString();
			serviceToken = messageInfoParams.get("serviceToken").toString();
			runTestId = (Integer) messageInfoParams.get("testId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			List<RunTestResultEntity> runTestResultEntityList = runTestService.getRunTestResult(runTestId);
			result = runTestService.getMessageInfo(runTestResultEntityList);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("messageInfo", result);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	@PostMapping(value = "/messageInfoOV")
	public JSONObject messageInfoOV(@RequestBody JSONObject messageInfoParams) {
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		JSONObject resultMap = new JSONObject();
		// List result = new ArrayList<>();
		try {
			sessionId = messageInfoParams.get("sessionId").toString();
			serviceToken = messageInfoParams.get("serviceToken").toString();
			runTestId = (Integer) messageInfoParams.get("testId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			runTestId = (int) messageInfoParams.get("testId");
			List<OvTestResultEntity> runTestResultEntityList = runTestService.getOvRunTestResult(runTestId);
			List<RunTestModel> objRunTestModelList = new ArrayList<>();
			List<String> objRunTestModelList1 = new ArrayList<>();
			String useCaseList = messageInfoParams.get("useCaseName").toString();
			/*
			 * if (useCaseList.contains("rssiImbalance_RangeCheck")) {
			 * 
			 * String mileStone="P_PREEXISTING_RSSI"; for (OvTestResultEntity entity :
			 * runTestResultEntityList) { String MileStone2=entity.getMilestone();
			 * if(mileStone.equals(MileStone2)) { RunTestModel runModel = new
			 * RunTestModel(); runModel.setName(MileStone2); String resultout=
			 * entity.getCurrentResult(); if(resultout.contains("Successfully")) {
			 * runModel.setStatus("pass"); }else { runModel.setStatus("fail"); }
			 * objRunTestModelList.add(runModel); break; } }
			 * 
			 * }else { String
			 * migrationSubType=messageInfoParams.get("migrationSubType").toString(); int
			 * programId = Integer.parseInt(messageInfoParams.get("programId").toString());
			 * String orderNo; if(migrationSubType.equalsIgnoreCase("AUDIT")) {
			 * ProgramTemplateEntity programTemplateEntity =
			 * fileUploadService.getProgramTemplate(programId,
			 * Constants.POST_MIGRATION_MILESTONE);
			 * orderNo=programTemplateEntity.getValue(); }else { ProgramTemplateEntity
			 * programTemplateEntity = fileUploadService.getProgramTemplate(programId,
			 * Constants.MIGRATION_MILESTONE);
			 * orderNo=programTemplateEntity.getValue().toString(); } String[]
			 * Mile=orderNo.split(",");
			 * 
			 * //List<RunTestResultEntity> runTestResultEntityList =
			 * runTestService.getRunTestResult(runTestId);
			 * 
			 * for(int i=0;i<=Mile.length-1;i++) { String mileStone=Mile[i]; for
			 * (OvTestResultEntity entity : runTestResultEntityList) { String
			 * MileStone2=entity.getMilestone(); if(mileStone.equals(MileStone2)) {
			 * RunTestModel runModel = new RunTestModel(); runModel.setName(MileStone2);
			 * String resultout= entity.getCurrentResult();
			 * if(resultout.contains("Successfully")) { runModel.setStatus("pass"); }else {
			 * runModel.setStatus("fail"); } objRunTestModelList.add(runModel); break; } }
			 * 
			 * } }
			 */
			for (OvTestResultEntity entity : runTestResultEntityList) {
				String MileStone2 = entity.getMilestone();

				RunTestModel runModel = new RunTestModel();
				runModel.setName(MileStone2);
				String resultout = entity.getCurrentResult();
				if (resultout.contains("Successfully")) {
					runModel.setStatus("pass");
				} else {
					runModel.setStatus("fail");
				}
				if (!objRunTestModelList1.contains(MileStone2)) {
					objRunTestModelList1.add(MileStone2);
					objRunTestModelList.add(runModel);
				}

			}
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (OvTestResultEntity entity : runTestResultEntityList) {
				String resultout = entity.getCurrentResult();
				sb.append(resultout);
				sb.append("\n");
			}
			String result = sb.toString();

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("milestones", objRunTestModelList);
			resultMap.put("history", result);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	@PostMapping(value = "/messageInfoOVpre")
	public JSONObject messageInfoOVpre(@RequestBody JSONObject messageInfoParams) {
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		JSONObject resultMap = new JSONObject();
		// List result = new ArrayList<>();
		try {
			sessionId = messageInfoParams.get("sessionId").toString();
			serviceToken = messageInfoParams.get("serviceToken").toString();
			runTestId = (Integer) messageInfoParams.get("testId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			runTestId = (int) messageInfoParams.get("testId");
			List<PremigrationOvUpadteEntity> runTestResultEntityList = runTestService.getOvRunTestResultpre(runTestId);
			List<RunTestModel> objRunTestModelList = new ArrayList<>();
			// String useCaseList = messageInfoParams.get("useCaseName").toString();

			for (PremigrationOvUpadteEntity entity : runTestResultEntityList) {
				String MileStone2 = entity.getFileName();

				RunTestModel runModel = new RunTestModel();
				runModel.setName(MileStone2);
				String resultout = entity.getCurrentResult();
				if (resultout.contains("Successfully")) {
					runModel.setStatus("pass");
				} else {
					runModel.setStatus("fail");
				}
				objRunTestModelList.add(runModel);

				break;
			}

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (PremigrationOvUpadteEntity entity : runTestResultEntityList) {
				String resultout = entity.getCurrentResult();
				sb.append(resultout);
				sb.append("\n");
			}
			String result = sb.toString();

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("milestones", objRunTestModelList);
			resultMap.put("history", result);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will load running logs
	 * 
	 * @param displayFailureLogs
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/geterrorLogs")
	public JSONObject getDisplayFailureLogs(@RequestBody JSONObject displayFailureParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		int wfmid = -1;
		String type = "";
		String migtype = null;
		boolean progressStatus = false;
		try {
			sessionId = displayFailureParams.get("sessionId").toString();
			serviceToken = displayFailureParams.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (displayFailureParams.containsKey("wfmid")) {
				wfmid = (Integer) displayFailureParams.get("wfmid");
			}
			migtype = displayFailureParams.get("type").toString();
			if (migtype != null && migtype.equalsIgnoreCase("premigration")) {
				type = displayFailureParams.get("preErrorFile").toString();
			} else if (migtype != null && migtype.equalsIgnoreCase("negrow")) {
				type = displayFailureParams.get("neGrowErrorFile").toString();
			} else if (migtype != null && migtype.equalsIgnoreCase("migration")) {
				type = displayFailureParams.get("migErrorFile").toString();
			} else if (migtype != null && migtype.equalsIgnoreCase("postmigration")) {
				type = displayFailureParams.get("postErrorFile").toString();
			} else if (migtype != null && migtype.equalsIgnoreCase("PreAudit")) {
				type = displayFailureParams.get("preAuditErrorFile").toString();
			} else if (migtype != null && migtype.equalsIgnoreCase("nestatus")) {
				type = displayFailureParams.get("neStatusErrorFile").toString();
			}
			String filePath = type;
			String output = null;
			if (migtype != null && (migtype.equalsIgnoreCase("migration") || migtype.equalsIgnoreCase("negrow"))) {
				File file = new File(filePath);
				if (!file.exists() && wfmid >= 0) {
					WorkFlowManagementEntity workManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(wfmid);
					if (migtype.equalsIgnoreCase("migration")) {
						RunTestEntity runMigEntity = workManagementEntity.getRunMigTestEntity();
						if (runMigEntity.getId() > 0) {
							output = writeMessageInfo("MIGRATION", runMigEntity.getId(), filePath);
						}
					} else {
						RunTestEntity runNeGrowEntity = workManagementEntity.getRunNEGrowEntity();
						if (runNeGrowEntity.getId() > 0) {
							output = writeMessageInfo("NEGROW", runNeGrowEntity.getId(), filePath);
						}
					}
				} else {
					output = runTestService.readOutputFile(filePath);
				}
			} else if (migtype != null && migtype.equalsIgnoreCase("postmigration")) {
				File file = new File(filePath);
				if (file.exists() && wfmid >= 0) {
					WorkFlowManagementEntity workManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(wfmid);
					RunTestEntity runPostMigEntity = workManagementEntity.getRunPostMigTestEntity();
					if (runPostMigEntity != null && runPostMigEntity.getId() > 0) {
						output = runTestService.readOutputFile(filePath) + "\n"
								+ writeMessageInfo("", runPostMigEntity.getId(), filePath);
					} else {
						output = runTestService.readOutputFile(filePath);
					}
				}
			} else if (migtype != null && migtype.equalsIgnoreCase("PreAudit")) {
				File file = new File(filePath);
				if (file.exists() && wfmid >= 0) {
					WorkFlowManagementEntity workManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(wfmid);
					RunTestEntity runPostMigEntity = workManagementEntity.getRunPreAuditTestEntity();
					if (runPostMigEntity != null && runPostMigEntity.getId() > 0) {
						output = runTestService.readOutputFile(filePath) + "\n"
								+ writeMessageInfo("", runPostMigEntity.getId(), filePath);
					} else {
						output = runTestService.readOutputFile(filePath);
					}
				}
			} else if (migtype != null && migtype.equalsIgnoreCase("nestatus")) {
				File file = new File(filePath);
				if (file.exists() && wfmid >= 0) {
					WorkFlowManagementEntity workManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(wfmid);
					RunTestEntity runPostMigEntity = workManagementEntity.getRunNEStatusTestEntity();
					if (runPostMigEntity != null && runPostMigEntity.getId() > 0) {
						output = runTestService.readOutputFile(filePath) + "\n"
								+ writeMessageInfo("", runPostMigEntity.getId(), filePath);
					} else {
						output = runTestService.readOutputFile(filePath);
					}
				}
			} else {
				output = runTestService.readOutputFile(filePath);
			}
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("errorLogs", output);
			resultMap.put("isProcessCompleted", progressStatus);
			// commonUtil.saveAudit(Constants.EVENT_MIGRATION,
			// Constants.EVENT_MIGRATION_RUN_TEST,
			// Constants.ACTION_EXECUTE, "Show Display failure Log Successfully",
			// sessionId);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in Display failure in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.LOAD_FAILED));
		}
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String writeMessageInfo(String type, Integer runtestId, String checkPath) {
		StringBuilder output = new StringBuilder(type + "\n" + "--------------------------" + "\n");
		try {
			List<RunTestResultEntity> runTestResultEntityList = runTestService.getRunTestResult(runtestId);
			List<Map> result = runTestService.getMessageInfo(runTestResultEntityList);
			int count = 1;
			for (Map scriptinfo : result) {
				if (!scriptinfo.isEmpty()) {
					output.append(count + ". " + scriptinfo.get("scriptName") + "\n" + "--------------------------"
							+ "\n" + scriptinfo.get("scriptOutput") + "\n" + "--------------------------" + "\n"
							+ "Info: " + scriptinfo.get("saneIssue").toString().toUpperCase() + "\n");
					count++;
				}
			}
		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementServiceImpl writeMessageInfo() "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return output.toString();
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/getDuoSessionStatus")
	public JSONObject getDuoSessionStatus(@RequestBody JSONObject duoSessionStatusParams) {
		JSONObject resultMap = new JSONObject();
		resultMap.put("isDuoSessionConnected", false);
		JSONObject expiryDetails = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		List<String> neIdsConnected = new ArrayList<String>();
		try {
			sessionId = duoSessionStatusParams.get("sessionId").toString();
			serviceToken = duoSessionStatusParams.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			UserDetailsEntity userEntity = userActionRepositoryImpl.getUserDetailsBasedName(user.getUserName());
			for (Entry<String, SocketSession> entry : GlobalStatusMap.socketSessionUser.entrySet()) {
				String key = entry.getKey().toString();

				if (key.contains(userEntity.getVpnUserName())) {
					SocketSession ses = GlobalStatusMap.socketSessionUser.get(key);
					if (ses.isConnectedSession()) {
						resultMap.put("isDuoSessionConnected", true);
						break;
					}
				}
			}
			for (Entry<String, SocketSession> entry : GlobalStatusMap.socketSessionUser.entrySet()) {
				String key = entry.getKey().toString();
				if (key.contains(userEntity.getVpnUserName()) && !key.equals(userEntity.getVpnUserName())) {
					neIdsConnected.add(key.replace(userEntity.getVpnUserName() + "_", ""));
				}
			}
			resultMap.put("neIdsConnected", neIdsConnected);

			/*
			 * if
			 * (GlobalStatusMap.socketSessionUser.containsKey(userEntity.getVpnUserName()))
			 * { SocketSession ses =
			 * GlobalStatusMap.socketSessionUser.get(userEntity.getVpnUserName()); if
			 * (ses.isConnectedSession()) { resultMap.put("isDuoSessionConnected", true); }
			 * }
			 */

		} catch (Exception e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	public JSONObject ConstantScriptCBand(int programId, String ciqFName, String neId, String sessionId,
			String constantFile, String connectionLocation, String configType, boolean addDefaultCurlRule,
			String csvPath) {

		ArrayList<String> bashfilename = new ArrayList();
		ArrayList<String> array = new ArrayList<>();

		try {
			ciqFName = StringUtils.substringBeforeLast(ciqFName, ".");
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));

			/*
			 * int programId = 3; String ciqFName = "UNY-NE-VZ_CIQ_Ver2.91_04012019"; String
			 * neId = "57170"; String sessionId =runTestParams.get("sessionId").toString();
			 * String constantFile = "RF";
			 */

			StringBuilder constantScriptsFilePath = new StringBuilder();
			StringBuilder dBconstantScriptsFilePath = new StringBuilder();

			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);

			String basePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");

			String useCaseName = null;

			if ("GROWVDU".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowvDUBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowvDUBashFile/");
				;

				useCaseName = Constants.GROWVDUUSECASE + "_" + neId + dateString;
			}
			if ("GROWVDUCELL".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowvDUCELLBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowvDUELLBashFile/");
				;

				useCaseName = Constants.GROWVDUCELLUSECASE + "_" + neId + dateString;
			}
			if ("GROWVDUPNP".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowvDUpnpBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowvDUpnpBashFile/");
				;

				useCaseName = Constants.GROWVDUPNPUSECASE + "_" + neId + dateString;
			}
			if ("GROWDSSPNP".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowDSSPNPBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowDSSPNPBashFile/");
				;

				useCaseName = Constants.GROWDSSPNPUSECASE + "_" + neId + dateString;
			}
			if ("GROWDSSAU".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowDSSAUBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowDSSAUBashFile/");

				useCaseName = Constants.GROWDSSAUUSECASE + "_" + neId + dateString;
			}
			if ("GROWDSSAUPF".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("GrowDSSAUPFBashFile/");

				dBconstantScriptsFilePath.append(csvPath).append("GrowDSSAUPFBashFile/");
				;

				useCaseName = Constants.GROWDSSAUPFUSECASE + "_" + neId + dateString;
			}

			logger.info("RunTestController.ConstantScript useCaseName: {}", useCaseName);

			File directory = new File(constantScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				for (String singleFile : filename) {
					if (constantFile.equalsIgnoreCase("GROWVDU")) {
						if (singleFile.contains("vDU_Grow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWVDUPNP")) {
						if (singleFile.contains("vDU_pnp")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWVDUCELL")) {
						if (singleFile.contains("vDU_Cell")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWDSSPNP")) {
						if (singleFile.contains("pnpGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWDSSAU")) {
						if (singleFile.contains("vDUCellGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("GROWDSSAUPF")) {
						if (singleFile.contains("vDUGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					}

				}
			}
			List<UploadFileEntity> uploadFileEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			ArrayList fileNameList = new ArrayList();
			if (!uploadFileEntity.isEmpty()) {
				for (UploadFileEntity SingleUploadFileEntity : uploadFileEntity) {
					fileNameList.add(SingleUploadFileEntity.getFileName());
				}
			}

			// Delete upload script
			ArrayList<Integer> number = new ArrayList<>();
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<RunTestEntity> runTestEntity = useCaseBuilderService.getRunTestDetails(useCaseName);
			if (CommonUtil.isValidObject(runTestEntity)) {
				for (RunTestEntity runTestEntitys : runTestEntity) {
					if (runTestEntitys.getStatus().equalsIgnoreCase("InProgress")) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
										+ " as NE is in progress",
								sessionId, "");
					} else {
						useCaseBuilderRepository.deleteruntestResult(useCaseBuilder.getId());
					}
				}

			}

			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());

			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {

				UploadFileEntity deleteUploadFileEntity = uploadFileService
						.getUploadScriptByPath(dBconstantScriptsFilePath, uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity)
						&& deleteUploadFileEntity.getFileName().contains(dateString)) {
					if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
						if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
								&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
							List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
									.getUseCaseXmlRuleEntityList(useCaseBuilderParamEntity.getId());
							if (CommonUtil.isValidObject(useCaseXmlRuleEntity)) {
								for (UseCaseXmlRuleEntity useCaseXmlRuleEntitys : useCaseXmlRuleEntity) {
									useCaseBuilderRepository.deleteUseCaseXmlRule(useCaseXmlRuleEntitys.getId());
								}
							}
							useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
						}
					}
					uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
				}
			}
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				useCaseBuilderRepository.deleteUseCaseBuilder(useCaseBuilder.getId());
			}

			for (String file : bashfilename) {

				UploadFileEntity createUploadFileEntity = new UploadFileEntity();
				createUploadFileEntity.setFileName(file);
				createUploadFileEntity.setFilePath(dBconstantScriptsFilePath.toString());
				createUploadFileEntity.setNeListEntity(null);
				createUploadFileEntity.setUploadedBy(Constants.UPLOADED_BY);
				createUploadFileEntity.setRemarks(Constants.REMARKS);
				createUploadFileEntity.setUseCount(0);
				createUploadFileEntity.setCustomerId(2);
				createUploadFileEntity.setCreationDate(new Date());
				createUploadFileEntity.setProgram(customerDetailsEntity.getProgramName());
				// createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
				createUploadFileEntity.setState(Constants.STATE);
				// createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				createUploadFileEntity.setMigrationType(Constants.PREMIGRATION_TYPE);
				createUploadFileEntity.setSubType(Constants.PREMIG_SUB_TYPE);

				createUploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				createUploadFileEntity.setNeVersion(null);
				createUploadFileEntity.setScriptType(Constants.SCRIPT_TYPE);
				createUploadFileEntity.setConnectionLocation(connectionLocation);
				createUploadFileEntity.setConnectionLocationUserName(Constants.CONNECTION_LOCATION_USER_NAME);
				createUploadFileEntity.setPrompt(Constants.PROMPT);
				createUploadFileEntity
						.setConnectionLocationPwd(PasswordCrypt.encrypt(Constants.CONNECTION_LOCATION_PWD));
				createUploadFileEntity.setConnectionTerminal(Constants.CONNECTION_TERMINAL);
				createUploadFileEntity.setConnectionTerminalUserName(Constants.CONNECTION_TERMINAL_USER_NAME);
				createUploadFileEntity
						.setConnectionTerminalPwd(PasswordCrypt.encrypt(Constants.CONNECTION_TERMINAL_PWD));

				/*
				 * if (!fileNameList.isEmpty()) { if (fileNameList.contains(file)) { // Do
				 * nothing } else {
				 * uploadFileService.createUploadScript(createUploadFileEntity); } } else {
				 */
				uploadFileService.createUploadScript(createUploadFileEntity);
				// }
			}

			// Insert Use case

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<UploadFileEntity> uploadEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			uploadEntity = uploadEntity.stream().filter(x -> x.getFileName().contains(dateString))
					.collect(Collectors.toList());

			logger.info("RunTestController.ConstantScript dBconstantScriptsFilePath: {}",
					dBconstantScriptsFilePath.toString());
			logger.info("RunTestController.ConstantScript uploadEntity size: {}", uploadEntity.size());
			try {
				if (useCaseBuilderEntity != null) {
					logger.info("RunTestController.ConstantScript usecase found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					for (UploadFileEntity uploadFile : uploadEntity) {

						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							return CommonUtil.buildResponseJson(Constants.FAIL, "Check List script is empty", sessionId,
									"");
						}

						if (checkListScriptDetails.size() > 1) {
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilderEntity.getId(), uploadFile.getId());
						if (useCaseBuilderParamEntity != null) {
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							useCaseScriptsModel.setScriptId(useCaseBuilderParamEntity.getId().toString());
							useCaseScriptsModel.setScriptName(uploadFile.getFileName());
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel
									.setScriptSequence(useCaseBuilderParamEntity.getExecutionSequence().toString());
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
						} else {

							String scriptExeSeq;
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							String fileNameWithExeSeq = uploadFile.getFileName();
							String[] exeSeqSplit = fileNameWithExeSeq.split("_");

							if (exeSeqSplit[2].contains("-")) {
								String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
								boolean numeric = true;
								numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = internalExeSeqSplit[0];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());

							} else {
								boolean numeric = true;
								numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = exeSeqSplit[2];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}

							useCaseScriptsModel.setScriptId(null);
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel.setScriptSequence(scriptExeSeq);
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
							i++;
						}

					}

					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();

					useCaseBuilderModel.setId(useCaseBuilderEntity.getId().toString());
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
					useCaseBuilderModel.setExecutionSequence(useCaseBuilderEntity.getExecutionSequence().toString());
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript updating usecase builder done: {}", useCaseName);
				} else {
					logger.info("RunTestController.ConstantScript usecase not found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					ArrayList scriptSeqList = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					String test = "";
					String rfFileNameFinal = "";
					ArrayList<Integer> arI = new ArrayList<>();
					for (UploadFileEntity uploadFile : uploadEntity) {
						String rfFileName = uploadFile.getFileName();
						if (rfFileName.contains("BASH_RF_NB")) {
							String rfFileNameAfter = StringUtils.substringAfter(rfFileName, "BASH_RF_NB-IoTAdd_");
							if (rfFileNameAfter.contains("_")) {
								test = StringUtils.substringBefore(rfFileNameAfter, "_");
							}
							if (Pattern.matches("[0-9]*", test)) {
								rfFileNameFinal = test;
							} else {
								rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							}
							arI.add(Integer.parseInt(rfFileNameFinal));
						}
					}
					Collections.sort(arI);
					// System.out.println(arI);
					for (UploadFileEntity uploadFile : uploadEntity) {
						xmlRuleModel = new ArrayList();
						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							logger.info("RunTestController.ConstantScript Check List script is empty for the program");
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							logger.info(
									"RunTestController.ConstantScript Check List script is empty for file: {} in Checklist :{} ",
									uploadFile.getFileName(), checkListScriptDetEntity.getCheckListFileName());

							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for file: " + uploadFile.getFileName()
											+ " in Checklist :" + checkListScriptDetEntity.getCheckListFileName(),
									sessionId, "");
						}

						if (checkListScriptDetails.size() > 1) {
							logger.info(
									"RunTestController.ConstantScript Check List script having more than one record for file: {} ",
									uploadFile.getFileName());
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						String scriptExeSeq;
						String fseq = "";
						String firstName;

						UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

						Map<String, String> scriptDetails = new HashMap<>();
						scriptDetails.put("scriptName", uploadFile.getFileName());
						scriptDetails.put("scriptFileId", uploadFile.getId().toString());

						String fileNameWithExeSeq = uploadFile.getFileName();
						String[] exeSeqSplit = fileNameWithExeSeq.split("_");

						if (exeSeqSplit[2].contains("-")) {
							String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
							boolean numeric = true;
							numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = internalExeSeqSplit[0];
							} else {
								String fileName = uploadFile.getFileName();
								if (fileName.contains("BASH_RF_NB")) {
									String fileAfter = StringUtils.substringAfter(fileName, "BASH_RF_NB-IoTAdd_");
									if (fileAfter.contains("_")) {
										fseq = StringUtils.substringBefore(fileAfter, "_");
									}
									if (Pattern.matches("[0-9]*", fseq)) {

									} else {
										fseq = StringUtils.substringBefore(fileAfter, "-");
									}
									/*
									 * if(fileName.contains("BASH_RF_NB")) { fseq =
									 * Integer.toString((Integer.parseInt(fseq)+1)); }
									 */

									fseq = Integer.toString((arI.indexOf(Integer.parseInt(fseq))) + 1);
									while (array.contains(fseq)) {
										fseq = Integer.toString(Integer.parseInt(fseq) + 1);
									}
									array.add(fseq);
									// System.out.println(fseq);
								}
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								else {
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
								}
							}
						}

						else {
							boolean numeric = true;
							numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = exeSeqSplit[2];
							} else {
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}
						}

						logger.info("RunTestController.ConstantScript addDefaultCurlRule: {}, so adding curl rule",
								addDefaultCurlRule);

						if (CommonUtil.isValidObject(xmlRuleModel) && addDefaultCurlRule) {
							XmlRuleBuilderEntity xmlRuleBuilderEntity = xmlRuleBuilderService.findByRuleName(programId,
									"curl");
							if (CommonUtil.isValidObject(xmlRuleBuilderEntity)) {
								XmlRuleModel ruleModel = new XmlRuleModel();
								ruleModel.setXmlId(String.valueOf(xmlRuleBuilderEntity.getId()));
								Map<String, String> xmlDetails = new HashMap<String, String>();
								xmlDetails.put("xmlName", xmlRuleBuilderEntity.getRuleName());
								ruleModel.setXmlDetails(xmlDetails);
								ruleModel.setXmlSequence("1");
								xmlRuleModel.add(ruleModel);
							}
						}

						useCaseScriptsModel.setScriptId(null);
						useCaseScriptsModel.setScript(scriptDetails);
						useCaseScriptsModel.setCmdRules(cmdRuleModel);
						useCaseScriptsModel.setXmlRules(xmlRuleModel);
						useCaseScriptsModel.setFileRules(fileRuleModel);
						useCaseScriptsModel.setShellRules(shellRuleModel);
						useCaseScriptsModel.setScriptSequence(fseq);
						useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

						scriptSeqList.add(fseq);
						scriptList.add(useCaseScriptsModel);
						i++;
					}
					int maxId = useCaseBuilderRepository.getMaxUseCaseId();
					maxId = maxId + 1;
					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseName);
					useCaseBuilderModel.setExecutionSequence(Integer.toString(maxId));
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {

						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript create usecase builder done: {}", useCaseName);

				}

			} catch (Exception e) {
				logger.error(
						"Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			// Copy File

			for (UploadFileEntity entity : uploadEntity) {

				String srcPath = constantScriptsFilePath.toString() + entity.getFileName();

				StringBuilder desBuilderPath = new StringBuilder();

				if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.MIGRATION_TYPE)
									.replace("subType", Constants.SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				} else {

					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.PREMIGRATION_TYPE)
									.replace("subType", Constants.PREMIG_SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				}

				File dest = new File(desBuilderPath.toString() + entity.getFileName());

				if (!dest.exists()) {
					FileUtil.createDirectory(desBuilderPath.toString());
				}

				File source = new File(srcPath);
				FileUtils.copyFile(source, dest);

				// ForDuo
				savecurlCommandMig(source, basePath, useCaseName, neId, programId, ciqFName);
			}

		} catch (Exception e) {
			logger.error("Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	public JSONObject ConstantScriptFSU(int programId, String ciqFName, String neId, String sessionId,
			String constantFile, String connectionLocation, String configType, boolean addDefaultCurlRule,
			String csvPath) {

		ArrayList<String> bashfilename = new ArrayList();
		ArrayList<String> array = new ArrayList<>();

		try {
			ciqFName = StringUtils.substringBeforeLast(ciqFName, ".");
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));

			/*
			 * int programId = 3; String ciqFName = "UNY-NE-VZ_CIQ_Ver2.91_04012019"; String
			 * neId = "57170"; String sessionId =runTestParams.get("sessionId").toString();
			 * String constantFile = "RF";
			 */

			StringBuilder constantScriptsFilePath = new StringBuilder();
			StringBuilder dBconstantScriptsFilePath = new StringBuilder();

			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);

			String basePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH");

			String useCaseName = null;

			if ("GROWFSU".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("FSUBashFile/");

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_FSUGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);
				;

				useCaseName = Constants.GROWFSUUSECASE + "_" + neId + dateString;
			} else if ("DeGrow".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("DeGrowBashFile/");

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_DEGROW_NEGROW
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);
				;
				// DeGrowBashFileNeCreationTimeCBashFile
				useCaseName = Constants.DEGROWENBUSECASE + "_" + neId + dateString;
			} else if ("NECREATION".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(csvPath).append("NeCreationTimeCBashFile/");

				dBconstantScriptsFilePath.append(Constants.SEPARATOR).append(Constants.CUSTOMER)
						.append(Constants.SEPARATOR).append(programId).append(Constants.PRE_MIGRATION_NECREATION
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);
				;

				useCaseName = Constants.CREATIONNEUSECASE + "_" + neId + dateString;
			} else if ("COMM".equalsIgnoreCase(constantFile)) {
				constantScriptsFilePath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				dBconstantScriptsFilePath
						.append(Constants.SEPARATOR).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
						.append(programId).append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT
								.replace("filename", ciqFName).replace("enbId", neId).replaceAll(" ", "_"))
						.append(Constants.SEPARATOR);

				useCaseName = Constants.COMMISION_USECASE + "_" + neId + dateString;
				;
			}

			logger.info("RunTestController.ConstantScript useCaseName: {}", useCaseName);

			File directory = new File(constantScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				for (String singleFile : filename) {
					if (constantFile.equalsIgnoreCase("GROWFSU")) {
						if (singleFile.contains("FSU_TEMPLATE")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("DeGrow")) {
						if (singleFile.contains("DeGrow")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("NECREATION")) {
						if (singleFile.contains("NeCreation") || singleFile.contains("PackageInventory")) {
							if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
								bashfilename.add(singleFile);
							}
						}
					} else if (constantFile.equalsIgnoreCase("COMM")) {
						if (singleFile.endsWith(".sh") && singleFile.contains(dateString)) {
							bashfilename.add(singleFile);
						}
					}

				}
			}
			List<UploadFileEntity> uploadFileEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			ArrayList fileNameList = new ArrayList();
			if (!uploadFileEntity.isEmpty()) {
				for (UploadFileEntity SingleUploadFileEntity : uploadFileEntity) {
					fileNameList.add(SingleUploadFileEntity.getFileName());
				}
			}

			// Delete upload script
			ArrayList<Integer> number = new ArrayList<>();
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<RunTestEntity> runTestEntity = useCaseBuilderService.getRunTestDetails(useCaseName);
			if (CommonUtil.isValidObject(runTestEntity)) {
				for (RunTestEntity runTestEntitys : runTestEntity) {
					if (runTestEntitys.getStatus().equalsIgnoreCase("InProgress")) {
						return CommonUtil.buildResponseJson(Constants.FAIL,
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
										+ " as NE is in progress",
								sessionId, "");
					} else {
						useCaseBuilderRepository.deleteruntestResult(useCaseBuilder.getId());
					}
				}

			}

			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());

			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {

				UploadFileEntity deleteUploadFileEntity = uploadFileService
						.getUploadScriptByPath(dBconstantScriptsFilePath, uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity)
						&& deleteUploadFileEntity.getFileName().contains(dateString)) {
					if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
						if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
								&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
							List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
									.getUseCaseXmlRuleEntityList(useCaseBuilderParamEntity.getId());
							if (CommonUtil.isValidObject(useCaseXmlRuleEntity)) {
								for (UseCaseXmlRuleEntity useCaseXmlRuleEntitys : useCaseXmlRuleEntity) {
									useCaseBuilderRepository.deleteUseCaseXmlRule(useCaseXmlRuleEntitys.getId());
								}
							}
							useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
						}
					}
					uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
				}
			}
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				useCaseBuilderRepository.deleteUseCaseBuilder(useCaseBuilder.getId());
			}

			for (String file : bashfilename) {

				UploadFileEntity createUploadFileEntity = new UploadFileEntity();
				createUploadFileEntity.setFileName(file);
				createUploadFileEntity.setFilePath(dBconstantScriptsFilePath.toString());
				createUploadFileEntity.setNeListEntity(null);
				createUploadFileEntity.setUploadedBy(Constants.UPLOADED_BY);
				createUploadFileEntity.setRemarks(Constants.REMARKS);
				createUploadFileEntity.setUseCount(0);
				createUploadFileEntity.setCustomerId(2);
				createUploadFileEntity.setCreationDate(new Date());
				createUploadFileEntity.setProgram(customerDetailsEntity.getProgramName());
				// createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
				createUploadFileEntity.setState(Constants.STATE);
				// createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				if (file.contains("COMM") || file.contains("RF")) {
					createUploadFileEntity.setMigrationType(Constants.MIGRATION_TYPE);
					createUploadFileEntity.setSubType(Constants.SUB_TYPE);
				} else {
					createUploadFileEntity.setMigrationType(Constants.PREMIGRATION_TYPE);
					createUploadFileEntity.setSubType(Constants.PREMIG_SUB_TYPE);
				}
				createUploadFileEntity.setCustomerDetailsEntity(customerDetailsEntity);
				createUploadFileEntity.setNeVersion(null);
				createUploadFileEntity.setScriptType(Constants.SCRIPT_TYPE);
				createUploadFileEntity.setConnectionLocation(connectionLocation);
				createUploadFileEntity.setConnectionLocationUserName(Constants.CONNECTION_LOCATION_USER_NAME);
				createUploadFileEntity.setPrompt(Constants.PROMPT);
				createUploadFileEntity
						.setConnectionLocationPwd(PasswordCrypt.encrypt(Constants.CONNECTION_LOCATION_PWD));
				createUploadFileEntity.setConnectionTerminal(Constants.CONNECTION_TERMINAL);
				createUploadFileEntity.setConnectionTerminalUserName(Constants.CONNECTION_TERMINAL_USER_NAME);
				createUploadFileEntity
						.setConnectionTerminalPwd(PasswordCrypt.encrypt(Constants.CONNECTION_TERMINAL_PWD));

				/*
				 * if (!fileNameList.isEmpty()) { if (fileNameList.contains(file)) { // Do
				 * nothing } else {
				 * uploadFileService.createUploadScript(createUploadFileEntity); } } else {
				 */
				uploadFileService.createUploadScript(createUploadFileEntity);
				// }
			}

			// Insert Use case

			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			List<UploadFileEntity> uploadEntity = uploadFileService
					.getUploadFileEntity(dBconstantScriptsFilePath.toString());
			uploadEntity = uploadEntity.stream().filter(x -> x.getFileName().contains(dateString))
					.collect(Collectors.toList());

			logger.info("RunTestController.ConstantScript dBconstantScriptsFilePath: {}",
					dBconstantScriptsFilePath.toString());
			logger.info("RunTestController.ConstantScript uploadEntity size: {}", uploadEntity.size());
			try {
				if (useCaseBuilderEntity != null) {
					logger.info("RunTestController.ConstantScript usecase found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					for (UploadFileEntity uploadFile : uploadEntity) {

						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							return CommonUtil.buildResponseJson(Constants.FAIL, "Check List script is empty", sessionId,
									"");
						}

						if (checkListScriptDetails.size() > 1) {
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
								.getUseCaseBuilderParam(useCaseBuilderEntity.getId(), uploadFile.getId());
						if (useCaseBuilderParamEntity != null) {
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							useCaseScriptsModel.setScriptId(useCaseBuilderParamEntity.getId().toString());
							useCaseScriptsModel.setScriptName(uploadFile.getFileName());
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel
									.setScriptSequence(useCaseBuilderParamEntity.getExecutionSequence().toString());
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
						} else {

							String scriptExeSeq;
							UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

							Map<String, String> scriptDetails = new HashMap<>();
							scriptDetails.put("scriptName", uploadFile.getFileName());
							scriptDetails.put("scriptFileId", uploadFile.getId().toString());

							String fileNameWithExeSeq = uploadFile.getFileName();
							String[] exeSeqSplit = fileNameWithExeSeq.split("_");

							if (exeSeqSplit[2].contains("-")) {
								String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
								boolean numeric = true;
								numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = internalExeSeqSplit[0];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());

							} else {
								boolean numeric = true;
								numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
								if (numeric)
									scriptExeSeq = exeSeqSplit[2];
								else
									// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}

							useCaseScriptsModel.setScriptId(null);
							useCaseScriptsModel.setScript(scriptDetails);
							useCaseScriptsModel.setCmdRules(cmdRuleModel);
							useCaseScriptsModel.setXmlRules(xmlRuleModel);
							useCaseScriptsModel.setFileRules(fileRuleModel);
							useCaseScriptsModel.setShellRules(shellRuleModel);
							useCaseScriptsModel.setScriptSequence(scriptExeSeq);
							useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

							scriptList.add(useCaseScriptsModel);
							i++;
						}

					}

					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();

					useCaseBuilderModel.setId(useCaseBuilderEntity.getId().toString());
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
					useCaseBuilderModel.setExecutionSequence(useCaseBuilderEntity.getExecutionSequence().toString());
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {
						useCaseBuilderService.updateUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript updating usecase builder done: {}", useCaseName);
				} else {
					logger.info("RunTestController.ConstantScript usecase not found: {}", useCaseName);
					List<UseCaseScriptsModel> scriptList = new ArrayList();
					List<CmdRuleModel> cmdRuleModel = new ArrayList();
					List<XmlRuleModel> xmlRuleModel = new ArrayList();
					List<FileRuleModel> fileRuleModel = new ArrayList();
					List<ShellRuleModel> shellRuleModel = new ArrayList();
					ArrayList scriptSeqList = new ArrayList();
					int maxScriptSeqId = useCaseBuilderRepository.getMaxExeSeqId(programId);
					int i = 1;
					String test = "";
					String rfFileNameFinal = "";
					ArrayList<Integer> arI = new ArrayList<>();
					for (UploadFileEntity uploadFile : uploadEntity) {
						String rfFileName = uploadFile.getFileName();
						if (rfFileName.contains("BASH_RF_NB")) {
							String rfFileNameAfter = StringUtils.substringAfter(rfFileName, "BASH_RF_NB-IoTAdd_");
							if (rfFileNameAfter.contains("_")) {
								test = StringUtils.substringBefore(rfFileNameAfter, "_");
							}
							if (Pattern.matches("[0-9]*", test)) {
								rfFileNameFinal = test;
							} else {
								rfFileNameFinal = StringUtils.substringBefore(rfFileNameAfter, "-");
							}
							arI.add(Integer.parseInt(rfFileNameFinal));
						}
					}
					Collections.sort(arI);
					// System.out.println(arI);
					for (UploadFileEntity uploadFile : uploadEntity) {
						xmlRuleModel = new ArrayList();
						List<CheckListScriptDetEntity> checkListScriptDetails;
						CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepositoryImpl
								.getCheckListDetails(programId);
						if (checkListScriptDetEntity == null) {
							logger.info("RunTestController.ConstantScript Check List script is empty for the program");
							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for the program", sessionId, "");
						}
						checkListScriptDetails = useCaseBuilderRepositoryImpl.getExeseq(programId,
								uploadFile.getFileName(), configType, checkListScriptDetEntity.getCheckListFileName());
						if (checkListScriptDetails == null || checkListScriptDetails.isEmpty()) {
							logger.info(
									"RunTestController.ConstantScript Check List script is empty for file: {} in Checklist :{} ",
									uploadFile.getFileName(), checkListScriptDetEntity.getCheckListFileName());

							return CommonUtil.buildResponseJson(Constants.FAIL,
									"Check List script is empty for file: " + uploadFile.getFileName()
											+ " in Checklist :" + checkListScriptDetEntity.getCheckListFileName(),
									sessionId, "");
						}

						if (checkListScriptDetails.size() > 1) {
							logger.info(
									"RunTestController.ConstantScript Check List script having more than one record for file: {} ",
									uploadFile.getFileName());
							/*
							 * return CommonUtil.buildResponseJson(Constants.FAIL,
							 * "Check List script having more than one record", sessionId, "");
							 */
						}

						String scriptExeSeq;
						String fseq = "";
						String firstName;

						UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();

						Map<String, String> scriptDetails = new HashMap<>();
						scriptDetails.put("scriptName", uploadFile.getFileName());
						scriptDetails.put("scriptFileId", uploadFile.getId().toString());

						String fileNameWithExeSeq = uploadFile.getFileName();
						String[] exeSeqSplit = fileNameWithExeSeq.split("_");

						if (exeSeqSplit[2].contains("-")) {
							String[] internalExeSeqSplit = exeSeqSplit[2].split("-");
							boolean numeric = true;
							numeric = internalExeSeqSplit[0].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = internalExeSeqSplit[0];
							} else {
								String fileName = uploadFile.getFileName();
								if (fileName.contains("BASH_RF_NB")) {
									String fileAfter = StringUtils.substringAfter(fileName, "BASH_RF_NB-IoTAdd_");
									if (fileAfter.contains("_")) {
										fseq = StringUtils.substringBefore(fileAfter, "_");
									}
									if (Pattern.matches("[0-9]*", fseq)) {

									} else {
										fseq = StringUtils.substringBefore(fileAfter, "-");
									}
									/*
									 * if(fileName.contains("BASH_RF_NB")) { fseq =
									 * Integer.toString((Integer.parseInt(fseq)+1)); }
									 */

									fseq = Integer.toString((arI.indexOf(Integer.parseInt(fseq))) + 1);
									while (array.contains(fseq)) {
										fseq = Integer.toString(Integer.parseInt(fseq) + 1);
									}
									array.add(fseq);
									// System.out.println(fseq);
								}
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								else {
									scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
								}
							}
						}

						else {
							boolean numeric = true;
							numeric = exeSeqSplit[2].matches("-?\\d+(\\.\\d+)?");
							if (numeric) {
								scriptExeSeq = exeSeqSplit[2];
							} else {
								/*
								 * if (scriptSeqList.isEmpty()) { scriptExeSeq = Integer.toString(i); } else {
								 * List sortedlist = new ArrayList<>(scriptSeqList);
								 * Collections.sort(sortedlist); int maxExeSeq = Integer.parseInt((String)
								 * sortedlist.get(sortedlist.size() - 1)); scriptExeSeq =
								 * Integer.toString(maxExeSeq + 1); }
								 */
								// scriptExeSeq = Integer.toString(maxScriptSeqId + i);
								scriptExeSeq = Integer.toString(checkListScriptDetails.get(0).getScriptExeSeq());
							}
						}

						logger.info("RunTestController.ConstantScript addDefaultCurlRule: {}, so adding curl rule",
								addDefaultCurlRule);

						if (CommonUtil.isValidObject(xmlRuleModel) && addDefaultCurlRule) {
							XmlRuleBuilderEntity xmlRuleBuilderEntity = xmlRuleBuilderService.findByRuleName(programId,
									"curl");
							if (CommonUtil.isValidObject(xmlRuleBuilderEntity)) {
								XmlRuleModel ruleModel = new XmlRuleModel();
								ruleModel.setXmlId(String.valueOf(xmlRuleBuilderEntity.getId()));
								Map<String, String> xmlDetails = new HashMap<String, String>();
								xmlDetails.put("xmlName", xmlRuleBuilderEntity.getRuleName());
								ruleModel.setXmlDetails(xmlDetails);
								ruleModel.setXmlSequence("1");
								xmlRuleModel.add(ruleModel);
							}
						}

						useCaseScriptsModel.setScriptId(null);
						useCaseScriptsModel.setScript(scriptDetails);
						useCaseScriptsModel.setCmdRules(cmdRuleModel);
						useCaseScriptsModel.setXmlRules(xmlRuleModel);
						useCaseScriptsModel.setFileRules(fileRuleModel);
						useCaseScriptsModel.setShellRules(shellRuleModel);
						useCaseScriptsModel.setScriptSequence(fseq);
						useCaseScriptsModel.setScriptRemarks(Constants.REMARKS);

						scriptSeqList.add(fseq);
						scriptList.add(useCaseScriptsModel);
						i++;
					}
					int maxId = useCaseBuilderRepository.getMaxUseCaseId();
					maxId = maxId + 1;
					UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
					useCaseBuilderModel.setCustomerId(2);
					useCaseBuilderModel.setNeVersion(null);
					useCaseBuilderModel.setUseCaseName(useCaseName);
					useCaseBuilderModel.setExecutionSequence(Integer.toString(maxId));
					useCaseBuilderModel.setLsmVersion(null);
					useCaseBuilderModel.setLsmName(null);
					useCaseBuilderModel.setRemarks(Constants.REMARKS);
					useCaseBuilderModel.setScriptList(scriptList);
					useCaseBuilderModel.setCiqFileName(ciqFName);

					if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.MIGRATION_TYPE,
								programId, Constants.SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}
					} else {

						useCaseBuilderService.createUseCaseBuilder(useCaseBuilderModel, 2, Constants.PREMIGRATION_TYPE,
								programId, Constants.PREMIG_SUB_TYPE, sessionId);
						if (GlobalStatusMap.WFM_PRE_MIG_USECASES.containsKey(sessionId + neId)) {
							WorkFlowManagementPremigration wfmUsecases = GlobalStatusMap.WFM_PRE_MIG_USECASES
									.get(sessionId + neId);
							List<String> useCaseList = wfmUsecases.getUsecases();
							useCaseList.add(useCaseName);
							wfmUsecases.setUsecases(useCaseList);
							GlobalStatusMap.WFM_PRE_MIG_USECASES.put(sessionId + neId, wfmUsecases);
						}

					}
					logger.info("RunTestController.ConstantScript create usecase builder done: {}", useCaseName);

				}

			} catch (Exception e) {
				logger.error(
						"Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			}
			// Copy File

			for (UploadFileEntity entity : uploadEntity) {

				String srcPath = constantScriptsFilePath.toString() + entity.getFileName();

				StringBuilder desBuilderPath = new StringBuilder();

				if (constantFile.equalsIgnoreCase("COMM") || constantFile.equalsIgnoreCase("RF")) {
					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.MIGRATION_TYPE)
									.replace("subType", Constants.SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				} else {

					desBuilderPath.append(basePath).append(Constants.CUSTOMER).append(Constants.SEPARATOR)
							.append(Constants.UPLOAD_FILE_PATH_WITHOUT_VERSION
									.replace("programId", Integer.toString(programId))
									.replace("migrationType", Constants.PREMIGRATION_TYPE)
									.replace("subType", Constants.PREMIG_SUB_TYPE))
							.append(Constants.SEPARATOR).append(useCaseName).append(Constants.SEPARATOR);
				}

				File dest = new File(desBuilderPath.toString() + entity.getFileName());

				if (!dest.exists()) {
					FileUtil.createDirectory(desBuilderPath.toString());
				}

				File source = new File(srcPath);
				FileUtils.copyFile(source, dest);

				// ForDuo
				savecurlCommandMig(source, basePath, useCaseName, neId, programId, ciqFName);
			}

		} catch (Exception e) {
			logger.error("Exception in ConstantScript() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	/*
	 * @SuppressWarnings({ "unchecked" })
	 * 
	 * @PostMapping(value = "/fetchRadioUnit") public JSONObject
	 * getradioUnit(@RequestBody JSONObject radioUnitParams) {
	 * 
	 * JSONObject resultMap = new JSONObject();
	 * 
	 * String sessionId = null; String serviceToken = null; JSONObject expiryDetails
	 * = null; ArrayList<LinkedHashMap<String, String>> enbDetails;
	 * LinkedHashMap<String, String> enbMap; String enbId = ""; String ciqFileName;
	 * String programId; String enbName = "";
	 * 
	 * try {
	 * 
	 * sessionId = radioUnitParams.get("sessionId").toString(); serviceToken =
	 * radioUnitParams.get("serviceToken").toString(); enbDetails =
	 * (ArrayList<LinkedHashMap<String, String>>) radioUnitParams.get("eNBDetails");
	 * enbMap = enbDetails.get(0); enbId = enbMap.get("eNBId"); enbName =
	 * enbMap.get("enbName"); ciqFileName =
	 * radioUnitParams.get("ciqName").toString(); programId =
	 * radioUnitParams.get("programId").toString();
	 * 
	 * expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
	 * 
	 * if (expiryDetails != null) { return expiryDetails; }
	 * 
	 * String[] radioUnit = runTestService.getRadioUnit(enbId, ciqFileName,
	 * programId, enbName); JSONArray jsonArr = new JSONArray();
	 * 
	 * if (radioUnit != null || radioUnit.length != 0) { List<String> newRadios =
	 * new ArrayList<String>();
	 * 
	 * for (int z = 0; z < radioUnit.length; z++) { jsonArr.add(radioUnit[z]); }
	 * resultMap.put("radioName", jsonArr); resultMap.put("sessionId", sessionId);
	 * resultMap.put("serviceToken", serviceToken); resultMap.put("status",
	 * Constants.SUCCESS);
	 * 
	 * } else { jsonArr.add("No RadioUnit Selected"); resultMap.put("radioName",
	 * jsonArr); resultMap.put("sessionId", sessionId);
	 * resultMap.put("serviceToken", serviceToken); resultMap.put("status",
	 * Constants.SUCCESS); }
	 * 
	 * } catch (Exception e) { resultMap.put("sessionId", sessionId);
	 * resultMap.put("serviceToken", serviceToken); resultMap.put("status",
	 * Constants.FAIL); resultMap.put("reason", e.getMessage());
	 * logger.info("Exception in getradioUnit() in RunTestController" +
	 * ExceptionUtils.getFullStackTrace(e)); }
	 * 
	 * return resultMap;
	 * 
	 * }
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/runTestRET")
	public JSONObject runTestRET(@RequestPart(required = false, value = "UPLOAD") MultipartFile ret,
			@RequestParam("retRunTest") String retRunTest) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		boolean rfScriptsFlag = false;
		String serviceToken = null;
		String userName = null;
		boolean useCurrPassword;
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
			if (runTestParams.containsKey("userName")) {
				userName = runTestParams.get("userName").toString();
			}
			/*
			 * if ( uploadFileService.isFileEmpty(ret)) { resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason", "Upload Ret Form");
			 * resultMap.put("sessionId", sessionId); resultMap.put("serviceToken",
			 * serviceToken); return resultMap; }
			 */
			Map run = (Map) runTestParams.get("runTestFormDetails");
			useCurrPassword = (boolean) run.get("currentPassword");
			String sanePassword = run.get("password").toString();

			String migrationType = runTestParams.get("migrationType").toString();
			if (migrationType.equals("migration")) {
				rfScriptsFlag = (boolean) run.get("rfScriptFlag");
			}
			if (migrationType.equalsIgnoreCase("PreMigration")) {
				migrationType = "Premigration";
			}
			String subType = runTestParams.get("migrationSubType").toString();
			String programName = runTestParams.get("programName").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			JSONObject output = new JSONObject();

			if (useCurrPassword == false && sanePassword.isEmpty()) {

				output = runTestService.getSaneDetailsforPassword(runTestParams);

				if (!output.isEmpty()) {
					resultMap.put("status", "PROMPT");
					resultMap.put("password", output);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("requestType", "RUN_TEST");

					return resultMap;
				}
			}
			String enbId = "";
			String enbName = "";
			int programIds = Integer.parseInt(runTestParams.get("programId").toString());
			List<Map> neList = (List<Map>) run.get("neDetails");
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

				max = max;

			}
			if (ret == null && ObjectUtils.isEmpty(auditRetDetailsEntityList1)) {

				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Upload Ret Form Is NOT FOUND");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				return resultMap;

			}
			if (!runs.get("lsmVersion").toString().isEmpty() || migrationType.equalsIgnoreCase("postmigration")) {
				int count = 0;
				NetworkConfigEntity previousNetworkConfigEntity = null;
				for (Map neid : neList) {
					NeMappingModel neMappingModelversion = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntityversion = new CustomerDetailsEntity();
					programDetailsEntityversion.setId(programIds);
					neMappingModelversion.setProgramDetailsEntity(programDetailsEntityversion);
					neMappingModelversion.setEnbId(neid.get("neId").toString());
					NetworkConfigEntity neMappingEntitiesForVersion = runTestRepository
							.getNetWorkEntityDetails(neMappingModelversion);
					if (migrationType.equalsIgnoreCase("postmigration")) {
						if (count == 0) {
							previousNetworkConfigEntity = neMappingEntitiesForVersion;
						} else {
							if (previousNetworkConfigEntity != null && neMappingEntitiesForVersion != null
									&& (!previousNetworkConfigEntity.getId()
											.equals(neMappingEntitiesForVersion.getId()))) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								resultMap.put("reason", "Selected Sites don't belong to the same SM Version");
								return resultMap;
							}
							previousNetworkConfigEntity = neMappingEntitiesForVersion;
						}
						count++;
					}
					if (neMappingEntitiesForVersion != null) {
						String lsmVersion = neMappingEntitiesForVersion.getNeVersionEntity().getNeVersion();
						if (!lsmVersion.equals(runs.get("lsmVersion"))
								&& !runs.get("lsmVersion").toString().isEmpty()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", "Selected Sites don't belong to the selected SM Version");
							return resultMap;
						}
					} else {
						if (runs.get("lsmVersion").toString().isEmpty() || run.get("lsmName").toString().isEmpty()) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							resultMap.put("reason", "NE is not Mapped");
							return resultMap;
						}
					}
				}
			}
			String htmlOutputFileName = "";
			if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION) && "AUDIT".equalsIgnoreCase(subType)) {
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
				htmlOutputFileName = "_ORAN_AUDIT_" + timeStamp + ".html";
			}

			String enbStatus = runTestService.getRuntestEnbProgressStatus(runTestParams);

			List<Map> scriptSeqDetails = (List<Map>) run.get("scripts");
			if (scriptSeqDetails.isEmpty()) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "No Scripts found to run");
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				return resultMap;
			}
			int xmlScriptsCount = 0;
			if (programName.contains("4G-USM-LIVE") && "Audit".equalsIgnoreCase(subType)) {

				for (Map scriptInfoDetails : scriptSeqDetails) {
					String scriptname = scriptInfoDetails.get("scriptName").toString();
					if (FilenameUtils.getExtension(scriptname).equalsIgnoreCase("xml")) {
						xmlScriptsCount++;
					}
				}
				if (scriptSeqDetails.size() != xmlScriptsCount && xmlScriptsCount != 0) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Select Usecases containing same type of extension");
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
			}

			if (enbStatus.equalsIgnoreCase(Constants.SUCCESS)) {

				int programId = Integer.parseInt(runTestParams.get("programId").toString());
				Map runTestFormDetails = (Map) runTestParams.get("runTestFormDetails");

				String testname = runTestFormDetails.get("testname").toString();

				if (migrationType.equalsIgnoreCase(Constants.MIGRATION)) {
					migrationType = "Migration";
				} else if (migrationType.equalsIgnoreCase(Constants.POST_MIGRATION)) {
					migrationType = "PostMigration";
				} else if (migrationType.equalsIgnoreCase(Constants.PRE_MIGRATION)) {
					migrationType = "Premigration";
				}

				if ("precheck".equalsIgnoreCase(subType)) {
					subType = "PreCheck";
				} else if ("commission".equalsIgnoreCase(subType)) {
					subType = "Commission";
				} else if ("postcheck".equalsIgnoreCase(subType)) {
					subType = "PostCheck";
				} else if ("AUDIT".equalsIgnoreCase(subType)) {
					subType = "Audit";
				} else if ("RANATP".equalsIgnoreCase(subType)) {
					subType = "RanATP";
				} else if ("NEGrow".equalsIgnoreCase(subType)) {
					subType = "NEGrow";
				}

				String isTestNamePresent = runTestService.getRunTestEntity(programId, migrationType, subType, testname);
				if (isTestNamePresent != Constants.SUCCESS) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", isTestNamePresent);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
				List<LinkedHashMap> useCaseList = (List) run.get("useCase");
				ArrayList<String> useList = new ArrayList<>();
				for (Map usecase : useCaseList) {
					useList.add(usecase.get("useCaseName").toString());
				}
				System.out.println(useCaseList);
				if (!(programName.equals("VZN-4G-FSU"))) {
					for (Map usecase : useCaseList) {
						String useCaseName = usecase.get("useCaseName").toString();
						String rfUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
						if (useCaseName.contains("CommissionScriptUsecase") && !useList.contains(rfUseCaseName)) {
							String newUseCaseName = useCaseName.replace("CommissionScriptUsecase", "RFUsecase");
							boolean isuseCasePresent = runTestService.getusecaseDetails(programId, migrationType,
									subType, newUseCaseName);
							System.out.println(isuseCasePresent);
							if (!isuseCasePresent) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										"Please complete the RF Scipts execution to execute the commission scripts");
								resultMap.put("sessionId", sessionId);
								resultMap.put("serviceToken", serviceToken);
								return resultMap;
							}
						}

					}
				}
				Map<String, RunTestEntity> runTestEntity = runTestService.insertRunTestDetails(runTestParams, "");
				Set<String> runtestNeidList = runTestEntity.keySet();
				if (runtestNeidList.size() != neList.size()) {
					List<Map> newNeidList = new ArrayList<>();
					for (Map neid : neList) {
						if (runtestNeidList.contains(neid.get("neId"))) {
							newNeidList.add(neid);
						}
					}
					runs.put("neDetails", newNeidList);
					runTestParams.put("runTestFormDetails", runs);
				}
				int uniqueId = max;
				System.out.println("runTestEntity.get(enbId).getId()   " + runTestEntity.get(enbId).getId());
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
							fileSave = retService.saveRETform(sheet, fileN, programId, userName, enbId, uniqueId);
							System.out.println("Out from saveRetForm");

							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason", GlobalInitializerListener.faultCodeMap
									.get(FaultCodes.CREATED_CHECKLIST_DETAILS_SUCCESSFULLY));
							commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
									Constants.EVENT_PRE_MIGRATION_CHECK_LIST, Constants.ACTION_SAVE,
									"File Uploaded Successfully" + fileN, sessionId);

						} else {
							logger.info("File Is Incorrect");
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason", "Incorrect file uploaded");
							resultMap.put("sessionId", sessionId);
							resultMap.put("serviceToken", serviceToken);
							return resultMap;
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

				if ("Audit".equalsIgnoreCase(subType)
						&& (programName.contains("5G") || xmlScriptsCount != 0 || programName.contains("4G-FSU"))) {
					result = runTestService.getRuntestExecResult5GAudit(runTestParams, runTestEntity, "CURRENT",
							htmlOutputFileName, null, rfScriptsFlag);
				} else {
					result = runTestService.getRuntestExecResult(runTestParams, runTestEntity, "CURRENT",
							htmlOutputFileName, null, rfScriptsFlag);
				}
				resultMap.put("runTestEntity", runTestEntity);
				resultMap.put("password", output);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				if (result.equalsIgnoreCase(Constants.SUCCESS)) {
					resultMap.put("status", Constants.SUCCESS);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.COULD_NOT_CONNECT));
				}

			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason", enbStatus);
			}
			commonUtil.saveAudit(Constants.EVENT_MIGRATION, Constants.EVENT_MIGRATION_RUN_TEST,
					Constants.ACTION_EXECUTE, "Run Test Executed Successfully", sessionId);

		} catch (RctException e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}

	@RequestMapping(value = "/bulkAuditReport", method = RequestMethod.POST)
	public void downloadAudit4GBulkSummaryReport(@RequestBody JSONObject downloadBulkData, HttpServletResponse response)
			throws IOException {
		JSONObject ciqList = new JSONObject();
		String searchStatus = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String userName = null;
		String toDate = null;
		String fromDate = null;
		String migrationType = null;
		String migrationSubType = null;
		JSONObject expiryDetails = null;

		FileInputStream inputStream = null;
		OutputStream outputStream = null;

		File downloadFile = null;
		String neId = null;
		String neName = null;
		String programName = null;
		String commPath = null;
		String envPath = null;
		String csvPath = null;
		String commZipName = null;
		String envZipName = null;
		String csvZipName = null;
		String postfilepath = null;
		String prefilePath = null;
		String prefileName = null;
		String fileAbsolutepath = null;
		OutputStream outStream = null;

		boolean showUserData = false;

		int programId = Integer.parseInt(downloadBulkData.get("programId").toString());

		HashMap<String, ArrayList<String>> paths4G = new HashMap<>();
		ArrayList<String> arr1 = new ArrayList<>();
		ArrayList<String> arr2 = new ArrayList<>();
		ArrayList<String> arr3 = new ArrayList<>();
		ArrayList<String> arr4 = new ArrayList<>();
		try {
			StringBuilder zipFileFolderpath = new StringBuilder();
			JSONObject jsonObject = loadrunTest(downloadBulkData);
			RunTestModel runModel = new RunTestModel();

			List<RunTestModel> runTestDetails = null;

			runTestDetails = (List<RunTestModel>) jsonObject.get("runTestTableDetails");

			// List<LinkedHashMap> runTestFormDetails = (List)
			// jsonObject.get("runTestTableDetails");
			StringBuilder FileWritePath = new StringBuilder();
			FileWritePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR).append("PostMigration/")
					.append("BulkReport");
			File fileExists = new File(FileWritePath.toString());
			if (fileExists.exists()) {
				FileUtil.deleteFileOrFolder(FileWritePath.toString());
				// FileUtil.createDirectory(uploadPath.toString());
			}
			if (!fileExists.exists()) {
				FileUtil.createDirectory(FileWritePath.toString());
			}

			for (RunTestModel runTestEntity : runTestDetails) {
				String sourcePath = runTestEntity.getResultFilePath();
				String resultName = runTestEntity.getResult();

				String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + sourcePath + "/"
						+ resultName.trim().replaceAll(" ", "_") + "/";
				File SourceFile = new File(filePath.toString());
				File DSourceFile = new File(FileWritePath.toString());

				if (SourceFile.exists() && !SourceFile.isDirectory()) {
					FileUtils.copyFileToDirectory(SourceFile, DSourceFile);
				}

			}
			try {
				// String zipFilepath = FileWritePath+"/"+ "BulkReport" + ".zip";
				String zipFilepath = FileWritePath + ".zip";
				boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath.toString(), FileWritePath.toString());
				if (!status) {
					logger.info("downloadFile() file not found:" + fileAbsolutepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}

				downloadFile = new File(zipFilepath);
				fileAbsolutepath = zipFilepath;
				if (!downloadFile.exists()) {
					logger.info("downloadBulkFile) file not found:" + fileAbsolutepath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"File Not Found\"}");
					out.close();
					return;
				}
				inputStream = new FileInputStream(downloadFile);
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
				if (CommonUtil.isValidObject(FileWritePath) && CommonUtil.isValidObject(zipFilepath)) {
					FileUtil.deleteFileOrFolder(FileWritePath.toString());
					FileUtil.deleteFileOrFolder(zipFilepath);
				}
			} catch (FileNotFoundException fne) {
				logger.error("Exception in GenerateDownloadScripts(): " + ExceptionUtils.getFullStackTrace(fne));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"File Not Found\"}");
				out.close();
			} catch (Exception e) {
				logger.error("Exception in GenerateDownloadScripts(): " + ExceptionUtils.getFullStackTrace(e));
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \"Error While Downloading\"}");
				out.close();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();

					}
				} catch (Exception e) {
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

	@RequestMapping(value = "/bulkAuditSummaryReport", method = RequestMethod.POST)
	public void downloadAudit4GBulk(@RequestBody JSONObject downloadBulkReport, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String userName = "";
		String neName = null;
		String programName = "";

		int programId = Integer.parseInt(downloadBulkReport.get("programId").toString());

		try {
			filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(Constants.AUDIT_4G_BULK_SUMMARY_REPORT_FILEPATH);
			// neName = downloadBulkReport.get("neName").toString();
			String fileName = "";
			boolean status = false;

			JSONObject jsonObject = loadrunTest(downloadBulkReport);
			
			List<RunTestModel> model = (List<RunTestModel>) jsonObject.get("runTestTableDetails");
			
			
			Set<Integer> set = new HashSet<>();
			for (RunTestModel runModel : model) {
				
				int id = runModel.getId();
				set.add(id);
			}
			downloadBulkReport.put("set",set);
			
			JSONObject jsonObject1 = getAudit4GPassFailSummaryReport(downloadBulkReport);
			
			

			JSONObject jsonObject2 = getAudit4GSummaryReport(jsonObject1);

			jsonObject1.put("postAuditIssues", jsonObject2.get("postAuditIssues"));

			 

			AuditPassFailSummaryModel runModel = new AuditPassFailSummaryModel();

			List<AuditPassFailSummaryModel> runTestDetails = (List<AuditPassFailSummaryModel>) jsonObject1
					.get("passfailStatus");


			StringBuilder FileWritePath = new StringBuilder();

			FileWritePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR).append("PostMigration/")
					.append("BulkExcelReport");
			File fileExists = new File(FileWritePath.toString());
			if (fileExists.exists()) {
				FileUtil.deleteFileOrFolder(FileWritePath.toString());
			}
			if (!fileExists.exists()) {
				FileUtil.createDirectory(FileWritePath.toString());
			}

			programName = downloadBulkReport.get("programName").toString();
			userName = downloadBulkReport.get("userName").toString();
			if (programName.contains("4G-FSU")) {
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1,
						FileWritePath.toString());
				fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
			} else if (programName.contains("4G-USM-LIVE")) {
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1,
						FileWritePath.toString());
				fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
			} else if (programName.contains("5G-DSS")) {
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1,
						FileWritePath.toString());
				fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
				logger.error("5G-DSS Bulk download() not found  :" + fileName);
			} else if (programName.contains("5G-CBAND")) {
				status = audit4GFsuSummaryService.createBulkAudit4GFsuSummaryReportExcel(jsonObject1,
						FileWritePath.toString());
				fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
				logger.error("5GCBAND Bulk download() not found  :" + fileName);
			}

			if (status) {
				String xlsxFilePath = FileWritePath.toString() + Constants.SEPARATOR + fileName;
				downloadFile = new File(xlsxFilePath);
				if (!downloadFile.exists()) {
					logger.error("downloadFile() file not found:" + xlsxFilePath);
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"Error While Downloading\"}");
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
				if (CommonUtil.isValidObject(FileWritePath)) {
					FileUtil.deleteFileOrFolder(FileWritePath.toString());
				}
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.close();
				return;

			}

		} catch (Exception e) {
			logger.error("Exception in Audit4GSummaryController.downloadAudit4GSummaryReport(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.close();
		}
	}

	public JSONObject getAudit4GPassFailSummaryReport(@RequestBody JSONObject audit4GSummaryReportDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String neName = null;
		String neId = null;
		String programName = "";
		String toDate = null;
		String fromDate = null;
		String userName = null;
		String migrationSubType = null;
		String migrationType = null;

		try {
			sessionId = audit4GSummaryReportDetails.get("sessionId").toString();
			serviceToken = audit4GSummaryReportDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			migrationType = audit4GSummaryReportDetails.get("migrationType").toString();
			migrationSubType = audit4GSummaryReportDetails.get("migrationSubType").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			Map<String, Object> paginationData = (Map<String, Object>) audit4GSummaryReportDetails.get("pagination");

			if (expiryDetails != null) {
				return expiryDetails;
			}

			RunTestModel runTestModel = new Gson().fromJson(
					audit4GSummaryReportDetails.toJSONString((Map) audit4GSummaryReportDetails.get("searchCriteria")),
					RunTestModel.class);
			programName = audit4GSummaryReportDetails.get("programName").toString();
			userName = audit4GSummaryReportDetails.get("userName").toString();


			Map<String, Map<String, Map<String, String>>> auditPassFailSummaryModelList1 = new HashMap<>();
			List<AuditPassFailSummaryModel> auditPassFailSummaryModelList = new ArrayList<>();
			Set<String> headerNames = new HashSet<>();


			Set<Integer>  set1 = new HashSet<Integer>();
			set1 = (Set<Integer>) audit4GSummaryReportDetails.get("set");
			
			if (programName.contains("4G-FSU")) {

					List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = audit4GFsuSummaryService
							.getAudit4GFsuPassFailSummaryEntityListByRunTestId(set1);
				
					if (audit4GFsuPassFailSummaryEntityList != null && !audit4GFsuPassFailSummaryEntityList.isEmpty()) {
						neId = audit4GFsuPassFailSummaryEntityList.get(0).getNeId();
					}
				
					resultMap.put("neId", neId);

					headerNames = audit4GFsuPassFailSummaryEntityList.stream()
							.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getTestName())
							.collect(Collectors.toSet());
					auditPassFailSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuPassFailSummaryReportModelList(
							audit4GFsuPassFailSummaryEntityList, programName, userName);
				
			} else if (programName.contains("5G-DSS")) {

				List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailEntityList = audit5GDSSSummaryService
						.getAudit5GDSSPassFailSummaryEntityListByRunTestId(set1);

				if (audit5GDSSPassFailEntityList != null && !audit5GDSSPassFailEntityList.isEmpty()) {
					neId = audit5GDSSPassFailEntityList.get(0).getNeId();
					logger.error("5GDSS AuditPassFailList {}", audit5GDSSPassFailEntityList);
				}
				resultMap.put("neId", neId);

				headerNames = audit5GDSSPassFailEntityList.stream()
						.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getTestName())
						.collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit5GDSSSummaryDto
						.getAudit5GDSSPassFailReportModelList(audit5GDSSPassFailEntityList, programName, userName);
				logger.error("AuditPassFail Summary of 5G-DSS {} ", auditPassFailSummaryModelList);

			} else if (programName.contains("4G-USM-LIVE")) {
				List<Audit4GPassFailEntity> audit4GPassFailSEntityList = audit4GSummaryService
						.getAudit4GPassFailEntityListByRunTestId(set1);

				if (audit4GPassFailSEntityList != null && !audit4GPassFailSEntityList.isEmpty()) {
					neId = audit4GPassFailSEntityList.get(0).getNeId();
				}
				resultMap.put("neId", neId);

				headerNames = audit4GPassFailSEntityList.stream()
						.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getTestName()).collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit4GSummaryDto
						.getAudit4GPassFailReportModelList(audit4GPassFailSEntityList, programName, userName);

			} else if (programName.contains("5G-CBAND")) {
				List<Audit5GCBandPassFailEntity> audit5GCBandPassFailEntityList = audit5GCBandSummaryService
						.getAudit5GCBandPassFailEntityListByRunTestId(set1);

				if (audit5GCBandPassFailEntityList != null && !audit5GCBandPassFailEntityList.isEmpty()) {
					neId = audit5GCBandPassFailEntityList.get(0).getNeId();
					logger.error("5GCBAND Audit PassFailEntity", audit5GCBandPassFailEntityList);
				}
				resultMap.put("neId", neId);
				headerNames = audit5GCBandPassFailEntityList.stream()
						.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getTestName())
						.collect(Collectors.toSet());
				auditPassFailSummaryModelList = audit5GCBandSummaryDto
						.getAudit5GCBandPassFailReportModelList(audit5GCBandPassFailEntityList, programName, userName);
				logger.error("AuditPassFail Summary of 5G-CBAND {}", auditPassFailSummaryModelList);
			}

			resultMap.put("passfailStatus", auditPassFailSummaryModelList);
			resultMap.put("programName", programName);
			resultMap.put("neheaders", headerNames);
			resultMap.put("status", Constants.SUCCESS);

			logger.error("ResultMap {}", resultMap);
		} catch (Exception e) {
			logger.error("Exception Audit4GSummaryController in getAudit4GSummaryReport() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", "Failed to Fetch Summary Report");
		}

		return resultMap;
	}

	public JSONObject getAudit4GSummaryReport(@RequestBody JSONObject audit4GSummaryReportDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String neName = null;
		String neId = null;
		String programName = "";
		try {
			programName = audit4GSummaryReportDetails.get("programName").toString();

			List<AuditPassFailSummaryModel> bulkaudit4GFsuSummaryModelList = (List<AuditPassFailSummaryModel>) audit4GSummaryReportDetails
					.get("passfailStatus");

			// Map<String, Object> audit4GSummaryModelList = new HashMap<String, Object>();
			List<Audit4GSummaryModel> audit4GSummaryModelList = new ArrayList<>();
			List<Audit4GSummaryModel> audit4GSummaryModelList1 = new ArrayList<>();
			for (AuditPassFailSummaryModel entry : bulkaudit4GFsuSummaryModelList) {
				List<AuditRunModel> auditNeRunSummary = entry.getAuditNeRunSummary();

				for (AuditRunModel runEntry : auditNeRunSummary) {
					int runTestId = Integer.parseInt(runEntry.getRunId());

					if (programName.contains("4G-USM-LIVE")) {
						List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService
								.getAudit4GSummaryEntityListByRunTestId(runTestId);

						if (audit4GSummaryEntityList != null && !audit4GSummaryEntityList.isEmpty()) {
							neId = audit4GSummaryEntityList.get(0).getNeId();
						}
						resultMap.put("neId", neId);
						audit4GSummaryModelList1 = audit4GSummaryDto
								.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
					} else if (programName.contains("5G-CBAND")) {
						List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = audit5GCBandSummaryService
								.getAudit5GCBandSummaryEntityListByRunTestId(runTestId);

						if (audit5GCBandSummaryEntityList != null && !audit5GCBandSummaryEntityList.isEmpty()) {
							neId = audit5GCBandSummaryEntityList.get(0).getNeId();
							logger.error("5GBand AuditSummary {}", audit5GCBandSummaryEntityList);
						}
						resultMap.put("neId", neId);
						audit4GSummaryModelList1 = audit5GCBandSummaryDto
								.getAudit5GCBandSummaryReportModelList(audit5GCBandSummaryEntityList);
						logger.error("AuditSummary5GCBand {}", audit4GSummaryModelList1);
					} else if (programName.contains("5G-DSS")) {
						List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = audit5GDSSSummaryService
								.getAudit5GDSSSummaryEntityListByRunTestId(runTestId);

						if (audit5GDSSSummaryEntityList != null && !audit5GDSSSummaryEntityList.isEmpty()) {
							neId = audit5GDSSSummaryEntityList.get(0).getNeId();
							logger.error("5GDSS AuditSummary {}", audit5GDSSSummaryEntityList);
						}
						resultMap.put("neId", neId);
						audit4GSummaryModelList1 = audit5GDSSSummaryDto
								.getAudit5GDSSSummaryReportModelList(audit5GDSSSummaryEntityList);
						logger.error("AuditSummary5GDSS {}", audit4GSummaryModelList1);
					} else if (programName.contains("4G-FSU")) {
						List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = audit4GFsuSummaryService
								.getAudit4GFsuSummaryEntityListByRunTestId(runTestId);

						if (audit4GFsuSummaryEntityList != null && !audit4GFsuSummaryEntityList.isEmpty()) {
							neId = audit4GFsuSummaryEntityList.get(0).getNeId();
						}

						resultMap.put("neId", neId);
						audit4GSummaryModelList1 = audit4GFsuSummaryDto
								.getAudit4GFsuSummaryReportModelList(audit4GFsuSummaryEntityList);
					}
					audit4GSummaryModelList.addAll(audit4GSummaryModelList1);
				}
			}

			// int runTestId =
			// Integer.parseInt(audit4GSummaryReportDetails.get("runTestId").toString());

			resultMap.put("postAuditIssues", audit4GSummaryModelList);
			// resultMap.put("postAuditSuccess", audit4GSuccessSummaryModelList);
			resultMap.put("status", Constants.SUCCESS);

		} catch (Exception e) {
			logger.error("Exception Audit4GSummaryController in getAudit4GSummaryReport() "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", "Failed to Fetch Summary Report");
		}
		return resultMap;
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

	public void getFiles(String migfilepath, String programId, String zipFileFolderpath, String folderName)
			throws IOException {

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

	public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
