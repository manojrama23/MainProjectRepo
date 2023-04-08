package com.smart.rct.common.models;

public class CheckListTemplateColumnsModel {
	private String columnName;
	private String columnHeaderName;
	private String validationRule;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnHeaderName() {
		return columnHeaderName;
	}

	public void setColumnHeaderName(String columnHeaderName) {
		this.columnHeaderName = columnHeaderName;
	}

	public String getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(String validationRule) {
		this.validationRule = validationRule;
	}
}
