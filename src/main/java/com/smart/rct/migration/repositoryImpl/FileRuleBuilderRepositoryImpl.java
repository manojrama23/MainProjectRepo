package com.smart.rct.migration.repositoryImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.migration.repository.FileRuleBuilderRepository;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.User;


@Repository
@Transactional
public class FileRuleBuilderRepositoryImpl implements FileRuleBuilderRepository {

	final static Logger logger = LoggerFactory.getLogger(FileRuleBuilderRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * this method will create the file rule builder Details.
	 * 
	 * @param fileRuleBuilderEntity
	 * @return boolean
	 */
	@Override
	public boolean createFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity) {

		boolean status = false;
		try {
			entityManager.persist(fileRuleBuilderEntity);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  createFileRuleBuilder() in  FileRuleBuilderRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will search the file rule builder Details.
	 * 
	 * @param searchBy,searchParameter,page,count
	 * @return map
	 */
	@SuppressWarnings("unchecked")
	/*
	 * @SuppressWarnings("deprecation")
	 * 
	 * @Override public Map<String, Object> searchByRuleName(String searchBy, String
	 * searchParameter, int customerId, int page, int count) {
	 * List<FileRuleBuilderEntity> objList = null; Map<String, Object> objMap = new
	 * HashMap<String, Object>(); BigInteger totcountdd = null; int pageNo = (page -
	 * 1) * count; Double pageNationCount = null; try { Session session =
	 * entityManager.unwrap(Session.class).getSession(); StringBuilder objBuilder =
	 * new StringBuilder(); StringBuilder objquery = new StringBuilder(); if
	 * (StringUtils.isNotEmpty(searchBy) || StringUtils.isNotEmpty(searchParameter))
	 * { if ("ruleName".equals(searchBy)) { objBuilder.setLength(0);
	 * 
	 * if (page != 0) { objBuilder.append(
	 * "SELECT COUNT(*) FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_ID = " + "'" +
	 * customerId + "'" + " AND c.RULE_NAME like " + "'" + "%" + searchParameter +
	 * "%" + "'");
	 * 
	 * } objquery.setLength(0); objquery.append(
	 * "SELECT c.ID as id, c.RULE_NAME as ruleName,c.SEARCH_PARAMETER as searchParameter,c.FILE_NAME as fileName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_ID = "
	 * + customerId + " AND c.RULE_NAME like " + "'" + "%" + searchParameter + "%" +
	 * "' order by c.ID " + " limit " + " " + pageNo + "," + count + " ");
	 * 
	 * } else if ("searchParameter".equals(searchBy)) { objBuilder.setLength(0);
	 * 
	 * if (page != 0) { objBuilder.append(
	 * "SELECT count(*) FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_NAME = " +
	 * customerId + " AND c.SEARCH_PARAMETER like " + "'" + "%" + searchParameter +
	 * "%" + "'");
	 * 
	 * } objquery.setLength(0); objquery.append(
	 * "SELECT c.ID as id, c.RULE_NAME as ruleName,c.SEARCH_PARAMETER as searchParameter,c.FILE_NAME as fileName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_ID = "
	 * + customerId + " AND c.SEARCH_PARAMETER like " + "'" + "%" + searchParameter
	 * + "%" + "' order by c.ID " + " limit " + " " + pageNo + "," + count + " ");
	 * 
	 * }
	 * 
	 * } else {
	 * 
	 * objBuilder.setLength(0);
	 * 
	 * if (page != 0) { objBuilder.
	 * append("SELECT COUNT(*) FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_ID = "
	 * + "'" + customerId + "'");
	 * 
	 * } objquery.setLength(0); objquery.append(
	 * "SELECT c.ID as id, c.RULE_NAME as ruleName,c.SEARCH_PARAMETER as searchParameter,c.FILE_NAME as fileName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_FILE_RULE_BUILDER c where c.CUSTOMER_ID = "
	 * + customerId + "  order by c.ID " + " limit " + " " + pageNo + "," + count +
	 * " ");
	 * 
	 * } if (objquery.length() > 0 && objBuilder.length() > 0) { // for count
	 * SQLQuery query = session.createSQLQuery(objBuilder.toString()); totcountdd =
	 * (BigInteger) query.uniqueResult(); Double countdet = Double.valueOf(count);
	 * Double totcountddet = totcountdd.doubleValue(); pageNationCount =
	 * Math.ceil(totcountddet / countdet);
	 * 
	 * // for records SQLQuery q = session.createSQLQuery(objquery.toString());
	 * q.setResultTransformer(Transformers.aliasToBean(FileRuleBuilderEntity.class))
	 * ; objList = q.list(); } objMap.put("fileRuleDetails", objList);
	 * objMap.put("totalCount", pageNationCount.intValue());
	 * 
	 * } catch (Exception e) { logger.
	 * error("Exception searchByRuleName() in FileRuleBuilderRepositoryImpl :" +
	 * ExceptionUtils.getFullStackTrace(e)); } finally { entityManager.flush();
	 * entityManager.clear(); } return objMap; }
	 */
	@Override
	public Map<String, Object> loadFileRuleBuilderSearchDetails(int customerId,
			int page, int count, String migrationType, int programId, String subType, User user) {
		Map<String, Object> objMap = new HashMap<String, Object>();

		List<FileRuleBuilderModel> fileRuleBuilderList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;

		try {
			
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(FileRuleBuilderEntity.class);
			Conjunction conjunction = Restrictions.conjunction();

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				conjunction.add(Restrictions.eq("customerId", customerId));
			}
			
			if (StringUtils.isNotEmpty(subType)) {
				conjunction.add(Restrictions.and(Restrictions.eq("migrationType", migrationType),
					Restrictions.eq("customerDetailsEntity.id", programId), Restrictions.eq("subType", subType)));
			}else {
				conjunction.add(Restrictions.and(Restrictions.eq("migrationType", migrationType),
						Restrictions.eq("customerDetailsEntity.id", programId)));
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			List<FileRuleBuilderEntity> fileRuleBuilderEntityList = criteria.list();
		//	List<FileRuleBuilderEntity> fileRuleBuilderEntityList = criteria
		//			.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (fileRuleBuilderEntityList != null && fileRuleBuilderEntityList.size() > 0)
				for (FileRuleBuilderEntity objFileRuleBuilderEntity : fileRuleBuilderEntityList) {

					Date date = objFileRuleBuilderEntity.getCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					FileRuleBuilderModel objFileRuleBuilderModel = new FileRuleBuilderModel();

					objFileRuleBuilderModel.setFileName(objFileRuleBuilderEntity.getFileName());
					objFileRuleBuilderModel.setRuleName(objFileRuleBuilderEntity.getRuleName());
					objFileRuleBuilderModel.setId(objFileRuleBuilderEntity.getId());
					objFileRuleBuilderModel.setSearchParameter(objFileRuleBuilderEntity.getSearchParameter());
					objFileRuleBuilderModel.setStatus(objFileRuleBuilderEntity.getStatus());
					objFileRuleBuilderModel.setUseCount(objFileRuleBuilderEntity.getUseCount());
					objFileRuleBuilderModel.setCreatedBy(objFileRuleBuilderEntity.getCreatedBy());
					objFileRuleBuilderModel.setRemarks(objFileRuleBuilderEntity.getRemarks());
					objFileRuleBuilderModel.setTimeStamp(dateFormat);
					fileRuleBuilderList.add(objFileRuleBuilderModel);

				}

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(FileRuleBuilderEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("fileRuleDetails", fileRuleBuilderList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception loadFileRuleBuilderSearchDetails() in LsmRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * this method will delete the file rule Details.
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteFileRule(int id) {
		boolean status = false;
		try {
			entityManager.remove(getById(id));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception  deleteFileRule() in  FileRuleBuilderRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will update file rule builder details to DBu.RULE_NAME as
	 * ruleName,u.SEARCH_PARAMETER as searchParameter,u.STATUS as status FROM
	 * FILE_RULE_BUILDER where u.ID=cd.id
	 * 
	 * @param fileRuleBuilderEntity
	 * @return boolean
	 */

	@Override
	public boolean updateFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity) {

		boolean status = false;
		try {
			entityManager.merge(fileRuleBuilderEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception updateFileRuleBuilder() in FileRuleBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public FileRuleBuilderEntity getById(int id) {
		return entityManager.find(FileRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will check duplicate rule name
	 * 
	 * @param ruleName
	 * @return boolean
	 */

	@Override
	public boolean findByRuleName(String ruleName,int customerId, String migrationType, int programId, String userRole,String subType) {

		FileRuleBuilderEntity fileEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<FileRuleBuilderEntity> query = builder.createQuery(FileRuleBuilderEntity.class);
			Root<FileRuleBuilderEntity> root = query.from(FileRuleBuilderEntity.class);
			query.select(root);
			
			if (userRole.equalsIgnoreCase(Constants.SUPER_ROLE_ADMIN)) {
				query.where(builder.and(builder.equal(root.get("ruleName"), ruleName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			} else {
				query.where(builder.and(builder.equal(root.get("customerId"), customerId),
						builder.equal(root.get("ruleName"), ruleName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			}			
			TypedQuery<FileRuleBuilderEntity> queryResult = entityManager.createQuery(query);
			fileEntity = (FileRuleBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

			if (fileEntity != null) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Exception findByRuleName() in FileRuleBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public Map<String, Object> loadFileRuleBuilderSearchDetails(FileRuleBuilderModel fileRuleBuilderModel,
			int customerId, int page, int count, String migrationType, int programId, String subType, User user) {
		Map<String, Object> objMap = new HashMap<String, Object>();

		List<FileRuleBuilderModel> fileRuleBuilderList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;

		try {
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(FileRuleBuilderEntity.class);
			Conjunction objConjunction = Restrictions.conjunction();
			
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				objConjunction.add(Restrictions.eq("customerId", customerId));
			}

			if (StringUtils.isNotEmpty(fileRuleBuilderModel.getFileName())) {
				objConjunction.add(Restrictions.ilike("fileName", fileRuleBuilderModel.getFileName().trim()));
			}
			if (StringUtils.isNotEmpty(fileRuleBuilderModel.getRuleName())) {
				objConjunction.add(Restrictions.ilike("ruleName", fileRuleBuilderModel.getRuleName().trim()));
			}
			if (StringUtils.isNotEmpty(fileRuleBuilderModel.getSearchParameter())) {
				objConjunction.add(Restrictions.ilike("searchParameter", fileRuleBuilderModel.getSearchParameter().trim()));
			}
			if (StringUtils.isNotEmpty(fileRuleBuilderModel.getStatus())) {
				objConjunction.add(Restrictions.ilike("status", fileRuleBuilderModel.getStatus().trim()));
			}
			if (StringUtils.isNotEmpty(fileRuleBuilderModel.getCreatedBy())) {
				objConjunction.add(Restrictions.ilike("createdBy", fileRuleBuilderModel.getCreatedBy().trim()));
			}
			
			
			objConjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));
			objConjunction.add(Restrictions.eq("migrationType", migrationType));
			if (StringUtils.isNotEmpty(subType)) {
				objConjunction.add(Restrictions.eq("subType", subType));
			}
			criteria.add(objConjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));

			List<FileRuleBuilderEntity> fileRuleBuilderEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (fileRuleBuilderEntityList != null && fileRuleBuilderEntityList.size() > 0)
				for (FileRuleBuilderEntity objFileRuleBuilderEntity : fileRuleBuilderEntityList) {
					Date date = objFileRuleBuilderEntity.getCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					FileRuleBuilderModel objFileRuleBuilderModel = new FileRuleBuilderModel();

					objFileRuleBuilderModel.setFileName(objFileRuleBuilderEntity.getFileName());
					objFileRuleBuilderModel.setRuleName(objFileRuleBuilderEntity.getRuleName());
					objFileRuleBuilderModel.setId(objFileRuleBuilderEntity.getId());
					objFileRuleBuilderModel.setSearchParameter(objFileRuleBuilderEntity.getSearchParameter());
					objFileRuleBuilderModel.setStatus(objFileRuleBuilderEntity.getStatus());
					objFileRuleBuilderModel.setUseCount(objFileRuleBuilderEntity.getUseCount());
					objFileRuleBuilderModel.setCreatedBy(objFileRuleBuilderEntity.getCreatedBy());
					objFileRuleBuilderModel.setTimeStamp(dateFormat);
					objFileRuleBuilderModel.setRemarks(objFileRuleBuilderEntity.getRemarks());
					fileRuleBuilderList.add(objFileRuleBuilderModel);

				}

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(FileRuleBuilderEntity.class);
			criteriaCount.add(objConjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("fileRuleDetails", fileRuleBuilderList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception loadFileRuleBuilderSearchDetails() in LsmRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

}
