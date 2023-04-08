package com.smart.rct.common.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.repository.DuoGeneralConfigRepository;

@Repository
@EnableTransactionManagement
@Transactional
public class DuoGeneralConfigRepositoryImpl implements DuoGeneralConfigRepository{
	
	final static Logger logger = LoggerFactory.getLogger(DuoGeneralConfigRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<DuoGeneralConfigEntity> getDuoGeneralConfigList() {
		List<DuoGeneralConfigEntity> duoGeneralConfigEntity = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(DuoGeneralConfigEntity.class);
			duoGeneralConfigEntity = criteriaTotList
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info(
					"Exception in DuoGeneralConfigRepositoryImpl.getDuoGeneralConfigList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return duoGeneralConfigEntity;
	}
	
	@Override
	public boolean saveDuoGeneralConfigEntity(DuoGeneralConfigEntity duoGeneralConfigEntity) {
		boolean status = false;
		try {
			entityManager.merge(duoGeneralConfigEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in DuoGeneralConfigRepositoryImpl.saveSchedulingTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
}
