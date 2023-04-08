package com.smart.rct.common.models;

public class CategoryDetailsModel {
	private String issueId;
	private String inIssueEditMode;
	private String category;
	private String issue;
	private String technology;
	private String attribute;
	private String resolved;
	
	private String remarks;
	
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getInIssueEditMode() {
		return inIssueEditMode;
	}
	public void setInIssueEditMode(String inIssueEditMode) {
		this.inIssueEditMode = inIssueEditMode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getResolved() {
		return resolved;
	}
	public void setResolved(String resolved) {
		this.resolved = resolved;
	}

	
	

}
