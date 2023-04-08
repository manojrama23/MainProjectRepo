package com.smart.rct.common.repositoryImpl;

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
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.models.LsmModel;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.common.repository.LsmRepository;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class LsmRepositoryImpl implements LsmRepository {

	final static Logger logger = LoggerFactory.getLogger(LsmRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * This method will return createLsm status
	 * 
	 * @param lsmEntity
	 * @return boolean
	 */
	@Override
	public boolean createLsm(LsmEntity lsmEntity) {
		boolean status = false;
		try {
			entityManager.persist(lsmEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.createLsm(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return updateLsm
	 * 
	 * @param lsmEntity
	 * @return boolean
	 */
	@Override
	public boolean updateLsm(LsmEntity lsmEntity) {
		boolean status = false;
		try {
			lsmEntity.setCreationDate(new Date());
			entityManager.merge(lsmEntity);
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in LsmRepositoryImpl. updateLsm(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will get LsmById
	 * 
	 * @param articleId
	 * @return LsmEntity
	 */
	public LsmEntity getLsmById(int articleId) {
		return entityManager.find(LsmEntity.class, articleId);
	}

	/**
	 * This method will delete Lsm Details by id
	 * 
	 * @param lsmId
	 * @return boolean
	 */
	@Override
	public boolean deleteLsmDetails(int lsmId) {
		boolean status = false;
		try {
			entityManager.remove(getLsmById(lsmId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in  LsmRepositoryImpl.deleteLsmrDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * 
	 * this method will check duplicateLsm
	 * 
	 * @param objLsmModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateLsm(LsmModel objLsmModel) {
		boolean status = false;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = builder.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root);
			if (objLsmModel.getId() != null && objLsmModel.getId() != 0) {
				query.where(builder.and(builder.equal(root.get("lsmName"), objLsmModel.getLsmName()),
						builder.equal(root.get("lsmVersion"), objLsmModel.getLsmVersion()),
						builder.equal(root.get("programName"), objLsmModel.getProgramName()),
						builder.equal(root.get("neType"), objLsmModel.getNeType()),
						builder.equal(root.get("bucket"), objLsmModel.getBucket()),
						builder.equal(root.get("networkTypeDetailsEntity"), objLsmModel.getNetworkTypeId()),
						builder.notEqual(root.get("id"), objLsmModel.getId())));
			} else {
				query.where(builder.and(builder.equal(root.get("lsmName"), objLsmModel.getLsmName()),
						builder.equal(root.get("lsmVersion"), objLsmModel.getLsmVersion()),
						builder.equal(root.get("programName"), objLsmModel.getProgramName()),
						builder.equal(root.get("neType"), objLsmModel.getNeType()),
						builder.equal(root.get("bucket"), objLsmModel.getBucket()),
						builder.equal(root.get("networkTypeDetailsEntity"), objLsmModel.getNetworkTypeId())));
			}
			TypedQuery<LsmEntity> queryResult = entityManager.createQuery(query);
			List<LsmEntity> objLsmEntityList = queryResult.getResultList();

			if (CommonUtil.isValidObject(objLsmEntityList) && objLsmEntityList.size() > 0) {
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception in LsmRepositoryImpl.duplicateLsm(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * 
	 * this method will get LsmVersionsByNetworkType
	 * 
	 * @param networkTypeId
	 * @return lsmVersionList
	 */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	@Override
	public List<String> getLsmVersionsByNetworkType(Integer networkTypeId) {
		List<String> lsmVersionList = null;
		try {
			StringBuilder objquery = new StringBuilder();
			objquery.append("SELECT Distinct(LSM_VERSION) FROM LSM_DETAILS where NW_TYPE_ID=" + networkTypeId + " ");
			Session session = entityManager.unwrap(Session.class).getSession();
			SQLQuery q = session.createSQLQuery(objquery.toString());
			lsmVersionList = q.list();
		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.getLsmVersionsByNetworkType(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmVersionList;
	}

	/**
	 * 
	 * this method will get NetWorksBasedOnCustomer
	 * 
	 * @param customerId
	 * @return nwtList
	 */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	@Override
	public List<NetworkTypeDetailsModel> getNetWorksBasedOnCustomer(int customerId) {
		List<NetworkTypeDetailsModel> nwtList = null;
		try {
			StringBuilder objquery = new StringBuilder();
			objquery.append("SELECT nt.ID as id,nt.NW_TYPE as networkType FROM CUSTOMER_DETAILS cd ");
			objquery.append(" join NW_TYPE_DETAILS nt on cd.NW_TYPE_ID=nt.ID where cd.CUSTOMER_ID=" + customerId + " ");
			Session session = entityManager.unwrap(Session.class).getSession();
			SQLQuery q = session.createSQLQuery(objquery.toString());
			q.setResultTransformer(Transformers.aliasToBean(NetworkTypeDetailsModel.class));
			nwtList = q.list();
		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.getNetWorksBasedOnCustomert(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return nwtList;
	}

	/**
	 * 
	 * this method will getLsmEntity
	 * 
	 * @param lsmVersion
	 * @param lsmName
	 * @param id
	 * @return nwtList
	 */
	@SuppressWarnings("deprecation")
	@Override
	public LsmEntity getLsmEntity(String lsmVersion, String lsmName, Integer id) {
		logger.info("CustomerRepositoryImpl.getNetworkTypeById() networkTypeId: ");
		LsmEntity lsmEntity = null;

		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			criteria.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			criteria.add(Restrictions.eq("lsmName", lsmName));
			criteria.add(Restrictions.eq("lsmVersion", lsmVersion));
			criteria.add(Restrictions.eq("networkTypeDetailsEntity.id", id));
			lsmEntity = (LsmEntity) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.getLsmEntity(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmEntity;
	}

	/**
	 * 
	 * this method will get LsmEntityDetails
	 * 
	 * 
	 * @return lsmEntityList
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<LsmEntity> getLsmEntityDetails() {
		logger.info("CustomerRepositoryImpl.getNetworkTypeById() networkTypeId: ");
		List<LsmEntity> lsmEntityList = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			lsmEntityList = criteria.list();
		} catch (Exception e) {
			logger.error(
					"Exception in LsmRepositoryImpl.getLsmEntityDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmEntityList;
	}

	/**
	 * This method will return LsmDetails List
	 * 
	 * @param page
	 * @param count
	 * @return List<LsmEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getLsmDetails(LsmModel objLsmModel, int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<LsmModel> lsmList = new ArrayList<>();
		List<LsmModel> totCommList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			criteria.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");

			Conjunction conjunction = Restrictions.conjunction();

			if (objLsmModel != null) {
				if (StringUtils.isNotEmpty(objLsmModel.getNetworkType())) {
					Criterion eventNetWorkName = Restrictions.ilike("networkTypeDetailsEntity.networkType",
							objLsmModel.getNetworkType(), MatchMode.ANYWHERE);
					conjunction.add(eventNetWorkName);
				}
				if (StringUtils.isNotEmpty(objLsmModel.getProgramName())) {
					Criterion eventprogramName = Restrictions.ilike("programName", objLsmModel.getProgramName(),
							MatchMode.ANYWHERE);
					conjunction.add(eventprogramName);
				}
				if (StringUtils.isNotEmpty(objLsmModel.getNeType())) {
					Criterion eventneType = Restrictions.ilike("neType", objLsmModel.getNeType(), MatchMode.ANYWHERE);
					conjunction.add(eventneType);
				}
				if (StringUtils.isNotEmpty(objLsmModel.getLsmName())) {
					Criterion eventlsmName = Restrictions.ilike("lsmName", objLsmModel.getLsmName(),
							MatchMode.ANYWHERE);
					conjunction.add(eventlsmName);
				}
				if (StringUtils.isNotEmpty(objLsmModel.getBucket())) {
					Criterion eventbucket = Restrictions.ilike("bucket", objLsmModel.getBucket(), MatchMode.ANYWHERE);
					conjunction.add(eventbucket);
				}
				if (StringUtils.isNotEmpty(objLsmModel.getLsmName())) {
					Criterion eventstatus = Restrictions.ilike("status", objLsmModel.getStatus(), MatchMode.ANYWHERE);
					conjunction.add(eventstatus);
				}
			}

			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("networkTypeDetailsEntity.networkType"), "networkType")
					.add(Projections.property("networkTypeDetailsEntity.id"), "networkTypeId")
					.add(Projections.property("programName"), "programName")
					.add(Projections.property("neType"), "neType").add(Projections.property("bucket"), "bucket")
					.add(Projections.property("lsmVersion"), "lsmVersion").add(Projections.property("lsmIp"), "lsmIp")
					.add(Projections.property("lsmName"), "lsmName").add(Projections.property("createdBy"), "createdBy")
					.add(Projections.property("lsmUserName"), "lsmUserName")
					.add(Projections.property("lsmPassword"), "lsmPassword")
					.add(Projections.property("status"), "status").add(Projections.property("remarks"), "remarks")
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
							new String[] { "creationDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");
			criteria.setProjection(commonProjection);
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			// pagination List
			lsmList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(LsmModel.class)).list();
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			criteriaTotList.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			criteriaTotList.setProjection(commonProjection);
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(LsmModel.class)).list();
			// Tot count Details
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			criteriaCount.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			objMap.put("lsmList", lsmList);
			objMap.put("totList", totCommList);
			objMap.put("pageCount", paginationNumber);
		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.getLsmDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will return LsmDetails List
	 * 
	 * @param page
	 * @param count
	 * @return List<LsmEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<LsmModel> getLsmDetailsForCreateExcel(LsmModel objLsmModel) {
		List<LsmModel> totCommList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			criteriaTotList.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("networkTypeDetailsEntity.networkType"), "networkType")
					.add(Projections.property("networkTypeDetailsEntity.id"), "networkTypeId")
					.add(Projections.property("programName"), "programName")
					.add(Projections.property("neType"), "neType").add(Projections.property("bucket"), "bucket")
					.add(Projections.property("lsmVersion"), "lsmVersion").add(Projections.property("lsmIp"), "lsmIp")
					.add(Projections.property("lsmName"), "lsmName").add(Projections.property("createdBy"), "createdBy")
					.add(Projections.property("lsmUserName"), "lsmUserName")
					.add(Projections.property("lsmPassword"), "lsmPassword")
					.add(Projections.property("status"), "status").add(Projections.property("remarks"), "remarks")
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
							new String[] { "creationDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");
			criteriaTotList.setProjection(commonProjection);
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(LsmModel.class)).list();
		} catch (Exception e) {
			logger.error("Exception in LsmRepositoryImpl.getLsmDetailsForCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totCommList;
	}
}
