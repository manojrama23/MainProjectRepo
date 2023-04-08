package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "NE_DETAILS")
public class NetworkConfigDetailsEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "STEP", nullable = false)
	private Integer step;
	
	@ManyToOne
    @JoinColumn(name = "SERVER_TYPE_ID", referencedColumnName = "ID", nullable = false)
	private ServerTypeEntity serverTypeEntity;
	
	@Column(name = "SERVER_NAME", nullable = false)
	private String serverName;
	
	@Column(name = "SERVER_IP", nullable = false)
	private String serverIp;
	
	@Column(name = "SERVER_USERNAME", nullable = false)
	private String serverUserName;
	
	@Column(name = "SERVER_PWD", nullable = false)
	private String serverPassword;
	
	@ManyToOne
    @JoinColumn(name = "LOGIN_TYPE_ID", referencedColumnName = "ID", nullable= false)
	private LoginTypeEntity loginTypeEntity;
	
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	@Column(name = "PATH", nullable = false)
	private String path;
	
	@Column(name = "PROMPT", nullable = false)
	private String userPrompt;
	
	@Column(name = "SU_PROMPT", nullable = false)
	private String superUserPrompt;

	@ManyToOne
    @JoinColumn(name = "NE_ID", referencedColumnName = "ID", nullable = false)
	@JsonIgnore 
	private NetworkConfigEntity networkConfigEntity;
	
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public NetworkConfigEntity getNetworkConfigEntity() {
		return networkConfigEntity;
	}

	public void setNetworkConfigEntity(NetworkConfigEntity networkConfigEntity) {
		this.networkConfigEntity = networkConfigEntity;
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

