package com.smart.rct.postmigration.repositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.postmigration.entity.Audit4GRulesEntity;
import com.smart.rct.postmigration.repository.Audit4GRulesRepository;

@Transactional
@Repository
public class Audit4GRulesRepositoryImpl implements Audit4GRulesRepository{

final static Logger logger = LoggerFactory.getLogger(Audit4GRulesRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Audit4GRulesEntity getAudit4GRulesEntityById(int auditRuleId) {
		return entityManager.find(Audit4GRulesEntity.class, auditRuleId);
	}
}
