package com.smart.rct.usermanagement.repositoryImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.repository.UserRoleDetailsRepository;

@Transactional
@Repository
public class UserRoleDetailsRepositoryImpl implements UserRoleDetailsRepository {

	final static Logger logger = LoggerFactory.getLogger(UserRoleDetailsRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * This api create the Role details
	 * 
	 * @param userRoleEntity
	 * @return boolean
	 */
	@Override
	public boolean createRole(UserRoleDetailsEntity userRoleEntity) {
		boolean status = false;
		try {
			entityManager.persist(userRoleEntity);
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in UserDetailsRepositoryImpl.createUser(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This api update the Role details
	 * 
	 * @param userRoleEntity
	 * @return boolean
	 */
	@Override
	public boolean updateRole(UserRoleDetailsEntity userRoleEntity) {
		boolean status = false;
		try {
			UserRoleDetailsEntity userRoleDetailsEntity = getUserById(userRoleEntity.getId());
			if (userRoleDetailsEntity != null) {
				userRoleDetailsEntity.setRole(userRoleEntity.getRole());
				entityManager.flush();
				status = true;
			}
		} catch (Exception e) {
			logger.info("Exception in UserDetailsRepositoryImpl.updateRole(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This api delete the Role details
	 * 
	 * @param userRoleEntity
	 * @return boolean
	 */
	@Override
	public boolean deleteRole(int roleId) {
		boolean status = false;
		try {
			entityManager.remove(getUserById(roleId));
			status = true;
		} catch (Exception e) {
			logger.info("Exception in UserDetailsRepositoryImpl.deleteRole(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This api get the Role details
	 * 
	 * @param userRoleEntity
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRoleDetailsEntity> getRoleList() {
		List<UserRoleDetailsEntity> roleList = null;
		try {

			String hql = "select * from USER_ROLES";
			roleList = entityManager.createNativeQuery(hql, UserRoleDetailsEntity.class).getResultList();

		} catch (Exception e) {
			logger.info("Exception in UserDetailsRepositoryImpl.getRoleList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return roleList;
	}

	public UserRoleDetailsEntity getUserById(int articleId) {
		return entityManager.find(UserRoleDetailsEntity.class, articleId);
	}

	/**
	 * This api check the duplicate Role details
	 * 
	 * @param userRoleEntity
	 * @return boolean
	 */
	@Override
	public boolean duplicateRole(UserRoleDetailsEntity userRoleEntity) {
		boolean status = false;
		try {
			String hql = "select count(*) FROM USER_ROLES where role='" + userRoleEntity.getRole() + "'";
			BigInteger role = (BigInteger) entityManager.createNativeQuery(hql).getSingleResult();
			if (role.intValue() > 0) {
				status = true;
			}
		} catch (Exception e) {
			logger.info(
					"Exception in UserDetailsRepositoryImpl.duplicateRole(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

}
