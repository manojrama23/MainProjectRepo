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
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeTypeModel;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.Ip;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.models.CsvInfoAuditModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.repository.GenerateCsvRepository;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class GenerateCsvRepositoryImpl implements GenerateCsvRepository {
	final static Logger logger = LoggerFactory.getLogger(GenerateCsvRepositoryImpl.class);

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
			logger.error("Exception findAll() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return list;
	}

	@Override
	public boolean saveCsvAudit(GenerateInfoAuditEntity objInfo) {
		boolean status = false;
		try {
			entityManager.persist(objInfo);
			status = true;

		} catch (Exception e) {
			logger.error("Exception saveCsvAudit() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> getCsvAuditDetails(CsvInfoAuditModel csvModel,int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<GenerateInfoAuditEntity> generateInfoAuditEntities = null;
		List<GenerateInfoAuditEntity> searchList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {

			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (csvModel != null) {
				if (StringUtils.isNotEmpty(csvModel.getCsvFileName())) {
					Criterion csvFileName = Restrictions.eq("csvFileName", csvModel.getCsvFileName());
					conjunction.add(csvFileName);
				}
				if (StringUtils.isNotEmpty(csvModel.getCiqFileName())) {
					Criterion ciqFileName = Restrictions.eq("ciqFileName", csvModel.getCiqFileName());
					conjunction.add(ciqFileName);
				}
				if (StringUtils.isNotEmpty(csvModel.getNeName())) {
					Criterion neName = Restrictions.eq("neName", csvModel.getNeName());
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(csvModel.getFileType())) {
					Criterion fileType = Restrictions.eq("fileType", csvModel.getFileType());
					conjunction.add(fileType);
				}
				if (csvModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", csvModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (csvModel.getSearchStartDate() != null && !"".equals(csvModel.getSearchStartDate()) && csvModel.getSearchEndDate() != null && !"".equals(csvModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("generationDate", DateUtil.stringToDate(csvModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("generationDate", DateUtil.stringToDateEndTime(csvModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			
			criteria.add(Restrictions.or(Restrictions.eq("csvFileName", csvModel.getCsvFileName()), Restrictions.eq("neName", csvModel.getNeName())));
			
			
			
				criteria.add(conjunction);
				criteria.setFirstResult((page - 1) * count);
				criteria.setMaxResults(count);
				generateInfoAuditEntities = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
				
				org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
						.createCriteria(GenerateInfoAuditEntity.class);
				criteriaList.createAlias("programDetailsEntity", "programDetailsEntity");
				criteriaList.add(
						Restrictions.eq("programDetailsEntity.id", csvModel.getProgramDetailsEntity().getId()));
				searchList = criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

				Set<String> ciqName = searchList.stream().map(x -> x.getCiqFileName()).sorted()
						.collect(Collectors.toSet());
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
				objMap.put("csvfileName", csvfileName);
				objMap.put("neName", neName);	
			objMap.put("paginationcount", pagecount);
			objMap.put("ciqList", generateInfoAuditEntities);
		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in LsmRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
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
	public List<CsvInfoAuditModel> getCsvFilesList(int customerId) {
		List<GenerateInfoAuditEntity> generateInfoAuditEntities = null;
		List<CsvInfoAuditModel> csvInfoAuditModelList = new ArrayList<>();
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
					CsvInfoAuditModel objCsvInfoAuditModel = new CsvInfoAuditModel();

					objCsvInfoAuditModel.setCsvFileName(objGenerateInfoAuditEntity.getFileName());
					objCsvInfoAuditModel.setCsvFilePath(objGenerateInfoAuditEntity.getFilePath());
					objCsvInfoAuditModel.setGenerationDate(CommonUtil
							.dateToString(objGenerateInfoAuditEntity.getGenerationDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					objCsvInfoAuditModel.setGeneratedBy(objGenerateInfoAuditEntity.getGeneratedBy());
					objCsvInfoAuditModel.setId(objGenerateInfoAuditEntity.getId());

					csvInfoAuditModelList.add(objCsvInfoAuditModel);
				}

			}

		} catch (Exception e) {
			logger.error("Exception getLsmDetails() in LsmRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
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
					"Exception savepreGrowAudit() in CIQUploadRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
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
			logger.error("Exception getLsmDetails() in LsmRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
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
	public Map<String, Object> generateFilesListSearch(CsvInfoAuditModel objCsvInfoAuditModel, int customerId, int page, int count) {
		List<CsvInfoAuditModel> csvInfoAuditModelList = new ArrayList<>();
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

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getCsvFileName())) {
					Criterion eventfileName = Restrictions.ilike("csvFileName", objCsvInfoAuditModel.getCsvFileName(),
							MatchMode.ANYWHERE);
					conjunction.add(eventfileName);
				}

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getCsvFileName())) {
					Criterion eventCiqfileName = Restrictions.ilike("ciqFileName",
							objCsvInfoAuditModel.getCiqFileName(), MatchMode.ANYWHERE);
					conjunction.add(eventCiqfileName);
				}

				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getNeName())) {
					Criterion eventNeName = Restrictions.ilike("neName", objCsvInfoAuditModel.getNeName(),
							MatchMode.ANYWHERE);
					conjunction.add(eventNeName);
				}
				if (StringUtils.isNotEmpty(objCsvInfoAuditModel.getProgramName())) {
					Criterion eventNeName = Restrictions.ilike("programName", objCsvInfoAuditModel.getProgramName(),
							MatchMode.ANYWHERE);
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
					.setResultTransformer(new AliasToBeanResultTransformer(CsvInfoAuditModel.class)).list();

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
			logger.error("Exception getLsmDetails() in LsmRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<EnbPreGrowAuditEntity> enbEntityList = null;
		List<EnbPreGrowAuditEntity> enbsearchList = new ArrayList<>();
		double result = 0;
		int pagecount = 0;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(EnbPreGrowAuditEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (enbModel != null) {
				if (StringUtils.isNotEmpty(enbModel.getCiqFileName())) {
					Criterion ciqFileName = Restrictions.eq("ciqFileName", enbModel.getCiqFileName());
					conjunction.add(ciqFileName);
				}
				if (StringUtils.isNotEmpty(enbModel.getNeName())) {
					Criterion neName = Restrictions.eq("neName", enbModel.getNeName());
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(enbModel.getGrowingName())) {
					Criterion growingName = Restrictions.eq("growingName", enbModel.getGrowingName());
					conjunction.add(growingName);
				}
				if (StringUtils.isNotEmpty(enbModel.getSmVersion())) {
					 criteria.createAlias("neVersionEntity", "neVersionEntity");
					Criterion smVersion = Restrictions.eq("neVersionEntity.id", enbModel.getNeVersionEntity().getId());
					conjunction.add(smVersion);
				}
				if (StringUtils.isNotEmpty(enbModel.getSmName())) {
					 criteria.createAlias("neTypeEntity", "neTypeEntity");
					Criterion smName = Restrictions.eq("neTypeEntity.id", enbModel.getNeTypeEntity().getId());
					conjunction.add(smName);
				}
				if (StringUtils.isNotEmpty(enbModel.getUseCaseName())) {
					 criteria.createAlias("useCaseBuilderEntity", "useCaseBuilderEntity");
					Criterion useCaseName = Restrictions.eq("useCaseBuilderEntity.id", enbModel.getUseCaseBuilderEntity().getId());
					conjunction.add(useCaseName);
				}
				if (enbModel.getProgramDetailsEntity().getId() !=null ){
					 criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					 Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id", enbModel.getProgramDetailsEntity().getId());
					 conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);
				if (enbModel.getSearchStartDate() != null && !"".equals(enbModel.getSearchStartDate()) && enbModel.getSearchEndDate() != null && !"".equals(enbModel.getSearchEndDate())) {
					Criterion eventstartDate = Restrictions.ge("growingDate", DateUtil.stringToDate(enbModel.getSearchStartDate(), Constants.MM_DD_YYYY));
					Criterion eventEndDate = Restrictions.le("growingDate", DateUtil.stringToDateEndTime(enbModel.getSearchEndDate(), Constants.MM_DD_YYYY));
					conjunction.add(eventstartDate);
					conjunction.add(eventEndDate);
				}
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			enbEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			org.hibernate.Criteria criteriaList = entityManager.unwrap(Session.class)
					.createCriteria(EnbPreGrowAuditEntity.class);
			Set<String> growTemplate = enbsearchList.stream().map(x -> x.getGrowingName()).sorted()
					.collect(Collectors.toSet());
			
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(EnbPreGrowAuditEntity.class);
			criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.createAlias("neVersionEntity", "neVersionEntity");
			criteriaCount.createAlias("neTypeEntity", "neTypeEntity");
			criteriaCount.createAlias("useCaseBuilderEntity", "useCaseBuilderEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
		objMap.put("growTemplate", growTemplate);
		objMap.put("paginationcount", pagecount);
		objMap.put("ciqList", enbEntityList);
			
		}
		catch (Exception e) {
			logger.error("Exception getNeGrowDetails() in GenerateCsvRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
	
	
	
	
	@Override
	public List<CiqUploadAuditTrailDetEntity> getciqList(EnbPreGrowAuditModel enbModel,CiqUploadAuditTrailDetModel ciqModel) {
		List<CiqUploadAuditTrailDetEntity> ciqEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CiqUploadAuditTrailDetEntity> query = builder.createQuery(CiqUploadAuditTrailDetEntity.class);
			Root<CiqUploadAuditTrailDetEntity> root = query.from(CiqUploadAuditTrailDetEntity.class);
			query.select(root);
			query.where(
					builder.equal(root.get("programDetailsEntity"), enbModel.getProgramDetailsEntity().getId()));
			if (CommonUtil.isValidObject(ciqModel) && CommonUtil.isValidObject(ciqModel.getId())) {
				builder.equal(root.get("id"), ciqModel.getId());
			}
			if (CommonUtil.isValidObject(ciqModel) && CommonUtil.isValidObject(ciqModel.getCiqFileName())) {
				builder.like(root.get("ciqFileName"), ciqModel.getCiqFileName().trim());
			}
			TypedQuery<CiqUploadAuditTrailDetEntity> queryResult = entityManager.createQuery(query);
			ciqEntity = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in GenerateCsvRepositoryImpl.getciqList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ciqEntity;
	}

	@Override
	public List<GenerateInfoAuditEntity> getNeNameList(EnbPreGrowAuditModel enbModel,CsvInfoAuditModel csvModel) {
		// TODO Auto-generated method stub
		List<GenerateInfoAuditEntity> csvEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<GenerateInfoAuditEntity> query = builder.createQuery(GenerateInfoAuditEntity.class);
			Root<GenerateInfoAuditEntity> root = query.from(GenerateInfoAuditEntity.class);
			query.select(root);
			query.where(
					builder.equal(root.get("programDetailsEntity"), enbModel.getProgramDetailsEntity().getId()));
			if (CommonUtil.isValidObject(csvModel) && CommonUtil.isValidObject(csvModel.getId())) {
				builder.equal(root.get("id"), csvModel.getId());
			}
			if (CommonUtil.isValidObject(csvModel) && CommonUtil.isValidObject(csvModel.getNeName())) {
				builder.like(root.get("neName"), csvModel.getNeName().trim());
			}
			TypedQuery<GenerateInfoAuditEntity> queryResult = entityManager.createQuery(query);
			csvEntity = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in GenerateCsvRepositoryImpl.getNeNameList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return csvEntity;
	}

	@Override
	public List<NeVersionEntity> getSmVersionList(EnbPreGrowAuditModel enbModel,NeVersionModel neVersionModel) {
		// TODO Auto-generated method stub
		List<NeVersionEntity> neVersionEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> query = builder.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> root = query.from(NeVersionEntity.class);
			query.select(root);
			query.where(
					builder.equal(root.get("programDetailsEntity"), enbModel.getProgramDetailsEntity().getId()));
			if (CommonUtil.isValidObject(neVersionModel) && CommonUtil.isValidObject(neVersionModel.getId())) {
				builder.equal(root.get("id"), neVersionModel.getId());
			}
			if (CommonUtil.isValidObject(neVersionModel) && CommonUtil.isValidObject(neVersionModel.getNeVersion())) {
				builder.like(root.get("neVersion"), neVersionModel.getNeVersion().trim());
			}
			TypedQuery<NeVersionEntity> queryResult = entityManager.createQuery(query);
			neVersionEntity = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in GenerateCsvRepositoryImpl.getSmVersionList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neVersionEntity;
	}

	@Override
	public List<NeTypeEntity> getSmNameList(NeTypeModel neTypeModel) {
		// TODO Auto-generated method stubgetip
		List<NeTypeEntity> neTypeEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeTypeEntity> query = builder.createQuery(NeTypeEntity.class);
			Root<NeTypeEntity> root = query.from(NeTypeEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(neTypeModel) && CommonUtil.isValidObject(neTypeModel.getId())) {
				builder.equal(root.get("id"), neTypeModel.getId());
			}
			if (CommonUtil.isValidObject(neTypeModel) && CommonUtil.isValidObject(neTypeModel.getNeType())) {
				builder.like(root.get("neType"), neTypeModel.getNeType().trim());
			}
			TypedQuery<NeTypeEntity> queryResult = entityManager.createQuery(query);
			neTypeEntity = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in GenerateCsvRepositoryImpl.getSmNameList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neTypeEntity;
	}

	@Override
	public List<UseCaseBuilderEntity> getUseCaseList(UseCaseBuilderModel useCaseBuilderModel) {
		// TODO Auto-generated method stub
		List<UseCaseBuilderEntity> useCaseEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(useCaseBuilderModel) && CommonUtil.isValidObject(useCaseBuilderModel.getId())) {
				builder.equal(root.get("id"), useCaseBuilderModel.getId());
			}
			if (CommonUtil.isValidObject(useCaseBuilderModel) && CommonUtil.isValidObject(useCaseBuilderModel.getUseCaseName())) {
				builder.like(root.get("useCaseName"), useCaseBuilderModel.getUseCaseName().trim());
			}
			TypedQuery<UseCaseBuilderEntity> queryResult = entityManager.createQuery(query);
			useCaseEntity = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in GenerateCsvRepositoryImpl.getUseCaseList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseEntity;
	}
	
	@Override
	public Ip getip(String marketid) {
		Ip useCaseBuilderEntityList = null;
		try {
			Criteria crit = entityManager.unwrap(Session.class).createCriteria(Ip.class);
			crit.add(Restrictions.eq("marketid", marketid));
			useCaseBuilderEntityList = (Ip) crit.uniqueResult();
		} catch (Exception e) {
			logger.info(
					"Exception in GenerateCsvRepositoryImpl.getUseCaseList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntityList;
	}
	
	@Override
	public List<NeMappingEntity> getVersion(String enbid) {
		List<NeMappingEntity> EntityList = null;
		try {
			Criteria crit = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			crit.add(Restrictions.eq("enbId", enbid));
			EntityList = crit.list();
		} catch (Exception e) {
			logger.info(
					"Exception in GenerateCsvRepositoryImpl.getUseCaseList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return EntityList;
	}
}
