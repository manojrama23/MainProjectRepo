package com.smart.rct.migration.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.criterion.Order;
//import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.repository.RunTestResultRepository;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;

@Repository
@Transactional
public class RunTestResultRepositoryImpl implements RunTestResultRepository {

	final static Logger logger = LoggerFactory.getLogger(RunTestRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public boolean createRunTestResult(RunTestResultEntity runTestResultEntity) {
		boolean status = false;
		try {
			entityManager.persist(runTestResultEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in  createRunTestResult() in  RunTestResultRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public List<RunTestResultEntity> getRunTestResultList(Integer runTestId) {

		List<RunTestResultEntity> runTestResultEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestResultEntity.class);
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			criteria.add(conjunction);
			
			runTestResultEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestResultEntityList;

	}
	
	@Override
	public List<OvTestResultEntity> getOVRunTestResultList(Integer runTestId) {

		List<OvTestResultEntity> runTestResultEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(OvTestResultEntity.class);
			conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
			//conjunction.add((Criterion) Order.desc("creationDate"));
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
	
	
	//start ov premigration result window 
	@Override
	public List<PremigrationOvUpadteEntity> getOVRunTestResultListpre(Integer runTestId) {

		List<PremigrationOvUpadteEntity> runTestResultEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(PremigrationOvUpadteEntity.class);
			conjunction.add(Restrictions.eq("generateAudEntity.id", runTestId));
			//conjunction.add((Criterion) Order.desc("creationDate"));
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
	
	//end ov premigration result window 
	
	
	@Override
	public List<RunTestResultEntity> getRunTestResultEntityList(int runTestId,int scriptId) {

		List<RunTestResultEntity> runTestResultEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestResultEntity> query = cb.createQuery(RunTestResultEntity.class);
			Root<RunTestResultEntity> root = query.from(RunTestResultEntity.class);

			query.select(root);
			TypedQuery<RunTestResultEntity> typedQuery = entityManager.createQuery(query);
			runTestResultEntityList = typedQuery.getResultList();
			query.where(cb.and(cb.equal(root.get("runTestEntity"), runTestId),
					cb.equal(root.get("uploadFileEntity"), scriptId)));
			TypedQuery<RunTestResultEntity> queryResult = entityManager.createQuery(query);
			runTestResultEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestResultEntityList;

	}
	
	
	@Override
	public CustomerDetailsEntity getCustomerDetailsEntity(int programId) {
		CustomerDetailsEntity customerDetailsEntity = null;
		try {
			customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
		} catch (Exception e) {
			logger.error("Exception getCustomerDetailsEntity() :" + ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntity;
	}
	
	@Override
	public String getScriptOutput(Integer runTestId, Integer useCaseId, Integer scriptId) {
		List<RunTestResultEntity> runTestResultEntityList = null;
		String output=null;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestResultEntity> query = builder.createQuery(RunTestResultEntity.class);
			Root<RunTestResultEntity> root = query.from(RunTestResultEntity.class);
			query.select(root);
			query.where(builder.and(builder.equal(root.get("runTestEntity"), runTestId),
					builder.equal(root.get("useCaseBuilderEntity"), useCaseId), builder.equal(root.get("uploadFileEntity"), scriptId)));

			TypedQuery<RunTestResultEntity> queryResult = entityManager.createQuery(query);
			runTestResultEntityList = queryResult.getResultList();
			if(runTestResultEntityList != null)
			{
				for(RunTestResultEntity entity : runTestResultEntityList)
				{
//					output = entity.getScriptOutput();
					output = entity.getCurrentScriptOutput();
					if(!output.isEmpty()) {
					break;
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception getScriptOutput() in RunTestResultRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return output;
	}

	@Override
	public List<RunTestResultEntity> getPreviousRunTestResult(Integer runTestResultId) {
		List<RunTestResultEntity> runTestResultEntity = null;
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestResultEntity> query = cb.createQuery(RunTestResultEntity.class);
			Root<RunTestResultEntity> root = query.from(RunTestResultEntity.class);

			query.select(root);
			TypedQuery<RunTestResultEntity> typedQuery = entityManager.createQuery(query);
			query.where(cb.equal(root.get("runTestId"), runTestResultId));
			TypedQuery<RunTestResultEntity> queryResult = entityManager.createQuery(query);
			runTestResultEntity = queryResult.getResultList();
		}catch (Exception e) {
			logger.error("Exception getScriptOutput() in RunTEstResultRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestResultEntity;
		
	}

	@Override
		public boolean updateValueToPrevious(RunTestResultEntity runTestResultEntity) {
			boolean status = false;
			try {
				entityManager.merge(runTestResultEntity);
				status = true;
			} catch (Exception e) {
				logger.error("Exception in  updateValueToPrevious() in  RunTestResultRepositoryImpl:"
						+ ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return status;
		}

}