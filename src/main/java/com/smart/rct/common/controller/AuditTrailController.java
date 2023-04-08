package com.smart.rct.common.controller;

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
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.service.AuditService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.util.CommonUtil;

@RestController
public class AuditTrailController {

	final static Logger logger = LoggerFactory.getLogger(AuditTrailController.class);

	@Autowired
	AuditTrailDto auditTrailDto;

	@Autowired
	AuditService auditService;

	@Autowired
	CommonUtil commonUtil;
	
	
	/**
	 * This method will give the Audit Trail details
	 * 
	 * @param auditListDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.GET_AUDIT_DETAILS, method = RequestMethod.POST)
	public JSONObject getAuditTrail(@RequestBody JSONObject auditListDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String searchStatus = null;
		List<AuditTrailEntity> auditTrailEntities= null;
		List<AuditTrailModel> auditTrailModels= null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			searchStatus = auditListDetails.get("searchStatus").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			if(Constants.LOAD.equals(searchStatus)){
				Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");
				int count = paginationData.get("count");
				int page = paginationData.get("page");	
				Map<String, Object> auditList = auditService.getAuditDetails(page,count);
				resultMap.put("pageCount", auditList.get("pageCount"));
				auditTrailEntities = (List<AuditTrailEntity>) auditList.get("auditList");
			}if(Constants.SEARCH.equals(searchStatus)){
				Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");
				AuditTrailModel auditTrailModel = new Gson().fromJson(JSONObject.toJSONString((Map) auditListDetails.get("searchCriteria")),AuditTrailModel.class);
				int count = paginationData.get("count");
				int page = paginationData.get("page");	
				Map<String, Object> auditList = auditService.getAuditDetailsOnSearch(auditTrailModel,page,count);
				resultMap.put("pageCount", auditList.get("paginationcount"));
				auditTrailEntities = (List<AuditTrailEntity>) auditList.get("list");
			}
			if(CommonUtil.isValidObject(auditTrailEntities)){
				auditTrailModels = new ArrayList<AuditTrailModel>();
				for(AuditTrailEntity auditTrailEntity: auditTrailEntities){
					auditTrailModels.add(auditTrailDto.getAuditTrailDetailsModel(auditTrailEntity));
				}
			}
			List<String> eventNameList = auditService.getAuditFilters(Constants.AUDIT_SEARCH_EVENT_NAME, new AuditTrailEntity());
			resultMap.put("eventNameList", eventNameList);
			resultMap.put("auditTrailDetails", auditTrailModels);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in AuditTrailController.getauditList(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_AUDIT_DETAILS));
		}
		return resultMap;
	}
	
	
	/**
	 * This method will Filter the Audit Trail details based on criteria
	 * 
	 * @param auditDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_AUDIT_FILTER_DETAILS, method = RequestMethod.POST)
	public JSONObject getAuditFilters(@RequestBody JSONObject auditDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String searchStatus = null;
		String filterName="";
		try {
			sessionId = auditDetails.get("sessionId").toString();
			serviceToken = auditDetails.get("serviceToken").toString();
			searchStatus = auditDetails.get("searchStatus").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			AuditTrailEntity auditTrailEntity = new AuditTrailEntity();
			if(searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_EVENT_SUB_NAME)){
				auditTrailEntity.setEventName(auditDetails.get("eventName").toString());
				filterName="eventSubNameList";
			}else if(searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_ACTION)){
				auditTrailEntity.setEventName(auditDetails.get("eventName").toString());
				auditTrailEntity.setEventSubName(auditDetails.get("eventSubName").toString());
				filterName="eventActionList";
			}else if(searchStatus.equalsIgnoreCase(Constants.AUDIT_SEARCH_USER_NAME)){
				auditTrailEntity.setEventName(auditDetails.get("eventName").toString());
				auditTrailEntity.setEventSubName(auditDetails.get("eventSubName").toString());
				auditTrailEntity.setActionPerformed(auditDetails.get("actionPerformed").toString());
				filterName="eventUserList";
			}
			List<String> filterList = auditService.getAuditFilters(searchStatus, auditTrailEntity);
			resultMap.put(filterName, filterList);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in AuditTrailController.getAuditFilters(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FILTER_AUDIT_DETAILS));
		}
		return resultMap;
	}

	
}
