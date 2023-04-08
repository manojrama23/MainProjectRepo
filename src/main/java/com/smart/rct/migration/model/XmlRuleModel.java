package com.smart.rct.migration.model;

import java.util.Map;

public class XmlRuleModel {

	private String xmlId;
	private String xmlSequence;
	private String xmlRemarks;
	private Map<String, String> xmlDetails;

	public String getXmlId() {
		return xmlId;
	}

	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}

	public String getXmlSequence() {
		return xmlSequence;
	}

	public void setXmlSequence(String xmlSequence) {
		this.xmlSequence = xmlSequence;
	}

	public String getXmlRemarks() {
		return xmlRemarks;
	}

	public void setXmlRemarks(String xmlRemarks) {
		this.xmlRemarks = xmlRemarks;
	}

	public Map<String, String> getXmlDetails() {
		return xmlDetails;
	}

	public void setXmlDetails(Map<String, String> xmlDetails) {
		this.xmlDetails = xmlDetails;
	}

}
