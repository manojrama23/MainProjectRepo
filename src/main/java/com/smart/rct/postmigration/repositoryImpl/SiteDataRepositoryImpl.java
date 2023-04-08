package com.smart.rct.postmigration.repositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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

import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.postmigration.repository.SiteDataRepository;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class SiteDataRepositoryImpl implements SiteDataRepository {

	final static Logger logger = LoggerFactory.getLogger(SiteDataRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * This method will saveSiteDataAudit
	 * 
	 * @param siteDataEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSiteDataAudit(SiteDataEntity siteDataEntity) {
		boolean status = false;
		try {
			entityManager.merge(siteDataEntity);
			status = true;
		} catch (Exception e) {
			logger.error(
					"Exception in SiteDataRepositoryImpl.saveSiteDataAudit():" + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will deleteSiteDataDetails
	 * 
	 * @param siteDataModel
	 * @return boolean
	 */
	@Override
	public boolean deleteSiteDataDetails(SiteDataModel siteDataModel) {
		boolean status = false;
		try {
			SiteDataEntity entity = getSiteDataDetailsById(siteDataModel.getId());
			if (entity != null) {
				entityManager.remove(entity);
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception in SiteDataRepositoryImpl.deleteSiteDataDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will getSiteDataDetailsById
	 * 
	 * @param articleId
	 * @return SiteDataEntity
	 */
	@Override
	public SiteDataEntity getSiteDataDetailsById(int articleId) {
		return entityManager.find(SiteDataEntity.class, articleId);
	}

	/**
	 * This method will getSiteDataDetails
	 * 
	 * @param siteDataModel,page,count
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getSiteDataDetails(SiteDataModel siteDataModel, int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SiteDataEntity> siteDataEntities = null;
		List<SiteDataEntity> searchList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteDataEntity.class);
			criteria.addOrder(Order.desc("packedDate"));
			Conjunction conjunction = Restrictions.conjunction();
			if (siteDataModel != null) {
				if (StringUtils.isNotEmpty(siteDataModel.getCiqFileName())) {
					Criterion ciqFileName = Restrictions.eq("ciqFileName", siteDataModel.getCiqFileName());
					conjunction.add(ciqFileName);
				}
				if (StringUtils.isNotEmpty(siteDataModel.getFileName())) {
					Criterion fileName = Restrictions.ilike("fileName", siteDataModel.getFileName(),
							MatchMode.ANYWHERE);
					conjunction.add(fileName);
				}
				if (StringUtils.isNotEmpty(siteDataModel.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", siteDataModel.getNeName(), MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
				if (siteDataModel.getProgramDetailsEntity().getId() != null) {
					criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id",
							siteDataModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (siteDataModel.getSearchStartDate() != null && !"".equals(siteDataModel.getSearchStartDate())
						&& siteDataModel.getSearchEndDate() != null && !"".equals(siteDataModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("packedDate",
							DateUtil.stringToDate(siteDataModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("packedDate",
							DateUtil.stringToDateEndTime(siteDataModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			siteDataEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SiteDataEntity.class);
			criteriaList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaList
					.add(Restrictions.eq("programDetailsEntity.id", siteDataModel.getProgramDetailsEntity().getId()));
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			Set<String> ciqList = searchList.stream().map(x -> x.getCiqFileName()).sorted().collect(Collectors.toSet());
			Set<String> fileList = searchList.stream().map(x -> x.getFileName()).sorted().collect(Collectors.toSet());
			Set<String> neNameList = searchList.stream().map(x -> x.getNeName()).sorted().collect(Collectors.toSet());

			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SiteDataEntity.class);
			criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;

			objMap.put("ciqList", ciqList);
			objMap.put("fileList", fileList);
			objMap.put("neNameList", neNameList);
			objMap.put("paginationcount", pagecount);
			objMap.put("siteDataList", siteDataEntities);
		} catch (Exception e) {
			logger.error(
					"Exception in SiteDataRepositoryImpl.getSiteDataDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
}
