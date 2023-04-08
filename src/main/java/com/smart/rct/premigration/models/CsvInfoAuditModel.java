package com.smart.rct.premigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class CsvInfoAuditModel {
	private Integer id;
	private String csvFileName;
	private String csvFilePath;
	private Integer customerId;
	private String generatedBy;
	private String status;
	private String fileType;
	private String lsmVersion;
	private Integer networkTypeId;
	private String networkType;
	private String generationDate;
	
	private String programName;
	private String neName;
	private String ciqFileName;
	private String remarks;
	private CustomerDetailsEntity programDetailsEntity;
	private String searchStartDate;
	private String searchEndDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCsvFileName() {
		return csvFileName;
	}
	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}
	
	
	public String getCsvFilePath() {
		return csvFilePath;
	}
	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getGeneratedBy() {
		return generatedBy;
	}
	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public String getGenerationDate() {
		return generationDate;
	}
	public void setGenerationDate(String generationDate) {
		this.generationDate = generationDate;
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
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getCiqFileName() {
		return ciqFileName;
	}
	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
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
	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}
	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	
	
}
