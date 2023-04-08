package com.smart.rct.premigration.repositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.BucketEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.MmeIpEntity;
import com.smart.rct.common.entity.RrhAluEntity;
import com.smart.rct.common.entity.VlsmMMEIpEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.repository.GenerateRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class GenerateRepositoryImpl implements GenerateRepository{
	final static Logger logger = LoggerFactory.getLogger(GenerateRepositoryImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<CIQDetailsModel> findAll(String fileName) {
		List<CIQDetailsModel> list = new ArrayList<CIQDetailsModel>();
		try {
			Query query = new Query();
			query.with(new Sort(Sort.Direction.ASC, "id"));
			list = mongoTemplate.find(query, CIQDetailsModel.class, fileName);

		} catch (Exception e) {
			logger.error("Exception findAll() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return list;
	}

	@Override
	public JSONObject saveCsvAudit(GenerateInfoAuditEntity objInfo) {
		boolean status = false; 
		 String  Premigration_ID="";
	
		JSONObject result = new JSONObject();
		try {
			GenerateInfoAuditEntity manageEntity = entityManager.merge(objInfo);
			Premigration_ID=manageEntity.getId().toString();
			System.out.println(manageEntity.getId());
			status = true;

		} catch (Exception e) {
			logger.error("Exception saveCsvAudit() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		result.put("status", status);
		result.put("Premigration_ID", Premigration_ID);
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> getCsvAuditDetails(GenerateInfoAuditModel generateInfoAuditModel,int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<GenerateInfoAuditEntity> generateInfoAuditEntities = null;
		List<GenerateInfoAuditEntity> searchList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (generateInfoAuditModel != null) {
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getFileName())) {
					Criterion csvFileName = Restrictions.ilike("fileName", generateInfoAuditModel.getFileName(), MatchMode.ANYWHERE);
					conjunction.add(csvFileName);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getCiqFileName())) {
					Criterion ciqFileName = Restrictions.ilike("ciqFileName", generateInfoAuditModel.getCiqFileName(), MatchMode.ANYWHERE);
					conjunction.add(ciqFileName);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getSiteName())) {
					Criterion siteName = Restrictions.ilike("siteName", generateInfoAuditModel.getSiteName(), MatchMode.ANYWHERE);
					conjunction.add(siteName);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", generateInfoAuditModel.getNeName(), MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getFileType())) {
					Criterion fileType = Restrictions.ilike("fileType", generateInfoAuditModel.getFileType(), MatchMode.ANYWHERE);
					conjunction.add(fileType);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getGeneratedBy())) {
					Criterion generatedBy = Restrictions.eq("generatedBy", generateInfoAuditModel.getGeneratedBy());
					conjunction.add(generatedBy);
				}
				if (StringUtils.isNotEmpty(generateInfoAuditModel.getUserName())) {
					Criterion userName = Restrictions.ilike("generatedBy", generateInfoAuditModel.getUserName(),MatchMode.ANYWHERE);
					conjunction.add(userName);
				}
				if (generateInfoAuditModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", generateInfoAuditModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (generateInfoAuditModel.getSearchStartDate() != null && !"".equals(generateInfoAuditModel.getSearchStartDate()) && generateInfoAuditModel.getSearchEndDate() != null && !"".equals(generateInfoAuditModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("generationDate", DateUtil.stringToDate(generateInfoAuditModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("generationDate", DateUtil.stringToDateEndTime(generateInfoAuditModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
				criteria.add(conjunction);
				criteria.setFirstResult((page - 1) * count);
				criteria.setMaxResults(count);
				criteria.addOrder(Order.desc("generationDate"));
				generateInfoAuditEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
				
				org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
						.createCriteria(GenerateInfoAuditEntity.class);
				criteriaList.createAlias("programDetailsEntity", "programDetailsEntity");
				criteriaList.add(
						Restrictions.eq("programDetailsEntity.id", generateInfoAuditModel.getProgramDetailsEntity().getId()));
				searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

				Set<String> ciqName = searchList.stream().map(x -> x.getCiqFileName()).sorted()
						.collect(Collectors.toSet());
				/*Set<String> siteName = searchList.stream().map(x -> x.getSiteName()).sorted()
						.collect(Collectors.toSet());*/
				Set<String> csvfileName = searchList.stream().map(x -> x.getFileName()).sorted()
						.collect(Collectors.toSet());
				Set<String> neName = searchList.stream().map(x -> x.getNeName()).sorted()
						.collect(Collectors.toSet());
				
				
				org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
				criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
				criteriaCount.add(conjunction);
				criteriaCount.setProjection(Projections.rowCount());
				Long totCount = (Long) criteriaCount.uniqueResult();
				double size = totCount;
				result = Math.ceil(size / count);
				pagecount = (int) result;
				
				objMap.put("ciqName", ciqName);
				//objMap.put("siteName", siteName);
				objMap.put("csvfileName", csvfileName);
				objMap.put("neName", neName);	
			objMap.put("paginationcount", pagecount);
			objMap.put("fileList", generateInfoAuditEntities);
		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public LsmEntity getLsmById(int iLsmId) {
		LsmEntity objLsmEntity = null;
		try {
			objLsmEntity = entityManager.find(LsmEntity.class, iLsmId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objLsmEntity;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<GenerateInfoAuditModel> getCsvFilesList(int customerId) {
		List<GenerateInfoAuditEntity> generateInfoAuditEntities = null;
		List<GenerateInfoAuditModel> csvInfoAuditModelList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			criteria.createAlias("customerEntity", "customerEntity");
			criteria.add(Restrictions.eq("customerEntity.id", customerId));
			criteria.setFetchMode("customerEntity.customerDetails", FetchMode.LAZY);
			// criteria.addOrder(Order.asc("id"));

			generateInfoAuditEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			if (generateInfoAuditEntities != null && generateInfoAuditEntities.size() > 0) {

				for (GenerateInfoAuditEntity objGenerateInfoAuditEntity : generateInfoAuditEntities) {
					GenerateInfoAuditModel objCsvInfoAuditModel = new GenerateInfoAuditModel();

					objCsvInfoAuditModel.setFileName(objGenerateInfoAuditEntity.getFileName());
					objCsvInfoAuditModel.setFilePath(objGenerateInfoAuditEntity.getFilePath());
					objCsvInfoAuditModel.setGenerationDate(CommonUtil
							.dateToString(objGenerateInfoAuditEntity.getGenerationDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					objCsvInfoAuditModel.setGeneratedBy(objGenerateInfoAuditEntity.getGeneratedBy());
					objCsvInfoAuditModel.setId(objGenerateInfoAuditEntity.getId());

					csvInfoAuditModelList.add(objCsvInfoAuditModel);
				}

			}

		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return csvInfoAuditModelList;
	}

	@Override
	public List<UseCaseBuilderEntity> getUseCaseDetails(Integer customerId) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerId"), customerId));
			query.orderBy(cb.asc(root.get("useCaseName")));
			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntityList = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(" getUseCaseDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntityList;
	}

	@Override
	public boolean savepreGrowAudit(EnbPreGrowAuditEntity objInfo) {
		boolean status = false;
		try {
			entityManager.persist(objInfo);
			status = true;

		} catch (Exception e) {
			logger.error(
					"Exception savepreGrowAudit() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<EnbPreGrowAuditModel> getPreGrowListAudit(int customerId) {
		List<EnbPreGrowAuditEntity> enbPreGrowAuditEntityList = null;
		List<EnbPreGrowAuditModel> enbPreGrowAuditModelList = new ArrayList<>();
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(EnbPreGrowAuditEntity.class);
			criteria.createAlias("customerEntity", "customerEntity");
			criteria.add(Restrictions.eq("customerEntity.id", customerId));
			criteria.setFetchMode("customerEntity.customerDetails", FetchMode.LAZY);
			// criteria.addOrder(Order.asc("id"));

			enbPreGrowAuditEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

			if (enbPreGrowAuditEntityList != null && enbPreGrowAuditEntityList.size() > 0) {

				for (EnbPreGrowAuditEntity objEnbPreGrowAuditEntity : enbPreGrowAuditEntityList) {
					EnbPreGrowAuditModel objEnbPreGrowAuditModel = new EnbPreGrowAuditModel();

					objEnbPreGrowAuditModel.setCsvFileName(objEnbPreGrowAuditEntity.getCsvFileName());
					//objEnbPreGrowAuditModel.setDescription(objEnbPreGrowAuditEntity.getDescription());

					objEnbPreGrowAuditModel.setGrowingDate(CommonUtil
							.dateToString(objEnbPreGrowAuditEntity.getGrowingDate(), Constants.YYYY_MM_DD_HH_MM_SS));

					objEnbPreGrowAuditModel.setGrowPerformedBy(objEnbPreGrowAuditEntity.getGrowPerformedBy());
					objEnbPreGrowAuditModel.setId(objEnbPreGrowAuditEntity.getId());
					//objEnbPreGrowAuditModel.setSmId(objEnbPreGrowAuditEntity.getLsmEntity().getId());
					//objEnbPreGrowAuditModel.setSmName(objEnbPreGrowAuditEntity.getLsmEntity().getLsmName());
					objEnbPreGrowAuditModel.setStatus(objEnbPreGrowAuditEntity.getStatus());
					//objEnbPreGrowAuditModel.setUseCaseName(objEnbPreGrowAuditEntity.getUseCaseBuilderEntity().getUseCaseName());
					//objEnbPreGrowAuditModel.setUseCaseId(objEnbPreGrowAuditEntity.getUseCaseBuilderEntity().getId());
					objEnbPreGrowAuditModel
							.setCustomerName(objEnbPreGrowAuditEntity.getCustomerEntity().getCustomerName());
					enbPreGrowAuditModelList.add(objEnbPreGrowAuditModel);
				}

			}

		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return enbPreGrowAuditModelList;
	}

	/**
	 * This method will return generateFilesListSearch based on Search
	 * 
	 * @param page
	 * @param count
	 * @param objCsvInfoAuditModel
	 * @return objMap
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> generateFilesListSearch(GenerateInfoAuditModel objCsvInfoAuditModel, int customerId, int page, int count) {
		List<GenerateInfoAuditModel> csvInfoAuditModelList = new ArrayList<>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			criteria.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			criteria.createAlias("customerEntity", "customerEntity");
			criteria.setFetchMode("customerEntity.customerDetails", FetchMode.LAZY);
			Conjunction conjunction = Restrictions.conjunction();

			if (objCsvInfoAuditModel != null) {

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getFileName())) {
					Criterion eventfileName = Restrictions.ilike("fileName", objCsvInfoAuditModel.getFileName(),MatchMode.ANYWHERE);
					conjunction.add(eventfileName);
				}

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getCiqFileName())) {
					Criterion eventCiqfileName = Restrictions.ilike("ciqFileName",objCsvInfoAuditModel.getCiqFileName(), MatchMode.ANYWHERE);
					conjunction.add(eventCiqfileName);
				}

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getNeName())) {
					Criterion eventNeName = Restrictions.ilike("neName", objCsvInfoAuditModel.getNeName(),
							MatchMode.ANYWHERE);
					conjunction.add(eventNeName);
				}
				if (CommonUtil.isValidObject(objCsvInfoAuditModel.getProgramDetailsEntity())) {
					Criterion eventNeName = Restrictions.eq("programDetailsEntity", objCsvInfoAuditModel.getProgramDetailsEntity().getId());
					conjunction.add(eventNeName);
				}
				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getSearchStartDate())
						&& StringUtils.isNotEmpty(objCsvInfoAuditModel.getSearchEndDate())) {

					Criterion eventstartDate = Restrictions.ge("gebnerationDate", DateUtil
							.stringToDate(objCsvInfoAuditModel.getSearchStartDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					Criterion eventEndDate = Restrictions.le("gebnerationDate", DateUtil
							.stringToDate(objCsvInfoAuditModel.getSearchEndDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}

			if (customerId != 0) {
				Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
				conjunction.add(eventcustomerId);
			}

			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("networkTypeDetailsEntity.networkType"), "networkType")
					.add(Projections.property("networkTypeDetailsEntity.id"), "networkTypeId")
					.add(Projections.property("csvFileName"), "csvFileName")
					.add(Projections.property("generatedBy"), "generatedBy")
					.add(Projections.property("remarks"), "remarks")
					.add(Projections.property("programName"), "programName")
					.add(Projections.property("ciqFileName"), "ciqFileName")
					.add(Projections.property("neName"), "neName")
					.add(Projections.property("csvFilePath"), "csvFilePath")
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.GENERATION_DATE, '%Y-%m-%d %H:%i:%s') as generationDate",
							new String[] { "generationDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");

			criteria.add(conjunction);
			criteria.setProjection(commonProjection);

			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			csvInfoAuditModelList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(GenerateInfoAuditModel.class)).list();

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			criteriaCount.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			criteriaCount.createAlias("customerEntity", "customerEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;

			objMap.put("count", pagecount);
			objMap.put("list", csvInfoAuditModelList);

		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}


	@Override
	public boolean deleteGeneratedFileDetails(Integer id) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			GenerateInfoAuditEntity entity = getGenerateInfoAuditById(id);
			if (entity != null && !entity.getRemarks().contains("WFM")) {
				entityManager.remove(entity);
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception deleteGeneratedFileDetails() in GenerateRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	@Override
	public GenerateInfoAuditEntity getGenerateInfoAuditById(int articleId) {
		return entityManager.find(GenerateInfoAuditEntity.class, articleId);
	}
	
	/**
	 * this method will return MMEIP array
	 * 
	 * @param user
	 * @param customerId
	 * @return Set<String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public String[] getMmeIpDetails(String market) {
		String[] objIps=null;
		List<MmeIpEntity> objEntityList=null;
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(MmeIpEntity.class);
			criteria.add(Restrictions.eq("market", market));
			
		
			objEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();		
			
			if(objEntityList!=null && objEntityList.size()>0) {
				List<String> objListIps=objEntityList.stream().map(X->X.getMmeIp()).collect(Collectors.toList());
				
				objIps=new String[objListIps.size()];
				objIps=objListIps.toArray(objIps);
			}
		
		} catch (Exception e) {
			logger.error("Exception  getCiqList() in  GenerateRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objIps;
	}
	
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public String[] getBucketDetails(String ciqBucketName) {
		String[] objIps=null;
		List<BucketEntity> bucketEntities=null;
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(BucketEntity.class);
			criteria.add(Restrictions.eq("ciqBucketName", ciqBucketName));
			
		
			bucketEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();		
			
			if(bucketEntities!=null && bucketEntities.size()>0) {
				List<String> objListIps=bucketEntities.stream().map(X->X.getBucketName()).collect(Collectors.toList());
				
				objIps=new String[objListIps.size()];
				objIps=objListIps.toArray(objIps);
			}
		
		} catch (Exception e) {
			logger.error("Exception  getBucketDetails() in  GenerateRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objIps;
	}
	
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public String[] getRrhAluDetails(String oldModel) {
		String[] objIps=null;
		List<RrhAluEntity> aluEntities=null;
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(RrhAluEntity.class);
			criteria.add(Restrictions.eq("oldModel", oldModel));
			
		
			aluEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();		
			
			if(aluEntities!=null && aluEntities.size()>0) {
				List<String> objListIps=aluEntities.stream().map(X->X.getNewModel()).collect(Collectors.toList());
				
				objIps=new String[objListIps.size()];
				objIps=objListIps.toArray(objIps);
			}
		
		} catch (Exception e) {
			logger.error("Exception  getRrhAluDetails() in  GenerateRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objIps;
	}
	
	
	/* this method will return MMEIP array
	 * 
	 * @param user
	 * @param customerId
	 * @return Set<String>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public String[] getVlsmMmeIpDetails(String market) {
		String[] objIps=null;
		List<VlsmMMEIpEntity> objEntityList=null;
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(VlsmMMEIpEntity.class);
			criteria.add(Restrictions.eq("market", market));
			criteria.addOrder(Order.asc("id"));
		
			objEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();		
			
			if(objEntityList!=null && objEntityList.size()>0) {
				List<String> objListIps=objEntityList.stream().map(X->X.getMmeIp()).collect(Collectors.toList());
				
				objIps=new String[objListIps.size()];
				objIps=objListIps.toArray(objIps);
			}
		
		} catch (Exception e) {
			logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objIps;
	}


	@Override
	public boolean updateGeneratedFileDetails(GenerateInfoAuditEntity generateInfoAuditEntity) {
		boolean status = false;
		try {
			entityManager.merge(generateInfoAuditEntity);
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

}
