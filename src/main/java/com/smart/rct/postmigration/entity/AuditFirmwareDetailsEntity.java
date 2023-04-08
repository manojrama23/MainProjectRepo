package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_FIRMWARE_DETAILS")
public class AuditFirmwareDetailsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "FIRMWARE_NAME", nullable = false)
	private String firmwareName;
	
	@Column(name = "PACKAGE_VERSION")
	private String packageVersion;
	
	@Column(name = "PATCH_VERSION")
	private String patchVersion;
	
	@Column(name = "NE_TYPE", nullable = false)
	private String neType;
	
	@Column(name = "NE_VERSION")
	private String neVersion;

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

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public String getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getNeVersion() {
		return neVersion;
	}

	public void setNeVersion(String neVersion) {
		this.neVersion = neVersion;
	}
	
}
