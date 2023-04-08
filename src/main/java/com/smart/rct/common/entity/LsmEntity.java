package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LSM_DETAILS")
public class LsmEntity implements Comparable<LsmEntity> {
	private Integer id;
	private String lsmName;
	private String lsmIp;
	private String lsmVersion;
	private String createdBy;
	private String lsmUserName;
	private String lsmPassword;
	private Date creationDate;
	private String status;
	private String remarks;
	private String programName;
	private String neType;
	private String bucket;

	private NetworkTypeDetailsEntity networkTypeDetailsEntity;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "LSM_NAME", nullable = false)
	public String getLsmName() {
		return lsmName;
	}

	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}

	@Column(name = "LSM_IP", nullable = false)
	public String getLsmIp() {
		return lsmIp;
	}

	public void setLsmIp(String lsmIp) {
		this.lsmIp = lsmIp;
	}

	@Column(name = "CREATED_BY", nullable = false)
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name = "LSM_USERNAME", nullable = false)
	public String getLsmUserName() {
		return lsmUserName;
	}

	public void setLsmUserName(String lsmUserName) {
		this.lsmUserName = lsmUserName;
	}

	@Column(name = "LSM_PWD", nullable = false)
	public String getLsmPassword() {
		return lsmPassword;
	}

	public void setLsmPassword(String lsmPassword) {
		this.lsmPassword = lsmPassword;
	}

	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "STATUS", nullable = false)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "LSM_VERSION", nullable = false)
	public String getLsmVersion() {
		return lsmVersion;
	}

	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}

	@ManyToOne
	@JoinColumn(name = "NW_TYPE_ID", referencedColumnName = "ID", nullable = false)
	public NetworkTypeDetailsEntity getNetworkTypeDetailsEntity() {
		return networkTypeDetailsEntity;
	}

	public void setNetworkTypeDetailsEntity(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		this.networkTypeDetailsEntity = networkTypeDetailsEntity;
	}

	@Override
	public int compareTo(LsmEntity e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Column(name = "PROGRAME_NAME", nullable = false)
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	@Column(name = "NE_TYPE", nullable = false)
	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	@Column(name = "BUCKET", nullable = false)
	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

}
