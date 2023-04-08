package com.smart.rct.postmigration.repositoryImpl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.repository.Audit4GFsuSummaryRepository;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

import io.micrometer.shaded.org.pcollections.Empty;

@Transactional
@Repository
public class Audit4GFsuSummaryRepositoryImpl implements Audit4GFsuSummaryRepository {

	final static Logger logger = LoggerFactory.getLogger(Audit4GFsuSummaryRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public boolean deleteaudit4GFsuSummaryEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GFsuSummaryEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteaudit4GFsuSummaryEntityById() in  RunTestRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public Audit4GFsuSummaryEntity getaudit4GFsuSummaryEntityById(int auditSummaryId) {
		return entityManager.find(Audit4GFsuSummaryEntity.class, auditSummaryId);
	}

	@Override
	public Audit4GFsuSummaryEntity createAudit4GFsuSummaryEntity(Audit4GFsuSummaryEntity audit4GFsuSummaryEntity) {
		Audit4GFsuSummaryEntity audit4GFsuSummaryEntityResult = null;
		try {
			audit4GFsuSummaryEntityResult = entityManager.merge(audit4GFsuSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GFsuSummaryEntity() in  Audit4GFsuSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuSummaryEntityResult;
	}

	@Override
	public Audit4GFsuPassFailSummaryEntity createAudit4GFsuPassFailEntity(
			Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntity) {
		Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntityResult = null;
		try {
			audit4GFsuPassFailSummaryEntityResult = entityManager.merge(audit4GFsuPassFailSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAudit4GFsuSummaryEntity() in  Audit4GFsuSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuPassFailSummaryEntityResult;
	}

	@Override
	public List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityList(int runTestId) {

		List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GFsuSummaryEntity> query = cb.createQuery(Audit4GFsuSummaryEntity.class);
			Root<Audit4GFsuSummaryEntity> root = query.from(Audit4GFsuSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit4GFsuSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit4GFsuSummaryEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runTestId));
			TypedQuery<Audit4GFsuSummaryEntity> queryResult = entityManager.createQuery(query);
			audit4GFsuSummaryEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAudit4GFsuSummaryEntityList() in  Audit4GFsuSummaryRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuSummaryEntityList;

	}

	@Override
	public List<Audit4GFsuPassFailSummaryEntity> createAudit4GFsuPassFailEachId(int runId) {

		List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = new ArrayList<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit4GFsuPassFailSummaryEntity> query = cb
					.createQuery(Audit4GFsuPassFailSummaryEntity.class);
			Root<Audit4GFsuPassFailSummaryEntity> root = query.from(Audit4GFsuPassFailSummaryEntity.class);

			query.select(root);
			TypedQuery<Audit4GFsuPassFailSummaryEntity> typedQuery = entityManager.createQuery(query);
			audit4GFsuPassFailSummaryEntityList = typedQuery.getResultList();
			query.where(cb.equal(root.get("runTestEntity"), runId));
			TypedQuery<Audit4GFsuPassFailSummaryEntity> queryResult = entityManager.createQuery(query);
			audit4GFsuPassFailSummaryEntityList = queryResult.getResultList();
		} catch (Exception e) {
			logger.error(
					"Exception in  getAudit4GFsuSuccessSummaryEntityList() in  Audit4GFsuSuccessSummaryRepositoryImpl:"
							+ ExceptionUtils.getFullStackTrace(e));

		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return audit4GFsuPassFailSummaryEntityList;
	}

	@Override
	public List<Audit4GFsuPassFailSummaryEntity> createAudit4GFsuPassFailEntityList(Set<Integer> set1) {

		List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = new ArrayList<>();
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(Audit4GFsuPassFailSummaryEntity.class);
			
			
			criteria.add(Restrictions.in("runTestEntity.id",set1));

			audit4GFsuPassFailSummaryEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			System.out.println(audit4GFsuPassFailSummaryEntityList.size());

			
		} catch (Exception e) {
			logger.error(
					"Exception in  getAudit4GFsuSuccessSummaryEntityList() in  Audit4GFsuSuccessSummaryRepositoryImpl:"
							+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return audit4GFsuPassFailSummaryEntityList;

	}

	@Override
	public boolean deleteaudit4GFsuPassFailEntityById(int auditSummaryId) {
		boolean status = false;
		try {
			entityManager.remove(getaudit4GFsuPassFailEntityById(auditSummaryId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteaudit4GFsuPassFailEntityById() in  RunTestRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public Audit4GFsuPassFailSummaryEntity getaudit4GFsuPassFailEntityById(int auditSummaryId) {
		return entityManager.find(Audit4GFsuPassFailSummaryEntity.class, auditSummaryId);
	}

}
