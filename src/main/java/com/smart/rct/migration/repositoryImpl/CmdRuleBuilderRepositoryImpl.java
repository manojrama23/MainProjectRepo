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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleBuilderModel;
import com.smart.rct.migration.repository.CmdRuleBuilderRepository;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.User;

@SuppressWarnings("deprecation")
@Repository
@Transactional
public class CmdRuleBuilderRepositoryImpl implements CmdRuleBuilderRepository {

	final static Logger logger = LoggerFactory.getLogger(CmdRuleBuilderRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * this method will create the command rule builder Details.
	 * 
	 * @param cmdRuleEntity
	 * @return boolean
	 */
	@Override
	public boolean createCmdRule(CmdRuleBuilderEntity cmdRuleEntity) {

		boolean status = false;
		try {
			entityManager.persist(cmdRuleEntity);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  createCmdRule() in  CmdRuleBuilderRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will search the command rule builder Details.
	 * 
	 * @param searchBy,searchParameter,page,count
	 * @return map
	 */
	
	/*@Override
	public Map<String, Object> searchCmdRule(String searchBy, String searchParameter, int customerId, int page,
			int count) {
		List<FileRuleBuilderEntity> objList = null;
		Map<String, Object> objMap = new HashMap<String, Object>();
		BigInteger totcountdd = null;
		int pageNo = (page - 1) * count;
		Double pageNationCount = null;
		try {
			Session session = entityManager.unwrap(Session.class).getSession();
			StringBuilder objBuilder = new StringBuilder();
			StringBuilder objquery = new StringBuilder();
			if (StringUtils.isNotEmpty(searchBy) || StringUtils.isNotEmpty(searchParameter)) {
				if ("ruleName".equals(searchBy)) {
					objBuilder.setLength(0);

					if (page != 0) {
						objBuilder.append("SELECT COUNT(*) FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = "
								+ customerId + " AND c.RULE_NAME like " + "'" + "%" + searchParameter + "%" + "'");

					}
					objquery.setLength(0);
					objquery.append(
							"SELECT c.ID as id,c.CMD_DESC as commandDescription,c.CMD_NAME as cmdName,c.OPERAND1 as operand1,c.OPERAND1_COLUMN_NAME as operand1ColumnName,c.OPERAND2 as operand2,c.OPERAND2_COLUMN_NAME as operand2ColumnName,c.OPERATOR as operator,c.RULE_NAME as ruleName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = "
									+ customerId + " AND c.RULE_NAME like " + "'" + "%" + searchParameter + "%"
									+ "' order by c.ID " + " limit " + " " + pageNo + "," + count + " ");

				} else if ("cmdName".equals(searchBy)) {
					objBuilder.setLength(0);

					if (page != 0) {
						objBuilder.append("SELECT count(*) FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = "
								+ customerId + " AND c.CMD_NAME like " + "'" + "%" + searchParameter + "%" + "'");

					}
					objquery.setLength(0);
					objquery.append(
							"SELECT c.ID as id,c.CMD_DESC as commandDescription,c.CMD_NAME as cmdName,c.OPERAND1 as operand1,c.OPERAND1_COLUMN_NAME as operand1ColumnName,c.OPERAND2 as operand2,c.OPERAND2_COLUMN_NAME as operand2ColumnName,c.OPERATOR as operator,c.RULE_NAME as ruleName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = "
									+ customerId + " AND c.CMD_NAME like " + "'" + "%" + searchParameter + "%"
									+ "' order by c.ID " + " limit " + " " + pageNo + "," + count + " ");

				}

			} else {

				objBuilder.setLength(0);

				if (page != 0) {
					objBuilder
							.append("SELECT COUNT(*) FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = " + customerId);

				}
				objquery.setLength(0);
				objquery.append(
						"SELECT c.ID as id,c.CMD_DESC as commandDescription,c.CMD_NAME as cmdName,c.OPERAND1 as operand1,c.OPERAND1_COLUMN_NAME as operand1ColumnName,c.OPERAND2 as operand2,c.OPERAND2_COLUMN_NAME as operand2ColumnName,c.OPERATOR as operator,c.RULE_NAME as ruleName,c.STATUS as status,c.USE_COUNT as useCount,c.CUSTOMER_ID as customerId FROM MIG_CMD_RULE_BUILDER c where c.CUSTOMER_ID = "
								+ customerId + " order by c.ID " + " limit " + " " + pageNo + "," + count + " ");

			}
			if (objquery.length() > 0 && objBuilder.length() > 0) { // for count
				SQLQuery query = session.createSQLQuery(objBuilder.toString());
				totcountdd = (BigInteger) query.uniqueResult();
				Double countdet = Double.valueOf(count);
				Double totcountddet = totcountdd.doubleValue();
				pageNationCount = Math.ceil(totcountddet / countdet);

				// for records SQLQuery q = session.createSQLQuery(objquery.toString());
				q.setResultTransformer(Transformers.aliasToBean(CmdRuleBuilderEntity.class));
				objList = q.list();
			}
			objMap.put("cmdRuleBuilderData", objList);
			objMap.put("totalCount", pageNationCount.intValue());

		} catch (Exception e) {
			logger.error("Exception searchCmdRule() in CmdRuleBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}*/
	 

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> loadCmdRuleBuilderSearchDetails(int customerId, int page, int count,
			String migrationType, int programId, String subType, User user) {
		Map<String, Object> objMap = new HashMap<String, Object>();

		List<CmdRuleBuilderModel> cmdRuleBuilderList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;

		try {
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CmdRuleBuilderEntity.class);
			// criteria.addOrder(Order.asc("id"));
			Conjunction conjunction = Restrictions.conjunction();
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				conjunction.add(Restrictions.eq("customerId", customerId));
			}
			if (StringUtils.isNotEmpty(subType)) {
				conjunction.add(Restrictions.and(Restrictions.eq("migrationType", migrationType),
						Restrictions.eq("customerDetailsEntity.id", programId), Restrictions.eq("subType", subType)));
			} else {
				conjunction.add(Restrictions.and(Restrictions.eq("migrationType", migrationType),
						Restrictions.eq("customerDetailsEntity.id", programId)));
			}
			criteria.add(conjunction);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			List<CmdRuleBuilderEntity> cmdRuleBuilderEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (cmdRuleBuilderEntityList != null && cmdRuleBuilderEntityList.size() > 0)
				for (CmdRuleBuilderEntity objCmdRuleBuilderEntity : cmdRuleBuilderEntityList) {

					CmdRuleBuilderModel objCmdRuleBuilderModel = new CmdRuleBuilderModel();
					Date date = objCmdRuleBuilderEntity.getCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					
					objCmdRuleBuilderModel.setCmdName(objCmdRuleBuilderEntity.getCmdName());
					objCmdRuleBuilderModel.setRuleName(objCmdRuleBuilderEntity.getRuleName());
					objCmdRuleBuilderModel.setId(objCmdRuleBuilderEntity.getId());
					objCmdRuleBuilderModel.setRemarks(objCmdRuleBuilderEntity.getRemarks());
					objCmdRuleBuilderModel.setOperand1Values(objCmdRuleBuilderEntity.getOperand1Values());
					objCmdRuleBuilderModel.setOperand1ColumnNames(objCmdRuleBuilderEntity.getOperand1ColumnNames());
					objCmdRuleBuilderModel.setOperand2Values(objCmdRuleBuilderEntity.getOperand2Values());
					objCmdRuleBuilderModel.setOperand2ColumnNames(objCmdRuleBuilderEntity.getOperand2ColumnNames());
					objCmdRuleBuilderModel.setUseCount(objCmdRuleBuilderEntity.getUseCount());
					objCmdRuleBuilderModel.setOperator(objCmdRuleBuilderEntity.getOperator());
					objCmdRuleBuilderModel.setStatus(objCmdRuleBuilderEntity.getStatus());
					objCmdRuleBuilderModel.setCreatedBy(objCmdRuleBuilderEntity.getCreatedBy());
					objCmdRuleBuilderModel.setTimeStamp(dateFormat);
					objCmdRuleBuilderModel.setPrompt(objCmdRuleBuilderEntity.getPrompt());
					objCmdRuleBuilderModel.setLoopType(objCmdRuleBuilderEntity.getLoopType());
					cmdRuleBuilderList.add(objCmdRuleBuilderModel);

				}

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(CmdRuleBuilderEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("cmdRuleBuilderData", cmdRuleBuilderList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception loadCmdRuleBuilderSearchDetails() in LsmRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * this method will update command rule builder details to DBu.CMD_DESC as
	 * commandDescription,u.CMD_NAME as cmdName,u.OPERAND1 as
	 * operand1,u.OPERAND1_COLUMN_NAME as operand1ColumnName,u.OPERAND2 as
	 * operand2,u.OPERAND2_COLUMN_NAME as operand2ColumnName,u.OPERATOR as
	 * operator,u.RULE_NAME as ruleName,u.STATUS as status FROM MIG_CMD_RULE_BUILDER
	 * where u.ID=cd.id
	 * 
	 * @param cmdRuleEntity
	 * @return boolean
	 */

	@Override
	public boolean updateCmdRuleBuilder(CmdRuleBuilderEntity cmdRuleEntity) {

		boolean status = false;
		try {
			entityManager.merge(cmdRuleEntity);
			status = true;

		} catch (Exception e) {			
			logger.error("Exception updateCmdRuleBuilder() in CmdRuleBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will delete the command rule Details.
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteCmdRule(int id) {
		boolean status = false;
		try {
			entityManager.remove(getById(id));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception  deleteCmdRule() in  CmdRuleBuilderRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * 
	 * this method will retrieve id
	 * 
	 * @param id
	 * @return CmdRuleBuilderEntity
	 */
	public CmdRuleBuilderEntity getById(int id) {
		return entityManager.find(CmdRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will check duplicate command name
	 * 
	 * @param ruleName
	 * @return boolean
	 */
	@Override
	public boolean findByRuleName(String ruleName, String migrationType, int programId,
			String userRole,String subType) {
		CmdRuleBuilderEntity dupCmdEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CmdRuleBuilderEntity> query = builder.createQuery(CmdRuleBuilderEntity.class);
			Root<CmdRuleBuilderEntity> root = query.from(CmdRuleBuilderEntity.class);
			query.select(root);
			if (userRole.equalsIgnoreCase(Constants.SUPER_ROLE_ADMIN)) {
				query.where(builder.and(builder.equal(root.get("ruleName"), ruleName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			} else {
				query.where(builder.and(
						builder.equal(root.get("ruleName"), ruleName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			}

			TypedQuery<CmdRuleBuilderEntity> queryResult = entityManager.createQuery(query);
			dupCmdEntity = (CmdRuleBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

			if (dupCmdEntity != null) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Exception findByRuleName() in CmdRuleBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public String findCommand(String ruleName,String migrationType,String subType,int programId) {
		String listCmd = null;
		try {
			
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CmdRuleBuilderEntity> query = cb.createQuery(CmdRuleBuilderEntity.class);
			Root<CmdRuleBuilderEntity> root = query.from(CmdRuleBuilderEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("ruleName"), ruleName),
					cb.equal(root.get("migrationType"), migrationType),
					cb.equal(root.get("subType"), subType),
					cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity));

			TypedQuery<CmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			CmdRuleBuilderEntity cmdRuleBuilderEntity = typedQuery.getSingleResult();
			listCmd = cmdRuleBuilderEntity.getCmdName();

		} catch (Exception e) {
			logger.error(
					"Exception findCommand() in CmdRuleBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return listCmd;
	}

	@Override
	public CustomerDetailsEntity getCustomerDetailsEntity(int customerDetailsId) {
		CustomerDetailsEntity customerDetailsEntity = null;
		try {
			customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, customerDetailsId);
		} catch (Exception e) {
			logger.error("Exception getCustomerDetailsEntity() :" + ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntity;
	}

	@Override
	public Map<String, Object> loadCmdRuleBuilderSearchDetails(CmdRuleBuilderModel cmdRuleBuilderModel, int customerId,
			int page, int count, String migrationType, int programId, String subType, User user) {
		Map<String, Object> objMap = new HashMap<String, Object>();

		List<CmdRuleBuilderModel> cmdRuleBuilderList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;

		try {

			
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CmdRuleBuilderEntity.class);

			Conjunction objConjunction = Restrictions.conjunction();
			
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				objConjunction.add(Restrictions.eq("customerId", customerId));
			}
			
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getCmdName())) {
				objConjunction.add(Restrictions.ilike("cmdName", cmdRuleBuilderModel.getCmdName().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getRuleName())) {
				objConjunction.add(Restrictions.ilike("ruleName", cmdRuleBuilderModel.getRuleName().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getOperand1Values())) {
				objConjunction.add(Restrictions.ilike("operand1", cmdRuleBuilderModel.getOperand1Values().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getOperand2Values())) {
				objConjunction.add(Restrictions.ilike("operand2", cmdRuleBuilderModel.getOperand2Values().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getRemarks())) {
				objConjunction.add(Restrictions.ilike("remarks", cmdRuleBuilderModel.getRemarks().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getPrompt())) {
				objConjunction.add(Restrictions.ilike("prompt", cmdRuleBuilderModel.getPrompt().trim(),MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getStatus())) {
				objConjunction.add(Restrictions.ilike("status", cmdRuleBuilderModel.getStatus().trim(),MatchMode.ANYWHERE));
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
			List<CmdRuleBuilderEntity> cmdRuleBuilderEntityList = 
					criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (cmdRuleBuilderEntityList != null && cmdRuleBuilderEntityList.size() > 0)
				for (CmdRuleBuilderEntity objCmdRuleBuilderEntity : cmdRuleBuilderEntityList) {

					Date date = objCmdRuleBuilderEntity.getCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					CmdRuleBuilderModel objCmdRuleBuilderModel = new CmdRuleBuilderModel();

					objCmdRuleBuilderModel.setCmdName(objCmdRuleBuilderEntity.getCmdName());
					objCmdRuleBuilderModel.setRuleName(objCmdRuleBuilderEntity.getRuleName());
					objCmdRuleBuilderModel.setId(objCmdRuleBuilderEntity.getId());
					objCmdRuleBuilderModel.setRemarks(objCmdRuleBuilderEntity.getRemarks());
					objCmdRuleBuilderModel.setOperand1Values(objCmdRuleBuilderEntity.getOperand1Values());
					objCmdRuleBuilderModel.setOperand1ColumnNames(objCmdRuleBuilderEntity.getOperand1ColumnNames());
					objCmdRuleBuilderModel.setOperand2Values(objCmdRuleBuilderEntity.getOperand2Values());
					objCmdRuleBuilderModel.setOperand2ColumnNames(objCmdRuleBuilderEntity.getOperand2ColumnNames());
					objCmdRuleBuilderModel.setUseCount(objCmdRuleBuilderEntity.getUseCount());
					objCmdRuleBuilderModel.setOperator(objCmdRuleBuilderEntity.getOperator());
					objCmdRuleBuilderModel.setStatus(objCmdRuleBuilderEntity.getStatus());
					objCmdRuleBuilderModel.setCreatedBy(objCmdRuleBuilderEntity.getCreatedBy());
					objCmdRuleBuilderModel.setTimeStamp(dateFormat);
					objCmdRuleBuilderModel.setPrompt(objCmdRuleBuilderEntity.getPrompt());
					objCmdRuleBuilderModel.setLoopType(objCmdRuleBuilderEntity.getLoopType());
					cmdRuleBuilderList.add(objCmdRuleBuilderModel);

				}

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(CmdRuleBuilderEntity.class);
			criteriaCount.add(objConjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("cmdRuleBuilderData", cmdRuleBuilderList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception loadCmdRuleBuilderSearchDetails() in LsmRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

}
