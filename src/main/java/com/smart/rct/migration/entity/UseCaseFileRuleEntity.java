package com.smart.rct.migration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MIG_USE_CASE_FILE_RULE")
public class UseCaseFileRuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FILE_ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "MIG_FILE_RULE_ID", referencedColumnName = "ID", nullable = false)
	private FileRuleBuilderEntity fileRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SCRIPTS_ID", referencedColumnName = "SCRIPTS_ID", nullable = false)
	private UseCaseBuilderParamEntity useCaseBuilderScriptsEntity;
	
	@Column(name = "EXECUTION_SEQUENCE")
	private Integer fileRuleSequence;
	
	@Column(name = "REMARKS")
	private String fileRemarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FileRuleBuilderEntity getFileRuleBuilderEntity() {
		return fileRuleBuilderEntity;
	}

	public void setFileRuleBuilderEntity(FileRuleBuilderEntity fileRuleBuilderEntity) {
		this.fileRuleBuilderEntity = fileRuleBuilderEntity;
	}

	public UseCaseBuilderParamEntity getUseCaseBuilderScriptsEntity() {
		return useCaseBuilderScriptsEntity;
	}

	public void setUseCaseBuilderScriptsEntity(UseCaseBuilderParamEntity useCaseBuilderScriptsEntity) {
		this.useCaseBuilderScriptsEntity = useCaseBuilderScriptsEntity;
	}

	public Integer getFileRuleSequence() {
		return fileRuleSequence;
	}

	public void setFileRuleSequence(Integer fileRuleSequence) {
		this.fileRuleSequence = fileRuleSequence;
	}

	public String getFileRemarks() {
		return fileRemarks;
	}

	public void setFileRemarks(String fileRemarks) {
		this.fileRemarks = fileRemarks;
	}
}
