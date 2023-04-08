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
import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSIssueRepository;

@Transactional
@Repository
public class Audit5GDSSIssueRepositoryImpl implements Audit5GDSSIssueRepository{
final static Logger logger = LoggerFactory.getLogger(Audit5GDSSIssueRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Audit5GDSSIssueEntity> getAudit5GDSSIssueEntityList(String neId) {

		List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GDSSIssueEntity> query = cb.createQuery(Audit5GDSSIssueEntity.class);
			Root<Audit5GDSSIssueEntity> root = query.from(Audit5GDSSIssueEntity.class);

			query.select(root);
			TypedQuery<Audit5GDSSIssueEntity> typedQuery = entityManager.createQuery(query);
			audit5GDSSIssueEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("neId"), neId));
			TypedQuery<Audit5GDSSIssueEntity> queryResult = entityManager.createQuery(query);
			audit5GDSSIssueEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GDSSIssueEntityList() in  Audit5GDSSIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSIssueEntityList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Audit5GDSSIssueEntity> getAudit5GDSSIssueEntityListByRunTestId(int runTestId) {

		List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit5GDSSIssueEntity.class);
			criteria.createAlias("runTestEntity", "runTestEntity");
			Conjunction conjunction = Restrictions.conjunction();			
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			criteria.add(conjunction);
			audit5GDSSIssueEntityList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GDSSIssueEntityListByRunTestId() in  Audit5GDSSIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSIssueEntityList;
	}
	
	@Override
	public Audit5GDSSIssueEntity createAudit5GDSSIssueEntity(Audit5GDSSIssueEntity audit5GDSSIssueEntity) {
		Audit5GDSSIssueEntity audit5GDSSIssueEntityResult = null;
		try {
			audit5GDSSIssueEntityResult = entityManager.merge(audit5GDSSIssueEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GDSSIssueEntity() in  Audit5GDSSIssueRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSIssueEntityResult;
	}
	
	@Override
	public boolean deleteaudit5GDSSIssueEntityById(int auditIssueId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GDSSIssueEntityById(auditIssueId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GDSSIssueEntityById() in  Audit5GDSSIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit5GDSSIssueEntity getaudit5GDSSIssueEntityById(int auditIssueId) {
		return entityManager.find(Audit5GDSSIssueEntity.class, auditIssueId);
	}
}
