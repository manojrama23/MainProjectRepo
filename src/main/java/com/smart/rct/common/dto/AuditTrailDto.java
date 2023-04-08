package com.smart.rct.common.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.util.CommonUtil;

@Component
public class AuditTrailDto {

	final static Logger logger = LoggerFactory.getLogger(AuditTrailDto.class);

	@Autowired
	FileUploadRepository fileUploadRepository;

	/**
	 * This method will set the AuditTrailEntity with values from auditTrailModel.
	 * 
	 * @param auditTrailModel
	 * @return auditTrailEntity
	 */
	public AuditTrailEntity getAuditTrailDetailsEntity(AuditTrailModel auditTrailModel) {

		AuditTrailEntity auditTrailEntity = null;

		try {
			if (auditTrailModel != null) {
				auditTrailEntity = new AuditTrailEntity();
				if (auditTrailModel.getId() != null && auditTrailModel.getId() != 0) {
					auditTrailEntity.setId(Integer.valueOf(auditTrailModel.getId()));
				}
				auditTrailEntity.setEventName(auditTrailModel.getEventName());
				auditTrailEntity.setEventSubName(auditTrailModel.getEventSubName());
				auditTrailEntity.setActionPerformed(auditTrailModel.getAction());
				auditTrailEntity.setUserName(auditTrailModel.getUserName());
				auditTrailEntity.setEventDescription(auditTrailModel.getEventDescription());
				auditTrailEntity.setActionPerformedDate(new Date());
			}
		} catch (Exception e) {
			logger.error("Excpetion in AuditTrailDto.getAuditTrailDetailsEntity(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return auditTrailEntity;
	}

	/**
	 * This method will set the AuditTrailModel with values from auditTrailEntity.
	 * 
	 * @param auditTrailEntity
	 * @return auditTrailModel
	 */
	public AuditTrailModel getAuditTrailDetailsModel(AuditTrailEntity auditTrailEntity) {

		AuditTrailModel auditTrailModel = null;
		try {
			if (auditTrailEntity != null) {
				auditTrailModel = new AuditTrailModel();
				auditTrailModel.setId(Integer.valueOf(auditTrailEntity.getId()));
				auditTrailModel.setEventName(auditTrailEntity.getEventName());
				auditTrailModel.setEventSubName(auditTrailEntity.getEventSubName());
				auditTrailModel.setActionPerformed(auditTrailEntity.getActionPerformed());
				auditTrailModel.setUserName(auditTrailEntity.getUserName());
				auditTrailModel.setEventDescription(auditTrailEntity.getEventDescription());
				auditTrailModel.setDateTime(CommonUtil.dateToString(auditTrailEntity.getActionPerformedDate(),
						Constants.YYYY_MM_DD_HH_MM_SS));
			}
		} catch (Exception e) {
			logger.error("Excpetion in AuditTrailDto.getAuditTrailDetailsModel(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return auditTrailModel;
	}

}
