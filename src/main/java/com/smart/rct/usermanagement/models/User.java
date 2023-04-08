package com.smart.rct.usermanagement.models;

import java.io.Serializable;
import java.util.Random;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String userName;
	private Integer roleId;
	private Integer customerId;
	private String lastLoginTime;
	private String serviceToken;
	private String sessionId = null;
	private String loginDate;
	private String createdBy;
	private String[] programName;
	private String role;
	private String userFullName;
	private String emailId;
	private long lastAccessedTime;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public void setLastLoginTime(String time) {
		this.lastLoginTime = time;
	}

	public String getLastLoginTime() {
		return this.lastLoginTime;
	}

	public String getTokenKey() {
		if (this.sessionId != null) {
			return sessionId;
		}

		int hash = 17;
		Random randomGenerator = new Random();
		hash = hash * 31 + userName.hashCode();
		hash = hash * 31 + lastLoginTime.hashCode();
		hash = hash + randomGenerator.nextInt(1000);
		this.sessionId = Integer.toHexString(hash);
		return this.sessionId;
	}

	public String getServiceToken() {
		return serviceToken;
	}

	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

}
