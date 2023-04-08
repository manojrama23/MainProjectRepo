package com.smart.rct.postmigration.service;

import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;

public interface Audit5GDSSSummaryService {

	List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityListByRunTestId(int runTestId);

	Audit5GDSSSummaryEntity createAudit5GDSSSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue);

	Audit5GDSSSummaryEntity getaudit5GDSSSummaryEntityById(int auditSummaryId);

	List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityListByNeId(String neId);

	boolean createAudit5GDSSSummaryReportExcel(JSONObject audit5gDSSSummaryReportDetails, String filePath, String neName);

	boolean deleteAuditSummaryReport(int runTestId);
	
	Audit5GDSSPassFailSummaryEntity createAudit5GDSSPassFailEntity(int auditRuleId, int runTestId, String neId, String auditPassFail);

	List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailSummaryEntityListByRunTestId(Set<Integer> set1);

	List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailsEntityEachRunId(int runId);

	boolean deleteAuditPassFailReport(int runTestId);

}
