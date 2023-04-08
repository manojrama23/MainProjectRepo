package com.smart.rct.premigration.models;

import java.util.LinkedHashMap;

import javax.persistence.Id;

public class CheckListModel {
	
	@Id
	private Integer id;
	private String fileName;
	private String sheetName;
	private String seqOrder;
	private String ciq;
	private String program;
	//private boolean check; 
	private String enodeName;
	private Integer runTestId;
	private Integer stepIndex;
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
	public String getCiq() {
		return ciq;
	}
	public void setCiq(String ciq) {
		this.ciq = ciq;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}

	/*
	 * public boolean isCheck() { return check; } public void setCheck(boolean
	 * check) { this.check = check; }
	 */
	public String getEnodeName() {
		return enodeName;
	}
	public void setEnodeName(String enodeName) {
		this.enodeName = enodeName;
	}
	
	public Integer getRunTestId() {
		return runTestId;
	}
	public void setRunTestId(Integer runTestId) {
		this.runTestId = runTestId;
	}
	
	public Integer getStepIndex() {
		return stepIndex;
	}
	public void setStepIndex(Integer stepIndex) {
		this.stepIndex = stepIndex;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}

}
