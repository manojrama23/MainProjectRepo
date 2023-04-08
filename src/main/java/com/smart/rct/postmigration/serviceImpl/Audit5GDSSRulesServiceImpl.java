package com.smart.rct.postmigration.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSRulesRepository;
import com.smart.rct.postmigration.service.Audit5GDSSRulesService;

@Service
public class Audit5GDSSRulesServiceImpl implements Audit5GDSSRulesService{
	final static Logger logger = LoggerFactory.getLogger(Audit5GDSSRulesServiceImpl.class);
	
	@Autowired
	Audit5GDSSRulesRepository audit5GDSSRulesRepository;

	@Override
	public Audit5GDSSRulesEntity getAudit5GDSSRulesEntityById(int auditRuleId) {
		Audit5GDSSRulesEntity audit5GDSSRulesEntity = null;
		try {
			audit5GDSSRulesEntity = audit5GDSSRulesRepository.getAudit5GDSSRulesEntityById(auditRuleId);
 		} catch(Exception e) {
			logger.error("Exception Audit5GDSSRulesServiceImpl in getAudit5GDSSRulesEntityById() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSRulesEntity;
	}
}
