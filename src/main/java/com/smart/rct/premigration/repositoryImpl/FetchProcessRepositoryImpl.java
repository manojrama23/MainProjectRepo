package com.smart.rct.premigration.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvReport;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.FetchOVResponseModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.serviceImpl.EnodebDetails;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class FetchProcessRepositoryImpl implements FetchProcessRepository {

	final static Logger logger = LoggerFactory.getLogger(FetchProcessRepositoryImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public OvScheduledEntity getScheduledRecord(String neId, String trackerId) {
		OvScheduledEntity ovScheduledEntity = null;

		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvScheduledEntity.class);
			criteria.add(Restrictions.eq("trackerId", trackerId));
			criteria.add(Restrictions.eq("neId", neId));
			List<OvScheduledEntity> ovList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			if (!ObjectUtils.isEmpty(ovList)) {
				ovScheduledEntity = ovList.get(0);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ovScheduledEntity;
	}

	@Override
	public OvScheduledEntity saveScheduledDetails(OvScheduledEntity ovScheduledEntity) {
		OvScheduledEntity objOvScheduledEntity = null;
		try {

			objOvScheduledEntity = entityManager.merge(ovScheduledEntity);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvScheduledEntity;
	}
	
	@Override
	public OvReport saveOvReportsDetails(OvReport ovReports) {
		OvReport objOvScheduledEntity = null;
		try {

			objOvScheduledEntity = entityManager.merge(ovReports);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvScheduledEntity;
	}
	
	@Override
	public OvReport getOvReportsDetails(String trackerId, String enbId) {
		OvReport ScheduledEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<OvReport> query = cb.createQuery(OvReport.class);
			Root<OvReport> root = query.from(OvReport.class);

			query.select(root);
			if(enbId!=null)
			query.where(cb.and(cb.equal(root.get("enbId"), enbId), cb.equal(root.get("trackerid"), trackerId)));
			else
			query.where(cb.and(cb.equal(root.get("trackerid"), trackerId)));

			// query.where();
			TypedQuery<OvReport> queryResult = entityManager.createQuery(query);
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
	public CustomerDetailsEntity getProgrammeDetails(String programName) {
		CustomerDetailsEntity customerDetailsEntity = null;
		{
			try {
				Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CustomerDetailsEntity.class);
				criteria.add(Restrictions.eq("programName", programName));
				List<CustomerDetailsEntity> ovList = criteria
						.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
				if (!ObjectUtils.isEmpty(ovList)) {
					customerDetailsEntity = ovList.get(0);
				}
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return customerDetailsEntity;
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<ProgramTemplateEntity> getScheduledDaysDetails(ProgramTemplateModel programTemplateModel) {
		logger.info("CustomerRepositoryImpl.getProgTemplateDetails() called.. ");
		List<ProgramTemplateEntity> programTemplateEntities = null;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(
					Restrictions.eq("programDetailsEntity.id", programTemplateModel.getProgramDetailsEntity().getId()));
			if (StringUtils.isNotEmpty(programTemplateModel.getConfigType())) {
				criteria.add(Restrictions.eq("configType", programTemplateModel.getConfigType()));
			}
			/*
			 * if(StringUtils.isNotEmpty(programTemplateModel.getLabel())) {
			 * criteria.add(Restrictions.eq("label", programTemplateModel.getLabel())); }
			 */
			programTemplateEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.info(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return programTemplateEntities;
	}

	/**
	 * This method will getFetchTimeProgaramTemplate
	 * 
	 * @param programTemplateModel
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	@Override
	public ProgramTemplateEntity getFetchTimeProgaramTemplate(ProgramTemplateModel programTemplateModel) {
		ProgramTemplateEntity programTemplateEntity = null;
		boolean status = false;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(
					Restrictions.eq("programDetailsEntity.id", programTemplateModel.getProgramDetailsEntity().getId()));
			criteria.add(Restrictions.eq("label", programTemplateModel.getLabel()));
			List<ProgramTemplateEntity> programTemplateEntities = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (!ObjectUtils.isEmpty(programTemplateEntities)) {
				programTemplateEntity = programTemplateEntities.get(0);
			}
		} catch (Exception e) {
			logger.info(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return programTemplateEntity;
	}

	/**
	 * This method will getNetworkConfigDetails
	 * 
	 * @param objFetchOVResponseModel
	 * @return NetworkConfigEntity
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<NetworkConfigEntity> getNetworkConfigDetailsForOv(FetchOVResponseModel objFetchOVResponseModel) {

		List<NetworkConfigEntity> networkConfigList = null;

		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.createAlias("neTypeEntity", "neTypeEntity");
			Conjunction conjunction = Restrictions.conjunction();

			if (objFetchOVResponseModel != null) {
				if (CommonUtil.isValidObject(objFetchOVResponseModel.getCustomerDetailsEntity())
						&& CommonUtil.isValidObject(objFetchOVResponseModel.getCustomerDetailsEntity().getId())) {
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id",
							objFetchOVResponseModel.getCustomerDetailsEntity().getId());
					conjunction.add(eventprogramName);
					Criterion eventneType = Restrictions.or(Restrictions.eq("neTypeEntity.neType", Constants.FETCH_NE),
							Restrictions.eq("neTypeEntity.neType", Constants.SCRIPT_NE));
					conjunction.add(eventneType);

					criteria.add(conjunction);

					networkConfigList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
							.list();
				}

			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigList;
	}
	
	
	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param id,
	 *            filename
	 * @return List<EnodebDetails> 
	 */
	@Override
	public List<EnodebDetails> getEnbDetails(String ciqfileName, String dbcollectionFileName) {
		List<EnodebDetails> result =null;
		Query query = new Query(org.springframework.data.mongodb.core.query.Criteria.where("fileName").is(ciqfileName));
		try {
			query.fields().include("eNBName");
			query.fields().include("eNBId");
			query.fields().include("siteName");
			 result = mongoTemplate.find(query, EnodebDetails.class, dbcollectionFileName);
		
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return result;
	}
	
}
