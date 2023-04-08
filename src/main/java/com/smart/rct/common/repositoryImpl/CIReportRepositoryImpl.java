package com.smart.rct.common.repositoryImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.repository.CIReportRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Repository
@EnableTransactionManagement
@Transactional()
public class CIReportRepositoryImpl implements CIReportRepository {

	final static Logger logger = LoggerFactory.getLogger(CIReportRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * This method will getSchedulingVerizonEntityListForCIReports
	 * 
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForCIReports() {

		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListCurrentWeekForCIReports
	 * 
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListCurrentWeekForCIReports() {

		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Date endDate = new Date();
			String toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListForCIReportsPeriod
	 * 
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListForCIReportsPeriod(String fromDate, String toDate) {
		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedVerListForCIReportsPeriod() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListForCIReportsDayWise
	 * 
	 * @param date
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListForCIReportsDayWise(String date) {

		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			Criterion eventDate = Restrictions.eq("compDate", DateUtil.stringToDate(date, Constants.MM_DD_YYYY));
			conjunction.add(eventDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedVerListForCIReportsDayWise() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListCurrentWeekForVlsmLsmCIReports
	 * 
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListCurrentWeekForVlsmLsmCIReports() {

		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Date endDate = new Date();
			String toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = 7;
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Rolled Back"));
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListPeriodForVlsmLsmCIReports
	 * 
	 * @param fromDate,toDate
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListPeriodForVlsmLsmCIReports(String fromDate, String toDate) {
		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Rolled Back"));
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedVerListVlsmLsmCIReportsDailyWise
	 * 
	 * @param date
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedVerListVlsmLsmCIReportsDailyWise(String date) {

		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Rolled Back"));
			Criterion eventDate = Restrictions.eq("compDate", DateUtil.stringToDate(date, Constants.MM_DD_YYYY));
			conjunction.add(eventDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedVerListPeriodForVlsmLsmCIReportsDailyWise() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingSprintEntityListForCIReports
	 * 
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIReports() {

		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingSprintEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedsprListCurrentWeekForCIReports
	 * 
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedsprListCurrentWeekForCIReports() {
		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			Date endDate = new Date();
			String toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = 7;
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedsprListCurrentWeekForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedsprListCurrentWeekForCIReports
	 * 
	 * @param fromDate,toDate
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedsprListPeriodForCIReports(String fromDate, String toDate) {

		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedsprListCurrentWeekForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedsprListDayWiseForCIReports
	 * 
	 * @param date
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedsprListDayWiseForCIReports(String date) {

		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			// conjunction.add(Restrictions.eq("status", "Migrated").ignoreCase());
			conjunction.add(Restrictions.in("status", "Migrated", "Cancelled", "In Progress"));
			Criterion eventDate = Restrictions.eq("compDate", DateUtil.stringToDate(date, Constants.MM_DD_YYYY));
			conjunction.add(eventDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedsprListDayWiseForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingVerizonEntityListForMonthly
	 * 
	 * @return List<SchedulingVerizonEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForMonthly() {
		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(
					Restrictions.in("status", "Migrated", "Cancelled", "Rolled Back", "Conversion", "Carrier Add"));
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForMonthly() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingSprintEntityListForCIMonthly
	 * 
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIMonthly() {

		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Cancelled"));
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingSprintEntityListForCIMonthly() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingSprintEntityListForCIReportsMonday
	 * 
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<SchedulingSprintEntity> getSchedulingSprintEntityListForCIReportsMonday() {
		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "In Progress"));
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingSprintEntityListForCIReportsMonday() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedsprListCurrentWeekForCIReportsMonday
	 * 
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedsprListCurrentWeekForCIReportsMonday() {
		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			Date endDate = new Date();
			String toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = 7;
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Cancelled"));
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedsprListCurrentWeekForCIReportsMonday() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedsprListPeriodForCIReportsMonday
	 * 
	 * @param fromDate,toDate
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingSprintEntity> getSchedsprListPeriodForCIReportsMonday(String fromDate, String toDate) {
		List<SchedulingSprintEntity> totList = new ArrayList<>();
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(SchedulingSprintEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Cancelled"));
			Criterion eventstartDate = Restrictions.ge("compDate",
					DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
			Criterion eventEndDate = Restrictions.le("compDate",
					DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
			conjunction.add(eventstartDate);
			conjunction.add(eventEndDate);
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedsprListPeriodForCIReportsMonday() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

	/**
	 * This method will getSchedulingVerizonEntityListForCIReportsFriday
	 * 
	 * @param fromDate,toDate
	 * @return List<SchedulingSprintEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingVerizonEntity> getSchedulingVerizonEntityListForCIReportsFriday() {
		List<SchedulingVerizonEntity> totList = new ArrayList<>();
		try {
			// total List
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingVerizonEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.in("status", "Migrated", "Conversion", "Carrier Add"));
			criteriaTotList.add(conjunction);
			totList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in SchedulingRepositoryImpl.getSchedulingVerizonEntityListForCIReports() :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return totList;
	}

}
