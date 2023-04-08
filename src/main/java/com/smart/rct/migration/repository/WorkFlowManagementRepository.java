package com.smart.rct.migration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;

public interface WorkFlowManagementRepository {
	
	RunTestEntity getRunTestEntity(RunTestEntity runTestEntity);
	WorkFlowManagementEntity createWorkFlowMangement(WorkFlowManagementEntity workFlowManagementEntity);
	Map<String, Object> getWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, int page, int count, Integer programId);
	List<RunTestEntity> getRunTestListWfm(List<Integer> runtestIdList);
	WorkFlowManagementEntity getWorkFlowManagementEntity(Integer workFlowId);
	
	boolean deleteWfmrunTest(int wfmRunTestId);
	
	boolean getWFMRunTestEntity(int programId, String testname);
	boolean getWfmEnbStatus(int programId, String neName,Object neId);
	
	WorkFlowManagementEntity getWFMEntity(int runtestId, String type);
	
	Map<String, Object> getInProgressWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, Integer programId,
			List<String> userNameList);
	
	Map<String, Object> getDuoExecErrorSiteList(WorkFlowManagementModel runTestModel, Integer programId,
			List<String> userNameList, String useCaseName);
}
