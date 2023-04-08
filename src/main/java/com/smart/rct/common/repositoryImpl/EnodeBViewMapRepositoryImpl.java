package com.smart.rct.common.repositoryImpl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.models.TrackLatiTudeModel;
import com.smart.rct.common.repository.EnodeBViewMapRepository;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;

@Repository
@Transactional
public class EnodeBViewMapRepositoryImpl implements EnodeBViewMapRepository {

	final static Logger logger = LoggerFactory.getLogger(EnodeBViewMapRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * This api get the getMapEnodeBDetails
	 * 
	 * @param customerId
	 * @return Map<String, TrackLatiTudeModel>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, TrackLatiTudeModel> getMapEnodeBDetails(Integer customerId) {
		List<CiqUploadAuditTrailDetEntity> auditTrailEntityList = null;
		Map<String, TrackLatiTudeModel> objMap = new LinkedHashMap<>();
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("customerEntity", "customerEntity");
			criteria.add(Restrictions.eq("customerEntity.id", Integer.valueOf(customerId)));
			criteria.addOrder(Order.desc("creationDate"));
			auditTrailEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			LinkedHashSet<String> objFileNames = auditTrailEntityList.stream().map(X -> X.getCiqFileName())
					.collect(Collectors.toCollection(LinkedHashSet::new));
			if (objFileNames != null && objFileNames.size() > 0) {
				for (String fileNameData : objFileNames) {
					List<CIQDetailsModel> resultList = null;
					Query query = new Query(Criteria.where("fileName").is(fileNameData));
					resultList = mongoTemplate.find(query, CIQDetailsModel.class, fileNameData);
					if (resultList != null && resultList.size() > 0) {
						for (CIQDetailsModel objCIQDetailsModelLocla : resultList) {
							if (!objMap.containsKey(objCIQDetailsModelLocla.geteNBName())) {
								/*
								 * LinkedHashMap<String, String> objMapDeta =
								 * objCIQDetailsModelLocla.getCiqMap(); TrackLatiTudeModel objTrackLatiTudeModel
								 * = new TrackLatiTudeModel();
								 * 
								 * if (objMapDeta.containsKey("Latitude") &&
								 * objMapDeta.containsKey("Longitude"))
								 * objTrackLatiTudeModel.setLatitude(objMapDeta.get("Latitude"));
								 * objTrackLatiTudeModel.setLongitude(objMapDeta.get("Longitude"));
								 * 
								 * objMap.put(objCIQDetailsModelLocla.geteNBName(), objTrackLatiTudeModel);
								 */
							}
						}

					}

				}

			}
		} catch (Exception e) {
			logger.error("Exception  getEnbTableDetails() in  EnodeBViewMapRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
}
