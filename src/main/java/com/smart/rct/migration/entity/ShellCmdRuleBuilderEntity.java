package com.smart.rct.migration.entity;

import java.util.Date;

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
@Table(name = "MIG_SHELL_CMD_RULE_BUILDER")
public class ShellCmdRuleBuilderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "RULE_NAME")
	private String ruleName;

	@Column(name = "CMD_NAME")
	private String cmdName;
	
	@Column(name = "OPERAND1_COLUMN_NAMES")
	private String operand1ColumnNames;

	@Column(name = "OPERAND1_VALUES")
	private String operand1Values;

	@Column(name = "OPERAND2_COLUMN_NAMES")
	private String operand2ColumnNames;

	@Column(name = "OPERATOR")
	private String operator;

	@Column(name = "OPERAND2_VALUES")
	private String operand2Values;

	@Column(name = "PROMPT",nullable = false)
	private String prompt;
	
	@Column(name = "LOOP_TYPE",nullable = false)
	private String loopType;
	
	@Column(name = "STATUS")
	private String status;

	@Column(name = "REMARKS")
	private String remarks;	
	
	@Column(name = "USE_COUNT")
	private int useCount;

	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "MIGRATION_TYPE")
	private String migrationType;
	
	@Column(name = "SUB_TYPE")
	private String subType;
	
	@Column(name = "CREATED_BY")
	private String createdBy;	
	
	@Column(name = "RESULT_TYPE")
	private String resultType;	

	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getOperand1ColumnNames() {
		return operand1ColumnNames;
	}

	public void setOperand1ColumnNames(String operand1ColumnNames) {
		this.operand1ColumnNames = operand1ColumnNames;
	}

	public String getOperand1Values() {
		return operand1Values;
	}

	public void setOperand1Values(String operand1Values) {
		this.operand1Values = operand1Values;
	}

	public String getOperand2ColumnNames() {
		return operand2ColumnNames;
	}

	public void setOperand2ColumnNames(String operand2ColumnNames) {
		this.operand2ColumnNames = operand2ColumnNames;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperand2Values() {
		return operand2Values;
	}

	public void setOperand2Values(String operand2Values) {
		this.operand2Values = operand2Values;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getLoopType() {
		return loopType;
	}

	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}	
	
	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	
}