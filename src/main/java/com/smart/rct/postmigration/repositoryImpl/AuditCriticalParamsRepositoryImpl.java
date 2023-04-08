package com.smart.rct.postmigration.repositoryImpl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex1Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex2Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex3Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex4Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex5Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsIndex6Entity;
import com.smart.rct.postmigration.entity.AuditCriticalParamsSummaryEntity;
import com.smart.rct.postmigration.models.AuditCriticalParamsSummaryModel;
import com.smart.rct.postmigration.repository.AuditCriticalParamsRepository;
import com.smart.rct.postmigration.service.AuditCriticalParamsService;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.util.DateUtil;


@Transactional
@Repository
@Component

public class AuditCriticalParamsRepositoryImpl implements AuditCriticalParamsRepository {
	
	final static Logger logger = LoggerFactory.getLogger(AuditCriticalParamsRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Autowired
	AuditCriticalParamsService auditCriticalParamsService;
	
	@Override
	public AuditCriticalParamsSummaryEntity createAuditCriticalParamsSummaryEntity(
			AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntity) {
		// TODO Auto-generated method stub
		AuditCriticalParamsSummaryEntity auditCriticalParamsEntityResult = null;
		try {
			auditCriticalParamsEntityResult = entityManager.merge(auditCriticalParamsSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsSummaryEntity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsEntityResult;
	}

	@Override
	public AuditCriticalParamsSummaryEntity updateAuditCriticalParamsSummaryEntity(
			AuditCriticalParamsSummaryEntity auditCriticalParamsSummaryEntity) {
		// TODO Auto-generated method stub
		AuditCriticalParamsSummaryEntity auditCriticalParamsEntityResult = null;
		try {
			auditCriticalParamsEntityResult = entityManager.merge(auditCriticalParamsSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in  updateAuditCriticalParamsSummaryEntity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsEntityResult;
	}
	@Override
	public AuditCriticalParamsIndex1Entity createAuditCriticalParamsIndex1Entity(
			AuditCriticalParamsIndex1Entity auditCriticalParamsIndex1Entity) {
		AuditCriticalParamsIndex1Entity auditCriticalParamsIndex1EntityResult = null;
		try {
			auditCriticalParamsIndex1EntityResult = entityManager.merge(auditCriticalParamsIndex1Entity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsIndex1Entity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex1EntityResult;
	}
	
	@Override
	public AuditCriticalParamsIndex2Entity createAuditCriticalParamsIndex2Entity(
			AuditCriticalParamsIndex2Entity auditCriticalParamsIndex2Entity) {
		AuditCriticalParamsIndex2Entity auditCriticalParamsIndex2EntityResult = null;
		try {
			auditCriticalParamsIndex2EntityResult = entityManager.merge(auditCriticalParamsIndex2Entity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsIndex2Entity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex2EntityResult;
	}
	
	@Override
	public AuditCriticalParamsIndex3Entity createAuditCriticalParamsIndex3Entity(
			AuditCriticalParamsIndex3Entity auditCriticalParamsIndex3Entity) {
		AuditCriticalParamsIndex3Entity auditCriticalParamsIndex3EntityResult = null;
		try {
			auditCriticalParamsIndex3EntityResult = entityManager.merge(auditCriticalParamsIndex3Entity);
		} catch (Exception e) {
			logger.error("Exception in  AuditCriticalParamsIndex3Entity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex3EntityResult;
	}
	
	@Override
	public AuditCriticalParamsIndex4Entity createAuditCriticalParamsIndex4Entity(
			AuditCriticalParamsIndex4Entity auditCriticalParamsIndex4Entity) {
		AuditCriticalParamsIndex4Entity auditCriticalParamsIndex4EntityResult = null;
		try {
			auditCriticalParamsIndex4EntityResult = entityManager.merge(auditCriticalParamsIndex4Entity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsIndex4Entity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex4EntityResult;
	}
	@Override
	public AuditCriticalParamsIndex5Entity createAuditCriticalParamsIndex5Entity(
			AuditCriticalParamsIndex5Entity auditCriticalParamsIndex5Entity) {
		AuditCriticalParamsIndex5Entity auditCriticalParamsIndex5EntityResult = null;
		try {
			auditCriticalParamsIndex5EntityResult = entityManager.merge(auditCriticalParamsIndex5Entity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsIndex5Entity() in  AuditCriticalParamsIndex5Entity:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex5EntityResult;
	}
	@Override
	public AuditCriticalParamsIndex6Entity createAuditCriticalParamsIndex6Entity(
			AuditCriticalParamsIndex6Entity auditCriticalParamsIndex6Entity) {
		AuditCriticalParamsIndex6Entity auditCriticalParamsIndex6EntityResult = null;
		try {
			auditCriticalParamsIndex6EntityResult = entityManager.merge(auditCriticalParamsIndex6Entity);
		} catch (Exception e) {
			logger.error("Exception in  createAuditCriticalParamsIndex6Entity() in  AuditCriticalParamsRepository:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			//entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndex6EntityResult;
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
			if (resultTableData.containsKey(auditIndex11) ||  resultTableData.containsKey(auditIndex12)) {
					boolean isFound = false;
					int isIndex = -1;
					int auditIndex11Value = -1, auditIndex12Value = -1;
					String value;
					if(resultTableData.get(auditIndex11) != null)
						auditIndex11Value = Integer.parseInt(resultTableData.get(auditIndex11));
					
					if(resultTableData.get(auditIndex12) != null)
						auditIndex12Value = Integer.parseInt(resultTableData.get(auditIndex12));
					

					if (!index1List.isEmpty()) {
						for (int i = 0; i < index1List.size(); i++) {
							AuditCriticalParamsIndex1Entity index11 = index1List.get(i);
							if ((auditIndex11Value != -1 && auditIndex11Value == index11.getCellNum())  || (auditIndex12Value != -1 && auditIndex12Value == index11.getCellIdentity())) {
								isFound = true;
								index1 = index11;
								isIndex = i;
								break;
							}
						}
					}
					StringBuilder auditIssue = new StringBuilder();
					for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX1_HEADERS) {
						value = resultTableData.get(key);

						if (resultTableData.containsKey(key)) {
							if (isFound && isIndex != -1) {
								if (key.equals("cell-num"))
									index1.setCellNum(Integer.parseInt(value));
								if (key.equals("cell-identity"))
									index1.setCellIdentity(Integer.parseInt(value));
								if (key.equals("operational-state"))
									index1.setOperationalState(value);
								if (key.equals("administrative-state"))
									index1.setAdministrativeState(value);
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
								
								System.out.println("===auditIssue:"+ auditIssue);
								String auditString = key+" : "+value;
								System.out.println("================auditString: "+ auditString);
								if(! key.equals(auditIndex11) && auditIssueList.toString().contains(auditString)) {
									System.out.println("================contains==========");
									if(auditIssue.length() != 0)
										auditIssue.append(sep);
									auditIssue.append(auditString);
									index1.setAuditStatus("Failure");
									index1.setAuditResult(auditString);
								}
								index1List.add(isIndex, index1);
							}
							else {
								System.out.println("================Not Fund");
								if (key.equals("cell-num"))
									index1.setCellNum(Integer.parseInt(value));
								if (key.equals("cell-identity"))
									index1.setCellIdentity(Integer.parseInt(value));
								if (key.equals("operational-state"))
									index1.setOperationalState(value);
								if (key.equals("administrative-state"))
									index1.setAdministrativeState(value);
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
								
								System.out.println("===auditIssue:"+ auditIssue);
								String auditString = key+" : "+value;
								System.out.println("================auditString: "+ auditString);
								if(! key.equals(auditIndex11) && auditIssueList.toString().contains(auditString)) {
									System.out.println("================contains==========");
									if(auditIssue.length() != 0)
										auditIssue.append(sep);
									auditIssue.append(auditString);
									index1.setAuditStatus("Failure");
									index1.setAuditResult(auditString);
								}
								index1List.add(index1);
							}
																			
						}
					}
															
			}
			
			// Index2
			
			if (resultTableData.containsKey(auditIndex21) ||  resultTableData.containsKey(auditIndex22)) {
				boolean isFound = false;
				int isIndex = -1;
				String auditIndex21Value = null, auditIndex22Value = null;
				String value;
				StringBuilder auditIssue = new StringBuilder();
				if(resultTableData.get(auditIndex21) != null)
					auditIndex21Value = resultTableData.get(auditIndex21);
				if(resultTableData.get(auditIndex22) != null)
					auditIndex22Value = resultTableData.get(auditIndex22);
				

				if (!index2List.isEmpty()) {
					for (int i = 0; i < index2List.size(); i++) {
						AuditCriticalParamsIndex2Entity index = index2List.get(i);
						if ((auditIndex21Value != null && auditIndex21Value.equals(index.getUnitType()))  || (auditIndex22Value != null && auditIndex22Value.equals(index.getUnitId()))) {
							isFound = true;
							index2 = index;
							isIndex = i;
							break;
						}
					}
				}
				for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX2_HEADERS) {
					value = resultTableData.get(key);
					if (resultTableData.containsKey(key)) {
						if (isFound && isIndex != -1) {
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
							if (key.equals("rx-power"))
								index2.setConnectedDuCpriPortId("connected-du-cpri-port-id");
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
							
							String auditString = key+" : "+value;
							if(! key.equals(auditIndex21) && auditIssueList.toString().contains(auditString)) {
								if(auditIssue.length() != 0)
									auditIssue.append(sep);
								auditIssue.append(auditString);
								index2.setAuditStatus("Failure");
								index2.setAuditResult(auditString);
							}
							
							index2List.add(isIndex, index2);
						}
						else {
							if (key.equals("cell-num"))
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
								if (key.equals("rx-power"))
									index2.setConnectedDuCpriPortId("connected-du-cpri-port-id");
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
								String auditString = key+" : "+value;
								if(! key.equals(auditIndex21) && auditIssueList.toString().contains(auditString)) {
									if(auditIssue.length() != 0)
										auditIssue.append(sep);
									auditIssue.append(auditString);
									index2.setAuditStatus("Failure");
									index2.setAuditResult(auditString);
								}
							index2List.add(index2);
						}
																		
					}
				}
														
		}
			
			// Index3
			
						if (resultTableData.containsKey(auditIndex31) ||  resultTableData.containsKey(auditIndex32)) {
							boolean isFound = false;
							int isIndex = -1;
							String auditIndex31Value = null, auditIndex32Value = null;
							String value;
							StringBuilder auditIssue = new StringBuilder();
							if(resultTableData.get(auditIndex31) != null)
								auditIndex31Value = resultTableData.get(auditIndex31);
							if(resultTableData.get(auditIndex32) != null)
								auditIndex32Value = resultTableData.get(auditIndex32);
							

							if (!index3List.isEmpty()) {
								for (int i = 0; i < index3List.size(); i++) {
									AuditCriticalParamsIndex3Entity index = index3List.get(i);
									if ((auditIndex31Value != null && auditIndex31Value.equals(index.getNeId()))  || (auditIndex32Value != null && auditIndex32Value.equals(index.getFlavorId()))) {
										isFound = true;
										index3 = index;
										isIndex = i;
										break;
									}
								}
							}
							for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX3_HEADERS) {
								value = resultTableData.get(key);
								if (resultTableData.containsKey(key)) {
									if (isFound && isIndex != -1) {
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
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex31) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index3.setAuditStatus("Failure");
											index3.setAuditResult(auditString);
										}
										
										index3.setAuditStatus("Failed");
										index3List.add(isIndex, index3);
									}
									else {
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
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex31) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index3.setAuditStatus("Failure");
											index3.setAuditResult(auditString);
										}
										index3.setAuditStatus("Failed");
										index3List.add(index3);
									}
																					
								}
							}
																	
					}
						
						
						// Index4
						
						if (resultTableData.containsKey(auditIndex41) ||  resultTableData.containsKey(auditIndex42)) {
							boolean isFound = false;
							int isIndex = -1;
							String auditIndex41Value = null, auditIndex42Value = null;
							String value;
							StringBuilder auditIssue = new StringBuilder();
							if(resultTableData.get(auditIndex41) != null)
								auditIndex41Value = resultTableData.get(auditIndex41);
							if(resultTableData.get(auditIndex42) != null)
								auditIndex42Value = resultTableData.get(auditIndex42);

							if (!index4List.isEmpty()) {
								for (int i = 0; i < index4List.size(); i++) {
									AuditCriticalParamsIndex4Entity index = index4List.get(i);
									if ((auditIndex41Value != null && auditIndex41Value.equals(index.getNeId()))) {
										isFound = true;
										index4 = index;
										isIndex = i;
										break;
									}
								}
							}
							for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX4_HEADERS) {
								value = resultTableData.get(key);
								if (resultTableData.containsKey(key)) {
									if (isFound && isIndex != -1) {
										if (key.equals("ne-id"))
											index4.setNeId(value);
										if (key.equals("alarm-unit-type"))
											index4.setAlarmUnitType(value);
										if (key.equals("alarm-type"))
											index4.setAlarmType(value);
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex41) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index4.setAuditStatus("Failure");
											index4.setAuditResult(auditString);
										}
										index4List.add(isIndex, index4);
									}
									else {
										if (key.equals("ne-id"))
											index4.setNeId(value);
										if (key.equals("alarm-unit-type"))
											index4.setAlarmUnitType(value);
										if (key.equals("alarm-type"))
											index4.setAlarmType(value);
											
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex41) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index4.setAuditStatus("Failure");
											index4.setAuditResult(auditString);
										}
										index4List.add(index4);
									}
																					
								}
							}
																	
					}
						
						// Index5
						
						if (resultTableData.containsKey(auditIndex51) ||  resultTableData.containsKey(auditIndex52)) {
							boolean isFound = false;
							int isIndex = -1;
							String auditIndex51Value = null, auditIndex52Value = null;
							String value;
							StringBuilder auditIssue = new StringBuilder();

							if(resultTableData.get(auditIndex51) != null)
								auditIndex51Value = resultTableData.get(auditIndex51);
							if(resultTableData.get(auditIndex52) != null)
								auditIndex52Value = resultTableData.get(auditIndex52);

							if (!index5List.isEmpty()) {
								for (int i = 0; i < index5List.size(); i++) {
									AuditCriticalParamsIndex5Entity index = index5List.get(i);
									if ((auditIndex51Value != null && auditIndex51Value.equals(index.getFsuId()))) {
										isFound = true;
										index5 = index;
										isIndex = i;
										break;
									}
								}
							}
							for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX5_HEADERS) {
								value = resultTableData.get(key);
								if (resultTableData.containsKey(key)) {
									if (isFound && isIndex != -1) {
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
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex51) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index5.setAuditStatus("Failure");
											index5.setAuditResult(auditString);
										}
										index5List.add(isIndex, index5);
									}
									else {
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
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex51) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index5.setAuditStatus("Failure");
											index5.setAuditResult(auditString);
										}
										index5List.add(index5);
									}
																					
								}
							}
																	
					}
						// Index6
						
						if (resultTableData.containsKey(auditIndex61) ||  resultTableData.containsKey(auditIndex62)) {
							boolean isFound = false;
							int isIndex = -1;
							String auditIndex61Value = null, auditIndex62Value = null;
							String value;
							StringBuilder auditIssue = new StringBuilder();

							if(resultTableData.get(auditIndex61) != null)
								auditIndex61Value = resultTableData.get(auditIndex61);
							if(resultTableData.get(auditIndex62) != null)
								auditIndex62Value = resultTableData.get(auditIndex62);
							

							if (!index6List.isEmpty()) {
								for (int i = 0; i < index6List.size(); i++) {
									AuditCriticalParamsIndex6Entity index = index6List.get(i);
									if ((auditIndex61Value != null && auditIndex61Value.equals(index.getPodId()))) {
										isFound = true;
										index6 = index;
										isIndex = i;
										break;
									}
								}
							}
							for (String key: AuditConstants.AUDIT_CRITICAL_PARAMS_INDEX6_HEADERS) {
								value = resultTableData.get(key);
								if (resultTableData.containsKey(key)) {
									if (isFound && isIndex != -1) {
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
											
											String auditString = key+" : "+value;
											if(! key.equals(auditIndex61) && auditIssueList.toString().contains(auditString)) {
												if(auditIssue.length() != 0)
													auditIssue.append(sep);
												auditIssue.append(auditString);
												index5.setAuditStatus("Failure");
												index5.setAuditResult(auditString);
											}
										index6List.add(isIndex, index6);
									}
									else {
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
										
										String auditString = key+" : "+value;
										if(! key.equals(auditIndex61) && auditIssueList.toString().contains(auditString)) {
											if(auditIssue.length() != 0)
												auditIssue.append(sep);
											auditIssue.append(auditString);
											index5.setAuditStatus("Failure");
											index5.setAuditResult(auditString);
										}
										index6List.add(index6);
									}
																					
								}
							}
																	
					}

		}
		
