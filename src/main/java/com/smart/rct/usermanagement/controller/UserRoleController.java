package com.smart.rct.usermanagement.controller;

import java.util.List;

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
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.usermanagement.dto.UserRoleDetailsDto;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.UserRoleDetailsModel;
import com.smart.rct.usermanagement.service.UserRoleDetailsService;
import com.smart.rct.util.CommonUtil;

@RestController
public class UserRoleController {

	final static Logger logger = LoggerFactory.getLogger(UserRoleController.class);

	@Autowired
	UserRoleDetailsService userRoleDetailsService;

	@Autowired
	UserRoleDetailsDto userRoleDetailsDto;

	@Autowired
	CommonUtil commonUtil;

	/**
	 * This api create the Role
	 * 
	 * @param createRoleDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.CREATE_ROLE, method = RequestMethod.POST)
	public JSONObject createRole(@RequestBody JSONObject createRoleDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		try {
			sessionId = createRoleDetails.get("sessionId").toString();
			serviceToken = createRoleDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			UserRoleDetailsModel userRoleDetailsModel = new Gson()
					.fromJson(createRoleDetails.get("roleDetail").toString(), UserRoleDetailsModel.class);
			UserRoleDetailsEntity userRoleEntity = userRoleDetailsDto.getUserRoleDetailsEntity(userRoleDetailsModel);
			if (userRoleEntity != null) {
				if (!userRoleDetailsService.duplicateRole(userRoleEntity)) {
					if (userRoleDetailsService.createRole(userRoleEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATED_ROLE_SUCCESSFULLY));
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATE_ROLE_FAILED));
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_EXIST));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATE_ROLE_FAILED));
			logger.error("Exception in UserRoleController.createRole(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api update the Role
	 * 
	 * @param roleDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPDATE_ROLE, method = RequestMethod.POST)
	public JSONObject updateRole(@RequestBody JSONObject updateRoleDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		try {
			sessionId = updateRoleDetails.get("sessionId").toString();
			serviceToken = updateRoleDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			UserRoleDetailsModel userRoleDetailsModel = new Gson()
					.fromJson(updateRoleDetails.get("roleDetail").toString(), UserRoleDetailsModel.class);
			UserRoleDetailsEntity userRoleEntity = userRoleDetailsDto.getUserRoleDetailsEntity(userRoleDetailsModel);
			if (userRoleEntity != null) {
				if (!userRoleDetailsService.duplicateRole(userRoleEntity)) {
					if (userRoleDetailsService.updateRole(userRoleEntity)) {
						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_UPDATED_SUCCESSFULLY));
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_UPDATE_FAILED));
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_EXIST));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_UPDATE_FAILED));
			logger.error("Exception in UserRoleController.updateRole(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api delete the Role
	 * 
	 * @param deleteRoleDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_ROLE, method = RequestMethod.POST)
	public JSONObject deleteRole(@RequestBody JSONObject deleteRoleDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String roleId = null;
		try {
			sessionId = deleteRoleDetails.get("sessionId").toString();
			serviceToken = deleteRoleDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			roleId = deleteRoleDetails.get("rollId").toString();
			if (StringUtils.isNotEmpty(roleId)) {
				if (userRoleDetailsService.deleteRole(Integer.valueOf(roleId))) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_DELETED_SUCCESSFULLY));
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_DELETION_FAILED));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.ROLE_DELETION_FAILED));
			logger.error("Exception in UserRoleController.deleteRole(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api get the Role details
	 * 
	 * @param getRoleListDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_ROLE_LIST, method = RequestMethod.POST)
	public JSONObject getRoleList(@RequestBody JSONObject getRoleListDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		try {
			sessionId = getRoleListDetails.get("sessionId").toString();
			serviceToken = getRoleListDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			List<UserRoleDetailsEntity> userRoleDetailsEntityList = userRoleDetailsService.getRoleList();
			resultMap.put("roleDetails", userRoleDetailsEntityList);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.GET_ROLE_FAILED));
			logger.error("Exception in UserRoleController.getRoleList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

}