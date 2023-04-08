package com.smart.rct.common.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.common.repository.NeVersionRepository;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class NeVersionRepositoryImpl implements NeVersionRepository {

	final static Logger logger = LoggerFactory.getLogger(NeVersionRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * This method will Save the Neversion Details and return Status
	 * 
	 * @param neEntity
	 * @return boolean
	 */
	@Override
	public boolean createNeVersion(NeVersionEntity neEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			entityManager.merge(neEntity);
			status = true;
		} catch (Exception e) {
			logger.error(
					"Exception in  NeVersionRepositoryImpl.createNeVersion(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will check the duplication of Neversion Details and return Status
	 * 
	 * @param neVersionModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateNeVersion(NeVersionModel neVersionModel) {
		boolean status = false;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> query = builder.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> root = query.from(NeVersionEntity.class);
			query.select(root);
			if (neVersionModel.getId() != null && neVersionModel.getId() != 0) {
				query.where(builder.and(
						builder.equal(root.get("programDetailsEntity"),
								neVersionModel.getProgramDetailsEntity().getId()),
						builder.equal(root.get("neVersion"), neVersionModel.getNeVersion()),
						builder.notEqual(root.get("id"), neVersionModel.getId())));
			} else {
				query.where(builder.and(
						builder.equal(root.get("programDetailsEntity"),
								neVersionModel.getProgramDetailsEntity().getId()),
						builder.equal(root.get("neVersion"), neVersionModel.getNeVersion())));
			}
			TypedQuery<NeVersionEntity> queryResult = entityManager.createQuery(query);
			List<NeVersionEntity> configEntities = queryResult.getResultList();

			if (CommonUtil.isValidObject(configEntities) && configEntities.size() > 0) {
				status = true;
			}
			logger.info("status: " + status);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in NetworkConfigRepositoryImpl.duplicateNetworkConfig():"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will getNeVersionList
	 * 
	 * @param neVersionModel
	 * @return NeVersionEntity
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<NeVersionEntity> getNeVersionList(NeVersionModel neVersionModel) {
		List<NeVersionEntity> neVersionEntities = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeVersionEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			Conjunction conjunction = Restrictions.conjunction();
			if (neVersionModel != null) {
				if (CommonUtil.isValidObject(neVersionModel.getProgramDetailsEntity())
						&& CommonUtil.isValidObject(neVersionModel.getProgramDetailsEntity().getId())) {
					Criterion eventProgramName = Restrictions.eq("programDetailsEntity.id",
							neVersionModel.getProgramDetailsEntity().getId());
					conjunction.add(eventProgramName);
				}
				if (StringUtils.isNotEmpty(neVersionModel.getNeVersion())) {
					Criterion eventNeVersion = Restrictions.ilike("neVersion", neVersionModel.getNeVersion(),
							MatchMode.ANYWHERE);
					conjunction.add(eventNeVersion);
				}
				if (StringUtils.isNotEmpty(neVersionModel.getStatus())) {
					Criterion eventStatus = Restrictions.or(Restrictions.eq("status", neVersionModel.getStatus()),Restrictions.eq("status", "StandBy"));
					conjunction.add(eventStatus);
				}
			}
			criteria.add(conjunction);
			criteria.addOrder(Order.desc("creationDate"));
			neVersionEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info(
					"Exception in NeVersionRepositoryImpl.getNeVersionList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neVersionEntities;
	}

	/**
	 * This api get the getNeVersionById
	 * 
	 * @param neVersionDetailId
	 * @return NeVersionEntity
	 */
	@Override
	public NeVersionEntity getNeVersionById(int neVersionDetailId) {
		logger.info("NeVersionRepositoryImpl.getNeVersionById() customerId: " + neVersionDetailId);
		NeVersionEntity neVersionEntity = null;
		try {
			neVersionEntity = entityManager.find(NeVersionEntity.class, neVersionDetailId);
		} catch (Exception e) {
			logger.info(
					"Exception in NeVersionRepositoryImpl.getNeVersionById() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neVersionEntity;
	}

	/**
	 * This api will deleteNeVersionDetails
	 * 
	 * @param neVersionDetailId
	 * @return boolean
	 */
	@Override
	public boolean deleteNeVersionDetails(int neVersionDetailId) {
		logger.info("NeVersionRepositoryImpl deleteNeVersionDetails(): " + neVersionDetailId);
		boolean status = false;
		try {
			Query query = entityManager.createQuery("DELETE FROM  NeVersionEntity WHERE id=:id");
			query.setParameter("id", neVersionDetailId);
			query.executeUpdate();
			status = true;
		} catch (Exception e) {
			status = false;
			logger.info("Exception in NeVersionRepositoryImpl.deleteNeVersionDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

}
