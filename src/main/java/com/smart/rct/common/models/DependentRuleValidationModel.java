package com.smart.rct.common.models;

import java.util.Map;

public class DependentRuleValidationModel {

	public String condition;
	public String outputFormate;
	public String results;
	public String multipleColunms;
	public String multipleBy;
	public Map<String,String> statWithEnbMap;
	

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getOutputFormate() {
		return outputFormate;
	}

	public void setOutputFormate(String outputFormate) {
		this.outputFormate = outputFormate;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public String getMultipleColunms() {
		return multipleColunms;
	}

	public void setMultipleColunms(String multipleColunms) {
		this.multipleColunms = multipleColunms;
	}

	public String getMultipleBy() {
		return multipleBy;
	}

	public void setMultipleBy(String multipleBy) {
		this.multipleBy = multipleBy;
	}

	public Map<String, String> getStatWithEnbMap() {
		return statWithEnbMap;
	}

	public void setStatWithEnbMap(Map<String, String> statWithEnbMap) {
		this.statWithEnbMap = statWithEnbMap;
	}
	
	
	

}
