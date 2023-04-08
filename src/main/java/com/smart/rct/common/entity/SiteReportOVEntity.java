package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.postmigration.entity.SiteDataEntity;


@Entity
@Table(name = "SITEREPORT_OV_UPDATE_RUN_TEST_RESULT")
public class SiteReportOVEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "SITE_DATA_LIST", referencedColumnName = "ID", nullable = false)
	private SiteDataEntity siteDataEntity;
	
	@Column(name = "CURRENT_RESULT")
	private String currentResult;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "FILE_PATH", columnDefinition="LONGTEXT")
	private String filePath;

	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	

	public SiteDataEntity getSiteDataEntity() {
		return siteDataEntity;
	}

	public void setSiteDataEntity(SiteDataEntity siteDataEntity) {
		this.siteDataEntity = siteDataEntity;
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

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getCurrentResult() {
		return currentResult;
	}

	public void setCurrentResult(String currentResult) {
		this.currentResult = currentResult;
	}


}
