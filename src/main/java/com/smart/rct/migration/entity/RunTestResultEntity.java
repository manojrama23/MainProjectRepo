package com.smart.rct.migration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerDetailsEntity;

@Entity
@Table(name = "MIG_RUN_TEST_RESULT")
public class RunTestResultEntity {

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

	@Column(name = "NE_NAME")
	private String neName;

	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;

	@ManyToOne
	@JoinColumn(name = "USE_CASE_ID", referencedColumnName = "USE_CASE_ID", nullable = false)
	private UseCaseBuilderEntity useCaseBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SCRIPT_ID", referencedColumnName = "ID", nullable = false)
	private UploadFileEntity uploadFileEntity;

	@ManyToOne
	@JoinColumn(name = "CMD_ID", referencedColumnName = "ID")
	private CmdRuleBuilderEntity cmdRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "FILE_ID", referencedColumnName = "ID")
	private FileRuleBuilderEntity fileRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "XML_ID", referencedColumnName = "ID")
	private XmlRuleBuilderEntity xmlRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SHELL_ID", referencedColumnName = "ID")
	private ShellCmdRuleBuilderEntity shellCmdRuleBuilderEntity;

	
	@Column(name = "CURRENT_RESULT")
	private String currentResult;

	@Column(name = "PREVIOUS_RESULT")
	private String previousResult;

	@Column(name = "CURRENT_SCRIPT_OUTPUT", columnDefinition = "LONGTEXT")
	private String currentScriptOutput;

	@Column(name = "PREVIOUS_SCRIPT_OUTPUT", columnDefinition = "LONGTEXT")
	private String previousScriptOutput;

	@Column(name = "RULE_RESULT_JOSN", columnDefinition = "LONGTEXT")
	private String ruleResultJson;
	
	@Column(name = "SCRIPT_EXE_SEQ")
	private Integer scriptExeSeq;
	
	
	public Integer getScriptExeSeq() {
		return scriptExeSeq;
	}

	public void setScriptExeSeq(Integer scriptExeSeq) {
		this.scriptExeSeq = scriptExeSeq;
	}

	public ShellCmdRuleBuilderEntity getShellCmdRuleBuilderEntity() {
		return shellCmdRuleBuilderEntity;
	}

	public void setShellCmdRuleBuilderEntity(ShellCmdRuleBuilderEntity shellCmdRuleBuilderEntity) {
		this.shellCmdRuleBuilderEntity = shellCmdRuleBuilderEntity;
	}

	public UseCaseBuilderEntity getUseCaseBuilderEntity() {
		return useCaseBuilderEntity;
	}

	public void setUseCaseBuilderEntity(UseCaseBuilderEntity useCaseBuilderEntity) {
		this.useCaseBuilderEntity = useCaseBuilderEntity;
	}

	public UploadFileEntity getUploadFileEntity() {
		return uploadFileEntity;
	}

	public void setUploadFileEntity(UploadFileEntity uploadFileEntity) {
		this.uploadFileEntity = uploadFileEntity;
	}

	public CmdRuleBuilderEntity getCmdRuleBuilderEntity() {
		return cmdRuleBuilderEntity;
	}

	public void setCmdRuleBuilderEntity(CmdRuleBuilderEntity cmdRuleBuilderEntity) {
		this.cmdRuleBuilderEntity = cmdRuleBuilderEntity;
	}

	public FileRuleBuilderEntity getFileRuleBuilderEntity() {
		return fileRuleBuilderEntity;
	}

	public void setFileRuleBuilderEntity(FileRuleBuilderEntity fileRuleBuilderEntity) {
		this.fileRuleBuilderEntity = fileRuleBuilderEntity;
	}

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity() {
		return xmlRuleBuilderEntity;
	}

	public void setXmlRuleBuilderEntity(XmlRuleBuilderEntity xmlRuleBuilderEntity) {
		this.xmlRuleBuilderEntity = xmlRuleBuilderEntity;
	}

	public String getRuleResultJson() {
		return ruleResultJson;
	}

	public void setRuleResultJson(String ruleResultJson) {
		this.ruleResultJson = ruleResultJson;
	}

	public RunTestEntity getRunTestEntity() {
		return runTestEntity;
	}

	public void setRunTestEntity(RunTestEntity runTestEntity) {
		this.runTestEntity = runTestEntity;
	}

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

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getCurrentResult() {
		return currentResult;
	}

	public void setCurrentResult(String currentResult) {
		this.currentResult = currentResult;
	}

	public String getPreviousResult() {
		return previousResult;
	}

	public void setPreviousResult(String previousResult) {
		this.previousResult = previousResult;
	}

	public String getCurrentScriptOutput() {
		return currentScriptOutput;
	}

	public void setCurrentScriptOutput(String currentScriptOutput) {
		this.currentScriptOutput = currentScriptOutput;
	}

	public String getPreviousScriptOutput() {
		return previousScriptOutput;
	}

	public void setPreviousScriptOutput(String previousScriptOutput) {
		this.previousScriptOutput = previousScriptOutput;
	}

}
