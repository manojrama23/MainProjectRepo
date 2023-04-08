package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RRH_ALU_INFO")
public class RrhAluEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "NEW_MODEL", nullable = false)
	private String newModel;
	
	@Column(name = "OLD_MODEL", nullable = false)
	private String oldModel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNewModel() {
		return newModel;
	}

	public void setNewModel(String newModel) {
		this.newModel = newModel;
	}

	public String getOldModel() {
		return oldModel;
	}

	public void setOldModel(String oldModel) {
		this.oldModel = oldModel;
	}
	
	

}
