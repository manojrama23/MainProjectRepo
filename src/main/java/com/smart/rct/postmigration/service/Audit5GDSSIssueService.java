package com.smart.rct.postmigration.service;

import com.smart.rct.migration.entity.RunTestEntity;

public interface Audit5GDSSIssueService {

	void createAudit5GDSSIssueEntity(String neId, RunTestEntity runTestEntity);

	boolean deleteaudit5GDSSIssueEntityByRunTestId(int runTestId);

}
