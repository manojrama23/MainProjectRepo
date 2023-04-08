package com.smart.rct.postmigration.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuIssueEntity;
import com.smart.rct.postmigration.repository.Audit4GFsuIssueRepository;
import com.smart.rct.postmigration.service.Audit4GFsuIssueService;

@Service
public class Audit4GFsuIssueServiceImpl implements Audit4GFsuIssueService{

	final static Logger logger = LoggerFactory.getLogger(Audit4GFsuIssueServiceImpl.class);
	
	@Autowired
	Audit4GFsuIssueRepository audit4GFsuIssueRepository;
	
	@Override
	public void createAudit4GFsuIssueEntity(String neId, RunTestEntity runTestEntity) {
		try {
			Audit4GFsuIssueEntity audit4GFsuIssueEntity = new Audit4GFsuIssueEntity();
			List<Audit4GFsuIssueEntity> audit4GFsuIssueEntityList = audit4GFsuIssueRepository.getAudit4GFsuIssueEntityList(neId);
			if(audit4GFsuIssueEntityList!=null && !audit4GFsuIssueEntityList.isEmpty()) {
				audit4GFsuIssueEntity = audit4GFsuIssueEntityList.get(0);
				audit4GFsuIssueEntity.setRunTestEntity(runTestEntity);
				audit4GFsuIssueRepository.createAudit4GFsuIssueEntity(audit4GFsuIssueEntity);
			} else {
				audit4GFsuIssueEntity.setNeId(neId);
				audit4GFsuIssueEntity.setRunTestEntity(runTestEntity);
				audit4GFsuIssueRepository.createAudit4GFsuIssueEntity(audit4GFsuIssueEntity);
			}
 		} catch(Exception e) {
			logger.error("Exception Audit4GIssueServiceImpl in createAudit4GFsuIssueEntity() " + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	@Override
	public boolean deleteaudit4GFsuIssueEntityByRunTestId(int runTestId) {
		boolean status = true;
		try {
			List<Audit4GFsuIssueEntity> audit4GFsuIssueEntityList = audit4GFsuIssueRepository.getAudit4GFsuIssueEntityListByRunTestId(runTestId);
			if(audit4GFsuIssueEntityList!=null && !audit4GFsuIssueEntityList.isEmpty()) {
				status = audit4GFsuIssueRepository.deleteaudit4GFsuIssueEntityById(audit4GFsuIssueEntityList.get(0).getId());
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GIssueEntityById() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
}
