package com.smart.rct.migration.model;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;

public class UploadFileModel {
	private Integer id;

	private String lsmVersion;

	private String fileName;

	private String filePath;

	private String uploadedBy;

	private String remarks;

	private int useCount;

	private String nwType;

	private String lsmName;

	private int customerId;

	private String creationDate;
	
	private String program;

	private String migrationType;
	
	private String state;
	
	private NetworkConfigEntity neList; 
	
	private CustomerDetailsEntity customerDetailsEntity;
	
	private NeVersionEntity neVersion;

	private String subType;
	
	private String scriptType;
	
	private String connectionLocation;
	
	private String connectionLocationUserName;
	
	private String connectionLocationPwd;
	
	private String connectionTerminal;
	
	private String connectionTerminalUserName;
	
	private String connectionTerminalPwd;
	
	private Map<String, Object> connectionTerminalDetails;
	
	private String prompt;
	
	private String arguments;
	
	private String sudoPassword;

	public String getSudoPassword() {
		return sudoPassword;
	}

	public void setSudoPassword(String sudoPassword) {
		this.sudoPassword = sudoPassword;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public NetworkConfigEntity getNeList() {
		return neList;
	}

	public void setNeList(NetworkConfigEntity neList) {
		this.neList = neList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLsmVersion() {
		return lsmVersion;
	}

	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}

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

	public String getNwType() {
		return nwType;
	}

	public void setNwType(String nwType) {
		this.nwType = nwType;
	}

	public String getLsmName() {
		return lsmName;
	}

	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
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

	public Map<String, Object> getConnectionTerminalDetails() {
		return connectionTerminalDetails;
	}

	public void setConnectionTerminalDetails(Map<String, Object> connectionTerminalDetails) {
		this.connectionTerminalDetails = connectionTerminalDetails;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
}
