package com.smart.rct.migration.model;

public class CmdRuleBuilderModel {

	private Integer id;
	private String ruleName;
	private String cmdName;
	private String operand1ColumnNames;
	private String operand1Values;	
	private String operand2ColumnNames;
	private String operator;
	private String operand2Values;
	private String prompt;
	private String loopType;
	private String status;
	private String remarks;
	private int useCount;
	private String createdBy;
	private String migrationType;
	private String subType;
	private String timeStamp;
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
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
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

}
