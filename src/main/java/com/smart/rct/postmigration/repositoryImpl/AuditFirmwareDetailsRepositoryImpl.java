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

import com.smart.rct.postmigration.entity.AuditFirmwareDetailsEntity;
import com.smart.rct.postmigration.repository.AuditFirmwareDetailsRepository;

@Repository
@Transactional
public class AuditFirmwareDetailsRepositoryImpl implements AuditFirmwareDetailsRepository {

	static final Logger logger = LoggerFactory.getLogger(AuditFirmwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String neType) {

		List<AuditFirmwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditFirmwareDetailsEntity> query = cb.createQuery(AuditFirmwareDetailsEntity.class);
			Root<AuditFirmwareDetailsEntity> root = query.from(AuditFirmwareDetailsEntity.class);

			query.select(root);
			TypedQuery<AuditFirmwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = typedQuery.getResultList();
			query.where(cb.like(cb.lower(root.get("firmwareName")), "%" + firmwareName.toLowerCase() + "%"),
					cb.equal(root.get("neType"), neType));
			TypedQuery<AuditFirmwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditFirmwareDetailsEntityList() in  AuditFirmwareDetailsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}
	
	@Override
	public List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String neType, String neVersion) {

		List<AuditFirmwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditFirmwareDetailsEntity> query = cb.createQuery(AuditFirmwareDetailsEntity.class);
			Root<AuditFirmwareDetailsEntity> root = query.from(AuditFirmwareDetailsEntity.class);

			query.select(root);
			TypedQuery<AuditFirmwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = typedQuery.getResultList();
			query.where(cb.like(cb.lower(root.get("firmwareName")), "%" + firmwareName.toLowerCase() + "%"),
					cb.equal(root.get("neType"), neType),
					cb.equal(root.get("neVersion"), neVersion));
			TypedQuery<AuditFirmwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditFirmwareDetailsEntityList() in  AuditFirmwareDetailsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}

	@Override
	public List<AuditFirmwareDetailsEntity> getAuditFirmwareDetailsEntityALL(String firmwareName, String neType, String softwareVersion, String packageVersion) {

		List<AuditFirmwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditFirmwareDetailsEntity> query = cb.createQuery(AuditFirmwareDetailsEntity.class);
			Root<AuditFirmwareDetailsEntity> root = query.from(AuditFirmwareDetailsEntity.class);

			query.select(root);
			TypedQuery<AuditFirmwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = typedQuery.getResultList();
			query.where(cb.like(cb.lower(root.get("firmwareName")), "%" + firmwareName.toLowerCase() + "%"),
					cb.equal(root.get("neType"), neType),
					cb.equal(root.get("packageVersion"), packageVersion),
					cb.equal(root.get("neVersion"), softwareVersion));
			TypedQuery<AuditFirmwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditFirmwareDetailsEntityList() in  AuditFirmwareDetailsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}
}
