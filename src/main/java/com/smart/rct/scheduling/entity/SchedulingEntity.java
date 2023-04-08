package com.smart.rct.scheduling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULING_DETAILS")
public class SchedulingEntity{
	
	private Integer id;	
	private String commissioningName;
	private String networkType;
	private String lsmVersion;
	private String lsmName;
	private String remarks;
	private String preMigFileName;
	private Date preMigValidateDate;	
	private Date preMigGenerateDate;
	private Date preMigGrowDate;
	private String migUseCaseName;
	private Date migDate;
	
	private String postMigUseCaseName;
	private Date postMigDate;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "COMMISSIONING_NAME",nullable=false)
	public String getCommissioningName() {
		return commissioningName;
	}
	public void setCommissioningName(String commissioningName) {
		this.commissioningName = commissioningName;
	}
	
	@Column(name = "NETWORK_TYPE",nullable=false)
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	
	@Column(name = "LSM_VERSION",nullable=false)
	public String getLsmVersion() {
		return lsmVersion;
	}
	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}
	
	@Column(name = "LSM_NAME",nullable=false)
	public String getLsmName() {
		return lsmName;
	}
	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}
	
	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Column(name = "PRE_MIG_FILE_NAME")
	public String getPreMigFileName() {
		return preMigFileName;
	}
	public void setPreMigFileName(String preMigFileName) {
		this.preMigFileName = preMigFileName;
	}
	
	@Column(name = "PRE_MIG_VALIDATE_DATE")
	public Date getPreMigValidateDate() {
		return preMigValidateDate;
	}
	public void setPreMigValidateDate(Date preMigValidateDate) {
		this.preMigValidateDate = preMigValidateDate;
	}
	
	@Column(name = "PRE_MIG_GENERATE_DATE")
	public Date getPreMigGenerateDate() {
		return preMigGenerateDate;
	}
	public void setPreMigGenerateDate(Date preMigGenerateDate) {
		this.preMigGenerateDate = preMigGenerateDate;
	}
	
	@Column(name = "PRE_MIG_GROW_DATE")
	public Date getPreMigGrowDate() {
		return preMigGrowDate;
	}
	public void setPreMigGrowDate(Date preMigGrowDate) {
		this.preMigGrowDate = preMigGrowDate;
	}
	
	@Column(name = "MIG_USE_CASE_NAME")
	public String getMigUseCaseName() {
		return migUseCaseName;
	}
	public void setMigUseCaseName(String migUseCaseName) {
		this.migUseCaseName = migUseCaseName;
	}
	
	@Column(name = "MIG_DATE")
	public Date getMigDate() {
		return migDate;
	}
	public void setMigDate(Date migDate) {
		this.migDate = migDate;
	}
	
	@Column(name = "POST_MIG_USE_CASE_NAME")
	public String getPostMigUseCaseName() {
		return postMigUseCaseName;
	}
	public void setPostMigUseCaseName(String postMigUseCaseName) {
		this.postMigUseCaseName = postMigUseCaseName;
	}
	
	@Column(name = "POST_MIG_DATE")
	public Date getPostMigDate() {
		return postMigDate;
	}
	public void setPostMigDate(Date postMigDate) {
		this.postMigDate = postMigDate;
	}
	
	

}
