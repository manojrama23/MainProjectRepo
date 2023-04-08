package com.smart.rct.premigration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;

@Entity
@Table(name = "NE_MAPPING")
public class NeMappingEntity {

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID")
	private CustomerDetailsEntity programDetailsEntity;

	@Column(name = "ENB_ID")
	private String enbId;
	
	@Column(name = "SITE_NAME")
	private String siteName;
	
	@ManyToOne
    @JoinColumn(name = "NETWORK_CONFIG_ID", referencedColumnName = "ID")
	private NetworkConfigEntity networkConfigEntity;
	
	@Column(name = "SITE_CONFIG_TYPE")
	private String siteConfigType;
	
	@Column(name = "ENB_SB_IP")
	private String enbSbIp;
	
	@Column(name = "ENB_SB_VLAN")
	private String enbSbVlan;
	
	@Column(name = "BTS_IP")
	private String btsIp;
	
	@Column(name = "ENB_OAM_IP")
	private String enbOamIp;
	
	@Column(name = "ENB_VLAN_ID")
	private String enbVlanId;
	
	@Column(name = "BTS_ID")
	private String btsId;
	
	@Column(name = "BSM_IP")
	private String bsmIp;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "CIQ_NAME")
	private String ciqName;

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

	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}

	public NetworkConfigEntity getNetworkConfigEntity() {
		return networkConfigEntity;
	}

	public void setNetworkConfigEntity(NetworkConfigEntity networkConfigEntity) {
		this.networkConfigEntity = networkConfigEntity;
	}

	public String getSiteConfigType() {
		return siteConfigType;
	}

	public void setSiteConfigType(String siteConfigType) {
		this.siteConfigType = siteConfigType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public String getBtsIp() {
		return btsIp;
	}

	public void setBtsIp(String btsIp) {
		this.btsIp = btsIp;
	}

	public String getEnbOamIp() {
		return enbOamIp;
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
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}
	
	

}