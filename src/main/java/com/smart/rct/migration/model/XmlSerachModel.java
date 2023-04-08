package com.smart.rct.migration.model;

public class XmlSerachModel {

	private String ruleName;
	private String rootName;
	private String subRootName;
	private String createdBy;
	private String cmdName;
	private String prompt;
	private String status;

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public String getSubRootName() {
		return subRootName;
	}

	public void setSubRootName(String subRootName) {
		this.subRootName = subRootName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
