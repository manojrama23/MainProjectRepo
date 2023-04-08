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

import com.smart.rct.postmigration.entity.AuditHardwareDetailsEntity;
import com.smart.rct.postmigration.repository.AuditHardwareDetailsRepository;

@Transactional
@Repository
public class AuditHardwareDetailsRepositoryImpl implements AuditHardwareDetailsRepository{
	static final Logger logger = LoggerFactory.getLogger(AuditHardwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName) {

		List<AuditHardwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditHardwareDetailsEntity> query = cb.createQuery(AuditHardwareDetailsEntity.class);
			Root<AuditHardwareDetailsEntity> root = query.from(AuditHardwareDetailsEntity.class);

			query.select(root);
			TypedQuery<AuditHardwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = typedQuery.getResultList();
			//query.where(cb.equal(root.get("hardwareName"), hardwareName));
			query.where(cb.like(cb.lower(root.get("hardwareName")), "%" + hardwareName.toLowerCase() + "%"));
			TypedQuery<AuditHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GIssueEntityList() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}
	@Override
	public List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityListRx(String hardwareName, String vendorName,
			String type,String waveLength) {

		List<AuditHardwareDetailsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditHardwareDetailsEntity> query = cb.createQuery(AuditHardwareDetailsEntity.class);
			Root<AuditHardwareDetailsEntity> root = query.from(AuditHardwareDetailsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("hardwareName"), hardwareName),
					//cb.equal(root.get("vendorName"), vendorName),
					//cb.equal(root.get("type"), type),
					cb.equal(root.get("type"), type)));
			TypedQuery<AuditHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditconstantsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditConstantsEntityList() in  AuditConstantsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditconstantsEntityList;
	}
	@Override
	public List<AuditHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type) {

		List<AuditHardwareDetailsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditHardwareDetailsEntity> query = cb.createQuery(AuditHardwareDetailsEntity.class);
			Root<AuditHardwareDetailsEntity> root = query.from(AuditHardwareDetailsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("hardwareName"), hardwareName),
					cb.equal(root.get("type"), type)));
			TypedQuery<AuditHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
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
