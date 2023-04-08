package com.smart.rct.usermanagement.repository;

import java.util.Date;
import java.util.List;

import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserDetailsModel;

public interface UserActionRepository {
	
	public List<UserDetailsEntity> getUserList(String status);

	public UserDetailsModel getUserDetails(String userName);

	public boolean setLastLogin(String userName, Date loginDate);

	public UserDetailsEntity getUserDetailsByEmailId(String emailId);

	public boolean changePassword(String userName, String newPassword, boolean decrypt);

	public boolean changeVPNPassword(String userName, String newPassword);

	public UserRoleDetailsEntity getRoleById(Integer roleId);
}
