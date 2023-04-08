package com.smart.rct.common.models;

import java.util.List;

public class NetworkVersionModel {

	private Integer id;
	private String networkType;
	private List<String> lsmVersionList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public List<String> getLsmVersionList() {
		return lsmVersionList;
	}
	public void setLsmVersionList(List<String> lsmVersionList) {
		this.lsmVersionList = lsmVersionList;
	}
	
	

}
