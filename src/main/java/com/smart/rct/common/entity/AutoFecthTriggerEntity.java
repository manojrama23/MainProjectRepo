package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "AUTO_FETCH_TRIGGER_DETAILS")
public class AutoFecthTriggerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;
	@Column(name = "SCHEDULED_HOURS")
	private String scheduledHours;
	
	@Column(name = "SCHEDULED_DATE")
	private String scheduledDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}
	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}
	public String getScheduledHours() {
		return scheduledHours;
	}
	public void setScheduledHours(String scheduledHours) {
		this.scheduledHours = scheduledHours;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	
	
	
	
	
}
