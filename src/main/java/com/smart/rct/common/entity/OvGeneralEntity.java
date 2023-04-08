package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OV_TEMPLATE")
public class OvGeneralEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	
	
	@Column(name = "PARAMETER_NAME", nullable = false)
	private String label;
	
	@Column(name = "PARAMETER_VALUE", nullable = false,columnDefinition="LONGTEXT")
	private String value;
	
	@Column(name = "CONFIG_TYPE", nullable = false,columnDefinition="TEXT")
	private String configType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}
	
	
	
}
