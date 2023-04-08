package com.smart.rct.migration.entity;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.smart.rct.common.entity.CustomerDetailsEntity;

@Entity
@Table(name = "MIG_USE_CASE_BUILDER_SCRIPTS", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "EXECUTION_SEQUENCE", "USE_CASE_ID"}, name = "UK_MIG_USE_CASE_BUILDER_SCRIPTS") })
public class UseCaseBuilderParamEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCRIPTS_ID")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "SCRIPT_DETAILS_ID", referencedColumnName = "ID")
	private UploadFileEntity scriptsDetails;
	
	@Column(name = "EXECUTION_SEQUENCE")
	private Integer executionSequence;

	@ManyToOne
	@JoinColumn(name = "USE_CASE_ID", referencedColumnName = "USE_CASE_ID", nullable = false)
	private UseCaseBuilderEntity useCaseBuilderEntity;
	
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "useCaseBuilderParamEntity", cascade = CascadeType.ALL)
	private Set<UseCaseCmdRuleEntity> useCaseCmdRuleEntitySet;
	
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "useCaseBuilderParamEntity", cascade = CascadeType.ALL)
	private Set<UseCaseShellRuleEntity> useCaseShellRuleEntitySet;
	
	
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "useCaseBuilderScriptsEntity", cascade = CascadeType.ALL)
	private Set<UseCaseFileRuleEntity> useCaseFileRuleEntitySet;
	
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "useCaseBuilderScriptsEntity", cascade = CascadeType.ALL)
	private Set<UseCaseXmlRuleEntity> useCaseXmlRuleEntitySet;
	
	@Column(name = "REMARKS")
	private String scriptRemarks;
	
	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	public CustomerDetailsEntity customerDetailsEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Set<UseCaseShellRuleEntity> getUseCaseShellRuleEntitySet() {
		return useCaseShellRuleEntitySet;
	}

	public void setUseCaseShellRuleEntitySet(Set<UseCaseShellRuleEntity> useCaseShellRuleEntitySet) {
		this.useCaseShellRuleEntitySet = useCaseShellRuleEntitySet;
	}
	
	public UploadFileEntity getScriptsDetails() {
		return scriptsDetails;
	}

	public void setScriptsDetails(UploadFileEntity scriptsDetails) {
		this.scriptsDetails = scriptsDetails;
	}

	public Integer getExecutionSequence() {
		return executionSequence;
	}

	public void setExecutionSequence(Integer executionSequence) {
		this.executionSequence = executionSequence;
	}

	public UseCaseBuilderEntity getUseCaseBuilderEntity() {
		return useCaseBuilderEntity;
	}

	public void setUseCaseBuilderEntity(UseCaseBuilderEntity useCaseBuilderEntity) {
		this.useCaseBuilderEntity = useCaseBuilderEntity;
	}

	public Set<UseCaseCmdRuleEntity> getUseCaseCmdRuleEntitySet() {
		return useCaseCmdRuleEntitySet;
	}

	public void setUseCaseCmdRuleEntitySet(Set<UseCaseCmdRuleEntity> useCaseCmdRuleEntitySet) {
		this.useCaseCmdRuleEntitySet = useCaseCmdRuleEntitySet;
	}

	public Set<UseCaseFileRuleEntity> getUseCaseFileRuleEntitySet() {
		return useCaseFileRuleEntitySet;
	}

	public void setUseCaseFileRuleEntitySet(Set<UseCaseFileRuleEntity> useCaseFileRuleEntitySet) {
		this.useCaseFileRuleEntitySet = useCaseFileRuleEntitySet;
	}

	public Set<UseCaseXmlRuleEntity> getUseCaseXmlRuleEntitySet() {
		return useCaseXmlRuleEntitySet;
	}

	public void setUseCaseXmlRuleEntitySet(Set<UseCaseXmlRuleEntity> useCaseXmlRuleEntitySet) {
		this.useCaseXmlRuleEntitySet = useCaseXmlRuleEntitySet;
	}

	public String getScriptRemarks() {
		return scriptRemarks;
	}

	public void setScriptRemarks(String scriptRemarks) {
		this.scriptRemarks = scriptRemarks;
	}
	
	public CustomerDetailsEntity getCustomerDetailsEntity() {
		return customerDetailsEntity;
	}

	public void setCustomerDetailsEntity(CustomerDetailsEntity customerDetailsEntity) {
		this.customerDetailsEntity = customerDetailsEntity;
	}

}
