package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "MIG_RUN_TEST_INPUT")
public class RunTestInputEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "RUN_TEST_ID")
	private Integer runTestID;
	@Column(name = "RUNTEST_INPUT_JSON", columnDefinition = "TEXT")
	private String runTestInputJson;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRunTestID() {
		return runTestID;
	}
	public void setRunTestID(Integer runTestID) {
		this.runTestID = runTestID;
	}
	public String getRunTestInputJson() {
		return runTestInputJson;
	}
	public void setRunTestInputJson(String runTestInputJson) {
		this.runTestInputJson = runTestInputJson;
	}
	
	
	
	

}
