package com.smart.rct.postmigration.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.postmigration.entity.AuditCbandHardwareDetailsEntity;
import com.smart.rct.postmigration.repository.AuditCbandHardwareDetailsRepository;

@Transactional
@Repository
public class AuditCbandHardwareDetailsRepositoryImpl implements AuditCbandHardwareDetailsRepository{
	static final Logger logger = LoggerFactory.getLogger(AuditCbandHardwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<AuditCbandHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName) {

		List<AuditCbandHardwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCbandHardwareDetailsEntity> query = cb.createQuery(AuditCbandHardwareDetailsEntity.class);
			Root<AuditCbandHardwareDetailsEntity> root = query.from(AuditCbandHardwareDetailsEntity.class);

			query.select(root);
			TypedQuery<AuditCbandHardwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = typedQuery.getResultList();
			//query.where(cb.equal(root.get("hardwareName"), hardwareName));
			//query.where(cb.like(cb.lower(root.get("hardwareName")), "%" + hardwareName.toLowerCase() + "%"));
			//TypedQuery<AuditCbandHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
			//auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditHardwareDetailsEntityList() in  AuditCbandHardwareDetailsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}
	
}
