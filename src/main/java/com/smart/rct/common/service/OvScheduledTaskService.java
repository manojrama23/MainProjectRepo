package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.OvScheduledModel;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;

public interface OvScheduledTaskService {
	Map<String,List<OvScheduledEntity>> getOvScheduledDetails(List<String> forceFetchIds,CustomerDetailsEntity programmeEntity);
	OvScheduledEntity getOvScheduledServiceDetails(String trackerId, String enbId);
	OvScheduledEntity mergeOvScheduledDetails(OvScheduledEntity ovScheduledEntity);
	Map<String, Object> getOvStatusScheduledDetails(int page, int count, String programName);
	Map<String, Object> getOvStatusScheduledSearchDetails(OvScheduledModel ovScheduledModel,int page, int count,String programName);
	OvScheduledEntity getOvDetails(Integer workFlowId);
	//List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId);
        JSONObject forceFetchDetails(FetchDetailsModel fetchDetailsModel,String fetchType,String remarks,List<OvScheduledEntity> listSchedule);
	List<OvScheduledEntity> getForceFecthOvDetails(List<String> neids);
	AutoFecthTriggerEntity getAutoFetchDetails(String programName);
	AutoFecthTriggerEntity mergeAutoFetchDetails(AutoFecthTriggerEntity autoFecthTriggerEntity);
	JSONObject uploadScheduledCiqDetails(List<String> scriftNeids);
	JSONObject processScheduledCiqDetails(List<String> scriftNeids,CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity );
	boolean getOvDetailsForCreateExcel(OvScheduledModel ovScheduledModel, List<String> programs, boolean b, int page,
			int count);
	boolean deleteOvDetails(int ovId);
	OvScheduledEntity getOvDetail(Integer ovId);
	List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId, String programName);
	
}
