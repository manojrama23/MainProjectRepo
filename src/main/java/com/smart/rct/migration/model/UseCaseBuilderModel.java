package com.smart.rct.migration.model;

import java.util.List;

import com.smart.rct.common.entity.NeVersionEntity;

public class UseCaseBuilderModel {

	private String id;
	private String nwType;
	private String lsmName;
	private String lsmVersion;
	private String useCaseName;
	private String remarks;
	private String executionSequence;
	private Integer useCount;
	private Integer customerId;
	private String migrationType;
	private String createdBy;
	private String timeStamp;
	
	private String ciqFileName;
	
	private NeVersionEntity neVersion;

	private List<UseCaseScriptsModel> scriptList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLsmVersion() {
		return lsmVersion;
	}

	public void setLsmVersion(String lsmVersion) {
		this.lsmVersion = lsmVersion;
	}

	public String getUseCaseName() {
		return useCaseName;
	}

	public void setUseCaseName(String useCaseName) {
		this.useCaseName = useCaseName;
	}	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getExecutionSequence() {
		return executionSequence;
	}

	public void setExecutionSequence(String executionSequence) {
		this.executionSequence = executionSequence;
	}

	public List<UseCaseScriptsModel> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<UseCaseScriptsModel> scriptList) {
		this.scriptList = scriptList;
	}

	public Integer getUseCount() {
		return useCount;
	}

	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
	}	

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}	
	
	public NeVersionEntity getNeVersion() {
		return neVersion;
	}

	public void setNeVersion(NeVersionEntity neVersion) {
		this.neVersion = neVersion;
		
	}

	public String getCiqFileName() {
		return ciqFileName;
	}

	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
	}
	
	
	

}
