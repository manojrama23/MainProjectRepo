package com.smart.rct.postmigration.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GRulesEntity;
import com.smart.rct.postmigration.entity.Audit4GSuccessSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSuccessSummaryModel;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;

@Component
public class Audit4GSummaryDto {
	final static Logger logger = LoggerFactory.getLogger(Audit4GSummaryDto.class);
	
	public List<Audit4GSummaryModel> getAudit4GSummaryReportModelList(List<Audit4GSummaryEntity> audit4GSummaryEntityList){
		List<Audit4GSummaryModel> audit4GSummaryModelList = new ArrayList<>();
		try {
			for(Audit4GSummaryEntity audit4GSummaryEntity : audit4GSummaryEntityList) {
				
				Audit4GSummaryModel audit4GSummaryModel = new Audit4GSummaryModel();
				Audit4GRulesEntity audit4GRulesEntity = audit4GSummaryEntity.getAudit4gRulesEntity();
				audit4GSummaryModel.setAuditIssue(audit4GSummaryEntity.getAuditIssue());
				audit4GSummaryModel.setTestName(audit4GRulesEntity.getTestName());
				audit4GSummaryModel.setTest(audit4GRulesEntity.getTest());
				audit4GSummaryModel.setYangCommand(audit4GRulesEntity.getYangCommand());
				audit4GSummaryModel.setExpectedResult(audit4GRulesEntity.getExpectedResult());
				audit4GSummaryModel.setActionItem(audit4GRulesEntity.getActionItem());
				audit4GSummaryModel.setRemarks(audit4GRulesEntity.getRemarks());
				audit4GSummaryModel.setErrorCode(audit4GRulesEntity.getErrorCode());
				audit4GSummaryModel.setNeId(audit4GSummaryEntity.getNeId());	
				audit4GSummaryModel.setReferenceMOP(audit4GRulesEntity.getReferenceMOP());			
				audit4GSummaryModelList.add(audit4GSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception Audit4GSummaryDto in getAudit4GSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GSummaryModelList;
	}
	
	public List<Audit4GSuccessSummaryModel> getAudit4GPassSummaryReportModelList(List<Audit4GSuccessSummaryEntity> audit4GPassSummaryEntityList){
		List<Audit4GSuccessSummaryModel> audit4GPassSummaryModelList = new ArrayList<>();
		try {
			for(Audit4GSuccessSummaryEntity audit4GSuccessSummaryEntity : audit4GPassSummaryEntityList) {
				
				Audit4GSuccessSummaryModel audit4GSuccessSummaryModel = new Audit4GSuccessSummaryModel();
				Audit4GRulesEntity audit4GRulesEntity = audit4GSuccessSummaryEntity.getAudit4gRulesEntity();
				audit4GSuccessSummaryModel.setAuditSuccess(audit4GSuccessSummaryEntity.getAuditSuccess());
				audit4GSuccessSummaryModel.setTestName(audit4GRulesEntity.getTestName());
				audit4GSuccessSummaryModel.setTest(audit4GRulesEntity.getTest());
				audit4GSuccessSummaryModel.setYangCommand(audit4GRulesEntity.getYangCommand());
				audit4GSuccessSummaryModel.setExpectedResult(audit4GRulesEntity.getExpectedResult());
				audit4GSuccessSummaryModel.setActionItem(audit4GRulesEntity.getActionItem());
				audit4GSuccessSummaryModel.setRemarks(audit4GRulesEntity.getRemarks());
				audit4GSuccessSummaryModel.setErrorCode(audit4GRulesEntity.getErrorCode());				
				audit4GPassSummaryModelList.add(audit4GSuccessSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception Audit4GSuccessSummaryDto in getAudit4GSuccessSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GPassSummaryModelList;
	}

	public List<AuditPassFailSummaryModel> getAudit4GPassFailReportModelList(
			List<Audit4GPassFailEntity> audit4gPassFailSummaryEntityList, String programName, String userName) {

		AuditPassFailSummaryModel auditSummaryModel = new AuditPassFailSummaryModel();
		List<AuditPassFailSummaryModel> audit4gPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit4gPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit4gPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit4gPassFailSummaryEntityList:" + audit4gPassFailSummaryEntityList);

		audit4gPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit4GPassFailEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit4gPassFailSummaryEntityList:" + audit4gPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;

		List<RunTestEntity> runTestLIst = audit4gPassFailSummaryEntityList.stream()
				.filter(x -> null != x.getRunTestEntity()).map(auditSumm -> auditSumm.getRunTestEntity())
				.collect(Collectors.toList());


		Map<String, Date> runTestCreateDateMap = new HashMap<>();
		runTestLIst.forEach(x -> {
			runTestCreateDateMap.put(x.getId().toString(), x.getCreationDate());
		});
		try {
			for (Audit4GPassFailEntity audit4GFsuPassFailSummaryEntity : audit4gPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit4gRulesEntity().getTestName();
				String passFail = audit4GFsuPassFailSummaryEntity.getAuditPassFail();
				String runId = audit4GFsuPassFailSummaryEntity.getRunTestEntity().getId() + "";
			
				Date creationDate = audit4GFsuPassFailSummaryEntity.getCreationDate();
				System.out.println(creationDate);
				//String userName =  audit4GFsuPassFailSummaryEntity.getRunTestEntity().getUserName();
				// String date = creationDate.toLocaleString();
				if (neNameMap.containsKey(neId)) {
					Map<String, Map<String, String>> existingNemap = neNameMap.get(neId);
					// check existing run
					if (existingNemap.containsKey(runId)) {
						Map<String, String> map = existingNemap.get(runId);
						map.put(testName2, passFail);
						existingNemap.put(runId, map);
						neNameMap.put(neId, existingNemap);
					} else {
						runTestMap = new HashMap<>();
						itemPassFail = new HashMap<String, String>();
						itemPassFail.put(testName2, passFail);
						runTestMap.put(runId, itemPassFail);
						existingNemap.put(runId,itemPassFail);
						
	
						 
					}

				} else {
					runTestMap = new HashMap<>();
					// item level
					itemPassFail = new HashMap<String, String>();
					itemPassFail.put(testName2, passFail);
					// run test level
					runTestMap.put(runId, itemPassFail);
					// NeId level
					neNameMap.put(neId, runTestMap);
					
					
				//	neNameMap.put();
				//	neNameMap.put("creationDate", creationDate.toString());
					
				}

			}

		} catch (Exception e) {
			logger.error("Exception Audit4GFsuPassFailSummaryDto in getAudit4GFsuPassFailSummaryReportModelList() "
					+ ExceptionUtils.getFullStackTrace(e));

		}

		List<AuditPassFailSummaryModel> auditSumList = new ArrayList<>();
		// ne id basis iteration
		for (Map.Entry<String, Map<String, Map<String, String>>> entry : neNameMap.entrySet()) {
			String neId = entry.getKey();
			AuditPassFailSummaryModel auditSum = new AuditPassFailSummaryModel();
			List<AuditRunModel> auditNeRunSummary = new ArrayList<>();
			Map<String, Map<String, String>> neIdValue = entry.getValue();
			// run id basis iteration
			for (Map.Entry<String, Map<String, String>> runEntry : neIdValue.entrySet()) {
				AuditRunModel auditRun = new AuditRunModel();
				Map<String, String> auditFields = runEntry.getValue();
				auditRun.setRunId(runEntry.getKey());
				auditRun.setRunTestParams(auditFields);
				
				/*
				 * if(runTestCreateDateMap.get(runEntry.getKey()) != null) {
				 * System.out.println(runTestCreateDateMap.get(runEntry.getKey())); }
				 */
				auditRun.setCreationDate(runTestCreateDateMap.get(runEntry.getKey().trim()));
			//	auditRun.setCreationDate(creationDate);
				
				System.out.println(runTestCreateDateMap.get(runEntry.getKey()));
				System.out.println(runEntry.getKey());
				String key = runEntry.getKey();
				Date createdDate = runTestCreateDateMap.get(key);
				auditNeRunSummary.add(auditRun);
			}

			auditSum.setTech(programName);
			auditSum.setNeId(neId);
			auditSum.setAuditNeRunSummary(auditNeRunSummary);
			//auditSum.setCreationDate(auditNeRunSummary);
			auditSum.setUserName(userName);
			auditSumList.add(auditSum);
		}

		// ehsreturn audit4gPassFailSummaryModelList;
		return auditSumList;
	}

	public Map<String, Map<String, Map<String, String>>> getAudit4GFsuPassFailSummaryReportModelList_back(
			List<Audit4GPassFailEntity> audit4gPassFailSummaryEntityList) {
		List<AuditPassFailSummaryModel> audit4gPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit4gPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit4gPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit4gPassFailSummaryEntityList:" + audit4gPassFailSummaryEntityList);

		audit4gPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit4GPassFailEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit4gPassFailSummaryEntityList:" + audit4gPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;
		try {
			for (Audit4GPassFailEntity audit4GFsuPassFailSummaryEntity : audit4gPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit4gRulesEntity().getTestName();
				String passFail = audit4GFsuPassFailSummaryEntity.getAuditPassFail();
				String runId = audit4GFsuPassFailSummaryEntity.getRunTestEntity().getId() + "";
				Date creationDate = audit4GFsuPassFailSummaryEntity.getCreationDate();
				String date = creationDate.toLocaleString();
				if (neNameMap.containsKey(neId)) {
					Map<String, Map<String, String>> existingNemap = neNameMap.get(neId);
					// check existing run
					if (existingNemap.containsKey(runId)) {
						Map<String, String> map = existingNemap.get(runId);
						map.put(testName2, passFail);
						existingNemap.put(runId, map);
						neNameMap.put(neId, existingNemap);
					} else {
						runTestMap = new HashMap<>();
						itemPassFail = new HashMap<String, String>();
						itemPassFail.put(testName2, passFail);
						runTestMap.put(runId, itemPassFail);
						neNameMap.put(neId, runTestMap);
					}

				} else {
					runTestMap = new HashMap<>();
					// item level
					itemPassFail = new HashMap<String, String>();
					itemPassFail.put(testName2, passFail);
					// run test level
					runTestMap.put(runId, itemPassFail);
					// NeId level
					neNameMap.put(neId, runTestMap);
				}

			}

		} catch (Exception e) {
			logger.error("Exception Audit4GFsuPassFailSummaryDto in getAudit4GFsuPassFailSummaryReportModelList() "
					+ ExceptionUtils.getFullStackTrace(e));

		}

		// ehsreturn audit4gPassFailSummaryModelList;
		return neNameMap;
	}
}
