package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;

public interface Audit5GDSSIssueRepository {

	Audit5GDSSIssueEntity createAudit5GDSSIssueEntity(Audit5GDSSIssueEntity Audit5GDSSIssueEntity);

	List<Audit5GDSSIssueEntity> getAudit5GDSSIssueEntityList(String neId);

	boolean deleteaudit5GDSSIssueEntityById(int auditIssueId);

	List<Audit5GDSSIssueEntity> getAudit5GDSSIssueEntityListByRunTestId(int runTestId);

}
