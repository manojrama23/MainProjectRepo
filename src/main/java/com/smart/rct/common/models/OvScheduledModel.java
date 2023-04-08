package com.smart.rct.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;

public class OvScheduledModel {

	
	private Integer id;
	private CustomerDetailsEntity customerDetailsEntity;
	private String neId;
	private String ciqName;
	private String ciqUpdateJson;
	private String ciqGenerationDate;
	private String fetchDate;
	private String premigrationScheduledTime;
	private String neGrowScheduledTime;
	private String migrationScheduledTime;
	private String postmigrationAuditScheduledTime;
	private String ranAtpScheduledTime;
	private String envFileExportScheduledTime;
	private String premigrationReScheduledTime;
	
	private String migrationReScheduledTime;
	private String neGrowReScheduledTime;
	private String postmigrationAuditReScheduledTime;
	private String ranAtpReScheduledTime;
	private String envFileExportReScheduledTime;
	private String migrationStartDate;
	private String migrationCompleteTime;
	private String postmigrationAuditStartDate;
	
	//dummy IP
	private String integrationType;
	
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}

	
	public String getMigrationStartDate() {
		return migrationStartDate;
	}
	public void setMigrationStartDate(String migrationStartDate) {
		this.migrationStartDate = migrationStartDate;
	}
	public String getMigrationCompleteTime() {
		return migrationCompleteTime;
	}
	public void setMigrationCompleteTime(String migrationCompleteTime) {
		this.migrationCompleteTime = migrationCompleteTime;
	}
	public String getPostmigrationAuditStartDate() {
		return postmigrationAuditStartDate;
	}
	public void setPostmigrationAuditStartDate(String postmigrationAuditStartDate) {
		this.postmigrationAuditStartDate = postmigrationAuditStartDate;
	}
	public String getPostmigrationAuditCompleteTime() {
		return postmigrationAuditCompleteTime;
	}
	public void setPostmigrationAuditCompleteTime(String postmigrationAuditCompleteTime) {
		this.postmigrationAuditCompleteTime = postmigrationAuditCompleteTime;
	}
	private String postmigrationAuditCompleteTime;
	private String trackerId;
	private String orderNumber;
	private String workPlanID;
	private String preMigStatus;
	private String neGrowStatus;
	private String MigStatus;
	private String siteName;

	private String PostMigAuditStatus;
	private String PostMigRanAtPStatus;
	private String envExportStatus;
	private String envStatus;
	private String envGenerationDate;
	private String envFileName;
	private String envFilePath;
	private String envStatusJson;
	private String envUploadJson;
	private String ciqFilePath;
	private String preMigGrowJson;
	private String preMigGrowStatus;
	private String preMigGrowGenerationDate;
	@JsonIgnore
	private WorkFlowManagementEntity workFlowManagementEntity;
	private String fetchRemarks;
	private String fetchDetailsJson;
	private String preErrorFile;
	private String negrowErrorFile;
	private String migErrorFile;
	private String PostErrorFile;
	private String wfmid;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}
	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getCiqName() {
		return ciqName;
	}
	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}
	
	public String getFetchDate() {
		return fetchDate;
	}
	public void setFetchDate(String fetchDate) {
		this.fetchDate = fetchDate;
	}
	public String getPremigrationScheduledTime() {
		return premigrationScheduledTime;
	}
	public void setPremigrationScheduledTime(String premigrationScheduledTime) {
		this.premigrationScheduledTime = premigrationScheduledTime;
	}
	public String getNeGrowScheduledTime() {
		return neGrowScheduledTime;
	}
	public void setNeGrowScheduledTime(String neGrowScheduledTime) {
		this.neGrowScheduledTime = neGrowScheduledTime;
	}
	public String getMigrationScheduledTime() {
		return migrationScheduledTime;
	}
	public void setMigrationScheduledTime(String migrationScheduledTime) {
		this.migrationScheduledTime = migrationScheduledTime;
	}
	public String getPostmigrationAuditScheduledTime() {
		return postmigrationAuditScheduledTime;
	}
	public void setPostmigrationAuditScheduledTime(String postmigrationAuditScheduledTime) {
		this.postmigrationAuditScheduledTime = postmigrationAuditScheduledTime;
	}
	public String getRanAtpScheduledTime() {
		return ranAtpScheduledTime;
	}
	public void setRanAtpScheduledTime(String ranAtpScheduledTime) {
		this.ranAtpScheduledTime = ranAtpScheduledTime;
	}
	public String getEnvFileExportScheduledTime() {
		return envFileExportScheduledTime;
	}
	public void setEnvFileExportScheduledTime(String envFileExportScheduledTime) {
		this.envFileExportScheduledTime = envFileExportScheduledTime;
	}
	public String getPremigrationReScheduledTime() {
		return premigrationReScheduledTime;
	}
	public void setPremigrationReScheduledTime(String premigrationReScheduledTime) {
		this.premigrationReScheduledTime = premigrationReScheduledTime;
	}
	public String getMigrationReScheduledTime() {
		return migrationReScheduledTime;
	}
	public void setMigrationReScheduledTime(String migrationReScheduledTime) {
		this.migrationReScheduledTime = migrationReScheduledTime;
	}
	public String getNeGrowReScheduledTime() {
		return neGrowReScheduledTime;
	}
	public void setNeGrowReScheduledTime(String neGrowReScheduledTime) {
		this.neGrowReScheduledTime = neGrowReScheduledTime;
	}
	public String getPostmigrationAuditReScheduledTime() {
		return postmigrationAuditReScheduledTime;
	}
	public void setPostmigrationAuditReScheduledTime(String postmigrationAuditReScheduledTime) {
		this.postmigrationAuditReScheduledTime = postmigrationAuditReScheduledTime;
	}
	public String getRanAtpReScheduledTime() {
		return ranAtpReScheduledTime;
	}
	public void setRanAtpReScheduledTime(String ranAtpReScheduledTime) {
		this.ranAtpReScheduledTime = ranAtpReScheduledTime;
	}
	public String getEnvFileExportReScheduledTime() {
		return envFileExportReScheduledTime;
	}
	public void setEnvFileExportReScheduledTime(String envFileExportReScheduledTime) {
		this.envFileExportReScheduledTime = envFileExportReScheduledTime;
	}
	public String getTrackerId() {
		return trackerId;
	}
	public void setTrackerId(String trackerId) {
		this.trackerId = trackerId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getWorkPlanID() {
		return workPlanID;
	}
	public void setWorkPlanID(String workPlanID) {
		this.workPlanID = workPlanID;
	}
	public String getPreMigStatus() {
		return preMigStatus;
	}
	public void setPreMigStatus(String preMigStatus) {
		this.preMigStatus = preMigStatus;
	}
	public String getNeGrowStatus() {
		return neGrowStatus;
	}
	public void setNeGrowStatus(String neGrowStatus) {
		this.neGrowStatus = neGrowStatus;
	}
	public String getMigStatus() {
		return MigStatus;
	}
	public void setMigStatus(String migStatus) {
		MigStatus = migStatus;
	}
	public String getPostMigAuditStatus() {
		return PostMigAuditStatus;
	}
	public void setPostMigAuditStatus(String postMigAuditStatus) {
		PostMigAuditStatus = postMigAuditStatus;
	}
	public String getPostMigRanAtPStatus() {
		return PostMigRanAtPStatus;
	}
	public void setPostMigRanAtPStatus(String postMigRanAtPStatus) {
		PostMigRanAtPStatus = postMigRanAtPStatus;
	}
	public String getEnvExportStatus() {
		return envExportStatus;
	}
	public void setEnvExportStatus(String envExportStatus) {
		this.envExportStatus = envExportStatus;
	}
	public String getEnvFileName() {
		return envFileName;
	}
	public void setEnvFileName(String envFileName) {
		this.envFileName = envFileName;
	}
	public String getEnvFilePath() {
		return envFilePath;
	}
	public void setEnvFilePath(String envFilePath) {
		this.envFilePath = envFilePath;
	}
	public WorkFlowManagementEntity getWorkFlowManagementEntity() {
		return workFlowManagementEntity;
	}
	public void setWorkFlowManagementEntity(WorkFlowManagementEntity workFlowManagementEntity) {
		this.workFlowManagementEntity = workFlowManagementEntity;
	}
	public String getEnvStatusJson() {
		return envStatusJson;
	}
	public void setEnvStatusJson(String envStatusJson) {
		this.envStatusJson = envStatusJson;
	}
	public String getEnvUploadJson() {
		return envUploadJson;
	}
	public void setEnvUploadJson(String envUploadJson) {
		this.envUploadJson = envUploadJson;
	}
	public String getCiqFilePath() {
		return ciqFilePath;
	}
	public void setCiqFilePath(String ciqFilePath) {
		this.ciqFilePath = ciqFilePath;
	}
	public String getPreMigGrowJson() {
		return preMigGrowJson;
	}
	public void setPreMigGrowJson(String preMigGrowJson) {
		this.preMigGrowJson = preMigGrowJson;
	}
	public String getPreMigGrowStatus() {
		return preMigGrowStatus;
	}
	public void setPreMigGrowStatus(String preMigGrowStatus) {
		this.preMigGrowStatus = preMigGrowStatus;
	}
	
	public String getFetchRemarks() {
		return fetchRemarks;
	}
	public void setFetchRemarks(String fetchRemarks) {
		this.fetchRemarks = fetchRemarks;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getEnvStatus() {
		return envStatus;
	}
	public void setEnvStatus(String envStatus) {
		this.envStatus = envStatus;
	}
	public String getEnvGenerationDate() {
		return envGenerationDate;
	}
	public void setEnvGenerationDate(String envGenerationDate) {
		this.envGenerationDate = envGenerationDate;
	}
	public String getPreMigGrowGenerationDate() {
		return preMigGrowGenerationDate;
	}
	public void setPreMigGrowGenerationDate(String preMigGrowGenerationDate) {
		this.preMigGrowGenerationDate = preMigGrowGenerationDate;
	}
	public String getCiqUpdateJson() {
		return ciqUpdateJson;
	}
	public void setCiqUpdateJson(String ciqUpdateJson) {
		this.ciqUpdateJson = ciqUpdateJson;
	}
	public String getCiqGenerationDate() {
		return ciqGenerationDate;
	}
	public void setCiqGenerationDate(String ciqGenerationDate) {
		this.ciqGenerationDate = ciqGenerationDate;
	}
	public String getFetchDetailsJson() {
		return fetchDetailsJson;
	}
	public void setFetchDetailsJson(String fetchDetailsJson) {
		this.fetchDetailsJson = fetchDetailsJson;
	}
	public String getNegrowErrorFile() {
		return negrowErrorFile;
	}
	public void setNegrowErrorFile(String negrowErrorFile) {
		this.negrowErrorFile = negrowErrorFile;
	}
	public String getPreErrorFile() {
		return preErrorFile;
	}
	public void setPreErrorFile(String preErrorFile) {
		this.preErrorFile = preErrorFile;
	}
	public String getMigErrorFile() {
		return migErrorFile;
	}
	public void setMigErrorFile(String migErrorFile) {
		this.migErrorFile = migErrorFile;
	}
	public String getPostErrorFile() {
		return PostErrorFile;
	}
	public void setPostErrorFile(String postErrorFile) {
		PostErrorFile = postErrorFile;
	}
	public String getWfmid() {
		return wfmid;
	}
	public void setWfmid(String wfmid) {
		this.wfmid = wfmid;
	}
	
	
	
}
