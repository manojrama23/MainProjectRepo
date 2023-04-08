package com.smart.rct.premigration.entity;

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
@Table(name = "CIQ_UPLOAD_AUDITTRAIL")
public class CiqUploadAuditTrailDetEntity {
	
	@Id
	@Column(name = "ID",nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "CIQ_FILE_NAME",nullable = false)
	private String ciqFileName;
	
	@Column(name = "CIQ_FILE_PATH",nullable = false)
	private String ciqFilePath;
	
	@Column(name = "SCRIPT_FILE_NAME")
	private String scriptFileName;
	
	@Column(name = "SCRIPT_FILE_PATH")
	private String scriptFilePath;
	
	@Column(name = "CHECK_LIST_FILE_NAME")
	private String checklistFileName;
	
	@Column(name = "CHECK_LIST_FILE_PATH")
	private String checklistFilePath;
	
	@Column(name = "CIQ_VERSION",nullable = false)
	private String ciqVersion;
	
	@Column(name = "FILE_SOURCE_TYPE",nullable = false)
	private String fileSourceType;
	
	@Column(name = "UPLOADED_BY",nullable = false)
	private String uploadBy;
	
	@Column(name = "REMARKS",nullable = false)
	private String remarks;
	
	@Column(name = "CREATION_DATE",nullable = false)
	private Date creationDate;
	
	@Column(name = "FETCH_INFO",columnDefinition="LONGTEXT")
	private String fetchInfo;

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

	public String getCiqVersion() {
		return ciqVersion;
	}

	public void setCiqVersion(String ciqVersion) {
		this.ciqVersion = ciqVersion;
	}

	public String getFileSourceType() {
		return fileSourceType;
	}

	public void setFileSourceType(String fileSourceType) {
		this.fileSourceType = fileSourceType;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public String getFetchInfo() {
		return fetchInfo;
	}

	public void setFetchInfo(String fetchInfo) {
		this.fetchInfo = fetchInfo;
	}
	
	
	
}
