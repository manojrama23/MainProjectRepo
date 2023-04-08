package com.smart.rct.common.models;

import java.util.List;

public class CriticalCheckDetails {
	
	private String checkPerformed;
	private String title;
	private String remarks;
	private List<String> options;
	private List<String> mandatory;
	private String desc;
	
	
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCheckPerformed() {
		return checkPerformed;
	}
	public void setCheckPerformed(String checkPerformed) {
		this.checkPerformed = checkPerformed;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public List getOptions() {
		return options;
	}
	public void setOptions(List options) {
		this.options = options;
	}
	public List getMandatory() {
		return mandatory;
	}
	public void setMandatory(List options) {
		this.mandatory = options;
	}
	

}
