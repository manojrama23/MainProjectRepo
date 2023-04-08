package com.smart.rct.common.models;

import java.util.List;

public class ValidationTemplateModel {
	
	private String sheetName;
	private String subSheetName;
	private List<ValidationTemplateColumnModel> validationColumns;
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getSubSheetName() {
		return subSheetName;
	}
	public void setSubSheetName(String subSheetName) {
		this.subSheetName = subSheetName;
	}
	public List<ValidationTemplateColumnModel> getValidationColumns() {
		return validationColumns;
	}
	public void setValidationColumns(List<ValidationTemplateColumnModel> validationColumns) {
		this.validationColumns = validationColumns;
	}
	
	
	
	

}
