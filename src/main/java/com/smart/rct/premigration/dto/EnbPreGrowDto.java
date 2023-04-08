package com.smart.rct.premigration.dto;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.util.CommonUtil;

@Component
public class EnbPreGrowDto {
	final static Logger logger = LoggerFactory.getLogger(EnbPreGrowDto.class);
	public EnbPreGrowAuditModel getenbPreGrowAuditModel(EnbPreGrowAuditEntity enbPreGrowEntity) {
		EnbPreGrowAuditModel enbPreGrowAuditModel = null;
		try {
			if (enbPreGrowEntity != null){
				enbPreGrowAuditModel = new EnbPreGrowAuditModel();
				enbPreGrowAuditModel.setId(Integer.valueOf(enbPreGrowEntity.getId()));
				enbPreGrowAuditModel.setCsvFileName(enbPreGrowEntity.getCsvFileName());
				enbPreGrowAuditModel.setGrowPerformedBy(enbPreGrowEntity.getGrowPerformedBy());
				enbPreGrowAuditModel.setGrowingDate(CommonUtil.dateToString(enbPreGrowEntity.getGrowingDate(),Constants.YYYY_MM_DD_HH_MM_SS));
				enbPreGrowAuditModel.setGrowingName(enbPreGrowEntity.getGrowingName());
				enbPreGrowAuditModel.setUseCaseName(enbPreGrowEntity.getUsecaseName());
				enbPreGrowAuditModel.setStatus(enbPreGrowEntity.getStatus());
				enbPreGrowAuditModel.setSmName(enbPreGrowEntity.getSmName());
				enbPreGrowAuditModel.setSmVersion(enbPreGrowEntity.getSmVersion());
				enbPreGrowAuditModel.setProgramDetailsEntity(enbPreGrowEntity.getProgramDetailsEntity());
				enbPreGrowAuditModel.setCiqFileName(enbPreGrowEntity.getCiqFileName());
				enbPreGrowAuditModel.setNeName(enbPreGrowEntity.getNeName());
				enbPreGrowAuditModel.setRemarks(enbPreGrowEntity.getRemarks());
			}
		}catch (Exception e) {
			logger.error("Excpetion getciqAuditDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return enbPreGrowAuditModel;
	}	


}
