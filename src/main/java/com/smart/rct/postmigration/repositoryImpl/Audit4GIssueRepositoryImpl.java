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
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.repository.Audit4GIssueRepository;

@Transactional
@Repository
public class Audit4GIssueRepositoryImpl implements Audit4GIssueRepository{
final static Logger logger = LoggerFactory.getLogger(Audit4GIssueRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Audit4GIssueEntity> getAudit4GIssueEntityList(String neId) {

		List<Audit4GIssueEntity> audit4GIssueEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GIssueEntity> query = cb.createQuery(Audit4GIssueEntity.class);
			Root<Audit4GIssueEntity> root = query.from(Audit4GIssueEntity.class);

			query.select(root);
			TypedQuery<Audit4GIssueEntity> typedQuery = entityManager.createQuery(query);
			audit4GIssueEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("neId"), neId));
			TypedQuery<Audit4GIssueEntity> queryResult = entityManager.createQuery(query);
			audit4GIssueEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GIssueEntityList() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GIssueEntityList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Audit4GIssueEntity> getAudit4GIssueEntityListByRunTestId(int runTestId) {

		List<Audit4GIssueEntity> audit4GIssueEntityList = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit4GIssueEntity.class);
			criteria.createAlias("runTestEntity", "runTestEntity");
			Conjunction conjunction = Restrictions.conjunction();			
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			criteria.add(conjunction);
			audit4GIssueEntityList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GIssueEntityListByRunTestId() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GIssueEntityList;
	}
	
	@Override
	public Audit4GIssueEntity createAudit4GIssueEntity(Audit4GIssueEntity audit4GIssueEntity) {
		Audit4GIssueEntity audit4GIssueEntityResult = null;
		try {
			audit4GIssueEntityResult = entityManager.merge(audit4GIssueEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GIssueEntity() in  Audit4GIssueRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GIssueEntityResult;
	}
	
	@Override
	public boolean deleteaudit4GIssueEntityById(int auditIssueId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GIssueEntityById(auditIssueId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GIssueEntityById() in  Audit4GIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit4GIssueEntity getaudit4GIssueEntityById(int auditIssueId) {
		return entityManager.find(Audit4GIssueEntity.class, auditIssueId);
	}
}
