package com.smart.rct.premigration.models;

import javax.persistence.Column;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;

public class NeMappingModel {
	private Integer id;
	private String enbId;
	private String searchStartDate;
	private String searchEndDate;
	private CustomerDetailsEntity programDetailsEntity;
	private NetworkConfigEntity networkConfigEntity;
	private String siteConfigType;
	private String creationDate;
	private String enbSbIp;
	private String enbSbVlan;
	private String btsIp;
	private String enbOamIp;
	private String enbVlanId;
	private String btsId;
	private String bsmIp;
	private String ciqName;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEnbId() {
		return enbId;
	}
	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}
	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}
	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	public NetworkConfigEntity getNetworkConfigEntity() {
		return networkConfigEntity;
	}
	public void setNetworkConfigEntity(NetworkConfigEntity networkConfigEntity) {
		this.networkConfigEntity = networkConfigEntity;
	}
	public String getSearchStartDate() {
		return searchStartDate;
	}
	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}
	public String getSearchEndDate() {
		return searchEndDate;
	}
	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
	}
	public String getSiteConfigType() {
		return siteConfigType;
	}
	public void setSiteConfigType(String siteConfigType) {
		this.siteConfigType = siteConfigType;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getBtsIp() {
		return btsIp;
	}
	public void setBtsIp(String btsIp) {
		this.btsIp = btsIp;
	}
	public String getEnbOamIp() {
		return enbOamIp;
	}
	public String getEnbSbIp() {
		return enbSbIp;
	}
	public void setEnbSbIp(String enbSbIp) {
		this.enbSbIp = enbSbIp;
	}
	public String getEnbSbVlan() {
		return enbSbVlan;
	}
	public void setEnbSbVlan(String enbSbVlan) {
		this.enbSbVlan = enbSbVlan;
	}
	public void setEnbOamIp(String enbOamIp) {
		this.enbOamIp = enbOamIp;
	}
	public String getEnbVlanId() {
		return enbVlanId;
	}
	public void setEnbVlanId(String enbVlanId) {
		this.enbVlanId = enbVlanId;
	}
	public String getBtsId() {
		return btsId;
	}
	public void setBtsId(String btsId) {
		this.btsId = btsId;
	}
	public String getBsmIp() {
		return bsmIp;
	}
	public void setBsmIp(String bsmIp) {
		this.bsmIp = bsmIp;
	}
	public String getCiqName() {
		return ciqName;
	}
	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

}
