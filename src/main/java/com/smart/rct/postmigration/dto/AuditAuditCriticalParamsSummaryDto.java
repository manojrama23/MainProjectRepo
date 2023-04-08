package com.smart.rct.postmigration.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditCriticalParamsSummaryModel;

@Component
public class AuditAuditCriticalParamsSummaryDto {
	final static Logger logger = LoggerFactory.getLogger(AuditAuditCriticalParamsSummaryDto.class);
	
	public List<AuditCriticalParamsSummaryModel> getAuditCriticalParamsSummaryReportModelList(List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityList){
		List<AuditCriticalParamsSummaryModel> auditCriticalParamsSummaryModelList = new ArrayList<>();
		try {
			for(AuditCriticalParamsSummaryEntity auditCriticalSummaryEntity : auditCriticalParamsSummaryEntityList) {
				
				AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel = new AuditCriticalParamsSummaryModel();
				auditCriticalParamsSummaryModel.setNeName(auditCriticalSummaryEntity.getNeName());
				auditCriticalParamsSummaryModel.setNeId(auditCriticalSummaryEntity.getNeId());
				auditCriticalParamsSummaryModel.setProgramName(auditCriticalSummaryEntity.getProgramName());
				auditCriticalParamsSummaryModel.setSiteName(auditCriticalSummaryEntity.getSiteName());
				auditCriticalParamsSummaryModel.setUserName(auditCriticalSummaryEntity.getUserName());
				auditCriticalParamsSummaryModel.setStatus(auditCriticalSummaryEntity.getStatus());
				auditCriticalParamsSummaryModel.setRunTestId(auditCriticalSummaryEntity.getRunTestEntity().getId());
				auditCriticalParamsSummaryModel.setTimeStamp(auditCriticalSummaryEntity.getRunTestEntity().getCreationDate().toString());
				auditCriticalParamsSummaryModelList.add(auditCriticalParamsSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception getAuditCriticalParamsSummaryReportModelList in AuditAuditCriticalParamsSummaryDto() " + ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryModelList;
	}
}
