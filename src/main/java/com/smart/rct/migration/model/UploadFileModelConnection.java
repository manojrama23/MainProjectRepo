package com.smart.rct.migration.model;

public class UploadFileModelConnection {
	
	private String terminalName;
	
	private String termUsername;
	
	private String termPassword;
	
	private String prompt;


	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String getTermUsername() {
		return termUsername;
	}

	public void setTermUsername(String termUsername) {
		this.termUsername = termUsername;
	}

	public String getTermPassword() {
		return termPassword;
	}

	public void setTermPassword(String termPassword) {
		this.termPassword = termPassword;
	}

}
