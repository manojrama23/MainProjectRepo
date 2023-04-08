package com.smart.rct.postmigration.repositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSRulesRepository;

@Transactional
@Repository
public class Audit5GDSSRulesRepositoryImpl implements Audit5GDSSRulesRepository{

final static Logger logger = LoggerFactory.getLogger(Audit5GDSSRulesRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Audit5GDSSRulesEntity getAudit5GDSSRulesEntityById(int auditRuleId) {
		return entityManager.find(Audit5GDSSRulesEntity.class, auditRuleId);
	}
}
