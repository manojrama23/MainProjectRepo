package com.smart.rct.postmigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class SiteDataModel {
	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String fileName;
	private String filePath;
	private String ciqFileName;
	private String neName;
	private String remarks;
	private String packedBy;
	private String packedDate;
	private String searchStartDate;
	private String searchEndDate;
	private String siteReportStatus;
	private String neId;
	private String siteName;
	private String reportType;
	private String ovSiteReportStatus;
	
	public String getOvSiteReportStatus() {
		return ovSiteReportStatus;
	}

	public void setOvSiteReportStatus(String ovSiteReportStatus) {
		this.ovSiteReportStatus = ovSiteReportStatus;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPackedBy() {
		return packedBy;
	}

	public void setPackedBy(String packedBy) {
		this.packedBy = packedBy;
	}

	public String getPackedDate() {
		return packedDate;
	}

	public void setPackedDate(String packedDate) {
		this.packedDate = packedDate;
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

	public String getSiteReportStatus() {
		return siteReportStatus;
	}

	public void setSiteReportStatus(String siteReportStatus) {
		this.siteReportStatus = siteReportStatus;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	

}
