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
import com.smart.rct.common.entity.CustomerEntity;

@Entity
@Table(name = "ENB_PRE_GROW_AUDIT")
public class EnbPreGrowAuditEntity {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "CIQ_FILENAME", nullable = false)
	private String ciqFileName;
	
	@Column(name = "NE_NAME", nullable = false)
	private String neName;
	
	@Column(name = "GROWING_NAME", nullable = false)
	private String growingName;
	
	@Column(name = "SM_VERSION", nullable = false)
	private String smVersion;
	
	@Column(name = "SM_NAME", nullable = false)
	private String smName;
	
	@Column(name = "CSV_FILENAME", nullable = false)
	private String csvFileName;
	
	@Column(name = "USECASE_NAME", nullable = false)
	private String usecaseName;
	
	@Column(name = "REMARKS", nullable = false)
	private String remarks;

	@Column(name = "STATUS", nullable = false)
	private String status;
	
	@Column(name = "GROWING_DATE", nullable = false)
	private Date growingDate;
	
	@Column(name = "GROW_PERFORMED_BY", nullable = false)
	private String growPerformedBy;
	
	
	
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID", nullable = false)
	private CustomerEntity  customerEntity;
	
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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


	public String getGrowingName() {
		return growingName;
	}


	public void setGrowingName(String growingName) {
		this.growingName = growingName;
	}


	public String getSmVersion() {
		return smVersion;
	}


	public void setSmVersion(String smVersion) {
		this.smVersion = smVersion;
	}


	public String getSmName() {
		return smName;
	}


	public void setSmName(String smName) {
		this.smName = smName;
	}


	public String getCsvFileName() {
		return csvFileName;
	}


	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}


	public String getUsecaseName() {
		return usecaseName;
	}


	public void setUsecaseName(String usecaseName) {
		this.usecaseName = usecaseName;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getGrowingDate() {
		return growingDate;
	}


	public void setGrowingDate(Date growingDate) {
		this.growingDate = growingDate;
	}


	public String getGrowPerformedBy() {
		return growPerformedBy;
	}


	public void setGrowPerformedBy(String growPerformedBy) {
		this.growPerformedBy = growPerformedBy;
	}


	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}


	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}


	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}


	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	

}
