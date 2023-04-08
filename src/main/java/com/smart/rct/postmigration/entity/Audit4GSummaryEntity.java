package com.smart.rct.postmigration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.migration.entity.RunTestEntity;

@Entity
@Table(name = "AUDIT_4G_SUMMARY")
public class Audit4GSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "AUDIT_RULE_ID", referencedColumnName = "ID", nullable = false)
	private Audit4GRulesEntity audit4gRulesEntity;
	
	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;
	
	@Column(name = "NE_ID", nullable = false)
	private String neId;
	
	@Column(name = "AUDIT_ISSUE", columnDefinition="LONGTEXT")
	private String auditIssue;
	

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Audit4GRulesEntity getAudit4gRulesEntity() {
		return audit4gRulesEntity;
	}

	public void setAudit4gRulesEntity(Audit4GRulesEntity audit4gRulesEntity) {
		this.audit4gRulesEntity = audit4gRulesEntity;
	}

	public RunTestEntity getRunTestEntity() {
		return runTestEntity;
	}

	public void setRunTestEntity(RunTestEntity runTestEntity) {
		this.runTestEntity = runTestEntity;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getAuditIssue() {
		return auditIssue;
	}

	public void setAuditIssue(String auditIssue) {
		this.auditIssue = auditIssue;
	}
	
}
