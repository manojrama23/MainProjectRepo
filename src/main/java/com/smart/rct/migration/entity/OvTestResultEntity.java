package com.smart.rct.migration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "OV_UPDATE_RUN_TEST_RESULT")
public class OvTestResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "RUN_TEST_ID", referencedColumnName = "ID", nullable = false)
	private RunTestEntity runTestEntity;
	
	@Column(name = "CURRENT_RESULT")
	private String currentResult;
	
	@Column(name = "MILE_STONE")
	private String milestone;
	
	@Column(name = "RSSI_CONTENT", columnDefinition="LONGTEXT")
	private String rssiContant;

	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
/*	@Column(name = "HTML_FILEPATH")
	private String htmlFilePath;
	
	public String getHtmlFilePath() {
		return htmlFilePath;
	}

	public void setHtmlFilePath(String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}*/

	public String getRssiContant() {
		return rssiContant;
	}

	public void setRssiContant(String rssiContant) {
		this.rssiContant = rssiContant;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public RunTestEntity getRunTestEntity() {
		return runTestEntity;
	}

	public void setRunTestEntity(RunTestEntity runTestEntity) {
		this.runTestEntity = runTestEntity;
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

	public String getMilestone() {
		return milestone;
	}

	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}
}
