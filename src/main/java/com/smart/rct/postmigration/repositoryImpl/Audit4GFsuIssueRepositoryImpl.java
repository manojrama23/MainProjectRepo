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
import com.smart.rct.postmigration.entity.Audit4GFsuIssueEntity;
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.repository.Audit4GFsuIssueRepository;

@Transactional
@Repository
public class Audit4GFsuIssueRepositoryImpl implements Audit4GFsuIssueRepository{
final static Logger logger = LoggerFactory.getLogger(Audit4GIssueRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Audit4GFsuIssueEntity> getAudit4GFsuIssueEntityList(String neId) {

		List<Audit4GFsuIssueEntity> audit4GFsuIssueEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GFsuIssueEntity> query = cb.createQuery(Audit4GFsuIssueEntity.class);
			Root<Audit4GFsuIssueEntity> root = query.from(Audit4GFsuIssueEntity.class);

			query.select(root);
			TypedQuery<Audit4GFsuIssueEntity> typedQuery = entityManager.createQuery(query);
			audit4GFsuIssueEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("neId"), neId));
			TypedQuery<Audit4GFsuIssueEntity> queryResult = entityManager.createQuery(query);
			audit4GFsuIssueEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuIssueEntityList() in  Audit4GFsuIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuIssueEntityList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Audit4GFsuIssueEntity> getAudit4GFsuIssueEntityListByRunTestId(int runTestId) {

		List<Audit4GFsuIssueEntity> audit4GFsuIssueEntityList = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit4GFsuIssueEntity.class);
			criteria.createAlias("runTestEntity", "runTestEntity");
			Conjunction conjunction = Restrictions.conjunction();			
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			criteria.add(conjunction);
			audit4GFsuIssueEntityList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuIssueEntityListByRunTestId() in  Audit4GFsuIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuIssueEntityList;
	}
	
	@Override
	public Audit4GFsuIssueEntity createAudit4GFsuIssueEntity(Audit4GFsuIssueEntity audit4GFsuIssueEntity) {
		Audit4GFsuIssueEntity audit4GFsuIssueEntityResult = null;
		try {
			audit4GFsuIssueEntityResult = entityManager.merge(audit4GFsuIssueEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GFsuIssueEntity() in  Audit4GFsuIssueRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuIssueEntityResult;
	}
	
	@Override
	public boolean deleteaudit4GFsuIssueEntityById(int auditIssueId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GFsuIssueEntityById(auditIssueId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GFsuIssueEntityById() in  Audit4GFsuIssueRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit4GFsuIssueEntity getaudit4GFsuIssueEntityById(int auditIssueId) {
		return entityManager.find(Audit4GFsuIssueEntity.class, auditIssueId);
	}
}

