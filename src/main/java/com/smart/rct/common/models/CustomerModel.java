package com.smart.rct.common.models;

import java.util.List;

public class CustomerModel {
	
	
	
	private Integer id;
	private Integer customerId;
	private String customerName;
	private String iconPath;
	private String status;
	private String customerShortName;
	private List<CustomerDetailsModel> customerDetails;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<CustomerDetailsModel> getCustomerDetails() {
		return customerDetails;
	}
	public void setCustomerDetails(List<CustomerDetailsModel> customerDetails) {
		this.customerDetails = customerDetails;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public String getCustomerShortName() {
		return customerShortName;
	}
	public void setCustomerShortName(String customerShortName) {
		this.customerShortName = customerShortName;
	}
	
}
