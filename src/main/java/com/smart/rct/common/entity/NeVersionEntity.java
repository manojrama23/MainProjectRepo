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

@Entity
@Table(name = "NE_VERSION_DETAILS")
public class NeVersionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	@Column(name = "NE_VERSION", nullable = false)
	private String neVersion;
	@Column(name = "STATUS", nullable = false)
	private String status;
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	@Column(name = "RELEASE_VERSION")
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
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getReleaseVersion() {
		return releaseVersion;
	}
	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}
	
	
	
	
}
