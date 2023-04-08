package com.smart.rct.common.models;

import java.util.List;

public class CIQTemplateModel {
	
	private String sheetName;
	private String seqOrder;
	private String sheetType;
	private String headerRow;
	private String readingRange;
	private String enbIdColumnHeaderName;
	private String enbNameColumnHeaderName;
	//private String subSheetName;
	List<ProgramTemplateColumnsModel> columns;
	private String subSheetName;
	private String subSheetAliasName;
	private String sheetAliasName;
	
	
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
	public List<ProgramTemplateColumnsModel> getColumns() {
		return columns;
	}
	public void setColumns(List<ProgramTemplateColumnsModel> columns) {
		this.columns = columns;
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
	public String getEnbIdColumnHeaderName() {
		return enbIdColumnHeaderName;
	}
	public void setEnbIdColumnHeaderName(String enbIdColumnHeaderName) {
		this.enbIdColumnHeaderName = enbIdColumnHeaderName;
	}
	public String getEnbNameColumnHeaderName() {
		return enbNameColumnHeaderName;
	}
	public void setEnbNameColumnHeaderName(String enbNameColumnHeaderName) {
		this.enbNameColumnHeaderName = enbNameColumnHeaderName;
	}
	public String getSubSheetName() {
		return subSheetName;
	}
	public void setSubSheetName(String subSheetName) {
		this.subSheetName = subSheetName;
	}
	public String getSubSheetAliasName() {
		return subSheetAliasName;
	}
	public void setSubSheetAliasName(String subSheetAliasName) {
		this.subSheetAliasName = subSheetAliasName;
	}
	public String getSheetAliasName() {
		return sheetAliasName;
	}
	public void setSheetAliasName(String sheetAliasName) {
		this.sheetAliasName = sheetAliasName;
	}
	
	
	
	
	
	
	

}
