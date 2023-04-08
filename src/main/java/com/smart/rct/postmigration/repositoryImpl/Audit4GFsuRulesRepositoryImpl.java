package com.smart.rct.postmigration.repositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.repository.Audit4GFsuRulesRepository;

@Transactional
@Repository
public class Audit4GFsuRulesRepositoryImpl implements Audit4GFsuRulesRepository{

final static Logger logger = LoggerFactory.getLogger(Audit4GFsuRulesRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Audit4GFsuRulesEntity getAudit4GFsuRulesEntityById(int auditRuleId) {
		return entityManager.find(Audit4GFsuRulesEntity.class, auditRuleId);
	}
}
