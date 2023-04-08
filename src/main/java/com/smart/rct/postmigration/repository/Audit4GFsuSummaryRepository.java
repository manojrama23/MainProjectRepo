package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;

public interface Audit4GFsuSummaryRepository {

	Audit4GFsuSummaryEntity getaudit4GFsuSummaryEntityById(int auditSummaryId);

	Audit4GFsuSummaryEntity createAudit4GFsuSummaryEntity(Audit4GFsuSummaryEntity audit4gFsuSummaryEntity);
	
	List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityList(int runTestId);

	boolean deleteaudit4GFsuSummaryEntityById(int auditSummaryId);
	
	boolean deleteaudit4GFsuPassFailEntityById(int auditSummaryId);

	Audit4GFsuPassFailSummaryEntity createAudit4GFsuPassFailEntity(Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntity);
	
	List<Audit4GFsuPassFailSummaryEntity> createAudit4GFsuPassFailEntityList(Set<Integer> set1);
	//Audit4GFsuPassFailSummaryEntity createAudit4GFsuPassFailEntityList(int runTestId);

	List<Audit4GFsuPassFailSummaryEntity> createAudit4GFsuPassFailEachId(int runId);
	
	 Audit4GFsuPassFailSummaryEntity getaudit4GFsuPassFailEntityById(int auditSummaryId);
	
}
