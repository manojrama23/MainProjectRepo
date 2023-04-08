package com.smart.rct.common.models;

import java.util.List;

public class NeCommissionModel {

	private String label;
	private String backgroundColor;
	private List<String> data;
	private List<String> percData;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public List<String> getPercData() {
		return percData;
	}

	public void setPercData(List<String> percData) {
		this.percData = percData;
	}

}
