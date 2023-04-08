package com.smart.rct.premigration.repositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.NeMappingRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class NeMappingRepositoryImpl implements NeMappingRepository{
	final static Logger logger = LoggerFactory.getLogger(NeMappingRepositoryImpl.class);


	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Map<String, Object> getNeMapping(NeMappingModel neMappingModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<NeMappingEntity> neMappingEntity = null;
		List<NeConfigTypeEntity> neConfigType = new ArrayList<>();
		int pagecount = 0;
		double result = 0;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			criteria.setFetchMode("networkConfigEntity", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();
			if (neMappingModel != null) {
				if (StringUtils.isNotEmpty(neMappingModel.getEnbId())) {
					Criterion enbId = Restrictions.ilike("enbId", neMappingModel.getEnbId(), MatchMode.ANYWHERE);
					conjunction.add(enbId);
				}
				if (StringUtils.isNotEmpty(neMappingModel.getSiteConfigType())) {
					Criterion siteConfigType = Restrictions.ilike("siteConfigType", neMappingModel.getSiteConfigType(), MatchMode.ANYWHERE);
					conjunction.add(siteConfigType);
				}
				if (neMappingModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				//If enbId is not passed in criteria then check the dates else ignore the date range
				if (!StringUtils.isNotEmpty(neMappingModel.getEnbId()) && neMappingModel.getSearchStartDate() != null && !"".equals(neMappingModel.getSearchStartDate()) && neMappingModel.getSearchEndDate() != null && !"".equals(neMappingModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("creationDate", DateUtil.stringToDate(neMappingModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("creationDate", DateUtil.stringToDateEndTime(neMappingModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			neMappingEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.add(Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId()));
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;	
			objMap.put("NeMappingDetails", neMappingEntity);
			objMap.put("PageCount", pagecount);
			
			org.hibernate.Criteria criteriaMarketList = entityManager.unwrap(Session.class).createCriteria(NeConfigTypeEntity.class);
			criteriaMarketList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaMarketList.add(Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId()));
			neConfigType = criteriaMarketList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> neConfigTypeList = neConfigType.stream().map(x -> x.getNwConfigType()).sorted().collect(Collectors.toSet());
			objMap.put("neConfigTypeList", neConfigTypeList);
			}
		catch (Exception e) {
			logger.error("Exception getNeMapping() in NeMappingRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} 
		return objMap;

	}

	
	@Override
	public List<NeMappingEntity> getNeMapping(NeMappingModel neMappingModel) {
		List<NeMappingEntity> neMappingEntityList = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (neMappingModel != null) {
				if (StringUtils.isNotEmpty(neMappingModel.getEnbId())) {
					Criterion enbId = Restrictions.eq("enbId", neMappingModel.getEnbId());
					conjunction.add(enbId);
				}
				if (StringUtils.isNotEmpty(neMappingModel.getSiteConfigType())) {
					Criterion siteConfigType = Restrictions.ilike("siteConfigType", neMappingModel.getSiteConfigType(), MatchMode.ANYWHERE);
					conjunction.add(siteConfigType);
				}
				if (neMappingModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (neMappingModel.getSearchStartDate() != null && !"".equals(neMappingModel.getSearchStartDate()) && neMappingModel.getSearchEndDate() != null && !"".equals(neMappingModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("creationDate", DateUtil.stringToDate(neMappingModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("creationDate", DateUtil.stringToDateEndTime(neMappingModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			criteria.add(conjunction);
			neMappingEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		}
		catch (Exception e) {
			logger.error("Exception getNeMapping() in NeMappingRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} 
		return neMappingEntityList;

	}
	
	@Override
	public Map<String,Map<String,List<NetworkConfigEntity>>> getDropDownList(Integer programId) {
		Map<String,Map<String,List<NetworkConfigEntity> >> marketList=new LinkedHashMap<>();
		Map<String,List<NetworkConfigEntity> > versionList=new LinkedHashMap<>();
		List<NetworkConfigEntity> SearchMarketList = new ArrayList<>();
		
		try {
		org.hibernate.Criteria criteriaMarketList = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
		criteriaMarketList.createAlias("programDetailsEntity", "programDetailsEntity");
		criteriaMarketList.createAlias("neVersionEntity", "neVersionEntity");
		criteriaMarketList.add(Restrictions.eq("programDetailsEntity.id", programId));
		SearchMarketList = criteriaMarketList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		Set<String> marketNameList = SearchMarketList.stream().filter(x -> StringUtils.isNotEmpty(x.getNeMarket())).map(x -> x.getNeMarket()).sorted().collect(Collectors.toSet());
		for( String marketName : marketNameList){
			versionList=new LinkedHashMap<>();
			List<String> smVersionList = SearchMarketList.stream().filter(x -> marketName.equals(x.getNeMarket()) && (Constants.ACTIVE.equalsIgnoreCase(x.getNeVersionEntity().getStatus()) || Constants.StandBy.equalsIgnoreCase(x.getNeVersionEntity().getStatus()))).map(x->x.getNeVersionEntity().getNeVersion()).collect(Collectors.toList());
			for(String smVersion :  smVersionList){
				List<NetworkConfigEntity> lsmList =  SearchMarketList.stream().filter(x -> marketName.equals(x.getNeMarket())).filter(x ->smVersion.equals(x.getNeVersionEntity().getNeVersion())).map(x->x).collect(Collectors.toList());
				versionList.put(smVersion, lsmList);
			}
			marketList.put(marketName, versionList);
		}
	}catch (Exception e) {
		logger.error("Exception  getDropDownList() in  NeMappingRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
	} 
		return marketList;
	}

	@Override
	public boolean saveNeMappingDetails(NeMappingEntity neMappingEntity) {
		boolean status = false;
		try {
			entityManager.merge(neMappingEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in NeMappingRepositoryImpl.saveNeMappingDetails():"+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}


	@Override
	public List<NeConfigTypeEntity> getNeConfigTypeDetails(Integer programId) {
		logger.info("NeMappingRepositoryImpl.neConfigTypeDetails() called.. programId: " + programId);
		List<NeConfigTypeEntity> neConfigTypeDetails = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeConfigTypeEntity> query = builder.createQuery(NeConfigTypeEntity.class);
			Root<NeConfigTypeEntity> root = query.from(NeConfigTypeEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(programId)) {
				query.where(builder.equal(root.get("programDetailsEntity"), programId));
			}
			TypedQuery<NeConfigTypeEntity> queryResult = entityManager.createQuery(query);
			neConfigTypeDetails = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NeMappingRepositoryImpl.getProgramGenerateFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neConfigTypeDetails;
	}


	@Override
	public boolean saveNeConfigType(NeConfigTypeEntity neConfigTypeEntity) {
		boolean status = false;
		try {
			entityManager.merge(neConfigTypeEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in NeMappingRepositoryImpl.saveNeConfigType():"+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	
	
	@Override
	public List<NeMappingEntity> getSName(String gnodebId) {
		List<NeMappingEntity> neMappingEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeMappingEntity> query = builder.createQuery(NeMappingEntity.class);
			Root<NeMappingEntity> root = query.from(NeMappingEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("enbId"), gnodebId));
			
			TypedQuery<NeMappingEntity> queryResult = entityManager.createQuery(query);
			neMappingEntity = queryResult.getResultList();
			
		} catch (Exception e) {
			logger.info("Exception in NeMappingRepositoryImpl.getProgramGenerateFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neMappingEntity;
	}
	
	@Override
	public List<NeMappingEntity> getGNBS(String siteName) {
		List<NeMappingEntity> neMappingEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeMappingEntity> query = builder.createQuery(NeMappingEntity.class);
			Root<NeMappingEntity> root = query.from(NeMappingEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("siteName"), siteName));
			
			TypedQuery<NeMappingEntity> queryResult = entityManager.createQuery(query);
			neMappingEntity = queryResult.getResultList();
			
		} catch (Exception e) {
			logger.info("Exception in NeMappingRepositoryImpl.getProgramGenerateFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neMappingEntity;
	}



	
	@Override
	public List<NeMappingEntity> getNeMappingList(NeMappingModel neMappingModel) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<NeMappingEntity> neMappingEntity = null;
		List<NeConfigTypeEntity> neConfigType = new ArrayList<>();
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			//criteria.setFetchMode("networkConfigEntity", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();
			if (neMappingModel != null) {
				
			//	if (StringUtils.isNotEmpty(neMappingModel.getSiteConfigType())) {
				if (neMappingModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				//If enbId is not passed in criteria then check the dates else ignore the date range
				
			//}
			criteria.add(conjunction);
			criteria.addOrder(Order.asc("siteConfigType"));
			neMappingEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			
			}
		}
		catch (Exception e) {
			logger.error("Exception getNeMapping() in NeMappingRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} 
		return neMappingEntity;

	}
		

	@Override
	public List<NetworkConfigEntity> getNeConfigList(Integer programId) {
		List<NetworkConfigEntity> SearchMarketList = new ArrayList<>();
		try {
			org.hibernate.Criteria criteriaMarketList = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteriaMarketList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaMarketList.createAlias("neVersionEntity", "neVersionEntity");
			criteriaMarketList.add(Restrictions.eq("programDetailsEntity.id", programId));
			SearchMarketList = criteriaMarketList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		}catch (Exception e) {
			logger.error("Exception  getDropDownList() in  NeMappingRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} 
			return SearchMarketList;
	}
	
	
	
}