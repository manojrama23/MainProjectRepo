package com.smart.rct.usermanagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.usermanagement.dto.UserDetailsDto;
import com.smart.rct.usermanagement.dto.UserRoleDetailsDto;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.service.UserDetailsService;
import com.smart.rct.usermanagement.service.UserRoleDetailsService;
import com.smart.rct.util.CommonUtil;

@RestController
public class UserDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);

	@Autowired
	CustomerService customerService;

	@Autowired
	NetworkTypeDetailsService networkTypeDetailsService;

	@Autowired
	UserDetailsDto userDetailsDto;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	UserRoleDetailsService userRoleDetailsService;

	@Autowired
	UserRoleDetailsDto userRoleDetailsDto;

	@Autowired
	CommonUtil commonUtil;

	/**
	 * This method will create user - Save new user details to DB
	 * 
	 * @param createUserDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.CREATE_USER, method = RequestMethod.POST)
	public JSONObject createUser(@RequestBody JSONObject createUserDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		try {
			sessionId = createUserDetails.get("sessionId").toString();
			serviceToken = createUserDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			UserDetailsModel userDetailsModel = new Gson().fromJson(
					createUserDetails.toJSONString((Map) createUserDetails.get("dbDetail")), UserDetailsModel.class);
			if (userDetailsService.duplicateUser(userDetailsModel)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.USERNAME_EXIST));
				return resultMap;
			}
			UserDetailsEntity userEntity = userDetailsDto.getUserDetailsEntity(userDetailsModel, sessionId);
			if (userEntity != null) {
				if (userDetailsService.createUser(userEntity)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_USER_MANAGEMENT,
							Constants.ACTION_SAVE,
							"User" + " " + userDetailsModel.getUserFullName() + " " + "Created Successfully",
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATION_FAILED));
				}
			}
		} catch (Exception e) {
			logger.info("Exception in UserDetailsController.createUser(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATION_FAILED));
		}
		return resultMap;
	}

	/**
	 * This method will update user details to DB
	 * 
	 * @param updateUserDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.UPDATE_USER, method = RequestMethod.POST)
	public JSONObject updateUser(@RequestBody JSONObject updateUserDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		try {
			sessionId = updateUserDetails.get("sessionId").toString();
			serviceToken = updateUserDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			UserDetailsModel userDetailsModel = new Gson().fromJson(
					updateUserDetails.toJSONString((Map) updateUserDetails.get("dbDetail")), UserDetailsModel.class);
			if (userDetailsService.duplicateUser(userDetailsModel)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.USERNAME_EXIST));
				return resultMap;
			}
			UserDetailsEntity userEntity = userDetailsDto.getUserDetailsEntity(userDetailsModel, sessionId);
			if (userEntity != null) {
				if (userDetailsService.updateUser(userEntity)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPDATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_USER_MANAGEMENT,
							Constants.ACTION_UPDATE,
							"User" + " " + userDetailsModel.getUserFullName() + " Details Updated Successfully",
							sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPDATION_FAILED));
				}
			}
		} catch (Exception e) {
			logger.info("Exception in UserDetailsController.updateUser(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPDATION_FAILED));
		}
		return resultMap;
	}

	/**
	 * This api gives the userList
	 * 
	 * @param userListDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.USER_LIST, method = RequestMethod.POST)
	public JSONObject userList(@RequestBody JSONObject userListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		UserDetailsModel userDetailsModel = null;
		try {
			sessionId = userListDetails.get("sessionId").toString();
			serviceToken = userListDetails.get("serviceToken").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (StringUtils.isNotEmpty(userListDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(userListDetails.get("customerId").toString());
			}
			if (CommonUtil.isValidObject(userListDetails.get("searchStatus")) && "search".equals(userListDetails.get("searchStatus"))) {
				userDetailsModel = new Gson().fromJson(
						JSONObject.toJSONString((Map) userListDetails.get("searchDetails")),
						UserDetailsModel.class);
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) userListDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			Map<String, Object> objMap = userDetailsService.getUserList(user, userDetailsModel, customerId, page, count);
			List<UserDetailsModel> userList = (List<UserDetailsModel>) objMap.get("userList");
			List<UserRoleDetailsEntity> roleList = userRoleDetailsService.getRoleList();
			List<CustomerEntity> customerList = customerService.getCustomerList(true, false);
			//Object pageCount = userDetailsService.getPageCount(page, count);
			if (!CommonUtil.isValidObject(userList)) {
				userList = new ArrayList<UserDetailsModel>();
			}
			resultMap.put("pageCount", objMap.get("pageCount"));
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", null);
			resultMap.put("userList", userList);
			resultMap.put("roleList", roleList);
			resultMap.put("customerList", customerList);
		} catch (Exception e) {
			logger.info("Exception in UserDetailsController.userList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.GET_LIST_FAILED));

		}
		return resultMap;
	}

	/**
	 * This api delete the deleteUser
	 * 
	 * @param deleteUserDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_USER, method = RequestMethod.POST)
	public JSONObject deleteUser(@RequestBody JSONObject deleteUserDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String userId = null;
		try {
			sessionId = deleteUserDetails.get("sessionId").toString();
			serviceToken = deleteUserDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			userId = deleteUserDetails.get("userId").toString();
			if (StringUtils.isNotEmpty(userId)) {
				UserDetailsEntity userDetailsEntity = userDetailsService.getUserById(Integer.valueOf(userId));
				if (userDetailsService.deleteUser(Integer.valueOf(userId))) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_USER_MANAGEMENT,
							Constants.ACTION_DELETE,
							"User Deleted Successfully: " + userDetailsEntity.getUserFullName(), sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETION_FAILED));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETION_FAILED));
			logger.error("Exception in UserDetailsController.deleteUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getUserNameList", method = RequestMethod.POST)
	public JSONObject getUserNameList(@RequestBody JSONObject userListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		UserDetailsModel userDetailsModel = null;
		try {
			sessionId = userListDetails.get("sessionId").toString();
			serviceToken = userListDetails.get("serviceToken").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (StringUtils.isNotEmpty(userListDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(userListDetails.get("customerId").toString());
			}
			List<String> userNameList =  userDetailsService.getUserNameList(user, userDetailsModel, customerId);
			resultMap.put("userNameList", userNameList);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", null);
		} catch (Exception e) {
			logger.info("Exception in UserDetailsController.userList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.GET_LIST_FAILED));

		}
		return resultMap;
	}
	
}
