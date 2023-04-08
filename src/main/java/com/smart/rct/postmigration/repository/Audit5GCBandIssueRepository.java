package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit5GCBandIssueEntity;

public interface Audit5GCBandIssueRepository {

	Audit5GCBandIssueEntity createAudit5GCBandIssueEntity(Audit5GCBandIssueEntity Audit5GCBandIssueEntity);

	List<Audit5GCBandIssueEntity> getAudit5GCBandIssueEntityList(String neId);

	boolean deleteaudit5GCBandIssueEntityById(int auditIssueId);

	List<Audit5GCBandIssueEntity> getAudit5GCBandIssueEntityListByRunTestId(int runTestId);

}
