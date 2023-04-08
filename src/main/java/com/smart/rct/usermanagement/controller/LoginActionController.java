package com.smart.rct.usermanagement.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repositoryImpl.UserActionRepositoryImpl;
import com.smart.rct.usermanagement.service.UserActionService;
import com.smart.rct.usermanagement.service.UserRoleDetailsService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class LoginActionController {

	final static Logger logger = LoggerFactory.getLogger(LoginActionController.class);

	@Autowired
	UserActionService userActionService;

	@Autowired
	UserRoleDetailsService userRoleDetailsService;

	@Autowired
	CustomerService customerService;

	@Autowired
	NetworkTypeDetailsService networkTypeDetailsService;

	@Autowired
	CommonUtil commonUtil;
	
	@Autowired    
	Environment env;
	
	@Autowired
	UserActionRepositoryImpl userActionRepositoryImpl;
	

	/**
	 * This is login controller
	 * 
	 * @param user
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.USER_LOGIN, method = RequestMethod.POST)
	public @ResponseBody JSONObject loginAction(@RequestBody JSONObject loginDetails, HttpServletRequest request,
			HttpSession session) {
		JSONObject resultMap = new JSONObject();
		User user = null;
		String serviceToken = null;

		try {
			user = new User();
			user.setUserName(loginDetails.get("username").toString());
			serviceToken = loginDetails.get("serviceToken").toString();
			//String sessionTimeOut = env.getProperty("sessionTimeOut").toString();
			String sessionTimeOut =  LoadPropertyFiles.getInstance().getProperty("sessionTimeOut").toString();
			//server properties
		//String config_path = "/home/user/apache/apache-tomcat-8.5.35/conf";
		//String server_properties_path = "/home/user/rct/Samsung/SMART/COMMON/Conf/application.properties";
		//String serverConfigDirPath = System.getProperty("catalina.base");
//		ClassLoader classLoader = getClass().getClassLoader();
//	    File file = new File(classLoader.getResource("server_properties_path").getFile());
//			File file = new File("/home/user/rct/Samsung/SMART/COMMON/Conf/application.properties"); 
//			  
//			  BufferedReader br = new BufferedReader(new FileReader(file)); 
//			  
//			  String st; 
//			  while ((st = br.readLine()) != null) {
//				  if(st.contains("sessionTimeOut")) {
//					  Pattern p = Pattern.compile("\\d+");
//				        Matcher m = p.matcher(st);
//				       String timeOut =  m.toString();
//				  }
//				  
//			  }

			UserDetailsModel userEntity = userActionService.getUserDetails(user.getUserName());

			if (userActionService.validUser(userEntity, loginDetails.get("password").toString())) {
				
				/*ConcurrentHashMap<String, User> map = new ConcurrentHashMap<String, User>();
				
				if(GlobalStatusMap.loginUsersDetails.size() >= Integer.valueOf(LoadPropertyFiles.getInstance().getProperty("totalActiveSessions"))) {
					resultMap = new JSONObject();
					resultMap.put("validUser", false);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", "Total Active users exceeds "+LoadPropertyFiles.getInstance().getProperty("totalActiveSessions"));
					return resultMap;
				}

				int count = 0;
				map = GlobalStatusMap.loginUsersDetails;
				for (Map.Entry<String, User> entry : map.entrySet()) {
					User entryUser = entry.getValue();
					if(entryUser.getUserName().equalsIgnoreCase(user.getUserName())) {
						count++;
					}
				}
				
				if(count >= Integer.valueOf(LoadPropertyFiles.getInstance().getProperty("sessionsPerUser"))) {
					resultMap = new JSONObject();
					resultMap.put("validUser", false);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", "Active User count exceeded "+LoadPropertyFiles.getInstance().getProperty("sessionsPerUser"));
					return resultMap;
				}*/
				
				if (!Constants.ACTIVE.equals(userEntity.getStatus())) {
					resultMap = new JSONObject();
					resultMap.put("validUser", false);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.USER_BLOCKED));
					resultMap.put("sessionTimeOut", sessionTimeOut);
					return resultMap;
				}

				Date loginDate = new Date();
				userActionService.setLastLogin(user.getUserName(), loginDate);
				user.setId(userEntity.getId());
				user.setRoleId(userEntity.getRoleId());
				user.setCustomerId(userEntity.getCustomerId());
				user.setServiceToken(serviceToken);
				user.setCreatedBy(userEntity.getCreatedBy());
				user.setRole(userEntity.getRole());
				user.setEmailId(userEntity.getEmailId());
				user.setUserFullName(userEntity.getUserFullName());

				if (CommonUtil.isValidObject(userEntity.getProgramNamehidden())) {
					user.setProgramName(userEntity.getProgramNamehidden().split(","));
				}

				if (userEntity.getLastLoginDate() != null) {
					user.setLastLoginTime((userEntity.getLastLoginDate()).toString());
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					user.setLastLoginTime((sdf.format(loginDate)));
				}
				resultMap.put("validUser", true);
				resultMap.put("userName", userEntity.getUserName());

				resultMap.put("emailId", userEntity.getEmailId());
				resultMap.put("userFullName", userEntity.getUserFullName());

				resultMap.put("userGroup", userActionService.getRoleById(userEntity.getRoleId()).getRole());
				resultMap.put("customerId", userEntity.getCustomerId());
				resultMap.put("sessionTimeOut", sessionTimeOut);
				if (CommonUtil.isValidObject(userEntity.getCustomerId()) && userEntity.getRoleId() >= 3) {
					resultMap.put("customerName",
							customerService.getCustomerById(userEntity.getCustomerId()).getCustomerName());
				} else {
					resultMap.put("customerName", "");
				}
				resultMap.put("networkTypeId", userEntity.getNetworkTypeId());
				if (CommonUtil.isValidObject(userEntity.getNetworkTypeId()) && userEntity.getRoleId() >= 3) {
					resultMap.put("networkType", networkTypeDetailsService
							.getNetworkTypeById(userEntity.getNetworkTypeId()).getNetworkType());
				} else {
					resultMap.put("networkType", "");
				}
				resultMap.put("rctSnapShot", LoadPropertyFiles.getInstance().getProperty("rctSnapShot"));
				resultMap.put("sessionId", user.getTokenKey());
				resultMap.put("serviceToken", serviceToken);
				
				List<CustomerDetailsEntity> programNamesList = null;
				if (user.getRoleId() <= 3) {
					CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
					CustomerEntity customerEntity = new CustomerEntity();
					customerEntity.setId(userEntity.getCustomerId());
					customerDetailsModel.setCustomerEntity(customerEntity);
					programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
					resultMap.put("programNamesList", programNamesList);
				} else {
					programNamesList = customerService.getProgramDetailsList(user);
					resultMap.put("programNamesList", programNamesList);
				}

				UserSessionPool.getInstance().addUser(user);
				request.getSession().setAttribute("userName", user);
				user.setLoginDate((new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())).toString());
				List<CustomerEntity> customerList = null;
				if (user.getRoleId() < Integer.parseInt(Constants.ROLE_ID_ADMIN)) {
					customerList =  customerService.getCustomerList(false, false);
				}else{
					customerList = new ArrayList<CustomerEntity>();
					CustomerEntity customerEntity = customerService.getCustomerById(user.getCustomerId());
					if(customerEntity!=null && Constants.ACTIVE.equalsIgnoreCase(customerEntity.getStatus())){
						customerList.add(customerEntity);
					}
				}
				resultMap.put("customerList", customerList);
				
				if(user.getRoleId() >= Integer.parseInt(Constants.ROLE_ID_ADMIN) && (!CommonUtil.isValidObject(customerList) || customerList.size()<= 0)){
					resultMap.put("validUser", false);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.USER_LOGIN_BLOCKED));
					return resultMap;
				}else if(user.getRoleId() >= Integer.parseInt(Constants.ROLE_ID_COMM_MANAGER) && (!CommonUtil.isValidObject(programNamesList) || programNamesList.size()<= 0)){
					resultMap.put("validUser", false);
					resultMap.put("serviceToken", serviceToken);
					resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.USER_LOGIN_BLOCKED));
					return resultMap;
				}
				GlobalStatusMap.loginUsersDetails.put(user.getTokenKey(), user);
				if(!GlobalStatusMap.socketSessionInCreation.containsKey(userEntity.getVpnUserName())){
					GlobalStatusMap.socketSessionInCreation.put(userEntity.getVpnUserName(), false);
				}				
				if(!GlobalStatusMap.userNeQueue.containsKey(userEntity.getVpnUserName())) {
					GlobalStatusMap.userNeQueue.put(userEntity.getVpnUserName(), new ConcurrentLinkedQueue<>());
				}
				resultMap.put("userDetails", user);
			} else {
				resultMap.put("validUser", false);
				resultMap.put("serviceToken", serviceToken);
				resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.INVALID_USER_OR_PASSWORD));
			}
		} catch (Exception e) {
			logger.error("Exception in LoginActionController.loginAction(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("validUser", false);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.USER_LOGIN_FAILED));
		}
		return resultMap;
	}

	/**
	 * This controller will logout the user
	 * 
	 * @param logoutDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.USER_LOGOUT, method = RequestMethod.POST)
	public @ResponseBody JSONObject logoutAction(@RequestBody JSONObject logoutDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		try {
			serviceToken = logoutDetails.get("serviceToken").toString();
			sessionId = logoutDetails.get("sessionId").toString();
			//stopDuoSession(sessionId);
			stopDuoSessionNEID(sessionId);
			UserSessionPool.getInstance().removeUser(sessionId);
			if (GlobalStatusMap.loginUsersDetails.containsKey(sessionId)) {
				GlobalStatusMap.loginUsersDetails.remove(sessionId);
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("serviceToken", serviceToken);
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in LoginActionController.logoutAction(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.USER_LOGOUT_FAILED));
			return resultMap;
		}
	}
	private void stopDuoSessionNEID(String sessionId) {
		try {
			User user1 = UserSessionPool.getInstance().getSessionUser(sessionId);
			UserDetailsEntity userEntity = userActionRepositoryImpl.getUserDetailsBasedName(user1.getUserName());
			System.out.println("-----------------------NeQueue############ : " + GlobalStatusMap.userNeQueue.get(userEntity.getVpnUserName()));
			if(GlobalStatusMap.userNeQueue.get(userEntity.getVpnUserName()).isEmpty()) {
				//GlobalStatusMap.socketSessionInCreation.remove(userEntity.getVpnUserName());
				for (Entry<String, Boolean> entry : GlobalStatusMap.socketSessionInCreation.entrySet()) {
					String key = entry.getKey().toString();
					Boolean value=entry.getValue();
					System.out.println("IN_Creation"+"-----"+key+"----"+value);
					
					if (key.contains(userEntity.getVpnUserName())) {
						GlobalStatusMap.socketSessionInCreation.remove(key);
						
					}
				}
				GlobalStatusMap.userNeQueue.remove(userEntity.getVpnUserName());
				for (Entry<String, SocketSession> entry : GlobalStatusMap.socketSessionUser.entrySet()) {
					String key = entry.getKey().toString();
					System.out.println("IN_USE"+"-----"+key);
					if (key.contains(userEntity.getVpnUserName())) {
						SocketSession ses = GlobalStatusMap.socketSessionUser.get(key);
						GlobalStatusMap.socketSessionUser.remove(key);
						ses.disconnectSession();
						
					}
				}
				System.out.println("-----------------------Session Disconnect ############ : ");
				/*if(GlobalStatusMap.socketSessionUser.containsKey(userEntity.getVpnUserName())) {
					SocketSession ses = GlobalStatusMap.socketSessionUser.get(userEntity.getVpnUserName());
					GlobalStatusMap.socketSessionUser.remove(userEntity.getVpnUserName());
					ses.disconnectSession();
					System.out.println("-----------------------Session Disconnect ############ : ");
				}*/			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void stopDuoSession(String sessionId) {
		try {
			User user1 = UserSessionPool.getInstance().getSessionUser(sessionId);
			UserDetailsEntity userEntity = userActionRepositoryImpl.getUserDetailsBasedName(user1.getUserName());
			System.out.println("-----------------------NeQueue############ : " + GlobalStatusMap.userNeQueue.get(userEntity.getVpnUserName()));
			if(GlobalStatusMap.userNeQueue.get(userEntity.getVpnUserName()).isEmpty()) {
				GlobalStatusMap.socketSessionInCreation.remove(userEntity.getVpnUserName());
				GlobalStatusMap.userNeQueue.remove(userEntity.getVpnUserName());
				if(GlobalStatusMap.socketSessionUser.containsKey(userEntity.getVpnUserName())) {
					SocketSession ses = GlobalStatusMap.socketSessionUser.get(userEntity.getVpnUserName());
					GlobalStatusMap.socketSessionUser.remove(userEntity.getVpnUserName());
					ses.disconnectSession();
					System.out.println("-----------------------Session Disconnect ############ : ");
				}			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * It will return success or failure in case of forget password
	 * 
	 * @param forgotPasswordDetails
	 * @return
	 * @throws MessagingException
	 * @throws AuthenticationFailedException
	 * @throws IOException
	 * @throws AddressException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.FORGOT_PASSWORD, method = RequestMethod.POST)
	public @ResponseBody JSONObject forgotPassword(@RequestBody JSONObject forgotPasswordDetails) {
		JSONObject resultMap = new JSONObject();
		try {
			String emailId = forgotPasswordDetails.get("emailId").toString();
			UserDetailsEntity userDetail = userActionService.getUserDetailsByEmailId(emailId);
			if (userDetail == null) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.NOT_A_REGISTERED_EMAIL));
				return resultMap;
			}

			String userName = userDetail.getUserName();
			String userFullName = userDetail.getUserFullName();
			String newPassword = randomString(8);
			if (newPassword != null) {
				try {
					if (userActionService.mailNewPassword(userFullName, userName, newPassword, emailId)) {
						userActionService.resetPassword(userName, newPassword);
						if (userActionService.changePassword(userName, newPassword, false)) {
							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason", null);
						} else {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.PASSWORD_RESET_FAILED));
							return resultMap;
						}
						return resultMap;
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNABLE_TO_READ_PROPERTIES));
						return resultMap;
					}
				} catch (AuthenticationFailedException ae) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNABLE_TO_AUTHENTICATE));
					return resultMap;
				} catch (MessagingException me) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNABLE_TO_MESSAGE));
					return resultMap;
				} catch (RctException e) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", e.getMessage());
					return resultMap;
				} catch (TimeoutException te) {
					logger.error("Exception Failed to send message:" + ExceptionUtils.getFullStackTrace(te));
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.EMAIL_SESSION_TIME_OUT));
					return resultMap;
				}

			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.PASSWORD_RESET_FAILED));
				return resultMap;
			}
		} catch (Exception e) {
			logger.error("Exception in LoginActionController.forgotPassword(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.PASSWORD_RESET_FAILED));
			return resultMap;
		}
	}

	/**
	 * This controller will enable the user to change password
	 * 
	 * @param changePasswordDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.CHANGE_PASSWORD, method = RequestMethod.POST)
	public @ResponseBody JSONObject changePassword(@RequestBody JSONObject changePasswordDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		try {
			String userName = changePasswordDetails.get("userName").toString();
			String password = changePasswordDetails.get("currentPassword").toString();
			String newPassword = changePasswordDetails.get("newPassword").toString();
			sessionId = changePasswordDetails.get("sessionId").toString();
			serviceToken = changePasswordDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			UserDetailsModel userEntity = userActionService.getUserDetails(userName);
			if (userActionService.validUser(userEntity, password)) {
				if (!userActionService.changePassword(userName, newPassword, true)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNABLE_TO_CHANGE_PASSWORD));
					return resultMap;
				}
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.INVALID_PASSWORD));
				return resultMap;
			}
			resultMap.put("status", Constants.SUCCESS);
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in LoginActionController.changePassword(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNABLE_TO_CHANGE_PASSWORD));
			return resultMap;
		}
	}

	/**
	 * This method generates a returns a random String for password
	 * 
	 * @param passLength
	 * @return String
	 */
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
			logger.error("Exception in LoginActionController.randomString(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return ranString;
	}
}
