package com.smart.rct.common.models;

import java.util.Date;

import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;

public class CustomerDetailsModel {
	
	private Integer id;
	private NetworkTypeDetailsEntity networkTypeDetailsEntity;
	private String programName;
	private String programDescription;
	private String status;
	private Date creationDate;
	private String createdBy;
	private CustomerEntity customerEntity;
	private Integer sourceProgramId;
	private String sourceprogramName;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public NetworkTypeDetailsEntity getNetworkTypeDetailsEntity() {
		return networkTypeDetailsEntity;
	}
	public void setNetworkTypeDetailsEntity(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		this.networkTypeDetailsEntity = networkTypeDetailsEntity;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramDescription() {
		return programDescription;
	}
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}
	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}
	public Integer getSourceProgramId() {
		return sourceProgramId;
	}
	public void setSourceProgramId(Integer sourceProgramId) {
		this.sourceProgramId = sourceProgramId;
	}
	public String getSourceprogramName() {
		return sourceprogramName;
	}
	public void setSourceprogramName(String sourceprogramName) {
		this.sourceprogramName = sourceprogramName;
	}
	
}
