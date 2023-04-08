package com.smart.rct.migration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "4G_RET_TEST")
public class RetTestEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "NE_ID")
	private String neId;
	
	@Column(name = "RET_NAME")
	private String retName;
	
	@Column(name = "RemoteCellID")
	private String remoteCellID;
	
	@Column(name = "SECTOR_ID")
	private String sectorId;
	
	@Column(name = "ANTENNA_POSITION")
	private String antennaPosition;
	
	@Column(name = "Mount_Type")
	private String mountType;
	
	@Column(name = "BAND")
	private String band;
	
	@Column(name = "ANTENNA_MODEL")
	private String antennaModel;
	
	@Column(name = "RET_SERIAL_NUMBER")
	private String retSerialNumber;
	
	@Column(name = "ANTENNA_AISG_RF_PORT_Number")
	private String AntennaAisgRFPortNumber;
	
	@Column(name = "ELECTRICAL_TILT")
	private String electricalTilt;
	
	@Column(name = "RRH_SERIAL_NUMBER")
	private String rrhSerialNumber;
	
	@Column(name = "Diplexer_Present")
	private String diplexerPresent;
	
	@Column(name = "Power_Feeding_Switch")
	private String powerFeedingSwitch;

	@Column(name = "ANTENNA_SERIAL_NUMBER")
	private String antennaSerialNumber;
	
	@Column(name = "COMMENTS")
	private String comments;
	
	@Column(name = "File_Name")
	private String retFileName;
	
	@Column(name = "Time_Stamp")
	private String timeStamp;
	
	@Column(name = "UserName")
	private String userName;
	
	@Column(name = "UniqueId")
	private String uniqueId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getRetName() {
		return retName;
	}

	public void setRetName(String retName) {
		this.retName = retName;
	}

	public String getRemoteCellID() {
		return remoteCellID;
	}

	public void setRemoteCellID(String remoteCellID) {
		this.remoteCellID = remoteCellID;
	}

	public String getSectorId() {
		return sectorId;
	}

	public void setSectorId(String sectorId) {
		this.sectorId = sectorId;
	}

	public String getAntennaPosition() {
		return antennaPosition;
	}

	public void setAntennaPosition(String antennaPosition) {
		this.antennaPosition = antennaPosition;
	}

	public String getMountType() {
		return mountType;
	}

	public void setMountType(String mountType) {
		this.mountType = mountType;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getAntennaModel() {
		return antennaModel;
	}

	public void setAntennaModel(String antennaModel) {
		this.antennaModel = antennaModel;
	}

	public String getRetSerialNumber() {
		return retSerialNumber;
	}

	public void setRetSerialNumber(String retSerialNumber) {
		this.retSerialNumber = retSerialNumber;
	}

	public String getAntennaAisgRFPortNumber() {
		return AntennaAisgRFPortNumber;
	}

	public void setAntennaAisgRFPortNumber(String antennaAisgRFPortNumber) {
		AntennaAisgRFPortNumber = antennaAisgRFPortNumber;
	}

	public String getElectricalTilt() {
		return electricalTilt;
	}

	public void setElectricalTilt(String electricalTilt) {
		this.electricalTilt = electricalTilt;
	}

	public String getRrhSerialNumber() {
		return rrhSerialNumber;
	}

	public void setRrhSerialNumber(String rrhSerialNumber) {
		this.rrhSerialNumber = rrhSerialNumber;
	}

	public String getDiplexerPresent() {
		return diplexerPresent;
	}

	public void setDiplexerPresent(String diplexerPresent) {
		this.diplexerPresent = diplexerPresent;
	}

	public String getPowerFeedingSwitch() {
		return powerFeedingSwitch;
	}

	public void setPowerFeedingSwitch(String powerFeedingSwitch) {
		this.powerFeedingSwitch = powerFeedingSwitch;
	}

	public String getAntennaSerialNumber() {
		return antennaSerialNumber;
	}

	public void setAntennaSerialNumber(String antennaSerialNumber) {
		this.antennaSerialNumber = antennaSerialNumber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRetFileName() {
		return retFileName;
	}

	public void setRetFileName(String retFileName) {
		this.retFileName = retFileName;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
}