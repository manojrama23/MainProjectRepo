package com.smart.rct.common.models;

import javax.persistence.Column;

public class TrackerDetailsModel {
	
	private String trackerId;
	private String prelatedEnbIds;
	private String prelatedGnbIds;
	private String fsuIds;
	private String dssIds;
	private String commissionDate;
	private String workPlanId;
	private String status;
	private String cBandIds;
	
	private String trackerDetailsJson;
	private String workPlanDetailsJson;
	private String commissionDetailsJson;
	private String siteName;

	
	private String workPlanStatus;
	public String getWorkPlanStatus() {
		return workPlanStatus;
	}
	public void setWorkPlanStatus(String workPlanStatus) {
		this.workPlanStatus = workPlanStatus;
	}
	private String CiqCreated;
	private String scriptDevlopement;
	private String ciqValidate;
	private String reason;
	
	//dummy IP
	private String integrationType;
	
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	
	public String getTrackerId() {
		return trackerId;
	}
	public void setTrackerId(String trackerId) {
		this.trackerId = trackerId;
	}
	public String getPrelatedEnbIds() {
		return prelatedEnbIds;
	}
	public void setPrelatedEnbIds(String prelatedEnbIds) {
		this.prelatedEnbIds = prelatedEnbIds;
	}
	public String getPrelatedGnbIds() {
		return prelatedGnbIds;
	}
	public void setPrelatedGnbIds(String prelatedGnbIds) {
		this.prelatedGnbIds = prelatedGnbIds;
	}
	public String getCommissionDate() {
		return commissionDate;
	}
	public void setCommissionDate(String commissionDate) {
		this.commissionDate = commissionDate;
	}
	public String getWorkPlanId() {
		return workPlanId;
	}
	public void setWorkPlanId(String workPlanId) {
		this.workPlanId = workPlanId;
	}
	public String getFsuIds() {
		return fsuIds;
	}
	public void setFsuIds(String fsuIds) {
		this.fsuIds = fsuIds;
	}
	public String getDssIds() {
		return dssIds;
	}
	public void setDssIds(String dssIds) {
		this.dssIds = dssIds;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTrackerDetailsJson() {
		return trackerDetailsJson;
	}
	public void setTrackerDetailsJson(String trackerDetailsJson) {
		this.trackerDetailsJson = trackerDetailsJson;
	}
	public String getWorkPlanDetailsJson() {
		return workPlanDetailsJson;
	}
	public void setWorkPlanDetailsJson(String workPlanDetailsJson) {
		this.workPlanDetailsJson = workPlanDetailsJson;
	}
	public String getCommissionDetailsJson() {
		return commissionDetailsJson;
	}
	public void setCommissionDetailsJson(String commissionDetailsJson) {
		this.commissionDetailsJson = commissionDetailsJson;
	}
	public String getcBandIds() {
		return cBandIds;
	}
	public void setcBandIds(String cBandIds) {
		this.cBandIds = cBandIds;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public String getScriptDevlopement() {
		return scriptDevlopement;
	}
	public void setScriptDevlopement(String scriptDevlopement) {
		this.scriptDevlopement = scriptDevlopement;
	}
	
	public String getCiqCreated() {
		return CiqCreated;
	}
	public void setCiqCreated(String ciqCreated) {
		CiqCreated = ciqCreated;
	}
	public String getCiqValidate() {
		return ciqValidate;
	}
	public void setCiqValidate(String ciqValidate) {
		this.ciqValidate = ciqValidate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	

}
