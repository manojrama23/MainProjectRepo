package com.smart.rct.common.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smart.rct.postmigration.models.AuditRunModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditSummaryModel {
	private String testName;
	private String yangCommand;
	private String test;
	private String expectedResult;
	private String actionItem;
	private String auditIssue;
	private String remarks;
	private String errorCode;
	private String referenceMOP;
	private Date creationDate;
	private String auditNeRunSummary;
	private String neId;
	private String tech;

	private String node;

	private Set<String> headerNames;

	private String userName;
	private String runId;
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getYangCommand() {
		return yangCommand;
	}
	public void setYangCommand(String yangCommand) {
		this.yangCommand = yangCommand;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getExpectedResult() {
		return expectedResult;
	}
	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}
	public String getActionItem() {
		return actionItem;
	}
	public void setActionItem(String actionItem) {
		this.actionItem = actionItem;
	}
	public String getAuditIssue() {
		return auditIssue;
	}
	public void setAuditIssue(String auditIssue) {
		this.auditIssue = auditIssue;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getReferenceMOP () {
		return referenceMOP;
	}
	public void setReferenceMOP(String referenceMOP) {
		this.referenceMOP = referenceMOP;
	}
	public String getAuditNeRunSummary() {
		return auditNeRunSummary;
	}
	public void setAuditNeRunSummary(String auditNeRunSummary) {
		this.auditNeRunSummary = auditNeRunSummary;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getTech() {
		return tech;
	}
	public void setTech(String tech) {
		this.tech = tech;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public Set<String> getHeaderNames() {
		return headerNames;
	}
	public void setHeaderNames(Set<String> headerNames) {
		this.headerNames = headerNames;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}
	
	
	
}

