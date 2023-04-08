package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MME_IP_INFO")
public class MmeIpEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "MARKET", nullable = false)
	private String market;
	
	@Column(name = "MMEIP", nullable = false)
	private String mmeIp;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getMmeIp() {
		return mmeIp;
	}
	public void setMmeIp(String mmeIp) {
		this.mmeIp = mmeIp;
	}
	
	

}
