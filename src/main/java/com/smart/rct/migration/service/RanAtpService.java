package com.smart.rct.migration.service;

import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.RunTestEntity;

public interface RanAtpService {

	
	public String getRuntestExecResult(JSONObject runTestParams, Map<String, RunTestEntity> runTestEntity,
			String runType,String htmlOutputFileName,String opsAtpFileContent,String fileName) throws RctException;
	
	public Map<String, RunTestEntity> insertRunTestDetails(JSONObject runTestParams,String perlPath);
	
	public JSONObject getSaneDetailsforPassword(JSONObject runTestParams);
	
	
	
}
