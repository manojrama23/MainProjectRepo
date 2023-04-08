package com.smart.rct.premigration.repositoryImpl;

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
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.models.GenerateConstantsModel;
import com.smart.rct.premigration.repository.GrowConstantsRepository;
import com.smart.rct.util.CommonUtil;
@Repository
@Transactional
public class GrowConstantsRepositoryImpl implements GrowConstantsRepository{
	
	final static Logger logger = LoggerFactory.getLogger(CIQUploadRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	
	/**
	 * this method will return CiqList
	 * 
	 * @param user
	 * @param customerId
	 * @return Set<String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<GrowConstantsEntity> getGrowConstantsDetails() {
		List<GrowConstantsEntity> objEntityList=null;
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(GrowConstantsEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			
		
			objEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();			
		
		} catch (Exception e) {
			logger.error("Exception  getGrowConstantsDetails() in  GrowConstantsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objEntityList;
	}


	@Override
	public List<GrowConstantsEntity> getGrowConstantsDetails(GenerateConstantsModel generateConstantsModel) {
		logger.info("GrowConstantsRepositoryImpl.getGrowConstantsDetails() called.. ");
		List<GrowConstantsEntity> growConstantsEntities = null;
		try {
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GrowConstantsEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			
			if (CommonUtil.isValidObject(generateConstantsModel) && CommonUtil.isValidObject(generateConstantsModel.getId())) {
				Criterion id = Restrictions.eq("id", generateConstantsModel.getId());
				conjunction.add(id);
			}
			if (CommonUtil.isValidObject(generateConstantsModel) && CommonUtil.isValidObject(generateConstantsModel.getProgramDetailsEntity())) {
				Criterion programDetailsEntity = Restrictions.eq("programDetailsEntity.id",generateConstantsModel.getProgramDetailsEntity().getId());
				conjunction.add(programDetailsEntity);
			}
			if(CommonUtil.isValidObject(generateConstantsModel) && CommonUtil.isValidObject(generateConstantsModel.getLabel())){
				Criterion label = Restrictions.ilike("label", generateConstantsModel.getLabel());
				conjunction.add(label);
			}
			criteria.add(conjunction);
			logger.info("GrowConstantsRepositoryImpl.getGrowConstantsDetails() criteria: "+criteria.toString());
			growConstantsEntities = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			
		} catch (Exception e) {
			logger.info("Exception in GrowConstantsRepositoryImpl.getGrowConstantsDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return growConstantsEntities;
	}


	

}
