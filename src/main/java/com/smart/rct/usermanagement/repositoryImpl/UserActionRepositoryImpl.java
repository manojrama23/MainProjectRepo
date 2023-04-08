package com.smart.rct.usermanagement.repositoryImpl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repository.UserActionRepository;
import com.smart.rct.util.PasswordCrypt;

@Repository
@Transactional
public class UserActionRepositoryImpl implements UserActionRepository {
	final static Logger logger = LoggerFactory.getLogger(UserActionRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * This method gets User Details for a userName
	 * 
	 * @param userName
	 * @return UserDetailsEntity
	 */

	public UserDetailsEntity getUserDetailsBasedName(String userName) {
		UserDetailsEntity user = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserDetailsEntity> query = builder.createQuery(UserDetailsEntity.class);
			Root<UserDetailsEntity> root = query.from(UserDetailsEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("userName"), userName));
			TypedQuery<UserDetailsEntity> queryResult = entityManager.createQuery(query);
			user = (UserDetailsEntity) queryResult.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception in UserActionRepositoryImpl.getUserDetailsBasedName: "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return user;
	}

	/**
	 * This method is used to log last login date for a user
	 * 
	 * @param userName
	 * @param loginDate
	 * @return boolean
	 */
	@Override
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	public boolean setLastLogin(String userName, Date loginDate) {
		boolean status = false;
		try {
			UserDetailsEntity user = getUserDetailsBasedName(userName);
			user.setLastLoginDate(loginDate);
			entityManager.merge(user);
			status = true;
		} catch (Exception e) {
			logger.error(
					"Exception in UserActionRepositoryImpl.setLastLogin(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method gets User Details for emailId
	 * 
	 * @param emailId
	 * @return UserDetailsEntity
	 */
	@Override
	public UserDetailsEntity getUserDetailsByEmailId(String emailId) {
		UserDetailsEntity user = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserDetailsEntity> query = builder.createQuery(UserDetailsEntity.class);
			Root<UserDetailsEntity> root = query.from(UserDetailsEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("emailId"), emailId));
			TypedQuery<UserDetailsEntity> queryResult = entityManager.createQuery(query);
			user = (UserDetailsEntity) queryResult.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception in UserActionRepositoryImpl.getUserDetailsByEmailId(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return user;
	}

	/**
	 * This method is used to change user password
	 * 
	 * @param userName
	 * @param newPassword
	 * @return boolean
	 */
	@Override
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	public boolean changePassword(String userName, String newPassword, boolean decrypt) {
		boolean status = false;
		try {
			UserDetailsEntity user = getUserDetailsBasedName(userName);
			if (decrypt) {
				user.setPassword(PasswordCrypt.encrypt(PasswordCrypt.decryptPasswordUI(newPassword)));
			} else {
				user.setPassword(PasswordCrypt.encrypt(newPassword));
			}
			entityManager.merge(user);
			status = true;
		} catch (Exception e) {
			logger.error("Exception UserActionRepositoryImpl.changePassword(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method is used to change user password
	 * 
	 * @param userName
	 * @param newPassword
	 * @return boolean
	 */
	@Override
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	public boolean changeVPNPassword(String userName, String newPassword) {
		boolean status = false;
		try {
			UserDetailsEntity user = getUserDetailsBasedName(userName);
			user.setVpnPassword(PasswordCrypt.encrypt(newPassword));
			entityManager.merge(user);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in UserActionRepositoryImpl.changeVPNPassword(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method returns UserRoleDetailsEntity by its Id
	 * 
	 * @param roleId
	 * @return UserRoleDetailsEntity
	 */
	@Override
	public UserRoleDetailsEntity getRoleById(Integer roleId) {
		logger.info("UserActionRepositoryImpl.getRoleById() roleId: " + roleId);
		UserRoleDetailsEntity roleEntity = null;
		try {
			roleEntity = entityManager.find(UserRoleDetailsEntity.class, roleId);
		} catch (Exception e) {
			logger.error("Exception in UserActionRepositoryImpl.getRoleById(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return roleEntity;
	}
	
	/**
	 * This api get the CustomerList
	 * 
	 * @param addAllRecord
	 * @return List<UserDetailsEntity>
	 */
	@Override
	public List<UserDetailsEntity> getUserList(String status) {
		List<UserDetailsEntity> userEntityList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserDetailsEntity> query = builder.createQuery(UserDetailsEntity.class);
			Root<UserDetailsEntity> root = query.from(UserDetailsEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("status"), status));
			TypedQuery<UserDetailsEntity> queryResult = entityManager.createQuery(query);
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getCustomerList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return userEntityList;
	}

	/**
	 * This method will getUserDetails
	 * 
	 * @param userName
	 * @return UserDetailsModel
	 */
	@SuppressWarnings("deprecation")
	@Override
	public UserDetailsModel getUserDetails(String userName) {
		logger.info("UserActionRepositoryImpl.getUserDetails() userName: " + userName);
		// UserDetailsEntity user = null;
		UserDetailsModel userDetailsModel = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteriaTotList.createAlias("userRoleDetailsEntity", "userRoleDetailsEntity");
			criteriaTotList.createAlias("customerEntity", "customerEntity");
			criteriaTotList.add(Restrictions.eq("userName", userName));

			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("userRoleDetailsEntity.id"), "roleId")
					.add(Projections.property("userRoleDetailsEntity.role"), "role")
					.add(Projections.property("customerEntity.customerName"), "customerName")
					.add(Projections.property("customerEntity.id"), "customerId")
					.add(Projections.property("userName"), "userName")
					.add(Projections.property("programName"), "programNamehidden")
					.add(Projections.property("remarks"), "remarks").add(Projections.property("status"), "status")
					.add(Projections.property("userFullName"), "userFullName")
					.add(Projections.property("emailId"), "emailId").add(Projections.property("password"), "password")
					.add(Projections.property("vpnUserName"), "vpnUserName")
					.add(Projections.property("vpnPassword"), "vpnPassword")
					.add(Projections.property("createdBy"), "createdBy")

					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
							new String[] { "creationDate" }, new Type[] { new StringType() }))
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.LAST_LOGIN_DATE, '%Y-%m-%d %H:%i:%s') as lastLoginDate",
							new String[] { "lastLoginDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");
			criteriaTotList.setProjection(commonProjection);
			userDetailsModel = (UserDetailsModel) criteriaTotList
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(UserDetailsModel.class)).uniqueResult();
		} catch (Exception e) {
			logger.error(
					"Exception in UserActionRepositoryImpl.getUserDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return userDetailsModel;
	}
}
