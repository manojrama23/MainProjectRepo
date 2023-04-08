package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Set;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;

public interface Audit5GDSSSummaryRepository {

	Audit5GDSSSummaryEntity getaudit5GDSSSummaryEntityById(int auditSummaryId);
	
	Audit5GDSSPassFailSummaryEntity getaudit5GDSSPassFailEntityById(int auditSummaryId);

	Audit5GDSSSummaryEntity createAudit5GDSSSummaryEntity(Audit5GDSSSummaryEntity audit5GDSSSummaryEntity);

	List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityList(int runTestId);

	boolean deleteaudit5GDSSSummaryEntityById(int auditSummaryId);
	
	boolean deleteaudit5GDSSPassFailEntityById(int auditSummaryId);

	Audit5GDSSPassFailSummaryEntity createAudit5GDSSPassFailEntity(Audit5GDSSPassFailSummaryEntity audit5gdssPassFailSummaryEntity);

	List<Audit5GDSSPassFailSummaryEntity> createAudit5GDSSPassFailEntityList(Set<Integer> set1);

	List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailEntityList(int runId);

}
