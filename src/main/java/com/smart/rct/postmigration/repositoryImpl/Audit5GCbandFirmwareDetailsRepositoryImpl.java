package com.smart.rct.postmigration.repositoryImpl;

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

import com.smart.rct.postmigration.entity.Audit5GCbandFirmwareDetailsEntity;
import com.smart.rct.postmigration.repository.Audit5GCbandFirmwareDetailsRepository;

@Transactional
@Repository
public class Audit5GCbandFirmwareDetailsRepositoryImpl implements Audit5GCbandFirmwareDetailsRepository{
	
	static final Logger logger = LoggerFactory.getLogger(Audit5GCbandFirmwareDetailsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Audit5GCbandFirmwareDetailsEntity> getAuditFirmwareDetailsEntityList(String firmwareName, String relVersion, String neVersion, String prodCode) {

		List<Audit5GCbandFirmwareDetailsEntity> auditHardwareDetailsEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Audit5GCbandFirmwareDetailsEntity> query = cb.createQuery(Audit5GCbandFirmwareDetailsEntity.class);
			Root<Audit5GCbandFirmwareDetailsEntity> root = query.from(Audit5GCbandFirmwareDetailsEntity.class);

			query.select(root);
			//TypedQuery<Audit5GCbandFirmwareDetailsEntity> typedQuery = entityManager.createQuery(query);
			//auditHardwareDetailsEntityList = typedQuery.getResultList();
			query.where(cb.like(cb.lower(root.get("firmwareName")), "%" + firmwareName.toLowerCase() + "%"),
					cb.equal(root.get("relVersion"), relVersion),
					cb.equal(root.get("neVersion"), neVersion),
					cb.equal(root.get("prodCode"), prodCode));
			TypedQuery<Audit5GCbandFirmwareDetailsEntity> queryResult = entityManager.createQuery(query);
			auditHardwareDetailsEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditFirmwareDetailsEntityList() in  Audit5GCbandFirmwareDetailsRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditHardwareDetailsEntityList;
	}
}
