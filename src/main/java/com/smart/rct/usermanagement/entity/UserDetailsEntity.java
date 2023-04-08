package com.smart.rct.usermanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerEntity;

@Entity
@Table(name = "USER_DETAILS")
public class UserDetailsEntity {
	private Integer id;
	private String userName;
	private String password;
	private String emailId;
	private String programName;
	private String remarks;
	private Date creationDate;
	private Date lastLoginDate;
	private String status;
	private String userFullName;	
	private String vpnUserName;
	private String vpnPassword;
	private String createdBy;
	private UserRoleDetailsEntity userRoleDetailsEntity;
	private CustomerEntity customerEntity;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "USER_NAME", nullable = false)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "PASSWORD",nullable = false,length=400)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", nullable = false)
	public UserRoleDetailsEntity getUserRoleDetailsEntity() {
		return userRoleDetailsEntity;
	}

	public void setUserRoleDetailsEntity(UserRoleDetailsEntity userRoleDetailsEntity) {
		this.userRoleDetailsEntity = userRoleDetailsEntity;
	}

	@Column(name = "EMAIL_ID", nullable = false)
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Column(name = "REMARKS" ,length=400)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "CREATION_DATE")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "LAST_LOGIN_DATE")
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Column(name = "STATUS", nullable = false,length=10)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "USER_FULL_NAME")
	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID", nullable = false)
	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	@Column(name = "PROGRAM_NAME", nullable = false)
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	@Column(name = "VPN_USER_NAME", nullable = false)
	public String getVpnUserName() {
		return vpnUserName;
	}

	public void setVpnUserName(String vpnUserName) {
		this.vpnUserName = vpnUserName;
	}
	@Column(name = "VPN_PASSWORD", nullable = false,length=400)
	public String getVpnPassword() {
		return vpnPassword;
	}

	public void setVpnPassword(String vpnPassword) {
		this.vpnPassword = vpnPassword;
	}
	@Column(name = "CREATED_BY",nullable = false)
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	
	

}
