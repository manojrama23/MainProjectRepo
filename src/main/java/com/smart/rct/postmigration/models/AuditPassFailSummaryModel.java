package com.smart.rct.postmigration.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;

public class AuditPassFailSummaryModel {

	private Integer id;

	private String neId;

	private Date creationDate;

	private String neName;

	private String tech;

	private String node;

	private List<AuditRunModel> auditNeRunSummary;

	private String testName;

	private String auditPassFail;

	private Set<String> headerNames;
	
	private String userName;
//	private List<Audit4GSummaryModel> audit4gFsuRulesEntity;
	

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private RunTestEntity runTestEntity;
	
	
	
	


	public RunTestEntity getRunTestEntity() {
		return runTestEntity;
	}

	public void setRunTestEntity(RunTestEntity runTestEntity) {
		this.runTestEntity = runTestEntity;
	}


	public Integer getId() {
		return id;
	}

	public List<AuditRunModel> getAuditNeRunSummary() {
		return auditNeRunSummary;
	}

	public void setAuditNeRunSummary(List<AuditRunModel> auditNeRunSummary) {
		this.auditNeRunSummary = auditNeRunSummary;
	}

	public Set<String> getHeaderNames() {
		return headerNames;
	}

	public void setHeaderNames(Set<String> headerNames) {
		this.headerNames = headerNames;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
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

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getAuditPassFail() {
		return auditPassFail;
	}

	public void setAuditPassFail(String auditPassFail) {
		this.auditPassFail = auditPassFail;
	}

}
