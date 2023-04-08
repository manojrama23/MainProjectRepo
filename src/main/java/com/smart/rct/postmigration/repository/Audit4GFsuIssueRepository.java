package com.smart.rct.postmigration.repository;

import java.util.List;


import com.smart.rct.postmigration.entity.Audit4GFsuIssueEntity;


public interface Audit4GFsuIssueRepository {

	Audit4GFsuIssueEntity createAudit4GFsuIssueEntity(Audit4GFsuIssueEntity audit4gFsuIssueEntity);

	List<Audit4GFsuIssueEntity> getAudit4GFsuIssueEntityList(String neId);

	boolean deleteaudit4GFsuIssueEntityById(int auditIssueId);

	List<Audit4GFsuIssueEntity> getAudit4GFsuIssueEntityListByRunTestId(int runTestId);

	

}
