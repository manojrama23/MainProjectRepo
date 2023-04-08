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

import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.repository.ActiveUsersTrackingRepository;
import com.smart.rct.common.repository.AuditTrailRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Repository
@Transactional
public class ActiveUsersTrackingRepositoryImpl implements ActiveUsersTrackingRepository {

	final static Logger logger = LoggerFactory.getLogger(ActiveUsersTrackingRepositoryImpl.class);

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
	public boolean savedetail(ActiveUsersTracking activeUsersTracking) {
		boolean status = false;
		try {
			entityManager.persist(activeUsersTracking);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in ActiveUsersTrackingRepositoryImpl.savedetail(): " + ExceptionUtils.getFullStackTrace(e));
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
	public Map<String, Object> getActiveUsers(int page, int count) {
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

	
}
