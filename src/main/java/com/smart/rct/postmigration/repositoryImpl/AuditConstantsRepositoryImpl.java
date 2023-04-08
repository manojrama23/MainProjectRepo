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

import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;

@Repository
@Transactional
public class AuditConstantsRepositoryImpl implements AuditConstantsRepository{

	static final Logger logger = LoggerFactory.getLogger(AuditConstantsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<AuditConstantsEntity> getAuditConstantsEntityList(String programName, String parameterName, String type) {

		List<AuditConstantsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditConstantsEntity> query = cb.createQuery(AuditConstantsEntity.class);
			Root<AuditConstantsEntity> root = query.from(AuditConstantsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("programName"), programName),
					cb.equal(root.get("parameterName"), parameterName),
					cb.equal(root.get("type"), type)));
			TypedQuery<AuditConstantsEntity> queryResult = entityManager.createQuery(query);
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
	public List<AuditConstantsEntity> getAuditConstantsEntityList(String programName, String parameterName) {

		List<AuditConstantsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditConstantsEntity> query = cb.createQuery(AuditConstantsEntity.class);
			Root<AuditConstantsEntity> root = query.from(AuditConstantsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("programName"), programName),
					cb.equal(root.get("parameterName"), parameterName)));
			TypedQuery<AuditConstantsEntity> queryResult = entityManager.createQuery(query);
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
