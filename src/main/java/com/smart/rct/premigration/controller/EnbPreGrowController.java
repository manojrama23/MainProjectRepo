package com.smart.rct.premigration.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.dto.EnbPreGrowDto;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.service.EnbPreGrowService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class EnbPreGrowController {
	final static Logger logger = LoggerFactory.getLogger(EnbPreGrowController.class);
	
	
	@Autowired
	EnbPreGrowService enbPreGrowService;
	
	@Autowired
	EnbPreGrowDto enbPreGrowDto;
	
	
	/**
	 * This method will give the NeGrow details
	 * 
	 * @param neDetails
	 * @return resultMap
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_NE_GROW_DETAILS)
	public JSONObject getNeGrowDetails(@RequestBody JSONObject neDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String searchStatus = null;
		Integer programId = null;
		String programName = null;
		List<EnbPreGrowAuditEntity> enbPreGrowAuditEntity = null;
		List<EnbPreGrowAuditModel> enbPreGrowAuditModel = null;
		EnbPreGrowAuditModel enbModel = null;
		try {
			sessionId = neDetails.get("sessionId").toString();
			serviceToken = neDetails.get("serviceToken").toString();
			searchStatus = neDetails.get("searchStatus").toString();
			programName = neDetails.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) { return expiryDetails; }
			
			programId = (Integer) neDetails.get("programId");
			/*Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			List<CiqUploadAuditTrailDetEntity> ciqList = enbPreGrowService.getCiqList(user, String.valueOf(programId),startDate,curdate);
			resultMap.put("ciqList",ciqList);
			Map<String, List<String>> versionList = enbPreGrowService.getSmDetails(programId);
			resultMap.put("versionList",versionList);
			Map<String, List<String>> versionSearchList = enbPreGrowService.getSmSearchDetails(programId);
			resultMap.put("versionSearchList",versionSearchList);
			Map<String, List<String>> ciqNeSearchList = enbPreGrowService.getCiqNeSearchDetails(programId);
				resultMap.put("ciqNeSearchList",ciqNeSearchList);
			Map<String, Integer> paginationData = (Map<String, Integer>) neDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			if (Constants.LOAD.equals(searchStatus)) {
				enbModel = new EnbPreGrowAuditModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				enbModel.setProgramDetailsEntity(programDetailsEntity);
				enbModel.setSearchStartDate(startDate);
				enbModel.setSearchEndDate(curdate);
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				enbModel = new Gson().fromJson(neDetails.toJSONString((Map) neDetails.get("searchCriteria")),EnbPreGrowAuditModel.class);
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				enbModel.setProgramDetailsEntity(programDetailsEntity);
			}
			Map<String, Object> csvList = enbPreGrowService.getNeGrowDetails(enbModel, page, count);
			resultMap.put("pageCount", csvList.get("paginationcount"));
			resultMap.put("useCaseList", csvList.get("useCaseList"));
			resultMap.put("usecaseSearchList", csvList.get("usecaseSearchList"));
			resultMap.put("searchStartDate", startDate);
			resultMap.put("searchEndDate", curdate);
			enbPreGrowAuditEntity = (List<EnbPreGrowAuditEntity>) csvList.get("preGrowList");
			if (CommonUtil.isValidObject(enbPreGrowAuditEntity)) {
				enbPreGrowAuditModel = new ArrayList<EnbPreGrowAuditModel>();
				for(EnbPreGrowAuditEntity enbPreGrowEntity : enbPreGrowAuditEntity) {
					enbPreGrowAuditModel.add(enbPreGrowDto.getenbPreGrowAuditModel(enbPreGrowEntity));
				}
			}*/
			resultMap.put("neGrowdetails", enbPreGrowAuditModel);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_PRE_GROW_DETAILS));
			logger.error("Exception in getCsvAuditDetails()   GenerateCsvController:" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	
	
	
}
