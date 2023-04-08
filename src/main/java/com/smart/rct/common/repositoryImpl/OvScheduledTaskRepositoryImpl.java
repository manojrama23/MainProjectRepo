package com.smart.rct.common.repositoryImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.OvScheduledModel;
import com.smart.rct.common.repository.OvScheduledTaskRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.util.DateUtil;

@Repository
@Transactional
public class OvScheduledTaskRepositoryImpl implements OvScheduledTaskRepository {

	final static Logger logger = LoggerFactory.getLogger(OvScheduledTaskRepositoryImpl.class);
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<OvScheduledEntity> getOvScheduledDetails(String date, List<String> forceFetchIds,
			CustomerDetailsEntity programmeEntity) {
		List<OvScheduledEntity> ovList = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);

			if (!ObjectUtils.isEmpty(programmeEntity)) {
				criteria.createAlias("customerDetailsEntity", "customerDetailsEntity");
				criteria.add(Restrictions.eq("customerDetailsEntity.id", programmeEntity.getId()));
			}

			if (!ObjectUtils.isEmpty(forceFetchIds)) {
				criteria.add(Restrictions.in("neId", forceFetchIds));
			}

			Criterion c1 = Restrictions.eq("premigrationScheduledTime", date);
			Criterion c2 = Restrictions.eq("migrationScheduledTime", date);
			Criterion c3 = Restrictions.eq("neGrowScheduledTime", date);
			Criterion c4 = Restrictions.eq("postmigrationAuditScheduledTime", date);
			Criterion c5 = Restrictions.eq("ranAtpScheduledTime", date);
			// Criterion c6 = Restrictions.eq("envFileExportScheduledTime", date);

