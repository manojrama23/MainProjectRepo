package com.smart.rct.premigration.models;

import java.util.LinkedHashMap;

import javax.persistence.Id;

public class CheckListDetailsModel {

	@Id
	private Integer id;
	private String fileName;
	private String sheetName;
	private String seqOrder;
	private String subSheetName;
	private String configType;
	private LinkedHashMap<String, String> checkListMap = new LinkedHashMap<>();
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
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
	public LinkedHashMap<String, String> getCheckListMap() {
		return checkListMap;
	}
	public void setCheckListMap(LinkedHashMap<String, String> checkListMap) {
		this.checkListMap = checkListMap;
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
