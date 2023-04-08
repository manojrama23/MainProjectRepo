package com.smart.rct.premigration.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.dto.NeMappingDto;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.util.CommonUtil;

@RestController
public class NeMappingController {
	final static Logger logger = LoggerFactory.getLogger(NeMappingController.class);

    @Autowired
	NeMappingService neMappingService;
    @Autowired
    NeMappingDto neMappingDto;
    @Autowired
	CommonUtil commonUtil;
    
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_NE_MAPPING_DETAILS)
	public JSONObject getNeMappingDetails(@RequestBody JSONObject neDetails) {
		String sessionId = null;
		String programName = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		String searchStatus = null;
		JSONObject expiryDetails = null;
		Integer programId = null;
		List<NeMappingEntity> neMappingEntity = null;
		NeMappingModel neMappingModel = null;
		try {
			sessionId = neDetails.get("sessionId").toString();
			serviceToken = neDetails.get("serviceToken").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			searchStatus = neDetails.get("searchStatus").toString();
			programName = neDetails.get("programName").toString();
			if (expiryDetails != null) 
			{ 
				return expiryDetails; 
			}
			programId = (Integer) neDetails.get("programId");
			Map<String, Integer> paginationData = (Map<String, Integer>) neDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");
			if (Constants.LOAD.equals(searchStatus)){
				neMappingModel = new NeMappingModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				neMappingModel = new Gson().fromJson(neDetails.toJSONString((Map) neDetails.get("searchCriteria")),NeMappingModel.class);
				
				String tempEnb = neMappingModel.getEnbId();
				if (programName.equals("VZN-5G-MM")) {
					if (StringUtils.isNotEmpty(tempEnb) && tempEnb.length()>0 && tempEnb.charAt(0)=='0') {
						tempEnb = tempEnb.substring(1, tempEnb.length());
						neMappingModel.setEnbId(tempEnb);
					}
				}
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
			}
			Map<String,Map<String,List<NetworkConfigEntity>>> dropDownList = neMappingService.getDropDownList(programId);
			resultMap.put("dropDownList", dropDownList);
			Map<String, Object> neMappingList = neMappingService.getNeMapping(neMappingModel, page, count);
			neMappingEntity = (List<NeMappingEntity>) neMappingList.get("NeMappingDetails");
			resultMap.put("neConfigTypeList", neMappingList.get("neConfigTypeList"));
			resultMap.put("neMappingDetails", neMappingEntity);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("pageCount", neMappingList.get("PageCount"));
			resultMap.put("status", Constants.SUCCESS);
		}catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_NE_MAPPING_DETAILS));
			logger.error("Exception in getNeMapping()   NeMappingController:" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_NE_MAPPING, method = RequestMethod.POST)
	public JSONObject saveNeMappingDetails(@RequestBody JSONObject updatedNeMapDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String siteName=null;

		try {
			sessionId = updatedNeMapDetails.get("sessionId").toString();
			serviceToken = updatedNeMapDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			NeMappingModel neMappingModel = new Gson().fromJson(JSONObject.toJSONString((Map) updatedNeMapDetails.get("updatedNeMapDetails")), NeMappingModel.class);
			//Hardcoding the value for 5G as its a manadtory parameter in 4G and 5G
			if(neMappingModel.getNetworkConfigEntity().getProgramDetailsEntity().getNetworkTypeDetailsEntity().getNetworkType().equals("5G"))
			{
				neMappingModel.setSiteConfigType("NB-IoT Add");
			}
			NeMappingEntity neMappingEntity = neMappingDto.getNetworkConfigEntity(neMappingModel, sessionId);
			String gnodebId=neMappingEntity.getEnbId();
			String programName=null;
			programName=neMappingModel.getNetworkConfigEntity().getProgramDetailsEntity().getProgramName();
			if(programName.contains("VZN-5G-MM"))
			{
			 List<NeMappingEntity> data =neMappingService.getSiteName(gnodebId);
			 
			  if(data!=null)
			   siteName=data.get(0).getSiteName();
			 
			 List<NeMappingEntity> data1 =neMappingService.getGnodebs(siteName);
			 
			 if (neMappingEntity != null  && data1!=null) {
				 
					for(int i=0;i<data1.size();i++)
					{    
					NeMappingEntity obj=data1.get(i);
					neMappingModel.setEnbId(obj.getEnbId());
					neMappingModel.setId(obj.getId());
					NeMappingEntity neMappingEntity1 = neMappingDto.getNetworkConfigEntity(obj,neMappingModel, sessionId);
					 	if (neMappingService.saveNeMappingDetails(neMappingEntity1)) {
						resultMap.put("status", Constants.SUCCESS);
						resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_DETAILS_UPDATED_SUCCESSFULLY));
						commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_NE_MAPPING,Constants.ACTION_UPDATE, "NE Mapping Details Updated Successfully for NE: "+neMappingModel.getEnbId(), sessionId);
					}
					}
			}else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_NE_MAPPING_DETAILS));
			}
			 
		}else
		{

			if (neMappingEntity != null) {
				if (neMappingService.saveNeMappingDetails(neMappingEntity)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.NE_MAPPING_DETAILS_UPDATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_NE_MAPPING,Constants.ACTION_UPDATE, "NE Mapping Details Updated Successfully for NE: "+neMappingModel.getEnbId(), sessionId);
				}
			}else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_NE_MAPPING_DETAILS));
			}
				
		}
		}catch (Exception e) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_NE_MAPPING_DETAILS));
					logger.error("Exception in saveNeMappingDetails()   NeMappingController:" + ExceptionUtils.getFullStackTrace(e));
			}
		return resultMap;

	}
}
