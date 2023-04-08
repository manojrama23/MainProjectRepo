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
@Table(name = "AUDIT_5GDSS_PASSFAIL")
public class Audit5GDSSPassFailSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "AUDIT_RULE_ID", referencedColumnName = "ID", nullable = false)
	private Audit5GDSSRulesEntity audit5GDSSRulesEntity;
	
	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;
	
	@Column(name = "NE_ID", nullable = false)
	private String neId;
	
	@Column(name = "AUDIT_PASSFAIL", columnDefinition="LONGTEXT")
	private String auditPassFail;
	
	@Column (name = "CREATION_DATE",nullable = false)
	private Date creationDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Audit5GDSSRulesEntity getAudit5GDSSRulesEntity() {
		return audit5GDSSRulesEntity;
	}

	public void setAudit5GDSSRulesEntity(Audit5GDSSRulesEntity audit5gdssRulesEntity) {
		audit5GDSSRulesEntity = audit5gdssRulesEntity;
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

	public String getAuditPassFail() {
		return auditPassFail;
	}

	public void setAuditPassFail(String auditPassFail) {
		this.auditPassFail = auditPassFail;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	
	
}
