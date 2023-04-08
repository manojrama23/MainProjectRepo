package com.smart.rct.postmigration.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;

public interface Audit4GFsuSummaryService {

	List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityListByRunTestId(int runTestId);

	Audit4GFsuSummaryEntity createAudit4GFsuSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue);

	Audit4GFsuSummaryEntity getaudit4GFsuSummaryEntityById(int auditSummaryId);

	List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityListByNeId(String neId);

	boolean createAudit4GFsuSummaryReportExcel(JSONObject audit4gFsuSummaryReportDetails, String filePath, String neName);

	boolean deleteAuditSummaryReport(int runTestId);	
	
	Audit4GFsuPassFailSummaryEntity createAudit4GFsuPassFailSummaryEntity(int auditRuleId, int runTestId, String neId, String auditPassFail);

	List<Audit4GFsuPassFailSummaryEntity> getAudit4GFsuPassFailSummaryEntityListByRunTestId(Set<Integer> set1);

	List<Audit4GFsuPassFailSummaryEntity> getAudit4GFsuPassFailSummaryEntityEachRunId(int runId);

	boolean createAudit4GFsuPassFailSummaryReportExcel(JSONObject audit4gFsuSummaryReportDetails, String filePath, String neName);

	boolean createBulkAudit4GFsuSummaryReportExcel(JSONObject downloadBulkReport, String filePath);


	boolean deleteAuditPassFailReport(int runTestId);


	

}
