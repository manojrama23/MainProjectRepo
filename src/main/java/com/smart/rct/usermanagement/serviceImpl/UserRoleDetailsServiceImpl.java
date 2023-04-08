package com.smart.rct.usermanagement.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.repository.UserRoleDetailsRepository;
import com.smart.rct.usermanagement.service.UserRoleDetailsService;

@Service
public class UserRoleDetailsServiceImpl implements UserRoleDetailsService {
	final static Logger logger = LoggerFactory.getLogger(UserRoleDetailsServiceImpl.class);

	@Autowired
	UserRoleDetailsRepository objUserRoleDetailsRepository;

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
			status = objUserRoleDetailsRepository.createRole(userRoleEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in createRole()   UserRoleDetailsServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
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
			status = objUserRoleDetailsRepository.updateRole(userRoleEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRole()   UserRoleDetailsServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api delete the Role details
	 * 
	 * @param roleId
	 * @return boolean
	 */
	@Override
	public boolean deleteRole(Integer roleId) {
		boolean status = false;
		try {
			status = objUserRoleDetailsRepository.deleteRole(roleId);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteRole()   UserRoleDetailsServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api get the Role details
	 * 
	 * @param
	 * @return List
	 */
	@Override
	public List<UserRoleDetailsEntity> getRoleList() {
		List<UserRoleDetailsEntity> objList = null;
		try {
			objList = objUserRoleDetailsRepository.getRoleList();
		} catch (Exception e) {
			logger.error("Exception in getRoleList()   UserRoleDetailsServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objList;
	}

	/**
	 * This api check the duplicate Role details
	 * 
	 * @param userRoleEntity
	 * @return boolean
	 */
	@Override
	public boolean duplicateRole(UserRoleDetailsEntity userRoleEntity) {
		return objUserRoleDetailsRepository.duplicateRole(userRoleEntity);
	}

}
