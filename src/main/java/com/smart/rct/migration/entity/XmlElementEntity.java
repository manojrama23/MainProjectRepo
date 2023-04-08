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
@Table(name = "MIG_XML_ELEMENT")
public class XmlElementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "ELEMENT_NAME")
	private String elementName;
	
	@Column(name = "OPERATOR")
	private String operator;

	@Column(name = "ELEMENT_VALUE")
	private String elementValue;

	@ManyToOne
	@JoinColumn(name = "XML_RULE_ID", referencedColumnName = "ID", nullable = false)
	private XmlRuleBuilderEntity xmlRuleBuilderEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getElementValue() {
		return elementValue;
	}

	public void setElementValue(String elementValue) {
		this.elementValue = elementValue;
	}

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity() {
		return xmlRuleBuilderEntity;
	}

	public void setXmlRuleBuilderEntity(XmlRuleBuilderEntity xmlRuleBuilderEntity) {
		this.xmlRuleBuilderEntity = xmlRuleBuilderEntity;
	}
	
}
