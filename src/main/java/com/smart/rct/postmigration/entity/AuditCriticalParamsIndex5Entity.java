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
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX5")
public class AuditCriticalParamsIndex5Entity {

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

	@Column(name = "FSU_ID", nullable = true)
	private String fsuId;
	
	public String getFsuId() {
		return fsuId;
	}

	public void setFsuId(String fsuId) {
		this.fsuId = fsuId;
	}

	public String getSupportCellNumber() {
		return supportCellNumber;
	}

	public void setSupportCellNumber(String supportCellNumber) {
		this.supportCellNumber = supportCellNumber;
	}

	public String getConnectedPodType() {
		return connectedPodType;
	}

	public void setConnectedPodType(String connectedPodType) {
		this.connectedPodType = connectedPodType;
	}

	public String getConnectedPodId() {
		return connectedPodId;
	}

	public void setConnectedPodId(String connectedPodId) {
		this.connectedPodId = connectedPodId;
	}

	public String getConnectedPodPortId() {
		return connectedPodPortId;
	}

	public void setConnectedPodPortId(String connectedPodPortId) {
		this.connectedPodPortId = connectedPodPortId;
	}

	public String getVlanId() {
		return vlanId;
	}

	public void setVlanId(String vlanId) {
		this.vlanId = vlanId;
	}


	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}

	@Column(name = "SUPPORT_CELL_NUMBER",  nullable = true)
	private String supportCellNumber;
	
	@Column(name = "CONNECTED_POD_TYPE", nullable = true)
	private String connectedPodType;
	
	@Column(name = "CONNECTED_POD_ID", nullable = true)
	private String connectedPodId;
	
	
	@Column(name = "CONNECTED_POD_PORT_ID", nullable = true)
	private String connectedPodPortId;
	
	@Column(name = "VLAN_ID", nullable = true)
	private String vlanId;
	
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

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@ManyToOne
	@JoinColumn(name = "INDEX_ID", referencedColumnName = "ID", nullable = false)
	private AuditCriticalParamsSummaryEntity auditCriticalParamEntity;


}
