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
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandRulesEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;

@Component
public class Audit5GCBandSummaryDto {
	final static Logger logger = LoggerFactory.getLogger(Audit5GCBandSummaryDto.class);
	
	public List<Audit4GSummaryModel> getAudit5GCBandSummaryReportModelList(List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList){
		List<Audit4GSummaryModel> audit5GCBandSummaryModelList = new ArrayList<>();
		try {
			for(Audit5GCBandSummaryEntity audit5GCBandSummaryEntity : audit5GCBandSummaryEntityList) {
				
				Audit4GSummaryModel audit5GCBandSummaryModel = new Audit4GSummaryModel();
				Audit5GCBandRulesEntity audit5GCBandRulesEntity = audit5GCBandSummaryEntity.getAudit5gCbandRulesEntity();
				audit5GCBandSummaryModel.setAuditIssue(audit5GCBandSummaryEntity.getAuditIssue());
				audit5GCBandSummaryModel.setTestName(audit5GCBandRulesEntity.getTestName());
				audit5GCBandSummaryModel.setTest(audit5GCBandRulesEntity.getTest());
				audit5GCBandSummaryModel.setYangCommand(audit5GCBandRulesEntity.getYangCommand());
				audit5GCBandSummaryModel.setExpectedResult(audit5GCBandRulesEntity.getExpectedResult());
				audit5GCBandSummaryModel.setActionItem(audit5GCBandRulesEntity.getActionItem());
				audit5GCBandSummaryModel.setRemarks(audit5GCBandRulesEntity.getRemarks());
				audit5GCBandSummaryModel.setErrorCode(audit5GCBandRulesEntity.getErrorCode());
				audit5GCBandSummaryModel.setNeId(audit5GCBandSummaryEntity.getNeId());	
				audit5GCBandSummaryModel.setReferenceMOP(audit5GCBandRulesEntity.getReferenceMOP());
				audit5GCBandSummaryModelList.add(audit5GCBandSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception Audit5GCBandSummaryDto in getAudit5GCBandSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandSummaryModelList;
	}

	public List<AuditPassFailSummaryModel> getAudit5GCBandPassFailReportModelList(
			List<Audit5GCBandPassFailEntity> audit5gcBandPassFailModelList, String programName, String userName) {
		


		AuditPassFailSummaryModel auditSummaryModel = new AuditPassFailSummaryModel();
		List<AuditPassFailSummaryModel> audit5gcBandPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit5gcBandPassFailModelList.stream()
				.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit5gcBandPassFailModelList.stream()
				.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit5gcBandPassFailSummaryEntityList:" + audit5gcBandPassFailModelList);
		
		logger.error("Audit5GCBandPassFailModelList {}",audit5gcBandPassFailModelList);

		audit5gcBandPassFailModelList.stream()
				.sorted(Comparator.comparing(Audit5GCBandPassFailEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit5gcBandPassFailSummaryEntityList:" + audit5gcBandPassFailModelList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;

		List<RunTestEntity> runTestLIst = audit5gcBandPassFailModelList.stream()
				.filter(x -> null != x.getRunTestEntity()).map(auditSumm -> auditSumm.getRunTestEntity())
				.collect(Collectors.toList());

		logger.error("5GCBand RuntestList {}",runTestLIst);
		/*
		 * Map<Integer, Date> runTestCreateDateMap = runTestLIst.stream()
		 * .collect(Collectors.toMap(RunTestEntity::getId,
		 * RunTestEntity::getCreationDate));
		 */

		Map<String, Date> runTestCreateDateMap = new HashMap<>();
		runTestLIst.forEach(x -> {
			runTestCreateDateMap.put(x.getId().toString(), x.getCreationDate());
		});
		try {
			for (Audit5GCBandPassFailEntity audit4GFsuPassFailSummaryEntity : audit5gcBandPassFailModelList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit5GCBandRulesEntity().getTestName();
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

		// ehsreturn audit5gcBandPassFailSummaryModelList;
		return auditSumList;
	
	}
	
	public Map<String, Map<String, Map<String, String>>> getAudit4GFsuPassFailSummaryReportModelList_back(
			List<Audit5GCBandPassFailEntity> audit5gcBandPassFailSummaryEntityList) {
		List<AuditPassFailSummaryModel> audit5gcBandPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit5gcBandPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit5gcBandPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit5GCBandRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit5gcBandPassFailSummaryEntityList:" + audit5gcBandPassFailSummaryEntityList);

		audit5gcBandPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit5GCBandPassFailEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit5gcBandPassFailSummaryEntityList:" + audit5gcBandPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;
		try {
			for (Audit5GCBandPassFailEntity audit4GFsuPassFailSummaryEntity : audit5gcBandPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit5GCBandRulesEntity().getTestName();
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

		// ehsreturn audit5gcBandPassFailSummaryModelList;
		return neNameMap;
	}
}
