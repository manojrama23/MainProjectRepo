package com.smart.rct.common.models;

import com.smart.rct.common.entity.LoginTypeEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ServerTypeEntity;



public class NetworkConfigDetailsModel {

	private Integer id;
	private Integer step;
	private ServerTypeEntity serverTypeEntity;
	private String serverName;
	private String serverIp;
	private String serverUserName;
	private String serverPassword;
	private LoginTypeEntity loginTypeEntity;
	private String createdBy;
	private String creationDate;
	private String path;
	private String userPrompt;
	private String superUserPrompt;
	private NetworkConfigEntity networkConfigEntity;
	
	
	public NetworkConfigEntity getNetworkConfigEntity() {
		return networkConfigEntity;
	}
	public void setNetworkConfigEntity(NetworkConfigEntity networkConfigEntity) {
		this.networkConfigEntity = networkConfigEntity;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getServerUserName() {
		return serverUserName;
	}
	public void setServerUserName(String serverUserName) {
		this.serverUserName = serverUserName;
	}
	public String getServerPassword() {
		return serverPassword;
	}
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ServerTypeEntity getServerTypeEntity() {
		return serverTypeEntity;
	}
	public void setServerTypeEntity(ServerTypeEntity serverTypeEntity) {
		this.serverTypeEntity = serverTypeEntity;
	}
	public LoginTypeEntity getLoginTypeEntity() {
		return loginTypeEntity;
	}
	public void setLoginTypeEntity(LoginTypeEntity loginTypeEntity) {
		this.loginTypeEntity = loginTypeEntity;
	}
	public String getUserPrompt() {
		return userPrompt;
	}
	public void setUserPrompt(String userPrompt) {
		this.userPrompt = userPrompt;
	}
	public String getSuperUserPrompt() {
		return superUserPrompt;
	}
	public void setSuperUserPrompt(String superUserPrompt) {
		this.superUserPrompt = superUserPrompt;
	}
	
}
