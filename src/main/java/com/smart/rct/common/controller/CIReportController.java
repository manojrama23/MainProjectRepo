package com.smart.rct.common.controller;

import java.util.LinkedHashMap;
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

import com.smart.rct.common.service.CIReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.util.CommonUtil;

@RestController
public class CIReportController {
	
	final static Logger logger = LoggerFactory.getLogger(CIReportController.class);
	
	@Autowired
	CIReportService objCIReportService;

	
	/**
	 * This api gives the getCIReport
	 * 
	 * @param ciReportDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getCIReport", method = RequestMethod.POST)
	public JSONObject getCIReport(@RequestBody JSONObject ciReportDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String selectedDuration = null;
		String fromDate = null;
		String toDate = null;
		String selectionDate = null;
		try {
			sessionId = ciReportDetails.get("sessionId").toString();
			serviceToken = ciReportDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			customerId = Integer.valueOf(ciReportDetails.get("customerId").toString());
			selectedDuration = ciReportDetails.get("selectedDuration").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if (Constants.VZN_CUSTOMER_ID == customerId.intValue()) {
				if (StringUtils.isNotEmpty(selectedDuration) && "Weekly".equalsIgnoreCase(selectedDuration)) {
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingVerizonEntityListForCIReportsFriday();
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				} else if (StringUtils.isNotEmpty(selectedDuration) && "Periodic".equalsIgnoreCase(selectedDuration)) {
					fromDate = ciReportDetails.get("periodicFromDate").toString();
					toDate = ciReportDetails.get("periodicToDate").toString();
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingVerizonEntityListForCIReportsPeriodFriday(fromDate, toDate);
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				} else if (StringUtils.isNotEmpty(selectedDuration) && "Daily".equalsIgnoreCase(selectedDuration)) {
					selectionDate = ciReportDetails.get("dailySelectedDate").toString();
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingVerizonEntityListForCIReportsDailyWise(selectionDate);
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				}
			} else if (Constants.SPT_CUSTOMER_ID == customerId.intValue()) {
				if (StringUtils.isNotEmpty(selectedDuration) && "weekly".equalsIgnoreCase(selectedDuration)) {
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingSprintEntityListForCIReportsMonday();
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				} else if (StringUtils.isNotEmpty(selectedDuration) && "Periodic".equalsIgnoreCase(selectedDuration)) {
					fromDate = ciReportDetails.get("periodicFromDate").toString();
					toDate = ciReportDetails.get("periodicToDate").toString();
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingSprintEntityListForCIReportsMondayPeriod(fromDate, toDate);
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				} else if (StringUtils.isNotEmpty(selectedDuration) && "Daily".equalsIgnoreCase(selectedDuration)) {
					selectionDate = ciReportDetails.get("dailySelectedDate").toString();
					Map<String, Object> objTotalCIReportModel = objCIReportService
							.getSchedulingSprintEntityListForCIReportsDailyWise(selectionDate);
					resultMap.put("totCIReportDetails", objTotalCIReportModel);
				}
			} else {
				resultMap.put("totCIReportDetails", new LinkedHashMap<String, Object>());
			}
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_REPORT_DETAILS));
			logger.info("Exception in EnodeBViewMapController.getMapEnodeBDetails(): "+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
}
