package com.smart.rct.common.repositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.smart.rct.common.entity.LoginTypeEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ServerTypeEntity;
import com.smart.rct.common.models.LoginTypeModel;
import com.smart.rct.common.models.NeTypeModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.ServerTypeModel;
import com.smart.rct.common.repository.NetworkConfigRepository;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class NetworkConfigRepositoryImpl implements NetworkConfigRepository{
	
	final static Logger logger = LoggerFactory.getLogger(NetworkConfigRepositoryImpl.class);
	
	@PersistenceContext
	EntityManager entityManager;
	
	/**
	 * This method will getNetworkConfigDetails
	 * 
	 * @param networkConfigModel,page,count,programNamesList
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getNetworkConfigDetails(NetworkConfigModel networkConfigModel, int page, int count, List<CustomerDetailsEntity> programNamesList) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<NetworkConfigEntity> networkConfigList = new ArrayList<>();
		List<NetworkConfigEntity> totCommList = new ArrayList<>();
		List<Integer> programs = null;
		double result = 0;
		int paginationNumber = 0;
		try {
			if (CommonUtil.isValidObject(programNamesList) ){
				programs =  programNamesList.stream().map(x->x.getId()).collect(Collectors.toList());
			}
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.createAlias("neTypeEntity", "neTypeEntity");
			criteria.createAlias("loginTypeEntity", "loginTypeEntity");
			criteria.setFetchMode("neDetails", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();

			if (networkConfigModel != null) {
				if (CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity()) && CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity().getId())) {
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", networkConfigModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity().getId())) {
					Criterion eventneType = Restrictions.eq("neTypeEntity.id", networkConfigModel.getNeTypeEntity().getId());
					conjunction.add(eventneType);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeName())) {
					Criterion eventNeName = Restrictions.ilike("neName", networkConfigModel.getNeName(),MatchMode.ANYWHERE);
					conjunction.add(eventNeName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity().getId())) {
					criteria.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventVersion = Restrictions.eq("neVersionEntity.id", networkConfigModel.getNeVersionEntity().getId());
					conjunction.add(eventVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
					criteria.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventNeVersion = Restrictions.ilike("neVersionEntity.neVersion", networkConfigModel.getNeVersion(),MatchMode.ANYWHERE);
					conjunction.add(eventNeVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getStatus())) {
					Criterion eventStatus = Restrictions.ilike("status", networkConfigModel.getStatus());
					conjunction.add(eventStatus);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity().getId())) {
					Criterion eventLoginType = Restrictions.eq("loginTypeEntity.id", networkConfigModel.getLoginTypeEntity().getId());
					conjunction.add(eventLoginType);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeMarket())) {
					Criterion eventNeMarket = Restrictions.ilike("neMarket", networkConfigModel.getNeMarket(),MatchMode.ANYWHERE);
					conjunction.add(eventNeMarket);
				}
			}
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				conjunction.add(eventRestrictedPrograms);
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			//pagination List
			networkConfigList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			for(NetworkConfigEntity configEntity: networkConfigList){
				configEntity.getNeDetails().sort((p1,p2) -> Integer.valueOf(p1.getStep()).compareTo(Integer.valueOf(p2.getStep())));
			}
			//total List
			Conjunction totalListConjunction = Restrictions.conjunction();
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaTotList.createAlias("neTypeEntity", "neTypeEntity");
			criteriaTotList.createAlias("loginTypeEntity", "loginTypeEntity");
			if (networkConfigModel != null && StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
			criteriaTotList.createAlias("neVersionEntity", "neVersionEntity");
			}
			criteriaTotList.setFetchMode("neDetails", FetchMode.LAZY);
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				totalListConjunction.add(eventRestrictedPrograms);
				criteriaTotList.add(totalListConjunction);
			}
			criteriaTotList.addOrder(Order.desc("creationDate"));
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> neNameList = totCommList.stream().map(x->x.getNeName()).sorted().collect(Collectors.toSet());
			Set<String> neMarketList = totCommList.stream().filter(x->CommonUtil.isValidObject(x.getNeMarket()) &&  x.getNeMarket().length() > 0).map(x->x.getNeMarket()).sorted().collect(Collectors.toSet());
			// Tot count Details
			Conjunction totalCountConjunction = Restrictions.conjunction();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.createAlias("neTypeEntity", "neTypeEntity");
			criteriaCount.createAlias("loginTypeEntity", "loginTypeEntity");
			if (networkConfigModel != null && StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
			criteriaCount.createAlias("neVersionEntity", "neVersionEntity");
			}
			criteriaCount.setFetchMode("neDetails", FetchMode.LAZY);
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				totalCountConjunction.add(eventRestrictedPrograms);
				criteriaCount.add(totalCountConjunction);
			}
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			objMap.put("neMarketList", neMarketList);
			objMap.put("neNameList", neNameList);
			objMap.put("networkConfigList", networkConfigList);
			objMap.put("totList", totCommList);
			objMap.put("pageCount", paginationNumber);
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigRepositoryImpl.getnetworkConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getNeTypeList
	 * 
	 * @param neTypeModel
	 * @return List<NeTypeEntity>
	 */
	@Override
	public List<NeTypeEntity> getNeTypeList(NeTypeModel neTypeModel) {
		List<NeTypeEntity> neTypeList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeTypeEntity> query = builder.createQuery(NeTypeEntity.class);
			Root<NeTypeEntity> root = query.from(NeTypeEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(neTypeModel) && CommonUtil.isValidObject(neTypeModel.getId())) {
				query.where(builder.equal(root.get("id"), neTypeModel.getId()));
			}
			if (CommonUtil.isValidObject(neTypeModel) && CommonUtil.isValidObject(neTypeModel.getNeType())) {
				query.where(builder.like(root.get("neType"), neTypeModel.getNeType().trim()));
			}
			TypedQuery<NeTypeEntity> queryResult = entityManager.createQuery(query);
			neTypeList = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NetworkConfigRepositoryImpl.getNeTypeList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neTypeList;
	}

	/**
	 * This method will getLoginTypeList
	 * 
	 * @param loginTypeModel
	 * @return List<LoginTypeEntity>
	 */
	@Override
	public List<LoginTypeEntity> getLoginTypeList(LoginTypeModel loginTypeModel) {
		List<LoginTypeEntity> loginTypeList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<LoginTypeEntity> query = builder.createQuery(LoginTypeEntity.class);
			Root<LoginTypeEntity> root = query.from(LoginTypeEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(loginTypeModel) && CommonUtil.isValidObject(loginTypeModel.getId())) {
				builder.equal(root.get("id"), loginTypeModel.getId());
			}
			if (CommonUtil.isValidObject(loginTypeModel) && CommonUtil.isValidObject(loginTypeModel.getLoginType())) {
				builder.like(root.get("loginType"), loginTypeModel.getLoginType().trim());
			}
			TypedQuery<LoginTypeEntity> queryResult = entityManager.createQuery(query);
			loginTypeList = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NetworkConfigRepositoryImpl.getLoginTypeList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return loginTypeList;
	}

	/**
	 * This method will getServerTypeList
	 * 
	 * @param serverTypeModel
	 * @return List<ServerTypeEntity>
	 */
	@Override
	public List<ServerTypeEntity> getServerTypeList(ServerTypeModel serverTypeModel) {
		List<ServerTypeEntity> serverTypeList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<ServerTypeEntity> query = builder.createQuery(ServerTypeEntity.class);
			Root<ServerTypeEntity> root = query.from(ServerTypeEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(serverTypeModel) && CommonUtil.isValidObject(serverTypeModel.getId())) {
				builder.equal(root.get("id"), serverTypeModel.getId());
			}
			if (CommonUtil.isValidObject(serverTypeModel) && CommonUtil.isValidObject(serverTypeModel.getServerType())) {
				builder.like(root.get("serverType"), serverTypeModel.getServerType().trim());
			}
			TypedQuery<ServerTypeEntity> queryResult = entityManager.createQuery(query);
			serverTypeList = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NetworkConfigRepositoryImpl.getServerTypeList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return serverTypeList;
	}

	/**
	 * This method will duplicateNetworkConfig
	 * 
	 * @param networkConfigModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateNetworkConfig(NetworkConfigModel networkConfigModel) {
		boolean status = false;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = builder.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root);
			if (networkConfigModel.getId() != null && networkConfigModel.getId() != 0) {
				query.where(builder.and(builder.equal(root.get("neName"), networkConfigModel.getNeName()),
			    builder.equal(root.get("neVersionEntity"), networkConfigModel.getNeVersionEntity().getId()),
				builder.equal(root.get("programDetailsEntity"), networkConfigModel.getProgramDetailsEntity().getId()),
				builder.equal(root.get("neTypeEntity"), networkConfigModel.getNeTypeEntity().getId()),
				builder.equal(root.get("neMarket"), networkConfigModel.getNeMarket()),
				builder.notEqual(root.get("id"), networkConfigModel.getId())));
			} else {
				query.where(builder.and(builder.equal(root.get("neName"), networkConfigModel.getNeName()),
				builder.equal(root.get("neVersionEntity"), networkConfigModel.getNeVersionEntity().getId()),
				builder.equal(root.get("programDetailsEntity"), networkConfigModel.getProgramDetailsEntity().getId()),
				builder.equal(root.get("neMarket"), networkConfigModel.getNeMarket()),
				builder.equal(root.get("neTypeEntity"), networkConfigModel.getNeTypeEntity().getId())));
			}
			TypedQuery<NetworkConfigEntity> queryResult = entityManager.createQuery(query);
			List<NetworkConfigEntity> configEntities = queryResult.getResultList();
			if (CommonUtil.isValidObject(configEntities) && configEntities.size() > 0) {
				status = true;
			}
			logger.info("status: "+status);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in NetworkConfigRepositoryImpl.duplicateNetworkConfig(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will createNetworkConfig
	 * 
	 * @param networkConfigEntity
	 * @return boolean
	 */
	@Override
	public boolean createNetworkConfig(NetworkConfigEntity networkConfigEntity) {
		boolean status = false;
		try {
			if(CommonUtil.isValidObject(networkConfigEntity) && CommonUtil.isValidObject(networkConfigEntity.getId()) && networkConfigEntity.getId() > 0){
				deleteNetworkConfigServerDetails(networkConfigEntity);
			}
			entityManager.merge(networkConfigEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigRepositoryImpl.createNetworkConfig(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will deleteNetworkConfigServerDetails
	 * 
	 * @param networkConfigEntity
	 * @return boolean
	 */
	public boolean deleteNetworkConfigServerDetails(NetworkConfigEntity networkConfigEntity) {
		boolean status = false; 
		try{
			Set<Integer> idList = null;
			if(CommonUtil.isValidObject(networkConfigEntity.getNeDetails())  && networkConfigEntity.getNeDetails().size() > 0){
				List<NetworkConfigDetailsEntity> neDetails = networkConfigEntity.getNeDetails();
				idList = neDetails.stream().map(x->x.getId()).sorted().collect(Collectors.toSet());
			}
			String hql = "DELETE FROM  NetworkConfigDetailsEntity WHERE networkConfigEntity.id=:id";
			if(CommonUtil.isValidObject(idList)){
				hql = hql+ " and id not in (:list)";
			}
			Query query = entityManager.createQuery(hql);
			query.setParameter("id", networkConfigEntity.getId());
			if(CommonUtil.isValidObject(idList)){
				query.setParameter("list", idList);
			}
			query.executeUpdate();
			status = true;
		}catch(Exception e){
			status = false;
			logger.error("Exception in NetworkConfigRepositoryImpl.deleteNetworkConfigServerDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		
		return status;
	}
	
	public NetworkConfigEntity getNetworkConfigById(int networkConfigId) {
		return entityManager.find(NetworkConfigEntity.class, networkConfigId);
	}
	
	
	/**
	 * This method will deleteNetworkConfigDetails
	 * 
	 * @param networkConfigId
	 * @return boolean
	 */
	@Override
	public boolean deleteNetworkConfigDetails(Integer networkConfigId) {
		boolean status = false;
		try {
			entityManager.remove(getNetworkConfigById(networkConfigId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in NetworkConfigRepositoryImpl.deleteNetworkConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will getNetworkConfigServerDetailsById
	 * 
	 * @param networkConfigDetailId
	 * @return NetworkConfigDetailsEntity
	 */
	@Override
	public NetworkConfigDetailsEntity getNetworkConfigServerDetailsById(int networkConfigDetailId) {
		NetworkConfigDetailsEntity networkConfigDetailsEntity = null;
		try {
			networkConfigDetailsEntity =  entityManager.find(NetworkConfigDetailsEntity.class, networkConfigDetailId);
		} catch (Exception e) {
			logger.info("Exception in NetworkConfigRepositoryImpl.getNetworkConfigServerDetailsById(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigDetailsEntity;
	}
	
	/**
	 * This method will deleteNetworkConfigServerDetails
	 * 
	 * @param networkConfigDetailId
	 * @return boolean
	 */
	@Override
	public boolean deleteNetworkConfigServerDetails(int networkConfigDetailId) {
		logger.info("NetworkConfigRepositoryImpl.deleteNetworkConfigServerDetails() networkConfigDetailId: " + networkConfigDetailId);
		boolean status = false;
		try {
			Query query = entityManager.createQuery("DELETE FROM  NetworkConfigDetailsEntity WHERE id=:id");
			query.setParameter("id", networkConfigDetailId);
			query.executeUpdate();
			status = true;
		} catch (Exception e) {
			logger.error("Exception NetworkConfigRepositoryImpl.deleteNetworkConfigServerDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	/**
	 * This method will getNetworkConfigDetailsForExPort
	 * 
	 * @param networkConfigModel
	 * @return List<NetworkConfigEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<NetworkConfigEntity> getNetworkConfigDetailsForExPort(NetworkConfigModel networkConfigModel) {
		List<NetworkConfigEntity> totCommList = new ArrayList<>();
		try {
			//total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteriaTotList.createAlias("neTypeEntity", "neTypeEntity");
			criteriaTotList.createAlias("loginTypeEntity", "loginTypeEntity");
			
			criteriaTotList.setFetchMode("neDetails", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();
			
			if (networkConfigModel != null) {
				if (CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity()) && CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity().getId())) {
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", networkConfigModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity().getId())) {
					Criterion eventneType = Restrictions.eq("neTypeEntity.id", networkConfigModel.getNeTypeEntity().getId());
					conjunction.add(eventneType);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeName())) {
					Criterion eventNeName = Restrictions.ilike("neName", networkConfigModel.getNeName(),MatchMode.ANYWHERE);
					conjunction.add(eventNeName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity().getId())) {
					criteriaTotList.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventVersion = Restrictions.eq("neVersionEntity.id", networkConfigModel.getNeVersionEntity().getId());
					conjunction.add(eventVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
					criteriaTotList.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventNeVersion = Restrictions.ilike("neVersionEntity.neVersion", networkConfigModel.getNeVersion(),MatchMode.ANYWHERE);
					conjunction.add(eventNeVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getStatus())) {
					Criterion eventStatus = Restrictions.ilike("status", networkConfigModel.getStatus());
					conjunction.add(eventStatus);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity().getId())) {
					Criterion eventLoginType = Restrictions.eq("loginTypeEntity.id", networkConfigModel.getLoginTypeEntity().getId());
					conjunction.add(eventLoginType);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeMarket())) {
					Criterion eventNeMarket = Restrictions.ilike("neMarket", networkConfigModel.getNeMarket(),MatchMode.ANYWHERE);
					conjunction.add(eventNeMarket);
				}
			}
			criteriaTotList.add(conjunction);
			
			criteriaTotList.addOrder(Order.desc("creationDate"));
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigRepositoryImpl.getNetworkConfigDetailsForExPort(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totCommList;
	}

	/**
	 * This method will getNetworkConfigDetails
	 * 
	 * @param networkConfigModel
	 * @return List<NetworkConfigEntity>
	 */
	@Override
	public List<NetworkConfigEntity> getNetworkConfigDetails(NetworkConfigModel networkConfigModel) {
		List<NetworkConfigEntity> networkConfigEntities = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = builder.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(networkConfigModel) && CommonUtil.isValidObject(networkConfigModel.getId())) {
				query.where(builder.equal(root.get("id"), networkConfigModel.getId()));
			}
			if (CommonUtil.isValidObject(networkConfigModel) && CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity())) {
				query.where(builder.equal(root.get("neTypeEntity"), networkConfigModel.getNeTypeEntity().getId()));
			}
			TypedQuery<NetworkConfigEntity> queryResult = entityManager.createQuery(query);
			networkConfigEntities = queryResult.getResultList();
			for(NetworkConfigEntity configEntity: networkConfigEntities){
				configEntity.getNeDetails().sort((p1,p2) -> Integer.valueOf(p1.getStep()).compareTo(Integer.valueOf(p2.getStep())));
			}
		} catch (Exception e) {
			logger.info("Exception in NetworkConfigRepositoryImpl.getNetworkConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigEntities;
	}

	@Override
	public List<NetworkConfigEntity> getNetworkConfigList(int programId) {
		List<NetworkConfigEntity> networkConfigEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = cb.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("programDetailsEntity"), programId));

			TypedQuery<NetworkConfigEntity> typedQuery = entityManager.createQuery(query);
			networkConfigEntityList = (List<NetworkConfigEntity>) typedQuery.getResultList();

		} catch (Exception e) {
			logger.error("Exception getNetworkConfigList() in NetworkConfigRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigEntityList;
	}
	
	@Override
	public Map<String, Object> getNetworkConfigDetailsPage(NetworkConfigModel networkConfigModel,
			List<CustomerDetailsEntity> programNamesList) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<NetworkConfigEntity> networkConfigList = new ArrayList<>();
		List<NetworkConfigEntity> totCommList = new ArrayList<>();
		List<Integer> programs = null;
		double result = 0;
		int paginationNumber = 0;
		try {
			if (CommonUtil.isValidObject(programNamesList) ){
				programs =  programNamesList.stream().map(x->x.getId()).collect(Collectors.toList());
			}
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.createAlias("neTypeEntity", "neTypeEntity");
			criteria.createAlias("loginTypeEntity", "loginTypeEntity");
			criteria.setFetchMode("neDetails", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();

			if (networkConfigModel != null) {
				if (CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity()) && CommonUtil.isValidObject(networkConfigModel.getProgramDetailsEntity().getId())) {
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", networkConfigModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeTypeEntity().getId())) {
					Criterion eventneType = Restrictions.eq("neTypeEntity.id", networkConfigModel.getNeTypeEntity().getId());
					conjunction.add(eventneType);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeName())) {
					Criterion eventNeName = Restrictions.ilike("neName", networkConfigModel.getNeName(),MatchMode.ANYWHERE);
					conjunction.add(eventNeName);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity()) && CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity().getId())) {
					criteria.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventVersion = Restrictions.eq("neVersionEntity.id", networkConfigModel.getNeVersionEntity().getId());
					conjunction.add(eventVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
					criteria.createAlias("neVersionEntity", "neVersionEntity");
					Criterion eventNeVersion = Restrictions.ilike("neVersionEntity.neVersion", networkConfigModel.getNeVersion(),MatchMode.ANYWHERE);
					conjunction.add(eventNeVersion);
				}
				if (StringUtils.isNotEmpty(networkConfigModel.getStatus())) {
					Criterion eventStatus = Restrictions.ilike("status", networkConfigModel.getStatus());
					conjunction.add(eventStatus);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity()) && CommonUtil.isValidObject(networkConfigModel.getLoginTypeEntity().getId())) {
					Criterion eventLoginType = Restrictions.eq("loginTypeEntity.id", networkConfigModel.getLoginTypeEntity().getId());
					conjunction.add(eventLoginType);
				}
				if (CommonUtil.isValidObject(networkConfigModel.getNeMarket())) {
					Criterion eventNeMarket = Restrictions.ilike("neMarket", networkConfigModel.getNeMarket(),MatchMode.ANYWHERE);
					conjunction.add(eventNeMarket);
				}
			}
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				conjunction.add(eventRestrictedPrograms);
			}
			criteria.add(conjunction);
//			criteria.setFirstResult((page - 1) * count);
//			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			//pagination List
			networkConfigList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			for(NetworkConfigEntity configEntity: networkConfigList){
				configEntity.getNeDetails().sort((p1,p2) -> Integer.valueOf(p1.getStep()).compareTo(Integer.valueOf(p2.getStep())));
			}
			//total List
			Conjunction totalListConjunction = Restrictions.conjunction();
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaTotList.createAlias("neTypeEntity", "neTypeEntity");
			criteriaTotList.createAlias("loginTypeEntity", "loginTypeEntity");
			if (networkConfigModel != null && StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
			criteriaTotList.createAlias("neVersionEntity", "neVersionEntity");
			}
			criteriaTotList.setFetchMode("neDetails", FetchMode.LAZY);
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				totalListConjunction.add(eventRestrictedPrograms);
				criteriaTotList.add(totalListConjunction);
			}
			criteriaTotList.addOrder(Order.desc("creationDate"));
			totCommList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> neNameList = totCommList.stream().map(x->x.getNeName()).sorted().collect(Collectors.toSet());
			Set<String> neMarketList = totCommList.stream().filter(x->CommonUtil.isValidObject(x.getNeMarket()) &&  x.getNeMarket().length() > 0).map(x->x.getNeMarket()).sorted().collect(Collectors.toSet());
			// Tot count Details
			Conjunction totalCountConjunction = Restrictions.conjunction();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.createAlias("neTypeEntity", "neTypeEntity");
			criteriaCount.createAlias("loginTypeEntity", "loginTypeEntity");
			if (networkConfigModel != null && StringUtils.isNotEmpty(networkConfigModel.getNeVersion())) {
			criteriaCount.createAlias("neVersionEntity", "neVersionEntity");
			}
			criteriaCount.setFetchMode("neDetails", FetchMode.LAZY);
			if (CommonUtil.isValidObject(programs)) {
				Criterion eventRestrictedPrograms = Restrictions.in("programDetailsEntity.id", programs);
				totalCountConjunction.add(eventRestrictedPrograms);
				criteriaCount.add(totalCountConjunction);
			}
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
//			Long totCount = (Long) criteriaCount.uniqueResult();
//			double size = totCount;
//			result = Math.ceil(size / count);
//			paginationNumber = (int) result;
			objMap.put("neMarketList", neMarketList);
			objMap.put("neNameList", neNameList);
			objMap.put("networkConfigList", networkConfigList);
			objMap.put("totList", totCommList);
			objMap.put("pageCount", paginationNumber);
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigRepositoryImpl.getnetworkConfigDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

}
