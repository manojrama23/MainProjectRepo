package com.smart.rct.premigration.models;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;

public class EnbPreGrowAuditModel {
	private Integer id;
	private String growingName;
	private String csvFileName;
	private String ciqFileName;
	private String neName;
	private Integer smId;
	private String smName;
	private String smVersion;
	private String description;
	private String growingDate;
	private String growPerformedBy;
	private String useCaseName;
	private Integer useCaseId;
	private String customerId;
	private String customerName;
	private String status;
	private String csvFilePath;
	private String searchStartDate;
	private String searchEndDate;
	private CustomerDetailsEntity programDetailsEntity;
	private UseCaseBuilderEntity useCaseBuilderEntity;
	private LsmEntity lsmEntity;
	private NeTypeEntity neTypeEntity;
	private NeVersionEntity neVersionEntity;
	private String remarks;



	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGrowingName() {
		return growingName;
	}
	public void setGrowingName(String growingName) {
		this.growingName = growingName;
	}
	public String getCsvFileName() {
		return csvFileName;
	}
	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}
	public Integer getSmId() {
		return smId;
	}
	public void setSmId(Integer smId) {
		this.smId = smId;
	}
	public String getSmName() {
		return smName;
	}
	public void setSmName(String smName) {
		this.smName = smName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGrowingDate() {
		return growingDate;
	}
	public void setGrowingDate(String growingDate) {
		this.growingDate = growingDate;
	}
	public String getGrowPerformedBy() {
		return growPerformedBy;
	}
	public void setGrowPerformedBy(String growPerformedBy) {
		this.growPerformedBy = growPerformedBy;
	}
	public String getUseCaseName() {
		return useCaseName;
	}
	public void setUseCaseName(String useCaseName) {
		this.useCaseName = useCaseName;
	}
	public Integer getUseCaseId() {
		return useCaseId;
	}
	public void setUseCaseId(Integer useCaseId) {
		this.useCaseId = useCaseId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCsvFilePath() {
		return csvFilePath;
	}
	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}
	public String getSearchStartDate() {
		return searchStartDate;
	}
	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}
	public String getSearchEndDate() {
		return searchEndDate;
	}
	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
	}
	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}
	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}
	public String getCiqFileName() {
		return ciqFileName;
	}
	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getSmVersion() {
		return smVersion;
	}
	public void setSmVersion(String smVersion) {
		this.smVersion = smVersion;
	}
	public UseCaseBuilderEntity getUseCaseBuilderEntity() {
		return useCaseBuilderEntity;
	}
	public void setUseCaseBuilderEntity(UseCaseBuilderEntity useCaseBuilderEntity) {
		this.useCaseBuilderEntity = useCaseBuilderEntity;
	}
	public LsmEntity getLsmEntity() {
		return lsmEntity;
	}
	public void setLsmEntity(LsmEntity lsmEntity) {
		this.lsmEntity = lsmEntity;
	}
	public NeTypeEntity getNeTypeEntity() {
		return neTypeEntity;
	}
	public void setNeTypeEntity(NeTypeEntity neTypeEntity) {
		this.neTypeEntity = neTypeEntity;
	}
	public NeVersionEntity getNeVersionEntity() {
		return neVersionEntity;
	}
	public void setNeVersionEntity(NeVersionEntity neVersionEntity) {
		this.neVersionEntity = neVersionEntity;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	
	
	

}
