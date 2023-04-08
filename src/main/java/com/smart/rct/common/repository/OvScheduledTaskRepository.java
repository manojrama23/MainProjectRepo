package com.smart.rct.common.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.models.OvScheduledModel;

public interface OvScheduledTaskRepository {

	
	List<OvScheduledEntity> getOvScheduledDetails(String date,List<String> forceFetchIds,CustomerDetailsEntity programmeEntity);
	OvScheduledEntity getOvScheduledServiceDetails(String trackerId, String enbId);
	OvScheduledEntity createOvScheduleDetails(OvScheduledEntity ovScheduledEntity);
	Map<String, Object> getOvStatusScheduledDetails(int page, int count, String programId);
	Map<String, Object> getOvStatusScheduledSearchDetails(OvScheduledModel ovScheduledModel,int page, int count,String programName);
	OvScheduledEntity getOvDetails(Integer workFlowId);
	//List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId);
	List<OvScheduledEntity> getOvDetailsForExPort(OvScheduledModel ovScheduledModel);
        List<OvScheduledEntity> getForceFecthOvDetails(List<String> neids);
	AutoFecthTriggerEntity getAutoFetchDetails(String programName);
	AutoFecthTriggerEntity mergeAutoFetchDetails(AutoFecthTriggerEntity autoFecthTriggerEntity);
	boolean deleteOvDetails(int ovId);
	OvScheduledEntity getOvDetail(Integer ovId);
	List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId, String programName);
	OvScheduledEntity get4gOvDetails(String programName);
	
}
