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
@Table(name = "GENERATE_INFO_AUDIT")
public class GenerateInfoAuditEntity {
	
	@Id
	@Column(name = "ID",nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "FILE_NAME",nullable=false)
	private String fileName;
	
	@Column(name = "FILE_PATH",nullable=false)
	private String filePath;
	
	@Column(name = "FILE_TYPE",nullable=false)
	private String fileType;
	
	@Column(name = "CIQ_FILE_NAME",nullable=false)
	private String ciqFileName;
	
	@Column(name = "NE_NAME",nullable=false)
	private String neName;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "GENERATED_BY",nullable=false)
	private String generatedBy;
	
	@Column(name = "GENERATION_DATE",nullable=false)
	private Date generationDate;
	
	@Column(name = "SITE_NAME")
	private String siteName;
	
	@Column(name = "OVUPDATE_STATUS")
	private String ovUpdateStatus;	

	public String getOvUpdateStatus() {
		return ovUpdateStatus;
	}
	@Column(name = "INTEGRATION_TYPE")
	private String integrationType;

	public String getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
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

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public Date getGenerationDate() {
		return generationDate;
	}

	public void setGenerationDate(Date generationDate) {
		this.generationDate = generationDate;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
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
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
