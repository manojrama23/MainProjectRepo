package com.smart.rct.common.models;

public class LsmModel {
	private Integer id;
	private String lsmName;
	private String lsmIp;
	private String lsmVersion;
	private String createdBy;
	
	private String lsmUserName;
	private String lsmPassword;
	private String   creationDate;
	private String status;
	private String remarks;
	private Integer networkTypeId;
	private String networkType;
	
	private String programName;
	private String neType;
	private String bucket;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLsmName() {
		return lsmName;
	}
	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}
	public String getLsmIp() {
		return lsmIp;
	}
	public void setLsmIp(String lsmIp) {
		this.lsmIp = lsmIp;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLsmUserName() {
		return lsmUserName;
	}
	public void setLsmUserName(String lsmUserName) {
		this.lsmUserName = lsmUserName;
	}
	public String getLsmPassword() {
		return lsmPassword;
	}
	public void setLsmPassword(String lsmPassword) {
		this.lsmPassword = lsmPassword;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
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
	public String getLsmVersion() {
		return lsmVersion;
	}
	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}
	
	public Integer getNetworkTypeId() {
		return networkTypeId;
	}
	public void setNetworkTypeId(Integer networkTypeId) {
		this.networkTypeId = networkTypeId;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	
	
	
	
	
}
