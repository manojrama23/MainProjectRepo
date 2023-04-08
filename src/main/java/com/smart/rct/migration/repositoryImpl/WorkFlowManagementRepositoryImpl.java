package com.smart.rct.migration.repositoryImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
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

import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.WorkFlowManagementModel;
import com.smart.rct.migration.repository.WorkFlowManagementRepository;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class WorkFlowManagementRepositoryImpl implements WorkFlowManagementRepository {
	final static Logger logger = LoggerFactory.getLogger(WorkFlowManagementRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public RunTestEntity getRunTestEntity(RunTestEntity runTestEntity) {
		RunTestEntity runTestEntityResult = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);

			query.select(root);
			// TypedQuery<RunTestEntity> typedQuery = entityManager.createQuery(query);
			// runTestEntity = typedQuery.getSingleResult();
			query.where(cb.equal(root.get("id"), runTestEntity.getId()));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntityResult = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntityResult;
	}

	@Override
	public WorkFlowManagementEntity getWorkFlowManagementEntity(Integer workFlowId) {
		WorkFlowManagementEntity workFlowManagementEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<WorkFlowManagementEntity> query = cb.createQuery(WorkFlowManagementEntity.class);
			Root<WorkFlowManagementEntity> root = query.from(WorkFlowManagementEntity.class);

			query.select(root);
			// TypedQuery<RunTestEntity> typedQuery = entityManager.createQuery(query);
			// runTestEntity = typedQuery.getSingleResult();
			query.where(cb.equal(root.get("id"), workFlowId));
			TypedQuery<WorkFlowManagementEntity> queryResult = entityManager.createQuery(query);
			workFlowManagementEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getWorkFlowManagementEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return workFlowManagementEntity;
	}

	@Override
	public WorkFlowManagementEntity createWorkFlowMangement(WorkFlowManagementEntity workFlowManagementEntity) {
		WorkFlowManagementEntity workFlowManagementEntityUpdate = null;
		System.out.println("calling mergeworkflowmanagement in repo"+workFlowManagementEntity.getEnbId());
		try {
			workFlowManagementEntityUpdate = entityManager.merge(workFlowManagementEntity);
		} catch (Exception e) {
			logger.error("Exception in  createWorkFlowMangement() in  WorkFlowManagementRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return workFlowManagementEntityUpdate;
	}

	@Override
	public Map<String, Object> getWorkFlowManagementDetails(WorkFlowManagementModel runTestModel, int page, int count,
			Integer programId) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		double result = 0;
		int paginationNumber = 0;
		List<RunTestEntity> runTestEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(WorkFlowManagementEntity.class);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runMigTestEntity.runTestResultEntity", FetchMode.LAZY);
			if (runTestModel != null) {
//				if (StringUtils.isNotEmpty(runTestModel.getLsmVersion())) {
//					Criterion lsmVersion = Restrictions.ilike("lsmVersion", runTestModel.getLsmVersion().trim(),
//							MatchMode.ANYWHERE);
//					conjunction.add(lsmVersion);
//				}

//				if (StringUtils.isNotEmpty(runTestModel.getTestName())) {
//					Criterion testName = Restrictions.ilike("testName", runTestModel.getTestName().trim(),
//							MatchMode.ANYWHERE);
//					conjunction.add(testName);
//				}
				if (StringUtils.isNotEmpty(runTestModel.getCiqName())) {
					Criterion ciqName = Restrictions.ilike("ciqName", runTestModel.getCiqName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", runTestModel.getNeName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
//				if (StringUtils.isNotEmpty(runTestModel.getLsmName())) {
//					Criterion lsmName = Restrictions.ilike("lsmName", runTestModel.getLsmName().trim(),
//							MatchMode.ANYWHERE);
//					conjunction.add(lsmName);
//				}
				
				if (StringUtils.isNotEmpty(runTestModel.getPreMigStatus())) {
					Criterion preMigStatus = Restrictions.ilike("preMigStatus", runTestModel.getPreMigStatus().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(preMigStatus);
				}
				
				if (StringUtils.isNotEmpty(runTestModel.getMigStatus())) {
					Criterion migStatus = Restrictions.ilike("MigStatus", runTestModel.getMigStatus().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(migStatus);
				}
				
				if (StringUtils.isNotEmpty(runTestModel.getNeGrowStatus())) {
					Criterion neGrowStatus = Restrictions.ilike("neGrowStatus", runTestModel.getNeGrowStatus().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(neGrowStatus);
				}
				
				if (StringUtils.isNotEmpty(runTestModel.getPostMigStatus())) {
					Criterion postMigStatus = Restrictions.ilike("PostMigStatus", runTestModel.getPostMigStatus().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(postMigStatus);
				}
				
				if (StringUtils.isNotEmpty(runTestModel.getSiteReportStatus())) {
					Criterion siteReportStatus = Restrictions.ilike("siteReportStatus", runTestModel.getSiteReportStatus().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(siteReportStatus);
				}

				if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())
						&& runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				} else if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
				} else if (runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchEndDate);
				}
			}

			conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));

			
			 if (StringUtils.isNotEmpty(runTestModel.getUserName())) {
			 conjunction.add(Restrictions.eq("userName", runTestModel.getUserName())); }
			 

			criteria.add(conjunction);
			logger.info(
					"WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails() criteria: " + criteria.toString());
			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(WorkFlowManagementEntity.class);
			criteriaCount.setFetchMode("runMigTestEntity", FetchMode.LAZY);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			logger.info("WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails() totCount: " + totCount
					+ ", paginationNumber: " + paginationNumber);
			objMap.put("list", runTestEntityList);
			objMap.put("paginationNumber", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public List<RunTestEntity> getRunTestListWfm(List<Integer> runtestIdList) {
		List<RunTestEntity> runTestEntityList = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			criteria.add(Restrictions.in("id", runtestIdList));
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runTestResultEntity", FetchMode.LAZY);
			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementRepositoryImpl.getRunTestListWfm(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return runTestEntityList;
	}

	/**
	 * This method will delete WFM Test by id
	 * 
	 * @param wfmRunTestId
	 * @return boolean
	 */
	@Override
	public boolean deleteWfmrunTest(int wfmRunTestId) {
		boolean status = false;
		try {
			entityManager.remove(getWfmRunTestId(wfmRunTestId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will return WorkFlowManagementEntity
	 * 
	 * @param wfmRunTestId
	 * @return WorkFlowManagementEntity
	 */
	public WorkFlowManagementEntity getWfmRunTestId(int wfmRunTestId) {
		return entityManager.find(WorkFlowManagementEntity.class, wfmRunTestId);
	}

	@Override
	public boolean getWFMRunTestEntity(int programId, String testname) {
		List<RunTestEntity> runTestEntity = null;
		boolean isTestNamePresent = true;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("testName"), testname));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntity = queryResult.getResultList();

			if (runTestEntity.isEmpty()) {
				isTestNamePresent = false;
			}

		} catch (Exception e) {
			logger.error(" getWFMRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return isTestNamePresent;
	}
	
	@Override
	public boolean getWfmEnbStatus(int programId,String neName,Object neId) {

		boolean inProgressStatus = true;
		List<WorkFlowManagementEntity> workFlowManagementEntity = null;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<WorkFlowManagementEntity> query = cb.createQuery(WorkFlowManagementEntity.class);
			Root<WorkFlowManagementEntity> root = query.from(WorkFlowManagementEntity.class);

			query.select(root);
//			query.where(
//					cb.and(cb.equal(root.get("neName"), neName), cb.equal(root.get("progressStatus"), "InProgress")));
			if(StringUtils.isNotEmpty(neName)) {
				query.where(
						cb.and(cb.equal(root.get("neName"), neName), cb.equal(root.get("status"), "InProgress")));
			}else {
				logger.error("neName empty1*****:"+neId);
				if(null!=neId&&StringUtils.isNotEmpty(neId.toString())) {
					logger.error("neName empty*****:"+neId);
				}
			}
			
			
			

			TypedQuery<WorkFlowManagementEntity> queryResult = entityManager.createQuery(query);
			workFlowManagementEntity = queryResult.getResultList();

			if (workFlowManagementEntity.isEmpty()) {
				inProgressStatus = false;
			}

		} catch (Exception e) {
			logger.error(" Exception RunTestRepositoryImpl in getRuntestEnbProgressStatus()  : "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return inProgressStatus;
	}
	
	@Override
	public WorkFlowManagementEntity getWFMEntity(int runtestId, String type) {
		WorkFlowManagementEntity workFlowManagementEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<WorkFlowManagementEntity> query = cb.createQuery(WorkFlowManagementEntity.class);
			Root<WorkFlowManagementEntity> root = query.from(WorkFlowManagementEntity.class);
			query.select(root);
			if("migration".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runMigTestEntity"), runtestId));
			} else if("audit".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runPostMigTestEntity"), runtestId));
			} else if("negrow".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runNEGrowEntity"), runtestId));
			} else if("ranatp".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runRanAtpTestEntity"), runtestId));
			}else if("preaudit".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runPreAuditTestEntity"), runtestId));
			} else if("nestatus".equalsIgnoreCase(type)) {
				query.where(cb.equal(root.get("runNEStatusTestEntity"), runtestId));
			}
			TypedQuery<WorkFlowManagementEntity> queryResult = entityManager.createQuery(query);
			workFlowManagementEntity = queryResult.getSingleResult();
		} catch (Exception e) {
			logger.error(" Exception WorkFlowManagementRepositoryImpl in getWFMEntity()  : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return workFlowManagementEntity;
	}
	
	@Override
	public Map<String, Object> getInProgressWorkFlowManagementDetails(WorkFlowManagementModel runTestModel,	Integer programId, List<String> userNameList) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<RunTestEntity> runTestEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(WorkFlowManagementEntity.class);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runMigTestEntity.runTestResultEntity", FetchMode.LAZY);
			/*Criterion postMigStatus = Restrictions.ilike("PostMigStatus", "InProgress",
					MatchMode.ANYWHERE);
			
			conjunction.add(postMigStatus);*/
			//conjunction.add(preAuditStatus);
			if (runTestModel != null) {
				if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())
						&& runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				}
			}

			conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));

			Disjunction or =Restrictions.disjunction();
			for(String userName: userNameList) {
				or.add(Restrictions.eq("userName", userName));
			}
			conjunction.add(or);
			Disjunction or2 =Restrictions.disjunction();
			 or2.add(Restrictions.ilike("PostMigStatus", "InProgress"));
			 or2.add(Restrictions.ilike("PreAuditStatus", "InProgress"));
			 
			conjunction.add(or2);
			criteria.add(conjunction);
			logger.info(
					"WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails() criteria: " + criteria.toString());
			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			objMap.put("list", runTestEntityList);

		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
	
	@Override
	public Map<String, Object> getDuoExecErrorSiteList(WorkFlowManagementModel runTestModel,Integer programId, 
			List<String> userNameList, String useCaseName) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<Audit5GCBandSummaryEntity> summaryList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Audit5GCBandSummaryEntity.class);
			criteria.createAlias("runTestEntity", "runTestEntity");
			criteria.createAlias("audit5GCBandRulesEntity", "audit5GCBandRulesEntity");
			criteria.addOrder(Order.desc("runTestEntity.creationDate"));
			//criteria.setFetchMode("runMigTestEntity.runTestResultEntity", FetchMode.LAZY);
			Criterion postMigStatus = Restrictions.ilike("runTestEntity.progressStatus", "Completed",
					MatchMode.ANYWHERE);
			conjunction.add(postMigStatus);
			if (runTestModel != null) {
				if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())
						&& runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("runTestEntity.creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("runTestEntity.creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				}
			}
			conjunction.add(Restrictions.eq("audit5GCBandRulesEntity.id", 102));
			conjunction.add(Restrictions.eq("runTestEntity.customerDetailsEntity.id", programId));
			conjunction.add(Restrictions.like("runTestEntity.useCase", useCaseName, MatchMode.ANYWHERE));

			Disjunction or =Restrictions.disjunction();
			for(String userName: userNameList) {
				or.add(Restrictions.eq("runTestEntity.userName", userName));
			}
			conjunction.add(or);
			 

			criteria.add(conjunction);
			logger.info(
					"WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails() criteria: " + criteria.toString());
			summaryList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			objMap.put("list", summaryList);

		} catch (Exception e) {
			logger.error("Exception in WorkFlowManagementRepositoryImpl.getWorkFlowManagementDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
}