			Criterion c7 = Restrictions.eq("premigrationReScheduledTime", date);
			Criterion c8 = Restrictions.eq("migrationReScheduledTime", date);
			Criterion c9 = Restrictions.eq("neGrowReScheduledTime", date);
			Criterion c10 = Restrictions.eq("postmigrationAuditScheduledTime", date);
			Criterion c11 = Restrictions.eq("postmigrationAuditReScheduledTime", date);
			// Criterion c12= Restrictions.eq("envFileExportReScheduledTime", date);
			Criterion condition = Restrictions.or(c1, c2, c3, c4, c5, c7, c8, c9, c10, c11);
			criteria.add(condition);
			ovList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ovList;
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
	public Map<String, Object> getOvStatusScheduledDetails(int page, int count, String programName) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<OvScheduledEntity> auditTrailEntity = null;
		double result = 0;
		int pagecount = 0;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			if (programName != null && !programName.equals("")) {
				criteria.createAlias("customerDetailsEntity", "customerDetailsEntity");
				criteria.add(Restrictions.eq("customerDetailsEntity.programName", programName));
			}
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("fetchDate"));
			auditTrailEntity = criteria.list();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("pageCount", pagecount);
			objMap.put("ovStatusList", auditTrailEntity);
		} catch (Exception e) {
			logger.error(
					"Exception in AuditTrailRepositoryImpl.getAuditDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public OvScheduledEntity getOvScheduledServiceDetails(String trackerId, String enbId) {

		OvScheduledEntity ScheduledEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<OvScheduledEntity> query = cb.createQuery(OvScheduledEntity.class);
			Root<OvScheduledEntity> root = query.from(OvScheduledEntity.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("neId"), enbId), cb.equal(root.get("trackerId"), trackerId)));
			// query.where();
			TypedQuery<OvScheduledEntity> queryResult = entityManager.createQuery(query);
			ScheduledEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getWorkFlowManagementEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ScheduledEntity;
	}

	@Override
	public List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId,String programName) {

		List<OvScheduledEntity> ScheduledEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<OvScheduledEntity> query = cb.createQuery(OvScheduledEntity.class);
			Root<OvScheduledEntity> root = query.from(OvScheduledEntity.class);
			Join<OvScheduledEntity,CustomerDetailsEntity> join = root.join("customerDetailsEntity");
			//root.alias("customerDetailsEntity");
			query.select(root);
			query.where(cb.and(cb.equal(root.get("trackerId"), trackerId), cb.equal(join.get("programName"), programName)));
//			query.where(cb.equal(root.get("trackerId"), trackerId));
//			query.where(cb.equal(join.get("programName"), programName));
			// query.where();
			TypedQuery<OvScheduledEntity> queryResult = entityManager.createQuery(query);
			ScheduledEntity = queryResult.getResultList();

		} catch (Exception e) {
			logger.error(" getWorkFlowManagementEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ScheduledEntity;
	}

	@Override
	public OvScheduledEntity createOvScheduleDetails(OvScheduledEntity ScheduledEntity) {
		OvScheduledEntity ScheduledEntityUpdate = null;
		try {
			ScheduledEntityUpdate = entityManager.merge(ScheduledEntity);
		} catch (Exception e) {
			logger.error("Exception in  createWorkFlowMangement() in  WorkFlowManagementRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ScheduledEntityUpdate;
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
	public Map<String, Object> getOvStatusScheduledSearchDetails(OvScheduledModel ovScheduledModel, int page, int count,
			String programName) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<OvScheduledEntity> auditTrailEntity = null;
		double result = 0;
		int pagecount = 0;
		page = 1;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			if (programName != null && !programName.equals("")) {
				criteria.createAlias("customerDetailsEntity", "customerDetailsEntity");
				criteria.add(Restrictions.eq("customerDetailsEntity.programName", programName));
			}

			Conjunction conjunction = Restrictions.conjunction();
			if (ovScheduledModel != null) {
				if (StringUtils.isNotEmpty(ovScheduledModel.getNeId())) {
					Criterion neId = Restrictions.eq("neId", ovScheduledModel.getNeId());
					conjunction.add(neId);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getCiqName())) {
					Criterion ciqName = Restrictions.eq("ciqName", ovScheduledModel.getCiqName());
					conjunction.add(ciqName);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getTrackerId())) {
					Criterion trackerId = Restrictions.eq("trackerId", ovScheduledModel.getTrackerId());
					conjunction.add(trackerId);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getWorkPlanID())) {
					Criterion workPlanID = Restrictions.eq("workPlanID", ovScheduledModel.getWorkPlanID());
					conjunction.add(workPlanID);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getPreMigStatus())) {
					Criterion preMigStatus = Restrictions.eq("preMigStatus", ovScheduledModel.getPreMigStatus());
					conjunction.add(preMigStatus);
				}

				if (StringUtils.isNotEmpty(ovScheduledModel.getEnvExportStatus())) {
					Criterion envExportStatus = Restrictions.eq("envExportStatus",
							ovScheduledModel.getEnvExportStatus());
					conjunction.add(envExportStatus);
				}

				if (StringUtils.isNotEmpty(ovScheduledModel.getPreMigGrowStatus())) {
					Criterion preMigGrowStatus = Restrictions.eq("preMigGrowStatus",
							ovScheduledModel.getPreMigGrowStatus());
					conjunction.add(preMigGrowStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getSiteName())) {
					Criterion preMigGrowStatus = Restrictions.eq("siteName", ovScheduledModel.getSiteName());
					conjunction.add(preMigGrowStatus);
				}
				//
				if (StringUtils.isNotEmpty(ovScheduledModel.getCiqGenerationDate())) {
					Criterion envExportStatus = Restrictions.eq("ciqGenerationDate",
							ovScheduledModel.getCiqGenerationDate());
					conjunction.add(envExportStatus);
				}
//				if (StringUtils.isNotEmpty(ovScheduledModel.getFetchDate())) {
//					DateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS );
//					Date date= dateFormat.parse(ovScheduledModel.getFetchDate());
//					Criterion dateStatus = Restrictions.eq("fetchDate",date);
//					conjunction.add(dateStatus);
//			}
				if (StringUtils.isNotEmpty(ovScheduledModel.getFetchDate())) {
					
					// added c1, c2 to fetch date withh and w/o time stamp
					Criterion c1 = Restrictions.like("fetchDate",						
							ovScheduledModel.getFetchDate(), MatchMode.ANYWHERE);
					Criterion c2 = Restrictions.eq("fetchDate",						
							ovScheduledModel.getFetchDate());
					Criterion eventstartDate = Restrictions.or(c1,c2);
					conjunction.add(eventstartDate);
					System.out.println(eventstartDate.toString());
			}
				if (StringUtils.isNotEmpty(ovScheduledModel.getFetchRemarks())) {
					Criterion envExportStatus = Restrictions.eq("fetchRemarks",
							ovScheduledModel.getFetchRemarks());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getEnvStatus())) {
					Criterion envExportStatus = Restrictions.eq("envStatus",
							ovScheduledModel.getEnvStatus());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getPreMigGrowGenerationDate())) {
					Criterion envExportStatus = Restrictions.eq("preMigGrowGenerationDate",
							ovScheduledModel.getPreMigGrowGenerationDate());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getEnvGenerationDate())) {
					Criterion envExportStatus = Restrictions.eq("envGenerationDate",
							ovScheduledModel.getEnvGenerationDate());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getEnvFileName())) {
					Criterion envExportStatus = Restrictions.eq("envFileName",
							ovScheduledModel.getEnvFileName());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getNeGrowStatus())) {
					Criterion envExportStatus = Restrictions.eq("neGrowStatus",
							ovScheduledModel.getNeGrowStatus());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getMigStatus())) {
					Criterion envExportStatus = Restrictions.eq("MigStatus",
							ovScheduledModel.getMigStatus());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getMigrationStartDate())) {
					Criterion envExportStatus = Restrictions.eq("migrationStartDate",
							ovScheduledModel.getMigrationStartDate());
					conjunction.add(envExportStatus);
				}
				if (StringUtils.isNotEmpty(ovScheduledModel.getMigrationCompleteTime())) {
					Criterion envExportStatus = Restrictions.eq("migrationCompleteTime",
							ovScheduledModel.getMigrationCompleteTime());
					conjunction.add(envExportStatus);
				}//dummy ip
				if(StringUtils.isNotEmpty(ovScheduledModel.getIntegrationType())) {
					Criterion integrationType = Restrictions.like("integrationType", ovScheduledModel.getIntegrationType(), MatchMode.ANYWHERE).ignoreCase();
					conjunction.add(integrationType);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("fetchDate"));
			auditTrailEntity = criteria.list();
			
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("pageCount", pagecount);
			objMap.put("ovStatusList", auditTrailEntity);
		} catch (Exception e) {
			logger.error(
					"Exception in AuditTrailRepositoryImpl.getAuditDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
		
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public OvScheduledEntity getOvDetails(Integer workFlowId) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		OvScheduledEntity ovScheduledEntity = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);

			criteria.createAlias("workFlowManagementEntity", "workFlowManagementEntity");
			criteria.add(Restrictions.eq("workFlowManagementEntity.id", workFlowId));
			ovScheduledEntity = (OvScheduledEntity) criteria.uniqueResult();
		} catch (Exception e) {
			logger.error(
					"Exception in AuditTrailRepositoryImpl.getAuditDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ovScheduledEntity;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public OvScheduledEntity get4gOvDetails(String programName) {
		OvScheduledEntity ovDetailsEntity = null;
		{
			try {
				Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
				criteria.add(Restrictions.eq("programName", programName));
				List<OvScheduledEntity> ovList = criteria
						.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
				if (!ObjectUtils.isEmpty(ovList)) {
					ovDetailsEntity = ovList.get(0);
				}
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return ovDetailsEntity;
		}
	}
	@Override
	public OvScheduledEntity getOvDetail(Integer ovId) {
		OvScheduledEntity ovScheduledEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<OvScheduledEntity> query = cb.createQuery(OvScheduledEntity.class);
			Root<OvScheduledEntity> root = query.from(OvScheduledEntity.class);

			query.select(root);
			// TypedQuery<RunTestEntity> typedQuery = entityManager.createQuery(query);
			// runTestEntity = typedQuery.getSingleResult();
			query.where(cb.equal(root.get("id"), ovId));
			TypedQuery<OvScheduledEntity> queryResult = entityManager.createQuery(query);
			ovScheduledEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getWorkFlowManagementEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ovScheduledEntity;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<OvScheduledEntity> getForceFecthOvDetails(List<String> neids) {
		List<OvScheduledEntity> ovScheduledEntityList = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			criteria.add(Restrictions.in("neId", neids));
			ovScheduledEntityList = criteria.list();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ovScheduledEntityList;
	}

	@Override
	public AutoFecthTriggerEntity getAutoFetchDetails(String programName) {
		AutoFecthTriggerEntity autoFecthTriggerEntity = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(AutoFecthTriggerEntity.class);
			if (programName != null && !programName.equals("")) {
				criteria.createAlias("customerDetailsEntity", "customerDetailsEntity");
				criteria.add(Restrictions.eq("customerDetailsEntity.programName", programName));
			}

			List<AutoFecthTriggerEntity> listData = criteria.list();
			if (!ObjectUtils.isEmpty(listData)) {
				autoFecthTriggerEntity = listData.get(0);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return autoFecthTriggerEntity;
	}

	@Override
	public AutoFecthTriggerEntity mergeAutoFetchDetails(AutoFecthTriggerEntity autoFecthTriggerEntity) {
		AutoFecthTriggerEntity autonewFecthTriggerEntity = null;
		try {
			autonewFecthTriggerEntity = entityManager.merge(autoFecthTriggerEntity);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return autonewFecthTriggerEntity;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<OvScheduledEntity> getOvDetailsForExPort(OvScheduledModel ovScheduledModel) {
		List<OvScheduledEntity> totCommList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			criteriaTotList.createAlias("customerDetailsEntity", "customerDetailsEntity");
			
			Conjunction conjunction = Restrictions.conjunction();

			if (StringUtils.isNotEmpty(ovScheduledModel.getNeId())) {
				Criterion neId = Restrictions.eq("neId", ovScheduledModel.getNeId());
				conjunction.add(neId);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getCiqName())) {
				Criterion ciqName = Restrictions.eq("ciqName", ovScheduledModel.getCiqName());
				conjunction.add(ciqName);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getTrackerId())) {
				Criterion trackerId = Restrictions.eq("trackerId", ovScheduledModel.getTrackerId());
				conjunction.add(trackerId);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getWorkPlanID())) {
				Criterion workPlanID = Restrictions.eq("workPlanID", ovScheduledModel.getWorkPlanID());
				conjunction.add(workPlanID);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getPreMigStatus())) {
				Criterion preMigStatus = Restrictions.eq("preMigStatus", ovScheduledModel.getPreMigStatus());
				conjunction.add(preMigStatus);
			}

			if (StringUtils.isNotEmpty(ovScheduledModel.getEnvExportStatus())) {
				Criterion envExportStatus = Restrictions.eq("envExportStatus", ovScheduledModel.getEnvExportStatus());
				conjunction.add(envExportStatus);
			}

			if (StringUtils.isNotEmpty(ovScheduledModel.getPreMigGrowStatus())) {
				Criterion preMigGrowStatus = Restrictions.eq("preMigGrowStatus",
						ovScheduledModel.getPreMigGrowStatus());
				conjunction.add(preMigGrowStatus);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getSiteName())) {
				Criterion preMigGrowStatus = Restrictions.eq("siteName", ovScheduledModel.getSiteName());
				conjunction.add(preMigGrowStatus);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getFetchDate())) {
				// added c1, c2 to fetch date withh and w/o time stamp
				Criterion c1 = Restrictions.like("fetchDate", ovScheduledModel.getFetchDate(), MatchMode.ANYWHERE);
				Criterion c2 = Restrictions.eq("fetchDate", ovScheduledModel.getFetchDate());
				Criterion dateStatus = Restrictions.or(c1,c2);
				conjunction.add(dateStatus);
			}
			if (StringUtils.isNotEmpty(ovScheduledModel.getFetchRemarks())) {
				Criterion fetchStatus = Restrictions.eq("fetchRemarks", ovScheduledModel.getFetchRemarks());
				conjunction.add(fetchStatus);
			}
			
			String dateFetch = ovScheduledModel.getFetchDate();
			System.out.println("Fetch Date************* : " +dateFetch);
			String statusOfFetch = ovScheduledModel.getFetchRemarks();
			System.out.println(" Fetch Status ++++++++++ : " + statusOfFetch);
			criteriaTotList.add(conjunction);
			criteriaTotList.addOrder(Order.desc("fetchDate"));
			
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			
			System.out.println(totCommList);
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigRepositoryImpl.getNetworkConfigDetailsForExPort(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totCommList;
	}

	@Override
	public boolean deleteOvDetails(int ovId) {
		boolean status = false;
		try {
			entityManager.remove(getOvId(ovId));
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

	public OvScheduledEntity getOvId(int ovId) {
		return entityManager.find(OvScheduledEntity.class, ovId);
	}

}
