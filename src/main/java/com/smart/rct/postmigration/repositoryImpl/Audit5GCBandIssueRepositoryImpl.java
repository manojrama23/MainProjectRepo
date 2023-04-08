package com.smart.rct.postmigration.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandIssueEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandIssueRepository;

@Transactional
@Repository
public class Audit5GCBandIssueRepositoryImpl implements Audit5GCBandIssueRepository{
final static Logger logger = LoggerFactory.getLogger(Audit5GCBandIssueRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Audit5GCBandIssueEntity> getAudit5GCBandIssueEntityList(String neId) {

		List<Audit5GCBandIssueEntity> audit5GCBandIssueEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GCBandIssueEntity> query = cb.createQuery(Audit5GCBandIssueEntity.class);
			Root<Audit5GCBandIssueEntity> root = query.from(Audit5GCBandIssueEntity.class);

			query.select(root);
			TypedQuery<Audit5GCBandIssueEntity> typedQuery = entityManager.createQuery(query);
			audit5GCBandIssueEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("neId"), neId));
			TypedQuery<Audit5GCBandIssueEntity> queryResult = entityManager.createQuery(query);
			audit5GCBandIssueEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GCBandIssueEntityList() in  Audit5GCBandIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandIssueEntityList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Audit5GCBandIssueEntity> getAudit5GCBandIssueEntityListByRunTestId(int runTestId) {

		List<Audit5GCBandIssueEntity> audit5GCBandIssueEntityList = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit5GCBandIssueEntity.class);
			criteria.createAlias("runTestEntity", "runTestEntity");
			Conjunction conjunction = Restrictions.conjunction();			
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			criteria.add(conjunction);
			audit5GCBandIssueEntityList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GCBandIssueEntityListByRunTestId() in  Audit5GCBandIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandIssueEntityList;
	}
	
	@Override
	public Audit5GCBandIssueEntity createAudit5GCBandIssueEntity(Audit5GCBandIssueEntity audit5GCBandIssueEntity) {
		Audit5GCBandIssueEntity audit5GCBandIssueEntityResult = null;
		try {
			audit5GCBandIssueEntityResult = entityManager.merge(audit5GCBandIssueEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GCBandIssueEntity() in  Audit5GCBandIssueRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandIssueEntityResult;
	}
	
	@Override
	public boolean deleteaudit5GCBandIssueEntityById(int auditIssueId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GCBandIssueEntityById(auditIssueId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GCBandIssueEntityById() in  Audit5GCBandIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit5GCBandIssueEntity getaudit5GCBandIssueEntityById(int auditIssueId) {
		return entityManager.find(Audit5GCBandIssueEntity.class, auditIssueId);
	}
}
