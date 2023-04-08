package com.smart.rct.premigration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smart.rct.common.entity.CustomerDetailsEntity;

@Entity
@Table(name = "NE_CONFIG_TYPE_LIST")
public class NeConfigTypeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID",nullable = false)
	private Integer id;

	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID")
	private CustomerDetailsEntity programDetailsEntity;
	
	
	@Column(name = "NE_CONFIG_TYPE")
	private String nwConfigType ;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}


	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}


	public String getNwConfigType() {
		return nwConfigType;
	}


	public void setNwConfigType(String nwConfigType) {
		this.nwConfigType = nwConfigType;
	}
	
	
	
	
}
