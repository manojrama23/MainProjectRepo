package com.smart.rct.postmigration.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandIssueEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandIssueRepository;
import com.smart.rct.postmigration.service.Audit5GCBandIssueService;

@Service
public class Audit5GCBandIssueServiceImpl implements Audit5GCBandIssueService{

	final static Logger logger = LoggerFactory.getLogger(Audit5GCBandIssueServiceImpl.class);
	
	@Autowired
	Audit5GCBandIssueRepository audit5GCBandIssueRepository;
	
	@Override
	public void createAudit5GCBandIssueEntity(String neId, RunTestEntity runTestEntity) {
		try {
			Audit5GCBandIssueEntity audit5GCBandIssueEntity = new Audit5GCBandIssueEntity();
			List<Audit5GCBandIssueEntity> audit5GCBandIssueEntityList = audit5GCBandIssueRepository.getAudit5GCBandIssueEntityList(neId);
			if(audit5GCBandIssueEntityList!=null && !audit5GCBandIssueEntityList.isEmpty()) {
				audit5GCBandIssueEntity = audit5GCBandIssueEntityList.get(0);
				audit5GCBandIssueEntity.setRunTestEntity(runTestEntity);
				audit5GCBandIssueRepository.createAudit5GCBandIssueEntity(audit5GCBandIssueEntity);
			} else {
				audit5GCBandIssueEntity.setNeId(neId);
				audit5GCBandIssueEntity.setRunTestEntity(runTestEntity);
				audit5GCBandIssueRepository.createAudit5GCBandIssueEntity(audit5GCBandIssueEntity);
			}
 		} catch(Exception e) {
			logger.error("Exception Audit5GCBandIssueServiceImpl in createAudit5GCBandIssueEntity() " + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	@Override
	public boolean deleteaudit5GCBandIssueEntityByRunTestId(int runTestId) {
		boolean status = true;
		try {
			List<Audit5GCBandIssueEntity> audit5GCBandIssueEntityList = audit5GCBandIssueRepository.getAudit5GCBandIssueEntityListByRunTestId(runTestId);
			if(audit5GCBandIssueEntityList!=null && !audit5GCBandIssueEntityList.isEmpty()) {
				status = audit5GCBandIssueRepository.deleteaudit5GCBandIssueEntityById(audit5GCBandIssueEntityList.get(0).getId());
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GCBandIssueEntityById() in  Audit5GCBandIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
}
