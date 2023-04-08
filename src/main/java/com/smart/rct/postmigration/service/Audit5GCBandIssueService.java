package com.smart.rct.postmigration.service;

import com.smart.rct.migration.entity.RunTestEntity;

public interface Audit5GCBandIssueService {

	void createAudit5GCBandIssueEntity(String neId, RunTestEntity runTestEntity);

	boolean deleteaudit5GCBandIssueEntityByRunTestId(int runTestId);

}
