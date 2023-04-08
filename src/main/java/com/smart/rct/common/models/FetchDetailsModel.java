package com.smart.rct.common.models;

import java.util.List;

public class FetchDetailsModel {
	
	private Integer id;
	private String sessionId;
	private long serviceToken;
	private Integer ciqNetworkConfigId;
	
	private Integer scriptNetworkConfigId;
	private Boolean allowDuplicate;
	private String remarks;
	private List<String> market;
	private List<String> rfScriptList;
	private Boolean activate;
	private String fileSourceType;
	private String customerName;
	private Integer customerId;
	private Integer programId;
	private String programName;
	private List<String> completedMarkets;
	private String checkList;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getServiceToken() {
		return serviceToken;
	}
	public void setServiceToken(long serviceToken) {
		this.serviceToken = serviceToken;
	}
	public Integer getCiqNetworkConfigId() {
		return ciqNetworkConfigId;
	}
	public void setCiqNetworkConfigId(Integer ciqNetworkConfigId) {
		this.ciqNetworkConfigId = ciqNetworkConfigId;
	}
	public Integer getScriptNetworkConfigId() {
		return scriptNetworkConfigId;
	}
	public void setScriptNetworkConfigId(Integer scriptNetworkConfigId) {
		this.scriptNetworkConfigId = scriptNetworkConfigId;
	}
	public Boolean getAllowDuplicate() {
		return allowDuplicate;
	}
	public void setAllowDuplicate(Boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public List<String> getMarket() {
		return market;
	}
	public void setMarket(List<String> market) {
		this.market = market;
	}
	public List<String> getRfScriptList() {
		return rfScriptList;
	}
	public void setRfScriptList(List<String> rfScriptList) {
		this.rfScriptList = rfScriptList;
	}
	public Boolean getActivate() {
		return activate;
	}
	public void setActivate(Boolean activate) {
		this.activate = activate;
	}
	public String getFileSourceType() {
		return fileSourceType;
	}
	public void setFileSourceType(String fileSourceType) {
		this.fileSourceType = fileSourceType;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getProgramId() {
		return programId;
	}
	public void setProgramId(Integer programId) {
		this.programId = programId;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public List<String> getCompletedMarkets() {
		return completedMarkets;
	}
	public void setCompletedMarkets(List<String> completedMarkets) {
		this.completedMarkets = completedMarkets;
	}
	public String getCheckList() {
		return checkList;
	}
	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}
	
	

}
