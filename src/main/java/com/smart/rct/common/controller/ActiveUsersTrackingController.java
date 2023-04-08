package com.smart.rct.common.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.smart.rct.common.dto.AuditTrailDto;
import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.service.ActiveUsersTrackingService;
import com.smart.rct.common.service.AuditService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.GlobalStatusMap;

@RestController
public class ActiveUsersTrackingController {

	final static Logger logger = LoggerFactory.getLogger(ActiveUsersTrackingController.class);

	@Autowired
	AuditTrailDto auditTrailDto;

	@Autowired
	ActiveUsersTrackingService as;

	@Autowired
	CommonUtil commonUtil;
	
	
	/**
	 * This method will give the Audit Trail details
	 * 
	 * @param auditListDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.UPDATE_ACTIVE_USERS, method = RequestMethod.GET)
	public String addActiveUsers() {
		ActiveUsersTracking au = new ActiveUsersTracking();
		au.setActiveUsers(GlobalStatusMap.loginUsersDetails.size());
		au.setActiveSessions(GlobalStatusMap.socketSessionUser.size());
		au.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
		as.savedetail(au);
		try {
			logger.info("Test Controller");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
}
