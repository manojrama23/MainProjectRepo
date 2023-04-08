package com.smart.rct.postmigration.repositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.postmigration.entity.Audit5GCBandRulesEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandRulesRepository;

@Transactional
@Repository
public class Audit5GCBandRulesRepositoryImpl implements Audit5GCBandRulesRepository{

final static Logger logger = LoggerFactory.getLogger(Audit5GCBandRulesRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Audit5GCBandRulesEntity getAudit5GCBandRulesEntityById(int auditRuleId) {
		return entityManager.find(Audit5GCBandRulesEntity.class, auditRuleId);
	}
}