		for (AuditCriticalParamsIndex1Entity e :  index1List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex1Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex2Entity e :  index2List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex2Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex3Entity e :  index3List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex3Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex4Entity e :  index4List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex4Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex5Entity e :  index5List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex5Entity(e, auditCriticalParamsEntity);
		}
		for (AuditCriticalParamsIndex6Entity e :  index6List) {
			auditCriticalParamsService.createAuditCriticalParamsIndex6Entity(e, auditCriticalParamsEntity);
		}
		
		

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityById(Integer runTestId) {
		
		  List<AuditCriticalParamsSummaryEntity> AuditCriticalParamsSummaryEntityList = null;
			try {
				@SuppressWarnings("deprecation")
				org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(AuditCriticalParamsSummaryEntity.class);
				criteria.createAlias("runTestEntity", "runTestEntity");
				Conjunction conjunction = Restrictions.conjunction();			
				conjunction.add(Restrictions.eq("runTestEntity.id", runTestId));
				criteria.add(conjunction);
				AuditCriticalParamsSummaryEntityList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return AuditCriticalParamsSummaryEntityList;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex1Entity> getAuditCriticalParamsIndex1EntityById(Integer indexId) {
		List<AuditCriticalParamsIndex1Entity> auditCriticalParamsIndex1Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex1Entity> query = cb.createQuery(AuditCriticalParamsIndex1Entity.class);
			Root<AuditCriticalParamsIndex1Entity> root = query.from(AuditCriticalParamsIndex1Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex1Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex1Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex1Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex1Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex1Entity;
	        
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex2Entity> getAuditCriticalParamsIndex2EntityById(Integer indexId) {
		
		List<AuditCriticalParamsIndex2Entity> auditCriticalParamsIndex2Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex2Entity> query = cb.createQuery(AuditCriticalParamsIndex2Entity.class);
			Root<AuditCriticalParamsIndex2Entity> root = query.from(AuditCriticalParamsIndex2Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex2Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex2Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex2Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex2Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex2Entity;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex3Entity> getAuditCriticalParamsIndex3EntityById(Integer indexId) {
		
		List<AuditCriticalParamsIndex3Entity> auditCriticalParamsIndex3Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex3Entity> query = cb.createQuery(AuditCriticalParamsIndex3Entity.class);
			Root<AuditCriticalParamsIndex3Entity> root = query.from(AuditCriticalParamsIndex3Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex3Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex3Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex3Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex3Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex3Entity;
	        
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex4Entity> getAuditCriticalParamsIndex4EntityById(Integer indexId) {
		
		List<AuditCriticalParamsIndex4Entity> auditCriticalParamsIndex4Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex4Entity> query = cb.createQuery(AuditCriticalParamsIndex4Entity.class);
			Root<AuditCriticalParamsIndex4Entity> root = query.from(AuditCriticalParamsIndex4Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex4Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex4Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex4Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex4Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex4Entity;
	        
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex5Entity> getAuditCriticalParamsIndex5EntityById(Integer indexId) {
		
		List<AuditCriticalParamsIndex5Entity> auditCriticalParamsIndex5Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex5Entity> query = cb.createQuery(AuditCriticalParamsIndex5Entity.class);
			Root<AuditCriticalParamsIndex5Entity> root = query.from(AuditCriticalParamsIndex5Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex5Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex5Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex5Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex5Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex5Entity;
	        
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex6Entity> getAuditCriticalParamsIndex6EntityById(Integer indexId) {
		
		List<AuditCriticalParamsIndex6Entity> auditCriticalParamsIndex6Entity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsIndex6Entity> query = cb.createQuery(AuditCriticalParamsIndex6Entity.class);
			Root<AuditCriticalParamsIndex6Entity> root = query.from(AuditCriticalParamsIndex6Entity.class);
			Predicate id = cb.equal(root.get("auditCriticalParamEntity").get("id"), indexId);
			query.where(id);
			TypedQuery<AuditCriticalParamsIndex6Entity> typedQuery = entityManager.createQuery(query);
			auditCriticalParamsIndex6Entity = typedQuery.getResultList();
			TypedQuery<AuditCriticalParamsIndex6Entity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndex6Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex6Entity;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsSummaryEntity> getAuditCriticalParamsSummaryEntityList() {
		
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsIndexEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<AuditCriticalParamsSummaryEntity> query = cb.createQuery(AuditCriticalParamsSummaryEntity.class);
			Root<AuditCriticalParamsSummaryEntity> root = query.from(AuditCriticalParamsSummaryEntity.class);
			query.orderBy(cb.desc(root.get("creationDate")));
		    query.select(root);
			TypedQuery<AuditCriticalParamsSummaryEntity> typedQuery = entityManager.createQuery(query);
			//query.where(cb.equal(root.get("runTestEntity"), runTestId));
			TypedQuery<AuditCriticalParamsSummaryEntity> queryResult = entityManager.createQuery(query);
			auditCriticalParamsIndexEntityList = queryResult.getResultList();

		} catch (Exception e) {
			logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return auditCriticalParamsIndexEntityList ;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object>  getAuditCriticalParamsSummaryEntityList(int page, int count, String fromDate, String toDate) {
		
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<AuditCriticalParamsSummaryEntity> auditCriticalSummaryEntity = null;
		double result = 0;
		int pagecount = 0;
		try {

			Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(AuditCriticalParamsSummaryEntity.class);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat(Constants.MM_DD_YYYY);

			System.out.println("===fromDate: " + fromDate);
			System.out.println("===toDate: " + toDate);
			if (fromDate != null && !fromDate.equals("")) {
				criteria.createAlias("runTestEntity", "runTestEntity");
			}
			
			Criterion searchStartDate = Restrictions.ge("creationDate", dateFormat.parse(fromDate));
			Criterion searchEndDate = Restrictions.le("creationDate", dateFormat.parse(toDate));
			
			System.out.println("===searchStartDate: " + searchStartDate);
			System.out.println("===searchEndDate: " + searchEndDate);
			
			criteria.add(searchStartDate);
			criteria.add(searchEndDate);
			
			Conjunction conjunction = Restrictions.conjunction();
			/*conjunction.add(Restrictions.eq("runTestId", runTestId));
			 
			if (StringUtils.isNotEmpty(neId)) {
				conjunction.add(Restrictions.like("neId", neId, MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(neName)) {
				conjunction.add(Restrictions.like("neName", neName, MatchMode.ANYWHERE));
			}
			if (StringUtils.isNotEmpty(siteName)) {
				conjunction.add(Restrictions.like("runTestId", siteName, MatchMode.ANYWHERE));
			}
			
			if (StringUtils.isNotEmpty(siteName)) {
				conjunction.add(Restrictions.like("programName", programName, MatchMode.ANYWHERE));
			}
			
			if (StringUtils.isNotEmpty(status)) {
				conjunction.add(Restrictions.like("status", status, MatchMode.ANYWHERE));
			}*/
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			auditCriticalSummaryEntity = criteria.list();
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(AuditCriticalParamsSummaryEntity.class);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
			objMap.put("pageCount", pagecount);
			objMap.put("auditStatusList", auditCriticalSummaryEntity);
			
			/*criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			criteria.addOrder(Order.desc("creationDate"));
			auditCriticalParamsIndexEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();*/
			System.out.println(auditCriticalSummaryEntity.size());

		} catch (Exception e) {
			logger.error("Exception in  getAuditCriticaParamsSummaryEntityList() in  AuditCriticalParamsRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;

	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getAuditCriticalParamsSearchSummaryEntityList(AuditCriticalParamsSummaryModel auditSummaryModel, int page, int count) {

		Map<String, Object> objMap = new HashMap<String, Object>();
		List<AuditCriticalParamsSummaryEntity> auditCriticalParamsEntity = null;
		List<Predicate> predList = new LinkedList<Predicate>();
		double result = 0;
		int pagecount = 0;
		try {
			if (auditSummaryModel != null) {
				org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
						.createCriteria(AuditCriticalParamsSummaryEntity.class);
				Conjunction conjunction = Restrictions.conjunction();
				auditCriticalParamsEntity = new ArrayList<>();
				CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
				CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
				countQuery.select(criteriaBuilder.count(countQuery.from(AuditCriticalParamsSummaryEntity.class)));
				Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

				CriteriaQuery<AuditCriticalParamsSummaryEntity> criteriaQuery = criteriaBuilder
						.createQuery(AuditCriticalParamsSummaryEntity.class);
				Root<AuditCriticalParamsSummaryEntity> from = criteriaQuery
						.from(AuditCriticalParamsSummaryEntity.class);
				CriteriaQuery<AuditCriticalParamsSummaryEntity> select = criteriaQuery.select(from);
						
				if (StringUtils.isNotEmpty(auditSummaryModel.getNeId()) && !"".equals(auditSummaryModel.getNeId())) {
					Predicate neId = criteriaBuilder.like(from.get("neId"), "%" + auditSummaryModel.getNeId() + "%");
					System.out.println("===neId: " + neId);
					predList.add(neId);
				}
				if (StringUtils.isNotEmpty(auditSummaryModel.getNeName())
						&& !"".equals(auditSummaryModel.getNeName())) {
					Predicate neName = criteriaBuilder.like(criteriaBuilder.lower(from.get("neName")),
							"%" + auditSummaryModel.getNeName().toLowerCase() + "%");
					System.out.println("===neName: " + neName);
					predList.add(neName);

				}
				
				if (auditSummaryModel.getRunTestId() != null
						&& StringUtils.isNotEmpty(auditSummaryModel.getRunTestId().toString())
						&& !"".equals(auditSummaryModel.getRunTestId())) {
					Predicate runTestId = criteriaBuilder.equal(from.get("runTestEntity").get("id"), auditSummaryModel.getRunTestId());
					System.out.println("===runTestId: " + runTestId);
					predList.add(runTestId);
				}

				if (StringUtils.isNotEmpty(auditSummaryModel.getSiteName())
						&& !"".equals(auditSummaryModel.getSiteName())) {
					Predicate siteName = criteriaBuilder.like(criteriaBuilder.lower(from.get("siteName")),
							"%" + auditSummaryModel.getSiteName().toLowerCase() + "%");
					System.out.println("===siteName: " + siteName);
					predList.add(siteName);
					
					
				}
				if (StringUtils.isNotEmpty(auditSummaryModel.getProgramName())
						&& !"".equals(auditSummaryModel.getProgramName())) {
					Predicate programName = criteriaBuilder.like(criteriaBuilder.lower(from.get("programName")),
							"%" + auditSummaryModel.getProgramName().toLowerCase() + "%");
					System.out.println("===programName: " + programName);
					predList.add(programName);
			        
				}

				if (StringUtils.isNotEmpty(auditSummaryModel.getStatus())
						&& !"".equals(auditSummaryModel.getStatus())) {
					Predicate status = criteriaBuilder.like(criteriaBuilder.lower(from.get("status")),
							"%" + auditSummaryModel.getStatus().toLowerCase() + "%");
					System.out.println("===status: " + status);
					predList.add(status);
				}
				criteria.add(conjunction);
				if (auditSummaryModel.getFromDate() != null && !"".equals(auditSummaryModel.getFromDate())
						&& auditSummaryModel.getToDate() != null && !"".equals(auditSummaryModel.getToDate())) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.MM_DD_YYYY);
					Date startDate = dateFormat.parse(auditSummaryModel.getFromDate());
					Date endDate = dateFormat.parse(auditSummaryModel.getToDate());
					Predicate auditStartDate = criteriaBuilder.greaterThanOrEqualTo(from.get("creationDate").as(java.sql.Date.class), startDate);
					Predicate auditEndDate = criteriaBuilder.lessThanOrEqualTo(from.get("creationDate").as(java.sql.Date.class), endDate);
					predList.add(auditStartDate);
					predList.add(auditEndDate);
					System.out.println("===searchStartDate: " + startDate);
					System.out.println("===searchEndDate: " + endDate);

				} else if (auditSummaryModel.getFromDate() != null && !"".equals(auditSummaryModel.getFromDate())) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.MM_DD_YYYY);
					Date startDate = dateFormat.parse(auditSummaryModel.getFromDate());
					Predicate auditStartDate = criteriaBuilder.greaterThanOrEqualTo(from.get("creationDate").as(java.sql.Date.class), startDate);
					predList.add(auditStartDate);
					System.out.println("===searchStartDate: " + startDate);

				} else if (auditSummaryModel.getToDate() != null && !"".equals(auditSummaryModel.getToDate())) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.MM_DD_YYYY);
					Date endDate = dateFormat.parse(auditSummaryModel.getToDate());
					Predicate auditEndDate = criteriaBuilder.lessThanOrEqualTo(from.get("creationDate").as(java.sql.Date.class), endDate);
					predList.add(auditEndDate);
					System.out.println("===searchEndDate: " + endDate);

				}
				Predicate[] predArray = new Predicate[predList.size()];
				predList.toArray(predArray);
				criteriaQuery.orderBy(criteriaBuilder.desc(from.get("creationDate")));
		        criteriaQuery.where(predArray);
				TypedQuery<AuditCriticalParamsSummaryEntity> typedQuery = entityManager.createQuery(select);
				typedQuery.setFirstResult((page - 1) * count);
				typedQuery.setMaxResults(count);
				auditCriticalParamsEntity = typedQuery.getResultList();				
				
				double size = totalCount;
				result = Math.ceil(size / count);
				pagecount = (int) result;

				objMap.put("pageCount", pagecount);
				objMap.put("auditStatusList", auditCriticalParamsEntity);

			}
		} catch (Exception e) {
			logger.error(
					"Exception in getAuditCriticalParamsSearchSummaryEntityList AuditCriticalParamsRepositoryImpl: "
							+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex1Entity> getAuditCriticalParamsIndex1Entity() {
		  List<AuditCriticalParamsIndex1Entity> auditCriticalParamsIndex1Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex1Entity> query = cb.createQuery(AuditCriticalParamsIndex1Entity.class);
				Root<AuditCriticalParamsIndex1Entity> root = query.from(AuditCriticalParamsIndex1Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex1Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex1Entity = typedQuery.getResultList();
				TypedQuery<AuditCriticalParamsIndex1Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex1Entity = queryResult.getResultList();
			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticaParamsSummaryEntityById() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex1Entity;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex2Entity> getAuditCriticalParamsIndex2Entity() {
		
		  List<AuditCriticalParamsIndex2Entity> auditCriticalParamsIndex2Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex2Entity> query = cb.createQuery(AuditCriticalParamsIndex2Entity.class);
				Root<AuditCriticalParamsIndex2Entity> root = query.from(AuditCriticalParamsIndex2Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex2Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex2Entity = typedQuery.getResultList();
				//query.where(cb.equal(root.get("runTestEntity"), runTestId));
				TypedQuery<AuditCriticalParamsIndex2Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex2Entity = queryResult.getResultList();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticalParamsIndex2Entity() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex2Entity;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex3Entity> getAuditCriticalParamsIndex3Entity() {
		
		  List<AuditCriticalParamsIndex3Entity> auditCriticalParamsIndex3Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex3Entity> query = cb.createQuery(AuditCriticalParamsIndex3Entity.class);
				Root<AuditCriticalParamsIndex3Entity> root = query.from(AuditCriticalParamsIndex3Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex3Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex3Entity = typedQuery.getResultList();
				//query.where(cb.equal(root.get("runTestEntity"), runTestId));
				TypedQuery<AuditCriticalParamsIndex3Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex3Entity = queryResult.getResultList();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticalParamsIndex3Entity() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex3Entity;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex4Entity> getAuditCriticalParamsIndex4Entity() {
		
		  List<AuditCriticalParamsIndex4Entity> auditCriticalParamsIndex4Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex4Entity> query = cb.createQuery(AuditCriticalParamsIndex4Entity.class);
				Root<AuditCriticalParamsIndex4Entity> root = query.from(AuditCriticalParamsIndex4Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex4Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex4Entity = typedQuery.getResultList();
				//query.where(cb.equal(root.get("runTestEntity"), runTestId));
				TypedQuery<AuditCriticalParamsIndex4Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex4Entity = queryResult.getResultList();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticalParamsIndex4Entity() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex4Entity; 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex5Entity> getAuditCriticalParamsIndex5Entity() {
		
		  List<AuditCriticalParamsIndex5Entity> auditCriticalParamsIndex5Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex5Entity> query = cb.createQuery(AuditCriticalParamsIndex5Entity.class);
				Root<AuditCriticalParamsIndex5Entity> root = query.from(AuditCriticalParamsIndex5Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex5Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex5Entity = typedQuery.getResultList();
				//query.where(cb.equal(root.get("runTestEntity"), runTestId));
				TypedQuery<AuditCriticalParamsIndex5Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex5Entity = queryResult.getResultList();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticalParamsIndex5Entity() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex5Entity;
	        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditCriticalParamsIndex6Entity> getAuditCriticalParamsIndex6Entity() {
		
		  List<AuditCriticalParamsIndex6Entity> auditCriticalParamsIndex6Entity = null;
			try {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<AuditCriticalParamsIndex6Entity> query = cb.createQuery(AuditCriticalParamsIndex6Entity.class);
				Root<AuditCriticalParamsIndex6Entity> root = query.from(AuditCriticalParamsIndex6Entity.class);
				query.orderBy(cb.desc(root.get("auditCriticalParamEntity").get("creationDate")));
				query.orderBy(cb.asc(root.get("auditStatus")));
				query.select(root).distinct(true);
				TypedQuery<AuditCriticalParamsIndex6Entity> typedQuery = entityManager.createQuery(query);
				auditCriticalParamsIndex6Entity = typedQuery.getResultList();
				//query.where(cb.equal(root.get("runTestEntity"), runTestId));
				TypedQuery<AuditCriticalParamsIndex6Entity> queryResult = entityManager.createQuery(query);
				auditCriticalParamsIndex6Entity = queryResult.getResultList();

			} catch (Exception e) {
				logger.error("Exception in  getAuditCriticalParamsIndex6Entity() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
			} finally {
				entityManager.flush();
				entityManager.clear();
			}
			return auditCriticalParamsIndex6Entity; 
	}

	@Override
	public boolean deleteAuditCriticalSummaryEntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			entityManager.remove(getauditCriticalParamsSummaryEntityById(id));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean deleteAuditCriticalIndex1EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex1Entity auditCriticalParamsIndex1Entity: getAuditCriticalParamsIndex1EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex1EntityById(auditCriticalParamsIndex1Entity.getId()));			
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean deleteAuditCriticalIndex2EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex2Entity auditCriticalParamsIndex2Entity: getAuditCriticalParamsIndex2EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex2EntityById(auditCriticalParamsIndex2Entity.getId()));			

			}		
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean deleteAuditCriticalIndex3EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex3Entity auditCriticalParamsIndex3Entity: getAuditCriticalParamsIndex3EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex3EntityById(auditCriticalParamsIndex3Entity.getId()));			

			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean deleteAuditCriticalIndex4EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex4Entity auditCriticalParamsIndex4Entity: getAuditCriticalParamsIndex4EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex4EntityById(auditCriticalParamsIndex4Entity.getId()));			

			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean deleteAuditCriticalIndex5EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex5Entity auditCriticalParamsIndex5Entity: getAuditCriticalParamsIndex5EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex5EntityById(auditCriticalParamsIndex5Entity.getId()));			

			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	@Override
	public boolean deleteAuditCriticalIndex6EntityByRunTestId(Integer id) {
		boolean status = false;
		try {
			for(AuditCriticalParamsIndex6Entity auditCriticalParamsIndex6Entity: getAuditCriticalParamsIndex6EntityById(id)) {
				entityManager.remove(getauditCriticalParamsIndex6EntityById(auditCriticalParamsIndex6Entity.getId()));			

			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditCriticalSummaryEntityByRunTestId() in  AuditCriticalParamsRepository:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	public AuditCriticalParamsIndex1Entity getauditCriticalParamsIndex1EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex1Entity.class, auditId);
	}
	public AuditCriticalParamsIndex2Entity getauditCriticalParamsIndex2EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex2Entity.class, auditId);
	}
	public AuditCriticalParamsIndex3Entity getauditCriticalParamsIndex3EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex3Entity.class, auditId);
	}
	public AuditCriticalParamsIndex4Entity getauditCriticalParamsIndex4EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex4Entity.class, auditId);
	}
	public AuditCriticalParamsIndex5Entity getauditCriticalParamsIndex5EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex5Entity.class, auditId);
	}
	public AuditCriticalParamsIndex6Entity getauditCriticalParamsIndex6EntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsIndex6Entity.class, auditId);
	}
	public AuditCriticalParamsSummaryEntity getauditCriticalParamsSummaryEntityById(int auditId) {
		return entityManager.find(AuditCriticalParamsSummaryEntity.class, auditId);
	}
}
