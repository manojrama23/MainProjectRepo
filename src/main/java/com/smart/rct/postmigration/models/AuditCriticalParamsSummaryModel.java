package com.smart.rct.postmigration.models;

import java.util.Date;

import com.smart.rct.migration.entity.RunTestEntity;

public class AuditCriticalParamsSummaryModel {
	
	private String timeStamp;
	
	private String fromDate;
	
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	private String toDate;

	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String userName;
	
	private String programName;
	
	private String neId;
	
	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	private Integer Id;


	public Integer getRunTestId() {
		return runTestId;
	}

	public void setRunTestId(Integer runTestId) {
		this.runTestId = runTestId;
	}

	private Integer runTestId;

	private String neName;
	
	private String siteName;
	
	private String status;
	
	private String sfpStatus;
	
	public String getSfpStatus() {
		return sfpStatus;
	}

	public void setSfpStatus(String sfpStatus) {
		this.sfpStatus = sfpStatus;
	}

	public String getRetStatus() {
		return retStatus;
	}

	public void setRetStatus(String retStatus) {
		this.retStatus = retStatus;
	}

	public String getUdaStatus() {
		return UdaStatus;
	}

	public void setUdaStatus(String udaStatus) {
		UdaStatus = udaStatus;
	}

	public String getHwStatus() {
		return hwStatus;
	}

	public void setHwStatus(String hwStatus) {
		this.hwStatus = hwStatus;
	}

	private String retStatus;

	private String UdaStatus;

	private String hwStatus;

	
}
