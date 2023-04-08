package com.smart.rct.usermanagement.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repository.UserRoleDetailsRepository;

@Component
public class UserDetailsDto {
	final static Logger logger = LoggerFactory.getLogger(UserDetailsDto.class);

	@Autowired
	UserRoleDetailsRepository userRoleDetailsRepository;

	/**
	 * This method will get UserDetailsEntity from UserDetailsModel
	 * 
	 * @param UserDetailsModel,
	 *            sessionId
	 * @return UserDetailsEntity
	 */
	public UserDetailsEntity getUserDetailsEntity(UserDetailsModel userDetailsModel, String sessionId) {
		UserDetailsEntity userDetailsEntity = null;
		try {
			if (userDetailsModel != null) {
				userDetailsEntity = new UserDetailsEntity();
				if (userDetailsModel.getId() != null && userDetailsModel.getId() != 0) {
					userDetailsEntity.setId(Integer.valueOf(userDetailsModel.getId()));
				}
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				userDetailsEntity.setUserName(userDetailsModel.getUserName());
				userDetailsEntity.setEmailId(userDetailsModel.getEmailId());
				userDetailsEntity.setPassword(userDetailsModel.getPassword());
				userDetailsEntity.setRemarks(userDetailsModel.getRemarks());
				userDetailsEntity.setStatus(userDetailsModel.getStatus());
				userDetailsEntity.setUserFullName(userDetailsModel.getUserFullName());
				if (userDetailsModel.getProgramName() != null) {
					userDetailsEntity.setProgramName(String.join(",", userDetailsModel.getProgramName()));
				}
				userDetailsEntity.setVpnPassword(userDetailsModel.getVpnPassword());
				userDetailsEntity.setVpnUserName(userDetailsModel.getVpnUserName());
				userDetailsEntity.setCreatedBy(user.getUserName());
				UserRoleDetailsEntity objUserRoleDetailsEntity = new UserRoleDetailsEntity();
				objUserRoleDetailsEntity.setId(userDetailsModel.getRoleId());
				CustomerEntity objCustomerEntity = new CustomerEntity();
				objCustomerEntity.setId(userDetailsModel.getCustomerId());
				userDetailsEntity.setCustomerEntity(objCustomerEntity);
				userDetailsEntity.setUserRoleDetailsEntity(objUserRoleDetailsEntity);
			}
		} catch (Exception e) {
			logger.error("Excpetion in UserDetailsDto.getUserDetailsEntity(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return userDetailsEntity;
	}

}
