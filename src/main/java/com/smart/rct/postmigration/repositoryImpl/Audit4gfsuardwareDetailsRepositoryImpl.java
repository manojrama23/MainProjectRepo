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

import com.smart.rct.postmigration.entity.Audit4GfsuHardwareDetailsEntity;
import com.smart.rct.postmigration.repository.AuditFSUHardwareDetailsRepository;


@Transactional
@Repository
public class Audit4gfsuardwareDetailsRepositoryImpl implements AuditFSUHardwareDetailsRepository{
	static final Logger logger = LoggerFactory.getLogger(Audit4gfsuardwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	public List<Audit4GfsuHardwareDetailsEntity> getAuditHardwareDetailsEntityList(String hardwareName, String type) {

		List<Audit4GfsuHardwareDetailsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GfsuHardwareDetailsEntity> query = cb.createQuery(Audit4GfsuHardwareDetailsEntity.class);
			Root<Audit4GfsuHardwareDetailsEntity> root = query.from(Audit4GfsuHardwareDetailsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("hardwareName"), hardwareName),
					cb.equal(root.get("type"), type)));
			TypedQuery<Audit4GfsuHardwareDetailsEntity> queryResult = entityManager.createQuery(query);
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
