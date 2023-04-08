package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Set;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;

public interface Audit5GCBandSummaryRepository {

	Audit5GCBandSummaryEntity getaudit5GCBandSummaryEntityById(int auditSummaryId);

	Audit5GCBandSummaryEntity createAudit5GCBandSummaryEntity(Audit5GCBandSummaryEntity audit5GCBandSummaryEntity);

	List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityList(int runTestId);

	boolean deleteaudit5GCBandSummaryEntityById(int auditSummaryId);
	
	boolean deleteaudit5GCBandPassFailEntityById(int auditSummaryId);
	
	Audit5GCBandPassFailEntity getaudit5GCBandPassFailEntityById(int auditSummaryId);

	Audit5GCBandPassFailEntity createAudit5GCBandPassFailEntity(Audit5GCBandPassFailEntity audit5gcBandPassFailEntity);

	List<Audit5GCBandPassFailEntity> createAudit5GCBandPassFailEachId(int runId);

	List<Audit5GCBandPassFailEntity> createAudit5GCBandPassFailEntityList(Set<Integer> set1);

}
