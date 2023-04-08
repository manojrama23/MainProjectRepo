package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jcraft.jsch.Logger;

@Entity
@Table(name = "OV_SCHEDULED_DETAILS")
public class OvScheduledEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;
	@Column(name = "CIQ_Name")
	private String ciqName;
	@Column(name = "CIQ_Generation_Date")
	private String ciqGenerationDate;
	
	@Column(name = "CIQ_Update_Status", columnDefinition="TEXT")
	private String ciqUpdateJson;
	
	@Column(name = "NE_ID")
	private String neId;
	
	@Column(name = "FETCH_DATE")
	private String fetchDate;
	@Column(name = "PREMIGRATION_SCHEDULED_TIME")
	private String premigrationScheduledTime;
	@Column(name = "MIGRATION_SCHEDULED_TIME")
	private String migrationScheduledTime;
	@Column(name = "NEGROW_SCHEDULED_TIME")
	private String neGrowScheduledTime;
	@Column(name = "POSTMIGRATION_AUDIT_SCHEDULED_TIME")
	private String postmigrationAuditScheduledTime;
	@Column(name = "RANATP_SCHEDULED_TIME")
	private String ranAtpScheduledTime;
	/*@Column(name = "ENV_FILE_SCHEDULED_TIME")
	private String envFileExportScheduledTime;*/
	@Column(name = "PREMIGRATION_RE_SCHEDULED_TIME")
	private String premigrationReScheduledTime;
	@Column(name = "MIGRATION_RE_SCHEDULED_TIME")
	private String migrationReScheduledTime;
	@Column(name = "NEGROW_RE_SCHEDULED_TIME")
	private String neGrowReScheduledTime;
	@Column(name = "POSTMIGRATION_AUDIT_RE_SCHEDULED_TIME")
	private String postmigrationAuditReScheduledTime;
	@Column(name = "RANATP_RE_SCHEDULED_TIME")
	private String ranAtpReScheduledTime;
	/*@Column(name = "ENV_FILE_RE_SCHEDULED_TIME")
	private String envFileExportReScheduledTime;*/
	@Column(name = "TRCKER_ID")
	private String trackerId;
	
	@Column(name = "SITE_NAME")
	private String siteName;
	
	@Column(name = "PREMIG_GROW_STATUS")
	private String preMigGrowStatus;
	
	@Column(name = "PREMIG_GROW_GENERATION_DATE")
	private String preMigGrowGenerationDate;
	
	@Column(name = "PREMIG_GROW_JSON",columnDefinition="TEXT")
	private String preMigGrowJson;
	
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	
	@Column(name = "WORK_PLAN_ID")
	private String workPlanID;
	
	@Column(name = "MIG_START_DATE")
	private String migrationStartDate;
	
	@Column(name = "MIG_COMPLETION_DATE")
	private String migrationCompleteTime;
	
	@Column(name = "POST_MIG_START_DATE")
	private String postmigrationAuditStartDate;
	
	@Column(name = "POST_MIG_COMPLETION_DATE")
	private String postmigrationAuditCompleteTime;
	
	@Column(name = "PRE_MIG_STATUS")
	private String preMigStatus;

	@Column(name = "NEGROW_STATUS")
	private String neGrowStatus;

	@Column(name = "MIG_STATUS")
	private String migStatus;

	@Column(name = "POST_MIG_AUDIT_STATUS")
	private String postMigAuditStatus;
	
	@Column(name = "POST_MIG_RANAtp_STATUS")
	private String postMigRanAtpStatus;
	
	@Column(name = "ENV_EXPORT_STATUS")
	private String envExportStatus;
	
	@Column(name = "ENV_STATUS")
	private String envStatus;
	
	@Column(name = "ENV_GENERATION_DATE")
	private String envGenerationDate;
	
	@Column(name = "ENV_File_Name")
	private String envFileName;
	
	@Column(name = "ENV_File_PATH")
	private String envFilePath;
	
	@Column(name = "GROW_File_Name")
	private String growFileName;
	
	@Column(name = "GROW_File_PATH")
	private String growFilePath;

	@Column(name = "ENV_Upload_Json",columnDefinition="TEXT")
	private String envUploadJson;
	
	@Column(name = "ENV_Status_Json",columnDefinition="TEXT")
	private String envStatusJson;
	
	@Column(name = "FETCH_DET_JSON", columnDefinition="TEXT")
	private String fetchDetailsJson;
	
	@ManyToOne
	@JoinColumn(name = "WFM_RUN_TEST_ID", referencedColumnName = "ID")
	private WorkFlowManagementEntity workFlowManagementEntity;
	
	@Column(name = "CIQ_File_Path")
	private String ciqFilePath;
	
	@Column(name = "FETCH_REMARKS")
	private String fetchRemarks;
	
	//dummy ip
	@Column(name = "MIGRATION_STRATEGY")
	private String integrationType;
	
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}

	public Integer getId() {
		return id;
	}

	public String getEnvUploadJson() {
		return envUploadJson;
	}

	public void setEnvUploadJson(String envUploadJson) {
		this.envUploadJson = envUploadJson;
	}

	public String getEnvStatusJson() {
		return envStatusJson;
	}

	public void setEnvStatusJson(String envStatusJson) {
		this.envStatusJson = envStatusJson;
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

	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
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

	public String getMigrationScheduledTime() {
		return migrationScheduledTime;
	}

	public void setMigrationScheduledTime(String migrationScheduledTime) {
		this.migrationScheduledTime = migrationScheduledTime;
	}

	public String getNeGrowScheduledTime() {
		return neGrowScheduledTime;
	}

	public void setNeGrowScheduledTime(String neGrowScheduledTime) {
		this.neGrowScheduledTime = neGrowScheduledTime;
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

	public WorkFlowManagementEntity getWorkFlowManagementEntity() {
		return workFlowManagementEntity;
	}

	public void setWorkFlowManagementEntity(WorkFlowManagementEntity workFlowManagementEntity) {
		this.workFlowManagementEntity = workFlowManagementEntity;
	}

	/*public String getEnvFileExportScheduledTime() {
		return envFileExportScheduledTime;
	}

	public void setEnvFileExportScheduledTime(String envFileExportScheduledTime) {
		this.envFileExportScheduledTime = envFileExportScheduledTime;
	}

	public String getEnvFileExportReScheduledTime() {
		return envFileExportReScheduledTime;
	}

	public void setEnvFileExportReScheduledTime(String envFileExportReScheduledTime) {
		this.envFileExportReScheduledTime = envFileExportReScheduledTime;
	}*/

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
		return migStatus;
	}

	public void setMigStatus(String migStatus) {
		this.migStatus = migStatus;
	}

	

	public String getPostMigAuditStatus() {
		return postMigAuditStatus;
	}

	public void setPostMigAuditStatus(String postMigAuditStatus) {
		this.postMigAuditStatus = postMigAuditStatus;
	}

	

	public String getPostMigRanAtpStatus() {
		return postMigRanAtpStatus;
	}

	public void setPostMigRanAtpStatus(String postMigRanAtpStatus) {
		this.postMigRanAtpStatus = postMigRanAtpStatus;
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

	public String getFetchRemarks() {
		return fetchRemarks;
	}

	public void setFetchRemarks(String fetchRemarks) {
		this.fetchRemarks = fetchRemarks;
	}

	public void setEnvFilePath(String envFilePath) {
		this.envFilePath = envFilePath;
	}

	public String getCiqFilePath() {
		return ciqFilePath;
	}

	public void setCiqFilePath(String ciqFilePath) {
		this.ciqFilePath = ciqFilePath;
	}

	public String getPreMigGrowStatus() {
		return preMigGrowStatus;
	}

	public void setPreMigGrowStatus(String preMigGrowStatus) {
		this.preMigGrowStatus = preMigGrowStatus;
	}
	public String getPreMigGrowJson() {
		return preMigGrowJson;
	}

	public void setPreMigGrowJson(String preMigGrowJson) {
		this.preMigGrowJson = preMigGrowJson;
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
		System.out.println("envGenerationDate");
	}

	public String getCiqGenerationDate() {
		return ciqGenerationDate;
	}

	public void setCiqGenerationDate(String ciqGenerationDate) {
		this.ciqGenerationDate = ciqGenerationDate;
	}

	public String getCiqUpdateJson() {
		return ciqUpdateJson;
	}

	public void setCiqUpdateJson(String ciqUpdateJson) {
		this.ciqUpdateJson = ciqUpdateJson;
	}

	public String getPreMigGrowGenerationDate() {
		return preMigGrowGenerationDate;
	}

	public void setPreMigGrowGenerationDate(String preMigGrowGenerationDate) {
		this.preMigGrowGenerationDate = preMigGrowGenerationDate;
	}
        public String getFetchDetailsJson() {
		return fetchDetailsJson;
	}

	public void setFetchDetailsJson(String fetchDetailsJson) {
		this.fetchDetailsJson = fetchDetailsJson;
	}
	public String getGrowFileName() {
		return growFileName;
	}

	public void setGrowFileName(String growFileName) {
		this.growFileName = growFileName;
	}

	public String getGrowFilePath() {
		return growFilePath;
	}

	public void setGrowFilePath(String growFilePath) {
		this.growFilePath = growFilePath;
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


}

