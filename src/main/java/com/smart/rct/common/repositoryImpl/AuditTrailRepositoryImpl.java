package com.smart.rct.common.repositoryImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.repository.AuditTrailRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Repository
@Transactional
public class AuditTrailRepositoryImpl implements AuditTrailRepository {

	final static Logger logger = LoggerFactory.getLogger(AuditTrailRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	CommonUtil commonUtil;

	/**
	 * This method will saveAuditTrailDetails
	 * 
	 * @param auditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean savedetail(AuditTrailEntity auditTrailEntity) {
		boolean status = false;
		try {
			entityManager.persist(auditTrailEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in AuditTrailRepositoryImpl.savedetail(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return getAuditDetails
	 * 
	 * @param page
	 * @param count
	 * @return auditTrailEntity
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getAuditDetails(int page, int count) {
		logger.info("AuditTrailRepositoryImpl.getAuditDetails() page: " + page + ", count: " + count);
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<AuditTrailEntity> auditTrailEntity = null;
		double result = 0;
		int pagecount = 0;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(AuditTrailEntity.class);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("actionPerformedDate"));
			auditTrailEntity = criteria.list();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(AuditTrailEntity.class);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("pageCount", pagecount);
			objMap.put("auditList", auditTrailEntity);
		} catch (Exception e) {
			logger.error(
					"Exception in AuditTrailRepositoryImpl.getAuditDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This method will return getAuditDetails based on filters
	 * 
	 * @param searchStatus
	 * @param auditTrailEntity
	 * @return filterList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAuditFilters(String searchStatus, AuditTrailEntity auditTrailEntity) {
		logger.info("AuditTrailRepositoryImpl.getAuditFilters() searchStatus: " + searchStatus);
		List<String> filterList = null;
		try {
			String columnName = "";
			if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_EVENT_NAME)) {
				columnName = "eventName";
			} else if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_EVENT_SUB_NAME)) {
				columnName = "eventSubName";
			} else if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_ACTION)) {
				columnName = "actionPerformed";
			} else if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_USER_NAME)) {
				columnName = "userName";
			}
			String hql = "SELECT Distinct(" + columnName + ") FROM AuditTrailEntity as filterList";
			if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_EVENT_SUB_NAME)) {
				hql += " where eventName='" + auditTrailEntity.getEventName() + "'";
			} else if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_ACTION)) {
				hql += " where eventName='" + auditTrailEntity.getEventName() + "' AND eventSubName='"
						+ auditTrailEntity.getEventSubName() + "'";
			} else if (searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_USER_NAME)) {
				hql += " where eventName='" + auditTrailEntity.getEventName() + "' AND eventSubName='"
						+ auditTrailEntity.getEventSubName() + "' AND actionPerformed='"
						+ auditTrailEntity.getActionPerformed() + "'";
			}
			logger.info("AuditTrailRepositoryImpl.getAuditFilters() hql: " + hql);
			filterList = (List<String>) entityManager.createQuery(hql).getResultList();
		} catch (Exception e) {
			logger.error(
					"Exception in AuditTrailRepositoryImpl.getAuditFilters(): " + ExceptionUtils.getFullStackTrace(e));
		}

		return filterList;
	}

	/**
	 * This method will return getAuditDetails based on Search
	 * 
	 * @param page
	 * @param count
	 * @param auditTrailModel
	 * @param timeZone
	 * @return objMap
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getAuditDetailsOnSearch(AuditTrailModel auditTrailModel, int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<AuditTrailEntity> auditTrailEntity = null;
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(AuditTrailEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (auditTrailModel != null) {
				if (StringUtils.isNotEmpty(auditTrailModel.getEventName())) {
					Criterion eventName = Restrictions.eq("eventName", auditTrailModel.getEventName());
					conjunction.add(eventName);
				}
				if (StringUtils.isNotEmpty(auditTrailModel.getEventSubName())) {
					Criterion eventSubName = Restrictions.eq("eventSubName", auditTrailModel.getEventSubName());
					conjunction.add(eventSubName);
				}
				if (StringUtils.isNotEmpty(auditTrailModel.getAction())) {
					Criterion action = Restrictions.eq("actionPerformed", auditTrailModel.getAction());
					conjunction.add(action);
				}
				if (StringUtils.isNotEmpty(auditTrailModel.getUserName())) {
					Criterion userName = Restrictions.eq("userName", auditTrailModel.getUserName());
					conjunction.add(userName);
				}
				if (auditTrailModel.getFromDate() != null && !"".equals(auditTrailModel.getFromDate()) && auditTrailModel.getToDate() != null && !"".equals(auditTrailModel.getToDate())) {
					Criterion eventstartDate = Restrictions.ge("actionPerformedDate", DateUtil.stringToDate(auditTrailModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("actionPerformedDate", DateUtil.stringToDateEndTime(auditTrailModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}else if (auditTrailModel.getFromDate() != null && !"".equals(auditTrailModel.getFromDate())) {
					Criterion eventstartDate = Restrictions.ge("actionPerformedDate", DateUtil.stringToDate(auditTrailModel.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
				}else if (auditTrailModel.getToDate() != null && !"".equals(auditTrailModel.getToDate())) {
					Criterion eventEndDate = Restrictions.le("actionPerformedDate", DateUtil.stringToDateEndTime(auditTrailModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventEndDate);
				}
				criteria.add(conjunction);
				criteria.setFirstResult((page - 1) * count);
				criteria.setMaxResults(count);
				criteria.addOrder(Order.desc("actionPerformedDate"));
				auditTrailEntity = criteria.list();
				Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(AuditTrailEntity.class);
				criteriaCount.add(conjunction);
				criteriaCount.setProjection(Projections.rowCount());
				Long totCount = (Long) criteriaCount.uniqueResult();
				double size = totCount;
				result = Math.ceil(size / count);
				pagecount = (int) result;
			}
			objMap.put("paginationcount", pagecount);
			objMap.put("list", auditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception in AuditTrailRepositoryImpl.getAuditDetailsOnSearch(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

}
