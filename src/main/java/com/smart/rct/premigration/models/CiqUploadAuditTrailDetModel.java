package com.smart.rct.premigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class CiqUploadAuditTrailDetModel {
	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String programName;
	private String ciqFileName;
	private String ciqFilePath;
	private String scriptFileName;
	private String scriptFilePath;
	private String checklistFileName;
	private String checklistFilePath;
	private String ciqVersion;
	private String fileSourceType;
	private String uploadBy;
	private String remarks;
	private String creationDate;
	private String searchStartDate;
	private String searchEndDate;
	private String fromDate;
	private String toDate;
	private String fetchInfo;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUploadBy() {
		return uploadBy;
	}

	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
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

	public String getCiqVersion() {
		return ciqVersion;
	}

	public void setCiqVersion(String ciqVersion) {
		this.ciqVersion = ciqVersion;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public String getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public String getCiqFileName() {
		return ciqFileName;
	}

	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
	}

	public String getScriptFileName() {
		return scriptFileName;
	}

	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}

	public String getChecklistFileName() {
		return checklistFileName;
	}

	public void setChecklistFileName(String checklistFileName) {
		this.checklistFileName = checklistFileName;
	}

	public String getFileSourceType() {
		return fileSourceType;
	}

	public void setFileSourceType(String fileSourceType) {
		this.fileSourceType = fileSourceType;
	}

	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}

	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getCiqFilePath() {
		return ciqFilePath;
	}

	public void setCiqFilePath(String ciqFilePath) {
		this.ciqFilePath = ciqFilePath;
	}

	public String getScriptFilePath() {
		return scriptFilePath;
	}

	public void setScriptFilePath(String scriptFilePath) {
		this.scriptFilePath = scriptFilePath;
	}

	public String getChecklistFilePath() {
		return checklistFilePath;
	}

	public void setChecklistFilePath(String checklistFilePath) {
		this.checklistFilePath = checklistFilePath;
	}

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

	public String getFetchInfo() {
		return fetchInfo;
	}

	public void setFetchInfo(String fetchInfo) {
		this.fetchInfo = fetchInfo;
	}
	
	
}
