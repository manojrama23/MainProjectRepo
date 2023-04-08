package com.smart.rct.postmigration.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.repository.Audit4GIssueRepository;
import com.smart.rct.postmigration.service.Audit4GIssueService;

@Service
public class Audit4GIssueServiceImpl implements Audit4GIssueService{

	final static Logger logger = LoggerFactory.getLogger(Audit4GIssueServiceImpl.class);
	
	@Autowired
	Audit4GIssueRepository audit4GIssueRepository;
	
	@Override
	public void createAudit4GIssueEntity(String neId, RunTestEntity runTestEntity) {
		try {
			Audit4GIssueEntity audit4GIssueEntity = new Audit4GIssueEntity();
			List<Audit4GIssueEntity> audit4GIssueEntityList = audit4GIssueRepository.getAudit4GIssueEntityList(neId);
			if(audit4GIssueEntityList!=null && !audit4GIssueEntityList.isEmpty()) {
				audit4GIssueEntity = audit4GIssueEntityList.get(0);
				audit4GIssueEntity.setRunTestEntity(runTestEntity);
				audit4GIssueRepository.createAudit4GIssueEntity(audit4GIssueEntity);
			} else {
				audit4GIssueEntity.setNeId(neId);
				audit4GIssueEntity.setRunTestEntity(runTestEntity);
				audit4GIssueRepository.createAudit4GIssueEntity(audit4GIssueEntity);
			}
 		} catch(Exception e) {
			logger.error("Exception Audit4GIssueServiceImpl in createAudit4GIssueEntity() " + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	@Override
	public boolean deleteaudit4GIssueEntityByRunTestId(int runTestId) {
		boolean status = true;
		try {
			List<Audit4GIssueEntity> audit4GIssueEntityList = audit4GIssueRepository.getAudit4GIssueEntityListByRunTestId(runTestId);
			if(audit4GIssueEntityList!=null && !audit4GIssueEntityList.isEmpty()) {
				status = audit4GIssueRepository.deleteaudit4GIssueEntityById(audit4GIssueEntityList.get(0).getId());
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GIssueEntityById() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
}
