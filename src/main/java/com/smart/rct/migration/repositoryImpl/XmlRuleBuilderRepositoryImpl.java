package com.smart.rct.migration.repositoryImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.dto.UseCaseBuilderDto;
import com.smart.rct.migration.entity.XmlElementEntity;
import com.smart.rct.migration.entity.XmlRootEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.XmlElementModel;
import com.smart.rct.migration.model.XmlRootModel;
import com.smart.rct.migration.model.XmlRuleBuilderModel;
import com.smart.rct.migration.model.XmlSerachModel;
import com.smart.rct.migration.repository.XmlRuleBuilderRepository;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.User;

@Repository
@Transactional
public class XmlRuleBuilderRepositoryImpl implements XmlRuleBuilderRepository {

	final static Logger logger = LoggerFactory.getLogger(XmlRuleBuilderRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UseCaseBuilderDto useCaseBuilderDto;

	@Override
	public boolean createXmlRuleBuilder(XmlRuleBuilderEntity xmlRuleBuilderEntity, List<XmlRootModel> xmlRootModelList,
			List<XmlElementModel> xmlElementModelList, int programId) {

		boolean saveStatus = false;
		try {
			entityManager.persist(xmlRuleBuilderEntity);
			if (!xmlRootModelList.isEmpty()) {
				for (XmlRootModel xmlRootModel : xmlRootModelList) {
					XmlRootEntity xmlRootEntity = new XmlRootEntity();
					xmlRootEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
					xmlRootEntity.setRootKey(xmlRootModel.getRootKey());
					xmlRootEntity.setRootValue(xmlRootModel.getRootValue());
					entityManager.persist(xmlRootEntity);
				}
			}
			if (!xmlElementModelList.isEmpty()) {
				for (XmlElementModel xmlElementModel : xmlElementModelList) {
					XmlElementEntity xmlElementEntity = new XmlElementEntity();
					xmlElementEntity.setElementName(xmlElementModel.getElementName());
					xmlElementEntity.setElementValue(xmlElementModel.getElementValue());
					xmlElementEntity.setOperator(xmlElementModel.getOperator());
					xmlElementEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
					entityManager.persist(xmlElementEntity);
				}
			}
			saveStatus = true;

		} catch (Exception e) {
			logger.error(" createXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return saveStatus;
	}

	//@Override
	//public List<XmlRuleBuilderEntity> loadXmlRuleBuilderSearchDetails1(int parseInt, int parseInt2,
	//		XmlSerachModel searchModel, String migrationType, int programId, String subType, User user,
		//	int customerId) {
		//List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = new ArrayList<XmlRuleBuilderEntity>();
		//try {

			//UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
				//	user.getRoleId());

			//CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			//CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			//CriteriaQuery<XmlRuleBuilderEntity> query = cb.createQuery(XmlRuleBuilderEntity.class);
			//Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
			//query.select(root);

			//if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				//query.where(cb.equal(root.get("customerId"), customerId));
			//}

			//if (StringUtils.isNotEmpty(searchModel.getRuleName())) {
				//query.where(cb.like(root.get("ruleName"), searchModel.getRuleName().trim()));
			//}
			//if (StringUtils.isNotEmpty(searchModel.getRootName())) {
				//query.where(cb.like(root.get("rootName"), searchModel.getRootName().trim()));
			//}
			//if (StringUtils.isNotEmpty(searchModel.getCreatedBy())) {
				//query.where(cb.like(root.get("createdBy"), searchModel.getCreatedBy().trim()));
			//}
			//if (StringUtils.isNotEmpty(searchModel.getSubRootName())) {
			//	query.where(cb.like(root.get("subRootName"), searchModel.getSubRootName().trim()));
			//}
			//if (StringUtils.isNotEmpty(subType)) {
			//	query.where(cb.and(cb.equal(root.get("migrationType"), migrationType),
					//	cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					//	cb.equal(root.get("subType"), subType)));
			//} else {
				//query.where(cb.and(cb.equal(root.get("migrationType"), migrationType),
				//		cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			//}
			//query.orderBy(cb.desc(root.get("creationDate")));
			//TypedQuery<XmlRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			//xmlRuleBuilderEntityList = typedQuery.getResultList();
		//} catch (Exception e) {
			//logger.error(" loadXmlRuleBuilderSearchDetails() : " + ExceptionUtils.getFullStackTrace(e));
		//} finally {
			//entityManager.flush();
			//entityManager.clear();
		//}
		//return xmlRuleBuilderEntityList;
	//}
			
		public Map<String,Object> loadXmlRuleBuilderSearchDetails(int page, int count,
				XmlSerachModel cmdRuleBuilderModel, String migrationType, int programId, String subType, User user,
				int customerId) {
			
			Map<String, Object> objMap = new HashMap<String, Object>();
		
			List<XmlRuleBuilderModel> cmdRuleBuilderList = new ArrayList<>();
		
			double result = 0;
			int paginationNumber = 0;
		
			try {
		
				
				UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
				
				Criteria criteria = entityManager.unwrap(Session.class).createCriteria(XmlRuleBuilderEntity.class);
				criteria.setFetchMode("customerDetailsEntity", FetchMode.LAZY);
				criteria.setFetchMode("xmlRootEntitySet", FetchMode.LAZY);
				criteria.setFetchMode("xmlElementEntitySet", FetchMode.LAZY);
				
				Conjunction objConjunction = Restrictions.conjunction();
				
				if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
					objConjunction.add(Restrictions.eq("customerId", customerId));
				}
				
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getRuleName())) {
					objConjunction.add(Restrictions.ilike("ruleName", cmdRuleBuilderModel.getRuleName().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getRootName())) {
					objConjunction.add(Restrictions.ilike("rootName", cmdRuleBuilderModel.getRootName().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getCreatedBy())) {
					objConjunction.add(Restrictions.ilike("createdBy", cmdRuleBuilderModel.getCreatedBy().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getSubRootName())) {
					objConjunction.add(Restrictions.ilike("subRootName", cmdRuleBuilderModel.getSubRootName().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getPrompt())) {
					objConjunction.add(Restrictions.ilike("prompt", cmdRuleBuilderModel.getPrompt().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getStatus())) {
					objConjunction.add(Restrictions.ilike("status", cmdRuleBuilderModel.getStatus().trim(),MatchMode.ANYWHERE));
				}
				if (StringUtils.isNotEmpty(cmdRuleBuilderModel.getCmdName())) {
					objConjunction.add(Restrictions.ilike("cmdName", cmdRuleBuilderModel.getCmdName().trim(),MatchMode.ANYWHERE));
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
				List<XmlRuleBuilderEntity> cmdRuleBuilderEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
				if (cmdRuleBuilderEntityList != null && cmdRuleBuilderEntityList.size() > 0)
					cmdRuleBuilderList = useCaseBuilderDto.convertXmlRuleBuilderEntityToModel(cmdRuleBuilderEntityList);
					/*for (XmlRuleBuilderEntity objCmdRuleBuilderEntity : cmdRuleBuilderEntityList) {
		
						Date date = objCmdRuleBuilderEntity.getCreationDate();
						String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
						XmlRuleBuilderModel objCmdRuleBuilderModel = new XmlRuleBuilderModel();
		
						objCmdRuleBuilderModel.setRuleName(objCmdRuleBuilderEntity.getRuleName());
						objCmdRuleBuilderModel.setRootName(objCmdRuleBuilderEntity.getRootName());
						objCmdRuleBuilderModel.setSubRootName(objCmdRuleBuilderEntity.getSubRootName());
						objCmdRuleBuilderModel.setCreatedBy(objCmdRuleBuilderEntity.getCreatedBy());
						objCmdRuleBuilderModel.setTimeStamp(dateFormat);
						objCmdRuleBuilderModel.setRemarks(objCmdRuleBuilderEntity.getRemarks());
						objCmdRuleBuilderModel.setLoopType(objCmdRuleBuilderEntity.getLoopType());
						objCmdRuleBuilderModel.setCmdName(objCmdRuleBuilderEntity.getCmdName());
						objCmdRuleBuilderModel.setPrompt(objCmdRuleBuilderEntity.getPrompt());
						objCmdRuleBuilderModel.setStatus(objCmdRuleBuilderEntity.getStatus());
						objCmdRuleBuilderModel
						
						
						cmdRuleBuilderList.add(objCmdRuleBuilderModel);
		
					}*/
		
				Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(XmlRuleBuilderEntity.class);
				criteriaCount.add(objConjunction);
				criteriaCount.setProjection(Projections.rowCount());
				Long totCount = (Long) criteriaCount.uniqueResult();
				double size = totCount;
				result = Math.ceil(size / count);
				paginationNumber = (int) result;
				
				objMap.put("xmlRuleBuilderEntityList", cmdRuleBuilderList);
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

	@Override
	public Map<String, Object>  loadXmlRuleBuilderDetails(int page, int count, String migrationType, int programId, String subType, User user, int customerId) {
		List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = new ArrayList<XmlRuleBuilderEntity>();
		List<XmlRuleBuilderModel> cmdRuleBuilderList = new ArrayList<>();
		double result = 0;
		int paginationNumber = 0;
		Map<String, Object> objMap = new HashMap<String, Object>();
		try {
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class, user.getRoleId());
			
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(XmlRuleBuilderEntity.class);
			criteria.setFetchMode("customerDetailsEntity", FetchMode.LAZY);
			criteria.setFetchMode("xmlRootEntitySet", FetchMode.LAZY);
			criteria.setFetchMode("xmlElementEntitySet", FetchMode.LAZY);
			
			Conjunction objConjunction = Restrictions.conjunction();
			
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				objConjunction.add(Restrictions.eq("customerId", customerId));
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
			List<XmlRuleBuilderEntity> cmdRuleBuilderEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (cmdRuleBuilderEntityList != null && cmdRuleBuilderEntityList.size() > 0)
				cmdRuleBuilderList = useCaseBuilderDto.convertXmlRuleBuilderEntityToModel(cmdRuleBuilderEntityList);
				/*for (XmlRuleBuilderEntity objCmdRuleBuilderEntity : cmdRuleBuilderEntityList) {
	
					Date date = objCmdRuleBuilderEntity.getCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					XmlRuleBuilderModel objCmdRuleBuilderModel = new XmlRuleBuilderModel();
	
					objCmdRuleBuilderModel.setRuleName(objCmdRuleBuilderEntity.getRuleName());
					objCmdRuleBuilderModel.setRootName(objCmdRuleBuilderEntity.getRootName());
					objCmdRuleBuilderModel.setSubRootName(objCmdRuleBuilderEntity.getSubRootName());
					objCmdRuleBuilderModel.setCreatedBy(objCmdRuleBuilderEntity.getCreatedBy());
					objCmdRuleBuilderModel.setTimeStamp(dateFormat);
					objCmdRuleBuilderModel.setRemarks(objCmdRuleBuilderEntity.getRemarks());
					objCmdRuleBuilderModel.setLoopType(objCmdRuleBuilderEntity.getLoopType());
					objCmdRuleBuilderModel.setCmdName(objCmdRuleBuilderEntity.getCmdName());
					objCmdRuleBuilderModel.setPrompt(objCmdRuleBuilderEntity.getPrompt());
					objCmdRuleBuilderModel.setStatus(objCmdRuleBuilderEntity.getStatus());
					
					cmdRuleBuilderList.add(objCmdRuleBuilderModel);
	
				}*/
	
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(XmlRuleBuilderEntity.class);
			criteriaCount.add(objConjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			
			objMap.put("xmlRuleBuilderEntityList", cmdRuleBuilderList);
			objMap.put("totalCount", paginationNumber);
			
		} catch (Exception e) {
			logger.error(" loadXmlRuleBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public XmlRuleBuilderEntity getXmlRuleBuilderEntity(int xmlRuleId) {
		XmlRuleBuilderEntity xmlRuleBuilderEntity = null;
		try {
			xmlRuleBuilderEntity = entityManager.find(XmlRuleBuilderEntity.class, xmlRuleId);
		} catch (Exception e) {
			logger.error(" getXmlRuleBuilderEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return xmlRuleBuilderEntity;
	}

	@Override
	public boolean updateXmlRuleBuilder(XmlRuleBuilderEntity xmlRuleBuilderEntity, List<XmlRootModel> xmlRootModelList,
			List<XmlElementModel> xmlElementModelList, int programId) {
		boolean updateStatus = false;
		try {
			entityManager.merge(xmlRuleBuilderEntity);
			if (!xmlRootModelList.isEmpty()) {
				for (XmlRootModel xmlRootModel : xmlRootModelList) {
					if(xmlRootModel.getRootId()!=null && !xmlRootModel.getRootId().trim().isEmpty()) {
						XmlRootEntity xmlRootEntity = entityManager.find(XmlRootEntity.class,
								Integer.parseInt(xmlRootModel.getRootId()));
						xmlRootEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						xmlRootEntity.setRootKey(xmlRootModel.getRootKey());
						xmlRootEntity.setRootValue(xmlRootModel.getRootValue());
						entityManager.merge(xmlRootEntity);
					}else {
						XmlRootEntity xmlRootEntity = new XmlRootEntity();
						xmlRootEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						xmlRootEntity.setRootKey(xmlRootModel.getRootKey());
						xmlRootEntity.setRootValue(xmlRootModel.getRootValue());
						entityManager.persist(xmlRootEntity);
					}
				}
			}
			if (!xmlElementModelList.isEmpty()) {
				for (XmlElementModel xmlElementModel : xmlElementModelList) {
					if(xmlElementModel.getElementId()!=null && !xmlElementModel.getElementId().trim().isEmpty()) {
						XmlElementEntity xmlElementEntity = entityManager.find(XmlElementEntity.class,
								Integer.parseInt(xmlElementModel.getElementId()));
						xmlElementEntity.setElementName(xmlElementModel.getElementName());
						xmlElementEntity.setElementValue(xmlElementModel.getElementValue());
						xmlElementEntity.setOperator(xmlElementModel.getOperator());
						xmlElementEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						entityManager.merge(xmlElementEntity);
					}else {
						XmlElementEntity xmlElementEntity = new XmlElementEntity();
						xmlElementEntity.setElementName(xmlElementModel.getElementName());
						xmlElementEntity.setElementValue(xmlElementModel.getElementValue());
						xmlElementEntity.setOperator(xmlElementModel.getOperator());
						xmlElementEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						entityManager.persist(xmlElementEntity);
					}
				}
			}
			updateStatus = true;
		} catch (Exception e) {
			logger.error(" updateXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return updateStatus;
	}

	@Override
	public boolean deleteXmlRuleBuilder(int xmlRuleBuilderId) {
		boolean deleteXmlRuleStatus = false;
		try {
			XmlRuleBuilderEntity xmlRuleBuilderEntity = entityManager.find(XmlRuleBuilderEntity.class, xmlRuleBuilderId);			
			entityManager.remove(xmlRuleBuilderEntity);
			deleteXmlRuleStatus = true;
		} catch (Exception e) {
			logger.error(" deleteXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteXmlRuleStatus;
	}

	@Override
	public boolean findByRuleName(String ruleName, int customerId, String migrationType, int programId,
			String userRole,String subType) {
		XmlRuleBuilderEntity dupCmdEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<XmlRuleBuilderEntity> query = builder.createQuery(XmlRuleBuilderEntity.class);
			Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
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

			TypedQuery<XmlRuleBuilderEntity> queryResult = entityManager.createQuery(query);
			dupCmdEntity = (XmlRuleBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

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
	
	
	@Override
	public XmlRuleBuilderEntity findByRuleName(int programId,String ruleName) {
		XmlRuleBuilderEntity xmlRuleEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<XmlRuleBuilderEntity> query = builder.createQuery(XmlRuleBuilderEntity.class);
			Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("ruleName"), ruleName),
					builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity));

			TypedQuery<XmlRuleBuilderEntity> queryResult = entityManager.createQuery(query);
			xmlRuleEntity = (XmlRuleBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

		} catch (Exception e) {
			logger.error("Exception findByRuleName() in CmdRuleBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlRuleEntity;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<XmlElementEntity> getXmlElementEntity(Integer id) {
		List<XmlElementEntity> xmlElementEntity = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(XmlElementEntity.class);

			criteria.add(Restrictions.eq("xmlRuleBuilderEntity.id", id));

			xmlElementEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlElementEntity;
	}
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<XmlRootEntity> getXmlRootEntity(Integer id) {
		List<XmlRootEntity> xmlRootEntity = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(XmlRootEntity.class);

			criteria.add(Restrictions.eq("xmlRuleBuilderEntity.id", id));

			xmlRootEntity = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getCiqList() in  FileUploadRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlRootEntity;
	}
	
	@Override
	public boolean deleteXmlElementById(int id) {
		boolean deleteXmlElementStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from XmlElementEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteXmlElementStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteXmlElementStatus;
	}
	
	
	@Override
	public boolean deleteXmlRootById(int id) {
		boolean deleteXmlRootStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from XmlRootEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteXmlRootStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteXmlRootStatus;
	}

	@Override
	public List<XmlRuleBuilderEntity> getXmlRuleBuilderEntityList(int programId) {
		List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<XmlRuleBuilderEntity> query = cb.createQuery(XmlRuleBuilderEntity.class);
			Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId));

			TypedQuery<XmlRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			xmlRuleBuilderEntityList = (List<XmlRuleBuilderEntity>) typedQuery.getResultList();

		} catch (Exception e) {
			logger.error("Exception getXmlRuleBuilderEntityList() in XmlRuleBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlRuleBuilderEntityList;
	}

}
