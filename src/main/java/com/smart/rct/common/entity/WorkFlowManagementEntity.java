package com.smart.rct.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

import com.smart.rct.migration.entity.RunTestEntity;

@Entity
@Table(name = "WORK_FLOW_MANAGE_DETAILS")
public class WorkFlowManagementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;
	
	@Column(name = "NE_NAME")
	private String neName;

	@Column(name = "CIQ_Name")
	private String ciqName;

	@Column(name = "TEST_NAME")
	private String testName;

	@Column(name = "TEST_DESCRIPTION")
	private String testDescription;

	@Column(name = "LSM_NAME")
	private String lsmName;

	@Column(name = "LSM_VERSION")
	private String lsmVersion;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "CUSTOMER_ID")
	private int customerId;

	@Column(name = "PRE_MIG_STATUS")
	private String preMigStatus;

	@Column(name = "NEGROW_STATUS")
	private String neGrowStatus;

	@Column(name = "MIG_STATUS")
	private String MigStatus;

	@Column(name = "POST_MIG_STATUS")
	private String PostMigStatus;
	//new
	@Column(name = "PRE_AUDIT_STATUS")
	private String PreAuditStatus;
	
	@Column(name = "NE_UP_STATUS")
	private String NeUpStatus;

	@Column(name = "INTEGRATION_TYPE")
	private String integrationType;

	@ManyToOne
	@JoinColumn(name = "MIG_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runMigTestEntity;

	@ManyToOne
	@JoinColumn(name = "NEGROW_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runNEGrowEntity;

	@ManyToOne
	@JoinColumn(name = "POST_MIG_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runPostMigTestEntity;
	//new
	@ManyToOne
	@JoinColumn(name = "PRE_AUDIT_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runPreAuditTestEntity;
	
	@ManyToOne
	@JoinColumn(name = "NE_STATUS_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runNEStatusTestEntity;
	@ManyToOne
	@JoinColumn(name = "RAN_ATP_RUN_TEST_ID", referencedColumnName = "ID")
	private RunTestEntity runRanAtpTestEntity;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "WFM_THREAD_NAME")
	private String wfmThreadName;

	@Column(name = "ENBID")
	private String enbId;
	
	@Column(name = "FILEPATH_PRE", columnDefinition = "LONGTEXT")
	private String filePathPre;
	
	@Column(name = "FILENAME_PRE", columnDefinition = "LONGTEXT")
	private String fileNamePre;
	
	@Column(name = "COMMISION_PATH", columnDefinition = "LONGTEXT")
	private String commPath;

	@Column(name = "ENV_PATH", columnDefinition = "LONGTEXT")
	private String envPath;
	
	@Column(name = "CSV_PATH", columnDefinition = "LONGTEXT")
	private String csvPath;
	
	@Column(name = "COMMISION_ZIPNAME", columnDefinition = "LONGTEXT")
	private String commZipName;

	@Column(name = "ENV_ZIPNAME", columnDefinition = "LONGTEXT")
	private String envZipName;
	
	@Column(name = "CSV_ZIPNAME", columnDefinition = "LONGTEXT")
	private String csvZipName;

	
	@Column(name = "PRE_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String preErrorFile;
	
	@Column(name = "NEGROW_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String 	negrowErrorFile;
	
	@Column(name = "MIG_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String migErrorFile;
	
	@Column(name = "POST_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String PostErrorFile;
	//NEW
	@Column(name = "PREAUDIT_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String PREAUDITErrorFile;
	
	@Column(name = "NESTATUS_ERROR_FILE", columnDefinition = "LONGTEXT")
	private String NEStatusErrorFile;
	
	@Column(name = "SITENAME")
	private String siteName;
	
	@Column(name = "SITE_REPORT_STATUS")
	private String siteReportStatus;
	
	@Column(name = "SITE_REPORT_ID")
	private Integer siteReportId;
	
	@Column(name = "OV_SITE_REPORT_STATUS")
	private String ovSiteReportStatus;

	@Column(name = "REMARKS")
	private String remarks;

	
	
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getOvSiteReportStatus() {
		return ovSiteReportStatus;
	}

	public void setOvSiteReportStatus(String ovSiteReportStatus) {
		this.ovSiteReportStatus = ovSiteReportStatus;
	}

	public String getNeUpStatus() {
		return NeUpStatus;
	}

	public void setNeUpStatus(String neUpStatus) {
		NeUpStatus = neUpStatus;
	}

	public RunTestEntity getRunNEStatusTestEntity() {
		return runNEStatusTestEntity;
	}

	public void setRunNEStatusTestEntity(RunTestEntity runNEStatusTestEntity) {
		this.runNEStatusTestEntity = runNEStatusTestEntity;
	}

	public String getNEStatusErrorFile() {
		return NEStatusErrorFile;
	}

	public void setNEStatusErrorFile(String nEStatusErrorFile) {
		NEStatusErrorFile = nEStatusErrorFile;
	}

	public String getPreAuditStatus() {
		return PreAuditStatus;
	}

	public void setPreAuditStatus(String preAuditStatus) {
		PreAuditStatus = preAuditStatus;
	}

	public RunTestEntity getRunPreAuditTestEntity() {
		return runPreAuditTestEntity;
	}

	public void setRunPreAuditTestEntity(RunTestEntity runPreAuditTestEntity) {
		this.runPreAuditTestEntity = runPreAuditTestEntity;
	}

	public String getPREAUDITErrorFile() {
		return PREAUDITErrorFile;
	}

	public void setPREAUDITErrorFile(String pREAUDITErrorFile) {
		PREAUDITErrorFile = pREAUDITErrorFile;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestDescription() {
		return testDescription;
	}

	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}

	public String getLsmName() {
		return lsmName;
	}

	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}

	public String getLsmVersion() {
		return lsmVersion;
	}

	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	public String getPreMigStatus() {
		return preMigStatus;
	}

	public void setPreMigStatus(String preMigStatus) {
		this.preMigStatus = preMigStatus;
	}

	public String getMigStatus() {
		return MigStatus;
	}

	public void setMigStatus(String migStatus) {
		MigStatus = migStatus;
	}

	public String getPostMigStatus() {
		return PostMigStatus;
	}

	public void setPostMigStatus(String postMigStatus) {
		PostMigStatus = postMigStatus;
	}

	public String getNeGrowStatus() {
		return neGrowStatus;
	}

	public void setNeGrowStatus(String neGrowStatus) {
		this.neGrowStatus = neGrowStatus;
	}

	public RunTestEntity getRunNEGrowEntity() {
		return runNEGrowEntity;
	}

	public void setRunNEGrowEntity(RunTestEntity runNEGrowEntity) {
		this.runNEGrowEntity = runNEGrowEntity;
	}

	public RunTestEntity getRunMigTestEntity() {
		return runMigTestEntity;
	}

	public void setRunMigTestEntity(RunTestEntity runMigTestEntity) {
		this.runMigTestEntity = runMigTestEntity;
	}

	public RunTestEntity getRunPostMigTestEntity() {
		return runPostMigTestEntity;
	}

	public void setRunPostMigTestEntity(RunTestEntity runPostMigTestEntity) {
		this.runPostMigTestEntity = runPostMigTestEntity;
	}
	
	public RunTestEntity getRunRanAtpTestEntity() {
		return runRanAtpTestEntity;
	}

	public void setRunRanAtpTestEntity(RunTestEntity runRanAtpTestEntity) {
		this.runRanAtpTestEntity = runRanAtpTestEntity;
	}

	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getWfmThreadName() {
		return wfmThreadName;
	}

	public void setWfmThreadName(String wfmThreadName) {
		this.wfmThreadName = wfmThreadName;
	}
	
	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}

	public String getFilePathPre() {
		return filePathPre;
	}

	public void setFilePathPre(String filePathPre) {
		this.filePathPre = filePathPre;
	}

	public String getFileNamePre() {
		return fileNamePre;
	}

	public void setFileNamePre(String fileNamePre) {
		this.fileNamePre = fileNamePre;
	}

	public String getCommPath() {
		return commPath;
	}

	public void setCommPath(String commPath) {
		this.commPath = commPath;
	}

	public String getEnvPath() {
		return envPath;
	}

	public void setEnvPath(String envPath) {
		this.envPath = envPath;
	}

	public String getCsvPath() {
		return csvPath;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	public String getCommZipName() {
		return commZipName;
	}

	public void setCommZipName(String commZipName) {
		this.commZipName = commZipName;
	}

	public String getEnvZipName() {
		return envZipName;
	}

	public void setEnvZipName(String envZipName) {
		this.envZipName = envZipName;
	}

	public String getCsvZipName() {
		return csvZipName;
	}

	public void setCsvZipName(String csvZipName) {
		this.csvZipName = csvZipName;
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

	public String getNegrowErrorFile() {
		return negrowErrorFile;
	}

	public void setNegrowErrorFile(String negrowErrorFile) {
		this.negrowErrorFile = negrowErrorFile;
	}

	public String getPostErrorFile() {
		return PostErrorFile;
	}

	public void setPostErrorFile(String postErrorFile) {
		PostErrorFile = postErrorFile;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public String getSiteReportStatus() {
		return siteReportStatus;
	}

	public void setSiteReportStatus(String siteReportStatus) {
		this.siteReportStatus = siteReportStatus;
	}

	public Integer getSiteReportId() {
		return siteReportId;
	}

	public void setSiteReportId(Integer siteReportId) {
		this.siteReportId = siteReportId;
	}

}
