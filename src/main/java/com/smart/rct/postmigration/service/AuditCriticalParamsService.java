package com.smart.rct.postmigration.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex1Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex2Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex3Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex4Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex5Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex6Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.models.AuditCriticalParamsSummaryModel;

public interface AuditCriticalParamsService {

	AuditCriticalParamsSummaryEntity createAuditCriticalParamsSummaryEntity(String neId, int runTestId);
	

	AuditCriticalParamsIndex1Entity createAuditCriticalParamsIndex1Entity(AuditCriticalParamsIndex1Entity index1,
			AuditCriticalParamsSummaryEntity auditCriticalResults);
	
	AuditCriticalParamsIndex2Entity createAuditCriticalParamsIndex2Entity(AuditCriticalParamsIndex2Entity index2,
			AuditCriticalParamsSummaryEntity auditCriticalResults);


	AuditCriticalParamsIndex3Entity createAuditCriticalParamsIndex3Entity(AuditCriticalParamsIndex3Entity index3,
			AuditCriticalParamsSummaryEntity auditCriticalResults);


	AuditCriticalParamsIndex4Entity createAuditCriticalParamsIndex4Entity(AuditCriticalParamsIndex4Entity index4,
			AuditCriticalParamsSummaryEntity auditCriticalResults);
	
	AuditCriticalParamsIndex5Entity createAuditCriticalParamsIndex5Entity(AuditCriticalParamsIndex5Entity index5,
			AuditCriticalParamsSummaryEntity auditCriticalResults);

	AuditCriticalParamsIndex6Entity createAuditCriticalParamsIndex6Entity(AuditCriticalParamsIndex6Entity index6,
			AuditCriticalParamsSummaryEntity auditCriticalResults);
	
	void storeAuditCriticalParams(AuditCriticalParamsSummaryEntity auditCriticalParamsEntity,
			List<LinkedHashMap<String, String>> tabelData, StringBuilder auditIssueList);
	
	List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityById(Integer id);

	List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityList();

	
	List<AuditCriticalParamsIndex1Entity> getAuditCriticalParamsIndex1Entity();


	List<AuditCriticalParamsIndex6Entity> getAuditCriticalParamsIndex6Entity();


	List<AuditCriticalParamsIndex5Entity> getAuditCriticalParamsIndex5Entity();


	List<AuditCriticalParamsIndex4Entity> getAuditCriticalParamsIndex4Entity();


	List<AuditCriticalParamsIndex3Entity> getAuditCriticalParamsIndex3Entity();


	List<AuditCriticalParamsIndex2Entity> getAuditCriticalParamsIndex2Entity();


	boolean updateExecStatus(int runTestId, String status);


	boolean createAuditCriticalParamsBulkReportExcel(JSONObject auditCriticalParamsReportDetails);


	Map<String, Object> getAuditCriticalParamsSummaryEntityList(int page, int count, String fromDate, String toDate);


	Map<String, Object> getAuditCriticalParamsSearchSummaryEntityList(AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel, int page, int count);


	boolean updateAuditStatus(int runTestId, String sfpStatus, String retStatus, String udaStatus, String hwStatus);


	boolean deleteAuditCriticalSummaryEntityByRunTestId(int runTestId);


	boolean deleteAuditCriticalIndex1EntityByRunTestId(int indexId);


		
}
