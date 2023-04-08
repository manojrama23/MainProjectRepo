package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_DETAIL_CONSTANTS")
public class AuditDetailConstantsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "BANDWIDTH")
	private String bandWidth;
	
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@Column(name = "BAND")
	private String band;
	
	@Column(name = "DIVERSITY")
	private String diversity;
	

	@Column(name = "POWER")
	private String power;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(String bandWidth) {
		this.bandWidth = bandWidth;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getDiversity() {
		return diversity;
	}

	public void setDiversity(String diversity) {
		this.diversity = diversity;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	
	
	
}
