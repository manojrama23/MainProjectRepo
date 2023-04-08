package com.smart.rct.migration.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;

public interface WorkFlowManagementService {

	RunTestEntity getRunTestEntity(RunTestEntity runTestEntity);

	WorkFlowManagementEntity insertWorkManagementDetails(JSONObject runTestParams);

	WorkFlowManagementEntity mergeWorkFlowMangement(WorkFlowManagementEntity workFlowManagementEntity);

	Map<String, Object> getWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, int page, int count,
			Integer programId);

	void getWorkFlowManageStatus(WorkFlowManagementEntity workFlowManagementEntity);

	WorkFlowManagementEntity getWorkFlowManagementEntity(Integer workFlowId);

	boolean deleteWfmrunTest(int wfmRunTestId);

	Map<String, List<Map>> formUseCases(List<Map> migrationUseCases);

	String getWFMRunTestEntity(int programId, String testname);

	public String runWFM(JSONObject premigrationInputsThread, JSONObject migrationInputsThread, JSONObject postmigrationInputsThread, JSONObject negrowInputsThread, 
			JSONObject preauditInputs, JSONObject neStatusInputs, List<Map<String, String>> enbList, String programid, String programname, Map<String, Integer> temp);
	
	public String continueWFM(Integer wfid, JSONObject negrowJson, JSONObject migrationJson, JSONObject postMigJson, List<Map<String, String>> enbList, JSONObject preAuditInputs, JSONObject neStatusInputs );

	

	public JSONObject runIndependentWFM(String sessionId, String serviceToken, String state, JSONObject premigrationInputs,
			JSONObject migrationInputs, JSONObject postmigrationInputs, JSONObject negrowInputs,
			List<Map<String, String>> enbList, String programId, String programName, Integer workFlowId);
	
	LinkedHashMap<String, String> getSkipandContinueId(int runTestId, String runType);
	
	public void getreRunMigStatus(WorkFlowManagementEntity workFlowManagementEntity);

	String getRunnTestWfmEnbProgressStatus(JSONObject runTestParams);

	List<Map<String, String>> getInProgressWorkFlowManagementDetails(WorkFlowManagementModel runTestModel,
			Integer programId, List<String> userNameList);

	List<Map<String, String>> getDuoExecErrorSiteList(WorkFlowManagementModel runTestModel, Integer programId,
			List<String> userNameList, String programName, String useCaseName);

	public void getreRunNEStatus(WorkFlowManagementEntity workFlowManagementEntity );
}
