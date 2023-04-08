package com.smart.rct.postmigration.service;

import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;

public interface Audit4GSummaryService {

	List<Audit4GSummaryEntity> getAudit4GSummaryEntityListByRunTestId(int runTestId);

	Audit4GSummaryEntity createAudit4GSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue);

	Audit4GSummaryEntity getaudit4GSummaryEntityById(int auditSummaryId);

	List<Audit4GSummaryEntity> getAudit4GSummaryEntityListByNeId(String neId);

	boolean createAudit4GSummaryReportExcel(JSONObject audit4gSummaryReportDetails, String filePath, String neName);

	boolean deleteAuditSummaryReport(int runTestId);
	
	
	Audit4GPassFailEntity createAudit4GPassFailEntity(int auditRuleId, int runTestId, String neId, String auditPassFail);
	

	List<Audit4GPassFailEntity> getAudit4GPassFailEntityEachRunId(int runId);

	List<Audit4GPassFailEntity> getAudit4GPassFailEntityListByRunTestId(Set<Integer> set1);

	boolean deleteAuditPassFailReport(int runTestId);

	
	
	
	

}
