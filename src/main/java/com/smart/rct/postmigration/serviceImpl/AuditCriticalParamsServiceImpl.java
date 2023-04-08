package com.smart.rct.postmigration.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.OvScheduledModel;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex1Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex2Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex3Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex4Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex5Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex6Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.models.AuditCriticalParamsSummaryModel;
import com.smart.rct.postmigration.repository.AuditCriticalParamsRepository;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class AuditCriticalParamsServiceImpl implements AuditCriticalParamsService {
	final static Logger logger = LoggerFactory.getLogger(AuditCriticalParamsServiceImpl.class);
	@Autowired
	RunTestRepository runTestRepository;

	@Autowired
	AuditCriticalParamsRepository auditCriticalParamsRepository;

	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;

	@Override
	public AuditCriticalParamsSummaryEntity createAuditCriticalParamsSummaryEntity(String neId, int runTestId) {
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityResultList = null;
		AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntityResult = null;
		try {
			String isstoreAuditCriticalParamsEnabled = LoadPropertyFiles.getInstance()
					.getProperty("storeAuditCriticalParams");
			if (isstoreAuditCriticalParamsEnabled != null && isstoreAuditCriticalParamsEnabled.equals("disabled")) {
				System.out.println("storeAuditCriticalParamsEnabled: " + isstoreAuditCriticalParamsEnabled);
				return auditCriticalParamsSummaryEntityResult;
			}
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			auditCriticalParamsSummaryEntityResultList = auditCriticalParamsService
					.getAuditCriticalParamsSummaryEntityById(runTestEntity.getId());
			AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntity = new AuditCriticalParamsSummaryEntity();
			if (auditCriticalParamsSummaryEntityResultList != null
					&& !auditCriticalParamsSummaryEntityResultList.isEmpty()) {
				auditCriticalParamsSummaryEntityResult = auditCriticalParamsSummaryEntityResultList.get(0);
			} else {
				auditCriticalParamsSummaryEntity.setNeId(neId);
				auditCriticalParamsSummaryEntity.setNeName(runTestEntity.getNeName());
				auditCriticalParamsSummaryEntity.setUserName(runTestEntity.getUserName());
				auditCriticalParamsSummaryEntity.setRunTestEntity(runTestEntity);
				auditCriticalParamsSummaryEntity.setStatus(runTestEntity.getResult());
				auditCriticalParamsSummaryEntity.setSiteName(neId);
				auditCriticalParamsSummaryEntity
						.setProgramId(runTestEntity.getCustomerDetailsEntity().getSourceProgramId());
				auditCriticalParamsSummaryEntity
						.setProgramName(runTestEntity.getCustomerDetailsEntity().getProgramName());
				auditCriticalParamsSummaryEntity.setCreationDate(runTestEntity.getCreationDate());
				auditCriticalParamsSummaryEntityResult = auditCriticalParamsRepository
						.createAuditCriticalParamsSummaryEntity(auditCriticalParamsSummaryEntity);
			}

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsSummaryEntity()   AuditCriticalParamsServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityResult;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean updateExecStatus(int runTestId, String status) {
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityResultList = null;
		AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntityResult = null;
		boolean updateStatus = false;
		try {
			String isstoreAuditCriticalParamsEnabled = LoadPropertyFiles.getInstance()
					.getProperty("storeAuditCriticalParams");
			if (isstoreAuditCriticalParamsEnabled.equals("disabled")) {
				System.out.println("storeAuditCriticalParamsEnabled: " + isstoreAuditCriticalParamsEnabled);
				return updateStatus;
			}
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			auditCriticalParamsSummaryEntityResultList = auditCriticalParamsService
					.getAuditCriticalParamsSummaryEntityById(runTestEntity.getId());
			if (auditCriticalParamsSummaryEntityResultList != null
					&& !auditCriticalParamsSummaryEntityResultList.isEmpty()) {
				auditCriticalParamsSummaryEntityResult = auditCriticalParamsSummaryEntityResultList.get(0);
				auditCriticalParamsSummaryEntityResult.setCreationDate(runTestEntity.getCreationDate());
				auditCriticalParamsSummaryEntityResult.setStatus(status);
				auditCriticalParamsSummaryEntityResult = auditCriticalParamsRepository
						.updateAuditCriticalParamsSummaryEntity(auditCriticalParamsSummaryEntityResult);
				updateStatus = true;
			}
		} catch (Exception e) {
			updateStatus = false;
			logger.error("Exception in createAuditCriticalParamsSummaryEntity()   AuditCriticalParamsServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return updateStatus;
	}
	@SuppressWarnings("unused")
	@Override
	public boolean updateAuditStatus(int runTestId, String sfpStatus, String retStatus, String udaStatus, String hwStatus) {
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityResultList = null;
		AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntityResult = null;
		boolean updateStatus = false;
		String isstoreAuditCriticalParamsEnabled = LoadPropertyFiles.getInstance()
				.getProperty("storeAuditCriticalParams");
		if (isstoreAuditCriticalParamsEnabled.equals("disabled")) {
			System.out.println("storeAuditCriticalParamsEnabled: " + isstoreAuditCriticalParamsEnabled);
			return updateStatus;
		}
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			auditCriticalParamsSummaryEntityResultList = auditCriticalParamsService
					.getAuditCriticalParamsSummaryEntityById(runTestEntity.getId());
			if (auditCriticalParamsSummaryEntityResultList != null
					&& !auditCriticalParamsSummaryEntityResultList.isEmpty()) {
				auditCriticalParamsSummaryEntityResult = auditCriticalParamsSummaryEntityResultList.get(0);
			}
			auditCriticalParamsSummaryEntityResult.setSfpAuditStatus(sfpStatus);
			auditCriticalParamsSummaryEntityResult.setRetAuditStatus(retStatus);
			auditCriticalParamsSummaryEntityResult.setUdaAuditStatus(udaStatus);
			auditCriticalParamsSummaryEntityResult.setHwAuditStatus(hwStatus);


			auditCriticalParamsSummaryEntityResult = auditCriticalParamsRepository
					.updateAuditCriticalParamsSummaryEntity(auditCriticalParamsSummaryEntityResult);
			updateStatus = true;

		} catch (Exception e) {
			updateStatus = false;
			logger.error("Exception in createAuditCriticalParamsSummaryEntity()   AuditCriticalParamsServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return updateStatus;
	}
	
	@Override
	public AuditCriticalParamsIndex1Entity createAuditCriticalParamsIndex1Entity(AuditCriticalParamsIndex1Entity index1,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex1Entity AuditCriticalParamsIndex1EntityResult = null;
		try {
			AuditCriticalParamsIndex1Entity auditCriticalParamsIndex1Entity = index1;
			auditCriticalParamsIndex1Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex1EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex1Entity(auditCriticalParamsIndex1Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex1EntityResult;
	}

	@Override
	public AuditCriticalParamsIndex2Entity createAuditCriticalParamsIndex2Entity(AuditCriticalParamsIndex2Entity index2,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex2Entity AuditCriticalParamsIndex2EntityResult = null;
		try {
			AuditCriticalParamsIndex2Entity auditCriticalParamsIndex2Entity = index2;
			auditCriticalParamsIndex2Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex2EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex2Entity(auditCriticalParamsIndex2Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex2EntityResult;
	}

	@Override
	public AuditCriticalParamsIndex3Entity createAuditCriticalParamsIndex3Entity(AuditCriticalParamsIndex3Entity index3,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex3Entity AuditCriticalParamsIndex3EntityResult = null;
		try {
			AuditCriticalParamsIndex3Entity auditCriticalParamsIndex3Entity = index3;
			auditCriticalParamsIndex3Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex3EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex3Entity(auditCriticalParamsIndex3Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex3EntityResult;
	}

	@Override
	public AuditCriticalParamsIndex4Entity createAuditCriticalParamsIndex4Entity(AuditCriticalParamsIndex4Entity index4,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex4Entity AuditCriticalParamsIndex4EntityResult = null;
		try {
			AuditCriticalParamsIndex4Entity auditCriticalParamsIndex4Entity = index4;
			auditCriticalParamsIndex4Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex4EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex4Entity(auditCriticalParamsIndex4Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex4EntityResult;
	}

	@Override
	public AuditCriticalParamsIndex5Entity createAuditCriticalParamsIndex5Entity(AuditCriticalParamsIndex5Entity index2,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex5Entity AuditCriticalParamsIndex5EntityResult = null;
		try {
			AuditCriticalParamsIndex5Entity auditCriticalParamsIndex5Entity = index2;
			auditCriticalParamsIndex5Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex5EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex5Entity(auditCriticalParamsIndex5Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex5EntityResult;
	}

	@Override
	public AuditCriticalParamsIndex6Entity createAuditCriticalParamsIndex6Entity(AuditCriticalParamsIndex6Entity index6,
			AuditCriticalParamsSummaryEntity auditCriticalResults) {
		AuditCriticalParamsIndex6Entity AuditCriticalParamsIndex6EntityResult = null;
		try {
			AuditCriticalParamsIndex6Entity auditCriticalParamsIndex6Entity = index6;
			auditCriticalParamsIndex6Entity.setAuditCriticalParamEntity(auditCriticalResults);
			AuditCriticalParamsIndex6EntityResult = auditCriticalParamsRepository
					.createAuditCriticalParamsIndex6Entity(auditCriticalParamsIndex6Entity);

		} catch (Exception e) {
			logger.error("Exception in createAuditCriticalParamsIndex1Entity()   createAuditCriticalParamsIndex1Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return AuditCriticalParamsIndex6EntityResult;
	}

	@Override
	public void storeAuditCriticalParams(AuditCriticalParamsSummaryEntity auditCriticalParamsEntity,
			List<LinkedHashMap<String, String>> tabelData, StringBuilder auditIssueList) {

		AuditCriticalParamsIndex1Entity index1 = null;
		AuditCriticalParamsIndex2Entity index2 = null;
		AuditCriticalParamsIndex3Entity index3 = null;
		AuditCriticalParamsIndex4Entity index4 = null;
		AuditCriticalParamsIndex5Entity index5 = null;
		AuditCriticalParamsIndex6Entity index6 = null;

		List<AuditCriticalParamsIndex1Entity> index1List = new ArrayList<>();
		List<AuditCriticalParamsIndex2Entity> index2List = new ArrayList<>();
		List<AuditCriticalParamsIndex3Entity> index3List = new ArrayList<>();
		List<AuditCriticalParamsIndex4Entity> index4List = new ArrayList<>();
		List<AuditCriticalParamsIndex5Entity> index5List = new ArrayList<>();
		List<AuditCriticalParamsIndex6Entity> index6List = new ArrayList<>();

		String isstoreAuditCriticalParamsEnabled = LoadPropertyFiles.getInstance()
				.getProperty("storeAuditCriticalParams");
		if (isstoreAuditCriticalParamsEnabled.equals("disabled")) {
			System.out.println("storeAuditCriticalParamsEnabled: " + isstoreAuditCriticalParamsEnabled);
			return;
		}
		String sfpAudit = "N/A";
		String retAudit = "N/A";
		String udaAudit = "N/A";
		String hwAudit = "N/A";
		for (LinkedHashMap<String, String> resultTableData : tabelData) {
			if (resultTableData.isEmpty())
				continue;

			index1 = new AuditCriticalParamsIndex1Entity();
			index2 = new AuditCriticalParamsIndex2Entity();
			index3 = new AuditCriticalParamsIndex3Entity();
			index4 = new AuditCriticalParamsIndex4Entity();
			index5 = new AuditCriticalParamsIndex5Entity();
			index6 = new AuditCriticalParamsIndex6Entity();

			String auditIndex11 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX1_INDEXES.get(0);
			String auditIndex12 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX1_INDEXES.get(1);

			String auditIndex21 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX2_INDEXES.get(0);
			String auditIndex22 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX2_INDEXES.get(1);

			String auditIndex31 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX3_INDEXES.get(0);
			String auditIndex32 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX3_INDEXES.get(1);

			String auditIndex41 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX4_INDEXES.get(0);
			String auditIndex42 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX4_INDEXES.get(1);

			String auditIndex51 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX5_INDEXES.get(0);
			String auditIndex52 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX5_INDEXES.get(1);

			String auditIndex61 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX6_INDEXES.get(0);
			String auditIndex62 = AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX6_INDEXES.get(1);
			String sep = ",";

			// Index1
			if (resultTableData.containsKey(auditIndex11) || resultTableData.containsKey(auditIndex12)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();
				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX1_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("cell-num"))
							index1.setCellNum(Integer.parseInt(value));
						if (key.equals("cell-identity"))
							index1.setCellIdentity(Integer.parseInt(value));
						if (key.equals("operational-state"))
							index1.setOperationalState(value);
						if (key.equals("administrative-state"))
							index1.setAdministrativeState(value);
						if (key.equals("activation-state"))
							index1.setActivationState(value);
						if (key.equals("cell-path-type"))
							index1.setCellPathType(value);
						if (key.equals("spectrum-sharing"))
							index1.setSpectrumSharing(value);
						if (key.equals("slot-level-operation-mode"))
							index1.setSlotLevelOperationalMode(value);
						if (key.equals("dl-antenna-count"))
							index1.setDlAntennaCount(value);
						if (key.equals("ul-antenna-count"))
							index1.setUlAntennaCount(value);
						if (key.equals("number-of-rx-paths-per-ru"))
							index1.setNumberOfRxPathsPerRU(value);

						String auditString = key + " : " + value;
						if ((auditIssueList != null && auditIssueList.toString().contains(auditString))) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							index1.setAuditStatus("Failed");
							index1.setAuditResult(auditIssue.toString());
						} else
							index1.setAuditStatus("Passed");
					}
				}
				index1List.add(index1);
			}
			// Index2

			if (resultTableData.containsKey(auditIndex21) || resultTableData.containsKey(auditIndex22)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();
				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX2_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("unit-type"))
							index2.setUnitType(value);
						if (key.equals("unit-id"))
							index2.setUnitId(value);
						if (key.equals("port-id"))
							index2.setPortId(value);
						if (key.equals("tx-power"))
							index2.setTxPower(value);
						if (key.equals("rx-power"))
							index2.setRxPower(value);
						if (key.equals("connected-du-cpri-port-id"))
							index2.setConnectedDuCpriPortId(value);
						if (key.equals("connected-enb-digital-unit-board-id"))
							index2.setConnectedEnbDigitalUnitBoardId(value);
						if (key.equals("connected-enb-digital-unit-port-id"))
							index2.setConnectedEnbDigitalUnitPortId(value);
						if (key.equals("enb-ne-id"))
							index2.setEnbNeId(value);
						if (key.equals("du-cpri-port-mode"))
							index2.setDuCpriPortMode(value);
						if (key.equals("mplan-ipv6"))
							index2.setMplaneIpv6(value);
						if (key.equals("pri-port-mode"))
							index2.setPriPortMode(value);
						if (key.equals("hardware-name"))
							index2.setHardWareName(value);

						if (key.equals("connected-digital-unit-board_id"))
							index2.setConnectedDigitalUnitBoardId(value);
						if (key.equals("radio-unit_port-id"))
							index2.setRadioUnitPortId(value);
						if (key.equals("vendor-name"))
							index2.setVendorName(value);
						if (key.equals("firmware-name"))
							index2.setFirmwareName(value);
						if (key.equals("package-version"))
							index2.setPackageVersion(value);
						if (key.equals("patch-version"))
							index2.setPatchVersion(value);
						if (key.equals("software-version"))
							index2.setSoftwareVersion(value);
						if (key.equals("cpri-speed-running"))
							index2.setCpriSpeedRunning(value);
						if (key.equals("tx-wavelength"))
							index2.setTxWavelength(value);

						if (key.equals("hardware-name"))
							hwAudit = "Passed";
						if (key.equals("tx-power") || key.equals("rx-power"))
							sfpAudit = "Passed";
						if (key.equals("radio-unit_port-id") || key.equals("antenna-line-device-id") || key.equals("cascade-radio-unit-id") || key.equals("connected-digital-unit-board-id") || key.equals("connected-digital-unit-port-id") )
							retAudit = "Passed";
						
						String auditString = key + " : " + value;
						if (auditIssueList != null && auditIssueList.toString().contains(auditString)) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							if (key.equals("hardware-name"))
								hwAudit = "Failed";
							if (key.equals("tx-power") || key.equals("rx-power"))
								sfpAudit = "Failed";
							if (key.equals("radio-unit_port-id") || key.equals("antenna-line-device-id") || key.equals("cascade-radio-unit-id") || key.equals("connected-digital-unit-board-id") || key.equals("connected-digital-unit-port-id") )
								retAudit = "Failed";
							auditCriticalParamsService.updateAuditStatus(auditCriticalParamsEntity.getRunTestEntity().getId(), sfpAudit, retAudit, udaAudit, hwAudit);
							index2.setAuditStatus("Failed");
							index2.setAuditResult(auditIssue.toString());
						} else
							index2.setAuditStatus("Passed");
					}

				}
				index2List.add(index2);
			}

			// Index3

			if (resultTableData.containsKey(auditIndex31) || resultTableData.containsKey(auditIndex32)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();
				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX3_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("ne-id"))
							index3.setNeId(value);
						if (key.equals("ne_type"))
							index3.setNeType(value);
						if (key.equals("sw-version"))
							index3.setSwVersion(value);
						if (key.equals("flavor-id"))
							index3.setFlavorId(value);
						if (key.equals("ip-address"))
							index3.setIpSddress(value);
						if (key.equals("f1-app-state"))
							index3.setF1ApState(value);

						String auditString = key + " : " + value;
						if (!key.equals(auditIndex31)
								&& (auditIssueList != null && auditIssueList.toString().contains(auditString))) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							index3.setAuditStatus("Failed");
							index3.setAuditResult(auditIssue.toString());
						} else
							index3.setAuditStatus("Passed");
						// index3List.add(isIndex, index3);
					}

				}
				index3List.add(index3);
			}

			// Index4

			if (resultTableData.containsKey(auditIndex41) || resultTableData.containsKey(auditIndex42)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();

				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX4_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("ne-id"))
							index4.setNeId(value);
						if (key.equals("alarm-unit-type"))
							index4.setAlarmUnitType(value);
						if (key.equals("alarm-type"))
							index4.setAlarmType(value);

						String auditString = key + " : " + value;
						if (!key.equals(auditIndex41)
								&& (auditIssueList != null && auditIssueList.toString().contains(auditString))) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							index4.setAuditStatus("Failed");
							index4.setAuditResult(auditIssue.toString());
						} else
							index4.setAuditStatus("Passed");
						// index4List.add(isIndex, index4);
					}
				}
				index4List.add(index4);
			}

			// Index5

			if (resultTableData.containsKey(auditIndex51) || resultTableData.containsKey(auditIndex52)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();
				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX5_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("fsu-id"))
							index5.setFsuId(value);
						if (key.equals("support-cell-number"))
							index5.setSupportCellNumber(value);
						if (key.equals("connected-pod-type"))
							index5.setConnectedPodType(value);
						if (key.equals("connected-pod-id"))
							index5.setConnectedPodId(value);
						if (key.equals("connected-pod-port-id"))
							index5.setConnectedPodId(value);
						if (key.equals("connected-pod-id"))
							index5.setConnectedPodPortId(value);
						if (key.equals("vlan-id"))
							index5.setVlanId(value);

						String auditString = key + " : " + value;
						if (!key.equals(auditIndex51)
								&& (auditIssueList != null && auditIssueList.toString().contains(auditString))) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							index5.setAuditStatus("Failed");
							index5.setAuditResult(auditIssue.toString());
						} else
							index5.setAuditStatus("Passed");
						// index5List.add(isIndex, index5);
					}
				}
				index5List.add(index5);
			}
			// Index6

			if (resultTableData.containsKey(auditIndex61) || resultTableData.containsKey(auditIndex62)) {
				String value;
				StringBuilder auditIssue = new StringBuilder();
				for (String key : AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX6_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (key.equals("fsu-id"))
							if (key.equals("pod-id"))
								index6.setPodId(value);
						if (key.equals("pod-type"))
							index6.setPodType(value);
						if (key.equals("dss"))
							index6.setDss(value);
						if (key.equals("ip"))
							index6.setIp(value);
						if (key.equals("snc-state"))
							index6.setSncState(value);
						if (key.equals("gateway"))
							index6.setGateway(value);
						if (key.equals("mtu"))
							index6.setMtu(value);

						if (key.equals("dss"))
							udaAudit = "Passed";
						
						String auditString = key + " : " + value;
						if (!key.equals(auditIndex61)
								&& (auditIssueList != null && auditIssueList.toString().contains(auditString))) {
							if (auditIssue.length() != 0)
								auditIssue.append(sep);
							auditIssue.append(auditString);
							if (key.equals("dss"))
								udaAudit = "Failed";
							auditCriticalParamsService.updateAuditStatus(auditCriticalParamsEntity.getRunTestEntity().getId(), sfpAudit, retAudit, udaAudit, hwAudit);
							index6.setAuditStatus("Failed");
							auditCriticalParamsEntity.setUdaAuditStatus("Failed");
							index6.setAuditResult(auditIssue.toString());
						} else
							index6.setAuditStatus("Passed");
						// index6List.add(isIndex, index6);
					}
				}
				index6List.add(index6);
			}
		}

		for (AuditCriticalParamsIndex1Entity e : index1List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex1Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex2Entity e : index2List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex2Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex3Entity e : index3List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex3Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex4Entity e : index4List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex4Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex5Entity e : index5List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex5Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex6Entity e : index6List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex6Entity(e, auditCriticalParamsEntity);
		}

	}

	@Override
	public List<AuditCriticalParamsIndex1Entity> getAuditCriticalParamsIndex1Entity() {

		List<AuditCriticalParamsIndex1Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex1Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex2Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsIndex2Entity> getAuditCriticalParamsIndex2Entity() {

		List<AuditCriticalParamsIndex2Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex2Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex2Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsIndex3Entity> getAuditCriticalParamsIndex3Entity() {

		List<AuditCriticalParamsIndex3Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex3Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex3Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsIndex4Entity> getAuditCriticalParamsIndex4Entity() {

		List<AuditCriticalParamsIndex4Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex4Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex4Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsIndex5Entity> getAuditCriticalParamsIndex5Entity() {

		List<AuditCriticalParamsIndex5Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex5Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex5Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsIndex6Entity> getAuditCriticalParamsIndex6Entity() {

		List<AuditCriticalParamsIndex6Entity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex6Entity();

		} catch (Exception e) {
			logger.error("Exception in getAuditCriticalParamsIndex6Entity()   AuditCriticalParamsSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean createAuditCriticalParamsBulkReportExcel(JSONObject auditCriticalParamsReportDetails) {
		boolean status = false;
		try {
			List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityResultList = null;
			List<AuditCriticalParamsIndex1Entity> auditCriticalParamsIndex1EntityList = null;
			List<AuditCriticalParamsIndex2Entity> auditCriticalParamsIndex2EntityList = null;
			List<AuditCriticalParamsIndex3Entity> auditCriticalParamsIndex3EntityList = null;
			List<AuditCriticalParamsIndex4Entity> auditCriticalParamsIndex4EntityList = null;
			List<AuditCriticalParamsIndex5Entity> auditCriticalParamsIndex5EntityList = null;
			List<AuditCriticalParamsIndex6Entity> auditCriticalParamsIndex6EntityList = null;

			auditCriticalParamsSummaryEntityResultList = auditCriticalParamsService
					.getAuditCriticalParamsSummaryEntityList();
			auditCriticalParamsIndex1EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex1Entity();
			auditCriticalParamsIndex2EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex2Entity();
			auditCriticalParamsIndex3EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex3Entity();
			auditCriticalParamsIndex4EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex4Entity();
			auditCriticalParamsIndex5EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex5Entity();
			auditCriticalParamsIndex6EntityList = auditCriticalParamsService.getAuditCriticalParamsIndex6Entity();

			if (auditCriticalParamsSummaryEntityResultList != null
					&& !auditCriticalParamsSummaryEntityResultList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_SUMMAR_REPORT);
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 10);
				headerFont.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setFont(headerFont);

				XSSFCellStyle failureCellStyle = workbook.createCellStyle();
				// failureCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,
				// 204, 203)));
				failureCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 127, 127)));
				failureCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				Row headerRow = sheet.createRow(1);

				XSSFCellStyle cellStyle = workbook.createCellStyle();
				XSSFCellStyle cellStyleDate = workbook.createCellStyle();
				XSSFCreationHelper createHelper = workbook.getCreationHelper();
				cellStyle.setWrapText(true);
				cellStyleDate.setWrapText(true);
				cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat(Constants.YYYY_MM_DD_HH_MM_SS));

				String[] columnHeaderNames = Constants.AUDIT_CRITICAL_PARAMS_SUMMARY_REPORT_HEADERS;
				for (int i = 0; i < columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}

				int rowCount = 2;
				for (AuditCriticalParamsSummaryEntity auditCriticalSummaryModel : auditCriticalParamsSummaryEntityResultList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getUserName());
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getSfpAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getRetAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getUdaAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getHwAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getStatus());
					// cell.setHyperlink(arg0);

				}

				Sheet sheet1 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT1);
				Row headerRow1 = sheet1.createRow(1);
				String[] columnHeaderNames1 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS1;
				for (int i = 0; i < columnHeaderNames1.length; i++) {
					sheet1.setColumnWidth(i, 9000);
					Cell cell = headerRow1.createCell(i);
					cell.setCellValue(columnHeaderNames1[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex1Entity auditCriticalSummaryModel : auditCriticalParamsIndex1EntityList) {
					Row row = sheet1.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					if (auditCriticalSummaryModel.getCellIdentity() != null)
						cell.setCellValue(auditCriticalSummaryModel.getCellIdentity());

					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("spectrum-sharing ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSpectrumSharing() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSpectrumSharing());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("slot-level-operation-mode ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSlotLevelOperationalMode() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSlotLevelOperationalMode());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);

					if (auditCriticalSummaryModel.getCellNum() != null)
						cell.setCellValue(auditCriticalSummaryModel.getCellNum());
					else
						cell.setCellValue("N/A");

					// Need to change to user-label
					cell = row.createCell(cellCount++);
					if (result != null && result.contains("user-label ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getUlAntennaCount() != null)
						cell.setCellValue(auditCriticalSummaryModel.getUlAntennaCount());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("dl-antenna-count ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getDlAntennaCount() != null)
						cell.setCellValue(auditCriticalSummaryModel.getDlAntennaCount());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("dl-antenna-count ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getUlAntennaCount() != null)
						cell.setCellValue(auditCriticalSummaryModel.getUlAntennaCount());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("number-of-rx-paths-per-ru ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getNumberOfRxPathsPerRU() != null)
						cell.setCellValue(auditCriticalSummaryModel.getNumberOfRxPathsPerRU());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("cell-path-type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getCellPathType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getCellPathType());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);

					if (result != null && result.contains("administrative-state ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getAdministrativeState() != null)
						cell.setCellValue(auditCriticalSummaryModel.getAdministrativeState());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("operational-state ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getOperationalState() != null)
						cell.setCellValue(auditCriticalSummaryModel.getOperationalState());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("activation-state ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getActivationState() != null)
						cell.setCellValue(auditCriticalSummaryModel.getActivationState());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("power ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getPower() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPower());
					else
						cell.setCellValue("N/A");
				}

				// Index2
				Sheet sheet2 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT2);
				Row headerRow2 = sheet2.createRow(1);
				String[] columnHeaderNames2 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS2;
				for (int i = 0; i < columnHeaderNames2.length; i++) {
					sheet2.setColumnWidth(i, 9000);
					Cell cell = headerRow2.createCell(i);
					cell.setCellValue(columnHeaderNames2[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex2Entity auditCriticalSummaryModel : auditCriticalParamsIndex2EntityList) {
					Row row = sheet2.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					if (auditCriticalSummaryModel.getUnitType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getUnitType());

					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);

					cell.setCellStyle(cellStyle);
					if (auditCriticalSummaryModel.getUnitId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getUnitId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("port-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getPortId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPortId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("tx-power ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getTxPower() != null)
						cell.setCellValue(auditCriticalSummaryModel.getTxPower());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("rx-power ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getRxPower() != null)
						cell.setCellValue(auditCriticalSummaryModel.getRxPower());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-du-cpri-port-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedDuCpriPortId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedDuCpriPortId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-enb-digital-unit-board-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedEnbDigitalUnitBoardId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedEnbDigitalUnitBoardId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-enb-digital-unit-port-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedEnbDigitalUnitPortId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedEnbDigitalUnitPortId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("du-cpri-port-mode ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getDuCpriPortMode() != null)
						cell.setCellValue(auditCriticalSummaryModel.getDuCpriPortMode());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("pri-port-mode ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getPriPortMode() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPriPortMode());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("hardware-name ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getHardWareName() != null)
						cell.setCellValue(auditCriticalSummaryModel.getHardWareName());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("mplan-ipv6 ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getMplaneIpv6() != null)
						cell.setCellValue(auditCriticalSummaryModel.getMplaneIpv6());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("enb-ne-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getEnbNeId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getEnbNeId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-digital-unit-board-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedDigitalUnitBoardId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedDigitalUnitBoardId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("radio-unit-port-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getRadioUnitPortId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getRadioUnitPortId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("vendor-name ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getVendorName() != null)
						cell.setCellValue(auditCriticalSummaryModel.getVendorName());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("firmware-name ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getFirmwareName() != null)
						cell.setCellValue(auditCriticalSummaryModel.getFirmwareName());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("package-version ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getPackageVersion() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPackageVersion());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("patch-version ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getPatchVersion() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPatchVersion());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("software-name ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSoftwareName() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSoftwareName());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("software-version ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSoftwareVersion() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSoftwareVersion());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("cpri-speed-running ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getCpriSpeedRunning() != null)
						cell.setCellValue(auditCriticalSummaryModel.getCpriSpeedRunning());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("tx-wavelength ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getTxWavelength() != null)
						cell.setCellValue(auditCriticalSummaryModel.getTxWavelength());
					else
						cell.setCellValue("N/A");
				}

				// Index3
				Sheet sheet3 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT3);
				Row headerRow3 = sheet3.createRow(1);
				String[] columnHeaderNames3 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS3;
				for (int i = 0; i < columnHeaderNames3.length; i++) {
					sheet3.setColumnWidth(i, 9000);
					Cell cell = headerRow3.createCell(i);
					cell.setCellValue(columnHeaderNames3[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex3Entity auditCriticalSummaryModel : auditCriticalParamsIndex3EntityList) {
					Row row = sheet3.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());

					cell = row.createCell(cellCount++);

					cell.setCellStyle(cellStyle);
					if (auditCriticalSummaryModel.getNeId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getNeId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("ne_type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getNeType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getNeType());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("sw-version ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}

					if (auditCriticalSummaryModel.getSwVersion() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSwVersion());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("flavor-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getFlavorId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getFlavorId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("ip-address ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getIpSddress() != null)
						cell.setCellValue(auditCriticalSummaryModel.getIpSddress());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("f1-app-state ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getF1ApState() != null)
						cell.setCellValue(auditCriticalSummaryModel.getF1ApState());
					else
						cell.setCellValue("N/A");
				}

				// Index4
				Sheet sheet4 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT4);
				Row headerRow4 = sheet4.createRow(1);
				String[] columnHeaderNames4 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS4;
				for (int i = 0; i < columnHeaderNames4.length; i++) {
					sheet4.setColumnWidth(i, 9000);
					Cell cell = headerRow4.createCell(i);
					cell.setCellValue(columnHeaderNames4[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex4Entity auditCriticalSummaryModel : auditCriticalParamsIndex4EntityList) {
					Row row = sheet4.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("ne_id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}

					if (auditCriticalSummaryModel.getNeId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getNeId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("alarm-unit-type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getAlarmUnitType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getAlarmUnitType());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("alarm-type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getAlarmType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getAlarmType());
					else
						cell.setCellValue("N/A");

				}
				// Index5
				Sheet sheet5 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT5);
				Row headerRow5 = sheet5.createRow(1);
				String[] columnHeaderNames5 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS5;
				for (int i = 0; i < columnHeaderNames5.length; i++) {
					sheet5.setColumnWidth(i, 9000);
					Cell cell = headerRow5.createCell(i);
					cell.setCellValue(columnHeaderNames5[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex5Entity auditCriticalSummaryModel : auditCriticalParamsIndex5EntityList) {
					Row row = sheet5.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("fsu-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getFsuId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getFsuId());

					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("support-cell-number ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSupportCellNumber() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSupportCellNumber());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-pod-type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedPodType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedPodType());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-pod-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedPodId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedPodId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("connected-pod-port-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getConnectedPodPortId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getConnectedPodPortId());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("vlan-id ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getVlanId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getVlanId());
					else
						cell.setCellValue("N/A");

				}

				// Index 6
				Sheet sheet6 = workbook.createSheet(Constants.AUDIT_CRITICAL_PARAMS_REPORT6);
				Row headerRow6 = sheet6.createRow(1);
				String[] columnHeaderNames6 = Constants.AUDIT_CRITICAL_PARAMS_REPORT_HEADERS6;
				for (int i = 0; i < columnHeaderNames6.length; i++) {
					sheet6.setColumnWidth(i, 9000);
					Cell cell = headerRow6.createCell(i);
					cell.setCellValue(columnHeaderNames6[i]);
					cell.setCellStyle(headerCellStyle);
				}

				rowCount = 2;
				for (AuditCriticalParamsIndex6Entity auditCriticalSummaryModel : auditCriticalParamsIndex6EntityList) {
					Row row = sheet6.createRow(rowCount++);
					int cellCount = 0;
					String result = auditCriticalSummaryModel.getAuditResult();

					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyleDate);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getCreationDate());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(
							auditCriticalSummaryModel.getAuditCriticalParamEntity().getRunTestEntity().getId());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getSiteName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getProgramName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditCriticalParamEntity().getUserName());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(auditCriticalSummaryModel.getAuditStatus());

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					if (auditCriticalSummaryModel.getPodId() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPodId());

					else
						cell.setCellValue("N/A");
					cell = row.createCell(cellCount++);

					if (result != null && result.contains("dss ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getDss() != null)
						cell.setCellValue(auditCriticalSummaryModel.getDss());
					else
						cell.setCellValue("N/A");
					cell = row.createCell(cellCount++);
					if (result != null && result.contains("ip ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getIp() != null)
						cell.setCellValue(auditCriticalSummaryModel.getIp());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("pod-type ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}

					if (auditCriticalSummaryModel.getPodType() != null)
						cell.setCellValue(auditCriticalSummaryModel.getPodType());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("snc-state ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getSncState() != null)
						cell.setCellValue(auditCriticalSummaryModel.getSncState());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("gateway ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getGateway() != null)
						cell.setCellValue(auditCriticalSummaryModel.getGateway());
					else
						cell.setCellValue("N/A");

					cell = row.createCell(cellCount++);
					if (result != null && result.contains("mtu ")) {
						cell.setCellStyle(failureCellStyle);
					} else {
						cell.setCellStyle(cellStyle);
					}
					if (auditCriticalSummaryModel.getMtu() != null)
						cell.setCellValue(auditCriticalSummaryModel.getMtu());
					else
						cell.setCellValue("N/A");
				}

				/*
				 * String fileName = "AUDIT_CRITICAL_PARAMS_REPORT.xlsx"; try (FileOutputStream
				 * fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
				 * workbook.write(fileOut); workbook.close(); status = true; } catch (Exception
				 * e) { status = false; logger.error(ExceptionUtils.getFullStackTrace(e)); }
				 */

				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.AUDIT_CRITICAL_PARAMS_DETAILS));
				File networkConfigDirectory = new File(fileNameBuilder.toString());
				if (!networkConfigDirectory.exists()) {
					networkConfigDirectory.mkdir();
				}
				fileNameBuilder.append(Constants.AUDIT_CRITICAL_PARAMS_SUMMARY_XLSX);

				// Write the output to a file
				FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
				workbook.write(fileOut);
				fileOut.close();
				workbook.close();
				status = true;

			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception in createAuditCriticalParamsBulkReportExcel()   AuditCriticalParamsServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityById(Integer id) {

		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository
					.getAuditCriticalParamsSummaryEntityById(id);

		} catch (Exception e) {
			logger.error(
					"Exception in getAuditCriticaParamsSummaryEntityById()   AuditCriticalParamsSummaryServiceImpl:"
							+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	@Override
	public List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityList() {
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityList = null;
		try {
			auditCriticalParamsSummaryEntityList = auditCriticalParamsRepository
					.getAuditCriticalParamsSummaryEntityList();

		} catch (Exception e) {
			logger.error(
					"Exception in getAuditCriticaParamsSummaryEntityById()   AuditCriticalParamsSummaryServiceImpl:"
							+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditCriticalParamsSummaryEntityList;
	}

	public AuditCriticalParamsSummaryModel auditStatusEntityDto(
			AuditCriticalParamsSummaryEntity auditCriticalSummaryEntity) {
		AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);

			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			if (auditCriticalSummaryEntity != null) {
				auditCriticalParamsSummaryModel = new AuditCriticalParamsSummaryModel();
				ModelMapper modelMapper = new ModelMapper();
				// AuditCriticalParamsSummaryModel auditCriticalParamsSummaryModel =
				// modelMapper.map(auditCriticalSummaryEntity,
				// AuditCriticalParamsSummaryModel.class);
				auditCriticalParamsSummaryModel.setId(Integer.valueOf(auditCriticalSummaryEntity.getId()));
				auditCriticalParamsSummaryModel.setNeName(auditCriticalSummaryEntity.getNeName());
				auditCriticalParamsSummaryModel.setNeId(auditCriticalSummaryEntity.getNeId());
				auditCriticalParamsSummaryModel.setProgramName(auditCriticalSummaryEntity.getProgramName());
				auditCriticalParamsSummaryModel.setSiteName(auditCriticalSummaryEntity.getSiteName());
				auditCriticalParamsSummaryModel.setUserName(auditCriticalSummaryEntity.getUserName());
				auditCriticalParamsSummaryModel.setStatus(auditCriticalSummaryEntity.getStatus());
				auditCriticalParamsSummaryModel.setRunTestId(auditCriticalSummaryEntity.getRunTestEntity().getId());
				auditCriticalParamsSummaryModel.setSfpStatus(auditCriticalSummaryEntity.getSfpAuditStatus());
				auditCriticalParamsSummaryModel.setRetStatus(auditCriticalSummaryEntity.getRetAuditStatus());
				auditCriticalParamsSummaryModel.setUdaStatus(auditCriticalSummaryEntity.getUdaAuditStatus());
				auditCriticalParamsSummaryModel.setHwStatus(auditCriticalSummaryEntity.getHwAuditStatus());

				sdf.setTimeZone(TimeZone.getDefault());
				auditCriticalParamsSummaryModel.setTimeStamp(sdf.format(auditCriticalSummaryEntity.getCreationDate()));
			}

		} catch (Exception e) {
			logger.error("Excpetion getciqAuditDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}

		return auditCriticalParamsSummaryModel;
	}

	@Override
	public Map<String, Object> getAuditCriticalParamsSummaryEntityList(int page, int count, String fromDate,
			String toDate) {
		Map<String, Object> auditSummaryEntity = null;
		try {
			List<AuditCriticalParamsSummaryModel> listAuditSummaryModel = new ArrayList<>();
			auditSummaryEntity = auditCriticalParamsRepository.getAuditCriticalParamsSummaryEntityList(page, count,
					fromDate, toDate);

			if (!ObjectUtils.isEmpty(auditSummaryEntity) && auditSummaryEntity.containsKey("auditStatusList")) {
				List<AuditCriticalParamsSummaryEntity> auditStatusEntities = (List<AuditCriticalParamsSummaryEntity>) auditSummaryEntity
						.get("auditStatusList");
				if (!ObjectUtils.isEmpty(auditStatusEntities)) {
					for (AuditCriticalParamsSummaryEntity auditStatusSummaryEntity : auditStatusEntities) {
						listAuditSummaryModel.add(auditStatusEntityDto(auditStatusSummaryEntity));
					}
				}

				auditSummaryEntity.put("auditStatusList", listAuditSummaryModel);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return auditSummaryEntity;
		/*
		 * List<AuditCriticalParamsSummaryEntity> auditCriticalParamsSummaryEntityList =
		 * null; try { auditCriticalParamsSummaryEntityList =
		 * auditCriticalParamsRepository.getAuditCriticalParamsSummaryEntityList(page,
		 * count, fromDate, toDate, neId, neName, runTestId, siteName, programName,
		 * status);
		 * 
		 * } catch (Exception e) { logger.
		 * error("Exception in getAuditCriticaParamsSummaryEntity()   AuditCriticalParamsSummaryServiceImpl:"
		 * + ExceptionUtils.getFullStackTrace(e)); } return
		 * auditCriticalParamsSummaryEntityList;
		 */
	}

	@Override
	public Map<String, Object> getAuditCriticalParamsSearchSummaryEntityList(
			AuditCriticalParamsSummaryModel auditSummaryModel, int page, int count) {
		Map<String, Object> auditSummaryEntity = null;
		try {
			List<AuditCriticalParamsSummaryModel> listAuditSummaryModel = new ArrayList<>();
			auditSummaryEntity = auditCriticalParamsRepository
					.getAuditCriticalParamsSearchSummaryEntityList(auditSummaryModel, page, count);

			if (auditSummaryEntity != null && !ObjectUtils.isEmpty(auditSummaryEntity)
					&& auditSummaryEntity.containsKey("auditStatusList")) {
				@SuppressWarnings("unchecked")
				List<AuditCriticalParamsSummaryEntity> auditStatusEntities = (List<AuditCriticalParamsSummaryEntity>) auditSummaryEntity
						.get("auditStatusList");
				if (!ObjectUtils.isEmpty(auditStatusEntities)) {
					for (AuditCriticalParamsSummaryEntity auditStatusSummaryEntity : auditStatusEntities) {
						listAuditSummaryModel.add(auditStatusEntityDto(auditStatusSummaryEntity));
					}
				}

				auditSummaryEntity.put("auditStatusList", listAuditSummaryModel);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return auditSummaryEntity;

	}
	
	@Override
	public boolean deleteAuditCriticalSummaryEntityByRunTestId(int runTestId) {
		boolean status = true;
		try {
			
			List<AuditCriticalParamsSummaryEntity> auditCriticalSummaryEntityList = getAuditCriticalParamsSummaryEntityById(
					runTestId);
			if (auditCriticalSummaryEntityList != null && !auditCriticalSummaryEntityList.isEmpty()) {
				AuditCriticalParamsSummaryEntity auditCriticalParamEntity = auditCriticalSummaryEntityList.get(0);
				Integer indexId = auditCriticalParamEntity.getId();

				auditCriticalParamsRepository.deleteAuditCriticalIndex1EntityByRunTestId(indexId);
				auditCriticalParamsRepository.deleteAuditCriticalIndex2EntityByRunTestId(indexId);
				auditCriticalParamsRepository.deleteAuditCriticalIndex3EntityByRunTestId(indexId);
				auditCriticalParamsRepository.deleteAuditCriticalIndex4EntityByRunTestId(indexId);
				auditCriticalParamsRepository.deleteAuditCriticalIndex5EntityByRunTestId(indexId);
				auditCriticalParamsRepository.deleteAuditCriticalIndex6EntityByRunTestId(indexId);
				status = auditCriticalParamsRepository
						.deleteAuditCriticalSummaryEntityByRunTestId(auditCriticalSummaryEntityList.get(0).getId());
			}

		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@Override
	public boolean deleteAuditCriticalIndex1EntityByRunTestId(int indexId) {
		boolean status = true;
		try {
			List<AuditCriticalParamsIndex1Entity> auditCriticalIndex1EntityList = auditCriticalParamsRepository.getAuditCriticalParamsIndex1EntityById(indexId);
			System.out.print("=============auditCriticalIndex1EntityList.get(0).getRunTestEntity().getId()"+ auditCriticalIndex1EntityList.get(0).getAuditCriticalParamEntity().getId() +"\n"+ auditCriticalIndex1EntityList.size()+"\n");
			if(auditCriticalIndex1EntityList != null && !auditCriticalIndex1EntityList.isEmpty()) {
				status = auditCriticalParamsRepository.deleteAuditCriticalSummaryEntityByRunTestId(auditCriticalIndex1EntityList.get(0).getAuditCriticalParamEntity().getId());
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
}
