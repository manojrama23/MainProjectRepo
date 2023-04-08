package com.smart.rct.premigration.serviceImpl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ENODEB_DETAILS")
public class EnodebDetails {
	@Id
	private String id;
	private String eNBId;
	private String eNBName;
	private String siteName;
	
	private String ciqMap;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String geteNBId() {
		return eNBId;
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

	
	public String getCiqMap() {
		return ciqMap;
	}

	public void setCiqMap(String ciqMap) {
		this.ciqMap = ciqMap;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

}
