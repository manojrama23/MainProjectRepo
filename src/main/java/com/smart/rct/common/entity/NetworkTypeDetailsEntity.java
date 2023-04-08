package com.smart.rct.common.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NW_TYPE_DETAILS")
public class NetworkTypeDetailsEntity {
	
	private Integer id;
	private String networkType;
	private String createdBy;
	private Date caretedDate;
	private String status;
	private String remarks;
	private String networkColor;
	
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "NW_TYPE")
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	
	@Column(name = "CREATED_BY")
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Column(name = "CREATION_DATE")
	public Date getCaretedDate() {
		return caretedDate;
	}
	public void setCaretedDate(Date caretedDate) {
		this.caretedDate = caretedDate;
	}
	
	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Column(name = "NETWORK_COLOR")
	public String getNetworkColor() {
		return networkColor;
	}
	public void setNetworkColor(String networkColor) {
		this.networkColor = networkColor;
	}
	
	

}
