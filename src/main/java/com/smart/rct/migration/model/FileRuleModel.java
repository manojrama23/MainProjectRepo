package com.smart.rct.migration.model;

import java.util.Map;

public class FileRuleModel {

	private String fileRuleId;
	private String fileRuleSequence;
	private String fileRuleRemarks;
	private Map<String, String> fileDetails;

	public String getFileRuleId() {
		return fileRuleId;
	}

	public void setFileRuleId(String fileRuleId) {
		this.fileRuleId = fileRuleId;
	}

	public String getFileRuleSequence() {
		return fileRuleSequence;
	}

	public void setFileRuleSequence(String fileRuleSequence) {
		this.fileRuleSequence = fileRuleSequence;
	}

	public String getFileRuleRemarks() {
		return fileRuleRemarks;
	}

	public void setFileRuleRemarks(String fileRuleRemarks) {
		this.fileRuleRemarks = fileRuleRemarks;
	}

	public Map<String, String> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(Map<String, String> fileDetails) {
		this.fileDetails = fileDetails;
	}

}
