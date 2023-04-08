package com.smart.rct.premigration.entity;

import java.util.Date;

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
@Table(name = "CHECKLIST_SCRIPT_DETAILS")
public class CheckListScriptDetEntity {
	
	@Id
	@Column(name = "ID",nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "CHECKLIST_FILE_NAME",nullable = false)
	private String checkListFileName;
	
	@Column(name = "SHEET_NAME",nullable = false)
	private String sheetName;
	
	@Column(name = "CONFIG_TYPE",nullable = false)
	private String configType;
	
	@Column(name = "STEP_INDEX", nullable = false)
	private Integer stepIndex;
	
	@Column(name = "SCRIPT_NAME",nullable = false)
	private String scriptName;
	
	@Column(name = "SCRIPT_SEQUENCE")
	private int scriptExeSeq;
	
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

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

	public String getCheckListFileName() {
		return checkListFileName;
	}

	public void setCheckListFileName(String checkListFileName) {
		this.checkListFileName = checkListFileName;
	}

	public Integer getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(Integer stepIndex) {
		this.stepIndex = stepIndex;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	
	public int getScriptExeSeq() {
		return scriptExeSeq;
	}

	public void setScriptExeSeq(int scriptExeSeq) {
		this.scriptExeSeq = scriptExeSeq;
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

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}
	
}
