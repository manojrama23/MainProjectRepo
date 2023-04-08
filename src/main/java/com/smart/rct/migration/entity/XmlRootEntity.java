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
@Table(name = "MIG_XML_ROOT")
public class XmlRootEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "ROOT_KEY")
	private String rootKey;

	@Column(name = "ROOT_VALUE")
	private String rootValue;

	@ManyToOne
	@JoinColumn(name = "XML_RULE_ID", referencedColumnName = "ID", nullable = false)
	private XmlRuleBuilderEntity xmlRuleBuilderEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}	

	public String getRootKey() {
		return rootKey;
	}

	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}

	public String getRootValue() {
		return rootValue;
	}

	public void setRootValue(String rootValue) {
		this.rootValue = rootValue;
	}

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity() {
		return xmlRuleBuilderEntity;
	}

	public void setXmlRuleBuilderEntity(XmlRuleBuilderEntity xmlRuleBuilderEntity) {
		this.xmlRuleBuilderEntity = xmlRuleBuilderEntity;
	}

}
