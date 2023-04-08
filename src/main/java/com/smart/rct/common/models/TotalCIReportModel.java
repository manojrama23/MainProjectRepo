package com.smart.rct.common.models;

import java.util.List;

public class TotalCIReportModel {
	private String name;
	private long migrtedSiteCount;
	private long totalCount;
	private float percenatgeOfMigrated;
	private long remaining;
	List<MarketModel> objSiteList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getMigrtedSiteCount() {
		return migrtedSiteCount;
	}
	public void setMigrtedSiteCount(long migrtedSiteCount) {
		this.migrtedSiteCount = migrtedSiteCount;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public float getPercenatgeOfMigrated() {
		return percenatgeOfMigrated;
	}
	public void setPercenatgeOfMigrated(float percenatgeOfMigrated) {
		this.percenatgeOfMigrated = percenatgeOfMigrated;
	}
	public List<MarketModel> getObjSiteList() {
		return objSiteList;
	}
	public void setObjSiteList(List<MarketModel> objSiteList) {
		this.objSiteList = objSiteList;
	}
	public long getRemaining() {
		return remaining;
	}
	public void setRemaining(long remaining) {
		this.remaining = remaining;
	}
	
	
	
	
	
}
