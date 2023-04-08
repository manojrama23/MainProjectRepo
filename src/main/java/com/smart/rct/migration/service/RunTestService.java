package com.smart.rct.migration.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;

public interface RunTestService {

	public List<RunTestModel> getRunTestDetails(int page, int count, Integer customerId, Integer programId,
			String migrationStatus, String migrationSubStatus, int days);

	public List<RunTestModel> getRunTestSearchDetails(RunTestModel runTestModel, int page, int count,
			Integer customerId, Integer programId, String migrationStatus, String migrationSubStatus, int days);

	public List<NetworkTypeDetailsModel> getNetWorksBasedOnCustomer(Integer customerId);

	public Map<String, Object> getdropDownDetails(List<NetworkTypeDetailsModel> neTypeDetailsList);

	public String getRuntestExecResult(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntity,
			String runType, String htmlOutputFileName, Map<String, Object> reRunDetails, boolean rfScriptsFlag)
			throws RctException;

	public String generateScript(JSONObject runTestParams) throws RctException;

	public Map<String, RunTestEntity> insertRunTestDetails(JSONObject runTestParams, String perlPath);

	public List<RunTestResultEntity> getRunTestResult(Integer runTestId);

	public boolean deleteRunTest(Integer runTestId);

	public JSONObject getResult(List<RunTestResultEntity> entityList, String neName);

	public String getScriptOutput(Integer runTestId, Integer useCaseId, Integer scriptId, String useCaseName,
			String scriptName);

	public int loadRunTestDetails(int programId, String migrationType, String migrationSubType, int customerId,
			int history, RunTestModel runTestModels);

	public RunTestEntity getRunTestEntity(Integer runTestId);

	public String getConnection(JSONObject connectionParams) throws RctException;

	public List<Map> getSmList(int programId);

	public CustomerDetailsEntity getCustomerDetailsEntity(int programId);

	public JSONObject getSaneDetailsforPassword(JSONObject runTestParams);

	public List<Map> getUseCaseList(int programId, String migrationType, String subType, String ciqFName,
			List<Map> neList);

	public List<Map> getUseCaseList(int programId, String migrationType, String subType);

	public String loadRunningLog(Integer runTestId);

	public String readOutputFile(String fileName) throws Exception;

	public String[] getBandName(String enbId, String ciqFileName, String programId);

	public String getRuntestEnbProgressStatus(JSONObject runTestParams);

	public String getRunTestEntity(int programId, String migrationType, String subType, String testname);

	public boolean getInProgressRunTestDetails(int programId, String migrationType, String subType);

	public Map<String, Object> getRunTestDetails(RunTestModel runTestModel, int page, int count, Integer programId,
			String migrationType, String migrationSubType,boolean wfmKey);

	List<Map> getMigrationUseCaseList(int programId, String migrationType, String subType, String ciqFileName,
			List<String> enbId, String programName);

	Map<String, RunTestEntity> getRunTestDetailsMap(JSONObject runTestParams, Integer runTestId);

	JSONObject getInputRuntestJson(int runTestId);

	List<String> getBandColumnValuesBySheet(String enbId, String ciqFileName, String programId,
			List<String> bandNamesList);

	/*
	 * String getRuntestExecResult5GAudit(JSONObject runTestParams, Map<String,
	 * RunTestEntity> runTestEntityMap, String runType, String htmlOutputFileName,
	 * Map<String, Object> reRunDetails, boolean rfScriptsFlag, Map<String, Object>
	 * auditoutput) throws RctException;
	 */

	String generateScript5GAudit(JSONObject runTestParams) throws RctException;

	List<Map> getMigrationUseCaseListWFM(int programId, String migrationType, String subType, String ciqFileName,
			String enbId, String programName);

	public List getMessageInfo(List<RunTestResultEntity> runTestResultEntityList);

	String getRunTestEnbProgressStatus(RunTestEntity runtestEntity);

	List<Map> getPostMigrationUseCaseList(int programId, String migrationType, String subType, List<String> enbId);

	public boolean getusecaseDetails(int programId, String migrationType, String subType, String useCaseName);

	// String[] getRadioUnit(String enbId, String ciqFileName, String programId,
	// String enbName);

	String getRuntestExecResult5GAudit(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntityMap,
			String runType, String htmlOutputFileName, Map<String, Object> reRunDetails, boolean rfScriptsFlag)
			throws RctException;

	JSONObject getRuntestExecResult5GDSSPrepost(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntityMap,
			String runType, String htmlOutputFileName, Map<String, Object> reRunDetails, boolean rfScriptsFlag,
			Map<String, Object> auditoutput, Map<String, RunTestEntity> runTestEntityMap1) throws RctException;

	public JSONObject getTrakerIdList(JSONObject ovUpdateJson, Map<String, RunTestEntity> runTestEntityMap);

	Map<String, RunTestEntity> getRunTestDetailsMapOVupdate(JSONObject runTestParams, Integer runTestId);

	public JSONObject getWorkPlanIdList(String trackerID);

	public JSONObject patchMileStone(String WorkplanID, String migrationSubType, int programId,
			Map<String, RunTestEntity> runTestEntityMap, String neId, String[] Milestone);

	Map<String, RunTestEntity> getRunTestDetailsMapOVupdateforAudit(String neid, Integer runTestId);

	List<OvTestResultEntity> getOvRunTestResult(Integer runTestId);
	
	JSONObject getForceRunTestResult(String status, Integer runTestId);
	
	JSONObject getWFMRunTestResult(String status, Integer wfmTestId);

	public boolean deleteOvRunTestResult(Integer runTestId);

	boolean deleteOVupdateEntityById(int OVid);

	public JSONObject patchMileStoneIndividual(String workplanID, String migrationSubType, int programId,
			Map<String, RunTestEntity> runTestEntity, String neId, String milestone);

	public JSONObject patchMileStoneRSSI(String workplanID, String migrationSubType, int programId,
			Map<String, RunTestEntity> runTestEntityMap, String neId, String milestone);

	public String getRuntestExecResultNeUp(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntityMap,
			String runType, String htmlOutputFileName, Map<String, Object> reRunDetails, boolean rfScriptsFlag)
			throws RctException;

	String loadneOplogs(Integer runTestId);;

	List<PremigrationOvUpadteEntity> getOvRunTestResultpre(Integer runTestId);

	JSONObject HTMLUploadToOV(JSONObject ovUpdateJson, Map<String, RunTestEntity> runTestEntityMap);

}
