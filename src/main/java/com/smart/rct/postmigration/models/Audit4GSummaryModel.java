package com.smart.rct.postmigration.models;

import java.util.Date;
import java.util.List;

public class Audit4GSummaryModel {
	
	private Date creationDate;
	
	private List<AuditRunModel> auditNeRunSummary;
		
	private String neId;	

	private String tech;

 	private String userName;  
	
	private String test;
	
	private String testName;
	
	private String yangCommand;
	
	private String auditIssue;
	
	private String expectedResult;
	
	private String actionItem;
	
	private String remarks;
	
	private String errorCode;

	private String referenceMOP;
	
	private String runId;
	

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
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

	public String getAuditIssue() {
		return auditIssue;
	}

	public void setAuditIssue(String auditIssue) {
		this.auditIssue = auditIssue;
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
	
	public String getRemarks() {
		if (remarks == null) {
			return "";
		}
		return remarks;
	}

	public void setRemarks(String remarks) {
		if (remarks == null) {
			this.remarks = "";
		}
		this.remarks = remarks;
	}
	
	public String getErrorCode() {
		if (errorCode == null) {
			return "";
		}
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		if (errorCode == null) {
			this.errorCode = "";
		}
		this.errorCode = errorCode;
	}

	public String getReferenceMOP () {
		return referenceMOP;
	}
	public void setReferenceMOP(String referenceMOP) {
		this.referenceMOP = referenceMOP;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<AuditRunModel> getAuditNeRunSummary() {
		return auditNeRunSummary;
	}

	public void setAuditNeRunSummary(List<AuditRunModel> auditNeRunSummary) {
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
