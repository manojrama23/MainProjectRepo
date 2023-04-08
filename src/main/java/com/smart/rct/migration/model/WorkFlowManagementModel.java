package com.smart.rct.migration.model;

import javax.persistence.Column;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class WorkFlowManagementModel {

	private CustomerDetailsEntity customerDetailsEntity;
	
	private String enbId;

	private String neName;


	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}
	
	private String integrationType;
	private String progressStatus;

	private String ciqName;

	private String outputFilepath;

	private String testName;

	private String testDescription;

	private String lsmName;

	private String lsmVersion;

	private String useCase;

	private String status;

	private String userName;

	private String creationDate;

	private int customerId;

	private int id;

	private String result;

	private String fromDate;

	private String toDate;

	RunTestModel migrationRunTestModel;
	
	RunTestModel negrowRunTestModel;
	//new
	RunTestModel  preAuditMigrationRunTestModel;
	
	//new
        RunTestModel  neStatusRunTestModel;
	          
	RunTestModel PostMigrationRunTestModel;
	
	RunTestModel ranAtpRunTestModel;

	private String preMigStatus;

	private String neGrowStatus;
   //new
	private String PreAuditStatus;
	//new
	private String NeStatus;

	private String MigStatus;

	private String PostMigStatus;

	private String inputRequired;

	private String preMigrationScriptPath;

	private String preMigrationFileName;
	private String filePathPre;
	private String fileNamePre;
	private String commPath;
	private String envPath;
	private String csvPath;
	private String commZipName;
	private String envZipName;
	private String csvZipName;
	private String preErrorFile;
	private String neGrowErrorFile;
	//new
	private String preAuditErrorFile;
	
	//new
	private String neStatusErrorFile;
	
	private String migErrorFile;
	private String PostErrorFile;
	
	private String siteName;
	
	private String siteReportStatus;
	private Integer siteReportId;
	private String testInfo;
	private String progressCount;
	private String totalScript;
	private String totalRFScript;

	private String ovSiteReportStatus;
	public String getTestInfo() {
		return testInfo;
	}

	public String getOvSiteReportStatus() {
		return ovSiteReportStatus;
	}

	public void setOvSiteReportStatus(String ovSiteReportStatus) {
		this.ovSiteReportStatus = ovSiteReportStatus;
	}


	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
	}
public RunTestModel getNeStatusRunTestModel() {
		return neStatusRunTestModel;
	}

	public void setNeStatusRunTestModel(RunTestModel neStatusRunTestModel) {
		this.neStatusRunTestModel = neStatusRunTestModel;
	}

	public String getNeStatus() {
		return NeStatus;
	}

	public void setNeStatus(String neStatus) {
		NeStatus = neStatus;
	}

	public String getNeStatusErrorFile() {
		return neStatusErrorFile;
	}

	public void setNeStatusErrorFile(String neStatusErrorFile) {
		this.neStatusErrorFile = neStatusErrorFile;
	}

	public RunTestModel getPreAuditMigrationRunTestModel() {
		return preAuditMigrationRunTestModel;
	}

	public void setPreAuditMigrationRunTestModel(RunTestModel preAuditMigrationRunTestModel) {
		this.preAuditMigrationRunTestModel = preAuditMigrationRunTestModel;
	}

	public String getPreAuditStatus() {
		return PreAuditStatus;
	}

	public void setPreAuditStatus(String preAuditStatus) {
		PreAuditStatus = preAuditStatus;
	}

	public String getPreAuditErrorFile() {
		return preAuditErrorFile;
	}

	public void setPreAuditErrorFile(String preAuditErrorFile) {
		this.preAuditErrorFile = preAuditErrorFile;
	}
	
	public String getProgressCount() {
		return progressCount;
	}

	public void setProgressCount(String progressCount) {
		this.progressCount = progressCount;
	}

	public String getTotalScript() {
		return totalScript;
	}

	public void setTotalScript(String totalScript) {
		this.totalScript = totalScript;
	}

	public String getTotalRFScript() {
		return totalRFScript;
	}

	public void setTotalRFScript(String totalRFScript) {
		this.totalRFScript = totalRFScript;
	}
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
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

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public RunTestModel getMigrationRunTestModel() {
		return migrationRunTestModel;
	}

	public void setMigrationRunTestModel(RunTestModel migrationRunTestModel) {
		this.migrationRunTestModel = migrationRunTestModel;
	}

	public RunTestModel getPostMigrationRunTestModel() {
		return PostMigrationRunTestModel;
	}

	public void setPostMigrationRunTestModel(RunTestModel postMigrationRunTestModel) {
		PostMigrationRunTestModel = postMigrationRunTestModel;
	}
	
	public RunTestModel getranAtpRunTestModel() {
		return ranAtpRunTestModel;
	}

	public void setranAtpRunTestModel(RunTestModel ranAtpRunTestModel) {
		this.ranAtpRunTestModel = ranAtpRunTestModel;
	}

	
	public RunTestModel getNegrowRunTestModel() {
		return negrowRunTestModel;
	}

	public void setNegrowRunTestModel(RunTestModel negrowRunTestModel) {
		this.negrowRunTestModel = negrowRunTestModel;
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

	public String getNeGrowStatus() {
		return neGrowStatus;
	}

	public void setNeGrowStatus(String neGrowStatus) {
		this.neGrowStatus = neGrowStatus;
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

	public String getInputRequired() {
		return inputRequired;
	}

	public void setInputRequired(String inputRequired) {
		this.inputRequired = inputRequired;
	}

	public String getPreMigrationScriptPath() {
		return preMigrationScriptPath;
	}

	public void setPreMigrationScriptPath(String preMigrationScriptPath) {
		this.preMigrationScriptPath = preMigrationScriptPath;
	}

	public String getPreMigrationFileName() {
		return preMigrationFileName;
	}

	public void setPreMigrationFileName(String preMigrationFileName) {
		this.preMigrationFileName = preMigrationFileName;
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

	public String getNeGrowErrorFile() {
		return neGrowErrorFile;
	}

	public void setNeGrowErrorFile(String neGrowErrorFile) {
		this.neGrowErrorFile = neGrowErrorFile;
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

	public String getSiteName() {
		return siteName;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteReportStatus() {
		return siteReportStatus;
	}

	public String getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
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
