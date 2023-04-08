package com.smart.rct.postmigration.entity;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerDetailsEntity;

@Entity
@Table(name = "PARTIAL_SITE_DATA_LIST")
public class PartialSaveSiteReportEntity {

	
	//@Column(name = "ID",nullable = false)
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//private Integer id;
	@Id
	@Column(name = "NE_ID",nullable = false)
	private String neId;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "CIQ_FILE_NAME",nullable=false)
	private String ciqFileName;
	
	@Column(name = "NE_NAME",nullable=false)
	private String neName;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "PACKED_BY",nullable=false)
	private String packedBy;
	
	@Column(name = "PACKED_DATE",nullable=false)
	private Date packedDate;
	
	@Column(name = "SITE_REPORT_JSON", columnDefinition = "TEXT")
	private String siteReportJson;
	
	@Column(name = "SITE_REPORT_STATUS")
	private String siteReportStatus;
	
	
	@Column(name = "SITE_NAME")
	private String siteName;
	
	@Column(name = "REPORT_TYPE")
	private String reportType;
	
	


	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}

	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
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

	public String getPackedBy() {
		return packedBy;
	}

	public void setPackedBy(String packedBy) {
		this.packedBy = packedBy;
	}

	public Date getPackedDate() {
		return packedDate;
	}

	public void setPackedDate(Date packedDate) {
		this.packedDate = packedDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSiteReportJson() {
		return siteReportJson;
	}

	public void setSiteReportJson(String siteReportJson) {
		this.siteReportJson = siteReportJson;
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