package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VZW_4G_CIQ_IP_PLAN")
public class IPPLANEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "MARKET", nullable = false)
	public String Market;
	
	@Column(name = "ENB_NAME")
	public String eNB_Name;
	@Column(name = "ENB_ID")
	public String eNB_ID;
	@Column(name = "VLAN")
	public String VLAN;
	@Column(name = "VLAN_ID")
	public String eNB_OAM_VLAN;
	@Column(name = "`ENB_OAM/S&B_VLAN_PREFIX`")
	public String vlanprefix;
	@Column(name = "`ENB_OAM_IP&eNB_S&B_IP`")
	public String oamIP;
	@Column(name = "`OAM_GATEWAY_IP/ENB_S&B_GATEWAY_IP`")
	public String oamGatewayIP;
	public String getMarket() {
		return Market;
	}

	public void setMarket(String market) {
		Market = market;
	}

	public String geteNB_Name() {
		return eNB_Name;
	}

	public void seteNB_Name(String eNB_Name) {
		this.eNB_Name = eNB_Name;
	}

	public String geteNB_ID() {
		return eNB_ID;
	}

	public void seteNB_ID(String eNB_ID) {
		this.eNB_ID = eNB_ID;
	}

	public String getVLAN() {
		return VLAN;
	}

	public void setVLAN(String vLAN) {
		VLAN = vLAN;
	}

	public String geteNB_OAM_VLAN() {
		return eNB_OAM_VLAN;
	}

	public void seteNB_OAM_VLAN(String eNB_OAM_VLAN) {
		this.eNB_OAM_VLAN = eNB_OAM_VLAN;
	}

	public String getVlanprefix() {
		return vlanprefix;
	}

	public void setVlanprefix(String vlanprefix) {
		this.vlanprefix = vlanprefix;
	}

	public String getOamIP() {
		return oamIP;
	}

	public void setOamIP(String oamIP) {
		this.oamIP = oamIP;
	}

	public String getOamGatewayIP() {
		return oamGatewayIP;
	}

	public void setOamGatewayIP(String oamGatewayIP) {
		this.oamGatewayIP = oamGatewayIP;
	}
	

}
