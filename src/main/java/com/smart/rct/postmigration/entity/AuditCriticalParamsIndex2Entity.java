package com.smart.rct.postmigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX2")
public class AuditCriticalParamsIndex2Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String value) {
		this.unitId = value;
	}

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public String getTxPower() {
		return txPower;
	}

	public void setTxPower(String txPower) {
		this.txPower = txPower;
	}

	public String getRxPower() {
		return rxPower;
	}

	public void setRxPower(String rxPower) {
		this.rxPower = rxPower;
	}

	public String getConnectedDuCpriPortId() {
		return connectedDuCpriPortId;
	}

	public void setConnectedDuCpriPortId(String connectedDuCpriPortId) {
		this.connectedDuCpriPortId = connectedDuCpriPortId;
	}

	public String getConnectedEnbDigitalUnitBoardId() {
		return connectedEnbDigitalUnitBoardId;
	}

	public void setConnectedEnbDigitalUnitBoardId(String connectedEnbDigitalUnitBoardId) {
		this.connectedEnbDigitalUnitBoardId = connectedEnbDigitalUnitBoardId;
	}

	public String getConnectedEnbDigitalUnitPortId() {
		return connectedEnbDigitalUnitPortId;
	}

	public void setConnectedEnbDigitalUnitPortId(String connectedEnbDigitalUnitPortId) {
		this.connectedEnbDigitalUnitPortId = connectedEnbDigitalUnitPortId;
	}

	public String getEnbNeId() {
		return enbNeId;
	}

	public void setEnbNeId(String enbNeId) {
		this.enbNeId = enbNeId;
	}

	public String getDuCpriPortMode() {
		return duCpriPortMode;
	}

	public void setDuCpriPortMode(String duCpriPortMode) {
		this.duCpriPortMode = duCpriPortMode;
	}

	public String getMplaneIpv6() {
		return mplaneIpv6;
	}

	public void setMplaneIpv6(String mplaneIpv6) {
		this.mplaneIpv6 = mplaneIpv6;
	}

	public String getHardWareName() {
		return hardWareName;
	}

	public void setHardWareName(String hardWareName) {
		this.hardWareName = hardWareName;
	}

	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}

	@Column(name = "UNIT_TYPE", nullable = true)
	private String unitType;
	
	@Column(name = "UNIT_ID",  nullable = true)
	private String unitId;
	
	@Column(name = "PORT_ID", nullable = true)
	private String portId;
	
	@Column(name = "TX_POWER", nullable = true)
	private String txPower;
	
	@Column(name = "CONNECTED_DIGITAL_UNIT_BOARD_ID", nullable = true)
	private String connectedDigitalUnitBoardId;
	 
	
	@Column(name = "RADIO_UNIT_PORT_ID", nullable = true)
	private String radioUnitPortId;
	
	
	public String getConnectedDigitalUnitBoardId() {
		return connectedDigitalUnitBoardId;
	}

	public void setConnectedDigitalUnitBoardId(String connectedDigitalUnitBoardId) {
		this.connectedDigitalUnitBoardId = connectedDigitalUnitBoardId;
	}

	public String getRadioUnitPortId() {
		return radioUnitPortId;
	}

	public void setRadioUnitPortId(String radioUnitPortId) {
		this.radioUnitPortId = radioUnitPortId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
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

	public String getSoftwareName() {
		return softwareName;
	}

	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getCpriSpeedRunning() {
		return cpriSpeedRunning;
	}

	public void setCpriSpeedRunning(String cpriSpeedRunning) {
		this.cpriSpeedRunning = cpriSpeedRunning;
	}

	public String getTxWavelength() {
		return txWavelength;
	}

	public void setTxWavelength(String txWavelength) {
		this.txWavelength = txWavelength;
	}

	@Column(name = "VENDOR_NAME", nullable = true)
	private String vendorName;
	
	
	@Column(name = "FIRMWARE_NAME", nullable = true)
	private String firmwareName;
	
	
	@Column(name = "PACKAGE_VERSION", nullable = true)
	private String packageVersion;
	
	
	@Column(name = "PATCH_VERSION", nullable = true)
	private String patchVersion;
	
	
	@Column(name = "SOFTWARE_NAME", nullable = true)
	private String softwareName;
	
	
	@Column(name = "SOFTWARE_VERSION", nullable = true)
	private String softwareVersion;
	
	
	@Column(name = "CPRI_SPEED_RUNNING", nullable = true)
	private String cpriSpeedRunning;
	
	
	@Column(name = "TX_WAVELENGTH", nullable = true)
	private String txWavelength;

	@Column(name = "RX_POWER", nullable = true)
	private String rxPower;
	
	@Column(name = "CONNECTED_DU_CPRI_PORT_ID", nullable = true)
	private String connectedDuCpriPortId;
	
	@Column(name = "CONNECTED_ENB_DIGITAL_UNIT_BOARD_ID", nullable = true)
	private String connectedEnbDigitalUnitBoardId;
	
	@Column(name = "CONNECTED_ENB_DIGITAL_UNIT_PORT_ID", nullable = true)
	private String connectedEnbDigitalUnitPortId;
	
	@Column(name = "ENB_NE_ID", nullable = true)
	private String enbNeId;
	
	@Column(name = "DU_CPRI_PORT_MODE", nullable = true)
	private String duCpriPortMode;
	
	@Column(name = "MPLANE_IPV6", nullable = true)
	private String mplaneIpv6;
	
	@Column(name = "HARDWARE_NAME", nullable = true)
	private String hardWareName;
	
	@Column(name = "PRI_PORT_MODE", nullable = true)
	private String priPortMode;
	
	public String getPriPortMode() {
		return priPortMode;
	}

	public void setPriPortMode(String priPortMode) {
		this.priPortMode = priPortMode;
	}

	@Column(name = "AUDIT_STATUS", nullable = true)
	private String auditStatus;
	
	@Column(name = "AUDIT_RESULTS", nullable = true)
	private String auditResult;
	
	public String getAuditResult() {
		return auditResult;
	}

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@ManyToOne
	@JoinColumn(name = "INDEX_ID", referencedColumnName = "ID", nullable = false)
	private AuditCriticalParamsSummaryEntity auditCriticalParamEntity;
	
	
}
