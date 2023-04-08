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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Component;

import com.smart.rct.common.models.AuditSummaryModel;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSuccessSummaryModel;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;

@Component
public class Audit4GFsuSummaryDto {
	final static Logger logger = LoggerFactory.getLogger(Audit4GFsuSummaryDto.class);

	public List<Audit4GSummaryModel> getAudit4GFsuSummaryReportModelList(
			List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList) {
		List<Audit4GSummaryModel> audit4GFsuSummaryModelList = new ArrayList<>();
		try {
			for (Audit4GFsuSummaryEntity audit4GFsuSummaryEntity : audit4GFsuSummaryEntityList) {

				Audit4GSummaryModel audit4GFsuSummaryModel = new Audit4GSummaryModel();
				Audit4GFsuRulesEntity audit4GFsuRulesEntity = audit4GFsuSummaryEntity.getAudit4gFsuRulesEntity();
				audit4GFsuSummaryModel.setAuditIssue(audit4GFsuSummaryEntity.getAuditIssue());
				audit4GFsuSummaryModel.setTestName(audit4GFsuRulesEntity.getTestName());
				audit4GFsuSummaryModel.setTest(audit4GFsuRulesEntity.getTest());
				audit4GFsuSummaryModel.setYangCommand(audit4GFsuRulesEntity.getYangCommand());
				audit4GFsuSummaryModel.setExpectedResult(audit4GFsuRulesEntity.getExpectedResult());
				audit4GFsuSummaryModel.setActionItem(audit4GFsuRulesEntity.getActionItem());
				audit4GFsuSummaryModel.setRemarks(audit4GFsuRulesEntity.getRemarks());
				audit4GFsuSummaryModel.setErrorCode(audit4GFsuRulesEntity.getErrorCode());
				audit4GFsuSummaryModel.setNeId(audit4GFsuSummaryEntity.getNeId());	
				audit4GFsuSummaryModel.setReferenceMOP(audit4GFsuRulesEntity.getReferenceMOP());	
				audit4GFsuSummaryModelList.add(audit4GFsuSummaryModel);
			}
		} catch(Exception e) {
			logger.error("Exception Audit4GFsuSummaryDto in getAudit4GFsuSummaryReportModelList() " + ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuSummaryModelList;
	}

	public List<AuditPassFailSummaryModel> getAudit4GFsuPassFailEachIdList(
			List<Audit4GFsuPassFailSummaryEntity> audit4gFsuPassFailSummaryEntityList) {
		List<AuditPassFailSummaryModel> audit4GFsuPassFailSummaryModelList = new ArrayList<>();
		try {
			for (Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntity : audit4gFsuPassFailSummaryEntityList) {

				AuditPassFailSummaryModel auditPassFailSummaryModel = new AuditPassFailSummaryModel();
				Audit4GFsuRulesEntity audit4GFsuRulesEntity = audit4GFsuPassFailSummaryEntity
						.getAudit4gFsuRulesEntity();

				auditPassFailSummaryModel.setAuditPassFail(audit4GFsuPassFailSummaryEntity.getAuditPassFail());
				auditPassFailSummaryModel.setTestName(audit4GFsuRulesEntity.getTestName());
				// auditPassFailSummaryModel.setTest(audit4GFsuRulesEntity.getTest());
				audit4GFsuPassFailSummaryModelList.add(auditPassFailSummaryModel);
			}

		} catch (Exception e) {
			logger.error("Exception Audit4GFsuPassFailSummaryDto in getAudit4GFsuPassFailSummaryReportModelList() "
					+ ExceptionUtils.getFullStackTrace(e));

		}

		return audit4GFsuPassFailSummaryModelList;
	}

	public List<AuditPassFailSummaryModel> getAudit4GFsuPassFailSummaryReportModelList(
			List<Audit4GFsuPassFailSummaryEntity> audit4gFsuPassFailSummaryEntityList, String programName,String userName) {

		AuditPassFailSummaryModel auditSummaryModel = new AuditPassFailSummaryModel();
		List<AuditPassFailSummaryModel> audit4GFsuPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit4gFsuPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit4gFsuPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit4gFsuPassFailSummaryEntityList:" + audit4gFsuPassFailSummaryEntityList);

		audit4gFsuPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit4GFsuPassFailSummaryEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit4gFsuPassFailSummaryEntityList:" + audit4gFsuPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;

		List<RunTestEntity> runTestLIst = audit4gFsuPassFailSummaryEntityList.stream()
				.filter(x -> null != x.getRunTestEntity()).map(auditSumm -> auditSumm.getRunTestEntity())
				.collect(Collectors.toList());



		Map<String, Date> runTestCreateDateMap = new HashMap<>();
		runTestLIst.forEach(x -> {
			runTestCreateDateMap.put(x.getId().toString(), x.getCreationDate());
		});
		try {
			for (Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntity : audit4gFsuPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit4gFsuRulesEntity().getTestName();
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

				auditRun.setCreationDate(runTestCreateDateMap.get(runEntry.getKey().trim()));
	
				
				System.out.println(runTestCreateDateMap.get(runEntry.getKey()));
				System.out.println(runEntry.getKey());
				String key = runEntry.getKey();
				Date createdDate = runTestCreateDateMap.get(key);
				auditNeRunSummary.add(auditRun);
			}

			auditSum.setTech(programName);
			auditSum.setNeId(neId);
			auditSum.setAuditNeRunSummary(auditNeRunSummary);

			auditSum.setUserName(userName);
			auditSumList.add(auditSum);
		}


		return auditSumList;
	}

	public Map<String, Map<String, Map<String, String>>> getAudit4GFsuPassFailSummaryReportModelList_back(
			List<Audit4GFsuPassFailSummaryEntity> audit4gFsuPassFailSummaryEntityList) {
		List<AuditPassFailSummaryModel> audit4GFsuPassFailSummaryModelList = new ArrayList<>();
		Set<String> collect = audit4gFsuPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getTestName()).collect(Collectors.toSet());

		System.out.println("collect:" + collect);

		Set<Integer> collectId = audit4gFsuPassFailSummaryEntityList.stream()
				.map(auditSumm -> auditSumm.getAudit4gFsuRulesEntity().getId()).collect(Collectors.toSet());
		System.out.println("collectId:" + collectId);

		System.out.println("audit4gFsuPassFailSummaryEntityList:" + audit4gFsuPassFailSummaryEntityList);

		audit4gFsuPassFailSummaryEntityList.stream()
				.sorted(Comparator.comparing(Audit4GFsuPassFailSummaryEntity::getNeId)).collect(Collectors.toList());

		System.out.println("audit4gFsuPassFailSummaryEntityList:" + audit4gFsuPassFailSummaryEntityList);

		Map<String, Map<String, Map<String, String>>> neNameMap = new HashedMap<>();
		Map<String, String> itemPassFail = null;
		Map<String, Map<String, String>> runTestMap = null;
		try {
			for (Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryEntity : audit4gFsuPassFailSummaryEntityList) {

				String neId = audit4GFsuPassFailSummaryEntity.getNeId();
				String testName2 = audit4GFsuPassFailSummaryEntity.getAudit4gFsuRulesEntity().getTestName();
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

		// ehsreturn audit4GFsuPassFailSummaryModelList;
		return neNameMap;
	}



}
