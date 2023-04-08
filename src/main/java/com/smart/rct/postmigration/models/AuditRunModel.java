package com.smart.rct.postmigration.models;

import java.util.Date;
import java.util.Map;

public class AuditRunModel {
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	private String runId;
	private Date creationDate;

	private Map<String, String> runTestParams;

	

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public Map<String, String> getRunTestParams() {
		return runTestParams;
	}

	public void setRunTestParams(Map<String, String> runTestParams) {
		this.runTestParams = runTestParams;
	}
}
