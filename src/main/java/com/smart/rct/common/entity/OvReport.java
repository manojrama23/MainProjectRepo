package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "OV_REPORT")
public class OvReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "TRACKER_ID")
	private String trackerid;
	
	@Column(name = "WORKPLAN_ID")
	private String workplanid;
	
	@Column(name = "PROGRAM_NAME")
	private String programName;
	
	@Column(name = "COMMISSION_DATE")
	private String commissionDate;
	
	@Column(name = "ENB_ID")
	private String enbId;
	
	@Column(name = "SITE_NAME")
	private String siteName;
	
	@Column(name = "COMISSIONING_CIQ")
	private String commissioningCiq;
	
	@Column(name = "SCRIPT_DEVELOPEMENT")
	private String scriptDevlopement;
	
	@Column(name = "CIQ_VALIATE_FINISH")
	private String ciqValidateFinish;

	@Column(name = "REASON")
	private String reason;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTrackerid() {
		return trackerid;
	}

	public void setTrackerid(String trackerid) {
		this.trackerid = trackerid;
	}

	public String getWorkplanid() {
		return workplanid;
	}

	public void setWorkplanid(String workplanid) {
		this.workplanid = workplanid;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getCommissionDate() {
		return commissionDate;
	}

	public void setCommissionDate(String commissionDate) {
		this.commissionDate = commissionDate;
	}

	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getCommissioningCiq() {
		return commissioningCiq;
	}

	public void setCommissioningCiq(String commissioningCiq) {
		this.commissioningCiq = commissioningCiq;
	}

	public String getScriptDevlopement() {
		return scriptDevlopement;
	}

	public void setScriptDevlopement(String scriptDevlopement) {
		this.scriptDevlopement = scriptDevlopement;
	}

	public String getCiqValidateFinish() {
		return ciqValidateFinish;
	}

	public void setCiqValidateFinish(String ciqValidateFinish) {
		this.ciqValidateFinish = ciqValidateFinish;
	}

}

