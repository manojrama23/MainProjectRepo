package com.smart.rct.premigration.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@Component
public class CiqUploadAuditTrailDetailsDto {
	final static Logger logger = LoggerFactory.getLogger(CiqUploadAuditTrailDetailsDto.class);
	
	public CiqUploadAuditTrailDetEntity getCiqUploadAuditTrailDetEntity(CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel,String sessionId ) {
		CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = null;
		try {
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			ciqUploadAuditTrailDetEntity = new CiqUploadAuditTrailDetEntity();
			if(!sessionId.isEmpty()) {
			ciqUploadAuditTrailDetEntity.setUploadBy(user.getUserName());
			}else
				ciqUploadAuditTrailDetEntity.setUploadBy("");
			ciqUploadAuditTrailDetEntity.setId(ciqUploadAuditTrailDetModel.getId());
			ciqUploadAuditTrailDetEntity.setProgramDetailsEntity(ciqUploadAuditTrailDetModel.getProgramDetailsEntity());
			ciqUploadAuditTrailDetEntity.setCiqFileName(ciqUploadAuditTrailDetModel.getCiqFileName());
			ciqUploadAuditTrailDetEntity.setCiqFilePath(ciqUploadAuditTrailDetModel.getCiqFilePath());
			ciqUploadAuditTrailDetEntity.setScriptFileName(ciqUploadAuditTrailDetModel.getScriptFileName());
			ciqUploadAuditTrailDetEntity.setScriptFilePath(ciqUploadAuditTrailDetModel.getScriptFilePath());
			ciqUploadAuditTrailDetEntity.setChecklistFileName(ciqUploadAuditTrailDetModel.getChecklistFileName());
			ciqUploadAuditTrailDetEntity.setChecklistFilePath(ciqUploadAuditTrailDetModel.getChecklistFilePath());
			ciqUploadAuditTrailDetEntity.setCiqVersion(ciqUploadAuditTrailDetModel.getCiqVersion());
			ciqUploadAuditTrailDetEntity.setFileSourceType(ciqUploadAuditTrailDetModel.getFileSourceType());
			ciqUploadAuditTrailDetEntity.setRemarks(ciqUploadAuditTrailDetModel.getRemarks());
			//ciqUploadAuditTrailDetEntity.setUploadBy(user.getUserName());
			ciqUploadAuditTrailDetEntity.setCreationDate(new Date());
		} catch (Exception e) {
			logger.error("Excpetion CiqUploadAuditTrailDetailsDto.getCiqUploadAuditTrailDetEntity() : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return ciqUploadAuditTrailDetEntity;
	}

	public CiqUploadAuditTrailDetModel getciqAuditDetailsModel(CiqUploadAuditTrailDetEntity ciqTrailEntity) {
		CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = null;
		try {
			if (ciqTrailEntity != null){
				ciqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
				ciqUploadAuditTrailDetModel.setId(Integer.valueOf(ciqTrailEntity.getId()));
				ciqUploadAuditTrailDetModel.setProgramDetailsEntity(ciqTrailEntity.getProgramDetailsEntity());
				ciqUploadAuditTrailDetModel.setCiqFileName(ciqTrailEntity.getCiqFileName());
				ciqUploadAuditTrailDetModel.setCiqFilePath(ciqTrailEntity.getCiqFilePath());
				ciqUploadAuditTrailDetModel.setScriptFileName(ciqTrailEntity.getScriptFileName());
				ciqUploadAuditTrailDetModel.setScriptFilePath(ciqTrailEntity.getScriptFilePath());
				ciqUploadAuditTrailDetModel.setChecklistFileName(ciqTrailEntity.getChecklistFileName());
				ciqUploadAuditTrailDetModel.setChecklistFilePath(ciqTrailEntity.getChecklistFilePath());
				ciqUploadAuditTrailDetModel.setCiqVersion(ciqTrailEntity.getCiqVersion());
				ciqUploadAuditTrailDetModel.setFileSourceType(ciqTrailEntity.getFileSourceType());
				ciqUploadAuditTrailDetModel.setUploadBy(ciqTrailEntity.getUploadBy());
				ciqUploadAuditTrailDetModel.setCreationDate(CommonUtil.dateToString(ciqTrailEntity.getCreationDate(), Constants.YYYY_MM_DD_HH_MM_SS));
				ciqUploadAuditTrailDetModel.setRemarks(ciqTrailEntity.getRemarks());
				ciqUploadAuditTrailDetModel.setFetchInfo(ciqTrailEntity.getFetchInfo());

			}
		}catch (Exception e) {
			logger.error("Excpetion getciqAuditDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return ciqUploadAuditTrailDetModel;
	}
}
