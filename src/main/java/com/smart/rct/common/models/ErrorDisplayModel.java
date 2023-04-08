package com.smart.rct.common.models;

public class ErrorDisplayModel {
	private Integer rowId;
	private String sheetName;
	private String subSheetName;
	private String propertyName;
	private String errorMessage;
	private String cellId;
	private String enbName;
	
	

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

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

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getEnbName() {
		return enbName;
	}

	public void setEnbName(String enbName) {
		this.enbName = enbName;
	}
	
	

}
