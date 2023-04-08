package com.smart.rct.common.models;

import java.util.LinkedHashSet;

import com.smart.rct.common.entity.CustomerDetailsEntity;

public class FetchOVResponseModel {
	private String programName;
	private LinkedHashSet<String> market;
	private LinkedHashSet<String> neidList;
	private String commissionDate;
	private String workPlanId;
	private String trackerId;
	private String orderNo;
	private CustomerDetailsEntity customerDetailsEntity;
	private String status;
	private String fetchJson;
	private String siteName;
	
	//dummy IP
	private String integrationType;
	
	public String getIntegrationType() {
		return integrationType;
	}
	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	
	public LinkedHashSet<String> getMarket() {
		return market;
	}
	public void setMarket(LinkedHashSet<String> market) {
		this.market = market;
	}
	public LinkedHashSet<String> getNeidList() {
		return neidList;
	}
	public void setNeidList(LinkedHashSet<String> neidList) {
		this.neidList = neidList;
	}
	public String getCommissionDate() {
		System.out.println("Comission Date in FetchOVResponseModel "+commissionDate);
		return commissionDate;
	}
	public void setCommissionDate(String commissionDate) {
		this.commissionDate = commissionDate;
	}
	public String getWorkPlanId() {
		return workPlanId;
	}
	public void setWorkPlanId(String workPlanId) {
		this.workPlanId = workPlanId;
	}
	public String getTrackerId() {
		return trackerId;
	}
	public void setTrackerId(String trackerId) {
		this.trackerId = trackerId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}
	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFetchJson() {
		return fetchJson;
	}
	public void setFetchJson(String fetchJson) {
		this.fetchJson = fetchJson;
	}
	
	
	
}
