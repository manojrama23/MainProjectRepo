package com.smart.rct.scheduling.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;
import com.smart.rct.scheduling.repository.SchedulingRepository;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class SchedulingRepositoryImpl implements SchedulingRepository{
	
	final static Logger logger = LoggerFactory.getLogger(SchedulingRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public boolean createSchedule(SchedulingEntity schedulingEntity) {
		
			boolean status = false;
			try {
				entityManager.persist(schedulingEntity);
				status = true;
			} catch (Exception e) {
				logger.error("Exception  createSchedule() in  SchedulingRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
				e.printStackTrace();
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return status;
	}

	@Override
	public boolean duplicateScheduling(SchedulingModel objSchedulingModel) {
		boolean status = false;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<SchedulingEntity> query = builder.createQuery(SchedulingEntity.class);
			Root<SchedulingEntity> root = query.from(SchedulingEntity.class);
			query.select(root);
			if (objSchedulingModel.getId() != null && objSchedulingModel.getId() != 0) {
				query.where(builder.and(builder.equal(root.get("lsmName"), objSchedulingModel.getLsmName()),
						builder.equal(root.get("lsmVersion"), objSchedulingModel.getLsmVersion()),
						builder.equal(root.get("networkType"), objSchedulingModel.getNetworkType()),
						builder.notEqual(root.get("id"), objSchedulingModel.getId())));

			} else {
				query.where(builder.and(builder.equal(root.get("lsmName"), objSchedulingModel.getLsmName()),
						builder.equal(root.get("lsmVersion"), objSchedulingModel.getLsmVersion()),
						builder.equal(root.get("networkType"), objSchedulingModel.getNetworkType())));
			}
			TypedQuery<SchedulingEntity> queryResult = entityManager.createQuery(query);
			List<SchedulingEntity> objSchedulingEntityList = queryResult.getResultList();

			if (CommonUtil.isValidObject(objSchedulingEntityList) && objSchedulingEntityList.size() > 0) {
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception duplicateScheduling() in SchedulingRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

}
