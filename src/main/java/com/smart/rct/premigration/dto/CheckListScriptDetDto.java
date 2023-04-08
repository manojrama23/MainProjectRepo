package com.smart.rct.premigration.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@Component
public class CheckListScriptDetDto {

	final static Logger logger = LoggerFactory.getLogger(CheckListScriptDetDto.class);
	public CheckListScriptDetEntity getCheckListScriptDetEntity(CheckListScriptDetModel checkListScriptDetModel,String sessionId){
		CheckListScriptDetEntity checkListScriptDetEntity = null;
		try{
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			checkListScriptDetEntity = new CheckListScriptDetEntity();
			checkListScriptDetEntity.setId(checkListScriptDetModel.getId());
			checkListScriptDetEntity.setProgramDetailsEntity(checkListScriptDetModel.getProgramDetailsEntity());
			checkListScriptDetEntity.setCheckListFileName(checkListScriptDetModel.getCheckListFileName());
			checkListScriptDetEntity.setSheetName(checkListScriptDetModel.getSheetName());
			checkListScriptDetEntity.setConfigType(checkListScriptDetModel.getConfigType());
			checkListScriptDetEntity.setStepIndex(checkListScriptDetModel.getStepIndex());
			checkListScriptDetEntity.setScriptName(checkListScriptDetModel.getScriptName());
			checkListScriptDetEntity.setScriptExeSeq(checkListScriptDetModel.getScriptExeSeq());
			checkListScriptDetEntity.setCreatedBy(user.getUserName());
			checkListScriptDetEntity.setCreationDate(new Date());
		} catch (Exception e) {
			logger.error("Excpetion CheckListScriptDetDto.getCheckListScriptDetEntity() : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return checkListScriptDetEntity;
	}
	
	
	public CheckListScriptDetModel getCheckListScriptDetModel(CheckListScriptDetEntity checkListScriptDetEntity){
		CheckListScriptDetModel checkListScriptDetModel = null;
		try{
			checkListScriptDetModel = new CheckListScriptDetModel();
			checkListScriptDetModel.setId(checkListScriptDetEntity.getId());
			checkListScriptDetModel.setProgramDetailsEntity(checkListScriptDetEntity.getProgramDetailsEntity());
			checkListScriptDetModel.setCheckListFileName(checkListScriptDetEntity.getCheckListFileName());
			checkListScriptDetModel.setSheetName(checkListScriptDetEntity.getSheetName());
			checkListScriptDetModel.setConfigType(checkListScriptDetEntity.getConfigType());
			checkListScriptDetModel.setStepIndex(checkListScriptDetEntity.getStepIndex());
			checkListScriptDetModel.setScriptName(checkListScriptDetEntity.getScriptName());
			checkListScriptDetModel.setScriptExeSeq(checkListScriptDetEntity.getScriptExeSeq());
			checkListScriptDetModel.setCreatedBy(checkListScriptDetEntity.getCreatedBy());
			checkListScriptDetModel.setCreationDate(CommonUtil.dateToString(checkListScriptDetEntity.getCreationDate(),Constants.YYYY_MM_DD_HH_MM_SS));
		} catch (Exception e) {
			logger.error("Excpetion CheckListScriptDetDto.getCheckListScriptDetModel() : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return checkListScriptDetModel;
	}
}
