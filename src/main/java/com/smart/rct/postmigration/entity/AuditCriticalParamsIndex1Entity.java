package com.smart.rct.postmigration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.smart.rct.migration.entity.RunTestEntity;

@Entity
@Table(name = "AUDIT_CRITICAL_PARAMS_INDEX1")
public class AuditCriticalParamsIndex1Entity {

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

	public Integer getCellNum() {
		return cellNum;
	}

	public void setCellNum(Integer cellNum) {
		this.cellNum = cellNum;
	}

	public Integer getCellIdentity() {
		return cellIdentity;
	}

	public void setCellIdentity(Integer cellIdentity) {
		this.cellIdentity = cellIdentity;
	}

	public Integer getInternalCellNum() {
		return internalCellNum;
	}

	public void setInternalCellNum(Integer internalCellNum) {
		this.internalCellNum = internalCellNum;
	}

	public String getAdministrativeState() {
		return administrativeState;
	}

	public void setAdministrativeState(String administrativeState) {
		this.administrativeState = administrativeState;
	}

	public String getOperationalState() {
		return operationalState;
	}

	public void setOperationalState(String operationalState) {
		this.operationalState = operationalState;
	}

	public String getCellSetupState() {
		return cellSetupState;
	}

	public void setCellSetupState(String cellSetupState) {
		this.cellSetupState = cellSetupState;
	}

	public String getS1ApState() {
		return s1ApState;
	}

	public void setS1ApState(String s1ApState) {
		this.s1ApState = s1ApState;
	}

	public String getDlAntennaCount() {
		return dlAntennaCount;
	}

	public void setDlAntennaCount(String dlAntennaCount) {
		this.dlAntennaCount = dlAntennaCount;
	}

	public String getUlAntennaCount() {
		return ulAntennaCount;
	}

	public void setUlAntennaCount(String ulAntennaCount) {
		this.ulAntennaCount = ulAntennaCount;
	}

	public String getNumberOfRxPathsPerRU() {
		return numberOfRxPathsPerRU;
	}

	public void setNumberOfRxPathsPerRU(String numberOfRxPathsPerRU) {
		this.numberOfRxPathsPerRU = numberOfRxPathsPerRU;
	}

	public String getCellPathType() {
		return cellPathType;
	}

	public void setCellPathType(String cellPathType) {
		this.cellPathType = cellPathType;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getSpectrumSharing() {
		return spectrumSharing;
	}

	public void setSpectrumSharing(String spectrumSharing) {
		this.spectrumSharing = spectrumSharing;
	}

	public String getSlotLevelOperationalMode() {
		return slotLevelOperationalMode;
	}

	public void setSlotLevelOperationalMode(String slotLevelOperationalMode) {
		this.slotLevelOperationalMode = slotLevelOperationalMode;
	}

	public String getDssTargetLTECellNumber() {
		return dssTargetLTECellNumber;
	}

	public void setDssTargetLTECellNumber(String dssTargetLTECellNumber) {
		this.dssTargetLTECellNumber = dssTargetLTECellNumber;
	}

	public AuditCriticalParamsSummaryEntity getAuditCriticalParamEntity() {
		return auditCriticalParamEntity;
	}

	public void setAuditCriticalParamEntity(AuditCriticalParamsSummaryEntity auditCriticalParamEntity) {
		this.auditCriticalParamEntity = auditCriticalParamEntity;
	}

	@Column(name = "CELL_NUM", nullable = true)
	private Integer cellNum;
	
	@Column(name = "CELL_IDENTITY", nullable = true)
	private Integer cellIdentity;
	
	@Column(name = "INTERNAL_CELL_NUMBER", nullable = true)
	private Integer internalCellNum;
	
	@Column(name = "ADMINISTRATIVE_STATE",  nullable = true)
	private String administrativeState;
	
	@Column(name = "OPERATIONAL_STATE", nullable = true)
	private String operationalState;
	
	@Column(name = "ACTIVATION_STATE", nullable = true)
	private String activationState;
	
	public String getActivationState() {
		return activationState;
	}

	public void setActivationState(String activationState) {
		this.activationState = activationState;
	}

	@Column(name = "CELL_SETUP_STATE", nullable = true)
	private String cellSetupState;
	
	
	@Column(name = "S1_AP_STATE", nullable = true)
	private String s1ApState;
	
	@Column(name = "DL_ANTENNA_COUNT", nullable = true)
	private String dlAntennaCount;
	
	@Column(name = "UL_ANTENNA_COUNT", nullable = true)
	private String ulAntennaCount;
	
	@Column(name = "NUMBER_OF_RX_PATHS_PER_RU", nullable = true)
	private String numberOfRxPathsPerRU;
	
	@Column(name = "CELL_PATH_TYPE", nullable = true)
	private String cellPathType;
	
	@Column(name = "POWER", nullable = true)
	private String power;
	
	@Column(name = "SPECTRUM_SHARING", nullable = true)
	private String spectrumSharing;
	
	@Column(name = "SLOT_LEVEL_OPERATIONAL_MODE", nullable = true)
	private String slotLevelOperationalMode;
	
	@Column(name = "DSS_TARGET_LTE_CELL_NUMBER", nullable = true)
	private String dssTargetLTECellNumber;
	
	@Column(name = "AUDIT_RESULT", nullable = true)
	private String auditResult;
	
	public String getAuditResult() {
		return auditResult;
	}

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	@Column(name = "AUDIT_STATUS", nullable = true)
	private String auditStatus;
	
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
