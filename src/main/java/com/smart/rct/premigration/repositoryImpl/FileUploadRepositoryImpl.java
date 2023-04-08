package com.smart.rct.premigration.repositoryImpl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.serviceImpl.Counter;
import com.smart.rct.premigration.serviceImpl.EnodebDetails;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Repository
@Transactional
public class FileUploadRepositoryImpl implements FileUploadRepository {

	final static Logger logger = LoggerFactory.getLogger(FileUploadRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * this method will create CiqAudit
	 * 
	 * @param objCiqUploadAuditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean createCiqAudit(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity) {
		boolean status = false;
		CiqUploadAuditTrailDetEntity oldCiqUploadAuditTrailDetEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CiqUploadAuditTrailDetEntity> query = builder.createQuery(CiqUploadAuditTrailDetEntity.class);
			Root<CiqUploadAuditTrailDetEntity> root = query.from(CiqUploadAuditTrailDetEntity.class);
			query.select(root);
			query.where(builder.and(
					builder.equal(root.get("programDetailsEntity"),
							objCiqUploadAuditTrailEntity.getProgramDetailsEntity().getId()),
					builder.equal(root.get("ciqFileName"), objCiqUploadAuditTrailEntity.getCiqFileName())));

			TypedQuery<CiqUploadAuditTrailDetEntity> queryResult = entityManager.createQuery(query);
			oldCiqUploadAuditTrailDetEntity = (CiqUploadAuditTrailDetEntity) queryResult.getResultList().stream()
					.findFirst().orElse(null);
			if (oldCiqUploadAuditTrailDetEntity != null) {
				oldCiqUploadAuditTrailDetEntity.setCreationDate(objCiqUploadAuditTrailEntity.getCreationDate());
				oldCiqUploadAuditTrailDetEntity.setRemarks(objCiqUploadAuditTrailEntity.getRemarks());
				oldCiqUploadAuditTrailDetEntity.setFileSourceType(objCiqUploadAuditTrailEntity.getFileSourceType());
				oldCiqUploadAuditTrailDetEntity.setUploadBy(objCiqUploadAuditTrailEntity.getUploadBy());
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getChecklistFileName())) {
					oldCiqUploadAuditTrailDetEntity
							.setChecklistFileName(objCiqUploadAuditTrailEntity.getChecklistFileName());
				}
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getCiqFileName())) {
					oldCiqUploadAuditTrailDetEntity.setCiqFileName(objCiqUploadAuditTrailEntity.getCiqFileName());
				}
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getScriptFileName())) {
					oldCiqUploadAuditTrailDetEntity.setScriptFileName(objCiqUploadAuditTrailEntity.getScriptFileName());
				}
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getChecklistFilePath())) {
					oldCiqUploadAuditTrailDetEntity
							.setChecklistFilePath(objCiqUploadAuditTrailEntity.getChecklistFilePath());
				}
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getCiqFilePath())) {
					oldCiqUploadAuditTrailDetEntity.setCiqFilePath(objCiqUploadAuditTrailEntity.getCiqFilePath());
				}
				if (CommonUtil.isValidObject(objCiqUploadAuditTrailEntity.getScriptFilePath())) {
					oldCiqUploadAuditTrailDetEntity.setScriptFilePath(objCiqUploadAuditTrailEntity.getScriptFilePath());
				}

				entityManager.merge(oldCiqUploadAuditTrailDetEntity);
				status = true;
			} else {
				entityManager.merge(objCiqUploadAuditTrailEntity);
				status = true;
			}

		} catch (Exception e) {
			logger.error(
					"Exception  createCiqAudit() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return CiqAuditDetails List
	 * 
	 * @param page,
	 *            count
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getCiqAuditDetails(int page, int count, Integer customerId) {

		logger.info("AuditTrailRepositoryImpl.getAuditDetails() page: " + page + ", count: " + count);
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<CiqUploadAuditTrailDetEntity> auditTrailEntityList = null;
		List<CiqUploadAuditTrailDetModel> ciqList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("customerEntity", "customerEntity");
			criteria.add(Restrictions.eq("customerEntity.id", customerId));

			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			auditTrailEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			if (auditTrailEntityList != null && auditTrailEntityList.size() > 0) {
				for (CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailDetEntity : auditTrailEntityList) {

					CiqUploadAuditTrailDetModel objModelCiqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
					objModelCiqUploadAuditTrailDetModel.setCreationDate(CommonUtil.dateToString(
							objCiqUploadAuditTrailDetEntity.getCreationDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					objModelCiqUploadAuditTrailDetModel
							.setCiqFileName(objCiqUploadAuditTrailDetEntity.getCiqFileName());
					objModelCiqUploadAuditTrailDetModel.setId(objCiqUploadAuditTrailDetEntity.getId());
					objModelCiqUploadAuditTrailDetModel.setRemarks(objCiqUploadAuditTrailDetEntity.getRemarks());
					objModelCiqUploadAuditTrailDetModel.setUploadBy(objCiqUploadAuditTrailDetEntity.getUploadBy());
					ciqList.add(objModelCiqUploadAuditTrailDetModel);
				}
			}

			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteriaCount.createAlias("customerEntity", "customerEntity");
			criteriaCount.add(Restrictions.eq("customerEntity.id", customerId));
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("count", pagecount);
			objMap.put("ciqList", ciqList);
		} catch (Exception e) {
			logger.error(
					"Exception getAuditDetails() in AuditTrailRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * this method will update CiqAuditDetails
	 * 
	 * @param objCiqUploadAuditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean updateCiqAuditDetails(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity) {
		boolean status = false;
		try {
			entityManager.merge(objCiqUploadAuditTrailEntity);
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception updateCiqAuditDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This api deletes CIQ file
	 * 
	 * @param fileId,
	 * @return boolean
	 */
	@Override
	public boolean deleteCiq(int fileId) {
		boolean status = false;
		try {
			CiqUploadAuditTrailDetEntity entity = getCiqAuditById(fileId);
			if (entity != null) {
				entityManager.remove(entity);
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception deleteCiq() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will return CiqAudit By Id
	 * 
	 * @param articleId
	 * @return CiqUploadAuditTrailEntity
	 */
	public CiqUploadAuditTrailDetEntity getCiqAuditById(int articleId) {
		return entityManager.find(CiqUploadAuditTrailDetEntity.class, articleId);
	}

	/**
	 * this method will return CiqDetails By Filename
	 * 
	 * @param fileName
	 * @return boolean
	 */
	public boolean deleteCiqDetailsByFilename(String fileName) {
		boolean status = false;
		logger.info("deleteCiqDetailsByFilename() fileName: " + fileName);
		try {
			/*
			 * Query query = new Query();
			 * query.addCriteria(Criteria.where("fileName").is(fileName));
			 * mongoTemplate.findAllAndRemove(query, CIQDetailsModel.class, "CIQ_DETAILS");
			 */
			mongoTemplate.dropCollection(fileName);
			mongoTemplate.findAllAndRemove(query(where("_id").is(fileName)), Counter.class, "counters");
			status = true;
		} catch (Exception e) {
			logger.error("Exception deleteCiqDetailsByFilename() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	/**
	 * this method will return CiqList
	 * 
	 * @param user
	 * @param customerId
	 * @return Set<String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate) {
		List<CiqUploadAuditTrailDetEntity> ciqList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.addOrder(Order.desc("ciqFileName"));
			Conjunction conjunction = Restrictions.conjunction();

			if (StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
				Criterion eventstartDate = Restrictions.ge("creationDate",
						DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
				Criterion eventEndDate = Restrictions.le("creationDate",
						DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
				conjunction.add(eventstartDate);
				conjunction.add(eventEndDate);
			}
			criteria.add(Restrictions.eq("programDetailsEntity.id", Integer.parseInt(programId)));
			/*
			 * Projection commonProjection = Projections.projectionList()
			 * .add(Projections.property("ciqFileName"), "ciqFileName")
			 * .add(Projections.property("uploadBy"), "uploadBy")
			 * .add(Projections.property("programDetailsEntity.programName"), "programName")
			 * .add(Projections.property("remarks"), "remarks")
			 * .add(Projections.property("uploadBy"), "uploadBy")
			 * .add(Projections.property("ciqVersion"), "ciqVersion")
			 * .add(Projections.sqlProjection(
			 * "DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate", new
			 * String[] { "creationDate" }, new Type[] { new StringType() }))
			 * 
			 * .add(Projections.property("id"), "id");
			 * 
			 * criteria.setProjection(commonProjection);
			 */
			criteria.add(conjunction);

			ciqList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ciqList;
	}

	/**
	 * This method will deleteCiqRowDetails
	 * 
	 * @param id,fileName
	 * @return boolean
	 */

	@Override
	public boolean deleteCiqRowDetails(int id, String fileName) {
		// TODO Auto-generated method stub

		boolean status = false;
		try {
			mongoTemplate.findAndRemove(query(where("id").is(id)), CIQDetailsModel.class, fileName);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  deleteCiqRowDetails() in  FileUploadRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param id,
	 *            filename
	 * @return Map<String, Object>
	 */
	@Override
	public List<Map<String, String>> getEnbDetails(String id, String fileName, String dbcollectionFileName) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Set<Map<String, String>> objSet = new LinkedHashSet<>();

		Map<String, String> objTestMap = new HashMap<>();
		Query query = new Query(Criteria.where("fileName").is(fileName));
		try {
			query.fields().include("eNBName");
			query.fields().include("eNBId");
			query.fields().include("siteName");
			List<EnodebDetails> result = mongoTemplate.find(query, EnodebDetails.class, dbcollectionFileName);

			for (EnodebDetails enbData : result) {
				Map<String, String> objMap = new HashMap<>();

				String enBID = enbData.geteNBId();
				String enbName = enbData.geteNBName();
				String siteName = enbData.getSiteName();
				// String site_name = enbData.getCiqMap().get("VZW Site Name").toString();
				/*
				 * if(objMap.containsKey("eNBId") && objMap.get("eNBId").equalsIgnoreCase(enBID)
				 * )
				 */
				if (StringUtils.isNotEmpty(enBID) && StringUtils.isNotEmpty(enbName)) {
					if (!objTestMap.containsKey(enBID)) {
						objMap.put("eNBId", enBID);
						objMap.put("eNBName", enbName);
						objMap.put("siteName", siteName);
						// objMap.put("siteName", site_name);
						objTestMap.put(enBID, enbName);
						resultList.add(objMap);
					}

				}

			}
		} catch (Exception e) {
			logger.error("Exception CiqDetails() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<Map<String, String>> getEnbDetails5G(String id, String fileName, String dbcollectionFileName) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Set<Map<String, String>> objSet = new LinkedHashSet<>();
		ObjectMapper mapper = new ObjectMapper();
		List<String> sheetAliasNameList = null;
		Map<String, String> objTestMap = new HashMap<>();
		ProgramTemplateEntity objProgramTemplateEntity = getProgramTemplate(Integer.parseInt(id),
				Constants.CIQ_VALIDATE_TEMPLATE);

		try {
			if (objProgramTemplateEntity != null && StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {

				JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());
				System.out.println(objData.get("sheetAliasNameList"));

				sheetAliasNameList = mapper.readValue(objData.get("sheetAliasNameList").toString(),
						new TypeReference<List<String>>() {
						});
				System.out.println(sheetAliasNameList);
				for (String sheetAliasName : sheetAliasNameList) {
					Query query = new Query(
							Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetAliasName));
					query.fields().include("eNBName");
					query.fields().include("eNBId");
					List<EnodebDetails> result = mongoTemplate.find(query, EnodebDetails.class, dbcollectionFileName);

					for (EnodebDetails enbData : result) {
						Map<String, String> objMap = new HashMap<>();
						String enBID = enbData.geteNBId();
						String enbName = enbData.geteNBName();
						if (StringUtils.isNotEmpty(enBID) && StringUtils.isNotEmpty(enbName)) {
							if (!objTestMap.containsKey(enBID)) {
								objMap.put("eNBId", enBID);
								objMap.put("eNBName", enbName);
								objTestMap.put(enBID, enbName);
								resultList.add(objMap);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception CiqDetails() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}

		return resultList;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param fileName,enbId,enbName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getEnbTableDetailsRanConfig(String fileName, String enbId, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName));
		query.addCriteria(Criteria.where("sheetAliasName").is(sheetAliasName));
		query.addCriteria(Criteria.where("eNBId").is(enbId));
		// query.addCriteria(Criteria.where("eNBName").is(enbName));
		if (StringUtils.isNotEmpty(subSheetAliasName)) {
			query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetAliasName));
		}

		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}
	
	@Override
	public List<CIQDetailsModel> getEnbTableDetailsRanConfigBySiteName(String fileName, String SiteName, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName));
		query.addCriteria(Criteria.where("sheetAliasName").is(sheetAliasName));
		query.addCriteria(Criteria.where("SiteName").is(SiteName));
		// query.addCriteria(Criteria.where("eNBName").is(enbName));
		if (StringUtils.isNotEmpty(subSheetAliasName)) {
			query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetAliasName));
		}

		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	
	@Override
	public List<CIQDetailsModel> getEnbTableDetailsRanConfigg(String fileName, String NEID, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName));
		query.addCriteria(Criteria.where("sheetAliasName").is(sheetAliasName));
		query.addCriteria(Criteria.where("eNBId").is(NEID));
		// query.addCriteria(Criteria.where("eNBName").is(enbName));
		if (StringUtils.isNotEmpty(subSheetAliasName)) {
			query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetAliasName));
		}
		

		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}
	
	@Override
	public List<CIQDetailsModel> getEnbTableDetailsRanConfig2(String fileName, String eNB4G, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName));
		query.addCriteria(Criteria.where("sheetAliasName").is(sheetAliasName));
		query.addCriteria(Criteria.where("eNBId").is(eNB4G));
		// query.addCriteria(Criteria.where("eNBName").is(enbName));
		if (StringUtils.isNotEmpty(subSheetAliasName)) {
			query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetAliasName));
		}
		

		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}
		
		@Override
		public List<CIQDetailsModel> getEnbTableDetailsRanConfigg3(String fileName, String eNB4G, String enbName,
				String dbcollectionFileName, String sheetAliasName, String subSheetAliasName) {
			List<CIQDetailsModel> resultList = null;

			Query query = new Query();
			query.addCriteria(Criteria.where("fileName").is(fileName));
			query.addCriteria(Criteria.where("sheetAliasName").is(sheetAliasName));
			query.addCriteria(Criteria.where("eNBId").is(eNB4G));
			// query.addCriteria(Criteria.where("eNBName").is(enbName));
			if (StringUtils.isNotEmpty(subSheetAliasName)) {
				query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetAliasName));
			}
			

			/*
			 * Query query = new Query();
			 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
			 * .is(enbName));
			 */
			try {
				resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

			} catch (Exception e) {
				logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
						+ ExceptionUtils.getFullStackTrace(e));

			}
		return resultList;
	}
	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param fileName,enbId,enbName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getEnbTableDetails(String fileName, String enbId, String enbName,
			String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("eNBId").is(enbId));// .and("eNBName").is(enbName));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		if (null != resultList && resultList.size() > 0 && StringUtils.isEmpty(resultList.get(0).geteNBName())) {
			try {
				List<CIQDetailsModel> listCIQDetailsModel1 = getCIQDetailsModelList(enbId, dbcollectionFileName);
				if (StringUtils.isEmpty(listCIQDetailsModel1.get(0).geteNBName())) {

					List<CIQDetailsModel> listOfCiqDetails2 = getCiqDetailsForRuleValidationsheet(enbId,
							dbcollectionFileName, "CIQUpstateNY", "eNBId");
					if (StringUtils.isEmpty(listOfCiqDetails2.get(0).geteNBName())) {
						System.out.println("getCiqDetailsForRuleValidationsheet" + enbId);
					} else {
						resultList = listOfCiqDetails2;
					}
				} else {
					resultList = listCIQDetailsModel1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultList;
		
	}
	

	public List<CIQDetailsModel> getCIQDetailsModelList(String enbId, String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("eNBId").is(enbId));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(
					"Exception getCIQDetailsModelList() in RunTestServiceImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	/**
	 * This method will return EnbDetails based on filename
	 * 
	 * @param filename,
	 *            id
	 * @return ArrayList<String>
	 */
	@Override
	public List<CIQDetailsModel> getEnbTableSheetDetails(String fileName, String sheetAliasName, String enbId,
			String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetAliasName)
				.and("eNBName").is(enbId));// .and("eNBName").is(enbName));
		// where("sheetAliasName").is(sheetAliasName);
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CIQDetailsModel> getEnbTableSheetDetailss(String fileName, String sheetAliasName, String enbId,
			String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetAliasName)
				.and("eNBName").is(enbId));// .and("eNBName").is(enbName));
		// where("sheetAliasName").is(sheetAliasName);
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}
	@Override
	public List<CIQDetailsModel> getEnbTableSheetDetailsss(String fileName, String sheetAliasName, String eNB4G,
			String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetAliasName)
				.and("eNBId").is(eNB4G));// .and("eNBName").is(enbName));
		// where("sheetAliasName").is(sheetAliasName);
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public ArrayList<String> getEnbDetailsFilename(String fileName, String enbId) {
		ArrayList<String> enb = new ArrayList<String>();
		Query query = new Query(Criteria.where("fileName").is(fileName));
		try {
			List<EnodebDetails> result = mongoTemplate.find(query(where("eNBId").is(enbId)), EnodebDetails.class,
					fileName);
			for (EnodebDetails enbData : result) {
				enb.add(enbData.getCiqMap());
			}
		} catch (Exception e) {
			logger.error("Exception getEnbDetailsFilename() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		System.out.println(enb);
		return enb;
	}

	/**
	 * This method will return getAuditDetailsonSearch
	 * 
	 * @param page,
	 *            count
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCiqAuditDetails(CiqUploadAuditTrailDetModel ciqAuditTrailModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<CiqUploadAuditTrailDetEntity> ciqUploadauditEntity = null;
		List<CiqUploadAuditTrailDetEntity> ciqsearchList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (ciqAuditTrailModel != null) {
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getCiqFileName())) {
					Criterion ciqFileName = Restrictions.ilike("ciqFileName", ciqAuditTrailModel.getCiqFileName(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqFileName);
				}
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getScriptFileName())) {
					Criterion scriptFileName = Restrictions.ilike("scriptFileName",
							ciqAuditTrailModel.getScriptFileName(), MatchMode.ANYWHERE);
					conjunction.add(scriptFileName);
				}
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getChecklistFileName())) {
					Criterion checklistFileName = Restrictions.ilike("checklistFileName",
							ciqAuditTrailModel.getChecklistFileName(), MatchMode.ANYWHERE);
					conjunction.add(checklistFileName);
				}
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getCiqVersion())) {
					Criterion ciqVersion = Restrictions.ilike("ciqVersion", ciqAuditTrailModel.getCiqVersion(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqVersion);
				}
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getUploadBy())) {
					Criterion uploadedBy = Restrictions.ilike("uploadBy", ciqAuditTrailModel.getUploadBy(),
							MatchMode.ANYWHERE);
					conjunction.add(uploadedBy);
				}
				if (StringUtils.isNotEmpty(ciqAuditTrailModel.getFileSourceType())) {
					Criterion fileSourceType = Restrictions.eq("fileSourceType",
							ciqAuditTrailModel.getFileSourceType());
					conjunction.add(fileSourceType);
				}
				if (ciqAuditTrailModel.getProgramDetailsEntity().getId() != null) {
					criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id",
							ciqAuditTrailModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (ciqAuditTrailModel.getFromDate() != null && !"".equals(ciqAuditTrailModel.getFromDate())
						&& ciqAuditTrailModel.getToDate() != null && !"".equals(ciqAuditTrailModel.getToDate())) {
					Criterion eventstartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(ciqAuditTrailModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(ciqAuditTrailModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			ciqUploadauditEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			// tot List
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteriaList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaList.add(
					Restrictions.eq("programDetailsEntity.id", ciqAuditTrailModel.getProgramDetailsEntity().getId()));
			ciqsearchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			Set<String> ciqName = ciqsearchList.stream().map(x -> x.getCiqFileName()).sorted()
					.collect(Collectors.toSet());
			Set<String> scriptName = ciqsearchList.stream().filter(y -> StringUtils.isNotEmpty(y.getScriptFileName()))
					.map(x -> x.getScriptFileName()).sorted().collect(Collectors.toSet());
			Set<String> checkList = ciqsearchList.stream().map(x -> x.getChecklistFileName()).sorted()
					.collect(Collectors.toSet());
			Set<String> ciqVersion = ciqsearchList.stream().map(x -> x.getCiqVersion()).sorted()
					.collect(Collectors.toSet());
			Set<String> type = ciqsearchList.stream().map(x -> x.getFileSourceType()).sorted()
					.collect(Collectors.toSet());
			Set<String> userName = ciqsearchList.stream().map(x -> x.getUploadBy()).sorted()
					.collect(Collectors.toSet());

			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("ciqName", ciqName);
			objMap.put("scriptName", scriptName);
			objMap.put("checkList", checkList);
			objMap.put("ciqVersion", ciqVersion);
			objMap.put("type", type);
			objMap.put("userName", userName);
			objMap.put("paginationcount", pagecount);
			objMap.put("ciqList", ciqUploadauditEntity);
		} catch (Exception e) {
			logger.error("Exception getAuditDetailsOnSearch() in AuditTrailRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	@Override
	public CiqUploadAuditTrailDetEntity getCiqAuditBasedONFileNameAndProgram(String fileName, Integer programId) {
		CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailDetEntity = null;

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("ciqFileName", fileName));
			criteria.add(Restrictions.eq("programDetailsEntity.id", programId));
			objCiqUploadAuditTrailDetEntity = (CiqUploadAuditTrailDetEntity) criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

		} catch (Exception e) {
			logger.error("Exception getCiqAuditBasedONFileNameAndProgram() in AuditTrailRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqUploadAuditTrailDetEntity;

	}

	@Override
	public CiqUploadAuditTrailDetEntity getLatestCheckListByProgram(Integer programId) {
		CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailDetEntity = null;

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("programDetailsEntity.id", programId));
			criteria.add(Restrictions.isNotNull("checklistFileName"));
			criteria.add(Restrictions.isNotNull("checklistFilePath"));
			criteria.addOrder(Order.desc("creationDate"));
			objCiqUploadAuditTrailDetEntity = (CiqUploadAuditTrailDetEntity) criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list().stream().findFirst()
					.orElse(null);

		} catch (Exception e) {
			logger.error("Exception getLatestCheckListByProgram() in AuditTrailRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqUploadAuditTrailDetEntity;

	}

	@Override
	public boolean deleteCheckListDetailsByFilename(String checkListDbeName) {
		boolean status = false;
		try {

			mongoTemplate.dropCollection(checkListDbeName);
			mongoTemplate.findAllAndRemove(query(where("_id").is(checkListDbeName)), Counter.class, "counters");
			status = true;
		} catch (Exception e) {
			logger.error("Exception deleteCheckListDetailsByFilename() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	/**
	 * this method will return CiqList
	 * 
	 * @param user
	 * @param customerId
	 * @return Set<String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public ProgramTemplateEntity getProgramTemplate(Integer programId, String paramName) {
		ProgramTemplateEntity objEntity = null;

		try {
			logger.info("FileUploadRepositoryImpl  getProgramTemplate() programId: " + programId + ", paramName: "
					+ paramName);
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(ProgramTemplateEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");

			criteria.add(Restrictions.eq("programDetailsEntity.id", programId));
			criteria.add(Restrictions.eq("label", paramName));
			objEntity = (ProgramTemplateEntity) criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

		} catch (Exception e) {
			logger.error("Exception  getProgramTemplate() in  FileUploadRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objEntity;
	}

	@Override
	public String getEnBDataByPath(String collectionName, String sheetAliasName, String enbId, String path) {
		String res = null;
		Criteria criteria = new Criteria().andOperator(Criteria.where("eNBId").is(enbId),
				Criteria.where("sheetAliasName").is(sheetAliasName));
		Query query = new Query(criteria);
		logger.info("getEnBDataByPath() criteria collectionName: " + collectionName + ", sheetAliasName: "
				+ sheetAliasName + ", enbId: " + enbId + ", path: " + path);
		try {
			List<CIQDetailsModel> detailsModels = mongoTemplate.find(query, CIQDetailsModel.class, collectionName);
			if (CommonUtil.isValidObject(detailsModels) && detailsModels.size() > 0) {
				CIQDetailsModel details = detailsModels.get(0);
				if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
						&& details.getCiqMap().containsKey(path)) {
					CiqMapValuesModel data = details.getCiqMap().get(path);
					if (CommonUtil.isValidObject(data)) {
						res = data.getHeaderValue();
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception getEnBDataByPath() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return res;
	}

	@Override
	public List<CIQDetailsModel> getEnBData(String collectionName, String sheetAliasName, String enbId) {
		List<CIQDetailsModel> detailsModels = null;
		Criteria criteria = new Criteria().andOperator(Criteria.where("eNBId").is(enbId),
				Criteria.where("sheetAliasName").is(sheetAliasName));
		Query query = new Query(criteria);
		logger.info("getEnBDataByPath() criteria: " + criteria.toString());
		try {
			detailsModels = mongoTemplate.find(query, CIQDetailsModel.class, collectionName);
		} catch (Exception e) {
			logger.error(
					"Exception getEnBDataByPath() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return detailsModels;
	}

	/**
	 * This method will saveGrowConstant
	 * 
	 * @param growConstantsEntity
	 * @return boolean
	 */
	@Override
	public boolean saveGrowConstant(GrowConstantsEntity growConstantsEntity) {
		logger.info("FileUploadRepositoryImpl.saveGrowConstant()");
		boolean status = false;
		try {
			entityManager.merge(growConstantsEntity);
			status = true;
		} catch (Exception e) {
			logger.info(
					"Exception in FileUploadRepositoryImpl.saveGrowConstant(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public List<GrowConstantsEntity> getGrowConstantsDetails() {

		List<GrowConstantsEntity> growConstantsEntityList = null;

		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(GrowConstantsEntity.class);
			criteria.addOrder(Order.asc("id"));
			growConstantsEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			// TODO: handle exception
		}

		return growConstantsEntityList;

	}

	@Override
	public List<CIQDetailsModel> getCiqRowsByCellId(String collectionName, String sheetAliasName, String enbId,
			String cellId) {
		List<CIQDetailsModel> detailsModels = null;
		Criteria criteria = new Criteria().andOperator(Criteria.where("eNBId").is(enbId),
				Criteria.where("sheetAliasName").is(sheetAliasName),
				Criteria.where("ciqMap." + Constants.ORAN_COMM_SCRIPT_Cell_ID + ".headerValue").is(cellId));
		Query query = new Query(criteria);
		logger.info("getCiqRowsByCellId() criteria: " + criteria.toString());
		try {
			detailsModels = mongoTemplate.find(query, CIQDetailsModel.class, collectionName);
		} catch (Exception e) {
			logger.error("Exception getCiqRowsByCellId() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return detailsModels;
	}

	/**
	 * This method will return getEnbTableDetailsSheetBased
	 * 
	 * @param fileName,enbId,enbName,sheetName,dbcollectionFileName
	 * @return List<CIQDetailsModel>
	 */
	/*
	 * public List<CIQDetailsModel> getEnbTableDetailsSheetBased(String fileName,
	 * String enbId, String enbName,String sheetName,String dbcollectionFileName) {
	 * List<CIQDetailsModel> resultList = null;
	 * 
	 * Query query = new Query(
	 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
	 * .is(enbName).and("sheetName").is(sheetName)); try { resultList =
	 * mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);
	 * 
	 * } catch (Exception e) { logger.
	 * error("Exception getEnbTableDetailsSheetBased() in FileUploadRepositoryImpl :"
	 * + ExceptionUtils.getFullStackTrace(e));
	 * 
	 * } return resultList; }
	 */

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param dbcollectionFileName,sheetName,subSheetName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getRanConfigDetailsValidation(String dbcollectionFileName, String sheetName,
			String subSheetName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("sheetAliasName").is(sheetName));
		if (StringUtils.isNotEmpty(subSheetName)) {
			query.addCriteria(Criteria.where("subSheetAliasName").is(subSheetName));
		}

		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getRanConfigDetailsValidation() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param dbcollectionFileName,sheetName,subSheetName,enbName,enbId
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getRanConfigDetailsEnbValidation(String dbcollectionFileName, String sheetName,
			String subSheetName, String enbName, String enbId) {
		List<CIQDetailsModel> resultList = null;
		Criteria criteria = new Criteria();

		criteria = new Criteria().andOperator(Criteria.where("eNBId").is(enbId),
				Criteria.where("sheetAliasName").is(sheetName), Criteria.where("eNBName").is(enbName));
		if (StringUtils.isNotEmpty(subSheetName)) {
			criteria = new Criteria().andOperator(Criteria.where("eNBId").is(enbId),
					Criteria.where("sheetAliasName").is(sheetName),
					Criteria.where("subSheetAliasName").is(subSheetName));
		}
		Query query = new Query(criteria);
		logger.info(
				"getRanConfigDetailsEnbValidation() criteria: " + criteria.toString() + ", query: " + query.toString());
		/*
		 * Query query = new Query();
		 * Criteria.where("fileName").is(fileName).and("eNBId").is(enbId).and("eNBName")
		 * .is(enbName));
		 */
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);
		} catch (Exception e) {
			logger.error("Exception getRanConfigDetailsEnbValidation() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultList;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param fileName,enbId,enbName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getEnbDetails(String enbId) {
		List<CIQDetailsModel> resultList = null;
		try {
			MongoDatabase db = mongoTemplate.getDb();
			MongoIterable<String> collections = db.listCollectionNames();
			Query query = new Query();
			query.addCriteria(Criteria.where("eNBId").is(enbId));
			for (String dbcollectionFileName : collections) {
				resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);
				if (CommonUtil.isValidObject(resultList) && resultList.size() > 0) {
					CiqMapValuesModel latitude = resultList.get(0).getCiqMap().get(Constants.MAP_LATITUDE);
					CiqMapValuesModel longitude = resultList.get(0).getCiqMap().get(Constants.MAP_LONGITUDE);
					CiqMapValuesModel market = resultList.get(0).getCiqMap().get(Constants.MAP_MARKET);
					CiqMapValuesModel cellId = resultList.get(0).getCiqMap().get(Constants.MAP_Cell_ID);
					String enbName = resultList.get(0).geteNBName();
					if (CommonUtil.isValidObject(latitude) && CommonUtil.isValidObject(cellId)
							&& CommonUtil.isValidObject(longitude) && CommonUtil.isValidObject(enbName)
							&& CommonUtil.isValidObject(market)) {
						break;
					}

				}
			}

		} catch (Exception e) {
			logger.error(
					"Exception getEnbDetails() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<NetworkConfigEntity> getNeConfigVersionMarketBased(Integer programId) {
		logger.info("NeMappingRepositoryImpl.neConfigTypeDetails() called.. programId: " + programId);
		List<NetworkConfigEntity> neConfigTypeDetails = null;
		try {
			org.hibernate.Criteria criteriaMarketList = entityManager.unwrap(Session.class)
					.createCriteria(NetworkConfigEntity.class);
			criteriaMarketList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaMarketList.createAlias("neVersionEntity", "neVersionEntity");
			criteriaMarketList.add(Restrictions.eq("programDetailsEntity.id", programId));
			neConfigTypeDetails = criteriaMarketList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.info("Exception in NeMappingRepositoryImpl.getProgramGenerateFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neConfigTypeDetails;
	}

	@Override
	public List<CIQDetailsModel> getEnbTableDetailss(String fileName, String sheetName, String subSheetName,
			String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetName").is(sheetName));// .and("subSheetName").is(subSheetName));
		if (subSheetName != null) {
			query.addCriteria(Criteria.where("subSheetName").is(subSheetName));
		}
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<Map<String, String>> getEnbDetailssheet(String id, String fileName, String sheetname,
			String dbcollectionFileName) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Set<Map<String, String>> objSet = new LinkedHashSet<>();

		Map<String, String> objTestMap = new HashMap<>();
		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetname));
		try {
			query.fields().include("eNBName");
			query.fields().include("eNBId");
			List<EnodebDetails> result = mongoTemplate.find(query, EnodebDetails.class, dbcollectionFileName);

			for (EnodebDetails enbData : result) {
				Map<String, String> objMap = new HashMap<>();

				String enBID = enbData.geteNBId();
				String enbName = enbData.geteNBName();
				// String site_name = enbData.getCiqMap().get("VZW Site Name").toString();
				/*
				 * if(objMap.containsKey("eNBId") && objMap.get("eNBId").equalsIgnoreCase(enBID)
				 * )
				 */
				if (StringUtils.isNotEmpty(enBID)) {
					if (!objTestMap.containsKey(enBID)) {
						objMap.put("eNBId", enBID);
						if (StringUtils.isNotEmpty(enbName)) {
							objMap.put("eNBName", enbName);
						} else {
							enbName = "-";
							objMap.put("eNBName", enbName);
						}

						// objMap.put("siteName", site_name);
						objTestMap.put(enBID, enbName);
						resultList.add(objMap);
					}

				}

			}
		} catch (Exception e) {
			logger.error("Exception getEnbDetailssheet() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where(idname).is(enbId).and("sheetAliasName").is(sheetname));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CIQDetailsModel> getsheetData(String fileName, String sheetName, String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetName));// .and("subSheetName").is(subSheetName));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(
					"Exception getsheetData() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CIQDetailsModel> getsheetData4G(String fileName, String sheetName, String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName).and("sheetName").is(sheetName));// .and("subSheetName").is(subSheetName));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(
					"Exception getsheetData() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public Map<String, List<Map<String, String>>> getEnbDetails5GMM(String id, String fileName,
			String dbcollectionFileName) {
		Map<String, List<Map<String, String>>> siteList = new HashMap<>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		Set<Map<String, String>> objSet = new LinkedHashSet<>();
		ObjectMapper mapper = new ObjectMapper();
		List<String> sheetAliasNameList = null;
		Map<String, String> objTestMap = new HashMap<>();
		ProgramTemplateEntity objProgramTemplateEntity = getProgramTemplate(Integer.parseInt(id),
				Constants.CIQ_VALIDATE_TEMPLATE);

		try {
			if (objProgramTemplateEntity != null && StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {

				JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());
				System.out.println(objData.get("sheetAliasNameList"));

				sheetAliasNameList = mapper.readValue(objData.get("sheetAliasNameList").toString(),
						new TypeReference<List<String>>() {
						});
				System.out.println(sheetAliasNameList);
				for (String sheetAliasName : sheetAliasNameList) {
					Query query = new Query(
							Criteria.where("fileName").is(fileName).and("sheetAliasName").is(sheetAliasName));
					query.fields().include("eNBName");
					query.fields().include("eNBId");
					query.fields().include("siteName");
					List<EnodebDetails> result = mongoTemplate.find(query, EnodebDetails.class, dbcollectionFileName);

					for (EnodebDetails enbData : result) {
						Map<String, String> objMap = new HashMap<>();
						String enBID = enbData.geteNBId();
						String enbName = enbData.geteNBName();
						String sitename = enbData.getSiteName();
						if (StringUtils.isNotEmpty(enBID) && StringUtils.isNotEmpty(enbName)) {
							if (!objTestMap.containsKey(enBID)) {
								objMap.put("eNBId", enBID);
								objMap.put("eNBName", enbName);
								objMap.put("siteName", sitename);
								objTestMap.put(enBID, enbName);
								resultList.add(objMap);
							}
						}
					}
				}
			}

			Set<String> siteset = resultList.stream().filter(x -> StringUtils.isNotEmpty(x.get("siteName")))
					.map(x -> x.get("siteName")).collect(Collectors.toSet());
			for (String site : siteset) {
				List<Map<String, String>> objMap = resultList.stream()
						.filter(x -> StringUtils.isNotEmpty(x.get("siteName")))
						.filter(x -> x.get("siteName").equals(site)).map(x -> x).collect(Collectors.toList());
				siteList.put(site, objMap);
			}
		} catch (Exception e) {
			logger.error("Exception CiqDetails() in FileUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return siteList;
	}

	@Override
	public boolean updateInfo(String uniqFetchId, String info) {
		boolean status = false;
		try {

			String query = "update CiqUploadAuditTrailDetEntity c set c.fetchInfo='" + info
					+ "' where c.fileSourceType='" + uniqFetchId + "'";
			entityManager.createQuery(query).executeUpdate();
			status = true;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;

	}
	
	@Override
	public boolean upDateCiqNameInNeMapping(String ciqName, CopyOnWriteArraySet<String> setNeids,Integer  programId) {
		boolean status = false;
		try {
			
			String hql = "update  NeMappingEntity c set c.ciqName='" + ciqName
										+ "' where (c.enbId in (:list) AND  PROGRAME_NAME_ID  =(:programId))";
			
			javax.persistence.Query query = entityManager.createQuery(hql);
			query.setParameter("list", setNeids);
			query.setParameter("programId", programId);
		
			query.executeUpdate();
			status = true;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}



	@Override
	public List<NeMappingEntity> getCiqWithNeMappinDetails(HashSet<String> setNeIds,int pID) {
		List<NeMappingEntity> neMappingEntityList = null;
		try {
			//Conjunction conjunction = Restrictions.conjunction();
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			criteria.add(Restrictions.in("enbId", setNeIds));
			criteria.add(Restrictions.in("programDetailsEntity.id", pID));
			neMappingEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return neMappingEntityList;
	}
	@Override
	public Integer getciqDetails(String string) {
		Integer count = 0;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.ilike("ciqFileName", string,MatchMode.ANYWHERE));
			criteria.add(conjunction);
			List<CiqUploadAuditTrailDetEntity> ciqList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			count= ciqList.size();

		}catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return count;

	}

}
