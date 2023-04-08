package com.smart.rct.common.models;

import java.util.List;

public class MarketModelDetails {
	
	List<String> market=null;
	List<String> region=null;
	List<String> feRegion=null;
	List<String> feNight=null;
	List<String> feDay=null;

	public List<String> getFeNight() {
		return feNight;
	}

	public void setFeNight(List<String> feNight) {
		this.feNight = feNight;
	}

	public List<String> getFeDay() {
		return feDay;
	}

	public void setFeDay(List<String> feDay) {
		this.feDay = feDay;
	}

	public List<String> getRegion() {
		return region;
	}

	public void setRegion(List<String> region) {
		this.region = region;
	}

	public List<String> getFeRegion() {
		return feRegion;
	}

	public void setFeRegion(List<String> feRegion) {
		this.feRegion = feRegion;
	}

	public List<String> getMarket() {
		return market;
	}

	public void setMarket(List<String> market) {
		this.market = market;
	}
	
	

}
