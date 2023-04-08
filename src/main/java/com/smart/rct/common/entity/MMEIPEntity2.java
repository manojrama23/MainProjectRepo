package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VZW_4G_CIQ_MME")
public class MMEIPEntity2 {

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

	@Column(name = "`Market Prefix`", nullable = false)
	public String Market_Prefix;
	
	
	@Column(name = "`MME IP`")
	public String MME_IP;
	
	@Column(name = "`MME_INDEX`")
	public String MME_INDEX;
	
	
	public String getMME_INDEX() {
		return MME_INDEX;
	}

	public void setMME_INDEX(String mME_INDEX) {
		MME_INDEX = mME_INDEX;
	}

	public String getMarket_Prefix() {
		return Market_Prefix;
	}

	public void setMarket_Prefix(String market_Prefix) {
		Market_Prefix = market_Prefix;
	}

	public String getMME_IP() {
		return MME_IP;
	}

	public void setMME_IP(String mME_IP) {
		MME_IP = mME_IP;
	}

	

	
	
	
}
