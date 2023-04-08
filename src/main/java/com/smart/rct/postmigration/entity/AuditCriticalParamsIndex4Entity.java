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
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX4")
public class AuditCriticalParamsIndex4Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public String getAlarmUnitType() {
		return alarmUnitType;
	}

	public void setAlarmUnitType(String alarmUnitType) {
		this.alarmUnitType = alarmUnitType;
	}

	public String getAlarmType() {
		return AlarmType;
	}

	public void setAlarmType(String alarmType) {
		AlarmType = alarmType;
	}

	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NE_ID",  nullable = true)
	private String neId;
	
	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "ALARM_UNIT_TYPE",  nullable = true)
	private String alarmUnitType;
	
	@Column(name = "ALARM_TYPE", nullable = true)
	private String AlarmType;
	
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
