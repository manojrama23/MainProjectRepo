package com.smart.rct.premigration.repositoryImpl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.CheckListModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.CIQUploadRepository;
import com.smart.rct.premigration.serviceImpl.CounterServiceImpl;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class CIQUploadRepositoryImpl implements CIQUploadRepository {
	final static Logger logger = LoggerFactory.getLogger(CIQUploadRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	CounterServiceImpl objCounterServiceImpl;

	// @Autowired
	// private CheckListModel checkListModel;

	/**
	 * This method will save CIQ
	 * 
	 * @param entity
	 * @return entity
	 */
	@Override
	public CIQDetailsModel save(CIQDetailsModel entity, String dbCollectionName) {
		try {
			mongoTemplate.save(entity, dbCollectionName);
		} catch (Exception e) {
			logger.error(
					"Exception CIQDetailsModel() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return entity;
	}

	/**
	 * This method will find details
	 * 
	 * @param fileName
	 * @return list
	 */
	@Override
	public List<CIQDetailsModel> findAll(String fileName) {
		List<CIQDetailsModel> list = new ArrayList<CIQDetailsModel>();
		try {
			Query query = new Query();
			query.with(new Sort(Sort.Direction.ASC, "id"));
			list = mongoTemplate.find(query, CIQDetailsModel.class, fileName);
		} catch (Exception e) {
			logger.error("Exception findAll() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return list;
	}

	/**
	 * This method will find details based on filename
	 * 
	 * @param fileName
	 * @param pageable
	 * @return string
	 */
	@Override
	public Page<CIQDetailsModel> findByFileName(String fileName, Pageable pageable) {
		List<CIQDetailsModel> list = new ArrayList<CIQDetailsModel>();
		Page<CIQDetailsModel> result = null;
		try {
			Query query = new Query().with(pageable);
			// query.addCriteria(Criteria.where("fileName").is(fileName));
			list = mongoTemplate.find(query, CIQDetailsModel.class, fileName);
			long count = mongoTemplate.count(query, CIQDetailsModel.class, fileName);
			result = new PageImpl<CIQDetailsModel>(list, pageable, count);

		} catch (Exception e) {
			logger.error(
					"Exception findByFileName() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}

	/**
	 * This method will update details
	 * 
	 * @param entity
	 * @return boolean
	 */
	@Override
	public boolean updateCiqFileDetails(CIQDetailsModel entity) {
		boolean status = false;
		try {

			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(entity.getId()));
			Update update = new Update();
			update.set("fileName", entity.getFileName());
			update.set("eNBId", entity.geteNBId());
			update.set("eNBName", entity.geteNBName());
			update.set("ciqMap", entity.getCiqMap());
			mongoTemplate.findAndModify(query, update, CIQDetailsModel.class, entity.getFileName());
			status = true;
		} catch (Exception e) {
			logger.error("Exception updateCiqFileDetails() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will create CIQ details
	 * 
	 * @param createCiqEntity
	 * @return boolean
	 */
	@Override
	public boolean createCiqFileDetaiils(CIQDetailsModel createCiqEntity) {
		boolean status = false;
		try {
			mongoTemplate.save(createCiqEntity, createCiqEntity.getFileName());
			status = true;
		} catch (Exception e) {
			logger.error("Exception createCiqFileDetaiils() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will return getEnbTableDetailsById
	 * 
	 * @param fileName,id
	 * @return CIQDetailsModel
	 */
	@Override
	public CIQDetailsModel getEnbTableDetailsById(String fileName, Integer id, String dbCollcetionDbName) {
		CIQDetailsModel objCIQDetailsModel = null;

		try {
			objCIQDetailsModel = mongoTemplate.findById(id, CIQDetailsModel.class, dbCollcetionDbName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetailsById() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return objCIQDetailsModel;
	}

	/**
	 * This method will return updateCiqFileDetailsEnbs
	 * 
	 * @param entity
	 * @return boolean
	 */
	@Override
	public boolean updateCiqFileDetailsEnbs(CIQDetailsModel entity) {
		boolean status = false;
		try {

			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(entity.getId()));
			Update update = new Update();
			update.set("fileName", entity.getFileName());
			update.set("eNBId", entity.geteNBId());
			update.set("eNBName", entity.geteNBName());
			update.set("ciqMap", entity.getCiqMap());
			mongoTemplate.findAndModify(query, update, CIQDetailsModel.class, entity.getFileName());
			status = true;
		} catch (Exception e) {
			logger.error("Exception updateCiqFileDetails() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param fileName,enbId,enbName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CIQDetailsModel> getCiqSheetNames(String fileName, String dbCollectionName) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName));
		try {

			query.fields().include("sheetName");
			query.fields().include("subSheetName");
			//List<EnodebDetails> result = mongoTemplate.find(query, EnodebDetails.class, dbCollectionName);
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbCollectionName);

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
	public List<CIQDetailsModel> getCiqSheetNamesBasedOnEnb(String fileName, String dbCollectionName, String enbName,
			String enbId) {
		List<CIQDetailsModel> resultList = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName));
		query.addCriteria(Criteria.where("eNBId").is(enbId));
		query.addCriteria(Criteria.where("eNBName").is(enbName));
		try {

			query.fields().include("sheetAliasName");
			query.fields().include("subSheetAliasName");
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbCollectionName);

		} catch (Exception e) {
			logger.error("Exception getCiqSheetNamesBasedOnEnb() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public Map<String, Object> getCiqSheetDisply(String programFileName, String sheetName, String subSheetName,Map<String,String> ciqSearchMap, int page, int count) {
		List<CIQDetailsModel> list = new ArrayList<CIQDetailsModel>();
		Page<CIQDetailsModel> result = null;
		Map<String, Object> objnewMap = new LinkedHashMap<>();
		page = page - 1;
		try {
			// for pagination
			Pageable pageableRequest = PageRequest.of(page, count, new Sort(Sort.Direction.ASC, "id"));

			Query query = new Query().with(pageableRequest);
			query.addCriteria(Criteria.where("sheetName").is(sheetName));
			if (StringUtils.isNotEmpty(subSheetName)) {
				query.addCriteria(Criteria.where("subSheetName").is(subSheetName));
			}
			if (CommonUtil.isValidObject(ciqSearchMap)) {
				for (Map.Entry<String,String> entry : ciqSearchMap.entrySet())  {
					if (StringUtils.isNotEmpty(entry.getValue())) {
						logger.info("getCiqSheetDisply() key : ciqMap."+entry.getKey()+".headerValue, Value:"+entry.getValue());
						query.addCriteria(Criteria.where("ciqMap."+entry.getKey()+".headerValue").regex(".*"+entry.getValue()+".*","i"));
					}
				}
			}
			list = mongoTemplate.find(query, CIQDetailsModel.class, programFileName);
			long countDet = mongoTemplate.count(query, CIQDetailsModel.class, programFileName);
			result = new PageImpl<CIQDetailsModel>(list, pageableRequest, countDet);
			objnewMap.put("count", result.getTotalPages());
			objnewMap.put("list", list);

		} catch (Exception e) {
			logger.error("Exception getCiqSheetDisply() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objnewMap;
	}

	/**
	 * This method will return ciqDetails
	 * 
	 * @param fileName
	 * @return L
	 */
	@Override
	public List<CIQDetailsModel> getCiqFileDetails(String fileName) {
		List<CIQDetailsModel> objCIQDetailsModel = null;

		try {
			objCIQDetailsModel = mongoTemplate.findAll(CIQDetailsModel.class, fileName);

		} catch (Exception e) {
			logger.error("Exception getEnbTableDetailsById() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return objCIQDetailsModel;
	}

	@Override
	public CheckListDetailsModel saveCheckList(CheckListDetailsModel entity, String dbCollectionName) {
		try {
			mongoTemplate.save(entity, dbCollectionName);
		} catch (Exception e) {
			logger.error(
					"Exception saveCheckList() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return entity;
	}

	/**
	 * This method will return CiqDetails based on filename
	 * 
	 * @param fileName,enbId,enbName
	 * @return List<CIQDetailsModel>
	 */
	@Override
	public List<CheckListDetailsModel> getCheckListSheetNames(String fileName, String dbCollectionName) {
		List<CheckListDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName));
		try {

			query.fields().include("sheetName");
			resultList = mongoTemplate.find(query, CheckListDetailsModel.class, dbCollectionName);

		} catch (Exception e) {
			logger.error("Exception getCheckListSheetNames() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CheckListDetailsModel> getCheckListAllSheetNames(String fileName, String dbCollectionName) {
		List<CheckListDetailsModel> resultList = null;

		Query query = new Query(Criteria.where("fileName").is(fileName));
		try {

			resultList = mongoTemplate.find(query, CheckListDetailsModel.class, dbCollectionName);

		} catch (Exception e) {
			logger.error("Exception getCheckListAllSheetNames() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public Map<String, Object> getCheckListSheetDisply(String programFileName, String sheetName, int page, int count) {
		List<CheckListDetailsModel> list = new ArrayList<CheckListDetailsModel>();
		Page<CheckListDetailsModel> result = null;
		Map<String, Object> objnewMap = new LinkedHashMap<>();
		page = page - 1;
		try {
			/*// for pagination
			Pageable pageableRequest = PageRequest.of(page, count, new Sort(Sort.Direction.ASC, "id"));

			Query query = new Query().with(pageableRequest);
			query.addCriteria(Criteria.where("sheetName").is(sheetName));
			list = mongoTemplate.find(query, CheckListDetailsModel.class, programFileName);
			long countDet = mongoTemplate.count(query, CheckListDetailsModel.class, programFileName);
			result = new PageImpl<CheckListDetailsModel>(list, pageableRequest, countDet); 
			objnewMap.put("count", result.getTotalPages());
			objnewMap.put("list", list);*/
			
			Query query = new Query();
			query.addCriteria(Criteria.where("sheetName").is(sheetName));
			list = mongoTemplate.find(query, CheckListDetailsModel.class, programFileName);
			objnewMap.put("list", list);
			
		} catch (Exception e) {
			logger.error("Exception getCheckListSheetDisply() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objnewMap;
	}

	@Override
	public Map<String, Object> getAllCheckListSheetDisply(String programFileName, String sheetName, int page, int count,
			String enodeName,int runTestId) {
		List<CheckListModel> list = new ArrayList<CheckListModel>();
		Page<CheckListModel> result = null;
		Map<String, Object> objnewMap = new LinkedHashMap<>();
		page = page - 1;
		try {
			// for pagination
			//Pageable pageableRequest = PageRequest.of(page, count, new Sort(Sort.Direction.ASC, "id"));

			//Query query = new Query().with(pageableRequest);
			Query query = new Query();
			query.addCriteria(Criteria.where("sheetName").is(sheetName));
			query.addCriteria(Criteria.where("enodeName").is(enodeName));
			//query.addCriteria(Criteria.where("runTestId").is(runTestId));
			list = mongoTemplate.find(query, CheckListModel.class, programFileName);
			long countDet = mongoTemplate.count(query, CheckListModel.class, programFileName);
			//result = new PageImpl<CheckListModel>(list, pageableRequest, countDet);
			//objnewMap.put("count", result.getTotalPages());
			objnewMap.put("list", list);

		} catch (Exception e) {
			logger.error("Exception getAllCheckListSheetDisply() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objnewMap;
	}
	
	@Override
	public List<CheckListModel> getCheckListDetails(String programFileName,String enodeName,int runTestId,int stepIndex) {
		List<CheckListModel> checkListModel = new ArrayList<CheckListModel>();
		
		try {
			Query query = new Query();
			
			query.addCriteria(Criteria.where("enodeName").is(enodeName));
			//query.addCriteria(Criteria.where("runTestId").is(runTestId));
			query.addCriteria(Criteria.where("stepIndex").is(stepIndex));
			checkListModel = mongoTemplate.find(query, CheckListModel.class, programFileName);

		} catch (Exception e) {
			logger.error("Exception getAllCheckListSheetDisply() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return checkListModel;
	}

	@Override
	public Map<String, Object> insertChecklistDetails(String programFileName, String sheetName, String enodeName,
			String remarks,int runTestId) {
		List<CheckListDetailsModel> list = new ArrayList<CheckListDetailsModel>();
		List<CheckListModel> checkListModelList = new ArrayList<CheckListModel>();
		Map<String, Object> objnewMap = new LinkedHashMap<>();
		try {

			Query query = new Query(Criteria.where("sheetName").is(sheetName));
			list = mongoTemplate.find(query, CheckListDetailsModel.class, programFileName);
			
			Query query1 = new Query(Criteria.where("sheetName").is(sheetName));
			query1.addCriteria(Criteria.where("enodeName").is(enodeName));
			
			list = mongoTemplate.find(query, CheckListDetailsModel.class, programFileName);
			List<CheckListDetailsModel> existList = mongoTemplate.find(query1, CheckListDetailsModel.class, "checkListModel");
			
			logger.info("insertChecklistDetails() existList: "+existList.size());
			
			if(existList  == null || existList.size()<=0){
				for (CheckListDetailsModel checkListDetailsModel : list) {
					CheckListModel checkListModel = new CheckListModel();
					checkListModel.setId(objCounterServiceImpl.getNextSequence("checkListModel"));
					// checkListModel.setId(checkListDetailsModel.getId());
					checkListModel.setFileName(checkListDetailsModel.getFileName());
					checkListModel.setSheetName(checkListDetailsModel.getSheetName());
					checkListModel.setSeqOrder(checkListDetailsModel.getSeqOrder());
					checkListModel.setEnodeName(enodeName);
					//checkListModel.setRunTestId(runTestId);
					checkListModel.setStepIndex(checkListDetailsModel.getId());
					
					// checkListModel.setCheck(false);
					LinkedHashMap<String, String> map = checkListDetailsModel.getCheckListMap();
//					map.put("check", "false");
//					map.put("Remarks", remarks);

					LinkedHashMap<String, String> finalMap = new LinkedHashMap<String, String>();

					finalMap.put("check","");
					for (Map.Entry<String, String> entry : map.entrySet()) {
						finalMap.put(entry.getKey(), entry.getValue());
					}
					finalMap.put("Remarks", remarks);

					checkListModel.setCheckListMap(finalMap);

					// checkListModelList.add(checkListModel);
					mongoTemplate.insert(checkListModel);
					// save(checkListModel);

				}
			}else{
				
			}
			
			objnewMap.put("list", checkListModelList);

		} catch (Exception e) {
			logger.error("Exception insertChecklistDetails() in FileUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objnewMap;
	}

	@Override
	public boolean saveCheckListFileDetaiils(CheckListDetailsModel createCheckListEntity, String dbCollectionName) {
		boolean status = false;
		try {

			mongoTemplate.save(createCheckListEntity, dbCollectionName);
			status = true;

		} catch (Exception e) {
			logger.error("Exception saveCheckListFileDetaiils() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean updateCheckListFileDetaiils(CheckListDetailsModel createCheckListEntity, String dbCollectionName) {
		boolean status = false;
		try {

			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(createCheckListEntity.getId()));
			Update update = new Update();
			update.set("fileName", createCheckListEntity.getFileName());
			update.set("checkListMap", createCheckListEntity.getCheckListMap());
			mongoTemplate.findAndModify(query, update, CheckListDetailsModel.class, dbCollectionName);
			status = true;
		} catch (Exception e) {
			logger.error("Exception updateCheckListFileDetaiils() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean updateCheckListFileDetails(CheckListModel createCheckListEntity, String dbCollectionName,
			String enodeName) {
		boolean status = false;
		try {

			Query query = new Query();
			//query.addCriteria(Criteria.where("runTestId").is(createCheckListEntity.getRunTestId()));
			query.addCriteria(Criteria.where("enodeName").is(enodeName));
			query.addCriteria(Criteria.where("stepIndex").is(createCheckListEntity.getStepIndex()));
			Update update = new Update();
			LinkedHashMap<String, String> map = createCheckListEntity.getCheckListMap();
			//map.put("check", "true");
			map.put("check", createCheckListEntity.getCheckListMap().get("check"));
			createCheckListEntity.setCheckListMap(map);
			update.set("checkListMap", createCheckListEntity.getCheckListMap());
			// update.set("check", true);

			mongoTemplate.findAndModify(query, update, CheckListModel.class, dbCollectionName);
			status = true;
		} catch (Exception e) {
			logger.error("Exception updateCheckListFileDetaiils() in CIQUploadRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean deleteCheckListRowDetails(int id, String dbCollectionName) {
		boolean status = false;
		try {
			mongoTemplate.findAndRemove(query(where("id").is(id)), CheckListDetailsModel.class, dbCollectionName);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  deleteCheckListRowDetails() in  FileUploadRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean duplicateEnbDetails(NeMappingModel neMappingModel) {
		boolean status = false;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("programDetailsEntity.id", neMappingModel.getProgramDetailsEntity().getId()));
			criteria.add(Restrictions.eq("enbId", neMappingModel.getEnbId()));
			criteria.setProjection(Projections.rowCount());
			Long duplicatecount = (Long) criteria.uniqueResult();
			if (duplicatecount.intValue() > 0) {
				status = true;
			}

		} catch (Exception e) {
			logger.info("Exception in duplicateEnbDetails : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public boolean saveEnbDetails(NeMappingEntity neMappingEntity) {
		boolean status = false;
		try {
			entityManager.merge(neMappingEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in saveEnbDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

}
