package com.smart.rct.common.models;

import java.util.List;

import javax.persistence.Column;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LoginTypeEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NeVersionEntity;

public class NetworkConfigModel{
	
	private Integer id;
	private CustomerDetailsEntity programDetailsEntity;
	private String neMarket;
	private NeTypeEntity neTypeEntity;
	private String neName;
	private NeVersionEntity neVersionEntity;
	private String neIp;
	private String neRsIp;
	private String neUserName;
	private String nePassword;
	private LoginTypeEntity loginTypeEntity;
	private String createdBy;
	private String creationDate;
	private String status;
	private String remarks;
	private List<NetworkConfigDetailsModel> neDetails;
	private String neVersion;
	private String neUserPrompt;
	private String neSuperUserPrompt;
	private String neRelVersion;
	
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

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}
	
	public NeVersionEntity getNeVersionEntity() {
		return neVersionEntity;
	}

	public void setNeVersionEntity(NeVersionEntity neVersionEntity) {
		this.neVersionEntity = neVersionEntity;
	}

	public String getNeIp() {
		return neIp;
	}

	public void setNeIp(String neIp) {
		this.neIp = neIp;
	}

	public String getNeUserName() {
		return neUserName;
	}

	public void setNeUserName(String neUserName) {
		this.neUserName = neUserName;
	}

	public String getNePassword() {
		return nePassword;
	}

	public void setNePassword(String nePassword) {
		this.nePassword = nePassword;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public NeTypeEntity getNeTypeEntity() {
		return neTypeEntity;
	}

	public void setNeTypeEntity(NeTypeEntity neTypeEntity) {
		this.neTypeEntity = neTypeEntity;
	}

	public LoginTypeEntity getLoginTypeEntity() {
		return loginTypeEntity;
	}

	public void setLoginTypeEntity(LoginTypeEntity loginTypeEntity) {
		this.loginTypeEntity = loginTypeEntity;
	}

	public List<NetworkConfigDetailsModel> getNeDetails() {
		return neDetails;
	}

	public void setNeDetails(List<NetworkConfigDetailsModel> neDetails) {
		this.neDetails = neDetails;
	}

	public String getNeMarket() {
		return neMarket;
	}

	public void setNeMarket(String neMarket) {
		this.neMarket = neMarket;
	}

	public String getNeRsIp() {
		return neRsIp;
	}

	public void setNeRsIp(String neRsIp) {
		this.neRsIp = neRsIp;
	}

	public String getNeVersion() {
		return neVersion;
	}

	public void setNeVersion(String neVersion) {
		this.neVersion = neVersion;
	}

	public String getNeUserPrompt() {
		return neUserPrompt;
	}

	public void setNeUserPrompt(String neUserPrompt) {
		this.neUserPrompt = neUserPrompt;
	}

	public String getNeSuperUserPrompt() {
		return neSuperUserPrompt;
	}

	public void setNeSuperUserPrompt(String neSuperUserPrompt) {
		this.neSuperUserPrompt = neSuperUserPrompt;
	}

	public String getNeRelVersion() {
		return neRelVersion;
	}

	public void setNeRelVersion(String neRelVersion) {
		this.neRelVersion = neRelVersion;
	}

}
