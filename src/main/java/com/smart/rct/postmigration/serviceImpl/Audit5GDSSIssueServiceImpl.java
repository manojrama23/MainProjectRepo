package com.smart.rct.postmigration.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSIssueRepository;
import com.smart.rct.postmigration.service.Audit5GDSSIssueService;

@Service
public class Audit5GDSSIssueServiceImpl implements Audit5GDSSIssueService{

	final static Logger logger = LoggerFactory.getLogger(Audit5GDSSIssueServiceImpl.class);
	
	@Autowired
	Audit5GDSSIssueRepository audit5GDSSIssueRepository;
	
	@Override
	public void createAudit5GDSSIssueEntity(String neId, RunTestEntity runTestEntity) {
		try {
			Audit5GDSSIssueEntity audit5GDSSIssueEntity = new Audit5GDSSIssueEntity();
			List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = audit5GDSSIssueRepository.getAudit5GDSSIssueEntityList(neId);
			if(audit5GDSSIssueEntityList!=null && !audit5GDSSIssueEntityList.isEmpty()) {
				audit5GDSSIssueEntity = audit5GDSSIssueEntityList.get(0);
				audit5GDSSIssueEntity.setRunTestEntity(runTestEntity);
				audit5GDSSIssueRepository.createAudit5GDSSIssueEntity(audit5GDSSIssueEntity);
			} else {
				audit5GDSSIssueEntity.setNeId(neId);
				audit5GDSSIssueEntity.setRunTestEntity(runTestEntity);
				audit5GDSSIssueRepository.createAudit5GDSSIssueEntity(audit5GDSSIssueEntity);
			}
 		} catch(Exception e) {
			logger.error("Exception Audit5GDSSIssueServiceImpl in createAudit5GDSSIssueEntity() " + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	@Override
	public boolean deleteaudit5GDSSIssueEntityByRunTestId(int runTestId) {
		boolean status = true;
		try {
			List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = audit5GDSSIssueRepository.getAudit5GDSSIssueEntityListByRunTestId(runTestId);
			if(audit5GDSSIssueEntityList!=null && !audit5GDSSIssueEntityList.isEmpty()) {
				status = audit5GDSSIssueRepository.deleteaudit5GDSSIssueEntityById(audit5GDSSIssueEntityList.get(0).getId());
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GDSSIssueEntityById() in  Audit5GDSSIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
}
