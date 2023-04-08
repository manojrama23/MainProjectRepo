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

import com.smart.rct.postmigration.entity.AuditDetailConstantsEntity;
import com.smart.rct.postmigration.repository.AuditDetailConstantsRepository;

@Repository
@Transactional
public class AuditDetailConstantsRepositoryImpl implements AuditDetailConstantsRepository{

	static final Logger logger = LoggerFactory.getLogger(AuditConstantsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<AuditDetailConstantsEntity> getAuditConstantsEntityList(String bandWidth, String productCode, String band, String diversity) {

		List<AuditDetailConstantsEntity> auditconstantsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditDetailConstantsEntity> query = cb.createQuery(AuditDetailConstantsEntity.class);
			Root<AuditDetailConstantsEntity> root = query.from(AuditDetailConstantsEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("bandWidth"), bandWidth),
					cb.equal(root.get("productCode"), productCode),
					cb.equal(root.get("band"), band),
					cb.equal(root.get("diversity"), diversity)));
			TypedQuery<AuditDetailConstantsEntity> queryResult = entityManager.createQuery(query);
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
