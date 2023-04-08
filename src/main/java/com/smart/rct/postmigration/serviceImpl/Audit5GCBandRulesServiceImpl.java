package com.smart.rct.postmigration.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.postmigration.entity.Audit5GCBandRulesEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandRulesRepository;
import com.smart.rct.postmigration.service.Audit5GCBandRulesService;

@Service
public class Audit5GCBandRulesServiceImpl implements Audit5GCBandRulesService{
	final static Logger logger = LoggerFactory.getLogger(Audit5GCBandRulesServiceImpl.class);
	
	@Autowired
	Audit5GCBandRulesRepository audit5GCBandRulesRepository;

	@Override
	public Audit5GCBandRulesEntity getAudit5GCBandRulesEntityById(int auditRuleId) {
		Audit5GCBandRulesEntity audit5GCBandRulesEntity = null;
		try {
			audit5GCBandRulesEntity = audit5GCBandRulesRepository.getAudit5GCBandRulesEntityById(auditRuleId);
 		} catch(Exception e) {
			logger.error("Exception Audit5GCBandRulesServiceImpl in getAudit5GCBandRulesEntityById() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandRulesEntity;
	}
}
