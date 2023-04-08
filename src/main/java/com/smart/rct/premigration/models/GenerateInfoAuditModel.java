package com.smart.rct.premigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class GenerateInfoAuditModel {
	
	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String fileName;
	private String filePath;
	private String fileType;
	private String ciqFileName;
	private String neName;
	private String generatedBy;
	private String generationDate;
	private String searchStartDate;
	private String searchEndDate;
	private String remarks;
	private String siteName;
	private String userName;
	private String ovUpdateStatus;
	private String integrationType;
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	public String getOvUpdateStatus() {
		return ovUpdateStatus;
	}
	public void setOvUpdateStatus(String ovUpdateStatus) {
		this.ovUpdateStatus = ovUpdateStatus;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}
	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getCiqFileName() {
		return ciqFileName;
	}
	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getGeneratedBy() {
		return generatedBy;
	}
	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}
	public String getGenerationDate() {
		return generationDate;
	}
	public void setGenerationDate(String generationDate) {
		this.generationDate = generationDate;
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
