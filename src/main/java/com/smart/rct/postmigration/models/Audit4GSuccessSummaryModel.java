package com.smart.rct.postmigration.models;

public class Audit4GSuccessSummaryModel {
	
	private String test;
	
	private String testName;
	
	private String yangCommand;
	
	private String auditSuccess;
	
	private String expectedResult;
	
	private String actionItem;
	
	private String remarks;
	
	private String errorCode;

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

	public String getAuditSuccess() {
		return auditSuccess;
	}

	public void setAuditSuccess(String auditSuccess) {
		this.auditSuccess = auditSuccess;
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
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	
	
}
