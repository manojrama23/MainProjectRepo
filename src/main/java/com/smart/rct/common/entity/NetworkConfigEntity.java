package com.smart.rct.common.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NE_LIST")
public class NetworkConfigEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "NE_MARKET")
	private String neMarket;
	
	@ManyToOne
    @JoinColumn(name = "NE_TYPE_ID", referencedColumnName = "ID", nullable= false)
	private NeTypeEntity neTypeEntity;
	
	@Column(name = "NE_NAME", nullable = false)
	private String neName;
	
	@ManyToOne
    @JoinColumn(name = "NE_VERSION_ID", referencedColumnName = "ID")
	private NeVersionEntity neVersionEntity;
	
	@Column(name = "NE_IP", nullable = false)
	private String neIp;
	
	@Column(name = "NE_RS_IP")
	private String neRsIp;
	
	@Column(name = "NE_REL_VERSION")
	private String neRelVersion;
	
	public String getNeRelVersion() {
		return neRelVersion;
	}

	public void setNeRelVersion(String neRelVersion) {
		this.neRelVersion = neRelVersion;
	}

	@Column(name = "NE_USERNAME", nullable = false)
	private String neUserName;
	
	@Column(name = "NE_PWD", nullable = false)
	private String nePassword;
	
	@ManyToOne
    @JoinColumn(name = "LOGIN_TYPE_ID", referencedColumnName = "ID", nullable= false)
	private LoginTypeEntity loginTypeEntity;
	
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	@Column(name = "STATUS", nullable = false)
	private String status;
	
	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "PROMPT", nullable = false)
	private String neUserPrompt;
	
	@Column(name = "SU_PROMPT", nullable = false)
	private String neSuperUserPrompt;
	
	@OneToMany(targetEntity = NetworkConfigDetailsEntity.class, mappedBy = "networkConfigEntity", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<NetworkConfigDetailsEntity> neDetails;
	
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
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

	public List<NetworkConfigDetailsEntity> getNeDetails() {
		return neDetails;
	}

	public void setNeDetails(List<NetworkConfigDetailsEntity> neDetails) {
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
	
}
