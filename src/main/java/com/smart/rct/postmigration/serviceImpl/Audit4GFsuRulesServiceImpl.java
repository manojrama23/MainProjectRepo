package com.smart.rct.postmigration.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.repository.Audit4GFsuRulesRepository;
import com.smart.rct.postmigration.service.Audit4GFsuRulesService;

@Service
public class Audit4GFsuRulesServiceImpl implements Audit4GFsuRulesService{
	final static Logger logger = LoggerFactory.getLogger(Audit4GFsuRulesServiceImpl.class);
	
	@Autowired
	Audit4GFsuRulesRepository audit4GFsuRulesRepository;

	@Override
	public Audit4GFsuRulesEntity getAudit4GFsuRulesEntityById(int auditRuleId) {
		Audit4GFsuRulesEntity audit4GFsuRulesEntity = null;
		try {
			audit4GFsuRulesEntity = audit4GFsuRulesRepository.getAudit4GFsuRulesEntityById(auditRuleId);
 		} catch(Exception e) {
			logger.error("Exception Audit4GFsuRulesServiceImpl in getAudit4GRulesEntityById() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuRulesEntity;
	}
}
