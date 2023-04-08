package com.smart.rct.migration.repositoryImpl;

import java.text.DateFormat;
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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.model.UploadFileModel;
import com.smart.rct.migration.model.UploadFileModelConnection;
import com.smart.rct.migration.repository.UploadFileRepository;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.DateUtil;

@SuppressWarnings("deprecation")
@Repository
@Transactional
public class UploadFileRepositoryImpl implements UploadFileRepository {

	private static final Logger logger = LoggerFactory.getLogger(UploadFileRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * this method will create upload script
	 * 
	 * @param uploadFileEntity
	 * @return boolean
	 */
	@Override
	public boolean createUploadScript(UploadFileEntity uploadFileEntity) {
		boolean status = false;
		try {
			entityManager.persist(uploadFileEntity);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return uploadScriptDetails List
	 * 
	 * @param page,
	 *            count
	 * @return Map<String, Object>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getUploadScriptDetails(int customerId, int page, int count, String migrationType,
			int programId, String subType, User user) {
		Map<String, Object> objMap = new HashMap<>();
		List<UploadFileEntity> uploadList = null;
		double result = 0;
		int pagecount = 0;
		try {
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
					user.getRoleId());

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UploadFileEntity.class);
			criteria.setFetchMode("neListEntity", FetchMode.LAZY);
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				criteria.add(Restrictions.eq("customerId", customerId));
			}
			criteria.add(Restrictions.ne("uploadedBy", "System"));
			criteria.add(Restrictions.eq("migrationType", migrationType));
			criteria.add(Restrictions.eq("customerDetailsEntity", customerDetailsEntity));
			criteria.add(Restrictions.eq("subType", subType));
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			uploadList = criteria.list();

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UploadFileEntity.class);
			criteriaCount.setFetchMode("neListEntity", FetchMode.LAZY);
			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				criteriaCount.add(Restrictions.eq("customerId", customerId));
			}
			
			criteriaCount.add(Restrictions.ne("uploadedBy", "System"));
			criteriaCount.add(Restrictions.eq("migrationType", migrationType));
			criteriaCount.add(Restrictions.eq("customerDetailsEntity", customerDetailsEntity));
			criteriaCount.add(Restrictions.eq("subType", subType));
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;

