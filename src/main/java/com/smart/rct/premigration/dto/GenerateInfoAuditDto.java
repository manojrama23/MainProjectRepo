package com.smart.rct.premigration.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.util.CommonUtil;

@Component
public class GenerateInfoAuditDto {
	final static Logger logger = LoggerFactory.getLogger(GenerateInfoAuditDto.class);

	public GenerateInfoAuditModel getcsvAuditDetailsModel(GenerateInfoAuditEntity generateInfoAuditEntity) {
		GenerateInfoAuditModel csvInfoAuditModels = null;
		try {
			if (generateInfoAuditEntity != null){
				csvInfoAuditModels = new GenerateInfoAuditModel();
				csvInfoAuditModels.setId(Integer.valueOf(generateInfoAuditEntity.getId()));
				csvInfoAuditModels.setFileName(generateInfoAuditEntity.getFileName());
				csvInfoAuditModels.setFileType(generateInfoAuditEntity.getFileType());
				csvInfoAuditModels.setFilePath(generateInfoAuditEntity.getFilePath());
				csvInfoAuditModels.setGenerationDate(CommonUtil.dateToString(generateInfoAuditEntity.getGenerationDate(), Constants.YYYY_MM_DD_HH_MM_SS));
				csvInfoAuditModels.setGeneratedBy(generateInfoAuditEntity.getGeneratedBy());
				csvInfoAuditModels.setCiqFileName(generateInfoAuditEntity.getCiqFileName());
				csvInfoAuditModels.setNeName(generateInfoAuditEntity.getNeName());
				csvInfoAuditModels.setProgramDetailsEntity(generateInfoAuditEntity.getProgramDetailsEntity());
				csvInfoAuditModels.setRemarks(generateInfoAuditEntity.getRemarks());
				csvInfoAuditModels.setSiteName(generateInfoAuditEntity.getSiteName());
				csvInfoAuditModels.setOvUpdateStatus(generateInfoAuditEntity.getOvUpdateStatus());
				csvInfoAuditModels.setIntegrationType(generateInfoAuditEntity.getIntegrationType());
				}
		}catch (Exception e) {
			logger.error("Excpetion getciqAuditDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return csvInfoAuditModels;
	}
}
