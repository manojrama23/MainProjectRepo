package com.smart.rct.postmigration.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.common.entity.ReportsEntity;

public interface ReportsService {


	Map<String, Object> getReportsDetails(Integer customerId, int page, int count, String programName,ReportsModel reportsModel);

	boolean getDetailsToCreateExcel(Integer customerId, int page, int count, String programName,
			ReportsModel reportsModel, List<String> filter,String type);


	void insertRunTestReportDetails(String programName, String enbId, String migType, String migSubType,
			List<LinkedHashMap> useCaseList, String finalStatus, String user);

	void insertPostMigAuditReportDetails(String enbId, Integer programId, String programName, String user,
			String ciqFileName, Map<String, String> scriptDetails, String finalStatus, String migSubtype);

	void insertReportDetails(JSONObject reportsObject, Integer programId, String programName, String user,
			String ciqFileName, String filetype);

}
