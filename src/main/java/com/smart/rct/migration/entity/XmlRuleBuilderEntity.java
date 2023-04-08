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

@Entity
@Table(name = "MIG_XML_RULE_BUILDER")
public class XmlRuleBuilderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "RULE_NAME")
	private String ruleName;

	@Column(name = "ROOT_NAME")
	private String rootName;

	@Column(name = "SUB_ROOT_NAME")
	private String subRootName;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "MIGRATION_TYPE")
	private String migrationType;

	@Column(name = "SUB_TYPE")
	private String subType;

	@Column(name = "USE_COUNT")
	private int useCount;
	
	@Column(name = "CUSTOMER_ID")
	private Integer customerId;	
	
	@Column(name = "LOOP_TYPE")
	private String loopType;	
	
	@Column(name = "CMD_NAME")
	private String cmdName;
	
	@Column(name = "PROMPT",nullable = false)
	private String prompt;
	
	@Column(name = "STATUS")
	private String status;

	@ManyToOne
	@JoinColumn(name = "PROGRAM_ID", referencedColumnName = "ID", nullable = false)
	private CustomerDetailsEntity customerDetailsEntity;

	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "xmlRuleBuilderEntity", cascade = CascadeType.ALL)
	private Set<XmlRootEntity> xmlRootEntitySet;

	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "xmlRuleBuilderEntity", cascade = CascadeType.ALL)
	private Set<XmlElementEntity> xmlElementEntitySet;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public String getSubRootName() {
		return subRootName;
	}

	public void setSubRootName(String subRootName) {
		this.subRootName = subRootName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public Set<XmlRootEntity> getXmlRootEntitySet() {
		return xmlRootEntitySet;
	}

	public void setXmlRootEntitySet(Set<XmlRootEntity> xmlRootEntitySet) {
		this.xmlRootEntitySet = xmlRootEntitySet;
	}

	public Set<XmlElementEntity> getXmlElementEntitySet() {
		return xmlElementEntitySet;
	}

	public void setXmlElementEntitySet(Set<XmlElementEntity> xmlElementEntitySet) {
		this.xmlElementEntitySet = xmlElementEntitySet;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	
	public String getLoopType() {
		return loopType;
	}

	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}
	
	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
