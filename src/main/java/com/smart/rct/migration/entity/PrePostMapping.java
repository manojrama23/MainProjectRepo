package com.smart.rct.migration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "PRE_POST_MAPPING")
public class PrePostMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "NE_ID")
	private String neId;

	@Column(name = "OUTPUT", columnDefinition = "LONGTEXT")
	private String outPut;
	
	@Column(name = "PROGRAMID")
	private int programId;

	public String getOutPut() {
		return outPut;
	}

	public void setOutPut(String outPut) {
		this.outPut = outPut;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	

}
