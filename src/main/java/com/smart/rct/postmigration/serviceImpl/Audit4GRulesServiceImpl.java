package com.smart.rct.postmigration.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.postmigration.entity.Audit4GRulesEntity;
import com.smart.rct.postmigration.repository.Audit4GRulesRepository;
import com.smart.rct.postmigration.service.Audit4GRulesService;

@Service
public class Audit4GRulesServiceImpl implements Audit4GRulesService{
	final static Logger logger = LoggerFactory.getLogger(Audit4GRulesServiceImpl.class);
	
	@Autowired
	Audit4GRulesRepository audit4GRulesRepository;

	@Override
	public Audit4GRulesEntity getAudit4GRulesEntityById(int auditRuleId) {
		Audit4GRulesEntity audit4GRulesEntity = null;
		try {
			audit4GRulesEntity = audit4GRulesRepository.getAudit4GRulesEntityById(auditRuleId);
 		} catch(Exception e) {
			logger.error("Exception Audit4GRulesServiceImpl in getAudit4GRulesEntityById() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GRulesEntity;
	}
}
