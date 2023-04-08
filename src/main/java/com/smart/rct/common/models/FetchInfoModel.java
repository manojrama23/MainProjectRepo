package com.smart.rct.common.models;

public class FetchInfoModel {

	
	private String ciqName;
	private String reason;
	private String marketName;
	
	private String status;
	private String updatedWithNewCiq;

	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdatedWithNewCiq() {
		return updatedWithNewCiq;
	}

	public void setUpdatedWithNewCiq(String updatedWithNewCiq) {
		this.updatedWithNewCiq = updatedWithNewCiq;
	}
	
	
	
}
