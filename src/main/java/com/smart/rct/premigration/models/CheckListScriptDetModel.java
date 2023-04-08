package com.smart.rct.premigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class CheckListScriptDetModel {
	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String checkListFileName;
	private String sheetName;
	private String configType;
	private Integer stepIndex;
	private String scriptName;
	private int scriptExeSeq;
	private String createdBy;
	private String creationDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}
	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	public String getCheckListFileName() {
		return checkListFileName;
	}
	public void setCheckListFileName(String checkListFileName) {
		this.checkListFileName = checkListFileName;
	}
	public Integer getStepIndex() {
		return stepIndex;
	}
	public void setStepIndex(Integer stepIndex) {
		this.stepIndex = stepIndex;
	}
	public String getScriptName() {
		return scriptName;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	public int getScriptExeSeq() {
		return scriptExeSeq;
	}
	public void setScriptExeSeq(int scriptExeSeq) {
		this.scriptExeSeq = scriptExeSeq;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	
}
