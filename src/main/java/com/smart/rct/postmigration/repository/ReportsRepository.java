package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.postmigration.models.ReportsModel;


public interface ReportsRepository {
	
//	RunTestEntity getRunTestEntity(RunTestEntity runTestEntity);
//	WorkFlowManagementEntity createWorkFlowMangement(WorkFlowManagementEntity workFlowManagementEntity);
//	Map<String, Object> getWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, int page, int count, Integer programId);
//	List<RunTestEntity> getRunTestListWfm(List<Integer> runtestIdList);
//	WorkFlowManagementEntity getWorkFlowManagementEntity(Integer workFlowId);
//	
//	boolean deleteWfmrunTest(int wfmRunTestId);
//	
//	boolean getWFMRunTestEntity(int programId, String testname);
//	boolean getWfmEnbStatus(int programId, String neName);
	
	ReportsEntity createReports(ReportsEntity reportsEntity);
	ReportsEntity getEntityData(String programName, String enbId);
	Map<String, Object> getReportsDetails(Integer customerId, int page, int count, String programName,
			ReportsModel reportsModel,String type);
	CustomerDetailsEntity getCustomerDetailsEntityById(int programId);

	
}
