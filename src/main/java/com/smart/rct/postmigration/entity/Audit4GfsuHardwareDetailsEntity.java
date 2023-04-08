package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_4G_FSU_HARDWARE_DETAILS")
public class Audit4GfsuHardwareDetailsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "HARDWARE_NAME", nullable = false)
	private String hardwareName;
	
	@Column(name = "VENDOR_NAME")
	private String vendorName;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "WAVELENGTH")
	private String waveLength;
	
	@Column(name = "RX_PASS_MAX_POWER")
	private String failRxPowerUL;
	
	@Column(name = "RX_PASS_MIN_POWER")
	private String failRxPowerLL;
	
	@Column(name = "RX_WARNING_MAX_POWER")
	private String warningRxPowerUL;
	
	@Column(name = "RX_WARNING_MIN_POWER")
	private String warningRxPowerLL;
	
	@Column(name = "TX_MAX_POWER")
	private String tXPowerUL;
	
	@Column(name = "TX_MIN_POWER")
	private String tXPowerLL;
	
	@Column(name = "INTERFACE")
	private String interFace;
	
	@Column(name = "ATTENUATOR_REQ_OVER_MAXLIMIT")
	private String attenuatorReq;
     
	public String getAttenuatorReq() {
		return attenuatorReq;
	}

	public void setAttenuatorReq(String attenuatorReq) {
		this.attenuatorReq = attenuatorReq;
	}

	public String getInterFace() {
		return interFace;
	}

	public void setInterFace(String interFace) {
		this.interFace = interFace;
	}

	public String gettXPowerUL() {
		return tXPowerUL;
	}

	public void settXPowerUL(String tXPowerUL) {
		this.tXPowerUL = tXPowerUL;
	}

	public String gettXPowerLL() {
		return tXPowerLL;
	}

	public void settXPowerLL(String tXPowerLL) {
		this.tXPowerLL = tXPowerLL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWaveLength() {
		return waveLength;
	}

	public void setWaveLength(String waveLength) {
		this.waveLength = waveLength;
	}

	public String getFailRxPowerUL() {
		return failRxPowerUL;
	}

	public void setFailRxPowerUL(String failRxPowerUL) {
		this.failRxPowerUL = failRxPowerUL;
	}

	public String getFailRxPowerLL() {
		return failRxPowerLL;
	}

	public void setFailRxPowerLL(String failRxPowerLL) {
		this.failRxPowerLL = failRxPowerLL;
	}

	public String getWarningRxPowerUL() {
		return warningRxPowerUL;
	}

	public void setWarningRxPowerUL(String warningRxPowerUL) {
		this.warningRxPowerUL = warningRxPowerUL;
	}

	public String getWarningRxPowerLL() {
		return warningRxPowerLL;
	}

	public void setWarningRxPowerLL(String warningRxPowerLL) {
		this.warningRxPowerLL = warningRxPowerLL;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHardwareName() {
		return hardwareName;
	}

	public void setHardwareName(String hardwareName) {
		this.hardwareName = hardwareName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}	
}