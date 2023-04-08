package com.smart.rct.common.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER_LIST")
public class CustomerEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	@Column(name = "ICON_PATH")
	private String iconPath;
	@Column(name = "STATUS")
	private String status;

	@Column(name = "CUSTOMER_SHORT_NAME")
	private String customerShortName;

	@OneToMany(targetEntity = CustomerDetailsEntity.class, mappedBy = "customerEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<CustomerDetailsEntity> customerDetails;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<CustomerDetailsEntity> getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(List<CustomerDetailsEntity> customerDetails) {
		this.customerDetails = customerDetails;
	}

	public String getCustomerShortName() {
		return customerShortName;
	}

	public void setCustomerShortName(String customerShortName) {
		this.customerShortName = customerShortName;
	}

}
