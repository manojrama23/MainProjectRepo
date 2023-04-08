package com.smart.rct.common.repositoryImpl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.common.repository.SiteDetailsReportRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.PartialSaveSiteReportEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.util.DateUtil;
@Repository
@EnableTransactionManagement
@Transactional()
public class SiteDetailsReportRepositoryImpl implements SiteDetailsReportRepository{
	final static Logger logger = LoggerFactory.getLogger(SiteDetailsReportRepositoryImpl.class);
	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public SiteDataEntity getSiteDetailsById(int siteDataId) {
		return entityManager.find(SiteDataEntity.class, siteDataId);
	}
	@Override
	public PartialSaveSiteReportEntity getSiteDetailsForSavefile(String neId) {
		return entityManager.find(PartialSaveSiteReportEntity.class, neId);
		/*List<SiteDataEntity> siteDataEntityList = null;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteDataEntity.class);
			criteria.add(Restrictions.eq("reportType", Constants.REPORT_TYPE_SITE));
			criteria.add(Restrictions.eq("neId", neId));
			criteria.add(Restrictions.ilike("fileName", "%Partial%"));
			criteria.addOrder(Order.desc("packedDate"));
			
			siteDataEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return  siteDataEntityList;
	*/
		
	}
	
	@Override
	public List<SiteDataEntity> getHistorySiteDetails(String neId) {
		List<SiteDataEntity> siteDataEntityList = null;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteDataEntity.class);
			criteria.add(Restrictions.eq("reportType", Constants.REPORT_TYPE_SITE));
			criteria.add(Restrictions.eq("neId", neId));
			criteria.add(Restrictions.not(Restrictions.ilike("fileName", "%Partial%")));
			criteria.addOrder(Order.desc("packedDate"));
			/*Conjunction conjunction = Restrictions.conjunction();
			Criterion criterion = Restrictions.eq("neId", neId);
			conjunction.add(criterion);
			criteria.add(criterion);*/
			siteDataEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return siteDataEntityList;
	}
	
	
	
	
	
	@Override
	public List<SiteDataEntity> getDonldSiteDetails(int programDetailsEntity, Date fromDate, Date toDate)  {
		List<SiteDataEntity> siteDataEntityList = null;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteDataEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("reportType", Constants.REPORT_TYPE_SITE));
			criteria.add(Restrictions.eq("programDetailsEntity.id", programDetailsEntity));
			Criterion searchStartDate=Restrictions.ge("packedDate",fromDate);
			Criterion searchEndDate = Restrictions.le("packedDate",toDate);
			criteria.add(searchStartDate);
			criteria.add(searchEndDate);
			criteria.addOrder(Order.desc("packedDate"));
			
			/*Conjunction conjunction = Restrictions.conjunction();
			Criterion criterion = Restrictions.eq("neId", neId);
			conjunction.add(criterion);*/
			//criteria.add(Criterion);
			siteDataEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return siteDataEntityList;
	}
	@Override
	public SiteDataEntity saveSiteDataEntity(SiteDataEntity siteDataEntity) {
		SiteDataEntity objSiteDataEntity=null;
		boolean status = false;
		try {
			objSiteDataEntity=entityManager.merge(siteDataEntity);
			status = true;
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objSiteDataEntity;
	}
	
	@Override
	public PartialSaveSiteReportEntity savePartialSiteDataEntity(PartialSaveSiteReportEntity partialSaveSiteReportEntity) {
		PartialSaveSiteReportEntity objSiteDataEntity=null;
		boolean status = false;
		try {
			objSiteDataEntity=entityManager.merge(partialSaveSiteReportEntity);
			status = true;
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objSiteDataEntity;
	}
	@Override
	public SiteDataEntity getSiteDataEntity(Integer runTestId) {
		SiteDataEntity siteDataEntity = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteDataEntity.class);
			conjunction.add(Restrictions.eq("id", runTestId));
			criteria.add(conjunction);
			siteDataEntity = (SiteDataEntity) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error(" getGenerateInfoAuditEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return siteDataEntity;
	}
	@Override
	public boolean updateSiteDataEntity(SiteDataEntity siteDataEntity) {
		boolean status = false;
		try {
			entityManager.merge(siteDataEntity);
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception updateGeneratedFileDetails() in GenerateRepositoryImpl :"+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	public boolean updateSiteReportOv(SiteReportOVEntity siteReportOVEntity) {
		boolean status = false;
		try {
			siteReportOVEntity.setCreationDate(new Date());
			entityManager.merge(siteReportOVEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRunTest() RunTestRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public List<SiteReportOVEntity> getSiteReportOVEntity(Integer runTestId) {

		List<SiteReportOVEntity> runTestResultEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(SiteReportOVEntity.class);
			conjunction.add(Restrictions.eq("siteDataEntity.id", runTestId));
			//conjunction.add((Criterion) Order.desc("creationDate"));prsors
			criteria.add(conjunction);
			criteria.addOrder(Order.desc("creationDate"));
			
			runTestResultEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestResultEntityList;

	}

}