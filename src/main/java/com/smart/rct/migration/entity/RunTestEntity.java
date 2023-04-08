package com.smart.rct.migration.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.smart.rct.common.entity.CustomerDetailsEntity;

@Entity
@Table(name = "MIG_RUN_TEST")
public class RunTestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;
	
	@Column(name = "MIGRATION_TYPE")
	private String migrationType;
	
	@Column(name = "MIGRATION_SUB_TYPE")
	private String migrationSubType;

	@Column(name = "CHECKLST_FILENAME")
	private String checklistFileName;
	
	@Column(name = "NE_NAME")
	private String neName;
	
	@Column(name = "PROGRESS_STATUS")
	private String progressStatus;
	
	@Column(name = "CIQ_Name")
	private String ciqName;
	
	
	@Column(name = "OUTPUT_FILEPATH")
	private String outputFilepath;
	
	
	
	//bhuvana
	
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "runTestEntity", cascade = CascadeType.ALL)
	private Set<RunTestResultEntity> runTestResultEntity;

	public String getOutputFilepath() {
		return outputFilepath;
	}

	public void setOutputFilepath(String outputFilepath) {
		this.outputFilepath = outputFilepath;
	}

	@Column(name = "TEST_NAME")
	private String testName;

	@Column(name = "TEST_DESCRIPTION")
	private String testDescription;

	@Column(name = "TEST_INFO")
	private String testInfo;
	
	@Column(name = "LSM_NAME")
	private String lsmName;

	@Column(name = "LSM_VERSION")
	private String lsmVersion;

	@Column(name = "USE_CASE_NAMES",columnDefinition="LONG_TEXT")
	private String useCase;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "USE_CASE_DETAILS", columnDefinition = "TEXT")
	private String useCaseDetails;

	@Column(name = "CUSTOMER_ID")
	private int customerId;

	@Column(name = "USE_CASE_SEQUENCE")
	private int useCaseSequence;
	
	@Column(name = "RESULT")
	private String result;
	
	@Column(name = "RESULT_FILEPATH")
	private String resultFilePath;
	
	@Column(name = "GENERATE_SCRIPTPATH", columnDefinition = "LONGTEXT")
	private String generateScriptPath;
	
	@Column(name = "FAILED_SCRIPT")
	private String failedScript;
	
	@Column(name = "RUN_THREAD_NAME")
	private String runtestThreadName;
	
	@Column(name = "MigStatusDesc")
	private String migStatusDesc;
	
	@Column(name = "OVUPDATE_STATUS")
	private String ovUpdateStatus;
	
	@Column(name = "OVUPDATE_REASON")
	private String ovUpdateReason;
	
	@Column(name = "TOTAL_SCRIPT")
	private String totalScript;
	
	@Column(name = "PROGRESS_SCRIPT")
	private String progressScript;
	
	@Column(name = "TOTAL_RFSCRIPT")
	private String totalRFScript;
	public String getTotalScript() {
		return totalScript;
	}

	public void setTotalScript(String totalScript) {
		this.totalScript = totalScript;
	}

	public String getProgressScript() {
		return progressScript;
	}

	public String getTestInfo() {
		return testInfo;
	}

	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
	}

	public void setProgressScript(String progressScript) {
		this.progressScript = progressScript;
	}

	public String getTotalRFScript() {
		return totalRFScript;
	}

	public void setTotalRFScript(String totalRFScript) {
		this.totalRFScript = totalRFScript;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "runTestEntity", cascade = CascadeType.ALL)
	private Set<OvTestResultEntity> ovTestResultEntity;
	
	public Set<OvTestResultEntity> getOvTestResultEntity() {
		return ovTestResultEntity;
	}

	public void setOvTestResultEntity(Set<OvTestResultEntity> ovTestResultEntity) {
		this.ovTestResultEntity = ovTestResultEntity;
	}

	public String getOvUpdateReason() {
		return ovUpdateReason;
	}

	public void setOvUpdateReason(String ovUpdateReason) {
		this.ovUpdateReason = ovUpdateReason;
	}

	public String getOvUpdateStatus() {
		return ovUpdateStatus;
	}

	public void setOvUpdateStatus(String ovUpdateStatus) {
		this.ovUpdateStatus = ovUpdateStatus;
	}

	public String getMigStatusDesc() {
		return migStatusDesc;
	}

	public void setMigStatusDesc(String migStatusDesc) {
		this.migStatusDesc = migStatusDesc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	
	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
	}

	public String getMigrationSubType() {
		return migrationSubType;
	}

	public void setMigrationSubType(String migrationSubType) {
		this.migrationSubType = migrationSubType;
	}

	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getUseCaseDetails() {
		return useCaseDetails;
	}

	public void setUseCaseDetails(String useCaseDetails) {
		this.useCaseDetails = useCaseDetails;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getUseCaseSequence() {
		return useCaseSequence;
	}

	public void setUseCaseSequence(int useCaseSequence) {
		this.useCaseSequence = useCaseSequence;
	}

	public String getChecklistFileName() {
		return checklistFileName;
	}

	public void setChecklistFileName(String checklistFileName) {
		this.checklistFileName = checklistFileName;
	}

	public Set<RunTestResultEntity> getRunTestResultEntity() {
		return runTestResultEntity;
	}

	public void setRunTestResultEntity(Set<RunTestResultEntity> runTestResultEntity) {
		this.runTestResultEntity = runTestResultEntity;
	}

	public String getResultFilePath() {
		return resultFilePath;
	}

	public void setResultFilePath(String resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

	public String getGenerateScriptPath() {
		return generateScriptPath;
	}

	public void setGenerateScriptPath(String generateScriptPath) {
		this.generateScriptPath = generateScriptPath;
	}

	public String getFailedScript() {
		return failedScript;
	}

	public void setFailedScript(String failedScript) {
		this.failedScript = failedScript;
	}

	public String getruntestThreadName() {
		return runtestThreadName;
	}
	
	public void setruntestThreadName(String runtestThreadName) {
		this.runtestThreadName = runtestThreadName;
	}
}
