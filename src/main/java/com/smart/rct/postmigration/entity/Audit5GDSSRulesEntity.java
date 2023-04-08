package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_5G_DSS_RULES")
public class Audit5GDSSRulesEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
    @Column(name = "TEST_NAME", columnDefinition="LONGTEXT")
	private String testName;
	
	@Column(name = "TEST",nullable=false, columnDefinition="LONGTEXT")
	private String test;
	
	@Column(name = "YANG_COMMANDS",nullable=false, columnDefinition="LONGTEXT")
	private String yangCommand;
	
	@Column(name = "EXPECTED_RESULT",nullable=false, columnDefinition="LONGTEXT")
	private String expectedResult;
	
	@Column(name = "ACTION_ITEM",nullable=false,columnDefinition="LONGTEXT")
	private String actionItem;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "REMARKS", columnDefinition="LONGTEXT")
	private String remarks;

	@Column(name = "REFERENCE_MOP", columnDefinition="LONGTEXT")
	private String referenceMOP;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getYangCommand() {
		return yangCommand;
	}

	public void setYangCommand(String yangCommand) {
		this.yangCommand = yangCommand;
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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
}
