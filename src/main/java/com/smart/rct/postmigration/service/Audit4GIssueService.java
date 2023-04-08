package com.smart.rct.postmigration.service;

import com.smart.rct.migration.entity.RunTestEntity;

public interface Audit4GIssueService {

	void createAudit4GIssueEntity(String neId, RunTestEntity runTestEntity);

	boolean deleteaudit4GIssueEntityByRunTestId(int runTestId);

}
