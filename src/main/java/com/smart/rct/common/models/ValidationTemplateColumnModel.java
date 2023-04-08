package com.smart.rct.common.models;

import java.util.List;

public class ValidationTemplateColumnModel {
	
	private String columnName;
	private String columnValue;
	private String operator;
	private String dependToOtherColumn;
	private String dataType;
	private String regexPattern;
	private String minLen;
	private String maxLen;
	private String mandatory;
	
	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRegexPattern() {
		return regexPattern;
	}

	public void setRegexPattern(String regexPattern) {
		this.regexPattern = regexPattern;
	}

	public String getMinLen() {
		return minLen;
	}

	public void setMinLen(String minLen) {
		this.minLen = minLen;
	}

	public String getMaxLen() {
		return maxLen;
	}

	public void setMaxLen(String maxLen) {
		this.maxLen = maxLen;
	}

	private List<ValidationDependColumnModel> dependsColumnsList;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDependToOtherColumn() {
		return dependToOtherColumn;
	}

	public void setDependToOtherColumn(String dependToOtherColumn) {
		this.dependToOtherColumn = dependToOtherColumn;
	}

	public List<ValidationDependColumnModel> getDependsColumnsList() {
		return dependsColumnsList;
	}

	public void setDependsColumnsList(List<ValidationDependColumnModel> dependsColumnsList) {
		this.dependsColumnsList = dependsColumnsList;
	}
	

}
