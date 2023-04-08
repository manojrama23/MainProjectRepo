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
import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.Audit5GDSSPassFailModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;

@Component
public class Audit5GDSSSummaryDto {
	final static Logger logger = LoggerFactory.getLogger(Audit5GDSSSummaryDto.class);
	
	public List<Audit4GSummaryModel> getAudit5GDSSSummaryReportModelList(List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList){
		List<Audit4GSummaryModel> audit5GDSSSummaryModelList = new ArrayList<>();
		try {
			for(Audit5GDSSSummaryEntity audit5GDSSSummaryEntity : audit5GDSSSummaryEntityList) {
				
				Audit4GSummaryModel audit5GDSSSummaryModel = new Audit4GSummaryModel();
				Audit5GDSSRulesEntity audit5GDSSRulesEntity = audit5GDSSSummaryEntity.getAudit5gCbandRulesEntity();
				audit5GDSSSummaryModel.setAuditIssue(audit5GDSSSummaryEntity.getAuditIssue());
				audit5GDSSSummaryModel.setTestName(audit5GDSSRulesEntity.getTestName());
				audit5GDSSSummaryModel.setTest(audit5GDSSRulesEntity.getTest());
				audit5GDSSSummaryModel.setYangCommand(audit5GDSSRulesEntity.getYangCommand());
				audit5GDSSSummaryModel.setExpectedResult(audit5GDSSRulesEntity.getExpectedResult());
				audit5GDSSSummaryModel.setActionItem(audit5GDSSRulesEntity.getActionItem());
				audit5GDSSSummaryModel.setRemarks(audit5GDSSRulesEntity.getRemarks());
				audit5GDSSSummaryModel.setErrorCode(audit5GDSSRulesEntity.getErrorCode());
				audit5GDSSSummaryModel.setNeId(audit5GDSSSummaryEntity.getNeId());	
				audit5GDSSSummaryModel.setReferenceMOP(audit5GDSSRulesEntity.getReferenceMOP());
				audit5GDSSSummaryModelList.add(audit5GDSSSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception Audit5GDSSSummaryDto in getAudit5GDSSSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSSummaryModelList;
	}

	public List<Audit5GDSSPassFailModel> getAudit5GDSSPassFailSummaryReportModelList(
			List<Audit5GDSSPassFailSummaryEntity> audit5gdssPassFailSummaryEntityList) {
		List<Audit5GDSSPassFailModel> audit5GDSSPassFailSummaryModelList = new ArrayList<>();
		try {
			for (Audit5GDSSPassFailSummaryEntity audit5GDSSPassFailSummaryEntity : audit5gdssPassFailSummaryEntityList) {
				
				Audit5GDSSPassFailModel auditPassFailSummaryModel = new Audit5GDSSPassFailModel();
				Audit5GDSSRulesEntity audit4GFsuRulesEntity = audit5GDSSPassFailSummaryEntity.getAudit5GDSSRulesEntity();
				
				auditPassFailSummaryModel.setAuditPassFail(audit5GDSSPassFailSummaryEntity.getAuditPassFail());
				auditPassFailSummaryModel.setTestName(audit4GFsuRulesEntity.getTestName());
			//	auditPassFailSummaryModel.setTest(audit4GFsuRulesEntity.getTest());
				audit5GDSSPassFailSummaryModelList.add(auditPassFailSummaryModel);
			}
			
		}catch (Exception e) {
			logger.error("Exception Audit4GFsuPassFailSummaryDto in getAudit4GFsuPassFailSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));

		}
	
		return null;
	}

	public List<AuditPassFailSummaryModel> getAudit5GDSSPassFailReportModelList(
			List<Audit5GDSSPassFailSummaryEntity> audit5gdssPassFailModelList, String programName, String userName) {
		
		AuditPassFailSummaryModel auditSummaryModel = new AuditPassFailSummaryModel();
		List<AuditPassFailSummaryModel> audit5gdssPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit5gdssPassFailModelList.stream()
				.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit5gdssPassFailModelList.stream()
				.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit5gDssPassFailSummaryEntityList:" + audit5gdssPassFailModelList);
		
		logger.error("Audit5GDSS PassFailModelList {} " ,audit5gdssPassFailModelList);

		audit5gdssPassFailModelList.stream()
				.sorted(Comparator.comparing(Audit5GDSSPassFailSummaryEntity::getNeId)).collect(Collectors.toList());

		logger.error("Audit5GDSS PassFail Model list {} " , audit5gdssPassFailModelList);
		System.out.println("audit5gDssPassFailSummaryEntityList:" + audit5gdssPassFailModelList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;

		List<RunTestEntity> runTestLIst = audit5gdssPassFailModelList.stream()
				.filter(x -> null != x.getRunTestEntity()).map(auditSumm -> auditSumm.getRunTestEntity())
				.collect(Collectors.toList());


		Map<String, Date> runTestCreateDateMap = new HashMap<>();
		runTestLIst.forEach(x -> {
			runTestCreateDateMap.put(x.getId().toString(), x.getCreationDate());
		});
		try {
			for (Audit5GDSSPassFailSummaryEntity audit4GFsuPassFailSummaryEntity : audit5gdssPassFailModelList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit5GDSSRulesEntity().getTestName();
				String passFail = audit4GFsuPassFailSummaryEntity.getAuditPassFail();
				String runId = audit4GFsuPassFailSummaryEntity.getRunTestEntity().getId() + "";
			
				Date creationDate = audit4GFsuPassFailSummaryEntity.getCreationDate();
				System.out.println(creationDate);
	
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
			logger.error("Audit5gdssPassFailModelList {}",audit5gdssPassFailModelList);
		
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

		// ehsreturn audit5gdssPassFailSummaryModelList;
		return auditSumList;
	}
	
	public Map<String, Map<String, Map<String, String>>> getAudit4GFsuPassFailSummaryReportModelList_back(
			List<Audit5GDSSPassFailSummaryEntity> audit5gDssPassFailSummaryEntityList) {
		List<AuditPassFailSummaryModel> audit5gdssPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit5gDssPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit5gDssPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit5GDSSRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit5gDssPassFailSummaryEntityList:" + audit5gDssPassFailSummaryEntityList);

		audit5gDssPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit5GDSSPassFailSummaryEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit5gDssPassFailSummaryEntityList:" + audit5gDssPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;
		try {
			for (Audit5GDSSPassFailSummaryEntity audit4GFsuPassFailSummaryEntity : audit5gDssPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit5GDSSRulesEntity().getTestName();
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

		// ehsreturn audit5gdssPassFailSummaryModelList;
		return neNameMap;
	}
}
