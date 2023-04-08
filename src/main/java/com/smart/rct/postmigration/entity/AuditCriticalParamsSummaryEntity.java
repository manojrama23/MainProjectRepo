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
@Table(name = "AUDIT_CRITICAL_PARAMS_SUMMARY")
public class AuditCriticalParamsSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID",nullable = false)
	private Integer id;
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public Integer getProgramId() {
		return programId;
	}

	public void setProgramId(Integer integer) {
		this.programId = integer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column (name = "CREATION_DATE",nullable = false)
	private Date creationDate;

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	

	@Column(name = "SITE_NAME", nullable = true)
	private String siteName;
	
	@Column(name = "USER_NAME", nullable = true)
	private String userName;
	
	@Column(name = "NE_ID", nullable = true)
	private String neId;
	
	@Column(name = "NE_NAME", nullable = true)
	private String neName;
	
	@Column(name = "PROGRAM_NAME", nullable = true)
	private String programName;
	
	
	@Column(name = "PROGRAM_ID", nullable = true)
	private Integer programId;
	
	
	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;
	

	@Column(name = "STATUS", nullable = false)
	private String status;
	
	@Column(name = "SFP_AUDIT_STATUS", nullable = true)
	private String sfpAuditStatus;

	public String getSfpAuditStatus() {
		return sfpAuditStatus;
	}

	public void setSfpAuditStatus(String sfpAuditStatus) {
		this.sfpAuditStatus = sfpAuditStatus;
	}

	public String getRetAuditStatus() {
		return retAuditStatus;
	}

	public void setRetAuditStatus(String redAuditStatus) {
		this.retAuditStatus = redAuditStatus;
	}

	public String getUdaAuditStatus() {
		return udaAuditStatus;
	}

	public void setUdaAuditStatus(String udaAuditStatus) {
		this.udaAuditStatus = udaAuditStatus;
	}

	public String getHwAuditStatus() {
		return hwAuditStatus;
	}

	public void setHwAuditStatus(String hwAuditStatus) {
		this.hwAuditStatus = hwAuditStatus;
	}

	@Column(name = "RET_AUDIT_STATUS", nullable = true)
	private String retAuditStatus;
	
	@Column(name = "UDA_AUDIT_STATUS", nullable = true)
	private String udaAuditStatus;
	
	@Column(name = "HW_AUDIT_STATUS", nullable = true)
	private String hwAuditStatus;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
}
