package com.smart.rct.migration.model;

import java.util.Map;

public class CmdRuleModel {

	private String cmdId;
	private String cmdSequence;
	private String cmdRemarks;
	private Map<String, String> cmdDetails;

	public String getCmdId() {
		return cmdId;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public String getCmdSequence() {
		return cmdSequence;
	}

	public void setCmdSequence(String cmdSequence) {
		this.cmdSequence = cmdSequence;
	}

	public String getCmdRemarks() {
		return cmdRemarks;
	}

	public void setCmdRemarks(String cmdRemarks) {
		this.cmdRemarks = cmdRemarks;
	}

	public Map<String, String> getCmdDetails() {
		return cmdDetails;
	}

	public void setCmdDetails(Map<String, String> cmdDetails) {
		this.cmdDetails = cmdDetails;
	}

}
