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
@Table(name = "MIG_USE_CASE_COMMAND_RULE")
public class UseCaseCmdRuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COMMAND_ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "MIG_CMD_RULE_ID", referencedColumnName = "ID", nullable = false)
	private CmdRuleBuilderEntity cmdRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SCRIPTS_ID", referencedColumnName = "SCRIPTS_ID", nullable = false)
	private UseCaseBuilderParamEntity useCaseBuilderParamEntity;
	
	@Column(name = "EXECUTION_SEQUENCE")
	private Integer commandRuleSequence;
	
	@Column(name = "REMARKS")
	private String cmdRemarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CmdRuleBuilderEntity getCmdRuleBuilderEntity() {
		return cmdRuleBuilderEntity;
	}

	public void setCmdRuleBuilderEntity(CmdRuleBuilderEntity cmdRuleBuilderEntity) {
		this.cmdRuleBuilderEntity = cmdRuleBuilderEntity;
	}

	public UseCaseBuilderParamEntity getUseCaseBuilderParamEntity() {
		return useCaseBuilderParamEntity;
	}

	public void setUseCaseBuilderParamEntity(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		this.useCaseBuilderParamEntity = useCaseBuilderParamEntity;
	}

	public Integer getCommandRuleSequence() {
		return commandRuleSequence;
	}

	public void setCommandRuleSequence(Integer commandRuleSequence) {
		this.commandRuleSequence = commandRuleSequence;
	}

	public String getCmdRemarks() {
		return cmdRemarks;
	}

	public void setCmdRemarks(String cmdRemarks) {
		this.cmdRemarks = cmdRemarks;
	}

}
