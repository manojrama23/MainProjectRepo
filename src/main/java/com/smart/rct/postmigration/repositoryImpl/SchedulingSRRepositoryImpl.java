package com.smart.rct.postmigration.repositoryImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.models.MarketModelDetails;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.postmigration.repository.SchedulingSRRepository;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Repository
@Transactional
public class SchedulingSRRepositoryImpl implements SchedulingSRRepository {

	final static Logger logger = LoggerFactory.getLogger(SchedulingSRRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * This method will saveVerizonSchedulingDetails
	 * 
	 * @param schedulingVerizonEntity
	 * @return boolean
	 */
	@Override
	public boolean saveVerizonSchedulingDetails(SchedulingVerizonEntity schedulingVerizonEntity) {
		boolean status = false;
		try {
			entityManager.merge(schedulingVerizonEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.saveVerizonSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will saveSprintSchedulingDetails
	 * 
	 * @param schedulingSprintEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSprintSchedulingDetails(SchedulingSprintEntity schedulingSprintEntity) {
		boolean status = false;
		try {
			entityManager.merge(schedulingSprintEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.saveSprintSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will deleteVerizonDetails
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteVerizonDetails(int id) {
		boolean status = false;
		try {
			entityManager.remove(getVerizonDetailsById(id));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in SchedulingRepositoryImpl.deleteVerizonDetails():"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public SchedulingVerizonEntity getVerizonDetailsById(int id) {
		return entityManager.find(SchedulingVerizonEntity.class, id);
	}

	/**
	 * This method will deleteSprintDetails
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteSprintDetails(int id) {
		boolean status = false;
		try {
			entityManager.remove(getSprintDetailsById(id));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in SchedulingRepositoryImpl.deleteSprintDetails():"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public SchedulingSprintEntity getSprintDetailsById(int id) {
		return entityManager.find(SchedulingSprintEntity.class, id);
	}

	/**
	 * This method will getVerizonSchedulingDetails
	 * 
	 * @param idschedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getVerizonSchedulingDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId) {
		List<SchedulingVerizonEntity> schedulingVerizonEntityList = null;
		List<SchedulingVerizonEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList = new ArrayList<>();
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingVerizonModel != null) {
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingVerizonModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingVerizonModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbName())) {
					conjunction.add(Restrictions.like("enbName", schedulingVerizonModel.getEnbName(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getCarriers())) {
					conjunction.add(Restrictions.like("carriers", schedulingVerizonModel.getCarriers(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getAlarmPresent())) {
					conjunction.add(Restrictions.like("alarmPresent", schedulingVerizonModel.getAlarmPresent(), MatchMode.ANYWHERE));
				}
				if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())
						&& schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate", DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
					conjunction.add(forecastEndDate);
				}else if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate", DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
				}else if (schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastEndDate);
				}
				
				if (schedulingVerizonModel.getCompDate() != null && !"".equals(schedulingVerizonModel.getCompDate())
						&& schedulingVerizonModel.getCompEndDate() != null && !"".equals(schedulingVerizonModel.getCompEndDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_));
					Criterion compEndDate = Restrictions.le("compDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
					conjunction.add(compEndDate);
				}else if (schedulingVerizonModel.getCompDate() != null && !"".equals(schedulingVerizonModel.getCompDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
				}else if (schedulingVerizonModel.getCompEndDate() != null && !"".equals(schedulingVerizonModel.getCompEndDate())) {
					Criterion compEndDate = Restrictions.le("compDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			logger.info("SchedulingSRRepositoryImpl.getVerizonSchedulingDetails() criteria: "+criteria.toString());
			schedulingVerizonEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> enodebId = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbId()))
					.map(x -> x.getEnbId()).sorted().collect(Collectors.toSet());
			Set<String> enodebName = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbName()))
					.map(x -> x.getEnbName()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));

			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));

			MarketModelDetails comboBoxListDetails = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});

			objMap.put("username", username);
			objMap.put("market", market);
			objMap.put("enodebId", enodebId);
			objMap.put("comboBoxListDetails", comboBoxListDetails);
			objMap.put("enodebName", enodebName);
			objMap.put("paginationcount", pagecount);
			objMap.put("schedulingVerizonEntityList", schedulingVerizonEntityList);
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getSprintSchedulingDetails
	 * 
	 * @param idschedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getSprintSchedulingDetails(SchedulingSprintModel schedulingSprintModel, int page,
			int count, int customerId) {
		List<SchedulingSprintEntity> schedulingSprintEntityList = null;
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SchedulingSprintEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList1 = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingSprintModel != null) {
				if (StringUtils.isNotEmpty(schedulingSprintModel.getRegion())) {
					conjunction.add(Restrictions.like("region", schedulingSprintModel.getRegion(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingSprintModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getCascade())) {
					conjunction.add(Restrictions.like("cascade", schedulingSprintModel.getCascade(), MatchMode.ANYWHERE));
				}
				if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())
						&& schedulingSprintModel.getEndDate() != null
						&& !"".equals(schedulingSprintModel.getEndDate())) {
					Criterion startDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					Criterion endDate = Restrictions.le("startDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startDate);
					conjunction.add(endDate);
				}else if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())) {
					Criterion startDate = Restrictions.ge("startDate", DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startDate);
				}else if (schedulingSprintModel.getEndDate() != null && !"".equals(schedulingSprintModel.getEndDate())) {
					Criterion endDate = Restrictions.le("startDate", DateUtil.stringToDateEndTime(schedulingSprintModel.getEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(endDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			logger.info("SchedulingSRRepositoryImpl.getSprintSchedulingDetails() criteria: "+criteria.toString());
			schedulingSprintEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> region = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getRegion()))
					.map(x -> x.getRegion()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList1 = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList1.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));
			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));
			JsonObject objregionData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_REGION"));
			JsonObject objFeregionData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FEREGION"));
			JsonObject objFenightData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FE_NIGHT"));
			JsonObject objFedayData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FE_DAY"));
			MarketModelDetails marketDetailsList = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails regionDetailsList = mapper.readValue(objregionData.get("RegionData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails feregionDetailsList = mapper.readValue(objFeregionData.get("FeregionData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails fenightDetailsList = mapper.readValue(objFenightData.get("FeNightData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails fedayDetailsList = mapper.readValue(objFedayData.get("FeDayData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			objMap.put("marketDetailsList", marketDetailsList);
			objMap.put("regionDetailsList", regionDetailsList);
			objMap.put("feregionDetailsList", feregionDetailsList);
			objMap.put("fenightDetailsList", fenightDetailsList);
			objMap.put("fedayDetailsList", fedayDetailsList);
			objMap.put("market", market);
			objMap.put("region", region);
			objMap.put("username", username);
			objMap.put("paginationcount", pagecount);
			objMap.put("schedulingSprintEntityList", schedulingSprintEntityList);
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getSchedulingDetailsToExPort
	 * 
	 * @param idschedulingVerizonModel
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedulingDetailsToExPort(SchedulingVerizonModel schedulingVerizonModel) {
		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingVerizonModel != null) {
				
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getStatus())) {
					conjunction.add(Restrictions.like("status", schedulingVerizonModel.getStatus(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingVerizonModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingVerizonModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbName())) {
					conjunction.add(Restrictions.like("enbName", schedulingVerizonModel.getEnbName(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getCarriers())) {
					conjunction.add(Restrictions.like("carriers", schedulingVerizonModel.getCarriers(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getAlarmPresent())) {
					conjunction.add(Restrictions.like("alarmPresent", schedulingVerizonModel.getAlarmPresent(), MatchMode.ANYWHERE));
				}
				if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())
						&& schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate", DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
					conjunction.add(forecastEndDate);
				}else if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate", DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
				}else if (schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastEndDate);
				}
				
				if (schedulingVerizonModel.getCompDate() != null && !"".equals(schedulingVerizonModel.getCompDate())
						&& schedulingVerizonModel.getCompEndDate() != null && !"".equals(schedulingVerizonModel.getCompEndDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_));
					Criterion compEndDate = Restrictions.le("compDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
					conjunction.add(compEndDate);
				}else if (schedulingVerizonModel.getCompDate() != null && !"".equals(schedulingVerizonModel.getCompDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",DateUtil.stringToDate(schedulingVerizonModel.getCompDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
				}else if (schedulingVerizonModel.getCompEndDate() != null && !"".equals(schedulingVerizonModel.getCompEndDate())) {
					Criterion compEndDate = Restrictions.le("compDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compEndDate);
				}
			}
			criteriaTotList.add(conjunction);
			
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingDetailsToExPort() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingDetailsToExPort
	 * 
	 * @param schedulingSprintModel
	 * @return List<schedulingSprintModel>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedulingDetailsToExPort(SchedulingSprintModel schedulingSprintModel) {
		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingSprintModel != null) {
				if (StringUtils.isNotEmpty(schedulingSprintModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingSprintModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEnbId())) {
					conjunction.add(Restrictions.like("region", schedulingSprintModel.getRegion(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingSprintModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getCascade())) {
					conjunction.add(Restrictions.like("cascade", schedulingSprintModel.getCascade(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getType())) {
					conjunction.add(Restrictions.like("type", schedulingSprintModel.getType(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEodType())) {
					conjunction.add(Restrictions.like("type", schedulingSprintModel.getEodType(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getTypeOne())) {
					conjunction.add(Restrictions.like("typeOne", schedulingSprintModel.getTypeOne(), MatchMode.ANYWHERE));
				}
				if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())
						&& schedulingSprintModel.getCompEndDate() != null
						&& !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion searchCompStartDate = Restrictions.ge("compDate",
							DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					Criterion searchCompEndDate = Restrictions.le("compDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchCompStartDate);
					conjunction.add(searchCompEndDate);
				}else if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())) {
					Criterion startDate = Restrictions.ge("compDate", DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startDate);
				}else if (schedulingSprintModel.getCompEndDate() != null && !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion endDate = Restrictions.le("compDate", DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(endDate);
				}
				if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())
						&& schedulingSprintModel.getEndDate() != null
						&& !"".equals(schedulingSprintModel.getEndDate())) {
					Criterion startDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					Criterion endDate = Restrictions.le("startDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startDate);
					conjunction.add(endDate);
				}else if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())) {
					Criterion startDate = Restrictions.ge("startDate", DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startDate);
				}else if (schedulingSprintModel.getEndDate() != null && !"".equals(schedulingSprintModel.getEndDate())) {
					Criterion endDate = Restrictions.le("startDate", DateUtil.stringToDateEndTime(schedulingSprintModel.getEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(endDate);
				}
			}
			criteriaTotList.add(conjunction);
			
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingDetailsToExPort() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getVerizonOverallReportsDetails
	 * 
	 * @param schedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getVerizonOverallReportsDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId) {
		List<SchedulingVerizonEntity> overallVerizonEntityList = null;
		List<SchedulingVerizonEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();

		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingVerizonModel != null) {

				if (StringUtils.isNotEmpty(schedulingVerizonModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingVerizonModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingVerizonModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbName())) {
					conjunction.add(Restrictions.like("enbName", schedulingVerizonModel.getEnbName(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getStatus())) {
					conjunction.add(Restrictions.like("status", schedulingVerizonModel.getStatus(), MatchMode.ANYWHERE));
				}
				
				if (schedulingVerizonModel.getForecastStartDate() != null
						&& !"".equals(schedulingVerizonModel.getForecastStartDate())
						&& schedulingVerizonModel.getForecastEndDate() != null
						&& !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate",
							DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil
							.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
					conjunction.add(forecastEndDate);
				}else if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate", DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
				}else if (schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.addOrder(Order.desc("id"));
			logger.info("SchedulingSRRepositoryImpl.getVerizonOverallReportsDetails() criteria: "+criteria.toString());
			overallVerizonEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> enodebId = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbId()))
					.map(x -> x.getEnbId()).sorted().collect(Collectors.toSet());
			Set<String> enodebName = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbName()))
					.map(x -> x.getEnbName()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());
			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));

			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));

			MarketModelDetails comboBoxListDetails = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			objMap.put("comboBoxListDetails", comboBoxListDetails);
			objMap.put("username", username);
			objMap.put("market", market);
			objMap.put("enodebId", enodebId);
			objMap.put("enodebName", enodebName);
			objMap.put("paginationcount", pagecount);
			objMap.put("overallVerizonEntityList", overallVerizonEntityList);

		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getSprintOverallReportsDetails
	 * 
	 * @param schedulingSprintModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getSprintOverallReportsDetails(SchedulingSprintModel schedulingSprintModel, int page,
			int count, int customerId) {
		List<SchedulingSprintEntity> overallSprintEntityList = null;
		List<SchedulingSprintEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();

		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingSprintModel != null) {
				if (StringUtils.isNotEmpty(schedulingSprintModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingSprintModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getRegion())) {
					conjunction.add(Restrictions.like("region", schedulingSprintModel.getRegion(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingSprintModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getCascade())) {
					conjunction.add(Restrictions.like("cascade", schedulingSprintModel.getCascade(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getType())) {
					conjunction.add(Restrictions.like("type", schedulingSprintModel.getType(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEodType())) {
					conjunction.add(Restrictions.like("type", schedulingSprintModel.getEodType(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getTypeOne())) {
					conjunction.add(Restrictions.like("typeOne", schedulingSprintModel.getTypeOne(), MatchMode.ANYWHERE));
				}
				if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())
						&& schedulingSprintModel.getCompEndDate() != null
						&& !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",
							DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					Criterion compEndDate = Restrictions.le("compDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
					conjunction.add(compEndDate);
				}else if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())) {
					Criterion compStartDate = Restrictions.ge("compDate",
							DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compStartDate);
				}else if (schedulingSprintModel.getCompEndDate() != null && !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion compEndDate = Restrictions.le("compDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(compEndDate);
				}
				if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())
						&& schedulingSprintModel.getStartEndDate() != null
						&& !"".equals(schedulingSprintModel.getStartEndDate())) {
					Criterion startStartDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					Criterion startEndDate = Restrictions.le("startDate", DateUtil
							.stringToDateEndTime(schedulingSprintModel.getStartEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startStartDate);
					conjunction.add(startEndDate);
				}else if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())) {
					Criterion startStartDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startStartDate);
				}else if (schedulingSprintModel.getStartEndDate() != null && !"".equals(schedulingSprintModel.getStartEndDate())) {
					Criterion startEndDate = Restrictions.le("startDate", DateUtil
							.stringToDateEndTime(schedulingSprintModel.getStartEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(startEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			logger.info("SchedulingSRRepositoryImpl.getSprintOverallReportsDetails() criteria: "+criteria.toString());
			overallSprintEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> enodebId = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbId()))
					.map(x -> x.getEnbId()).sorted().collect(Collectors.toSet());
			Set<String> region = searchList.stream().filter(x -> CommonUtil.isValidObjects(x.getRegion()))
					.map(x -> x.getRegion()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));
			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));
			JsonObject objregionData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_REGION"));
			JsonObject objFeregionData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FEREGION"));
			JsonObject objFenightData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FE_NIGHT"));
			JsonObject objFedayData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_FE_DAY"));
			MarketModelDetails marketDetailsList = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails regionDetailsList = mapper.readValue(objregionData.get("RegionData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails feregionDetailsList = mapper.readValue(objFeregionData.get("FeregionData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails fenightDetailsList = mapper.readValue(objFenightData.get("FeNightData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			MarketModelDetails fedayDetailsList = mapper.readValue(objFedayData.get("FeDayData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			objMap.put("marketDetailsList", marketDetailsList);
			objMap.put("regionDetailsList", regionDetailsList);
			objMap.put("feregionDetailsList", feregionDetailsList);
			objMap.put("fenightDetailsList", fenightDetailsList);
			objMap.put("fedayDetailsList", fedayDetailsList);
			objMap.put("username", username);
			objMap.put("paginationcount", pagecount);
			objMap.put("market", market);
			objMap.put("region", region);
			objMap.put("enodebId", enodebId);
			objMap.put("overallSprintEntityList", overallSprintEntityList);

		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getVerizonEodDetails
	 * 
	 * @param schedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getVerizonEodDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId) {
		List<SchedulingVerizonEntity> schedulingVerizonEntityList = null;
		List<SchedulingVerizonEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList = new ArrayList<>();
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingVerizonModel != null) {
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingVerizonModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbId())) {
					conjunction.add(Restrictions.like("enbId", schedulingVerizonModel.getEnbId(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getEnbName())) {
					conjunction.add(Restrictions.like("enbName", schedulingVerizonModel.getEnbName(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getCarriers())) {
					conjunction.add(Restrictions.like("carriers", schedulingVerizonModel.getCarriers(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingVerizonModel.getAlarmPresent())) {
					conjunction.add(Restrictions.like("alarmPresent", schedulingVerizonModel.getAlarmPresent(), MatchMode.ANYWHERE));
				}
				if (schedulingVerizonModel.getForecastStartDate() != null
						&& !"".equals(schedulingVerizonModel.getForecastStartDate())
						&& schedulingVerizonModel.getForecastEndDate() != null
						&& !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate",
							DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil
							.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
					conjunction.add(forecastEndDate);
				}else if (schedulingVerizonModel.getForecastStartDate() != null && !"".equals(schedulingVerizonModel.getForecastStartDate())) {
					Criterion forecastStartDate = Restrictions.ge("forecastStartDate",
							DateUtil.stringToDate(schedulingVerizonModel.getForecastStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastStartDate);
				}else if (schedulingVerizonModel.getForecastEndDate() != null && !"".equals(schedulingVerizonModel.getForecastEndDate())) {
					Criterion forecastEndDate = Restrictions.le("forecastStartDate", DateUtil
							.stringToDateEndTime(schedulingVerizonModel.getForecastEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(forecastEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			schedulingVerizonEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> enodebId = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbId()))
					.map(x -> x.getEnbId()).sorted().collect(Collectors.toSet());
			Set<String> enodebName = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getEnbName()))
					.map(x -> x.getEnbName()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));

			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));

			MarketModelDetails comboBoxListDetails = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});

			objMap.put("comboBoxListDetails", comboBoxListDetails);
			objMap.put("market", market);
			objMap.put("enodebId", enodebId);
			objMap.put("enodebName", enodebName);
			objMap.put("username", username);
			objMap.put("paginationcount", pagecount);
			objMap.put("schedulingVerizonEntityList", schedulingVerizonEntityList);
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getVerizonEodDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getSprintEodDetails
	 * 
	 * @param schedulingSprintModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getSprintEodDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId) {
		List<SchedulingSprintEntity> schedulingSprintEntityList = null;
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SchedulingSprintEntity> searchList = new ArrayList<>();
		List<UserDetailsEntity> searchUserList = new ArrayList<>();
		List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (schedulingSprintModel != null) {
				if (StringUtils.isNotEmpty(schedulingSprintModel.getMarket())) {
					conjunction.add(Restrictions.like("market", schedulingSprintModel.getMarket(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getEnbId())) {
					conjunction.add(Restrictions.like("region", schedulingSprintModel.getRegion(), MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(schedulingSprintModel.getCascade())) {
					conjunction.add(Restrictions.like("cascade", schedulingSprintModel.getCascade(), MatchMode.ANYWHERE));
				}
				if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())
						&& schedulingSprintModel.getCompEndDate() != null
						&& !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion searchCompStartDate = Restrictions.ge("compDate",
							DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					Criterion searchCompEndDate = Restrictions.le("compDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchCompStartDate);
					conjunction.add(searchCompEndDate);
				}else if (schedulingSprintModel.getCompDate() != null && !"".equals(schedulingSprintModel.getCompDate())) {
					Criterion searchCompStartDate = Restrictions.ge("compDate",
							DateUtil.stringToDate(schedulingSprintModel.getCompDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchCompStartDate);
				}else if (schedulingSprintModel.getCompEndDate() != null && !"".equals(schedulingSprintModel.getCompEndDate())) {
					Criterion searchCompEndDate = Restrictions.le("compDate",
							DateUtil.stringToDateEndTime(schedulingSprintModel.getCompEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchCompEndDate);
				}
				if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())
						&& schedulingSprintModel.getStartEndDate() != null
						&& !"".equals(schedulingSprintModel.getStartEndDate())) {
					Criterion searchAmStartDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					Criterion searchAmEndDate = Restrictions.le("startDate", DateUtil
							.stringToDateEndTime(schedulingSprintModel.getStartEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchAmStartDate);
					conjunction.add(searchAmEndDate);
				}else if (schedulingSprintModel.getStartDate() != null && !"".equals(schedulingSprintModel.getStartDate())) {
					Criterion searchAmStartDate = Restrictions.ge("startDate",
							DateUtil.stringToDate(schedulingSprintModel.getStartDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchAmStartDate);
				}else if (schedulingSprintModel.getStartEndDate() != null && !"".equals(schedulingSprintModel.getStartEndDate())) {
					Criterion searchAmEndDate = Restrictions.le("startDate", DateUtil
							.stringToDateEndTime(schedulingSprintModel.getStartEndDate(), Constants.DD_MM_YYYY_));
					conjunction.add(searchAmEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			schedulingSprintEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> market = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getMarket()))
					.map(x -> x.getMarket()).sorted().collect(Collectors.toSet());
			Set<String> region = searchList.stream().filter(x -> CommonUtil.isValidObject(x.getRegion()))
					.map(x -> x.getRegion()).sorted().collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingSprintEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			org.hibernate.Criteria criteriaUserList = entityManager.unwrap(Session.class)
					.createCriteria(UserDetailsEntity.class);
			criteriaUserList.createAlias("customerEntity", "customerEntity");
			criteriaUserList.add(Restrictions.eq("customerEntity.id", customerId));
			searchUserList = criteriaUserList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> username = searchUserList.stream().map(x -> x.getUserName()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
			criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerId));
			searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			Map<String, String> comboBoxList = searchComboList.stream().collect(Collectors
					.toMap(SchedulingReportsTemplateEntity::getLabel, SchedulingReportsTemplateEntity::getValue));

			ObjectMapper mapper = new ObjectMapper();

			JsonObject objData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_MARKET"));

			MarketModelDetails comboBoxListDetails = mapper.readValue(objData.get("MarketData").toString(),
					new TypeReference<MarketModelDetails>() {
					});
			JsonObject objregionData = CommonUtil.parseRequestDataToJson(comboBoxList.get("SCHEDULING_REGION"));
			MarketModelDetails regionDetailsList = mapper.readValue(objregionData.get("RegionData").toString(),
					new TypeReference<MarketModelDetails>() {
					});

			objMap.put("regionDetailsList", regionDetailsList);
			objMap.put("comboBoxListDetails", comboBoxListDetails);
			objMap.put("market", market);
			objMap.put("region", region);
			objMap.put("username", username);
			objMap.put("paginationcount", pagecount);
			objMap.put("schedulingSprintEntityList", schedulingSprintEntityList);
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSprintEodDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will getCustomerIdList
	 * 
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getCustomerIdList() {
		List<CustomerEntity> customerEntity = null;
		Map<String, Object> objMap = new HashMap<String, Object>();
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CustomerEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", Constants.ACTIVE));
			conjunction.add(Restrictions.ne("id", 1));
			criteria.add(conjunction);
			customerEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			objMap.put("customerlist", customerEntity);
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getCustomerIdList(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
	
	/**
	 * This method will saveVerizonSchedulingDetails
	 * 
	 * @param schedulingVerizonEntity
	 * @return boolean
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getNeDetailsForMap(SchedulingSRModel schedulingSRModel,List<CustomerEntity> customerEntities) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> customerList =  new ArrayList<Map<String, Object>>();
		Map<String, Object> customerMap = new HashMap<String, Object>();
		Map<String,Date> neList = new HashMap<String,Date>();
		Set<String> allMarkets = new HashSet<String>();
		try {
			//List<Integer> customerIds = customerEntities.stream().map(e->e.getId()).collect(Collectors.toList()); 
			for(CustomerEntity customerEntity: customerEntities){
				
				if(CommonUtil.isValidObject(customerEntity) && Constants.VZN_CUSTOMER_ID == customerEntity.getId() 
						&& (!CommonUtil.isValidObject(schedulingSRModel.getCustomerId()) || schedulingSRModel.getCustomerId() == Constants.VZN_CUSTOMER_ID)){
					Map<String,Date> neVerizonList = new HashMap<String,Date>();
					Criteria verizonCriteria = entityManager.unwrap(Session.class).createCriteria(SchedulingVerizonEntity.class);
					Conjunction verizonConjunction = Restrictions.conjunction();
					if(CommonUtil.isValidObject(schedulingSRModel)){
						if (schedulingSRModel.getSearchStartDate() != null && !"".equals(schedulingSRModel.getSearchStartDate())
								&& schedulingSRModel.getSearchEndDate() != null && !"".equals(schedulingSRModel.getSearchEndDate())) {
							Criterion searchStartDate = Restrictions.ge("compDate",
									DateUtil.stringToDate(schedulingSRModel.getSearchStartDate(), Constants.MM_DD_YYYY));
							Criterion  searchEndDate = Restrictions.le("compDate", DateUtil
									.stringToDateEndTime(schedulingSRModel.getSearchEndDate(), Constants.MM_DD_YYYY));
							verizonConjunction.add( searchStartDate);
							verizonConjunction.add( searchEndDate);
						}else if (schedulingSRModel.getSearchStartDate() != null && !"".equals(schedulingSRModel.getSearchStartDate())) {
							Criterion searchStartDate = Restrictions.ge("compDate",
									DateUtil.stringToDate(schedulingSRModel.getSearchStartDate(), Constants.MM_DD_YYYY));
							verizonConjunction.add( searchStartDate);
						}else if (schedulingSRModel.getSearchEndDate() != null && !"".equals(schedulingSRModel.getSearchEndDate())) {
							Criterion  searchEndDate = Restrictions.le("compDate", DateUtil
									.stringToDateEndTime(schedulingSRModel.getSearchEndDate(), Constants.MM_DD_YYYY));
							verizonConjunction.add( searchEndDate);
						}
						if(StringUtils.isNotEmpty(schedulingSRModel.getMarket())){
							verizonConjunction.add(Restrictions.eq("market", schedulingSRModel.getMarket()));
						}
					}
					verizonCriteria.add(verizonConjunction);
					logger.info("SchedulingRepositoryImpl.getNeDetailsForMap() verizonCriteria: "+verizonCriteria.toString());
					List<SchedulingVerizonEntity> vznList= verizonCriteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
					/*neVerizonList = vznList.stream().filter(e->neVerizonList.containsKey(e.getEnbId())).collect(
			                Collectors.toMap(SchedulingVerizonEntity::getEnbId, SchedulingVerizonEntity::getCompDate));*/
					for(SchedulingVerizonEntity schedulingVerizonEntity: vznList){
						if(!neVerizonList.containsKey(schedulingVerizonEntity.getEnbId())){
							neVerizonList.put(schedulingVerizonEntity.getEnbId(), schedulingVerizonEntity.getCompDate());
						}
					}
			        
					if(CommonUtil.isValidObject(neVerizonList) && neVerizonList.size()>0){
						logger.info("SchedulingRepositoryImpl.getNeDetailsForMap() neVerizonList size: "+neVerizonList.size());
						neList.putAll(neVerizonList);
					}
				}
				
				if(CommonUtil.isValidObject(customerEntity) && Constants.SPT_CUSTOMER_ID == customerEntity.getId() 
						&& (!CommonUtil.isValidObject(schedulingSRModel.getCustomerId()) || schedulingSRModel.getCustomerId() == Constants.SPT_CUSTOMER_ID)){
					Map<String,Date> neSprintList = new HashMap<String,Date>();
					Criteria sprintCriteria = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
					Conjunction sprintConjunction = Restrictions.conjunction();
					if(CommonUtil.isValidObject(schedulingSRModel)){
						if (schedulingSRModel.getSearchStartDate() != null && !"".equals(schedulingSRModel.getSearchStartDate())
								&& schedulingSRModel.getSearchEndDate() != null && !"".equals(schedulingSRModel.getSearchEndDate())) {
							Criterion searchStartDate = Restrictions.ge("compDate",
									DateUtil.stringToDate(schedulingSRModel.getSearchStartDate(), Constants.MM_DD_YYYY));
							Criterion  searchEndDate = Restrictions.le("compDate", DateUtil
									.stringToDateEndTime(schedulingSRModel.getSearchEndDate(), Constants.MM_DD_YYYY));
							sprintConjunction.add( searchStartDate);
							sprintConjunction.add( searchEndDate);
						}else if (schedulingSRModel.getSearchStartDate() != null && !"".equals(schedulingSRModel.getSearchStartDate())) {
							Criterion searchStartDate = Restrictions.ge("compDate",
									DateUtil.stringToDate(schedulingSRModel.getSearchStartDate(), Constants.MM_DD_YYYY));
							sprintConjunction.add( searchStartDate);
						}else if (schedulingSRModel.getSearchEndDate() != null && !"".equals(schedulingSRModel.getSearchEndDate())) {
							Criterion  searchEndDate = Restrictions.le("compDate", DateUtil
									.stringToDateEndTime(schedulingSRModel.getSearchEndDate(), Constants.MM_DD_YYYY));
							sprintConjunction.add( searchEndDate);
						}
						if(StringUtils.isNotEmpty(schedulingSRModel.getMarket())){
							sprintConjunction.add(Restrictions.eq("market", schedulingSRModel.getMarket()));
						}
					}
					sprintCriteria.add(sprintConjunction);
					logger.info("SchedulingRepositoryImpl.getNeDetailsForMap() sprintCriteria: "+sprintCriteria.toString());
					
					List<SchedulingSprintEntity> sptList= sprintCriteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
					/*neSprintList = sptList.stream().collect(
			                Collectors.toMap(SchedulingSprintEntity::getEnbId, SchedulingSprintEntity::getCompDate));*/
					for(SchedulingSprintEntity schedulingSprintEntity: sptList){
						if(!neSprintList.containsKey(schedulingSprintEntity.getEnbId())){
							neSprintList.put(schedulingSprintEntity.getEnbId(), schedulingSprintEntity.getCompDate());
						}
					}
					if(CommonUtil.isValidObject(neSprintList) && neSprintList.size()>0){
						logger.info("SchedulingRepositoryImpl.getNeDetailsForMap() neSprintList size: "+neSprintList.size());
						neList.putAll(neSprintList);
					}
				}
				
				List<SchedulingReportsTemplateEntity> searchComboList = new ArrayList<>();
				Criteria criteriaComboBoxList = entityManager.unwrap(Session.class)
						.createCriteria(SchedulingReportsTemplateEntity.class);
				criteriaComboBoxList.createAlias("customerEntity", "customerEntity");
				criteriaComboBoxList.add(Restrictions.eq("customerEntity.id", customerEntity.getId()));
				criteriaComboBoxList.add(Restrictions.eq("label", Constants.SCHEDULING_MARKET));
				searchComboList = criteriaComboBoxList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
						.list();
				Set<String> markets = new HashSet<String>();
				
				for(SchedulingReportsTemplateEntity schedulingReportsTemplateEntity: searchComboList){
					String  marketStr = schedulingReportsTemplateEntity.getValue();
					if(StringUtils.isNotEmpty(marketStr)){
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(marketStr);
						json =  (JSONObject) json.get("MarketData");
						JSONArray market = (JSONArray) json.get("market");
						if (market != null && market.size() > 0) {
							for (int i = 0; i < market.size(); i++) {
							markets.add((String) market.get(i));
							}
						}
					}
				}
				customerMap = new HashMap<String, Object>();
				customerMap.put("id", customerEntity.getId());
				customerMap.put("customerName", customerEntity.getCustomerName());
				customerMap.put("market", markets);
				allMarkets.addAll(markets);
				customerList.add(customerMap);
			}
			resultMap.put("market", allMarkets);
			resultMap.put("customerList", customerList);
			resultMap.put("neList", neList);
			logger.info("SchedulingRepositoryImpl.getNeDetailsForMap() neList size: "+neList.size());
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getNeDetailsForMap(): "+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return resultMap;
	}

}
