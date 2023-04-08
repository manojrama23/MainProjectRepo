package com.smart.rct.migration.model;

import java.util.Map;

public class ShellRuleModel {

	private String shellRuleId;
	private String shellRuleSequence;
	private String shellRuleRemarks;
	private Map<String, String> shellDetails;

	public String getShellRuleId() {
		return shellRuleId;
	}

	public void setShellRuleId(String shellRuleId) {
		this.shellRuleId = shellRuleId;
	}

	public String getShellRuleSequence() {
		return shellRuleSequence;
	}

	public void setShellRuleSequence(String shellRuleSequence) {
		this.shellRuleSequence = shellRuleSequence;
	}

	public String getShellRuleRemarks() {
		return shellRuleRemarks;
	}

	public void setShellRuleRemarks(String shellRuleRemarks) {
		this.shellRuleRemarks = shellRuleRemarks;
	}

	public Map<String, String> getShellDetails() {
		return shellDetails;
	}

	public void setShellDetails(Map<String, String> shellDetails) {
		this.shellDetails = shellDetails;
	}

	

}
