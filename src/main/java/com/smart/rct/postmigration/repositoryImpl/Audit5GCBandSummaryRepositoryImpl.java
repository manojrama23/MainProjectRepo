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
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandSummaryRepository;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class Audit5GCBandSummaryRepositoryImpl implements Audit5GCBandSummaryRepository{
	
	final static Logger logger = LoggerFactory.getLogger(Audit5GCBandSummaryRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public boolean deleteaudit5GCBandSummaryEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GCBandSummaryEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GCBandSummaryEntityById() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public Audit5GCBandSummaryEntity getaudit5GCBandSummaryEntityById(int auditSummaryId) {
		return entityManager.find(Audit5GCBandSummaryEntity.class, auditSummaryId);
	}
	
	@Override
	public Audit5GCBandSummaryEntity createAudit5GCBandSummaryEntity(Audit5GCBandSummaryEntity audit5GCBandSummaryEntity) {
		Audit5GCBandSummaryEntity audit5GCBandSummaryEntityResult = null;
		try {
			audit5GCBandSummaryEntityResult = entityManager.merge(audit5GCBandSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GCBandSummaryEntity() in  Audit5GCBandSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandSummaryEntityResult;
	}
	
	@Override
	public Audit5GCBandPassFailEntity createAudit5GCBandPassFailEntity(Audit5GCBandPassFailEntity audit5gcBandPassFailEntity) {
		Audit5GCBandPassFailEntity audit5GCBandPassFailEntityResult = null;
		try {
			audit5GCBandPassFailEntityResult = entityManager.merge(audit5gcBandPassFailEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GCBandPassFailEntity() in  Audit5GCBandSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		
		return audit5GCBandPassFailEntityResult;
	}
	
	@Override
	public List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityList(int runTestId) {

		List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GCBandSummaryEntity> query = cb.createQuery(Audit5GCBandSummaryEntity.class);
			Root<Audit5GCBandSummaryEntity> root = query.from(Audit5GCBandSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit5GCBandSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit5GCBandSummaryEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runTestId));
			TypedQuery<Audit5GCBandSummaryEntity> queryResult = entityManager.createQuery(query);
			audit5GCBandSummaryEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GCBandSummaryEntityList() in  Audit5GCBandSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandSummaryEntityList;

	}

	@Override
	public List<Audit5GCBandPassFailEntity> createAudit5GCBandPassFailEachId(int runId) {
		List<Audit5GCBandPassFailEntity> audit5GCBandPassFailEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GCBandPassFailEntity> query = cb.createQuery(Audit5GCBandPassFailEntity.class);
			Root<Audit5GCBandPassFailEntity> root = query.from(Audit5GCBandPassFailEntity.class);

			query.select(root);
			TypedQuery<Audit5GCBandPassFailEntity> typedQuery = entityManager.createQuery(query);
			audit5GCBandPassFailEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runId));
			TypedQuery<Audit5GCBandPassFailEntity> queryResult = entityManager.createQuery(query);
			audit5GCBandPassFailEntityList = queryResult.getResultList();
		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuSuccessSummaryEntityList() in  Audit4GFsuSuccessSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
			
		} finally {
				entityManager.flush();
				entityManager.clear();
			}
		
		return audit5GCBandPassFailEntityList;
	}

	@Override
	public List<Audit5GCBandPassFailEntity> createAudit5GCBandPassFailEntityList(Set<Integer> set1) {
		
		List<Audit5GCBandPassFailEntity> audit5GCBandPassFailSummaryEntityList = new ArrayList<>();
		try {

			
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit5GCBandPassFailEntity.class);
			
		
			criteria.add(Restrictions.in("runTestEntity.id", set1));
     
			audit5GCBandPassFailSummaryEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			
            System.out.println(audit5GCBandPassFailSummaryEntityList.size());
		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuSuccessSummaryEntityList() in  Audit4GFsuSuccessSummaryRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GCBandPassFailSummaryEntityList;	
	
	
	
	}

	@Override
	public boolean deleteaudit5GCBandPassFailEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GCBandPassFailEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteaudit5GCBandSummaryEntityById() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public Audit5GCBandPassFailEntity getaudit5GCBandPassFailEntityById(int auditSummaryId) {
		return entityManager.find(Audit5GCBandPassFailEntity.class, auditSummaryId);
	}
	
}
