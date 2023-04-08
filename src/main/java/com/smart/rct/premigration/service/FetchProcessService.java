package com.smart.rct.premigration.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.FetchOVResponseModel;
import com.smart.rct.common.models.TrackerDetailsModel;

public interface FetchProcessService {
	
	public void fetchExtraction(JSONObject fetchCiqDetails, JSONObject resultMap, String sessionId,
			String serviceToken, 
			FetchDetailsModel fetchDetailsModel);
	OvScheduledEntity getOvEnvUploadDetails(OvScheduledEntity ovScheduledEntity);
	void shedulingFetchDetails(List<TrackerDetailsModel> trackerDetailsModellist, String fetchType, String remarks,String programName);
	JSONObject createExcelFromRFDB(List<FetchOVResponseModel> listFetchOVResponseMdel, String days);
	OvScheduledEntity statusUpdateApi(OvScheduledEntity ovScheduledEntity, String type, String type2, String program);
	JSONObject getOvFetchDetails(String fetchType, String remarks, String programName, String fetchDate);
}
