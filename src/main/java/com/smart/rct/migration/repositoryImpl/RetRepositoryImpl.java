package com.smart.rct.migration.repositoryImpl;

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

import com.smart.rct.migration.entity.RetTestEntity;
import com.smart.rct.migration.repository.RetRepository;

@Transactional
@Repository
public class RetRepositoryImpl implements RetRepository {
static final Logger logger = LoggerFactory.getLogger(RetRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public List<RetTestEntity> getAuditRetDetailsEntityList(String neId, String uniqueId) {

		List<RetTestEntity> auditRetDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RetTestEntity> query = cb.createQuery(RetTestEntity.class);
			Root<RetTestEntity> root = query.from(RetTestEntity.class);

			query.select(root);
			TypedQuery<RetTestEntity> typedQuery = entityManager.createQuery(query);
			auditRetDetailsEntityList = typedQuery.getResultList();
			query.where(cb.and(cb.equal(root.get("neId"), neId),
					
					cb.equal(root.get("uniqueId"), uniqueId)));
			TypedQuery<RetTestEntity> queryResult = entityManager.createQuery(query);
			auditRetDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GIssueEntityList() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditRetDetailsEntityList;
	}
	@Override
	public boolean saveRetDeatil(RetTestEntity objInfo) {
		boolean status = false;
		try {
			entityManager.merge(objInfo);
			status = true;

		} catch (Exception e) {
			logger.error("Exception saveCsvAudit() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	@Override
	public List<RetTestEntity> getAuditRetEntity(String neId) {

		List<RetTestEntity> auditRetDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RetTestEntity> query = cb.createQuery(RetTestEntity.class);
			Root<RetTestEntity> root = query.from(RetTestEntity.class);

			query.select(root);
			TypedQuery<RetTestEntity> typedQuery = entityManager.createQuery(query);
			auditRetDetailsEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("neId"), neId));
			TypedQuery<RetTestEntity> queryResult = entityManager.createQuery(query);
			auditRetDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GIssueEntityList() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditRetDetailsEntityList;
	}
	

}
