
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
@Table(name = "AUDIT_4GFSU_PASS_SUMMARY")
public class Audit4GFsuSuccessSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "AUDIT_RULE_ID", referencedColumnName = "ID", nullable = false)
	private Audit4GFsuRulesEntity audit4gFsuRulesEntity;

	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;

	@Column(name = "NE_ID", nullable = false)
	private String neId;

	@Column(name = "AUDIT_PASS", columnDefinition = "LONGTEXT")
	private String auditSuccess;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Audit4GFsuRulesEntity getAudit4gFsuRulesEntity() {
		return audit4gFsuRulesEntity;
	}

	public void setAudit4gFsuRulesEntity(Audit4GFsuRulesEntity audit4gFsuRulesEntity) {
		this.audit4gFsuRulesEntity = audit4gFsuRulesEntity;
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

	public String getAuditSuccess() {
		return auditSuccess;
	}

	public void setAuditSuccess(String auditSuccess) {
		this.auditSuccess = auditSuccess;
	}
	
	

}
