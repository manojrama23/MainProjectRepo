package com.smart.rct.migration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;

@Entity
@Table(name = "MIG_UPLOADED_SCRIPT_DETAILS")
public class UploadFileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/*
	 * @ManyToOne //@JoinColumn(name = "NW_TYPE_DEATILS_ID", referencedColumnName =
	 * "ID", nullable = false)
	 * 
	 * @JoinColumn(name = "NW_TYPE_DEATILS_ID", referencedColumnName = "ID") private
	 * NetworkTypeDetailsEntity networkTypeDetailsEntity;
	 */
	
	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	public CustomerDetailsEntity customerDetailsEntity;

	@ManyToOne
	//@JoinColumn(name = "LSM_DETAILS_ID", referencedColumnName = "ID", nullable = false)
	@JoinColumn(name = "NE_LIST_ID", referencedColumnName = "ID")
	private NetworkConfigEntity neListEntity;
	
	@ManyToOne
	@JoinColumn(name = "NE_VERSION_ID",referencedColumnName = "ID")
	private NeVersionEntity neVersion;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "UPLOADED_BY")
	private String uploadedBy;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "USE_COUNT")
	private int useCount;

	@Column(name = "CUSTOMER_ID")
	private int customerId;

	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "PROGRAM")
	private String program;
	
	@Column(name = "MIGRATION_TYPE")
	private String migrationType;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "SUB_TYPE")
	private String subType;
	
	@Column(name = "SCRIPT_TYPE")
	private String scriptType;
	
	@Column(name = "CONNECTION_LOCATION")
	private String connectionLocation;
	
	@Column(name = "CONNECTION_LOCATION_USER_NAME")
	private String connectionLocationUserName;
	
	@Column(name = "CONNECTION_LOCATION_PWD")
	private String connectionLocationPwd;
	
	@Column(name = "CONNECTION_TERMINAL")
	private String connectionTerminal;
	
	@Column(name = "CONNECTION_TERMINAL_USER_NAME")
	private String connectionTerminalUserName;
	
	@Column(name = "CONNECTION_TERMINAL_PWD")
	private String connectionTerminalPwd;

	@Column(name = "PROMPT",nullable = false)
	private String prompt;
	
	@Column(name = "ARGUMENTS")
	private String arguments;
	
	@Column(name = "SUDO_PASSWORD")
	private String sudoPassword;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/*
	 * public NetworkTypeDetailsEntity getNetworkTypeDetailsEntity() { return
	 * networkTypeDetailsEntity; }
	 * 
	 * public void setNetworkTypeDetailsEntity(NetworkTypeDetailsEntity
	 * networkTypeDetailsEntity) { this.networkTypeDetailsEntity =
	 * networkTypeDetailsEntity; }
	 */

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public NetworkConfigEntity getNeListEntity() {
		return neListEntity;
	}

	public void setNeListEntity(NetworkConfigEntity neListEntity) {
		this.neListEntity = neListEntity;
	}
	
	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}

	public NeVersionEntity getNeVersion() {
		return neVersion;
	}

	public void setNeVersion(NeVersionEntity neVersion) {
		this.neVersion = neVersion;
	}
	
	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getConnectionLocation() {
		return connectionLocation;
	}

	public void setConnectionLocation(String connectionLocation) {
		this.connectionLocation = connectionLocation;
	}

	public String getConnectionLocationUserName() {
		return connectionLocationUserName;
	}

	public void setConnectionLocationUserName(String connectionLocationUserName) {
		this.connectionLocationUserName = connectionLocationUserName;
	}

	public String getConnectionLocationPwd() {
		return connectionLocationPwd;
	}

	public void setConnectionLocationPwd(String connectionLocationPwd) {
		this.connectionLocationPwd = connectionLocationPwd;
	}

	public String getConnectionTerminal() {
		return connectionTerminal;
	}

	public void setConnectionTerminal(String connectionTerminal) {
		this.connectionTerminal = connectionTerminal;
	}

	public String getConnectionTerminalUserName() {
		return connectionTerminalUserName;
	}

	public void setConnectionTerminalUserName(String connectionTerminalUserName) {
		this.connectionTerminalUserName = connectionTerminalUserName;
	}

	public String getConnectionTerminalPwd() {
		return connectionTerminalPwd;
	}

	public void setConnectionTerminalPwd(String connectionTerminalPwd) {
		this.connectionTerminalPwd = connectionTerminalPwd;
	}
	
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	
	public String getSudoPassword() {
		return sudoPassword;
	}

	public void setSudoPassword(String sudoPassword) {
		this.sudoPassword = sudoPassword;
	}

}
