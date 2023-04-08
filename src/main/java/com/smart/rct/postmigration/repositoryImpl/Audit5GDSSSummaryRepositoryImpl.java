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
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSSummaryRepository;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class Audit5GDSSSummaryRepositoryImpl implements Audit5GDSSSummaryRepository {

	final static Logger logger = LoggerFactory.getLogger(Audit5GDSSSummaryRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public boolean deleteaudit5GDSSSummaryEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GDSSSummaryEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteaudit5GDSSSummaryEntityById() in  RunTestRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public Audit5GDSSSummaryEntity getaudit5GDSSSummaryEntityById(int auditSummaryId) {
		return entityManager.find(Audit5GDSSSummaryEntity.class, auditSummaryId);
	}

	@Override
	public Audit5GDSSSummaryEntity createAudit5GDSSSummaryEntity(Audit5GDSSSummaryEntity audit5GDSSSummaryEntity) {
		Audit5GDSSSummaryEntity audit5GDSSSummaryEntityResult = null;
		try {
			audit5GDSSSummaryEntityResult = entityManager.merge(audit5GDSSSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GDSSSummaryEntity() in  Audit5GDSSSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSSummaryEntityResult;
	}

	@Override
	public List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityList(int runTestId) {

		List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GDSSSummaryEntity> query = cb.createQuery(Audit5GDSSSummaryEntity.class);
			Root<Audit5GDSSSummaryEntity> root = query.from(Audit5GDSSSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit5GDSSSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit5GDSSSummaryEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runTestId));
			TypedQuery<Audit5GDSSSummaryEntity> queryResult = entityManager.createQuery(query);
			audit5GDSSSummaryEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GDSSSummaryEntityList() in  Audit5GDSSSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSSummaryEntityList;

	}

	@Override
	public List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailEntityList(int runId) {

		List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GDSSPassFailSummaryEntity> query = cb
					.createQuery(Audit5GDSSPassFailSummaryEntity.class);
			Root<Audit5GDSSPassFailSummaryEntity> root = query.from(Audit5GDSSPassFailSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit5GDSSPassFailSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit5GDSSPassFailEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runId));
			TypedQuery<Audit5GDSSPassFailSummaryEntity> queryResult = entityManager.createQuery(query);
			audit5GDSSPassFailEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit5GDSSSummaryEntityList() in  Audit5GDSSSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));

		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSPassFailEntityList;
	}

	@Override
	public Audit5GDSSPassFailSummaryEntity createAudit5GDSSPassFailEntity(
			Audit5GDSSPassFailSummaryEntity audit5gdssPassFailSummaryEntity) {
		Audit5GDSSPassFailSummaryEntity audit5GDSSPassFailEntityResult = null;
		try {
			audit5GDSSPassFailEntityResult = entityManager.merge(audit5gdssPassFailSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit5GDSSPassFailEntity() in  Audit5GDSSPassFailRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDSSPassFailEntityResult;
	}

	@Override
	public List<Audit5GDSSPassFailSummaryEntity> createAudit5GDSSPassFailEntityList(Set<Integer> set1) {

		List<Audit5GDSSPassFailSummaryEntity> audit5GDssPassFailSummaryEntityList = new ArrayList<>();
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(Audit5GDSSPassFailSummaryEntity.class);

			criteria.add(Restrictions.in("runTestEntity.id", set1));

			audit5GDssPassFailSummaryEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			System.out.println(audit5GDssPassFailSummaryEntityList.size());
		} catch (Exception e) {
			logger.error(
					"Exception in  getaudit5GDssPassFailSummaryEntityList() in  Asudit5GDssPassFailSummaryEntityList:"
							+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit5GDssPassFailSummaryEntityList;

		
	}

	@Override
	public boolean deleteaudit5GDSSPassFailEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit5GDSSPassFailEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteaudit5GDSSSummaryEntityById() in  RunTestRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public Audit5GDSSPassFailSummaryEntity getaudit5GDSSPassFailEntityById(int auditSummaryId) {
		return entityManager.find(Audit5GDSSPassFailSummaryEntity.class, auditSummaryId);
	}
}
