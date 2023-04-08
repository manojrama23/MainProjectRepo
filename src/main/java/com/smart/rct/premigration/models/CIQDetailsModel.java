package com.smart.rct.premigration.models;

import java.util.LinkedHashMap;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.smart.rct.common.models.CiqMapValuesModel;

@Document(collection = "CIQ_DETAILS")
public class CIQDetailsModel {
	@Id
	private Integer id;
	private String fileName;
	private String eNBId;
	private String eNBName;
	private String sheetName;
	private String seqOrder;
	private String subSheetName;
	private String subSheetAliasName;
	private String sheetAliasName;
	private Integer sheetId;
	private String siteName;

	// private String subSheetName;;
	private LinkedHashMap<String, CiqMapValuesModel> ciqMap = new LinkedHashMap<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/*public LinkedHashMap<String, String> getCiqMap() {
		return ciqMap;
	}

	public void setCiqMap(LinkedHashMap<String, String> ciqMap) {
		this.ciqMap = ciqMap;
	}*/
	
	

	public String geteNBId() {
		return eNBId;
	}

	public LinkedHashMap<String, CiqMapValuesModel> getCiqMap() {
		return ciqMap;
	}

	public void setCiqMap(LinkedHashMap<String, CiqMapValuesModel> ciqMap) {
		this.ciqMap = ciqMap;
	}

	public void seteNBId(String eNBId) {
		this.eNBId = eNBId;
	}

	public String geteNBName() {
		return eNBName;
	}

	public void seteNBName(String eNBName) {
		this.eNBName = eNBName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getSeqOrder() {
		return seqOrder;
	}

	public void setSeqOrder(String seqOrder) {
		this.seqOrder = seqOrder;
	}

	public String getSubSheetName() {
		return subSheetName;
	}

	public void setSubSheetName(String subSheetName) {
		this.subSheetName = subSheetName;
	}

	public String getSubSheetAliasName() {
		return subSheetAliasName;
	}

	public void setSubSheetAliasName(String subSheetAliasName) {
		this.subSheetAliasName = subSheetAliasName;
	}

	public String getSheetAliasName() {
		return sheetAliasName;
	}

	public void setSheetAliasName(String sheetAliasName) {
		this.sheetAliasName = sheetAliasName;
	}

	public Integer getSheetId() {
		return sheetId;
	}

	public void setSheetId(Integer sheetId) {
		this.sheetId = sheetId;
	}
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	
	
	
	
	
	
}
