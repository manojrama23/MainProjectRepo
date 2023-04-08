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

import com.smart.rct.postmigration.entity.Audit5GMMHardwareDetailsEntity;
import com.smart.rct.postmigration.repository.Audit5GMMHardwareDetailsRepository;


@Transactional
@Repository
public class Audit5GMMHardwareDetailsRepositoryImpl implements Audit5GMMHardwareDetailsRepository{
	static final Logger logger = LoggerFactory.getLogger(AuditHardwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	public List<Audit5GMMHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type) {

		List<Audit5GMMHardwareDetailsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GMMHardwareDetailsEntity> query = cb.createQuery(Audit5GMMHardwareDetailsEntity.class);
			Root<Audit5GMMHardwareDetailsEntity> root = query.from(Audit5GMMHardwareDetailsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("hardwareName"), hardwareName),
					cb.equal(root.get("type"), type)));
			TypedQuery<Audit5GMMHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditconstantsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditConstantsEntityList() in  AuditConstantsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditconstantsEntityList;
	}
	
}
