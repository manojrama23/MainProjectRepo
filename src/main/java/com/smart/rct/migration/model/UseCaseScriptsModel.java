package com.smart.rct.migration.model;

import java.util.List;
import java.util.Map;

public class UseCaseScriptsModel {

	private String scriptId;

	private String scriptName;

	private String scriptRemarks;
	
	private String filePath;

	private List<CmdRuleModel> cmdRules;

	private List<FileRuleModel> fileRules;
	
	private List<XmlRuleModel> xmlRules;
	
	private List<ShellRuleModel> shellRules;

	private String scriptSequence;

	private Map<String, String> script;

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getScriptRemarks() {
		return scriptRemarks;
	}

	public void setScriptRemarks(String scriptRemarks) {
		this.scriptRemarks = scriptRemarks;
	}

	public List<CmdRuleModel> getCmdRules() {
		return cmdRules;
	}

	public void setCmdRules(List<CmdRuleModel> cmdRules) {
		this.cmdRules = cmdRules;
	}

	public List<FileRuleModel> getFileRules() {
		return fileRules;
	}

	public void setFileRules(List<FileRuleModel> fileRules) {
		this.fileRules = fileRules;
	}	

	public List<XmlRuleModel> getXmlRules() {
		return xmlRules;
	}

	public void setXmlRules(List<XmlRuleModel> xmlRules) {
		this.xmlRules = xmlRules;
	}

	public String getScriptSequence() {
		return scriptSequence;
	}

	public void setScriptSequence(String scriptSequence) {
		this.scriptSequence = scriptSequence;
	}

	public Map<String, String> getScript() {
		return script;
	}

	public void setScript(Map<String, String> script) {
		this.script = script;
	}
	
	public List<ShellRuleModel> getShellRules() {
		return shellRules;
	}

	public void setShellRules(List<ShellRuleModel> shellRules) {
		this.shellRules = shellRules;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
