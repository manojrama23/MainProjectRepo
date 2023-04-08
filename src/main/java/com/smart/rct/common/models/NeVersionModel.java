package com.smart.rct.common.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class NeVersionModel {

	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String neVersion;
	private String status;
	private String createdBy;
	private String creationDate;
	private String releaseVersion;
	
	
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
	public String getNeVersion() {
		return neVersion;
	}
	public void setNeVersion(String neVersion) {
		this.neVersion = neVersion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getReleaseVersion() {
		return releaseVersion;
	}
	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}
	
	
	
}
