package com.smart.rct.postmigration.service;

import com.smart.rct.migration.entity.RunTestEntity;

public interface Audit4GFsuIssueService {

	void createAudit4GFsuIssueEntity(String neId, RunTestEntity runTestEntity);

	boolean deleteaudit4GFsuIssueEntityByRunTestId(int runTestId);

}
