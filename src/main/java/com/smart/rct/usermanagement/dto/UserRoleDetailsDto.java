package com.smart.rct.usermanagement.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserRoleDetailsModel;

@Component
public class UserRoleDetailsDto {

	final static Logger logger = LoggerFactory.getLogger(UserRoleDetailsDto.class);

	/**
	 * This method will set the UserRoleDetailsEntity with values from
	 * userRoleDetailsModel.
	 * 
	 * @param userRoleDetailsModel
	 * @return userRoleDetailsEntity
	 */
	public UserRoleDetailsEntity getUserRoleDetailsEntity(UserRoleDetailsModel userRoleDetailsModel) {
		UserRoleDetailsEntity userRoleDetailsEntity = null;
		try {
			if (userRoleDetailsModel != null) {
				userRoleDetailsEntity = new UserRoleDetailsEntity();
				if (userRoleDetailsModel.getId() != null && userRoleDetailsModel.getId() > 0) {
					userRoleDetailsEntity.setId(Integer.valueOf(userRoleDetailsModel.getId()));
				}
				userRoleDetailsEntity.setRole(userRoleDetailsModel.getRole());
			}
		} catch (Exception e) {
			logger.error(
					"Excpetion UserRoleDetailsDto.getUserRoleDetailsEntity(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return userRoleDetailsEntity;
	}

	/**
	 * This method will set the list of UserRoleDetailsModel with values from list
	 * of UserRoleDetailsEntity.
	 * 
	 * @param UserRoleDetailsEntity
	 * @return userRoleDetailsModel
	 */
	public List<UserRoleDetailsModel> getRoleList(List<UserRoleDetailsEntity> roleList) {
		List<UserRoleDetailsModel> userRoleDetailsModelList = new ArrayList<>();
		UserRoleDetailsModel userRoleDetailsModel = null;

		if (roleList != null) {
			for (UserRoleDetailsEntity userRoleDetailsEntity : roleList) {
				userRoleDetailsModel = getUserRoleDetailsModel(userRoleDetailsEntity);
				userRoleDetailsModelList.add(userRoleDetailsModel);
			}
		}
		return userRoleDetailsModelList;
	}

	/**
	 * This method will get UserRoleDetailsModel from UserRoleDetailsEntity
	 * 
	 * @param UserRoleDetailsEntity
	 * @return UserRoleDetailsModel
	 */
	public UserRoleDetailsModel getUserRoleDetailsModel(UserRoleDetailsEntity userRoleDetailsEntity) {
		UserRoleDetailsModel userRoleDetailsModel = null;
		if (userRoleDetailsEntity != null) {
			userRoleDetailsModel = new UserRoleDetailsModel();
			userRoleDetailsModel.setId(userRoleDetailsEntity.getId());
			userRoleDetailsModel.setRole(userRoleDetailsEntity.getRole());
		}
		return userRoleDetailsModel;
	}
}
