package com.smart.rct.common.models;

import java.util.Date;

public class NetworkTypeDetailsModel {

	private Integer id;
	private String networkType;
	private String createdBy;
	private Date caretedDate;
	private String status="Active";
	private String remarks="";
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCaretedDate() {
		return caretedDate;
	}
	public void setCaretedDate(Date caretedDate) {
		this.caretedDate = caretedDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
