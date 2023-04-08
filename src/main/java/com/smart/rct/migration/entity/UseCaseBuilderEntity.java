package com.smart.rct.migration.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;

@Entity
@Table(name = "MIG_USE_CASE_BUILDER")
public class UseCaseBuilderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USE_CASE_ID")
	private Integer id;

	/*@ManyToOne
	@JoinColumn(name = "NW_TYPE_ID", referencedColumnName = "ID", nullable = false)
	private NetworkTypeDetailsEntity networkTypeDetailsEntity;*/

	@ManyToOne
	@JoinColumn(name = "NE_LIST_ID", referencedColumnName = "ID")
	public NetworkConfigEntity networkConfigEntity;
	
	@ManyToOne
	@JoinColumn(name = "NE_VERSION_ID",referencedColumnName = "ID")
	private NeVersionEntity neVersion;

	@Column(name = "USE_CASE_NAME")
	private String useCaseName;
	@Column(name = "USE_CASE_REMARKS")
	private String remarks;
	@Column(name = "EXECUTION_SEQUENCE")
	private Integer ExecutionSequence;

	@Column(name = "USE_COUNT")
	private Integer useCount;
	
	@Column(name = "USE_CASE_CREATION_TIME")
	private Date useCaseCreationDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "useCaseBuilderEntity", cascade = CascadeType.ALL)
	private Set<UseCaseBuilderParamEntity> useCaseBuilderParamEntity;

	@Column(name = "CUSTOMER_ID")
	private Integer customerId;
	
	@Column(name = "MIGRATION_TYPE")
	private String migrationType;
	
	@Column(name = "SUB_TYPE")
	private String subType;
	
	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	public CustomerDetailsEntity customerDetailsEntity;
	
	@Column(name = "CIQ_FILE_NAME")
	private String ciqFileName;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/*public NetworkTypeDetailsEntity getNetworkTypeDetailsEntity() {
		return networkTypeDetailsEntity;
	}

	public void setNetworkTypeDetailsEntity(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		this.networkTypeDetailsEntity = networkTypeDetailsEntity;
	}*/	

	public NetworkConfigEntity getNetworkConfigEntity() {
		return networkConfigEntity;
	}

	public void setNetworkConfigEntity(NetworkConfigEntity networkConfigEntity) {
		this.networkConfigEntity = networkConfigEntity;
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

	public Integer getExecutionSequence() {
		return ExecutionSequence;
	}

	public void setExecutionSequence(Integer executionSequence) {
		ExecutionSequence = executionSequence;
	}

	public Set<UseCaseBuilderParamEntity> getUseCaseBuilderParamEntity() {
		return useCaseBuilderParamEntity;
	}

	public void setUseCaseBuilderParamEntity(Set<UseCaseBuilderParamEntity> useCaseBuilderParamEntity) {
		this.useCaseBuilderParamEntity = useCaseBuilderParamEntity;
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

	public Date getUseCaseCreationDate() {
		return useCaseCreationDate;
	}

	public void setUseCaseCreationDate(Date useCaseCreationDate) {
		this.useCaseCreationDate = useCaseCreationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(String migrationType) {
		this.migrationType = migrationType;
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

	public String getCiqFileName() {
		return ciqFileName;
	}

	public void setCiqFileName(String ciqFileName) {
		this.ciqFileName = ciqFileName;
	}
	
	

}
