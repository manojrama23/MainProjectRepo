package com.smart.rct.common.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.smart.rct.common.models.TrackLatiTudeModel;
import com.smart.rct.common.service.CIReportService;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.EnodeBViewMapService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.premigration.models.DashBoardModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.service.UserDetailsService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class EnodeBViewMapController {
	
	final static Logger logger = LoggerFactory.getLogger(EnodeBViewMapController.class);
	
	@Autowired
	EnodeBViewMapService objEnodeBViewMapService;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CIReportService objCIReportService;
	
	
	/**
	 * This api gives the getCiqAuditDetailsList
	 * 
	 * @param pagingDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_MAP_ENB_DETAILS, method = RequestMethod.POST)
	public JSONObject getMapEnodeBDetails(@RequestBody JSONObject pagingDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		try {
			sessionId = pagingDetails.get("sessionId").toString();
			serviceToken = pagingDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			customerId = (Integer) pagingDetails.get("customerId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) { return expiryDetails; }
			Map<String, TrackLatiTudeModel> objViewMap = objEnodeBViewMapService.getMapEnodeBDetails(customerId);
			resultMap.put("mapDetails", objViewMap);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_AUDIT_DETAILS));
			logger.info("Exception in EnodeBViewMapController.getMapEnodeBDetails(): "+ ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	
	/**
	 * This api dashBoardDetails
	 * 
	 * @param dashBoardDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DASHBOARD_COUNT, method = RequestMethod.POST)
	public JSONObject dashBoardDetails(@RequestBody JSONObject dashBoardDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		List<CustomerEntity> customerEntities = null;
		int scheduledPercentage=58;
		int cancelledPercentage=42;
		try {
			sessionId = dashBoardDetails.get("sessionId").toString();
			serviceToken = dashBoardDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			DashBoardModel objDashBoardModel = userDetailsService.getDashBoardCountDetails();
			objDashBoardModel.setScheduledPercentage(String.valueOf(scheduledPercentage));
			objDashBoardModel.setCancelledPercentage(String.valueOf(cancelledPercentage));
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if(user.getRoleId()<3){
				customerEntities = customerService.getCustomerList(false, false);
				resultMap.put("customerList", customerEntities);
			}else{
				customerEntities =  new ArrayList<>();
				CustomerEntity customerEntity = customerService.getCustomerById(user.getCustomerId());
				if(customerEntity!=null && Constants.ACTIVE.equalsIgnoreCase(customerEntity.getStatus())){
					customerEntities.add(customerEntity);
				}
				resultMap.put("customerList", customerEntities);
			}
			Map<String, Object> objTotalCIReportModel = objCIReportService.getSchedulingDashBoardCIReports(customerEntities);
			Map<String, Object> objMarketCIReportModel = objCIReportService.getSchedulingDashBoardMarketCIReports(customerEntities);
			Map<String, Object> objreasonschartdata = objEnodeBViewMapService.getReasonsChartData();
			Map<String, Object> objrepchartdata = objEnodeBViewMapService.getRepChartData();
			
			resultMap.put("barChartData", objTotalCIReportModel);
			resultMap.put("marketBarChartData", objMarketCIReportModel);
			resultMap.put("repChartData", objrepchartdata);
			resultMap.put("reasonsChartData", objreasonschartdata);
			resultMap.put("dashBoardCountDetails", objDashBoardModel);
			resultMap.put("status", Constants.SUCCESS);
			
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_DBCOUNT));
			logger.error("Exception in EnodeBViewMapController.dashBoardDetails():" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
	
	/**
	 * This api for getCpuUsage
	 * 
	 * @param cpuUsageDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CPU_USAGE, method = RequestMethod.POST)
	public JSONObject getCpuUsage(@RequestBody JSONObject cpuUsageDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		try {
			sessionId = cpuUsageDetails.get("sessionId").toString();
			serviceToken = cpuUsageDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			StringBuilder cpuUsage  = userDetailsService.getCpuUsage();
			resultMap.put("cpuUsage", cpuUsage.toString());
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_DBCOUNT));
			logger.error("Exception in EnodeBViewMapController.getCpuUsage() :" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
	/**
	 * This api for getMapDetails
	 * 
	 * @param getMapDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_MAP_DETAILS, method = RequestMethod.POST)
	public JSONObject getMapDetails(@RequestBody JSONObject getMapDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		SchedulingSRModel schedulingSRModel = null;
		List<CustomerEntity> customerEntities = null;
		try {
			sessionId = getMapDetails.get("sessionId").toString();
			serviceToken = getMapDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			
			resultMap.put("searchStartDate", startDate);
			resultMap.put("searchEndDate", curdate);
			
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null){
				return expiryDetails;	
			}
			
			if (CommonUtil.isValidObject(getMapDetails.get("searchStatus")) && Constants.LOAD.equals(getMapDetails.get("searchStatus"))) {
				schedulingSRModel = new SchedulingSRModel();
				schedulingSRModel.setSearchStartDate(startDate);
				schedulingSRModel.setSearchEndDate(curdate);
			}
			if (CommonUtil.isValidObject(getMapDetails.get("searchStatus")) && Constants.SEARCH.equals(getMapDetails.get("searchStatus"))) {
				schedulingSRModel = new Gson().fromJson(JSONObject.toJSONString((Map) getMapDetails.get("searchDetails")),SchedulingSRModel.class);
			}
			
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if(user.getRoleId()<3){
				customerEntities = customerService.getCustomerList(false, false);
			}else{
				customerEntities =  new ArrayList<>();
				CustomerEntity customerEntity = customerService.getCustomerById(user.getCustomerId());
				if(customerEntity!=null && Constants.ACTIVE.equalsIgnoreCase(customerEntity.getStatus())){
					customerEntities.add(customerEntity);
				}
			}
			//List<Integer> customerIds = customerEntities.stream().map(e->e.getId()).collect(Collectors.toList()); 
			Map<String, Object> map = userDetailsService.getMapDetails(schedulingSRModel,customerEntities);
			
			resultMap.put("customerList",  map.get("customerList"));
			resultMap.put("markers", map.get("list"));
			resultMap.put("market", map.get("market"));
			resultMap.put("status", Constants.SUCCESS);
			
		}catch(Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_MAP_DETAILS));
			logger.error("Exception in EnodeBViewMapController.getMapDetails() :" + ExceptionUtils.getFullStackTrace(e));	
		}
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_USER_MAP_DETAILS, method = RequestMethod.POST)
	public JSONObject getUserDetails(@RequestBody JSONObject getMapDetails) {
		ConcurrentHashMap<String, User> map = new ConcurrentHashMap<String, User>();
		List<User> users = new ArrayList<>();
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = getMapDetails.get("sessionId").toString();
			serviceToken = getMapDetails.get("serviceToken").toString();
			map = GlobalStatusMap.loginUsersDetails;
			for (Map.Entry<String, User> entry : map.entrySet()) {
				User user = entry.getValue();
				users.add(user);
			}
			resultMap.put("userDetails", users);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_MAP_DETAILS));
			logger.error("Exception in getUserDetails() :" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
}
