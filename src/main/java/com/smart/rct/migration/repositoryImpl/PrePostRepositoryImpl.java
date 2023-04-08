package com.smart.rct.migration.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.migration.entity.PrePostMapping;
import com.smart.rct.migration.entity.RetTestEntity;
import com.smart.rct.migration.repository.PrePostRepository;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
@Transactional
@Repository
public class PrePostRepositoryImpl implements PrePostRepository {
static final Logger logger = LoggerFactory.getLogger(RetRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public boolean savePrePostDeatil(PrePostMapping objInfo) {
		boolean status = false;
		try {
			entityManager.merge(objInfo);
			status = true;

		} catch (Exception e) {
			logger.error("Exception savePrePostDeatil() in PrePostRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	@Override
	public List<PrePostMapping> getAuditPrePostEntity(String neId,int programId) {

		List<PrePostMapping> auditPrePostDetailsEntityList = null;
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<PrePostMapping> query = cb.createQuery(PrePostMapping.class);
			Root<PrePostMapping> root = query.from(PrePostMapping.class);

			query.select(root);
			query.where(cb.and(cb.equal(root.get("neId"), neId),
					cb.equal(root.get("programId"), programId)));
			TypedQuery<PrePostMapping> queryResult = entityManager.createQuery(query);
			auditPrePostDetailsEntityList = queryResult.getResultList();

		
			/*CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<PrePostMapping> query = cb.createQuery(PrePostMapping.class);
			Root<PrePostMapping> root = query.from(PrePostMapping.class);

			query.select(root);
			TypedQuery<PrePostMapping> typedQuery = entityManager.createQuery(query);
			auditPrePostDetailsEntityList = typedQuery.getResultList();
			//query.where(cb.equal(root.get("neId"), neId));
			query.where(cb.and(cb.equal(root.get("neId"), neId),
					cb.equal(root.get("programId"), programId)));
			
			TypedQuery<PrePostMapping> queryResult = entityManager.createQuery(query);
			auditPrePostDetailsEntityList = queryResult.getResultList();*/

		} catch (Exception e) {
			logger.error("Exception in  getAuditPrePostEntity() in  PrePostRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditPrePostDetailsEntityList;
	}
	
	@Override
	public boolean deletePrePostDeatil(String neId, int programId) {
		boolean deletePrePostStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from PrePostMapping WHERE neId = :neId AND programId = :programId");
			query.setParameter("neId", neId);
			query.setParameter("programId", programId);
			query.executeUpdate();
			deletePrePostStatus = true;
		} catch (Exception e) {
			logger.error("Exception in deletePrePostDeatil() in  PrePostRepositoryImpl: " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deletePrePostStatus;
	}
	

}
