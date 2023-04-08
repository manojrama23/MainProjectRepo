package com.smart.rct.postmigration.repository;

import java.util.List;

import com.smart.rct.postmigration.entity.Audit4GIssueEntity;

public interface Audit4GIssueRepository {

	Audit4GIssueEntity createAudit4GIssueEntity(Audit4GIssueEntity audit4gIssueEntity);

	List<Audit4GIssueEntity> getAudit4GIssueEntityList(String neId);

	boolean deleteaudit4GIssueEntityById(int auditIssueId);

	List<Audit4GIssueEntity> getAudit4GIssueEntityListByRunTestId(int runTestId);

}
