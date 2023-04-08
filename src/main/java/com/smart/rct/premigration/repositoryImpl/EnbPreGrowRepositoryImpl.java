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
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.repository.EnbPreGrowRepository;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;

@Transactional
@Repository
public class EnbPreGrowRepositoryImpl implements EnbPreGrowRepository{
	final static Logger logger = LoggerFactory.getLogger(EnbPreGrowRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	
	/**
	 * This api is to get the NeGrowDetails
	 * 
	 * @param enbModel,page,count
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count) {
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
					Criterion smVersion = Restrictions.eq("smVersion", enbModel.getSmVersion());
					conjunction.add(smVersion);
				}
				if (StringUtils.isNotEmpty(enbModel.getSmName())) {
					Criterion smName = Restrictions.eq("smName", enbModel.getSmName());
					conjunction.add(smName);
				}
				if (StringUtils.isNotEmpty(enbModel.getUseCaseName())) {
					Criterion useCaseName = Restrictions.eq("usecaseName", enbModel.getUseCaseName());
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
			criteriaList.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaList.add(
					Restrictions.eq("programDetailsEntity.id", enbModel.getProgramDetailsEntity().getId()));
			enbsearchList=criteriaList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			Set<String> ciqSearchList = enbsearchList.stream().map(x -> x.getCiqFileName()).sorted()
					.collect(Collectors.toSet());
			Set<String> smVersionSearchList = enbsearchList.stream().map(x -> x.getSmVersion()).sorted()
					.collect(Collectors.toSet());
			Set<String> smNameSearchList = enbsearchList.stream().map(x -> x.getSmName()).sorted()
					.collect(Collectors.toSet());
			Set<String> usecaseSearchList = enbsearchList.stream().map(x -> x.getUsecaseName()).sorted()
					.collect(Collectors.toSet());
			Set<String> neNameSearchList = enbsearchList.stream().map(x -> x.getNeName()).sorted()
					.collect(Collectors.toSet());
			org.hibernate.Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(EnbPreGrowAuditEntity.class);
			criteriaCount.createAlias("programDetailsEntity", "programDetailsEntity");
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("ciqSearchList",ciqSearchList);
			objMap.put("smVersionSearchList",smVersionSearchList);
			objMap.put("neNameSearchList",neNameSearchList);
			objMap.put("smNameSearchList",smNameSearchList);
			objMap.put("usecaseSearchList",usecaseSearchList);
			objMap.put("paginationcount", pagecount);
			objMap.put("preGrowList", enbEntityList);
			
		}
		catch (Exception e) {
			logger.error("Exception getNeGrowDetails() in GenerateCsvRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
	
	/**
	 * This api is to get the usecase List
	 * 
	 * @param useCaseBuilderModel
	 * @return List<UseCaseBuilderEntity>
	 */
	@Override
	public List<UseCaseBuilderEntity> getUseCaseList(UseCaseBuilderModel useCaseBuilderModel) {
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
	
	/**
	 * This api is to get the CIQ List from Retrieve CIQ table
	 * 
	 * @param user,programId,fromDate,toDate
	 * @return List<CiqUploadAuditTrailDetEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId,String fromDate,String toDate) {
		List<CiqUploadAuditTrailDetEntity> ciqList = new ArrayList<>();
		
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(CiqUploadAuditTrailDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.addOrder(Order.desc("ciqFileName"));
			Conjunction conjunction = Restrictions.conjunction();
			
			if (StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
				Criterion eventstartDate = Restrictions.ge("creationDate", DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
				Criterion eventEndDate = Restrictions.le("creationDate", DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
				conjunction.add(eventstartDate);
				conjunction.add(eventEndDate);
			}
			criteria.add(Restrictions.eq("programDetailsEntity.id", Integer.parseInt(programId)));
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
	 * This api is to get the SM version and SM Names
	 * 
	 * @param programId
	 * @return Map<String, List<String>>
	 */
	@Override
	public Map<String, List<String>> getSmDetails(Integer programId) {
		// TODO Auto-generated method stub
		List<NeVersionEntity> neVersionIdList = null;
		Map<String, List<String>> neList = new HashMap<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			neVersionIdList = (List<NeVersionEntity>) crTypedQuery.getResultList();
			for (NeVersionEntity neVersionEntity : neVersionIdList) {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> query = cb.createQuery(String.class);
				Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
				query.select(root.<String>get("neName")).where(cb.equal(root.get("neVersionEntity"), neVersionEntity));
				TypedQuery<String> typedQuery = entityManager.createQuery(query);
				List<String> smNameList = typedQuery.getResultList();
				neList.put(neVersionEntity.getNeVersion(),smNameList);
			}
	}catch (Exception e) {
		logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
		return neList;
	}
	
	
	@Override
	public Map<String, List<String>> getSmSearchDetails(Integer programId) {
		// TODO Auto-generated method stub
		List<EnbPreGrowAuditEntity> neVersionIdList = null;
		Map<String, List<String>> neList = new HashMap<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<EnbPreGrowAuditEntity> crQuery = criteria.createQuery(EnbPreGrowAuditEntity.class);
			Root<EnbPreGrowAuditEntity> crRoot = crQuery.from(EnbPreGrowAuditEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<EnbPreGrowAuditEntity> crTypedQuery = entityManager.createQuery(crQuery);
			neVersionIdList = (List<EnbPreGrowAuditEntity>) crTypedQuery.getResultList();
			for (EnbPreGrowAuditEntity neVersionEntity : neVersionIdList) {
				CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> query = criteriabuilder.createQuery(String.class);
				Root<EnbPreGrowAuditEntity> root = query.from(EnbPreGrowAuditEntity.class);
				query.select(root.<String>get("smName")).where(criteriabuilder.equal(root.get("smVersion"), neVersionEntity.getSmVersion()));
				TypedQuery<String> typedQuery = entityManager.createQuery(query);
				List<String> smNameList = typedQuery.getResultList();
				neList.put(neVersionEntity.getSmVersion(),smNameList);
			}
	}catch (Exception e) {
		logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
		return neList;
	}

	@Override
	public Map<String, List<String>> getCiqNeSearchDetails(Integer programId) {
		List<EnbPreGrowAuditEntity> ciqSearchList = null;
		Map<String, List<String>> neList = new HashMap<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<EnbPreGrowAuditEntity> crQuery = criteria.createQuery(EnbPreGrowAuditEntity.class);
			Root<EnbPreGrowAuditEntity> crRoot = crQuery.from(EnbPreGrowAuditEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<EnbPreGrowAuditEntity> crTypedQuery = entityManager.createQuery(crQuery);
			ciqSearchList = (List<EnbPreGrowAuditEntity>) crTypedQuery.getResultList();
			for (EnbPreGrowAuditEntity ciqListEntity : ciqSearchList) {
				CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> query = criteriabuilder.createQuery(String.class);
				Root<EnbPreGrowAuditEntity> root = query.from(EnbPreGrowAuditEntity.class);
				query.select(root.<String>get("neName")).where(criteriabuilder.equal(root.get("ciqFileName"), ciqListEntity.getCiqFileName()));
				TypedQuery<String> typedQuery = entityManager.createQuery(query);
				List<String> neNameList = typedQuery.getResultList();
				neList.put(ciqListEntity.getCiqFileName(),neNameList);
			}
	}catch (Exception e) {
		logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
		return neList;
	}
	
	
}
