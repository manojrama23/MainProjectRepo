package com.smart.rct.common.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.AutoFecthTriggerEntity;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.FetchOVResponseModel;
import com.smart.rct.common.models.OvCiqDetailsModel;
import com.smart.rct.common.models.OvScheduledModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.repository.OvScheduledTaskRepository;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.repository.WorkFlowManagementRepository;
import com.smart.rct.premigration.controller.UploadCIQController;
import com.smart.rct.premigration.dto.CiqUploadAuditTrailDetailsDto;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.Ip;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.repository.FetchProcessRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.repositoryImpl.GenerateCsvRepositoryImpl;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.serviceImpl.EnodebDetails;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class OvScheduledTaskServiceImpl implements OvScheduledTaskService {

	final static Logger logger = LoggerFactory.getLogger(OvScheduledTaskServiceImpl.class);

	@Autowired
	OvScheduledTaskRepository ovScheduledTaskRepository;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	UploadCIQController uploadCIQController;

	@Autowired
	FetchProcessRepository fetchProcessRepository;

	@Autowired
	FetchProcessService fetchProcessService;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerService customerService;

	@Autowired
	CiqUploadAuditTrailDetailsDto ciqUploadAuditTrailDetailsDto;
	
	@Autowired
	public FileUploadService fileUploadService;
	
	@Autowired
	GenerateCsvRepositoryImpl generateCsvRepositoryImpl;
	
	@Autowired
	WorkFlowManagementRepository workFlowManagementRepository;

	@Override
	public Map<String, List<OvScheduledEntity>> getOvScheduledDetails(List<String> forceFetchIds,CustomerDetailsEntity programmeEntity) {
		Map<String, List<OvScheduledEntity>> scheduledDetailsMap = new HashMap<>();
		try {
			String toDayDate = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD);
			List<OvScheduledEntity> listOvScheduledEntity = ovScheduledTaskRepository.getOvScheduledDetails(toDayDate,forceFetchIds,programmeEntity);

			if (!ObjectUtils.isEmpty(listOvScheduledEntity)) {
				//Individual phases
				List<OvScheduledEntity> preMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				List<OvScheduledEntity> neGrowOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				List<OvScheduledEntity> migOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				List<OvScheduledEntity> postAuditOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				List<OvScheduledEntity> postRanOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				
				//Pre Migration and NE grow
				List<OvScheduledEntity> preMigNeGrowOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				// Pre Migration ans migration
				List<OvScheduledEntity> preMigMigrationOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				//Pre Migration and Post Migration
				List<OvScheduledEntity> preMigPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				//Pre  migration ,Ne Grow and Post Migration
				List<OvScheduledEntity> preMigNeGrowPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();
				
			    // Pre migration , Migration and post Migration
				List<OvScheduledEntity> preMigMigPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				//Pre migration ,ne grow and Migration
				List<OvScheduledEntity> preMigNeGrowMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				//Pre migration,Negrow ,Migration and Post Migration
				List<OvScheduledEntity> preMigNeGrowMigPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();

				//NE grow and Migration
				List<OvScheduledEntity> neGrowMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//

				//Migration and Post Migration
				List<OvScheduledEntity> MigPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//

				//NE grow and Post Migration
				List<OvScheduledEntity> NeGrowPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//
				
				//NE grow and Migration ,post
				List<OvScheduledEntity> neGrowMigPostMigOvScheduledEntity = new ArrayList<OvScheduledEntity>();//

				// List<OvScheduledEntity> preMigEnvOvScheduledEntity = new
				// ArrayList<OvScheduledEntity>();

				for (OvScheduledEntity locOvScheduledEntity : listOvScheduledEntity) {

					if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationScheduledTime())
							&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPremigrationScheduledTime()))
							|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
									&& toDayDate
											.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

						logger.error("Into Premigration scheduled date and time.");
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getNeGrowScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getNeGrowScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getNeGrowReScheduledTime()) && toDayDate
										.equalsIgnoreCase(locOvScheduledEntity.getNeGrowReScheduledTime()))) {
							
							if ((StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime())
									&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getMigrationScheduledTime()))
									|| (StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime()) && toDayDate
											.equalsIgnoreCase(locOvScheduledEntity.getMigrationReScheduledTime()))) {

								if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
										&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
										|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
												&& toDayDate
														.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

									preMigNeGrowMigPostMigOvScheduledEntity.add(locOvScheduledEntity);
									continue;
								}
								preMigNeGrowMigOvScheduledEntity.add(locOvScheduledEntity);
								continue;
							}
							preMigNeGrowOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}
						
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getMigrationScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime()) && toDayDate
										.equalsIgnoreCase(locOvScheduledEntity.getMigrationReScheduledTime()))) {
							if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
									&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
									|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
											&& toDayDate
													.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

								preMigMigPostMigOvScheduledEntity.add(locOvScheduledEntity);
								continue;
							}
							preMigMigrationOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}
						
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
										&& toDayDate
												.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

							preMigPostMigOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}

						preMigOvScheduledEntity.add(locOvScheduledEntity);
						continue;
					}
					if ((StringUtils.isNotEmpty(locOvScheduledEntity.getNeGrowScheduledTime())
							&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getNeGrowScheduledTime()))
							|| (StringUtils.isNotEmpty(locOvScheduledEntity.getNeGrowReScheduledTime())
									&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getNeGrowReScheduledTime()))) {
						
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getMigrationScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime()) && toDayDate
										.equalsIgnoreCase(locOvScheduledEntity.getMigrationReScheduledTime()))) {

							if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
									&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
									|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
											&& toDayDate
													.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

								neGrowMigPostMigOvScheduledEntity.add(locOvScheduledEntity);
								continue;
							}
							neGrowMigOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}
						
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
										&& toDayDate
												.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

							NeGrowPostMigOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}
						neGrowOvScheduledEntity.add(locOvScheduledEntity);
						continue;
					}

					if ((StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime())
							&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getMigrationScheduledTime()))
							|| (StringUtils.isNotEmpty(locOvScheduledEntity.getMigrationScheduledTime()) && toDayDate
									.equalsIgnoreCase(locOvScheduledEntity.getMigrationReScheduledTime()))) {
						if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
								&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
								|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
										&& toDayDate
												.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

							MigPostMigOvScheduledEntity.add(locOvScheduledEntity);
							continue;
						}
						migOvScheduledEntity.add(locOvScheduledEntity);
						continue;
					}

					if ((StringUtils.isNotEmpty(locOvScheduledEntity.getPostmigrationAuditScheduledTime())
							&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getPostmigrationAuditScheduledTime()))
							|| (StringUtils.isNotEmpty(locOvScheduledEntity.getPremigrationReScheduledTime())
									&& toDayDate
											.equalsIgnoreCase(locOvScheduledEntity.getPremigrationReScheduledTime()))) {

						postAuditOvScheduledEntity.add(locOvScheduledEntity);
					}

					if ((StringUtils.isNotEmpty(locOvScheduledEntity.getRanAtpScheduledTime())
							&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getRanAtpScheduledTime()))
							|| (StringUtils.isNotEmpty(locOvScheduledEntity.getRanAtpReScheduledTime())
									&& toDayDate.equalsIgnoreCase(locOvScheduledEntity.getRanAtpReScheduledTime()))) {

						postRanOvScheduledEntity.add(locOvScheduledEntity);
					}
					
				}

				scheduledDetailsMap.put(Constants.PREMIGRATION_SCHEDULE, preMigOvScheduledEntity);
				scheduledDetailsMap.put(Constants.NE_GROW_SCHEDULE, neGrowOvScheduledEntity);
				scheduledDetailsMap.put(Constants.MIGRATION_SCHEDULE, migOvScheduledEntity);
				scheduledDetailsMap.put(Constants.POST_MIGRATION_AUDIT_SCHEDULE, postAuditOvScheduledEntity);
				scheduledDetailsMap.put(Constants.POST_MIGRATION_RANATP_SCHEDULE, postRanOvScheduledEntity);
				scheduledDetailsMap.put(Constants.PRE_MIG_NEGROW_SCHEDULE, preMigNeGrowOvScheduledEntity);
				//Pre migration,Negrow ,Migration and Post Migration
				scheduledDetailsMap.put(Constants.PRE_MIG_NEGROW_MIG_POSTMIG_SCHEDULE, preMigNeGrowMigPostMigOvScheduledEntity);
				//Pre migration ,ne grow and Migration
				scheduledDetailsMap.put(Constants.PRE_MIG_NEGROW_MIG_SCHEDULE, preMigNeGrowMigOvScheduledEntity);
				//Pre  migration ,Ne Grow and Post Migration
				scheduledDetailsMap.put(Constants.PRE_MIG_NEGROW_POSTMIG_SCHEDULE, preMigNeGrowPostMigOvScheduledEntity);
				// Pre migration , Migration and post Migration
				scheduledDetailsMap.put(Constants.PRE_MIG_MIG_POSTMIG_SCHEDULE, preMigMigPostMigOvScheduledEntity);
				// Pre Migration ans migration
				scheduledDetailsMap.put(Constants.PRE_MIG_MIG_SCHEDULE, preMigMigrationOvScheduledEntity);
				//Pre Migration and Post Migration
				scheduledDetailsMap.put(Constants.PRE_MIG_POSTMIG_SCHEDULE, preMigPostMigOvScheduledEntity);
				
				scheduledDetailsMap.put(Constants.NEGROW_MIG_SCHEDULE, neGrowMigOvScheduledEntity);
				scheduledDetailsMap.put(Constants.MIG_POSTMIG_SCHEDULE, MigPostMigOvScheduledEntity);
				scheduledDetailsMap.put(Constants.NEGROW_POSTMIG_SCHEDULE, NeGrowPostMigOvScheduledEntity);
				scheduledDetailsMap.put(Constants.NEGROW_MIG_POSTMIG_SCHEDULE, NeGrowPostMigOvScheduledEntity);

			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

		return scheduledDetailsMap;
	}

	/**
	 * this method will return getOvStatusScheduledDetails
	 * 
	 * @param page,count
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getOvStatusScheduledDetails(int page, int count, String programName) {
		Map<String, Object> ScheduledEntity = null;
		try {
			List<OvScheduledModel> listOvScheduledModel = new ArrayList<>();
			ScheduledEntity = ovScheduledTaskRepository.getOvStatusScheduledDetails(page, count, programName);

			if (!ObjectUtils.isEmpty(ScheduledEntity) && ScheduledEntity.containsKey("ovStatusList")) {
				List<OvScheduledEntity> ovStatusEntities = (List<OvScheduledEntity>) ScheduledEntity
						.get("ovStatusList");
				if (!ObjectUtils.isEmpty(ovStatusEntities)) {
					for (OvScheduledEntity ovScheduledEntity : ovStatusEntities) {
						listOvScheduledModel.add(ovScheduledEntityDto(ovScheduledEntity));
					}
				}

				ScheduledEntity.put("ovStatusList", listOvScheduledModel);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntity;
	}

	public OvScheduledModel ovScheduledEntityDto(OvScheduledEntity ovScheduledEntity)
  	{
  		ModelMapper modelMapper=new ModelMapper();
		WorkFlowManagementEntity workFlowManagementEntity = null;

  		OvScheduledModel ovScheduledModel = modelMapper.map(ovScheduledEntity, OvScheduledModel.class);
  		if(ovScheduledEntity.getWorkFlowManagementEntity()!=null) {
  		workFlowManagementEntity = workFlowManagementRepository.getWorkFlowManagementEntity(ovScheduledEntity.getWorkFlowManagementEntity().getId());
  		ovScheduledModel.setWfmid(workFlowManagementEntity.getId().toString());
  		ovScheduledModel.setPreErrorFile(workFlowManagementEntity.getPreErrorFile());
  		ovScheduledModel.setNegrowErrorFile(workFlowManagementEntity.getNegrowErrorFile());
  		ovScheduledModel.setMigErrorFile(workFlowManagementEntity.getMigErrorFile());
  		ovScheduledModel.setPostErrorFile(workFlowManagementEntity.getPostErrorFile());
  		}
  		return ovScheduledModel;
  	}

	@Override
	public OvScheduledEntity getOvDetails(Integer workFlowId) {
		OvScheduledEntity ScheduledEntity = null;
		try {
			ScheduledEntity = ovScheduledTaskRepository.getOvDetails(workFlowId);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntity;
	}

	@Override
	public OvScheduledEntity getOvScheduledServiceDetails(String trackerId, String enbId) {
		OvScheduledEntity ScheduledEntity = null;
		try {
			ScheduledEntity = ovScheduledTaskRepository.getOvScheduledServiceDetails(trackerId, enbId);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntity;
	}

	@Override
	public List<OvScheduledEntity> getOvScheduledServiceDetailsList(String trackerId,String programName) {
		List<OvScheduledEntity> ScheduledEntity = null;
		try {
			ScheduledEntity = ovScheduledTaskRepository.getOvScheduledServiceDetailsList(trackerId,programName);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntity;
	}

	@Override
	public OvScheduledEntity mergeOvScheduledDetails(OvScheduledEntity ovScheduledEntity) {
		OvScheduledEntity ScheduledEntityUpdate = null;
		try {
			ScheduledEntityUpdate = ovScheduledTaskRepository.createOvScheduleDetails(ovScheduledEntity);
		} catch (Exception e) {
			logger.error("Exception in mergeWorkFlowMangement()   WorkFlowManagementServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntityUpdate;
	}

	/**
	 * this method will return getOvStatusScheduledDetails
	 * 
	 * @param page,count
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getOvStatusScheduledSearchDetails(OvScheduledModel ovScheduledModel, int page, int count,
			String programName) {
		Map<String, Object> ScheduledEntity = null;
		try {
			List<OvScheduledModel> listOvScheduledModel = new ArrayList<>();
			ScheduledEntity = ovScheduledTaskRepository.getOvStatusScheduledSearchDetails(ovScheduledModel, page, count,
					programName);

			if (!ObjectUtils.isEmpty(ScheduledEntity) && ScheduledEntity.containsKey("ovStatusList")) {
				List<OvScheduledEntity> ovStatusEntities = (List<OvScheduledEntity>) ScheduledEntity
						.get("ovStatusList");
				if (!ObjectUtils.isEmpty(ovStatusEntities)) {
					for (OvScheduledEntity ovScheduledEntity : ovStatusEntities) {
						listOvScheduledModel.add(ovScheduledEntityDto(ovScheduledEntity));
					}
				}

				ScheduledEntity.put("ovStatusList", listOvScheduledModel);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return ScheduledEntity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject forceFetchDetails(FetchDetailsModel fetchDetailsModel, String fetchType, String remarks,
			List<OvScheduledEntity> listSchedule) {
		// TODO Auto-generated method stub
		ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = new ConcurrentHashMap<>();
		JSONObject statusJson = new JSONObject();
		try {
			FetchOVResponseModel objFetchOVResponseModel = new FetchOVResponseModel();
			CustomerDetailsEntity customerDetailsEntity = new CustomerDetailsEntity();
			customerDetailsEntity.setId(fetchDetailsModel.getProgramId());
			customerDetailsEntity.setProgramName(fetchDetailsModel.getProgramName());
			objFetchOVResponseModel.setCustomerDetailsEntity(customerDetailsEntity);
			JSONObject objFetchRequest = new JSONObject();
			List<String> market = fetchDetailsModel.getMarket();
			List<String> rfScriptList = fetchDetailsModel.getRfScriptList();
			List<NetworkConfigEntity> networkConfigList = fetchProcessRepository
					.getNetworkConfigDetailsForOv(objFetchOVResponseModel);
			int ciqNetworkConfigId = 0;
			int scriptNetworkConfigId = 0;
			if (!ObjectUtils.isEmpty(networkConfigList)) {

				for (NetworkConfigEntity locNetworkConfigEntity : networkConfigList) {
					if (Constants.FETCH_NE.equalsIgnoreCase(locNetworkConfigEntity.getNeTypeEntity().getNeType())) {
						ciqNetworkConfigId = locNetworkConfigEntity.getId();
					}
					if (Constants.SCRIPT_NE.equalsIgnoreCase(locNetworkConfigEntity.getNeTypeEntity().getNeType())) {
						scriptNetworkConfigId = locNetworkConfigEntity.getId();
					}
				}
			}
			if (ciqNetworkConfigId > 0 && scriptNetworkConfigId > 0) {
				objFetchRequest.put("ciqNetworkConfigId", ciqNetworkConfigId);
				objFetchRequest.put("scriptNetworkConfigId", scriptNetworkConfigId);
			}
			objFetchRequest.put("sessionId", fetchDetailsModel.getSessionId());
			objFetchRequest.put("serviceToken", fetchDetailsModel.getServiceToken());
			objFetchRequest.put("market", market);
			objFetchRequest.put("rfScriptList", rfScriptList);
			objFetchRequest.put("fileSourceType", fetchType);
			objFetchRequest.put("customerName", "");
			objFetchRequest.put("customerId",
					fetchDetailsModel.getCustomerId());
			objFetchRequest.put("programName", fetchDetailsModel.getProgramName());
			objFetchRequest.put("programId", objFetchOVResponseModel.getCustomerDetailsEntity().getId());
			objFetchRequest.put("activate", false);
			if(!fetchDetailsModel.getRemarks().equals("") && fetchDetailsModel.getRemarks()!=null)
				remarks = fetchDetailsModel.getRemarks() + "," + remarks;
			objFetchRequest.put("remarks", remarks);
			objFetchRequest.put("allowDuplicate", false);
			if (Constants.DSS_5G.equalsIgnoreCase(fetchDetailsModel.getProgramName())) {
				List<String> neIdList = getDssNeids(rfScriptList);
				objFetchRequest.put("rfScriptList", neIdList);
			}
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(customerDetailsEntity);
			programTemplateModel.setConfigType("s&r");
			List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
					.getScheduledDaysDetails(programTemplateModel);
			String rfDBInteraction ="";
			
			if(!ObjectUtils.isEmpty(listProgramTemplateEntity)){
			rfDBInteraction = listProgramTemplateEntity.stream()
					.filter(entity -> Constants.FETCH_FROM_RFDB.equalsIgnoreCase(entity.getLabel()))
					.map(entity -> entity.getValue()).findFirst().get();
			}
			if(!rfDBInteraction.equalsIgnoreCase("OFF") && Constants.USM_LIVE_4G.equalsIgnoreCase(fetchDetailsModel.getProgramName())) {
				statusJson=fetchFromRfDb(fetchDetailsModel, fetchType, remarks, listSchedule);
			}else {

			JSONObject objresultJson = uploadCIQController.fetchPreMigrationFiles(objFetchRequest);
			if (!ObjectUtils.isEmpty(objresultJson) && objresultJson.containsKey("status")
					&& Constants.SUCCESS.equalsIgnoreCase(objresultJson.get("status").toString())
					&& objresultJson.containsKey("ovAuditList")
					&& !ObjectUtils.isEmpty(objresultJson.get("ovAuditList"))) {
				List<CiqUploadAuditTrailDetEntity> ciqDetails = (List<CiqUploadAuditTrailDetEntity>) objresultJson
						.get("ovAuditList");
				for (CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity : ciqDetails) {
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(
							String.valueOf(ciqUploadAuditTrailDetEntity.getProgramDetailsEntity().getId()),
							ciqUploadAuditTrailDetEntity.getCiqFileName());
					List<EnodebDetails> gnbDetails = fetchProcessRepository
							.getEnbDetails(ciqUploadAuditTrailDetEntity.getCiqFileName(), dbcollectionFileName);
					if (!ObjectUtils.isEmpty(gnbDetails)) {
						for (EnodebDetails objDetails : gnbDetails) {
							if (rfScriptList.contains(objDetails.geteNBId())) {
								OvCiqDetailsModel ovCiqDetailsModel = new OvCiqDetailsModel();
								ovCiqDetailsModel.setCiqName(ciqUploadAuditTrailDetEntity.getCiqFileName());
								ovCiqDetailsModel.setCiqPath(ciqUploadAuditTrailDetEntity.getCiqFilePath());
								ovCiqDetailsModel.setGnbId(objDetails.geteNBId());
								ciqMapDetails.put(objDetails.geteNBId(), ovCiqDetailsModel);
							}
						}
					}

				}
			} else {
				statusJson.put("status", Constants.FAIL);
				if (objresultJson.containsKey("reason") && objresultJson.get("reason") != null) {
					statusJson.put("reason", objresultJson.get("reason").toString());
				}

				return statusJson;
			}

			boolean status = saveScheduledDetailswithOutRfdb(listSchedule, ciqMapDetails);
			logger.error("Status: "+ status);
			if (status) {
				statusJson.put("status", Constants.SUCCESS);
			} else {
				statusJson.put("status", Constants.FAIL);
				statusJson.put("reason", "error in scheduledetails");
			}
			}

		} catch (Exception e) {
			statusJson.put("status", Constants.FAIL);
			statusJson.put("reason", "error in scheduledetails");
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}
		return statusJson;
	}

	@Override
	public List<OvScheduledEntity> getForceFecthOvDetails(List<String> neids) {
		return ovScheduledTaskRepository.getForceFecthOvDetails(neids);
	}

	public boolean saveScheduledDetailswithOutRfdb(List<OvScheduledEntity> listOvScheduledEntity,
			ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails) {
		boolean status = false;

		try {

			if (!ObjectUtils.isEmpty(listOvScheduledEntity)) {
				for (OvScheduledEntity objOvScheduledEntity : listOvScheduledEntity) {
					logger.error("In Schedule Details: " + objOvScheduledEntity );
					OvScheduledEntity objNewOvScheduledEntity = new OvScheduledEntity();
					objNewOvScheduledEntity = objOvScheduledEntity;
					String neId = objNewOvScheduledEntity.getNeId();

					if (ciqMapDetails.containsKey(neId)) {
						logger.error(neId);
						OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(neId,
								objNewOvScheduledEntity, ciqMapDetails.get(neId).getCiqName(),
								ciqMapDetails.get(neId).getCiqPath());
						persistOvScheduledEntity.setFetchDetailsJson("Uploaded via tool from force fetch");
						fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
					}

				}
				status = true;
			}

		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	private OvScheduledEntity getOvScheduledEntityDetails(String neId, OvScheduledEntity persistOvScheduledEntity,
			String ciqName, String ciqPath) {

		try {
			persistOvScheduledEntity.setFetchDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
			persistOvScheduledEntity.setCiqGenerationDate(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM));
			persistOvScheduledEntity.setCiqName(ciqName);
			persistOvScheduledEntity.setCiqFilePath(ciqPath);
			persistOvScheduledEntity.setFetchRemarks(Constants.COMPLETED);

			persistOvScheduledEntity
					.setPremigrationScheduledTime(DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD));

			persistOvScheduledEntity.setPreMigStatus(Constants.SCHEDULED);
			persistOvScheduledEntity.setEnvStatus(Constants.SCHEDULED);
			persistOvScheduledEntity.setPreMigGrowStatus(Constants.SCHEDULED);

			if (!Constants.OV_SCHEDULED_TIME_OFF.equalsIgnoreCase(persistOvScheduledEntity.getNeGrowScheduledTime())) {
				persistOvScheduledEntity.setNeGrowStatus(Constants.SCHEDULED);
			}

			if (!Constants.OV_SCHEDULED_TIME_OFF
					.equalsIgnoreCase(persistOvScheduledEntity.getMigrationScheduledTime())) {
				persistOvScheduledEntity.setMigStatus(Constants.SCHEDULED);
			}

			if (!Constants.OV_SCHEDULED_TIME_OFF
					.equalsIgnoreCase(persistOvScheduledEntity.getPostmigrationAuditScheduledTime())) {
				persistOvScheduledEntity.setPostMigAuditStatus(Constants.SCHEDULED);
			}

			if (!Constants.OV_SCHEDULED_TIME_OFF.equalsIgnoreCase(persistOvScheduledEntity.getRanAtpScheduledTime())) {
				persistOvScheduledEntity.setPostMigRanAtpStatus(Constants.SCHEDULED);
			}

		} catch (Exception e) {
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}
		return persistOvScheduledEntity;
	}

	@Override
	public AutoFecthTriggerEntity getAutoFetchDetails(String programName) {
		// TODO Auto-generated method stub
		return ovScheduledTaskRepository.getAutoFetchDetails(programName);
	}

	@Override
	public AutoFecthTriggerEntity mergeAutoFetchDetails(AutoFecthTriggerEntity autoFecthTriggerEntity) {
		// TODO Auto-generated method stub
		return ovScheduledTaskRepository.mergeAutoFetchDetails(autoFecthTriggerEntity);
	}

	
	
	@SuppressWarnings("unchecked")
	public JSONObject fetchFromRfDb(FetchDetailsModel fetchDetailsModel, String fetchType, String remarks,List<OvScheduledEntity> listSchedule) {
		JSONObject statusJson = new JSONObject();
		
		try {

			// RF Db Code Merge from supriya
			List<FetchOVResponseModel> list4GFetchOVResponseMdel=getFetchResponseDetails(listSchedule, fetchDetailsModel);
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			OvGeneralEntity fetchDays = customerRepository.getOvlabelTemplate(Constants.OV_FETCH_DAYS);

			programTemplateModel.setProgramDetailsEntity(list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity());
			programTemplateModel.setConfigType("s&r");
			List<ProgramTemplateEntity> listProgramTemplateEntity = fetchProcessRepository
					.getScheduledDaysDetails(programTemplateModel);

			/*
			 * String rfDBInteraction = listProgramTemplateEntity.stream() .filter(entity ->
			 * Constants.FETCH_FROM_RFDB.equalsIgnoreCase(entity.getLabel())) .map(entity ->
			 * entity.getValue()).findFirst().get();
			 * if(rfDBInteraction.equalsIgnoreCase("OFF")) { ConcurrentHashMap<String,
			 * OvCiqDetailsModel> ciqMapDetails = fetch5GDetails(
			 * list4GFetchOVResponseMdel,fetchType,remarks); if
			 * (!ObjectUtils.isEmpty(ciqMapDetails)) {
			 * saveScheduledDetails5Gmm(list4GFetchOVResponseMdel, ciqMapDetails); } }else {
			 */
			JSONObject obj = fetchProcessService.createExcelFromRFDB(list4GFetchOVResponseMdel, fetchDays.getValue());
			String ciqName = obj.get("ciqFileName").toString();
			String ciqPath = obj.get("ciqFilePath").toString();
			// Bala
			List<String> listOfNonExistingEnbs = (List<String>) obj.get("listOfNonExistingEnbs");
			String scriptPath = obj.get("scriptPath").toString();
			String scriptFiles = "";
			if (!ObjectUtils.isEmpty(obj.get("scriptFiles"))) {
				scriptFiles = obj.get("scriptFiles").toString();
			}
			File ciqDir = new File(obj.get("ciqFilePath").toString());
			boolean isAllowDuplicate = false;
			if (ciqDir.exists()) {
				if (!isAllowDuplicate) {
					isAllowDuplicate = true;
				}

			}
			StringBuilder filePath = new StringBuilder();
			filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.SEPARATOR)
					.append(obj.get("ciqFilePath").toString());
			File file = new File(filePath + "" + obj.get("ciqFileName"));
			FileInputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile(filePath.toString() + "" + ciqName, ciqName,
					"text/plain", IOUtils.toByteArray(input));
			String ciqFileName = multipartFile.getOriginalFilename();
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(filePath);
			StringBuilder ciqFileTempPath = sbPath.append(Constants.SEPARATOR).append("TEMP/");
			if (CommonUtil.isValidObject(ciqName)
					&& fileUploadService.uploadMultipartFile(multipartFile, ciqFileTempPath.toString())) {
				JSONObject fileProcessResult = uploadCIQController.preMigrationFileProcess(multipartFile, filePath,
						ciqFileTempPath, isAllowDuplicate, "CIQ",
						list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId(), ciqFileName,
						Constants.USM_LIVE_4G);
				CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				customerDetailsModel.setId(list4GFetchOVResponseMdel.get(0).getCustomerDetailsEntity().getId());
				List<CustomerDetailsEntity> detailsEntities = customerService
						.getCustomerDetailsList(customerDetailsModel);
				if (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
					ciqUploadAuditTrailDetModel.setProgramDetailsEntity(detailsEntities.get(0));
				}
				String ciqFileSavePath = filePath.toString();
				String scriptFileSavePath = " ";

				ciqFileSavePath = ciqFileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
				scriptFileSavePath = scriptPath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
				scriptFileSavePath = StringUtils.substringBeforeLast(scriptFileSavePath, "/");
				ciqUploadAuditTrailDetModel.setCiqFilePath(ciqPath);
				ciqUploadAuditTrailDetModel.setScriptFilePath(scriptFileSavePath);
				ciqUploadAuditTrailDetModel.setScriptFileName(scriptFiles);
				ciqUploadAuditTrailDetModel.setChecklistFilePath("");
				ciqUploadAuditTrailDetModel.setChecklistFileName("");
				ciqUploadAuditTrailDetModel.setFileSourceType(fetchType);
				ciqUploadAuditTrailDetModel.setCiqVersion(Constants.CIQ_VERSION_ORIGINAL);
				ciqUploadAuditTrailDetModel.setCiqFileName(multipartFile.getOriginalFilename());

				ciqUploadAuditTrailDetModel.setRemarks(remarks);
				CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = ciqUploadAuditTrailDetailsDto
						.getCiqUploadAuditTrailDetEntity(ciqUploadAuditTrailDetModel, "");
				fileUploadService.createCiqAudit(ciqUploadAuditTrailDetEntity);
			}

			boolean status=saveScheduledDetailswithRfdb(listSchedule, ciqName, ciqPath, listOfNonExistingEnbs,"FETCH");
			
			if (status) {
				statusJson.put("status", Constants.SUCCESS);
			} else {
				statusJson.put("status", Constants.FAIL);
				statusJson.put("reason", "error in scheduledetails");
			}

		} catch (Exception e) {
			statusJson.put("status", Constants.FAIL);
			statusJson.put("reason", "error in scheduledetails");
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}
		return statusJson;
	}

	public List<FetchOVResponseModel> getFetchResponseDetails(List<OvScheduledEntity> listOvScheduledEntity,
			FetchDetailsModel fetchDetailsModel) {

		List<FetchOVResponseModel> listData = new ArrayList<>();
		try {
			for (OvScheduledEntity ovlocalScheduledEntity : listOvScheduledEntity) {
				FetchOVResponseModel objFetchOVResponseModel = new FetchOVResponseModel();
				objFetchOVResponseModel.setCustomerDetailsEntity(ovlocalScheduledEntity.getCustomerDetailsEntity());
				objFetchOVResponseModel.setCommissionDate(ovlocalScheduledEntity.getMigrationScheduledTime());
				LinkedHashSet<String> neidSet = new LinkedHashSet<String>();
				neidSet.add(ovlocalScheduledEntity.getNeId());
				LinkedHashSet<String> marketNameList = new LinkedHashSet<String>();
				marketNameList.add(getMarketName(ovlocalScheduledEntity.getNeId()));
				objFetchOVResponseModel.setNeidList(neidSet);
				objFetchOVResponseModel.setMarket(marketNameList);
				objFetchOVResponseModel.setSiteName(ovlocalScheduledEntity.getSiteName());
				objFetchOVResponseModel.setTrackerId(ovlocalScheduledEntity.getTrackerId().toString());
				objFetchOVResponseModel.setWorkPlanId(ovlocalScheduledEntity.getWorkPlanID());
				//dummy IP
				objFetchOVResponseModel.setIntegrationType(ovlocalScheduledEntity.getIntegrationType());
				listData.add(objFetchOVResponseModel);
			}

		} catch (Exception e) {
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}

		return listData;

	}

	public String getMarketName(String enbId) {
		String marketName = null;
		try {
			String enbIdValue = enbId.replaceAll("^0+(?!$)", "");
			if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 5) {
				String marketId = enbIdValue.substring(0, 2);
				Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
				if (!ObjectUtils.isEmpty(ipDetails)) {
					marketName = ipDetails.getMarketName();
				}

			} else if (StringUtils.isNotEmpty(enbIdValue) && enbIdValue.length() == 6) {
				String marketId = enbIdValue.substring(0, 3);
				Ip ipDetails = generateCsvRepositoryImpl.getip(marketId);
				if (!ObjectUtils.isEmpty(ipDetails)) {
					marketName = ipDetails.getMarketName();
				}

			}
		} catch (Exception e) {
			logger.info(ExceptionUtils.getFullStackTrace(e));
		}
		return marketName;
	}
	
	public boolean saveScheduledDetailswithRfdb(List<OvScheduledEntity> listOvScheduledEntity,
			 String ciqName,
			String ciqPath,List<String> listOfNonExistingEnbs,String type) {
		boolean status = false;

		try {

			if (!ObjectUtils.isEmpty(listOvScheduledEntity)) {
				for (OvScheduledEntity objOvScheduledEntity : listOvScheduledEntity) {
					OvScheduledEntity objNewOvScheduledEntity = new OvScheduledEntity();
					objNewOvScheduledEntity = objOvScheduledEntity;
					String neId = objNewOvScheduledEntity.getNeId();

					if (!listOfNonExistingEnbs.contains(neId)) {
						OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(neId,
								objNewOvScheduledEntity, ciqName,
								ciqPath);
					
						persistOvScheduledEntity.setFetchDetailsJson("Uploaded via tool for force fetch");
						fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
					}

				}
				status = true;
			}

		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}
	@Override
	@SuppressWarnings("unchecked")
	public JSONObject uploadScheduledCiqDetails(List<String> scriftNeids){
		JSONObject resultMap = new JSONObject();
		try {
			List<OvScheduledEntity> listSchedule = ovScheduledTaskRepository.getForceFecthOvDetails(scriftNeids);
			if (!ObjectUtils.isEmpty(listSchedule)) {
				List<String> dbList = listSchedule.stream().map(entity -> entity.getNeId())
						.collect(Collectors.toList());

				List<String> notMappedOvData = scriftNeids.stream()
						.filter(data -> !(dbList.contains(data))).collect(Collectors.toList());

				if (!ObjectUtils.isEmpty(notMappedOvData)) {
					String ovNotMapData = String.join(",", notMappedOvData);
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "NE Id " + ovNotMapData + " is not present in ov hence not uploaded");
					return resultMap;
				}else {
					resultMap.put("status", Constants.SUCCESS);
				}
		} else {
			if (!ObjectUtils.isEmpty(scriftNeids)) {
				String ovNotMapData = String.join(",", scriftNeids);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "NE Id " + ovNotMapData + " is not present in ov hence not uploaded");
				return resultMap;
			}
		}
		}catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}	
		
		return resultMap;
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public JSONObject processScheduledCiqDetails(List<String> scriftNeids,CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity ){
		JSONObject resultMap = new JSONObject();
		ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails = new ConcurrentHashMap<>();
		try {
			List<OvScheduledEntity> listSchedule = ovScheduledTaskRepository.getForceFecthOvDetails(scriftNeids);
			if (!ObjectUtils.isEmpty(listSchedule)) {
				String dbcollectionFileName = CommonUtil.createMongoDbFileName(
						String.valueOf(ciqUploadAuditTrailDetEntity.getProgramDetailsEntity().getId()),
						ciqUploadAuditTrailDetEntity.getCiqFileName());
				List<EnodebDetails> gnbDetails = fetchProcessRepository
						.getEnbDetails(ciqUploadAuditTrailDetEntity.getCiqFileName(), dbcollectionFileName);
				
				if (!ObjectUtils.isEmpty(gnbDetails)) {
					for (EnodebDetails objDetails : gnbDetails) {
						if (scriftNeids.contains(objDetails.geteNBId())) {
							OvCiqDetailsModel ovCiqDetailsModel = new OvCiqDetailsModel();
							ovCiqDetailsModel.setCiqName(ciqUploadAuditTrailDetEntity.getCiqFileName());
							ovCiqDetailsModel.setCiqPath(ciqUploadAuditTrailDetEntity.getCiqFilePath());
							ovCiqDetailsModel.setGnbId(objDetails.geteNBId());
							ciqMapDetails.put(objDetails.geteNBId(), ovCiqDetailsModel);
						}
					}
				}
				
				if(!ObjectUtils.isEmpty(ciqMapDetails))
				{
					boolean status= saveScheduledDetailswithCiqUpload(listSchedule, ciqMapDetails);
					
					if(status)
					{
						resultMap.put("status", Constants.SUCCESS);
					}
				}else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Given NeIds Not Present in Ciq");
				}
		} 
		}catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}	
		
		return resultMap;
		
	}
	
	public boolean saveScheduledDetailswithCiqUpload(List<OvScheduledEntity> listOvScheduledEntity,
			ConcurrentHashMap<String, OvCiqDetailsModel> ciqMapDetails) {
		boolean status = false;

		try {

			if (!ObjectUtils.isEmpty(listOvScheduledEntity)) {
				for (OvScheduledEntity objOvScheduledEntity : listOvScheduledEntity) {
					OvScheduledEntity objNewOvScheduledEntity = new OvScheduledEntity();
					objNewOvScheduledEntity = objOvScheduledEntity;
					String neId = objNewOvScheduledEntity.getNeId();

					if (ciqMapDetails.containsKey(neId)) {
						OvScheduledEntity persistOvScheduledEntity = getOvScheduledEntityDetails(neId,
								objNewOvScheduledEntity, ciqMapDetails.get(neId).getCiqName(),
								ciqMapDetails.get(neId).getCiqPath());
						persistOvScheduledEntity.setFetchDetailsJson("Uploaded via tool from force upload");
						fetchProcessRepository.saveScheduledDetails(persistOvScheduledEntity);
					}

				}
				status = true;
			}

		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	@Override
	public boolean getOvDetailsForCreateExcel(OvScheduledModel ovScheduledModel, List<String> programNamesList,
			boolean addToZip,int page,int count) {
		boolean status = false;
		List<OvScheduledEntity> objOvModel = null;
		String[] columns = Constants.OV_DETAILS_COLUMN;
		Workbook workbook = new XSSFWorkbook();
		try {
			// Create a Sheet
			Sheet sheet = workbook.createSheet("OV DETAILS");
			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BROWN.getIndex());
			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			// Create a Row
			Row headerRow = sheet.createRow(0);
			// Create cells
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			List<OvScheduledEntity> objChildList = new ArrayList<>();

			objOvModel = ovScheduledTaskRepository.getOvDetailsForExPort(ovScheduledModel);
			if (objOvModel != null && objOvModel.size() > 0) {
				int rowNum = 1;
				for (OvScheduledEntity objOvEntity : objOvModel) {
					if (CommonUtil.isValidObject(programNamesList) && programNamesList.size() > 0 && programNamesList
							.contains(objOvEntity.getCustomerDetailsEntity().getProgramName())) {
						Row row = sheet.createRow(rowNum++);

						row.createCell(0).setCellValue(objOvEntity.getId());
						if (CommonUtil.isValidObject(objOvEntity.getCustomerDetailsEntity())) {
							row.createCell(1)
									.setCellValue(objOvEntity.getCustomerDetailsEntity().getProgramName());
						} else {
							row.createCell(1).setCellValue("");
						}
						row.createCell(2).setCellValue(objOvEntity.getSiteName());
						row.createCell(3).setCellValue(objOvEntity.getNeId());
						row.createCell(4).setCellValue(objOvEntity.getIntegrationType()); //dummy IP
						row.createCell(5).setCellValue(objOvEntity.getFetchDate());
						row.createCell(6).setCellValue(objOvEntity.getFetchRemarks());
						row.createCell(7).setCellValue(objOvEntity.getCiqName());
						row.createCell(8).setCellValue(objOvEntity.getCiqGenerationDate());
						row.createCell(9).setCellValue(objOvEntity.getPremigrationScheduledTime());
						row.createCell(10).setCellValue(objOvEntity.getPreMigGrowStatus());
						row.createCell(11).setCellValue(objOvEntity.getPreMigGrowGenerationDate());
						row.createCell(12).setCellValue(objOvEntity.getEnvStatus());
						row.createCell(13).setCellValue(objOvEntity.getEnvGenerationDate());
						row.createCell(14).setCellValue(objOvEntity.getEnvExportStatus());
						row.createCell(15).setCellValue(objOvEntity.getEnvFileName());
						row.createCell(16).setCellValue(objOvEntity.getNeGrowScheduledTime());
						row.createCell(17).setCellValue(objOvEntity.getNeGrowStatus());
						row.createCell(18).setCellValue(objOvEntity.getMigrationScheduledTime());
						row.createCell(19).setCellValue(objOvEntity.getMigrationScheduledTime());
						row.createCell(20).setCellValue(objOvEntity.getMigrationScheduledTime());
						row.createCell(21).setCellValue(objOvEntity.getMigStatus());
						row.createCell(22).setCellValue(objOvEntity.getTrackerId());
						row.createCell(23).setCellValue(objOvEntity.getWorkPlanID());
					}
					// Resize all columns to fit the content size
					for (int i = 0; i < columns.length; i++) {
						sheet.autoSizeColumn(i);
					}

				
				}
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
			File networkConfigDirectory = new File(fileNameBuilder.toString());
			if (!networkConfigDirectory.exists()) {
				networkConfigDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.NETWORKCONFIG_XLSX);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;

		} catch (Exception e) {
			logger.error("Excpetion in NetworkConfigServiceImpl.getLsmDetailsForCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}
	
	 public List<String> getDssNeids(List<String> dssIdsList)
	    {
	    	List<String> dataList=new ArrayList<>();
	    	try {
				if(!ObjectUtils.isEmpty(dssIdsList))
				{
					for(String dssId:dssIdsList)
					{
						if(StringUtils.isNotEmpty(dssId))
						{
							String enbIdValue = dssId.trim().replaceAll("^0+(?!$)", "");
							String neId=enbIdValue.substring(3, enbIdValue.length());
							dataList.add(neId);
						}
					}
				}
			} catch (Exception e) {
				logger.error( ExceptionUtils.getFullStackTrace(e));
			}
	    	return dataList;
	    }
	 @Override
		public boolean deleteOvDetails(int ovId) {
			// TODO Auto-generated method stub
			return ovScheduledTaskRepository.deleteOvDetails(ovId);
		}
	 
	 @Override
		public OvScheduledEntity getOvDetail(Integer ovId) {
			OvScheduledEntity ScheduledEntity = null;
			try {
				ScheduledEntity = ovScheduledTaskRepository.getOvDetail(ovId);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
			return ScheduledEntity;
		}
	 
//	 @Override
//		public OvScheduledEntity editOvDetail(String neId,String trackerId) {
//			OvScheduledEntity ScheduledEntity = null;
//			try {
//				ScheduledEntity = ovScheduledTaskRepository.getOvDetail(ovId);
//			} catch (Exception e) {
//				logger.error(ExceptionUtils.getFullStackTrace(e));
//			}
//			return ScheduledEntity;
//		}

}
