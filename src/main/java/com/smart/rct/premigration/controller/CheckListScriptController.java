package com.smart.rct.premigration.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.dto.CheckListScriptDetDto;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.premigration.service.CheckListScriptService;
import com.smart.rct.util.CommonUtil;

@RestController
public class CheckListScriptController {
	final static Logger logger = LoggerFactory.getLogger(CheckListScriptController.class);
	
	@Autowired
	CheckListScriptService checkListScriptService;
	
	@Autowired
	CheckListScriptDetDto checkListScriptDetDto;
	
	@Autowired
	CommonUtil commonUtil;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_CHECKLIST_SCRIPT_DETAILS, method = RequestMethod.POST)
	public JSONObject saveCheckListBasedScriptExecutionDetails(@RequestBody JSONObject checkListBasedScriptDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer stepIndex=null;
		String checkListFileName=null;
		String sheetName=null;
		String configType=null;
		CustomerDetailsEntity programDetailsEntity = null;
		String programId = null;
		try {
			sessionId = checkListBasedScriptDetails.get("sessionId").toString();
			serviceToken = checkListBasedScriptDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			checkListFileName = checkListBasedScriptDetails.get("checkListFileName").toString();
			sheetName= checkListBasedScriptDetails.get("sheetName").toString();
			if(CommonUtil.isValidObject(checkListBasedScriptDetails.get("configType"))){
				configType=checkListBasedScriptDetails.get("configType").toString();
			}else{
				configType="";
			}
			stepIndex  = Integer.parseInt(checkListBasedScriptDetails.get("stepIndex").toString());
			programId = checkListBasedScriptDetails.get("programId").toString();
			programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(Integer.parseInt(programId));
			List<LinkedHashMap> scriptList = (List<LinkedHashMap>) checkListBasedScriptDetails.get("scriptList");
			List<CheckListScriptDetModel> list = new ArrayList<CheckListScriptDetModel>();
			if (scriptList != null && scriptList.size() > 0) {
				CheckListScriptDetModel checkListScriptDetModel = null;
				for (LinkedHashMap script: scriptList) {
					checkListScriptDetModel= new CheckListScriptDetModel();
					if(script.get("id") !=null && org.apache.commons.lang.StringUtils.isNotEmpty(script.get("id").toString())){
						checkListScriptDetModel.setId(Integer.parseInt(script.get("id").toString()));
					}
					checkListScriptDetModel.setProgramDetailsEntity(programDetailsEntity);
					checkListScriptDetModel.setStepIndex(stepIndex);
					checkListScriptDetModel.setScriptName(script.get("scriptName").toString());
					checkListScriptDetModel.setScriptExeSeq(Integer.parseInt(script.get("scriptExeSeq").toString()));
					checkListScriptDetModel.setCheckListFileName(checkListFileName);
					checkListScriptDetModel.setSheetName(sheetName);
					checkListScriptDetModel.setConfigType(configType);
					list.add(checkListScriptDetModel);
				}
			}
			
			logger.info("CheckListScriptController.saveCheckListBasedScriptExecutionDetails() list size: "+list.size());
			
			if (list != null && list.size() > 0) {
				Set<Integer> idList = list.stream().filter(x->x.getId()!=null).map(x->x.getId()).sorted().collect(Collectors.toSet());
				boolean deleteStatus = checkListScriptService.deleteCheckListBasedScriptExecutionDetails(list.get(0), idList);
				if(deleteStatus){
					logger.info("CheckListScriptController.saveCheckListBasedScriptExecutionDetails() deleted existing ids apart from : "+idList+" for stepIndex:"+stepIndex);
					
				}
				for (CheckListScriptDetModel checkListScriptDetModel: list) {
					boolean isDuplicateScriptNameExist = checkListScriptService.isDuplicateScriptNameExist(checkListScriptDetModel);
					if(isDuplicateScriptNameExist){
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_NAME_ALREADY_EXIST));
						return resultMap;
					}
					
					boolean isDuplicateSeqExist = checkListScriptService.isDuplicateSeqExist(checkListScriptDetModel);
					if(isDuplicateSeqExist){
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.EXE_SEQ_ALREADY_EXIST));
						return resultMap;
					}
					
					checkListScriptService.saveCheckListBasedScriptExecutionDetails(checkListScriptDetDto.getCheckListScriptDetEntity(checkListScriptDetModel, sessionId));
				}
			}else{
				Set<Integer> idList = new HashSet<Integer>();
				CheckListScriptDetModel checkListScriptDetModel = new CheckListScriptDetModel();
				checkListScriptDetModel.setProgramDetailsEntity(programDetailsEntity);
				checkListScriptDetModel.setStepIndex(stepIndex);
				checkListScriptDetModel.setCheckListFileName(checkListFileName);
				checkListScriptDetModel.setSheetName(sheetName);
				boolean deleteStatus = checkListScriptService.deleteCheckListBasedScriptExecutionDetails(checkListScriptDetModel, idList);
				if(deleteStatus){
					logger.info("CheckListScriptController.saveCheckListBasedScriptExecutionDetails() deleted existing ids apart from : "+idList+" for stepIndex:"+stepIndex);
				}
			}
			commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CHECK_LIST,Constants.ACTION_SAVE, "Scripts Execution Sequences Added For: " + checkListFileName, sessionId);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_SEQ_DETAILS_SAVED_SUCCESSFULLY));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SAVE_CHECKLIST_SCRIPT_DETAILS));
			logger.error("Exception in CheckListScriptController.saveCheckListBasedScriptExecutionDetails():" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CHECKLIST_SCRIPT_DETAILS, method = RequestMethod.POST)
	public JSONObject getCheckListBasedScriptExecutionDetails(@RequestBody JSONObject checkListBasedScriptDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer stepIndex=null;
		String checkListFileName=null;
		String programId = null;
		String sheetName = null;
		CustomerDetailsEntity programDetailsEntity = null;
		try {
			sessionId = checkListBasedScriptDetails.get("sessionId").toString();
			serviceToken = checkListBasedScriptDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			checkListFileName = checkListBasedScriptDetails.get("checkListFileName").toString();
			stepIndex  = Integer.parseInt(checkListBasedScriptDetails.get("stepIndex").toString());
			programId = checkListBasedScriptDetails.get("programId").toString();
			sheetName = checkListBasedScriptDetails.get("sheetName").toString();
			CheckListScriptDetModel checkListScriptDetModel= new CheckListScriptDetModel();
			checkListScriptDetModel.setCheckListFileName(checkListFileName);
			checkListScriptDetModel.setStepIndex(stepIndex);
			checkListScriptDetModel.setSheetName(sheetName);
			programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(Integer.parseInt(programId));
			checkListScriptDetModel.setProgramDetailsEntity(programDetailsEntity);
			List<CheckListScriptDetEntity> entityList = checkListScriptService.getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);
			List<CheckListScriptDetModel> list = new ArrayList<CheckListScriptDetModel>();
			if (entityList != null && entityList.size() > 0) {
				for (CheckListScriptDetEntity checkListScriptDetEntity: entityList) {
					list.add(checkListScriptDetDto.getCheckListScriptDetModel(checkListScriptDetEntity));
				}
			}
			logger.info("CheckListScriptController.getCheckListBasedScriptExecutionDetails() list size: "+list.size());
			resultMap.put("scriptList", list);
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CHECKLIST_SCRIPT_DETAILS));
			logger.error("Exception in CheckListScriptController.getCheckListBasedScriptExecutionDetails():" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
}