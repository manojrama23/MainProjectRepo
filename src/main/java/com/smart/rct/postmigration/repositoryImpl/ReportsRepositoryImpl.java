package com.smart.rct.postmigration.repositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.MarketModelDetails;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.migration.repository.WorkFlowManagementRepository;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.repository.ReportsRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class ReportsRepositoryImpl implements ReportsRepository {
	final static Logger logger = LoggerFactory.getLogger(ReportsRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public ReportsEntity createReports(ReportsEntity reportsEntity) {
		ReportsEntity reportsEntityUpdate = null;
		try {
			reportsEntityUpdate = entityManager.merge(reportsEntity);
			
		} catch (Exception e) {
			logger.error("Exception in  createReports() in reportsRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return reportsEntityUpdate;
	}
	
	@Override
	public ReportsEntity getEntityData(String programName,String enbId) {
		ReportsEntity runTestEntity = null;
		List <ReportsEntity> runTestEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<ReportsEntity> query = cb.createQuery(ReportsEntity.class);
			Root<ReportsEntity> root = query.from(ReportsEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("programeName"), programName), cb.equal(root.get("enbId"), enbId));
			TypedQuery<ReportsEntity> queryResult = entityManager.createQuery(query);
			//runTestEntity = queryResult.getSingleResult();
			logger.error("Into getEntityData");
			runTestEntityList = queryResult.getResultList();
			logger.error("getResultList");
			if(runTestEntityList.size() > 0) {
				runTestEntity = runTestEntityList.get(0);
			}
		} catch (Exception e) {
			logger.error(" getEntityData() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}
	
	@Override
	public Map<String, Object> getReportsDetails(Integer customerId, int page, int count, String programName,ReportsModel reportsModel,String type) {

		List<ReportsEntity> runTestEntity = null;
		List<ReportsEntity> runTestEntityy = null;
		Map<String, Object> objMap = new HashMap<String, Object>();
		
		double result = 0;
		int pagecount = 0;

		try {

			if (reportsModel != null) {
				Criteria criteria = entityManager.unwrap(Session.class).createCriteria(ReportsEntity.class);
				Conjunction conjunction = Restrictions.conjunction();
				if (StringUtils.isNotEmpty(reportsModel.getMarket())) {
					conjunction.add(Restrictions.like("market", reportsModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(reportsModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", reportsModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(reportsModel.getNeName())) {
					conjunction.add(Restrictions.like("neName", reportsModel.getNeName(), MatchMode.ANYWHERE));
				}
				/*
				 * if (StringUtils.isNotEmpty(reportsModel.getStatus())) {
				 * conjunction.add(Restrictions.like("status", reportsModel.getStatus(),
				 * MatchMode.ANYWHERE)); }
				 */
				if(programName !=null && !programName.equals("")) {
					conjunction.add(Restrictions.like("programeName",programName , MatchMode.ANYWHERE));
				}
				
				
				if (reportsModel.getFromDate() != null
						&& !"".equals(reportsModel.getFromDate())
						&& reportsModel.getToDate() != null
						&& !"".equals(reportsModel.getToDate())) {
					
					Criterion eventstartDate = Restrictions.ge("startDate", DateUtil.stringToDate(reportsModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("startDate", DateUtil.stringToDateEndTime(reportsModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}else if (reportsModel.getFromDate() != null && !"".equals(reportsModel.getFromDate())) {
					
					
					Criterion eventstartDate =Restrictions.ge("startDate", DateUtil.stringToDate(reportsModel.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					
				}else if (reportsModel.getToDate() != null && !"".equals(reportsModel.getToDate())) {
					
					Criterion forecastEndDate = Restrictions.le("startDate", DateUtil.stringToDateEndTime(reportsModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(forecastEndDate);
				}
			
				if(type.equals("download")) {
					criteria.add(conjunction);
					runTestEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();	
				}
				else {
				criteria.add(conjunction);
				criteria.setFirstResult((page - 1) * count);
				criteria.setMaxResults(count);
				criteria.addOrder(Order.desc("startDate"));
				runTestEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();	
				
				org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
						.createCriteria(ReportsEntity.class);
				criteriaCount.add(conjunction);
				criteriaCount.setProjection(Projections.rowCount());
				Long totCount = (Long) criteriaCount.uniqueResult();
				double size = totCount;
				result = Math.ceil(size / count);
				pagecount = (int) result;
				}
			}
			else {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<ReportsEntity> query = cb.createQuery(ReportsEntity.class);
				Root<ReportsEntity> root = query.from(ReportsEntity.class);
				query.select(root);
				if(programName!=null && !programName.equals("") ) {
					query.where(cb.equal(root.get("customerId"), customerId), cb.equal(root.get("programeName"), programName));
				}else {
				query.where(cb.equal(root.get("customerId"), customerId));
				}
				if(type.equals("download")) {
					TypedQuery<ReportsEntity> queryResult = entityManager.createQuery(query);
					runTestEntity = queryResult.getResultList();
				}
				else {
				TypedQuery<ReportsEntity> queryResult = entityManager.createQuery(query);
				queryResult.setFirstResult((page - 1) * count);
				queryResult.setMaxResults(count);
				runTestEntity = queryResult.getResultList();
				
				TypedQuery<ReportsEntity> queryResultt = entityManager.createQuery(query);
				query.where(cb.equal(root.get("customerId"), customerId));
				runTestEntityy = queryResultt.getResultList();
				int totCount = runTestEntityy.size();
				double size = totCount;
				result = Math.ceil(size / count);
				pagecount = (int) result;
				}
				
			}
			objMap.put("paginationcount", pagecount);
			objMap.put("runTestEntity", runTestEntity);


		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap ;
		
	}
	
	@Override
	public CustomerDetailsEntity getCustomerDetailsEntityById(int programId) {
		return entityManager.find(CustomerDetailsEntity.class, programId);
	}
}
