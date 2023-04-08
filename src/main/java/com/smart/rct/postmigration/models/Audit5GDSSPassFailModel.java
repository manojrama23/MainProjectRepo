 package com.smart.rct.postmigration.models;

import java.util.Date;


import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;

public class Audit5GDSSPassFailModel {
	
	private Date creationDate;
	
	private String neName;
	
	private String tech;
	
	private String node;
	
	
	private Audit5GDSSRulesEntity audit5GDSSRulesEntity;
	
	
	//private RunTestEntity runTestEntity;
	
	private String testName;
	
	private String auditPassFail;

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

	public Audit5GDSSRulesEntity getAudit5GDSSRulesEntity() {
		return audit5GDSSRulesEntity;
	}

	public void setAudit5GDSSRulesEntity(Audit5GDSSRulesEntity audit5gdssRulesEntity) {
		audit5GDSSRulesEntity = audit5gdssRulesEntity;
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
