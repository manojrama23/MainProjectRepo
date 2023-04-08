package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX3")
public class AuditCriticalParamsIndex3Entity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "NE_ID", nullable = true)
	private String neId;
	
	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String value) {
		this.neType = value;
	}

	public String getSwVersion() {
		return swVersion;
	}

	public void setSwVersion(String swVersion) {
		this.swVersion = swVersion;
	}

	public String getFlavorId() {
		return flavorId;
	}

	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}

	public String getIpSddress() {
		return ipSddress;
	}

	public void setIpSddress(String ipSddress) {
		this.ipSddress = ipSddress;
	}

	public String getF1ApState() {
		return f1ApState;
	}

	public void setF1ApState(String f1ApState) {
		this.f1ApState = f1ApState;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	
	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}

	@Column(name = "NE_TYPE",  nullable = true)
	private String neType;
	
	@Column(name = "SW_VERSION", nullable = true)
	private String swVersion;
	
	@Column(name = "FLAVOR_ID",  nullable = true)
	private String flavorId;
	
	@Column(name = "IP_ADDRESS", nullable = true)
	private String ipSddress;
	
	@Column(name = "F1_AP_STATE", nullable = true)
	private String f1ApState;
	
	
	@Column(name = "SOFTWARE_VERSION" , nullable = true)
	private String softwareVersion;
	
	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "AUDIT_STATUS", nullable = true)
	private String auditStatus;
	
	@Column(name = "AUDIT_RESULT", nullable = true)
	private String auditResult;
	
	public String getAuditResult() {
		return auditResult;
	}

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	@ManyToOne
	@JoinColumn(name = "INDEX_ID", referencedColumnName = "ID", nullable = false)
	private AuditCriticalParamsSummaryEntity auditCriticalParamEntity;
	
	
}
