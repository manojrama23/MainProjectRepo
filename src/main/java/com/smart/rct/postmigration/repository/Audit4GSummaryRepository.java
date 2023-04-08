package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Set;

import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;

public interface Audit4GSummaryRepository {

	Audit4GSummaryEntity getaudit4GSummaryEntityById(int auditSummaryId);

	Audit4GSummaryEntity createAudit4GSummaryEntity(Audit4GSummaryEntity audit4gSummaryEntity);

	List<Audit4GSummaryEntity> getAudit4GSummaryEntityList(int runTestId);

	boolean deleteaudit4GSummaryEntityById(int auditSummaryId);
	
	boolean deleteaudit4GPassFailEntityById(int auditSummaryId);
	
	Audit4GPassFailEntity getaudit4GPassFailEntityById(int auditSummaryId);

	Audit4GPassFailEntity createAudit4GPassFailEntity(Audit4GPassFailEntity audit4gPassFailEntity);

	List<Audit4GPassFailEntity> getAudit4GPassFailEntityList(int runId);

	List<Audit4GPassFailEntity> createAudit4GPassFailEntityList(Set<Integer> set1);

}
