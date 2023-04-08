package com.smart.rct.postmigration.repositoryImpl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.constants.Constants;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.repository.Audit4GSummaryRepository;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class Audit4GSummaryRepositoryImpl implements Audit4GSummaryRepository {
	
	final static Logger logger = LoggerFactory.getLogger(Audit4GSummaryRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public boolean deleteaudit4GSummaryEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GSummaryEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GSummaryEntityById() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit4GSummaryEntity getaudit4GSummaryEntityById(int auditSummaryId) {
		return entityManager.find(Audit4GSummaryEntity.class, auditSummaryId);
	}
	
	@Override
	public Audit4GSummaryEntity createAudit4GSummaryEntity(Audit4GSummaryEntity audit4GSummaryEntity) {
		Audit4GSummaryEntity audit4GSummaryEntityResult = null;
		try {
			audit4GSummaryEntityResult = entityManager.merge(audit4GSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GSummaryEntity() in  Audit4GSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GSummaryEntityResult;
	}
	
	@Override
	public Audit4GPassFailEntity createAudit4GPassFailEntity(Audit4GPassFailEntity audit4gPassFailEntity) {
		Audit4GPassFailEntity audit4GPassFailEntityResult = null;
		try {
			audit4GPassFailEntityResult = entityManager.merge(audit4gPassFailEntity);
			
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GPassFailEntity() in  Audit4GSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
			
		}
		
		return audit4GPassFailEntityResult;
	}
	
	
	@Override
	public List<Audit4GSummaryEntity> getAudit4GSummaryEntityList(int runTestId) {

		List<Audit4GSummaryEntity> audit4GSummaryEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GSummaryEntity> query = cb.createQuery(Audit4GSummaryEntity.class);
			Root<Audit4GSummaryEntity> root = query.from(Audit4GSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit4GSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit4GSummaryEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runTestId));
			TypedQuery<Audit4GSummaryEntity> queryResult = entityManager.createQuery(query);
			audit4GSummaryEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GSummaryEntityList() in  Audit4GSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GSummaryEntityList;

	}

	@Override
	public List<Audit4GPassFailEntity> getAudit4GPassFailEntityList(int runId) {
	
		List<Audit4GPassFailEntity> audit4GPassFailEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GPassFailEntity> query = cb.createQuery(Audit4GPassFailEntity.class);
			Root<Audit4GPassFailEntity> root = query.from(Audit4GPassFailEntity.class);

			query.select(root);
			TypedQuery<Audit4GPassFailEntity> typedQuery = entityManager.createQuery(query);
			audit4GPassFailEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runId));
			TypedQuery<Audit4GPassFailEntity> queryResult = entityManager.createQuery(query);
			audit4GPassFailEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GSummaryEntityList() in  Audit4GSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GPassFailEntityList;

	}

	@Override
	public List<Audit4GPassFailEntity> createAudit4GPassFailEntityList(Set<Integer> set1) {

		List<Audit4GPassFailEntity> audit4GFsuPassFailSummaryEntityList = new ArrayList<>();
		try {

			
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit4GPassFailEntity.class);
			
			criteria.add(Restrictions.in("runTestEntity.id", set1));
     
            audit4GFsuPassFailSummaryEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			
            System.out.println(audit4GFsuPassFailSummaryEntityList.size());
		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuSuccessSummaryEntityList() in  Audit4GFsuSuccessSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuPassFailSummaryEntityList;	
	
	}

	@Override
	public boolean deleteaudit4GPassFailEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GPassFailEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit4GPassFailEntityById() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	public Audit4GPassFailEntity getaudit4GPassFailEntityById(int auditSummaryId) {
		return entityManager.find(Audit4GPassFailEntity.class, auditSummaryId);
	}

}
