package com.smart.rct.postmigration.entity;

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
@Table(name = "AUDIT_5G_CBAND_SUMMARY")
public class Audit5GCBandSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "AUDIT_RULE_ID", referencedColumnName = "ID", nullable = false)
	private Audit5GCBandRulesEntity audit5GCBandRulesEntity;
	
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

	public Audit5GCBandRulesEntity getAudit5gCbandRulesEntity() {
		return audit5GCBandRulesEntity;
	}

	public void setAudit5gCbandRulesEntity(Audit5GCBandRulesEntity audit5GCBandRulesEntity) {
		this.audit5GCBandRulesEntity = audit5GCBandRulesEntity;
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
