package com.smart.rct.usermanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsModel {
	private Integer id;
	private String userName;
	private String password;
	private String cnfrmPswd;
	private Integer roleId;
	private String role;
	private String emailId;
	private String location;
	private String remarks;
	private String creationDate;
	private String lastLoginDate;
	private String status;
	private String userFullName;
	private String customerName;
	private Integer customerId;
	private Integer networkTypeId;
	private String networkType;
	private String[] programName;
	private String vpnUserName;
	private String vpnPassword;
	private String createdBy;
	private String programNamehidden;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCnfrmPswd() {
		return cnfrmPswd;
	}

	public void setCnfrmPswd(String cnfrmPswd) {
		this.cnfrmPswd = cnfrmPswd;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	

	public String getVpnUserName() {
		return vpnUserName;
	}

	public void setVpnUserName(String vpnUserName) {
		this.vpnUserName = vpnUserName;
	}

	public String getVpnPassword() {
		return vpnPassword;
	}

	public void setVpnPassword(String vpnPassword) {
		this.vpnPassword = vpnPassword;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String[] getProgramName() {
		return programName;
	}

	public void setProgramName(String[] programName) {
		this.programName = programName;
	}
	@JsonIgnore 
	public String getProgramNamehidden() {
		return programNamehidden;
	}

	public void setProgramNamehidden(String programNamehidden) {
		this.programNamehidden = programNamehidden;
	}

}
