package com.smart.rct.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CUSTOMER_DETAILS", uniqueConstraints = {@UniqueConstraint(columnNames = { "PROGRAM_NAME"}, name = "UK_CUSTOMER_DETAIL_PROGRAM_NAMES") })
public class CustomerDetailsEntity implements Serializable {

	private static final long serialVersionUID = 4938504160507741754L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NW_TYPE_ID", referencedColumnName = "ID", nullable = false)
	private NetworkTypeDetailsEntity networkTypeDetailsEntity;
	@Column(name = "PROGRAM_NAME")
	private String programName;
	@Column(name = "PROGRAM_DESCRIPTION")
	private String programDescription;
	@Column(name = "STATUS")
	private String status;
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID", nullable = false)
	@JsonIgnore
	private CustomerEntity customerEntity;
	@Column(name = "SOURCE_PROGRAM_ID")
	private Integer sourceProgramId;
	@Column(name = "SOURCE_PROGRAM_NAME")
	private String sourceprogramName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public NetworkTypeDetailsEntity getNetworkTypeDetailsEntity() {
		return networkTypeDetailsEntity;
	}

	public void setNetworkTypeDetailsEntity(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		this.networkTypeDetailsEntity = networkTypeDetailsEntity;
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
