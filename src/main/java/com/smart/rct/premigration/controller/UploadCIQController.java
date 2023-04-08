package com.smart.rct.premigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.modelmapper.internal.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.EnbTemplateModel;
import com.smart.rct.common.models.ErrorDisplayModel;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NetworkConfigService;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.common.service.OvScheduledTaskService;
import com.smart.rct.configuration.DailyOvScheduleConfig;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.dto.CiqUploadAuditTrailDetailsDto;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.CheckListModel;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.CheckListScriptService;
import com.smart.rct.premigration.service.FetchProcessService;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class UploadCIQController {

	final static Logger logger = LoggerFactory.getLogger(UploadCIQController.class);
	@Autowired
	public FileUploadService fileUploadService;

	@Autowired
	NetworkTypeDetailsService networkTypeDetailsService;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	CustomerService customerService;

	@Autowired
	CiqUploadAuditTrailDetailsDto ciqUploadAuditTrailDetailsDto;

	@Autowired
	NetworkConfigService networkConfigService;

	@Autowired
	NeMappingService neMappingService;

	@Autowired
	CheckListScriptService checkListScriptService;
	@Autowired
	NeMappingService nemap;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	FetchProcessService fetchProcessService;
	
	@Autowired
	OvScheduledTaskService ovScheduledTaskService;
	
	@Autowired
	DailyOvScheduleConfig dailyOvScheduleConfig;

	@SuppressWarnings("unchecked")
	public JSONObject preMigrationFileProcess(MultipartFile preMigrationFile, StringBuilder filePath,
			StringBuilder fileTempPath, boolean isAllowDuplicate, String fileType, Integer programId,
			String ciqFileName, String programName) {
		JSONObject resultMap = new JSONObject();

		String id = null;
		try {
			CopyOnWriteArraySet<String> ciqNeIds=new CopyOnWriteArraySet<>();
			File preMigfile = new File(filePath.toString());
			File tempFile = new File(fileTempPath.toString());
			filePath.append(preMigrationFile.getOriginalFilename());
			Map<String, Object> fileProcessMap = null;
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("CIQ")) {
				if (programName.contains("5G") || programName.contains("VZN-4G-USM-LIVE")) {
					System.out.println("Uploading CIQ ..........................................................");

					NeMappingModel ne = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					programDetailsEntity.setSourceProgramId(programId);
					ne.setProgramDetailsEntity(programDetailsEntity);
					if (programName.equals("VZN-5G-MM") || programName.contains("VZN-5G-DSS")) {
						List<NeMappingEntity> entitydata = nemap.getGondebsByProgramName(ne);
						if (entitydata != null) {
							for (NeMappingEntity nedata : entitydata) {
								String enbid = nedata.getEnbId();
								Integer id1 = nedata.getId();

								if (StringUtils.isNotEmpty(enbid) && enbid.length() > 0 && enbid.charAt(0) == '0') {
									enbid = enbid.substring(1, enbid.length());
									nedata.setEnbId(enbid);
									nedata.setId(id1);
									nemap.saveNeMappingDetails(nedata);

								}
							}
						}
					}
					fileProcessMap = fileUploadService.process5GCiq(preMigrationFile,
							fileTempPath.toString() + preMigrationFile.getOriginalFilename(), isAllowDuplicate,
							programId, programName);
				} else {
					System.out.println(
							"4G Upload ......................................................................");
					fileProcessMap = fileUploadService.processCiq(preMigrationFile,
							fileTempPath.toString() + preMigrationFile.getOriginalFilename(), isAllowDuplicate,
							programId);
				}

				if (CommonUtil.isValidObject(fileProcessMap) && fileProcessMap.containsKey("status")
						&& (boolean) fileProcessMap.get("status")) {
					long startTime = System.currentTimeMillis();
					long neMappingEndTime = System.currentTimeMillis();

					String fileName = preMigrationFile.getOriginalFilename();
					String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId.toString(), fileName);

					NeMappingModel neMappingModel = new NeMappingModel();
					CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
					programDetailsEntity.setId(programId);
					neMappingModel.setProgramDetailsEntity(programDetailsEntity);
					List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
					List<String> neMappingEnbs = neMappingEntities.stream().map(x -> x.getEnbId())
							.collect(Collectors.toList());
					// if(StringUtils.isNotEmpty(programName) && programName.contains("4G"))
					// {
					// fileUploadService.saveNeversion20BMappingConfig(neMappingEntities, programId,
					// programName);
					// }

					List<Map<String, String>> objList;
					List<Map<String, String>> objList1;
					List<Map<String, String>> objList2;

					if (programName.contains("VZN-5G-DSS")) {
						objList = fileUploadService.getEnbDetailssheet(id, fileName, "vDUGrowSiteLevel(Day1)CQ",
								dbcollectionFileName);
						List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "vDUGrowSiteLevel(Day1)CQ",
								dbcollectionFileName);
						objList.addAll(fileUploadService.getDSSrowforNemappingACPFList(gnbDataList, "ACPF_ID"));
						objList.addAll(fileUploadService.getDSSNemappingrowAUPFList(gnbDataList, "AUPF_ID"));
						List<CIQDetailsModel> gnbDataListfsu = fileUploadService.getsheetData(fileName, "FSUCIQ",
								dbcollectionFileName);
						objList.addAll(fileUploadService.getDSSNemappingFSUList(gnbDataListfsu, "NE_ID"));
						
					} else if (programName.contains("5G-CBAND")){
						objList = fileUploadService.getEnbDetailssheet(id, fileName, "Day0_1",
								dbcollectionFileName);
						List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "Day0_1",
								dbcollectionFileName);
						objList.addAll(fileUploadService.getCBandEnbList(gnbDataList, "ENB_ID"));
						objList.addAll(fileUploadService.getCBandACPFList(gnbDataList, "ACPF_ID"));
						objList.addAll(fileUploadService.getCBandAUPFList(gnbDataList, "AUPF_ID"));
						objList1=fileUploadService.getCBandAUPFList(gnbDataList, "AUPF_ID");
						
					} else if (programName.contains("4G-FSU")){
						objList = fileUploadService.getEnbDetailssheet(id, fileName, "FSUCIQ",
								dbcollectionFileName);
					} else {
						objList = fileUploadService.getEnbDetails(id, fileName, dbcollectionFileName);
					}

					for (Map<String, String> enb : objList) {
						if (CommonUtil.isValidObject(neMappingEnbs) && !neMappingEnbs.contains(enb.get("eNBId"))) {
							// Ne Mapping for DSS - 19Nov2020
							if (programName.contains("VZN-5G-DSS")) {
								List<CIQDetailsModel> temp = fileUploadService.getCiqDetailsForRuleValidationsheet(
										enb.get("eNBId"), dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "eNBId");
								if (!ObjectUtils.isEmpty(temp) && temp.get(0).getCiqMap().containsKey("NEID")) {
									final String neid;
									neid = temp.get(0).getCiqMap().get("NEID").getHeaderValue();
									if (neMappingEnbs.contains(neid)) {
										NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
												.filter(x -> neid.equalsIgnoreCase(x.getEnbId()))
												.collect(Collectors.toList()).get(0);
										if (!ObjectUtils.isEmpty(neMappingEntity)) {
											neMappingEntity.setEnbId(enb.get("eNBId"));
											neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
											neMappingEntity.setCreationDate(new Date());
											fileUploadService.saveEnbDetails(neMappingEntity);
										}
									} else {
										NeMappingEntity neMappingEntity = new NeMappingEntity();

										neMappingEntity.setEnbId(enb.get("eNBId"));
										neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
										neMappingEntity.setCreationDate(new Date());
										fileUploadService.saveEnbDetails(neMappingEntity);
									}
								} else {
									NeMappingEntity neMappingEntity = new NeMappingEntity();

									neMappingEntity.setEnbId(enb.get("eNBId"));
									neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
									neMappingEntity.setCreationDate(new Date());
									fileUploadService.saveEnbDetails(neMappingEntity);
								}
							} else if (programName.contains("5G-CBAND")){
								NeMappingEntity neMappingEntity = new NeMappingEntity();

								neMappingEntity.setEnbId(enb.get("eNBId"));
								neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
								neMappingEntity.setCreationDate(new Date());
								fileUploadService.saveEnbDetails(neMappingEntity);
							} else {
								NeMappingEntity neMappingEntity = new NeMappingEntity();

								neMappingEntity.setEnbId(enb.get("eNBId"));
								neMappingEntity.setSiteName(enb.get("siteName"));
								neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
								neMappingEntity.setCreationDate(new Date());
								/*
								 * if(StringUtils.isNotEmpty(programName) && programName.contains("4G")) {
								 * NetworkConfigEntity
								 * netWorkConfigEntity=fileUploadService.getNeMappingEntity(enb.get("eNBId"),
								 * programId); if(netWorkConfigEntity!=null) {
								 * neMappingEntity.setNetworkConfigEntity(netWorkConfigEntity);
								 * neMappingEntity.setSiteConfigType("NB-IoT Add"); } }
								 */
								fileUploadService.saveEnbDetails(neMappingEntity);
							}
						} else if (CommonUtil.isValidObject(neMappingEnbs) && neMappingEnbs.contains(enb.get("eNBId"))
								&& programName.contains("VZN-5G-MM")) {
							NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
									.filter(x -> enb.get("eNBId").equalsIgnoreCase(x.getEnbId()))
									.collect(Collectors.toList()).get(0);
							if (neMappingEntity != null && StringUtils.isNotEmpty(enb.get("siteName"))
									&& !enb.get("siteName").equalsIgnoreCase(neMappingEntity.getSiteName())) {
								neMappingEntity.setSiteName(enb.get("siteName"));
								neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
								neMappingEntity.setCreationDate(new Date());
								fileUploadService.saveEnbDetails(neMappingEntity);
							}
						}
						ciqNeIds.add(enb.get("eNBId"));
					}

					// Auto NeMapping - 19/Nov/2020
					if (programName.contains("VZN-5G-DSS") || programName.contains("VZN-5G-MM") || programName.contains("5G-CBAND") 
							|| programName.contains("4G-FSU")) {
						neMappingEntities = neMappingService.getNeMapping(neMappingModel);
						neMappingEnbs = neMappingEntities.stream().map(x -> x.getEnbId()).collect(Collectors.toList());
						Map<String, List<String>> ipidList = new HashMap<>();
						Map<String, String> neVersionMap = new HashMap<>();
						if (programName.contains("VZN-5G-DSS")) {
							objList = fileUploadService.getEnbDetailssheet(id, fileName, "vDUGrowSiteLevel(Day1)CQ",
									dbcollectionFileName);
							ipidList = fileUploadService.getipDUidList(objList, id, fileName, dbcollectionFileName);
							List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "vDUGrowSiteLevel(Day1)CQ",
									dbcollectionFileName);
							List<Map<String, String>> objList3 = fileUploadService.getDSSAUPFList(gnbDataList, "AUPF_ID");
							objList3.addAll(fileUploadService.getDSSAUPFList(gnbDataList, "ACPF_ID"));
							List<CIQDetailsModel> gnbDataListday0 = fileUploadService.getsheetData(fileName, "vDUHELM(Day0)Orchestrator",
									dbcollectionFileName);
							ipidList = fileUploadService.getDSSipAupfGnbidList(objList3,gnbDataListday0, "vCUemsIpAddress", "AUPF_ID", ipidList);
							List<CIQDetailsModel> gnbDataListfsu = fileUploadService.getsheetData(fileName, "FSUCIQ",
									dbcollectionFileName);
							ipidList = fileUploadService.getipGnbidListFsu(gnbDataListfsu, "RS_IP", "NE_ID",ipidList);
							
							
						} else if(programName.contains("5G-CBAND")){
							List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "Day0_1",
									dbcollectionFileName);
							ipidList = fileUploadService.getipGnbidList(gnbDataList, "emsIpAddress", "NEID");
							ipidList = fileUploadService.getipAupfGnbidList(gnbDataList, "vCUemsIpAddress", "AUPF_ID", ipidList);
							ipidList = fileUploadService.getipAcpfGnbidList(gnbDataList, "vCUemsIpAddress", "ACPF_ID", ipidList);
							ipidList = fileUploadService.getipEnbGnbidList(gnbDataList, "eNBEmsIp", "ENB_ID", ipidList);
							
						} else if(programName.contains("4G-FSU")){
							List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "FSUCIQ",
									dbcollectionFileName);
							ipidList = fileUploadService.getipGnbidList(gnbDataList, "RS_IP", "NE_ID");
							neVersionMap = fileUploadService.getNeversionList(gnbDataList, "NE_ID", "NE_Version");
						} else {
							List<CIQDetailsModel> gnbDataList = fileUploadService.getsheetData(fileName, "5GNRCIQAU",
									dbcollectionFileName);
							ipidList = fileUploadService.getipGnbidList(gnbDataList, "EMS_IP", "GNODEB_AU_ID");
							neVersionMap = fileUploadService.getNeversionList(gnbDataList, "GNODEB_AU_ID", "ne_version");
						}
					

						Set<String> ipset = ipidList.keySet();
						Set<String> nullipset = new HashSet<>(ipset);
						List<NetworkConfigEntity> neList = neMappingService.getNeConfigList(programId);
						for (NetworkConfigEntity ne : neList) {

							for (String emsip : ipset) {
								try {
									InetAddress ip1 = InetAddress.getByName(emsip);
									InetAddress ip2 = InetAddress.getByName(ne.getNeRsIp());

									if (ip1.equals(ip2)) {
										nullipset.remove(emsip);
										if ("Active".equalsIgnoreCase(ne.getStatus()) && !ObjectUtils.isEmpty(ne.getNeVersionEntity()) 
												&& "Active".equalsIgnoreCase(ne.getNeVersionEntity().getStatus()) 
												&& !(programName.contains("VZN-5G-MM") || programName.contains("4G-FSU"))) {
											for (String gnbduid : ipidList.get(emsip)) {
												if (CommonUtil.isValidObject(neMappingEnbs)
														&& neMappingEnbs.contains(gnbduid)) {
													NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
															.filter(x -> gnbduid.equalsIgnoreCase(x.getEnbId()))
															.collect(Collectors.toList()).get(0);
													if (!ObjectUtils.isEmpty(neMappingEntity)) {
														neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
														neMappingEntity.setNetworkConfigEntity(ne);
														neMappingEntity.setSiteConfigType("NB-IoT Add");
														neMappingEntity.setCreationDate(new Date());
														fileUploadService.saveEnbDetails(neMappingEntity);
													}
												}
											}
										} else if ("Active".equalsIgnoreCase(ne.getStatus()) && !ObjectUtils.isEmpty(ne.getNeVersionEntity()) 
												&& (programName.contains("VZN-5G-MM") || programName.contains("4G-FSU"))) {
											for (String gnbduid : ipidList.get(emsip)) {
												if (CommonUtil.isValidObject(neMappingEnbs)
														&& neMappingEnbs.contains(gnbduid)
														&& neVersionMap.containsKey(gnbduid)
														&& neVersionMap.get(gnbduid).contains(ne.getNeVersionEntity().getNeVersion().toUpperCase())) {
													NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
															.filter(x -> gnbduid.equalsIgnoreCase(x.getEnbId()))
															.collect(Collectors.toList()).get(0);
													if (!ObjectUtils.isEmpty(neMappingEntity)) {
														neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
														neMappingEntity.setNetworkConfigEntity(ne);
														neMappingEntity.setSiteConfigType("NB-IoT Add");
														neMappingEntity.setCreationDate(new Date());
														fileUploadService.saveEnbDetails(neMappingEntity);
													}
												}
											}
										}
									}
								} catch (Exception e) {
									logger.error(
											"Exception during uploadCIQFile() in UploadCIQController Ip address format not correct"
													+ ExceptionUtils.getFullStackTrace(e));
								}
							}
						}

						for (String emsip : nullipset) {
							for (String gnbduid : ipidList.get(emsip)) {
								if (CommonUtil.isValidObject(neMappingEnbs) && neMappingEnbs.contains(gnbduid)) {
									NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
											.filter(x -> gnbduid.equalsIgnoreCase(x.getEnbId()))
											.collect(Collectors.toList()).get(0);
									if (!ObjectUtils.isEmpty(neMappingEntity)) {
										neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
										neMappingEntity.setNetworkConfigEntity(null);
										neMappingEntity.setSiteConfigType(null);
										neMappingEntity.setCreationDate(new Date());
										fileUploadService.saveEnbDetails(neMappingEntity);
									}
								}
							}
						}
					}
					// 4G auto ne mapping
					else if (programName.contains("VZN-4G-USM-LIVE")) {
						neMappingEntities = neMappingService.getNeMapping(neMappingModel);
						neMappingEnbs = neMappingEntities.stream().map(x -> x.getEnbId()).collect(Collectors.toList());
                                                String sheetName = "Upstate NY CIQ";
						//String sheetName = ciqFileName.split("-")[0];
						// String sheetName=ciqName[0];
						Map<String, List<String>> ipidList = new HashMap<>();
						// file name=WBV-VZ_CIQ_Ver_0.0.03_20201214.xlsx
						List<CIQDetailsModel> enbDataList = fileUploadService.getsheetData4G(fileName, sheetName,
								dbcollectionFileName);
						// enbDataList=enbDataList.stream().distinct().collect(Collectors.toList());
						/*
						 * distinctElements = enbDataList.stream() .filter( enbData -> enbData. )
						 * .collect( Collectors.toList() );
						 */
						ipidList = fileUploadService.getUSMIPList(enbDataList);
						
						Map<String, String> neVersionMap = fileUploadService.getNeversionList(enbDataList, "Samsung_eNB_ID", "ne_version");
						
						Set<String> ipset = ipidList.keySet();
						Set<String> nullipset = new HashSet<>(ipset);
						List<NetworkConfigEntity> neList = neMappingService.getNeConfigList(programId);
						List<String> nelist = new LinkedList<>();
						for (NetworkConfigEntity ne : neList) {

							for (String emsip : ipset) {
								try {
									InetAddress usmIP = InetAddress.getByName(emsip);
									InetAddress rsIP = InetAddress.getByName(ne.getNeRsIp());
									InetAddress ip = InetAddress.getByName(ne.getNeIp());

									// 2001:4888:a19:3143:1b4:292:0:100
									if (usmIP.equals(rsIP) || usmIP.equals(ip)) {
										nullipset.remove(emsip);
										if ("Active".equalsIgnoreCase(ne.getStatus()) && !ObjectUtils.isEmpty(ne.getNeVersionEntity())) {
												//&& "Active".equalsIgnoreCase(ne.getNeVersionEntity().getStatus())) {
											for (String enbid : ipidList.get(emsip)) {
												if (CommonUtil.isValidObject(neMappingEnbs)
														&& neMappingEnbs.contains(enbid)
														&& neVersionMap.containsKey(enbid)
														&& neVersionMap.get(enbid).contains(ne.getNeVersionEntity().getNeVersion().toUpperCase())) {
													NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
															.filter(x -> enbid.equalsIgnoreCase(x.getEnbId()))
															.collect(Collectors.toList()).get(0);
													if (!ObjectUtils.isEmpty(neMappingEntity)) {
														neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
														neMappingEntity.setNetworkConfigEntity(ne);
														if (usmIP.equals(rsIP)) {
															nelist.add(ne.getNeRsIp());
														} else {
															nelist.add(ne.getNeIp());
														}
														neMappingEntity.setSiteConfigType("NB-IoT Add");
														neMappingEntity.setCreationDate(new Date());
														fileUploadService.saveEnbDetails(neMappingEntity);
													}
												}
											}
										}
									}
								} catch (Exception e) {
									logger.error(
											"Exception during uploadCIQFile() in UploadCIQController Ip address format not correct"
													+ ExceptionUtils.getFullStackTrace(e));
								}
							}
						}
						List<String> enblist = new LinkedList<>();
						if (nullipset != null) {
							for (Map.Entry<String, List<String>> ipidlist : ipidList.entrySet()) {
								if (nelist.contains(ipidlist.getKey().toString())) {
									enblist.addAll(ipidlist.getValue());
								}
							}
						}
						for (String emsip : nullipset) {
							for (String enbid : ipidList.get(emsip)) {

								if (enblist.stream()
										.noneMatch(enblist1 -> enblist1.toString().equalsIgnoreCase(enbid))) {
									if (CommonUtil.isValidObject(neMappingEnbs) && neMappingEnbs.contains(enbid)) {
										NeMappingEntity neMappingEntity = neMappingEntities.parallelStream()
												.filter(x -> enbid.equalsIgnoreCase(x.getEnbId()))
												.collect(Collectors.toList()).get(0);
										if (!ObjectUtils.isEmpty(neMappingEntity)) {
											neMappingEntity.setProgramDetailsEntity(programDetailsEntity);
											neMappingEntity.setNetworkConfigEntity(null);
											neMappingEntity.setSiteConfigType(null);
											neMappingEntity.setCreationDate(new Date());
											fileUploadService.saveEnbDetails(neMappingEntity);
										}
									}

								}
							}
						}

					}

					neMappingEndTime = System.currentTimeMillis();
					logger.info("UploadCIQController.uploadCIQFile() time taken for NE Mapping: "
							+ (neMappingEndTime - startTime) + "ms");
				}
			} else if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("CHECKLIST")) {
				fileProcessMap = fileUploadService.processChecklist(preMigrationFile,
						fileTempPath.toString() + preMigrationFile.getOriginalFilename(), isAllowDuplicate, programId,
						ciqFileName);
			}
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("SCRIPT")) {
				String tempFilePath[] = tempFile.list();
				System.out.println(tempFilePath);
				String files[] = preMigfile.list();
				for (String deleteFiles : files) {
					if (tempFilePath[0].equals(FilenameUtils.removeExtension(deleteFiles))) {
						FileUtil.deleteFileOrFolder(preMigfile + "/" + deleteFiles);
					}
				}
				System.out.println(files);
				FileUtils.copyDirectory(tempFile, preMigfile);
				FileUtil.deleteFileOrFolder(fileTempPath.toString());
			} else if (CommonUtil.isValidObject(fileProcessMap) && fileProcessMap.containsKey("status")
					&& (boolean) fileProcessMap.get("status")) {
				FileUtils.copyDirectory(tempFile, preMigfile);
				FileUtil.deleteFileOrFolder(fileTempPath.toString());
			} else if (CommonUtil.isValidObject(fileProcessMap)) {
				FileUtil.deleteFileOrFolder(fileTempPath.toString());
				resultMap.put("status", Constants.FAIL);
				if (fileProcessMap.containsKey("reason")) {
					resultMap.put("reason", fileProcessMap.get("reason"));
				}
				return resultMap;
			}
			if(!ObjectUtils.isEmpty(ciqNeIds))
			{
				fileUploadService.upDateCiqNameInNeMapping(preMigrationFile.getOriginalFilename(), ciqNeIds,programId);
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILE_PROCESSES_SUCCESSFULL));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
			logger.error(
					"Exception during uploadCIQFile() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This method will upload the CIQFile
	 * 
	 * @param file
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPLOAD_CIQ, method = RequestMethod.POST)
	public JSONObject uploadCIQFile(@RequestPart(required = true, value = "CIQ") MultipartFile ciqFile,
			@RequestPart(required = true, value = "SCRIPTFILE") List<MultipartFile> scriptFiles,
			@RequestPart(required = false, value = "CHECKLIST") MultipartFile checkListFile,
			@RequestParam("uploadCiqFileDetails") String retriveCiqDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String programName = null;
		Integer programId = null;
		String fileSourceType = null;
		String activate = null;
		String remarks = null;
		String scriptFileNames = "";
		String ciqFileName = "";
		String processCiqPath = "";
		AtomicBoolean ovScheduleStatus=new AtomicBoolean();
		try {
			JSONParser parser = new JSONParser();
			JSONObject uploadCiqFileDetails = (JSONObject) parser.parse(retriveCiqDetails);
			boolean isAllowDuplicate = Boolean.valueOf(uploadCiqFileDetails.get("allowDuplicate").toString());
			sessionId = uploadCiqFileDetails.get("sessionId").toString();
			serviceToken = uploadCiqFileDetails.get("serviceToken").toString();
			programName = uploadCiqFileDetails.get("programName").toString();
			programId = Integer.parseInt(uploadCiqFileDetails.get("programId").toString());
			fileSourceType = uploadCiqFileDetails.get("fileSourceType").toString();
			activate = uploadCiqFileDetails.get("activate").toString();
			remarks = uploadCiqFileDetails.get("remarks").toString();

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (scriptFiles != null && scriptFiles.size() > 0) {
				String rfScriptValidations = rfScriptsValidations(scriptFiles, sessionId,
						uploadCiqFileDetails.get("programId").toString(),programName);
				if (StringUtils.isNotEmpty(rfScriptValidations) && !"success".equalsIgnoreCase(rfScriptValidations)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", rfScriptValidations);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					return resultMap;
				}
			}
			
			if(uploadCiqFileDetails.containsKey("overallInteraction") && !ObjectUtils.isEmpty(uploadCiqFileDetails.get("overallInteraction")) && "true".equalsIgnoreCase(uploadCiqFileDetails.get("overallInteraction").toString() ))
			{
				if(uploadCiqFileDetails.containsKey("scriptfiles") && !ObjectUtils.isEmpty(uploadCiqFileDetails.get("scriptfiles")) )
				{
					List<String> scriptList=Arrays.asList(uploadCiqFileDetails.get("scriptfiles").toString().split(","));
					JSONObject niIdDetails=ovScheduledTaskService.uploadScheduledCiqDetails(scriptList);
					if (Constants.SUCCESS.equalsIgnoreCase(niIdDetails.get("status").toString())) {
						ovScheduleStatus.getAndSet(true);
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", niIdDetails.get("reason").toString());
						return resultMap;
					}
				
				}
					
				
			}
			StringBuilder uploadPath = new StringBuilder();
			StringBuilder ciqUploadPath = new StringBuilder();
			StringBuilder scriptUploadPath = new StringBuilder();
			StringBuilder checklistUploadPath = new StringBuilder();

			StringBuilder ciqUploadTempPath = new StringBuilder();
			StringBuilder scriptUploadTempPath = new StringBuilder();
			StringBuilder checklistUploadTempPath = new StringBuilder();

			String fileName = ciqFile.getOriginalFilename();

			uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR);

			ciqUploadPath.append(uploadPath)
					.append(Constants.PRE_MIGRATION_CIQ
							.replace("filename", StringUtils.substringBeforeLast(fileName.toString(), "."))
							.replaceAll(" ", "_"));
			scriptUploadPath.append(uploadPath)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(fileName.toString(), "."))
							.replaceAll(" ", "_"));
			checklistUploadPath.append(uploadPath)
					.append(Constants.PRE_MIGRATION_CHECKLIST
							.replace("filename", StringUtils.substringBeforeLast(fileName.toString(), "."))
							.replaceAll(" ", "_"));

			ciqUploadTempPath.append(ciqUploadPath.toString()).append(Constants.TEMP);
			scriptUploadTempPath.append(scriptUploadPath.toString()).append(Constants.TEMP);
			checklistUploadTempPath.append(checklistUploadPath.toString()).append(Constants.TEMP);

			String ciqFilePath = ciqUploadPath.toString();
			String scriptFilePath = scriptUploadPath.toString();
			String checkListFilePath = checklistUploadPath.toString();

			if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Uploading of same CIQ  is in progress");
				return resultMap;
			} else {
				processCiqPath = ciqFilePath;
				GlobalStatusMap.inProgressCiqStatusMap.put(ciqFilePath, ciqFilePath);
			}

			File ciqDir = new File(ciqUploadPath.toString());
			if (ciqDir.exists()) {
				/*if (!isAllowDuplicate) {
					resultMap = CommonUtil.buildResponseJson(Constants.CONFIRM,
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CIQ_EXCEL_DUPLICATE), sessionId,
							serviceToken);

					if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
						GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
					}
					return resultMap;
				}*/
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "SRCT CIQ already Fetched or Uploaded. For any changes RF CIQ Version number should be different.");
				return resultMap;
			}

			JSONObject fileProcessResult = new JSONObject();

			if (CommonUtil.isValidObject(ciqFile)
					&& fileUploadService.uploadMultipartFile(ciqFile, ciqUploadTempPath.toString())) {
				ciqFileName = ciqFile.getOriginalFilename();
				fileProcessResult = preMigrationFileProcess(ciqFile, ciqUploadPath, ciqUploadTempPath, isAllowDuplicate,
						"CIQ", programId, ciqFileName, programName);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					deleteCiqDir(programId, ciqFileName);
					resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						resultMap.put("reason", fileProcessResult.get("reason"));
					}
					if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
						GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
					}
					return resultMap;
				}
			} else {
				deleteCiqDir(programId, ciqFileName);
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
				if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
					GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
				}
				return resultMap;
			}

			if (CommonUtil.isValidObject(scriptFiles) && scriptFiles.size() > 0) {
				for (MultipartFile scriptFile : scriptFiles) {
					scriptFileNames = scriptFileNames + scriptFile.getOriginalFilename() + ",";
					scriptUploadPath.setLength(0);
					scriptUploadPath.append(scriptFilePath);
					if (CommonUtil.isValidObject(scriptFile)
							&& fileUploadService.uploadMultipartFile(scriptFile, scriptUploadTempPath.toString())) {
						fileProcessResult = preMigrationFileProcess(scriptFile, scriptUploadPath, scriptUploadTempPath,
								isAllowDuplicate, "SCRIPT", programId, ciqFileName, programName);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							deleteCiqDir(programId, ciqFileName);
							resultMap.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								resultMap.put("reason", fileProcessResult.get("reason"));
							}
							if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
								GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
							}
							return resultMap;
						}
						String fileExtension = FilenameUtils.getExtension(scriptUploadPath.toString());
						logger.info("uploadCIQFile() extentionName:" + fileExtension);
						List<String> zipExtensions = Arrays.asList("tar.gz", "tgz", "gz", "zip", "7z");
						List<String> txtExtensions = Arrays.asList("txt");
						long underScoreCharCount = scriptFile.getOriginalFilename().chars().filter(num -> num == '_')
								.count();
						if (StringUtils.isNotEmpty(fileExtension) && zipExtensions.contains(fileExtension)) {
							String unzipDirPath = scriptUploadPath.toString().replace(scriptFile.getOriginalFilename(),
									"");
							String folderName = "";
							if (scriptFile.getOriginalFilename().contains("_")&& !scriptFile.getOriginalFilename().contains("_ENDC")) {
								folderName = StringUtils
										.substringBeforeLast(scriptFile.getOriginalFilename().toString(), "_");
								if (CommonUtil.isValidObject(folderName) && folderName.contains("_")) {
									folderName = StringUtils.substringAfter(folderName, "_");
								} else {
									folderName = StringUtils.substringBefore(folderName, " ");
								}
								if (CommonUtil.isValidObject(folderName)) {
									unzipDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
											+ Constants.SEPARATOR + folderName;
								}
							} else {
								unzipDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
										+ Constants.SEPARATOR
										+ FilenameUtils.removeExtension(scriptFile.getOriginalFilename());
							}
							logger.info("uploadCIQFile() folderName:" + folderName + ", unzipDirPath" + unzipDirPath);
							File unzipDir = new File(unzipDirPath);
							if (unzipDir.exists()) {
								FileUtil.deleteFileOrFolder(unzipDirPath);
							}
							if (!unzipDir.exists()) {
								FileUtil.createDirectory(unzipDirPath);
							}
							boolean unzipStatus = fileUploadService.unzipFile(scriptUploadPath.toString(),
									unzipDirPath);
							if (!unzipStatus) {
								resultMap = CommonUtil
										.buildResponseJson(Constants.CONFIRM,
												GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UNZIP_SCRIPT_FILE),
												sessionId, serviceToken);
								if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
									GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
								}
								return resultMap;
							}
						} else if (underScoreCharCount >= 3 && txtExtensions.contains(fileExtension)) {
							String fileMoveDirPath = scriptUploadPath.toString()
									.replace(scriptFile.getOriginalFilename(), "");
							String folderName = "";
							if (scriptFile.getOriginalFilename().contains("_")) {
								folderName = scriptFile.getOriginalFilename().toString().substring(
										StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(), "_", 2)
												+ 1,
										StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(), "_",
												3));
								if (CommonUtil.isValidObject(folderName)) {
									fileMoveDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
											+ Constants.SEPARATOR + folderName;
								}
								logger.info("uploadCIQFile() folderName:" + folderName + ", fileMoveDirPath"
										+ fileMoveDirPath);
								File fileMoveDir = new File(fileMoveDirPath);
								if (!fileMoveDir.exists()) {
									FileUtil.createDirectory(fileMoveDirPath);
								}
								FileUtils.copyFileToDirectory(new File(scriptUploadPath.toString()), fileMoveDir);
								// FileUtil.deleteFileOrFolder(scriptUploadPath.toString());
							}
						} else {
							/*
							 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
							 * Constants.FAIL); resultMap.put("reason",
							 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
							 * FAILED_TO_UPLOAD_SCRIPT_FILE)); return resultMap;
							 */
						}
					}
				}
			}
			if (!CommonUtil.isValidObject(checkListFile)) {
				CiqUploadAuditTrailDetEntity auditTrailDetEntity = fileUploadService
						.getLatestCheckListByProgram(programId);
				System.out.println("checklist file path"+ auditTrailDetEntity.getChecklistFilePath());
				System.out.println("checklist file name"+ auditTrailDetEntity.getChecklistFileName());
				if (CommonUtil.isValidObject(auditTrailDetEntity)
						&& CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFileName())
						&& auditTrailDetEntity.getChecklistFileName().length() > 0
						&& CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFilePath())) {
					String checkListPath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
							+ auditTrailDetEntity.getChecklistFilePath() + auditTrailDetEntity.getChecklistFileName();
					File latestCheckListFile = new File(checkListPath);
					if (latestCheckListFile.exists()) {
						FileInputStream input = new FileInputStream(latestCheckListFile);
						MultipartFile multipartFile = new MockMultipartFile(
								checklistUploadTempPath.toString() + "" + auditTrailDetEntity.getChecklistFileName(),
								auditTrailDetEntity.getChecklistFileName(), "text/plain", IOUtils.toByteArray(input));
						checkListFile = multipartFile;
					}
				}
			}

			if (CommonUtil.isValidObject(checkListFile)
					&& fileUploadService.uploadMultipartFile(checkListFile, checklistUploadTempPath.toString())) {
				fileProcessResult = preMigrationFileProcess(checkListFile, checklistUploadPath, checklistUploadTempPath,
						isAllowDuplicate, "CHECKLIST", programId, ciqFileName, programName);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					deleteCiqDir(programId, ciqFileName);
					resultMap.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						resultMap.put("reason", fileProcessResult.get("reason"));
					}
					if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
						GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
					}
					return resultMap;
				}
			} else {
				/*
				 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
				 * Constants.FAIL); resultMap.put("reason",
				 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
				 * CHECKLIST_FILE_NOT_FOUND)); return resultMap;
				 */
			}
			if (fileProcessResult != null && fileProcessResult.containsKey("status")
					&& fileProcessResult.get("status").equals(Constants.SUCCESS)) {
				CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = new CiqUploadAuditTrailDetModel();
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				customerDetailsModel.setId(programId);
				List<CustomerDetailsEntity> detailsEntities = customerService
						.getCustomerDetailsList(customerDetailsModel);
				if (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
					ciqUploadAuditTrailDetModel.setProgramDetailsEntity(detailsEntities.get(0));
				}
				String ciqFileSavePath = ciqFilePath;
				String scriptFileSavePath = scriptFilePath;

				ciqFileSavePath = ciqFileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
				scriptFileSavePath = scriptFileSavePath
						.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
				ciqUploadAuditTrailDetModel.setCiqFilePath(ciqFileSavePath);
				ciqUploadAuditTrailDetModel.setScriptFilePath(scriptFileSavePath);

				if (CommonUtil.isValidObject(checkListFile)) {
					String checkListFileSavePath = checkListFilePath;
					checkListFileSavePath = checkListFileSavePath
							.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
					ciqUploadAuditTrailDetModel.setChecklistFilePath(checkListFileSavePath);
				} else {
					ciqUploadAuditTrailDetModel.setChecklistFilePath("");
				}
				if(ovScheduleStatus.get())
				{
					ciqUploadAuditTrailDetModel.setFileSourceType("OV-Force Upload");
				}else
					ciqUploadAuditTrailDetModel.setFileSourceType(Constants.UPLOAD);
				ciqUploadAuditTrailDetModel.setCiqVersion(Constants.CIQ_VERSION_ORIGINAL);
				ciqUploadAuditTrailDetModel.setCiqFileName(ciqFile.getOriginalFilename());
				if (CommonUtil.isValidObject(scriptFileNames) && scriptFileNames.length() > 0) {
					ciqUploadAuditTrailDetModel
							.setScriptFileName(scriptFileNames.substring(0, scriptFileNames.length() - 1));
				}
				if (CommonUtil.isValidObject(checkListFile)) {
					ciqUploadAuditTrailDetModel.setChecklistFileName(checkListFile.getOriginalFilename());
				} else {
					ciqUploadAuditTrailDetModel.setChecklistFileName("");
				}
				
				if(ovScheduleStatus.get())
				{	if(!remarks.equals(""))
						ciqUploadAuditTrailDetModel.setRemarks(remarks +"," +"Uploaded through OV Force Upload Functionality");
					else
						ciqUploadAuditTrailDetModel.setRemarks("Uploaded through OV Force Upload Functionality");
				}else
					ciqUploadAuditTrailDetModel.setRemarks(remarks);
				CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = ciqUploadAuditTrailDetailsDto
						.getCiqUploadAuditTrailDetEntity(ciqUploadAuditTrailDetModel, sessionId);
				fileUploadService.createCiqAudit(ciqUploadAuditTrailDetEntity);
				commonUtil
						.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
								Constants.ACTION_UPLOAD, "PreMigration Files Uploaded Successfully CIQ: " + ciqFileName
										+ " SCRIPT: " + scriptFileNames + " CHECKLIST: " + checkListFile + "",
								sessionId);
				if (CommonUtil.isValidObject(activate) && activate.equalsIgnoreCase("true")) {
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
							Constants.ACTION_UPLOAD, "PreMigration Files Activated Successfully", sessionId);
				}
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.CIQ_FILE_UPLOADED_SUCCESSFULLY));
				if(ovScheduleStatus.get())
				{
					List<String> scriptList=Arrays.asList(uploadCiqFileDetails.get("scriptfiles").toString().split(","));
					JSONObject resultStatus=ovScheduledTaskService.processScheduledCiqDetails(scriptList, ciqUploadAuditTrailDetEntity);
				         if(Constants.FAIL.equalsIgnoreCase(resultStatus.get("status").toString()))
				         {
						StringBuilder reasonBuilder = new StringBuilder();
						reasonBuilder.append(
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.CIQ_FILE_UPLOADED_SUCCESSFULLY));
						reasonBuilder.append(" but there is some problem with scheduling neIds");
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason", reasonBuilder.toString());
				         }else {
				        	 CustomerDetailsEntity programmeEntity=new CustomerDetailsEntity();
								programmeEntity.setId(programId);
				 			dailyOvScheduleConfig.OvScheduledTasksExcution(scriptList,programmeEntity,"OV-Force Upload");
				         }
				        	 
				
				}

			} else {
				deleteCiqDir(programId, ciqFileName);
				resultMap.put("status", Constants.FAIL);
				if (fileProcessResult.containsKey("reason")) {
					resultMap.put("reason", fileProcessResult.get("reason"));
				}
			}
			if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(ciqFilePath)) {
				GlobalStatusMap.inProgressCiqStatusMap.remove(ciqFilePath);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
			if (GlobalStatusMap.inProgressCiqStatusMap.containsKey(processCiqPath)) {
				GlobalStatusMap.inProgressCiqStatusMap.remove(processCiqPath);
			}
			deleteCiqDir(programId, ciqFileName);
			logger.error(
					"Exception during uploadCIQFile() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
		}

		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.FETCH_PRE_MIGRATION_FILES, method = RequestMethod.POST)
	public JSONObject fetchPreMigrationFiles(@RequestBody JSONObject fetchCiqDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String programName = null;
		Integer scriptNetworkConfigId = null;
		Integer programId = null;
		Integer customerId = null;
		Integer ciqNetworkConfigId = null;
		boolean isAllowDuplicate = false;
		MultipartFile ciqFile = null;
		MultipartFile scriptFile = null;
		MultipartFile checkListFile = null;
		String remarks = null;
		String activate = null;
		// String ciqFileName = null;
		String scriptFileNames = "";
		long startTime = System.currentTimeMillis();
		long ciqFetchTime = System.currentTimeMillis();
		long rfScriptsFetchTime = System.currentTimeMillis();

		try {
			sessionId = fetchCiqDetails.get("sessionId").toString();
			serviceToken = fetchCiqDetails.get("serviceToken").toString();

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			customerId = Integer.parseInt(fetchCiqDetails.get("customerId").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}

			ObjectMapper mapper = new ObjectMapper();

			FetchDetailsModel fetchDetailsModel = mapper.readValue(fetchCiqDetails.toJSONString(),
					new TypeReference<FetchDetailsModel>() {
					});
			
			/*fetchDetailsModel.setRemarks("Fetch from SRCT");
			
			JSONObject statusOfOv=fetchProcessService.getOvGetDetails();
			
			if(statusOfOv.containsKey("trakerDetails"))
			{
				List<TrackerDetailsModel> allInfoTrakerList=(List<TrackerDetailsModel>)statusOfOv.get("trakerDetails");
				if(!ObjectUtils.isEmpty(allInfoTrakerList))
				{
					fetchProcessService.shedulingFetchDetails(allInfoTrakerList);
				}
			}
			
			
			if(statusOfOv.containsKey("statusCode") && "205".equalsIgnoreCase(statusOfOv.get("statusCode").toString()))
			{
				String responseBody=statusOfOv.get("response").toString();
				FetchOVMapModel fetchOVMapModel = new ObjectMapper().readValue(responseBody, FetchOVMapModel.class);
				FetchOVResponseMdel fetchOVResponseMdel=fetchProcessService.getOvResponseDetails(fetchOVMapModel,"4G");
				if(ObjectUtils.isEmpty(fetchOVResponseMdel.getMarket()))
				{
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", " Markets not Provided By OV");
					return resultMap;
				}else if(ObjectUtils.isEmpty(fetchOVResponseMdel.getNeidList()))
				{
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Ne Ids not Provided By OV");
					return resultMap;
				}
				fetchDetailsModel.setMarket(new ArrayList<>(fetchOVResponseMdel.getMarket()));
				fetchDetailsModel.setRfScriptList(new ArrayList<>(fetchOVResponseMdel.getNeidList()));
				fetchDetailsModel.setRemarks("Fetch from OV");
			}else if(statusOfOv.containsKey("statusCode") && "200".equalsIgnoreCase(statusOfOv.get("statusCode").toString())) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "OV Username OR Password is Wrong!");
				return resultMap;
			}else if(statusOfOv.containsKey("fetchIntraction") && Constants.OV_INTRACTION_OFF.equalsIgnoreCase(statusOfOv.get("fetchIntraction").toString())) {
				
				if(ObjectUtils.isEmpty(fetchDetailsModel.getMarket()))
				{
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Please Provide Market Names");
					return resultMap;
				}else if(ObjectUtils.isEmpty(fetchDetailsModel.getRfScriptList()))
				{
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "Please Provide NeIds");
					return resultMap;
				}
				
			}
			
			if(ObjectUtils.isEmpty(fetchDetailsModel.getMarket()))
			{
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", "Please Provide Market Names");
				return resultMap;
			}*/
			fetchProcessService.fetchExtraction(fetchCiqDetails, resultMap, sessionId, serviceToken, fetchDetailsModel);
			/*
			 * List<String> marketNamesList = fetchDetailsModel.getMarket(); if
			 * (!ObjectUtils.isEmpty(marketNamesList)) { for (String marketName :
			 * marketNamesList) {
			 * 
			 * List<String> completedMarketsList=fetchDetailsModel.getCompletedMarkets();
			 * if(ObjectUtils.isEmpty( completedMarketsList)) { completedMarketsList=new
			 * ArrayList<>(); }else if(completedMarketsList.contains(marketName)){
			 * 
			 * continue; }
			 * 
			 * scriptFileNames = "";
			 * 
			 * isAllowDuplicate =
			 * Boolean.valueOf(fetchCiqDetails.get("allowDuplicate").toString());
			 * programName = fetchCiqDetails.get("programName").toString(); programId =
			 * Integer.parseInt(fetchCiqDetails.get("programId").toString());
			 * ciqNetworkConfigId =
			 * Integer.parseInt(fetchCiqDetails.get("ciqNetworkConfigId").toString());
			 * scriptNetworkConfigId =
			 * Integer.parseInt(fetchCiqDetails.get("scriptNetworkConfigId").toString()); if
			 * (!CommonUtil.isValidObject(ciqNetworkConfigId) ||
			 * !CommonUtil.isValidObject(scriptNetworkConfigId)) { resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
			 * return resultMap; } remarks = fetchCiqDetails.get("remarks").toString();
			 * activate = fetchCiqDetails.get("activate").toString();
			 * 
			 * Map<String, Object> objMap = new HashMap<String, Object>();
			 * NetworkConfigModel networkConfigModel = new NetworkConfigModel();
			 * networkConfigModel.setId(ciqNetworkConfigId); List<NetworkConfigEntity>
			 * networkConfigEntities = networkConfigService
			 * .getNetworkConfigDetails(networkConfigModel);
			 * 
			 * StringBuilder filePath = new StringBuilder(); StringBuilder ciqFilePath = new
			 * StringBuilder(); StringBuilder scriptFilePath = new StringBuilder();
			 * StringBuilder checklistFilePath = new StringBuilder();
			 * 
			 * StringBuilder ciqFileTempPath = new StringBuilder(); StringBuilder
			 * scriptFileTempPath = new StringBuilder(); StringBuilder checklistFileTempPath
			 * = new StringBuilder();
			 * 
			 * String ciqFileFetchPath = ""; String scriptFileFetchPath = ""; String
			 * checkListFileFetchPath = "";
			 * 
			 * filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")) //
			 * .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
			 * .append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
			 * .append(Constants.SEPARATOR);
			 * 
			 * JSONObject fileProcessResult = new JSONObject();
			 * 
			 * if (networkConfigEntities != null && networkConfigEntities.size() > 0) {
			 * objMap.put("port",
			 * Integer.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").
			 * toString())); ProgramTemplateModel programTemplateModel = new
			 * ProgramTemplateModel(); CustomerDetailsEntity programDetailsEntity = new
			 * CustomerDetailsEntity(); programDetailsEntity.setId(programId);
			 * programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			 * programTemplateModel.setLabel(Constants.CIQ_FILE_PATH);
			 * List<ProgramTemplateEntity> entities = customerService
			 * .getProgTemplateDetails(programTemplateModel); if
			 * (CommonUtil.isValidObject(entities) && entities.size() > 0 &&
			 * CommonUtil.isValidObject(entities.get(0).getValue()) &&
			 * entities.get(0).getValue().length() > 0) { objMap.put("sourcePath",
			 * entities.get(0).getValue()); } else { resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": " + Constants.CIQ_FILE_PATH); return
			 * resultMap; }
			 * 
			 * programTemplateModel.setLabel(Constants.CIQ_NAME_TEMPLATE); entities =
			 * customerService.getProgTemplateDetails(programTemplateModel); if
			 * (CommonUtil.isValidObject(entities) && entities.size() > 0 &&
			 * CommonUtil.isValidObject(entities.get(0).getValue()) &&
			 * entities.get(0).getValue().length() > 0) { objMap.put("fileName",
			 * entities.get(0).getValue()); } else { resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": " + Constants.CIQ_NAME_TEMPLATE); return
			 * resultMap; } File dir = new File(filePath.toString()); if (!dir.exists()) {
			 * FileUtil.createDirectory(filePath.toString()); }
			 * objMap.put("destinationPath", filePath.toString()); startTime =
			 * System.currentTimeMillis(); Map<String, Object> result =
			 * fileUploadService.fetchFileFromServer(networkConfigEntities.get(0), objMap,
			 * marketName, fetchDetailsModel, "CIQ"); ciqFetchTime =
			 * System.currentTimeMillis(); logger.
			 * info("UploadCIQController.fetchPreMigrationFiles() time taken for fetching ciq: "
			 * + (ciqFetchTime - startTime) + "ms");
			 * 
			 * if (CommonUtil.isValidObject(result) && result.containsKey("reason") &&
			 * result.get("reason").equals("No such file")) { resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_NOT_FOUND));
			 * return resultMap; } else { List<String> fileList = (List<String>)
			 * result.get("fileList"); if (commonUtil.isValidObject(fileList) &&
			 * fileList.size() > 0) { String fileName = fileList.get(0);
			 * 
			 * File file = new File(filePath.toString() + "" + fileName);
			 * 
			 * ciqFilePath.append(filePath) .append(Constants.PRE_MIGRATION_CIQ
			 * .replace("filename", StringUtils.substringBeforeLast(fileName.toString(),
			 * ".")) .replaceAll(" ", "_")); scriptFilePath.append(filePath)
			 * .append(Constants.PRE_MIGRATION_SCRIPT .replace("filename",
			 * StringUtils.substringBeforeLast(fileName.toString(), ".")) .replaceAll(" ",
			 * "_")); checklistFilePath.append(filePath)
			 * .append(Constants.PRE_MIGRATION_CHECKLIST .replace("filename",
			 * StringUtils.substringBeforeLast(fileName.toString(), ".")) .replaceAll(" ",
			 * "_"));
			 * 
			 * ciqFileTempPath.append(ciqFilePath.toString()).append(Constants.TEMP);
			 * scriptFileTempPath.append(scriptFilePath.toString()).append(Constants.TEMP);
			 * checklistFileTempPath.append(checklistFilePath.toString()).append(Constants.
			 * TEMP);
			 * 
			 * ciqFileFetchPath = ciqFilePath.toString(); scriptFileFetchPath =
			 * scriptFilePath.toString(); checkListFileFetchPath =
			 * checklistFilePath.toString();
			 * 
			 * File ciqDir = new File(ciqFilePath.toString()); if (ciqDir.exists()) { if
			 * (!isAllowDuplicate) { resultMap = CommonUtil
			 * .buildResponseJson(Constants.CONFIRM, GlobalInitializerListener.faultCodeMap
			 * .get(FaultCodes.CIQ_EXCEL_DUPLICATE), sessionId, serviceToken); //
			 * resultMap.put("completedMarkets", completedMarketsList); isAllowDuplicate =
			 * true; // return resultMap; }
			 * 
			 * }
			 * 
			 * if (file.exists()) { FileInputStream input = new FileInputStream(file);
			 * MultipartFile multipartFile = new MockMultipartFile( filePath.toString() + ""
			 * + fileName, fileName, "text/plain", IOUtils.toByteArray(input)); ciqFile =
			 * multipartFile; if (CommonUtil.isValidObject(multipartFile) &&
			 * fileUploadService .uploadMultipartFile(ciqFile, ciqFileTempPath.toString()))
			 * { file.delete(); ciqFileName = multipartFile.getOriginalFilename();
			 * fileProcessResult = preMigrationFileProcess(ciqFile, ciqFilePath,
			 * ciqFileTempPath, isAllowDuplicate, "CIQ", programId, ciqFileName,
			 * programName); if (fileProcessResult != null &&
			 * fileProcessResult.containsKey("status") &&
			 * fileProcessResult.get("status").equals(Constants.FAIL)) {
			 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); if (fileProcessResult.containsKey("reason")) {
			 * resultMap.put("reason", fileProcessResult.get("reason")); } return resultMap;
			 * } } else { deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap
			 * .get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE)); return resultMap; } } } else {
			 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap
			 * .get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE)); return resultMap; } } } else {
			 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
			 * return resultMap; }
			 * 
			 * if (!CommonUtil.isValidObject(ciqFileName)) { deleteCiqDir(programId,
			 * ciqFileName); resultMap.put("status", Constants.FAIL);
			 * resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FAILED_TO_FETCH_CIQ_FILE)); return resultMap; }
			 * 
			 * networkConfigModel = new NetworkConfigModel();
			 * networkConfigModel.setId(scriptNetworkConfigId); networkConfigEntities =
			 * networkConfigService.getNetworkConfigDetails(networkConfigModel); if
			 * (networkConfigEntities != null && networkConfigEntities.size() > 0) {
			 * objMap.put("port",
			 * Integer.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").
			 * toString())); ProgramTemplateModel programTemplateModel = new
			 * ProgramTemplateModel(); CustomerDetailsEntity programDetailsEntity = new
			 * CustomerDetailsEntity(); programDetailsEntity.setId(programId);
			 * programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			 * programTemplateModel.setLabel(Constants.SCRIPT_FILE_PATH);
			 * List<ProgramTemplateEntity> entities = customerService
			 * .getProgTemplateDetails(programTemplateModel); String todayDateFolderName =
			 * DateUtil.dateToString(new Date(), Constants.MM_DD_YY); if
			 * (CommonUtil.isValidObject(entities) && entities.size() > 0 &&
			 * CommonUtil.isValidObject(entities.get(0).getValue()) &&
			 * entities.get(0).getValue().length() > 0) { String sourcePath =
			 * entities.get(0).getValue().replaceAll("date", todayDateFolderName);
			 * objMap.put("sourcePath", sourcePath); } else { deleteCiqDir(programId,
			 * ciqFileName); resultMap.put("status", Constants.FAIL);
			 * resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": " + Constants.SCRIPT_FILE_PATH); return
			 * resultMap; }
			 * 
			 * programTemplateModel.setLabel(Constants.SCRIPT_NAME_TEMPLATE); entities =
			 * customerService.getProgTemplateDetails(programTemplateModel); if
			 * (CommonUtil.isValidObject(entities) && entities.size() > 0 &&
			 * CommonUtil.isValidObject(entities.get(0).getValue()) &&
			 * entities.get(0).getValue().length() > 0) { objMap.put("fileName",
			 * entities.get(0).getValue()); } else { deleteCiqDir(programId, ciqFileName);
			 * resultMap.put("status", Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": " + Constants.SCRIPT_NAME_TEMPLATE);
			 * return resultMap; } File dir = new File(scriptFileTempPath.toString()); if
			 * (!dir.exists()) { FileUtil.createDirectory(scriptFileTempPath.toString()); }
			 * objMap.put("destinationPath", scriptFilePath.toString()); startTime =
			 * System.currentTimeMillis(); Map<String, Object> result =
			 * fileUploadService.fetchFileFromServer(networkConfigEntities.get(0), objMap,
			 * marketName, fetchDetailsModel, "RF_SCRIPTS"); rfScriptsFetchTime =
			 * System.currentTimeMillis(); logger.info(
			 * "UploadCIQController.fetchPreMigrationFiles() time taken for fetching RF script Files: "
			 * + (rfScriptsFetchTime - startTime) + "ms"); if
			 * (CommonUtil.isValidObject(result) && result.containsKey("reason") &&
			 * result.get("reason").equals("No such file")) { deleteCiqDir(programId,
			 * ciqFileName); resultMap.put("status", Constants.FAIL);
			 * resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND)
			 * ); return resultMap; } else {
			 * 
			 * List<String> existingScripts = new LinkedList<>();
			 * 
			 * if (isAllowDuplicate)
			 * 
			 * { CiqUploadAuditTrailDetEntity existCiqUploadAuditTrailDetEntity =
			 * fileUploadRepository .getCiqAuditBasedONFileNameAndProgram(ciqFileName,
			 * programId); if (existCiqUploadAuditTrailDetEntity != null && StringUtils
			 * .isNotEmpty(existCiqUploadAuditTrailDetEntity.getScriptFileName())) {
			 * existingScripts = new LinkedList(Arrays
			 * .asList(existCiqUploadAuditTrailDetEntity.getScriptFileName().split(",")));;
			 * } } List<String> fileList = (List<String>) result.get("fileList"); if
			 * (commonUtil.isValidObject(fileList) && fileList.size() > 0) { for (String
			 * fileName : fileList) { File file = new File(scriptFilePath.toString() +
			 * fileName); if (file.exists()) { scriptFileNames = scriptFileNames + fileName
			 * + ","; if (existingScripts.contains(fileName)) {
			 * existingScripts.remove(fileName); } FileInputStream input = new
			 * FileInputStream(file); MultipartFile multipartFile = new MockMultipartFile(
			 * scriptFileTempPath.toString() + "" + fileName, fileName, "text/plain",
			 * IOUtils.toByteArray(input)); scriptFile = multipartFile; if
			 * (CommonUtil.isValidObject(multipartFile) && fileUploadService
			 * .uploadMultipartFile(multipartFile, scriptFileTempPath.toString())) {
			 * 
			 * fileProcessResult = preMigrationFileProcess(multipartFile, scriptFilePath,
			 * scriptFileTempPath, isAllowDuplicate, "SCRIPT", programId, ciqFileName,
			 * programName); if (fileProcessResult != null &&
			 * fileProcessResult.containsKey("status") &&
			 * fileProcessResult.get("status").equals(Constants.FAIL) &&
			 * fileProcessResult.get("reason").equals("File Already Exist")) { resultMap =
			 * CommonUtil.buildResponseJson(Constants.CONFIRM,
			 * GlobalInitializerListener.faultCodeMap
			 * .get(FaultCodes.SCRIPT_FILE_DUPLICATE), sessionId, serviceToken); return
			 * resultMap; } String fileExtension = FilenameUtils
			 * .getExtension(scriptFilePath.toString()); List<String> zipExtensions =
			 * Arrays.asList("tar.gz", "tgz", "gz", "zip", "7z"); List<String> txtExtensions
			 * = Arrays.asList("txt"); long underScoreCharCount =
			 * scriptFile.getOriginalFilename().chars() .filter(num -> num == '_').count();
			 * if (zipExtensions.contains(fileExtension)) { String unzipDirPath =
			 * scriptFilePath.toString() .replace(scriptFile.getOriginalFilename(), "");
			 * String folderName = ""; if (fileName.contains("_")) { folderName =
			 * StringUtils.substringBeforeLast(fileName, "_"); if
			 * (CommonUtil.isValidObject(folderName) && folderName.contains("_")) {
			 * folderName = StringUtils.substringAfter(folderName, "_"); } else { folderName
			 * = StringUtils.substringBefore(folderName, " "); } if
			 * (CommonUtil.isValidObject(folderName)) { unzipDirPath = StringUtils
			 * .substringBeforeLast(scriptFilePath.toString(), "/") + Constants.SEPARATOR +
			 * folderName; } } else { unzipDirPath = StringUtils
			 * .substringBeforeLast(scriptFilePath.toString(), "/") + Constants.SEPARATOR +
			 * FilenameUtils .removeExtension(scriptFile.getOriginalFilename()); }
			 * logger.info("fetchPreMigrationFiles() folderName:" + folderName +
			 * ", unzipDirPath" + unzipDirPath); File unzipDir = new File(unzipDirPath); if
			 * (!unzipDir.exists()) { FileUtil.createDirectory(unzipDirPath); }
			 * 
			 * boolean unzipStatus = fileUploadService .unzipFile(scriptFilePath.toString(),
			 * unzipDirPath); if (!unzipStatus) { deleteCiqDir(programId, ciqFileName);
			 * resultMap = CommonUtil.buildResponseJson(Constants.CONFIRM,
			 * GlobalInitializerListener.faultCodeMap
			 * .get(FaultCodes.FAILED_TO_UNZIP_SCRIPT_FILE), sessionId, serviceToken);
			 * return resultMap; } } else if (underScoreCharCount >= 3 &&
			 * txtExtensions.contains(fileExtension)) { String fileMoveDirPath =
			 * scriptFilePath.toString() .replace(scriptFile.getOriginalFilename(), "");
			 * String folderName = ""; if (scriptFile.getOriginalFilename().contains("_")) {
			 * folderName = scriptFile.getOriginalFilename().toString().substring(
			 * StringUtils.ordinalIndexOf( scriptFile.getOriginalFilename().toString(), "_",
			 * 2) + 1, StringUtils.ordinalIndexOf(
			 * scriptFile.getOriginalFilename().toString(), "_", 3)); if
			 * (CommonUtil.isValidObject(folderName)) { fileMoveDirPath = StringUtils
			 * .substringBeforeLast(scriptFilePath.toString(), "/") + Constants.SEPARATOR +
			 * folderName; } logger.info("uploadCIQFile() folderName:" + folderName +
			 * ", fileMoveDirPath" + fileMoveDirPath); File fileMoveDir = new
			 * File(fileMoveDirPath); if (!fileMoveDir.exists()) {
			 * FileUtil.createDirectory(fileMoveDirPath); }
			 * FileUtils.copyFileToDirectory(new File(scriptFilePath.toString()),
			 * fileMoveDir); // FileUtil.deleteFileOrFolder(scriptFilePath.toString()); } }
			 * } } String temp = scriptFilePath.toString().replaceAll(fileName, "");
			 * scriptFilePath = new StringBuilder(); scriptFilePath.append(temp); }
			 * if(!ObjectUtils.isEmpty(existingScripts)) { scriptFileNames = scriptFileNames
			 * + String.join(",", existingScripts)+","; }
			 * 
			 * } else { deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_SCRIPT_NOT_FOUND)
			 * ); return resultMap; } } } else { deleteCiqDir(programId, ciqFileName);
			 * resultMap.put("status", Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
			 * return resultMap; }
			 * 
			 * if (!CommonUtil.isValidObject(checkListFile)) { CiqUploadAuditTrailDetEntity
			 * auditTrailDetEntity = fileUploadService
			 * .getLatestCheckListByProgram(programId); if
			 * (CommonUtil.isValidObject(auditTrailDetEntity) &&
			 * CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFileName()) &&
			 * auditTrailDetEntity.getChecklistFileName().length() > 0 &&
			 * CommonUtil.isValidObject(auditTrailDetEntity.getChecklistFilePath())) {
			 * 
			 * String checkListPath =
			 * LoadPropertyFiles.getInstance().getProperty("BASE_PATH") +
			 * auditTrailDetEntity.getChecklistFilePath() +
			 * auditTrailDetEntity.getChecklistFileName(); File latestCheckListFile = new
			 * File(checkListPath); if (latestCheckListFile.exists()) { FileInputStream
			 * input = new FileInputStream(latestCheckListFile); MultipartFile multipartFile
			 * = new MockMultipartFile( checklistFileTempPath.toString() + "" +
			 * auditTrailDetEntity.getChecklistFileName(),
			 * auditTrailDetEntity.getChecklistFileName(), "text/plain",
			 * IOUtils.toByteArray(input)); checkListFile = multipartFile; } }
			 * 
			 * }
			 * 
			 * if (CommonUtil.isValidObject(checkListFile) &&
			 * fileUploadService.uploadMultipartFile(checkListFile,
			 * checklistFileTempPath.toString())) { fileProcessResult =
			 * preMigrationFileProcess(checkListFile, checklistFilePath,
			 * checklistFileTempPath, isAllowDuplicate, "CHECKLIST", programId, ciqFileName,
			 * programName); if (fileProcessResult != null &&
			 * fileProcessResult.containsKey("status") &&
			 * fileProcessResult.get("status").equals(Constants.FAIL)) {
			 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); if (fileProcessResult.containsKey("reason")) {
			 * resultMap.put("reason", fileProcessResult.get("reason")); } return resultMap;
			 * } } else {
			 * 
			 * deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * CHECKLIST_FILE_NOT_FOUND)); return resultMap;
			 * 
			 * }
			 * 
			 * if (fileProcessResult != null && fileProcessResult.containsKey("status") &&
			 * fileProcessResult.get("status").equals(Constants.SUCCESS)) {
			 * CiqUploadAuditTrailDetModel ciqUploadAuditTrailDetModel = new
			 * CiqUploadAuditTrailDetModel(); CustomerDetailsModel customerDetailsModel =
			 * new CustomerDetailsModel(); customerDetailsModel.setId(programId);
			 * List<CustomerDetailsEntity> detailsEntities = customerService
			 * .getCustomerDetailsList(customerDetailsModel); if
			 * (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
			 * ciqUploadAuditTrailDetModel.setProgramDetailsEntity(detailsEntities.get(0));
			 * }
			 * 
			 * String ciqFileSavePath = ciqFileFetchPath; String scriptFileSavePath =
			 * scriptFileFetchPath; ciqFileSavePath = ciqFileSavePath
			 * .replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
			 * scriptFileSavePath = scriptFileSavePath
			 * .replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
			 * ciqUploadAuditTrailDetModel.setCiqFilePath(ciqFileSavePath);
			 * ciqUploadAuditTrailDetModel.setScriptFilePath(scriptFileSavePath);
			 * 
			 * if (CommonUtil.isValidObject(checkListFile)) { String checkListFileSavePath =
			 * checkListFileFetchPath; checkListFileSavePath = checkListFileSavePath
			 * .replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
			 * ciqUploadAuditTrailDetModel.setChecklistFilePath(checkListFileSavePath); }
			 * else { ciqUploadAuditTrailDetModel.setChecklistFilePath(""); }
			 * ciqUploadAuditTrailDetModel.setFileSourceType(Constants.FETCH);
			 * ciqUploadAuditTrailDetModel.setCiqVersion(Constants.CIQ_VERSION_ORIGINAL);
			 * ciqUploadAuditTrailDetModel.setCiqFileName(ciqFileName); if
			 * (CommonUtil.isValidObject(scriptFileNames) && scriptFileNames.length() > 0) {
			 * ciqUploadAuditTrailDetModel .setScriptFileName(scriptFileNames.substring(0,
			 * scriptFileNames.length() - 1)); } if
			 * (CommonUtil.isValidObject(checkListFile)) {
			 * ciqUploadAuditTrailDetModel.setChecklistFileName(checkListFile.
			 * getOriginalFilename()); } else {
			 * ciqUploadAuditTrailDetModel.setChecklistFileName(""); }
			 * ciqUploadAuditTrailDetModel.setRemarks(remarks); CiqUploadAuditTrailDetEntity
			 * ciqUploadAuditTrailDetEntity = ciqUploadAuditTrailDetailsDto
			 * .getCiqUploadAuditTrailDetEntity(ciqUploadAuditTrailDetModel, sessionId);
			 * fileUploadService.createCiqAudit(ciqUploadAuditTrailDetEntity);
			 * commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
			 * Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD, Constants.ACTION_FETCH,
			 * "PreMigration Files Fetched Successfully For: " + ciqFileName, sessionId); if
			 * (CommonUtil.isValidObject(activate) && activate.equalsIgnoreCase("true")) {
			 * commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION,
			 * Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD, Constants.ACTION_FETCH,
			 * "PreMigration Files Fetched are Activated Successfully For: " + ciqFileName,
			 * sessionId); } resultMap.put("status", Constants.SUCCESS);
			 * resultMap.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * CIQ_FILE_FETCHED_SUCCESSFULLY));
			 * 
			 * } else { deleteCiqDir(programId, ciqFileName); resultMap.put("status",
			 * Constants.FAIL); if (fileProcessResult.containsKey("reason")) {
			 * resultMap.put("reason", fileProcessResult.get("reason")); } }
			 * 
			 * // completedMarketsList.add(marketName); } }
			 */
		} catch (Exception e) {
			// deleteCiqDir(programId, ciqFileName);
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_FETCH_CIQ_FILE));
			logger.error(
					"Exception during uploadCIQFile() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api gives the getCiqAuditDetailsList
	 * 
	 * @param pagingDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CIQ_AUDIT_DETAILS, method = RequestMethod.POST)
	public JSONObject getCiqAuditDetailsList(@RequestBody JSONObject ciqDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String searchStatus = null;
		Integer programId = null;
		String programName = null;
		String fileSourceType = null;
		String ovOverallInteraction = null;
		List<CiqUploadAuditTrailDetEntity> ciqUploadAuditTrailDetEntities = null;
		List<CiqUploadAuditTrailDetModel> ciqUploadAuditTrailDetModels = null;
		CiqUploadAuditTrailDetModel ciqAuditTrailModel = null;
		try {
			// fileUploadService.processChecklist(null, null,
			// true,2,"UNY-NE-VZ_CIQ_Ver2.82_01282019.xlsx");
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			searchStatus = ciqDetails.get("searchStatus").toString();
			programName = ciqDetails.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			programId = (Integer) ciqDetails.get("programId");

			Date endDate = new Date();
			String curdate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
			c.add(Calendar.DATE, -pastHistory);
			Date sdate = c.getTime();
			String startDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			Map<String, Integer> paginationData = (Map<String, Integer>) ciqDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");

			if (Constants.LOAD.equals(searchStatus)) {
				fileSourceType = ciqDetails.get("fileSourceType").toString();
				ciqAuditTrailModel = new CiqUploadAuditTrailDetModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				ciqAuditTrailModel.setProgramDetailsEntity(programDetailsEntity);
				ciqAuditTrailModel.setFromDate(startDate);
				ciqAuditTrailModel.setToDate(curdate);
				// ciqAuditTrailModel.setFileSourceType(fileSourceType);

				NetworkConfigModel networkConfigModel = new NetworkConfigModel();
				networkConfigModel.setProgramDetailsEntity(programDetailsEntity);
//				Map<String, Object> objNetworkConfigMap = networkConfigService
//						.getNetworkConfigDetails(networkConfigModel, page, count, null);
				Map<String, Object> objNetworkConfigMap = networkConfigService
						.getNetworkConfigDetailsByPage(networkConfigModel, null);
				if (objNetworkConfigMap != null && objNetworkConfigMap.size() > 0) {
					List<NetworkConfigEntity> networkConfigList = (List<NetworkConfigEntity>) objNetworkConfigMap
							.get("networkConfigList");
					if (CommonUtil.isValidObject(networkConfigList) && networkConfigList.size() > 0) {
						for (NetworkConfigEntity networkConfigEntity : networkConfigList) {
							if (networkConfigEntity.getNeTypeEntity().getId() == 1
									&& Constants.ACTIVE.equals(networkConfigEntity.getStatus())) {
								resultMap.put("ciqServer", networkConfigEntity.getNeName());
								resultMap.put("ciqServerIp", networkConfigEntity.getNeIp());
								resultMap.put("ciqNetworkConfigId", networkConfigEntity.getId());
							}
							if (networkConfigEntity.getNeTypeEntity().getId() == 2
									&& Constants.ACTIVE.equals(networkConfigEntity.getStatus())) {
								resultMap.put("scriptServer", networkConfigEntity.getNeName());
								resultMap.put("scriptServerIp", networkConfigEntity.getNeIp());
								resultMap.put("scriptNetworkConfigId", networkConfigEntity.getId());
							}
						}
					}

					ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(programId,
							Constants.SITE_DETAILS_TEMPLATE);
					if (!ObjectUtils.isEmpty(programTemplateEntity)) {
						resultMap.put("marketList", programTemplateEntity.getValue());
					} else {
						resultMap.put("marketList", null);
					}
				}
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				ciqAuditTrailModel = new Gson().fromJson(
						ciqDetails.toJSONString((Map) ciqDetails.get("searchCriteria")),
						CiqUploadAuditTrailDetModel.class);
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				ciqAuditTrailModel.setProgramDetailsEntity(programDetailsEntity);
			}

			Map<String, Object> auditList = fileUploadService.getCiqAuditDetails(ciqAuditTrailModel, page, count);
			resultMap.put("ciqName", auditList.get("ciqName"));
			resultMap.put("scriptName", auditList.get("scriptName"));
			resultMap.put("checkList", auditList.get("checkList"));
			resultMap.put("ciqVersion", auditList.get("ciqVersion"));
			resultMap.put("type", auditList.get("type"));
			resultMap.put("userName", auditList.get("userName"));
			resultMap.put("searchStartDate", startDate);
			resultMap.put("searchEndDate", curdate);
			resultMap.put("pageCount", auditList.get("paginationcount"));
			ciqUploadAuditTrailDetEntities = (List<CiqUploadAuditTrailDetEntity>) auditList.get("ciqList");
			
			List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
			configDetailModelList=customerService.getOvTemplateDetails(configDetailModelList,"general");
			for(ProgramTemplateModel template : configDetailModelList) {
				if(template.getLabel().equals("OV OVERALL INTERACTION"))
					ovOverallInteraction= template.getValue();
			}

			if (CommonUtil.isValidObject(ciqUploadAuditTrailDetEntities)) {
				ciqUploadAuditTrailDetModels = new ArrayList<CiqUploadAuditTrailDetModel>();
				for (CiqUploadAuditTrailDetEntity ciqUploadDetails : ciqUploadAuditTrailDetEntities) {
					ciqUploadAuditTrailDetModels
							.add(ciqUploadAuditTrailDetailsDto.getciqAuditDetailsModel(ciqUploadDetails));
				}
			}
			resultMap.put("ciqUploadAuditTrailDetModels", ciqUploadAuditTrailDetModels);
			resultMap.put("ovOverallInteraction", ovOverallInteraction);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getauditList()   UploadCIQController:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_RETRIVE_CIQ_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will update Ciq Audit details to DB
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPDATE_CIQ_AUDIT_DETAILS, method = RequestMethod.POST)
	public JSONObject updateCiqAuditDetails(
			@RequestPart(required = false, value = "SCRIPTFILE") List<MultipartFile> scriptFiles,
			@RequestPart(required = false, value = "CHECKLIST") MultipartFile checkListFile,
			@RequestParam("updateCiqDetails") String ciqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		String programName = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		Integer customerId = null;
		String scriptFileNames = "";
		Integer programId = null;
		boolean isAllowDuplicate = true;
		try {
			JSONParser parser = new JSONParser();
			JSONObject updateCiqUpdateDetails = (JSONObject) parser.parse(ciqUpdateDetails);
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			programName = updateCiqUpdateDetails.get("programName").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			String deletedScripts = "70040.zip";
			if (CommonUtil.isValidObject(updateCiqUpdateDetails.get("deletedScripts"))) {
				deletedScripts = updateCiqUpdateDetails.get("deletedScripts").toString();
			}
			customerId = Integer.parseInt(updateCiqUpdateDetails.get("customerId").toString());
			programId = Integer.parseInt(updateCiqUpdateDetails.get("programId").toString());
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (scriptFiles != null && scriptFiles.size() > 0) {
				String rfScriptValidations = rfScriptsValidations(scriptFiles, sessionId,
						updateCiqUpdateDetails.get("programId").toString(),programName);
				if (StringUtils.isNotEmpty(rfScriptValidations) && !"success".equalsIgnoreCase(rfScriptValidations)) {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason", rfScriptValidations);
					mapObject.put("sessionId", sessionId);
					mapObject.put("serviceToken", serviceToken);
					return mapObject;
				}
			}
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create()
					.fromJson(updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("ciqAuditDetail")),
							CiqUploadAuditTrailDetEntity.class);

			StringBuilder uploadPath = new StringBuilder();
			StringBuilder scriptUploadPath = new StringBuilder();
			StringBuilder checklistUploadPath = new StringBuilder();

			StringBuilder scriptUploadTempPath = new StringBuilder();
			StringBuilder checklistUploadTempPath = new StringBuilder();

			String ciqFileName = uploadedCiqAuditEntity.getCiqFileName();

			uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append(Constants.CUSTOMER)
					.append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR);

			scriptUploadPath.append(uploadPath)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));
			checklistUploadPath.append(uploadPath)
					.append(Constants.PRE_MIGRATION_CHECKLIST
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));

			scriptUploadTempPath.append(scriptUploadPath.toString()).append(Constants.TEMP);
			checklistUploadTempPath.append(checklistUploadPath.toString()).append(Constants.TEMP);

			String scriptFilePath = scriptUploadPath.toString();
			String checkListFilePath = checklistUploadPath.toString();

			JSONObject fileProcessResult = new JSONObject();

			if (CommonUtil.isValidObject(deletedScripts) && StringUtils.isNotEmpty(deletedScripts)) {
				String[] oldScriptNames = deletedScripts.split(",");
				scriptUploadPath.setLength(0);
				scriptUploadPath.append(scriptFilePath);
				String oldScriptDirPath = "";
				String folderName = "";
				for (String oldScriptName : oldScriptNames) {
					File oldScriptFile = new File(scriptUploadPath.toString() + oldScriptName);
					logger.info("updateCiqAuditDetails() deleteing if exist ScriptName:" + oldScriptName + ", Path"
							+ scriptUploadPath.toString() + oldScriptName);
					if (oldScriptFile.exists()) {
						oldScriptFile.delete();
						logger.info("updateCiqAuditDetails() deleted ScriptName:" + oldScriptName + ", Path"
								+ scriptUploadPath.toString() + oldScriptName);
						String scriptNamesString = uploadedCiqAuditEntity.getScriptFileName();
						scriptNamesString = scriptNamesString.replaceAll(oldScriptName + ",", "");
						scriptNamesString = scriptNamesString.replaceAll(oldScriptName, "");
						uploadedCiqAuditEntity.setScriptFileName(scriptNamesString);
						logger.info("updateCiqAuditDetails() After deleted ScriptFileNames :"
								+ uploadedCiqAuditEntity.getScriptFileName());
					}

					if (oldScriptName.contains("_") && !oldScriptName.contains("ENDC")) {
						folderName = StringUtils.substringBeforeLast(oldScriptName, "_");
						if (CommonUtil.isValidObject(folderName) && folderName.contains("_")) {
							folderName = StringUtils.substringAfter(folderName, "_");
						} else {
							folderName = StringUtils.substringBefore(folderName, " ");
						}
						if (CommonUtil.isValidObject(folderName)) {
							oldScriptDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
									+ Constants.SEPARATOR + folderName;
						}
					} else {
						oldScriptDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
								+ Constants.SEPARATOR + FilenameUtils.removeExtension(oldScriptName);
					}

					File dir = new File(oldScriptDirPath.toString());
					if (dir.exists()) {
						FileUtils.deleteDirectory(dir);
					}
				}
			}

			if (CommonUtil.isValidObject(scriptFiles) && scriptFiles.size() > 0) {
				String[] oldScriptNames = uploadedCiqAuditEntity.getScriptFileName().split(",");
				for (MultipartFile scriptFile : scriptFiles) {
					boolean scriptFileFound = false;
					for (String oldScriptName : oldScriptNames) {
						if (oldScriptName.equals(scriptFile.getOriginalFilename())) {
							scriptFileFound = true;
							break;
						}
					}
					if (!scriptFileFound) {
						scriptFileNames = scriptFileNames + scriptFile.getOriginalFilename() + ",";
					}
					scriptUploadPath.setLength(0);
					scriptUploadPath.append(scriptFilePath);
					if (CommonUtil.isValidObject(scriptFile)
							&& fileUploadService.uploadMultipartFile(scriptFile, scriptUploadTempPath.toString())) {
						fileProcessResult = preMigrationFileProcess(scriptFile, scriptUploadPath, scriptUploadTempPath,
								isAllowDuplicate, "SCRIPT", programId, ciqFileName, null);
						if (fileProcessResult != null && fileProcessResult.containsKey("status")
								&& fileProcessResult.get("status").equals(Constants.FAIL)) {
							deleteCiqDir(programId, ciqFileName);
							mapObject.put("status", Constants.FAIL);
							if (fileProcessResult.containsKey("reason")) {
								mapObject.put("reason", fileProcessResult.get("reason"));
							}
							return mapObject;
						}
						String fileExtension = FilenameUtils.getExtension(scriptUploadPath.toString());
						List<String> zipExtensions = Arrays.asList("tar.gz", "tgz", "gz", "zip", "7z");
						List<String> txtExtensions = Arrays.asList("txt");
						long underScoreCharCount = scriptFile.getOriginalFilename().chars().filter(num -> num == '_')
								.count();
						if (zipExtensions.contains(fileExtension)) {
							String unzipDirPath = scriptUploadPath.toString().replace(scriptFile.getOriginalFilename(),
									"");
							String folderName = "";
							if (scriptFile.getOriginalFilename().contains("_") && !scriptFile.getOriginalFilename().contains("ENDC")) {
								folderName = StringUtils
										.substringBeforeLast(scriptFile.getOriginalFilename().toString(), "_");
								if (CommonUtil.isValidObject(folderName) && folderName.contains("_")) {
									folderName = StringUtils.substringAfter(folderName, "_");
								} else {
									folderName = StringUtils.substringBefore(folderName, " ");
								}
								if (CommonUtil.isValidObject(folderName)) {
									unzipDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
											+ Constants.SEPARATOR + folderName;
								}
							} else {
								String tempfolder = FilenameUtils.removeExtension(scriptFile.getOriginalFilename());
								if (programName.equals("VZN-5G-MM")) {
									if (StringUtils.isNotEmpty(tempfolder) && tempfolder.length() > 0
											&& tempfolder.charAt(0) == '0') {
										tempfolder = tempfolder.substring(1, tempfolder.length());
									}
								}
								unzipDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
										+ Constants.SEPARATOR + FilenameUtils.removeExtension(tempfolder);
							}
							logger.info(
									"updateCiqAuditDetails() folderName:" + folderName + ", unzipDirPath" + unzipDirPath
											+ ", OriginalFilename: " + scriptFile.getOriginalFilename().toString());
							File unzipDir = new File(unzipDirPath);
							if (unzipDir.exists()) {
								FileUtil.deleteFileOrFolder(unzipDirPath);
							}
							if (!unzipDir.exists()) {
								FileUtil.createDirectory(unzipDirPath);
							}
							boolean unzipStatus = fileUploadService.unzipFile(scriptUploadPath.toString(),
									unzipDirPath);
							if (!unzipStatus) {
								mapObject = CommonUtil
										.buildResponseJson(Constants.CONFIRM,
												GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UNZIP_SCRIPT_FILE),
												sessionId, serviceToken);
								return mapObject;
							}
						} else if (underScoreCharCount >= 3 && txtExtensions.contains(fileExtension)) {
							String fileMoveDirPath = scriptUploadPath.toString()
									.replace(scriptFile.getOriginalFilename(), "");
							String folderName = "";
							if (scriptFile.getOriginalFilename().contains("_")) {
								folderName = scriptFile.getOriginalFilename().toString().substring(
										StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(), "_", 2)
												+ 1,
										StringUtils.ordinalIndexOf(scriptFile.getOriginalFilename().toString(), "_",
												3));
								if (CommonUtil.isValidObject(folderName)) {
									fileMoveDirPath = StringUtils.substringBeforeLast(scriptUploadPath.toString(), "/")
											+ Constants.SEPARATOR + folderName;
								}
								logger.info("uploadCIQFile() folderName:" + folderName + ", fileMoveDirPath"
										+ fileMoveDirPath);
								File fileMoveDir = new File(fileMoveDirPath);
								if (!fileMoveDir.exists()) {
									FileUtil.createDirectory(fileMoveDirPath);
								}
								FileUtils.copyFileToDirectory(new File(scriptUploadPath.toString()), fileMoveDir);
								// FileUtil.deleteFileOrFolder(scriptUploadPath.toString());
							}
						} else {
							/*
							 * deleteCiqDir(programId, ciqFileName); mapObject.put("status",
							 * Constants.FAIL); mapObject.put("reason",
							 * GlobalInitializerListener.faultCodeMap
							 * .get(FaultCodes.FAILED_TO_UPLOAD_SCRIPT_FILE)); return mapObject;
							 */
						}
					}
				}
				if (CommonUtil.isValidObject(scriptFileNames) && scriptFileNames.length() > 0) {
					String scriptFileSavePath = scriptFilePath;
					scriptFileSavePath = scriptFileSavePath
							.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");
					uploadedCiqAuditEntity.setScriptFilePath(scriptFileSavePath);
					if (StringUtils.isNotEmpty(uploadedCiqAuditEntity.getScriptFileName())) {
						String val1 = "";
						if (uploadedCiqAuditEntity.getScriptFileName().contains(FilenameUtils
								.removeExtension(scriptFileNames.substring(0, scriptFileNames.length() - 1))))
							;
						{
							String val = StringUtils.substringAfter(uploadedCiqAuditEntity.getScriptFileName(),
									FilenameUtils.removeExtension(
											scriptFileNames.substring(0, scriptFileNames.length() - 1)));
							val1 = StringUtils.substringBefore(val, ",");
							if (!val1.isEmpty())
								uploadedCiqAuditEntity.setScriptFileName(uploadedCiqAuditEntity.getScriptFileName()
										.replace(FilenameUtils.removeExtension(
												scriptFileNames.substring(0, scriptFileNames.length() - 1)) + val1,
												scriptFileNames.substring(0, scriptFileNames.length() - 1)));
							else
								uploadedCiqAuditEntity.setScriptFileName(uploadedCiqAuditEntity.getScriptFileName()
										+ "," + scriptFileNames.substring(0, scriptFileNames.length() - 1));

						}

					} else {
						uploadedCiqAuditEntity
								.setScriptFileName(scriptFileNames.substring(0, scriptFileNames.length() - 1));
					}
				}
			}

			if (CommonUtil.isValidObject(checkListFile)
					&& fileUploadService.uploadMultipartFile(checkListFile, checklistUploadTempPath.toString())) {
				fileProcessResult = preMigrationFileProcess(checkListFile, checklistUploadPath, checklistUploadTempPath,
						isAllowDuplicate, "CHECKLIST", programId, ciqFileName, null);
				if (fileProcessResult != null && fileProcessResult.containsKey("status")
						&& fileProcessResult.get("status").equals(Constants.FAIL)) {
					deleteCiqDir(programId, ciqFileName);
					mapObject.put("status", Constants.FAIL);
					if (fileProcessResult.containsKey("reason")) {
						mapObject.put("reason", fileProcessResult.get("reason"));
					}
					return mapObject;
				}
				String checkListFileSavePath = checkListFilePath;
				checkListFileSavePath = checkListFileSavePath
						.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"), "");

				// Copying Prev checklist script details to updated checklist if script details
				// are not exist for new checklist
				CheckListScriptDetModel checkListScriptDetModel = new CheckListScriptDetModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				checkListScriptDetModel.setProgramDetailsEntity(programDetailsEntity);

				checkListScriptDetModel.setCheckListFileName(uploadedCiqAuditEntity.getChecklistFileName());
				List<CheckListScriptDetEntity> oldList = checkListScriptService
						.getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);

				checkListScriptDetModel.setCheckListFileName(checkListFile.getOriginalFilename());
				List<CheckListScriptDetEntity> newList = checkListScriptService
						.getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);

				logger.info("updateCiqAuditDetails Prev checklist:" + uploadedCiqAuditEntity.getChecklistFileName()
						+ ", Script Exec details list size: " + oldList.size());
				logger.info("updateCiqAuditDetails Updated checklist:" + checkListFile.getOriginalFilename()
						+ ", Script Exec details list size: " + newList.size());

				if (oldList != null && oldList.size() > 0 && (newList == null || newList.size() <= 0)) {
					logger.info("Copying Prev checklist:" + uploadedCiqAuditEntity.getChecklistFileName()
							+ " script details to updated checklist: " + checkListFile.getOriginalFilename());
					for (CheckListScriptDetEntity checkListScriptDetEntity : oldList) {
						CheckListScriptDetEntity scriptDetEntity = new CheckListScriptDetEntity();
						scriptDetEntity.setCheckListFileName(checkListFile.getOriginalFilename());
						scriptDetEntity.setProgramDetailsEntity(checkListScriptDetEntity.getProgramDetailsEntity());
						scriptDetEntity.setSheetName(checkListScriptDetEntity.getSheetName());
						scriptDetEntity.setStepIndex(checkListScriptDetEntity.getStepIndex());
						scriptDetEntity.setScriptName(checkListScriptDetEntity.getScriptName());
						scriptDetEntity.setScriptExeSeq(checkListScriptDetEntity.getScriptExeSeq());
						scriptDetEntity.setCreatedBy(checkListScriptDetEntity.getCreatedBy());
						scriptDetEntity.setCreationDate(checkListScriptDetEntity.getCreationDate());
						checkListScriptService.saveCheckListBasedScriptExecutionDetails(scriptDetEntity);
					}
				}
				uploadedCiqAuditEntity.setChecklistFilePath(checkListFileSavePath);
				uploadedCiqAuditEntity.setChecklistFileName(checkListFile.getOriginalFilename());
			}

			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			uploadedCiqAuditEntity.setUploadBy(user.getUserName());
			uploadedCiqAuditEntity.setCreationDate(new Date());

			if (uploadedCiqAuditEntity != null) {
				if(StringUtils.isNotEmpty(uploadedCiqAuditEntity.getScriptFileName()))
				{
					List<String> finaScripts=new ArrayList<>();
					String[] listScripts=uploadedCiqAuditEntity.getScriptFileName().split(",");
					for(String scriptName:listScripts)
					{
						if(StringUtils.isNotEmpty(scriptName))
						{
							finaScripts.add(scriptName);
						}
						if(finaScripts.size()>0)
						{
						uploadedCiqAuditEntity.setScriptFileName(String.join(",",finaScripts));
						}else {
							uploadedCiqAuditEntity.setScriptFileName(null);
						}
					}
				}
				if (fileUploadService.updateCiqAuditDetails(uploadedCiqAuditEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.CIQ_AUDIT_DETAILS_UPDATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
							Constants.ACTION_UPDATE, "CIQ Details Updated Successfully For: " + ciqFileName, sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_AUDIT_DETAILS));
				}
			}
		} catch (Exception e) {
			logger.info("Exception in updateCiqUpdateDetails in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_AUDIT_DETAILS));
			mapObject.put("status", Constants.FAIL);
		}
		return mapObject;
	}

	private boolean deleteCiqDir(Integer programId, String ciqFileName) {
		boolean status = false;
		try {
			if (CommonUtil.isValidObject(programId) && CommonUtil.isValidObject(ciqFileName)) {
				String folderName = StringUtils.substringBeforeLast(ciqFileName.toString(), ".").replaceAll(" ", "_");

				StringBuilder fileInputPath = new StringBuilder();
				fileInputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
						.append(Constants.SEPARATOR).append(Constants.PRE_MIGRATION_INPUT);
				fileInputPath.append(folderName);

				logger.info("deleteCiq fileInputPath: " + fileInputPath.toString());
				File fileInputDir = new File(fileInputPath.toString());
				if (fileInputDir.exists()) {
					FileUtils.deleteDirectory(fileInputDir);
				}

				/*
				 * StringBuilder fileOutputPath = new StringBuilder();
				 * fileOutputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"
				 * )) .append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
				 * .append(Constants.SEPARATOR).append(Constants.PRE_MIGRATION_OUTPUT);
				 * fileOutputPath.append(folderName); logger.info("deleteCiq fileOutputPath: " +
				 * fileInputPath.toString()); File fileOutputDir = new
				 * File(fileOutputPath.toString()); if (fileOutputDir.exists()) {
				 * FileUtils.deleteDirectory(fileOutputDir); }
				 */
			}
			status = true;
		} catch (Exception e) {
			logger.info("Exception in deleteCiqDir in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will delete the Ciq
	 * 
	 * @param deleteCiqdetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_CIQ_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteCiq(@RequestBody JSONObject deleteCiqdetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String programName = null;
		Integer programId = null;
		JSONObject expiryDetails = null;
		Integer fileId = null;
		String ciqFileName = null;
		String scriptFileName = null;
		String checklistFileName = "";
		try {
			sessionId = deleteCiqdetails.get("sessionId").toString();
			serviceToken = deleteCiqdetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			programId = (Integer) deleteCiqdetails.get("programId");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			ciqFileName = deleteCiqdetails.get("ciqFileName").toString();

			if (deleteCiqdetails.get("scriptFileName") != null) {
				scriptFileName = deleteCiqdetails.get("scriptFileName").toString();
			}
			checklistFileName = deleteCiqdetails.get("checklistFileName").toString();

			fileId = Integer.parseInt(deleteCiqdetails.get("id").toString());

			CiqUploadAuditTrailDetModel ciqAuditdetails = new CiqUploadAuditTrailDetModel();
			ciqAuditdetails.setId(fileId);
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(programId);
			ciqAuditdetails.setProgramDetailsEntity(programDetailsEntity);
			ciqAuditdetails.setCiqFileName(ciqFileName);
			ciqAuditdetails.setChecklistFileName(checklistFileName);

			if (CommonUtil.isValidObject(ciqAuditdetails)
					&& CommonUtil.isValidObject(ciqAuditdetails.getChecklistFileName())
					&& ciqAuditdetails.getChecklistFileName().length() > 0) {
				fileUploadService.deleteCheckList(ciqAuditdetails);
			}
			boolean status = fileUploadService.deleteCiq(ciqAuditdetails);
			if (status) {
				status = deleteCiqDir(programId, ciqFileName);
				if (!status) {
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CIQ_DETAILS));
					resultMap.put("status", Constants.FAIL);
					return resultMap;
				}
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_CIQ_DETAILS_SUCCESSFULLY));
			commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
					Constants.ACTION_DELETE, "CIQ Details Deleted Successfully of file" + ciqFileName, sessionId);
		} catch (Exception e) {
			logger.info("Exception in deleteCiq in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CIQ_DETAILS));
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	/**
	 * This api gives the getCiqList FileNames
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CIQ_DETAILS, method = RequestMethod.POST)
	public JSONObject getCiqList(@RequestBody JSONObject ciqDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String customerId = null;
		List<CiqUploadAuditTrailDetEntity> ciqList = null;
		String fromDate = null;
		String toDate = null;
		String searchStatus = null;
		String programId = null;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			searchStatus = ciqDetails.get("searchStatus").toString();
			programId = ciqDetails.get("programId").toString();

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			customerId = ciqDetails.get("customerId").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (Constants.LOAD.equals(searchStatus)) {
				Date endDate = new Date();
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				ciqList = fileUploadService.getCiqList(user, programId, fromDate, toDate);
			} else if (Constants.SEARCH.equals(searchStatus)) {

				fromDate = ciqDetails.get("fromDate").toString();
				toDate = ciqDetails.get("toDate").toString();

				ciqList = fileUploadService.getCiqList(user, programId, fromDate, toDate);
			}

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("fromDate", fromDate);
			resultMap.put("toDate", toDate);
			resultMap.put("getCiqList", ciqList);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in getCiqList() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_DETAILS));
		}

		return resultMap;
	}

	/**
	 * This api gives the retriveCiqDetails
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.RETRIVE_CIQ_DETAILS, method = RequestMethod.POST)
	public JSONObject retriveCiqDetails(@RequestBody JSONObject ciqDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			String fileName = ciqDetails.get("fileName").toString();

			Map<String, Integer> paginationData = (Map<String, Integer>) ciqDetails.get("pagination");

			int page = paginationData.get("page");
			int count = paginationData.get("count");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			// reading user list
			Map<String, Object> list = fileUploadService.retriveCiqDetails(fileName, page, count);
			List ciqDetailsList = new ArrayList();
			ciqDetailsList.add(list.get("ciqDetailsList"));
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("pageCount", list.get("pageCount"));
			resultMap.put("ciqUploadDetails", ciqDetailsList);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in getFddTddDetails() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_RETRIVE_CIQ_DETAILS));
		}

		return resultMap;
	}

	/**
	 * This method will updateCiqFileDetaiils to DB
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPDATE_CIQ_DETAILS, method = RequestMethod.POST)
	public JSONObject updateCiqFileDetaiils(@RequestBody JSONObject updateCiqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create()
					.fromJson(updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("ciqAuditDetail")),
							CiqUploadAuditTrailDetEntity.class);

			CIQDetailsModel updateCiq = new Gson().fromJson(
					updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("ciqDetails")),
					CIQDetailsModel.class);

			if (updateCiq != null) {

				if (fileUploadService.updateCiqFileDetaiils(updateCiq, uploadedCiqAuditEntity, user)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.UPDATED_CIQ_DETAILS_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_UPDATE,
							"CIQ File Details Updated Successfully for " + updateCiq.getFileName(), sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_DETAILS));
				}
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in updateCiqUpdateDetails in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_DETAILS));

		}
		return mapObject;
	}

	/**
	 * This method will create CiqFileDetaiils to DB
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.CREATE_CIQ_DETAILS, method = RequestMethod.POST)
	public JSONObject createCiqFileDetaiils(@RequestBody JSONObject updateCiqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			CIQDetailsModel uploadedCiqEntity = new Gson().fromJson(
					updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("ciqDetails")),
					CIQDetailsModel.class);

			if (uploadedCiqEntity != null) {

				if (fileUploadService.createCiqFileDetaiils(uploadedCiqEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.CREATED_CIQ_DETAILS_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_SAVE, "CIQ Details Are Created Successfully", sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_CREATE_CIQ_DETAILS));
				}
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in updateCiqUpdateDetails in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_CREATE_CIQ_DETAILS));

		}
		return mapObject;
	}

	/**
	 * This api delete LsmDetails
	 * 
	 * @param deleteCiqRowDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_CIQ_ROW_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteCiqRowDetails(@RequestBody JSONObject deleteCiqRowDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();

		String programId = null;
		Integer id = null;
		try {
			sessionId = deleteCiqRowDetails.get("sessionId").toString();
			serviceToken = deleteCiqRowDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			programId = deleteCiqRowDetails.get("programId").toString();
			id = Integer.valueOf(deleteCiqRowDetails.get("id").toString());
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create()
					.fromJson(deleteCiqRowDetails.toJSONString((Map) deleteCiqRowDetails.get("ciqAuditDetail")),
							CiqUploadAuditTrailDetEntity.class);

			/*
			 * CIQDetailsModel deleteCiq = new Gson().fromJson(
			 * deleteCiqRowDetails.toJSONString((Map)
			 * deleteCiqRowDetails.get("ciqDetails")), CIQDetailsModel.class);
			 */
			if (uploadedCiqAuditEntity != null && id != null) {
				if (fileUploadService.deleteCiqRowDetails(id, uploadedCiqAuditEntity, user)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.DELETED_CIQ_ROW_DETAILS_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_DELETE,
							"CIQ Details Deleted Successfully of file " + uploadedCiqAuditEntity.getCiqFileName(),
							sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CIQ_ROW_DETAILS));
				}
			}
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.error(
					"Exception  deleteCiqRowDetails()   in UploadCIQController:" + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CIQ_ROW_DETAILS));
		}
		return mapObject;
	}

	/**
	 * This api gives the getEnodeBDetails
	 * 
	 * @param ciqDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_ENB_DETAILS, method = RequestMethod.POST)
	public JSONObject getEnbDetails(@RequestBody JSONObject ciqDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String id = null;
		String fileName = null;
		String programId = null;
		String programName = null;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = ciqDetails.get("ciqName").toString();
			programId = ciqDetails.get("programId").toString();
			programName = ciqDetails.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, fileName);

			List<Map<String, String>> objList = null;
			if (programName.contains("5G") || programName.contains("4G-FSU")) {
				objList = fileUploadService.getEnbDetails5G(programId, fileName, dbcollectionFileName);
			} else {
				objList = fileUploadService.getEnbDetails(id, fileName, dbcollectionFileName);
			}

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("eNBList", objList);
		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_ENB_DETAILS));
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	/**
	 * This api gives the getEnbInfo
	 * 
	 * @param enbDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getEnbDetails5GMM", method = RequestMethod.POST)
	public JSONObject getEnbDetails5GMM(@RequestBody JSONObject ciqDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String id = null;
		String fileName = null;
		String programId = null;
		String programName = null;
		try {
			sessionId = ciqDetails.get("sessionId").toString();
			serviceToken = ciqDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = ciqDetails.get("ciqName").toString();
			programId = ciqDetails.get("programId").toString();
			programName = ciqDetails.get("programName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, fileName);

			Map<String, List<Map<String, String>>> objList = null;
			objList = fileUploadService.getEnbDetails5GMM(programId, fileName, dbcollectionFileName);

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("siteList", objList);
		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_ENB_DETAILS));
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	/**
	 * This api gives the getEnbInfo
	 * 
	 * @param enbDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_ENB_INFO, method = RequestMethod.POST)
	public JSONObject getEnbInfo(@RequestBody JSONObject enbDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		List<String> enbMenuList = new ArrayList<String>();
		String enbName = null;
		String enbId = null;
		String fileName = null;
		try {
			sessionId = enbDetails.get("sessionId").toString();
			serviceToken = enbDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			programId = (Integer) enbDetails.get("programId");
			enbName = enbDetails.get("enbName").toString();
			// fileId = Integer.parseInt(enbDetails.get("fileId").toString());
			enbId = enbDetails.get("enbId").toString();
			fileName = enbDetails.get("fileName").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(programId);
			programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			programTemplateModel.setLabel(Constants.ENB_MENU_TEMPLATE);
			List<ProgramTemplateEntity> entities = customerService.getProgTemplateDetails(programTemplateModel);
			Map<String, Set<String>> sheetSet = fileUploadService.getCiqSheetNamesBasedOnEnb(fileName,
					CommonUtil.createMongoDbFileName(String.valueOf(programId), fileName), enbName, enbId);
			if (CommonUtil.isValidObject(entities) && entities.size() > 0
					&& StringUtils.isNotEmpty(entities.get(0).getValue())) {
				ObjectMapper mapper = new ObjectMapper();
				JsonObject objData = CommonUtil.parseRequestDataToJson(entities.get(0).getValue());

				List<EnbTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("ciqMenu").toString(),
						new TypeReference<List<EnbTemplateModel>>() {
						});

				if (myCIQTemplateModel != null && myCIQTemplateModel.size() > 0 && sheetSet != null) {
					enbMenuList = myCIQTemplateModel.stream().filter(x -> sheetSet.containsKey(x.getSheetAliasName()))
							.map(x -> x.getMenuName()).collect(Collectors.toList());
				}

				/*
				 * JSONObject obj = CommonUtil.parseDataToJSON(entities.get(0).getValue());
				 * JSONArray menuarray = (JSONArray) obj.get("ciqMenu"); for (int i = 0; i <
				 * menuarray.size(); i++) { obj = (JSONObject) menuarray.get(i);
				 * enbMenuList.add(obj.get("menuName").toString()); }
				 */
			}

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("enbMenuList", enbMenuList);
		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_ENB_INFO));
			resultMap.put("status", Constants.FAIL);

		}
		return resultMap;
	}

	/**
	 * This api gives the getEnbTableInfo
	 * 
	 * @param enbDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_ENB_TABLE_INFO, method = RequestMethod.POST)
	public JSONObject getEnbTableInfo(@RequestBody JSONObject enbDetails) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String fileName = null;
		String enbName = null;
		String menuName = null;
		String programId = null;
		// int fileId;
		String enbId = null;
		List<String> enbMenuList = new ArrayList<String>();
		try {
			sessionId = enbDetails.get("sessionId").toString();
			serviceToken = enbDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = enbDetails.get("fileName").toString();
			enbName = enbDetails.get("enbName").toString();
			// fileId = Integer.parseInt(enbDetails.get("fileId").toString());
			enbId = enbDetails.get("enbId").toString();
			menuName = enbDetails.get("menuName").toString();
			programId = enbDetails.get("programId").toString();
			Map<String, Integer> paginationData = (Map<String, Integer>) enbDetails.get("pagination");

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			String dbcollectionFileName = CommonUtil.createMongoDbFileName(programId, fileName);
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			Map<String, Object> list = fileUploadService.getEnbTableDetails(programId, fileName, enbId, enbName,
					menuName, page, count, dbcollectionFileName);

			resultMap.put("status", Constants.SUCCESS);
			if (list != null && list.containsKey("eNodeMapDetails") && list.containsKey("pageCount")) {
				resultMap.put("eNodeMapDetails", list.get("eNodeMapDetails"));
				resultMap.put("pageCount", list.get("pageCount"));
			} else {
				resultMap.put("eNodeMapDetails", null);
				resultMap.put("pageCount", 0);
			}
		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_ENB_TABLE_INFO));
			resultMap.put("status", Constants.FAIL);
		}
		return resultMap;
	}

	/**
	 * This api gives the getEnodeBDetailsByFilename
	 * 
	 * @param ciqList
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_ENB_DETAILS_FILENAME, method = RequestMethod.POST)
	public JSONObject getEnbDetailsFilename(@RequestBody JSONObject ciqList) {

		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String fileName = null;
		String enbId = null;
		try {
			sessionId = ciqList.get("sessionId").toString();
			serviceToken = ciqList.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			fileName = ciqList.get("fileName").toString();
			enbId = ciqList.get("enbId").toString();
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			ArrayList<String> objList = fileUploadService.getEnbDetailsFilename(fileName, enbId);

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("ciqDetailsList", objList);

		} catch (Exception e) {
			logger.info("Exception in getEnbDetails in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_ENB_DETAILS_FILENAME));

		}

		return resultMap;
	}

	/**
	 * This method will updateCiqFileDetaiils to DB
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.UPDATE_CIQ_ENB_DETAILS, method = RequestMethod.POST)
	public JSONObject updateCiqFileDetaiilsEnbs(@RequestBody JSONObject updateCiqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		String menuName = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			menuName = updateCiqUpdateDetails.get("menuName").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create()
					.fromJson(updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("ciqAuditDetail")),
							CiqUploadAuditTrailDetEntity.class);

			/*
			 * CIQDetailsModel updateCiq = new Gson().fromJson(
			 * updateCiqUpdateDetails.toJSONString((Map)
			 * updateCiqUpdateDetails.get("ciqDetails")), CIQDetailsModel.class);
			 */

			CIQDetailsModel updateCiq = new Gson().fromJson(
					updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("eNodeBDetails")),
					CIQDetailsModel.class);

			if (uploadedCiqAuditEntity != null && StringUtils.isNotEmpty(menuName)) {

				if (fileUploadService.updateCiqFileDetaiilsEndBased(updateCiq, menuName, uploadedCiqAuditEntity,
						user)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.UPDATED_CIQ_ENB_DETAILS_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_RAN_CONFIG,
							Constants.ACTION_UPDATE,
							"CIQ Enb Details Updated Successfully for " + updateCiq.geteNBName(), sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_ENB_DETAILS));
				}
			} else {
				mapObject.put("status", Constants.FAIL);
				mapObject.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_ENB_DETAILS));
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in updateCiqUpdateDetails in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_CIQ_ENB_DETAILS));

		}
		return mapObject;
	}

	/**
	 * This method will give the getCiqSheeTname
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CIQ_SHEETNAME, method = RequestMethod.POST)
	public JSONObject getCiqSheeTname(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String fileName = null;
		Map<String, Set<String>> sheetSet = null;
		JSONObject expiryDetails = null;
		String programId = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			customerId = (Integer) auditListDetails.get("customerId");
			fileName = auditListDetails.get("fileName").toString();
			programId = auditListDetails.get("programId").toString();
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			sheetSet = fileUploadService.getCiqSheetNames(fileName,
					CommonUtil.createMongoDbFileName(programId, fileName));

			resultMap.put("SheetDetails", sheetSet);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getauditList()   UploadCIQController:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give getCiqSheetDisply
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CIQ_SHEET_DISPLAY, method = RequestMethod.POST)
	public JSONObject getCiqSheetDisply(@RequestBody JSONObject getCiqDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Map<String, Object> objMap = null;
		Integer customerId = null;
		String fileName = null;
		String sheetName = null;
		String subSheetName = null;
		JSONObject expiryDetails = null;
		String programId = null;
		String searchStatus = null;
		Map<String, String> ciqSearchMap = null;
		try {
			sessionId = getCiqDetails.get("sessionId").toString();
			serviceToken = getCiqDetails.get("serviceToken").toString();
			if (CommonUtil.isValidObject(getCiqDetails.get("searchStatus"))) {
				searchStatus = getCiqDetails.get("searchStatus").toString();
			}
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			customerId = (Integer) getCiqDetails.get("customerId");
			fileName = getCiqDetails.get("fileName").toString();
			sheetName = getCiqDetails.get("sheetName").toString();
			programId = getCiqDetails.get("programId").toString();
			subSheetName = getCiqDetails.get("subSheetName").toString();
			if (CommonUtil.isValidObject(searchStatus) && Constants.SEARCH.equals(searchStatus)
					&& CommonUtil.isValidObject(getCiqDetails.get("searchCriteria"))) {
				ciqSearchMap = (Map<String, String>) getCiqDetails.get("searchCriteria");
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) getCiqDetails.get("pagination");

			int page = paginationData.get("page");
			int count = paginationData.get("count");
			objMap = fileUploadService.getCiqSheetDisply(CommonUtil.createMongoDbFileName(programId, fileName),
					sheetName, subSheetName, ciqSearchMap, page, count);

			resultMap.put("SheetDisplayDetails", objMap);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			if (objMap != null && objMap.containsKey("count")) {
				resultMap.put("pageCount", objMap.get("count"));
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error(
					"Exception in getCiqSheetDisply()   UploadCIQController:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give getCheckList
	 * 
	 * @param checkListDetails
	 * @return String
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CHECKLIST_DATA, method = RequestMethod.POST)
	public JSONObject getCheckList(@RequestBody JSONObject checkListDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String customerId = null;
		List<CiqUploadAuditTrailDetEntity> ciqList = null;
		String fromDate = null;
		String toDate = null;
		String searchStatus = null;
		String programId = null;
		try {
			sessionId = checkListDetails.get("sessionId").toString();
			serviceToken = checkListDetails.get("serviceToken").toString();
			searchStatus = checkListDetails.get("searchStatus").toString();
			programId = checkListDetails.get("programId").toString();

			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			customerId = checkListDetails.get("customerId").toString();
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (Constants.LOAD.equals(searchStatus)) {
				Date endDate = new Date();
				toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				ciqList = fileUploadService.getCiqList(user, programId, fromDate, toDate);
			} else if (Constants.SEARCH.equals(searchStatus)) {

				fromDate = checkListDetails.get("fromDate").toString();
				toDate = checkListDetails.get("toDate").toString();

				ciqList = fileUploadService.getCiqList(user, programId, fromDate, toDate);
			}

			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("fromDate", fromDate);
			resultMap.put("toDate", toDate);
			resultMap.put("getChecklistList", ciqList);

		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.info("Exception in getCheckList() in UploadCIQController" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_DETAILS));
		}

		return resultMap;
	}

	/**
	 * This method will give the getCheckListSheetNames
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_CHECKLIST_SHEET_DETAILS, method = RequestMethod.POST)
	public JSONObject getCheckListSheetNames(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String ciqFileName = null;
		String checkListFileName = null;
		Set<String> sheetSet = null;
		JSONObject expiryDetails = null;
		String programId = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			customerId = (Integer) auditListDetails.get("customerId");
			ciqFileName = auditListDetails.get("ciqFileName").toString();
			checkListFileName = auditListDetails.get("checkListFileName").toString();
			programId = auditListDetails.get("programId").toString();
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			sheetSet = fileUploadService.getCheckListSheetNames(checkListFileName,
					CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName));

			resultMap.put("SheetDetails", sheetSet);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getCheckListSheetNames()   UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give the getDeatilsByChecklist
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.GET_DETAILS_BY_CHECK_LIST, method = RequestMethod.POST)
	public JSONObject getDeatilsByChecklist(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Map<String, Object> objMap = null;
		Integer customerId = null;
		String sheetName = null;
		JSONObject expiryDetails = null;
		String programId = null;
		String ciqFileName = null;
		String checkListFileName = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			customerId = (Integer) auditListDetails.get("customerId");
			checkListFileName = auditListDetails.get("checkListFileName").toString();
			ciqFileName = auditListDetails.get("ciqFileName").toString();
			sheetName = auditListDetails.get("sheetName").toString();
			programId = auditListDetails.get("programId").toString();

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");

			int page = paginationData.get("page");
			int count = paginationData.get("count");
			objMap = fileUploadService.getCheckListSheetDisply(
					CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName), sheetName,
					page, count);

			resultMap.put("SheetDisplayDetails", objMap);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			if (objMap != null && objMap.containsKey("count")) {
				resultMap.put("pageCount", objMap.get("count"));
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getDeatilsByChecklist()   UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give the getCheckListSheetNames
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getCheckListAllSheetNames", method = RequestMethod.POST)
	public JSONObject getCheckListAllSheetNames(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String ciqFileName = null;
		String checkListFileName = null;
		Set<String> sheetSet = null;
		JSONObject expiryDetails = null;
		String programId = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();
			customerId = (Integer) auditListDetails.get("customerId");
			ciqFileName = auditListDetails.get("ciqFileName").toString();
			checkListFileName = auditListDetails.get("checklistFileName").toString();
			programId = auditListDetails.get("programId").toString();
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			sheetSet = fileUploadService.getCheckListAllSheetNames(checkListFileName,
					CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName));

			resultMap.put("SheetDetails", sheetSet);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getCheckListSheetNames()   UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give the getDeatilsByChecklist
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/insertChecklistDetails", method = RequestMethod.POST)
	public JSONObject insertChecklistDetails(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Map<String, Object> objMap = null;
		Integer customerId = null;
		String sheetName = null;
		JSONObject expiryDetails = null;
		String programId = null;
		String ciqFileName = null;
		String checkListFileName = null;
		String enodeName = null;
		String remarks = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			customerId = (Integer) auditListDetails.get("customerId");
			checkListFileName = auditListDetails.get("checkListFileName").toString();
			ciqFileName = auditListDetails.get("ciqFileName").toString();
			sheetName = auditListDetails.get("sheetName").toString();
			programId = auditListDetails.get("programId").toString();
			enodeName = auditListDetails.get("enodeName").toString();
			remarks = auditListDetails.get("remarks").toString();

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			// Map<String, Integer> paginationData = (Map<String, Integer>)
			// auditListDetails.get("pagination");

			// int page = paginationData.get("page");
			// int count = paginationData.get("count");
			objMap = fileUploadService.insertChecklistDetails(
					CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName), sheetName,
					enodeName, remarks, 0);

			resultMap.put("SheetDisplayDetails", objMap);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			if (objMap != null && objMap.containsKey("count")) {
				resultMap.put("pageCount", objMap.get("count"));
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getDeatilsByChecklist()   UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will give the getDeatilsByChecklist
	 * 
	 * @param auditListDetails
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllDetailsByChecklist", method = RequestMethod.POST)
	public JSONObject getAllDetailsByChecklist(@RequestBody JSONObject auditListDetails) {

		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Map<String, Object> objMap = null;
		Integer customerId = null;
		String sheetName = null;
		JSONObject expiryDetails = null;
		String programId = null;
		String ciqFileName = null;
		String checkListFileName = null;
		String enodeName = null;
		Integer runTestId = null;
		try {
			sessionId = auditListDetails.get("sessionId").toString();
			serviceToken = auditListDetails.get("serviceToken").toString();

			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			customerId = (Integer) auditListDetails.get("customerId");
			checkListFileName = auditListDetails.get("checklistFileName").toString();
			ciqFileName = auditListDetails.get("ciqFileName").toString();
			sheetName = auditListDetails.get("sheetName").toString();
			programId = auditListDetails.get("programId").toString();
			enodeName = auditListDetails.get("enodeName").toString();
			runTestId = Integer.parseInt(auditListDetails.get("runTestId").toString());

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) auditListDetails.get("pagination");

			int page = paginationData.get("page");
			int count = paginationData.get("count");
			objMap = fileUploadService.getAllCheckListSheetDisply("checkListModel", sheetName, page, count, enodeName,
					runTestId);

			resultMap.put("SheetDisplayDetails", objMap);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			if (objMap != null && objMap.containsKey("count")) {
				resultMap.put("pageCount", objMap.get("count"));
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			logger.error("Exception in getDeatilsByChecklist()   UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_CIQ_SHEET_DETAILS));

		}
		return resultMap;
	}

	/**
	 * This method will create saveCheckListFileDetaiils
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateCheckListFileDetails", method = RequestMethod.POST)
	public JSONObject updateCheckListFileDetails(@RequestBody JSONObject updateCiqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		String checkListFileName = null;
		String ciqFileName = null;
		String programId = null;
		String enodeName = null;
		try {
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			programId = updateCiqUpdateDetails.get("programId").toString();
			checkListFileName = updateCiqUpdateDetails.get("checklistFileName").toString();
			ciqFileName = updateCiqUpdateDetails.get("ciqFileName").toString();
			enodeName = updateCiqUpdateDetails.get("enodeName").toString();
			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}

			// List<CheckListModel> uploadedCiqEntityList = (List<CheckListModel>)
			// updateCiqUpdateDetails.get("checklistTableData");

			ObjectMapper objMapper = new ObjectMapper();
			String data = CommonUtil.convertObjectToJson(updateCiqUpdateDetails);
			JsonObject objData = CommonUtil.parseRequestDataToJson(data);

			List<CheckListModel> uploadedCiqEntityList = objMapper
					.readValue(objData.get("checklistTableData").toString(), new TypeReference<List<CheckListModel>>() {
					});

			// CheckListModel uploadedCiqEntity = new Gson().fromJson(
			// updateCiqUpdateDetails.toJSONString((Map)
			// updateCiqUpdateDetails.get("checklistTableData")),
			// CheckListModel.class);

			if (uploadedCiqEntityList != null) {
				for (CheckListModel uploadedCiqEntity : uploadedCiqEntityList) {
					if (fileUploadService.updateCheckListFileDetails(uploadedCiqEntity, "checkListModel", enodeName)) {
						mapObject.put("status", Constants.SUCCESS);
						mapObject.put("reason", GlobalInitializerListener.faultCodeMap
								.get(FaultCodes.CREATED_CHECKLIST_DETAILS_SUCCESSFULLY));
					} else {
						mapObject.put("status", Constants.FAIL);
						mapObject.put("reason", GlobalInitializerListener.faultCodeMap
								.get(FaultCodes.FAILED_TO_CREATE_CHECKLIST_DETAILS));
					}
				}

			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in saveCheckListFileDetaiils in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_CREATE_CHECKLIST_DETAILS));

		}
		return mapObject;
	}

	/**
	 * This method will create saveCheckListFileDetaiils
	 * 
	 * @param updateCiqUpdateDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.SAVE_CHECK_LIST_FILE_DETAILS, method = RequestMethod.POST)
	public JSONObject saveCheckListFileDetaiils(@RequestBody JSONObject updateCiqUpdateDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		String checkListFileName = null;
		String ciqFileName = null;
		String programId = null;
		try {
			sessionId = updateCiqUpdateDetails.get("sessionId").toString();
			serviceToken = updateCiqUpdateDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			programId = updateCiqUpdateDetails.get("programId").toString();
			checkListFileName = updateCiqUpdateDetails.get("checkListFileName").toString();
			ciqFileName = updateCiqUpdateDetails.get("ciqFileName").toString();
			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			CheckListDetailsModel uploadedCiqEntity = new Gson().fromJson(
					updateCiqUpdateDetails.toJSONString((Map) updateCiqUpdateDetails.get("checklistDetails")),
					CheckListDetailsModel.class);

			if (uploadedCiqEntity != null) {

				if (fileUploadService.saveCheckListFileDetaiils(uploadedCiqEntity,
						CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName))) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.CREATED_CHECKLIST_DETAILS_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CHECK_LIST,
							Constants.ACTION_SAVE, "CheckList Created Successfully CHECKLIST: " + checkListFileName,
							sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_CREATE_CHECKLIST_DETAILS));
				}
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.info("Exception in saveCheckListFileDetaiils in UploadCIQController "
					+ ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_CREATE_CHECKLIST_DETAILS));

		}
		return mapObject;
	}

	/**
	 * This api delete deleteCheckListRowDetails
	 * 
	 * @param deleteCiqRowDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_CHECKLIST_ROW_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteCheckListRowDetails(@RequestBody JSONObject deleteCiqRowDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();

		String programId = null;
		Integer id = null;
		String checkListFileName = null;
		String ciqFileName = null;
		try {
			sessionId = deleteCiqRowDetails.get("sessionId").toString();
			serviceToken = deleteCiqRowDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			programId = deleteCiqRowDetails.get("programId").toString();
			id = Integer.valueOf(deleteCiqRowDetails.get("id").toString());
			checkListFileName = deleteCiqRowDetails.get("checkListFileName").toString();
			ciqFileName = deleteCiqRowDetails.get("ciqFileName").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(ciqFileName)) {
				ciqFileName = ciqFileName.replaceAll(Constants.CIQ_FILE_MODIFIED, Constants.XLXSEXTENTION);
			}
			if (id != null && StringUtils.isNotEmpty(ciqFileName) && StringUtils.isNotEmpty(checkListFileName)) {
				if (fileUploadService.deleteCheckListRowDetails(id,
						CommonUtil.createMongoDbFileNameCheckList(programId, checkListFileName, ciqFileName))) {
					mapObject.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CHECK_LIST,
							Constants.ACTION_DELETE,
							"CheckList Details Deleted Successfully CHECKLIST: " + checkListFileName, sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.FAILED_TO_DELETE_CHECKLIST_ROW_DETAILS));
				}
			}
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			logger.error(
					"Exception  deleteCiqRowDetails()   in UploadCIQController:" + ExceptionUtils.getFullStackTrace(e));
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_CHECKLIST_ROW_DETAILS));
		}
		return mapObject;
	}

	/**
	 * This api delete deleteValidationDetails
	 * 
	 * @param deleteCiqRowDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/validationCiqDetails", method = RequestMethod.POST)
	public JSONObject validationCiqDetails(@RequestBody JSONObject deleteCiqRowDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();

		String programId = null;
		String fileName = null;
		try {
			sessionId = deleteCiqRowDetails.get("sessionId").toString();
			serviceToken = deleteCiqRowDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			fileName = deleteCiqRowDetails.get("fileName").toString();
			programId = deleteCiqRowDetails.get("programId").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(programId)) {
				String dbCollectionName = CommonUtil.createMongoDbFileName(programId, fileName);

				Map<String, Object> objMap = fileUploadService.validationCiqDetails(dbCollectionName,
						Integer.valueOf(programId));
				if (objMap != null && objMap.size() > 0 && objMap.containsKey("validationDetails")) {
					List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) objMap.get("validationDetails");
					if (objErrorMap.size() > 0) {
						mapObject.put("status", Constants.FAIL);
						mapObject.put("errorDetails", objErrorMap);
						mapObject.put("reason", "Validations Fail");
					} else {
						mapObject.put("status", Constants.SUCCESS);
					}
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("errorDetails", new LinkedHashMap<>());
					mapObject.put("reason", "Validations Fail");
				}
				commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
						Constants.ACTION_VALIDATE, "CIQ Details Validated Successfully For: " + fileName, sessionId);
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("errorDetails", new LinkedHashMap<>());
			mapObject.put("reason", "Validations Fail");
			logger.error("Exception  validationCiqDetails()   in UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			/*
			 * mapObject.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FAILED_TO_DELETE_VALIDATION_DETAILS));
			 */
		}
		return mapObject;
	}

	/**
	 * This api delete deleteValidationDetails
	 * 
	 * @param deleteCiqRowDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/validationEnbDetails", method = RequestMethod.POST)
	public JSONObject validationEnbDetails(@RequestBody JSONObject validatCiqRowDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();

		String programId = null;
		String fileName = null;
		String enbName = null;
		String enbId = null;
		try {
			sessionId = validatCiqRowDetails.get("sessionId").toString();
			serviceToken = validatCiqRowDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			fileName = validatCiqRowDetails.get("fileName").toString();
			programId = validatCiqRowDetails.get("programId").toString();
			enbName = validatCiqRowDetails.get("enbName").toString();
			enbId = validatCiqRowDetails.get("enbId").toString();
			List<Map<String, String>> enbList = (List<Map<String, String>>) validatCiqRowDetails.get("neDetails");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(programId) && StringUtils.isNotEmpty(enbName)
					&& StringUtils.isNotEmpty(enbId)) {
				String dbCollectionName = CommonUtil.createMongoDbFileName(programId, fileName);

				Map<String, Object> objMap = fileUploadService.validationEnbDetails(dbCollectionName,
						Integer.valueOf(programId), enbName, enbId);
				if (objMap != null && objMap.size() > 0 && objMap.containsKey("validationDetails")) {
					List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) objMap.get("validationDetails");
					if (objErrorMap.size() > 0) {
						mapObject.put("status", Constants.FAIL);
						mapObject.put("errorDetails", objErrorMap);
						mapObject.put("reason", "Validations Fail");
					} else {
						mapObject.put("status", Constants.SUCCESS);
					}
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("errorDetails", new LinkedHashMap<>());
					mapObject.put("reason", "Validations Fail");
				}
				commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
						Constants.ACTION_VALIDATE,
						"CIQ Details Validated Successfully For: " + fileName + ", NE: " + enbId, sessionId);
			}

		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("errorDetails", new LinkedHashMap<>());
			mapObject.put("reason", "Validations Fail");
			logger.error("Exception  validationEnbDetails()   in UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			/*
			 * mapObject.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FAILED_TO_DELETE_VALIDATION_DETAILS));
			 */
		}
		return mapObject;
	}
	/**
	 * This api delete deleteValidationDetails
	 * 
	 * @param deleteCiqRowDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/validationEnbDetailsPreMigration", method = RequestMethod.POST)
	public JSONObject validationEnbDetailsPreMigration(@RequestBody JSONObject validatCiqRowDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();

		String programId = null;
		String fileName = null;
		String enbName = null;
		String enbId = null;
		try {
			sessionId = validatCiqRowDetails.get("sessionId").toString();
			serviceToken = validatCiqRowDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			fileName = validatCiqRowDetails.get("fileName").toString();
			programId = validatCiqRowDetails.get("programId").toString();
//			enbName = validatCiqRowDetails.get("enbName").toString();
//			enbId = validatCiqRowDetails.get("enbId").toString();
			List<Map<String, String>> enbList = (List<Map<String, String>>) validatCiqRowDetails.get("neDetails");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}

			if (StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(programId)) {
				String dbCollectionName = CommonUtil.createMongoDbFileName(programId, fileName);
				Map<String, Object> objMap = new HashMap<>();
				List<ErrorDisplayModel> objErrorMap1 = new ArrayList<>();
				if (null != enbList && enbList.size() > 0) {

					for (Map<String, String> enb : enbList) {

						enbId = enb.get("neId");
						enbName = enb.get("neName");
						if (StringUtils.isNotEmpty(enbName) && StringUtils.isNotEmpty(enbId)) {
							Map<String, Object> neMap = fileUploadService.validationEnbDetails(dbCollectionName,
									Integer.valueOf(programId), enbName, enbId);
							if (neMap != null && neMap.size() > 0 && neMap.containsKey("validationDetails")) {
								List<ErrorDisplayModel> objErrorMap = (List<ErrorDisplayModel>) neMap
										.get("validationDetails");
								if (objErrorMap.size() > 0) {
									objErrorMap1.addAll(objErrorMap);
								}
							}
						}

					}

						if (objErrorMap1.size() > 0) {
							mapObject.put("status", Constants.FAIL);
							mapObject.put("errorDetails", objErrorMap1);
							mapObject.put("reason", "Validations Fail");
						} else {
							mapObject.put("status", Constants.SUCCESS);
						}
					commonUtil.saveAudit(Constants.EVENT_PRE_MIGRATION, Constants.EVENT_PRE_MIGRATION_CIQ_UPLOAD,
							Constants.ACTION_VALIDATE,
							"CIQ Details Validated Successfully For: " + fileName + ", NE: " + enbId, sessionId);
				}
			}
		} catch (Exception e) {
			mapObject.put("status", Constants.FAIL);
			mapObject.put("errorDetails", new LinkedHashMap<>());
			mapObject.put("reason", "Validations Fail");
			logger.error("Exception  validationEnbDetails()   in UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
			/*
			 * mapObject.put("reason",
			 * GlobalInitializerListener.faultCodeMap.get(FaultCodes.
			 * FAILED_TO_DELETE_VALIDATION_DETAILS));
			 */
		}
		return mapObject;
	}


	public String rfScriptsValidations(List<MultipartFile> scriptFiles, String sessionId, String programId,String programName) {
		String status = null;
		StringBuilder uploadPath = new StringBuilder();
		uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
				// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
				.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId).append(Constants.SEPARATOR)
				.append(sessionId).append(Constants.SEPARATOR);

		try {
			AtomicBoolean statusOfXml = new AtomicBoolean();
			AtomicBoolean statusOfExcutionSequence = new AtomicBoolean();
			AtomicBoolean zipChildFolderStatus = new AtomicBoolean();
			AtomicBoolean zipNameStatus = new AtomicBoolean();
			AtomicBoolean statusOfSequenceNumber = new AtomicBoolean();
			List<String> files = new ArrayList<>();

			

			for (MultipartFile rfScriptFile : scriptFiles) {
				fileUploadService.uploadMultipartFile(rfScriptFile, uploadPath.toString());
			}

			File fileDetails = new File(uploadPath.toString());
			if (fileDetails.exists()) {
				File[] listOfFiles = fileDetails.listFiles();
				breakListOfFiles: for (File localFile : listOfFiles) {
					String path = localFile.getAbsolutePath();
					String zipFolder = FilenameUtils.removeExtension(FilenameUtils.getName(path));
					if (!NumberUtils.isNumber(zipFolder)&& !zipFolder.contains("_ENDC")) {
						
						zipNameStatus.set(true);
						break breakListOfFiles;
						
						
					}
					if (path.endsWith("zip")) {

						ZipFile zf = new ZipFile(new File(path));

						Enumeration entries = zf.entries();

						while (entries.hasMoreElements()) {
							ZipEntry ze = (ZipEntry) entries.nextElement();
							if (ze.isDirectory()) {
								if (!(zipFolder.equalsIgnoreCase(
										FilenameUtils.getName(StringUtils.removeEnd(ze.getName(), "/"))))) {
									String N=ze.getName();
									String M=FilenameUtils.getName(StringUtils.removeEnd(ze.getName(), "/"));
									zipChildFolderStatus.set(true);
									break breakListOfFiles;
								}
							} else if (!ze.isDirectory()) {
								String fileName = FilenameUtils.getName(ze.getName()).replaceAll("-", "_");
								String sequence = fileName.split("_")[0];
								if (!FilenameUtils.getExtension(ze.getName()).equalsIgnoreCase("xml")) {
									statusOfXml.set(true);
									break breakListOfFiles;
								} else if (!NumberUtils.isNumber(sequence) && programId.contains("4G")) {
									statusOfExcutionSequence.set(true);
									break breakListOfFiles;
								}
							}

						}
					} else if (path.endsWith("7z")) {
						SevenZFile sevenZFile = new SevenZFile(new File(path));
						SevenZArchiveEntry ze;
						while ((ze = sevenZFile.getNextEntry()) != null) {
							if (ze.isDirectory()) {
								if (!(zipFolder.equalsIgnoreCase(
										FilenameUtils.getName(StringUtils.removeEnd(ze.getName(), "/"))))) {
									zipChildFolderStatus.set(true);
									break breakListOfFiles;
								}
							} else if (!ze.isDirectory()) {
								String fileName = FilenameUtils.getName(ze.getName()).replaceAll("-", "_");
								String sequence = fileName.split("_")[0];
								if (!FilenameUtils.getExtension(ze.getName()).equalsIgnoreCase("xml")) {
									statusOfXml.set(true);
									break breakListOfFiles;
								} else if (!NumberUtils.isNumber(sequence) && programId.contains("4G")) {
									statusOfExcutionSequence.set(true);
									break breakListOfFiles;
								}
							}
						}
					}

				}

			}

			if (statusOfXml.get()) {
				status = "RF Scripts Should  XML Files";
			} else if (statusOfExcutionSequence.get()) {
				status = "RF Scripts Name Should Be Proper";
			} else if (zipChildFolderStatus.get()) {
				status = "RF Scripts Folder Name Should Be Proper";
			} else if (zipNameStatus.get()) {
				if(programName.contains("5G-CBAND"))
					status = "RF Scripts Folder Name Should Be vDU Id";
				else
					status = "RF Scripts Folder Name Should Be eNodeb Id";
			} else {
				status = "success";
			}
			File fileInputDir = new File(uploadPath.toString());
			if (fileInputDir.exists()) {
				FileUtils.deleteDirectory(fileInputDir);
			}
		} catch (Exception e) {
			File fileInputDir = new File(uploadPath.toString());
			if (fileInputDir.exists()) {
				try {
					FileUtils.deleteDirectory(fileInputDir);
				} catch (IOException e1) {
					logger.error("Exception  rfScriptsValidations()   in UploadCIQController:"
							+ ExceptionUtils.getFullStackTrace(e1));
				}
			}
			logger.error("Exception  rfScriptsValidations()   in UploadCIQController:"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;

	}

}
