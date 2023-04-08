package com.smart.rct.migration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MIG_USE_CASE_XML_RULE")
public class UseCaseXmlRuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "XML_RULE_ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "MIG_XML_RULE_ID", referencedColumnName = "ID", nullable = false)
	private XmlRuleBuilderEntity xmlRuleBuilderEntity;

	@ManyToOne
	@JoinColumn(name = "SCRIPTS_ID", referencedColumnName = "SCRIPTS_ID", nullable = false)
	private UseCaseBuilderParamEntity useCaseBuilderScriptsEntity;

	@Column(name = "EXECUTION_OCCURENCE")
	private Integer xmlRuleSequence;

	@Column(name = "REMARKS")
	private String xmlRemarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity() {
		return xmlRuleBuilderEntity;
	}

	public void setXmlRuleBuilderEntity(XmlRuleBuilderEntity xmlRuleBuilderEntity) {
		this.xmlRuleBuilderEntity = xmlRuleBuilderEntity;
	}

	public UseCaseBuilderParamEntity getUseCaseBuilderScriptsEntity() {
		return useCaseBuilderScriptsEntity;
	}

	public void setUseCaseBuilderScriptsEntity(UseCaseBuilderParamEntity useCaseBuilderScriptsEntity) {
		this.useCaseBuilderScriptsEntity = useCaseBuilderScriptsEntity;
	}

	public Integer getXmlRuleSequence() {
		return xmlRuleSequence;
	}

	public void setXmlRuleSequence(Integer xmlRuleSequence) {
		this.xmlRuleSequence = xmlRuleSequence;
	}

	public String getXmlRemarks() {
		return xmlRemarks;
	}

	public void setXmlRemarks(String xmlRemarks) {
		this.xmlRemarks = xmlRemarks;
	}	

}
