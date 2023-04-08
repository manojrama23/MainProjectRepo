package com.smart.rct.common.models;

import java.util.List;

public class CheckListTemplateModel {

	private String sheetName;
	private String configType;
	private String seqOrder;
	private String sheetType;
	private String headerRow;
	private String readingRange;
	private String subSheetName;
	
	List<CheckListTemplateColumnsModel> columns;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getSeqOrder() {
		return seqOrder;
	}

	public void setSeqOrder(String seqOrder) {
		this.seqOrder = seqOrder;
	}

	public String getSheetType() {
		return sheetType;
	}

	public void setSheetType(String sheetType) {
		this.sheetType = sheetType;
	}

	public String getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(String headerRow) {
		this.headerRow = headerRow;
	}

	public String getReadingRange() {
		return readingRange;
	}

	public void setReadingRange(String readingRange) {
		this.readingRange = readingRange;
	}

	public List<CheckListTemplateColumnsModel> getColumns() {
		return columns;
	}

	public void setColumns(List<CheckListTemplateColumnsModel> columns) {
		this.columns = columns;
	}

	public String getSubSheetName() {
		return subSheetName;
	}

	public void setSubSheetName(String subSheetName) {
		this.subSheetName = subSheetName;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}
}
