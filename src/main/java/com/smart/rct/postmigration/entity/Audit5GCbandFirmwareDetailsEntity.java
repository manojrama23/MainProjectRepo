package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_5G_CBAND_FIRMWARE_DETAILS")
public class Audit5GCbandFirmwareDetailsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "FIRMWARE_NAME", nullable = false)
	private String firmwareName;
	
	@Column(name = "BUILD_VERSION")
	private String buildVersion;
	
	@Column(name = "NE_VERSION")
	private String neVersion;
	
	@Column(name = "RELEASE_VERSION")
	private String relVersion;
	
	@Column(name = "PRODUCT_CODE")
	private String prodCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirmwareName() {
		return firmwareName;
	}

	public void setFirmwareName(String firmwareName) {
		this.firmwareName = firmwareName;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getNeVersion() {
		return neVersion;
	}

	public void setNeVersion(String neVersion) {
		this.neVersion = neVersion;
	}

	public String getRelVersion() {
		return relVersion;
	}

	public void setRelVersion(String relVersion) {
		this.relVersion = relVersion;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	
}
