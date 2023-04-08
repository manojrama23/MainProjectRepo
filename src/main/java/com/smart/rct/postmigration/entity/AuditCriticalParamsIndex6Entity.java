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
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX6")
public class AuditCriticalParamsIndex6Entity {


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
	
	
	@Column(name = "POD_ID", nullable = true)
	private String podId;
	
	public String getPodId() {
		return podId;
	}

	public void setPodId(String podId) {
		this.podId = podId;
	}

	public String getPodType() {
		return podType;
	}

	public void setPodType(String podType) {
		this.podType = podType;
	}

	public String getDss() {
		return dss;
	}

	public void setDss(String dss) {
		this.dss = dss;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSncState() {
		return sncState;
	}

	public void setSncState(String sncState) {
		this.sncState = sncState;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getMtu() {
		return mtu;
	}

	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}


	@Column(name = "POD_TYPE", nullable = true)
	private String podType;
	
	@Column(name = "DSS", nullable = true)
	private String dss;
	
	@Column(name = "IP",  nullable = true)
	private String ip;
	
	@Column(name = "SNC_STATE", nullable = true)
	private String sncState;
	
	@Column(name = "GATEWAY", nullable = true)
	private String gateway;
	
	@Column(name = "MTU", nullable = true)
	private String mtu;
	
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
