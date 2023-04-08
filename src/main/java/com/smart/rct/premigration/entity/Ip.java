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

@Entity
@Table(name = "IP")
public class Ip {
	
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "IP", nullable = false)
	public String ip;
	
	@Column(name = "MARKET_ID", nullable = false)
	public String marketid;

	@Column(name = "PORT", nullable = false)
	public String port;
	
	@Column(name = "MARKET_NAME", nullable = false)
	public String marketName;
	
	@Column(name = "CSL_SERVER_IPV6", nullable = false)
	public String cslServerIpv6;
	
	@Column(name = "CSL_PORT_NUM", nullable = false)
	public String cslPortNum;
	
	@Column(name = "SECOND_CSL_SERVER_IPV6", nullable = false)
	public String secondCslServerIpv6;
	
	@Column(name = "THIRD_CSL_SERVER_IPV6", nullable = false)
	public String thirdCslServerIpv6;
	
	@Column(name = "SECOND_CSL_PORT_NUM", nullable = false)
	public String secondCslPortNum;

	@Column(name = "PREFIX", nullable = false)
	public String prefix;
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Integer getId() {
		return id;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getCslServerIpv6() {
		return cslServerIpv6;
	}

	public void setCslServerIpv6(String cslServerIpv6) {
		this.cslServerIpv6 = cslServerIpv6;
	}

	public String getCslPortNum() {
		return cslPortNum;
	}

	public void setCslPortNum(String cslPortNum) {
		this.cslPortNum = cslPortNum;
	}

	public String getSecondCslServerIpv6() {
		return secondCslServerIpv6;
	}

	public void setSecondCslServerIpv6(String secondCslServerIpv6) {
		this.secondCslServerIpv6 = secondCslServerIpv6;
	}

	public String getSecondCslPortNum() {
		return secondCslPortNum;
	}

	public void setSecondCslPortNum(String secondCslPortNum) {
		this.secondCslPortNum = secondCslPortNum;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMarketid() {
		return marketid;
	}

	public void setMarketid(String marketid) {
		this.marketid = marketid;
	}

	public String getThirdCslServerIpv6() {
		return thirdCslServerIpv6;
	}

	public void setThirdCslServerIpv6(String thirdCslServerIpv6) {
		this.thirdCslServerIpv6 = thirdCslServerIpv6;
	}
	
}