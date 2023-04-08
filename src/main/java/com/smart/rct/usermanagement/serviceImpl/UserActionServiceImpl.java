package com.smart.rct.usermanagement.serviceImpl;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.exception.RctException;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repository.UserActionRepository;
import com.smart.rct.usermanagement.service.UserActionService;
import com.smart.rct.util.EmailUtil;
import com.smart.rct.util.PasswordCrypt;

@Service
@Transactional
public class UserActionServiceImpl implements UserActionService {

	final static Logger logger = LoggerFactory.getLogger(UserActionServiceImpl.class);

	@Autowired
	UserActionRepository userActionRepository;

	@Autowired
	EmailUtil emailUtil;

	/**
	 * This method validates userName and password for a user
	 * 
	 * @param userName
	 * @param password
	 * @return boolean
	 */
	@Override
	public boolean validUser(UserDetailsModel userEntity, String password) {
		try {
			if (userEntity != null && PasswordCrypt.decryptPasswordUI(password)
					.equals(PasswordCrypt.decrypt(userEntity.getPassword()))) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.validUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return false;
	}

	/**
	 * This method gets User Details for a userName
	 * 
	 * @param userName
	 * @return UserDetailsModel
	 */
	@Override
	public UserDetailsModel getUserDetails(String userName) {
		UserDetailsModel user = null;
		try {
			user = userActionRepository.getUserDetails(userName);
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.getUserDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return user;
	}

	/**
	 * This api get the userlist by status
	 * 
	 * @param status
	 * @return List<UserDetailsEntity>
	 */
	@Override
	public List<UserDetailsEntity> getUserList(String status)) {
		List<UserDetailsEntity> userDetailsEntity = null;
		try {
			userDetailsEntity = userActionRepository.getUserList(status);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getCustomerList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return userDetailsEntity;
	}
	
	
	/**
	 * This method gets last login details
	 * 
	 * @param userName
	 * @param loginDate
	 * @return return
	 */
	@Override
	public boolean setLastLogin(String userName, Date loginDate) {
		boolean status = false;
		try {
			return userActionRepository.setLastLogin(userName, loginDate);
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.setLastLogin(): " + ExceptionUtils.getFullStackTrace(e));
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
		UserDetailsEntity userDetailsEntity = null;
		try {
			userDetailsEntity = userActionRepository.getUserDetailsByEmailId(emailId);
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.getUserDetailsByEmailId(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return userDetailsEntity;
	}

	/**
	 * This method is used to change user password
	 * 
	 * @param userName
	 * @param newPassword
	 * @return boolean
	 */
	@Override
	public boolean changePassword(String userName, String newPassword, boolean decrypt) {
		boolean status = false;
		try {
			return userActionRepository.changePassword(userName, newPassword, decrypt);
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.changePassword(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method is resetPassword
	 * 
	 * @param userName
	 * @return String
	 */
	@Override
	public String resetPassword(String userName, String newPwd) {
		String newPassword = null;
		try {
			newPassword = newPwd;
			if (changePassword(userName, PasswordCrypt.encryptPasswordUI(newPassword), false)) {
				return newPassword;
			}
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.resetPassword(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return newPassword;
	}

	/**
	 * This method generates a returns a random String for password
	 * 
	 * @param passLength
	 * @return String
	 */
	@SuppressWarnings("unused")
	private String randomString(int passLength) {
		String ranString = null;
		String desiredCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom secRandom = new SecureRandom();
		try {
			StringBuilder sb = new StringBuilder(passLength);
			for (int i = 0; i < passLength; i++) {
				sb.append(desiredCharacters.charAt(secRandom.nextInt(desiredCharacters.length())));
			}
			ranString = sb.toString();
		} catch (Exception e) {
			logger.error("Exception UserActionServiceImpl.randomString(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return ranString;
	}

	/**
	 * This method sends the mail with new password
	 * 
	 * @param userName,
	 *            newPassword, emailId
	 * @return boolean
	 */
	@Override
	public boolean mailNewPassword(String userFullName, String userName, String newPassword, String emailId)
			throws Exception {
		try {
			try {
				String[] toList = emailId.split(",");

				StringBuilder bodyText = new StringBuilder();
				bodyText.append("Dear " + userFullName + ",");
				bodyText.append("<br/><br/>");
				bodyText.append("Please find login details for your account with SMART:");
				bodyText.append("<br/>");
				bodyText.append("Username : " + userName);
				bodyText.append("<br/>");
				bodyText.append("Password : " + newPassword);
				bodyText.append("<br/><br/>");
				bodyText.append("Regards");
				bodyText.append("<br/>");
				bodyText.append("SMART Administrator");
				String subject = "Login details for SMART";
				if (emailUtil.sendEmail(toList, null, null, subject, bodyText.toString(), true)) {
					return true;
				}
			} catch (InterruptedException | TimeoutException te) {
				throw te;
			}
		} catch (RctException e) {
			throw e;
		} catch (AddressException e) {
			throw e;
		} catch (AuthenticationFailedException ae) {
			throw ae;
		} catch (MessagingException me) {
			throw me;
		}
		return false;
	}

	/**
	 * This method returns UserRoleDetailsEntity by its Id
	 * 
	 * @param roleId
	 * @return UserRoleDetailsEntity
	 */
	@Override
	public UserRoleDetailsEntity getRoleById(Integer roleId) {
		return userActionRepository.getRoleById(roleId);
	}

}
