package com.smart.rct.scheduling.model;

import java.util.Date;

public class SchedulingModel {
	
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
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCommissioningName() {
		return commissioningName;
	}
	public void setCommissioningName(String commissioningName) {
		this.commissioningName = commissioningName;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getLsmVersion() {
		return lsmVersion;
	}
	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}
	public String getLsmName() {
		return lsmName;
	}
	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getPreMigFileName() {
		return preMigFileName;
	}
	public void setPreMigFileName(String preMigFileName) {
		this.preMigFileName = preMigFileName;
	}
	public Date getPreMigValidateDate() {
		return preMigValidateDate;
	}
	public void setPreMigValidateDate(Date preMigValidateDate) {
		this.preMigValidateDate = preMigValidateDate;
	}
	public Date getPreMigGenerateDate() {
		return preMigGenerateDate;
	}
	public void setPreMigGenerateDate(Date preMigGenerateDate) {
		this.preMigGenerateDate = preMigGenerateDate;
	}
	public Date getPreMigGrowDate() {
		return preMigGrowDate;
	}
	public void setPreMigGrowDate(Date preMigGrowDate) {
		this.preMigGrowDate = preMigGrowDate;
	}
	public String getMigUseCaseName() {
		return migUseCaseName;
	}
	public void setMigUseCaseName(String migUseCaseName) {
		this.migUseCaseName = migUseCaseName;
	}
	public Date getMigDate() {
		return migDate;
	}
	public void setMigDate(Date migDate) {
		this.migDate = migDate;
	}
	public String getPostMigUseCaseName() {
		return postMigUseCaseName;
	}
	public void setPostMigUseCaseName(String postMigUseCaseName) {
		this.postMigUseCaseName = postMigUseCaseName;
	}
	public Date getPostMigDate() {
		return postMigDate;
	}
	public void setPostMigDate(Date postMigDate) {
		this.postMigDate = postMigDate;
	}
}