			objMap.put("uploadScriptTableDetails", uploadList);
			objMap.put("count", pagecount);
		} catch (Exception e) {
			logger.error("Exception getUploadScriptDetails() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * This method will return lsm details List
	 * 
	 * @param
	 * @return List<LsmModel>
	 */

	@Override
	public List<LsmEntity> getLsmDetails() {
		List<LsmEntity> objLsmVersionList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = builder.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root);
			TypedQuery<LsmEntity> queryResult = entityManager.createQuery(query);
			objLsmVersionList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error(
					"Exception getLsmDetails() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objLsmVersionList;
	}

	@Override
	public List<NetworkConfigEntity> getNeDetails(int programId) {
		List<NetworkConfigEntity> objLsmVersionList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = builder.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("programDetailsEntity"), programId));
			TypedQuery<NetworkConfigEntity> queryResult = entityManager.createQuery(query);
			objLsmVersionList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error(
					"Exception getLsmDetails() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objLsmVersionList;
	}

	/**
	 * This method will return network type List
	 * 
	 * @param
	 * @return Map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<NetworkTypeDetailsEntity> getNetworkType(String custNaame) {
		Map<String, Object> objMap = new HashMap<>();
		List<NetworkTypeDetailsEntity> objList = null;
		try {
			Session session = entityManager.unwrap(Session.class).getSession();
			StringBuilder objqueryNwType = new StringBuilder();

			objqueryNwType.append("SELECT DISTINCT(NW_TYPE) as networkType FROM NW_TYPE_DETAILS order by NW_TYPE");

			SQLQuery q = session.createSQLQuery(objqueryNwType.toString());
			q.setResultTransformer(Transformers.aliasToBean(NetworkTypeDetailsEntity.class));
			objList = q.list();
			objMap.put("nwType", objList);

		} catch (Exception e) {
			logger.error(
					"Exception getNetworkType() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objList;
	}

	/**
	 * this method will update upload script details to DBu.FILE_NAME as
	 * fileName,u.FILE_PATH as filePath,u.EDITABLE as editable,u.LSM_NAME as
	 * lsmName,u.LSM_VERSION as lsmVersion,u.NW_TYPE as nwType,u.REMARKS as
	 * remarks,u.UPLOADED_BY as uploadedBy FROM MIG_UPLOAD_FILE where u.ID=cd.id
	 * 
	 * @param uploadFileEntity
	 * @return boolean
	 */
	@Override
	public boolean updateUploadScript(UploadFileEntity uploadFileEntity) {
		boolean status = false;
		try {
			entityManager.merge(uploadFileEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception updateUploadScript() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will delete the upload script Details.
	 * 
	 * @param id
	 * @return boolean
	 */

	@Override
	public boolean deleteUploadScript(int id) {
		boolean status = false;
		try {
			entityManager.remove(getById(id));
			status = true;
		} catch (Exception e) {
			logger.error("Exception deleteUploadScript() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public UploadFileEntity getById(int id) {
		return entityManager.find(UploadFileEntity.class, id);
	}

	@Override
	public NetworkTypeDetailsEntity getNwEntity(String networkType) {
		NetworkTypeDetailsEntity objNetworkTypeDetailsEntity = null;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkTypeDetailsEntity> query = builder.createQuery(NetworkTypeDetailsEntity.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("networkType"), networkType));
			TypedQuery<NetworkTypeDetailsEntity> queryResult = entityManager.createQuery(query);
			objNetworkTypeDetailsEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error("Exception getNwEntity() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objNetworkTypeDetailsEntity;
	}

	@Override
	public LsmEntity getLsm(String lsmVersion, String lsmName) {
		LsmEntity objLsmEntity = null;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = builder.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root);
			query.where(builder.and(builder.equal(root.get("lsmVersion"), lsmVersion),
					builder.equal(root.get("lsmName"), lsmName)));
			TypedQuery<LsmEntity> queryResult = entityManager.createQuery(query);
			objLsmEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error("Exception getLsm() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objLsmEntity;
	}

	@Override
	public NetworkConfigEntity getNeEntity(String neVersion, String neName, int programId) {
		NetworkConfigEntity objNeEntity = null;
		NeVersionEntity objNeVersionEntity = null;
		try {

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder builderNeVersion = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> queryNeVersionEntity = builderNeVersion.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> rootNeVersionEntity = queryNeVersionEntity.from(NeVersionEntity.class);
			queryNeVersionEntity.select(rootNeVersionEntity);
			queryNeVersionEntity.where(builderNeVersion.and(
					builderNeVersion.equal(rootNeVersionEntity.get("neVersion"), neVersion),
					builderNeVersion.equal(rootNeVersionEntity.get("programDetailsEntity"), customerDetailsEntity)));
			TypedQuery<NeVersionEntity> queryResultNeVersionEntity = entityManager.createQuery(queryNeVersionEntity);
			objNeVersionEntity = queryResultNeVersionEntity.getSingleResult();

			// NeVersionEntity neVersionEntity = entityManager.find(NeVersionEntity.class,
			// neVersion);

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = builder.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root);
			// query.where(builder.and(builder.equal(root.get("lsmVersion"),
			// lsmVersion),builder.equal(root.get("lsmName"), lsmName),
			// builder.equal(root.get("networkTypeDetailsEntity"), networkTypeId)));
			query.where(builder.and(builder.equal(root.get("neVersionEntity"), objNeVersionEntity),
					builder.equal(root.get("neName"), neName)));
			// query.where(builder.equal(root.get("lsmVersion"), lsmVersion) );
			TypedQuery<NetworkConfigEntity> queryResult = entityManager.createQuery(query);
			objNeEntity = queryResult.getSingleResult();

			/*
			 * Criteria
			 * criteria=entityManager.unwrap(Session.class).createCriteria(LsmEntity.class);
			 * criteria.createAlias("networkTypeDetailsEntity", "networkTypeDetailsEntity");
			 * criteria.add(Restrictions.eq("lsmVersion", lsmVersion));
			 * criteria.add(Restrictions.eq("lsmName", lsmName));
			 * criteria.add(Restrictions.eq("networkTypeDetailsEntity.id", networkTypeId));
			 * objLsmEntity=(LsmEntity)criteria.uniqueResult();
			 */

		} catch (Exception e) {
			logger.error("Exception getLsm() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objNeEntity;
	}

	@Override
	public NeVersionEntity getNeVersionEntity(String neVersion, String programId) {
		NeVersionEntity objNeEntity = null;
		try {

			int iProgramId = Integer.parseInt(programId);

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, iProgramId);

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> query = builder.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> root = query.from(NeVersionEntity.class);
			query.select(root);

			query.where(builder.and(builder.equal(root.get("programDetailsEntity"), customerDetailsEntity),
					builder.equal(root.get("neVersion"), neVersion)));
			TypedQuery<NeVersionEntity> queryResult = entityManager.createQuery(query);
			objNeEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error("Exception getLsm() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objNeEntity;
	}

	@Override
	public UploadFileEntity getUploadScriptByPath(StringBuilder uploadPath, String fileName) {
		UploadFileEntity uploadScriptEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = builder.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root);
			// query.where(builder.equal(root.get("filePath"), uploadPath.toString()));
			query.where(builder.and(builder.equal(root.get("filePath"), uploadPath.toString()),
					builder.equal(root.get("fileName"), fileName)));
			TypedQuery<UploadFileEntity> queryResult = entityManager.createQuery(query);
			uploadScriptEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error("Exception getUploadScriptByPath() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadScriptEntity;
	}

	@Override
	public UploadFileEntity getUploadScriptDuplicate(String fileName, String migrationType, String programName,
			String subType) {
		UploadFileEntity uploadScriptEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = builder.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root);
			// query.where(builder.equal(root.get("filePath"), uploadPath.toString()));
			query.where(builder.and(builder.equal(root.get("migrationType"), migrationType),
					builder.equal(root.get("fileName"), fileName), builder.equal(root.get("program"), programName),
					builder.equal(root.get("subType"), subType)));
			TypedQuery<UploadFileEntity> queryResult = entityManager.createQuery(query);
			uploadScriptEntity =  queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(
					"Exception getUploadScriptDuplicate() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadScriptEntity;
	}

	@Override
	public UploadFileEntity getFilePath(int id) {
		UploadFileEntity uploadFileEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("id"), id));

			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntity = typedQuery.getSingleResult();
			// filePath = uploadFileEntity.getFilePath();

		} catch (Exception e) {
			logger.error(
					"Exception getFilePath() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntity;
	}

	@Override
	public List<UploadFileEntity> getUploadFileEntity(String filePath) {
		List<UploadFileEntity> uploadFileEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("filePath"), filePath));

			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntity = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(
					"Exception getUploadFileEntity() in UploadFileRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> searchUploadScript(String fileName, String uploadedBy, String startDate, String endDate,
			int customerId, int page, int count, String migrationType, String programName, String subType, User user,
			String state) {
		Map<String, Object> objMap = new HashMap<>();

		List<UploadFileModel> uploadFileModelList = new ArrayList<>();

		double result = 0;
		int paginationNumber = 0;

		try {

			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
					user.getRoleId());

			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UploadFileEntity.class);
			// criteria.addOrder(Order.asc("id"));
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("neListEntity", FetchMode.LAZY);
			
			if (fileName != null) {
				Criterion eventFileName = Restrictions.ilike("fileName", fileName, MatchMode.ANYWHERE);
				conjunction.add(eventFileName);
			}
			if (uploadedBy != null) {
				Criterion eventUploadedBy = Restrictions.ilike("uploadedBy", uploadedBy, MatchMode.ANYWHERE);
				conjunction.add(eventUploadedBy);
			}
			conjunction.add(Restrictions.ne("uploadedBy", "System"));
			
			if (startDate != null && endDate != null) {
				// DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dStartDate = format.parse(startDate);
				Date dEndDate = format.parse(endDate);

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromDate = dateFormat.format(dStartDate);
				String toDate = dateFormat.format(dEndDate);

				String toDateAfterTrim = toDate.substring(0, 10);
				toDateAfterTrim = toDateAfterTrim + " " + "23:59:59";

				Date finalStartDate = dateFormat.parse(fromDate);
				Date finalEndDate = dateFormat.parse(toDateAfterTrim);

				Criterion dateRange = Restrictions.between("creationDate", finalStartDate, finalEndDate);
				conjunction.add(dateRange);
			}else if (startDate != null && !"".equals(startDate)) {
				
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dStartDate = format.parse(startDate);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromDate = dateFormat1.format(dStartDate);

				Date finalStartDate = dateFormat1.parse(fromDate);
				
				Criterion startDateRange = Restrictions.ge("creationDate", finalStartDate);
				conjunction.add(startDateRange);
			}else if (endDate != null && !"".equals(endDate)) {
				
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dEndDate = format.parse(endDate);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String toDate = dateFormat1.format(dEndDate);

				String toDateAfterTrim = toDate.substring(0, 10);
				toDateAfterTrim = toDateAfterTrim + " " + "23:59:59";

				Date finalEndDate = dateFormat1.parse(toDateAfterTrim);
				
				Criterion endDateRange = Restrictions.le("creationDate", finalEndDate);
				conjunction.add(endDateRange);
			}
			if (state != null) {
				Criterion eventState = Restrictions.eq("state", state);
				conjunction.add(eventState);
			}

			conjunction.add(Restrictions.eq("migrationType", migrationType));

			conjunction.add(Restrictions.eq("program", programName));
			conjunction.add(Restrictions.eq("subType", subType));

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				conjunction.add(Restrictions.eq("customerId", customerId));
			}

			// Projection commonProjection = Projections.projectionList()
			// .add(Projections.property("fileName"), "fileName")
			// .add(Projections.property("state"), "state")
			// .add(Projections.property("uploadedBy"), "uploadedBy")
			// .add(Projections.sqlProjection(
			// "DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
			// new String[] { "creationDate" }, new Type[] { new StringType() }))
			// .add(Projections.property("remarks"), "remarks");

			// criteria.setProjection(commonProjection);
			criteria.add(conjunction);

			List<UploadFileEntity> uploadFileEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (uploadFileEntityList != null && !uploadFileEntityList.isEmpty())
				for (UploadFileEntity objUploadFileEntity : uploadFileEntityList) {

					UploadFileModelConnection objUploadFileModelConnection = new UploadFileModelConnection();
					Map<String, Object> connectionTerminal = new HashMap<>();

					UploadFileModel objuploadFileModel = new UploadFileModel();

					objuploadFileModel.setId(objUploadFileEntity.getId());
					objuploadFileModel.setFileName(objUploadFileEntity.getFileName());
					objuploadFileModel.setFilePath(objUploadFileEntity.getFilePath());
					objuploadFileModel.setState(objUploadFileEntity.getState());
					objuploadFileModel.setUploadedBy(objUploadFileEntity.getUploadedBy());
					String sCreationDate = dateFormat.format(objUploadFileEntity.getCreationDate());
					objuploadFileModel.setCreationDate(sCreationDate);
					objuploadFileModel.setRemarks(objUploadFileEntity.getRemarks());
					objuploadFileModel.setUploadedBy(objUploadFileEntity.getUploadedBy());
					objuploadFileModel.setArguments(objUploadFileEntity.getArguments());

					objuploadFileModel.setScriptType(objUploadFileEntity.getScriptType());
					objuploadFileModel.setConnectionLocation(objUploadFileEntity.getConnectionLocation());
					objuploadFileModel
							.setConnectionLocationUserName(objUploadFileEntity.getConnectionLocationUserName());
					objuploadFileModel.setConnectionTerminal(objUploadFileEntity.getConnectionTerminal());
					objuploadFileModel.setConnectionLocationPwd(objUploadFileEntity.getConnectionLocationPwd());

					objUploadFileModelConnection.setTerminalName(objUploadFileEntity.getConnectionTerminal());
					objUploadFileModelConnection.setTermUsername(objUploadFileEntity.getConnectionTerminalUserName());
					objUploadFileModelConnection.setTermPassword(objUploadFileEntity.getConnectionTerminalPwd());
					objUploadFileModelConnection.setPrompt(objUploadFileEntity.getPrompt());
					connectionTerminal.put("connectionTerminal", objUploadFileModelConnection);
					objuploadFileModel.setConnectionTerminalDetails(connectionTerminal);

					if (objUploadFileEntity.getNeListEntity() != null) {
						objuploadFileModel.setLsmName(objUploadFileEntity.getNeListEntity().getNeName());
						objuploadFileModel.setLsmVersion(
								objUploadFileEntity.getNeListEntity().getNeVersionEntity().getNeVersion());
					} else if (objUploadFileEntity.getNeVersion() != null) {
						objuploadFileModel.setLsmVersion(objUploadFileEntity.getNeVersion().getNeVersion());
					}

					uploadFileModelList.add(objuploadFileModel);

				}

			Conjunction conjunctionCount = Restrictions.conjunction();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UploadFileEntity.class);
			criteria.setFetchMode("neListEntity", FetchMode.LAZY);
			if (fileName != null) {
				Criterion eventFileName = Restrictions.ilike("fileName", fileName, MatchMode.ANYWHERE);
				conjunctionCount.add(eventFileName);
			}
			if (uploadedBy != null) {
				Criterion eventUploadedBy = Restrictions.ilike("uploadedBy", uploadedBy, MatchMode.ANYWHERE);
				conjunctionCount.add(eventUploadedBy);
			}
			conjunctionCount.add(Restrictions.ne("uploadedBy", "System"));
			
			if (startDate != null && endDate != null) {
				// DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dStartDate = format.parse(startDate);
				Date dEndDate = format.parse(endDate);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromDate = dateFormat1.format(dStartDate);
				String toDate = dateFormat1.format(dEndDate);

				String toDateAfterTrim = toDate.substring(0, 10);
				toDateAfterTrim = toDateAfterTrim + " " + "23:59:59";

				Date finalStartDate = dateFormat1.parse(fromDate);
				Date finalEndDate = dateFormat1.parse(toDateAfterTrim);

				Criterion dateRange = Restrictions.between("creationDate", finalStartDate, finalEndDate);
				conjunctionCount.add(dateRange);
			}else if (startDate != null && !"".equals(startDate)) {
				
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dStartDate = format.parse(startDate);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromDate = dateFormat1.format(dStartDate);

				Date finalStartDate = dateFormat1.parse(fromDate);
				
				Criterion startDateRange = Restrictions.ge("creationDate", finalStartDate);
				conjunctionCount.add(startDateRange);
			}else if (endDate != null && !"".equals(endDate)) {
				
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				Date dEndDate = format.parse(endDate);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String toDate = dateFormat1.format(dEndDate);

				String toDateAfterTrim = toDate.substring(0, 10);
				toDateAfterTrim = toDateAfterTrim + " " + "23:59:59";

				Date finalEndDate = dateFormat1.parse(toDateAfterTrim);
				
				Criterion endDateRange = Restrictions.le("creationDate", finalEndDate);
				conjunctionCount.add(endDateRange);
			}
			
			if (state != null) {
				Criterion eventState = Restrictions.eq("state", state);
				conjunctionCount.add(eventState);
			}

			conjunctionCount.add(Restrictions.eq("migrationType", migrationType));

			conjunctionCount.add(Restrictions.eq("program", programName));
			conjunctionCount.add(Restrictions.eq("subType", subType));

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				conjunctionCount.add(Restrictions.eq("customerId", customerId));
			}

			criteriaCount.add(conjunctionCount);

			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("uploadScriptData", uploadFileModelList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error("Exception searchUploadScript() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public UploadFileEntity getUploadFileEntity(int scriptId) {

		UploadFileEntity uploadFileEntity = null;
		try {
			uploadFileEntity = entityManager.find(UploadFileEntity.class, scriptId);
		} catch (Exception e) {
			logger.error("Exception getUploadFileEntity() in UploadFileRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return uploadFileEntity;

	}

}
