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
@Table(name = "MIG_USE_CASE_SHELL_RULE")
public class UseCaseShellRuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SHELL_ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "MIG_SHELL_RULE_ID", referencedColumnName = "ID", nullable = false)
	private ShellCmdRuleBuilderEntity shellRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SCRIPTS_ID", referencedColumnName = "SCRIPTS_ID", nullable = false)
	private UseCaseBuilderParamEntity useCaseBuilderParamEntity;
	
	@Column(name = "EXECUTION_SEQUENCE")
	private Integer shellRuleSequence;
	
	@Column(name = "REMARKS")
	private String shellRemarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ShellCmdRuleBuilderEntity getShellRuleBuilderEntity() {
		return shellRuleBuilderEntity;
	}

	public void setShellRuleBuilderEntity(ShellCmdRuleBuilderEntity shellRuleBuilderEntity) {
		this.shellRuleBuilderEntity = shellRuleBuilderEntity;
	}

	public UseCaseBuilderParamEntity getUseCaseBuilderParamEntity() {
		return useCaseBuilderParamEntity;
	}

	public void setUseCaseBuilderParamEntity(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		this.useCaseBuilderParamEntity = useCaseBuilderParamEntity;
	}

	public Integer getShellRuleSequence() {
		return shellRuleSequence;
	}

	public void setShellRuleSequence(Integer shellRuleSequence) {
		this.shellRuleSequence = shellRuleSequence;
	}

	public String getShellRemarks() {
		return shellRemarks;
	}

	public void setShellRemarks(String shellRemarks) {
		this.shellRemarks = shellRemarks;
	}
	
	

	}
