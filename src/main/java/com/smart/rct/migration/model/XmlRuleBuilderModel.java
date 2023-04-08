package com.smart.rct.migration.model;

import java.util.List;

public class XmlRuleBuilderModel {

	private String id;
	private String ruleName;
	private String rootName;
	private String subRootName;
	private String remarks;
	private List<XmlRootModel> rootDetails;
	private List<XmlElementModel> elementDetails;
	private String createdBy;
	private String timeStamp;
	private String migrationType;
	private String loopType;
	private String cmdName;
	private String prompt;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getLoopType() {
		return loopType;
	}

	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<XmlRootModel> getRootDetails() {
		return rootDetails;
	}

	public void setRootDetails(List<XmlRootModel> rootDetails) {
		this.rootDetails = rootDetails;
	}

	public List<XmlElementModel> getElementDetails() {
		return elementDetails;
	}

	public void setElementDetails(List<XmlElementModel> elementDetails) {
		this.elementDetails = elementDetails;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
	}

}
