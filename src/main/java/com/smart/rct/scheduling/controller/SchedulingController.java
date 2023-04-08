package com.smart.rct.scheduling.controller;

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
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.scheduling.dto.SchedulingDto;
import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;
import com.smart.rct.scheduling.service.SchedulingService;
import com.smart.rct.util.CommonUtil;

@RestController
public class SchedulingController {

	final static Logger logger = LoggerFactory.getLogger(SchedulingController.class);
	
	@Autowired
	CommonUtil commonUtil;
	
	@Autowired
	SchedulingService objSchedulingService;
	
	@Autowired
	SchedulingDto objSchedulingDto;
	
	/**
	 * This method will Add Scheduling Details to DB
	 * 
	 * @param addSchedulingDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/scheduling", method = RequestMethod.POST)
	public JSONObject addSchedulingDetails(@RequestBody JSONObject addSchedulingDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;

		try {

			sessionId = addSchedulingDetails.get("sessionId").toString();
			serviceToken = addSchedulingDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			// check if session expired
			/*
			 * expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId); if
			 * (expiryDetails != null) { return expiryDetails; }
			 */

			SchedulingModel objSchedulingModel = new Gson()
					.fromJson(addSchedulingDetails.toJSONString((Map) addSchedulingDetails.get("schedulingDetails")), SchedulingModel.class);
			// check duplicate scheduling creation
						if (objSchedulingService.duplicateScheduling(objSchedulingModel)) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.LSMNAME_LSMVERSION_NETWORKTYPE_EXISTS));
							return resultMap;
						}
			SchedulingEntity objSchedulingEntity = objSchedulingDto.getSchedulingEntity(objSchedulingModel, sessionId);

			if (objSchedulingEntity != null) {
				// creating new
				if (objSchedulingService.createSchedule(objSchedulingEntity)) {
					// audit required
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCHEDULING_DETAILS_CREATED_SUCCESSFULLY));
					//commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_SYSTEM_MANAGER_CONFIG, Constants.ACTION_SAVE, "Scheduling Details Created Successfully" , sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_SCHEDULING_DETAILS));
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in addSchedulingDetails   SchedulingController:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_SCHEDULING_DETAILS));
			return resultMap;
		}

	}
}
