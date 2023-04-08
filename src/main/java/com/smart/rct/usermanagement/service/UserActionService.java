package com.smart.rct.usermanagement.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import com.smart.rct.exception.RctException;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserDetailsModel;

public interface UserActionService {
	
	public List<UserDetailsEntity> getUserList(String status);
	

	public boolean validUser(UserDetailsModel userEntity, String password);

	public UserDetailsModel getUserDetails(String userName);

	public boolean setLastLogin(String userName, Date loginDate);

	public UserDetailsEntity getUserDetailsByEmailId(String emailId);

	public boolean changePassword(String userName, String newPassword, boolean decrypt);

	public String resetPassword(String userName, String newPassword);

	public boolean mailNewPassword(String userFullName, String userName, String newPassword, String emailId)
			throws AddressException, AuthenticationFailedException, MessagingException, InterruptedException,
			TimeoutException, Exception, RctException;

	public UserRoleDetailsEntity getRoleById(Integer roleId);

}
