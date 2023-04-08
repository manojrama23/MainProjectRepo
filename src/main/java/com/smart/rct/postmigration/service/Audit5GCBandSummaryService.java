package com.smart.rct.postmigration.service;

import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;

public interface Audit5GCBandSummaryService {

	List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityListByRunTestId(int runTestId);

	Audit5GCBandSummaryEntity createAudit5GCBandSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue);

	Audit5GCBandSummaryEntity getaudit5GCBandSummaryEntityById(int auditSummaryId);

	List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityListByNeId(String neId);

	boolean createAudit5GCBandSummaryReportExcel(JSONObject audit5gCbandSummaryReportDetails, String filePath, String neName);

	boolean deleteAuditSummaryReport(int runTestId);
	
	Audit5GCBandPassFailEntity createAudit5GCBandPassFailEntity(int auditRuleId, int runTestId, String neId, String PassFail) ;

	List<Audit5GCBandPassFailEntity> getAudit5GCBandPassFailsEntityEachRunId(int runId);

	List<Audit5GCBandPassFailEntity> getAudit5GCBandPassFailEntityListByRunTestId(Set<Integer> set1);

	boolean deleteAuditPassFailReport(int runTestId);



}
